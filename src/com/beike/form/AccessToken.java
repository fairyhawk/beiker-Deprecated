package com.beike.form;

import java.io.Serializable;

/**
 * <p>Title: 未进行类设计的微博AccessToken  简单处理</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class AccessToken implements Serializable{
	//头像
	private String headIcon;
	//性别
	private int gender;
	
	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
	
}
