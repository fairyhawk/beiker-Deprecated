package com.beike.service.lucene.search;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beike.service.common.EmailService;

@Component("luceneAlertService")
public class LuceneAlertService {

	@Autowired
	private  EmailService emailService;






	/**
	 * 
	 * janwen
	 * 
	 * @param mailto
	 *            收件人
	 * @param subject
	 *            邮件主题,可以忽略
	 * @param emailbody
	 *            邮件内容
	 * @param emailcode
	 *            邮件在数据库代码 "SEARCH_SERVICE_ALERT"
	 * @throws Exception
	 * 
	 */
	public  void sendMail(String subject,String mailto, String[] emailbody,
			String emailcode) throws Exception {
		emailService.send(null, null, null, null, null, subject,
				new String[] { mailto }, null, null, new Date(), emailbody,
				emailcode);
	}

	
}
