package com.beike.dao.user;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.user.UserAddress;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 7:01:40 PM     
 * @version 1.0
 */
public interface UserAddressDao extends GenericDao<UserAddress, Long> {
	/**
	 * 新增用户地址信息
	 * @param userExpand
	 * @return
	 */
	public Long addUserAddress(UserAddress address);
	
	/**
	 * 更新用户地址信息
	 * @param userExpand
	 * @return
	 */
	public int updateUserAddress(UserAddress address);
	
	/**
	 * 获取用户地址信息
	 * @param userId 用户ID
	 * @return
	 */
	public List<UserAddress> getUserAddressByUserId(Long userId);
}
