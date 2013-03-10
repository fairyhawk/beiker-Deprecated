package com.beike.core.service.trx.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.core.service.trx.TrxLogService;
import com.beike.dao.trx.TrxLogDao;
import com.beike.util.Amount;
import com.beike.util.BankInfoUtil;
import com.beike.util.EnumUtil;

/**
 * @Title: TrxLogService.java
 * @Package com.beike.core.service.trx
 * @Description: 日志服务类
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 3:25:41 PM
 * @version V1.0
 */
@Service("trxLogService")
public class TrxLogServiceImpl implements TrxLogService {
	private final Log logger = LogFactory.getLog(TrxLogServiceImpl.class);

	@Autowired
	private TrxLogDao trxLogDao;

	public void addTrxLogForSuc(List<Payment> paymentList,
			List<TrxorderGoods> trxorderGoodsList) {
		double vcActAmount = 0.0;// 账户里虚拟币金额
		double cashActAmount = 0.0;// 账户里现金金额
		double cashPayAmount = 0.0;// 网银支付的现金金额
		double actAmount = 0.0;// 账户里虚拟币金额+账户里现金
		String providerType = "";// 第三方支付渠道
		String payChannel = "";// 支付通道
		StringBuilder payInfoSb = new StringBuilder();

		if (paymentList == null || paymentList.size() == 0
				|| trxorderGoodsList == null || trxorderGoodsList.size() == 0) {

			logger
					.info("++++++paymentList  or trxorderGoodsList    is null!+++++++++");

			return;

		}

		try {
			// 遍历Payment
			for (Payment paymentItem : paymentList) {
				if (PaymentType.ACTVC.equals(paymentItem.getPaymentType())) {
					vcActAmount = paymentItem.getTrxAmount();

				} else if (PaymentType.ACTCASH.equals(paymentItem
						.getPaymentType())) {
					cashActAmount = paymentItem.getTrxAmount();

				} else if (PaymentType.PAYCASH.equals(paymentItem
						.getPaymentType())) {
					cashPayAmount = paymentItem.getTrxAmount();
					providerType = EnumUtil.transEnumToString(paymentItem
							.getProviderType());
					payChannel = paymentItem.getPayChannel();

				}

			}
			// 账户支付金额合并
			actAmount = Amount.add(vcActAmount, cashActAmount);

			// 支付日志信息封装
			payInfoSb.append("账户支付：￥");
			payInfoSb.append(actAmount);
			payInfoSb.append(";");

			// 如果有网银支付记录
			if (cashPayAmount > 0.0) {// 如果有网银支付记录。cashPayAmount>0则一定会有，反之亦然。
				String providerAndChannel = BankInfoUtil
						.convProviderAndChannel(providerType, payChannel);
				payInfoSb.append(providerAndChannel);

			}
			payInfoSb.append("网银支付：￥");
			payInfoSb.append(cashPayAmount);

			// 遍历商品订单,循环存入
			for (TrxorderGoods item : trxorderGoodsList) {
				Long trxorderGoodsId = item.getId();
				logger.info("++++trxorderGoodsId:" + trxorderGoodsId
						+ "++++logContent:" + payInfoSb.toString());
				TrxLog trxLog = new TrxLog(item.getTrxGoodsSn(), new Date(),
						TrxLogType.SUCCESS, "购买成功", payInfoSb.toString());
				trxLogDao.addTrxLog(trxLog);

			}

		} catch (Exception e) {
			logger.debug("+++++++++++" + e + "++++++++++++++");
			e.printStackTrace();

		}

	}
	
	
	/**
	 * 根据传入的trxorderGoodsList记录运营下单操作日志
	 * 
	 * @param trxorderGoodsList
	 */
	public void addTrxLogForCreate(List<TrxorderGoods> trxorderGoodsList){
		
		if(trxorderGoodsList==null  || trxorderGoodsList.size()==0){
			logger.info("++++++ trxorderGoodsList    is null!+++++++++");
			return ;
		}
		
		for(TrxorderGoods trxorderGoods:trxorderGoodsList){
	
			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(),new Date(), TrxLogType.INIT, "下单成功", "商品订单号:"+ trxorderGoods.getTrxGoodsSn());
			trxLogDao.addTrxLog(trxLog);
	
		}
	
	}
}
