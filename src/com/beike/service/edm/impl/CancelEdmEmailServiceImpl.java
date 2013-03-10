package com.beike.service.edm.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.beike.dao.edm.CancelEDMMailDao;
import com.beike.service.edm.CancelEdmEmailService;

/**
 *
 * @author 赵静龙 创建时间：2012-10-18
 */
@Service("cancelEdmEmailService")
public class CancelEdmEmailServiceImpl implements CancelEdmEmailService {
	
	final static Logger logger = Logger.getLogger(CancelEdmEmailServiceImpl.class);
	@Autowired
	private CancelEDMMailDao cancelEDMMailDao;
	
	public void addCancelEdmMail(Map<String,Object> edmCancelEmailInfo){
		try {
			cancelEDMMailDao.addCancelEdmMail(edmCancelEmailInfo);
		} catch (DataIntegrityViolationException e) {
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}