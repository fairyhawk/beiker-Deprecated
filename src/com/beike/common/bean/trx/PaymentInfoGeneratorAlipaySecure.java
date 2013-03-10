package com.beike.common.bean.trx;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.HttpUtils;
import com.beike.util.StringUtils;
import com.beike.util.security.Base64;
import com.beike.util.security.RSASignature;

/**   
 * @title: PaymentInfoGeneratorAlipaySecure.java
 * @package com.beike.common.bean.trx
 * @description: 支付宝无线支付相关
 * @author wangweijie  
 * @date 2012-6-19 下午05:05:09
 * @version v1.0   
 */
public class PaymentInfoGeneratorAlipaySecure {
	
	
	private static final Log logger = LogFactory.getLog(PaymentInfoGeneratorAlipaySecure.class);
	
	//回调地址
	//private static final String CallbackURL = Configuration.getInstance().getValue("");  
	
	//通知地址
	private static final String NotifyURL = Configuration.getInstance().getValue("alipaySecureNotifyURL");	
	
	//查询接口地址
	private static final String AlipayQueryReqURL = Configuration.getInstance().getValue("alipayQueryReqURL");
	
	
	//退款地址
	//private static final String RefundURL = Configuration.getInstance().getValue("");	
	
	// 合作商户ID
	private static final String PARTNER = Configuration.getInstance().getValue("partner");
	
	//账户ID
	private static final String SELLER = PARTNER;

	//单笔交易查询接口
	private static final String SINGLE_TRADE_QUERY = "single_trade_query";		

	//签名类型，RSA可用
	private static final String SIGN_TYPE = "RSA";
	
	private static String MD5AlipayKey = Configuration.getInstance().getValue("alipayKeyValue");
   
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	/**
	 *
	 * @param orderNo  订单号
	 * @param goodsName 商品名称
	 * @param description 商品的具体描述信息
	 * @param amount 商品总价
	 * @return String
	 * @throws
	 */
	public static String getReqForSecurePayment(String outTradeNo,String goodsName,String description,String amount){
		if(StringUtils.isEmpty(description)){
			description = goodsName;
		}
		
		StringBuffer message = new StringBuffer("");
		message.append("partner=\"" + PARTNER +"\"");			//合作商户ID
		message.append("&seller=\"" + SELLER + "\"");	//账户ID 和 合作商户ID一样
		message.append("&out_trade_no=\"" + outTradeNo + "\"");		//商户自己产生的订单编号
		message.append("&subject=\"" + goodsName + "\"");	//商品名称
		
		message.append("&body=\"" + description + "\"");		//商品的具体描述信息
		message.append("&total_fee=\"" + amount +"\"");	//本次支付总费用，以元为单位
		message.append("&notify_url=\"" + NotifyURL + "\"");	//商家提供的URL地址。订单支付结束时，支付宝服务端会回调这个URL，通知商家本次支付结果
		
		
		String sign = "";
		try {
			sign = RSASignature.sign(message.toString(),AlipaySecureRSAKey.getPrivateKey());
			sign = URLEncoder.encode(sign,"UTF-8");
		} catch (Exception e) {
			logger.error("sign error:" + e);
			e.printStackTrace();
		};

		message.append("&sign=\"" + sign +"\"");	//签名
		message.append("&sign_type=\""+ SIGN_TYPE + "\"");		//加密类型RSA

		logger.info("++++++++++++message to alipay:"+message.toString());
		return message.toString();
		
	}
	
	/**
	 * 查询接口
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public static QueryResult queryByOrder(String outTradeNo) throws Exception {

		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("service", SINGLE_TRADE_QUERY); // 接口名称
		reqMap.put("partner", PARTNER); // 合作者身份ID
		reqMap.put("_input_charset", AlipayConfig.input_charset); // 字符编码格式
		//reqMap.put("trade_no", ""); // 支付宝交易号---------------------------------------
		reqMap.put("out_trade_no", outTradeNo); //
		
		String sign = AlipayMd5Encrypt.md5(createLinkString(reqMap) + MD5AlipayKey);
		reqMap.put("sign", sign);
		reqMap.put("sign_type", AlipayConfig.sign_type);

		try {
			Map<String, String> resMap = HttpUtils.URLGetAli(AlipayQueryReqURL, reqMap,AlipayConfig.input_charset);
			String result = "";
			logger.info("-------resMap" + resMap);
			if ("T".equals(resMap.get("is_success"))&&
					("TRADE_SUCCESS".equals(resMap.get("trade_status")) || "TRADE_FINISHED".equals(resMap.get("trade_status")))) {

				result = "SUCCESS";
			} else  {
				result = "ERROR";

			}
			QueryResult qr = new QueryResult();
			qr.setRb_PayStatus(result);
			qr.setR2_TrxId(resMap.get("out_trade_no"));
			qr.setR3_Amt(resMap.get("price"));
			qr.setR5_Pid(resMap.get("trade_no"));
			return qr;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * 支付宝安全支付：RSA验签名检查
	 *  add by  jianjun.huo
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @return 布尔值
	 * @throws Exception 
	 */
	public static boolean checkAlipaySecureSign(String content, String sign) throws Exception{
			// 支付宝公钥
			String publicKey = AlipaySecureRSAKey.getPublicKey();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
			signature.initVerify(pubKey);
			signature.update(content.getBytes("utf-8"));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

	}

	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
}
