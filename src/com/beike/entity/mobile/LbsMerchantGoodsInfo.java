package com.beike.entity.mobile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *  lbs商品信息，在用于查询lbs分店信息的时候，此作为一个List集合置于分店下
 * @author 赵静龙 创建时间：2012-9-18
 */
public class LbsMerchantGoodsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 商品id **/
	private Long goodsId;
	/** 商品名称 **/
	private String goodsName;
	/** 商品标题（短名称） **/
	private String goodsTitle;
	/** 标签 **/
	/** 分类tag，用空格分隔 **/
	private String classificationTag;
	/** 分类tag所对应的id，用空格分隔 **/
	private String classificationTagId;
	/** 城市 **/
	private String city;
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
	private Integer goodsOrderLoseAbsDate;
	/** 商品订单过期时间点 **/
	private Timestamp goodsOrderLoseDate;
	/** 到开始时间自动发布上 **/
	private Timestamp goodsStartTime;
	/** 结束时间 **/
	private Timestamp goodsEndTime;
	/** 1可用(上线)  0不可用(下线) **/
	private Integer goodsIsAvaliable;
	/** 1置顶 0不置顶 **/
	private String goodsIsTop;
	/** 温馨提示 **/
	private String kindlyWarnings;
	/** 是否支持退款 0:不支持退款 1:支持退款 **/
	private Integer goodsIsRefund;
	/** 1是现金券 0不是现金券 **/
	private String couponCash;
	/** 是否预付款 0：否; 1：是 **/
	private Integer goodsIsAdvance;
	/** 是否支持预定0:否1:是 **/
	private String goodsScheduled;
	
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
	public Long getGoodsId() {
		return goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public String getCity() {
		return city;
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
	public Integer getGoodsOrderLoseAbsDate() {
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
	public Integer getGoodsIsAvaliable() {
		return goodsIsAvaliable;
	}
	public String getGoodsIsTop() {
		return goodsIsTop;
	}
	public String getKindlyWarnings() {
		return kindlyWarnings;
	}
	public Integer getGoodsIsRefund() {
		return goodsIsRefund;
	}
	public String getCouponCash() {
		return couponCash;
	}
	public Integer getGoodsIsAdvance() {
		return goodsIsAdvance;
	}
	public String getGoodsScheduled() {
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
	public void setCity(String city) {
		this.city = city;
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
	public void setGoodsOrderLoseAbsDate(Integer goodsOrderLoseAbsDate) {
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
	public void setGoodsIsAvaliable(Integer goodsIsAvaliable) {
		this.goodsIsAvaliable = goodsIsAvaliable;
	}
	public void setGoodsIsTop(String goodsIsTop) {
		this.goodsIsTop = goodsIsTop;
	}
	public void setKindlyWarnings(String kindlyWarnings) {
		this.kindlyWarnings = kindlyWarnings;
	}
	public void setGoodsIsRefund(Integer goodsIsRefund) {
		this.goodsIsRefund = goodsIsRefund;
	}
	public void setCouponCash(String couponCash) {
		this.couponCash = couponCash;
	}
	public void setGoodsIsAdvance(Integer goodsIsAdvance) {
		this.goodsIsAdvance = goodsIsAdvance;
	}
	public void setGoodsScheduled(String goodsScheduled) {
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
	public String getClassificationTag() {
		return classificationTag;
	}
	public void setClassificationTag(String classificationTag) {
		this.classificationTag = classificationTag;
	}
	public String getClassificationTagId() {
		return classificationTagId;
	}
	public void setClassificationTagId(String classificationTagId) {
		this.classificationTagId = classificationTagId;
	}
}
