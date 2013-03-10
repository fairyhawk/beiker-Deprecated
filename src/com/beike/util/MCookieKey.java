package com.beike.util;

import com.beike.util.ipparser.CityUtils;

/**
 * 存放手机wap系统中Cookie的键值
 * 
 * @author kun.wang
 */
public class MCookieKey {
	// cookie key
	/** 记住用户手机号或者邮箱的cookie的key */
	public static final String USER_ID_MEMORY = "USER_ID_MEMORY_";

	/** 记住用户密码的cookie */
	public static final String USER_PASSWORD_MEMORY = "USER_PASSWORD_MEMORY";
	
	/** 用户登陆记住我标志cookie的key */
	public static final String MEM_ME_KEY_ID = "MEM_ME_KEY_ID_";
	
	/** 用户一个月免登陆cookie的key */
	public static final String MEM_MONTH_KEY_ID = "MEM_MONTH_KEY_ID_";
	
	/** 用户信息对象的key */
	public static final String MEM_USER_INFO_KEY = "MEM_USER_INFOKEY_";
	
	/** 用户上一次登陆城市cookie的key */
	public static final String CITY_COOKIENAME = CityUtils.CITY_COOKIENAME;
	
	/** 注册时随即验证码的key */
	public static final String RANDOM_VALIDATE_CODE = "RANDOM_VALIDATE_CODE";
	
	/** 导航点击状态key */
	public static final String CATALOG_CLICK_STATE = "CATALOG_CLICK_STATE";
	
	
	// Memchache  key
	
	/** 登陆错误次数Mem的key，记录 */
	public static final String MEM_LOGIN_IP = "LOGIN_IP";
	
	/** 用户登陆时，输入错误三次以上时，需要输入验证码状态的MEM的key */
	public static final String MEM_USER_LOGIN_CODE_OPEN = "USER_LOGIN_CODE_OPEN";
	
	/** Memchache 存储地市信息 */
	public static final String MEM_AREAINFO_KEY = "MEM_AREA_INFO_KEY_";
	
	/** 注册发送短信次数key */
	public static final String MEM_REGIST_SMS_COUNT = "REGIST_SMS_COUNT";
	
	/** CITY_CATLOG */
	public static final String CITY_CATLOG = "CITY_CATLOG";
	
	
	// WAP-----提示信息key
	/** 登陆时验证码输入错误信息key */
	public static final String LOGIN_VALIDATE_CODE_ERROR = "LOGIN_VALIDATE_CODE_ERROR";
	
	/** wap--登陆用户名密码错误提示 */
	public static final String USER_PWD_ERROR = "USER_PWD_ERROR";
	
	/** wap--登陆所使用的用户未验证 */
	public static final String USER_UNAVAILABLE = "USER_UNAVAILABLE";
	
	/** wap--注册用户手机格式不正确 */
	public static final String REG_MOBILE_FORMATE_ERROR = "REG_MOBILE_FORMATE_ERROR";
	
	/** wap--注册用户手机验证码输入错误  */
	public static final String REG_VALIDATE_CODE_ERROR = "REG_VALIDATE_CODE_ERROR";
	
	/** wap--SYS_BUSY_INFO */
	public static final String SYS_BUSY_INFO = "SYS_BUSY_INFO";
	
	/** USER exist */
	public static final String REG_MOBILE_EXIST = "REG_MOBILE_EXIST";
	
	/** 密码只能为6-16为数字字符或下划线提示key */
	public static final String PWD_FORMATE_ERROR = "PWD_FORMATE_ERROR";
	
	/** 一天只允许一个用户发送10次验证短信提示key */
	public static final String SMS_COUNT_OUT = "SMS_COUNT_OUT";

	/** 注册失败，数据问题抛出异常 */
	public static final String REG_SYS_BUSY = "REG_SYS_BUSY";
	
	/** 提示不要篡改参数 */
	public static final String CHANGE_PARAM_ERROR = "CHANGE_PARAM_ERROR";
	
	/** 保存优惠卷失败 */
	public static final String SAVE_COUPON_ERROR = "WAP_SAVE_COUPON_ERROR";
	
	/**没有找到优惠卷 */
	public static final String COUPON_NOT_EXIST = "COUPON_NOT_EXIST";
	
	/** 短信发送优惠卷失败 */
	public static final String SEND_COUPON_ERROR = "SEND_COUPON_ERROR";
	
	/** 未登录用户只能下载五次优惠卷信息 */
	public static final String SEND_MORE_THAN_FIVE = "SEND_MORE_THAN_FIVE";
	
	/** 邮箱格式不正确 */
	public static final String REG_EMAIL_FORMATE_ERROR = "REG_EMAIL_FORMATE_ERROR";
	
	public static final String REG_EMAIL_EXIST = "REG_EMAIL_EXIST";
	/** 验证已经验证通过的用户，提示信息 */
	public static final String VALIDATE_USER_EXIST = "VALIDATE_USER_EXIST";
}
