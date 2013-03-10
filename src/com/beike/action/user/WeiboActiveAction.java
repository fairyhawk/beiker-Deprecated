package com.beike.action.user;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.UserException;
import com.beike.entity.common.Sms;
import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.service.common.WeiboService;
import com.beike.service.operation.wish.WishActivityService;
import com.beike.service.user.UserService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.RandomNumberUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.sina.RequestToken;
import com.beike.util.sina.SinaAccessToken;
import com.beike.util.singletonlogin.SingletonLoginUtils;
import com.beike.util.tencent.QWeiboSyncApi;
import com.beike.util.tencent.ResModel;
import com.beike.util.tencent.TencentUser;
import com.beike.util.tencent.QWeiboType.ResultType;
import com.beiker.model.operation.wish.InviteRecordBean;
import com.beiker.model.operation.wish.PrizePeopleInfo;
import com.beiker.model.operation.wish.WeiboInfo;

/**
 * <p>
 * Title:微博活动Action
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
public class WeiboActiveAction {
	private final Log log = LogFactory.getLog(WeiboActiveAction.class);
	private String ACTIVE_PAGE = "http://www.qianpin.com/user/goActivePage.do?sourceid=";
	@Autowired
	private UserService userService;

	private WeiboService weiboService;

	private static final String SMS_TYPE = "15";

	private static final int SMS_RANDOM = 6;

	private static final String FROM_SINA = "SINACONFIG";
	private static final String FROM_TENCENT = "TENCENTCONFIG";
	private static final String FROM_EMAIL = "EMAILCONFIG";
	private static PropertyUtil property = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);
	private static String TENCENT_customKey = property
			.getProperty(Constant.TENCENT_APP_KEY);
	private static String TENCENT_customSecrect = property
			.getProperty(Constant.TENCENT_APP_SECRET);
	@Autowired
	private WishActivityService wishActivityService;

	// IPHONE5
	private String content1 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，iphone5我就要拿回家了！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 万达国际电影城钻石卡一张
	private String content2 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，拿着万达电影院钻石卡，全年电影随便看！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 神秘明星演唱会票VIP座位
	private String content3 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，神秘明星演唱会VIP座位两张哦！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 奢侈品箱包
	private String content4 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，拿到了一直梦寐以求的大牌包包！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 桔子水晶酒店两晚居住权
	private String content5 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，享受两晚桔子水晶酒店的奢华住宿，我来了！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 知名化妆品套装一套
	private String content6 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，有了这一套大牌化妆品，带来独一无二的美丽惊喜！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 汽车加油卡一张
	private String content7 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，有了这张卡，近俩月加油不花钱了！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// KTV免费欢唱券
	private String content8 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，拿着KTV欢唱卷，过足了麦霸的瘾！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 饭店代金劵
	private String content9 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网 #我控制不了自己#，拿着3000元饭店代金券，山珍海味随便吃！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 天梭手表
	private String content10 = "我在参加千品网圆十全十美愿望大抽奖活动，快来和我一起参加把！@千品网#我控制不了自己#，戴上天梭手表感受“非凡创意，源于传统”！每天还送手机充值卡、Q币，好多奖品，我控制不了自己！你也来吧，别犹豫了！http://www.qianpin.com/9high";
	// 添加微博内容
	private List<String> contentList = new LinkedList<String>();
	{
		contentList.add(content1);
		contentList.add(content2);
		contentList.add(content3);
		contentList.add(content4);
		contentList.add(content5);
		contentList.add(content6);
		contentList.add(content7);
		contentList.add(content8);
		contentList.add(content9);
		contentList.add(content10);

	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	@Autowired
	private SmsService smsService;

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	@RequestMapping("/user/isActiveMobileExist.do")
	public String isActiveMobileExist(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String result = "";

		String mobile = request.getParameter("mobile");

		if (mobile == null || "".equals(mobile)) {
			result = "error";
		}

		boolean flag = wishActivityService.isActiveMobileExist(mobile);
		boolean isUserExist = false;
		try {
			isUserExist = userService.isUserExist(mobile, null);
		} catch (UserException e1) {
			isUserExist = false;
			e1.printStackTrace();
		}
		if (!flag && !isUserExist) {
			result = "no";
		} else {
			result = "yes";
		}

		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	@RequestMapping("/user/setFalseCount.do")
	public String setFalseCount(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String password = request.getParameter("password");
		String count = request.getParameter("count");
		if (!"gfd654#$%@".equals(password)) {
			request.setAttribute("error", "密码错误!");

			return "active/active_falsecount";
		}

		request.setAttribute("password", password);
		int intcount = 0;
		try {
			intcount = Integer.parseInt(count);
		} catch (Exception e) {
			request.setAttribute("error", "数量请填写数字!");
			return "active/active_falsecount";
		}
		request.setAttribute("count", intcount);
		wishActivityService.saveGenerateNgen(intcount, "WEIBO_ACTIVE_COUNT");
		request.setAttribute("error", "沉默数据保存成功!");
		return "active/active_falsecount";
	}

	/**
	 * 进入微博Action
	 */
	@RequestMapping("/user/goActivePage.do")
	public String goActivePage(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// String secretx=request.getParameter("secret");
			String sourceid = request.getParameter("sourceid");
			String fromweb = WebUtils.getCookieValue("WEIBO_FROMWEB", request);
			// 记录cookie微博
			String requesturl = "http://www.qianpin.com/user/goActivePage.do";
			Cookie requestUrlCookie = WebUtils.cookie(
					"REQUESTURI_REFER_COOKIE", requesturl, -1);
			response.addCookie(requestUrlCookie);
			// 查询活动榜 放到memcache里
			// 查询参与总人数 放到memcache里

			// 判断微博用户是否注册过，假如注册过直接跳到活动中心
			String weiboid = null;
			if (FROM_SINA.equals(fromweb)) {
				String stokenauth = WebUtils.getCookieValue("STOKENAUTH",
						request);
				SinaAccessToken accessToken = (SinaAccessToken) memCacheService
						.get("SINA_USER_ACCESSTOKEN_" + stokenauth);
				if (accessToken != null) {
					weiboService = (WeiboService) BeanUtils.getBean(request,
							"SINACONFIGService");
					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(
									accessToken.getUserId() + "",
									ProfileType.SINACONFIG);
					if (bindUserid != 0) {
						User user = userService.findById(bindUserid);
						if (user != null) {
							SingletonLoginUtils
									.addSingleton(user, userService,
											user.getId() + "", response, false,
											request);
						}
						return "redirect:/user/forwardActiveCenter.do";
					}

				}
			} else if (FROM_TENCENT.equals(fromweb)) {
				String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO",
						request);
				ResModel resModel = (ResModel) memCacheService
						.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
				if (resModel != null) {
					weiboid = resModel.getWeiboid();
					weiboService = (WeiboService) BeanUtils.getBean(request,
							"TENCENTCONFIGService");

					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(weiboid + "",
									ProfileType.TENCENTCONFIG);
					if (bindUserid != 0) {
						User user = userService.findById(bindUserid);
						if (user != null) {
							SingletonLoginUtils
									.addSingleton(user, userService,
											user.getId() + "", response, false,
											request);
						}
						return "redirect:/user/forwardActiveCenter.do";
					}

				}
			}

			// 查询活动榜
			List<InviteRecordBean> listWishUserRank = wishActivityService
					.createWishUserRank();
			if (listWishUserRank != null) {
				memCacheService.set("listWishUserRank", listWishUserRank);
			}

			Long falseCount = wishActivityService.getFalseCount();
			memCacheService.set("falseCount", falseCount);
			StringBuilder redirecturl = new StringBuilder(
					"/jsp/active/active_index.jsp");
			if (sourceid != null) {
				Cookie sourceCookie = WebUtils.cookie("SOURCEID_", sourceid,
						60 * 60 * 24 * 1);
				response.addCookie(sourceCookie);
			}
			// if(secretx!=null){
			// Cookie secretxCookie=WebUtils.cookie("SECRET_", secretx, 60 * 60
			// * 24 *1);
			// response.addCookie(secretxCookie);
			// }
			if (fromweb != null && !"".equals(fromweb)) {
				redirecturl.append("?fromweb=");
				redirecturl.append(fromweb);
				request.setAttribute("fromweb", fromweb);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// redirect 跳转到活动未登录页面
		// return "redirect:.."+redirecturl.toString();

		PrizePeopleInfo ppi = wishActivityService.getPeopleInfo();
		request.setAttribute("ppi", ppi);
		return "active/active_index";
	}

	/**
	 * 活动用户登录
	 */
	@RequestMapping("/user/activeUserLogin.do")
	public String activeUserLogin(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String result = "";
		// 用户名、密码
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = null;
		boolean isMobile = MobilePurseSecurityUtils
				.isJointMobileNumber(username);
		String mobile = null;
		String useremail = null;
		if (isMobile) {
			mobile = username;
		} else {
			useremail = username;
		}

		try {
			user = userService.isUserLogin(mobile, password, useremail);
		} catch (UserException e) {
			e.printStackTrace();
			result = "login_error";
			try {
				response.getWriter().write(result);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		if (user == null) {
			result = "login_error";
			try {
				response.getWriter().write(result);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
				response, false, request);
		// 用户获得手机假如为空
		if (user.getMobile() == null || "".equals(user.getMobile())) {
			result = "user_mobile_null:" + user.getEmail();
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		result = "ok";
		try {
			response.getWriter().write(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * ajax 调用发短信
	 */
	@RequestMapping("/user/activeSendSms.do")
	public String activeSendSms(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String result = "";
		String smstemplate = Constant.SMS_REGIST_TEMPLATE;
		String randomNumber = UUID.randomUUID().toString();
		Cookie randomCookie = WebUtils.cookie("ACTIVE_SMS_RANDOM",
				randomNumber, 60 * 60 * 30);
		response.addCookie(randomCookie);
		// 判断注册验证码
		String checkcode = request.getParameter("checkcode");

		String cookiecode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
				request);
		if (cookiecode == null) {
			result = "regist_code_error";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		String memCode = (String) memCacheService
				.get("validCode_" + cookiecode);

		if (memCode == null || !memCode.equalsIgnoreCase(checkcode)) {
			result = "regist_code_error";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		String mobile = request.getParameter("USER_MOBILE");
		boolean flag = false;
		try {
			flag = userService.isUserExist(mobile, null);
		} catch (UserException e1) {
			e1.printStackTrace();
			flag = false;
		}

		if (flag) {
			result = "no";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		Map<String, String> map = (Map<String, String>) memCacheService
				.get("ACTIVE_REGIST_RANDOMNUMBER_" + randomNumber);

		String mcode = null;
		String vcode = "";
		if (map != null) {
			String mobileValidateCode = map.get(mobile);
			int fcount = 1;
			if (mobileValidateCode != null) {
				String count = mobileValidateCode.split(":")[0];
				vcode = mobileValidateCode.split(":")[1];
				fcount = Integer.parseInt(count);
				if (fcount >= 5) {
					result = "validate_timeout";
					try {
						response.getWriter().write(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
				fcount++;
				mcode = fcount + ":" + vcode;
			}
		}
		// 发送短信
		sendActiveSmsValidate(mobile, request, smstemplate, mcode,
				randomNumber, response);
		try {
			result = "ok";
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Map<String, String> sendActiveSmsValidate(String mobile,
			HttpServletRequest request, String smsTemplate,
			String validateCode, String random, HttpServletResponse response) {
		Map<String, String> smsMap = null;

		int count = 1;
		String vCode = "";
		String[] str = null;
		if (validateCode != null) {
			str = validateCode.split(":");
			if (str != null && str.length == 2) {
				count = Integer.parseInt(str[0]);
				vCode = str[1];
			}
		}
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
			String randomNumbers = "";
			if (str == null || str.length != 2) {
				randomNumbers = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
				// 剩余条数不足了。加日志看。add by wenhua.cheng
				log.info("++++++++++++randomNumbers:" + randomNumbers
						+ "+++++++++++++++++");
			} else {
				randomNumbers = vCode;
			}
			// 短信参数
			Object[] param = new Object[] { randomNumbers };
			content = MessageFormat.format(template, param);
			sourceBean = new SmsInfo(mobile, content, SMS_TYPE,"0");
			smsMap = smsService.sendSms(sourceBean);
			// 设置到session里
			Map<String, String> map = new HashMap<String, String>();
			if (validateCode != null) {
				map.put(mobile, count + ":" + randomNumbers);
			} else {
				map.put(mobile, "1:" + randomNumbers);
			}

			memCacheService.set("ACTIVE_REGIST_RANDOMNUMBER_" + random, map);

			if (smsMap == null) {
				smsMap = new HashMap<String, String>();
			}
			smsMap.put("validateCode", randomNumbers);
		}
		return smsMap;
	}

	/**
	 * 验证注册短信
	 */
	@RequestMapping("/user/validateActiveRegistSms.do")
	public String validateActiveRegistSms(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		// 验证短信
		String randomnumber = WebUtils.getCookieValue("ACTIVE_SMS_RANDOM",
				request);
		// 手机验证码
		String validatenumber = request.getParameter("validatenumber");

		String mobile = request.getParameter("mobile");
		if (randomnumber == null || validatenumber == null || mobile == null) {
			result = "VALIDATE_ERROR";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		Map<String, String> map = (Map<String, String>) memCacheService
				.get("ACTIVE_REGIST_RANDOMNUMBER_" + randomnumber);
		// 超时重新注册
		if (map == null) {
			result = "VALIDATE_ERROR";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		String mobileCode = map.get(mobile);
		String mc = null;
		if (mobileCode != null) {
			mc = mobileCode.split(":")[1];
		} else {
			result = "VALIDATE_ERROR";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		if (!mc.equals(validatenumber)) {
			result = "VALIDATE_ERROR";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		result = "ok";
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 活动用户注册
	 * 
	 * @throws Exception
	 */
	@RequestMapping("/user/activeUserRegist.do")
	public ModelAndView activeUserRegist(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String result = "";
		try {
			String sourceid = WebUtils.getCookieValue("SOURCEID_", request);
			// email
			String email = request.getParameter("email");
			// password
			String password = request.getParameter("password");

			String mobile = request.getParameter("mobile");
			String checkcode = request.getParameter("checkcode");

			String cookiecode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
					request);
			if (cookiecode == null) {
				result = "regist_code_error";
				try {
					response.getWriter().write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			String memCode = (String) memCacheService.get("validCode_"
					+ cookiecode);

			if (memCode == null || !memCode.equalsIgnoreCase(checkcode)) {
				result = "regist_code_error";
				try {
					response.getWriter().write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			String ip=WebUtils.getIpAddr(request);
			User user = null;
			try {
				user = userService.addUserEmailRegist(mobile, email, password,ip);
			} catch (UserException e) {
				log.info(e);
				result = "regist_system_error";
				response.getWriter().write(result);
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				result = "regist_system_error";
				response.getWriter().write(result);
				return null;
			}
			if (user == null) {
				result = "regist_system_error";
				response.getWriter().write(result);
				return null;
			}

			// 假如用户是从推荐链接过来注册的
			String fromweb = WebUtils.getCookieValue("WEIBO_FROMWEB", request);
			String weiboid = null;
			String nickName = null;
			String weiboName = null;
			if (fromweb == null) {
				fromweb = FROM_EMAIL;
			}
			if (FROM_SINA.equals(fromweb)) {
				String stokenauth = WebUtils.getCookieValue("STOKENAUTH",
						request);
				SinaAccessToken accessToken = (SinaAccessToken) memCacheService
						.get("SINA_USER_ACCESSTOKEN_" + stokenauth);
				RequestToken resToken = (RequestToken) memCacheService
						.get("SINA_REQUESTTOKEN_" + stokenauth);
				if (accessToken != null) {
					weiboid = accessToken.getUserId() + "";
					nickName = resToken.getScreenName();

					// 插入微博绑定信息
					weiboService = (WeiboService) BeanUtils.getBean(request,
							"SINACONFIGService");
					Long userid = user.getId();
					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(
									accessToken.getUserId() + "",
									ProfileType.SINACONFIG);

					// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(userid + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									accessToken.getUserId() + "",
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

				}
			} else if (FROM_TENCENT.equals(fromweb)) {
				String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO",
						request);
				ResModel resModel = (ResModel) memCacheService
						.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
				if (resModel != null) {
					weiboid = resModel.getWeiboid();
					nickName = resModel.getWeiboname();
					// 中文名字
					weiboName = resModel.getNickName();
					// 插入微博绑定信息

					weiboService = (WeiboService) BeanUtils.getBean(request,
							"TENCENTCONFIGService");
					Long userid = user.getId();
					Long bindUserid = weiboService
							.getBindingAccessTokenByWeiboId(weiboid + "",
									ProfileType.TENCENTCONFIG);

					// 此用户是否和此微博绑定 绑定了就不用管 没绑定更新
					if (bindUserid != 0) {
						// 用户绑定过 但是不是用的这个账号
						if (!((bindUserid + "").equals(userid + ""))) {
							weiboService.removeBindingAccessTokenByWeiboId(
									weiboid + "", ProfileType.TENCENTCONFIG);
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
				}

			}
			// 给两个人加奖券
			if (FROM_TENCENT.equals(fromweb)) {
				weiboid = nickName;
				nickName = weiboName;
			}
			if (sourceid != null && !"".equals(sourceid)) {
				// 查询用户
				User oldUser = userService.findById(Long.parseLong(sourceid));
				if (oldUser != null) {
					// 邀请记录、两个人的中奖信息
//					wishActivityService.saveRecord(sourceid + "", user.getId()
//							+ "", fromweb, weiboid, nickName, mobile);
				}
				//篡改数据 只给当前登录人加奖券
				else{
//					wishActivityService.saveSourceAward(user.getId()+"", null,
//							fromweb, weiboid, nickName, "");
				}
				// 清除cookie
				Cookie sourceCookie = WebUtils.removeableCookie("SOURCEID_");
				// Cookie secretCookie=WebUtils.removeableCookie("SECRET_");
				response.addCookie(sourceCookie);
				// response.addCookie(secretCookie);
			}

			else {
				// 就给注册人加奖券
//				wishActivityService.saveSourceAward(user.getId()+"", null,fromweb, weiboid,nickName,mobile);
			}

			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
		} catch (Exception e) {
			result = "regist_system_error";
			response.getWriter().write(result);
			return null;
		}

		result = "ok";
		response.getWriter().write(result);
		return null;
	}

	@RequestMapping("/user/activeLogout.do")
	public String activeLogout(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		// 清除用户登录状态
		SingletonLoginUtils.removeSingleton(response, request);

		Cookie fromWebCookie = WebUtils.removeableCookie("WEIBO_FROMWEB");
		response.addCookie(fromWebCookie);

		Cookie stokenauthCookie = WebUtils.removeableCookie("STOKENAUTH");
		response.addCookie(stokenauthCookie);

		Cookie sourceCookie = WebUtils.removeableCookie("SOURCEID_");
		// Cookie secretCookie=WebUtils.removeableCookie("SECRET_");
		response.addCookie(sourceCookie);
		// response.addCookie(secretCookie);

		Cookie tencentCookie = WebUtils.removeableCookie("TENCENT_WEIBO");
		response.addCookie(tencentCookie);
		SingletonLoginUtils.getMemcacheUser(request);

		String STATIC_URL = property.getProperty("STATIC_URL");
		String activebaseurl = "http://www.qianpin.com/user/goActivePage.do";
		if ("true".equals(STATIC_URL)) {
			activebaseurl = "http://www.qianpin.com/9high";
		}
		return "redirect:" + activebaseurl;
	}

	/**
	 * 跳转到活动中心页面
	 */
	@RequestMapping("/user/forwardActiveCenter.do")
	public String forwardActiveCenter(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		// 获得当前用户
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			String STATIC_URL = property.getProperty("STATIC_URL");
			String activebaseurl = "http://www.qianpin.com/user/goActivePage.do";
			if ("true".equals(STATIC_URL)) {
				activebaseurl = "http://www.qianpin.com/9high";
			}
			return "redirect:" + activebaseurl;
		}
		String fromweb = WebUtils.getCookieValue("WEIBO_FROMWEB", request);
		int randomNumber = RandomNumberUtils.getRandomNumber(10);
		String tempContent = "";
		try {
			if (contentList != null && contentList.size() > 0) {
				tempContent = contentList.get(randomNumber);
			}
		} catch (Exception e) {
			tempContent = content1;
		}
		StringBuilder sb = new StringBuilder(tempContent);
		// 生成邀请链接
		long userid = user.getId();
		String STATIC_URL = property.getProperty("STATIC_URL");
		// String
		// activebaseurl=" http://www.qianpin.com/user/goActivePage.do?sourceid=";
		// if("true".equals(STATIC_URL)){
		// activebaseurl="http://www.qianpin.com/9high";
		// }
		// activebaseurl += userid;
		sb.append(userid);
		sb.append("#9hwb_");
		sb.append(randomNumber + 1);
		if (FROM_TENCENT.equals(fromweb)) {
			sb.append("a");
		}
		// sb.append("&secret=");
		// String secretx = MobilePurseSecurityUtils.hmacSign(user
		// .getCustomerkey(), activebaseurl + userid);
		// sb.append(secretx);
		String url = sb.toString();
		String inviteurl = "http://www.qianpin.com/9high" + userid + "#9hwb_x";
		request.setAttribute("INVITE_URL", inviteurl);

		WeiboInfo weiboInfo = wishActivityService.getWeiboInfo(userid);
		if (weiboInfo == null) {
			weiboInfo = new WeiboInfo();
		}

		if (fromweb == null) {
			fromweb = weiboInfo.getFromweb();
		}

		// 查询微博绑定的用户名、微博头像、微博地址、查询粉丝列表随机生成5个人

		// 判断是腾讯还是新浪 还是普通的邮箱
		// 从新浪点过来的
		if (FROM_SINA.equals(fromweb)) {
			String stokenauth = WebUtils.getCookieValue("STOKENAUTH", request);
			SinaAccessToken accessToken = (SinaAccessToken) memCacheService
					.get("SINA_USER_ACCESSTOKEN_" + stokenauth);
			if (stokenauth != null) {
				RequestToken resToken = (RequestToken) memCacheService
						.get("SINA_REQUESTTOKEN_" + stokenauth);
				Long weiboid = resToken.getUserId();
				String headlogo = "http://tp2.sinaimg.cn/" + weiboid + "/50/1";
				weiboInfo.setWeibologo(headlogo);
				weiboInfo.setScreenName(resToken.getScreenName());
				weiboInfo.setWeiboid(weiboid + "");
				weiboInfo.setUrl("http://weibo.com/" + weiboid);
				request.setAttribute("weiboInfo", weiboInfo);
				request.setAttribute("fromweb", FROM_SINA);

				// 获得随机5个好友
				// System.setProperty("weibo4j.oauth.consumerKey",
				// Weibo.CONSUMER_KEY);
				// System.setProperty("weibo4j.oauth.consumerSecret",
				// Weibo.CONSUMER_SECRET);
				// Weibo weibo=new Weibo();
				// weibo.setToken(accessToken.getToken(),
				// accessToken.getTokenSecret());
				// Set<SinaUser> randomUserList=new HashSet<SinaUser>();
				// try {
				// Paging paging=new Paging ();
				// paging.setCount(100);
				// List<SinaUser>
				// sinaUserList=weibo.getFollowers(accessToken.getUserId()+"",
				// paging);
				// List<Integer>
				// randomNumberList=RandomNumberUtils.getRandomNumberx(3,sinaUserList.size());
				// for (Integer integer : randomNumberList) {
				// if(sinaUserList.size()>integer&&sinaUserList.get(integer)!=null){
				// randomUserList.add(sinaUserList.get(integer));
				// }
				// }
				// StringBuilder sburl=new StringBuilder(url+" ");
				// if(randomUserList!=null){
				// for (SinaUser sinaUser : randomUserList) {
				// sburl.append("@"+sinaUser.getScreenName()+" ");
				// }
				// }
				// url=sburl.toString();
				// } catch (WeiboException e) {
				// e.printStackTrace();
				// }

			} else {
				request.setAttribute("USER__INFO", user);
				request.setAttribute("fromweb", FROM_EMAIL);
			}
		}
		// 从腾讯微博过来的
		else if (FROM_TENCENT.equals(fromweb)) {
			String tokenSecret = WebUtils.getCookieValue("TENCENT_WEIBO",
					request);
			ResModel resModel = (ResModel) memCacheService
					.get("TENCENT_REQUESTTOKEN_" + tokenSecret);
			if (resModel != null) {
				weiboInfo.setWeibologo(resModel.getHead());
				weiboInfo.setScreenName(resModel.getNickName());
				weiboInfo.setWeiboid(resModel.getWeiboid());
				weiboInfo.setUrl("http://t.qq.com/" + resModel.getWeiboname());
				request.setAttribute("weiboInfo", weiboInfo);
				request.setAttribute("fromweb", FROM_TENCENT);
				// 获得腾讯随机5个好友
				QWeiboSyncApi api = new QWeiboSyncApi();
				List<TencentUser> listTencentUser = api.getFansList(100, 0,
						TENCENT_customKey, TENCENT_customSecrect,
						resModel.getToken(), resModel.getTokenSecret(),
						ResultType.ResultType_Json);

				Set<TencentUser> listRandomUser = new HashSet<TencentUser>();
				List<Integer> randomNumberList = RandomNumberUtils
						.getRandomNumberx(3, listTencentUser.size());
				for (Integer integer : randomNumberList) {
					if (listTencentUser.size() > integer
							&& listTencentUser.get(integer) != null) {
						listRandomUser.add(listTencentUser.get(integer));
					}
				}
				StringBuilder sburl = new StringBuilder(url + " ");
				if (listRandomUser != null) {
					for (TencentUser tencentUser : listRandomUser) {
						sburl.append("@" + tencentUser.getNickname() + " ");
					}
				}
				url = sburl.toString();
			} else {
				request.setAttribute("USER__INFO", user);
				request.setAttribute("fromweb", FROM_EMAIL);
			}
		}
		// 从email 正常注册过来的
		else {
			request.setAttribute("USER__INFO", user);
			request.setAttribute("fromweb", FROM_EMAIL);
		}

		String email = user.getEmail();
		if (email != null) {
			email = email.substring(0, email.indexOf("@"));
			email += "...";
		}
		request.setAttribute("USER_EMAIL", email);

		request.setAttribute("ACTIVE_URL", url);

		// 各种查询如下
		// 查询该用户推荐的用户列表
		List<InviteRecordBean> listInviteRecord = wishActivityService
				.getInviteRecordByUserID(Integer.valueOf(userid + ""));
		request.setAttribute("listInviteRecord", listInviteRecord);
		// 查询总排名
		Long userFollowCount = wishActivityService.createInviteRank(String
				.valueOf(userid));
		request.setAttribute("userFollowCount", userFollowCount);

		Long userPrizeCount = wishActivityService.getUserPrizeCount(userid);
		request.setAttribute("userPrizeCount", userPrizeCount);

		// 查询活动榜
		List<InviteRecordBean> listWishUserRank = wishActivityService
				.createWishUserRank();
		if (listWishUserRank != null) {
			memCacheService.set("listWishUserRank", listWishUserRank);
		}

		// 获得注册成功后的 那个奖号
		String awardno = wishActivityService.getRegistUserPrizeNo(userid);
		request.setAttribute("awardno", awardno);

		return "active/active_center";
	}

	public WishActivityService getWishActivityService() {
		return wishActivityService;
	}

	public void setWishActivityService(WishActivityService wishActivityService) {
		this.wishActivityService = wishActivityService;
	}

}
