package com.beike.action.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.UserException;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.WeiboDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.entity.common.Sms;
import com.beike.entity.shopcart.ShopcartSummary;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.GoodsForm;
import com.beike.form.SmsInfo;
import com.beike.form.UserForm;
import com.beike.service.comment.CommentService;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.service.goods.GoodsService;
import com.beike.service.shopcart.ShopCartService;
import com.beike.service.user.UserService;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StaticDomain;
import com.beike.util.TrxConstant;
import com.beike.util.TrxUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title:用户相关action
 * </p>
 * <p>
 * Description:用户登录、用户注册
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
@Controller
public class UserAction extends BaseUserAction {
	private final Log log = LogFactory.getLog(UserAction.class);
	@Autowired
	private SmsService smsService;

	@Autowired
	private UserService userService;

	@Autowired
	private VmAccountService vmAccountService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private WeiboDao weiboDao;
	@Autowired
	private ShopCartService shopcartService;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private CommentService commentService;

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private JavaMailSenderImpl javaMailSender;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	private static final String SMS_TYPE = "15";

	private static final int SMS_RANDOM = 6;
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	/**
	 * 获取首页登录后账户信息
	 */
	@RequestMapping("/index/getLoginMessage.do")
	public String getMainLoginDiv(HttpServletRequest request) {

		Long userId = SingletonLoginUtils.getLoginUserid(request);
		if (userId != null) {
			Long unusedTrxorderCount = userService.getUnusedTrxorder(userId);
			// Long unCommentCount = userService.unComment(userId);
			int unCommentCount = trxorderGoodsService.findCountByUserId(userId,
					Constant.TRX_GOODS_UNCOMMENT);
			Long readyLoseCount = userService.readyLoseTrxorderCount(userId);
			double balance = 0.0;
			try {
				balance = userService.userbalance(userId);
			} catch (Exception e) {
				e.printStackTrace();

			}
			// 未使用
			request.setAttribute("unusedTrxorderCount", unusedTrxorderCount);
			// 未评价
			request.setAttribute("unCommentCount", unCommentCount);
			// 快过期
			request.setAttribute("readyLoseCount", readyLoseCount);
			// 余额
			request.setAttribute("balance", balance);
		}

		return "index/user_message";
	}

	@RequestMapping("/header/getUserLoginInfoDiv.do")
	public Object getUserLoginInfoDiv(HttpServletRequest request) {
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user != null) {
			System.out.println("user...mem..." + user);
			request.setAttribute("DIV_QIANPIN_USER", user);
		}
		ShopcartSummary shopcartSummary = shopcartService
				.getShopSummary(request);
		request.setAttribute("div_shopcartSummary", shopcartSummary);
		request.setAttribute("loginflg", request.getParameter("loginflg"));
		return "templates/user_logininfo";
	}

	@RequestMapping("/user/getUserMsgCookie.do")
	public Object createMsgCookie(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SingletonLoginUtils.getMemcacheUser(request);

		String msgQueue = WebUtils.getCookieValue("USER_MSG_QUEUE", request);
		// 创建cookie
		if (msgQueue == null || (msgQueue.startsWith("0_") && user != null)) {
			// 未登录
			if (user == null) {
				msgQueue = "0_0";
			} else {
				msgQueue = "";
				Long userId = user.getId();
				// 未评价订单
				int unCommentCount = trxorderGoodsService.findCountByUserId(
						userId, Constant.TRX_GOODS_UNCOMMENT);
				if (unCommentCount > 0) {
					msgQueue = msgQueue + "1_" + unCommentCount + "|";
				}
				// 7天内过期余额
				double readyLoseBanlance = commentService
						.getRemindAccountBalance(userId, "30");
				if (readyLoseBanlance > 0) {
					msgQueue = msgQueue + "2_" + readyLoseBanlance + "|";
				}
				// 7天内过期订单
				Long readyLoseCount = userService
						.readyLoseTrxorderCount(userId);
				if (readyLoseCount > 0) {
					msgQueue = msgQueue + "3_" + readyLoseCount + "|";
				}
				if (!"".equals(msgQueue)) {
					msgQueue = msgQueue.substring(0, msgQueue.length() - 1);
				} else {
					msgQueue = "|";
				}
			}
			Cookie cookie = WebUtils.cookie("USER_MSG_QUEUE", msgQueue,
					60 * 60 * 24);
			response.addCookie(cookie);
		} else if (msgQueue != null && user == null
				&& !msgQueue.startsWith("0_")) {
			// 未登录
			msgQueue = "0_0";
			Cookie cookie = WebUtils.cookie("USER_MSG_QUEUE", msgQueue,
					60 * 60 * 24);
			response.addCookie(cookie);
		}
		return null;
	}

	@RequestMapping("/index/getUserLoginMessage.do")
	public String getNewMainLoginDiv(HttpServletRequest request) {
		Long userId = SingletonLoginUtils.getLoginUserid(request);
		if (userId != null) {
			Long unusedTrxorderCount = userService.getUnusedTrxorder(userId);
			// Long unCommentCount = userService.unComment(userId);
			int unCommentCount = trxorderGoodsService.findCountByUserId(userId,
					Constant.TRX_GOODS_UNCOMMENT);
			Long readyLoseCount = userService.readyLoseTrxorderCount(userId);
			double balance = 0.0;
			try {
				balance = userService.userbalance(userId);
			} catch (Exception e) {
				e.printStackTrace();

			}
			// 未使用
			request.setAttribute("unusedTrxorderCount", unusedTrxorderCount);
			// 未评价
			request.setAttribute("unCommentCount", unCommentCount);
			// 快过期
			request.setAttribute("readyLoseCount", readyLoseCount);
			// 余额
			request.setAttribute("balance", balance);
			// 用户ID
			request.setAttribute("newuserid", userId);
		}

		return "templates/user_message";
	}

	// 邮箱注册,下一步需要验证邮箱
	@RequestMapping("/user/sendEmail.do")
	public Object emailRegist(ModelMap model, HttpServletRequest request) {
		User user = getMemcacheUser(request);
		if (user == null) {
			request.setAttribute("ERRMSG", "用户操作超时,请重新登录!");
			return "user/login";
		}
		String email = request.getParameter("email");
		User userx = null;
		try {
			userx = userService.findUserByEmail(email);
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "USER_NOT_EXIST");
			return new ModelAndView("redirect:../500.html");
		}

		if (userx == null) {
			request.setAttribute("ERRMSG", "USER_NOT_EXIST");
			return new ModelAndView("redirect:../500.html");
		}

		String emailTemplate = request.getParameter("emailtemplate");
		String emailTitle = request.getParameter("emailTitle");
		if (emailTemplate == null || "".equals(emailTemplate)) {
			emailTemplate = "EMAIL_VALIDATE";
		}
		if (emailTitle == null || "".equals(emailTitle)) {
			emailTitle = "千品网邮箱认证邮件";
		}
		// 修改邮箱 邮件模板 RESETEMAIL
		String resetPasswordUrl = propertyUtil.getProperty("emailValidateUrl");

		String customerKey = user.getCustomerkey();
		String hmac = MobilePurseSecurityUtils.hmacSign(customerKey,
				user.getId() + "");
		resetPasswordUrl += "?id=" + user.getId() + "&userkey=" + hmac;
		String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
		String date = DateUtils.dateToStr(new Date());
		Object[] emailParams = new Object[] { time, resetPasswordUrl, date };
		try {
			emailService.send(null, null, null, null, null, emailTitle,
					new String[] { email }, null, null, new Date(),
					emailParams, emailTemplate);
			UserProfile userProfile = userService.getProfile(user.getId(),
					Constant.EMAIL_REGIST_URLKEY);
			if (userProfile == null) {
				userService.addProfile(Constant.EMAIL_REGIST_URLKEY, hmac,
						user.getId(), ProfileType.USERCONFIG);
			} else {
				userProfile.setValue(hmac);
				userService.updateProfile(userProfile);
			}
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
		}

		// 用户状态存放到session里
		// request.getSession().setAttribute(Constant.USER_LOGIN, user);

		// 跳转到邮件注册成功页面
		request.setAttribute("SEND_EMAIL_SUCCESS", "true");
		return "/user/useraccount";
	}

	@RequestMapping("/user/validateEmail.do")
	public Object validateEmail(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 1.验证邮件是否有效
		String id = request.getParameter("id");
		String userkey = request.getParameter("userkey");
		Long lid = null;
		User user = null;
		// 验证签名
		boolean isPassHmac = false;
		try {
			lid = Long.parseLong(id);
			user = userService.findById(lid);
			isPassHmac = MobilePurseSecurityUtils.isPassHmac(userkey,
					user.getCustomerkey(), id);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");
		}

		if (user.getEmail_isavalible() == 1) {
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}

		if (!isPassHmac) {
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");

		}

		// 用户扩展信息
		UserProfile userProfile = null;
		boolean isUsable = false;
		try {
			userProfile = userService.getProfile(lid,
					Constant.EMAIL_REGIST_URLKEY);
			isUsable = userService.isUrlUsable(userProfile.getValue(), lid,
					Constant.EMAIL_REGIST_URLKEY);

			if (!isUsable || !userProfile.getValue().equals(userkey)) {
				// request.setAttribute(Constant.USER_ERROR_MESSAGE,
				// "抱歉，此链接已经失效");
				request.setAttribute("ERRMSG", "URLISINVALID");
				return new ModelAndView("/user/500");
			}

			userProfile.setValue(userProfile.getValue() + "X");
			userService.updateProfile(userProfile);

		} catch (Exception e) {
			e.printStackTrace();
			// request.setAttribute(Constant.USER_ERROR_MESSAGE, "抱歉，此链接已经失效");
			request.setAttribute("ERRMSG", "URLISINVALID");
			return new ModelAndView("/user/500");
		}
		// 2.有效的话，将用户状态改为激活状态
		try {
			userService.activationEmail(user);
			this.sendRegistSuccessEmail(user,request, response);
		} catch (UserException e) {
			e.printStackTrace();
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "激活失败,请重新注册!");
			return new ModelAndView("/user/500");
		}
		user.setEmail_isavalible(1);
		user.setIsavalible(1);
		// request.getSession().setAttribute(Constant.USER_LOGIN, user);
		SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
				response, false, request);
		request.setAttribute("ACTIVATIONEMAIL_SUCCESS", "true");
		return "/user/useraccount";
	}

	public void sendRegistSuccessEmail(User user,HttpServletRequest request,HttpServletResponse response){
		try {
			String date = DateUtils.dateToStr(new Date());
			String absdate = date.replaceAll("-","");
			String fromEmail = javaMailSender.getUsername();
			Long areaId = CityUtils.getCityId(request, response);
			List<GoodsForm> listGoods = goodsService.getEmailRecommendGoodIdsByAreaId(areaId, 7);
			StringBuilder mailContent = new StringBuilder("");
			mailContent.append("<table width='700' border='0' align='center' cellpadding='0' cellspacing='0' style='font-size:14px;'>")
			.append("<tr><td><a href='http://www.qianpin.com' target='_blank'><img src='http://c1.qianpincdn.com/edm/template/images/logo.jpg' width='254' height='78' border='0' /></a></td>")
			.append("<td align='right'><img src='http://c1.qianpincdn.com/edm/template/images/topicon.jpg' width='171' height='78' border='0' /></td></tr>")
			.append("<tr><td height='33' colspan='2' bgcolor='#3c3c3c'><table width='700' border='0' cellspacing='0' cellpadding='0' style='color:#fff;'><tr>")
			.append("<td width='50' height='33' align='center'>&nbsp;</td>")
			.append("<td width='80' align='center'><a href='http://www.qianpin.com/ms.html' target='_blank' style='color:#fff;font-size:14px; font-weight:bold; text-decoration:none;'>美食</a></td>")
			.append("<td width='133' align='center'><a href='http://www.qianpin.com/xxyl.html' target='_blank' style='color:#fff;font-size:14px; font-weight:bold; text-decoration:none;'>休闲娱乐</a></td>")
			.append("<td width='115' align='center'><a href='http://www.qianpin.com/jdly.html' target='_blank' style='color:#fff;font-size:14px; font-weight:bold; text-decoration:none;'>酒店旅游</a></td>")
			.append("<td width='104' align='center'><a href='http://www.qianpin.com/lr.html' target='_blank' style='color:#fff;font-size:14px; font-weight:bold; text-decoration:none;'>丽人</a></td>")
			.append("<td width='104' align='center'><a href='http://www.qianpin.com/shfw.html' target='_blank' style='color:#fff;font-size:14px; font-weight:bold; text-decoration:none;'>生活服务</a></td>")
			.append("<td width='50' align='center'>&nbsp;</td></tr></table></td></tr>")
			.append("<tr><td colspan='2' bgcolor='#fff4e8' style='padding:20px;'>")
			.append("<table width='660' border='0' cellspacing='0' cellpadding='0'>")
			.append("<tr><td><table width='657' border='0' cellspacing='0' cellpadding='0' style='border:1px solid #ffc98d'>")
			.append("<tr><td colspan='2' bgcolor='#FFFFFF' style='font-size:14px; font-weight:bold; padding:20px;'>亲爱的千品网用户：</td></tr>")
			.append("<tr><td colspan='2' bgcolor='#FFFFFF' style=' text-indent:2em;line-height:22px; padding:0 20px; font-size:14px;'>")
			.append("<p style='line-height:35px; margin:0;'>您在千品网的邮箱 <strong style='font-family:Arial, Helvetica, sans-serif;'>")
			.append(user.getEmail()).append("</strong> 认证成功，您可以使用此邮箱登录千品网。</p>")
			.append("<p style='line-height:35px; margin:0;'>各种美食、自助餐、电影票立即抢购：<a href='http://www.qianpin.com?abacusoutsid=edm_v2_")
			.append(absdate).append("_tnl_all_sow_r9940' target='_blank' style='color:#3974f2; font-family:Arial, Helvetica, sans-serif;' >http://www.qianpin.com</a></p>")
			.append("<p style='line-height:25px; margin:0;'>&nbsp;</p></td></tr>")
			.append("<tr><td width='326' height='12' align='right' bgcolor='#FFFFFF'></td>")
			.append("<td width='329' height='85' rowspan='2' align='left' bgcolor='#FFFFFF'>")
			.append("<a target='_blank' href='http://www.qianpin.com/goods/searchGoodsByProperty.do?abacusoutsid=edm_v2_")
			.append(absdate).append("_tnl_all_sow_r9940' style='color:#3974f2; font-size:14px; font-weight:bold; margin-left:8px;'>更多热卖精品&gt;&gt;</a></td></tr>")
			.append("<tr><td align='right' bgcolor='#FFFFFF'><a target='_blank' href='http://www.qianpin.com/miaosha/listMiaoSha.do?status=1&abacusoutsid=edm_v2_")
			.append(absdate).append("_tnl_all_sow_r9940'><img src='http://c2.qianpincdn.com/jsp/images_a/jrmsbtn.png' width='126' height='50' border='0' /></a></td></tr>")
			.append("<tr><td height='20' colspan='2' bgcolor='#FFFFFF'>&nbsp;</td></tr></table></td></tr>"); //结束657的table
			if(null != listGoods && listGoods.size() == 6){
				mailContent.append("<tr><td><table width='657' border='0' cellspacing='0' cellpadding='0' style='background:url(http://c1.qianpincdn.com/edm/template/images/tt02.png) no-repeat;'>")
				.append("<tr><td width='541' height='20'></td><td width='116'></td></tr>")
				.append("<tr><td height='40'></td><td width='116'><a href='http://www.qianpin.com/goods/searchGoodsByProperty.do?abacusoutsid=edm_v2_").append(absdate).append("_tnl_all_sow_r9940' target='_blank' style='font-size:12px;'>更多热卖商品&gt;&gt;</a></td></tr></table>")
				.append("<table width='657' border='0' cellspacing='0' cellpadding='0' style='border:1px solid #ffc88d; border-top:0;'>")
				.append("<tr><td height='45' align='center' bgcolor='#fffaf6'>")
				.append("<table width='605' border='0' cellspacing='0' cellpadding='0' style='margin:15px 0; font-size:12px;'><tr>");
	
				for(int i = 0 ; i < listGoods.size() ; i++){
					GoodsForm goodsForm = listGoods.get(i);
					String city = PinyinUtil.hanziToPinyin(goodsForm.getCity(),"");
					String buyNowUrl = "http://"+city+".qianpin.com/shopcart/shopcart.do?command=addShopitem&goodsid="+goodsForm.getGoodsId()+"&merchantId="+goodsForm.getMerchantId()+"&abacusoutsid=edm_v2_"+absdate+"_tnl_all_sow_r9940";
					String logoPath = StaticDomain.getDomain("")+request.getContextPath()+"/jsp/uploadimages/"+goodsForm.getLogo4();
					String goodsUrl = "http://"+city+".qianpin.com/goods/"+goodsForm.getGoodsId()+".html?abacusoutsid=edm_v2_"+absdate+"_tnl_all_sow_r9940";
					String goodsName = goodsForm.getGoodsname();
					if(goodsName.length() > 15){
						goodsName = goodsName.substring(0,15);
					}
					mailContent.append("<td align='center' bgcolor='#FFFFFF'><table width='182' border='0' cellspacing='0' cellpadding='0' style='font-size:12px;'>")
					.append("<tr><td height='120' colspan='2' align='center' valign='middle'><a href='").append(goodsUrl).append("' title='").append(goodsForm.getGoodsname()).append("' target='_blank'><img src='").append(logoPath).append("' width='180' style='border:1px solid #ccc;' height='108' border='0' /></a></td></tr>")
					.append("<tr><td height='22' colspan='2' align='center'><a href='").append(goodsUrl).append("' title='").append(goodsForm.getGoodsname()).append("' target='_blank' style='color:#666; text-decoration:none; '>").append(goodsName).append("</a></td></tr>")
					.append("<tr><td width='99' height='30' bgcolor='#f3f3f3' style='color:#999; padding-left:5px;'>现价<span style='color:#F00; font-size:16px; font-family:Arial, Helvetica, sans-serif; margin-left:3px; font-weight:bold;'>&yen;").append(goodsForm.getCurrentPrice()).append("</span></td>")
					.append("<td width='83' align='right' bgcolor='#f3f3f3'><a href='").append(buyNowUrl).append("' target='_blank'><img src='http://c1.qianpincdn.com/edm/template/images/ljgmbtn.png' border='0' width='68' height='21' /></a></td></tr>")
					.append("<tr style='color:#999;'><td height='30' width='68px' style='padding-left:5px;'>原价 <del>").append(goodsForm.getSourcePrice()).append("</del></td><td align='right' width='114px'>已有<span style='color:#333;'>").append(goodsForm.getSalescount()).append("</span>人购买</td></tr></table></td>");
					if(i+1 != listGoods.size()){
						if((i+1)%3 == 0){
							mailContent.append("</tr></table><table width='605' border='0' cellspacing='0' cellpadding='0' style='margin:15px 0; font-size:12px;'><tr><td align='center' bgcolor='#FFFFFF'>");
						}else{
							mailContent.append("<td bgcolor='#FFFFFF' style='background:url(http://c1.qianpincdn.com/edm/template/images/v-line.png) center top repeat-y #fff;'>&nbsp;</td>");
						}
					}					
				}
				mailContent.append("</tr></table></td></tr></table></td></tr>");
			}
			//结束660table	
			mailContent.append("</table></td></tr>")
			.append("<tr><td height='50' colspan='2' align='center'>如果您以后不想收到此类邮件，请点击<a target='_blank' href='mailto:tuiding@qianpin.com?subject=退订' style='color:#000;'>取消订阅</a></td></tr></table>");		
		System.out.println(mailContent.toString());
			emailService.sendMail(user.getEmail(), fromEmail, mailContent.toString(), "千品网邮箱认证成功邮件");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("The validation  Successed registered user send  mail failed......");
		}
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
		} else if ("HUODONG".equals(loginRegSource)) {
			// add by xuxiaoxian 2012-06-06 活动页面过来的
			if ("user/login".equals(normalforUrl)
					|| "user/registsuccess".equals(normalforUrl)) {
				return normalforUrl;
			} else {
				return "../huodong/regist/regist";
			}
		} else {
			// 正常去向,判断是从哪里过来的,
			// String url=WebUtils.getRequestPath(request);
			String refer_url = WebUtils.getCookieValue(
					"REQUESTURI_REFER_COOKIE", request);
			log.info("login success....redirect url:" + refer_url);
			request.setAttribute("refer_url", refer_url);

			if ("user/login".equals(normalforUrl)
					|| "user/registsuccess".equals(normalforUrl)
					|| "user/regist".equals(normalforUrl)) {

				return normalforUrl;
			}

			if (StringUtils.isBlank(refer_url)) {
				refer_url = "/";
			}
			return new ModelAndView("redirect:" + refer_url);
		}

	}

	/**
	 * 退出登录
	 */
	@RequestMapping("/user/logout.do")
	public Object logout(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		User user = getMemcacheUser(request);
		if (user != null) {
			// request.getSession().removeAttribute(Constant.USER_LOGIN);
			SingletonLoginUtils.removeSingleton(response, request);
		}

		// 清除sina 缓存
		String sinacookie = WebUtils.getCookieValue("STOKENAUTH", request);
		memCacheService.remove("SINA_USER_ACCESSTOKEN_" + sinacookie);
		Cookie sinac = WebUtils.removeableCookie("STOKENAUTH", ".qianpin.com");
		response.addCookie(sinac);

		// 清除xiaonei 缓存
		String xuuid = WebUtils.getCookieValue("XWEIBOUUID", request);
		memCacheService.remove("XIAONEI_ACCESSTOKEN_" + xuuid);
		Cookie xiaoneic = WebUtils.removeableCookie("XWEIBOUUID",
				".qianpin.com");
		response.addCookie(xiaoneic);

		// 清除baidu缓存
		String buuid = WebUtils.getCookieValue("BAIDUUUID", request);
		memCacheService.remove("BAIDU_ACCESSTOKEN_" + buuid);
		Cookie baiduc = WebUtils.removeableCookie("BAIDUUUID", ".qianpin.com");
		response.addCookie(baiduc);
		Cookie baidusc = WebUtils.removeableCookie("BAIDUUSERID",
				".qianpin.com");
		response.addCookie(baidusc);

		// 清除QQ缓存
		String qquuid = WebUtils.getCookieValue("QQUUID", request);
		memCacheService.remove("QQ_ACCESSTOKEN_" + qquuid);
		Cookie qqc = WebUtils.removeableCookie("QQUUID", ".qianpin.com");
		response.addCookie(qqc);

		// 清除360缓存
		String tuan360uuid = WebUtils.getCookieValue("TUAN360UUID", request);
		memCacheService.remove("TUAN360_REQUESTTOKEN" + tuan360uuid);
		Cookie tuan360c = WebUtils.removeableCookie("TUAN360UUID",
				".qianpin.com");
		response.addCookie(tuan360c);
		Cookie tuan360usc = WebUtils.removeableCookie("TUAN360USERID",
				".qianpin.com");
		response.addCookie(tuan360usc);

		// 清除支付宝缓存
		String alipayuuid = WebUtils.getCookieValue("ALIPAYUUID", request);
		memCacheService.remove("ALIPAY_MODEL" + alipayuuid);
		Cookie alipayc = WebUtils
				.removeableCookie("ALIPAYUUID", ".qianpin.com");
		response.addCookie(alipayc);

		// 删除提示消息队列cookie
		Cookie msgQueue = WebUtils.removeableCookie("USER_MSG_QUEUE",
				".qianpin.com");
		response.addCookie(msgQueue);

		return new ModelAndView("redirect:../forward.do?param=index.index");
	}

	/**
	 * 功能：确认修改密码，输入完两遍密码点提交到此action
	 * 
	 * 访问页面名称:
	 * 
	 * /jsp/user/forgetpassword.jsp
	 * 
	 * 访问路径: /user/confirmResetPassword.do
	 * 
	 * 跳转页面名称: 1. 输入有误 跳转地址:/jsp/user/sendResetEmailSuccess.jsp 2. 成功后
	 * 跳转地址：/jsp/user/resetpasswordsuccess.jsp
	 * 
	 * 输入参数: 1.新密码:resetpassword 2.确认密码: confirmpassword
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * PASSWORD_NOT_SAME: 密码输入不一致
	 * 
	 * SYSTEM_ERROR:系统繁忙
	 * 
	 */
	@RequestMapping("/user/confirmResetPassword.do")
	public ModelAndView confirmResetPassword(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		String uid = request.getParameter("userid");
		String hmac = request.getParameter("hmac");
		User user = null;
		Long userid = 0L;
		try {
			userid = Long.parseLong(uid);
			user = userService.findById(userid);
		} catch (Exception e) {
			log.info("user not exist userid=" + userid);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "请不要篡改参数!");
			return new ModelAndView("redirect:../500.html");
		}
		if (user == null) {
			log.info("user not exist userid=" + userid);
			request.setAttribute("ERRMSG", "请不要篡改参数!");
			return new ModelAndView("redirect:../500.html");
		}
		// request.getSession().setAttribute("USER_FORGETPASSWORD_"+user.getId(),
		// user);
		memCacheService.set("USER_FORGETPASSWORD_" + user.getId(), user);
		String customerKey = user.getCustomerkey();
		boolean flag = MobilePurseSecurityUtils.isPassHmac(hmac, customerKey,
				uid);
		// 验证签名失败
		if (!flag) {
			log.info("confirm email validate hmac error!!!,userid=" + userid);
			request.setAttribute("ERRMSG", "请不要篡改参数!");
			return new ModelAndView("redirect:../500.html");
		}

		String password = request.getParameter("resetpassword");
		String confirmpassword = request.getParameter("confirmpassword");
		// 假如两个密码不一致
		if (!password.equals(confirmpassword)) {
			request.setAttribute("ERRMSG", "PASSWORD_NOT_SAME");
			return new ModelAndView("user/resetpassword");
		}

		String newPassword = MobilePurseSecurityUtils.secrect(password,
				user.getCustomerkey());
		user.setPassword(newPassword);
		try {
			userService.updateUserMessage(user);
		} catch (UserException e) {
			log.info("SYSTEM_ERROR,userid=" + userid);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "SYSTEM_ERROR");
			return new ModelAndView("user/forgetpassword");
		}
		String refer_url = WebUtils.getCookieValue("REQUESTURI_REFER_COOKIE",
				request);
		request.setAttribute("refer_url", refer_url);
		request.setAttribute("USER_NAME", user.getEmail());
		// 更新密码成功,清空cookie:SINGLETON_COOKIE_KEY by janwen
		response.addCookie(WebUtils.removeableCookie("SINGLETON_COOKIE_KEY",
				".qianpin.com"));
		response.addCookie(WebUtils.removeableCookie("REQUESTURI_REFER_COOKIE",
				".qianpin.com"));
		return new ModelAndView("user/resetpasswordsuccess");
	}

	// 邮件跳转到确定页面 验证链接
	@RequestMapping("/user/forwardConfirmResetPassword.do")
	public Object forwardConfirmResetPassword(ModelMap model,
			HttpServletRequest request) {
		String userid = request.getParameter("key");
		String hmac = request.getParameter("hmac");

		log.info("key:" + userid + "---hmac:" + hmac);
		User user = null;
		try {
			user = userService.findById(Long.parseLong(userid));
		} catch (Exception e) {
			log.info("user not exist userid=" + userid);
			e.printStackTrace();
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");
		}
		if (user == null) {
			log.info("user not exist userid=" + userid);
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");
		}
		// request.getSession().setAttribute("USER_FORGETPASSWORD_"+user.getId(),
		// user);
		memCacheService.set("USER_FORGETPASSWORD_" + user.getId(), user);
		String customerKey = user.getCustomerkey();
		boolean flag = MobilePurseSecurityUtils.isPassHmac(hmac, customerKey,
				userid);
		// 验证签名失败
		if (!flag) {
			log.info("confirm email validate hmac error!!!,userid=" + userid);
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");
		}

		// 用户扩展信息
		UserProfile userProfile = null;
		boolean isUsable = false;
		try {
			userProfile = userService.getProfile(user.getId(),
					"USER_RESET_PASSWORD");
			isUsable = userService.isUrlUsable(userProfile.getValue(),
					user.getId(), "USER_RESET_PASSWORD");

			if (!isUsable || !userProfile.getValue().equals(hmac)) {
				// request.setAttribute(Constant.USER_ERROR_MESSAGE,
				// "链接已经失效,请重新发送!");
				log.info("RESETURLISINVALID userid=" + userid);
				request.setAttribute("ERRMSG", "RESETURLISINVALID");
				return new ModelAndView("/user/500");
			}
			userProfile.setValue(userProfile.getValue() + "X");
			userService.updateProfile(userProfile);
		} catch (Exception e) {
			log.info("找回密码邮件认证失败:" + e);
			e.printStackTrace();
			// request.setAttribute(Constant.USER_ERROR_MESSAGE,
			// "链接已经失效,请重新激活!");
			log.info("RESETURLISINVALID userid=" + userid);
			request.setAttribute("ERRMSG", "RESETURLISINVALID");
			return new ModelAndView("/user/500");
		}
		request.setAttribute("userid", userid);
		request.setAttribute("hmac", hmac);
		return "/user/resetpassword";
	}

	/**
	 * 功能：忘记密码，用户输入手机号发送完短信后验证，或者输入完邮箱直接调用
	 * 
	 * 访问页面名称: /jsp/user/forgetpassword.jsp 访问路径: /user/forgetPassword.do
	 * 跳转页面名称: 1. 用户输入邮件调用后 跳转地址:/jsp/user/sendResetEmailSuccess.jsp 2.
	 * 用户输入手机调用后 跳转地址:/jsp/user/updatepassword_validatemobile.jsp
	 * 
	 * 输入参数: 1.手机/邮箱: USER_PARAM 2.假如输入是手机,输入邮箱不传此参数: validCode
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * USER_MOBILE_PARAM_ERROR: 用户名参数格式有误
	 * 
	 * VALIDATE_CODE_ERROR:注册码输入有误
	 * 
	 * USER_TIMEOUT:系统繁忙
	 * 
	 * USER_NOT_EXIST: 用户名不存在
	 * 
	 */
	@RequestMapping("/user/forgetPassword.do")
	public String forgetPassword(ModelMap model, HttpServletRequest request,
			HttpServletResponse respone) {
		// String validateCode = request
		// .getParameter(Constant.URSER_VALIDATE_CODE);
		String username = request.getParameter("USER_PARAM");

		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
				request);
		String vcode = (String) memCacheService.get("validCode_" + cookieCode);
		String piccode = request.getParameter("validCode");
		if (vcode == null) {
			log.info("USER_TIMEOUT,username:" + username + ",vcode=null");
			request.setAttribute("ERRMSG", "USER_TIMEOUT");
			return "/user/forgetpassword";
		}

		boolean ismobile = MobilePurseSecurityUtils
				.isJointMobileNumber(username);
		boolean isEmail = MobilePurseSecurityUtils.checkEmail(username, 0);
		if (username == null || "".equals(username)) {
			request.setAttribute("ERRMSG", "USER_MOBILE_PARAM_ERROR");
			return "/user/forgetpassword";
		}
		String uuid = UUID.randomUUID().toString();
		Cookie cookie = WebUtils.cookie("REGIST_RANDOMNUMBER", uuid, -1);
		respone.addCookie(cookie);
		if (ismobile) {

			/*
			 * User ux = null; try { ux =
			 * userService.findUserByMobile(username); } catch (UserException e)
			 * { e.printStackTrace(); }
			 * 
			 * if (ux == null) { log.info("USER_NOT_EXIST,username:" +
			 * username); request.setAttribute("ERRMSG", "USER_NOT_EXIST");
			 * return "user/forgetpassword"; } //
			 * request.getSession().setAttribute("USER_FORGETPASSWORD", ux);
			 * memCacheService.set("USER_FORGETPASSWORD_" + uuid, ux);
			 * Map<String, String> map = (Map<String, String>) memCacheService
			 * .get("REGIST_RANDOMNUMBER_" + uuid); if (vcode == null || map ==
			 * null) { request.setAttribute(Constant.USER_ERROR_MESSAGE,
			 * "VALIDATE_CODE_ERROR"); log.info("VALIDATE_CODE_ERROR,username:"
			 * + username); return "/user/forgetpassword"; } String mobileCode =
			 * map.get(username); String mc = null; if (mobileCode != null) { mc
			 * = mobileCode.split(":")[1]; }
			 * 
			 * if (!vcode.equalsIgnoreCase(mc)) { request.setAttribute("ERRMSG",
			 * "VALIDATE_CODE_ERROR"); log.info("VALIDATE_CODE_ERROR,username:"
			 * + username + ",vcode=" + vcode + ",mc=" + mc); return
			 * "user/forgetpassword"; }
			 * 
			 * return "/user/resetpassword";*
			 */

			if (!vcode.equalsIgnoreCase(piccode)) {
				request.setAttribute("ERRMSG", "VALIDATE_CODE_ERROR");
				log.info("VALIDATE_CODE_ERROR,username:" + username + ",vcode="
						+ vcode + ",piccode=" + piccode);
				return "/user/forgetpassword";
			}
			try {
				User ux = userService.findUserByMobile(username);

				if (ux == null) {
					log.info("USER_NOT_EXIST,username:" + username);
					request.setAttribute("ERRMSG", "USER_NOT_EXIST");
					return "user/forgetpassword";
				}
				// 解密后密码
				String dcpassword = MobilePurseSecurityUtils.decryption(
						ux.getPassword(), ux.getCustomerkey());
				// 短信参数
				String smsTemplate = Constant.SMS_MOBILE_FORGETPASSWORD;
				Sms sms = null;
				sms = smsService.getSmsByTitle(smsTemplate);
				if (sms != null) {
					SmsInfo sourceBean = null;
					String content = "";
					String template = sms.getSmscontent();
					// 短信参数
					Object[] param = new Object[] { dcpassword };
					content = MessageFormat.format(template, param);
					sourceBean = new SmsInfo(username, content, SMS_TYPE, "0");
					smsService.sendSms(sourceBean);

					memCacheService.remove("validCode_" + cookieCode);
					// Cookie delCookieCode =
					// WebUtils.removeableCookie("RANDOM_VALIDATE_CODE");
					// respone.addCookie(delCookieCode);
					return "user/sendPasswordMobileSuccess";
				}
			} catch (Exception e) {
				request.setAttribute("ERRMSG", "USER_TIMEOUT");
				return "user/forgetpassword";
			}
			return "user/forgetpassword";
		} else if (isEmail) {
			if (!vcode.equalsIgnoreCase(piccode)) {
				request.setAttribute("ERRMSG", "VALIDATE_CODE_ERROR");
				log.info("VALIDATE_CODE_ERROR,username:" + username + ",vcode="
						+ vcode + ",piccode=" + piccode);
				return "/user/forgetpassword";
			}
			User user = null;
			try {
				user = userService.findUserByEmail(username);
			} catch (UserException e1) {
				e1.printStackTrace();
				request.setAttribute("ERRMSG", "USER_NOT_EXIST");
				log.info("USER_NOT_EXIST,username:" + username);
				return "user/forgetpassword";
			}
			if (user == null) {
				request.setAttribute("ERRMSG", "USER_NOT_EXIST");
				log.info("USER_NOT_EXIST,username:" + username);
				return "user/forgetpassword";
			}

			// email 发邮件回调/user/confirmResetPassword.do 完成重输入密码
			String resetPasswordUrl = propertyUtil
					.getProperty("resetPasswordUrl");
			// 在本地测试
			// String resetPasswordUrl =
			// "http://www.qianpin.com/user/forwardConfirmResetPassword.do";

			String customerKey = user.getCustomerkey();
			String hmac = MobilePurseSecurityUtils.hmacSign(customerKey,
					user.getId() + "");
			resetPasswordUrl += "?key=" + user.getId() + "&hmac=" + hmac;

			String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
			String date = DateUtils.dateToStr(new Date());

			Object[] emailParams = new Object[] { time, resetPasswordUrl, date };
			try {
				emailService.send(null, null, null, null, null, "千品网重置密码邮件",
						new String[] { username }, null, null, new Date(),
						emailParams, "RESETEMAIL");
				// 加入扩展信息,找回密码邮件信息
				UserProfile userProfile = userService.getProfile(user.getId(),
						"USER_RESET_PASSWORD");
				if (userProfile == null) {
					userService.addProfile("USER_RESET_PASSWORD", hmac,
							user.getId(), ProfileType.USERCONFIG);
				} else {
					userProfile.setValue(hmac);
					userService.updateProfile(userProfile);
				}
			} catch (Exception e) {
				log.info(e);
				e.printStackTrace();
			}
			memCacheService.set("USER_FORGETPASSWORD_" + uuid, user);
			return "/user/sendResetEmailSuccess";
		} else {
			request.setAttribute("ERRMSG", "USER_MOBILE_PARAM_ERROR");
			return "/user/forgetpassword";
		}

	}

	/**
	 * 功能：用户注册流程
	 * 
	 * 访问页面名称: /jspuser/regist.jsp 访问路径: /user/userRegist.do 跳转页面名称:
	 * /jspuser/registsuccess.jsp
	 * 
	 * 输入参数: 1.用户名: EMAIL_REGIST 2.密码:USER_PASSWORD 3.验证码:validCode
	 * 
	 * 输出参数: 1.错误信息:ERRMSG 范围：request
	 * 
	 * USERNAME_ERROR: 用户名参数格式有误
	 * 
	 * PASSWORD_ERROR: 密码格式错误:
	 * 
	 * registcode:注册码输入有误
	 * 
	 * USER_EXIST:用户已经存在
	 * 
	 * SYSTEM_ERROR:系统繁忙
	 * 
	 */
	@RequestMapping("/user/userRegist.do")
	public Object userRegist(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			replaceScript(request);
		} catch (Exception e1) {
			return "redirect:http://www.qianpin.com/jsp/user/regist.jsp";
		}

		// 判断假如用户输入格式有误跳转页面
		String paramerror = (String) request
				.getAttribute(Constant.USER_PARAM_VALIDATE_ERROR);
		if ("true".equals(paramerror)) {
			request.setAttribute("ERRMSG", "USERNAME_ERROR");
			return checkToForwardUrl(request, "user/regist", "forward");
			// /return "user/regist";
		}
		// 密码格式
		String userpassworderror = (String) request
				.getAttribute(Constant.USER_PASSWROD_VALIDATE_ERROR);
		if ("true".equals(userpassworderror)) {
			request.setAttribute("ERRMSG", "PASSWORD_ERROR");
			return checkToForwardUrl(request, "user/regist", "forward");
			// return "user/regist";
		}
		// String registcode = (String) request
		// .getAttribute(Constant.USER_REGIST_CODE);
		// if ("true".equals(registcode)) {
		// request.setAttribute("ERRMSG", "VALIDATE_EROR");
		// return checkToForwardUrl(request, "user/regist","forward");
		// // return "user/regist";
		// }

		String validCode = request.getParameter(Constant.USER_REGIST_CODE);
		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
				request);
		if (cookieCode != null) {
			String sessionCode = (String) memCacheService.get("validCode_"
					+ cookieCode);
			memCacheService.remove("validCode_" + cookieCode);
			if (validCode == null || !validCode.equalsIgnoreCase(sessionCode)
					|| sessionCode == null) {
				request.setAttribute("ERRMSG", "VALIDATE_EROR");
				return checkToForwardUrl(request, "user/regist", "forward");
				// return "user/login";
			}
		} else {
			request.setAttribute("ERRMSG", "VALIDATE_EROR");
			return checkToForwardUrl(request, "user/regist", "forward");
		}
		// String sessionCode = (String) request.getSession().getAttribute(
		// "validCode");
		// if (!validCode.equals(sessionCode)) {
		// request.setAttribute("ERRMSG", "VALIDATECODE_ERROR");
		// request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
		// return checkToForwardUrl(request, "user/login","forward");
		// // return "user/login";
		// }

		// 判断假如用户已经存在跳转页面
		String userExist = (String) request.getAttribute(Constant.USER_EXIST);
		if ("true".equals(userExist)) {
			request.setAttribute("ERRMSG", "USER_EXIST");
			return checkToForwardUrl(request, "user/regist", "forward");
			// return "user/regist";
		}

		String email = request.getParameter(Constant.USER_EMAIL_REGIST);
		String password = request.getParameter(Constant.USER_PASSWORD);
		// 加入打印日志来源区分
		String loginRegSource = request
				.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);

		User user = null;
		// 邮箱注册
		try {
			String userIp = WebUtils.getIpAddr(request);
			UserForm userForm = new UserForm();
			userForm.setEmail(email);
			userForm.setPassword(password);
			userForm.setUserIp(userIp);
			user = userService.addUserEmailRegist(userForm);

		} catch (BaseException e) {
			if (!"TRX".equals(loginRegSource)) {

				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_reg_failed");
				logMap.put("error", e.getCode() + "");
				LogAction.printLog(logMap);
			} else {
				/************************ 日志开始 ************************************/
				// 交易日志埋点2.0
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "t_reg_failed");
				logMap.put("error", e.getCode() + "");
				LogAction.printLog(logMap);
				/************************ 日志结束 ************************************/
			}
			log.info(e);
			e.printStackTrace();
			// 设置错误信息
			request.setAttribute("ERRMSG", "SYSTEM_ERROR");
			return new ModelAndView("redirect:../500.html");
		} catch (Exception e) {
			e.printStackTrace();
			if (!"TRX".equals(loginRegSource)) {
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_reg_failed");
				logMap.put("error", e.getMessage());
				LogAction.printLog(logMap);
			} else {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "t_reg_failed");
				logMap.put("error", e.getMessage());
				LogAction.printLog(logMap);
				/************************ 日志结束 ************************************/

			}
			log.info("发送邮件失败....send email fail!");
			request.setAttribute("ERRMSG", "SYSTEM_ERROR");
			return new ModelAndView("redirect:../500.html");
		}

		if (user != null) {

			String city = CityUtils.getCity(request, response);
			// 0元抽奖 add by qiaowb 2012-06-04
			if ("LOTTERY".equals(loginRegSource)) {
				// 打印日志
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "h_reg");
				logMap2.put("uid", user.getId() + "");
				LogAction.printLog(logMap2);
			} else if (!"TRX".equals(loginRegSource)) {
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "UserRegInSite");
				logMap2.put("uid", user.getId() + "");
				logMap.put("action", "u_reg");
				logMap.put("uid", user.getId() + "");
				LogAction.printLog(logMap);
				LogAction.printLog(logMap2);
			} else {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap.put("action", "UserRegInTrx");// 交易流程注册完成时 日志所需
				logMap.put("uid", user.getId() + "");

				logMap2.put("action", "t_reg");// 日志埋点2.0
				logMap2.put("uid", user.getId() + "");
				LogAction.printLog(logMap);
				LogAction.printLog(logMap2);

				/************************ 日志结束 ************************************/

			}
		}

		// 用户状态存放到session里
		// request.getSession().setAttribute(Constant.USER_LOGIN, user);

		// 加入扩展信息,激活邮件信息
		try {
			String emailValidateUrl = propertyUtil
					.getProperty(Constant.EMAIL_VALIDATE_URL);
			StringBuilder sb = new StringBuilder();
			sb.append(emailValidateUrl);
			// 在本地测试
			// sb.append("http://www.qianpin.com/user/validateEmail.do");
			sb.append("?id=" + user.getId() + "&userkey=");
			String secret = MobilePurseSecurityUtils.hmacSign(
					user.getCustomerkey(), user.getId() + "");
			sb.append(secret);
			String subject = "千品网邮箱认证邮件"; // 确认?
			UserProfile userProfile = userService.getProfile(user.getId(),
					Constant.EMAIL_REGIST_URLKEY);
			if (userProfile == null) {
				userService.addProfile(Constant.EMAIL_REGIST_URLKEY, secret,
						user.getId(), ProfileType.USERCONFIG);
			} else {
				userProfile.setValue(secret);
				userService.updateProfile(userProfile);
			}

			String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
			String date = DateUtils.dateToStr(new Date());
			
			//author wenjie.mai 更改邮件模板，添加参数
			String abstime = date.replaceAll("-","");
			// 设置动态参数
			Object[] emailParams = new Object[] { time, sb.toString(), date,abstime };
			// 邮件模板参数未设置
			emailService.send(null, null, null, null, null, subject,
					new String[] { email }, null, null, new Date(),
					emailParams,Constant.NEW_REGIST_TEMPLATE);
		} catch (Exception e) {
			log.info("send email success....");
			e.printStackTrace();
		}

		if (user != null) {
			
			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			// 交易流程中加入用户类型COOkies //新注册的用户
			TrxUtils.addTrxCookies(response, request,
					TrxConstant.TRX_LOGIN_TYPE + user.getId(),
					TrxConstant.TRX_LOGIN_TYPE_NEW_REG);
		}

		// 判断固定参数 是主页注册 还是交易流程注册

		// String trxregist = request.getParameter("trxregist");
		// if (trxregist != null && "trxregist".equals(trxregist)) {
		// // 假如是交易流程注册需要跳转到 checkMobile.do 检验用户
		// request.setAttribute("trxregist", trxregist);
		// String referurl = request.getParameter("referurl");
		// request.getSession().setAttribute("referurl", referurl);
		// return "forward:user/checkMobile.action";
		// }
		/**
		 * 根据渠道号码,送优惠券
		 * add by wangweijie
		 */
		String csid = WebUtils.getCookieValue("bi_csid", request);
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("userId", String.valueOf(user.getId()));	//用户ID
		sourceMap.put("csid", csid);		//优惠券密码
		sourceMap.put("reqChannel","WEB");	//web		
		Map<String, String> returnMap = trxHessianServiceGateWay.autoBindCoupon(sourceMap);
		log.info("+++++++++autoBindCoupon return:" + returnMap);
		
		/**
		 * 注册成功，添加10元优惠券 add by wangweijie 2012-07-09 --- begin
		 */
		// String loginRegSource =
		// request.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
		long couponAmount = Long.parseLong(Constant.COUPON_AMOUNT); // 优惠金额
		long vmAccountId = Long.parseLong(Constant.COUPON_VMACCOUNT_ID); // 虚拟账户主键
		String validDate = Constant.COUPON_VMACCOUNT_LOSEDATE; // 有效期，跟虚拟账户的有效期一直（省去查数据库）
		long maxAge = (DateUtils.toDate(validDate, "yyyy-MM-dd").getTime() - new Date().getTime()) / 1000; // cookie 最大有效时间，单位为毫秒，需要转换为秒
		maxAge = maxAge < 0 ? 0 : maxAge; // 负数情况的处理

		if (!StringUtils.isEmpty(csid) && csid.startsWith("portal")) {// 上线后放开
			boolean addCouponResult = false;
			try {
				String description = "优惠券有效期" + validDate.substring(0, 10)
						+ "；逾期作废；不可提现"; //
				addCouponResult = userService.noTscAddCouponsForUser(
						user.getId(), vmAccountId, couponAmount, description);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e);
			}
			if (addCouponResult) {
				// 日志开始
				Map<String, String> couponLogMap = LogAction.getLogMap(request,
						response);
				couponLogMap.put("action", "mk_cashback");// 线上优惠券日志埋点
				couponLogMap.put("uid", String.valueOf(user.getId()));
				LogAction.printLog(couponLogMap);
				// 日志结束

				// 在COOKIE里添加新增10元优惠券信息
				request.setAttribute("couponAmount", couponAmount);
				request.setAttribute("addCouponResult", "success");
				response.addCookie(WebUtils.cookie(
						"NEW_COUPON_TIPS_" + user.getId(), "true", (int) maxAge));
			}
		}
		/**
		 * 注册成功，添加10元优惠券 add by wangweijie 2012-07-09 -----end
		 */

		// 跳转到正常的登录成功页面
		return checkToForwardUrl(request, "user/registsuccess", "redirect");
		// return "user/registsuccess";
	}

	// 检查mobile 是否为null 是否激活
	@RequestMapping("/user/checkMobile.do")
	public Object checkMobile(ModelMap model, HttpServletRequest request) {
		User user = getMemcacheUser(request);
		if (user == null) {
			request.setAttribute("ERRMSG", "用户未登录,请重新登录!");
			// return "user/login";
			return checkToForwardUrl(request, "user/login", "forward");
		}
		String email = user.getEmail();
		try {
			user = userService.findUserByEmail(email);
		} catch (UserException e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "系统繁忙，请稍候再试!");
			return new ModelAndView("redirect:../500.html");
		}
		if (user == null) {
			request.setAttribute("ERRMSG", "该用户不存在");
			return new ModelAndView("redirect:../500.html");
		}

		// 假如用户手机号为空 未认证 则进入验证短信页面
		if (user.getMobile() == null && user.getMobile_isavalible() == 0) {
			// 验证短信的页面
			return checkToForwardUrl(request, "user/validatesms", "forward");
			// return "user/validatesms";
		}

		// 用户手机已经认证过 1.交易注册或者登录后 检查
		// String trxregist=(String) request.getAttribute("trxregist");
		// 假如trxregist不为空 是从交易注册页面过来 跳转到登录后
		// String referurl = (String) request.getSession()
		// .getAttribute("referurl");
		// if (referurl != null && !"".equals(referurl)) {
		// return "redirect:" + referurl;
		// }

		// 网站首页登录成功
		return checkToForwardUrl(request, "user/main", "forward");
		// return "user/main";
	}

	/**
	 * 功能:发送短信 ajax调用 参数: 1.手机号:USER_MOBILE 返回: 1.登录超时:login_timeout
	 * 2.验证多次:validate_timeout 3.发送成功:ok //PM新需求，点击发送一次最多验证三次。同一用户最多发送10次 modify
	 * by wenhua.cheng
	 */
	// 单独发短信
	@RequestMapping("/user/sendSms.do")
	public String sendSmsValidate(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String mobile = request.getParameter("USER_MOBILE");
		// 来源区分（日志所需）
		String loginRegSource = request
				.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
		try {
			String result = "";
			String smstemplate = Constant.SMS_REGIST_TEMPLATE;
			String sm = request.getParameter("smstemplate");
			if (sm != null && !"".equals(sm)) {
				smstemplate = sm;
			}
			Long uid = SingletonLoginUtils.getLoginUserid(request);
			// MobilePurseSecurityUtils.isJointMobileNumber(mobile);
			boolean flag = false;
			flag = userService.isUserExist(mobile, null);
			if (flag) {
				result = "no";
				if ("TRX".equals(loginRegSource)) {
					/************************ 日志开始 ************************************/
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "t_mobile_falied");
					logMap.put("mobile", mobile);
					LogAction.printLog(logMap);
					/************************ 日志结束 ************************************/
				} else {

					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "u_mobile_falied");
					logMap.put("mobile", mobile);
					LogAction.printLog(logMap);

				}
				try {
					response.getWriter().write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				user.setMobile(mobile);
				// SingletonLoginUtils.addSingleton(user, userService,
				// user.getId()
				// + "", response, false, request);
			}
			// Map<String, String> map = (Map<String, String>)
			// request.getSession()
			// .getAttribute("REGIST_RANDOMNUMBER");
			Map<String, String> map = (Map<String, String>) memCacheService
					.get("REGIST_RANDOMNUMBER_NEW" + uid);
			String mcode = null;
			String vcode = "";
			int sendCount = 0; // 发送次数
			int validareCount = 0; // 单次校验次数默认为0
			if (map != null) {
				String mobileValidateCode = map.get(mobile);

				if (mobileValidateCode != null) {
					sendCount = Integer
							.parseInt(mobileValidateCode.split(":")[0]);
					vcode = mobileValidateCode.split(":")[1];

					if (sendCount > 10) { // pm新需求：一天之内同一个用户不管用什么手机号，短信发送不能超过10次

						result = "validate_timeout";
					} else {

						sendCount++;
						mcode = sendCount + ":" + vcode + ":" + validareCount;
						// 发送短信
						sendSmsValidate(mobile, request, smstemplate, mcode,
								user, response);
						result = "re_ok"; // 页面显示：再次发送成功

					}

					if ("TRX".equals(loginRegSource)) {
						/************************ 日志开始 ************************************/
						Map<String, String> logMap = LogAction.getLogMap(
								request, response);
						logMap.put("action", "t_mobile_send");
						logMap.put("mobile", mobile);
						LogAction.printLog(logMap);
						/************************ 日志结束 ************************************/
					} else {

						Map<String, String> logMap = LogAction.getLogMap(
								request, response);
						logMap.put("action", "u_mobile_send");
						logMap.put("mobile", mobile);
						LogAction.printLog(logMap);

					}

					response.getWriter().write(result);
					return null;

				}
			}
			// 发送短信
			sendSmsValidate(mobile, request, smstemplate, mcode, user, response);

			if ("TRX".equals(loginRegSource)) {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "t_mobile_send");
				logMap.put("mobile", mobile);
				LogAction.printLog(logMap);
				/************************ 日志结束 ************************************/
			} else {

				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_mobile_send");
				logMap.put("mobile", mobile);
				LogAction.printLog(logMap);
			}
			result = "ok";
			response.getWriter().write(result);
		} catch (Exception e) {
			e.printStackTrace();
			if ("TRX".equals(loginRegSource)) {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "t_mobile_falied");
				logMap.put("mobile", mobile);
				LogAction.printLog(logMap);
				/************************ 日志结束 ************************************/
			} else {
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_mobile_falied");
				logMap.put("mobile", mobile);
				LogAction.printLog(logMap);
			}
		}
		return null;
	}

	/**
	 * 用户中心短信
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user/sendUcenterSms.do")
	public String sendSmsUCenterValidate(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		String smstemplate = Constant.SMS_REGIST_TEMPLATE;
		String sm = request.getParameter("smstemplate");
		if (sm != null && !"".equals(sm)) {
			smstemplate = sm;
		}
		Long uid = SingletonLoginUtils.getLoginUserid(request);
		String mobile = request.getParameter("USER_MOBILE");

		boolean flag = false;
		try {
			flag = userService.isUserExist(mobile, null);
		} catch (UserException e1) {
			e1.printStackTrace();
			flag = false;
		}

		if (flag || StringUtils.isBlank(mobile)
				|| !MobilePurseSecurityUtils.isJointMobileNumber(mobile)
				|| uid == null) {
			result = "no";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		User user = SingletonLoginUtils.getMemcacheUser(request);
		Map<String, String> map = (Map<String, String>) memCacheService
				.get("REGIST_RANDOMNUMBER_" + uid);

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
		sendSmsUCenterValidate(mobile, request, smstemplate, mcode, user,
				response);
		try {
			result = "ok";
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送短信验证
	 * 
	 * @param mobile
	 * @param request
	 * @param sms
	 * @return
	 */
	private Map<String, String> sendSmsUCenterValidate(String mobile,
			HttpServletRequest request, String smsTemplate,
			String validateCode, User user, HttpServletResponse response) {
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
			sourceBean = new SmsInfo(mobile, content, SMS_TYPE, "0");
			smsMap = smsService.sendSms(sourceBean);
			// 设置到session里
			Map<String, String> map = new HashMap<String, String>();
			if (validateCode != null) {
				map.put(mobile, count + ":" + randomNumbers);
			} else {
				map.put(mobile, "1:" + randomNumbers);
			}

			// request.getSession().setAttribute("REGIST_RANDOMNUMBER", map);
			memCacheService.set("REGIST_RANDOMNUMBER_" + user.getId(), map);

			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);

			log.info("+++++++++++++++++++" + user.getMobile()
					+ "++++++++++++++");
			/** ********************************************* */
			if (smsMap == null) {
				smsMap = new HashMap<String, String>();
			}
			smsMap.put("validateCode", randomNumbers);
		}
		return smsMap;
	}

	/**
	 * 发送短信验证
	 * 
	 * @param mobile
	 * @param request
	 * @param sms
	 * @return //PM新需求，点击发送一次最多验证三次。同一用户最多发送10次 modify by wenhua.cheng
	 */
	private Map<String, String> sendSmsValidate(String mobile,
			HttpServletRequest request, String smsTemplate,
			String validateCode, User user, HttpServletResponse response) {
		Map<String, String> smsMap = null;

		int sendCount = 1; // 发送次数
		int validateCount = 0; // 单次发送的校验次数。发送一次校验数归零
		String vCode = "";
		String[] str = null;
		if (validateCode != null) {// validateCode 排列顺序为 发送次数：验证码：单次发送的校验次数
			str = validateCode.split(":");
			if (str != null && str.length == 3) {
				sendCount = Integer.parseInt(str[0]);
				vCode = str[1];
				// validateCount = Integer.parseInt(str[2]);
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
			// update by ye.tian 2011-11-23 要求注册每次发送验证码是相同的
			if (str == null || str.length != 3) {
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
			sourceBean = new SmsInfo(mobile, content, SMS_TYPE, "0");
			smsMap = smsService.sendSms(sourceBean);
			// 设置到session里
			Map<String, String> map = new HashMap<String, String>();
			if (validateCode != null) {
				map.put(mobile, sendCount + ":" + randomNumbers + ":"
						+ validateCount);
			} else {
				map.put(mobile, "1:" + randomNumbers + ":" + validateCount);
			}

			memCacheService.set("REGIST_RANDOMNUMBER_NEW" + user.getId(), map);
			// 先取出user，再setmobile add by wh.cheng

			user.setMobile(mobile);

			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);

			log.info("+++++++++++++++++++" + user.getMobile()
					+ "++++++++++++++");

			if (smsMap == null) {
				smsMap = new HashMap<String, String>();
			}
			smsMap.put("validateCode", randomNumbers);
		}
		return smsMap;
	}

	/**
	 * 功能:验证短信 请求: 1.验证码:VALIDATE_CODE 2.绑定手机号:mobile 3.验证后跳转页面:referurl 返回
	 * request 返回： 1.错误信息:ERRMSG USER_MOBILE_PARAM_ERROR: 用户手机格式有误
	 * USER_HAS_VALIDATE:用户已经验证了 USER_TIMEOUT：用户超时操作 VALIDATE_ERROR:验证码输入错误
	 * 
	 * 跳转页面: 1.错误页面：/user/validatesms 2.成功跳到主页面:/user/main 3.交易流程 验证sms后:跳转到
	 * referurl地址 // 交易流程中点击一次发送后校验三次不过则提示。
	 */
	@RequestMapping("/user/validateSms.do")
	public Object validateSms(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String loginRegSource = request
				.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);

		Map<String, String> map = null;
		// 验证码
		String validateCode = request
				.getParameter(Constant.URSER_VALIDATE_CODE);
		String mobile = request.getParameter("USER_BINDING_MOBILE");

		boolean ismobile = MobilePurseSecurityUtils.isJointMobileNumber(mobile);
		if (mobile == null || !ismobile) {
			request.setAttribute("ERRMSG", "USER_MOBILE_PARAM_ERROR");
			return checkToForwardUrl(request, "/user/validatesms", "forward");
			// return "/user/validatesms";
		}

		User user = getMemcacheUser(request);

		if (!mobile.equals(user.getMobile())) { // 防止第一次获取后，用验证码和新输入的手机号验证通过
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "VALIDATE_ERROR");
			return checkToForwardUrl(request, "/user/validatesms", "forward");

		}
		// 已经验证成功
		if (user.getMobile_isavalible() == 1) {
			request.setAttribute("ERRMSG", "USER_HAS_VALIDATE");
			return checkToForwardUrl(request, "user/loginsuccess", "forward");
			// return "user/loginsuccess";
		}
		// 加入抽奖流程手机验证 by janwen 2011.9.15
		if ("TRX".equals(loginRegSource) || "LOTTERY".equals(loginRegSource)) { // 交易流程短信校验差异化处理
			map = (Map<String, String>) memCacheService
					.get("REGIST_RANDOMNUMBER_NEW" + user.getId());
		} else {

			map = (Map<String, String>) memCacheService
					.get("REGIST_RANDOMNUMBER_" + user.getId());
		}
		log.info("++++++user:" + user + "->validateCode:" + validateCode
				+ "->map:" + map + "+++++++++++++++++++");
		if (user == null || validateCode == null || map == null) {
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "USER_TIMEOUT");
			return checkToForwardUrl(request, "/user/validatesms", "forward");
			// return "/user/validatesms";
		}

		String mobileCode = map.get(user.getMobile());
		String mc = null;
		if (mobileCode != null && validateCode != null) {
			mc = mobileCode.split(":")[1];
		} else {
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "VALIDATE_ERROR");
			return checkToForwardUrl(request, "/user/validatesms", "forward");
		}

		if ("TRX".equals(loginRegSource)) { // 交易流程短信校验差异化处理
			int validateCount = Integer.parseInt(mobileCode.split(":")[2]);
			if (validateCount > 1) {// 如果单次校验大于3，则提示校验次数大于2，必须重新单击发送
				request.setAttribute(Constant.USER_ERROR_MESSAGE,
						"VALIDATECOUNT_LIMITED");
				return checkToForwardUrl(request, "/user/validatesms",
						"forward");
			}
		}

		// try{
		// 验证错误 要更新session中的 手机号 对应验证错误次数
		log.info("+++++++validateCode:" + validateCode);
		log.info("+++++++mobileCode:" + mobileCode);
		log.info("++++++++user.getMobile():" + user.getMobile()
				+ "->+++user.id:" + user.getId());
		log.info("+++++++++++++requestMobible:" + mobile);
		if (!validateCode.equals(mc)) {
			if ("TRX".equals(loginRegSource)) {
				int sendCount = Integer.parseInt(mobileCode.split(":")[0]);
				int validateCount = Integer.parseInt(mobileCode.split(":")[2]);

				validateCount++;
				HashMap<String, String> validateMap = new HashMap<String, String>();
				validateMap.put(user.getMobile(), sendCount + ":" + mc + ":"
						+ validateCount);// 更新单次校验错误次数
				memCacheService.set("REGIST_RANDOMNUMBER_NEW" + user.getId(),
						validateMap); // 更新memcache
			}
			request.setAttribute(Constant.USER_ERROR_MESSAGE, "VALIDATE_ERROR");
			return checkToForwardUrl(request, "/user/validatesms", "forward");
		}

		user = userService.findById(user.getId());
		// 更新用户验证状态
		try {
			user.setMobile(mobile);
			userService.activationMobile(user, mobile);
		} catch (UserException e) {
			e.printStackTrace();
		}
		if (user != null) {

			if ("TRX".equals(loginRegSource)) {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "MobileAuthInTrx");// 交易流程手机认证 日志所需
				LogAction.printLog(logMap);

				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_mobile");// 日志埋点2.0
				logMap2.put("mobile", user.getMobile());
				LogAction.printLog(logMap2);
				/************************ 日志结束 ************************************/
			} else {

				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_mobile");
				logMap.put("mobile", user.getMobile());
				LogAction.printLog(logMap);

			}

		}

		String referurl = (String) request.getSession()
				.getAttribute("referurl");
		if (referurl != null && !"".equals(referurl)) {
			return "forward:" + referurl;
		}
		// 默认验证完短信跳到首页

		user.setMobile_isavalible(1);
		SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
				response, false, request);

		return checkToForwardUrl(request, "/user/useraccount", "forward");

	}

	/**
	 * 功能:用户登录 请求参数: 1.手机号或者邮箱 USER_LOGIN_USERNAME 2.密码 USER_PASSWORD
	 * 3.交易流程的话需要把那一页的地址传给我 返回: 1.错误信息 ERRMSG USERNAME_PARAM_ERROR: 用户名格式有误
	 * USER_NOT_EXIST: 用户不存在 PASSWORD_PARAM_ERROR: 密码格式有误 VALIDATECODE_ERROR:
	 * 验证码验证有误 USERNAME_OR_PASSWORD_ERROR: 登录失败，用户名和密码不匹配
	 */
	// 用户登录
	@RequestMapping("/user/userLogin.do")
	public Object userLogin(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String clientIp = WebUtils.getIpAddr(request);
		try {
			replaceScript(request);
		} catch (Exception e1) {
			return "redirect:http://www.qianpin.com/jsp/user/login.jsp";
		}
		Integer c = ipCount.get(clientIp);
		if (c == null) {
			c = 0;
		}
		if (c > Integer.MAX_VALUE - 100) {
			c = Integer.MAX_VALUE / 2;
		}
		c++;
		ipCount.put(clientIp, c);
		if (ipCount.size() > 16000) {
			Collection<Integer> counts = ipCount.values();
			List<Integer> list = new ArrayList<Integer>(counts);
			Collections.sort(list);
			for (int i = 0; i < list.size() && i < 6000; i++) {
				for (Map.Entry<String, Integer> entry : ipCount.entrySet()) {
					Integer value = entry.getValue();
					if (value == list.get(i)) {
						log.info("释放IP:" + entry.getKey() + ":"
								+ entry.getValue());
						ipCount.remove(entry.getKey());
					}
				}
			}
		}
		log.info("access_times ip=" + clientIp + ",times=" + c);
		if (c > 20) {
			request.setAttribute("ERRMSG", "VALIDATECODE_ERROR");
			request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			log.info("sb 输入验证码去吧" + ",ip=" + clientIp + ",times=" + c
					+ ",all ip=" + ipCount.size());
			return checkToForwardUrl(request, "user/login", "forward");
		}

		String loginRegSource = request
				.getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
		// 客户端ip

		// 假如验证码不为空 说明已经输入过错误密码至少三次 验证验证码
		String validCode = request.getParameter("validCode");
		if (validCode != null) {
			validCode = validCode.trim();
		}
		Integer times = (Integer) memCacheService.get("LOGIN_IP_" + clientIp);

		if (times != null) {
			if (times != null && times > 3 && StringUtils.isBlank(validCode)) {
				log.info("ip=" + clientIp + ",times=" + times + ",");
				validCode = "123456";
			}
		}
		if (validCode != null) {
			String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
					request);
			request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			if (cookieCode != null) {
				String sessionCode = (String) memCacheService.get("validCode_"
						+ cookieCode);
				memCacheService.remove("validCode_" + cookieCode);
				if (!validCode.equalsIgnoreCase(sessionCode)
						|| sessionCode == null) {
					request.setAttribute("ERRMSG", "VALIDATECODE_ERROR");
					request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
					return checkToForwardUrl(request, "user/login", "forward");
					// return "user/login";
				}
			} else {
				request.setAttribute("ERRMSG", "VALIDATECODE_ERROR");
				log.info("ip=" + clientIp + ",times=" + times
						+ ",VALIDATECODE_ERROR");
				return checkToForwardUrl(request, "user/login", "forward");
			}
			// String sessionCode = (String) request.getSession().getAttribute(
			// "validCode");
			// if (!validCode.equals(sessionCode)) {
			// request.setAttribute("ERRMSG", "VALIDATECODE_ERROR");
			// request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			// return checkToForwardUrl(request, "user/login","forward");
			// // return "user/login";
			// }
		}
		// 判断假如用户输入格式有误跳转页面
		String paramerror = (String) request
				.getAttribute(Constant.USER_PARAM_VALIDATE_ERROR);
		if ("true".equals(paramerror)) {
			request.setAttribute("ERRMSG", "USERNAME_PARAM_ERROR");

			return checkToForwardUrl(request, "user/login", "forward");
			// return "user/login";
		}

		// 密码格式
		String userpassworderror = (String) request
				.getAttribute(Constant.USER_PASSWROD_VALIDATE_ERROR);
		if ("true".equals(userpassworderror)) {
			request.setAttribute("ERRMSG", "PASSWORD_PARAM_ERROR");
			return checkToForwardUrl(request, "user/login", "forward");
			// return "user/login";
		}

		String username = request.getParameter(Constant.USER_LOGIN_USERNAME);
		String password = request.getParameter("USER_PASSWORD");

		String mobile = "";
		String useremail = "";
		String isemail = (String) request.getAttribute("isemail");
		if ("true".equals(isemail)) {
			useremail = username;
		} else {
			mobile = username;
		}
		User user = null;
		try {
			user = userService.isUserLogin(mobile, password, useremail);
		} catch (UserException e) {
			// e.printStackTrace();
			// 用户登录失败 同一IP记录登录次数
			request.setAttribute(Constant.USER_ERROR_MESSAGE,
					"USERNAME_OR_PASSWORD_ERROR");

			// Map<String, Integer> map = (Map<String, Integer>) request
			// .getSession().getAttribute("LOGIN_IP");
			if (times == null) {
				times = 0;
			}
			++times;
			// 大于等于三次开启验证码
			if (times >= 3) {
				request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			}
			// 一天超时
			memCacheService.set("LOGIN_IP_" + clientIp, times, 60 * 5);
			// request.getSession().setAttribute("LOGIN_IP", map);
			// 登录页面

			// return "user/login";
			if (!"TRX".equals(loginRegSource)) {
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_login_failed");
				logMap.put("error", e.getCode() + "");
				LogAction.printLog(logMap);
			} else {
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "t_login_failed");
				logMap.put("error", e.getCode() + "");
				LogAction.printLog(logMap);

			}
			return checkToForwardUrl(request, "user/login", "forward");

		}

		// request.getSession().setAttribute(Constant.USER_LOGIN, user);
		if (user != null) {

			//记录每次用户登陆
			UserLoginLog ulLog = new UserLoginLog();
			ulLog.setUserid(user.getId());
			ulLog.setUserEmail(user.getEmail());
			ulLog.setLoginIp(WebUtils.getIpAddr(request));
			try {
				userService.addLoginLog(ulLog);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			SingletonLoginUtils.addSingleton(user, userService, user.getId()
					+ "", response, false, request);
			// 0元抽奖 add by qiaowb 2012-06-04
			if ("LOTTERY".equals(loginRegSource)) {
				// 打印日志
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "h_login");
				logMap2.put("uid", user.getId() + "");
				LogAction.printLog(logMap2);
			} else if (!"TRX".equals(loginRegSource)) {
				// 打印日志
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "u_login");// 交易流程登录 日志所需
				logMap.put("uid", user.getId() + "");
				LogAction.printLog(logMap);
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "UserLoginInSite");
				LogAction.printLog(logMap2);
			} else {
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap.put("action", "UserLoginInTrx");
				LogAction.printLog(logMap);

				logMap2.put("action", "t_login");// 交易流程登录 日志所需
				logMap2.put("uid", user.getId() + "");
				LogAction.printLog(logMap2);
				/************************ 日志结束 ************************************/
			}

			Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user
					.getId());
			request.setAttribute("QIANPIN_USER", user);
			if (weiboNames != null) {
				request.setAttribute("WEIBO_NAMES", weiboNames);
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
			}

		}

		request.setAttribute("USER_LOGIN_EMAIL", useremail);

		// 判断假如用户没有记录mobile跳转到输入手机页面
		// String trxregist = request.getParameter("trxregist");
		// String referurl = request.getParameter("referurl");
		// if (trxregist != null && "trxregist".equals(trxregist)) {
		// request.getSession().setAttribute("referurl", referurl);
		// // 假如是交易流程注册需要跳转到 checkMobile.do 检验用户
		// request.setAttribute("trxregist", trxregist);
		// return "forward:user/checkMobile.action";
		// }
		// if (referurl != null && !"".equals(referurl)) {
		//
		// return "redirect:" + referurl;
		// }
		// String loginRegSource = (String) request
		// .getParameter(Constant.USER_LOGIN_REGISTER_SOURCE_TYPE);
		// // 如果是交易来的，则校验手机
		// if ("TRX".equals(loginRegSource)) {
		// checkMobile(model, request);
		// }

		// String requesturl=request.getServletPath();
		// String host=propertyUtil.getProperty("host");
		// String forwardUrl=host+requesturl;

		// 交易流程中加入用户类型COOkies
		// //如果是手机号校验通过的用户，置Cookies为空；否则就是没有校验过手机的（能从登录入口进来的都当作是非新用户处理）

		if (1 != user.getMobile_isavalible()) { //
			TrxUtils.addTrxCookies(response, request,
					TrxConstant.TRX_LOGIN_TYPE + user.getId(),
					TrxConstant.TRX_LOGIN_TYPE_MOBILE_VALIDATE);
		} else {
			TrxUtils.removeTrxCookies(response, request,
					TrxConstant.TRX_LOGIN_TYPE + user.getId());
		}

		return checkToForwardUrl(request, "user/useraccount", "redirect");
		// return "user/useraccount";
	}

	public static Map<String, Integer> ipCount = new java.util.concurrent.ConcurrentHashMap<String, Integer>(
			16 * 1024, 0.75f);

	@RequestMapping("/user/isUserExist.do")
	public String isUserExist(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = null;
		String ip = WebUtils.getIpAddr(request);
		// if ("183.149.209.62".equals(ip)) {
		// log.info("你个SB,已经给我攻破了,想要数据的话,给我发邮件:liranshuai@126.com");
		// pw.write("你个SB,已经给我攻破了,想要数据的话,给我发邮件:liranshuai@126.com");
		// return null;
		// }
		String str = "";
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			str = "error";
			pw.write(str);
			return null;
		}
		if (1 == 1) {
			pw.write("no");
			return null;
		}

		// 给SB黑客用的
		String email = request.getParameter("EMAIL_REGIST");
		String bi_gsid = WebUtils.getCookieValue("bi_gsid", request);
		if (StringUtils.isBlank(bi_gsid)) {
			log.info("ip:" + ip + "email:" + email + ",return no");
			pw.write("no");
			return null;
		}
		Integer count = ipCount.get(ip);
		if (count == null) {
			count = 0;
		}
		if (count > Integer.MAX_VALUE - 100) {
			count = Integer.MAX_VALUE / 2;
		}
		count++;
		ipCount.put(ip, count);
		if (count > 10) {
			log.info("ip:" + ip + "email:" + email + ",return no,count="
					+ count);
			pw.write("no");
			return null;
		}
		if (ipCount.size() > 16000) {
			Collection<Integer> counts = ipCount.values();
			List<Integer> list = new ArrayList<Integer>(counts);
			Collections.sort(list);
			for (int i = 0; i < list.size() && i < 6000; i++) {
				for (Map.Entry<String, Integer> entry : ipCount.entrySet()) {
					Integer value = entry.getValue();
					if (value == list.get(i)) {
						log.info("释放IP:" + entry.getKey() + ":"
								+ entry.getValue());
						ipCount.remove(entry.getKey());
					}
				}
			}
		}

		boolean isEmail = false;
		if (email == null && "".equals(email)) {
			isEmail = true;
			str = "null";
			pw.write(str);
			return null;
		}
		String mobile = request.getParameter("mobile");
		if (mobile == null && "".equals(mobile)) {
			str = "null";
			pw.write(str);
			return null;
		}
		boolean flag = false;
		try {
			String type = request.getParameter("type");
			if ("bangding".equals(type)) {
				// type=bangding时逻辑是反向的
				boolean f = MobilePurseSecurityUtils.isJointMobileNumber(email);
				if (f) {
					flag = userService.isUserExist(email, null);
				} else {
					flag = userService.isUserExist(null, email);
				}
				flag = !flag;
			} else {
				if (isEmail) {
					flag = userService.isUserExist(null, email);
				} else {
					flag = userService.isUserExist(mobile, email);
				}
			}
		} catch (UserException e) {
			e.printStackTrace();
			log.info(e);
			if (e.getCode() == 2002) {
				str = "no";
			} else {
				str = "error";
			}
			pw.write(str);
			return null;
		}
		if (!flag)
			str = "no";
		else
			str = "yes";
		pw.write(str);

		return null;
	}

	// ///////////////////////以下留做纪念---！！！做毛纪念啊，我一上来就改到这个方法。结果发现页面没生效。。改错位置了。nnd。
	// 2011.5.11////////////////////////////////////////////////////////

	// 注册和登录验证短信

	// 正常注册，下一步需要验证手机号
	// @RequestMapping("user/regist.do")
	// public String regist(ModelMap model, HttpServletRequest request) {
	// // 邮箱注册先判断验证码
	// String userregistcode = (String) request
	// .getAttribute(Constant.USER_REGIST_CODE);
	// if ("true".equals(userregistcode)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "验证码错误!");
	//
	// return "/user/reg_mobile";
	// }
	//
	// // 判断假如用户输入格式有误跳转页面
	// String paramerror = (String) request
	// .getAttribute(Constant.USER_PARAM_VALIDATE_ERROR);
	// if ("true".equals(paramerror)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "用户输入信息格式有误!");
	// return "/user/reg_mobile";
	// }
	//
	// String userpassworderror = (String) request
	// .getAttribute(Constant.USER_PASSWROD_VALIDATE_ERROR);
	// if ("true".equals(userpassworderror)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "密码格式有误!");
	// return "/user/reg_mobile";
	// }
	//
	// // 判断假如用户已经存在跳转页面
	// String userExist = (String) request.getAttribute(Constant.USER_EXIST);
	// if ("true".equals(userExist)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "用户已经存在!");
	// return "/user/reg_mobile";
	// }
	//
	// String mobile = request.getParameter(Constant.USER_MOBILE_REGIST);
	// String email = request.getParameter(Constant.USER_EMAIL_REGIST);
	// // 假如mobile、email 没有则跳到错误页面
	// if (mobile == null || "".equals(mobile)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "手机号不能为空!");
	// return "/user/reg_mobile";
	// }
	// if (email == null || "".equals(email)) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "Email不能为空!");
	// return "/user/reg_mobile";
	// }
	// String password = request.getParameter(Constant.USER_PASSWORD);
	// // 用户注册初始状态手机号 email
	// try {
	// userService.addUser(email, mobile, password);
	// } catch (UserException e) {
	// log.info(e);
	// // 设置错误信息
	// // request.setAttribute(Constant.USER_ERROR_MESSAGE,e.getMessage());
	// return "/user/reg_mobile";
	// }
	//
	// User user = null;
	// try {
	// user = userService.findUserByEmail(email);
	// } catch (UserException e) {
	// e.printStackTrace();
	// }
	// // 用户状态存放到session里
	// request.getSession().setAttribute(Constant.USER_LOGIN, user);
	//
	// // 判断发送验证码次数
	// Map<String, String> map = (Map<String, String>) request.getSession()
	// .getAttribute("REGIST_RANDOMNUMBER");
	// // Map<String, String> map =
	// // memCacheService.get("REGIST_RANDOMNUMBER_"+user.getId());
	// // String mcode=null;
	// if (map != null) {
	// // 去除session属性
	// request.getSession().removeAttribute("REGIST_RANDOMNUMBER");
	// // String mobileValidateCode=map.get(mobile);
	// // if(mobileValidateCode!=null){
	// // mcode=mobileValidateCode;
	// // mcode=mobileValidateCode;
	// // String count=mobileValidateCode.split(":")[0];
	// // vcode=mobileValidateCode.split(":")[1];
	// // int fcount=Integer.parseInt(count);
	// // if(fcount>=3){
	// // request.setAttribute(Constant.USER_ERROR_MESSAGE, "验证码验证超过3次!");
	// // return "/user/reg_mobile";
	// // }
	// // fcount++;
	// // mcode=fcount+":"+vcode;
	// // map.put(user.getMobile(),mcode);
	// // request.getSession().setAttribute("REGIST_RANDOMNUMBER", map);
	// // }
	// }
	//
	// // 短信模板转换 短信内容
	// // 发送短信，验证手机
	// Map<String, String> smsMap = null;
	// // TODO:短信模板内容设置
	// // 注册校验码 发送
	// smsMap = sendSmsValidate(mobile, request, Constant.SMS_REGIST_TEMPLATE,
	// null);
	//
	// // 跳转到短信验证页面
	// return "/user/reg_1_mobile";
	// }
	//
	// // 登录 没有输入手机号 发送短信
	// @RequestMapping("user/loginSendSms.do")
	// public String loginSendSms(ModelMap model, HttpServletRequest request) {
	// String mobile = request.getParameter("MOBILE_LOGIN");
	// String tempemail = request.getParameter("USER_LOGIN_EMAIL");
	// request.setAttribute("USER_LOGIN_EMAIL", tempemail);
	// boolean isMobile = MobilePurseSecurityUtils.isJointMobileNumber(mobile);
	// if (!isMobile) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "手机号码有误!");
	// return "user/login_mobile";
	// }
	//
	// // 判断用户名 mobile是否存在
	// boolean isExist = true;
	// try {
	// isExist = userService.isUserExist(mobile, null);
	// } catch (UserException e) {
	// isExist = false;
	// }
	// // 手机号存在
	// if (isExist) {
	// request.setAttribute(Constant.USER_ERROR_MESSAGE, "手机号码已经存在!");
	// return "user/login_mobile";
	// }
	// String validCode = request.getParameter("validCode");
	// if (validCode != null) {
	// String sessionCode = (String) request.getSession().getAttribute(
	// "validCode");
	// if (!validCode.equals(sessionCode)) {
	//
	// request.setAttribute("CODE_ERRMSG", "验证码错误!");
	// return "user/login_mobile";
	// }
	// }
	//
	// request.setAttribute("login_mobile_param", mobile);
	// // 发送验证短信
	// // Map<String,String> map=(Map<String, String>)
	// // request.getSession().getAttribute("REGIST_RANDOMNUMBER");
	// // int fcount=0;
	// // String mcode=null;
	// // String vcode="";
	// // if(map==null){
	// // map=new HashMap<String,String>();
	// // }else{
	// // String mobileValidateCode=map.get(mobile);
	// // if(mobileValidateCode!=null){
	// // // mcode=mobileValidateCode;
	// // String count=mobileValidateCode.split(":")[0];
	// // vcode=mobileValidateCode.split(":")[1];
	// // fcount=Integer.parseInt(count);
	// // if(fcount>=3){
	// // request.setAttribute(Constant.USER_ERROR_MESSAGE, "验证码验证超过3次!");
	// // return "user/login";
	// // }
	// // }
	// // }
	// // fcount++;
	// // mcode=fcount+":"+vcode;
	// // 发送短信
	// Map<String, String> retCode = sendSmsValidate(mobile, request,
	// Constant.SMS_REGIST_TEMPLATE, null);
	// // map.put(mobile, fcount+":"+retCode.get("validateCode"));
	// // request.getSession().setAttribute("REGIST_RANDOMNUMBER", map);
	// User user = (User) request.getSession().getAttribute(
	// Constant.USER_LOGIN);
	// user.setMobile(mobile);
	// request.setAttribute(Constant.USER_LOGIN, user);
	//
	// return "user/login_mobile_1";
	// }

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

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}

	public TrxorderGoodsDao getTrxorderGoodsDao() {
		return trxorderGoodsDao;
	}

	public void setTrxorderGoodsDao(TrxorderGoodsDao trxorderGoodsDao) {
		this.trxorderGoodsDao = trxorderGoodsDao;
	}

	public void setVmAccountService(VmAccountService vmAccountService) {
		this.vmAccountService = vmAccountService;
	}

	public VmAccountService getVmAccountService() {
		return vmAccountService;
	}
}
