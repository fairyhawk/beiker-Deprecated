package com.beike.service.impl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.exception.BaseException;
import com.beike.dao.common.SmsDao;
import com.beike.entity.common.Sms;
import com.beike.entity.common.SmsQuene;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;

/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: wenhua.cheng
 * @version: 1.0 Create at: 2011-4-20
 */
@Service("smsService")
public class SmsServiceImpl implements SmsService {
	private static Log log = LogFactory.getLog(SmsServiceImpl.class);
	@Autowired
	private SmsUtils smsUtils;

	public static Map<String, Sms> smsMap = new java.util.concurrent.ConcurrentHashMap<String, Sms>();

	public SmsUtils getSmsUtils() {
		return smsUtils;
	}

	public void setSmsUtils(SmsUtils smsUtils) {
		this.smsUtils = smsUtils;
	}

	@Override
	public Sms getSmsByTitle(String title) throws BaseException {
		Sms sms = smsMap.get(title);
		if (sms == null) {
			sms = smsDao.getSmsByTitle(title);
		}
		if (sms == null) {
			throw new BaseException(BaseException.SMSTEMPLATE_NOT_FOUNT);
		}
		smsMap.put(title, sms);
		return sms;
	}

	@Autowired
	private SmsDao smsDao;
	@Autowired
	private EmailService emailService;

	private static ResourceBundle rb = ResourceBundle.getBundle("smsconfig");

	private static final String operId = rb.getString("operId");

	private static final String operPass = rb.getString("operPass");

	private static final String sendUrl = rb.getString("smsSend");

	private static final String smsAutoSendCount = rb
			.getString("smsAutoSendCount");

	private static final String sender = rb.getString("sender");
	private static final String toEmail = rb.getString("toer");

	/**
	 * 参数类型进行了绑定。有点耦合
	 * 
	 * @param url
	 * @param sourceBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map sendSms(SmsInfo sourceBean) {
		log.info("sendSms " + sourceBean);
		
		if(sourceBean.getDesMobile().startsWith("10000")){  //如果手机号是10000打头侧不入库.add by wenhua.cheng
			return  null;
		}
		Map<String, String> sendMap = new HashMap<String, String>();

		sendMap.put("OperID", operId);
		sendMap.put("OperPass", operPass);

		// 暂时无用。执空
		sendMap.put("SendTime", "");
		sendMap.put("ValidTime", "");

		sendMap.put("AppendID", sourceBean.getAppendID());
		sendMap.put("DesMobile", sourceBean.getDesMobile());
		sendMap.put("Content", sourceBean.getContent());
		sendMap.put("ContentType", sourceBean.getContentType());

		//List<String> resultList = new ArrayList<String>();
		try {
			//Long begin = new Date().getTime();
			// resultList = HttpUtils.URLGet(sendUrl, sendMap);
			/*
			 * SmsSendThread smsThread = new SmsSendThread(sendMap);
			 * smsThread.start(); Long end = new Date().getTime();
			 * log.info("times=" + (end - begin)+",map="+sendMap);
			 */
			// 短信保存至数据库，定时批量发送 add by qiaowb 2011-11-4
			
			smsDao.saveSmsInfo(sourceBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

		// Map<String, String> smsMap = null;
		// SmsQuene smsQuene = new SmsQuene();
		// smsQuene.setId(1L);
		// smsQuene.setMobile("13581974941");
		// smsQuene.setSmscontent("121213242342_" + new
		// java.util.Date().getTime());
		// // try {
		// // System.out.print(URLEncoder.encode("用户提交到企信s通平台的状态报告错ss误码abc",
		// // "GBK"));
		// // } catch (UnsupportedEncodingException e) {
		// // e.printStackTrace();
		// // }
		// for (int i = 0; i < 10; i++) {
		// smsQuene.setSmscontent("121213242" + i + "342_"
		// + new java.util.Date().getTime());
		// String code = SmsUtils.sendSms(smsQuene);
		// }
		String content = "您在千品网购买的“久久丫100元储值卡：”服务密码为qh200926703mm764024有效期至2012-07-06【千品网】";
		Pattern p = Pattern.compile("服务密码为qh[0-9]{9}mm[0-9]{6}");
		Matcher m = p.matcher(content);
		if (m.find()) {
			content = content.replace("服务密码为qh", "优惠券序号").replace("mm", ",密码");
		}
		System.out.println(content);

	}

	public SmsDao getSmsDao() {
		return smsDao;
	}

	public void setSmsDao(SmsDao smsDao) {
		this.smsDao = smsDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.service.common.SmsService#sendSmsInfo()
	 */
	@Override
	public String sendSmsInfo() {
		List<SmsQuene> smsList = smsDao.getSmsInfoList(smsAutoSendCount);
		StringBuffer returnStr = new StringBuffer("");
		if (smsList != null && smsList.size() > 0) {
			for (SmsQuene smsQuene : smsList) {
				String content = smsQuene.getSmscontent();
				Pattern p = Pattern.compile("服务密码为qh[0-9]{9}mm[0-9]{6}");
				Matcher m = p.matcher(content);
				if (m.find()) {
					content = content.replace("服务密码为qh", "优惠券序号").replace("mm",
							",密码");
				}
				smsQuene.setSmscontent(content);
				String sendResult = SmsUtils.sendSms(smsQuene);
				if ("1".equals(sendResult)) {
					returnStr.append(smsQuene.getId()).append(",");
					smsDao.updateSmsInfo(sendResult, operId, operPass, sendUrl,
							smsQuene);
				} else {
					// 两种通道 都不好用发邮件通知
					sendErrorEmail("两种通道短信发送失败.." + smsQuene);
				}
			}
		}
		return returnStr.toString();
	}

	private void sendErrorEmail(String content) {
		if (toEmail != null) {
			String[] emails = toEmail.split(",");
			if (emails != null && emails.length > 0) {
				for (String string : emails) {
					try {
						emailService.sendMail(string, sender, content,
								"系统短信发送失败");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
}
