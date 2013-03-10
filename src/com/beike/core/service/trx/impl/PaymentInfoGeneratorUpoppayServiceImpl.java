package com.beike.core.service.trx.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfoGeneratorUpop;
import com.beike.common.bean.trx.QuickPayUtils;
import com.beike.common.bean.trx.UpopPayConfig;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.util.Amount;
import com.beike.util.Configuration;

/** 
* @ClassName: PaymentInfoGeneratorUpoppayServiceImpl 
* @Description: 银联支付接口实现类
* @author yurenli
* @date 2012-5-4 下午06:01:47 
* @version V1.0 
*/ 
@Service("paymentInfoGeneratorUpopService")
public class PaymentInfoGeneratorUpoppayServiceImpl implements
PaymentInfoGeneratorService{

	private final Log logger = LogFactory.getLog(PaymentInfoGeneratorUpoppayServiceImpl.class);
	
	/**
	 * 查询接口
	 */
	private static String upopQueryReqURL = Configuration.getInstance()
			.getValue("upopQueryReqURL");
	
	private static String upopMerCode = Configuration.getInstance().getValue("upopMerCode");
	@Override
	public String getReqDataForPayment(OrderInfo orderInfo) {
		String payRequestId = orderInfo.getPayRequestId(); // 支付请求号
		String needAmount = orderInfo.getNeedPayAamount() + ""; // 交易 金额
		String goodsName = orderInfo.getGoodsName();// 产品名称
		String extendInfo = orderInfo.getExtendInfo();// 扩展信息
		String providerChannel = orderInfo.getProviderChannel();//银行支付接口
		String result = PaymentInfoGeneratorUpop.getReqMd5HmacForOnlinePayment(payRequestId, needAmount,
				 extendInfo,providerChannel,goodsName);  
		
		
		return result;
	}

	@Override
	public OrderInfo queryByOrder(OrderInfo orderInfo) {
		OrderInfo resultorderInfo = new OrderInfo();
		String payRequestId = orderInfo.getPayRequestId();
		String[] valueVo = new String[]{
				UpopPayConfig.version,//协议版本
				UpopPayConfig.charset,//字符编码
				"01",//交易类型
				upopMerCode,//商户代码
				payRequestId,//订单号
				orderInfo.getCreateDate(),//交易时间
				""//保留域  说明：如果是收单机构保留域需传收单代码如：{acqCode=00215800}，商户直接接入upop不传收单代码
		};
		QuickPayUtils quickPayUtils = new QuickPayUtils();
		String res = quickPayUtils.doPostQueryCmd(upopQueryReqURL,new QuickPayUtils().createBackStr(valueVo, UpopPayConfig.queryVo));
		logger.info("+++++++++++++++++res+++++++="+res);
	
		
		if (res != null && !"".equals(res)) {
			
			String[] arr = QuickPayUtils.getResArr(res);
			
			int checkedRes = new QuickPayUtils().checkSecurity(arr);
			if(checkedRes==1){//验证签名
				//商户业务逻辑


				/**
				 * queryResult=0或者2时 respCode为00，其余情况下respCode为非全零的两位错误码
				 * queryResult为空时报文格式错误
				 * queryResult：
				 * 0：成功（响应码respCode为00）
				 * 1：失败（响应码respCode非00）
				 * 2：处理中（响应码respCode为00）
				 * 3：无此交易（响应码respCode非00）
				*/
				
				//以下是商户业务处理
				String queryResult = "";
				String amount = "";
				String qid = "";
				for (int i = 0; i < arr.length; i++) {
					//System.out.println(arr[i]);
					String[] queryResultArr = arr[i].split("=");
					// 处理商户业务逻辑
					if (queryResultArr.length >= 2 && "queryResult".equals(queryResultArr[0])) {
						queryResult = arr[i].substring(queryResultArr[0].length()+1);
					}
					
					if(queryResultArr.length >= 2 && "qid".equals(queryResultArr[0])){
						qid = arr[i].substring(queryResultArr[0].length()+1);
					}
					if(queryResultArr.length >= 2 && "settleAmount".equals(queryResultArr[0])){
						amount = arr[i].substring(queryResultArr[0].length()+1);
					}
					
				}
				if(queryResult!=""){
					if ("0".equals(queryResult)) {
						resultorderInfo.setPayStatus("SUCCESS");
						resultorderInfo.setPayRequestId(payRequestId);
						double orderAmount = Amount.div(Double.valueOf(amount),100);
						resultorderInfo.setQryAmount(String.valueOf(orderAmount));
						resultorderInfo.setProExternalId(qid);// 银行流水号
						logger.info("++++++++PayRequestId"+payRequestId+"+++++++SUCCESS+++++++++");
					}
					if ("1".equals(queryResult)) {
						resultorderInfo.setPayStatus("FAILED");
						logger.debug("queryByOrder()+++"+payRequestId+"+++1++++交易失败+++++++++");
					}
					if ("2".equals(queryResult)) {
						resultorderInfo.setPayStatus("FAILED");
						logger.debug("queryByOrder()+++"+payRequestId+"+++2++++交易处理中+++++++++");
					}
					if ("3".equals(queryResult)) {
						resultorderInfo.setPayStatus("FAILED");
						logger.debug("queryByOrder()+++"+payRequestId+"+++3++++无此交易+++++++++");
					}
				}else{
					resultorderInfo.setPayStatus("FAILED");
					logger.debug("queryByOrder()+++"+payRequestId+"+++++++报文格式错误+++++++++");
				}
			} else{
				resultorderInfo.setPayStatus("FAILED");
				logger.debug("queryByOrder()+++"+payRequestId+"+++++++验证签名失败+++++++++");
			}
		}
		return resultorderInfo;
	}

	@Override
	public OrderInfo refundByTrxId(OrderInfo orderInfo) {
		String refundRequetId = orderInfo.getRefundRequestId(); // 退款请求号
		String proExternalId = orderInfo.getProExternalId();// 支付机构交易流水号
		String refundReqAmount = orderInfo.getRefundReqAmount();// 需退款金额
		boolean boo = false;
		OrderInfo resultOrderInfo = new OrderInfo();
		try {
			boo = PaymentInfoGeneratorUpop.refundByTrxId(refundRequetId,
					refundReqAmount,proExternalId , "", "");
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
		logger.info("++++++++refundByTrxId++++++++++++refundReqAmout:" + refundReqAmount
				+ "++++refundRspCode:" + resultOrderInfo.getRefundRspCode()
				+ "+++++++");

		return resultOrderInfo;
	}

	
}
