package com.beike.dao.adweb;

import com.beike.common.entity.adweb.AdWebLog;
import com.beike.dao.GenericDao;

/**      
 * project:beiker  
 * Title:广告联盟日志
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 2, 2012 5:17:18 PM     
 * @version 1.0
 */
public interface AdWebLogDao extends GenericDao<AdWebLog, Long>{
	/**
	 * 增加广告联盟日志
	 * @param adcid
	 * @param adwi
	 * @param adcode
	 * @return
	 */
	public Long addAdWebLog(String adcid, String adwi, String adcode);
}
