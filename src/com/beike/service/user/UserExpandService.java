package com.beike.service.user;

import com.beike.common.enums.user.ProfileType;
import com.beike.entity.user.UserAddress;
import com.beike.entity.user.UserExpand;
import com.beike.form.AccessToken;
import com.beike.service.GenericService;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 10:48:30 AM     
 * @version 1.0
 */
public interface UserExpandService extends GenericService<UserExpand, Long> {
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
	 * 更新用户扩展信息、用户地址
	 * @param userExpand
	 * @return
	 */
	public int updateUserExpand(UserExpand userExpand,UserAddress userAddress);
	
	/**
	 * 更新用户头像
	 * @param userExpand
	 * @return
	 */
	public int updateUserAvatar(UserExpand userExpand);
	
	/**
	 * 获取用户扩展信息
	 * @param userId 用户ID
	 * @return
	 */
	public UserExpand getUserExpandByUserId(Long userId);
	
	/**
	 * 第三方登录用户信息同步
	 * @param userid
	 * @param accessToken
	 * @param profile
	 * @return
	 */
	public int addUserExpand(Long userid, AccessToken accessToken,ProfileType profile);
}
