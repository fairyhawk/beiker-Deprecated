package com.beike.entity.background.admin;


/**
 * Title : 	AdminRole
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
public class AdminRole {

	private int adminRoleId;
	private String adminAccount;
	private int roleId;
	public int getAdminRoleId() {
		return adminRoleId;
	}
	public void setAdminRoleId(int adminRoleId) {
		this.adminRoleId = adminRoleId;
	}
	public String getAdminAccount() {
		return adminAccount;
	}
	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
}
