package com.beike.wap.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * wap-优惠卷
 */
public class MCoupon implements Serializable{
	
	/** 主键id */
	private long id;
	
	/** 优惠卷名称 */
	private String couponName;
	
	/** 结束时间 */
	private Date endDate;
	
	/** 下载次数 */
	private long downCount;
	
	/** 优惠卷logo */
	private String couponLogo;
	
	/** 创建时间 */
	private Date createDate;
	
	/** 商户id */
	private long merchantid;
	
	/** 优惠券详情logo */
	private String couponDetailLogo;
	
	/** 优惠卷编号 */
	private String couponNumber;
	
	/** 优惠卷使用规则 */
	private String couponRules;
	
	/** 浏览次数 */
	private long browseCounts;
	
	/** 短信模板 */
	private String smstemplate;
	
	/** 优惠券title */
	private String coupon_title;

	/** wap-优惠卷url，只在wap中使用 */
	private String coupon_wap_url;
	
	/** 优惠卷detailogo实际名称 */
	private String detailLogoName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getDownCount() {
		return downCount;
	}

	public void setDownCount(long downCount) {
		this.downCount = downCount;
	}

	public String getCouponLogo() {
		return couponLogo;
	}

	public void setCouponLogo(String couponLogo) {
		this.couponLogo = couponLogo;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(long merchantid) {
		this.merchantid = merchantid;
	}

	public String getCouponDetailLogo() {
		return couponDetailLogo;
	}

	public void setCouponDetailLogo(String couponDetailLogo) {
		this.couponDetailLogo = couponDetailLogo;
	}

	public String getCouponNumber() {
		return couponNumber;
	}

	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
	}

	public String getCouponRules() {
		return couponRules;
	}

	public void setCouponRules(String couponRules) {
		this.couponRules = couponRules;
	}

	public long getBrowseCounts() {
		return browseCounts;
	}

	public void setBrowseCounts(long browseCounts) {
		this.browseCounts = browseCounts;
	}

	public String getSmstemplate() {
		return smstemplate;
	}

	public void setSmstemplate(String smstemplate) {
		this.smstemplate = smstemplate;
	}

	public String getCoupon_title() {
		return coupon_title;
	}

	public void setCoupon_title(String coupon_title) {
		this.coupon_title = coupon_title;
	}

	public String getCoupon_wap_url() {
		return coupon_wap_url;
	}

	public void setCoupon_wap_url(String coupon_wap_url) {
		this.coupon_wap_url = coupon_wap_url;
	}

	public String getDetailLogoName() {
		return detailLogoName;
	}

	public void setDetailLogoName(String detailLogoName) {
		this.detailLogoName = detailLogoName;
	}
}
