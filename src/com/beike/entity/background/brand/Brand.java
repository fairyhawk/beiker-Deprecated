package com.beike.entity.background.brand;

import java.sql.Timestamp;

import javax.persistence.Entity;

/**
 * Title : 	Brand
 * <p/>
 * Description	:后台品牌实体对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-30    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-30  
 */
@Entity
public class Brand {
	private int brandId;
	private int brandCountryId;
	private int brandProvinceId;
	private int brandCityId;
	private String brandName;
	private String brandLogo;
	private String brandCouponLogo;
	private String brandCouponDemoLogo;
	private String brandBannerLogo;
	private String brandIntroduction;
	private String brandBusinessDesc;
	private String brandBookPhone;
	private Timestamp brand_create_time;
	private String brandStatus;
	private Timestamp brand_modify_time;
	private String brandSevenRefound;
	private String brandOverRefound;
	private String brandQuality;
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public int getBrandCityId() {
		return brandCityId;
	}
	public void setBrandCityId(int brandCityId) {
		this.brandCityId = brandCityId;
	}
	public int getBrandCountryId() {
		return brandCountryId;
	}
	public void setBrandCountryId(int brandCountryId) {
		this.brandCountryId = brandCountryId;
	}
	public int getBrandProvinceId() {
		return brandProvinceId;
	}
	public void setBrandProvinceId(int brandProvinceId) {
		this.brandProvinceId = brandProvinceId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getBrandLogo() {
		return brandLogo;
	}
	public void setBrandLogo(String brandLogo) {
		this.brandLogo = brandLogo;
	}
	public String getBrandCouponLogo() {
		return brandCouponLogo;
	}
	public void setBrandCouponLogo(String brandCouponLogo) {
		this.brandCouponLogo = brandCouponLogo;
	}
	public String getBrandCouponDemoLogo() {
		return brandCouponDemoLogo;
	}
	public void setBrandCouponDemoLogo(String brandCouponDemoLogo) {
		this.brandCouponDemoLogo = brandCouponDemoLogo;
	}
	
	public String getBrandIntroduction() {
		return brandIntroduction;
	}
	public void setBrandIntroduction(String brandIntroduction) {
		this.brandIntroduction = brandIntroduction;
	}
	public String getBrandBusinessDesc() {
		return brandBusinessDesc;
	}
	public void setBrandBusinessDesc(String brandBusinessDesc) {
		this.brandBusinessDesc = brandBusinessDesc;
	}
	public String getBrandBookPhone() {
		return brandBookPhone;
	}
	public void setBrandBookPhone(String brandBookPhone) {
		this.brandBookPhone = brandBookPhone;
	}
	public Timestamp getBrand_create_time() {
		return brand_create_time;
	}
	public void setBrand_create_time(Timestamp brand_create_time) {
		this.brand_create_time = brand_create_time;
	}
	public String getBrandStatus() {
		return brandStatus;
	}
	public void setBrandStatus(String brandStatus) {
		this.brandStatus = brandStatus;
	}
	public Timestamp getBrand_modify_time() {
		return brand_modify_time;
	}
	public void setBrand_modify_time(Timestamp brand_modify_time) {
		this.brand_modify_time = brand_modify_time;
	}
	public String getBrandSevenRefound() {
		return brandSevenRefound;
	}
	public void setBrandSevenRefound(String brandSevenRefound) {
		this.brandSevenRefound = brandSevenRefound;
	}
	public String getBrandOverRefound() {
		return brandOverRefound;
	}
	public void setBrandOverRefound(String brandOverRefound) {
		this.brandOverRefound = brandOverRefound;
	}
	public String getBrandQuality() {
		return brandQuality;
	}
	public void setBrandQuality(String brandQuality) {
		this.brandQuality = brandQuality;
	}
	public String getBrandBannerLogo() {
		return brandBannerLogo;
	}
	public void setBrandBannerLogo(String brandBannerLogo) {
		this.brandBannerLogo = brandBannerLogo;
	}
	
}
