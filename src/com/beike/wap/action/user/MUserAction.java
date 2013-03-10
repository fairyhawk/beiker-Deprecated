package com.beike.wap.action.user;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.UserException;
import com.beike.entity.common.Sms;
import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.MCookieKey;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;
import com.beike.wap.entity.MUserTemp;
import com.beike.wap.service.MUserProfileService;
import com.beike.wap.service.MUserService;


/**
 * <p>
 * Title:手机用户相关action
 * </p>
 * <p>
 * Description:用户登录、用户注册、城市选择
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 8-11
 * @author kun.wang
 * @version 1.0
 */

@Controller
public class MUserAction extends MBaseUserAction{
	/** 日志记录 */
	private static Log log = LogFactory.getLog(MUserAction.class);
	
	/** 用户操作服务接口 */
	@Autowired
	private MUserService mUserService;
	
	/** 注册临时用户服务接口 */
	@Autowired
	private MUserProfileService mUserProfileService;
	
	/** 短信服务接口 */
	@Autowired
	private SmsService smsService;
	
//	/** 邮件服务 */ 
//	@Autowired
//	private EmailService emailService;

	private static final int MONTH = 60*60*24*30;// TODO 测试使用，将一个月30*24*3600修改为5分钟60*5
	
	/** 读取汉字提示信息文件 */
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_CH_INFO);
	
	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
	
	@RequestMapping("/wap/user/toUserLogin.do")
	public String toLoginPage(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String meState = WebUtils.getCookieValue(MCookieKey.MEM_ME_KEY_ID, request);
		String monthState = WebUtils.getCookieValue(MCookieKey.MEM_MONTH_KEY_ID, request);
		String userId = WebUtils.getCookieValue(MCookieKey.USER_ID_MEMORY, request);
		
		if(meState == null || meState.equals(""))
		{
			userId = "";
			meState = "";
			monthState = "";
		}
		if(monthState == null)
		{
			monthState = "";
		}
		
		String tempCount = WebUtils.getCookieValue(MCookieKey.MEM_LOGIN_IP, request);
		log.info("get count in cookie, cont = " + tempCount);
		Integer count = 0;
		try {
			if(tempCount!=null&&!tempCount.trim().equals("")){
				count = Integer.parseInt(tempCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = 0;
		}
		
		if(count >= 3)
		{
			model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
		}
		else
		{
			model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "");
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("meState", meState);
		model.addAttribute("monthState", monthState);
		model.addAttribute("ERRMSG", "");
		model.addAttribute("LOGMOBILE", "");
		return "wap/user/login";
	}
	
	@RequestMapping("/wap/user/mUserLogin.do")
	public Object mUserLogin(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		
		String tempCount = WebUtils.getCookieValue(MCookieKey.MEM_LOGIN_IP, request);
		Integer count = 0;
		try {
			if(tempCount == null || tempCount.trim().equals(""))
			{
				count = 0;
			}else
			{
				count = Integer.parseInt(tempCount);
			}
		} catch (Exception e) {
			count = 0;
		}
		
		String validateCode = request.getParameter("validateCode");
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		String[] checkMe = request.getParameterValues("me");
		String[] checkMonth = request.getParameterValues("month");
		
		if(userId != null)
		{
			userId = userId.trim();
		}
		
		if(checkMe == null)
		{
			WebUtils.removeMCookieByKey(response, MCookieKey.MEM_ME_KEY_ID);
			WebUtils.removeMCookieByKey(response, MCookieKey.USER_ID_MEMORY);
		}
		else
		{
			String meFlag = WebUtils.getCookieValue(MCookieKey.MEM_ME_KEY_ID, request);
			if(meFlag == "" || meFlag == null)
			{
				WebUtils.setMCookieByKey(response, MCookieKey.MEM_ME_KEY_ID, "true",60 * 60 * 24 * 30 * 12);
			}
			WebUtils.setMCookieByKey(response, MCookieKey.USER_ID_MEMORY, userId, 60 * 60 * 24 * 30 * 12);
		}
		if(checkMonth == null)
		{
			WebUtils.removeMCookieByKey(response, MCookieKey.MEM_MONTH_KEY_ID);
		}
		else
		{
			String monthFlag = WebUtils.getCookieValue(MCookieKey.MEM_MONTH_KEY_ID, request);
			if(monthFlag == "" || monthFlag == null)
			{
				WebUtils.setMCookieByKey(response, MCookieKey.MEM_MONTH_KEY_ID, "true", MONTH);
			}
//			WebUtils.removeMCookieByKey(response, MCookieKey.USER_ID_MEMORY);
//			WebUtils.setMCookieByKey(response, MCookieKey.USER_ID_MEMORY, userId, MONTH);
		}
		
		model.addAttribute("meState", checkMe==null?"":checkMe[0]);
		model.addAttribute("monthState", checkMonth == null?"": checkMonth[0]);
		model.addAttribute("userId", userId);
		
		if (userId == null || "".equals(userId))
		{
			log.info("userId is null, return to login page");
			model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.USER_PWD_ERROR));
			count++;
			WebUtils.setMCookieByKey(response, MCookieKey.MEM_LOGIN_IP, count+"", 60 * 60 * 24);
			if(count >= 3)
			{
				model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
			}
			model.addAttribute("LOGMOBILE", userId);
			return new ModelAndView("wap/user/login");
		}
		
		String email = "";
		String mobile = "";
		
		boolean ismobile = MobilePurseSecurityUtils.isJointMobileNumber(userId);
		boolean isEmail = MobilePurseSecurityUtils.checkEmail(userId, 0);
		
		boolean userIsMobile = false;
		if(ismobile)
		{
			log.info("user mobile login");
			mobile = userId;
			userIsMobile = true;
		}
//		TODO 暂时不支持邮箱登陆
//		else if(isEmail)
//		{
//			log.info("user email login");
//			email = userId;
//		}
		else
		{
			log.info("user id formate error");
			model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.USER_PWD_ERROR)); 
			count++;
			WebUtils.setMCookieByKey(response, MCookieKey.MEM_LOGIN_IP, count+"", 60 * 60 * 24);
			if(count >= 3)
			{
				model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
			}
			model.addAttribute("LOGMOBILE", userId);
			return new ModelAndView("wap/user/login");
		}
		
		if(validateCode != null)
		{
			String cookieCode = WebUtils.getCookieValue(MCookieKey.RANDOM_VALIDATE_CODE, request);
			if(cookieCode == null)
			{
				model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.LOGIN_VALIDATE_CODE_ERROR));
				model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
				model.addAttribute("LOGMOBILE", userId);
				return "wap/user/login";
			}
			
			String vCode = (String) memCacheService.get("validCode_"
					+ cookieCode);
			if(!validateCode.equals(vCode))
			{
				model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.LOGIN_VALIDATE_CODE_ERROR));
				model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
				model.addAttribute("LOGMOBILE", userId);
				return "wap/user/login";
			}
		}
		
		try {
			User user = mUserService.isUserLogin(mobile, password, email);
			if((userIsMobile && user.getMobile_isavalible() == 1) || (!userIsMobile && user.getEmail_isavalible() == 1))
			{
				log.info("login success!");
				int validy = 0;
				if(checkMonth == null || checkMonth.equals(""))
				{
					validy = 24 * 60 * 60;
				}
				else
				{
					validy = MONTH;
				}
				SingletonLoginUtils.addMSingleton(user, mUserService, user.getId()
						+ "", response, false, request, validy);
				WebUtils.setMCookieByKey(response, MCookieKey.MEM_LOGIN_IP, "0", 60 * 60 * 24);
				model.addAttribute("ERRMSG", "login success!");
				return checkToForwardUrl(request, "/wap/user/loginSuccess", "redirect");
			}else
			{
				log.info("the user id "+userId+" is not avalible");
				model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.USER_UNAVAILABLE)); 
				count++;
				WebUtils.setMCookieByKey(response, MCookieKey.MEM_LOGIN_IP, count+"", 60 * 60 * 24);
				if(count >= 3)
				{
					model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
				}
				model.addAttribute("LOGMOBILE", userId);
				return new ModelAndView("wap/user/login");//request, "user/regist", "forward"
			}
		} catch (UserException e) {
			e.printStackTrace();
			log.info("the user id : " + userId + "  login error");
			model.addAttribute("ERRMSG", propertyUtil.getProperty(MCookieKey.USER_PWD_ERROR));
			count++;
			WebUtils.setMCookieByKey(response, MCookieKey.MEM_LOGIN_IP, count+"", 60 * 60 * 24);
			if(count >= 3)
			{
				model.addAttribute(MCookieKey.MEM_USER_LOGIN_CODE_OPEN, "true");
			}
			model.addAttribute("LOGMOBILE", userId);
			return new ModelAndView("wap/user/login");
		}
	}

	/**
	 * 退出登录
	 */
	@RequestMapping("/wap/user/logout.do")
	public Object logout(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		User user = getMemcacheUser(request);
		if (user != null) {
			SingletonLoginUtils.removeSingleton(response, request);
		}

		String meState = WebUtils.getCookieValue(MCookieKey.MEM_ME_KEY_ID, request);
		if(meState == "" || meState == null)
		{
			WebUtils.removeMCookieByKey(response, MCookieKey.USER_ID_MEMORY);
		}
		
		WebUtils.removeMCookieByKey(response, MCookieKey.MEM_MONTH_KEY_ID);
		
		return new ModelAndView("redirect:/wap/goods/goodsIndexController.do?method=queryIndexShowMes");
	}
	
	@RequestMapping("/wap/user/toRegist.do")
	public Object toUserRegist(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		model.addAttribute("REGERR", "");
		model.addAttribute("REGNAME", "");
		model.addAttribute("REGEMAIL", "");
		return new ModelAndView("wap/user/regist");
	}
	
	@RequestMapping("/wap/user/userRegist.do")
	public Object userRegist(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String mobile = request.getParameter("regMobile");
		String password = request.getParameter("regPassword");
		String email = request.getParameter("regEmail");
		String validateCode = request.getParameter("validateCode");
		log.info(mobile+"\n"+password+"\n"+validateCode);
		model.addAttribute("REGNAME", mobile);
		model.addAttribute("REGEMAIL", email);
		User user = null;
//		if(password == null || mobile == null || password.trim() == "" || mobile.trim() == "")
//		{
//			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_MOBILE_FORMATE_ERROR));
//			return "wap/user/regist";
//		}
		
		boolean isMobile = MobilePurseSecurityUtils.isJointMobileNumber(mobile);
		if(!isMobile)
		{
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_MOBILE_FORMATE_ERROR));
			return "wap/user/regist";
		}
		
		try {
			user = mUserService.findUserByMobile(mobile);
			if(user != null)
			{
				model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_MOBILE_EXIST));
				return "wap/user/regist";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.SYS_BUSY_INFO));
			return "wap/user/regist";
		}
		
		boolean isEmail = MobilePurseSecurityUtils.checkEmail(email, 0);
		if(!isEmail)
		{
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_EMAIL_FORMATE_ERROR));
			return "wap/user/regist";
		}
		
		try {
			user = mUserService.findUserByEmail(email);
			if(user != null)
			{
				model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_EMAIL_EXIST));
				return "wap/user/regist";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.SYS_BUSY_INFO));
			return "wap/user/regist";
		}
		
		boolean pwdAvailable = MobilePurseSecurityUtils.isPasswordAvailable(password);
		if(!pwdAvailable)
		{
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.PWD_FORMATE_ERROR));
			return "wap/user/regist";
		}
		
		if(validateCode == null)
		{
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_VALIDATE_CODE_ERROR));
			return "wap/user/regist";
		}else
		{
			String cookieCode = WebUtils.getCookieValue(MCookieKey.RANDOM_VALIDATE_CODE, request);
			if(cookieCode == null)
			{
				model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_VALIDATE_CODE_ERROR));
				return "wap/user/regist";
			}
			
			String vCode = (String) memCacheService.get("validCode_"
					+ cookieCode);
			if(!vCode.equals(validateCode) || vCode == null)
			{
				model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_VALIDATE_CODE_ERROR));
				return "wap/user/regist";
			}
		}
		
		try {
			MUserTemp ut =  mUserProfileService.addUserTemp(mobile, email, password);
			StringBuffer validateUrl = new StringBuffer("");
			validateUrl.append(Constant.WAP_URL_FIELD).append("/")
					.append("/wap/v.do?i=").append(ut.getId())
					.append("&c=").append(ut.getvCode());
			Object[] smsParam_code = {ut.getvCode()+""};
			Object[] smsParam_url = {validateUrl.toString()};
			Map<String, Integer> map = (Map<String, Integer>) memCacheService.get(MCookieKey.MEM_REGIST_SMS_COUNT+ut.getId());
			int smsCount = 0;
			if(map == null || map.size() == 0)
			{
				map = new HashMap<String, Integer>();
				smsCount = 1;
			}
			else
			{
				smsCount = map.get(ut.getId()+"");
			}
			if(smsCount > 10)
			{
				model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.SMS_COUNT_OUT));
				return "wap/user/regist";
			}
			
			sendSmsValidate(ut.getMobile(), request, Constant.WAP_REGIST_SMS_TITLE_CODE, smsParam_code, response);
			sendSmsValidate(ut.getMobile(), request, Constant.WAP_REGIST_SMS_TITLE_URL, smsParam_url, response);
			smsCount++;
			log.info("sms count : " + smsCount);
			map.put(ut.getId()+"", smsCount);
			memCacheService.set(MCookieKey.MEM_REGIST_SMS_COUNT + ut.getId(), map, 60 * 60 * 24);
			
//			String subject = "千品网手机注册通知";
//			String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
//			String date = DateUtils.dateToStr(new Date());
//			// 设置动态参数
//			Object[] emailParams = new Object[] { time, Constant.WEB_QIANPIN_FIELD, date };
//			// 邮件模板参数未设置
//			try {
//				emailService.send(null, null, null, null, null, subject,
//						new String[] { email }, null, null, new Date(),
//						emailParams, Constant.WAP_REGIST_EMAIL_TEMPLATE);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			model.addAttribute("uid", ut.getId());
			model.addAttribute("VADERR", "");
			return "wap/user/validateregist";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("REGERR", propertyUtil.getProperty(MCookieKey.REG_SYS_BUSY));
			return "wap/user/regist";
		}
	}
	
	/**
	 * 验证手机
	 */
	@RequestMapping("/wap/v.do")
	public Object validateRegist(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String i = request.getParameter("i");
		String code = request.getParameter("c");
		model.addAttribute("uid", i);
		long id = 0 ;
		if(i == null || code == null)
		{
			model.addAttribute("VADERR", propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			return "wap/user/validateregist";
		}
		
		try{
			id = Long.parseLong(i);
		}catch (Exception e) {
			model.addAttribute("VADERR", propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			return "wap/user/validateregist";
		}
		boolean vadFlag = mUserProfileService.userIsExist(id, code);
		if(!vadFlag)
		{
			model.addAttribute("VADERR", propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			return "wap/user/validateregist";
		}
		
		MUserTemp userTemp = mUserProfileService.findById(id);
		if(userTemp == null)
		{
			model.addAttribute("VADERR",propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			return "wap/user/validateregist";
		}
		try {
			User user = null;
			user = mUserService.findUserByMobile(userTemp.getMobile());
			if(user != null)
			{
				model.addAttribute("VADERR",propertyUtil.getProperty(MCookieKey.VALIDATE_USER_EXIST));
				return "wap/user/validateregist";
			}
			user = mUserService.addMobileRegist(userTemp.getMobile(),userTemp.getEmail(), userTemp.getPassword(),userTemp.getCustomerkey());
//			mUserProfileService.deleteById(userTemp.getId()); 暂时不删除临时表信息
			String hmac = MobilePurseSecurityUtils.hmacSign(userTemp.getCustomerkey(), user.getId() + "");
			mUserService.addProfile(Constant.EMAIL_REGIST_URLKEY, hmac,
					user.getId(), ProfileType.WAPCONFIG);

			SingletonLoginUtils.addMSingleton(user, mUserService, user.getId()
					+ "", response, false, request, 24 * 60 * 60);
			WebUtils.removeableCookie(MCookieKey.USER_ID_MEMORY);
			WebUtils.setMCookieByKey(response, MCookieKey.USER_ID_MEMORY, user.getMobile(),
					60 * 60 * 24);
			return "redirect:/wap/validateSuccess.do?mobile="+user.getMobile();
		} catch (AccountException e) {
			model.addAttribute("VADERR", propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			e.printStackTrace();
			return "wap/user/validateregist";
		} catch (UserException e) {
			model.addAttribute("VADERR", propertyUtil.getProperty(MCookieKey.CHANGE_PARAM_ERROR));
			e.printStackTrace();
			return "wap/user/validateregist";
		}
	}
	
	@RequestMapping("/wap/validateSuccess.do")
	public Object validateSuccess(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String mobile = request.getParameter("mobile");
		model.addAttribute("mobile", mobile);
		return "wap/user/registsuccess";
	}
	
	/**
	 * 根据来源类型判断去向 add bu wenhua.cheng
	 * 
	 * @param request
	 * @param normalforUrl
	 *            常规的url
	 * @param mobileVerifyStatus
	 *            手机校验状态
	 * @param loginStatus
	 *            登陆状态
	 * @return
	 */
	public Object checkToForwardUrl(HttpServletRequest request,
			String normalforUrl, String todoType) {
		if ("".equals(normalforUrl) || normalforUrl == null) {
			throw new IllegalArgumentException("normalforUrl is not null");
		}
		String loginRegSource = request
				.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
		// 如果是交易来的，则获取参数后原样转发
		if ("TRX".equals(loginRegSource)) {
			String goodsDetailKey = request.getParameter("goodsDetailKey");
			request.setAttribute("goodsDetailKey", goodsDetailKey);
			String goodsCount = request.getParameter("goodsCount");
			String uuidTokenKey = request.getParameter(Constant.UUID_TOKEN_KEY);
			request.setAttribute("goodsCount", goodsCount);
			// request.setAttribute("mobileVerifyStatus", mobileVerifyStatus);
			// request.setAttribute("loginStatus", loginStatus);
			request.setAttribute(Constant.UUID_TOKEN_KEY, uuidTokenKey);
			log.info("++++++goodsDetailKey:" + goodsDetailKey + "->goodsCount:"
					+ goodsCount + "->uuidTokenKey:" + uuidTokenKey
					+ "+++++++++++++++++++++++++++++++++++");

			if ("redirect".equals(todoType)) {
				return new ModelAndView("redirect:"
						+ Constant.USER_TRX_LOGIN_REGISTER + "?goodsDetailKey="
						+ goodsDetailKey);
			} else {
				return new ModelAndView("forward:"
						+ Constant.USER_TRX_LOGIN_REGISTER);

			}
		} else {
			// 正常去向,判断是从哪里过来的,
			// String url=WebUtils.getRequestPath(request);
			String refer_url = WebUtils.getCookieValue(
					"REQUESTURI_REFER_COOKIE", request);
			log.info("login success....redirect url:" + refer_url);
			if(refer_url == null)
			{
				refer_url = "/wap/goods/goodsIndexController.do?method=queryIndexShowMes";
			}
			request.setAttribute("refer_url", refer_url);

//			if ("user/login".equals(normalforUrl)
//					|| "user/registsuccess".equals(normalforUrl)
//					|| "user/regist".equals(normalforUrl)) {
//				
//				return normalforUrl;
//			}

			if (StringUtils.isBlank(refer_url)) {
				refer_url = "/";
			}
			return new ModelAndView("redirect:" + refer_url);
		}
	}
	
	/**
	 * 发送短信验证
	 * 
	 * @param mobile
	 * @param request
	 * @param sms
	 * @return 
	 */
	private Map<String, String> sendSmsValidate(String mobile,
			HttpServletRequest request, String smsTemplate, Object[] param, HttpServletResponse response) {
		Map<String, String> smsMap = null;
		
		Sms sms = null;
		try {
			sms = smsService.getSmsByTitle(smsTemplate);
		} catch (BaseException e) {
			e.printStackTrace();
		}
		if (sms != null) {
			SmsInfo sourceBean = null;
			String content = "";
			String template = sms.getSmscontent();
			// 短信参数
			content = MessageFormat.format(template, param);
			log.info("sms info =======>> "+content);
			sourceBean = new SmsInfo(mobile, content, Constant.SMS_TYPE,"0");
			smsMap = smsService.sendSms(sourceBean);
			log.info("send sms map : " + smsMap);
		}
		return smsMap;
	}
}
