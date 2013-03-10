package com.beike.dao.background.admin;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.background.admin.AdminRole;
import com.beike.form.background.admin.AdminRoleForm;
/**
 * 
 * Title : 	AdminRoleDao
 * <p/>
 * Description	: 权限角色对应关系访问数据接口
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
public interface AdminRoleDao extends GenericDao<AdminRole,Long> {

	/**
	 * Description : 新增权限表单对应关系
	 * @param adminRoleList 权限对应角色列表
	 * @return
	 * @throws Exception
	 */
	public String addAdminRole(List<AdminRoleForm> adminRoleList) throws Exception;
	
	/**
	 * Description : 删除权限角色对应关系
	 * @param adminAccount 账户
	 * @return
	 * @throws Exception
	 */
	public String removeAdminRole(String adminAccount) throws Exception;
}
