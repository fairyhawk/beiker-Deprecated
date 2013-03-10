package com.beike.entity.user;

import java.io.Serializable;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 7:04:20 PM     
 * @version 1.0
 */
public class UserExpand implements Serializable {
	private Long id;
	//用户ID
	private Long userId;
	//昵称
	private String nickName;
	//真实姓名
	private String realName;
	//性别
	private int gender;
	//头像
	private String avatar;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public UserExpand(){
	}
	
	public UserExpand(Long userId, String nickName, String realName,
			int gender, String avatar) {
		this.userId = userId;
		this.nickName = nickName;
		this.realName = realName;
		this.gender = gender;
		this.avatar = avatar;
	}
}