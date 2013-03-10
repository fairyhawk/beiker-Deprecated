package com.beike.dao.adweb;

import com.beike.common.entity.adweb.AdWeb;
import com.beike.dao.GenericDao;

/**
 * <p>Title:广告联盟 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */

public interface AdWebDao extends GenericDao<AdWeb, Long>{
	
	
	/**
	 * 根据web
	 * @param code
	 * @return
	 */
	public AdWeb getAdWebByCode(String code);
	
	

}
