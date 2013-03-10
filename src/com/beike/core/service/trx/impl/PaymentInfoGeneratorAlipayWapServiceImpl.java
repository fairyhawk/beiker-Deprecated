package com.beike.core.service.trx.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipayWap;
import com.beike.common.bean.trx.QueryResult;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.util.StringUtils;

/**   
 * @title: PaymentInfoGeneratorAlipayWapServiceImpl.java
 * @package com.beike.core.service.trx.impl
 * @description: 支付宝wap支付 支付、查询、退款接口相关处理
 * @author wangweijie  
 * @date 2012-6-25 下午01:49:53
 * @version v1.0   
 */

@Service("paymentInfoGeneratorAlipayWapService")
public class PaymentInfoGeneratorAlipayWapServiceImpl implements PaymentInfoGeneratorService{
	private static final Log logger = LogFactory.getLog(PaymentInfoGeneratorAlipayWapServiceImpl.class);

	/**
	 * 支付回调返回
	 */
	@Override
	public String getReqDataForPayment(OrderInfo orderInfo) {
		String orderNo = orderInfo.getPayRequestId();	//订单编号
		String subject = orderInfo.getGoodsName();		//商品名称
		subject = StringUtils.cutffStr(subject,30,".......");			//商品名称(30字符)
		subject = subject.replaceAll("[&<>]", " ");		//避免&引起的xml解析错误
		String totalFee = String.valueOf(orderInfo.getNeedPayAamount()); // 交易 金额
		String cashierCode = orderInfo.getProviderChannel();		//支付前置银行代码
		return PaymentInfoGeneratorAlipayWap.getReqForAlipayWapPayment(StringUtils.getSysTimeRandom(),subject,orderNo,totalFee,cashierCode);
	}

	/**
	 * 查询订单处理
	 */
	@Override
	public OrderInfo queryByOrder(OrderInfo orderInfo) {
		String payRequestId = orderInfo.getPayRequestId();
		OrderInfo resultorderInfo = new OrderInfo();
		QueryResult queryResult = null;
		try {
			queryResult = PaymentInfoGeneratorAlipayWap.queryByOrder(payRequestId);
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
	 * 退款处理
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
