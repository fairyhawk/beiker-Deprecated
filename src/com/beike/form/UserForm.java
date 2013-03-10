package com.beike.form;
/**
 * <p>Title: 用户form</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

public class UserForm {
	private Long id;
	private String email;
	private String mobile;
	private String password;
	private String customerKey;
	
	private long email_isavalible=0;
	private long mobile_isavalible=0;
	private long isavalible=0;
	private String userIp;
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getCustomerKey() {
		return customerKey;
	}
	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		if(mobile==null){
			mobile="";
		}
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
	
	public UserForm(){
		
	}
	public UserForm(String email, String mobile, String password) {
		this.email = email;
		this.mobile = mobile;
		this.password = password;
	}
	public void setIsavalible(int isavalible) {
		this.isavalible = isavalible;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public long getIsavalible() {
		return isavalible;
	}
	public void setIsavalible(long isavalible) {
		this.isavalible = isavalible;
	}
	
}
