package com.beike.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

	public static int getInt(HttpServletRequest request, String str) {
		String tempStr = request.getParameter(str);
		int tempInt = 0;
		if (tempStr == null || "".equals(tempStr)) {
			tempStr = "0";
		}
		try {
			tempInt = Integer.parseInt(tempStr);
		} catch (Exception e) {
			e.printStackTrace();
			tempInt = 0;
			e.printStackTrace();
		}
		return tempInt;
	}

	public static int getInt(HttpServletRequest request, String str,
			int defaultInt) {
		String tempStr = request.getParameter(str);
		int tempInt = 0;
		if (tempStr == null || "".equals(tempStr)) {
			tempInt = defaultInt;
			return tempInt;
		}
		try {
			tempInt = Integer.parseInt(tempStr);
		} catch (Exception e) {
			e.printStackTrace();
			tempInt = 0;
			e.printStackTrace();
		}
		return tempInt;
	}
	
	/**
	 * 获取非空的数字     
	 * @param request
	 * @param str
	 * @param defaultInt
	 * @return
	 */
	public static int getUnsignedInt(HttpServletRequest request, String str,
			int defaultInt) {
		String tempStr = request.getParameter(str);
		int tempInt = 0;
		if (tempStr == null || "".equals(tempStr)) {
			tempInt = defaultInt;
			return tempInt;
		}
	
		try {
			tempInt = Integer.parseInt(tempStr);
		} catch (Exception e) {
			e.printStackTrace();
			tempInt = 0;
			e.printStackTrace();
		}
		if(tempInt<0){
			tempInt = 0;
			
		}
		return tempInt;
	}
}
