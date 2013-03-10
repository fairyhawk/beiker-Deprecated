package com.beike.core.service.trx.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfoGenerator;
import com.beike.common.bean.trx.QueryResult;
import com.beike.common.bean.trx.RefundResult;
import com.beike.core.service.trx.PaymentInfoGeneratorService;

/* @Title: PaymentInfoGeneratorYeepayServcieImpl.java
 * @Package com.beike.biz.service.trx
 * @Description: YEEPAY支付机构处理接口
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("paymentInfoGeneratorYeepayService")
public class PaymentInfoGeneratorYeepayServiceImpl implements
		PaymentInfoGeneratorService {

	private final Log logger = LogFactory
			.getLog(PaymentInfoGeneratorYeepayServiceImpl.class);

	/**
	 * 获取支付请求串
	 */
	@Override
	public String getReqDataForPayment(OrderInfo orderInfo) {
		String returnStr = PaymentInfoGenerator.getReqMd5HmacForOnlinePayment(
				orderInfo.getPayRequestId(),
				orderInfo.getNeedPayAamount() + "",
				orderInfo.getPayRequestId(), "", "", "", orderInfo
						.getExtendInfo(), orderInfo.getProviderChannel());

		return returnStr;
	}

	/**
	 * 获取查询数据
	 */
	@Override
	public OrderInfo queryByOrder(OrderInfo orderInfo) {
		String payRequestId = orderInfo.getPayRequestId();

		OrderInfo resultorderInfo = new OrderInfo();
		QueryResult queryResult = null;
		try {
			queryResult = PaymentInfoGenerator.queryByOrder(payRequestId);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		String payStatus = queryResult.getRb_PayStatus();
		String proExternalId = queryResult.getR2_TrxId();
		String qryAmount = queryResult.getR3_Amt();

		resultorderInfo.setPayStatus(payStatus);
		resultorderInfo.setProExternalId(proExternalId);
		resultorderInfo.setQryAmount(qryAmount);
		logger.info("+++++++++++++++qryOrder:" + payRequestId + "->qryResult:"
				+ payStatus + "->qryCode:" + queryResult.getR1_Code()
				+ "++++proExternallId:" + queryResult.getR2_TrxId()
				+ "++++confirmAmount:" + qryAmount + "+++++++++++");
		return resultorderInfo;
	}

	/**
	 * 退款
	 */
	@Override
	public OrderInfo refundByTrxId(OrderInfo orderInfo) {
		String refundRequetId = orderInfo.getRefundRequestId();
		String proExternalId = orderInfo.getProExternalId();
		String refundReqAmount = orderInfo.getRefundReqAmount();
		RefundResult refundResult = null;
		OrderInfo resultOrderInfo = new OrderInfo();
		try {
			refundResult = PaymentInfoGenerator.refundByTrxId(refundRequetId,
					proExternalId, refundReqAmount, "CNY", "");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		String refundRspAmount = refundResult.getR3_Amt();
		String refundRspCode = refundResult.getR1_Code();
		resultOrderInfo.setRefundRspCode(refundRspCode);
		if ("1".equals(refundRspCode)) {

			resultOrderInfo.setRefundStatus("SUCCESS");

		}
		logger.info("++++++++++++++++++++refundReqAmout:" + refundReqAmount
				+ "<-->refundRspAmount" + refundRspAmount + "++++refundResult:"
				+ refundRspCode + "+++++++");

		return resultOrderInfo;
	}
}
