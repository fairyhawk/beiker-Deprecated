package com.beike.form;

import java.util.Set;


/**
 * <p>Title:现金券Form</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @author wenjie.mai
 * @version 1.0
 */
public class CashCouponForm {

	public CashCouponForm() {
		
	}
	
	/**
	 * 商品ID
	 */
	private Long goodsid;
	
	/**
	 * 返现
	 */
	private double rebatePrice; 
	
	/**
	 * 售价
	 */
	private double currentPrice;
	
	/**
	 * 商品名称
	 */
	private String goodsname;
	
	/**
	 * 一级区域
	 */
	private String mainRegion;
	
	/**
	 * 二级区域
	 */
	private String subsetRegion;
	
	/**
	 * 现金券数量  "1" 有多条现金券  "0" 只有一张现金券
	 */
	private String cashCouponFlag;
	
	/**
	 * 现金券地域标志 "1" 有多家分店 "0" 只有一家店
	 */
	private String regionFlag;
	
	/**
	 * 现金券分店数量
	 */
	private int cashCouponRegionNumber;
		
	/**
	 * 区域
	 */
	private Set<String> mapRegion;
	
	public int getCashCouponRegionNumber() {
		return cashCouponRegionNumber;
	}

	public void setCashCouponRegionNumber(int cashCouponRegionNumber) {
		this.cashCouponRegionNumber = cashCouponRegionNumber;
	}

	public String getRegionFlag() {
		return regionFlag;
	}

	public void setRegionFlag(String regionFlag) {
		this.regionFlag = regionFlag;
	}

	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public double getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(double rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public String getMainRegion() {
		return mainRegion;
	}

	public void setMainRegion(String mainRegion) {
		this.mainRegion = mainRegion;
	}

	public String getSubsetRegion() {
		return subsetRegion;
	}

	public void setSubsetRegion(String subsetRegion) {
		this.subsetRegion = subsetRegion;
	}

	public String getCashCouponFlag() {
		return cashCouponFlag;
	}

	public void setCashCouponFlag(String cashCouponFlag) {
		this.cashCouponFlag = cashCouponFlag;
	}

	public Set<String> getMapRegion() {
		return mapRegion;
	}

	public void setMapRegion(Set<String> mapRegion) {
		this.mapRegion = mapRegion;
	}
}
