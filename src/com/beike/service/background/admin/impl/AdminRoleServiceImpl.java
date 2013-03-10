package com.beike.service.background.admin.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.background.admin.AdminRoleDao;
import com.beike.form.background.admin.AdminRoleForm;
import com.beike.service.background.admin.AdminRoleService;

/**
 * Title : 	AdminRoleServiceImpl
 * <p/>
 * Description	:角色服务实现类
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
 * <pre>1     2011-5-23   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-23  
 */
@Service("adminRoleService")
public class AdminRoleServiceImpl implements AdminRoleService {

	/*
	 * @see com.beike.service.background.admin.AdminRoleService#addAdminRole(java.util.List)
	 */
	public String addAdminRole(List<AdminRoleForm> adminRoleList) throws Exception {
		String result = null;
		result = adminRoleDao.addAdminRole(adminRoleList);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.admin.AdminRoleService#removeAdminRole(java.lang.String)
	 */
	public String removeAdminRole(String adminAccount) throws Exception {
		String result = null;
		result = adminRoleDao.removeAdminRole(adminAccount);
		return result;
	}
	
	@Autowired
	private AdminRoleDao adminRoleDao;
	
}
