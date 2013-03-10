package com.beike.entity.user;

import java.io.Serializable;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 7:15:06 PM     
 * @version 1.0
 */
public class UserAddress implements Serializable {
	private Long id;
	//用户ID
	private Long userid;
	//省
	private String province;
	//市
	private String city;
	//区
	private String area;
	//地址
	private String address;
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public UserAddress(){
	}
	
	public UserAddress(Long userId, String province, String city, String area,
			String address) {
		this.userid = userId;
		this.province = province;
		this.city = city;
		this.area = area;
		this.address = address;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
