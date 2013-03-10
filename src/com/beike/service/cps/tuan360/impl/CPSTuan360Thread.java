package com.beike.service.cps.tuan360.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.beike.service.cps.tuan360.CPSTuan360Service;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 9, 2012 3:00:52 PM     
 * @version 1.0
 */
public class CPSTuan360Thread extends Thread {
	private CPSTuan360Service cpsTuan360Service;
	private Map<String,Object> params;
	private int status;
	
	private static final Logger logger = Logger.getLogger(CPSTuan360Thread.class);

	public CPSTuan360Thread(CPSTuan360Service cpsTuan360Service,
			Map<String, Object> params, int status) {
		this.cpsTuan360Service = cpsTuan360Service;
		this.params = params;
		this.status = status;
	}

	@Override
	public void run() {
		try{
			logger.info("CPSTuan360Thread.run,status===" + status + "params===" + params);
			if(status == 0){
				cpsTuan360Service.saveOrderNoPay(params);
			}else if(status == 1){
				if(params!=null){
					String trxOrderId = (String)params.get("trxorder_id");
					if(StringUtils.isNotEmpty(trxOrderId)){
						//状态有1变为5
						cpsTuan360Service.updateOrderStatus(Long.parseLong(trxOrderId), 5, 1);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
