package com.beike.common.mobile.exception;

import com.beike.common.exception.BaseException;

/**  
* @Title:  手机用户异常类
* @Package com.beike.common.mobile.exception
* @Description: TODO
* @author wenjie.mai  
* @date Mar 26, 2012 5:43:10 PM
* @version V1.0  
*/
public class MobileUserException extends BaseException {

	private static final long serialVersionUID = -7704984367550383257L;
	
	public MobileUserException() {
		super();
	}

	public MobileUserException(int code) {
		super(code);
	}
		
	// 注册用户名格式错误
	public static final String USER_REGISTER_EMAIL_ERROR    = "300001";
	
	// 用户密码格式有误
	public static final String USER_REGISTER_PASSWORD_ERROR = "300002";
	
	// 用户名已经存在
	public static final String USER_EXIST = "300003";
	
	//创建账户错误码
	public static final String USER_ACCOUNT_ERROR = "300004";
	
	// 登录用户名格式错误
	public static final String USER_LOGIN_EMAIL_ERROR     = "300005";
	
	// 登录密码格式错误
	public static final String USER_LOGIN_PASSWORD_ERROR  = "300006";
	
	//用户系统错误
	public static final String USER_SYSTEM_ERROR = "300007";
	
	//邮箱或者手机号不存在
	public static final String USER_EMAIL_TEL_NOTEXIT = "300008";
	
	//地址失效
	public static final String USER_WEBADDRESS_FAILD = "300009";
	
	//输入参数错误
	public static final String INPUT_PARAM_ERROR = "100001";
	
	//输入参数解密错误
	public static final String INPUT_PARAM_DECRYPT_ERROR = "100002";
	
	//无效第三方用户
	public static final String USER_INVALID_THIRDPART = "300010";
	
	//验证邮箱或者手机号不存在
	public static final String USER_EMAIL_OR_TEL_NOTEXIT = "300011";
	
	//登录密码不一致
	public static final String USER_LOGIN_PASSWORD_DIFFENT = "300012";
	
	//用户登录超时
	public static final String USER_LOGIN_OUTTIME = "300013";
	
	//用户手机号已经存在
	public static final String USER_TELPHONE_ISEXIT = "300014";
	
	//短信发送超时
	public static final String USER_SMS_OUTTIME = "300015";
	
	//短信模板没有找到
	public static final String SMSTEMPLATE_NOT_FOUNT = "300016";
	
	//手机验证码不一致
	public static final String SMS_CODE_DIFFENT = "300017";
	
	//第三方帐号已经存在
	public static final String OPENID_EXIT = "300018";
}
