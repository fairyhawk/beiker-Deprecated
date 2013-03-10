package com.beike.entity.seo;

import java.util.Date;

/**
 * SEO 对应的品牌标签类
 * 
 * @author zx.liu
 */
public class MerchantTag {

	private Long id;
	// 品牌标签名称
	private String tagEnname;
	// 标签添加的时间
	private Date addTime;
	// 该标签对应品牌的ID
	private Long merchantId=0L;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTagEnname() {
		return tagEnname;
	}
	public void setTagEnname(String tagEnname) {
		this.tagEnname = tagEnname;
	}

	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	
}
