package com.beike.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.beike.dao.common.EmailDao;

/**
 * 发送邮件的工具类
 * 指定邮件服务器，密码，发送地址，发送内容等信息，就可以发送邮件了
 * @author ye.tian
 */
public class EmailUtils {
	

	/**
	 * 
	 * @param protocol 邮件协议，默认填入“smtp”
	 * @param host 邮件服务器
	 * @param username 如果需要认证（一般情况下必须），则为认证用户名
	 * @param password 同上，密码
	 * @param props 具体的发送参数,默认包含：
	    	props.put("mail.smtp.auth", "true");
	    	props.put("mail.smtp.timeout", "25000");
	 * @param contentType 邮件头的contentType定义，类似于HTML同一元素
	 * @param content 邮件内容
	 * @param subject 邮件标题
	 * @param from 发件人
	 * @param to 收件人
	 * @param cc 抄送
	 * @param bcc 密送
	 * @param sentDate 发送时间
	 * @throws MessagingException
	 */
	public static void send(String protocol, String host, String username,
			String password, Properties props, String contentType,
			Object content, String subject, String[] from, String[] to,
			String[] cc, String[] bcc, Date sentDate) throws MessagingException {
		if (from == null || from.length < 1) {
			throw new IllegalArgumentException("no send from specified");
		}
		if (to == null || to.length < 1) {
			throw new IllegalArgumentException("no send to specified");
		}

		Session sendMailSession = Session.getInstance(props);
		Message message = new MimeMessage(sendMailSession);
		message.setSubject(subject);
		InternetAddress[] fromAddr = new InternetAddress[from.length];
		for (int i = 0; i < from.length; i++) {
			fromAddr[i] = new InternetAddress(from[i]);
		}
		message.addFrom(fromAddr);

		for (int i = 0; i < to.length; i++) {
			message.addRecipient(RecipientType.TO, new InternetAddress(to[i]));
		}
		if (cc != null && cc.length > 0) {
			for (int i = 0; i < to.length; i++) {
				message.addRecipient(RecipientType.CC, new InternetAddress(
						cc[i]));
			}
		}
		if (bcc != null && bcc.length > 0) {
			for (int i = 0; i < bcc.length; i++) {
				message.addRecipient(RecipientType.BCC, new InternetAddress(
						bcc[i]));
			}
		}
		message.setSentDate(sentDate);
		message.setContent(content, "text/html; charset=GBK");
		Transport transport = sendMailSession.getTransport(protocol);
		transport.connect(host, username, password);
		transport.sendMessage(message, message.getAllRecipients());
	}
	
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
	 * @param emailCode			邮件编号
	 * @throws MessagingException
	 */
	public static void send(String host, String username, String password, String[] from, String contentType, String content,
			String subject, String[] to, String[] cc, String[] bcc, Date date,
			Object[] args,String emailCode) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.timeout", "25000");
		
		if(args!=null && args.length>0){
			content = MessageFormat.format(content, args);
		}
		if(from == null){
			from = new String[] { username };
		}
		send("smtp", host, username, password, props, contentType,content,
				subject, from, to, cc, bcc, date);
	}

	public static void main(String[] args) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.timeout", "25000");
		send("smtp", "smtp.yeepay.com", "xxx@sinobogroup", "6mFtrq", props,
				"text/html; charset=GBK", "1234", "test",
				new String[] { "junning.li@yeepay.com" }, new String[] {
						"	" },
				null, null, new Date());
		System.out.println("ok");
	}
}