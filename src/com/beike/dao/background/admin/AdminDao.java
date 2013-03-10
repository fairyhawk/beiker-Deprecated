package com.beike.dao.background.admin;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.background.admin.AdminUser;
import com.beike.form.background.admin.AdminUserForm;

/**
 * 
 * Title : 	AdminDao
 * <p/>
 * Description	: 权限访问数据接口
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
 * <pre>1     2011-5-20    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-20
 */
public interface AdminDao extends GenericDao<AdminUser,Long> {

	/**
	 * Description : 新增账户
	 * @param adminUserForm 账户对应表单
	 * @return java.lang.String
	 * @throws Exception
	 */
	public String addAdmin(AdminUserForm adminUserForm) throws Exception;
	
	/**
	 * Description : 根据adminUserId查询权限信息
	 * @param adminUserId
	 * @return
	 * @throws Exception
	 */
	public AdminUser queryAdminById(String adminUserId) throws Exception;
	
	/**
	 * Description : 查询账户所对应角色
	 * @return
	 * @throws Exception
	 */
	public Map<Map<Long,String>,Long> queryAdminRole(String adminUserId) throws Exception;
	
	/**
	 * Description : 根据账户ID查询对应角色ID
	 * @param adminId 账户ID
	 * @return
	 * @throws Exception
	 */
	public Map<Long,Long> queryAdminRoleId(String adminUserId) throws Exception;
	
	/**
	 * Description : 修改权限接口
	 * @param adminUserForm 
	 * @return
	 * @throws Exception
	 */
	public String updateAdmin(AdminUserForm adminUserForm) throws Exception;
	
	/**
	 * Description : 根据查询条件查权限列表
	 * @param adminUserForm 查询条件表单
	 * @return
	 * @throws Exception
	 */
	public List<AdminUser> queryAdminByConditions(AdminUserForm adminUserForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 根据查询条件查询权限条数
	 * @param adminUserForm 查询条件表单
	 * @return 
	 * @throws Exception
	 */
	public int queryAdminCountByConditions(AdminUserForm adminUserForm) throws Exception;
	
	/**
	 * Desceiption : 校验账户是否重复
	 * @param adminUserForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorAccount(AdminUserForm adminUserForm) throws Exception;
	
	/**
	 * Description : 校验用户名密码是否一致
	 * @param adminUserForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorPwd(AdminUserForm adminUserForm) throws Exception;
	
	/**
	 * Description : 修改权限密码
	 * @param adminUserForm
	 * @return
	 * @throws Exception
	 */
	public String updateAdminPwd(AdminUserForm adminUserForm) throws Exception;
}
