package com.beike.service.impl.common;

import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.beike.common.exception.BaseException;
import com.beike.dao.common.EmailDao;
import com.beike.entity.common.Email;
import com.beike.service.common.EmailService;

/**
 * <p>
 * Title:邮件服务
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
 * @date May 5, 2011
 * @author ye.tian modify by wenhua.cheng 换JMAIL 配置改为从Spring 中读取
 * @version 1.0
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {
	/*
	 * private final PropertyUtil propertyUtil = PropertyUtil
	 * .getInstance(Constant.PROPERTY_FILE_NAME); private final String host =
	 * propertyUtil .getProperty(Constant.EMAIL_HOST_CONFIG); private final
	 * String emailusername = propertyUtil
	 * .getProperty(Constant.EMAIL_USERNAME); private final String emailpassword
	 * = propertyUtil .getProperty(Constant.EMAL_PASSWORD); private final String
	 * contentType = propertyUtil .getProperty(Constant.EMAIL_CONTENTTYPE);
	 */

	@Autowired
	private EmailDao emailDao;

	@Autowired
	private JavaMailSenderImpl javaMailSender;

	public static Map<String, Email> emailMap = new java.util.concurrent.ConcurrentHashMap<String, Email>();

	public EmailDao getEmailDao() {
		return emailDao;
	}

	public void setEmailDao(EmailDao emailDao) {
		this.emailDao = emailDao;
	}

	/**
	 * 
	 * @param protocol
	 *            邮件协议，默认填入“smtp”
	 * @param host
	 *            邮件服务器
	 * @param username
	 *            如果需要认证（一般情况下必须），则为认证用户名
	 * @param password
	 *            同上，密码
	 * @param props
	 *            具体的发送参数,默认包含： props.put("mail.smtp.auth", "true");
	 *            props.put("mail.smtp.timeout", "25000");
	 * @param contentType
	 *            邮件头的contentType定义，类似于HTML同一元素
	 * @param content
	 *            邮件内容
	 * @param subject
	 *            邮件标题
	 * @param from
	 *            发件人
	 * @param to
	 *            收件人
	 * @param cc
	 *            抄送
	 * @param bcc
	 *            密送
	 * @param sentDate
	 *            发送时间
	 * @throws MessagingException
	 */
	/*
	 * public void send(String protocol, String host, String username, String
	 * password, Properties props, String contentType, Object content, String
	 * subject, String[] from, String[] to, String[] cc, String[] bcc, Date
	 * sentDate) throws Exception { if (from == null || from.length < 1) { throw
	 * new IllegalArgumentException("no send from specified"); } if (to == null
	 * || to.length < 1) { throw new
	 * IllegalArgumentException("no send to specified"); } Session
	 * sendMailSession = Session.getInstance(props); Message message = new
	 * MimeMessage(sendMailSession); message.setSubject(subject);
	 * InternetAddress[] fromAddr = new InternetAddress[from.length]; for (int i
	 * = 0; i < from.length; i++) { fromAddr[i] = new InternetAddress(from[i]);
	 * } message.addFrom(fromAddr); for (int i = 0; i < to.length; i++) {
	 * message.addRecipient(RecipientType.TO, new InternetAddress(to[i])); } if
	 * (cc != null && cc.length > 0) { for (int i = 0; i < to.length; i++) {
	 * message.addRecipient(RecipientType.CC, new InternetAddress( cc[i])); } }
	 * if (bcc != null && bcc.length > 0) { for (int i = 0; i < bcc.length; i++)
	 * { message.addRecipient(RecipientType.BCC, new InternetAddress( bcc[i]));
	 * } } message.setSentDate(sentDate); message.setContent(content,
	 * "text/html; charset=GBK"); Transport transport =
	 * sendMailSession.getTransport(protocol); transport.connect(host, username,
	 * password); transport.sendMessage(message, message.getAllRecipients()); }
	 */

	/**
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @param from
	 * @param contentType
	 * @param content
	 * @param subject
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param date
	 * @param args
	 * @param emailCode
	 *            邮件编号
	 * @throws MessagingException
	 */
	public void send(String host, String username, String password,
			String[] from, String contentType, String subject, String[] to,
			String[] cc, String[] bcc, Date date, Object[] args,
			String emailCode) throws Exception {
		/*
		 * if(host==null){ host=this.host; } if(username==null){
		 * username=this.emailusername; } if(password==null){
		 * password=this.emailpassword; } if(contentType==null){
		 * contentType=this.contentType; } Properties props = new Properties();
		 * props.put("mail.smtp.auth", "true"); props.put("mail.smtp.timeout",
		 * "25000");
		 */
		String content = "";
		Email email = null;
		String fromEmail = javaMailSender.getUsername();
		try {
			email = emailMap.get(emailCode);
			if (email == null) {
				email = emailDao.findEmailTemplate(emailCode);

				emailMap.put(emailCode, email);

			}

		} catch (BaseException e) {
			e.printStackTrace();
		}
		if (email == null)
			throw new Exception();
		content = email.getTemplatecontent();
		content = content.trim();
		subject = email.getTemplatesubject();
		// if (args != null && args.length > 0) {
		// content = MessageFormat.format(content, args);
		// }
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				content = content.replace("{" + i + "}", args[i].toString());
			}
		}
		/*
		 * if (from == null) { from = new String[] { username }; }
		 */
		/*
		 * send("smtp", host, username, password, props, contentType, content,
		 * email.getTemplatesubject(), from, to, cc, bcc, date);
		 */
		StringBuilder sbTo = new StringBuilder();
		for (String item : to) {
			sbTo.append(item);
		}
		System.out.println("send email content:" + content + "   over!");
		try {
			sendMail(sbTo.toString(), fromEmail, content, subject);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void sendMail(String mailto, String fromEmail, String text,
			String title) throws Exception {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
				true, "UTF-8");
		messageHelper.setFrom(new InternetAddress(fromEmail));
		messageHelper.setSubject(title);
		messageHelper.setText(text, true);
		messageHelper.setTo(new InternetAddress(mailto));
		mimeMessage = messageHelper.getMimeMessage();
		// javaMailSender.send(mimeMessage);

		EmailThread et = new EmailThread(mimeMessage);
		et.start();

	}

	class EmailThread extends Thread {

		private final MimeMessage mimeMessage;

		public EmailThread(MimeMessage mimeMessage) {
			this.mimeMessage = mimeMessage;
		}

		@Override
		public void run() {
			javaMailSender.send(mimeMessage);
		}

	}
}
