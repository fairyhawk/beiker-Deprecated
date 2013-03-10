package com.beike.service.adweb;
/**      
 * project:beiker  
 * Title:广告联盟日志
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 2, 2012 5:28:27 PM     
 * @version 1.0
 */
public interface AdWebLogService {
	/**
	 * 增加广告联盟日志
	 * @param adcid
	 * @param adwi
	 * @param adcode
	 * @return
	 */
	public Long addAdWebLog(String adcid,String adwi,String adcode);
}
