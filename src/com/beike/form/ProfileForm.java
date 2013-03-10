package com.beike.form;

import com.beike.common.enums.user.ProfileType;

/**
 * <p>Title:信息拓展交互 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

public class ProfileForm {
	
	private String name;
	private String value;
	private Long userid;
	private ProfileType proFileType;
	public ProfileForm() {
	}
	public ProfileForm(String name, String value, Long userid,
			ProfileType proFileType) {
		this.name = name;
		this.value = value;
		this.userid = userid;
		this.proFileType = proFileType;
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
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public ProfileType getProFileType() {
		return proFileType;
	}
	public void setProFileType(ProfileType proFileType) {
		this.proFileType = proFileType;
	}
}
