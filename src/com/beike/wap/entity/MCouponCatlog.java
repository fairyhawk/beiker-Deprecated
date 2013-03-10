package com.beike.wap.entity;

/**
 * <p>
 * Title:
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
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */

public class MCouponCatlog extends MAbstractCatlog {

	private static final long serialVersionUID = 1L;

	private Long couponId;

	private Long cityid;

	public Long getCityid() {
		return cityid;
	}

	public void setCityid(Long cityid) {
		this.cityid = cityid;
	}

	public MCouponCatlog() {

	}

	public MCouponCatlog(Long couponId) {
		this.couponId = couponId;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
}
