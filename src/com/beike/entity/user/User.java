package com.beike.entity.user;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Title: 用户实体
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
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private long id; // 主键 id
	private String email;// 邮件 email
	private String mobile;// 邮件是否验证
	private long email_isavalible;// 手机号
	private long mobile_isavalible;// 手机号是否验证 mobile_isavalible
	// private Date registdate;//注册时间 registdate
	// private Date lastlogindate;//最后登录时间
	private String password;// password
	// private String locale;//所属城市
	private long isavalible;// 是否激活(0冻结 1已激活)

	private String customerkey; // 用户密钥 用户生成密码

	private Date registDate;

	public Date getRegistDate() {
		return registDate;
	}

	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}

	public String getCustomerkey() {
		return customerkey;
	}

	public void setCustomerkey(String customerkey) {
		this.customerkey = customerkey;
	}

	public User() {

	}

	public User(long id, String email, String mobile, int email_isavalible,
			int mobile_isavalible, Date registdate, Date lastlogindate,
			String password, String locale, int isavalible) {
		this.id = id;
		this.email = email;
		this.mobile = mobile;
		this.email_isavalible = email_isavalible;
		this.mobile_isavalible = mobile_isavalible;
		// this.registdate = registdate;
		// this.lastlogindate = lastlogindate;
		this.password = password;
		// this.locale = locale;
		this.isavalible = isavalible;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public long getEmail_isavalible() {
		return email_isavalible;
	}

	public void setEmail_isavalible(long email_isavalible) {
		this.email_isavalible = email_isavalible;
	}

	public long getMobile_isavalible() {
		return mobile_isavalible;
	}

	public void setMobile_isavalible(long mobile_isavalible) {
		this.mobile_isavalible = mobile_isavalible;
	}

	// public Date getRegistdate() {
	// return registdate;
	// }
	// public void setRegistdate(Date registdate) {
	// this.registdate = registdate;
	// }
	// public Date getLastlogindate() {
	// return lastlogindate;
	// }
	// public void setLastlogindate(Date lastlogindate) {
	// this.lastlogindate = lastlogindate;
	// }
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// public String getLocale() {
	// return locale;
	// }
	// public void setLocale(String locale) {
	// this.locale = locale;
	// }
	public long getIsavalible() {
		return isavalible;
	}

	public void setIsavalible(long isavalible) {
		this.isavalible = isavalible;
	}
}
