package com.beike.action.user;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.common.enums.user.ProfileType;
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
import com.beike.util.alipay.AlipayModel;
import com.beike.util.alipay.AlipayService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;
 /*
 * com.beike.action.user.AlipayAction.java
 * @description: 支付宝相关Action
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-7，xuxiaoxian ,create class
 *
 */
@Controller
public class AlipayAction {
	
	private static final PropertyUtil property = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	private final MemCacheService memCacheService = MemCacheServiceImpl .getInstance();
	
	private final Log log = LogFactory.getLog(AlipayAction.class);
	
	private static String ALIPAY="ALIPAY";
	
	private WeiboService weiboService;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	/** 
	 * @date 2012-5-8
	 * @description:支付宝帐号绑定，用户授权之后，选择创建账户或者登陆时调用此方法 
	 * 请求:1.邮箱:email 2.绑定手机号:mobile 3.密码:password 
	 *  4.验证后跳转页面:referurl 返回 request 5.账户类型 是注册还是登陆:BINDTYPE
	 *  创建账户：CREATEACCOUNT 登陆：LOGINACCOUNT 返回： 1.错误信息:ERRMSG
	 * @param model
	 * @param request
	 * @param response
	 * @return Object
	 * @throws 
	 */
	@RequestMapping("/user/bindAlipayAccount.do")
	public Object bindAlipayAccount(ModelMap model,
			HttpServletRequest request, HttpServletResponse response){
		try {
			request.setAttribute("WEIBO_SITE_TYPE",property.getProperty("ALIPAY_APP_NAME"));
			String uuidstr = WebUtils.getCookieValue("ALIPAYUUID", request);
			if (StringUtils.isBlank(uuidstr)) {
//				 request.setAttribute("ERRMSG", "用户操作超时，请重新操作!");
//				 return new ModelAndView("redirect:../500.html");
				return "user/login";
			}
			
			AlipayModel alipayModel = (AlipayModel) memCacheService.get("ALIPAY_MODEL" + uuidstr);
			String real_name = alipayModel.getReal_name();
			if(StringUtils.isEmpty(real_name)){
				real_name = alipayModel.getUser_id();
			}
			request.setAttribute("WEIBO_USER_SCREENNAME", real_name);
			request.setAttribute("WEIBO_URL", "/user/bindAlipayAccount.do");
			if (alipayModel == null) {
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

			weiboService = (WeiboService) BeanUtils.getBean(request,"ALIPAYCONFIGService");
			
			//获取当前登录用户信息
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				Long userid = user.getId();//千品网userid
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
												alipayModel.getUser_id() + "",ProfileType.ALIPAYCONFIG);

				// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
										alipayModel.getUser_id() + "",ProfileType.ALIPAYCONFIG);
						AlipayModel alipayMod = (AlipayModel) weiboService
							.getBindingAccessToken(userid,ProfileType.ALIPAYCONFIG);
						if (StringUtils.isNotEmpty(alipayMod.getUser_id())) {
							weiboService.updateBindingAccessToken(alipayModel,
										userid, ProfileType.ALIPAYCONFIG);
						} else {
							weiboService.addBindingAccess(userid, alipayModel,
										ProfileType.ALIPAYCONFIG);
						}
					}
				} else {
					AlipayModel alipayMod = (AlipayModel) weiboService
							.getBindingAccessToken(userid,ProfileType.ALIPAYCONFIG);
					if (StringUtils.isNotEmpty(alipayMod.getUser_id())) {
						weiboService.updateBindingAccessToken(alipayModel, userid,
								ProfileType.ALIPAYCONFIG);
					} else {
						weiboService.addBindingAccess(userid, alipayModel,
								ProfileType.ALIPAYCONFIG);
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
						request.setAttribute("WEIBO_USER_SCREENNAME",real_name);
						return "user/loginunion";
					}
					if (xnewUser != null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",real_name);
						return "user/loginunion";
					}
					String ip=WebUtils.getIpAddr(request);
					newUser = userService.addUserEmailRegist(email,password,ip);

					if (newUser == null) {
						request.setAttribute("USER_EXIST", "true");
						request.setAttribute("WEIBO_USER_SCREENNAME",real_name);
						return "user/loginunion";
					}
					//打印日志
			
					Map<String,String> logMap2=LogAction.getLogMap(request, response);
					logMap2.put("action", "u_snsreg");
					logMap2.put("sns", ALIPAY);
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

				// 先判断之前这个支付宝账户 是否绑定过其他账户 假如绑定了先清除 最后给这个账户绑定上 
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
								alipayModel.getUser_id() + "",ProfileType.ALIPAYCONFIG);
				// 绑定过
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(newUser.getId() + ""))) {
						weiboService.removeBindingAccessTokenByWeiboId(
								alipayModel.getUser_id()+ "",ProfileType.ALIPAYCONFIG);
						weiboService.addBindingAccess(newUser.getId(),
								alipayModel, ProfileType.ALIPAYCONFIG);
					}
				} else {
					weiboService.addBindingAccess(newUser.getId(),
							alipayModel, ProfileType.ALIPAYCONFIG);
				}
	
				//将用户设为登录状态
				SingletonLoginUtils.addSingleton(newUser, userService,newUser.getId() + "", response, false, request);
	
				Map<String, String> weiboNames = weiboService.getWeiboNames(newUser.getId());
				memCacheService.set("WEIBO_NAMES_" + newUser.getId(),weiboNames);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				request.setAttribute("QIANPIN_USER", newUser);
				
				// 注册成功进入成功页面
				String requesturl = "";
				requesturl=WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
				if(StringUtils.isEmpty(requesturl)){
					requesturl=(String) request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
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
					log.info("用户支付宝帐号认证成功,登录失败!");
					e.printStackTrace();
					request.setAttribute("USER_LOGIN_ERROR", "true");
					return "/user/loginunion";
				}
				if (u == null) {
					log.info("用户支付宝帐号认证成功,登录失败!");
					request.setAttribute("USER_LOGIN_ERROR", "true");
					request.setAttribute("WEIBO_USER_SCREENNAME", real_name);
					return "/user/loginunion";
				}
				Map<String,String> logMap=LogAction.getLogMap(request, response);
				logMap.put("action", "u_snsbind");
				logMap.put("sns", ALIPAY);
				LogAction.printLog(logMap);
				SingletonLoginUtils.addSingleton(u, userService, u.getId()+ "", response, false, request);
				Long userid = u.getId();
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
										alipayModel.getUser_id() + "",ProfileType.ALIPAYCONFIG);
				// 绑定过
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						
						weiboService.removeBindingAccessTokenByWeiboId(alipayModel.getUser_id(), ProfileType.ALIPAYCONFIG);
					    AlipayModel alipayMod = (AlipayModel)weiboService.getBindingAccessToken(userid, ProfileType.ALIPAYCONFIG);
						
						if (StringUtils.isNotEmpty(alipayMod.getUser_id())) {
							weiboService.updateBindingAccessToken(alipayModel,userid, ProfileType.ALIPAYCONFIG);
						} else {
							weiboService.addBindingAccess(userid, alipayModel,ProfileType.ALIPAYCONFIG);
						}
					}
				} else {
					AlipayModel alipayMod = (AlipayModel) weiboService
							.getBindingAccessToken(userid,ProfileType.ALIPAYCONFIG);
					if (StringUtils.isNotEmpty(alipayMod.getUser_id())) {
						weiboService.updateBindingAccessToken(alipayModel,
								userid, ProfileType.ALIPAYCONFIG);
					} else {
						weiboService.addBindingAccess(userid, alipayModel,
								ProfileType.ALIPAYCONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + u.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", u);
				SingletonLoginUtils.addSingleton(u, userService, u.getId()+ "", response, false, request);
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/user/getAlipayUserInfo.do")
	public Object getAlipayUserInfo(ModelMap model,
			HttpServletRequest request, HttpServletResponse response){
		
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
	
		//计算得出通知验证结果
		boolean verify_result = PaymentInfoGeneratorAlipay.verify(params);
		
		if(verify_result){//验证成功
			request.setAttribute("WEIBO_SITE_TYPE",property.getProperty("ALIPAY_APP_NAME"));
			//etao专用 如果指定url，程序自动跳转到target_url参数指定的url
			String target_url = request.getParameter("target_url");
			String refer_url = null;
			if ( StringUtils.isNotEmpty(target_url)){
				refer_url = target_url;
				Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",target_url, -1);
				response.addCookie(requestUrlCookie);
				request.getSession().setAttribute("REQUESTURI_REFER_COOKIE", target_url);
			}
						
			AlipayModel alipayModel = new AlipayModel();
			alipayModel.setUser_id(params.get("user_id"));
			alipayModel.setNotify_id(params.get("notify_id"));
			alipayModel.setSign(params.get("sign"));
			alipayModel.setToken(params.get("token"));
			alipayModel.setReal_name(params.get("real_name"));
			alipayModel.setEmail(params.get("email"));
			
			String uuidstr = UUID.randomUUID().toString();
			int validy = 60 * 60 * 1;
			Cookie cookie = WebUtils.cookie("ALIPAYUUID", uuidstr, validy);
			response.addCookie(cookie);
			memCacheService.set("ALIPAY_MODEL" + uuidstr, alipayModel);
			
			String real_name = alipayModel.getReal_name();
			if(StringUtils.isEmpty(real_name)){
				real_name = alipayModel.getUser_id();
			}
			log.info("连接支付宝用户名称为:" + real_name);
			request.setAttribute("WEIBO_NAMES", real_name);
			request.setAttribute("WEIBO_USER_SCREENNAME", real_name);
			
			weiboService = (WeiboService) BeanUtils.getBean(request, "ALIPAYCONFIGService");
			String alipay_user_id = alipayModel.getUser_id();//支付宝用户id
			
			//获取当前登录用户信息
			User user = SingletonLoginUtils.getMemcacheUser(request);
			
			if (user != null) {
				Long userid = user.getId();//千品网用户Id
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(alipay_user_id , ProfileType.ALIPAYCONFIG);
				// 此用户是否和此支付宝帐号绑定  没绑定更新
				if (bindUserid != 0) {
					// 用户绑定过 但是不是用的这个账号
					if (!((bindUserid + "").equals(userid + ""))) {
						
						weiboService.removeBindingAccessTokenByWeiboId(alipay_user_id, ProfileType.ALIPAYCONFIG);
					    AlipayModel alipaymod = (AlipayModel)weiboService.getBindingAccessToken(userid, ProfileType.ALIPAYCONFIG);
						
						if (StringUtils.isNotEmpty(alipaymod.getUser_id())) {
							weiboService.updateBindingAccessToken(alipayModel,userid, ProfileType.ALIPAYCONFIG);
						} else {
							weiboService.addBindingAccess(userid, alipayModel,ProfileType.ALIPAYCONFIG);
						}
					}
				}else {
					AlipayModel alipayToken = (AlipayModel) weiboService
											.getBindingAccessToken(userid,ProfileType.ALIPAYCONFIG);
					if (StringUtils.isNotEmpty(alipayToken.getUser_id())) {
						weiboService.updateBindingAccessToken(alipayToken, userid,ProfileType.ALIPAYCONFIG);
					} else {
						weiboService.addBindingAccess(userid, alipayToken,ProfileType.ALIPAYCONFIG);
					}
				}
				Map<String, String> weiboNames = weiboService.getWeiboNames(userid);
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
				request.setAttribute("QIANPIN_USER", user);
				
				//通过支付宝账户Id获得千品userId
				Long userId = weiboService.getBindingAccessTokenByWeiboId(alipay_user_id, ProfileType.ALIPAYCONFIG);
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
				}
				//先判断etao是否指定url，若没有从cookie中取refer_url，取不到再从session中取
				if(StringUtils.isEmpty(refer_url)){
					refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
					if (StringUtils.isEmpty(refer_url)) {
						refer_url = (String)request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
						if(StringUtils.isEmpty(refer_url)){
							refer_url = "/forward.do?param=index.index";
							request.setAttribute("refer_url", refer_url);
							return new ModelAndView("user/login_forward");
						}		
					}
				}
				request.setAttribute("refer_url", refer_url);
				log.info("alipay authz login success....redirect url:" + refer_url);
				return new ModelAndView("redirect:" + refer_url);
			}else{
				request.setAttribute("WEIBO_URL","/user/bindAlipayAccount.do");
				// 判断该支付宝账户是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(
						alipay_user_id + "", ProfileType.ALIPAYCONFIG);
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
					//先判断etao是否指定url，若没有从cookie中取refer_url，取不到再从session中取
					if(StringUtils.isEmpty(refer_url)){
						refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE", request);
						if (StringUtils.isEmpty(refer_url)) {
							refer_url = (String)request.getSession().getAttribute("REQUESTURI_REFER_COOKIE");
							if(StringUtils.isEmpty(refer_url)){
								refer_url = "/forward.do?param=index.index";
								request.setAttribute("refer_url", refer_url);
								return new ModelAndView("user/login_forward");
							}		
						}
					}
					request.setAttribute("refer_url", refer_url);
					log.info("alipay authz login success....redirect url:"+ refer_url);
					return new ModelAndView("redirect:" + refer_url);
				}
				return "user/loginunion";
			}
		}else{
			request.setAttribute("ERRMSG", "支付宝登录验证失败,请重试!");
			return new ModelAndView("user/login");
		}
	}
	
	@RequestMapping("/user/alipayAuthorization.do")
	public String getAlipayAuthorization(ModelMap model,
			HttpServletRequest request, HttpServletResponse response){
//			String result = AlipayService.alipay_auth_authorize(map);
			String url=AlipayService.getUrl();
//			response.getWriter().write(result);
			
			try {
				response.sendRedirect(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		
	}
	
}
