package com.beike.entity.seo;

import java.util.Date;

/**
 * SEO 对应的优惠券标签类
 * 
 * @author zx.liu
 */
public class CouponTag {
	
	private Long id;
	// 优惠券标签名称
	private String tagEnname;
	// 标签添加的时间
	private Date addTime;
	// 该标签对应优惠券的ID
	private Long couponId=0L;
	
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
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
		
	
}
