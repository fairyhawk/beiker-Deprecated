/**  
* @Title: UserLoginLog.java
* @Package com.beike.userloginlog.model
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 21, 2012 11:43:54 AM
* @version V1.0  
*/
package com.beike.userloginlog.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: UserLoginLog
 * @Description: 用户登陆log bean
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 21, 2012 11:43:54 AM
 *
 */
public class UserLoginLog implements Serializable{

	 private Long id;//标识
	 
	 private Long userid;//用户ID
	 
	 private String userEmail;//用户邮箱
	 
	 private String loginIp;//用户登陆IP
	 
	 private Date loginTime;//用户登陆日期时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
}
