package com.beike.service.impl.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import cn.emay.sdk.client.api.Client;

import com.beike.entity.common.SmsQuene;
import com.beike.util.sms.HttpUtils;

/**
 * <p>
 * Title:统一发送短信的开关
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date Nov 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("smsUtils")
public class SmsUtils {
	
	private static final Log			logger			= LogFactory.getLog(SmsUtils.class);
	
	public static String					EM_CONFIG		= "EM";
	
	public static String					GD_CONFIG		= "GD";
	
	private static ResourceBundle	rb					= ResourceBundle.getBundle("smsconfig");
	
	private static final String		operId			= rb.getString("operId");
	
	private static final String		operPass		= rb.getString("operPass");
	
	private static final String		sendUrl			= rb.getString("smsSend");
	
	private final static String		srcCharset	= "GBK";
	
	public static String send(String config, SmsQuene smsQuene) {
		String result = "0";
		if(EM_CONFIG.equals(config)) {
			try {
				Map<String, String> mapconfig = new HashMap<String, String>();
				mapconfig.put("mobile", smsQuene.getMobile());
				mapconfig.put("content", smsQuene.getSmscontent());
				result = sendSmsWithEm(mapconfig);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if(GD_CONFIG.equals(config)) {
			try {
				Map<String, String> sendMap = new HashMap<String, String>();
				sendMap.put("OperID", operId);
				sendMap.put("OperPass", operPass);
				// 暂时无用置空
				sendMap.put("SendTime", "");
				sendMap.put("ValidTime", "");
				sendMap.put("AppendID", "");
				sendMap.put("DesMobile", smsQuene.getMobile());
				sendMap.put("Content", smsQuene.getSmscontent());
				sendMap.put("ContentType", "8");
				// 发送短信 0 失败 1 成功
				result = sendSms(smsQuene, sendUrl, sendMap);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static List<String>	configList	= new ArrayList<String>();
	static {
		configList.add(GD_CONFIG);
		configList.add(EM_CONFIG);
	}
	
	public static void main(String[] args) {
		SmsQuene smsQuene = new SmsQuene();
		smsQuene.setMobile("13811876835");
		String content = "123";
		smsQuene.setSmscontent(content);
		String sendResult = SmsUtils.sendSms(smsQuene);
		sendResult = SmsUtils.sendSms(smsQuene);
		System.out.println(sendResult);
	}
	
	public static String sendSms(SmsQuene smsQuene) {
		String sendResult = send(configList.get(0), smsQuene);
		if("0".equals(sendResult)) {
			logger.info("############################sms first channel error:" + configList + "###########################");
			synchronized (configList) {
				Collections.reverse(configList);
			}
			logger.info("############################sms first channel reverse:" + configList + "###########################");
			try {
				String[] mobiles = new String[]{"13581974941", "13811876835"};
				for(String mobile : mobiles) {
					SmsQuene s = new SmsQuene();
					s.setMobile(mobile);
					String content = "sms channel reverse" + configList;
					s.setSmscontent(content);
					SmsUtils.send(configList.get(0), s);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			sendResult = SmsUtils.send(configList.get(0), smsQuene);
		}
		
		return sendResult;
	}
	
	static ResourceBundle	bundle	= PropertyResourceBundle.getBundle("smsconfig");
	private static Client	client	= null;
	
	public synchronized static Client getClient() {
		if(null == client) {
			try {
				client = new Client(bundle.getString("softwareSerialNo1"), bundle.getString("key1"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	private static String sendSmsWithEm(Map<String, String> sendMap) {
		String result = "0";
		String mobile = sendMap.get("mobile");
		String content = sendMap.get("content");
		if(content.indexOf("【千品网】") == -1) {
			content += "【千品网】";
		}
		int i = 0;
		int code = 0;
		// 加入重试机制
		while (i < 3) {
			try {
				code = getClient().sendSMS(new String[]{mobile}, content, 5);
				// code = ClientWithConifg.getInstance().sendSMS("", new
				// String[]{mobile}, content, "", srcCharset, 5);
				logger.info("em sms content return code:" + code + ",mobile=" + mobile + ",content=" + content + ",i=" + i);
				if(code != 0) {
					i++;
					Random random = new Random();
					Thread.sleep(500 + random.nextInt(500));
				} else {
					result = "1";
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
				i++;
				Random random = new Random();
				try {
					Thread.sleep(500 + random.nextInt(500));
				} catch(InterruptedException e1) {}
			}
		}
		return result;
	}
	
	private static String sendSms(SmsQuene smsQuene, String sendUrl, Map<String, String> sendMap) {
		String sendResult = "0";
		try {
			int i = 0;
			// 加入重试机制
			while (i < 3) {
				List list = HttpUtils.URLGet(sendUrl, sendMap);
				StringBuffer buffer = new StringBuffer();
				if(list != null) {
					for(int j = 0; j < list.size(); j++) {
						buffer.append(list.get(j));
					}
				}
				logger.info("gd sms content return list:" + buffer + ",sendMap=" + sendMap + ",i=" + i);
				if(list != null && list.size() > 0) {
					String result = (String) list.get(0);
					if(result.indexOf("<code>03</code>") >= 0) {
						sendResult = "1";
						return sendResult;
					}
				}
				i++;
				Random random = new Random();
				Thread.sleep(500 + random.nextInt(500));
				
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sendResult;
	}
	
	//	public static void main(String[] args) {
	//		Map<String, String> sendMap = new HashMap<String, String>();
	//		sendMap.put("OperID", operId);
	//		sendMap.put("OperPass", operPass);
	//		// 暂时无用置空
	//		sendMap.put("SendTime", "");
	//		sendMap.put("ValidTime", "");
	//		sendMap.put("AppendID", "");
	//		sendMap.put("DesMobile", "13811876835");
	//		sendMap.put("Content", "hello 帅哥 hoho ");
	//		sendMap.put("ContentType", "15");
	//		// 发送短信 0 失败 1 成功
	//		String result = sendSms(null, sendUrl, sendMap);
	//		System.out.println(result);
	//	}
}
