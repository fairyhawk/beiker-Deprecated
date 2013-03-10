package com.beike.common.entity.adweb;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */

public class AdWeb {	
	
	
	public Long adwebid;
	
	public String adwebName;
	
	public String adwebTrxurl;
	
	public String adwebCode;

	public Long getAdwebid() {
		return adwebid;
	}

	public void setAdwebid(Long adwebid) {
		this.adwebid = adwebid;
	}

	public String getAdwebName() {
		return adwebName;
	}

	public void setAdwebName(String adwebName) {
		this.adwebName = adwebName;
	}

	public String getAdwebTrxurl() {
		return adwebTrxurl;
	}

	public void setAdwebTrxurl(String adwebTrxurl) {
		this.adwebTrxurl = adwebTrxurl;
	}

	public String getAdwebCode() {
		return adwebCode;
	}

	public void setAdwebCode(String adwebCode) {
		this.adwebCode = adwebCode;
	}
	
	public AdWeb(){
		
	}

	public AdWeb(Long adwebid, String adwebName, String adwebTrxurl,
			String adwebCode) {
		this.adwebid = adwebid;
		this.adwebName = adwebName;
		this.adwebTrxurl = adwebTrxurl;
		this.adwebCode = adwebCode;
	}

}
