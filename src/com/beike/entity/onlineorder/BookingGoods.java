package com.beike.entity.onlineorder;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author janwen
 * Nov 8, 2012
 * 点餐商品信息
 */
public class BookingGoods implements Serializable {

	
	private Long goodsid;
	
	private String logourl;
	
	private String goodsname;
	
	private String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	private Long sold;
	
	
	public Long getSold() {
		return sold;
	}

	private double currentPrice;
	
	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}



	public void setSold(Long sold) {
		this.sold = sold;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(double sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public List<String> getRegionexts() {
		return regionexts;
	}

	public void setRegionexts(List<String> regionexts) {
		this.regionexts = regionexts;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

	private double sourcePrice;
	
	private List<String> regionexts;
	
	
}
