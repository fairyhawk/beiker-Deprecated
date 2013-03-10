package com.beike.common.entity.vm;

import java.util.Date;

import com.beike.common.enums.trx.CancelType;

public class VmCancelRecord {

	private Long id;
	
	private Long version = 0L;
	
	private Long vmAccountId;
	
	private Long accountId;
	
	private Long subAccountId;
	
	private double amount ;
	
	private Date createDate;
	
	private Date updateDate;
	
	private Long operatorId = 0L;
	
	private CancelType cancelType;
	
	

	public CancelType getCancelType() {
		return cancelType;
	}

	public void setCancelType(CancelType cancelType) {
		this.cancelType = cancelType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getVmAccountId() {
		return vmAccountId;
	}

	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getSubAccountId() {
		return subAccountId;
	}

	public void setSubAccountId(Long subAccountId) {
		this.subAccountId = subAccountId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
}
