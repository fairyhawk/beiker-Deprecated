package com.beike.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title:wap所有的请求路径
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

public class MUrlConstant {
	private static String URL_CONSTANT = "com.beike.util.MUrlConstant";
	private static String PREFIX_NAME = "M_USER_";
	private static String NOTCHECK_PREFIX_NAME = "M_NOTCHECK_";
	// 用户需要登录检测的URL 检测完后跳转到用户中心
	public static final String M_USER_CHECK_LOGIN = "M_USER_CHECK_LOGIN";
	public static final String M_USER_NOT_CHECK_LOGIN = "M_USER_NOT_CHECK_LOGIN";
	///////////////////前端地址///////USER开头登录过滤///////////////////////////

	/** 商品购买 */
	public String M_USER_TRX_BUY = "/wap/goods/goodsBuyController.do";
	/** 用户中心-我的千品 */
	public String M_USER_CENTER = "/wap/mUserCenter.do";

	// /////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////不需要过滤地址//////////////////////////////////////////////////

	/** wap跳转登陆页面前数据处理action */
	public String M_NOTCHECK_TO_USER_LOGIN = "/wap/user/toUserLogin.do";

	/** wap用户登陆action */
	public String M_NOTCHECK_USER_LOGIN = "/wap/user/mUserLogin.do";
	
	/** 跳转到登录 */
	public String M_NOTCHECK_TO_LOGIN = "/wap/user/toUserLogin.do";

	/** 登录 */
	public String M_NOTCHECK_LOGOUT = "/wap/user/logout.do";
	
	/** 跳转到注册 */
	public String M_NOTCHECK_TO_REGIST = "/wap/user/toRegist.do";
	
	/** 注册 */
	public String M_NOTCHECK_USER_REGIST = "/wap/user/userRegist.do";
	
	/** wap手机注册验证 */
	public String M_NOTCHECK_WAP_VALIDATE = "/wap/v.do";
	
	/** 验证成功 */
	public String M_NOTCHECK_VALIDATE_SUCCESS = "/wap/validateSuccess.do";
	
	/** 商品分类查询 */
	public String M_NOTCHECK_GOODS_QUERY = "/wap/goods/goodsQueryController.do";
	
	/** 商品首页 */
	public String M_NOTCHECK_GOODS_INDEX = "/wap/goods/goodsIndexController.do";
	
	/** 商品详情 */
	public String M_NOTCHECK_GOODS_DETAIL = "/wap/goods/goodsDetailController.do";
	
	/** 优惠卷分类查询 */
	public String M_NOTCHECK_COUPON_QUERY = "/wap/coupon/couponQueryController.do";
	
	/** 优惠卷首页 */
	public String M_NOTCHECK_COUPON_INDEX = "/wap/coupon/couponIndexController.do";
	
	/** 优惠卷详情 */
	public String M_NOTCHECK_COUPON_DETAIL = "/wap/coupon/couponDetailController.do";
	
	/** 品牌分类查询 */
	public String M_NOTCHECK_BRAND_QUERY = "/wap/brand/brandQueryController.do";
	
	/** 品牌首页 */
	public String M_NOTCHECK_BRAND_INDEX = "/wap/brand/brandIndexController.do";
	
	/** 品牌详情 */
	public String M_NOTCHECK_BRAND_DETAIL = "/wap/brand/brandDetailController.do";
	
	/** 城市选择跳转 */
	public String M_NOTCHECK_CITY_FORWARD = "/wap/city/mForward.do";
	
	/** 选择城市 */
	public String M_NOTCHECK_CITY_CHOOSE = "/wap/city/mCityChoose.do";
	
	
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
				Set<String> loginFilter = new HashSet<String>();
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
				map.put(M_USER_CHECK_LOGIN, set);
				map.put(M_USER_NOT_CHECK_LOGIN, notfilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
