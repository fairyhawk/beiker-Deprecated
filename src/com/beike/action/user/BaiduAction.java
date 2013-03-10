/**
 * 
 */
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
import com.beike.dao.WeiboDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.BaiduAccessToken;
import com.beike.service.common.EmailService;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserService;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.util.BaiduOauthApiUtil;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.Digest;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;
/**
 * <p>
 * Title: 百度相关Action
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
 * @date 2011-10-14
 * @author qiaowb
 * @version 1.0
 */
@Controller
public class BaiduAction {
	private final Log log = LogFactory.getLog(BaiduAction.class);
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "emailService")
	private EmailService emailService;
	
	private WeiboService weiboService;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private WeiboDao weiboDao;

	public WeiboService getWeiboService() {
		return weiboService;
	}
	public void setWeiboService(WeiboService weiboService) {
		this.weiboService = weiboService;
	}
	private static String BAIDU="BAIDU";
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@RequestMapping("/user/baiduAuthorization.do")
	public Object redirectOAuthzPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			//获取Authorization Code
			String authorization_code = request.getParameter("code");
			Map<String,String> logMap=LogAction.getLogMap(request, response);
			logMap.put("action", "u_snsreq");
			LogAction.printLog(logMap);
			request.setAttribute("WEIBO_SITE_TYPE",propertyUtil.getProperty("BAIDU_APP_NAME"));
			//request.setAttribute("WEIBO_SITE_TYPE","百度");
			if (authorization_code == null) {
				request.setAttribute("ERRMSG", "授权错误,请重新授权!");
				return new ModelAndView("user/login");
			}
			//使用Authorization Code换取Access Token
			BaiduAccessToken accessToken = BaiduOauthApiUtil.getAccessToken(authorization_code);
			log.info("baidu accessToken="+accessToken);
			if(accessToken == null){
				request.setAttribute("ERRMSG", "授权错误,请重新授权!");
				return new ModelAndView("user/login");
			}
			Map<String, String> userMap = BaiduOauthApiUtil.getLoginUser(accessToken.getAccess_token());
			log.info("baidu userMap="+userMap);
			if(userMap == null || userMap.isEmpty()){
				request.setAttribute("ERRMSG", "授权错误,请重新授权!");
				return new ModelAndView("user/login");
			}
			Map<String,String> logMap2=LogAction.getLogMap(request, response);
			logMap2.put("action","u_sns");
			logMap2.put("sns", BAIDU);
			LogAction.printLog(logMap2);
			
			
			accessToken.setBaidu_id(userMap.get("uid"));
			accessToken.setScreenName(userMap.get("uname"));
			accessToken.setHeadIcon(userMap.get("portrait"));
			
			request.setAttribute("WEIBO_USER_SCREENNAME", accessToken.getScreenName());
			
			//Access Token写入cookie
			String uuidstr = UUID.randomUUID().toString();
			int validy = 60 * 60 * 1;
			Cookie cookie = WebUtils.cookie("BAIDUUUID", uuidstr, validy);
			response.addCookie(cookie);
			memCacheService.set("BAIDU_ACCESSTOKEN_" + uuidstr, accessToken);
			
			Cookie cookieUserid = WebUtils.cookie("BAIDUUSERID", accessToken.getBaidu_id(), -1);
			response.addCookie(cookieUserid);
			
			weiboService = (WeiboService) BeanUtils.getBean(request, "BAIDUCONFIGService");
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id() + "", ProfileType.BAIDUCONFIG);
				Long userid = user.getId();
				// 此用户是否和此百度绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(accessToken.getBaidu_id() + "",ProfileType.BAIDUCONFIG);
						BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService.getBindingAccessToken(userid,ProfileType.BAIDUCONFIG);
						if (!"".equals(bAccessToken.getBaidu_id())) {
							weiboService.updateBindingAccessToken(accessToken,userid, ProfileType.BAIDUCONFIG);
						} else {
							weiboService.addBindingAccess(userid, accessToken,ProfileType.BAIDUCONFIG);
						}
					}
				} else {
					BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService.getBindingAccessToken(userid,ProfileType.BAIDUCONFIG);
					if (!"".equals(bAccessToken.getBaidu_id())) {
						weiboService.updateBindingAccessToken(accessToken, userid,ProfileType.BAIDUCONFIG);
					} else {
						weiboService.addBindingAccess(userid, accessToken,ProfileType.BAIDUCONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(user.getId());
			
				request.setAttribute("WEIBO_NAMES", weiboNames);
				request.setAttribute("QIANPIN_USER", user);
			
				request.setAttribute("WEIBO_NAME", accessToken.getScreenName());
			} else {
				request.setAttribute("WEIBO_URL", "/user/baiduBindAccount.do");
			
				// 判断该百度是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id()+ "", ProfileType.BAIDUCONFIG);
				if (bindUserid != 0) {
					User accessUser = userService.findById(bindUserid);
					if (accessUser == null) {
						return "user/login";
					}
					Map<String,String> logMap3=LogAction.getLogMap(request, response);
					logMap3.put("action", "u_snslogin");
					logMap3.put("sns", BAIDU);
					LogAction.printLog(logMap3);
					
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
					
					SingletonLoginUtils.addSingleton(accessUser, userService,accessUser.getId() + "", response, false, request);
					String refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
					if (refer_url == null) {
						refer_url = "/forward.do?param=index.index";
						request.setAttribute("refer_url", refer_url);
						return "user/login_forward";
					}
					log.info("baidu authz login success....redirect url:"
							+ refer_url);
					request.setAttribute("refer_url", refer_url);
					return new ModelAndView("redirect:" + refer_url);
				}
				return "user/loginunion";
			}
			return "user/useraccount";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping("/user/baiduBindAccount.do")
	public Object bindWeiboAccount(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		try {
			// 假如没有被绑定过，从缓存读取token
			BaiduAccessToken accessToken = null;
			request.setAttribute("WEIBO_SITE_TYPE",propertyUtil.getProperty("BAIDU_APP_NAME"));
			//request.setAttribute("WEIBO_SITE_TYPE","百度");
			String xuuid = WebUtils.getCookieValue("BAIDUUUID", request);
			accessToken = (BaiduAccessToken) memCacheService.get("BAIDU_ACCESSTOKEN_" + xuuid);
			
			if (accessToken == null) {
				return "user/login";
			}

			String weiboName = accessToken.getScreenName();
			String bindType = request.getParameter("BINDTYPE");

			String username = request.getParameter(Constant.USER_LOGIN_USERNAME);
			String password = request.getParameter("USER_PASSWORD");
			request.setAttribute("WEIBO_USER_SCREENNAME",accessToken.getScreenName());
			request.setAttribute("WEIBO_URL", "/user/baiduBindAccount.do");
			if (bindType == null) {
				request.setAttribute("BINDTYPE_IS_NULL", "true");
				return "user/loginunion";
			}
			// 判断是创建账户 还是帮定已经有的账户
			boolean isEmail = MobilePurseSecurityUtils.checkEmail(username, 0);
			String mobile = null;
			String email = null;
			if (isEmail) {
				email = username;
			} else {
				mobile = username;
			}
			
			User user = SingletonLoginUtils.getMemcacheUser(request);
			weiboService = (WeiboService) BeanUtils.getBean(request,"BAIDUCONFIGService");
			if (user != null) {
				Long userid = user.getId();
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id()+ "", ProfileType.BAIDUCONFIG);
				// 此用户是否和此百度绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(accessToken.getBaidu_id() + "", ProfileType.BAIDUCONFIG);
						BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService.getBindingAccessToken(userid,ProfileType.BAIDUCONFIG);
						if (!"".equals(bAccessToken.getBaidu_id())) {
							weiboService.updateBindingAccessToken(accessToken,userid, ProfileType.BAIDUCONFIG);
						} else {
							weiboService.addBindingAccess(userid, accessToken,ProfileType.BAIDUCONFIG);
						}
					}
				} else {
					BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService.getBindingAccessToken(userid,ProfileType.BAIDUCONFIG);
					if (!"".equals(bAccessToken.getBaidu_id())) {
						weiboService.updateBindingAccessToken(accessToken, userid,ProfileType.BAIDUCONFIG);
					} else {
						weiboService.addBindingAccess(userid, accessToken,ProfileType.BAIDUCONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(bindUserid);
				// request.getSession().setAttribute("WEIBO_NAMES", weiboNames);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", user);
				SingletonLoginUtils.addSingleton(user, userService, userid + "",
						response, false, request);
			} else {
				if ("CREATEACCOUNT".equals(bindType)) {
					// 注册，用户已经存在
					User newUser = null;
					if (!isEmail) {
						request.setAttribute("ERRMSG", "USERNAME_PARAM_ERROR");
						return "user/useraccount";
					}
					User xnewUser = null;
					try {
						xnewUser = userService.findUserByEmail(email);
					} catch (UserException e1) {
						e1.printStackTrace();
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								accessToken.getScreenName());
						return "user/loginunion";
					}

					if (xnewUser != null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								accessToken.getScreenName());
						return "user/loginunion";
					}
					String ip=WebUtils.getIpAddr(request);
					try {
						newUser = userService.addUserEmailRegist(email, password,ip);
					} catch (UserException e) {
						e.printStackTrace();
					} catch (AccountException e) {
						e.printStackTrace();
					}
					if (newUser == null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",
								accessToken.getScreenName());
						return "user/loginunion";
					}
					
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snsreg");
					logMap2.put("sns", BAIDU);
					logMap2.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap2);
					
					
					//打印日志
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "UserRegInSite");
					logMap.put("uid", newUser.getId()+"");
					LogAction.printLog(logMap);
					
					

					// 新用户绑定
					weiboService.addBindingAccess(newUser.getId(), accessToken,
							ProfileType.BAIDUCONFIG);
					// request.getSession().setAttribute(Constant.USER_LOGIN,newUser);
					SingletonLoginUtils.addSingleton(newUser, userService,
							newUser.getId() + "", response, false, request);

					// 加入扩展信息,激活邮件信息
					try {
						String emailValidateUrl = propertyUtil.getProperty(Constant.EMAIL_VALIDATE_URL);
						StringBuilder sb = new StringBuilder();
						sb.append(emailValidateUrl);
						sb.append("?id=" + newUser.getId() + "&userkey=");
						String secret = MobilePurseSecurityUtils.hmacSign(
								newUser.getCustomerkey(), newUser.getId() + "");
						sb.append(secret);
						String subject = "请激活您的账号,完成注册"; // 确认?
						UserProfile userProfile = userService.getProfile(
								newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
						if (userProfile == null) {
							userService.addProfile(Constant.EMAIL_REGIST_URLKEY,secret, newUser.getId(),ProfileType.USERCONFIG);
						} else {
							userProfile.setValue(secret);
							userService.updateProfile(userProfile);
						}
						String time = DateUtils.dateToStr(new Date(),"yyyy年MM月dd日 HH:mm");
						String date = DateUtils.dateToStr(new Date());
						// 设置动态参数
						Object[] emailParams = new Object[] { time, sb.toString(),date };

						// 邮件模板参数未设置
						emailService.send(null, null, null, null, null, subject,
								new String[] { email }, null, null, new Date(),
								emailParams, Constant.EMAIL_VALIDATE_TEMPLATE);
					} catch (Exception e) {
						log.info("send email success....");
						e.printStackTrace();
					}

					// 先判断之前这个百度账户 是否绑定过其他账户 假如绑定了先清除 最后给这个账户绑定上 update by ye.tian
					// at 2011.6.19
					Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id() + "",ProfileType.BAIDUCONFIG);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(newUser.getId() + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(accessToken.getBaidu_id() + "",ProfileType.BAIDUCONFIG);
							weiboService.addBindingAccess(newUser.getId(),accessToken, ProfileType.BAIDUCONFIG);
						}
					} else {
						weiboService.addBindingAccess(newUser.getId(),accessToken, ProfileType.BAIDUCONFIG);
					}

					Map<String, String> weiboNames = weiboService.getWeiboNames(newUser.getId());
					// request.getSession().setAttribute("WEIBO_NAMES", weiboNames);
					request.setAttribute("WEIBO_NAMES", weiboNames);
					memCacheService.set("WEIBO_NAMES_" + newUser.getId(),weiboNames);
					request.setAttribute("QIANPIN_USER", newUser);
					SingletonLoginUtils.addSingleton(newUser, userService,newUser.getId() + "", response, false, request);
					// 注册成功进入成功页面
					String requesturl ="";
					requesturl=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
					if(requesturl==null||"".equals(requesturl)){
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
						log.info(e);
						e.printStackTrace();
						// return new ModelAndView("redirect:../500.html");
					}
					if (u == null) {
						log.info("用户认证成功,登录失败!");
						request.setAttribute("USER_LOGIN_ERROR", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME", weiboName);
						return "user/loginunion";
					}
					Long userid = u.getId();
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "u_snsbind");
					logMap.put("sns", BAIDU);
					LogAction.printLog(logMap);
					
					
					Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
							accessToken.getBaidu_id() + "",
							ProfileType.BAIDUCONFIG);
					// 绑定过
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (bindUserid.longValue() != userid.longValue()) {
							weiboService.removeBindingAccessTokenByWeiboId(
									accessToken.getBaidu_id() + "",
									ProfileType.BAIDUCONFIG);
							BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService
									.getBindingAccessToken(userid,
											ProfileType.BAIDUCONFIG);
							if (!"".equals(bAccessToken.getBaidu_id())) {
								weiboService.updateBindingAccessToken(accessToken,
										userid, ProfileType.BAIDUCONFIG);
							} else {
								weiboService.addBindingAccess(userid, accessToken,
										ProfileType.BAIDUCONFIG);
							}
						}
					} else {
						BaiduAccessToken bAccessToken = (BaiduAccessToken) weiboService
								.getBindingAccessToken(userid,
										ProfileType.BAIDUCONFIG);
						if (bAccessToken.getBaidu_id()!=null&&!"".equals(bAccessToken.getBaidu_id())) {
							weiboService.updateBindingAccessToken(accessToken,
									userid, ProfileType.BAIDUCONFIG);
						} else {
							weiboService.addBindingAccess(userid, accessToken,
									ProfileType.BAIDUCONFIG);
						}
					}
					Map<String, String> weiboNames = weiboService
							.getWeiboNames(userid);
					request.setAttribute("WEIBO_NAMES", weiboNames);
					memCacheService.set("WEIBO_NAMES_" + u.getId(), weiboNames);
					request.setAttribute("QIANPIN_USER", u);
					SingletonLoginUtils.addSingleton(u, userService,
							u.getId() + "", response, false, request);
				} else {
					request.setAttribute("ERRMSG", "系统繁忙,请稍候再试!");
					return new ModelAndView("redirect:../500.html");
				}
			}
			// 假如是交易过来的授权 认证绑定账户 需要跳转到交易之前的路径
			String trxauthz = request.getParameter("trxauthz");
			if (trxauthz != null && !"".equals(trxauthz)) {
				String weibo_referurl = (String) request.getSession().getAttribute(
						"weibo_referurl");
				if (weibo_referurl != null) {
					return "redirect:" + weibo_referurl;
				}
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
			return "user/useraccount";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	//百度一号直达
	@RequestMapping("/user/baiduAutoLogin.do")
	public Object baiduAutoLogin(HttpServletRequest request,
							HttpServletResponse response,ModelMap model){
		try{
			String uid = request.getParameter("uid");
			String uname = request.getParameter("uname");
			String timestamp = request.getParameter("timestamp");
			String sign = request.getParameter("sign");
			
			BaiduAccessToken accessToken = new BaiduAccessToken();
			if(StringUtils.isNotEmpty(uid)){
				accessToken.setBaidu_id(uid);
			}
			if(StringUtils.isNotEmpty(uname)){
				accessToken.setScreenName(uname);
			}
			String sourceValue = "timestamp="+timestamp+"uid="+uid+"uname="+uname+propertyUtil.getProperty("BAIDU_SECRET_KEY");
			String md5Value = Digest.signMD5(sourceValue, "UTF-8");
			//MD5签名不匹配，数据无效
			if(!md5Value.equals(sign)){
				getJsonResult(1,"md5签名验证失败！！",response);
				return null;
			}
			//当前登录用户
			User currentUser = SingletonLoginUtils.getMemcacheUser(request);
			weiboService = (WeiboService) BeanUtils.getBean(request,"BAIDUCONFIGService");
			if(currentUser != null){
				getJsonResult(0,"用户已登录！！",response);
			}else{
				// 判断该百度是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id()+ "", ProfileType.BAIDUCONFIG);
				//百度账号登陆百度用户ID cookie
				Cookie cookieUserid = WebUtils.cookie("BAIDUUSERID", accessToken.getBaidu_id(), -1);
				response.addCookie(cookieUserid);
				if (bindUserid != 0) {
					User user = userService.findById(bindUserid);
					Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user.getId());
					if (weiboNames != null) {
						memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
					}
					SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",response, false, request);		
					getJsonResult(0,"用户帐号已绑定，登录成功！！",response);
				}else{
					//后台注册百度用户，密码为8位随机小写字母、数字组合
					StringBuilder baiduId = new StringBuilder("baidu_");
					
					//百度用户名不存在，使用baidu_百度用户名创建账号；否则使用baidu_百度用户ID创建账号
					if(StringUtils.isNotEmpty(uname) && !userService.isUserExist(null, "baidu_" + uname)){
						baiduId = baiduId.append(uname);
					}else{
						baiduId = baiduId.append(uid);
					}
					try{
						String ip=WebUtils.getIpAddr(request);
						User newUser = userService.addUserEmailRegist(baiduId.toString(), com.beike.util.StringUtils.getRandomString(8).toLowerCase(),ip);									
						if(newUser != null){
							Map<String,String> logMap2=LogAction.getLogMap(request, response);
							logMap2.put("action", "u_snsreg");
							logMap2.put("sns", BAIDU);
							logMap2.put("uid", newUser.getId()+"");
							LogAction.printLog(logMap2);
										
							//打印日志
							Map<String, String> logMap = LogAction.getLogMap(request,
									response);
							logMap.put("action", "UserRegInSite");
							logMap.put("uid", newUser.getId()+"");
							LogAction.printLog(logMap);
							
							weiboService.addBindingAccess(newUser.getId(),accessToken, ProfileType.BAIDUCONFIG);
							
							Map<String, String> weiboNames = weiboDao.getWeiboScreenName(newUser.getId());
							if (weiboNames != null) {
								memCacheService.set("WEIBO_NAMES_" + newUser.getId(), weiboNames);
							}
							SingletonLoginUtils.addSingleton(newUser, userService,newUser.getId() + "", response, false, request);
							getJsonResult(0,"绑定用户成功！！",response);
						}else{
							getJsonResult(1,"创建千品帐号失败，请重试！！",response);
						}
					}catch(Exception ex){
						ex.printStackTrace();
						// 判断该百度是否有绑定账户 假如有绑定自动设置为登录状态
						bindUserid = weiboService.getBindingAccessTokenByWeiboId(accessToken.getBaidu_id()+ "", ProfileType.BAIDUCONFIG);
						//百度账号登陆百度用户ID cookie
						cookieUserid = WebUtils.cookie("BAIDUUSERID", accessToken.getBaidu_id(), -1);
						response.addCookie(cookieUserid);
						if (bindUserid != 0) {
							User user = userService.findById(bindUserid);
							Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user.getId());
							if (weiboNames != null) {
								memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
							}
							SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",response, false, request);		
							getJsonResult(0,"用户帐号已绑定，登录成功！！",response);
						}else{
							getJsonResult(1,"登录失败，请重试！！",response);
						}
					}
				}
			}
		}catch(Exception e){
			getJsonResult(1,"登录失败，请重试！！",response);
		}
		return null;
	}
	
	/** 
	 * @date Apr 19, 2012
	 * @description 
	 * @param flag 0，成功 1，失败
	 * @param message 回馈信息
	 * @param response
	 * @return String （json字符串）
	 * @throws 
	 */
	private void getJsonResult(int flag,String message,HttpServletResponse response){
		Map<String,String> result = new HashMap<String,String>();
		
		response.setContentType("text/json; charset=UTF-8");
		response.setHeader("progma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");	
		
		result.put("flag", flag+"");
		result.put("message", message);
		
		try {
			response.getWriter().print(JsonUtil.mapToJson(result));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
