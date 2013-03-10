package com.beike.entity.shopcart;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用于购物车显示的实体对象
 * 
 * @author zx.liu
 */

public class ShopCart {

	// 主键
	private Long shopCartId = 0L;
	
	// 品牌ID
	private Long merchantId = 0L;

	private String merchantName = "";  // 品牌名称
	
	// 商品ID
	private Long goodsId = 0L;

	private String goodsName = "";  // 商品名称
	
	private double goodsCurrentPrice;  // 商品现售价格
	
	private BigDecimal goodsRebatePrice;  // 商品返现金额
	
	private String logo4 = ""; // 商品的第四个Logo（logo4）
	
	private int isavaliable = 0; // 商品是否下架（0表示下架, 1表示在架）
	
	/**
	 * 补充属性01:商品可以销售的最大数量
	 */
	private Long maxCount = 0L;

	/**
	 * 补充属性02：商品销售结束时间
	 */
	private Date endTime;

	// 用户id
	private Long userId = 0L;
	
	//购买数量
	private Long buyCount = 0L;

	// 加入购物车时间 (排序倒序)
	private Date addTime = new Date();		

	
	private List<ShopCart> shopcartList;

	
	/**
	 * 补充属性03：商品已经销售数量
	 */
	private Long salesCount=0L;	
	
	/**
	 * 补充属性04：个人商品当前可买数量
	 */
	private Long canCount=0L;
	
	/**
	 * 补充属性04：商品个人可购买最大数量 对应 goods中个人限买量
	 */
	private Long userBuyCount = 0L;
	
	private Long miaoshaid = 0L;
	
	
	public Long getMiaoshaid() {
		return miaoshaid;
	}

	public void setMiaoshaid(Long miaoshaid) {
		this.miaoshaid = miaoshaid;
	}

	public Long getUserBuyCount() {
		return userBuyCount;
	}

	public void setUserBuyCount(Long userBuyCount) {
		this.userBuyCount = userBuyCount;
	}

	public Long getCanCount() {
		return canCount;
	}

	public void setCanCount(Long canCount) {
		this.canCount = canCount;
	}

	public Long getShopCartId() {
		return shopCartId;
	}

	public void setShopCartId(Long shopCartId) {
		this.shopCartId = shopCartId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public double getGoodsCurrentPrice() {
		return goodsCurrentPrice;
	}

	public void setGoodsCurrentPrice(double goodsCurrentPrice) {
		this.goodsCurrentPrice = goodsCurrentPrice;
	}

	public BigDecimal getGoodsRebatePrice() {
		return goodsRebatePrice;
	}

	public void setGoodsRebatePrice(BigDecimal goodsRebatePrice) {
		this.goodsRebatePrice = goodsRebatePrice;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Long buyCount) {
		this.buyCount = buyCount;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getLogo4() {
		return logo4;
	}

	public void setLogo4(String logo4) {
		this.logo4 = logo4;
	}

	public int getIsavaliable() {
		return isavaliable;
	}

	public void setIsavaliable(int isavaliable) {
		this.isavaliable = isavaliable;
	}

	public Long getMaxCount(){
		return maxCount;
	}

	public void setMaxCount(Long maxCount){
		this.maxCount = maxCount;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<ShopCart> getShopcartList() {
		return shopcartList;
	}

	public void setShopcartList(List<ShopCart> shopcartList) {
		this.shopcartList = shopcartList;
	}

	public Long getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Long salesCount) {
		this.salesCount = salesCount;
	}

	
}
