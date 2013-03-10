package com.beiker.model.operation.wish;

import java.sql.Timestamp;

public class InviteRecord {

	@Override
	public String toString() {

		return this.getAwardNo() + this.getEmail() + this.getMobile()
				+ this.getScreenName() + this.getSourceType()
				+ this.getRegisterTime().toLocaleString();
	}

	private String screenName;
	private String email;
	private String mobile;
	private String awardNo;
	private String sourceType;
	private Timestamp registerTime;
	private Long weiboid;


	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAwardNo() {
		return awardNo;
	}

	public void setAwardNo(String awardNo) {
		this.awardNo = awardNo;
	}

	public Timestamp getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Long getWeiboid() {
		return weiboid;
	}

	public void setWeiboid(Long weiboid) {
		this.weiboid = weiboid;
	}

}
