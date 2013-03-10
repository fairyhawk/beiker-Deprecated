package com.beike.interceptors;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.beike.common.exception.UserException;
import com.beike.service.user.UserService;
import com.beike.util.Constant;
import com.beike.util.MobilePurseSecurityUtils;

/**
 * <p>
 * Title: 用户登录拦截器
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public class UserLoginInterceptor extends BaseBeikeInterceptor {

	@Autowired
	private UserService userService;

	@Override
	protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request) {

		return null;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		boolean flag = super.preHandle(request, response, handler);
		if (flag) {

			String username = request
					.getParameter(Constant.USER_LOGIN_USERNAME);
			String password = request.getParameter("USER_PASSWORD");

			if (username == null || "".equals(username)) {
				request.setAttribute("USERNAME_VALIDATE_ERROR", "true");
				return true;
			}
			if (password == null || "".equals(password)) {
				request.setAttribute("PASSWORD_VALIDATE_ERROR", "true");
				return true;
			}

			boolean emailValidateUser = MobilePurseSecurityUtils.checkEmail(
					username, 0);

			String useremail = "";
			String usermobile = "";
			if (emailValidateUser) {
				useremail = username;
				request.setAttribute("isemail", "true");
			} else {
				usermobile = username;
			}

			// 验证密码格式是否有误
			if (password != null && !"".equals(password)
					&& password.length() < 6) {
				request.setAttribute(Constant.USER_PASSWROD_VALIDATE_ERROR,
						"true");
				return true;
			}

			// 2.判断用户名 mobile、email是否存在
			boolean isExist = true;
			try {
				isExist = userService.isUserExist(usermobile, useremail);
			} catch (UserException e) {
				isExist = false;
				e.printStackTrace();
			}
			if (isExist) {
				request.setAttribute(Constant.USER_EXIST, "true");
			}

		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// super.afterCompletion(request, response, handler, ex);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
