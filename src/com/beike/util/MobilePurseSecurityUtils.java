package com.beike.util;

import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * <p>
 * Title:手机钱包安全工具类
 * </p>
 * <p>
 * Description:
 * <p>
 * Copyri staticght:
 * </p>
 * <p>
 * Company: 易宝支付
 * </p>
 * 
 * @author ye.tian
 * @version 1.0
 */
public class MobilePurseSecurityUtils {

	private static CryptUtil cryptUtil = new CryptUtilImpl();
	
	public static CryptUtil getCrypt(){
		return cryptUtil;
	}

	// 24位加密
	private final static int count = 24;

	/**
	 * 数据签名
	 * 
	 * @param key
	 *            加密key
	 * @param value
	 *            数据
	 * @return
	 */
	public static String hmacSign(String key, String value) {

		return Digest.hmacSign(value, key);
	}

	/**
	 * 将报文数据加密
	 * 
	 * @param value
	 *            报文加密后的字符串
	 * @return
	 */
	public static String secrect(String value, String key) {
		return cryptUtil.cryptDes(value, getKey(key));
	}

	public static String getKey(String key) {
		return key.substring(0, count);
	}

	/**
	 * 将报文数据解密
	 * 
	 * @param value
	 *            解密后原文
	 * @return
	 */
	public static String decryption(String value, String key) {
		return cryptUtil.decryptDes(value, getKey(key));
	}

	/**
	 * 验签是否通过
	 * 
	 * @param key
	 *            加密key
	 * @param value
	 *            报文数据
	 * @param secretValue
	 *            加密后的报文数据
	 * @return false 未通过 true 通过
	 */
	public static boolean isPassHmac(String secretValue, String key,
			String value) {
		String svalue = Digest.hmacSign(value, key);
		boolean flag = false;
		if (secretValue.equals(svalue)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 根据Map里面设定的值 转化成签名
	 * 
	 * @param map
	 *            相应返回的数据报文数据
	 * @param customerKey
	 *            customer自己的 hamcKey
	 */
	public static void generateHmac(Map<String, String> map, String customerKey) {
		StringBuilder sb = new StringBuilder();
		Set<String> set = map.keySet();
		for (String string : set) {
			String value = map.get(string);
			sb.append(value);
		}
		String hmacSign = hmacSign(customerKey, sb.toString());
		map.put("hmac", hmacSign);
	}

	/**
	 * 产生不重复号(如：订单号、批次号)
	 * 
	 * @param prefix
	 *            前缀
	 * @return 生成号
	 */
	public static synchronized String generateOrderNumber(String prefix) {
		StringBuffer sb = new StringBuffer();
		long time = System.currentTimeMillis();
		UUID uuid = UUID.randomUUID();
		String uu = uuid.toString().split("-")[0];
		sb.append(prefix);
		sb.append(time);
		sb = new StringBuffer(sb.substring(0, sb.toString().length() - 2));
		sb.append(uu);
		return sb.toString();
	}

	/**
	 * 生成hmac方法 业务类型
	 * 
	 * @param p0_Cmd
	 *            商户编号
	 * @param p1_MerId
	 *            商户订单号
	 * @param p2_Order
	 *            支付金额
	 * @param p3_Amt
	 *            交易币种
	 * @param p4_Cur
	 *            商品名称
	 * @param p5_Pid
	 *            商品种类
	 * @param p6_Pcat
	 *            商品描述
	 * @param p7_Pdesc
	 *            商户接收支付成功数据的地址
	 * @param p8_Url
	 *            送货地址
	 * @param p9_SAF
	 *            商户扩展信息
	 * @param pa_MP
	 *            银行编码
	 * @param pd_FrpId
	 *            应答机制
	 * @param pr_NeedResponse
	 *            商户密钥
	 * @param keyValue
	 * @return 生成签名
	 */
	public static String getReqMd5HmacForOnlinePayment(String p0_Cmd,
			String p1_MerId, String p2_Order, String p3_Amt, String p4_Cur,
			String p5_Pid, String p6_Pcat, String p7_Pdesc, String p8_Url,
			String p9_SAF, String pa_MP, String pd_FrpId,
			String pr_NeedResponse, String keyValue) {
		StringBuffer sValue = new StringBuffer();
		// 业务类型
		sValue.append(p0_Cmd);
		// 商户编号
		sValue.append(p1_MerId);
		// 商户订单号
		sValue.append(p2_Order);
		// 支付金额
		sValue.append(p3_Amt);
		// 交易币种
		sValue.append(p4_Cur);
		// 商品名称
		sValue.append(p5_Pid);
		// 商品种类
		sValue.append(p6_Pcat);
		// 商品描述
		sValue.append(p7_Pdesc);
		// 商户接收支付成功数据的地址
		sValue.append(p8_Url);
		// 送货地址
		sValue.append(p9_SAF);
		// 商户扩展信息
		sValue.append(pa_MP);
		// 银行编码
		sValue.append(pd_FrpId);
		// 应答机制
		sValue.append(pr_NeedResponse);

		String sNewString = null;

		sNewString = hmacSign(keyValue, sValue.toString());
		return sNewString;
	}

	/**
	 * callback 返回校验hmac方法
	 * 
	 * @param hmac
	 *            商户编号
	 * @param p1_MerId
	 *            业务类型
	 * @param r0_Cmd
	 *            支付结果
	 * @param r1_Code
	 *            易宝支付交易流水号
	 * @param r2_TrxId
	 *            支付金额
	 * @param r3_Amt
	 *            交易币种
	 * @param r4_Cur
	 *            商品名称
	 * @param r5_Pid
	 *            商户订单号
	 * @param r6_Order
	 *            易宝支付会员ID
	 * @param r7_Uid
	 *            商户扩展信息
	 * @param r8_MP
	 *            交易结果返回类型
	 * @param r9_BType
	 *            交易结果返回类型
	 * @param keyValue
	 * @return
	 */
	public static boolean verifyCallback(String hmac, String p1_MerId,
			String r0_Cmd, String r1_Code, String r2_TrxId, String r3_Amt,
			String r4_Cur, String r5_Pid, String r6_Order, String r7_Uid,
			String r8_MP, String r9_BType, String keyValue) {
		StringBuffer sValue = new StringBuffer();
		// 商户编号
		sValue.append(p1_MerId);
		// 业务类型
		sValue.append(r0_Cmd);
		// 支付结果
		sValue.append(r1_Code);
		// 易宝支付交易流水号
		sValue.append(r2_TrxId);
		// 支付金额
		sValue.append(r3_Amt);
		// 交易币种
		sValue.append(r4_Cur);
		// 商品名称
		sValue.append(r5_Pid);
		// 商户订单号
		sValue.append(r6_Order);
		// 易宝支付会员ID
		sValue.append(r7_Uid);
		// 商户扩展信息
		sValue.append(r8_MP);
		// 交易结果返回类型
		sValue.append(r9_BType);
		String sNewString = null;
		sNewString = hmacSign(keyValue, sValue.toString());
		if (hmac.equals(sNewString)) {
			return (true);
		}
		return (false);
	}

	public static boolean isJointMobileNumber(String mobileNumber){
		String pattern = "^(1([0-9]{10}))$";
		return mobileNumber.matches(pattern);
	}


	/**
	 * 判断手机号
	 */
	public static boolean isJointUserLoginName(String mobileNumber) {
		// 判断该用户是否是 手机用户
		return isJointMobileNumber(mobileNumber);
	}
	
	/**
	 * 验证邮箱
	 * @param value
	 * @param length	邮箱长度 默认不超过40
	 * @return
	 */
	public static boolean checkEmail(String value, int length) {
		if(length==0){
			length=40;
		}
		
		return value
				.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
				&& value.length() <= length;
	}
	
	public static String getRandomNumber(int i){
		StringBuilder sb=new StringBuilder(System.currentTimeMillis()+"");
		String str=sb.reverse().substring(0, i).toString();
		return str;
	}
	
	/**
	 * 验证字符是否为6-16为字符、数字或下划线组成
	 * @param password
	 * @return 
	 */
	public static boolean isPasswordAvailable(String password)
	{
		String partten = "^[_0-9a-zA-Z]{3,}$";
		boolean flag = password.matches(partten) && password.length() >=6 && password.length() <= 16;
		return flag;
	}
}
