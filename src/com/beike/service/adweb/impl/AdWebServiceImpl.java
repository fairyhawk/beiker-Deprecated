package com.beike.service.adweb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.adweb.AdWeb;
import com.beike.dao.adweb.AdWebDao;
import com.beike.service.adweb.AdWebService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */
@Service("adWebService")
public class AdWebServiceImpl implements AdWebService {

	@Override
	public AdWeb getAdWebByCode(String code) {
		return adWebDao.getAdWebByCode(code);
	}
	
	@Autowired
	private AdWebDao adWebDao;
	
	
	public AdWebDao getAdWebDao() {
		return adWebDao;
	}


	public void setAdWebDao(AdWebDao adWebDao) {
		this.adWebDao = adWebDao;
	}
}
