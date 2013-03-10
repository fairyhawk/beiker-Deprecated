package com.beike.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title:所有的请求路径
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
 * @date Jun 13, 2011
 * @author ye.tian
 * @version 1.0
 */

public class UrlConstant {
	private static String URL_CONSTANT = "com.beike.util.UrlConstant";
	private static String PREFIX_NAME = "USER_";
	private static String NOTCHECK_PREFIX_NAME = "NOTCHECK_";
	// 用户需要登录检测的URL 检测完后跳转到用户中心
	private static String USER_CHECK_LOGIN = "USER_CHECK_LOGIN";
	private static String USER_NOT_CHECK_LOGIN = "USER_NOT_CHECK_LOGIN";
	// /////////////////前端地址///////USER开头登录过滤///////////////////////////

	// 用户中心
	public String USER_USER_ACCOUNT = "/forward.do?param=useraccount";

	// 用户修改手机
	public String USER_UPDATE_MOBILE = "/forward.do?param=updateMobile";

	// 用户修改email
	public String USER_UPDATE_EMAIL = "/forward.do?param=updateEmail";

	// 用户修改密码
	public String USER_UPDATE_PASSWORD = "/forward.do?param=updatePassword";
	// 用户注销
	public String USER_LOGOUT = "/user/logout.do";
	// 发送邮件 激活
	public String USER_SEND_EMAIL = "/user/sendEmail.do";

	public String USER_GO_TO_PAY = "/pay/goToPay.do";

	public String USER_UCENTER_PURSE = "/ucenter/showPurse.do";
	public String USER_UCENTER_REBATE = "/ucenter/showRebate.do";
	public String USER_PAY_SUC = "/pay/paySuccess.do";// 支付成

	public String USER_SHOW_TRXORDER = "/ucenter/showTrxGoodsOrder.do";
	public String USER_SEND_VOUHCER = "/ucenter/sendVoucher.do";
	public String USER_REFUND_APPLY = "/ucenter/refundApply.do";
	public String USER_COMMENT_GOODS = "/ucenter/commentGoods.do";
	public String USER_PRINT_VOUCHER = "/ucenter/printVoucher.do";
	//public String USER_JUMP_CARD = "/ucenter/toCard.do";
	public String USER_QUERY_CARD = "/ucenter/queryCard.do";
	public String USER_UPDATE_CARD = "/ucenter/updateCard.do";

	// /////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////不需要过滤地址//////////////////////////////////////////////////

	// 邮件确认修改密码
	public String NOTCHECK_FORWARD_CONFIRMPASSWORD = "forwardConfirmResetPassword.do";

	public String NOTCHECK_FORWARD_PASSWORD = "/forward.do?param=forgetpassword";
	// 跳到注册页面
	public String NOTCHECK_REGIST = "/forward.do?param=regist";

	// 重置密码
	public String NOTCHECK_RESET_PASSWORD = "/user/resetPassword.do";
	// 忘记密码
	public String NOTCHECK_FORGET_PASSWORD = "/user/forgetPassword.do";
	// 验证码
	public String NOTCHECK_VALIDATE_CODE = "/user/service.do";

	// 跳转到登录页
	public String NOTCHECK_LOGIN_JSP = "/forward.do?param=login";
	// 登录
	public String NOTCHECK_USER_LOGIN = "/user/userLogin.do";

	// 品牌搜索
	public String NOTCHECK_BRAND_SEARCH = "/brand/searchBrandsByProperty.do";
	// 品牌主页搜索
	public String NOTCHECK_BRAND_MAIN_SEARCH = "/brand/mainSearchBrandsByProperty.do";
	// 品牌详情
	public String NOTCHECK_BRAND_DETAIL = "/brand/showMerchant.do";
	// 品牌详情地图
	public String NOTCHECK_BRAND_MAP = "/brand/getMerchantMapList.do";

	// 优惠券搜索
	public String NOTCHECK_COUPON_SEARCH = "/coupon/searchCouponByProperty.do";
	// 优惠券详情
	public String NOTCHECK_COUPON_DETAIL = "/coupon/getCouponById.do";
	// 优惠券详情地图
	public String NOTCHECK_COUPON_MAP = "/coupon/getMerchantMapList.do";
	// 优惠券主页搜索
	public String NOTCHECK_COUPON_MAIN_SEARCH = "/coupon/mainSearchCouponByProperty.do";
	// 优惠券下载
	public String NOTCHECK_COUPON_DOWANLOAD = "/coupon/downloadCoupon.do";

	// 商品搜索
	public String NOTCHECK_GOODS_SEARCH = "/goods/searchGoodsByProperty.do";
	// 商品详情
	public String NOTCHECK_GOODS_DETAIL = "/goods/showGoodDetail.do";
	// 商品详情地图
	public String NOTCHECK_GOODS_MAP = "/goods/getGoodMapList.do";
	// 商品主页搜索
	public String NOTCHECK_GOODS_MAIN_SEARCH = "/goods/mainSearchGoodsByProperty.do";
	// 商品搜索商家
	public String NOTCHECK_GOODS_MERCHANT = "/goods/getGoodMerchant.do";

	public String NOTCHECK_BIND_SINA = "/user/bindSinaWeiboAccount.do";
	public String NOTCHECK_AUTH_SINA = "/user/sinaAuthorization.do";
	public String NOTCHECK_SINAUSER = "/user/getSinaUserinfo.do";

	public String NOTCHECK_BIND_XIAONEI = "/user/xiaoneiBindAccount.do";
	public String NOTCHECK_AUTH_XIAONEI = "/user/xiaoneiAuthorization.do";

	public String NOTCHECK_SHOPPINGCART = "/pay/shoppingCart.do";

	public String NOTCHECK_PAYCALLBACK = "/pay/payCallback.do";

	public String NOTCHECK_SENDSMS = "/user/sendSms.do";

	public String NOTCHECK_VALIDATESMS = "/user/validateSms.do";

	// 用户注册
	public String NOTCHECK_REGIST_USER = "/user/userRegist.do";

	// /////////////////////////////////////////////////////////////////////////////
	/**
	 * 获得所有请求路径
	 */
	public static Map<String, Set<String>> getRequestUrl() {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		try {
			Class clazz = Class.forName(URL_CONSTANT);
			Object obj = clazz.newInstance();
			Field fields[] = clazz.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				Set<String> set = new HashSet<String>();
				Set<String> notfilter = new HashSet<String>();
				for (Field field : fields) {
					// USER_开头的 都需要检测登录 并且跳到用户中心
					field.setAccessible(true);
					String objValue = (String) field.get(obj);
					if (field.getName().startsWith(PREFIX_NAME)) {
						set.add(objValue);
					} else if (field.getName().startsWith(NOTCHECK_PREFIX_NAME)) {
						notfilter.add(objValue);
					}
					field.setAccessible(false);
				}
				map.put(USER_CHECK_LOGIN, set);
				map.put(USER_NOT_CHECK_LOGIN, notfilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
