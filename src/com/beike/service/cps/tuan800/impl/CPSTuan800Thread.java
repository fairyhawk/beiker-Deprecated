package com.beike.service.cps.tuan800.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.beike.service.cps.tuan800.CPSTuan800Service;

public class CPSTuan800Thread extends Thread {

	private final CPSTuan800Service cpsTuan800Service;

	private final Map<String, Object> params;

	private final int status;
	
	private static final Logger logger = Logger.getLogger(CPSTuan800Thread.class);

	public CPSTuan800Thread(CPSTuan800Service cpsTuan800Service,
			Map<String, Object> params, int status) {
		this.cpsTuan800Service = cpsTuan800Service;
		this.params = params;
		this.status = status;

	}

	@Override
	public void run() {
		if (status == 0) {
			logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
					+ this.params);
			cpsTuan800Service.saveOrderNoPay(this.params);
		} else if (status == 1) {
			logger.info("----------------------------------------------------------------"
					+ this.params);
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cpsTuan800Service.saveOrderPay(this.params);
		}else if(status == 2){
			//退款
			cpsTuan800Service.cancelOrder(params);
		}
	}

}
