package com.beike.entity.user;

import java.io.Serializable;

/**
 * <p>
 * Title: 用户关联的SNS、微博账户信息
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public class WeiboAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;// 主键

	private String weibo_nick;// 微博名称缩写

	private String sessionkey;// oauthz需要

	private String sessionsecret;// oauthz需要 与sessionkey成对出现

	private long userid;// user表主键

	public WeiboAccount() {

	}

	public WeiboAccount(long id, String weibo_nick, String sessionkey,
			String sessionsecret, long userid) {
		this.id = id;
		this.weibo_nick = weibo_nick;
		this.sessionkey = sessionkey;
		this.sessionsecret = sessionsecret;
		this.userid = userid;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWeibo_nick() {
		return weibo_nick;
	}

	public void setWeibo_nick(String weibo_nick) {
		this.weibo_nick = weibo_nick;
	}

	public String getSessionkey() {
		return sessionkey;
	}

	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}

	public String getSessionsecret() {
		return sessionsecret;
	}

	public void setSessionsecret(String sessionsecret) {
		this.sessionsecret = sessionsecret;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}
}
