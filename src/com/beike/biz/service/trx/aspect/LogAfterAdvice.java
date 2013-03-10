/*package com.beike.biz.service.trx.aspect;    

import org.aspectj.apache.bcel.classfile.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.LogService;

*//**   
 * @Title: LogAfterAdvice.java
 * @Package com.beike.biz.service.trx.aspect
 * @Description: TODO
 * @date Jun 29, 2011 10:12:02 PM
 * @author wh.cheng
 * @version v1.0   
 *//*

public class LogAfterAdvice{
	
	@Autowired
	private LogService logService;
	
	public void afterReturning(Object returnObj, Method method, Object[] args,   
			                Object targetObj) throws Throwable{   
			        if(method.getName().equals("saveLog")) return;   
			        for(int i = 0; i < args.length; i++){   
			            if(args[i] instanceof LogVO){   
			                log.info("开始写入日志......");   
			                writeLog((LogVO)args[i]);   
			            }   
			        }   
			        
			        logService.saveLog();
			   }   
			
			   private void writeLog(LogVO vo){   
			      try {   
			           vo.setDescription(vo.getDescription() + "成功!");   
			           logService.saveLog(vo);   
			       } catch (RuntimeException e){   
			          log.error(e);   
			   }   
			     
			



}
 */