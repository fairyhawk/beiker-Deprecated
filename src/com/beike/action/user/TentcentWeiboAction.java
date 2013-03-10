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
import com.beike.util.singletonlogin.SingletonLoginUtils;
import com.beike.util.tencent.QWeiboSyncApi;
import com.beike.util.tencent.ResModel;
import com.beike.util.tencent.TencentUser;
import com.beike.util.tencent.QWeiboType.ResultType;

/**
 * <p>
 * Title:腾讯微博
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
 * @date Sep 13, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class TentcentWeiboAction {
	private static final PropertyUtil property = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);
	private static String customKey = property
			.getProperty(Constant.TENCENT_APP_KEY);
	private static String customSecrect = property
			.getProperty(Constant.TENCENT_APP_SECRET);

	private static String QIANPIN_URL = "http://t.qq.com/qianpincom";
	private static String QIANPIN_NAME = "千品网";
	private String tokenKey = null;
	private String tokenSecrect = null;
//	private String verify = null;
//	private String user_name = null;
	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	
	private static String TENCENT="TENCENT";

	private final Log log = LogFactory.getLog(TentcentWeiboAction.class);

	private WeiboService weiboService;
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);
	@Autowired
	private UserService userService;

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private EmailService emailService;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 关注微博
	 */
	public String addNotice(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO", request);
		// 未用微博登录 跳转到千品微博页面
		if (tokenSecret == null) {
			writeStr(response, QIANPIN_URL);
			return null;
		}
		ResModel resModel = (ResModel) memCacheService
				.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
		// 未用微博登录 跳转到千品微博页面
		if (resModel == null) {
			writeStr(response, QIANPIN_URL);
			return null;
		}
		QWeiboSyncApi api = new QWeiboSyncApi();

		boolean noticeFlag = api.addNotice(QIANPIN_NAME, customKey,
				customSecrect, resModel.getToken(), resModel.getTokenSecret(),
				ResultType.ResultType_Json);
		// 关注成功
		if (noticeFlag) {
			writeStr(response, "true");
			return null;
		} else {
			writeStr(response, QIANPIN_URL);
			return null;
		}
	}

	/**
	 * ajax 发微博
	 */
	@RequestMapping("/user/sendTencentMessage.do")
	public String sendTencentMessage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO", request);
		QWeiboSyncApi api = new QWeiboSyncApi();
		String content = request.getParameter("content");
		// cookie null 授权失败
		if (tokenSecret != null) {
			ResModel resModel = (ResModel) memCacheService
					.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
			if (resModel != null) {
				// 发布微博
				String res = api.publishMsg(customKey, customSecrect, resModel
						.getToken(), resModel.getTokenSecret(), content, null,
						ResultType.ResultType_Json);
				log.info("return send message:" + res);
				try {
					response.getWriter().write("success");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@RequestMapping("/user/bindTencentInfoAction.do")
	public Object bindTencentInfoAction(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setAttribute("WEIBO_SITE_TYPE", propertyUtil
					.getProperty("TENCENT_APP_NAME"));
			String stokenauth = WebUtils.getCookieValue("TENCENT_WEIBO",
					request);
			if (stokenauth == null) {

				return "user/login";
			}
			ResModel resModel = (ResModel) memCacheService
					.get("TENCENT_REQUESTTOKEN_" + stokenauth);
			// 解析token
			if (resModel == null) {
				return new ModelAndView("redirect:../500.html");
			}
			String weiboName = resModel.getNickName();
			request.setAttribute("WEIBO_USER_SCREENNAME", weiboName);
			request.setAttribute("WEIBO_URL", "/user/bindTencentInfoAction.do");

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
					"TENCENTCONFIGService");

			if (user != null) {
				Long userid = user.getId();
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						resModel.getWeiboid() + "", ProfileType.TENCENTCONFIG);

				// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(resModel
								.getWeiboid()
								+ "", ProfileType.TENCENTCONFIG);
						RequestToken bAccessToken = (RequestToken) weiboService
								.getBindingAccessToken(userid,
										ProfileType.TENCENTCONFIG);
						if (bAccessToken.getUserId() != 0) {
							weiboService.updateBindingAccessToken(resModel,
									userid, ProfileType.TENCENTCONFIG);
						} else {
							weiboService.addBindingAccess(userid, resModel,
									ProfileType.TENCENTCONFIG);
						}
					}
				} else {
					RequestToken bAccessToken = (RequestToken) weiboService
							.getBindingAccessToken(userid,
									ProfileType.TENCENTCONFIG);
					if (bAccessToken.getUserId() != 0) {
						weiboService.updateBindingAccessToken(resModel, userid,
								ProfileType.TENCENTCONFIG);
					} else {
						weiboService.addBindingAccess(userid, resModel,
								ProfileType.TENCENTCONFIG);
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
						request.setAttribute("WEIBO_USER_SCREENNAME", resModel
								.getNickName());
						return "user/loginunion";
					}

					if (xnewUser != null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME", resModel
								.getNickName());
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
						request.setAttribute("WEIBO_USER_SCREENNAME", resModel
								.getNickName());
						return "user/loginunion";
					}
					//打印日志
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snsreg");
					logMap2.put("sns",TENCENT);
					logMap2.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap2);
					
					
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
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
							.getBindingAccessTokenByWeiboId(resModel
									.getWeiboid()
									+ "", ProfileType.TENCENTCONFIG);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(newUser.getId() + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									resModel.getWeiboid() + "",
									ProfileType.TENCENTCONFIG);
							weiboService.addBindingAccess(newUser.getId(),
									resModel, ProfileType.TENCENTCONFIG);
						}
					} else {
						weiboService.addBindingAccess(newUser.getId(),
								resModel, ProfileType.TENCENTCONFIG);
					}

					// request.getSession().setAttribute(Constant.USER_LOGIN,newUser);

					Map<String, String> weiboNames = weiboService
							.getWeiboNames(newUser.getId());
					// request.setAttribute("WEIBO_NAMES", weiboNames);
					// memCacheService.set("WEIBO_NAMES_"+newUser.getId(),
					// weiboNames);
					if(weiboNames!=null){
						memCacheService.set("WEIBO_NAMES_" + newUser.getId(),
								weiboNames);
						request.setAttribute("WEIBO_NAMES", weiboNames);
					}
					request.setAttribute("QIANPIN_USER", newUser);
					SingletonLoginUtils.addSingleton(newUser, userService,
							newUser.getId() + "", response, false, request);
					// 注册成功进入成功页面
					String requesturl = "";
					requesturl = (String) request.getSession().getAttribute(
							"REQUESTURI_REFER_COOKIE");
					if (requesturl == null || "".equals(requesturl)) {
						requesturl = WebUtils.getCookieValue(
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
						request
								.setAttribute("WEIBO_USER_SCREENNAME",
										weiboName);
						return "/user/loginunion";
					}
					// request.getSession().setAttribute(Constant.USER_LOGIN,
					// u);
					SingletonLoginUtils.addSingleton(u, userService, u.getId()
							+ "", response, false, request);
					Long userid = u.getId();

					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(resModel
									.getWeiboid()
									+ "", ProfileType.TENCENTCONFIG);
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "u_snsbind");
					logMap.put("sns",TENCENT);
					LogAction.printLog(logMap);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(userid + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									resModel.getWeiboid() + "",
									ProfileType.TENCENTCONFIG);
							RequestToken bAccessToken = (RequestToken) weiboService
									.getBindingAccessToken(userid,
											ProfileType.TENCENTCONFIG);
							if (bAccessToken.getUserId() != 0) {
								weiboService.updateBindingAccessToken(resModel,
										userid, ProfileType.TENCENTCONFIG);
							} else {
								weiboService.addBindingAccess(userid, resModel,
										ProfileType.TENCENTCONFIG);
							}
						}
					} else {
						ResModel bAccessToken = (ResModel) weiboService
								.getBindingAccessToken(userid,
										ProfileType.TENCENTCONFIG);
						if (bAccessToken.getWeiboid() != null) {
							weiboService.updateBindingAccessToken(resModel,
									userid, ProfileType.TENCENTCONFIG);
						} else {
							weiboService.addBindingAccess(userid, resModel,
									ProfileType.TENCENTCONFIG);
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
				} else {
					request.setAttribute("ERRMSG", "系统繁忙,请稍候再试!");
					return "redirect:../500.html";
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
		String refer_url = "";
		refer_url = (String) request.getSession().getAttribute(
				"REQUESTURI_REFER_COOKIE");
		if (refer_url == null || "".equals(refer_url)) {
			refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE",
					request);
		}
		if (refer_url != null && !"".equals(refer_url)) {
			return "redirect:" + refer_url;
		}
		// 默认进入 用户账户中心
		return "/user/useraccount";
	}

	/**
	 * 回调后方法
	 */
	@RequestMapping("/user/getTencentInfoAction.do")
	public ModelAndView getTencentInfoAction(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> logMap = LogAction.getLogMap(request,
				response);
		logMap.put("action","u_snsreq");
		LogAction.printLog(logMap);
		String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO", request);
		String oauth_verifier = request.getParameter("oauth_verifier");
		if (tokenSecret == null) {
			return new ModelAndView("redirect:../500.html");
		}
		ResModel resModel = (ResModel) memCacheService
				.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
		// 解析token
		if (resModel == null) {
			return new ModelAndView("redirect:../500.html");
		}
		QWeiboSyncApi api = new QWeiboSyncApi();

		String responseStr = api.getAccessToken(customKey, customSecrect,
				resModel.getToken(), resModel.getTokenSecret(), oauth_verifier);
		log.info("responseStr:" + responseStr);
		TencentUser tencentUser = null;
		if (parseToken(responseStr)) {
			// 取出用户信息
			resModel.setOauth_verifier(oauth_verifier);

			resModel.setToken(tokenKey);
			resModel.setTokenSecret(tokenSecrect);

			tencentUser = api.getUserMsg(customKey, customSecrect, tokenKey,
					tokenSecrect, ResultType.ResultType_Json);
			// nickname
			if (tencentUser != null) {
				String nickName = tencentUser.getNickname();
				log.info("nickname:" + nickName);
				resModel.setWeiboname(nickName);
				resModel.setNickName(tencentUser.getName());
				resModel.setWeiboid(tencentUser.getUid());
				resModel.setHead(tencentUser.getHead());
				resModel.setHeadIcon(tencentUser.getHead());
				memCacheService.set("TENCENT_REQUESTTOKEN_" + tokenSecret,
						resModel);
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "u_sns");
				logMap2.put("sns", TENCENT);
				LogAction.printLog(logMap2);
			}
		}
		request.setAttribute("WEIBO_SITE_TYPE", property
				.getProperty("TENCENT_APP_NAME"));

		weiboService = (WeiboService) BeanUtils.getBean(request,
				"TENCENTCONFIGService");
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user != null) {
			Long userid = user.getId();

			Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
					resModel.getWeiboid() + "", ProfileType.TENCENTCONFIG);
			// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
			if (bindUserid != 0) {
				// 用户绑定过 但是不是用的这个账号
				if (!((bindUserid + "").equals(userid + ""))) {
					weiboService.removeBindingAccessTokenByWeiboId(resModel
							.getWeiboid()
							+ "", ProfileType.TENCENTCONFIG);
					RequestToken bAccessToken = (RequestToken) weiboService
							.getBindingAccessToken(userid,
									ProfileType.TENCENTCONFIG);
					if (bAccessToken.getUserId() != 0) {
						weiboService.updateBindingAccessToken(resModel, userid,
								ProfileType.TENCENTCONFIG);
					} else {
						weiboService.addBindingAccess(userid, resModel,
								ProfileType.TENCENTCONFIG);
					}
				}
			} else {
				RequestToken bAccessToken = (RequestToken) weiboService
						.getBindingAccessToken(userid,
								ProfileType.TENCENTCONFIG);
				if (bAccessToken.getUserId() != 0) {
					weiboService.updateBindingAccessToken(resModel, userid,
							ProfileType.TENCENTCONFIG);
				} else {
					weiboService.addBindingAccess(userid, resModel,
							ProfileType.TENCENTCONFIG);
				}
			}
			Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
			request.setAttribute("WEIBO_NAMES", weiboNames);
			memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
			request.setAttribute("QIANPIN_USER", user);
		} else {
			request.setAttribute("WEIBO_URL", "/user/bindTencentInfoAction.do");

			// 判断该微博是否有绑定账户 假如有绑定自动设置为登录状态
			Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
					resModel.getWeiboid() + "", ProfileType.TENCENTCONFIG);
			if (bindUserid != 0) {
				Map<String, String> logMap3 = LogAction.getLogMap(request,
						response);
				logMap3.put("action", "u_snslogin");
				logMap3.put("sns", TENCENT);
				LogAction.printLog(logMap3);
				
				User accessUser = userService.findById(bindUserid);
				if (accessUser == null) {
					return new ModelAndView("user/login");
				}
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
					return new ModelAndView("user/login_forward");
				}
				if ("http://www.qianpin.com/user/goActivePage.do"
						.equals(refer_url)) {
					Cookie weiboCookie = WebUtils.cookie("WEIBO_FROMWEB",
							"TENCENTCONFIG", -1);
					response.addCookie(weiboCookie);
				}
				request.setAttribute("refer_url", refer_url);
				log.info("sina authz tencent success....redirect url:"
						+ refer_url);
				return new ModelAndView("redirect:" + refer_url);
			}

			return new ModelAndView("user/loginunion");
		}

		return new ModelAndView("/user/useraccount");
	}
	@RequestMapping("/user/getTencentActiveAction.do")
	public Object getTencentActiveAction(HttpServletRequest request,
			HttpServletResponse response) {
		String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO", request);
		String oauth_verifier = request.getParameter("oauth_verifier");
		if (tokenSecret == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			//return new ModelAndView("redirect:../500.html");
		}
		ResModel resModel = (ResModel) memCacheService
				.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
		// 解析token
		if (resModel == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
			//return new ModelAndView("redirect:../500.html");
		}
		QWeiboSyncApi api = new QWeiboSyncApi();

		String responseStr = api.getAccessToken(customKey, customSecrect,
				resModel.getToken(), resModel.getTokenSecret(), oauth_verifier);
		log.info("responseStr:" + responseStr);
		TencentUser tencentUser = null;
		if (parseToken(responseStr)) {
			// 取出用户信息
			resModel.setOauth_verifier(oauth_verifier);

			resModel.setToken(tokenKey);
			resModel.setTokenSecret(tokenSecrect);

			tencentUser = api.getUserMsg(customKey, customSecrect, tokenKey,
					tokenSecrect, ResultType.ResultType_Json);
			// nickname
			if (tencentUser != null) {
				String nickName = tencentUser.getNickname();
				log.info("nickname:" + nickName);
				resModel.setWeiboname(nickName);
				resModel.setNickName(tencentUser.getName());
				resModel.setWeiboid(tencentUser.getUid());
				resModel.setHead(tencentUser.getHead());
				memCacheService.set("TENCENT_REQUESTTOKEN_" + tokenSecret,
						resModel);
			}
		}

		weiboService = (WeiboService) BeanUtils.getBean(request,
				"TENCENTCONFIGService");
		Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(resModel
				.getWeiboid()
				+ "", ProfileType.TENCENTCONFIG);
		if (bindUserid != 0) {
			User accessUser = userService.findById(bindUserid);
			if (accessUser == null) {
				return new ModelAndView("user/login");
			}
			SingletonLoginUtils.addSingleton(accessUser, userService,
					accessUser.getId() + "", response, false, request);

		}

		String refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE",
				request);
		if (refer_url == null) {
			refer_url = "/forward.do?param=index.index";
			request.setAttribute("refer_url", refer_url);
			return new ModelAndView("user/login_forward");
		}
		if ("http://www.qianpin.com/user/goActivePage.do".equals(refer_url)) {
			Cookie weiboCookie = WebUtils.cookie("WEIBO_FROMWEB",
					"TENCENTCONFIG", 60 * 60);
			response.addCookie(weiboCookie);
		}
		request.setAttribute("refer_url", refer_url);
		log.info("tencent authz login success....redirect url:" + refer_url);
		return new ModelAndView("redirect:" + refer_url);

	}

	/**
	 * 授权方法
	 */
	@RequestMapping("/user/tencentAuthorization.do")
	public String redirectOAuthzPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		QWeiboSyncApi api = new QWeiboSyncApi();
		String callbackurl = null;
		String ACTIVE_URL=request.getParameter("ACTIVE_URL");
		if(ACTIVE_URL!=null&&!"".equals(ACTIVE_URL)){
			callbackurl="http://www.qianpin.com/user/getTencentActiveAction.do";
		}else{
			callbackurl=property.getProperty(Constant.TENCENT_APP_CALL_BACK);
		}
		
		String responseStr = api.getRequestToken(customKey, customSecrect,callbackurl);
		// 假如授权失败 返回错误页面
		if (parseToken(responseStr)) {
			ResModel resModel = new ResModel();
			resModel.setToken(tokenKey);
			resModel.setTokenSecret(tokenSecrect);
			int validy = 60 * 60 * 1;
			Cookie cookie = WebUtils.cookie("TENCENT_WEIBO", tokenSecrect,
					validy);
			response.addCookie(cookie);
			memCacheService.set("TENCENT_REQUESTTOKEN_" + tokenSecrect,
					resModel);
		}
		// 跳转到授权页面
		return "redirect:http://open.t.qq.com/cgi-bin/authorize?oauth_token="
				+ tokenKey;
	}

	boolean parseToken(String response) {
		if (response == null || response.equals("")) {
			return false;
		}

		String[] tokenArray = response.split("&");

		if (tokenArray.length < 2) {
			return false;
		}

		String strTokenKey = tokenArray[0];
		String strTokenSecrect = tokenArray[1];

		String[] token1 = strTokenKey.split("=");
		if (token1.length < 2) {
			return false;
		}
		tokenKey = token1[1];

		String[] token2 = strTokenSecrect.split("=");
		if (token2.length < 2) {
			return false;
		}
		tokenSecrect = token2[1];
		// String[] ne2=name.split("=");
		// if(ne2.length<2){
		// return false;
		// }
		// user_name=ne2[1];

		return true;
	}

	private void writeStr(HttpServletResponse response, String content) {
		try {
			response.getWriter().write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
}
