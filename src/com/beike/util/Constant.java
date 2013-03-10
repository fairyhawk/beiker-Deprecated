package com.beike.util;

/**
 * <p>
 * Title: 全局变量
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
 * @date Apr 25, 2011
 * @author ye.tian
 * @version 1.0
 */

public class Constant {
	/**
	 * 交易流程中的注册/登陆转发地址 add by wenhua.cheng
	 */
	public static final String USER_TRX_LOGIN_REGISTER = "/pay/shoppingCart.do";
	/**
	 * 登陆/注册来源类型 add by wenhua.cheng
	 */
	public static final String USER_LOGIN_REGISTER_SOURCE_TYPE = "USER_LOGIN_REGISTER_SOURCE_TYPE";

	/**
	 * 修改手机短信模版 by janwen
	 */

	public static final String USER_MOBILE_UPDATE = "USER_MOBILE_UPDATE";
	
	
	/**
	 * 交易UUID_TOKEN
	 */
	public static final String UUID_TOKEN_KEY = "UUID_TOKEN_KEY";

	// 用户登录
	public static final String USER_LOGIN = "BEIKER_USER_LOGIN";

	public static final String MEMCACHE_USER_LOGIN = "USER_LOGIN_";

	// 用户错误信息
	public static final String USER_ERROR_MESSAGE = "ERRMSG";

	// 用户已经存在
	public static final String USER_EXIST = "USEREXIST";

	// 页面图片前缀
	public static final String UPLOAD_IMAGES_URL = "/jsp/uploadimages/";

	// 注册用户名格式错误
	public static final String USER_PARAM_VALIDATE_ERROR = "USER_PARAM_ERROR";

	// 用户密码格式有误
	public static final String USER_PASSWROD_VALIDATE_ERROR = "USER_PARAM_PASSWORD_ERROR";

	// 属性文件配置名称
	public static final String PROPERTY_FILE_NAME = "project";

	//
	public static final String MERCHANT_LOGO1 = "mc_logo1";

	// 销售量
	public static final String SALES_COUNT = "SALES_COUNT";

	// /////////////////////邮件相关参数配置/////////////////////////////////////////
	// 验证邮件地址
	public static final String EMAIL_VALIDATE_URL = "emailValidateUrl";
	// 邮件主机
	public static final String EMAIL_HOST_CONFIG = "emailhost";
	// 官方邮箱用户名 用于发送邮件
	public static final String EMAIL_USERNAME = "emailusername";
	// 官方邮件密码
	public static final String EMAL_PASSWORD = "emailpassword";
	// 邮件内容类型
	public static final String EMAIL_CONTENTTYPE = "contentType";

	// 使用注册激活邮件模板类型
	public static final String EMAIL_VALIDATE_TEMPLATE = "REGIST_VALIDATE";

	/** wap注册邮件通知模板 */
	public static final String WAP_REGIST_EMAIL_TEMPLATE = "WAP_REGIST_EMAIL";
	// //////////////////////////////////////////////////////////////
	// 用户emal 中的链接key 存入userprofile的name
	public static final String EMAIL_REGIST_URLKEY = "URLKEY";

	// 注册短信模板 名称
	public static final String SMS_REGIST_TEMPLATE = "SMSREGIST";
	
	//忘记密码短信模版 名称
	public static final String SMS_MOBILE_FORGETPASSWORD = "SMS_MOBILE_FORGETPASSWORD";

	
	
	public static final String SMS_BOOKING_SUCCESS_MESSAGE = "SMS_BOOKING_SUCCESS_MESSAGE";
	
	public static final String SMS_BOOKING_CONFIRMED_MESSAGE = "SMS_BOOKING_CONFIRMED_MESSAGE";
	/**
	 * 凭证短信下发平台模板 add by wenhua.cheng
	 */
	public static final String SMS_VOUCHER_DISPATCH = "VOUCHERDISPATCH";
	/**
	 * 点菜单凭证短信下发模板 add by renli.yu
	 */
	public static final String SMS_MENU_VOUCHER_DISPATCH = "SMS_MENU_VOUCHER_DISPATCH";
	
	/**
	 * 凭证短信下发淘宝模板
	 */
	public static final String SMS_VOUVHER_DISPATCH_FOR_TB="PARTAOBAO_VOUCHERDISPATCH";
	
	/**
	 * 凭证短信下发58模板
	 */
	public static final String SMS_VOUVHER_DISPATCH_FOR_58TC="PAR58TC_VOUCHERDISPATCH";

	/**
	 * 凭证短信下发一号店模板
	 */
	public static final String PAR1MALL_VOUCHERDISPATCH="PAR1MALL_VOUCHERDISPATCH";

	/**
	 * 凭证邮箱下发 add by wenhua.cheng
	 * 
	 */
	public static final String EMAIL_VOUCHER_DISPATCH = "VOUCHER_DIS_EMAIL";
	/**
	 * 阳光绿洲成功发送凭证邮箱下发 add by yurenli
	 * 
	 */
	public static final String MERCHANT_API_VOUCHER_EMAIL = "MERCHANT_API_VOUCHER_EMAIL";

	/**
	 * 商家自有凭证对应商品超卖后内部报警邮件
	 */

	public static final String MER_VOUCHER_OVER_ALERT = "MER_VOUCHER_OVER_ALERT";

	/**
	 * 商家自有凭证对应商品对应的短信模板
	 */

	public static final String MER_VOUCHERDISPATCH = "MER_VOUCHERDISPATCH";

	/**
	 * 通过商家API发送凭证的短信模板
	 */

	public static final String MER_VOUCHERDISPATCH_API = "MER_VOUCHERDISPATCH_API_SMS";

	/**
	 * 通过商家API发送凭证的邮件模板
	 */

	public static final String MER_VOUCHERDISPATCH_EMAIL = "MER_VOUCHERDISPATCH_API_EMAIL";

	/**
	 * 三十天子账户过期短信模板
	 */
	public static final String SMSACCOUNTNOTIFY_THIRTY = "SMSACCOUNTNOTIFY_THIRTY";
	/**
	 * 三天子账户过期短信模板
	 */
	public static final String SMSACCOUNTNOTIFY_THREE = "SMSACCOUNTNOTIFY_THREE";

	/**
	 * 交易配置文件名字
	 */
	public static final String PAY_INFO_PROPER_NAME = "payapiinfo";

	/**
	 * 支付请求号前缀
	 */
	public static final String PAY_REQUEST_FIX = "payRequestFix";

	/**
	 * 我的订单二维数组过滤类型
	 */
	public static final String TRX_GOODS_ALL = "TRX_GOODS_ALL";
	public static final String TRX_GOODS_UNUSEED = "TRX_GOODS_UNUSEED";
	public static final String TRX_GOODS_UNCOMMENT = "TRX_GOODS_UNCOMMENT";

	public static final String TRX_GOODS_VIEW_UNUSEED = "TRX_GOODS_VIEW_UNUSEED";
	public static final String TRX_GOODS_VIEW_UNCOMMENT = "TRX_GOODS_VIEW_UNCOMMENT";

	public static final String PURSE_ACTHISTROTY = "PURSE_ACTHISTROTY";
	public static final String PURSE_REBATE = "PURSE_REBATE";

	public static final String GOODS_DETAIL_PIC = "GOODS_DETAIL_PIC";

	// ///////////////////页面参数///////////////////////////////////
	// ////////////////////注册页面/////////////////////////////////////////
	// 用户手机注册参数名称
	public static final String USER_MOBILE_REGIST = "MOBILE_REGIST";
	// 用户email注册 参数名称
	public static final String USER_EMAIL_REGIST = "EMAIL_REGIST";
	// 用户密码 参数名称
	public static final String USER_PASSWORD = "USER_PASSWORD";
	// 用户短信验证码
	public static final String URSER_VALIDATE_CODE = "VALIDATE_CODE";
	// 页面用户验证码
	public static final String USER_REGIST_CODE = "validCode";
	// 用户登录密码
	public static final String USER_LOGIN_USERNAME = "USER_LOGIN_USERNAME";
	// /////////////////////////////////////////////////////////////

	// //////////////////////微博需要参数//////////////////////////////////////
	// //////////////////////校内需要参数//////////////////////////////////////
	// 应用认证授权的URL
	public static final String RENREN_OAUTH_AUTHORIZE_URL = "https://graph.renren.com/oauth/authorize";
	// 应用获取Access_token的URL
	public static final String RENREN_OAUTH_ACCESS_TOKEN_URL = "https://graph.renren.com/oauth/token";
	// 应用获取人人网session key的URL
	public static final String RENREN_API_SESSIONKEY_URL = "http://graph.renren.com/renren_api/session_key";
	// 站点保存在Cookie的用户ID名称
	public static final String COOKIE_USER_ID = "XIAONEI_USERID";
	// ////////////////////////////////////////////////////////////////////////

	public static final String CPAGE = "cpage";
	public static final String TOTALROWS = "totalRows";
	public static final String PB = "pb";
	public static final int PAGE_SIZE = 10;

	// 我的订单每页条数add by wenhua.cheng
	public static final int TRX_PAGE_SIZE = 5;

	public static final int PURSE_PAGE_SIZE = 10;

	// 商品订单每页条数
	public static final int ORDER_PAGE_SIZE = 20;

	// Memcache 存储下载次数 MEM_COUPON_DOWNCOUNT_ID
	public static final String MEM_COUPON_DOWNCOUNT = "MEM_COUPON_DOWNCOUNT_ID_";
	// Memcache 浏览次数 MEM_COUPON_BROWCOUNT_ID
	public static final String MEM_COUPON_BROWCOUNT = "MEM_COUPON_BROWCOUNT_ID_";

	/** wap手机页面汉字信息properties文件名称 */
	public static final String WAP_CH_INFO = "wapChInfo";

	public static final String WAP_PATH = "wap";

	/** wap注册短信模板验证码 */
	public static final String WAP_REGIST_SMS_TITLE_CODE = "WAPSMSREGIST_CODE";

	/** wap注册短信模板URL */
	public static final String WAP_REGIST_SMS_TITLE_URL = "WAPSMSREGIST_URL";

	/** wap千品网域名 */
	public static final String WAP_URL_FIELD = "http://m.qianpin.com";

	/** web千品网域名 */
	public static final String WEB_QIANPIN_FIELD = "http://www.qianpin.com";

	/** wap首页标志 */
	public static final String WAP_INDEX_PAGE = "1";
	/** wap优惠卷标志 */
	public static final String WAP_COUPON_PAGE = "2";
	/** wap品牌标志 */
	public static final String WAP_BRAND_PAGE = "3";
	/** wap我的千品标志 */
	public static final String WAP_MY_QIANPIN = "4";
	/** wap发送短信SMSTYPE */
	public static final String SMS_TYPE = "15";

	/** wap页面导航点击记录，默认首页 */
	public static String CLICK_STATE = WAP_INDEX_PAGE;
	/**商品数量为是1时对应的短信模板*/
	public static String VOUCHERDISPATCHMIN = "VOUCHERDISPATCHMIN";
	/**商品数量为是1> <=5时对应的短信模板*/
	public static String VOUCHERDISPATCHOTHER = "VOUCHERDISPATCHOTHER";
	/**商品数量为是5>时对应的短信模板*/
	public static String VOUCHERDISPATCHMAX = "VOUCHERDISPATCHMAX";
	/**阳光绿洲商品对小于等于5的短信模板*/
	public static String MER_VOUCHERDISPATCH_API_SMS_MIN = "MER_VOUCHERDISPATCH_API_SMS_MIN";
	/**阳光绿洲商品对大于5的短信模板*/
	public static String MER_VOUCHERDISPATCH_API_SMS_MAX = "MER_VOUCHERDISPATCH_API_SMS_MAX";
	/** wap-优惠卷、商品、品牌图片路径 */
	// public static final String OLD_UPLOADIMAGES_PATH =
	// "D:/Tomcat/apache-tomcat-6.0.18/webapps/ROOT"+UPLOAD_IMAGES_URL;

	/** wap-优惠卷、商品、品牌图片压缩后路径 */
	// public static final String ZIP_UPLOADIMAGES_PATH =
	// "D:/Tomcat/apache-tomcat-6.0.18/webapps/ROOT/jsp/wap/uploadimages" ;

	// public static final String ONLINE_IMAGE_PATH =
	// "D:/sinobogroup/workspace/beikerwap/WebRoot/jsp/templates/";

	// public static final String ONLINE_IMAGE_PATH_DETAIL
	// ="D:/sinobogroup/workspace/beikerwap/WebRoot/jsp/goods_detail/";
	// ///////////////////腾讯微博属性文件参数//////////////////////////////////////////
	public static final String TENCENT_APP_KEY = "TENCENT_APP_KEY";

	public static final String TENCENT_APP_SECRET = "TENCENT_APP_SECRET";

	public static final String TENCENT_APP_CALL_BACK = "TENCENT_APP_CALL_BACK";
	
	public static final String MOBILE_SMS_TEMPLATE = "SMSUPDATEUSER";
	
	public static final String CRYPT_KEY = "Cryptkey";
	
	public static final String MOBILE_AUTHCODE_TEMPLATE = "MOBILE_AUTHCODE";

	public static final String COUPON_VMACCOUNT_ID="67";//线上优惠券vmactId
	
	public static final String COUPON_AMOUNT="10";		//优惠券金额
	
	public static final String COUPON_VMACCOUNT_LOSEDATE="2012-09-30 23:59:59";//线上优惠券对应虚拟款项过期时间
	
	//新邮箱注册验证模板
	public static final String NEW_REGIST_TEMPLATE = "NEW_REGIST_VALIDATE";
	
	//新邮箱注册验证通过
	public static final String REGIST_SUCCESS_TEMPLATE = "NEW_REGIST_SUCCESS";
	
	//手机端: 密码找回获取验证码 wenjie.mai
	public static final String MOBILE_AUTHCODE_FORGETPWD = "MOBILE_AUTHCODE_FORGETPWD";
	
	//手机端: 密码找回发送旧密码 wenjie.mai
	public static final String MOBILE_OLDPWD_FORGETPWD   = "MOBILE_OLDPWD_FORGETPWD";
}
