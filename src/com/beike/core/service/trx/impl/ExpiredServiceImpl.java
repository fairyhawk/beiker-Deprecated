package com.beike.core.service.trx.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.enums.trx.VoucherVerifySource;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.ExpiredException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;
import com.beike.core.service.trx.ExpiredService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxLogDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.util.EnumUtil;

/**
 * @Title: ExpiredService.java
 * @Package com.beike.core.service.trx
 * @Description: 过期service实现
 * @date May 24, 2011 10:39:31 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("expiredService")
public class ExpiredServiceImpl implements ExpiredService {

	public Log logger = LogFactory.getLog(ExpiredServiceImpl.class);

	@Autowired
	public TrxorderGoodsService trxorderGoodsService;

	@Autowired
	public TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	public VoucherService voucherService;

	@Autowired
	private TrxLogDao trxLogDao;
	@Autowired
	private TrxSoaService trxSoaService;

	@Override
	public void processExpired(TrxorderGoods tgGoods) throws ExpiredException,
			VoucherException, StaleObjectStateException {
		// 如果商品订单不是支付成功装态，则不能置为过期
		if (!(TrxStatus.SUCCESS.equals(tgGoods.getTrxStatus()) && AuthStatus.SUCCESS.equals(tgGoods.getAuthStatus()))) {
			throw new ExpiredException(BaseException.EXPIRED_STATUS_INVALID);
		}
		
		// 过期凭证状态-同步销毁凭证库(包括同步trxgoods中凭证状态）
		tgGoods.setTrxStatus(TrxStatus.EXPIRED);
		trxorderGoodsDao.updateTrxGoods(tgGoods);
		voucherService.destoryExpiredVoucher(tgGoods.getVoucherId());

		/******************************** 写操作日志开始 **************************/
		try {
			TrxLog trxLog = new TrxLog(tgGoods.getTrxGoodsSn(), new Date(),TrxLogType.EXPIRED, "系统自动过期", "商品订单号："+ tgGoods.getTrxGoodsSn());
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {

			e.printStackTrace();

		}
		/******************************** 写操作日志结束 **************************/
		//取消预订部分
		try {
			trxSoaService.processScheduled(tgGoods.getId(),tgGoods.getTrxorderId());
		} catch (Exception e1) {
			logger.debug("+++++++++++++++++++trxGoodsId="+tgGoods.getId()+"+++++++++++booking ERROR"+e1);
			e1.printStackTrace();
		}
		
	}

	/**
	 * 一次性将历史数据置为"已使用"
	 * 
	 * @param tgGoods
	 * @throws ExpiredException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws TrxorderGoodsException
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 * @throws StaleObjectStateException
	 */
	@Override
	public void processSuccessToUsed(TrxorderGoods tgGoods)
			throws ExpiredException, VoucherException, ProcessServiceException,
			RebateException, AccountException, TrxOrderException,
			PaymentException, TrxorderGoodsException, RuleException,
			StaleObjectStateException {
		// 凭证自校验
		voucherService.checkVoucherSelf(tgGoods, EnumUtil
				.transEnumToString(VoucherVerifySource.SYSTEMAUTO), "0");

		/******************************** 写操作日志开始 **************************/
		try {
			TrxLog trxLog = new TrxLog(tgGoods.getTrxGoodsSn(), new Date(),
					TrxLogType.USED, "发送商家自有凭证，系统自动变更为'已使用'", "商品订单号："
							+ tgGoods.getTrxGoodsSn());
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {
			e.printStackTrace();

		}
		/******************************** 写操作日志结束 **************************/

	}
}