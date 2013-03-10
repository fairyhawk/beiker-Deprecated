package com.beike.entity.shopcart;

import java.util.Date;

/**
 * 购物车的Item
 *
 */

public class ShopItem {

	// 主键
	private Long shopcartid = 0L;
	
	// 品牌ID
	private Long merchantid = 0L;
	
	// 商品ID
	private Long goodsid = 0L;
	
	// 用户ID
	private Long userid = 0L;	
	
	//购买数量
	private Long buy_count = 0L;
	
	// 加入购物车时间 (排序倒序)
	private Date addtime = new Date();	

	/**
	 * 秒杀ID
	 */
	private Long miaoshaid = 0L;
	
	private Long limitCount = 1000L;
	
	
	public Long getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(Long limitCount) {
		this.limitCount = limitCount;
	}

	public Long getMiaoshaid() {
		return miaoshaid;
	}

	public void setMiaoshaid(Long miaoshaid) {
		this.miaoshaid = miaoshaid;
	}

	public Long getShopcartid() {
		return shopcartid;
	}

	public void setShopcartid(Long shopcartid) {
		this.shopcartid = shopcartid;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getBuy_count() {
		return buy_count;
	}

	public void setBuy_count(Long buy_count) {
		this.buy_count = buy_count;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	
}


