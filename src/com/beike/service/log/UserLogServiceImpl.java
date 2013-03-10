package com.beike.service.log;

import java.io.Serializable;

import org.springframework.stereotype.Service;

/**
 * <p>Title:用户日志service</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("userLogService")
public class UserLogServiceImpl implements BeikeLogService {

	public void processLog(Serializable logObj) {
		System.out.println(logObj);
	}

}
