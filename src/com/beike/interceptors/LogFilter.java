package com.beike.interceptors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.beike.util.WebUtils;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

public class LogFilter implements Filter {
	private static Logger log = Logger.getLogger(LogFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		// 访问者IP
		String ip = WebUtils.getIpAddr(request);
		// 访问的路径
		String path = request.getContextPath() + request.getServletPath();
		// 当前选择的城市
		String city = WebUtils.getCookieValue(CityUtils.CITY_COOKIENAME,
				request);
		// 当前登录者ID
		Long uid = SingletonLoginUtils.getLoginUserid(request);
		if (uid != null) {
			response.addCookie(WebUtils.cookie("uid", uid.toString(), -1));
		}
		StringBuffer buffer = new StringBuffer("");
		Enumeration<String> enume = request.getParameterNames();
		while (enume.hasMoreElements()) {
			String key = enume.nextElement();
			String[] value = request.getParameterValues(key);
			if (buffer.toString().length() > 0) {
				buffer.append(";");
			}
			buffer.append(key).append(":").append(Arrays.toString(value));
		}
		String cityStr = CityUtils.getCity(request, response);
		if (cityStr == null||"".equals(cityStr)) {
			cityStr = "";
		}
		request.setAttribute(CityUtils.CITY_COOKIENAME, cityStr);
		request.getSession().setAttribute(CityUtils.CITY_COOKIENAME, cityStr);
//		if (cityStr == null ||"".equals(cityStr)) {
//			cityStr = "beijing";
//		}
		request.setAttribute("QIANPIN_CITY", cityStr);
		if (!StringUtils.isBlank(cityStr)) {
			response.addCookie(WebUtils.cookie("city", cityStr, -1));
		}
		log.info("access_log,ip=" + ip + ",page=" + path + ",city=" + city
				+ ",uid=" + uid + ",parameter=" + buffer);
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
