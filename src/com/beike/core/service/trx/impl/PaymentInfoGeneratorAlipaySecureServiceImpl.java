package com.beike.core.service.trx.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipaySecure;
import com.beike.common.bean.trx.QueryResult;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.util.StringUtils;

/* @Title: PaymentInfoGeneratorAlipaySecureServiceImpl.java
 * @Package com.beike.biz.service.trx
 * @Description: ALIPAY安全支付机构处理接口
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("paymentInfoGeneratorAlipaySecureService")
public class PaymentInfoGeneratorAlipaySecureServiceImpl implements PaymentInfoGeneratorService {

	private final Log logger = LogFactory.getLog(PaymentInfoGeneratorAlipaySecureServiceImpl.class);
	
	
	/**
	 * 支付接口
	 * @throws BaseException 
	 */
	@Override
	public String getReqDataForPayment(OrderInfo orderInfo){
		String payRequestId = orderInfo.getPayRequestId(); // 支付请求号
		String needAmount = String.valueOf(orderInfo.getNeedPayAamount()); // 交易 金额
		String goodsName = orderInfo.getGoodsName();
		goodsName = StringUtils.cutffStr(goodsName,30,".......");			//商品名称(30字符)
		goodsName = goodsName.replaceAll("[&<>]", " ");		//避免&引起的xml解析错误
		String extendInfo = orderInfo.getExtendInfo();// 扩展信息
		return PaymentInfoGeneratorAlipaySecure.getReqForSecurePayment(payRequestId, goodsName, extendInfo, needAmount);
	}

	/**
	 * 查询接口
	 */
	@Override
	public OrderInfo queryByOrder(OrderInfo orderInfo) {
		String payRequestId = orderInfo.getPayRequestId();
		OrderInfo resultorderInfo = new OrderInfo();
		QueryResult queryResult = null;
		try {
			queryResult = PaymentInfoGeneratorAlipaySecure.queryByOrder(payRequestId);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		String payStatus = queryResult.getRb_PayStatus();
	//	String rspproExternalId = queryResult.getR2_TrxId();
		String qryAmount = queryResult.getR3_Amt();

		resultorderInfo.setPayStatus(payStatus);
		resultorderInfo.setPayRequestId(payRequestId);
		resultorderInfo.setQryAmount(qryAmount);
		resultorderInfo.setProExternalId(queryResult.getR5_Pid());// 银行流水号
		return resultorderInfo;
	}

	/**
	 * 退款接口
	 */
	@Override
	public OrderInfo refundByTrxId(OrderInfo orderInfo) {
		String refundRequetId = orderInfo.getRefundRequestId(); // 退款请求号
		String proExternalId = orderInfo.getProExternalId();// 支付机构交易流水号
		String refundReqAmount = orderInfo.getRefundReqAmount();// 需退款金额
		boolean boo = false;
		OrderInfo resultOrderInfo = new OrderInfo();
		try {
			/*
			 * modify by wangweijie 4 新老账号更换
			 * 2012-07-26
			 */
			String payRequestId = orderInfo.getPayRequestId();
			Long paymentId = orderInfo.getPaymentId();		//获得支付ID，由
			boo = PaymentInfoGeneratorAlipay.refundByTrxId(refundRequetId, proExternalId, refundReqAmount, "CNY", "",payRequestId,paymentId);
			/*
			 * end modify
			 */
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		//String refundRspAmount = refundReqAmount;
		if (boo) {
			resultOrderInfo.setRefundRspCode("1");
			resultOrderInfo.setRefundStatus("SUCCESS");

		} else {
			resultOrderInfo.setRefundRspCode("0");
		}
		logger.info("++++++++++++++++++++refundReqAmout:" + refundReqAmount
				+ "++++refundRspCode:" + resultOrderInfo.getRefundRspCode()
				+ "+++++++");

		return resultOrderInfo;
	}



}
