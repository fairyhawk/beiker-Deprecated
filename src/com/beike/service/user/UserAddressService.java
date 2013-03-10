package com.beike.service.user;

import java.util.List;

import com.beike.entity.user.UserAddress;
import com.beike.service.GenericService;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 10:47:22 AM     
 * @version 1.0
 */
public interface UserAddressService extends GenericService<UserAddress, Long>{
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
