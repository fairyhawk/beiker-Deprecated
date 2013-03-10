package com.beike.interceptors;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.beike.common.exception.UserException;
import com.beike.entity.log.UserLog;
import com.beike.service.log.BeikeLogService;
import com.beike.service.user.UserService;
import com.beike.util.Constant;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;

/**
 * <p>
 * Title:用户注册拦截器
 * </p>
 * <p>
 * Description:手机号注册、邮箱注册
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

public class UserRegistInterceptor extends BaseBeikeInterceptor {
	@Autowired
	private UserService userService;

	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);

	private boolean flag = false;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Resource(name = "userLogService")
	public void setBeikeLogService1(BeikeLogService beikeLogService) {
		super.setBeikeLogService(beikeLogService);
	}

	@Override
	protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request) {
		UserLog userLog = new UserLog();
		String data = paramdata(map);
		// userLog.setOperatedata(data);

		return userLog;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// flag 为true 需要拦截 false 不需要拦截
		flag = super.preHandle(request, response, handler);
		if (flag) {
			// 用户注册拦截
			// 1.判断用户 mobile、email、password格式是否正确
			// String
			// usermobile=request.getParameter(Constant.USER_MOBILE_REGIST);
			String useremail = request.getParameter(Constant.USER_EMAIL_REGIST);
			String password = request.getParameter(Constant.USER_PASSWORD);
			boolean emailValidateUser = true;
			// boolean mobileValidateUser=true;
			// 假如邮箱注册 首先判断验证码
			// String
			// userRegistCode=request.getParameter(Constant.USER_REGIST_CODE);
			// if(userRegistCode!=null){
			// String validateCode=(String)
			// request.getSession().getAttribute(Constant.USER_REGIST_CODE);
			// if(validateCode==null||!userRegistCode.equals(validateCode)){
			// request.setAttribute(Constant.USER_REGIST_CODE, "true");
			// return true;
			// }
			// }

			if (useremail == null || "".equals(useremail)) {
				request.setAttribute(Constant.USER_PARAM_VALIDATE_ERROR, "true");
				return true;
			}

			emailValidateUser = MobilePurseSecurityUtils.checkEmail(useremail,
					0);

			if (!emailValidateUser) {
				request.setAttribute(Constant.USER_PARAM_VALIDATE_ERROR, "true");
				return true;

			}
			// if(usermobile!=null&&!"".equals(usermobile)){
			// mobileValidateUser=MobilePurseSecurityUtils.isJointMobileNumber(usermobile);
			// }
			// 拦截手机或者email格式错误，设置标志交给action跳转

			// 验证密码格式是否有误
			if (password == null || "".equals(password)
					|| password.length() < 6) {
				request.setAttribute(Constant.USER_PASSWROD_VALIDATE_ERROR,
						"true");
				return true;
			}

			// 2.判断用户名 email是否存在
			boolean isExist = true;
			try {
				isExist = userService.isUserExist(null, useremail);
			} catch (UserException e) {
				isExist = false;
				e.printStackTrace();
			}
			if (isExist) {
				request.setAttribute(Constant.USER_EXIST, "true");
				return true;
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

}
