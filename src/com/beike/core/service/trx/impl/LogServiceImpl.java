package com.beike.core.service.trx.impl;    

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.LogService;

/**   
 * @Title: LogServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: TODO
 * @date Jun 29, 2011 10:25:53 PM
 * @author wh.cheng
 * @version v1.0   
 */
@Service("logService")
public class LogServiceImpl implements LogService {
	private Log logger =LogFactory.getLog(LogServiceImpl.class);
	public void saveLog() {
		// TODO Auto-generated method stub
		
		logger.info("+++++++++++++I am log advice");
		System.out.print("++++++++++++I am log advice");
		
	}
	
	

}
 