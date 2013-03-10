package com.beike.service.common;

import java.util.Date;

import javax.mail.MessagingException;

/**
 * <p>
 * Title: 邮件相关服务
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
 * @author ye.tian modify by wenhua.cheng
 * @version 1.0
 */

public interface EmailService {
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
	// modify by wenhua.cheng
	/*
	 * public void send(String protocol, String host, String username, String
	 * password, Properties props, String contentType, Object content, String
	 * subject, String[] from, String[] to, String[] cc, String[] bcc, Date
	 * sentDate) throws Exception;
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
			String emailCode) throws Exception;
	
	public void sendMail(String mailto, String fromEmail, String text, String title) throws Exception;
}
