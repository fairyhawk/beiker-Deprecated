package com.beike.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @ClassName: PartnerUtil
 * @Description: 分销商加密解密
 * @author yurenli
 * @date 2012-6-4 下午06:42:13
 * @version V1.0
 */
public class PartnerUtil {

	private static final Log logger = LogFactory.getLog(PartnerUtil.class);

	/**
	 * 分销商Des3Encryption加密
	 * 
	 * @param source
	 * @param partnerDesKeyFilePath
	 * @return
	 * @throws Exception
	 */
	public static String cryptDes(String source, String secretKey) {

		if (source == null || secretKey == null) {
			return null;
		}
		String retTemp = "";
		try {
			retTemp = URLEncoder.encode(source, "UTF-8");

			retTemp = DES58.encrypt(retTemp, secretKey);// des加密
			retTemp = new String(com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(retTemp.getBytes()));// base64加密
			retTemp = URLEncoder.encode(retTemp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("++++++++++++++++++URLEncoder.encode++++Exception+++++" + source);
			e.printStackTrace();
		}
		return retTemp;

	}

	/**
	 * 分销商Des3Encryption解密
	 * 
	 * @param source
	 * @param partnerDesKeyFilePath
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decryptDes(String source, String secretKey) {
		if (source == null || secretKey == null) {
			return null;
		}
		String decTemp = "";
		try {
			decTemp = URLDecoder.decode(source, "UTF-8");

			byte[] tmp = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(decTemp);// base64解密
			decTemp = DES58.decrypt(new String(tmp), secretKey);// des解密
			decTemp = URLDecoder.decode(decTemp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("++++++++++++++++++URLDecoder.decode++++Exception+++++");
			e.printStackTrace();
		}
		return decTemp;

	}

	public static void main(String[] args) throws Exception {
		String str = "ZDc3NWM3MDMyMDhjN2Y5MGQ4YjgxNTA0ODAyYWMyYzk1MzY3NzUyZTk1MWE0NmMwYzI4OTAxNDViNWE2YzgyNDZiMGMyMGIwNDY2YmQ4YTgwOWRlNjgwMWY3ZTRmYmMzYzgxZTNlYTI3YTk5MzA4MGRjMjIzMzhkYWFmZmVkN2E1MzY3NzUyZTk1MWE0NmMwYzI4OTAxNDViNWE2YzgyNGE3YjczNzA1MzVhNzg2MDA0ZGEyNTkxY2QwMmRhNTVkMmMyOWY3ZmMwZGNhNzM4ZWNiODI4ZmEwMTUxMTdmYWE3ZmIwN2YwMWE3NGZiYjJjYmZhZmVkMGMyNWVjMDgwNTM3OThkYWI0ZjcwZDhiMzAzMjQxNzZlNDBkNGJlYThlYmE5NDFjZjg1NDU3OGVkYmI1YWY0OWNkNTI0MTllOTFiZGM5MzE2MTRmOTdjZTljMWYyYzIwZjFiYzkyOTE5ZWY4ZDVhMjNhOTQ2YjQ5ZmIxOTgyNjQ1NzQ2NTU3YmYwZGZmZDAzNTI1MTk3M2FlMjRhNDIxMWIxOGIzNWRlZjJjNGNkODJiMjVkMGZkOTFjZDRlZGIxNWZkYjAzZjZiM2EyYjlhOTAxNWMxNmJmOTgzMTBiYjgwMTE5ZDBiODBmMGU2OTVmMGU5MGI1YjcxZmZlMTU5OGVkOTQ1NzY1ZTc0NTY2MmJiMWM3NTM1MjZjYWE5OTc3ODU0ZGYxMDg1MzBkMGY1MWJiNTYxYjk0YmY2MmY1NzRlMTAxZTY3ZTMxNmRhNmRhZDZhNmU5YzdlZGYxN2FiNmY0ZjA5YmIwMDNlNmFjZWY3ZmEwYWYyOGY4NGExNDNjYWE5ZWQ4ZTFmNDM0N2M1MzMxMTEzNjQ2ZGNjYmUwODI0Y2QyNTJlOTAzNzZjY2FmMmMwNjllZTkxYjEwNjIyMTBlMGYyNjdjMGY5NzZkNWM4MDVjOGYwZDE3NmQ2ZTJkZTI2Y2FiMjcxYWZiZDlkYzZiNzEyYmU1YjYzZDQ1MzY0YTk3MWFlZDQ5YjJmNmU4MzJmOGJhMzQ5NzBmOGQxODk4MTVkYzk3ZTc5MjMzMTIxMjk1ZTNiM2MyYTNmZmVlODc1NDI0OGU2MDNiODU3YTFkYzdmYjg1NDI5ZmFmYmU0OGRiMDc2ODk0ZjMzN2Y1MWEzZDE3NmE0MDNmNzUyYjliZmFjYzFiMjEyMjZhOGNhMjIxOTM3YmE0";
		String str1 = decryptDes(str, "4c21d3d0b88e451aa8516e8894a85cfe");
		System.out.println(str1);
	}

	/**
	 * 对字符串进行MD5签名,首尾放secret。
	 * 
	 * @param params明文
	 * 
	 * @return 密文
	 */

	public static String md5Signature(TreeMap<String, String> params, String secret) {

		String result = null;

		StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));

		if (orgin == null) {
			return result;
		}
		orgin.append(secret);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 对字符串进行MD5验签,首放secret，尾部不放secret。
	 * 
	 * @param params明文
	 * 
	 * @return 密文
	 */

	public static String md5SignatureSecret(TreeMap<String, String> params, String secret) {

		String result = null;

		StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));

		if (orgin == null) {
			return result;
		}
		// orgin.append(secret);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("GBK")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
		return result;
	}

	public static String md5SignatureSecret(String params, String appKey, String privateKey) {
		String result = null;
		if (StringUtils.isEmpty(params)) {
			return result;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(appKey).append("|").append(privateKey).append("|").append(params);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(sb.toString().getBytes("GBK")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 二行制转字符串
	 */
	private static String byte2hex(byte[] b) {

		StringBuffer hs = new StringBuffer();

		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toUpperCase();
	}

	/**
	 * 
	 * 添加参数的封装方法
	 */

	private static StringBuffer getBeforeSign(TreeMap<String, String> params, StringBuffer orgin) {

		if (params == null)

			return null;

		Map<String, String> treeMap = new TreeMap<String, String>();

		treeMap.putAll(params);

		Iterator<String> iter = treeMap.keySet().iterator();

		while (iter.hasNext()) {

			String name = (String) iter.next();

			orgin.append(name).append(params.get(name));

		}

		return orgin;

	}

	/**
	 * 数据加密
	 * 
	 * @param sArray
	 * @return
	 */
	public static String buildMysign(Map<String, String> sArray) {
		String prestr = createLinkString(sArray); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + "123qwe456rty"; // 把拼接后的字符串再与安全校验码直接连接起来
		String mysign = md5(prestr);
		return mysign;
	}

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

	public static String md5(String text) {

		return DigestUtils.md5Hex(getContentBytes(text, "utf-8"));

	}

	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}

		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}
}
