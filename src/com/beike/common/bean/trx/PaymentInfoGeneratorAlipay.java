package com.beike.common.bean.trx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.DateUtils;
import com.beike.util.HttpUtils;
import com.beike.util.StringUtils;

/**
 * @author yurenli 支付宝相关业务接口
 * 
 */
public class PaymentInfoGeneratorAlipay {

	private static Log log = LogFactory.getLog(PaymentInfoGeneratorAlipay.class);

	private static String partner = Configuration.getInstance().getValue(
			"partner");

	private static String alipayKeyValue = Configuration.getInstance()
			.getValue("alipayKeyValue");
	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static String ALIPAY_GATEWAY_NEW = Configuration.getInstance()
			.getValue("alipayCommonReqURL");

	private static String alipayCallbackURL = Configuration.getInstance()
			.getValue("alipayCallbackURL");
	
	private static String alipayCallbackNotifyURL = Configuration.getInstance()
	.getValue("alipayCallbackNotifyURL");

	private static String alipayrefundURL = Configuration.getInstance()
			.getValue("alipayrefundURL");
	
	/**
	 * 查询接口
	 */
	private static String alipayQueryReqURL = Configuration.getInstance()
			.getValue("alipayQueryReqURL");

	private static final String PAYMENT_TYPE = "1";

	// 交易接口
	private static final String SERVICE = "create_direct_pay_by_user";
	// 单笔查询
	private static final String SINGLE_TRADE_QUERY = "single_trade_query";
	// 退款标示
	private static final String REFUND_FASTPAY_BY_PLATFORM_NOPWD = "refund_fastpay_by_platform_nopwd";
	/**
	 * HTTPS形式消息验证地址
	 */
	private static final String HTTPS_VERIFY_URL = "https://www.alipay.com/cooperate/gateway.do?service=notify_verify&";
	/**
	 * HTTP形式消息验证地址
	 */
	private static final String HTTP_VERIFY_URL = "http://notify.alipay.com/trade/notify_query.do?";
	

	/**
	 * @param p2_Order支付请求号
	 * @param p3_Amt 交易金额
	 * @param p5_Pid 产品名称
	 * @param p6_Pcat 扩展信息
	 * @param p7_Pdesc 银行支付接口
	 * @param p9_SAF  扩展参数
	 * @param pa_MP   扩展参数
	 * @param pd_FrpId扩展参数
	 * @return
	 */
	public static String getReqMd5HmacForOnlinePayment(String p2_Order,
			String p3_Amt, String p5_Pid, String p6_Pcat, String p7_Pdesc,
			String p9_SAF, String pa_MP, String pd_FrpId) {
		
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", SERVICE); // 接口名称
		sParaTemp.put("partner", partner); // 合作者身份ID
		sParaTemp.put("_input_charset", AlipayConfig.input_charset); // 字符编码格式
																		// //不可空
		sParaTemp.put("notify_url", alipayCallbackNotifyURL); // 服务器异步通知页面路径 可为空（但不能空）
		sParaTemp.put("return_url", alipayCallbackURL); // 页面跳转同步通知页面路径
														// 可为空（但不能空）
		sParaTemp.put("seller_email", AlipayConfig.seller_email); // 卖家支付宝账号
																	// //可为空，但3个参数必有一个

		sParaTemp.put("out_trade_no", p2_Order); // 唯一订单号
		sParaTemp.put("subject", p5_Pid);// 订单名称，显示在支付宝收银台里的“商品名称”里
											// ************************!!
		sParaTemp.put("payment_type", PAYMENT_TYPE); // 支付类型,默认值为：1
		sParaTemp.put("body", p6_Pcat);// 订单备注，显示在支付宝收银台里的“商品描述”里
										// ******************多个商品名称******!!
		sParaTemp.put("total_fee", p3_Amt);// 订单总金额，显示在支付宝收银台里的“应付总额”里
		sParaTemp.put("show_url", ""); // 商品展示网址 可为空 *********************！！
		sParaTemp.put("paymethod", "");// 默认支付方式 可为空
		if("ALIPAY".equals(p7_Pdesc)){
			p7_Pdesc = "";
		}
		sParaTemp.put("defaultbank", p7_Pdesc);// 默认网银代号 可为空 如ICBC
		sParaTemp.put("anti_phishing_key", "");// 防钓鱼时间戳 可为空
		sParaTemp.put("exter_invoke_ip", "");// 客户端的IP地址 可为空
		sParaTemp.put("extra_common_param", "");// 自定义参数，可存放任何内容（除=、&等特殊字符外）
		sParaTemp.put("royalty_type", "");// 提成类型 可为空
		sParaTemp.put("royalty_parameters", ""); // 提成信息集 可为空
		sParaTemp.put("it_b_pay","30m");//超时时间

		Map<String, String> sPara = paraFilter(sParaTemp);

		String mysign = buildMysign(sPara);

		List<String> keys = new ArrayList<String>(sPara.keySet());

		StringBuffer sbalipay = new StringBuffer();// 请求参数

		sbalipay.append(ALIPAY_GATEWAY_NEW + "?");

		for (int i = 0; i < keys.size(); i++) {
			String name = keys.get(i);
			String value = sPara.get(name);
			sbalipay.append(name + "=" + value + "&");
		}
		sbalipay.append("sign=" + mysign + "&");
		sbalipay.append("sign_type=" + AlipayConfig.sign_type);

		return sbalipay.toString();
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 生成签名结果
	 * 
	 * @param sArray
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildMysign(Map<String, String> sArray) {
		String prestr = createLinkString(sArray); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + alipayKeyValue; // 把拼接后的字符串再与安全校验码直接连接起来
		String mysign = AlipayMd5Encrypt.md5(prestr);
		return mysign;
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

	
	/**
	 * 订单查询
	 * 新老账户更换 modify by wangweijie 2012-07-26 增加paymentId
	 *
	 * @param p2_Order
	 * @param paymentId
	 * @return
	 * @throws Exception
	 */
	public static QueryResult queryByOrder(String p2_Order) throws Exception {

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", SINGLE_TRADE_QUERY); // 接口名称
		sParaTemp.put("partner", partner); // 合作者身份ID
		sParaTemp.put("_input_charset", AlipayConfig.input_charset); // 字符编码格式
		sParaTemp.put("trade_no", ""); // 支付宝交易号---------------------------------------
		sParaTemp.put("out_trade_no", p2_Order); //

		Map<String, String> sPara = paraFilter(sParaTemp);
		String mysign = buildMysign(sPara);
		sParaTemp.put("sign", mysign);
		sParaTemp.put("sign_type", AlipayConfig.sign_type);

		Map<String, String> responseStr = null;
		try {
			responseStr = HttpUtils.URLGetAli(alipayQueryReqURL, sParaTemp);
			String result = "";
			if ("T".equals(responseStr.get("is_success"))&&"TRADE_SUCCESS".equals(responseStr.get("trade_status"))) {
				result = "SUCCESS";
			} else  {
				result = "ERROR";
			}
			QueryResult qr = new QueryResult();
			qr.setRb_PayStatus(result);
			qr.setR2_TrxId(responseStr.get("out_trade_no"));
			qr.setR3_Amt(responseStr.get("price"));
			qr.setR5_Pid(responseStr.get("trade_no"));
			return qr;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * 无密退款接口
	 * 
	 * @param p2_Order
	 * @param pb_TrxId
	 * @param p3_Amt
	 * @param p4_Cur
	 * @param p5_Desc
	 * @return
	 * @throws Exception
	 * 
	 * MODIFY by wangweijie 新老账号更改
	 */
	public static boolean refundByTrxId(String p2_Order, String pb_TrxId, String p3_Amt, String p4_Cur, String p5_Desc,String payRequestId,Long paymentId) throws Exception {
		boolean boo = false;
		//RefundResult rr = new RefundResult();
		String detailData = pb_TrxId + "^" + p3_Amt + "^" + p2_Order;// 原付款支付宝交易号^退款总金额^退款理由

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", REFUND_FASTPAY_BY_PLATFORM_NOPWD); // 接口名称
		
		boolean isNewPartner = isNewPartner(payRequestId,paymentId);
		log.info("+++payRequestId:"+payRequestId+"+++paymentId:"+paymentId+"++++isNewPartner:"+isNewPartner);
		/*
		 * modify wangweijie
		 */
		
		if(isNewPartner){
			sParaTemp.put("partner", partner); // 合作者身份ID
		}else{
			sParaTemp.put("partner", partner_old); // 合作者身份ID
		}
		/*
		 * end modify wangweijie
		 */
		sParaTemp.put("_input_charset", AlipayConfig.input_charset); // 字符编码格式
																		// //不可空
		sParaTemp.put("notify_url", alipayCallbackURL);// 服务器异步通知页面路径

		sParaTemp.put("refund_date", DateUtils.getStringDate());// 退款请求时间
		sParaTemp.put("batch_no", p2_Order);// 退款批次号
		sParaTemp.put("batch_num", "1");// 总笔数
		sParaTemp.put("detail_data", detailData);// 单笔数据集
		
		/*
		 * modify wangweijie
		 */
		
		String alipayMd5Key = isNewPartner?alipayKeyValue:alipayKeyValue_old;
		String mysign = AlipayMd5Encrypt.md5(getContent(sParaTemp,alipayMd5Key));
		/*
		 * end modify wangweijie
		 */
		sParaTemp.put("sign", mysign);
		sParaTemp.put("sign_type", AlipayConfig.sign_type);
		String refund = HttpUtils.refundAlipay(alipayrefundURL, sParaTemp);
		log.info("+++payRequestId:"+payRequestId+"+++paymentId:"+paymentId+"++++p2_Order:"+p2_Order+"+++refundReturn:"+refund);
		if (refund.contains("<is_success>T</is_success>")) {
			boo = true;
		} else if (refund.contains("<is_success>F</is_success>")) {
			boo = false;
		}

		return boo;
	}

	/**
	 * 退款部分参数组装
	 * 
	 * @param params
	 * @param privateKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String getContent(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		boolean first = true;
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);
			if (value == null || value.trim().length() == 0) {
				continue;
			}
			if (first) {
				prestr = prestr + key + "=" + value;
				first = false;
			} else {
				prestr = prestr + "&" + key + "=" + value;
			}
		}
		return prestr + privateKey;
	}

	/**
	 * 验证消息是否是支付宝发出的合法消息
	 * 
	 * @param params
	 *            通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verify(Map<String, String> params) {

		Map<String, String> sParaNew = paraFilter(params);// 过滤空值、sign与sign_type参数
		String mysign = buildMysign(sParaNew);// 获得签名结果
		
		String responseTxt = "true";
		if (params.get("notify_id") != null) {
			responseTxt = verifyResponse(params.get("notify_id"));
		}
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}
		// 写日志记录（若要调试，请取消下面两行注释）
		// String sWord = "responseTxt=" + responseTxt +
		// "\n notify_url_log:sign=" + sign + "&mysign="
		// + mysign + "\n 返回参数：" + AlipayCore.createLinkString(params);
		// AlipayCore.logResult(sWord);
		// 验证
		// responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
		// mysign与sign不等，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
		log.info("+++"+sParaNew+"+++++sign:"+sign+"++++++++newMysign:"+mysign+"+++");
		if (mysign.equals(sign) && responseTxt.equals("true")) {
			
			return true;
		} else {
			
			/**
			 * 新老账号验签
			 * add by wangweijie 2012-07-26
			 * begin
			 */
			mysign = buildMysignOld(sParaNew);// 获得签名结果
			if (params.get("notify_id") != null) {
				responseTxt = verifyResponseOld(params.get("notify_id"));
			}
			log.info("+++"+sParaNew+"+++++sign:"+sign+"++++++++oldMysign:"+mysign+"+++");
			if (mysign.equals(sign) && responseTxt.equals("true")) {
				
				return true;
			} 
			
			/**
			 * 新老账号验签
			 * add by wangweijie 2012-07-26
			 * end
			 */
			log.info("+++++old+new alipayActSignResult:false");
			return false;
		}
	}
	

	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * 
	 * @param notify_id
	 *            通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String verifyResponse(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		String transport = AlipayConfig.transport;
		String veryfy_url = "";
		if (transport.equalsIgnoreCase("https")) {
			veryfy_url = HTTPS_VERIFY_URL;
		} else {
			veryfy_url = HTTP_VERIFY_URL;
		}
		veryfy_url = veryfy_url + "partner=" + partner + "&notify_id="
				+ notify_id;

		return checkUrl(veryfy_url);
	}

	/**
	 * 获取远程服务器ATN结果
	 * 
	 * @param urlvalue
	 *            指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String checkUrl(String urlvalue) {
		String inputLine = "";

		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
			inputLine = "";
		}

		return inputLine;
	}

	public static void main(String[] args) throws Exception {

		// queryByOrder("QAPay00000306V");
		//refundByTrxId("", "", "", "", "");
	}
	
	
	/**
	 * 
	 * 新老账户更换新增代码
	 * add by wangweijie at 2012-07-23 
	 */
	private static String partner_old = Configuration.getInstance().getValue("partner_old");	//旧的partner
	private static String alipayKeyValue_old = Configuration.getInstance().getValue("alipayKeyValue_old");  //旧的MD5
	private static String specialPayRequestId = Configuration.getInstance().getValue("special_payRequestId");  //特殊的payRequestId,以分号分隔
	private static Set<String> newPayRequestIdList = new TreeSet<String>();
	private static String changerPaymentIdStr = Configuration.getInstance().getValue("changerPaymentId");
	private static long changerPaymentId = 0L;		//旧账号最后一笔paymentId
	
	static {
		String oldPayRequestIdArray[] = specialPayRequestId.split(";");		//旧账号产生的特殊的paymentId   TODO 需要在上线时,根据数据库内容修改payapiinfo.propertie文件的special_payRequestId字段
		if(null != oldPayRequestIdArray && oldPayRequestIdArray.length > 0){
			for(String payRequestId : oldPayRequestIdArray){
				newPayRequestIdList.add(payRequestId);
			}
		}
		
		if(!StringUtils.isEmpty(changerPaymentIdStr)){
			changerPaymentId = Long.parseLong(changerPaymentIdStr);
		}
	}
	
	
	/**
	 * 根据外部订单号，判断订单号是不是新账户的
	 *
	 * @param outTradeNo
	 * @return
	 */
	public static boolean isNewPartner(String payRequestId,Long paymentId){
		if(newPayRequestIdList.contains(payRequestId)){
			return true;
		}
		
		//如果payment的创建时间是在 账号变更后，则是新账号，否则是旧账号
		if(paymentId.intValue() > changerPaymentId){
			return true;
		}else{
			return false;
		}
	}
	

	
	/**
	 * 生成旧版本的签名结果
	 * @param sArray 要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildMysignOld(Map<String, String> sArray) {
		String prestr = createLinkString(sArray); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + alipayKeyValue_old; // 把拼接后的字符串再与安全校验码直接连接起来
		String mysign = AlipayMd5Encrypt.md5(prestr);
		return mysign;
	}
	
	
	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * 
	 * @param notify_id  通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String verifyResponseOld(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		String transport = AlipayConfig.transport;
		String veryfy_url = "";
		if (transport.equalsIgnoreCase("https")) {
			veryfy_url = HTTPS_VERIFY_URL;
		} else {
			veryfy_url = HTTP_VERIFY_URL;
		}
		veryfy_url = veryfy_url + "partner=" + partner_old + "&notify_id=" + notify_id;

		return checkUrl(veryfy_url);
	}
	
	/**
	 * end by wangweijie
	 */

}