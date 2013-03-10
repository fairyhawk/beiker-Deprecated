package com.beike.common.bean.trx.partner;

import java.util.Date;
import java.util.List;

/**
 * Title : ParT800OrderParam.java <br/>
 * Description : 800传入信息转换后的参数集<br/>
 * Company : Sinobo <br/>
 * Copyright : Copyright (c) 2010-2012 All rights reserved.<br/>
 * Created : 2012-11-7 上午10:55:13 <br/>
 * 
 * @author Wenzhong Gu
 * @version 1.0
 */
public class ParT800OrderParam {

	// 订单同步请求参数

	/**
	 * 平台goodsId
	 */
	private String goodsId;
	
	private String totalPrice;

	/**
	 * 800支付价格
	 */
	private String payPrice;

	/**
	 * 商品数量
	 */
	private String prodCount;

	/**
	 * 订单创建时间
	 */
	private String createDate;

	/**
	 * 订单支付时间
	 */
	private String payDate;

	/**
	 * 用户手机号
	 */
	private String mobile;

	/**
	 * 订单状态
	 */
	private String state;

	// 退款请求参数
	/**
	 * 订单状态
	 */
	private String status;

	/**
	 * 发货方
	 */
	private String sender;

	/**
	 * 短信模板
	 */
	private String smsTemplate;

	/**
	 * 商品订单号
	 */
	private String trxGoodsSn;

	/**
	 * 第三方券Id（我侧voucherId）
	 * TODO 目前发的是券码
	 */
	private String voucherId;

	/**
	 * 券码
	 */
	private String voucherCode;

	/**
	 * 订单号
	 */
	private String orderId;

	/**
	 * 第三方订单号（流水号）
	 */
	private String externalId;

	/**
	 * 团800订单号
	 */
	private String outRequestId;

	/**
	 * 团800券开始时间
	 */
	private Date startTime;
	/**
	 * 团800券失效时间
	 */
	private Date endTime;

	/**
	 * 分销商对应用户ID
	 */
	private Long userId;
	
	private String partnerId;

	private String reason;// 退款原因

	private List<Long> userIdList;
	/**
	 * 商家goodsId
	 */
	private String outGoodsId;

	/**
	 * 客户端IP
	 */
	private String clientIp = "";// 客户端IP

	/**
	 * @return the goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}

	/**
	 * @return the totalPrice
	 */
	public String getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
	 * @param goodsId
	 *            the goodsId to set
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * @return the payPrice
	 */
	public String getPayPrice() {
		return payPrice;
	}

	/**
	 * @param payPrice
	 *            the payPrice to set
	 */
	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	/**
	 * @return the prodCount
	 */
	public String getProdCount() {
		return prodCount;
	}

	/**
	 * @param prodCount
	 *            the prodCount to set
	 */
	public void setProdCount(String prodCount) {
		this.prodCount = prodCount;
	}

	/**
	 * @return the createDate
	 */
	public String getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the payDate
	 */
	public String getPayDate() {
		return payDate;
	}

	/**
	 * @param payDate
	 *            the payDate to set
	 */
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the trxGoodsSn
	 */
	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}

	/**
	 * @param trxGoodsSn
	 *            the trxGoodsSn to set
	 */
	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}

	/**
	 * @return the voucherId
	 */
	public String getVoucherId() {
		return voucherId;
	}

	/**
	 * @param voucherId
	 *            the voucherId to set
	 */
	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId
	 *            the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the userIdList
	 */
	public List<Long> getUserIdList() {
		return userIdList;
	}

	/**
	 * @param userIdList
	 *            the userIdList to set
	 */
	public void setUserIdList(List<Long> userIdList) {
		this.userIdList = userIdList;
	}

	/**
	 * @return the outGoodsId
	 */
	public String getOutGoodsId() {
		return outGoodsId;
	}

	/**
	 * @param outGoodsId
	 *            the outGoodsId to set
	 */
	public void setOutGoodsId(String outGoodsId) {
		this.outGoodsId = outGoodsId;
	}

	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @param clientIp
	 *            the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the smsTemplate
	 */
	public String getSmsTemplate() {
		return smsTemplate;
	}

	/**
	 * @param smsTemplate
	 *            the smsTemplate to set
	 */
	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	/**
	 * @return the voucherCode
	 */
	public String getVoucherCode() {
		return voucherCode;
	}

	/**
	 * @param voucherCode
	 *            the voucherCode to set
	 */
	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	/**
	 * @return the outRequestId
	 */
	public String getOutRequestId() {
		return outRequestId;
	}

	/**
	 * @param outRequestId the outRequestId to set
	 */
	public void setOutRequestId(String outRequestId) {
		this.outRequestId = outRequestId;
	}

	/**
	 * @return the partnerId
	 */
	public String getPartnerId() {
		return partnerId;
	}

	/**
	 * @param partnerId the partnerId to set
	 */
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

}