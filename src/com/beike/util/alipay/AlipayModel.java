package com.beike.util.alipay;

import java.io.Serializable;

import com.beike.form.AccessToken;
 /*
 * com.beike.util.alipay.AlipayModel.java
 * @description:
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-8，xuxiaoxian ,create class
 *
 */
public class AlipayModel extends AccessToken implements Serializable{
	
	/**
	 * serialVersionUID:
	 */
	private static final long serialVersionUID = 1L;
	private String user_id;       //支付宝用户id
	private String real_name; //用户真实姓名
	private String sign;             //签名
	private String token;         //令牌授权码
	private String notify_id;   //通知校验Id
	private String email; //支付宝登录帐号
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getNotify_id() {
		return notify_id;
	}
	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
