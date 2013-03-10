package com.beike.service.background.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.background.admin.AdminDao;
import com.beike.entity.background.admin.AdminUser;
import com.beike.form.background.admin.AdminRoleForm;
import com.beike.form.background.admin.AdminUserForm;
import com.beike.service.background.admin.AdminRoleService;
import com.beike.service.background.admin.AdminService;
import com.beike.util.StringUtils;
/**
 * Title : 	AdminServiceImpl
 * <p/>
 * Description	:权限服务实现类
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
 * <pre>1     2011-5-20   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-20  
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

	/*
	 * @see com.beike.service.background.admin.AdminService#addAdmin(com.beike.form.background.admin.AdminUserForm)
	 */
	public String addAdmin(AdminUserForm adminForm) throws Exception {
		String addResult = null;
		addResult = adminDao.addAdmin(adminForm);
		this.addAdminRole(adminForm);
		return addResult;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#queryAdminById(java.lang.String)
	 */
	public AdminUser queryAdminById(String adminId) throws Exception {
		AdminUser admin = null;
		admin = adminDao.queryAdminById(adminId);
		return admin;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#queryAdminRole(java.lang.String)
	 */
	public Map<Map<Long,String>,Long> queryAdminRole(String adminId) throws Exception {
		Map<Map<Long,String>,Long> adminRoleMap = null;
		adminRoleMap = adminDao.queryAdminRole(adminId);
		return adminRoleMap;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#queryAdminRoleId(java.lang.String)
	 */
	public Map<Long, Long> queryAdminRoleId(String adminId) throws Exception {
		Map<Long,Long> adminRoleIdMap = null;
		adminRoleIdMap = adminDao.queryAdminRoleId(adminId);
		return adminRoleIdMap;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#editAdmin(com.beike.form.background.admin.AdminUserForm)
	 */
	public String editAdmin(AdminUserForm adminForm) throws Exception {
		String updateResult = null;
		updateResult = adminDao.updateAdmin(adminForm);
		adminRoleService.removeAdminRole(adminForm.getAdminUserAccount());
		this.addAdminRole(adminForm);
		return updateResult;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#queryAdminByConditions(com.beike.form.background.admin.AdminUserForm, int, int)
	 */
	public List<AdminUser> queryAdminByConditions(AdminUserForm adminForm,int startRow,int pageSize)
		throws Exception {
		List<AdminUser> adminList = null;
		adminList = adminDao.queryAdminByConditions(adminForm,startRow,pageSize);
		return adminList;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#queryAdminCountByConditions(com.beike.form.background.admin.AdminUserForm)
	 */
	public int queryAdminCountByConditions(AdminUserForm adminForm)
			throws Exception {
		int adminCount = 0;
		adminCount = adminDao.queryAdminCountByConditions(adminForm);
		return adminCount;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#validatorAccount(com.beike.form.background.admin.AdminUserForm)
	 */
	public boolean validatorAccount(AdminUserForm adminForm) throws Exception {
		boolean flag = false;
		flag = adminDao.validatorAccount(adminForm);
		return flag;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminService#updateAdminPwd(com.beike.form.background.admin.AdminUserForm)
	 */
	public String updateAdminPwd(AdminUserForm adminForm) throws Exception {
		String result = null;
		result = adminDao.updateAdminPwd(adminForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.admin.AdminService#validatorPwd(com.beike.form.background.admin.AdminUserForm)
	 */
	public boolean validatorPwd(AdminUserForm adminForm) throws Exception {
		boolean flag = false;
		flag = adminDao.validatorPwd(adminForm);
		return flag;
	}
	
	private void addAdminRole(AdminUserForm adminForm) throws Exception{
		if(StringUtils.validNull(adminForm.getAdminUserRoleId())){
			String[] roleArray = adminForm.getAdminUserRoleId().split(","); 
			AdminRoleForm adminRoleForm = null;
			List<AdminRoleForm> adminRoleList = new ArrayList<AdminRoleForm>();
			for(String roleId : roleArray){
				adminRoleForm = new AdminRoleForm();
				adminRoleForm.setAdminAccount(adminForm.getAdminUserAccount());
				adminRoleForm.setRoleId(Integer.parseInt(roleId));
				adminRoleList.add(adminRoleForm);
			}
			adminRoleService.addAdminRole(adminRoleList);
		}
	}

	@Autowired
	private AdminDao adminDao;
	@Autowired
	private AdminRoleService adminRoleService;

}
