package com.beike.common.entity.trx;

import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.TrxStatus;

/**
 * @Title: RebateRec.java
 * @Package com.beike.common.entity.trx
 * @Description: 返现记录
 * @author wh.cheng@sinobogroup.com
 * @date May 9, 2011 12:28:04 AM
 * @version V1.0
 */
public class RebRecord extends BaseOrder {

	public RebRecord() {

	}
	private double trxAmount;
	private Long bizId;
	public Long goodsId;
	public String goodsName;
	public RebRecord(String requestId, String externalId, Long userId,
			OrderType orderType, TrxStatus trxStatus) {

		this.requestId = requestId;
		this.externalId = externalId;
		this.userId = userId;
		this.orderType = orderType;
		this.trxStatus = trxStatus;

	}


	public double getTrxAmount() {
		return trxAmount;
	}


	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}
	
	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	

	public Long getGoodsId() {
		return goodsId;
	}


	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}


	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}


	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	

}
