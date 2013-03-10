package com.beike.util.tencent;

import java.io.Serializable;

/**
 * <p>Title:腾讯微博用户信息 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Sep 13, 2011
 * @author ye.tian
 * @version 1.0
 */

public class TencentUser implements Serializable {
	
	private String name;
	
	private String nickname;
	
	private String uid;
	
	private String email;
	
	private String fansnum;
	
	private String idolnum;
	
	private String head;

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFansnum() {
		return fansnum;
	}

	public void setFansnum(String fansnum) {
		this.fansnum = fansnum;
	}

	public String getIdolnum() {
		return idolnum;
	}

	public void setIdolnum(String idolnum) {
		this.idolnum = idolnum;
	}
	
	public TencentUser(){
		
	}
	
	public TencentUser(String name, String nickname, String uid, String email,
			String fansnum, String idolnum) {
		this.name = name;
		this.nickname = nickname;
		this.uid = uid;
		this.email = email;
		this.fansnum = fansnum;
		this.idolnum = idolnum;
	}
}
