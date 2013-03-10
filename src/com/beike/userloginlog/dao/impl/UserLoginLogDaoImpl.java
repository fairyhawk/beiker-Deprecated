/**  
* @Title: UserLoginLogDaoImpl.java
* @Package com.beike.userloginlog.dao.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 21, 2012 11:52:16 AM
* @version V1.0  
*/
package com.beike.userloginlog.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.entity.brand.MergeWithBrands;
import com.beike.userloginlog.dao.UserLoginLogDao;
import com.beike.userloginlog.model.UserLoginLog;

/**
 * @ClassName: UserLoginLogDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 21, 2012 11:52:16 AM
 *
 */
@Repository("userLoginLogDao")
public class UserLoginLogDaoImpl extends GenericDaoImpl<UserLoginLog, Long> implements UserLoginLogDao {

	/**
	* @Title: addLoginLog
	* @Description: 添加用户登陆日志记录
	* @param @param ulLog
	* @param @throws Exception    设定文件
	* @return void    返回类型
	* @throws
	 */
	@Override
	public boolean addLoginLog(UserLoginLog ulLog) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO beiker_user_login_log(user_id,user_email,login_ip,login_time)");
		boolean isOK = false;
		if(null!=ulLog){
			sql.append(" VALUES(").append(ulLog.getUserid()).append(",'")
			                     .append(ulLog.getUserEmail()).append("','")
			                     .append(ulLog.getLoginIp()).append("',")
			                     .append("NOW()").append(")");
			isOK = true;
		}
		if(isOK){
			getJdbcTemplate().execute(sql.toString());
		}
		return isOK;

	}	

}
