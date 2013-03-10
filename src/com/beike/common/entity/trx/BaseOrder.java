package com.beike.common.entity.trx;

import java.util.Date;

import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.TrxStatus;

/**
 * @Title: BaseOrder.java
 * @Package com.beike.common.entity.trx
 * @Description: 交易记录和返现记录父类
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 4:21:02 PM
 * @version V1.0
 */
public class BaseOrder {

	protected Long id;

	protected String requestId;

	protected String externalId;

	protected Date createDate;

	protected String extendInfo="";

	protected Date closeDate;

	protected Long userId;

	protected OrderType orderType;

	protected TrxStatus trxStatus;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public TrxStatus getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(TrxStatus trxStatus) {
		this.trxStatus = trxStatus;
	}


	

}
