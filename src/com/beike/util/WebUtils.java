package com.beike.util;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class WebUtils {

	public static final String METHOD_HEAD = "HEAD";

	public static final String METHOD_GET = "GET";

	public static final String METHOD_POST = "POST";

	public static final String HEADER_PRAGMA = "Pragma";

	public static final String HEADER_EXPIRES = "Expires";

	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final int SESSION_COOKIE = -1;

	public static final int FOREVER_COOKIE = 0x12cc0300;
	
	public static void main(String[] args) {
		String params="goodId=123&abacusoutsid=wb_sina_app0611_35482";
		System.out.println(replaceParams(params, "goodId"));
	}
	
	public static String  replaceParams(String params,String paramsName){
		if(params.startsWith(paramsName)){
			params=params.replaceAll("&?("+paramsName+"=[0-9]+)&?", "");
		}else{
			params=params.replaceAll("&?("+paramsName+"=[0-9]+)&?", "&");
			if(params.endsWith("&")){
				params=params.substring(0, params.lastIndexOf("&"));
			}
		}
		return params;
	}

	public static Cookie cookie(String key, String value, int validy) {
		if (key != null && value != null) {
			Cookie cookie = new Cookie(key, value);
			cookie.setMaxAge(validy);
			cookie.setPath("/");
			cookie.setDomain(".qianpin.com");
			return cookie;
		} else {
			return null;
		}
	}

	public static Cookie cookie(String key, String value, int validy,
			String domainName) {
		if (key != null && value != null) {
			Cookie cookie = new Cookie(key, value);
			cookie.setMaxAge(validy);
			cookie.setPath("/");
			if (domainName != null)
				cookie.setDomain(domainName);
			return cookie;
		} else {
			return null;
		}
	}

	/**
	 * 根据Key 值来获得移除的 Cookie
	 * 
	 * @param key
	 *            Add by zx.liu
	 */
	public static Cookie removeableCookie(String key) {
		if (key != null) {
			Cookie cookie = new Cookie(key, "");
			cookie.setMaxAge(-1);
			cookie.setPath("/");
			cookie.setDomain(".qianpin.com");
			return cookie;
		} else {
			return null;
		}
	}

	public static Cookie removeableCookie(String key, String domainName) {
		if (key != null) {
			Cookie cookie = new Cookie(key, "");
			cookie.setMaxAge(0);
			cookie.setPath("/");
			if (domainName != null)
				cookie.setDomain(domainName);
			return cookie;
		} else {
			return null;
		}
	}

	public static boolean areEquals(Cookie cookie1, Cookie cookie2) {
		return cookie1.getName().equals(cookie2.getName())
				&& (cookie1.getValue() != null ? cookie1.getValue().equals(
						cookie2.getValue()) : cookie2.getValue() == null)
				&& (cookie1.getMaxAge() == cookie2.getMaxAge()
						&& cookie1.getSecure() == cookie2.getSecure() && cookie1
						.getVersion() == cookie2.getVersion())
				&& (cookie1.getComment() != null ? cookie1.getComment().equals(
						cookie2.getComment()) : cookie2.getComment() == null)
				&& (cookie1.getDomain() != null ? cookie1.getDomain().equals(
						cookie2.getDomain()) : cookie2.getDomain() == null)
				&& (cookie1.getPath() != null ? cookie1.getPath().equals(
						cookie2.getPath()) : cookie2.getPath() == null);
	}

	public static final String getCookieValue(String key, HttpServletRequest req) {
		String str = null;
		Cookie cookies[] = req.getCookies();
		if (null == cookies)
			return null;
		for (int i = 0; i < cookies.length && str == null; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals(key))
				return cookie.getValue();
		}
		return null;
	}

	public static void applyCacheSeconds(HttpServletResponse response,
			int pageCacheSecond, boolean mustRevalidate) {
		if (pageCacheSecond > 0) {
			// HTTP 1.0 header
			response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis()
					+ pageCacheSecond * 1000L);
			// HTTP 1.1 header
			String headerValue = "max-age=" + pageCacheSecond;
			if (mustRevalidate) {
				headerValue += ", must-revalidate";
			}
			response.setHeader(HEADER_CACHE_CONTROL, headerValue);
		} else if (pageCacheSecond == 0) {
			response.setHeader(HEADER_PRAGMA, "No-cache");
			// HTTP 1.0 header
			response.setDateHeader(HEADER_EXPIRES, 1L);
			// HTTP 1.1 header: "no-cache" is the standard value,
			// "no-store" is necessary to prevent caching on FireFox.
			response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
			response.addHeader(HEADER_CACHE_CONTROL, "no-store");
		}
	}

	public static String parseQueryString(HttpServletRequest request) {
		Map map = request.getParameterMap();
		String result = "";
		for (Object o : map.keySet()) {
			String key = o + "";
			String[] values = (String[]) map.get(key);
			result += key + "=" + values[0] + "&";
		}
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	protected WebUtils() {
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getRequestPath(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder(request.getRequestURI());
		Enumeration enumeration = request.getParameterNames();
		if (enumeration.hasMoreElements()) {
			sb.append("?");
		}
		while (enumeration.hasMoreElements()) {
			Object object = enumeration.nextElement();
			sb.append(object);
			sb.append("=");
			sb.append(request.getParameter(object.toString()));
			sb.append("&");
		}
		String requesturi = "";
		String contextPath = request.getContextPath();
		if (sb.indexOf("&") != -1) {
			requesturi = sb.substring(0, sb.lastIndexOf("&"));
		} else {
			requesturi = sb.toString();
		}
		requesturi = requesturi.substring(requesturi.indexOf(contextPath)
				+ contextPath.length());
		return requesturi;
	}

	/**
	 * 记录手机wap端的cookie
	 * 
	 * @param response
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param validy
	 *            时间
	 */
	public static void setMCookieByKey(HttpServletResponse response,
			String key, String value, int validy) {
		Cookie cookieObj;
		Cookie cookieObjLocal;
		cookieObj = cookie(key, value, validy);
		response.addCookie(cookieObj);
		cookieObjLocal = cookie(key, value, validy, null);
		response.addCookie(cookieObjLocal);
	}

	/**
	 * 清除手机wap端cookie，包括清除千品域名cookie以及本地cookie
	 * 
	 * @param response
	 * @param key
	 *            cookie键值
	 */
	public static void removeMCookieByKey(HttpServletResponse response,
			String key) {
		Cookie cookieObj;
		Cookie cookieObjLocal;
		cookieObj = removeableCookie(key);
		response.addCookie(cookieObj);
		cookieObjLocal = removeableCookie(key, null);
		response.addCookie(cookieObjLocal);
	}
}
