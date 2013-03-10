package com.beike.service.impl.common;

import java.io.File;
import java.sql.Timestamp;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.beike.service.common.SmsService;

/**
 * project:beiker Title:定时发送短信 Description: Copyright:Copyright (c) 2011
 * Company:Sinobo
 * 
 * @author qiaowb
 * @date Nov 4, 2011 1:26:21 PM
 * @version 1.0
 */
@Service("smsSendAutoDaemon")
public class SmsSendAutoDaemon implements InitializingBean {
	public Log logger = LogFactory.getLog(SmsSendAutoDaemon.class);
	@Resource(name = "smsService")
	private SmsService smsService;

	public void executeAutoSendSms() {
		logger.info("==============executeAutoSendSms start time:"
				+ new Timestamp(System.currentTimeMillis()) + "==============");
		smsService.sendSmsInfo();
		logger.info("==============executeAutoSendSms end time:"
				+ new Timestamp(System.currentTimeMillis()) + "==============");
	}

	@Override
	public void afterPropertiesSet() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (true) {
					File file = new File("/home/work/sendSms/send_sms");
					if (file.exists()) {
						try {
							if (smsService == null) {
								return;
							}
							String returnStr = "";
							try {
								returnStr = smsService.sendSmsInfo();
							} catch (NullPointerException e1) {
								e1.printStackTrace();
								return;
							}
							if (returnStr.equals("")) {
								try {
									sleep(2000);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						return;
					}

				}
			}
		};
		thread.start();
	}
}