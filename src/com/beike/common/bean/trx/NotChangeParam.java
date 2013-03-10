package com.beike.common.bean.trx;
/**
 * @Title: NotChangeParam.java
 * @Package com.beike.common.bean
 * @Description: 交易不找零参数集
 * @date 09 16, 2012 6:25:16 PM
 * @author wh.cheng
 * @version v1.0
 */
public class NotChangeParam {
	
	
	private double trxAmount;//本次不找零的发生额（例如：余额20，购买5元商品不找零，则此金额为5元）
	
	private Long subAccountId;//个人子账户ID
	
	
	private Long accountId;//个人总账户ID
	
	
	private Long vmAccountId;//虚拟款项ID
	
	private Long trxId =0L;
	
	public NotChangeParam(double trxAmount,Long subAccountId, Long accountId, Long vmAccountId,Long trxId) {
		this.trxAmount = trxAmount;
		this.subAccountId = subAccountId;
		this.accountId = accountId;
		this.vmAccountId = vmAccountId;
	}

	public double getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}
	public Long getSubAccountId() {
		return subAccountId;
	}


	public void setSubAccountId(Long subAccountId) {
		this.subAccountId = subAccountId;
	}


	public Long getAccountId() {
		return accountId;
	}


	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}


	public Long getVmAccountId() {
		return vmAccountId;
	}


	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}

	public Long getTrxId() {
		return trxId;
	}
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}

	@Override
	public String toString() {
		return "NotChangeParam [accountId=" + accountId + ", subAccountId="
				+ subAccountId + ", vmAccountId=" + vmAccountId + "]";
	}
	
	
	

}
