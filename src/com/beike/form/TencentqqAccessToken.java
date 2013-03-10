/**
 * 
 */
package com.beike.form;

/**
 * <p>Title:腾讯QQ AccessToken 未设计类版本 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date 2011-12-15
 * @author qiaowb
 * @version 1.0
 */
public class TencentqqAccessToken extends AccessToken {
	private String openid;//openID
	private String screenName;//显示名称
	private String access_token;//要获取的Access Token
	
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
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
}