package com.beike.common.bean.trx;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.HttpUtils;
import com.beike.util.StringUtils;

/**
 * @title: PaymentInfoGeneratorAlipayWap.java
 * @package com.beike.common.bean.trx
 * @description: 支付宝wap支付，支付、查询、退款接口
 * @author wangweijie
 * @date 2012-6-25 下午02:12:46
 * @version v1.0
 */
public class PaymentInfoGeneratorAlipayWap
{
	private static final Log log = LogFactory.getLog(PaymentInfoGeneratorAlipayWap.class);

	private static final String PARTNER = Configuration.getInstance().getValue("partner"); // 合作伙伴ID

	private static final String ALIPAY_SELLER = Configuration.getInstance().getValue("alipaySeller"); // 卖家支付宝账户名

	private static final String SERVICE_WAPTRADE_CREATE = "alipay.wap.trade.create.direct"; // 交易创建接口
	// /*alipay.wap.auth.authAndExecute
	// 交易授权接口*/

	// 查询接口地址
	private static final String AlipayQueryReqURL = Configuration.getInstance().getValue("alipayQueryReqURL");

	private static final String SECID = "MD5"; // 签名算法

	private static final String NotifyUrl = Configuration.getInstance().getValue("alipayWapNotifyURL"); // 通知地址

	private static final String PrivateKey = Configuration.getInstance().getValue("alipayKeyValue"); // MD5
	// key

	private static final String CallBackUrl = Configuration.getInstance().getValue("alipayWapReturnURL"); // 页面跳转回调地址

	// 单笔交易查询接口
	private static final String SINGLE_TRADE_QUERY = "single_trade_query";

	// MD5 key
	private static String MD5AlipayKey = Configuration.getInstance().getValue("alipayKeyValue");



	/**
	 * alipayWAp验签
	 *  add by jianjun.huo
	 * 
	 * @param map
	 * @param sign
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkAlipayWapSign(Map map, String sign) throws Exception
 {
		boolean flag = false;

		String verifyData = getAlipayWapVerifyData(map);

		String tosign = (verifyData == null ? "" : verifyData) + MD5AlipayKey;

		String mySign = DigestUtils.md5Hex(tosign.getBytes("utf-8"));
		flag =StringUtils.equals(mySign, sign) ? true : false;
		log.info("+++verifyData:" + verifyData + "++++tosign:" + tosign+ "+++mySign" + mySign);

		return flag;
	}

	/**
	 * 获得验签名的数据 add by jianjun.huo
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String getAlipayWapVerifyData(Map map)
	{
		String service = (String) ((Object[]) map.get("service"))[0];
		String v = (String) ((Object[]) map.get("v"))[0];
		String sec_id = (String) ((Object[]) map.get("sec_id"))[0];
		String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
		log.info("+++ parms：" + "service=" + service + "&v=" + v + "&sec_id=" + sec_id + "&notify_data=" + notify_data);
		return "service=" + service + "&v=" + v + "&sec_id=" + sec_id + "&notify_data=" + notify_data;
	}

	/**
	 * 获得支付宝wap接入支付请求数据
	 * 
	 * @return
	 */
	public static String getReqForAlipayWapPayment(String requestId, String subject, String orderNo, String totalFee, String cashierCode)
	{
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("service", SERVICE_WAPTRADE_CREATE);
		paramMap.put("req_id", requestId);
		paramMap.put("format", "xml");
		paramMap.put("partner", PARTNER);
		paramMap.put("sec_id", SECID);
		paramMap.put("v", "2.0");

		StringBuilder reqData = new StringBuilder("");
		reqData.append("<direct_trade_create_req>");
		reqData.append("<subject>" + subject + "</subject>");					//商品名称
		reqData.append("<out_trade_no>" + orderNo + "</out_trade_no>");	//外部交易号
		reqData.append("<total_fee>" + totalFee + "</total_fee>");			//订单价格
		reqData.append("<seller_account_name>" + ALIPAY_SELLER + "</seller_account_name>");		//卖家账号
		reqData.append("<notify_url>" + NotifyUrl + "</notify_url>");		//商户接收通知url
		reqData.append("<call_back_url>" + CallBackUrl + "</call_back_url>");	//支付成功跳转链接（只有交易成功后，才会跳转该链接）
		//reqData.append("<out_user></out_user>");		//商户系统唯一编号（不必传）
		//reqData.append("<merchant_url></merchant_url>");	//用户在支付宝页面可返回商户页面的链接（不必传）
		
		if(!StringUtils.isEmpty(cashierCode) && !cashierCode.equals("ALIPAY_WAP")){
			reqData.append("<cashier_code>"+cashierCode+"</cashier_code>");			//支付前置银行代码 

		}
		reqData.append("</direct_trade_create_req>");
		paramMap.put("req_data", reqData.toString());

		String sign = AlipayMd5Encrypt.md5(createLinkString(paramMap) + PrivateKey);

		paramMap.put("sign", sign); // 签名

		String resultMsg = "";
		try
		{
			resultMsg = mapToUrl(paramMap);
		} catch (UnsupportedEncodingException e)
		{
			log.error(e);
		}
		return resultMsg;
	}

	/**
	 * 查询接口
	 * 
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public static QueryResult queryByOrder(String outTradeNo) throws Exception
	{

		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("service", SINGLE_TRADE_QUERY); // 接口名称
		reqMap.put("partner", PARTNER); // 合作者身份ID
		reqMap.put("_input_charset", AlipayConfig.input_charset); // 字符编码格式
		// reqMap.put("trade_no", ""); //
		// 支付宝交易号---------------------------------------
		reqMap.put("out_trade_no", outTradeNo); //

		String sign = AlipayMd5Encrypt.md5(createLinkString(reqMap) + MD5AlipayKey);
		reqMap.put("sign", sign);
		reqMap.put("sign_type", AlipayConfig.sign_type);

		try
		{
			Map<String, String> resMap = HttpUtils.URLGetAli(AlipayQueryReqURL, reqMap, AlipayConfig.input_charset);
			String result = "";
			/*
			 * TRADE_FINISHED：支付成功，不能退款 TRADE_SUCCESS：支付成功，可以退款
			 */
			if ("T".equals(resMap.get("is_success")) && ("TRADE_SUCCESS".equals(resMap.get("trade_status")) || "TRADE_FINISHED".equals(resMap.get("trade_status"))))
			{
				result = "SUCCESS";
			} else
			{
				result = "ERROR";
			}
			QueryResult qr = new QueryResult();
			qr.setRb_PayStatus(result);
			qr.setR2_TrxId(resMap.get("out_trade_no"));
			qr.setR3_Amt(resMap.get("price"));
			qr.setR5_Pid(resMap.get("trade_no"));
			return qr;
		} catch (Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params)
	{

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++)
		{
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1)
			{// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else
			{
				prestr = prestr + key + "=" + value + "&";
			}
		}
		System.out.println("######################" + prestr);
		return prestr;
	}

	/**
	 * 将Map中的数据组装成url
	 * 
	 * @param params
	 * @return 返回url参数格式数据：如sec_id=MD5&v=2.0
	 * @throws UnsupportedEncodingException
	 */
	public static String mapToUrl(Map<String, String> params) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (String key : params.keySet())
		{
			String value = params.get(key);
			if (isFirst)
			{
				sb.append(key + "=" + URLEncoder.encode(value, "utf-8"));
				isFirst = false;
			} else
			{
				if (value != null)
				{
					sb.append("&" + key + "=" + URLEncoder.encode(value, "utf-8"));
				} else
				{
					sb.append("&" + key + "=");
				}
			}
		}
		return sb.toString();
	}
}
