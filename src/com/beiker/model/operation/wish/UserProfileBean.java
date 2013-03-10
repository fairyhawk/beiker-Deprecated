package com.beiker.model.operation.wish;

/**
 * beiker_userprofile实体类
 * @author kun.wang
 */
public class UserProfileBean {

	/** id */
	private long id;
	/** 属性名称 */
	private String name;
	/** 属性值 */
	private String value;
	/** 属性类型 */
	private String profileType;
	/** 用户id */
	private int userId;
	/** 时间 */
	private String profileDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getProfileDate() {
		return profileDate;
	}
	public void setProfileDate(String profileDate) {
		this.profileDate = profileDate;
	}
}
