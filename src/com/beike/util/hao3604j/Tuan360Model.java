package com.beike.util.hao3604j;

import java.io.Serializable;

import com.beike.form.AccessToken;
 /**
 * com.beike.util.hao3604j.Tuan360Model.java
 * @description:360Model
 * @author xuxiaoxian
 * Company: Sinobo
 *@date: Apr 16, 2012，xuxiaoxian，create class
 */


public class Tuan360Model extends AccessToken implements Serializable  {
	private String token;
	private String tokenSecret;
	private String qid;
	private String qname;
	private String qmail;
	private String from;
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public String getQname() {
		return qname;
	}
	public void setQname(String qname) {
		this.qname = qname;
	}
	public String getQmail() {
		return qmail;
	}
	public void setQmail(String qmail) {
		this.qmail = qmail;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	
}
