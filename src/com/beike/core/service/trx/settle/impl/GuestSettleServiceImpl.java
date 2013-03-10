package com.beike.core.service.trx.settle.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.action.pay.hessianclient.FinanceHessianServiceGateway;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.settle.GuestSettleService;
import com.beike.dao.trx.VoucherDao;

/**
 * @Title: GuestSettleServiceImpl.java
 * @Package com.beike.core.service.trx.settle.impl
 * @Description:商家新清结算接口
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("guestSettleService")
public class GuestSettleServiceImpl implements GuestSettleService {

	private static final Log logger = LogFactory.getLog(GuestSettleServiceImpl.class);

	@Resource(name = "financeClient.hessianServiceGateWay")
	private FinanceHessianServiceGateway financeHessianService;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private VoucherDao voucherDao;

	public static final String RSPCODE_KEY = "rspCode";
	public static final String RESPCODE_VALUE_IN_SUCCESS = "1";
	public static final String RESPCODE_VALUE_IN_EXCEPTION = "-1";
	
	/**
	 * 商家新清结算同步入账
	 * 
	 * @param requestData
	 * @param trxorderGoods
	 * @param voucherId
	 */
	@Override
	public void guestCreditForSync(TrxRequestData requestData,TrxorderGoods trxorderGoods, Long voucherId) {
		// 商家入账操作（招财宝商家新清洁算逻辑）
		String guestId = requestData.getGuestId();// 商家id
		String voucherCode = requestData.getVoucherCode(); // 凭证码
		String subGuestId = requestData.getSubGuestId(); // 分店id
		Long tgId = trxorderGoods.getId();// 商品订单ID
		logger.info("+++ guestCreditForSync  guestId:" + guestId+"subGuestId:"+subGuestId+",trxorderGoodsId:" + tgId + "voucherCode:"+ voucherCode);
		try {
			// 查询主库
			if (guestId.length() == 7 && subGuestId.length() == 7) {
			    logger.info("+++ guestCreditForSync invoce guestId:" + guestId+"subGuestId:"+subGuestId+",trxorderGoodsId:" + tgId + "voucherCode:"+ voucherCode);
				Map<String, String> sourceMap = new HashMap<String, String>();
				sourceMap.put("trxGoodsId", trxorderGoods.getId().toString());
				sourceMap.put("trxOrderId", trxorderGoods.getTrxorderId().toString());
				sourceMap.put("voucherCode", voucherCode);
				sourceMap.put("guestId", guestId);
				sourceMap.put("subGuestId", subGuestId);
				sourceMap.put("amount", trxorderGoods.getDividePrice() + "");
				sourceMap.put("voucherId", voucherId.toString());
				// 调用财务系统商家入账接口
				Map<String, String> map = financeHessianService.guestCredit(sourceMap);
				// 将商品订单入账状态改为SUCCESS
				if (RESPCODE_VALUE_IN_SUCCESS.equals(map.get(RSPCODE_KEY))) {
					trxorderGoods = trxorderGoodsService.preQryInWtDBByByTrxGoodsId(tgId);
					if (trxorderGoods != null) {
						trxorderGoodsService.updateTrxOrdrGoodsCreditStatusSuccess(trxorderGoods);
					}
				}

			}else{
			    logger.info("+++ guestCreditForSync failed guestId:" + guestId+"subGuestId:"+subGuestId+",trxorderGoodsId:" + tgId + "voucherCode:"+ voucherCode);
			}
		} catch (Exception e) {
			logger.error("validateVoucher+++ failed guestId:" + guestId	+ ",trxorderGoodsId:" + tgId + "voucherCode:"+ voucherCode, e);
		}
	}

	/**
	 * 商家新清结算异步入账
	 * 
	 * @param requestData
	 * @param trxorderGoods
	 * @param voucherId
	 * @throws Exception 
	 */
	public void guestCreditForAsyn(TrxorderGoods trxorderGoods) throws TrxorderGoodsException, StaleObjectStateException, Exception {
		Voucher voucher = voucherDao.findById(trxorderGoods.getVoucherId());
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("trxGoodsId", trxorderGoods.getId().toString());
		sourceMap.put("trxOrderId", trxorderGoods.getTrxorderId().toString());
		sourceMap.put("voucherCode", voucher.getVoucherCode());
		sourceMap.put("guestId", trxorderGoods.getGuestId() + "");
		sourceMap.put("subGuestId", trxorderGoods.getSubGuestId() + "");
		sourceMap.put("amount", trxorderGoods.getDividePrice() + "");
		sourceMap.put("voucherId", trxorderGoods.getVoucherId() + "");
		trxorderGoods.setLastUpdateDate(new Date());

		Map<String, String> creditQryInfo = financeHessianService.getGuestCreditInfo(sourceMap);
	
		// 返回为1代表已经入账成功,直接修改商品订单状态
		if(null==creditQryInfo|| creditQryInfo.isEmpty()){//握手失败
			
			throw  new Exception("定时补结算远程查询握手失败");
		}
		if (RESPCODE_VALUE_IN_SUCCESS.equals(creditQryInfo.get(RSPCODE_KEY))) {

			logger.info("+++ executeGuestCredit updateTrxGoods already success for remote qry :"+ trxorderGoods.getId());

			trxorderGoodsService.updateTrxOrdrGoodsCreditStatusSuccess(trxorderGoods);

		} else {// 调用入账
			Map<String, String> creditInfo = financeHessianService.guestCredit(sourceMap);
			
			if(null==creditInfo|| creditInfo.isEmpty()){//握手失败
				
				throw  new Exception("定时补结算远程入账请求握手失败");
			}
			// 入账成功，修改商品订单 入账状态为SUCCESS
			if (RESPCODE_VALUE_IN_SUCCESS.equals(creditInfo.get(RSPCODE_KEY))) {
				logger.info("+++ executeGuestCredit updateTrxGoods already success for :"+ trxorderGoods.getId());
				trxorderGoodsService.updateTrxOrdrGoodsCreditStatusSuccess(trxorderGoods);

			}
			
		}

	}

}
