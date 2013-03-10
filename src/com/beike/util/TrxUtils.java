package com.beike.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Title: TrxConstant.java
 * @Package com.beike.util
 * @Description: 交易相关常量
 * @date Jun 13, 2011 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxUtils {

	private static int timeout = 24 * 60 * 60;

	/**
	 * 写入交易流程中用户类型
	 * 
	 * @param response
	 * @param request
	 * @param trxLoinTypeKey
	 * @param trxLoinTypeValue
	 */
	public static void addTrxCookies(HttpServletResponse response,
			HttpServletRequest request, String trxLoginTypeKey,
			String trxLoginTypeValue) {

		Cookie cookie = WebUtils.cookie(trxLoginTypeKey, trxLoginTypeValue,
				timeout);
		response.addCookie(cookie);

	}

	/**
	 * 读取交易流程中用户类型
	 * 
	 * @param response
	 * @param request
	 * @param trxLoinTypeKey
	 * @param trxLoinTypeValue
	 */
	public static String getTrxCookies(HttpServletResponse response,
			HttpServletRequest request, String trxLoginTypeKey) {

		String trxLoginTypeValue = WebUtils.getCookieValue(trxLoginTypeKey,
				request);

		return trxLoginTypeValue;
	}

	/**
	 * 删除交易流程中登录类型Cookies
	 * 
	 * @param response
	 * @param request
	 * @param trxLoginTypeKey
	 */
	public static void removeTrxCookies(HttpServletResponse response,
			HttpServletRequest request, String trxLoginTypeKey) {

		Cookie cookie = WebUtils.removeableCookie(trxLoginTypeKey,
				".qianpin.com");

		response.addCookie(cookie);
	}

}
