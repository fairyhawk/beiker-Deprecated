/**  
* @Title: UserLoginLogDao.java
* @Package com.beike.userloginlog.dao
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 21, 2012 11:50:46 AM
* @version V1.0  
*/
package com.beike.userloginlog.dao;

import com.beike.dao.GenericDao;
import com.beike.userloginlog.model.UserLoginLog;

/**
 * @ClassName: UserLoginLogDao
 * @Description: 用户登陆DAO
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 21, 2012 11:50:46 AM
 *
 */
public interface UserLoginLogDao  extends GenericDao<UserLoginLog, Long>{

	/**
	 * 
	* @Title: addLoginLog
	* @Description: 添加用户登陆日志记录
	* @param @param ulLog
	* @param @throws Exception    设定文件
	* @return void    返回类型
	* @throws
	 */
	public boolean addLoginLog(UserLoginLog ulLog) throws Exception;
}
