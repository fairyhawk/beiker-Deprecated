package com.beike.service.impl.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.GenericDao;
import com.beike.dao.user.UserAddressDao;
import com.beike.entity.user.UserAddress;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.user.UserAddressService;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 10:59:27 AM     
 * @version 1.0
 */
@Service("userAddressService")
public class UserAddressServiceImpl extends GenericServiceImpl<UserAddress,Long> implements
		UserAddressService {

	@Autowired
	private UserAddressDao userAddressDao;
	
	/* (non-Javadoc)
	 * @see com.beike.service.impl.GenericServiceImpl#getDao()
	 */
	@Override
	public GenericDao getDao() {
		return userAddressDao;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserAddressService#addUserAddress(com.beike.entity.user.UserAddress)
	 */
	@Override
	public Long addUserAddress(UserAddress address) {
		return userAddressDao.addUserAddress(address);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserAddressService#getUserAddressById(java.lang.Long)
	 */
	@Override
	public List<UserAddress> getUserAddressByUserId(Long userId) {
		return userAddressDao.getUserAddressByUserId(userId);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserAddressService#updateUserAddress(com.beike.entity.user.UserAddress)
	 */
	@Override
	public int updateUserAddress(UserAddress address) {
		return userAddressDao.updateUserAddress(address);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.GenericService#findById(java.io.Serializable)
	 */
	@Override
	public UserAddress findById(Long id) {
		return null;
	}
}
