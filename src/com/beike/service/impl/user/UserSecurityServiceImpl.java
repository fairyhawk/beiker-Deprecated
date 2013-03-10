package com.beike.service.impl.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.user.UserSecurityDao;
import com.beike.service.user.UserSecurityService;


@Service("userSecurityService")
public class UserSecurityServiceImpl implements UserSecurityService {

	
	@Autowired
	private UserSecurityDao userSecurityDao;
	@Override
	public Map getUserBySign(String sign) {
		return userSecurityDao.getUserBySign(sign);
	}

	@Override
	public void updateSign(String sign) {
		 userSecurityDao.updateSign(sign);

	}

}
