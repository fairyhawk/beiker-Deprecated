package com.beike.dao.user;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.user.UserExpand;

/**      
 * project:beiker  
 * Title:用户扩展信息
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 7:00:56 PM     
 * @version 1.0
 */
public interface UserExpandDao extends GenericDao<UserExpand, Long> {
	/**
	 * 新增用户扩展信息
	 * @param userExpand
	 * @return
	 */
	public Long addUserExpand(UserExpand userExpand);
	
	/**
	 * 更新用户扩展信息
	 * @param userExpand
	 * @return
	 */
	public int updateUserExpand(UserExpand userExpand);
	
	/**
	 * 获取用户扩展信息
	 * @param userId 用户ID
	 * @return
	 */
	public UserExpand getUserExpandByUserId(Long userId);
	
	/**
	 * 通过用户IDs获取用户信息：昵称、头像等
	 * 评价系统使用
	 * @param userIds
	 * @return
	 */
	public List<Map<String,Object>> getUserInfoByIds(String userIds);
}
