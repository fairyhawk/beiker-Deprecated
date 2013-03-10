package com.beike.service.adweb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.adweb.AdWebLogDao;
import com.beike.service.adweb.AdWebLogService;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 2, 2012 5:29:45 PM     
 * @version 1.0
 */
@Service("adWebLogService")
public class AdWebLogServiceImpl implements AdWebLogService {
	@Autowired
	private AdWebLogDao adWebLogDao;
	
	public AdWebLogDao getAdWebLogDao() {
		return adWebLogDao;
	}

	public void setAdWebLogDao(AdWebLogDao adWebLogDao) {
		this.adWebLogDao = adWebLogDao;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.adweb.AdWebLogService#addAdWebLog(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Long addAdWebLog(String adcid, String adwi, String adcode) {
		return adWebLogDao.addAdWebLog(adcid, adwi, adcode);
	}

}
