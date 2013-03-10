package com.beike.wap.entity;

import java.util.Date;

/**
 * 临时信息，存放手机用户注册时的用户名，验证码
 * @author kun.wang
 */
public class MUserTemp {
	/** 主键id */
	private long id;
	/** 手机号 */
	private String mobile;
	/** 密码，加密后 */
	private String password;
	/** 激活码 */
	private long vCode;
	/** 注册时间 */
	private Date regDate;
	/** 注册邮箱 */
	private String email;
	/** 用户key，自动生成 */
	private String customerkey;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getvCode() {
		return vCode;
	}
	public void setvCode(long vCode) {
		this.vCode = vCode;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public String getCustomerkey() {
		return customerkey;
	}
	public void setCustomerkey(String customerkey) {
		this.customerkey = customerkey;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
