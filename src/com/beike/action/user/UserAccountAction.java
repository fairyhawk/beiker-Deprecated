package com.beike.action.user;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.persistence.Memento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.UserException;
import com.beike.entity.common.Sms;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.service.user.UserSecurityService;
import com.beike.service.user.UserService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title: 用户账户中心
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
 * @date May 12, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class UserAccountAction extends BaseUserAction {

	static final Map<String, String> securityCodeURL = new HashMap<String, String>();
	static {
		securityCodeURL.put("updatePassword", "updatePassword");
		securityCodeURL.put("updateMobile", "updateMobile");
		securityCodeURL.put("updateEmail", "updateEmail");
	}
	private final Log log = LogFactory.getLog(UserAccountAction.class);

	@Autowired
	private UserSecurityService userSecurityService;
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);
	@Autowired
	private SmsService smsService;
	private static final String SMS_TYPE = "15";
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	@RequestMapping("/forward.do")
	public String userForward(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String param = request.getParameter("param");
		String cityStr = CityUtils.getCity(request, response);
		String clientIp = WebUtils.getIpAddr(request);
		Map<String, Integer> map = (Map<String, Integer>) memCacheService
				.get("LOGIN_IP");
		Integer times = 0;
		if (map != null) {
			times = map.get(clientIp);
			if (times != null && times >= 3) {
				request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			}
		}
		if (!"login".equals(param) && !"regist".equals(param)
				&& !"forgetpassword".equals(param)) {
			String requesturl = WebUtils.getRequestPath(request);
			Cookie requestUrlCookie = WebUtils.cookie(
					"REQUESTURI_REFER_COOKIE", requesturl, 60 * 3);
			response.addCookie(requestUrlCookie);
		}
		/**
		 * ********* 注册抽奖页面跳转特殊加山寨处理开始(跳转回去的地址是个蒙窗)，活动下线后即可删除。没时间了，没时间了。有问题找陆总监
		 * ***********
		 */
		if ("regLotteryLogin".equals(param)
				|| "regLotteryBinging".equals(param)) {
			String requesturl = "/lotteryReg/lotteryRegAction.do?command=showLotteryRegInfo";
			Cookie requestUrlCookie = WebUtils.cookie(
					"REQUESTURI_REFER_COOKIE", requesturl, 60 * 3);
			response.addCookie(requestUrlCookie);

			param = "regLotteryLogin".equals(param) ? "login" : "updateMobile";

		}
		/** ******** 注册抽奖页面跳转特殊加山寨处理结束 ******** */
		if (param.indexOf(".") != -1) {
			// 增加日志2012-01-17
			if ("index.index".equals(param)) {
				Map<String, String> mapLog = LogAction.getLogMap(request,
						response);
				mapLog.put("action", "p_index");
				LogAction.printLog(mapLog);
				String abacusoutsid = request.getParameter("abacusoutsid");
				if ("".equals(cityStr.trim())
						&& ("web_360_KZ".equals(abacusoutsid)
								|| "web_360_MZ".equals(abacusoutsid)
								|| "web_sogou_KZ".equals(abacusoutsid) || "web_hao123_KZ"
								.equals(abacusoutsid))) {
					log.info("redirect normal page ");
					StringBuilder sb = new StringBuilder(
							"http://www.qianpin.com/huodong/recommend/index.jsp");
					String params = WebUtils.parseQueryString(request);
					if (!StringUtils.isBlank(params)) {
						sb.append("?");
						sb.append(params);
					}
					return "redirect:" + sb.toString();
				}

			}
			
			String prefix = param.substring(0, param.indexOf("."));
			String after = param.substring(param.indexOf(".") + 1);
			return "templates/" + after;
		} else {
			// 百度推广登录后跳转至我的订单 add by qiaowb 2012-03-05
			String tn = request.getParameter("tn");
			String baiduid = request.getParameter("baiduid");
			if ("login".equals(param) && tn != null && !"".equals(tn)
					&& "baidutuan_tg".equals(tn) && baiduid != null
					&& !"".equals(baiduid)) {
				// 判断用户是否登录
				User user = SingletonLoginUtils.getMemcacheUser(request);
				if (user == null) {
					// 百度推广
					request.setAttribute("baidu_access", "Y");
					Cookie requestUrlCookie = WebUtils
							.cookie(
									"REQUESTURI_REFER_COOKIE",
									"/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED",
									-1);
					response.addCookie(requestUrlCookie);
				} else {
					return "redirect:/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED";
				}
			}
			User user = SingletonLoginUtils.getMemcacheUser(request);

			String sign = request.getParameter("sign");

			// 用户通过验证邮件后,跳过安全验证
			if (user != null && user.getMobile() != null
					&& user.getMobile_isavalible() == 1
					&& securityCodeURL.containsKey(param)
					&& !com.beike.util.StringUtils.validNull(sign)
					&& userSecurityService.getUserBySign(sign) == null) {
				log.info("redirect to security check");
				return "redirect:/user/goToSecurityCheck.do?param=" + param;
			}

			if (com.beike.util.StringUtils.validNull(sign)) {
				Map signInfo = userSecurityService.getUserBySign(sign);
				if (signInfo != null) {
					Long userid = (Long) signInfo.get("userid");
					User findUser = userService.findById(userid);
					if (findUser != null) {
						// 默认用户通过安全校验
						memCacheService.set("SECURITY_USER_" + findUser.getId()
								+ "verified", "y");

						userSecurityService.updateSign(sign);
					}
				}
			}
			
			if("login".equals(param)&&user!=null){
				return "redirect:/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED";
			}
			return "user/" + param;
		}
	}

	/**
	 * 功能：修改手机号码，填写完验证码，手机号跳到此action
	 * 
	 * 访问页面名称:
	 * 
	 * /jsp/user/updateMobile.jsp
	 * 
	 * 访问路径: /user/validateUpdateMobileSms.do
	 * 
	 * 跳转页面名称: 1. 输入有误 跳转地址:/jsp/user/updateMobile.jsp 2. 成功后 跳转地址
	 * 用户中心：/jsp/user/updateMobile.jsp
	 * 
	 * 输入参数: 1.手机号:mobile 2.验证码: validateCode
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * USER_MOBILE_ERROR: 用户手机号格式错误
	 * 
	 * USER_TIMEOUT:用户操作超时
	 * 
	 * USER_VALIDATECODE_ERROR:手机验证码验证错误
	 * 
	 * 2.UPDATEMOBILE 修改手机成功
	 * 
	 */
	@RequestMapping("/user/validateUpdateMobileSms.do")
	public Object validateUpdateMobile(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String mobile = request.getParameter("mobile");
		String validateCode = request.getParameter("validateCode");

		boolean flag = MobilePurseSecurityUtils.isJointMobileNumber(mobile);
		User user = getMemcacheUser(request);
		if (user == null) {
			request.setAttribute("ERRMSG", "用户操作超时,请重新操作!");

			return new ModelAndView("redirect:../500.html");
		}
		if (!flag) {
			request.setAttribute("ERRMSG", "USER_MOBILE_ERROR");
			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			request.setAttribute("mobile", user.getMobile());
			request.setAttribute("fromPage", "updateMobile");
			return "user/securityCheck";
		}
		// 旧手机号 by janwen
		String old_mobile_phone = user.getMobile();
		if (mobile == null || "".equals(mobile)) {
			request.setAttribute("ERRMSG", "USER_MOBILE_ERROR");
			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			request.setAttribute("mobile", user.getMobile());
			request.setAttribute("fromPage", "updateMobile");
			return "user/securityCheck";
		}

		Map<String, String> map = (Map<String, String>) memCacheService
				.get("REGIST_RANDOMNUMBER_" + user.getId());
		if (validateCode == null || map == null) {
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "USER_TIMEOUT");
			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			return "redirect:../500.html";
		}
		log.info("map..." + map.size() + "mobile...." + mobile);
		String mobileCode = map.get(mobile);
		String mc = null;
		if (mobileCode != null) {
			log.info("mobileCode:" + mobileCode);
			mc = mobileCode.split(":")[1];
			if (mc == null) {
				request.setAttribute("ERRMSG", "USER_TIMEOUT");

				SingletonLoginUtils.addSingleton(user, userService, user
						.getId()
						+ "", response, false, request);
				return new ModelAndView("redirect:../500.html");
			}
		}
		log.info("mc:" + mc + "--->validateCode:" + validateCode);
		if (validateCode == null || "".equals(validateCode) || mc == null
				|| !mc.equals(validateCode)) {
			request.setAttribute("ERRMSG", "USER_VALIDATECODE_ERROR");

			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			request.setAttribute("mobile", user.getMobile());
			request.setAttribute("fromPage", "updateMobile");
			return "user/securityCheck";
		}

		// 手机号码已经验证的,必须验证是否通过安全校验 by janwen
		if ((user.getMobile_isavalible() == 1)
				&& !"y".equals(memCacheService
						.get(UserSecurityAction.SECURITY_PREFIX + user.getId()
								+ "verified"))) {
			return "redirect:/forward.do?param=updateMobile";
		}
		// 清除验证码缓存
		memCacheService.remove("REGIST_RANDOMNUMBER_" + user.getId());

		user.setMobile(mobile);
		user.setMobile_isavalible(1);
		user.setIsavalible(1);
		try {
			userService.updateUserMessage(user);
		} catch (UserException e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "系统繁忙,请重试!");
			return new ModelAndView("redirect:../500.html");
		}

		// 打印日志
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		logMap.put("action", "MobileAuthInSite");
		LogAction.printLog(logMap);

		SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
				response, false, request);
		try {
			// 手机修改成功,发送短信 by janwen
			String smsTemplate = Constant.USER_MOBILE_UPDATE;
			Sms sms = null;
			sms = smsService.getSmsByTitle(smsTemplate);
			if (sms != null && old_mobile_phone != null
					&& !"".equals(old_mobile_phone)) {
				SmsInfo sourceBean = null;
				String content = "";
				String template = sms.getSmscontent();
				// 短信参数
				Object[] param = new Object[] { user.getMobile(),
						String.valueOf(user.getId()) };
				content = MessageFormat.format(template, param);
				sourceBean = new SmsInfo(old_mobile_phone, content, SMS_TYPE,
						"0");
				smsService.sendSms(sourceBean);
			}
			// 用户账户中心
			request.setAttribute("UPDATEMOBILE", "true");
			// 清除用户校验通过信息 by janwen
			if (user.getMobile_isavalible() == 1) {
				memCacheService.remove(UserSecurityAction.SECURITY_PREFIX
						+ user.getId() + "verified");
				// 验证成功
				String newmobile = user.getMobile();
				if (!old_mobile_phone.equals(newmobile)) {
					request.setAttribute("newmobile",
							com.beike.util.StringUtils.starMobile(newmobile));
					return "user/updateMobileSuccess";
				}

			}
			return "user/updateMobile";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("UPDATEMOBILE", "true");
			return "user/updateMobile";
		}
	}

	/**
	 * 功能：用户修改邮箱，输入邮箱地址，点此action发送邮件
	 * 
	 * 访问页面名称:
	 * 
	 * /jsp/user/updateEmail.jsp
	 * 
	 * 访问路径: /user/sendUpdateEmail.do
	 * 
	 * 跳转页面名称: 1. 输入有误 跳转地址:/jsp/user/updateEmail.jsp
	 * 
	 * 2. 成功后 跳转地址 用户中心：/jsp/user/sendUpdateEmailSuccess.jsp
	 * 
	 * 输入参数: 1.邮件地址：email
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * USER_EMAIL_ERROR: 邮件地址输入有误
	 * 
	 * USER_TIMEOUT: 用户操作超时
	 */
	@RequestMapping("/user/sendUpdateEmail.do")
	public Object updateEmail(ModelMap model, HttpServletRequest request) {

		User user = getMemcacheUser(request);
		if (user == null) {
			request.setAttribute("ERRMSG", "用户操作超时,请重新登录!");
			return new ModelAndView("redirect:../500.html");
		}
		
		String email = request.getParameter("email");
		boolean flag = MobilePurseSecurityUtils.checkEmail(email, 0);

		if (!flag) {
			request.setAttribute("ERRMSG", "USER_EMAIL_ERROR");
			return "redirect:/forward.do?param=updateEmail";
		}
		
		// 手机号码已经验证的,必须验证是否通过安全校验 by janwen
		if ((user.getMobile_isavalible() == 1)
				&& !"y".equals(memCacheService
						.get(UserSecurityAction.SECURITY_PREFIX + user.getId()
								+ "verified"))) {
			// request.setAttribute("ERRMSG", "NOT_CONFIRMED");
			return "redirect:/forward.do?param=updateEmail";
		}
		
		User userx = null;
		try {
			userx = userService.findUserByEmail(email);
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "USER_EXIST");
			return "redirect:/forward.do?param=updateEmail";
		}

		if (userx != null) {
			request.setAttribute("ERRMSG", "USER_EXIST");
			return "user/updateEmail";
		}

		// 修改邮箱 邮件模板 RESETEMAIL
		String resetPasswordUrl = propertyUtil.getProperty("resetEmailUrl");

		String customerKey = user.getCustomerkey();
		String hmac = MobilePurseSecurityUtils.hmacSign(customerKey, email
				+ user.getId());
		resetPasswordUrl += "?id=" + user.getId() + "&key=" + email + "&hmac="
				+ hmac;
		String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
		String date = DateUtils.dateToStr(new Date());
		Object[] emailParams = new Object[] { time, resetPasswordUrl, date };
		try {
			emailService.send(null, null, null, null, null, "千品网邮箱认证邮件",
					new String[] { email }, null, null, new Date(),
					emailParams, "RESETEMAIL");
			// 加入扩展信息,找回密码邮件信息
			UserProfile userProfile = userService.getProfile(user.getId(),
					"USER_RESET_EMAIL");
			if (userProfile == null) {
				userService.addProfile("USER_RESET_EMAIL", hmac, user.getId(),
						ProfileType.USERCONFIG);
			} else {
				userProfile.setValue(hmac);
				userService.updateProfile(userProfile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e);
		}
		// 清空用户验证通过缓存
		if (user.getMobile_isavalible() == 1) {
			memCacheService.remove(UserSecurityAction.SECURITY_PREFIX
					+ user.getId() + "verified");
		}
		request.setAttribute("UPDATESUCCESS", "true");
		return "user/updateEmail";
	}

	@RequestMapping("/user/confirmUpdateEmail.do")
	public Object validateUpdateEmail(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		String email = request.getParameter("key");
		String userid = request.getParameter("id");
		String hmac = request.getParameter("hmac");
		User user = null;
		try {
			user = userService.findById(Long.parseLong(userid));
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}

		if (user == null) {
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}

		String customerKey = user.getCustomerkey();
		boolean flag = MobilePurseSecurityUtils.isPassHmac(hmac, customerKey,
				email + user.getId());
		// 验证签名失败
		if (!flag) {
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}

		// 用户扩展信息
		UserProfile userProfile = null;
		boolean isUsable = false;
		try {
			userProfile = userService.getProfile(user.getId(),
					"USER_RESET_EMAIL");
			isUsable = userService.isUrlUsable(userProfile.getValue(), user
					.getId(), "USER_RESET_EMAIL");

			if (!isUsable || !userProfile.getValue().equals(hmac)) {
				request.setAttribute("ERRMSG", "URLISINVALID");
				return new ModelAndView("/user/500");
			}
			userProfile.setValue(userProfile.getValue() + "X");
			userService.updateProfile(userProfile);
		} catch (Exception e) {
			log.info("找回密码邮件认证失败:" + e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}

		user.setEmail(email);
		user.setEmail_isavalible(1);
		user.setIsavalible(1);
		try {
			userService.updateUserMessage(user);
		} catch (UserException e) {
			e.printStackTrace();
			log.info(e);

		}
		request.getSession().setAttribute(Constant.USER_LOGIN, user);
		//
		SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
				response, false, request);
		request.setAttribute("email", email);
		return "user/sendUpdateEmailSuccess";
	}

	/**
	 * 功能：用户修改密码，输入旧密码，两遍新密码 提交到此action
	 * 
	 * 访问页面名称:
	 * 
	 * /jsp/user/updatePassword.jsp
	 * 
	 * 访问路径: /user/resetPassword.do
	 * 
	 * 跳转页面名称: 1. 输入有误 跳转地址:/jsp/user/updatePassword.jsp
	 * 
	 * 2. 成功后 跳转地址 用户中心：/jsp/user/useraccount.jsp
	 * 
	 * 输入参数: 1.旧密码:oldPassword 2.新密码:newPassword 3.确认密码:confirmPassword
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * CONFIRM_PASSWORD_ERROR: 用户两次确认密码不一致
	 * 
	 * USER_UPDATE_ERROR: 系统错误
	 * 
	 * USER_NOST_EXIST:该用户不存在
	 * 
	 * USER_LOGIN_ERROR:用户名或密码错误
	 */
	@RequestMapping("/user/resetPassword.do")
	public Object resetPassword(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		User user = getMemcacheUser(request);
		if (user == null) {
			request.setAttribute("ERRMSG", "用户操作超时,请重新操作!");
			return new ModelAndView("redirect:../500.html");
		}

		// 手机号码已经验证的,必须验证是否通过安全校验 by janwen
		if ((user.getMobile_isavalible() == 1)
				&& !"y".equals(memCacheService
						.get(UserSecurityAction.SECURITY_PREFIX + user.getId()
								+ "verified"))) {
			request.setAttribute("ERRMSG", "NOT_CONFIRMED");
			return "redirect:/forward.do?param=updatePassword";
		}
		String oldPassword = request.getParameter("oldPassword");
		String newPssword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		if (oldPassword == null || oldPassword.length() < 6) {
			request.setAttribute("ERRMSG", "PASSWORD_PARAM_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}
		if (newPssword == null || newPssword.length() < 6) {
			request.setAttribute("ERRMSG", "PASSWORD_PARAM_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}

		if (!newPssword.equals(confirmPassword)) {
			request.setAttribute("ERRMSG", "CONFIRM_PASSWORD_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}

		User userx = null;
		try {
			userx = userService.isUserLogin(user.getMobile(), oldPassword, user
					.getEmail());
		} catch (UserException e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "USER_LOGIN_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}
		if (userx == null) {
			request.setAttribute("ERRMSG", "USER_LOGIN_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}

		String password = MobilePurseSecurityUtils.secrect(newPssword, userx
				.getCustomerkey());
		userx.setPassword(password);

		try {
			userService.updateUserMessage(userx);
		} catch (UserException e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "USER_UPDATE_ERROR");
			return "redirect:/forward.do?param=updatePassword";
		}

		request.setAttribute("UPDATE_PASSWORD", "true");
		// 更新密码成功,清空cookie:SINGLETON_COOKIE_KEY,跳转到登陆页面/user/userLogin.do by
		// janwen
		// return "user/updatePassword";
		// 更新密码成功,清空手机验证信息
		if (user.getMobile_isavalible() == 1) {
			memCacheService.remove(UserSecurityAction.SECURITY_PREFIX
					+ user.getId() + "verified");
		}
		response.addCookie(WebUtils.removeableCookie("SINGLETON_COOKIE_KEY",
				".qianpin.com"));
		response.addCookie(WebUtils.removeableCookie("REQUESTURI_REFER_COOKIE",
				".qianpin.com"));
		return "/user/updatePasswordSuccess";
	}

	@Override
	public UserService getUserService() {
		return userService;
	}

	@Override
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
}
