package com.beike.action.lottery;

import java.util.List;


public class LotteryGoods {

	private String userId;

	private String userTel;

	private String userEmail;

	private String goodsName;
	private String balanceAmount;

	private String rebatePrice;

	private String payPrice;


	private String goodsId;
	
	






	/**
	 * 消费密码
	 */
	private String voucherCode;
	/**
	 * 商品订单号
	 */
	private String txrorderGoodsSn;
	/**
	 * 商家预约电话
	 */
	private String mertOrdTel;

	private int goodsCount;

	/**
	 * 商家名字
	 */
	private String merchantName;
	public List<LotteryGoods> getPif() {
		return pif;
	}

	public void setPif(List<LotteryGoods> pif) {
		this.pif = pif;
	}

	/**
	 * 商家id
	 */
	private String merchantId;
	
	private List<LotteryGoods> pif;
	

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * 商家地址
	 */
	private String merchantAddr;

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public LotteryGoods() {
	}

	public LotteryGoods(String goodsName, String payPrice, String rebatePrice) {

		this.goodsName = goodsName;
		this.payPrice = payPrice;
		this.rebatePrice = rebatePrice;

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}


	public String getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(String rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}


	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getMertOrdTel() {
		return mertOrdTel;
	}

	public void setMertOrdTel(String mertOrdTel) {
		this.mertOrdTel = mertOrdTel;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantAddr() {
		return merchantAddr;
	}

	public void setMerchantAddr(String merchantAddr) {
		this.merchantAddr = merchantAddr;
	}

	public String getTxrorderGoodsSn() {
		return txrorderGoodsSn;
	}

	public void setTxrorderGoodsSn(String txrorderGoodsSn) {
		this.txrorderGoodsSn = txrorderGoodsSn;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}


}
