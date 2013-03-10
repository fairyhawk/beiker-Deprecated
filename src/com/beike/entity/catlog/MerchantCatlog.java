package com.beike.entity.catlog;

/**
 * <p>
 * Title: 品牌
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

public class MerchantCatlog extends AbstractCatlog {

	private static final long serialVersionUID = 1L;

	private Long merchantId;

	private Long cityid;

	public Long getCityid() {
		return cityid;
	}

	public void setCityid(Long cityid) {
		this.cityid = cityid;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public MerchantCatlog() {

	}

	public MerchantCatlog(Long merchantId) {
		this.merchantId = merchantId;
	}
}
