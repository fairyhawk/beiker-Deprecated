package com.beike.form.background.goods;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Title : 	GoodsForm
 * <p/>
 * Description	:后台商品表单对象
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
 * @version 1.0.0.2011-6-14  
 */
public class GoodsForm {
	//,@RequestParam(value="goodsCurrentPriceBegin") String goodsCurrentPriceBegin
	//,@RequestParam(value="goodsCurrentPriceEnd") String goodsCurrentPriceEnd,@RequestParam(value="goodsCreateTimeBegin") String goodsCreateTimeBegin
	//,@RequestParam(value="goodsCreateTimeEnd") String goodsCreateTimeEnd
	private int goodsId;
	private String goodsName;
	private int goodsTagid;
	private String goodsTagextid;
	private BigDecimal goodsCurrentPrice;
	private BigDecimal goodsSourcePrice;
	private BigDecimal goodsDividePrice;
	private BigDecimal goodsRebatePrice;
	private int goodsMaxCount;
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp goodsEndTime;
	private String goodsLogo;
	private String goodsLogo2;
	private String goodsLogo3;
	private String goodsLogo4;
	private String goodsIntroduction;
	private String goodsReview;
	private String goodsStory;
	private String goodsStoryPic;
	private int guestId;
	private String guestCnName;
	private String goodsStatus;
	private String goodsBranchId;
	private int goodsOrderLoseAbsSate;
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp goodsOrderLoseDate;
	private Timestamp goodsCreateTimeBegin;
	private Timestamp goodsCreateTimeEnd;
	private Timestamp goodsCreateTime;
	private Timestamp goodsModifyTime;
	private BigDecimal goodsCurrentPriceBegin;
	private BigDecimal goodsCurrentPriceEnd;
	private String goodsIsTop;
	private int brandId ;
	
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsTagid() {
		return goodsTagid;
	}
	public void setGoodsTagid(int goodsTagid) {
		this.goodsTagid = goodsTagid;
	}
	public String getGoodsTagextid() {
		return goodsTagextid;
	}
	public void setGoodsTagextid(String goodsTagextid) {
		this.goodsTagextid = goodsTagextid;
	}
	public BigDecimal getGoodsCurrentPrice() {
		return goodsCurrentPrice;
	}
	public void setGoodsCurrentPrice(BigDecimal goodsCurrentPrice) {
		this.goodsCurrentPrice = goodsCurrentPrice;
	}
	public BigDecimal getGoodsSourcePrice() {
		return goodsSourcePrice;
	}
	public void setGoodsSourcePrice(BigDecimal goodsSourcePrice) {
		this.goodsSourcePrice = goodsSourcePrice;
	}
	public BigDecimal getGoodsDividePrice() {
		return goodsDividePrice;
	}
	public void setGoodsDividePrice(BigDecimal goodsDividePrice) {
		this.goodsDividePrice = goodsDividePrice;
	}
	public BigDecimal getGoodsRebatePrice() {
		return goodsRebatePrice;
	}
	public void setGoodsRebatePrice(BigDecimal goodsRebatePrice) {
		this.goodsRebatePrice = goodsRebatePrice;
	}
	public int getGoodsMaxCount() {
		return goodsMaxCount;
	}
	public void setGoodsMaxCount(int goodsMaxCount) {
		this.goodsMaxCount = goodsMaxCount;
	}
	public Timestamp getGoodsEndTime() {
		return goodsEndTime;
	}
	public void setGoodsEndTime(Timestamp goodsEndTime) {
		this.goodsEndTime = goodsEndTime;
	}
	public String getGoodsLogo() {
		return goodsLogo;
	}
	public void setGoodsLogo(String goodsLogo) {
		this.goodsLogo = goodsLogo;
	}
	public String getGoodsLogo2() {
		return goodsLogo2;
	}
	public void setGoodsLogo2(String goodsLogo2) {
		this.goodsLogo2 = goodsLogo2;
	}
	public String getGoodsLogo3() {
		return goodsLogo3;
	}
	public void setGoodsLogo3(String goodsLogo3) {
		this.goodsLogo3 = goodsLogo3;
	}
	public String getGoodsLogo4() {
		return goodsLogo4;
	}
	public void setGoodsLogo4(String goodsLogo4) {
		this.goodsLogo4 = goodsLogo4;
	}
	public String getGoodsIntroduction() {
		return goodsIntroduction;
	}
	public void setGoodsIntroduction(String goodsIntroduction) {
		this.goodsIntroduction = goodsIntroduction;
	}
	public String getGoodsReview() {
		return goodsReview;
	}
	public void setGoodsReview(String goodsReview) {
		this.goodsReview = goodsReview;
	}
	public String getGoodsStory() {
		return goodsStory;
	}
	public void setGoodsStory(String goodsStory) {
		this.goodsStory = goodsStory;
	}
	public String getGoodsStoryPic() {
		return goodsStoryPic;
	}
	public void setGoodsStoryPic(String goodsStoryPic) {
		this.goodsStoryPic = goodsStoryPic;
	}
	public int getGuestId() {
		return guestId;
	}
	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}
	public String getGoodsStatus() {
		return goodsStatus;
	}
	public void setGoodsStatus(String goodsStatus) {
		this.goodsStatus = goodsStatus;
	}
	public String getGoodsBranchId() {
		return goodsBranchId;
	}
	public void setGoodsBranchId(String goodsBranchId) {
		this.goodsBranchId = goodsBranchId;
	}
	public int getGoodsOrderLoseAbsSate() {
		return goodsOrderLoseAbsSate;
	}
	public void setGoodsOrderLoseAbsSate(int goodsOrderLoseAbsSate) {
		this.goodsOrderLoseAbsSate = goodsOrderLoseAbsSate;
	}
	public Timestamp getGoodsOrderLoseDate() {
		return goodsOrderLoseDate;
	}
	public void setGoodsOrderLoseDate(Timestamp goodsOrderLoseDate) {
		this.goodsOrderLoseDate = goodsOrderLoseDate;
	}
	public Timestamp getGoodsCreateTime() {
		return goodsCreateTime;
	}
	public void setGoodsCreateTime(Timestamp goodsCreateTime) {
		this.goodsCreateTime = goodsCreateTime;
	}
	public Timestamp getGoodsModifyTime() {
		return goodsModifyTime;
	}
	public void setGoodsModifyTime(Timestamp goodsModifyTime) {
		this.goodsModifyTime = goodsModifyTime;
	}
	public String getGoodsIsTop() {
		return goodsIsTop;
	}
	public void setGoodsIsTop(String goodsIsTop) {
		this.goodsIsTop = goodsIsTop;
	}
	public String getGuestCnName() {
		return guestCnName;
	}
	public void setGuestCnName(String guestCnName) {
		this.guestCnName = guestCnName;
	}
	public Timestamp getGoodsCreateTimeBegin() {
		return goodsCreateTimeBegin;
	}
	public void setGoodsCreateTimeBegin(Timestamp goodsCreateTimeBegin) {
		this.goodsCreateTimeBegin = goodsCreateTimeBegin;
	}
	public Timestamp getGoodsCreateTimeEnd() {
		return goodsCreateTimeEnd;
	}
	public void setGoodsCreateTimeEnd(Timestamp goodsCreateTimeEnd) {
		this.goodsCreateTimeEnd = goodsCreateTimeEnd;
	}
	public BigDecimal getGoodsCurrentPriceBegin() {
		return goodsCurrentPriceBegin;
	}
	public void setGoodsCurrentPriceBegin(BigDecimal goodsCurrentPriceBegin) {
		this.goodsCurrentPriceBegin = goodsCurrentPriceBegin;
	}
	public BigDecimal getGoodsCurrentPriceEnd() {
		return goodsCurrentPriceEnd;
	}
	public void setGoodsCurrentPriceEnd(BigDecimal goodsCurrentPriceEnd) {
		this.goodsCurrentPriceEnd = goodsCurrentPriceEnd;
	}
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	
}
