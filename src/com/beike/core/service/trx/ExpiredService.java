package com.beike.core.service.trx;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.ExpiredException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: ExpiredService.java
 * @Package com.beike.core.service.trx
 * @Description: 过期service接口
 * @date May 24, 2011 10:39:31 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface ExpiredService {

	/**
	 * 常规过期
	 * 
	 * @param tgGoods
	 * @throws ExpiredException
	 * @throws VoucherException
	 * @throws StaleObjectStateException
	 */
	public void processExpired(TrxorderGoods tgGoods) throws ExpiredException,

	VoucherException, StaleObjectStateException;

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
	 */
	public void processSuccessToUsed(TrxorderGoods tgGoods)
			throws ExpiredException, VoucherException, ProcessServiceException,
			RebateException, AccountException, TrxOrderException,
			PaymentException, TrxorderGoodsException,
			StaleObjectStateException, RuleException;

}