package com.beike.form;
/**
 * <p>Title:校内AccessToken 未设计类版本 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class XiaoNeiAccessToken extends AccessToken{
	private int renren_id;
	private String screenName;
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public int getRenren_id() {
		return renren_id;
	}

	public void setRenren_id(int renrenId) {
		renren_id = renrenId;
	}

	private String access_token;
	private long expires_in;
	private String refresh_token;
	private String scope;
	private String create_time;

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String createTime) {
		create_time = createTime;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String accessToken) {
		access_token = accessToken;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expiresIn) {
		expires_in = expiresIn;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refreshToken) {
		refresh_token = refreshToken;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
