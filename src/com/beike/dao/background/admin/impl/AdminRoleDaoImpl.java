package com.beike.dao.background.admin.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.admin.AdminRoleDao;
import com.beike.entity.background.admin.AdminRole;
import com.beike.form.background.admin.AdminRoleForm;
/**
 * Title : 	AdminRoleDaoImpl
 * <p/>
 * Description	:	权限角色对应关系数据访问实现
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
@Repository("adminRoleDao")
public class AdminRoleDaoImpl extends GenericDaoImpl<AdminRole,Long> implements AdminRoleDao {

	
	/*
	 * @see com.beike.dao.background.right.AdminRoleDao#addAdminRole(java.util.List)
	 */
	public String addAdminRole(List<AdminRoleForm> adminRoleList) throws Exception {
		final List<AdminRoleForm> adminRoleFinalList = adminRoleList;
		final String sql = "INSERT INTO beiker_admin_role(admin_account,role_id) VALUES(?,?) ";
		int[] count = this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter()
		{ 
			public void setValues(PreparedStatement ps,int i) throws SQLException {
				AdminRoleForm adminRoleForm = adminRoleFinalList.get(i);
				ps.setString(1, adminRoleForm.getAdminAccount());
				ps.setInt(2, adminRoleForm.getRoleId());
			}
			public int getBatchSize() 
			   { 
				   return adminRoleFinalList.size(); 
			   } 
		});
		return String.valueOf(count);
	}

	/*
	 * @see com.beike.dao.background.right.AdminRoleDao#removeAdminRole(java.lang.String)
	 */
	public String removeAdminRole(String adminAccount) throws Exception {
		String sql = "DELETE FROM beiker_admin_role WHERE admin_account = ? ";
		int flag = this.getSimpleJdbcTemplate().update(sql, adminAccount);
		return String.valueOf(flag);
	}

	
	
}
