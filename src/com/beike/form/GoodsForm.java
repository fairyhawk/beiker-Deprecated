package com.beike.form;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title:商品form
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */

public class GoodsForm implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long goodsId;
	private String goodsname;
	private double sourcePrice;
	private double currentPrice;
	private double payPrice;
	private double offerPrice;
	private double rebatePrice;
	private double dividePrice;
	private Float discount;
	private int maxcount;
	private Date endTime;
	private String city;
	private Date startTime;
	private int isavaliable;
	
	private String is_scheduled;

	public String getIs_scheduled() {
		return is_scheduled;
	}

	public void setIs_scheduled(String is_scheduled) {
		this.is_scheduled = is_scheduled;
	}

	private String logo1;
	private String logo2;
	private String logo3;

	private String logo4;

	private String detailPageUrl;
	
	private int refund;

	private String profilename;
	private String profilevalue;
	private Long merchantId; // 哪个商户的
	private String salescount;// 实际销售量+虚拟销售量
	private String merchantlogo;// 商户logo
	private String listlogo;// 商品列表logo

	private Map<Long, Set<String>> mapRegion;// 支持地区
	private String goodsTitle;
	
	//商品性质:0无特殊性质 1新品 add by qiaowb 2012-04-06
	private String character;
	
	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getProfilename() {
		return profilename;
	}

	public void setProfilename(String profilename) {
		this.profilename = profilename;
	}

	public String getProfilevalue() {
		return profilevalue;
	}

	public void setProfilevalue(String profilevalue) {
		this.profilevalue = profilevalue;
	}

	public String getDetailPageUrl() {
		return detailPageUrl;
	}

	public void setDetailPageUrl(String detailPageUrl) {
		this.detailPageUrl = detailPageUrl;
	}

	public String getLogo1() {
		return logo1;
	}

	public void setLogo1(String logo1) {
		this.logo1 = logo1;
	}

	public String getLogo2() {
		return logo2;
	}

	public void setLogo2(String logo2) {
		this.logo2 = logo2;
	}

	public String getLogo3() {
		return logo3;
	}

	public void setLogo3(String logo3) {
		this.logo3 = logo3;
	}

	public GoodsForm() {
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public double getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(double sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(double payPrice) {
		this.payPrice = payPrice;
	}

	public double getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(double offerPrice) {
		this.offerPrice = offerPrice;
	}

	public double getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(double rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public double getDividePrice() {
		return dividePrice;
	}

	public void setDividePrice(double dividePrice) {
		this.dividePrice = dividePrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public int getMaxcount() {
		return maxcount;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getIsavaliable() {
		return isavaliable;
	}

	public void setIsavaliable(int isavaliable) {
		this.isavaliable = isavaliable;
	}

	public GoodsForm(String goodsname, double sourcePrice, double currentPrice,
			double payPrice, double offerPrice, double rebatePrice,
			double dividePrice, double discount, int maxcount, Date endTime,
			String city, Date startTime, int isavaliable) {
		this.goodsname = goodsname;
		this.sourcePrice = sourcePrice;
		this.currentPrice = currentPrice;
		this.payPrice = payPrice;
		this.offerPrice = offerPrice;
		this.rebatePrice = rebatePrice;
		this.dividePrice = dividePrice;
		this.maxcount = maxcount;
		this.endTime = endTime;
		this.city = city;
		this.startTime = startTime;
		this.isavaliable = isavaliable;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getSalescount() {
		return salescount;
	}

	public void setSalescount(String salescount) {
		this.salescount = salescount;
	}

	public String getMerchantlogo() {
		return merchantlogo;
	}

	public void setMerchantlogo(String merchantlogo) {
		this.merchantlogo = merchantlogo;
	}

	public String getListlogo() {
		return listlogo;
	}

	public void setListlogo(String listlogo) {
		this.listlogo = listlogo;
	}

	public Map<Long, Set<String>> getMapRegion() {
		return mapRegion;
	}

	public void setMapRegion(Map<Long, Set<String>> mapRegion) {
		this.mapRegion = mapRegion;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public String getLogo4() {
		return logo4;
	}

	public void setLogo4(String logo4) {
		this.logo4 = logo4;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public int getRefund() {
		return refund;
	}

	public void setRefund(int refund) {
		this.refund = refund;
	}
}
