package com.beike.form;

import java.util.Date;

/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: wenhua.cheng
 * @version: 1.0 Create at: 2011-4-20
 * 
 */
public class SmsInfo {

	private Date sendTime;
	private Date validTime;
	private String appendID;
	private String desMobile;
	private String content;
	private String contentType;
	//短信类型 短信类型 0用户相关短信 1交易相关短信 add by qiaowb 2011-11-07
	private String smsType;
	
	public SmsInfo() {

	}

	public SmsInfo(String desMobile, String content, String contentType) {
		this.desMobile = desMobile;
		this.content = content;
		this.contentType = contentType;
	}

	public SmsInfo(String desMobile, String content, String contentType, String smsType) {
		this.desMobile = desMobile;
		this.content = content;
		this.contentType = contentType;
		this.smsType = smsType;
	}
	
	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public String getAppendID() {
		return appendID;
	}

	public void setAppendID(String appendID) {
		this.appendID = appendID;
	}

	public String getDesMobile() {
		return desMobile;
	}

	public void setDesMobile(String desMobile) {
		this.desMobile = desMobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "SmsInfo [sendTime=" + sendTime + ", validTime=" + validTime
				+ ", appendID=" + appendID + ", desMobile=" + desMobile
				+ ", content=" + content + ", contentType=" + contentType + "]";
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

}
