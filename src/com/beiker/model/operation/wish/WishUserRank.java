package com.beiker.model.operation.wish;

import java.io.Serializable;

public class WishUserRank implements Serializable {

	private String sourceType;
	private String screenName;
	private int inviteUsers;
	//第三方id
	private int weiboid;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public int getInviteUsers() {
		return inviteUsers;
	}

	public void setInviteUsers(int inviteUsers) {
		this.inviteUsers = inviteUsers;
	}

	public int getWeiboid() {
		return weiboid;
	}

	public void setWeiboid(int weiboid) {
		this.weiboid = weiboid;
	}
}
