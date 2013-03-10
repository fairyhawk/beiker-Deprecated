package com.beike.form;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class RenrenSessionKey extends WeiboSession {
	private String sessionkey;
	private int renrenid;

	public String getSessionkey() {
		return sessionkey;
	}

	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}

	public int getRenrenid() {
		return renrenid;
	}

	public void setRenrenid(int renrenid) {
		this.renrenid = renrenid;
	}
}
