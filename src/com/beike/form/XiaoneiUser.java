package com.beike.form;
/**
 * <p>Title:人人User类 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class XiaoneiUser {
	
	private int id;
	private String name;
	private String headurl;
	public String getHeadurl() {
		return headurl;
	}
	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public XiaoneiUser(){
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
