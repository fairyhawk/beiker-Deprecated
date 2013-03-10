package com.beike.util.tencent;



public class OauthKey {
	public String customKey;
	public String customSecrect;
	public String tokenKey;
	public String tokenSecrect;
	public String verify;
	public String callbackUrl;
	
	public String tencentid;
	
	public String screenname;
	
	public String getTokenKey() {
		return tokenKey;
	}

	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}

	public String getTokenSecrect() {
		return tokenSecrect;
	}

	public void setTokenSecrect(String tokenSecrect) {
		this.tokenSecrect = tokenSecrect;
	}

	public String getTencentid() {
		return tencentid;
	}

	public void setTencentid(String tencentid) {
		this.tencentid = tencentid;
	}

	public String getScreenname() {
		return screenname;
	}

	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}

	public OauthKey() {
		customKey = null;
		customSecrect = null;
		tokenKey = null;
		tokenSecrect = null;
		verify = null;
		callbackUrl =null;
	}
}
