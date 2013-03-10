package com.beike.action.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.beike.util.hao3604j.Hao360;
import com.beike.util.hao3604j.Tuan360Model;
import com.beike.util.hao3604j.http.AccessToken;
import com.beike.util.hao3604j.http.RequestToken;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * com.beike.util.hao3604j.Tuan360Model.java
 * @description:
 * @author xuxiaoxian
 * @Company: Sinobo
 * @date: Apr 16, 2012，xuxiaoxian，create class
 */
@Controller
public class Tuan360Action {
	
	private static final PropertyUtil property = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	private final MemCacheService memCacheService = MemCacheServiceImpl .getInstance();
	
	private final Log log = LogFactory.getLog(Tuan360Action.class);
	
	private static String TUAN360="TUAN360";
	
	private WeiboService weiboService;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;

	
	/** 
	 * @date Apr 17, 2012
	 * @description:360帐号绑定，用户授权之后，选择创建账户或者登陆时调用此方法 
	 * 请求:1.邮箱:email 2.绑定手机号:mobile 3.密码:password 
	 *  4.验证后跳转页面:referurl 返回 request 5.账户类型 是注册还是登陆:BINDTYPE
	 *  创建账户：CREATEACCOUNT 登陆：LOGINACCOUNT 返回： 1.错误信息:ERRMSG
	 * @param model
	 * @param request
	 * @param response
	 * @return Object
	 * @throws 
	 */
	@RequestMapping("/user/bindTuan360Account.do")
	public Object bindTuan360Account(ModelMap model,
			HttpServletRequest request, HttpServletResponse response){
		try {
			request.setAttribute("WEIBO_SITE_TYPE",property.getProperty("TUAN360_APP_NAME"));
			String uuidstr = WebUtils.getCookieValue("TUAN360UUID", request);
			if (StringUtils.isBlank(uuidstr)) {
//				 request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
//				 return new ModelAndView("redirect:../500.html");
				return "user/login";
			}
			
			Tuan360Model tuan360Model = (Tuan360Model) memCacheService.get("TUAN360_USER_REQUESTTOKEN" + uuidstr);
			String qName = tuan360Model.getQname();
			request.setAttribute("WEIBO_USER_SCREENNAME", qName);
			request.setAttribute("WEIBO_URL", "/user/bindTuan360Account.do");
			if (tuan360Model == null) {
				return "user/login";
			}

			String bindType = request.getParameter("BINDTYPE");
			if (bindType == null) {
				request.setAttribute("BINDTYPE_IS_NULL", "true");
				return "user/loginunion";
			}
			String username = request.getParameter(Constant.USER_LOGIN_USERNAME);
			String password = request.getParameter("USER_PASSWORD");
			if (StringUtils.isEmpty(username)) {
					request.setAttribute("USERNAME_PASSWORD_ERROR", "true");
					return "user/loginunion";
			}
			if (StringUtils.isEmpty(password)) {
				request.setAttribute("USERNAME_PASSWORD_ERROR", "true");
				return "user/loginunion";
			}

			// 判断是创建账户 还是绑定已经有的账户
			boolean isEmail = MobilePurseSecurityUtils.checkEmail(username, 0);
			boolean isMobile = MobilePurseSecurityUtils.isJointMobileNumber(username);
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

			weiboService = (WeiboService) BeanUtils.getBean(request,"TUAN360CONFIGService");
			
			//获取当前登录用户信息
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				Long userid = user.getId();//千品网userid
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
												tuan360Model.getQid() + "",ProfileType.TUAN360CONFIG);

				// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
										tuan360Model.getQid() + "",ProfileType.TUAN360CONFIG);
						Tuan360Model tuan360 = (Tuan360Model) weiboService
							.getBindingAccessToken(userid,ProfileType.TUAN360CONFIG);
						if (StringUtils.isNotEmpty(tuan360.getQid())) {
							weiboService.updateBindingAccessToken(tuan360Model,
										userid, ProfileType.TUAN360CONFIG);
						} else {
							weiboService.addBindingAccess(userid, tuan360Model,
										ProfileType.TUAN360CONFIG);
						}
					}
				} else {
					Tuan360Model tuan360 = (Tuan360Model) weiboService
							.getBindingAccessToken(userid,ProfileType.TUAN360CONFIG);
					if (StringUtils.isNotEmpty(tuan360.getQid())) {
						weiboService.updateBindingAccessToken(tuan360Model, userid,
								ProfileType.TUAN360CONFIG);
					} else {
						weiboService.addBindingAccess(userid, tuan360Model,
								ProfileType.TUAN360CONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
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
					//验证用户名是否已存在
					try {
						xnewUser = userService.findUserByEmail(email);
					} catch (UserException e1) {
						e1.printStackTrace();
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",tuan360Model.getQname());
						return "user/loginunion";
					}
					if (xnewUser != null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",tuan360Model.getQname());
						return "user/loginunion";
					}

					try {
						String ip=WebUtils.getIpAddr(request);
						newUser = userService.addUserEmailRegist(email,password,ip);
					} catch (UserException e) {
						e.printStackTrace();
					} catch (AccountException e) {
						e.printStackTrace();
					}
					// 新用户绑定
					if (newUser == null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",tuan360Model.getQname());
						return "user/loginunion";
					}
					//打印日志
			
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snsreg");
					logMap2.put("sns", TUAN360);
					logMap2.put("uid", newUser.getId()+"");
				    LogAction.printLog(logMap2);
			
					Map<String,String> logMap=LogAction.getLogMap(request, response);
					logMap.put("action", "UserRegInSite");
					logMap.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap);
					// 加入扩展信息,激活邮件信息
					try {
						String emailValidateUrl = property.getProperty(Constant.EMAIL_VALIDATE_URL);
						StringBuilder sb = new StringBuilder();
						sb.append(emailValidateUrl);
						sb.append("?id=" + newUser.getId() + "&userkey=");
						String secret = MobilePurseSecurityUtils.hmacSign(newUser.getCustomerkey(), newUser.getId() + "");
						sb.append(secret);
						String subject = "千品网邮箱认证邮件"; // 确认?
						UserProfile userProfile = userService.getProfile(newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
						if (userProfile == null) {
							userService.addProfile(Constant.EMAIL_REGIST_URLKEY, 
													secret,newUser.getId(), ProfileType.USERCONFIG);
						} else {
							userProfile.setValue(secret);
							userService.updateProfile(userProfile);
						}
						String time = DateUtils.dateToStr(new Date(),"yyyy年MM月dd日 HH:mm");
						String date = DateUtils.dateToStr(new Date());
						// 设置动态参数
						Object[] emailParams = new Object[] { time,sb.toString(), date };

						// 邮件模板参数未设置
						emailService.send(null, null, null, null, null,subject, new String[] { email }, null, null,
										new Date(), emailParams,Constant.EMAIL_VALIDATE_TEMPLATE);
					} catch (Exception e) {
						log.info("send email success....");
						e.printStackTrace();
					}

				// 先判断之前这个360账户 是否绑定过其他账户 假如绑定了先清除 最后给这个账户绑定上 
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
								tuan360Model.getQid() + "",ProfileType.TUAN360CONFIG);
				// 绑定过
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(newUser.getId() + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
								tuan360Model.getQid()+ "",ProfileType.TUAN360CONFIG);
						weiboService.addBindingAccess(newUser.getId(),
								tuan360Model, ProfileType.TUAN360CONFIG);
					}
				} else {
					weiboService.addBindingAccess(newUser.getId(),
							tuan360Model, ProfileType.TUAN360CONFIG);
				}
	
				//将用户设为登录状态
				SingletonLoginUtils.addSingleton(newUser, userService,newUser.getId() + "", response, false, request);
				//团360登陆360用户ID cookie
				Cookie cookieUserid = WebUtils.cookie("TUAN360USERID", tuan360Model.getQid(), -1);
				response.addCookie(cookieUserid);
				
				Map<String, String> weiboNames = weiboService.getWeiboNames(newUser.getId());
				memCacheService.set("WEIBO_NAMES_" + newUser.getId(),weiboNames);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				request.setAttribute("QIANPIN_USER", newUser);
				
				// 注册成功进入成功页面
				String requesturl = "";
				requesturl=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
				if(StringUtils.isEmpty(requesturl)){
					requesturl=WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
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
					log.info("用户360帐号认证成功,登录失败!");
					e.printStackTrace();
					request.setAttribute("USER_LOGIN_ERROR", "true");
					return "/user/loginunion";
				}
				if (u == null) {
					log.info("用户360帐号认证成功,登录失败!");
					request.setAttribute("USER_LOGIN_ERROR", "true");
					request.setAttribute("WEIBO_USER_SCREENNAME", tuan360Model.getQname());
					return "/user/loginunion";
				}
				Map<String,String> logMap=LogAction.getLogMap(request, response);
				logMap.put("action", "u_snsbind");
				logMap.put("sns", TUAN360);
				LogAction.printLog(logMap);
				SingletonLoginUtils.addSingleton(u, userService, u.getId()+ "", response, false, request);
				Long userid = u.getId();
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
										tuan360Model.getQid() + "",ProfileType.TUAN360CONFIG);
				// 绑定过
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						
						weiboService.removeBindingAccessTokenByWeiboId(tuan360Model.getQid(), ProfileType.TUAN360CONFIG);
					    Tuan360Model tuan360 = (Tuan360Model)weiboService.getBindingAccessToken(userid, ProfileType.TUAN360CONFIG);
						
						if (StringUtils.isNotEmpty(tuan360.getQid())) {
							weiboService.updateBindingAccessToken(tuan360Model,userid, ProfileType.TUAN360CONFIG);
						} else {
							weiboService.addBindingAccess(userid, tuan360Model,ProfileType.TUAN360CONFIG);
						}
					}
				} else {
					Tuan360Model tuan360 = (Tuan360Model) weiboService
							.getBindingAccessToken(userid,ProfileType.TUAN360CONFIG);
					if (StringUtils.isNotEmpty(tuan360.getQid())) {
						weiboService.updateBindingAccessToken(tuan360Model,
								userid, ProfileType.TUAN360CONFIG);
					} else {
						weiboService.addBindingAccess(userid, tuan360Model,
								ProfileType.TUAN360CONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + u.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", u);
				SingletonLoginUtils.addSingleton(u, userService, u.getId()+ "", response, false, request);
				//团360登陆360用户ID cookie
				Cookie cookieUserid = WebUtils.cookie("TUAN360USERID", tuan360Model.getQid(), -1);
				response.addCookie(cookieUserid);
			} else {
				request.setAttribute("ERRMSG", "系统繁忙,请稍候再试!");
				return new ModelAndView("redirect:../500.html");
			}
		}
		// 假如是交易过来的授权 认证绑定账户 需要跳转到交易之前的路径
		String trxauthz = request.getParameter("trxauthz");
		if (trxauthz != null && !"".equals(trxauthz)) {
			String weibo_referurl = (String) request.getSession().getAttribute("weibo_referurl");
			if (weibo_referurl != null) {
				return "redirect:" + weibo_referurl;
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String refer_url ="";
		refer_url=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
		if(refer_url==null||"".equals(refer_url)){
			refer_url=WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
		}
		if(StringUtils.isNotEmpty(refer_url)){
			return new ModelAndView("redirect:" + refer_url);
		}
		// 默认进入 用户账户中心
		return "/user/useraccount";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/user/get360UserInfo.do")
	public Object getRequstAccessToken(ModelMap modelMap,
					RequestToken requestToken,HttpServletRequest request,HttpServletResponse response){
		
		String verifier = request.getParameter("oauth_verifier");

		String uuidstr = WebUtils.getCookieValue("TUAN360UUID", request);
		if (uuidstr == null || verifier == null) {
			request.setAttribute("ERRMSG", "授权错误,请重新授权!");
			return new ModelAndView("user/login");
		}
		try{
			RequestToken resToken = (RequestToken) memCacheService.get("TUAN360_REQUESTTOKEN" + uuidstr);
			if (resToken == null)	{
				request.setAttribute("ERRMSG", "授权错误,请重新授权!");
				return new ModelAndView("user/login");
			}
			request.setAttribute("WEIBO_SITE_TYPE",property.getProperty("TUAN360_APP_NAME"));
			Hao360 hao360 = new Hao360();
			AccessToken tuan360accessToken = hao360.getOAuthAccessToken(resToken, verifier);
			if (tuan360accessToken == null) {
				request.setAttribute("ERRMSG", "授权错误,请重新授权!");
				return new ModelAndView("user/login");
			}
			String qid = tuan360accessToken.getParameter("qid");
			String name = java.net.URLDecoder.decode(tuan360accessToken.getParameter("qname"));
			String mail = java.net.URLDecoder.decode(tuan360accessToken.getParameter("qmail"));
			Tuan360Model tuan360Model = new Tuan360Model();
			tuan360Model.setQid(qid);
			tuan360Model.setQname(name);
			tuan360Model.setQmail(mail);
			tuan360Model.setToken(resToken.getToken());
			tuan360Model.setTokenSecret(resToken.getTokenSecret());
			
			memCacheService.set("TUAN360_USER_REQUESTTOKEN" + uuidstr, tuan360Model);
			if(StringUtils.isNotEmpty(name)){
				log.info("连接360用户名称为:" + name);
				request.setAttribute("WEIBO_NAMES", name);
			}
			
			request.setAttribute("WEIBO_USER_SCREENNAME", name);
			weiboService = (WeiboService) BeanUtils.getBean(request, "TUAN360CONFIGService");
			//获取当前登录用户信息
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				Long userid = user.getId();//千品网用户Id
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(qid , ProfileType.TUAN360CONFIG);
				// 此用户是否和此360帐号绑定  没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						
						weiboService.removeBindingAccessTokenByWeiboId(qid, ProfileType.TUAN360CONFIG);
					    Tuan360Model tuan360 = (Tuan360Model)weiboService.getBindingAccessToken(userid, ProfileType.TUAN360CONFIG);
						
						if (StringUtils.isNotEmpty(tuan360.getQid())) {
							weiboService.updateBindingAccessToken(tuan360Model,userid, ProfileType.TUAN360CONFIG);
						} else {
							weiboService.addBindingAccess(userid, tuan360Model,ProfileType.TUAN360CONFIG);
						}
					}
				}else {
					Tuan360Model bAccessToken = (Tuan360Model) weiboService
											.getBindingAccessToken(userid,ProfileType.TUAN360CONFIG);
					if (StringUtils.isNotEmpty(bAccessToken.getQid())) {
						weiboService.updateBindingAccessToken(bAccessToken, userid,ProfileType.TUAN360CONFIG);
					} else {
						weiboService.addBindingAccess(userid, bAccessToken,ProfileType.TUAN360CONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", user);
				
				//通过360账户Id获得千品userId
				Long userId = weiboService.getBindingAccessTokenByWeiboId(qid, ProfileType.TUAN360CONFIG);
				if (userId != 0) {
					User accessUser = userService.findById(userId);
					if (accessUser == null) {
						return new ModelAndView("user/login");
					}
					
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
					
					// 将用户设为登录状态
					SingletonLoginUtils.addSingleton(accessUser, userService,accessUser.getId() + "", response, false, request);
					
					//团360登陆360用户ID cookie
					Cookie cookieUserid = WebUtils.cookie("TUAN360USERID", qid, -1);
					response.addCookie(cookieUserid);
				}
				String refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
				if (refer_url == null) {
					refer_url = "/forward.do?param=index.index";
					request.setAttribute("refer_url", refer_url);
					return new ModelAndView("user/login_forward");
				}
//				if ("http://www.qianpin.com/user/goActivePage.do".equals(refer_url)) {
//					Cookie tuan360Cookie = WebUtils.cookie("WEIBO_FROMWEB", "TUAN360CONFIG", 60 * 60);
//					response.addCookie(tuan360Cookie);
//				}
				request.setAttribute("refer_url", refer_url);
				log.info("tuan360 authz login success....redirect url:" + refer_url);
				return new ModelAndView("redirect:" + refer_url);
			}else{
				request.setAttribute("WEIBO_URL","/user/bindTuan360Account.do");
				// 判断该360账户是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						qid + "", ProfileType.TUAN360CONFIG);
				if (bindUserid != 0) {
					User accessUser = userService.findById(bindUserid);
					if (accessUser == null) {
						return "user/login";
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
					
					//团360登陆360用户ID cookie
					Cookie cookieUserid = WebUtils.cookie("TUAN360USERID", qid, -1);
					response.addCookie(cookieUserid);
					
					String refer_url = WebUtils.getCookieValue(
							"REQUESTURI_REFER_COOKIE", request);
					if (refer_url == null) {
						refer_url = "/forward.do?param=index.index";
						request.setAttribute("refer_url", refer_url);
						return "user/login_forward";
					}
					request.setAttribute("refer_url", refer_url);
					log.info("tuan360 authz login success....redirect url:"+ refer_url);
					return new ModelAndView("redirect:" + refer_url);
				}
				return "user/loginunion";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/user/useraccount";		
	}
				
	@RequestMapping("/user/360Authorization.do")
	public String redirectOAuthzPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		RequestToken resToken=null;
		try {
			System.setProperty("hao3604j.oauth.consumerKey", Hao360.CONSUMER_KEY);
			System.setProperty("hao3604j.oauth.consumerSecret", Hao360.CONSUMER_SECRET);
			Hao360 hao360 = new Hao360();
			resToken = hao360.getOAuthRequestToken(Hao360.CALLBACK_URL);
			if(resToken != null){
				String uuidstr = UUID.randomUUID().toString();
				int validy = 60 * 60 * 1;
				Cookie cookie = WebUtils.cookie("TUAN360UUID", uuidstr, validy);
				response.addCookie(cookie);
				
				memCacheService.set("TUAN360_REQUESTTOKEN" + uuidstr, resToken);
				
				String  authorizeURL = resToken.getAuthenticationURL()+"&oauth_callback="+Hao360.CALLBACK_URL;			
				return "redirect:"+authorizeURL;
			}	
		} catch (Exception e) {
			request.setAttribute("ERRMSG", "连接360帐号失败，请重试！！");
			return "/user/login";
		}
		request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
		return "/user/regist";
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
