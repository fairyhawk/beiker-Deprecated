package com.beike.entity.merchant;

import java.io.Serializable;

/**
 * <p>Title:商家扩展属性 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 19, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class MerchantProfileType implements Serializable{
	
	private Long id;
	private String property_type;
	private String propertyname;
	
	private String propertyvalue;
	private Long merchantid;
	public MerchantProfileType() {
	}
	public MerchantProfileType(Long id, String property_type,
			String propertyname, String propertyvalue, Long merchantid) {
		this.id = id;
		this.property_type = property_type;
		this.propertyname = propertyname;
		this.propertyvalue = propertyvalue;
		this.merchantid = merchantid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProperty_type() {
		return property_type;
	}
	public void setProperty_type(String property_type) {
		this.property_type = property_type;
	}
	public String getPropertyname() {
		return propertyname;
	}
	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}
	public String getPropertyvalue() {
		return propertyvalue;
	}
	public void setPropertyvalue(String propertyvalue) {
		this.propertyvalue = propertyvalue;
	}
	public Long getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}
	
}
