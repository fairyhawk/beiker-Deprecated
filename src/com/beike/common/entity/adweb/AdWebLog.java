package com.beike.common.entity.adweb;

import java.util.Date;

/**      
 * project:beiker  
 * Title:广告联盟日志
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 2, 2012 5:19:09 PM     
 * @version 1.0
 */
public class AdWebLog {
	private Long adweblogId;
	private String adcid;
	private String adwi;
	private String adcode;
	private Date accessDate;
	
	public Long getAdweblogId() {
		return adweblogId;
	}
	public void setAdweblogId(Long adweblogId) {
		this.adweblogId = adweblogId;
	}
	public String getAdcid() {
		return adcid;
	}
	public void setAdcid(String adcid) {
		this.adcid = adcid;
	}
	public String getAdwi() {
		return adwi;
	}
	public void setAdwi(String adwi) {
		this.adwi = adwi;
	}
	public String getAdcode() {
		return adcode;
	}
	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}
	public Date getAccessDate() {
		return accessDate;
	}
	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}
}
