package com.beike.action.user;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.service.user.UserSecurityService;
import com.beike.service.user.UserService;
import com.beike.util.DateUtils;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * 用户账户安全中心
 * 
 * @author janwen Sep 17, 2012
 */
@Controller
public class UserSecurityAction extends BaseUserAction {

	@Autowired
	private SmsService smsService;

	private static final Log logger = LogFactory
			.getLog(UserSecurityAction.class);
	public static final String SECURITY_PREFIX = "SECURITY_USER_";
    public static final String SECURITY_SUFFIX = "_SCURITYCODE";
	private static final String SMS_PREFIX = "亲爱的千品用户，您申请的手机校验码为";
	private static final String SMS_SUFFIX = ",请在页面中提交校验码，完成验证。【千品网】";
	MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	@Autowired
	private UserSecurityService userSecurityService;
	
	
	@Autowired
	private UserService userService;
	/**
	 * 发送手机验证码 janwen
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/user/getSecurityCode.do")
	public void sendSecurityCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String responseMessage = "ok";
		try {

			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null && StringUtils.validNull(user.getMobile())) {
				String phoneNo = user.getMobile();
				Object sentSMSCache = memCacheService.get(SECURITY_PREFIX
						+ DateUtils.dateToStr(new Date()) + "_" + phoneNo);
				int sentSMS = 0;
				if (sentSMSCache != null
						&& StringUtils.validNull(sentSMSCache.toString())) {
					sentSMS = new Integer(sentSMSCache.toString());
				}
				// 验证码发送次数当天30次为上限
				if (sentSMS < 30) {
					String securityCode = RandomNumberUtils.getRandomNumbers(6);
					String securityMessage = SMS_PREFIX + securityCode
							+ SMS_SUFFIX;
					SmsInfo sourceBean = new SmsInfo();
					sourceBean.setDesMobile(phoneNo);
					sourceBean.setContent(securityMessage);
					logger.info(securityMessage);
					sourceBean.setSmsType("0");
					smsService.sendSms(sourceBean);
					cacheManager(SECURITY_PREFIX + phoneNo + SECURITY_SUFFIX,
							securityCode);
					memCacheService.set(
							SECURITY_PREFIX + DateUtils.dateToStr(new Date())
									+ "_" + phoneNo, sentSMS + 1);
				} else {
					responseMessage = "limit";
				}
			} else {
				responseMessage = "login_timeout";
			}
			response.getWriter().write(responseMessage);
		} catch (Exception e) {
			responseMessage = "fail";
			response.getWriter().write(responseMessage);
			logger.info("获取验证码失败");
			e.printStackTrace();
		}
	}
    /**
     * 
     * janwen
     * @param randomCode 手机验证码
     * @param phoneNo   手机号码
     * @param param   
     * @param uuid_cookie 图片验证码cookie:uuid
     * @param imageCode
     * @return
     *
     */
	private boolean checkRequest(String randomCode,String phoneNo, String param,
			String uuid_cookie, String imageCode) {
		boolean isValidRequest = StringUtils.validNull(randomCode,phoneNo, param,
				uuid_cookie, imageCode);
		if (isValidRequest) {
			isValidRequest = StringUtils.isMobileNo(phoneNo);
		}
		if (isValidRequest) {
			Object checkCookieCache = memCacheService
					.get("validCode_" + uuid_cookie);
			isValidRequest = checkCookieCache == null ? false
					: (checkCookieCache.toString().equals(imageCode));
			memCacheService.remove("validCode_" + uuid_cookie);
		}
		if(isValidRequest){
			Object randomCodeCache = memCacheService
					.get(SECURITY_PREFIX + phoneNo + SECURITY_SUFFIX);
			isValidRequest = randomCodeCache == null ? false
					: (randomCodeCache.toString().equals(randomCode));
		}

		return isValidRequest;
	}

	/**
	 * 
	 * janwen
	 * 
	 * @param request
	 * @param response
	 * @return 校验手机验证码
	 * 
	 */
	@RequestMapping(value = "/user/verifyCode.do")
	public String verifySecurityCode(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String receivedCode = request.getParameter("checkCode");
			String param = request.getParameter("param");
			String imageCode = request.getParameter("authCodeB");
			User user = SingletonLoginUtils.getMemcacheUser(request);
			String check_security_cookie = WebUtils.getCookieValue(
					"RANDOM_VALIDATE_CODE", request);
			if (user != null
					&& checkRequest(receivedCode,user.getMobile(),param,
							check_security_cookie, imageCode)) {
				String phoneNo = user.getMobile();
				cacheManager(SECURITY_PREFIX + phoneNo + "_SCURITYCODE", "");
				cacheManager(SECURITY_PREFIX + user.getId() + "verified", "y");
				return "user/" + param;
			}
			return "redirect:/user/goToSecurityCheck.do?param=" + param + "&ERRMSG=fail";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("验证码校验失败");
			return "redirect:../500.html";
		}
	}
	
	
	
	@RequestMapping("/userhelp/confirmURL.do")
	public String verifySecurityURL(HttpServletRequest request,
			HttpServletResponse response) {
		
		String sign =  request.getParameter("sign");
		if(StringUtils.validNull(sign)){
			Map signInfo = userSecurityService.getUserBySign(sign);
			if(signInfo != null){
				Long userid = (Long) signInfo.get("userid");
				User findUser = userService.findById(userid);
				if(findUser != null){
					//用户为空,或者当前登录用户与url用户不一致,为用户自动登录
					SingletonLoginUtils.addSingleton(findUser, userService, findUser.getId()
							+ "", response, false, request);
					//默认用户通过安全校验
				//	cacheManager(SECURITY_PREFIX + findUser.getId() + "verified", "y");
					//userSecurityService.updateSign(sign);
					return "redirect:/forward.do?param=updateMobile&sign=" + sign;
				}
			}
		}
		return "redirect:../404.html";
		
	}

	@RequestMapping("/user/goToSecurityCheck.do")
	public String gotoSecurityCheck(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SingletonLoginUtils.getMemcacheUser(request);
		String mobile = user.getMobile();
		request.setAttribute("mobile", StringUtils.starMobile(mobile));
		String param = request.getParameter("param");
		logger.info(param);
		request.setAttribute("fromPage", param);
		return "user/securityCheck";
	}

	private void cacheManager(String key, String value) {
		memCacheService.set(key, value,3*60);
	}
}
