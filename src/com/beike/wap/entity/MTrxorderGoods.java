package com.beike.wap.entity;

import java.util.Date;

import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.TrxStatus;

/**
 * @Title: Trxoder_goods.java
 * @Package com.beike.common.entity.trx
 * @Description: 订单商品明细
 * @date May 14, 2011 8:52:26 PM
 * @author wh.cheng
 * @version v1.0
 */
public class MTrxorderGoods {

	private Long id;

	private String trxGoodsSn;

	private String goodsName; // 商品名称

	private double sourcePrice; // 商品原价

	private double currentPrice; // 商品当前价

	private double payPrice; // 商品支付价格

	private double rebatePrice; // 返现价格

	private double dividePrice; // 分成价格（分成）

	private TrxStatus trxStatus;

	private AuthStatus authStatus;

	private String extend_info = "";

	private String description = "";

	private Date authDate;

	private Long trxorderId;

	private Long goodsId; // 商品ID
	
	private String voucherCode;

	private MerSettleStatus merSettleStatus;

	private Date createDate;

	private Long guestId = 0L;
	private Long voucherId = 0L;
	private Long commentId = 0L;
	private Long orderLoseAbsDate = 0L;
	private Date orderLoseDate;
	private String logo3;
	private Date handleDate;
	private String trxStatusSign;
	private Date confirmDate;
	private String tel = "";
	/**
	 * 前端显示新增属性
	 */
	private String belongMerchant;
	private String goodsPicrl;

	private Date reallyLoseDate;

	private String commentPoint;

	/**
	 * 新增冻结和结算相关属性
	 */

	private Long isFreeze = 0L;

	private Long settlementRecordId = 0L;

	/**
	 * 新增交易类型ID
	 */
	private Long trxRuleId;

	/**
	 * 
	 * @param trxGoodsSn
	 * @param goodsName
	 * @param sourcePrice
	 * @param currentPrice
	 * @param payPrice
	 * @param rebatePrice
	 * @param dividePrice
	 * @param trxStatus
	 * @param authStatus
	 * @param trxorderId
	 * @param merSettleStatus
	 * @param createDate
	 * @param guestId
	 * @param orderLoseAbsDate
	 * @param orderLoseDate
	 */

	public MTrxorderGoods(String trxGoodsSn, String goodsName,
			double sourcePrice,

			double currentPrice, double payPrice, double rebatePrice,
			double dividePrice, TrxStatus trxStatus, AuthStatus authStatus,
			Long trxorderId, MerSettleStatus merSettleStatus, Date createDate,
			Long guestId, Long orderLoseAbsDate, Date orderLoseDate) {
		this.trxGoodsSn = trxGoodsSn;
		this.goodsName = goodsName;
		this.sourcePrice = sourcePrice;
		this.currentPrice = currentPrice;
		this.payPrice = payPrice;
		this.rebatePrice = rebatePrice;
		this.rebatePrice = rebatePrice;
		this.trxStatus = trxStatus;
		this.authStatus = authStatus;
		this.trxorderId = trxorderId;
		this.merSettleStatus = merSettleStatus;
		this.createDate = createDate;
		this.guestId = guestId;
		this.orderLoseAbsDate = orderLoseAbsDate;
		this.orderLoseDate = orderLoseDate;

	}

	public MTrxorderGoods() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}

	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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

	public TrxStatus getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(TrxStatus trxStatus) {
		this.trxStatus = trxStatus;
	}

	public AuthStatus getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(AuthStatus authStatus) {
		this.authStatus = authStatus;
	}

	public String getExtend_info() {
		return extend_info;
	}

	public void setExtend_info(String extend_info) {
		this.extend_info = extend_info;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getAuthDate() {
		return authDate;
	}

	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}

	public Long getTrxorderId() {
		return trxorderId;
	}

	public void setTrxorderId(Long trxorderId) {
		this.trxorderId = trxorderId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public MerSettleStatus getMerSettleStatus() {
		return merSettleStatus;
	}

	public void setMerSettleStatus(MerSettleStatus merSettleStatus) {
		this.merSettleStatus = merSettleStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getOrderLoseAbsDate() {
		return orderLoseAbsDate;
	}

	public void setOrderLoseAbsDate(Long orderLoseAbsDate) {
		this.orderLoseAbsDate = orderLoseAbsDate;
	}

	public Date getOrderLoseDate() {
		return orderLoseDate;
	}

	public void setOrderLoseDate(Date orderLoseDate) {
		this.orderLoseDate = orderLoseDate;
	}

	public String getGoodsPicrl() {
		return goodsPicrl;
	}

	public void setGoodsPicrl(String goodsPicrl) {
		this.goodsPicrl = goodsPicrl;
	}

	public Date getReallyLoseDate() {
		return reallyLoseDate;
	}

	public void setReallyLoseDate(Date reallyLoseDate) {
		this.reallyLoseDate = reallyLoseDate;
	}

	public String getBelongMerchant() {
		return belongMerchant;
	}

	public void setBelongMerchant(String belongMerchant) {
		this.belongMerchant = belongMerchant;
	}

	public String getCommentPoint() {
		return commentPoint;
	}

	public void setCommentPoint(String commentPoint) {
		this.commentPoint = commentPoint;
	}

	public Long getIsFreeze() {
		return isFreeze;
	}

	public void setIsFreeze(Long isFreeze) {
		this.isFreeze = isFreeze;
	}

	public Long getSettlementRecordId() {
		return settlementRecordId;
	}

	public void setSettlementRecordId(Long settlementRecordId) {
		this.settlementRecordId = settlementRecordId;
	}

	public Long getTrxRuleId() {
		return trxRuleId;
	}

	public void setTrxRuleId(Long trxRuleId) {
		this.trxRuleId = trxRuleId;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String getLogo3() {
		return logo3;
	}

	public void setLogo3(String logo3) {
		this.logo3 = logo3;
	}

	public Date getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}

	public String getTrxStatusSign() {
		return trxStatusSign;
	}

	public void setTrxStatusSign(String trxStatusSign) {
		this.trxStatusSign = trxStatusSign;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	
}
