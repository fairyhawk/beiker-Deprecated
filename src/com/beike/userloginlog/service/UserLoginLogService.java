/**  
* @Title: UserLoginLogService.java
* @Package com.beike.userloginlog.service
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 21, 2012 11:42:42 AM
* @version V1.0  
*/
package com.beike.userloginlog.service;

import com.beike.userloginlog.model.UserLoginLog;

/**
 * @ClassName: UserLoginLogService
 * @Description: 用户登陆日志service
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 21, 2012 11:42:42 AM
 *
 */
public interface UserLoginLogService {

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
