package com.beike.util.tencent;

import java.io.Serializable;

import com.beike.form.AccessToken;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Sep 13, 2011
 * @author ye.tian
 * @version 1.0
 */

public class ResModel extends AccessToken implements Serializable {
	
	private String token;
	private String tokenSecret;
	private String weiboname;
	private String oauth_verifier;
	private String weiboid;
	private String head;
	
	private String nickName;
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getWeiboid() {
		return weiboid;
	}
	public void setWeiboid(String weiboid) {
		this.weiboid = weiboid;
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
	public String getWeiboname() {
		return weiboname;
	}
	public void setWeiboname(String weiboname) {
		this.weiboname = weiboname;
	}
	public String getOauth_verifier() {
		return oauth_verifier;
	}
	public void setOauth_verifier(String oauth_verifier) {
		this.oauth_verifier = oauth_verifier;
	}
}
