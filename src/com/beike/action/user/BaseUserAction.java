package com.beike.action.user;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.user.User;
import com.beike.service.user.UserService;
import com.beike.util.Constant;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 15, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class BaseUserAction {
	
	private MemCacheService memCacheService=MemCacheServiceImpl.getInstance();
	
	private Log log=LogFactory.getLog(this.getClass());
	
	
	@Autowired
	private UserService  userService;
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 判断请求IP 为奇数还是偶数
	 * @param request
	 * @return  true 为偶数  false 为奇数
	 */
	protected boolean isIpEven(HttpServletRequest request){
		boolean isIpEven=false;
		try {
			String requestIP = WebUtils.getIpAddr(request);
			log.info("requestIP is :" + requestIP);
			String ipprefix = "";
			//有点没逗号  如 172.164.23.1
			if (requestIP.indexOf(".") != -1 && requestIP.indexOf(",") == -1) {
				ipprefix = requestIP.substring(requestIP.lastIndexOf(".") + 1);
			}
			//有点有逗号 如 172.164.23.1,172.164.23.2
			//取前面的ip 尾数
			else if (requestIP.indexOf(".") != -1
					&& requestIP.indexOf(",") != -1) {
				ipprefix = requestIP.split(",")[0].substring(requestIP
						.split(",")[0].lastIndexOf(".") + 1);
			}
			log.info("requestIP prefix is :" + ipprefix);
			if (!org.apache.commons.lang.StringUtils.isBlank(ipprefix)) {
				Long prefixNum = 0l;
				prefixNum = Long.parseLong(ipprefix);
				if(prefixNum%2==0){
					isIpEven=true;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return isIpEven;
	}
	
	
	/**
	 * 过滤 script 脚本攻击
	 * @param resquest
	 * @return
	 * @throws Exception 
	 */
	protected void replaceScript(HttpServletRequest resquest) throws Exception{
		Enumeration  e=resquest.getParameterNames();
		while(e.hasMoreElements()){
			String str=(String) e.nextElement();
			String value=resquest.getParameter(str);
			if(value.indexOf("script")!=-1||value.indexOf("javascript")!=-1||value.indexOf("eval")!=-1){
				String ip=WebUtils.getIpAddr(resquest);
				log.info("[疑似攻击],refer url:"+str+" 用户ip:"+ip);
				throw new Exception();
			}
		}
	}

	/**
	 * 获得请求路径
	 * @param request
	 * @param response
	 * @return
	 */
	protected void setCookieUrl(HttpServletRequest request,HttpServletResponse response){
		String requesturl=WebUtils.getRequestPath(request);
		Cookie requestUrlCookie=WebUtils.cookie("REQUESTURI_REFER_COOKIE", requesturl, -1);
		response.addCookie(requestUrlCookie);
	}

	/**
	 * 获得当前登录的用户
	 * @param request
	 * @return
	 */
	protected User getMemcacheUser(HttpServletRequest request){
		Long uid=SingletonLoginUtils.getLoginUserid(request);
		if(uid==null)return null;
		User user=(User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN+uid);
		if(user==null){
			user=userService.findById(uid);
		}
//		if(user!=null){
//			request.getSession().setAttribute(Constant.USER_LOGIN, user);
//		}
		return user;
	}
	
	//获得地域一级、地域二级 属性一级、地域一级的中文名称
	protected String getCatlogName(List<RegionCatlog> regionCatlogList,Long catlogId){
		RegionCatlog rc=new RegionCatlog();
		rc.setCatlogid(catlogId);
		if(regionCatlogList!=null&&regionCatlogList.size()>0&&regionCatlogList.indexOf(rc)!=-1){
			RegionCatlog rcc=regionCatlogList.get(regionCatlogList.indexOf(rc));
			if(rcc==null){
				return "";
			}
			return rcc.getCatlogName();	
		}
		return "";
		
	}
}
