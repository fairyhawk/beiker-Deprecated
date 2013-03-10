package com.beike.service.log;

import java.io.Serializable;

/**
 * <p>Title: 操作日志接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Apr 27, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface BeikeLogService {
	
	public void processLog(Serializable logObj);
}
