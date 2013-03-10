package com.beike.service.miaosha.impl;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.service.miaosha.MiaoshaRemindService;
import com.beike.util.DateUtils;

/**      
 * project:beiker  
 * Title:秒杀通知定时
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 3, 2012 2:15:14 PM     
 * @version 1.0
 */
@Service("miaoshaRemindSendDaemon")
public class MiaoshaRemindSendDaemon {
	private static final Logger logger =Logger.getLogger(MiaoshaRemindSendDaemon.class);
	
	private static String Remind_SmsTemTitle = "SMS_MIAOSHA_REMIND";
	
	@Autowired
	private MiaoShaService miaoShaService;
	
	@Autowired
	private MiaoshaRemindService miaoshaRemindService;
	
	@Autowired
	private SmsService smsService;
	/**
	 * 发送提醒短信
	 */
	public void sendRemindSms() {
		logger.info("====MiaoshaRemindSendDaemon sendRemindSms begin====");
		Timestamp curTime = new Timestamp(System.currentTimeMillis());
		String timeS = DateUtils.getTimeBeforeOrAfterSenconds(curTime,60*10-30);
		String timeE = DateUtils.getTimeBeforeOrAfterSenconds(curTime,60*10+30);
		List<Map<String,Object>> lstMiaosha = miaoShaService.getNextBeginMiaoShaIDs(timeS, timeE);
		if(lstMiaosha!=null){
			try {
				Sms sms = smsService.getSmsByTitle(Remind_SmsTemTitle);
				String template = sms.getSmscontent();
				for(Map<String,Object> msMap:lstMiaosha){
					try{
						Long miaoshaId = (Long)msMap.get("id");
						String startTime = DateUtils.toString((Timestamp)msMap.get("m_start_time"),"d日H时m分");
						Object[] smsParam = new Object[] {msMap.get("m_short_title"), startTime};
						logger.info("sendRemindSms[miaoshaId:" + miaoshaId + "\tstartTime:" + startTime + "]");
						List<String> lstPhone = miaoshaRemindService.getRemindPhoneByMiaoshId(miaoshaId);
						if(lstPhone!=null){
							for(String mobile:lstPhone){
								String contentResult = MessageFormat.format(template,smsParam);
								SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15", "0");
								smsService.sendSms(sourceBean);
							}
							//删除已经处理过的预订通知
							miaoshaRemindService.deleteMiaoshaRemindByMiaoshId(miaoshaId);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("====MiaoshaRemindSendDaemon sendRemindSms end====");
	}
}