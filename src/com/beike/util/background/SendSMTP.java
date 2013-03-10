package com.beike.util.background;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendSMTP {

	private static String[] emailparameter = CommonIni.getEmailParameter();
	private static String mailKey = "gh%7t65$";

	/**
	 * 功能：自动发送邮件。
	 * 
	 * @param smtpHost
	 *            :smtp服务器地址。
	 * @param from
	 *            :发送人email地址。
	 * @param to
	 *            :收件人email地址。
	 * @param subject
	 *            :标题。
	 * @param message
	 *            :内容。
	 */
	public void sendMail(String smtpHost, String from, String to,
			String subject, String message) {
		try {
			// Set up the default parameters.
			Properties props = new Properties();
			props.put("mail.transport.protocol", emailparameter[0]);
			props.put("mail.smtp.host", emailparameter[1]);
			props.put("mail.smtp.port", emailparameter[2]);
			props.put("mail.smtp.auth", emailparameter[3]);
			MyAuthenticator auth = new MyAuthenticator(emailparameter[4],
					emailparameter[5]);
			// Create the session adn create a new mail message
			Session mailSession = Session.getInstance(props, auth);
			Message msg = new MimeMessage(mailSession);

			// Set the FROM, TO, DATE and SUBJECT fields
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			msg.setSentDate(new Date());
			msg.setSubject(subject);

			// Creste the body of the mail
			msg.setText(message);

			// Ask the Transport class to send our mail message
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	} // End of method sendMail().

	/**
	 * 功能：发送注册的确认邮件。
	 * 
	 * @param email
	 *            : 用户注册的email。
	 */
	public static boolean sendRegistMail(String userName, String email,
			String password) {
		try {
			// Set up the default parameters.
			Properties props = new Properties();
			props.put("mail.transport.protocol", emailparameter[0]);
			props.put("mail.smtp.host", emailparameter[1]);
			props.put("mail.smtp.port", emailparameter[2]);
			props.put("mail.smtp.auth", emailparameter[3]);
			MyAuthenticator auth = new MyAuthenticator(emailparameter[4],
					emailparameter[5]);
			// Create the session adn create a new mail message
			Session mailSession = Session.getInstance(props, auth);
			Message msg = new MimeMessage(mailSession);

			// Set the FROM, TO, DATE and SUBJECT fields
			msg.setFrom(new InternetAddress(emailparameter[4], new String(
					emailparameter[6].getBytes("ISO-8859-1"), "gbk")));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			msg.setSentDate(new Date());
			msg.setSubject("尊敬的" + userName + "，您在智联招聘的i聘账号！");

			//
			BufferedReader in = new BufferedReader(new FileReader(
					CommonIni.getProperty("userMailTemplate")));
			String result = "";
			String s;
			while ((s = in.readLine()) != null) {
				result = result + s + "\r\n";
			}

			in.close();

			result = result.replaceAll("\\$user ", userName);
			result = result.replaceAll("\\$password ", password);

			// 判断发送的Mime类型
			Multipart mp = new MimeMultipart();

			MimeBodyPart mbp = new MimeBodyPart();

			// 设置邮件发送数据的类型
			// mbp.setText(result);
			mbp.setHeader("Content-Type", "text/html;charset=gb2312");
			mbp.setContent(result, "text/html;charset=gb2312");

			mp.addBodyPart(mbp);
			msg.setContent(mp);

			// Ask the Transport class to send our mail message
			Transport.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

} // End of class SendSMTP.

class MyAuthenticator extends Authenticator {
	private final String accountID;
	private final String password;

	MyAuthenticator(String accountID, String password) {
		super();
		this.accountID = accountID;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(accountID, password);
	}
}