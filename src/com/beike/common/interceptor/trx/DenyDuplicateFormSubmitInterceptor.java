package com.beike.common.interceptor.trx;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.beike.interceptors.BaseBeikeInterceptor;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;


/**
 * @Title: DenyDuplicateFormSubmitInterceptor.java
 * @Package com.beike.common.interceptor.trx
 * @Description:
 * @date Jun 14, 2011 10:51:05 PM
 * @author wh.cheng
 * @version v1.0
 */
public class DenyDuplicateFormSubmitInterceptor extends BaseBeikeInterceptor {
	private  static Log logger=LogFactory.getLog(DenyDuplicateFormSubmitInterceptor.class);
	private static final String UUID_TOKEN_KEY_NAME = "UUID_TOKEN_KEY";
	private static final String REPEAT_SUBMIT = "REPEAT_SUBMIT";

	private MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		boolean pathFlag=super.preHandle(request, response, handler);
		boolean flag = true;
		if(pathFlag){
		
		String token = request.getParameter(UUID_TOKEN_KEY_NAME);
		// if (token == null) {
		// 赋一个令牌
		// String UuidTokenKey = StringUtils.createUUID();
		// /memCacheService.set(UuidTokenKey, new Object(),1800);// 配对Token
		// 往客户端发送Token
		// request.setAttribute(UUID_TOKEN_KEY_NAME, UuidTokenKey);
		// } else {
		String tokenResult = (String) memCacheService.get(token);
		if (tokenResult != null && !"".equals(tokenResult)) {
			// 销毁TOKEN
			logger.info("+++++First Submit , Token is :"+token+"+++++++++");
			memCacheService.remove(token);
		} else {
			//flag = false;
			logger.info("++++++repeat Submit , Token is :"+token+"+++++++++");
			request.setAttribute(REPEAT_SUBMIT, "true");
			//throw new Exception("表单重复提交或过期，令牌[" + token + "]");
		}
		}
		return flag;
	}

	@Override
	protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
