/**  
* @Title: UserLoginLogServiceImpl.java
* @Package com.beike.userloginlog.service.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 21, 2012 11:49:42 AM
* @version V1.0  
*/
package com.beike.userloginlog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.userloginlog.dao.UserLoginLogDao;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.userloginlog.service.UserLoginLogService;

/**
 * @ClassName: UserLoginLogServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 21, 2012 11:49:42 AM
 *
 */
@Service("userLoginLogService")
public class UserLoginLogServiceImpl implements UserLoginLogService {

	@Autowired
	private UserLoginLogDao userLoginLogDao;

	public void setUserLoginLogDao(UserLoginLogDao userLoginLogDao) {
		this.userLoginLogDao = userLoginLogDao;
	}
	/**
	 * 
	* @Title: addLoginLog
	* @Description: 添加用户登陆日志记录
	* @param @param ulLog
	* @param @throws Exception    设定文件
	* @return void    返回类型
	* @throws
	 */
	@Override
	public boolean addLoginLog(UserLoginLog ulLog) throws Exception {
		// TODO Auto-generated method stub
		return userLoginLogDao.addLoginLog(ulLog);

	}


}
