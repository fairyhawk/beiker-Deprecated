package com.beike.entity.catlog;

import java.sql.Timestamp;

/*******************************************************************************
 * 商品列表默认排序值对象
 * 
 * @author janwen
 * @time Dec 26, 2011 5:19:42 PM
 */
public class DefaultGoods{

	private Long goodsid;

	private int saled;
	private Timestamp on_time;
	
	private double currentPrice;

	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public int getSaled() {
		return saled;
	}

	public void setSaled(int saled) {
		this.saled = saled;
	}

	public Timestamp getOn_time() {
		return on_time;
	}

	public void setOn_time(Timestamp on_time) {
		this.on_time = on_time;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
}
