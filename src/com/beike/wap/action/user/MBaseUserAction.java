package com.beike.wap.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.beike.entity.user.User;
import com.beike.util.Constant;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;
import com.beike.wap.service.MUserService;

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
public class MBaseUserAction {
	
	private MemCacheService memCacheService=MemCacheServiceImpl.getInstance();
	
	private Log log=LogFactory.getLog(this.getClass());
	
	
	@Autowired
	private MUserService  mUserService;
	
	public MUserService getUserService() {
		return mUserService;
	}

	public void setUserService(MUserService mUserService) {
		this.mUserService = mUserService;
	}
	
	/**
	 * 获得请求路径
	 * @param request
	 * @param response
	 * @return
	 */
	protected void setCookieUrl(HttpServletRequest request,HttpServletResponse response){
		String requesturl=WebUtils.getRequestPath(request);
		WebUtils.setMCookieByKey(response, "REQUESTURI_REFER_COOKIE", requesturl, -1);
//		Cookie requestUrlCookie=WebUtils.cookie("REQUESTURI_REFER_COOKIE", requesturl, 60*3);
//		response.addCookie(requestUrlCookie);
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
			user=mUserService.findById(uid);
		}
//		if(user!=null){
//			request.getSession().setAttribute(Constant.USER_LOGIN, user);
//		}
		return user;
	}
	
	
}
