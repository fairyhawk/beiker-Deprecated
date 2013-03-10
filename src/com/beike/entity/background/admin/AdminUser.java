package com.beike.entity.background.admin;

import java.sql.Timestamp;

/**
 * Title : 	AdminUser
 * <p/>
 * Description	:权限实体对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-20    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-20  
 */
public class AdminUser {
	private int adminUserId;
	private String adminUserAccount;
	private String adminUserPwd;
	private String adminUserName;
	private String adminUserEmail;
	private String adminUserPhone;
	private String adminUserStatus;
	private String adminUserCreateUser;
	private Timestamp adminUserCreateTime;
	private String lastUpdateUser;
	private Timestamp lastUpdateTime;
	public int getAdminUserId() {
		return adminUserId;
	}
	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}
	public String getAdminUserAccount() {
		return adminUserAccount;
	}
	public void setAdminUserAccount(String adminUserAccount) {
		this.adminUserAccount = adminUserAccount;
	}
	public String getAdminUserPwd() {
		return adminUserPwd;
	}
	public void setAdminUserPwd(String adminUserPwd) {
		this.adminUserPwd = adminUserPwd;
	}
	public String getAdminUserName() {
		return adminUserName;
	}
	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}
	public String getAdminUserEmail() {
		return adminUserEmail;
	}
	public void setAdminUserEmail(String adminUserEmail) {
		this.adminUserEmail = adminUserEmail;
	}
	public String getAdminUserPhone() {
		return adminUserPhone;
	}
	public void setAdminUserPhone(String adminUserPhone) {
		this.adminUserPhone = adminUserPhone;
	}
	public String getAdminUserStatus() {
		return adminUserStatus;
	}
	public void setAdminUserStatus(String adminUserStatus) {
		this.adminUserStatus = adminUserStatus;
	}
	public String getAdminUserCreateUser() {
		return adminUserCreateUser;
	}
	public void setAdminUserCreateUser(String adminUserCreateUser) {
		this.adminUserCreateUser = adminUserCreateUser;
	}
	public Timestamp getAdminUserCreateTime() {
		return adminUserCreateTime;
	}
	public void setAdminUserCreateTime(Timestamp adminUserCreateTime) {
		this.adminUserCreateTime = adminUserCreateTime;
	}
	public String getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	
}
