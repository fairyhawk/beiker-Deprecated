package com.beike.common.entity.trx;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.beike.common.enums.trx.ActHistoryType;

/**
 * @Title: AccountHistory.java
 * @Package com.beike.common.entity.trx
 * @Description: 帐务历史实体类
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 4:19:55 PM
 * @version V1.0
 */
public class AccountHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Long id;
	

	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;

	private double balance;

	private Date createDate;

	private Long accountId;

	private ActHistoryType actHistoryType; // 帐务历史类型

	private String bizType="";// 业务类型

	private double trxAmount;

	private Long trxId;

	private String description="";

	private boolean isDispaly = true;
	
	
	private Long trxOrderId=0L;
	
	
	/** 订单金额 */
	private double ordAmount;
	/** 交易流水号(对平台) */
	private String externalId;
	/** 交易状态 */
	private String trxStatus;
	/** 交易请求号(对用户) */
	private String requestId;
	/** 订单类型 */
	private String orderType;
	/** 交易对应的商品id */
	private Long trxGoodsId;
	/** 充值交易对应银行信息 */
	private String trxBlankInfo;
	/** 充值账户信息 */
	private Payment payment;
	/** 商品信息 */
	private List<TrxorderGoods> trxOrderGoodsList;
	
	
	/** 我的奖励部分添加*/
	private Long goodsId;
	
	private String goodsName;
	

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * 
	 */

	public AccountHistory() {

	}

	public AccountHistory(double balance, Long accountId,
			ActHistoryType actHistoryType, String bizType, double trxAmount,
			Long trxId,Long trxOrderId) {

		this.balance = balance;
		this.accountId = accountId;
		this.actHistoryType = actHistoryType;
		this.bizType = bizType;
		this.trxAmount = trxAmount;
		this.trxId = trxId;
		this.trxOrderId=trxOrderId;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public ActHistoryType getActHistoryType() {
		return actHistoryType;
	}

	public void setActHistoryType(ActHistoryType actHistoryType) {
		this.actHistoryType = actHistoryType;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}



	public double getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}

	public Long getTrxId() {
		return trxId;
	}

	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDispaly() {
		return isDispaly;
	}

	public void setDispaly(boolean isDispaly) {
		this.isDispaly = isDispaly;
	}

	public Long getTrxOrderId() {
		return trxOrderId;
	}

	public void setTrxOrderId(Long trxOrderId) {
		this.trxOrderId = trxOrderId;
	}

	/**
	 * 订单金额
	 * @return the ordAmount
	 */
	public double getOrdAmount() {
		return ordAmount;
	}

	/**
	 * 订单金额
	 * @param ordAmount the ordAmount to set
	 */
	public void setOrdAmount(double ordAmount) {
		this.ordAmount = ordAmount;
	}

	/**
	 * 交易流水号(对平台)
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * 交易流水号(对平台)
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * 交易状态
	 * @return the trxStatus
	 */
	public String getTrxStatus() {
		return trxStatus;
	}

	/**
	 * 交易状态
	 * @param trxStatus the trxStatus to set
	 */
	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}

	/**
	 * 交易请求号(对用户)
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * 交易请求号(对用户)
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * 订单类型
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 订单类型
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 交易对应商品信息
	 * @return the trxGoodsId
	 */
	public Long getTrxGoodsId() {
		return trxGoodsId;
	}

	/**
	 * 交易对应商品信息
	 * @param trxGoodsId the trxGoodsId to set
	 */
	public void setTrxGoodsId(Long trxGoodsId) {
		this.trxGoodsId = trxGoodsId;
	}

	/**
	 * 充值交易对应银行信息
	 * @return the trxBlankInfo
	 */
	public String getTrxBlankInfo() {
		return trxBlankInfo;
	}

	/**
	 * 充值交易对应银行信息
	 * @param trxBlankInfo the trxBlankInfo to set
	 */
	public void setTrxBlankInfo(String trxBlankInfo) {
		this.trxBlankInfo = trxBlankInfo;
	}

	/**
	 * 充值账户信息
	 * @return the payment
	 */
	public Payment getPayment() {
		return payment;
	}

	/**
	 * 充值账户信息
	 * @param payment the payment to set
	 */
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	/**
	 * 商品信息
	 * @return the trxOrderGoodsList
	 */
	public List<TrxorderGoods> getTrxOrderGoodsList() {
		return trxOrderGoodsList;
	}

	/**
	 * 商品信息
	 * @param trxOrderGoodsList the trxOrderGoodsList to set
	 */
	public void setTrxOrderGoodsList(List<TrxorderGoods> trxOrderGoodsList) {
		this.trxOrderGoodsList = trxOrderGoodsList;
	}

	
}
