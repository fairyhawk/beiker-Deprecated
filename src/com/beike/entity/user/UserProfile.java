package com.beike.entity.user;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Title:用户扩展信息
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
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

public class UserProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String value;
	private String profiletype;
	private Long userid;
	private Date profiledate;

	public Date getProfiledate() {
		return profiledate;
	}

	public void setProfiledate(Date profiledate) {
		this.profiledate = profiledate;
	}

	public UserProfile() {

	}

	public UserProfile(Long id, String name, String value, String profiletype,
			Long userid) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.profiletype = profiletype;
		this.userid = userid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getProfiletype() {
		return profiletype;
	}

	public void setProfiletype(String profiletype) {
		this.profiletype = profiletype;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
}
