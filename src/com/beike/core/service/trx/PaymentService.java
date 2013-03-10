package com.beike.core.service.trx;

import java.util.List;

import com.beike.common.bean.trx.PaymentInfo;
import com.beike.common.entity.trx.Payment;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.StaleObjectStateException;

/**
 * @Title: PaymentService.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 支付信息CORE Servie
 * @date May 17, 2011 3:31:58 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PaymentService {
	/**
	 * payment创建
	 * @param paymentInfo
	 * @return
	 * @throws PaymentException
	 */
	public Payment create(PaymentInfo paymentInfo) throws PaymentException;

	/**
	 * Payment完成
	 * @param paymentInfo
	 * @param paymentList
	 * @return
	 * @throws PaymentException
	 * @throws StaleObjectStateException
	 */
	public List<Payment> complete(PaymentInfo paymentInfo,List<Payment> paymentList,boolean isToLoad)throws PaymentException, StaleObjectStateException;

	/**
	 * 根据主键查询payment
	 * 
	 * @param paymentId
	 * @return
	 */
	public Payment findById(Long paymentId);
	
	
	/**
	 * 预查询查询相关支付记录
	 * @param trxId
	 * @return
	 */
	public  List<Payment> preQryPayment(String trxIdStr);

}
