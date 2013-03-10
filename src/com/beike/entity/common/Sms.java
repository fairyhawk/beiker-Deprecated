package com.beike.entity.common;

import java.io.Serializable;

/**
 * <p>Title: 短信实体</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 6, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Sms implements Serializable{
	
	private int id;
	
	private String smstitle;
	
	private String smscontent;
	
	private String smstype;
	
	public Sms(){
		
	}

	public Sms(int id, String smstitle, String smscontent, String smstype) {
		this.id = id;
		this.smstitle = smstitle;
		this.smscontent = smscontent;
		this.smstype = smstype;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSmstitle() {
		return smstitle;
	}

	public void setSmstitle(String smstitle) {
		this.smstitle = smstitle;
	}

	public String getSmscontent() {
		return smscontent;
	}

	public void setSmscontent(String smscontent) {
		this.smscontent = smscontent;
	}

	public String getSmstype() {
		return smstype;
	}

	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}
}
