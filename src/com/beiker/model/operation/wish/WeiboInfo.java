package com.beiker.model.operation.wish;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Sep 17, 2011
 * @author ye.tian
 * @version 1.0
 */

public class WeiboInfo {
	
	private String weibologo;//微博logo
	
	private String screenName;//显示名字
	
	private String weiboid;//微博id
	
	private Long invitecount;
	
	private String fromweb;
	
	private String url;//url 微博

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFromweb() {
		return fromweb;
	}

	public void setFromweb(String fromweb) {
		this.fromweb = fromweb;
	}

	public Long getInvitecount() {
		return invitecount;
	}

	public void setInvitecount(Long invitecount) {
		this.invitecount = invitecount;
	}

	public String getWeibologo() {
		return weibologo;
	}

	public void setWeibologo(String weibologo) {
		this.weibologo = weibologo;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getWeiboid() {
		return weiboid;
	}

	public void setWeiboid(String weiboid) {
		this.weiboid = weiboid;
	}
	
}
