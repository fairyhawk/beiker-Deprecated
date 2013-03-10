package com.beike.entity.goods;

import java.io.Serializable;

/**
 * <p>Title: 商品扩展属性</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class GoodsProfile implements Serializable{
	
	private Long id;
	
	private String profileName;
	 
	private String profileValue;
	
	private Long goodsId;

	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileValue() {
		return profileValue;
	}

	public void setProfileValue(String profileValue) {
		this.profileValue = profileValue;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public GoodsProfile(){
		
	}
	public GoodsProfile(Long id, String profileName, String profileValue,
			Long goodsId) {
		this.id = id;
		this.profileName = profileName;
		this.profileValue = profileValue;
		this.goodsId = goodsId;
	}
}
