package com.beike.action.pay;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.bean.trx.AlipayWapParams;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipaySecure;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipayWap;
import com.beike.util.XMapUtil;

/**
 * @title: PayCallBackAction.java
 * @package com.beike.action.pay
 * @description: 支付宝安全支付和WAP支付回调
 * @author jianjun.huo
 * @date 2012-6-25 下午06:12:46
 * @version v1.0
 * 
 */
@Controller
public class PayCallBackAction
{

	private final Log logger = LogFactory.getLog(PayCallBackAction.class);

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	/**
	 * 支付宝Wap : 回调接口
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/pay/alipayWapPayCallBackAlipay.do")
	public void alipayWapCallbackNotify(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			logger.info("+++++++Wap++alipayWapCallbackNotify++begin**************************++++++++++++");
			boolean verified = false;
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			// 获得通知参数
			Map map = request.getParameterMap();
			// 获得通知签名
			String sign = (String) ((Object[]) map.get("sign"))[0];
			// 获得通知数据
			String notify_data = (String) ((Object[]) map.get("notify_data"))[0];

			// 验签名
			verified = PaymentInfoGeneratorAlipayWap.checkAlipayWapSign(map, sign);
			logger.info("++WAP+alipayWapCallbackNotify+++verified=" + verified + "++++++++++++++");

			// 获取参数内容
			XMapUtil.register(AlipayWapParams.class);
			AlipayWapParams alipayWapParams = (AlipayWapParams) XMapUtil.load(new ByteArrayInputStream(notify_data.getBytes("UTF-8")));
			String tradeStatus = alipayWapParams.getTradeStatus();
			logger.info("++WAP+alipayWapCallbackNotify+++tradeStatus=" + tradeStatus + "++++++++++++++");

			// 封装 hessian数据
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("payRequestId", alipayWapParams.getOutTradeNo());
			sourceMap.put("proExternallId", alipayWapParams.getTradeNo());
			sourceMap.put("sucTrxAmount", alipayWapParams.getTotalFee());
			sourceMap.put("reqChannel", "MC");

			// 回调
			if (verified && ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)))
			{
				Map<String, String> rspMap = trxHessianServiceGateWay.complateTrx(sourceMap);

				if (null != rspMap && "1".equals(rspMap.get("rspCode")))
				{
					logger.info("++WAP+alipayWapCallbackNotify++++payRequestId:" + alipayWapParams.getOutTradeNo() + "->rspCode:" + rspMap.get("rspCode") + "++++++payBackSuccess+++++");
					out.print("success");
				}
			}
			logger.info("++WAP+alipayWapCallbackNotify++++payRequestId:" + alipayWapParams.getOutTradeNo() + "+++verified:" + verified + "++++payBackFailed+++++");
			out.flush();
			out.close();
			logger.error("+++++WAP+alipayWapCallbackNotify++++++END+++++++++++");

		} catch (Exception e)
		{
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 支付宝安全支付回调接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/pay/alipaySecurePayCallBackAlipay.do")
	public void alipaySecureCallbackNotify(HttpServletRequest request, HttpServletResponse response) 
	{
		try
		{
			logger.info("+++++++MC+alipaySecureCallbackNotify++begin**************************++++++++++++");
			boolean verified = false;
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			// 获得通知参数
			Map map = request.getParameterMap();
			// 获得通知签名
			String sign = (String) ((Object[]) map.get("sign"))[0];
			// 获得待验签名的数据
			String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
			logger.info("++MC+alipaySecureCallbackNotify+++sign=" + sign + "+++notify_data=" + notify_data + "+++++++++++++");

			// 验签
			verified = PaymentInfoGeneratorAlipaySecure.checkAlipaySecureSign("notify_data=" + notify_data, sign);
			logger.info("++MC+alipaySecureCallbackNotify+++verified=" + verified + "++++++++++++++");

			// 获取支付宝参数内容
			XMapUtil.register(AlipayWapParams.class);
			AlipayWapParams alipayWapParams = (AlipayWapParams) XMapUtil.load(new ByteArrayInputStream(notify_data.getBytes("UTF-8")));
			String tradeStatus = alipayWapParams.getTradeStatus();
			logger.info("++MC+alipaySecureCallbackNotify+++tradeStatus=" + tradeStatus + "++++++++++++++");
			// 封装 hessian数据
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("payRequestId", alipayWapParams.getOutTradeNo());
			sourceMap.put("proExternallId", alipayWapParams.getTradeNo());
			sourceMap.put("sucTrxAmount", alipayWapParams.getTotalFee());
			sourceMap.put("reqChannel", "MC");

			logger.info("+MC++alipaySecureCallbackNotify+++payRequestId=" + alipayWapParams.getOutTradeNo() + "+++proExternallId=" + alipayWapParams.getTradeNo() + "+++sucTrxAmount=" + alipayWapParams.getTotalFee() + "++++++++");

			// 回调
			if (verified && ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)))
			{
				Map<String, String> rspMap = trxHessianServiceGateWay.complateTrx(sourceMap);

				if (null != rspMap && "1".equals(rspMap.get("rspCode")))
				{
					logger.info("++MC+alipaySecurePayCallBackAlipay+payRequestId:" + alipayWapParams.getOutTradeNo() + "->rspCode:" + rspMap.get("rspCode") + "++++++payBackSuccess+++++");
					out.print("success");
				}
			}
			logger.info("++MC+alipaySecurePayCallBackAlipay++payRequestId:" + alipayWapParams.getOutTradeNo() + "+++verified:" + verified + "++++++payBackFailed!+++++");
			out.flush();
			out.close();
			logger.info("++++++MC+alipaySecureCallbackNotify++end**************************++++++++++++");
		} catch (Exception e)
		{
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

}
