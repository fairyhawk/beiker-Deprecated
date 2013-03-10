package com.beike.dao.common;

import com.beike.common.exception.BaseException;
import com.beike.dao.GenericDao;
import com.beike.entity.common.Email;

/**
 * <p>Title:Email实体操作 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface EmailDao extends GenericDao<Email, Integer>{
	
	/**
	 * 根据编号查询email模板
	 * @param templateCode
	 * @return
	 */
	public Email findEmailTemplate(String templateCode)throws BaseException;
}
