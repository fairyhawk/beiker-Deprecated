package com.beike.core.service.trx;

import com.beike.common.bean.trx.OrderInfo;

/* @Title: PaymentInfoGeneratorServcie.java
 * @Package com.beike.biz.service.trx
 * @Description: PaymentInfoGeneratorServcie支付机构处理接口
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PaymentInfoGeneratorService {

	/**
	 * 获取支付请求串(支付接口)
	 * 
	 * @param orderInfo
	 * @return
	 */
	public String getReqDataForPayment(OrderInfo orderInfo);

	/**
	 * 单笔查询
	 * 
	 * @param orderRequstId
	 * @return
	 */
	public OrderInfo queryByOrder(OrderInfo orderInfo);

	/**
	 * 退款接口
	 * 
	 * @param orderRequstId
	 * @return
	 */
	public OrderInfo refundByTrxId(OrderInfo orderInfo);
}
