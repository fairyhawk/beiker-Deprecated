package com.beike.action.pay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;

/**
 * @Title: PayAction.java
 * @Package com.beike.action.pay
 * @Description:
 * @date Jun 1, 2011 7:33:26 PM
 * @author wh.cheng
 * @version v1.0
 */
@Controller
public class AlipayRefundCallBackAction {

	private final Log logger = LogFactory
			.getLog(AlipayRefundCallBackAction.class);


    /**
     * 退款回调。(预留。暂时无用)
     * @param request
     * @return
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@RequestMapping("/pay/refundCallBackAlipay.do")
	public String aliCallback(HttpServletRequest request) throws Exception {// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		// 退款结果明细
		String result_details = new String(request.getParameter(
				"result_details").getBytes("ISO-8859-1"), "utf-8");
		// 格式：第一笔交易#第二笔交易#第三笔交易
		// 第N笔交易格式：交易退款信息
		// 交易退款信息格式：原付款支付宝交易号^退款总金额^处理结果码^结果描述
		logger.info("+++++++++++++++++++++++++alipay result:" + params+ "++++++++++++++++++++++++");
		logger.info("+++++++++++++++++++++++++alipay result_details:"+ result_details + "++++++++++++++++++++++++");
		if (PaymentInfoGeneratorAlipay.verify(params)) {// 验证成功

			String[] result = result_details.split("^");
			String aliid = result[0];// 原付款支付宝交易号
			String amount = result[1];// 退款总金额
			String batch_no = request.getParameter("batch_no");// 退款批次号

			logger.info("refund notify callback +++success++aliid+"+aliid+"+++amount+++"+amount+"+++++batch_no"+batch_no);

			return null;
			// ////////////////////////////////////////////////////////////////////////////////////////
		} else {
			// 该页面可做页面美工编辑
			logger.error("+++++++++验证失败++++++++++++");
			throw new Exception();

		}
	}
	
	



}