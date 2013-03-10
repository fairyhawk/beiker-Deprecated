package com.beike.dao.background.admin.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.admin.AdminDao;
import com.beike.entity.background.admin.AdminUser;
import com.beike.form.background.admin.AdminUserForm;
import com.beike.util.StringUtils;
/**
 * Title : 	AdminDaoImpl
 * <p/>
 * Description	:	权限数据访问实现
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
@Repository("adminDao")
public class AdminDaoImpl extends GenericDaoImpl<AdminUser,Long> implements AdminDao {

	/*
	 * @see com.beike.dao.background.right.AdminDao#addAdmin(com.beike.form.background.admin.AdminUserForm)
	 */
	public String addAdmin(AdminUserForm adminUserForm) throws Exception {
		final AdminUserForm form = adminUserForm;
		final String sql="INSERT INTO beiker_admin_user(admin_account,admin_pwd,admin_name,admin_email,admin_phone,admin_status,admin_create_user,last_update_time) VALUES(?,?,?,?,?,?,?,now())";
		int flag = this.getJdbcTemplate().update(sql,new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getAdminUserAccount());
			   	ps.setString(2, StringUtils.md5(form.getAdminUserPwd()));
			   	ps.setString(3, form.getAdminUserName());
			   	ps.setString(4, form.getAdminUserEmail());
			   	ps.setString(5, form.getAdminUserPhone());
			   	ps.setString(6, form.getAdminUserStatus());
			   	ps.setString(7, form.getAdminUserCreateUser());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.right.AdminDao#queryAdminById(java.lang.String)
	 */
	public AdminUser queryAdminById(String adminId) throws Exception {
		AdminUser admin = null;
		if(!StringUtils.validNull(adminId)){
			return admin;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT admin_id,admin_account,admin_pwd,admin_name,admin_email,admin_phone,admin_status,admin_create_user,admin_create_time ");
		sql.append("FROM beiker_admin_user WHERE admin_id= ? ");
		System.out.println("adminId="+adminId);
		ParameterizedRowMapper<AdminUser> mapper = new ParameterizedRowMapper<AdminUser>() {
	        // notice the return type with respect to Java 5 covariant return types
	        public AdminUser mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	AdminUser admin = new AdminUser();
	        	admin.setAdminUserId(rs.getInt("admin_id"));
	        	admin.setAdminUserAccount(rs.getString("admin_account"));
	        	admin.setAdminUserPwd(rs.getString("admin_pwd"));
	        	admin.setAdminUserName(rs.getString("admin_name"));
	        	admin.setAdminUserEmail(rs.getString("admin_email"));
	        	admin.setAdminUserPhone(rs.getString("admin_phone"));
	        	admin.setAdminUserStatus(rs.getString("admin_status"));
	        	admin.setAdminUserCreateUser(rs.getString("admin_create_user"));
	        	admin.setAdminUserCreateTime(rs.getTimestamp("admin_create_time"));
	            return admin;
	        }
	    };

		admin = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), mapper, Integer.parseInt(adminId));
		return admin;
	}
	
	/*
	 * @see com.beike.dao.background.right.AdminDao#queryAdminRole(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<Map<Long,String>,Long> queryAdminRole(String adminId) throws Exception {
		Map<Map<Long,String>,Long> adminRoleMap = new HashMap<Map<Long,String>,Long>();
		List adminList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT r.admin_id admin_id,role.role_id role_id,role.role_name role_name FROM beiker_admin_user r ");
		sql.append("JOIN beiker_admin_role rr ON r.admin_account = rr.admin_account ");
		sql.append("JOIN beiker_role role ON role.role_id = rr.role_id ");
		if(StringUtils.validNull(adminId)){
			sql.append("WHERE r.admin_id = ? ");
			adminList = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{adminId});
		}else{
			adminList = this.getJdbcTemplate().queryForList(sql.toString());
		}
		if(null!=adminList&&adminList.size()>0){
			for(int i=0;i<adminList.size();i++){
				Map adminMap = (Map) adminList.get(i);
				Map<Long,String> roleMap = new IdentityHashMap<Long,String>();
				roleMap.put((Long)adminMap.get("role_id"), (String)adminMap.get("role_name"));
				adminRoleMap.put(roleMap,(Long)adminMap.get("admin_id"));
			}
		}
		return adminRoleMap;
	}
	
	/*
	 * @see com.beike.dao.background.right.AdminDao#queryAdminRoleId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<Long, Long> queryAdminRoleId(String adminId) throws Exception {
		
		Map<Long,Long> adminRoleIdMap = new HashMap<Long,Long>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT r.admin_id admin_id,role.role_id role_id FROM beiker_admin_user r ");
		sql.append("JOIN beiker_admin_role rr ON r.admin_account = rr.admin_account ");
		sql.append("JOIN beiker_role role ON role.role_id = rr.role_id ");
		sql.append("WHERE r.admin_id = ? ");
		
		List adminList = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{adminId});
		if(null!=adminList&&adminList.size()>0){
			for(int i=0; i<adminList.size(); i++){
				Map adminMap = (Map) adminList.get(i);
				adminRoleIdMap.put((Long)adminMap.get("role_id"), (Long)adminMap.get("admin_id"));
			}
		}
		return adminRoleIdMap;
	}
	
	/*
	 * @see com.beike.dao.background.right.AdminDao#updateAdmin(com.beike.form.background.admin.AdminUserForm)
	 */
	public String updateAdmin(AdminUserForm adminForm) throws Exception {
		final AdminUserForm form = adminForm;
		String sql = "UPDATE beiker_admin_user SET admin_name = ?,admin_email=?,admin_phone=?,admin_status=?,last_update_user=?,last_update_time=now() WHERE admin_id = ? ";
		int flag = this.getJdbcTemplate().update(sql,new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getAdminUserName());
				ps.setString(2, form.getAdminUserEmail());
				ps.setString(3, form.getAdminUserPhone());
				ps.setString(4, form.getAdminUserStatus());
				ps.setString(5, form.getLastUpdateUser());
				ps.setInt(6, form.getAdminUserId());
			}
		});
		return String.valueOf(flag);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<AdminUser> queryAdminByConditions(AdminUserForm adminForm,int startRow,int pageSize)
			throws Exception {
		List<AdminUser> adminList = null;
		List tempList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT admin_id,admin_account,admin_pwd,admin_name,admin_email,admin_phone,admin_status FROM beiker_admin_user ");
		sql.append("WHERE 1=1 ");
		if(StringUtils.validNull(adminForm.getAdminUserAccount())){
			sql.append(" AND admin_account like ").append("'%"+adminForm.getAdminUserAccount()+"%' ");
		}
		if(StringUtils.validNull(adminForm.getAdminUserEmail())){
			sql.append(" AND admin_email like ").append("'%"+adminForm.getAdminUserEmail()+"%' ");
		}
		if(StringUtils.validNull(adminForm.getAdminUserName())){
			sql.append(" AND admin_name like ").append("'%"+adminForm.getAdminUserName()+"%' ");
		}
		sql.append("  ORDER BY admin_id DESC LIMIT ?,? ");
		Object[] params = new Object[]{startRow,pageSize};
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			adminList = this.convertResultToObjectList(tempList);
		}
		return adminList;
	}

	/*
	 * @see com.beike.dao.background.right.AdminDao#queryAdminCountByConditions(com.beike.form.background.admin.AdminUserForm)
	 */
	public int queryAdminCountByConditions(AdminUserForm adminForm)
			throws Exception {
		int adminCount = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_admin_user WHERE 1=1 ");
		if(StringUtils.validNull(adminForm.getAdminUserAccount())){
			sql.append(" AND admin_account like ").append("'%"+adminForm.getAdminUserAccount()+"%' ");
		}
		if(StringUtils.validNull(adminForm.getAdminUserEmail())){
			sql.append(" AND admin_email like ").append("'%"+adminForm.getAdminUserEmail()+"%' ");
		}
		if(StringUtils.validNull(adminForm.getAdminUserName())){
			sql.append(" AND admin_name like ").append("'%"+adminForm.getAdminUserName()+"%' ");
		}
		adminCount = this.getJdbcTemplate().queryForInt(sql.toString());
		return adminCount;
	}
	
	/*
	 * @see com.beike.dao.background.right.AdminDao#validatorAccount(com.beike.form.background.admin.AdminUserForm)
	 */
	public boolean validatorAccount(AdminUserForm adminForm) throws Exception {
		boolean flag = false;
		String sql = "  SELECT COUNT(1) FROM beiker_admin_user WHERE admin_account = ? ";
		Object[] params = new Object[]{adminForm.getAdminUserAccount()};
		int count = this.getJdbcTemplate().queryForInt(sql, params);
		if(count>0){
			flag = true;
		}
		return flag;
	}

	/*
	 * @see com.beike.dao.background.right.AdminDao#updateAdminPwd(com.beike.form.background.admin.AdminUserForm)
	 */
	public String updateAdminPwd(AdminUserForm adminForm) throws Exception {
		String sql = "UPDATE beiker_admin_user SET admin_pwd = ? WHERE admin_account = ? ";
		Object[] params = new Object[]{StringUtils.md5(adminForm.getAdminUserPwd()),adminForm.getAdminUserAccount()};
		int[] types = new int[]{Types.VARCHAR,Types.VARCHAR};
		int flag = this.getJdbcTemplate().update(sql, params, types);
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.right.AdminDao#validatorPwd(com.beike.form.background.admin.AdminUserForm)
	 */
	public boolean validatorPwd(AdminUserForm adminForm) throws Exception {
		boolean flag = false;
		String sql = " SELECT COUNT(1) FROM beiker_admin_user WHERE admin_account = ? AND admin_pwd = ? ";
		Object[] params = new Object[]{adminForm.getAdminUserAccount(),StringUtils.md5(adminForm.getAdminUserPwd())};
		int[] types = new int[]{Types.VARCHAR,Types.VARCHAR};
		int result = this.getJdbcTemplate().queryForInt(sql, params, types);
		if(result>0){
			flag = true;
		}
		return flag;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<AdminUser> convertResultToObjectList(List results) throws Exception{
        List<AdminUser> objList = new ArrayList<AdminUser>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                AdminUser admin = this.convertResultMapToObject(result);
                objList.add(admin);
            }
        }
        return objList;
    }
    
    /**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result   jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private AdminUser convertResultMapToObject(Map result) throws Exception{
		AdminUser obj = new AdminUser();
			if (result != null) {
				Long adminId = ((Number)result.get("admin_id")).longValue();
				if(null!=adminId){
					obj.setAdminUserId(adminId.intValue());
				}
				if(StringUtils.validNull((String) result.get("admin_account"))){
					obj.setAdminUserAccount(result.get("admin_account").toString());
				}
				if(StringUtils.validNull((String) result.get("admin_pwd"))){
					obj.setAdminUserPwd(result.get("admin_pwd").toString());
				}
				if(StringUtils.validNull((String) result.get("admin_name"))){
					obj.setAdminUserName(result.get("admin_name").toString());
				}
				if(StringUtils.validNull((String) result.get("admin_email"))){
					obj.setAdminUserEmail(result.get("admin_email").toString());
				}
				if(StringUtils.validNull((String) result.get("admin_phone"))){
					obj.setAdminUserPhone(result.get("admin_phone").toString());
				}
				if(StringUtils.validNull((String) result.get("admin_status"))){
					obj.setAdminUserStatus(result.get("admin_status").toString());
				}
			}
		return obj;
	}

}
