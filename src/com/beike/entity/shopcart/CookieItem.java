package com.beike.entity.shopcart;

import java.util.Date;

/**
 * 用于保存到Cookie的Item // 暂未使用
 * 
 * @author zx.liu
 */

public class CookieItem {
	
	// 品牌ID
	private Long merchantId = 0L;
	
	// 商品ID
	private Long goodsId = 0L;
	
	//购买数量
	private Long buyCount = 0L;

	// 加入购物车时间 
	private Date addTime = new Date();

	public CookieItem(){
	}
	
	public CookieItem(Long merchantId, Long goodsId, Long buyCount, Date addTime){
		this.merchantId = merchantId;
		this.goodsId = goodsId;
		this.buyCount = buyCount;
		this.addTime = addTime;
	}
	
	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
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

	
}


