package com.beike.biz.service.trx;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.OrderCreateException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: BizProcessManager.java
 * @Package com.beike.biz.service.trx
 * @Description: 业务处理管理器
 * @date May 9, 2011 6:23:36 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface ProcessService {
	/**
	 * 后置处理：账户支付或网银支付信息组装或（老）返现后续处理
	 * @param orderInfo
	 * @return
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws OrderCreateException
	 */
	public OrderInfo processPost(OrderInfo orderInfo)
			throws ProcessServiceException, RebateException, AccountException,
			TrxOrderException, PaymentException, TrxorderGoodsException,
			VoucherException, RuleException, StaleObjectStateException,
			OrderCreateException,CouponException;

	/**
	 * 前置处理：下单或（老）返现创建
	 * @param orderInfo
	 * @return
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 */
	public OrderInfo processPro(OrderInfo orderInfo) throws TrxOrderException,CouponException,
			AccountException, PaymentException, TrxorderGoodsException,RuleException, StaleObjectStateException;
	
	
	/**
	 * 账户支付重试(暂时只对分销商来的订单有效)
	 * @param orderInfo
	 * @return
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 */
	public OrderInfo processReTry(OrderInfo orderInfo);


}
