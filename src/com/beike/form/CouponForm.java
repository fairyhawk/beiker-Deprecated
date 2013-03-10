package com.beike.form;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title:优惠券form</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 27, 2011
 * @author ye.tian
 * @version 1.0
 */

public class CouponForm {
	
	private Long couponid;
	
	private String couponName;//优惠券名称
	
	private String couponlogo;//优惠券列表logo
	
	private Date endDate;//结束时间
	
	private Long downcount;//下载次数
	
	private String coupondetaillogo;//优惠券详情logo
	
	private String smstemplate;//短信模板内容
	
	private Long browsecounts;//浏览次数
	
	private String couponnumber;//优惠券编号
	
	private String couponrules;//优惠券规则
	
	private Date createDate;
	
	private Map<Long,Set<String>> mapRegion;//支持地域
	
	private Long merchantid;//品牌的id
	
	private String coupontitle;
	
	public String getCoupontitle() {
		return coupontitle;
	}

	public void setCoupontitle(String coupontitle) {
		this.coupontitle = coupontitle;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public Map<Long, Set<String>> getMapRegion() {
		return mapRegion;
	}

	public void setMapRegion(Map<Long, Set<String>> mapRegion) {
		this.mapRegion = mapRegion;
	}


	public Long getDowncount() {
		return downcount;
	}

	public void setDowncount(Long downcount) {
		this.downcount = downcount;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public Long getCouponid() {
		return couponid;
	}

	public void setCouponid(Long couponid) {
		this.couponid = couponid;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponlogo() {
		return couponlogo;
	}

	public void setCouponlogo(String couponlogo) {
		this.couponlogo = couponlogo;
	}

	public String getCoupondetaillogo() {
		return coupondetaillogo;
	}

	public void setCoupondetaillogo(String coupondetaillogo) {
		this.coupondetaillogo = coupondetaillogo;
	}

	public String getSmstemplate() {
		return smstemplate;
	}

	public void setSmstemplate(String smstemplate) {
		this.smstemplate = smstemplate;
	}


	public Long getBrowsecounts() {
		return browsecounts;
	}

	public void setBrowsecounts(Long browsecounts) {
		this.browsecounts = browsecounts;
	}

	public String getCouponnumber() {
		return couponnumber;
	}

	public void setCouponnumber(String couponnumber) {
		this.couponnumber = couponnumber;
	}

	public String getCouponrules() {
		return couponrules;
	}

	public void setCouponrules(String couponrules) {
		this.couponrules = couponrules;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
