/**
 * 
 */
package com.beike.form;

/**
 * <p>Title:百度AccessToken 未设计类版本 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date 2011-10-14
 * @author qiaowb
 * @version 1.0
 */
public class BaiduAccessToken extends AccessToken {
	private String baidu_id;//百度ID
	private String screenName;//显示名称
	private String access_token;//要获取的Access Token
	private long expires_in;//Access Token的有效期，以秒为单位
	private String refresh_token;//用于刷新Access Token 的 Refresh Token,并不是所有应用都会返回该参数
	private String scope;//Access Token最终的访问范围，即用户实际授予的权限列表（用户在授权页面时，有可能会取消掉某些请求的权限）
	private String session_key;//基于http调用Open API时所需要的Session Key，其有效期与Access Token一致
	private String session_secret;//基于http调用Open API时计算参数签名用的签名密钥
	
	public String getBaidu_id() {
		return baidu_id;
	}
	public void setBaidu_id(String baidu_id) {
		this.baidu_id = baidu_id;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getSession_key() {
		return session_key;
	}
	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	public String getSession_secret() {
		return session_secret;
	}
	public void setSession_secret(String session_secret) {
		this.session_secret = session_secret;
	}
}