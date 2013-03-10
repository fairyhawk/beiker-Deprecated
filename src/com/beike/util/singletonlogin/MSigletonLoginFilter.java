package com.beike.util.singletonlogin;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.beike.common.listener.CatlogListener;
import com.beike.entity.user.User;
import com.beike.util.Constant;
import com.beike.util.MCookieKey;
import com.beike.util.MUrlConstant;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.service.MUserService;

/**
 * <p>Title:wap用户单点登录过滤器 </p>
 * <p>Description: 
 * 	  从cookie里取出userkey
 *    去memcache里取出值 验证签名  验证成功后登录成功
 * </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jun 11, 2011
 * @author ye.tian
 * @version 1.0
 */

public class MSigletonLoginFilter implements Filter {
	
	private static final String SINGLETON_COOKIE_KEY="SINGLETON_COOKIE_KEY";
	
	@Autowired
	private static MUserService mUserService;
	
	private static MemCacheService memCacheService=MemCacheServiceImpl.getInstance();
	
	private static String REQUEST_URL=CatlogListener.M_REQUEST_URL;
	
	private static final String M_USER_CHECK_LOGIN = MUrlConstant.M_USER_CHECK_LOGIN;
	
	private static final String M_USER_NOT_CHECK_LOGIN = MUrlConstant.M_USER_NOT_CHECK_LOGIN;
	
	private String USER_LOGIN_URL = "/wap/user/toUserLogin.do";
	
	private String CITY_CHOOSE_URL = "/wap/city/mForward.do?param=index.index";
	
	private Log logger = LogFactory.getLog(this.getClass());

	public void destroy() {

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest) arg0;
		HttpServletResponse response=(HttpServletResponse) arg1;
		//访问过来的路径
		String contextPath=request.getContextPath();
		
//		String cityName = WebUtils.getCookieValue(MCookieKey.CITY_COOKIENAME, request);
//		logger.info("\n==================================== city  =  " + cityName );
//		if(cityName ==null || cityName.trim().equals(""))
//		{
//			response.sendRedirect(contextPath+CITY_CHOOSE_URL);
//			return;
//		}
		
		int vTime = 60 * 60 * 24;
		
		String requesturl=WebUtils.getRequestPath(request);
		if(!requesturl.toLowerCase().contains("login") && !requesturl.toLowerCase().contains("regist") 
				&& !requesturl.toLowerCase().contains("wap/user/service") && !requesturl.toLowerCase().contains("sendcoupon")
				&& !requesturl.toLowerCase().contains("buygoodsstep"))
		{
			WebUtils.setMCookieByKey(response, "REQUESTURI_REFER_COOKIE", requesturl, -1);
		}
		
		String monthState = WebUtils.getCookieValue(MCookieKey.MEM_MONTH_KEY_ID, request);
		if(monthState != null && monthState.equals("true"))
		{
			vTime = 60 * 60 * 24 * 30;
		}
		
		Long uid=SingletonLoginUtils.getLoginUserid(request);
		User user=(User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN+uid);
		if(user!=null){
			SingletonLoginUtils.addMSingleton(user, mUserService, user.getId()
					+ "", response, false, request, vTime);// TODO 暂时存5分钟
		}
		

		//假如不需要登录过滤的.do请求 直接往下走
		boolean flagNotCheck=isNeedFilter(request,M_USER_NOT_CHECK_LOGIN);
		if(flagNotCheck){
			arg2.doFilter(arg0, arg1);
			return ;
		}
		
		//判断用户是否需要检测登录
		boolean flag=isNeedFilter(request,M_USER_CHECK_LOGIN);
		
		
		//从cookie里取出uuid
		String uuid=WebUtils.getCookieValue(SINGLETON_COOKIE_KEY, request);
		if(uuid==null||"".equals(uuid)){
			//TODO:跳转到登录页面 或者 不需要登录 跳转到其他页面
			if(flag){
				response.sendRedirect(contextPath+USER_LOGIN_URL);
			}else{
				arg2.doFilter(arg0, arg1);
			}
			return ;
		}
		
		String sigletonValue=(String) memCacheService.get(uuid);
		if(sigletonValue==null||"".equals(sigletonValue)){
			//TODO:跳转到错误页面 或者 不需要登录 跳转到其他页面

			if(flag){
				response.sendRedirect(contextPath+USER_LOGIN_URL);
			}else{
				arg2.doFilter(arg0, arg1);
			}
			return ;
		}
		
		String preValue=sigletonValue.substring(0, sigletonValue.lastIndexOf("|")+1);
		String lastValue=sigletonValue.substring(sigletonValue.lastIndexOf("|")+1);
		
		String userid=sigletonValue.substring(0,sigletonValue.indexOf("|"));
		
		boolean isPassHmac=MobilePurseSecurityUtils.isPassHmac(lastValue,preValue,userid);
		if(!isPassHmac){
			//TODO:跳转到错误页面 或者 不需要登录 跳转到其他页面
			if(flag){
				response.sendRedirect(contextPath+USER_LOGIN_URL);
			}else{
				arg2.doFilter(arg0, arg1);
			}
			return ;
		}
		
		//登录成功 默认跳转到首页 或者从哪来的跳转到哪
		if(flag){
			
			arg2.doFilter(arg0, arg1);
		}
		else{
			//非过滤的路径  从哪来的滚回哪去
			String requesturi=getRequestPath(request);
			request.getRequestDispatcher(requesturi).forward(request, response);
		}
		return ;
	}
	
	
	/**
	 * 判断是否需要过滤
	 * @param request
	 * @return
	 */
	private boolean isNeedFilter(HttpServletRequest request,String ischeckStr){
		Map<String,Set<String>> setString=(Map<String,Set<String>>) memCacheService.get(REQUEST_URL);
		if(setString==null){
			setString=MUrlConstant.getRequestUrl();
		}
		boolean flag=false;
		String requesturi=request.getRequestURI();
		if(requesturi.indexOf("forward.do")!=-1){
			requesturi=getRequestPath(request);
		}
		String contextPath=request.getContextPath();
		if(requesturi.indexOf(contextPath)!=-1){
			requesturi=requesturi.substring(requesturi.indexOf(contextPath)+contextPath.length());
		}
		
		Set<String> user_check_login_url=setString.get(ischeckStr);
		if(user_check_login_url.contains(requesturi)){
			flag=true;
		}
		return flag;
		
	}
	
	private String getRequestPath(HttpServletRequest request){
		StringBuilder sb=new StringBuilder(request.getRequestURI());
		Enumeration  enumeration=request.getParameterNames();
		if(enumeration.hasMoreElements()){
			sb.append("?");
		}
		while(enumeration.hasMoreElements()){
			Object object=enumeration.nextElement();
			sb.append(object);
			sb.append("=");
			sb.append(request.getParameter(object.toString()));
			sb.append("&");
		}
		String requesturi="";
		String contextPath=request.getContextPath();
		if(sb.indexOf("&")!=-1){
			requesturi=sb.substring(0, sb.lastIndexOf("&"));
		}else{
			requesturi=sb.toString();
		}
		requesturi=requesturi.substring(requesturi.indexOf(contextPath)+contextPath.length());
		return requesturi;
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}
