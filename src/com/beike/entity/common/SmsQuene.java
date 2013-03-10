package com.beike.entity.common;

import java.io.Serializable;

/**
 * <p>
 * Title: 短信队列
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
 * @date Nov 8, 2011
 * @author ye.tian
 * @version 1.0
 */

public class SmsQuene implements Serializable {

	private Long id;

	private String smscontent;

	private String mobile;

	public SmsQuene(Long id, String smscontent, String mobile) {
		this.id = id;
		this.smscontent = smscontent;
		this.mobile = mobile;
	}

	public SmsQuene() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSmscontent() {
		return smscontent;
	}

	public void setSmscontent(String smscontent) {
		this.smscontent = smscontent;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "SmsQuene [id=" + id + ", smscontent=" + smscontent
				+ ", mobile=" + mobile + "]";
	}

}
