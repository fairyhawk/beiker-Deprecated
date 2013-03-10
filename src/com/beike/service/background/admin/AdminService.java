package com.beike.service.background.admin;

import java.util.List;
import java.util.Map;

import com.beike.entity.background.admin.AdminUser;
import com.beike.form.background.admin.AdminUserForm;

/**
 * Title : 	AdminService
 * <p/>
 * Description	:权限服务接口类
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
public interface AdminService {
	
	/**
	 * Description : 新增账户
	 * @param adminForm 账户对应表单
	 * @return java.lang.String
	 * @throws Exception
	 */
	public String addAdmin(AdminUserForm adminForm) throws Exception;
	
	/**
	 * Description : 根据adminId查询权限信息
	 * @param adminId
	 * @return
	 * @throws Exception
	 */
	public AdminUser queryAdminById(String adminId) throws Exception;
	
	/**
	 * Description : 查询账户所对应角色
	 * @return
	 * @throws Exception
	 */
	public Map<Map<Long,String>,Long> queryAdminRole(String adminId) throws Exception;
	
	/**
	 * Description : 根据账户ID查询对应角色ID
	 * @param adminId 账户ID
	 * @return
	 * @throws Exception
	 */
	public Map<Long,Long> queryAdminRoleId(String adminId) throws Exception;
	
	/**
	 * Description : 修改权限接口
	 * @param adminForm 
	 * @return
	 * @throws Exception
	 */
	public String editAdmin(AdminUserForm adminForm) throws Exception;
	
	/**
	 * Description : 根据查询条件查权限列表
	 * @param adminForm 权限表单
	 * @param startRow 开始行数
	 * @param pageSize 每页大小
	 * @return
	 * @throws Exception
	 */
	public List<AdminUser> queryAdminByConditions(AdminUserForm adminForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 根据查询条件查询权限条数
	 * @param adminForm 查询条件表单
	 * @return 
	 * @throws Exception
	 */
	public int queryAdminCountByConditions(AdminUserForm adminForm) throws Exception;
	
	/**
	 * Desceiption : 校验账户是否重复
	 * @param adminForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorAccount(AdminUserForm adminForm) throws Exception;
	
	/**
	 * Description : 校验用户名密码是否一致
	 * @param adminForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorPwd(AdminUserForm adminForm) throws Exception;
	
	/**
	 * Description : 修改权限密码
	 * @param adminForm
	 * @return
	 * @throws Exception
	 */
	public String updateAdminPwd(AdminUserForm adminForm) throws Exception;
}
