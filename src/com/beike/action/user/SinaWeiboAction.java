package com.beike.action.user;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
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

import com.beike.action.LogAction;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.UserException;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.service.common.EmailService;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserService;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.sina.RequestToken;
import com.beike.util.sina.SinaAccessToken;
import com.beike.util.sina.SinaUser;
import com.beike.util.sina.WebOAuth;
import com.beike.util.sina.Weibo;
import com.beike.util.sina.WeiboException;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title:
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
 * @date May 11, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class SinaWeiboAction {
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);

	private WeiboService weiboService;

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	private final Log log = LogFactory.getLog(SinaWeiboAction.class);

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	
	private static String SINA="SINA";
	
	/**
	 * 新浪发布微博
	 */
	@RequestMapping("/user/sendSinaMessage.do")
	public String sendSinaMessage(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String stokenauth = WebUtils.getCookieValue("STOKENAUTH", request);
//		RequestToken resToken = (RequestToken) memCacheService
//		.get("SINA_REQUESTTOKEN_" + stokenauth);
		if(stokenauth!=null){
			SinaAccessToken sinaAccessToken = (SinaAccessToken) memCacheService.get("SINA_USER_ACCESSTOKEN_" + stokenauth);
			String content=request.getParameter("content");
//			content=content.replace("#", "&");
			Weibo weibo = new Weibo();
			if(sinaAccessToken!=null){
				weibo.setToken(sinaAccessToken.getToken(), sinaAccessToken.getTokenSecret());
				try {
					weibo.updateStatus(content);
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			response.getWriter().write("success");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 功能:新浪微博绑定，用户授权之后，选择创建账户或者登陆时调用此方法 请求: 1.邮箱:email 2.绑定手机号:mobile
	 * 3.密码:password 4.验证后跳转页面:referurl 返回 request 5.账户类型 是注册还是登陆:BINDTYPE
	 * 创建账户：CREATEACCOUNT 登陆：LOGINACCOUNT 返回： 1.错误信息:ERRMSG
	 * 
	 * Authtimeout: 授权超时，请重新进行授权操作，用户操作超时
	 * 
	 * USERNAME_PARAM_ERROR:用户名参数格式有误
	 * 
	 * USER_TIMEOUT：用户超时操作
	 * 
	 * USER_LOGIN_ERROR:用户名或密码有误，登陆失败
	 * 
	 * 跳转页面: 1.错误页面：/user/validatesms 2.成功跳到主页面:/user/main 3.交易流程 验证sms后:跳转到
	 * referurl地址
	 */
	@RequestMapping("/user/bindSinaWeiboAccount.do")
	public Object bindWeiboAccount(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
	
		try {
			request.setAttribute("WEIBO_SITE_TYPE",
					propertyUtil.getProperty("SINA_APP_NAME"));
			String stokenauth = WebUtils.getCookieValue("STOKENAUTH", request);
			if (stokenauth == null) {
				// request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
				// return new ModelAndView("redirect:../500.html");

				return "user/login";
			}
			SinaAccessToken sinaAccessToken = (SinaAccessToken) memCacheService
					.get("SINA_USER_ACCESSTOKEN_" + stokenauth);

			RequestToken resToken = (RequestToken) memCacheService
					.get("SINA_REQUESTTOKEN_" + stokenauth);
			// String screenName=(String)
			// memCacheService.get("WEIBO_NAMES_"+stokenauth);
			// resToken.setScreenName(screenName);
			String weiboName = resToken.getScreenName();
			request.setAttribute("WEIBO_USER_SCREENNAME", weiboName);
			request.setAttribute("WEIBO_URL", "/user/bindSinaWeiboAccount.do");
			if (sinaAccessToken == null) {
				// request.setAttribute("ERRMSG", "Authtimeout");
				return "user/login";
			}

			String bindType = request.getParameter("BINDTYPE");
			if (bindType == null) {
				request.setAttribute("BINDTYPE_IS_NULL", "true");
				return "user/loginunion";
			}

			String username = request
					.getParameter(Constant.USER_LOGIN_USERNAME);
			String password = request.getParameter("USER_PASSWORD");
			if (username == null || "".equals(username)) {
				request.setAttribute("USERNAME_PASSWORD_ERROR", "true");
				return "user/loginunion";
			}
			if (password == null || "".equals(password)) {
				request.setAttribute("USERNAME_PASSWORD_ERROR", "true");
				return "user/loginunion";
			}

			// 判断是创建账户 还是帮定已经有的账户
			boolean isEmail = MobilePurseSecurityUtils.checkEmail(username, 0);
			boolean isMobile = MobilePurseSecurityUtils
					.isJointMobileNumber(username);
			String mobile = null;
			String email = null;
			if (isEmail) {
				email = username;
			} else if (isMobile) {
				mobile = username;
			} else {
				request.setAttribute("USERNAME_PARAM_ERROR", "true");
				return "user/loginunion";
			}

			User user = SingletonLoginUtils.getMemcacheUser(request);
			weiboService = (WeiboService) BeanUtils.getBean(request,
					"SINACONFIGService");

			if (user != null) {
				Long userid = user.getId();
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						sinaAccessToken.getUserId() + "",
						ProfileType.SINACONFIG);

				// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
								resToken.getUserId() + "",
								ProfileType.SINACONFIG);
						RequestToken bAccessToken = (RequestToken) weiboService
								.getBindingAccessToken(userid,
										ProfileType.SINACONFIG);
						if (bAccessToken.getUserId() != 0) {
							weiboService.updateBindingAccessToken(resToken,
									userid, ProfileType.SINACONFIG);
						} else {
							weiboService.addBindingAccess(userid, resToken,
									ProfileType.SINACONFIG);
						}
					}
				} else {
					RequestToken bAccessToken = (RequestToken) weiboService
							.getBindingAccessToken(userid,
									ProfileType.SINACONFIG);
					if (bAccessToken.getUserId() != 0) {
						weiboService.updateBindingAccessToken(resToken, userid,
								ProfileType.SINACONFIG);
					} else {
						weiboService.addBindingAccess(userid, resToken,
								ProfileType.SINACONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService
						.getWeiboNames(userid);
				// request.setAttribute("WEIBO_NAMES", weiboNames);
				// memCacheService.set("WEIBO_NAMES_"+user.getId(), weiboNames);
				SingletonLoginUtils.addSingleton(user, userService,
						userid + "", response, false, request);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", user);
			} else {
				if ("CREATEACCOUNT".equals(bindType)) {
				
					
					
					// 注册，用户已经存在
					User newUser = null;
					if (!isEmail) {
						request.setAttribute("USERNAME_PARAM_ERROR", "true");
						return "/user/loginunion";
					}
					User xnewUser = null;
					try {
						xnewUser = userService.findUserByEmail(email);
					} catch (UserException e1) {
						e1.printStackTrace();
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								resToken.getScreenName());
						return "user/loginunion";
					}

					if (xnewUser != null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								resToken.getScreenName());
						return "user/loginunion";
					}

					try {
						String ip=WebUtils.getIpAddr(request);
						newUser = userService.addUserEmailRegist(email,
								password,ip);
					} catch (UserException e) {
						e.printStackTrace();
					} catch (AccountException e) {
						e.printStackTrace();
					}
					// 新用户绑定
					if (newUser == null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								resToken.getScreenName());
						return "user/loginunion";
					}
					//打印日志
					
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snsreg");
					logMap2.put("sns", SINA);
					logMap2.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap2);
					
					Map<String,String> logMap=LogAction.getLogMap(request, response);
					logMap.put("action", "UserRegInSite");
					logMap.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap);
					// 加入扩展信息,激活邮件信息
					try {
						String emailValidateUrl = propertyUtil
								.getProperty(Constant.EMAIL_VALIDATE_URL);
						StringBuilder sb = new StringBuilder();
						sb.append(emailValidateUrl);
						sb.append("?id=" + newUser.getId() + "&userkey=");
						String secret = MobilePurseSecurityUtils.hmacSign(
								newUser.getCustomerkey(), newUser.getId() + "");
						sb.append(secret);
						String subject = "千品网邮箱认证邮件"; // 确认?
						UserProfile userProfile = userService.getProfile(
								newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
						if (userProfile == null) {
							userService.addProfile(
									Constant.EMAIL_REGIST_URLKEY, secret,
									newUser.getId(), ProfileType.USERCONFIG);
						} else {
							userProfile.setValue(secret);
							userService.updateProfile(userProfile);
						}
						String time = DateUtils.dateToStr(new Date(),
								"yyyy年MM月dd日 HH:mm");
						String date = DateUtils.dateToStr(new Date());
						// 设置动态参数
						Object[] emailParams = new Object[] { time,
								sb.toString(), date };

						// 邮件模板参数未设置
						emailService.send(null, null, null, null, null,
								subject, new String[] { email }, null, null,
								new Date(), emailParams,
								Constant.EMAIL_VALIDATE_TEMPLATE);
					} catch (Exception e) {
						log.info("send email success....");
						e.printStackTrace();
					}

					// weiboService.addBindingAccess(newUser.getId(), resToken,
					// ProfileType.SINACONFIG);

					// 先判断之前这个微博账户 是否绑定过其他账户 假如绑定了先清除 最后给这个账户绑定上 update by
					// ye.tian
					// at 2011.6.19
					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(
									sinaAccessToken.getUserId() + "",
									ProfileType.SINACONFIG);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(newUser.getId() + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									sinaAccessToken.getUserId() + "",
									ProfileType.SINACONFIG);
							weiboService.addBindingAccess(newUser.getId(),
									resToken, ProfileType.SINACONFIG);
						}
					} else {
						weiboService.addBindingAccess(newUser.getId(),
								resToken, ProfileType.SINACONFIG);
					}

					// request.getSession().setAttribute(Constant.USER_LOGIN,newUser);
					SingletonLoginUtils.addSingleton(newUser, userService,
							newUser.getId() + "", response, false, request);

					Map<String, String> weiboNames = weiboService
							.getWeiboNames(newUser.getId());
					// request.setAttribute("WEIBO_NAMES", weiboNames);
					// memCacheService.set("WEIBO_NAMES_"+newUser.getId(),
					// weiboNames);
					memCacheService.set("WEIBO_NAMES_" + newUser.getId(),
							weiboNames);
					request.setAttribute("WEIBO_NAMES", weiboNames);
					request.setAttribute("QIANPIN_USER", newUser);
					SingletonLoginUtils.addSingleton(newUser, userService,
							newUser.getId() + "", response, false, request);
					// 注册成功进入成功页面
					String requesturl = "";
					requesturl=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
					if(requesturl==null||"".equals(requesturl)){
						requesturl=WebUtils.getCookieValue(
								"REQUESTURI_REFER_COOKIE", request);
					}
					request.setAttribute("refer_url", requesturl);
					
					/**
					* 根据渠道号码,送优惠券
					* add by wangweijie
					*/
					String csid = WebUtils.getCookieValue("bi_csid", request);
		
					Map<String, String> sourceMap = new HashMap<String, String>();
					sourceMap.put("userId", String.valueOf(newUser.getId()));	//用户ID
					sourceMap.put("csid", csid);		//优惠券密码
					sourceMap.put("reqChannel","WEB");	//web		
					Map<String, String> returnMap = trxHessianServiceGateWay.autoBindCoupon(sourceMap);
					log.info("+++++++++autoBindCoupon return:" + returnMap);
					
					/**
					 * 注册成功，添加10元优惠券
					 * add by wangweijie 2012-07-09 --- begin
					 */
					//String loginRegSource = request.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
					long couponAmount = Long.parseLong(Constant.COUPON_AMOUNT);	//  优惠金额
					long vmAccountId = Long.parseLong(Constant.COUPON_VMACCOUNT_ID);	//虚拟账户主键
					String validDate = Constant.COUPON_VMACCOUNT_LOSEDATE;	//有效期，跟虚拟账户的有效期一直（省去查数据库）
					long maxAge = (DateUtils.toDate(validDate, "yyyy-MM-dd").getTime() - new Date().getTime()) /1000; //cookie 最大有效时间，单位为毫秒，需要转换为秒
					maxAge = maxAge<0 ? 0 : maxAge;	//负数情况的处理
					
					if(!StringUtils.isEmpty(csid) && csid.startsWith("portal")){//上线后放开
						boolean addCouponResult = false;
						try{
							String description = "优惠券有效期"+validDate.substring(0,10)+"；逾期作废；不可提现";	//
							addCouponResult = userService.noTscAddCouponsForUser(newUser.getId(),vmAccountId, couponAmount, description);
						}catch (Exception e) {
							e.printStackTrace();
							log.debug(e);
						}
						if(addCouponResult){
							//日志开始
							Map<String, String> couponLogMap = LogAction.getLogMap(request,response);
							couponLogMap.put("action", "mk_cashback");//线上优惠券日志埋点
							couponLogMap.put("uid", String.valueOf(newUser.getId()));
							LogAction.printLog(couponLogMap);
							//日志结束
							//在COOKIE里添加新增10元优惠券信息
							request.setAttribute("couponAmount", couponAmount);
							request.setAttribute("addCouponResult", "success");
							response.addCookie(WebUtils.cookie("NEW_COUPON_TIPS_"+newUser.getId(), "true", (int)maxAge));
						}
					}
					/**
					 *注册成功，添加10元优惠券
					 *add by wangweijie 2012-07-09 -----end
					 */
					
					return "user/registsuccess";
				} else if ("LOGINACCOUNT".equals(bindType)) {

					User u = null;
					try {
						u = userService.isUserLogin(mobile, password, email);
					} catch (UserException e) {
						log.info("用户微博认证成功,登录失败!");
						e.printStackTrace();
						request.setAttribute("USER_LOGIN_ERROR", "true");
						return "/user/loginunion";
					}
					if (u == null) {
						log.info("用户微博认证成功,登录失败!");
						request.setAttribute("USER_LOGIN_ERROR", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME", weiboName);
						return "/user/loginunion";
					}
					Map<String,String> logMap=LogAction.getLogMap(request, response);
					logMap.put("action", "u_snsbind");
					logMap.put("sns", SINA);
					LogAction.printLog(logMap);
					// request.getSession().setAttribute(Constant.USER_LOGIN,
					// u);
					SingletonLoginUtils.addSingleton(u, userService, u.getId()
							+ "", response, false, request);
					Long userid = u.getId();

					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(
									sinaAccessToken.getUserId() + "",
									ProfileType.SINACONFIG);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(userid + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									sinaAccessToken.getUserId() + "",
									ProfileType.SINACONFIG);
							RequestToken bAccessToken = (RequestToken) weiboService
									.getBindingAccessToken(userid,
											ProfileType.SINACONFIG);
							if (bAccessToken.getUserId() != 0) {
								weiboService.updateBindingAccessToken(resToken,
										userid, ProfileType.SINACONFIG);
							} else {
								weiboService.addBindingAccess(userid, resToken,
										ProfileType.SINACONFIG);
							}
						}
					} else {
						RequestToken bAccessToken = (RequestToken) weiboService
								.getBindingAccessToken(userid,
										ProfileType.SINACONFIG);
						if (bAccessToken.getUserId() != 0) {
							weiboService.updateBindingAccessToken(resToken,
									userid, ProfileType.SINACONFIG);
						} else {
							weiboService.addBindingAccess(userid, resToken,
									ProfileType.SINACONFIG);
						}
					}
					Map<String, String> weiboNames = weiboService
							.getWeiboNames(userid);
					// request.setAttribute("WEIBO_NAMES", weiboNames);
					// memCacheService.set("WEIBO_NAMES_"+u.getId(),
					// weiboNames);
					request.setAttribute("WEIBO_NAMES", weiboNames);
					memCacheService.set("WEIBO_NAMES_" + u.getId(), weiboNames);
					request.setAttribute("QIANPIN_USER", u);
					SingletonLoginUtils.addSingleton(u, userService, u.getId()
							+ "", response, false, request);
					// if(!isBinding){
					// weiboService.removeBindingAccessTokenByWeiboId(sinaAccessToken.getUserId()+"",
					// ProfileType.SINACONFIG);
					// weiboService.addBindingAccess(userid, sinaAccessToken,
					// ProfileType.SINACONFIG);
					// }
				} else {
					request.setAttribute("ERRMSG", "系统繁忙,请稍候再试!");
					return new ModelAndView("redirect:../500.html");
				}
			}
			// 假如是交易过来的授权 认证绑定账户 需要跳转到交易之前的路径
			String trxauthz = request.getParameter("trxauthz");
			if (trxauthz != null && !"".equals(trxauthz)) {
				String weibo_referurl = (String) request.getSession()
						.getAttribute("weibo_referurl");
				if (weibo_referurl != null) {
					return "redirect:" + weibo_referurl;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
		String refer_url ="";
		refer_url=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
		if(refer_url==null||"".equals(refer_url)){
			refer_url=WebUtils.getCookieValue(
					"REQUESTURI_REFER_COOKIE", request);
		}
		if(refer_url!=null&&!"".equals(refer_url)){
			return new ModelAndView("redirect:" + refer_url);
		}
		// 默认进入 用户账户中心
		return "/user/useraccount";
	}

	@RequestMapping("/user/getSinaUserinfo.do")
	public Object getSinaUserinfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String stokenauth = WebUtils.getCookieValue("STOKENAUTH", request);
		Map<String, String> logMap = LogAction.getLogMap(request,
				response);
		logMap.put("action", "u_snsreq");
		LogAction.printLog(logMap);
		if (stokenauth == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
			//return new ModelAndView("redirect:../500.html");
		}
		RequestToken resToken = (RequestToken) memCacheService
				.get("SINA_REQUESTTOKEN_" + stokenauth);

		// RequestToken resToken=(RequestToken)
		// request.getSession().getAttribute("SINA_REQUESTTOKEN");
		if (resToken == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
			//return new ModelAndView("redirect:../500.html");
		}
		request.setAttribute("WEIBO_SITE_TYPE",
				propertyUtil.getProperty("SINA_APP_NAME"));
		String trxauthz = (String) request.getAttribute("trxauthz");
		if (trxauthz != null) {
			request.setAttribute("trxauthz", "trxauthz");
		}
		weiboService = (WeiboService) BeanUtils.getBean(request,
				"SINACONFIGService");

		String verifier = request.getParameter("oauth_verifier");
		if (verifier == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			//return new ModelAndView("redirect:../500.html");
		}
		SinaAccessToken accessToken = WebOAuth.requstAccessToken(resToken,
				verifier);
		long sinaUserId = 0;
		if (accessToken == null) {
			// SinaAccessToken xAccessToken=(SinaAccessToken)
			// memCacheService.get("SINA_USER_ACCESSTOKEN_"+stokenauth);
			// if(xAccessToken==null){
			// return "user/login";
			// }
			// else return "user/loginunion";
			return "user/login";
		}

		if (accessToken != null) {
			sinaUserId = accessToken.getUserId();
			resToken.setUserId(sinaUserId);
		}
		Map<String, String> logMapx = LogAction.getLogMap(request,
				response);
		logMapx.put("action", "u_sns");
		logMapx.put("sns", SINA);
		LogAction.printLog(logMapx);
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken.getToken(), accessToken.getTokenSecret());
		SinaUser sinaUser = null;
		String screenName = "";
		try {
			sinaUser = weibo.showUser(sinaUserId + "");
			if (sinaUser != null) {
				screenName = sinaUser.getScreenName();
				log.info("连接新浪微博,用户名称为:" + screenName);
				accessToken.setHeadIcon(String.valueOf(sinaUser.getProfileImageURL()));
				resToken.setHeadIcon(String.valueOf(sinaUser.getProfileImageURL()));
			}
			// 用户绑定微博所需要的信息
			accessToken.setScreenName(screenName);
			memCacheService.set("SINA_USER_ACCESSTOKEN_" + stokenauth,
					accessToken);
			// request.setAttribute("SINA_USER_ACCESSTOKEN", accessToken);
			// memCacheService.set("WEIBO_NAMES_"+stokenauth, screenName);
			request.setAttribute("WEIBO_USER_SCREENNAME", screenName);


			resToken.setScreenName(screenName);
			memCacheService.set("SINA_REQUESTTOKEN_" + stokenauth, resToken);
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				Long userid = user.getId();

				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						sinaUserId + "", ProfileType.SINACONFIG);
				// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
								sinaUserId + "", ProfileType.SINACONFIG);
						RequestToken bAccessToken = (RequestToken) weiboService
								.getBindingAccessToken(userid,
										ProfileType.SINACONFIG);
						if (bAccessToken.getUserId() != 0) {
							weiboService.updateBindingAccessToken(resToken,
									userid, ProfileType.SINACONFIG);
						} else {
							weiboService.addBindingAccess(userid, resToken,
									ProfileType.SINACONFIG);
						}
					}
				} else {
					RequestToken bAccessToken = (RequestToken) weiboService
							.getBindingAccessToken(userid,
									ProfileType.SINACONFIG);
					if (bAccessToken.getUserId() != 0) {
						weiboService.updateBindingAccessToken(resToken, userid,
								ProfileType.SINACONFIG);
					} else {
						weiboService.addBindingAccess(userid, resToken,
								ProfileType.SINACONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService
						.getWeiboNames(userid);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", user);
				// memCacheService.set("WEIBO_NAMES_"+user.getId(),weiboNames);;

				// request.setAttribute("WEIBO_URL",
				// "/user/getSinaUserinfo.do");
				// return "/user/useraccount";
			} else {
				request.setAttribute("WEIBO_URL",
						"/user/bindSinaWeiboAccount.do");

				// 判断该微博是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						sinaUserId + "", ProfileType.SINACONFIG);
				if (bindUserid != 0) {
					User accessUser = userService.findById(bindUserid);
					if (accessUser == null) {
						return "user/login";
					}
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snslogin");
					logMap2.put("sns", SINA);
					LogAction.printLog(logMap2);
					
					Map<String, String> weiboNames = weiboService.getWeiboNames(bindUserid);
					request.setAttribute("WEIBO_NAMES", weiboNames);
					memCacheService.set("WEIBO_NAMES_" + bindUserid, weiboNames);
					request.setAttribute("QIANPIN_USER", accessUser);
					
					//记录每次用户登陆
					UserLoginLog ulLog = new UserLoginLog();
					ulLog.setUserid(accessUser.getId());
					ulLog.setUserEmail(accessUser.getEmail());
					ulLog.setLoginIp(WebUtils.getIpAddr(request));
					try {
						userService.addLoginLog(ulLog);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					SingletonLoginUtils.addSingleton(accessUser, userService,
							accessUser.getId() + "", response, false, request);
					String refer_url = WebUtils.getCookieValue(
							"REQUESTURI_REFER_COOKIE", request);
					if (refer_url == null) {
						refer_url = "/forward.do?param=index.index";
						request.setAttribute("refer_url", refer_url);
						return "user/login_forward";
					}
					if("http://www.qianpin.com/user/goActivePage.do".equals(refer_url)){
						Cookie weiboCookie=WebUtils.cookie("WEIBO_FROMWEB", "SINACONFIG", -1);
						response.addCookie(weiboCookie);
					}
					request.setAttribute("refer_url", refer_url);
					log.info("sina authz login success....redirect url:"
							+ refer_url);
					return new ModelAndView("redirect:" + refer_url);
				}

				return "user/loginunion";
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return "/user/useraccount";

	}

	@RequestMapping("/user/sinaActiveAuthorization.do")
	public ModelAndView sinaActiveAuthorization(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String stokenauth = WebUtils.getCookieValue("STOKENAUTH", request);
		if (stokenauth == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
			//return new ModelAndView("redirect:../500.html");
		}
		RequestToken resToken = (RequestToken) memCacheService
				.get("SINA_REQUESTTOKEN_" + stokenauth);

		if (resToken == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
			//return new ModelAndView("redirect:../500.html");
		}
		request.setAttribute("WEIBO_SITE_TYPE",
				propertyUtil.getProperty("SINA_APP_NAME"));
		String trxauthz = (String) request.getAttribute("trxauthz");
		if (trxauthz != null) {
			request.setAttribute("trxauthz", "trxauthz");
		}
		weiboService = (WeiboService) BeanUtils.getBean(request,
				"SINACONFIGService");

		String verifier = request.getParameter("oauth_verifier");
		if (verifier == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			
			//request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			//return new ModelAndView("redirect:../500.html");
		}
		SinaAccessToken accessToken = WebOAuth.requstAccessToken(resToken,
				verifier);
		long sinaUserId = 0;

		if (accessToken != null) {
			sinaUserId = accessToken.getUserId();
			resToken.setUserId(sinaUserId);
		}
		
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken.getToken(), accessToken.getTokenSecret());
		SinaUser sinaUser = null;
		String screenName = "";
		try {
			sinaUser = weibo.showUser(sinaUserId + "");
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		if (sinaUser != null) {
			screenName = sinaUser.getScreenName();
			log.info("连接新浪微博,用户名称为:" + screenName);
		}
		// 用户绑定微博所需要的信息
		accessToken.setScreenName(screenName);
		memCacheService.set("SINA_USER_ACCESSTOKEN_" + stokenauth,
				accessToken);

		resToken.setScreenName(screenName);
		memCacheService.set("SINA_REQUESTTOKEN_" + stokenauth, resToken);
		
		
		String refer_url = WebUtils.getCookieValue(
				"REQUESTURI_REFER_COOKIE", request);
		if("http://www.qianpin.com/user/goActivePage.do".equals(refer_url)){
			Cookie weiboCookie=WebUtils.cookie("WEIBO_FROMWEB", "SINACONFIG", 60*60);
			response.addCookie(weiboCookie);
		}
		return new ModelAndView("redirect:" + refer_url);
	}

	@RequestMapping("/user/sinaAuthorization.do")
	public String redirectOAuthzPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		RequestToken resToken=null;
		String ACTIVE_URL=request.getParameter("ACTIVE_URL");
		if(ACTIVE_URL!=null&&!"".equals(ACTIVE_URL)){
			resToken=WebOAuth.request("http://www.qianpin.com/user/sinaActiveAuthorization.do");
		}else{
			resToken = WebOAuth.request(propertyUtil
				.getProperty("SINA_API_CALL_BACK"));
		}
		
		// User user=(User)
		// request.getSession().getAttribute("BEIKER_USER_LOGIN");
		// 判断是否是交易过来的 授权
		String trxauthz = request.getParameter("trxauthz");

		if (trxauthz != null && "trxauthz".equals(trxauthz)) {
			String referurl = request.getParameter("referurl");
			request.getSession().setAttribute("weibo_referurl", referurl);
			request.setAttribute("trxauthz", trxauthz);
		}

		if (resToken != null) {
			// request.getSession().setAttribute("SINA_REQUESTTOKEN_",resToken);
			// 存放一小时cookie
			int validy = 60 * 60 * 1;
			Cookie cookie = WebUtils.cookie("STOKENAUTH",
					resToken.getTokenSecret(), validy);
			response.addCookie(cookie);
			memCacheService.set(
					"SINA_REQUESTTOKEN_" + resToken.getTokenSecret(), resToken);
			String authorizationUrl = resToken.getAuthorizationURL();
			return "redirect:" + authorizationUrl;
		}

		request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
		return "/user/regist";
	}

	public WeiboService getWeiboService() {
		return weiboService;
	}

	public void setWeiboService(WeiboService weiboService) {
		this.weiboService = weiboService;
	}

	public UserService getUserService() {
		return userService;
	}

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
