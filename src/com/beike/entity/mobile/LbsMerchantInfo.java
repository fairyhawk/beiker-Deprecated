package com.beike.entity.mobile;

import java.io.Serializable;
import java.util.List;

/**
 * lbs分店信息
 * @author 赵静龙 创建时间：2012-9-18
 */
public class LbsMerchantInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 分店id **/
	private Long id;
	/** 分店名称 **/
	private String merchantName;
	/** 显示名称 **/
	private String displayName;
	/** 分店地址 **/
	private String addr;
	/** 城市 **/
	private String city;
	/** 分店介绍 **/
	private String merchantIntroduction;
	/** 电话 **/
	private String tel;
	/** 营业时间 **/
	private String businessTime;
	/** 质量保证 **/
	private Long quality;
	/** 标签 **/
	/** 商圈tag，用空格分隔 **/
	private String businessRegionTag;
	/** 商圈tag所对应的id，用空格分隔 **/
	private String businessRegionTagId;
	/** 分类tag，用空格分隔 **/
	private String classificationTag;
	/** 分类tag所对应的id，用空格分隔 **/
	private String classificationTagId;
	/** 品牌id **/
	private Long brandId;
	/** 品牌名称 **/
	private String brandName;
	/** 分店下的商品id集合，用逗号分隔 **/
	private String ids;
	/** 分店下的商品信息集合 **/
	private List<LbsMerchantGoodsInfo> goodsInfo;
	/** 经度 **/
	private String originalLon;
	/** 纬度 **/
	private String originalLat;
	/** 店铺域名 **/
	private String domainName;
	/** 是否VIP商户:0否 1是 **/
	private Short vipBrand;
	
	/** -------------------------品牌扩展信息-------------------- **/
	/** 图片 **/
	private String mcLogo1;
	private String mcLogo2;
	private String mcLogo3;
	private String mcLogo4;
	/** 商家累积销售量 **/
	private Integer mcSaleCount;
	/** 商家评价得分 **/
	private Long mcScore;
	/**　商家很好评价次数 **/
	private Long mcWellCount;
	/** 商家满意评价次数 **/
	private Long mcSatisfyCount;
	/** 商家差评价次数 **/
	private Long mcPoorCount;
	/** -------------------------品牌扩展信息-------------------- **/
	
	
	/** -------------------------分店扩展信息-------------------- **/
	/** 分店很好评价次数 **/
	private Long merWellCount;
	/** 分店满意评价次数 **/
	private Long merSatisfyCount;
	/** 分店差评价次数 **/
	private Long merPoorCount;
	/** -------------------------分店扩展信息-------------------- **/
	
	public Long getId() {
		return id;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getAddr() {
		return addr;
	}
	public String getCity() {
		return city;
	}
	public String getMerchantIntroduction() {
		return merchantIntroduction;
	}
	public String getTel() {
		return tel;
	}
	public String getBusinessTime() {
		return businessTime;
	}
	public Long getQuality() {
		return quality;
	}
	public Long getBrandId() {
		return brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public String getIds() {
		return ids;
	}
	public String getOriginalLon() {
		return originalLon;
	}
	public String getOriginalLat() {
		return originalLat;
	}
	public String getDomainName() {
		return domainName;
	}
	public Short getVipBrand() {
		return vipBrand;
	}
	public String getMcLogo1() {
		return mcLogo1;
	}
	public String getMcLogo2() {
		return mcLogo2;
	}
	public String getMcLogo3() {
		return mcLogo3;
	}
	public String getMcLogo4() {
		return mcLogo4;
	}

	public Integer getMcSaleCount() {
		return mcSaleCount;
	}
	public Long getMcScore() {
		return mcScore;
	}
	public Long getMcWellCount() {
		return mcWellCount;
	}
	public Long getMcSatisfyCount() {
		return mcSatisfyCount;
	}
	public Long getMcPoorCount() {
		return mcPoorCount;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setMerchantIntroduction(String merchantIntroduction) {
		this.merchantIntroduction = merchantIntroduction;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public void setBusinessTime(String businessTime) {
		this.businessTime = businessTime;
	}
	public void setQuality(Long quality) {
		this.quality = quality;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public void setOriginalLon(String originalLon) {
		this.originalLon = originalLon;
	}
	public void setOriginalLat(String originalLat) {
		this.originalLat = originalLat;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public void setVipBrand(Short vipBrand) {
		this.vipBrand = vipBrand;
	}
	public void setMcLogo1(String mcLogo1) {
		this.mcLogo1 = mcLogo1;
	}
	public void setMcLogo2(String mcLogo2) {
		this.mcLogo2 = mcLogo2;
	}
	public void setMcLogo3(String mcLogo3) {
		this.mcLogo3 = mcLogo3;
	}
	public void setMcLogo4(String mcLogo4) {
		this.mcLogo4 = mcLogo4;
	}

	public void setMcSaleCount(Integer mcSaleCount) {
		this.mcSaleCount = mcSaleCount;
	}
	public void setMcScore(Long mcScore) {
		this.mcScore = mcScore;
	}
	public void setMcWellCount(Long mcWellCount) {
		this.mcWellCount = mcWellCount;
	}
	public void setMcSatisfyCount(Long mcSatisfyCount) {
		this.mcSatisfyCount = mcSatisfyCount;
	}
	public void setMcPoorCount(Long mcPoorCount) {
		this.mcPoorCount = mcPoorCount;
	}
	public String getClassificationTag() {
		return classificationTag;
	}
	public String getBusinessRegionTag() {
		return businessRegionTag;
	}
	public void setBusinessRegionTag(String businessRegionTag) {
		this.businessRegionTag = businessRegionTag;
	}
	public void setClassificationTag(String classificationTag) {
		this.classificationTag = classificationTag;
	}
	public List<LbsMerchantGoodsInfo> getGoodsInfo() {
		return goodsInfo;
	}
	public void setGoodsInfo(List<LbsMerchantGoodsInfo> goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	public Long getMerWellCount() {
		return merWellCount;
	}
	public Long getMerSatisfyCount() {
		return merSatisfyCount;
	}
	public Long getMerPoorCount() {
		return merPoorCount;
	}
	public void setMerWellCount(Long merWellCount) {
		this.merWellCount = merWellCount;
	}
	public void setMerSatisfyCount(Long merSatisfyCount) {
		this.merSatisfyCount = merSatisfyCount;
	}
	public void setMerPoorCount(Long merPoorCount) {
		this.merPoorCount = merPoorCount;
	}
	public String getBusinessRegionTagId() {
		return businessRegionTagId;
	}
	public String getClassificationTagId() {
		return classificationTagId;
	}
	public void setBusinessRegionTagId(String businessRegionTagId) {
		this.businessRegionTagId = businessRegionTagId;
	}
	public void setClassificationTagId(String classificationTagId) {
		this.classificationTagId = classificationTagId;
	}
	
}
