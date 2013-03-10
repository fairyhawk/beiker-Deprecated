package com.beike.action.pay;

import java.util.List;

/**
 * @Title: PayInfoParam.java
 * @Package com.beike.action.pay
 * @Description: 前端显示的信息聚合bean
 * @date Jun 2, 2011 4:00:12 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PayInfoParam {

	private String userId;

	private String userTel;

	private String userEmail;

	private String goodsName;

	private String sourcePrice;

	private String rebatePrice;

	private String payPrice;

	private String dividePrice;

	private String goodsId;

	private String balanceAmount;

	private String needAmount;

	private String goodsDetailKey;

	private String payResult;
	private String rebateAmount;

	private String ordRequestId;

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
	
	/**
	 * 是否上下架
	 */
	private String isavaliable;
	
	/**
	 * 支付限购类型
	 */
	private String paylimit;
	
	private String miaoshaid;//秒杀ID
	
	private String changeVmAccount="";
	
	

	public String getChangeVmAccount() {
		return changeVmAccount;
	}
	public void setChangeVmAccount(String changeVmAccount) {
		this.changeVmAccount = changeVmAccount;
	}
	public String getMiaoshaid() {
		return miaoshaid;
	}
	public void setMiaoshaid(String miaoshaid) {
		this.miaoshaid = miaoshaid;
	}
	public String getPaylimit() {
		return paylimit;
	}

	public void setPaylimit(String paylimit) {
		this.paylimit = paylimit;
	}

	public String getIsavaliable() {
		return isavaliable;
	}

	public void setIsavaliable(String isavaliable) {
		this.isavaliable = isavaliable;
	}

	public List<PayInfoParam> getPif() {
		return pif;
	}

	public void setPif(List<PayInfoParam> pif) {
		this.pif = pif;
	}

	/**
	 * 商家id
	 */
	private String merchantId;

	private List<PayInfoParam> pif;

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

	public String totalPriceItem; // 单个商品总价（单价*数量）

	public PayInfoParam() {
	}

	public PayInfoParam(String goodsName, String payPrice, String rebatePrice) {

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

	public String getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(String sourcePrice) {
		this.sourcePrice = sourcePrice;
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

	public String getDividePrice() {
		return dividePrice;
	}

	public void setDividePrice(String dividePrice) {
		this.dividePrice = dividePrice;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getNeedAmount() {
		return needAmount;
	}

	public void setNeedAmount(String needAmount) {
		this.needAmount = needAmount;
	}

	public String getGoodsDetailKey() {
		return goodsDetailKey;
	}

	public void setGoodsDetailKey(String goodsDetailKey) {
		this.goodsDetailKey = goodsDetailKey;
	}

	public String getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	public String getOrdRequestId() {
		return ordRequestId;
	}

	public void setOrdRequestId(String ordRequestId) {
		this.ordRequestId = ordRequestId;
	}

	public String getPayResult() {
		return payResult;
	}

	public void setPayResult(String payResult) {
		this.payResult = payResult;
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

	public String getTotalPriceItem() {
		return totalPriceItem;
	}

	public void setTotalPriceItem(String totalPriceItem) {
		this.totalPriceItem = totalPriceItem;
	}

}
