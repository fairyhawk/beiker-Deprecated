package com.beike.core.service.trx.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.common.bean.trx.QueryResult;
import com.beike.core.service.trx.PaymentInfoGeneratorService;

/* @Title: PaymentInfoGeneratorYeepayServcieImpl.java
 * @Package com.beike.biz.service.trx
 * @Description: ALIPAY支付机构处理接口
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("paymentInfoGeneratorAlipayService")
public class PaymentInfoGeneratorAlipayServiceImpl implements PaymentInfoGeneratorService {

	private final Log logger = LogFactory.getLog(PaymentInfoGeneratorAlipayServiceImpl.class);
	
	/**
	 * 支付接口
	 */
	@Override
	public String getReqDataForPayment(OrderInfo orderInfo) {
		String payRequestId = orderInfo.getPayRequestId(); // 支付请求号
		String needAmount = orderInfo.getNeedPayAamount() + ""; // 交易 金额
	//	String goodsName = orderInfo.getGoodsName();// 产品名称
		String extendInfo = orderInfo.getExtendInfo();// 扩展信息
		String providerChannel = orderInfo.getProviderChannel();//银行支付接口
		
		String result = PaymentInfoGeneratorAlipay
				.getReqMd5HmacForOnlinePayment(payRequestId, needAmount,
						payRequestId + "", extendInfo,providerChannel, "p9_SAF",
						"pa_MP", "pd_FrpId");

		return result;
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
			queryResult = PaymentInfoGeneratorAlipay.queryByOrder(payRequestId);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		String payStatus = queryResult.getRb_PayStatus();
		//String rspproExternalId = queryResult.getR2_TrxId();
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
	//	String refundRspAmount = refundReqAmount;
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

	public static void main(String[] args) {

		PaymentInfoGeneratorAlipayServiceImpl aa = new PaymentInfoGeneratorAlipayServiceImpl();
		OrderInfo oi = new OrderInfo();
		oi.setRefundRequestId("20110913000113");
		oi.setProExternalId("2088502970160936");
		oi.setRefundReqAmount("0.3");
		aa.refundByTrxId(oi);

		/*
		 * PaymentInfoGeneratorAlipayServiceImpl aa = new
		 * PaymentInfoGeneratorAlipayServiceImpl(); OrderInfo oi = new
		 * OrderInfo(); oi.setRefundRequestId("201109090001");
		 * oi.setProExternalId("2088502970160936");
		 * oi.setRefundReqAmount("0.01"); aa.refundByTrxId(oi);
		 */

		/*
		 * String refund =
		 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?><alipay><is_success>F</is_success><error>DUPLICATE_BATCH_NO</error></alipay>"
		 * ; boolean boo = refund.contains("<is_success>T</is_success>");
		 * System.out.println(boo);
		 */
	}

}
