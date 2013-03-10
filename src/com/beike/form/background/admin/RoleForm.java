package com.beike.form.background.admin;

import java.sql.Timestamp;
/**
 * Title : 	RoleForm
 * <p/>
 * Description	:角色表单对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-23    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-23  
 */
public class RoleForm {
	private int roleId;
	private String roleName;
	private String roleNameEn;
	private String roleStatus;
	private String roleCreateUser;
	private Timestamp roleCreateTime;
	private String lastUpdateUser;
	private Timestamp lastUpdateTime;
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleNameEn() {
		return roleNameEn;
	}
	public void setRoleNameEn(String roleNameEn) {
		this.roleNameEn = roleNameEn;
	}
	public String getRoleStatus() {
		return roleStatus;
	}
	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}
	public String getRoleCreateUser() {
		return roleCreateUser;
	}
	public void setRoleCreateUser(String roleCreateUser) {
		this.roleCreateUser = roleCreateUser;
	}
	public Timestamp getRoleCreateTime() {
		return roleCreateTime;
	}
	public void setRoleCreateTime(Timestamp roleCreateTime) {
		this.roleCreateTime = roleCreateTime;
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
