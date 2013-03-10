package com.beike.service.common;

import java.util.Map;

import com.beike.common.exception.BaseException;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;


/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: wenhua.cheng
 * @version: 1.0 Create at: 2011-4-20
 * 
 */
public interface SmsService {

	public Map sendSms(SmsInfo sourceBean);


	/**
	 * 短信模板标题查找相应短信模板
	 * @param title		短信标题
	 * @return
	 */
	public Sms getSmsByTitle(String title)throws BaseException;
	
	/**
	 * 发送短信，从配置文件读取发送相关参数
	 * @return
	 */
	public String sendSmsInfo();
}
