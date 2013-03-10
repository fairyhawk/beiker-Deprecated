package com.beike.entity.mobile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * lbs商品信息
 * @author 赵静龙 创建时间：2012-9-18
 */
public class LbsGoodsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 商品id **/
	private Long goodsId;
	/** 商品名称 **/
	private String goodsName;
	/** 商品标题（短名称） **/
	private String goodsTitle;
	/** 标签 **/
	/** 商圈tag，用空格分隔 **/
	private String businessRegionTag;
	/** 商圈tag所对应的id，用空格分隔 **/
	private String businessRegionTagId;
	/** 分类tag，用空格分隔 **/
	private String classificationTag;
	/** 分类tag所对应的id，用空格分隔 **/
	private String classificationTagId;
	/** 分店id **/
	private Long merchantId;
	/** 电话 **/
	private String tel;
	/** 城市 **/
	private String city;
	/** 经度 **/
	private String originalLon;
	/** 纬度 **/
	private String originalLat;
	/** 原价格 **/
	private BigDecimal goodsSourcePrice;
	/** 当前价格 **/
	private BigDecimal goodsCurrentPrice;
	/** 分成价格 **/
	private BigDecimal goodsDividePrice;
	/** 返现价格 **/
	private BigDecimal goodsRebatePrice;
	private String goodsLogo;
	private String goodsLogo2;
	private String goodsLogo3;
	private String goodsLogo4;
	/** 售销上限最大数量 **/
	private Long maxCount;
	/** 个人可购买数量 **/
	private Integer goodsSingleCount;
	/** 千品物语图片 **/
	private String qpsharePic;
	/** 商品订单过期时间段。比如用户购买后10天不消费，则此订单过期。（时间单位由PM另定） **/
	private Long goodsOrderLoseAbsDate;
	/** 商品订单过期时间点 **/
	private Timestamp goodsOrderLoseDate;
	/** 到开始时间自动发布上 **/
	private Timestamp goodsStartTime;
	/** 结束时间 **/
	private Timestamp goodsEndTime;
	/** 1可用(上线)  0不可用(下线) **/
	private Long goodsIsAvaliable;
	/** 1置顶 0不置顶 **/
	private Long goodsIsTop;
	/** 温馨提示 **/
	private String kindlyWarnings;
	/** 是否支持退款 0:不支持退款 1:支持退款 **/
	private Integer goodsIsRefund;
	/** 1是现金券 0不是现金券 **/
	private Long couponCash;
	/** 是否预付款 0：否; 1：是 **/
	private Byte goodsIsAdvance;
	/** 是否支持预定0:否1:是 **/
	private Long goodsScheduled;
	
	/** 销售量 **/
	private Integer salesCount;
	/** 商品详细页 **/
	private String detailPageurl;
	/** 商品很好评价次数 **/
	private Long wellCount;
	/** 商品满意评价次数 **/
	private Long satisfyCount;
	/** 商品差评价次数 **/
	private Long poorCount;
	
	
	//下面为商品所属分店的相关信息：
	/** 分店名称 **/
	private String merchantName;
	/** 分店显示名称 **/
	private String merDisplayName;
	/** 分店地址 **/
	private String addr;
	/** 分店介绍 **/
	private String merchantIntroduction;
	/** 分店营业时间 **/
	private String merBusinessTime;
	/** 质量保证 **/
	private Long merQuality;
	/** 品牌id **/
	private Long brandId;
	/** 品牌名称 **/
	private String brandName;
	/** 店铺域名 **/
	private String merDomainName;
	/** 是否VIP商户:0否 1是 **/
	private Integer merVipBrand;
	
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
	
	public Long getGoodsId() {
		return goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public Long getMerchantId() {
		return merchantId;
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
	public Long getMerWellCount() {
		return merWellCount;
	}
	public Long getMerSatisfyCount() {
		return merSatisfyCount;
	}
	public Long getMerPoorCount() {
		return merPoorCount;
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
	public void setMerWellCount(Long merWellCount) {
		this.merWellCount = merWellCount;
	}
	public void setMerSatisfyCount(Long merSatisfyCount) {
		this.merSatisfyCount = merSatisfyCount;
	}
	public void setMerPoorCount(Long merPoorCount) {
		this.merPoorCount = merPoorCount;
	}
	public String getTel() {
		return tel;
	}
	public String getCity() {
		return city;
	}
	public String getOriginalLon() {
		return originalLon;
	}
	public String getOriginalLat() {
		return originalLat;
	}
	public BigDecimal getGoodsSourcePrice() {
		return goodsSourcePrice;
	}
	public BigDecimal getGoodsCurrentPrice() {
		return goodsCurrentPrice;
	}
	public BigDecimal getGoodsDividePrice() {
		return goodsDividePrice;
	}
	public BigDecimal getGoodsRebatePrice() {
		return goodsRebatePrice;
	}
	public String getGoodsLogo() {
		return goodsLogo;
	}
	public String getGoodsLogo2() {
		return goodsLogo2;
	}
	public String getGoodsLogo3() {
		return goodsLogo3;
	}
	public String getGoodsLogo4() {
		return goodsLogo4;
	}
	public Long getMaxCount() {
		return maxCount;
	}
	public Integer getGoodsSingleCount() {
		return goodsSingleCount;
	}
	public String getQpsharePic() {
		return qpsharePic;
	}
	public Long getGoodsOrderLoseAbsDate() {
		return goodsOrderLoseAbsDate;
	}
	public Timestamp getGoodsOrderLoseDate() {
		return goodsOrderLoseDate;
	}
	public Timestamp getGoodsStartTime() {
		return goodsStartTime;
	}
	public Timestamp getGoodsEndTime() {
		return goodsEndTime;
	}
	public Long getGoodsIsAvaliable() {
		return goodsIsAvaliable;
	}
	public Long getGoodsIsTop() {
		return goodsIsTop;
	}
	public String getKindlyWarnings() {
		return kindlyWarnings;
	}
	public Integer getGoodsIsRefund() {
		return goodsIsRefund;
	}
	public Long getCouponCash() {
		return couponCash;
	}
	public Byte getGoodsIsAdvance() {
		return goodsIsAdvance;
	}
	public Long getGoodsScheduled() {
		return goodsScheduled;
	}
	public Integer getSalesCount() {
		return salesCount;
	}
	public String getDetailPageurl() {
		return detailPageurl;
	}
	public Long getWellCount() {
		return wellCount;
	}
	public Long getSatisfyCount() {
		return satisfyCount;
	}
	public Long getPoorCount() {
		return poorCount;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setOriginalLon(String originalLon) {
		this.originalLon = originalLon;
	}
	public void setOriginalLat(String originalLat) {
		this.originalLat = originalLat;
	}
	public void setGoodsSourcePrice(BigDecimal goodsSourcePrice) {
		this.goodsSourcePrice = goodsSourcePrice;
	}
	public void setGoodsCurrentPrice(BigDecimal goodsCurrentPrice) {
		this.goodsCurrentPrice = goodsCurrentPrice;
	}
	public void setGoodsDividePrice(BigDecimal goodsDividePrice) {
		this.goodsDividePrice = goodsDividePrice;
	}
	public void setGoodsRebatePrice(BigDecimal goodsRebatePrice) {
		this.goodsRebatePrice = goodsRebatePrice;
	}
	public void setGoodsLogo(String goodsLogo) {
		this.goodsLogo = goodsLogo;
	}
	public void setGoodsLogo2(String goodsLogo2) {
		this.goodsLogo2 = goodsLogo2;
	}
	public void setGoodsLogo3(String goodsLogo3) {
		this.goodsLogo3 = goodsLogo3;
	}
	public void setGoodsLogo4(String goodsLogo4) {
		this.goodsLogo4 = goodsLogo4;
	}
	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}
	public void setGoodsSingleCount(Integer goodsSingleCount) {
		this.goodsSingleCount = goodsSingleCount;
	}
	public void setQpsharePic(String qpsharePic) {
		this.qpsharePic = qpsharePic;
	}
	public void setGoodsOrderLoseAbsDate(Long goodsOrderLoseAbsDate) {
		this.goodsOrderLoseAbsDate = goodsOrderLoseAbsDate;
	}
	public void setGoodsOrderLoseDate(Timestamp goodsOrderLoseDate) {
		this.goodsOrderLoseDate = goodsOrderLoseDate;
	}
	public void setGoodsStartTime(Timestamp goodsStartTime) {
		this.goodsStartTime = goodsStartTime;
	}
	public void setGoodsEndTime(Timestamp goodsEndTime) {
		this.goodsEndTime = goodsEndTime;
	}
	public void setGoodsIsAvaliable(Long goodsIsAvaliable) {
		this.goodsIsAvaliable = goodsIsAvaliable;
	}
	public void setGoodsIsTop(Long goodsIsTop) {
		this.goodsIsTop = goodsIsTop;
	}
	public void setKindlyWarnings(String kindlyWarnings) {
		this.kindlyWarnings = kindlyWarnings;
	}
	public void setGoodsIsRefund(Integer goodsIsRefund) {
		this.goodsIsRefund = goodsIsRefund;
	}
	public void setCouponCash(Long couponCash) {
		this.couponCash = couponCash;
	}
	public void setGoodsIsAdvance(Byte goodsIsAdvance) {
		this.goodsIsAdvance = goodsIsAdvance;
	}
	public void setGoodsScheduled(Long goodsScheduled) {
		this.goodsScheduled = goodsScheduled;
	}
	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}
	public void setDetailPageurl(String detailPageurl) {
		this.detailPageurl = detailPageurl;
	}
	public void setWellCount(Long wellCount) {
		this.wellCount = wellCount;
	}
	public void setSatisfyCount(Long satisfyCount) {
		this.satisfyCount = satisfyCount;
	}
	public void setPoorCount(Long poorCount) {
		this.poorCount = poorCount;
	}
	public String getBusinessRegionTag() {
		return businessRegionTag;
	}
	public String getClassificationTag() {
		return classificationTag;
	}
	public void setBusinessRegionTag(String businessRegionTag) {
		this.businessRegionTag = businessRegionTag;
	}
	public void setClassificationTag(String classificationTag) {
		this.classificationTag = classificationTag;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public String getMerDisplayName() {
		return merDisplayName;
	}
	public String getAddr() {
		return addr;
	}
	public String getMerchantIntroduction() {
		return merchantIntroduction;
	}
	public String getMerBusinessTime() {
		return merBusinessTime;
	}
	public Long getMerQuality() {
		return merQuality;
	}
	public Long getBrandId() {
		return brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public String getMerDomainName() {
		return merDomainName;
	}
	public Integer getMerVipBrand() {
		return merVipBrand;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public void setMerDisplayName(String merDisplayName) {
		this.merDisplayName = merDisplayName;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public void setMerchantIntroduction(String merchantIntroduction) {
		this.merchantIntroduction = merchantIntroduction;
	}
	public void setMerBusinessTime(String merBusinessTime) {
		this.merBusinessTime = merBusinessTime;
	}
	public void setMerQuality(Long merQuality) {
		this.merQuality = merQuality;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public void setMerDomainName(String merDomainName) {
		this.merDomainName = merDomainName;
	}
	public void setMerVipBrand(Integer merVipBrand) {
		this.merVipBrand = merVipBrand;
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
