package com.beike.common.entity.vm;

import java.util.Date;

import com.beike.common.enums.vm.RelevanceType;

/**
 * @Title: VmTrxExtend.java
 * @Package com.beike.common.entity.vm
 * @Description: 子账户账户历史实体
 * @author renli.yu
 * @date 2011-11-15 19:04:41
 * @version V1.0
 */
public class VmTrxExtend {

	private Long id;

	private Long version;// 版本号

	private Long vmAccountId;// 虚拟款项ID

	private Long accountId;// 用户账户ID

	private Long subAccountId;// 用户子账户ID

	private Long trxOrderId;// 交易订单ID

	private Long bizId;// 业务ID

	private Long isCreditAct = 1L;// 是否入账(取消异步操作，变更为同步入账，默认为1)

	private double paymentAmount;// 支付记录金额

	private double amount;// 发生额

	private Date createDate;// 创建时间

	private Date loseDate;// 过期时间

	private String trxRequestId;// 交易订单号

	private RelevanceType relevanceType;// 关联类型

	private String description;// 描述

	public VmTrxExtend() {

	}

	public VmTrxExtend(Long vmAccountId, Long accountId, Long subAccountId,
			Long trxOrderId, Long bizId, Long isCreditAct,
			double paymentAmount, double amount, Date createDate,
			Date loseDate, String trxRequestId, RelevanceType relevanceType,
			String description) {
		super();
		this.vmAccountId = vmAccountId;
		this.accountId = accountId;
		this.subAccountId = subAccountId;
		this.trxOrderId = trxOrderId;
		this.bizId = bizId;
		this.isCreditAct = isCreditAct;
		this.paymentAmount = paymentAmount;
		this.amount = amount;
		this.createDate = createDate;
		this.loseDate = loseDate;
		this.trxRequestId = trxRequestId;
		this.relevanceType = relevanceType;
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result
				+ ((subAccountId == null) ? 0 : subAccountId.hashCode());
		result = prime * result
				+ ((trxOrderId == null) ? 0 : trxOrderId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VmTrxExtend other = (VmTrxExtend) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (subAccountId == null) {
			if (other.subAccountId != null)
				return false;
		} else if (!subAccountId.equals(other.subAccountId))
			return false;
		if (trxOrderId == null) {
			if (other.trxOrderId != null)
				return false;
		} else if (!trxOrderId.equals(other.trxOrderId))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getTrxOrderId() {
		return trxOrderId;
	}

	public void setTrxOrderId(Long trxOrderId) {
		this.trxOrderId = trxOrderId;
	}

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
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

	public String getTrxRequestId() {
		return trxRequestId;
	}

	public void setTrxRequestId(String trxRequestId) {
		this.trxRequestId = trxRequestId;
	}

	public RelevanceType getRelevanceType() {
		return relevanceType;
	}

	public void setRelevanceType(RelevanceType relevanceType) {
		this.relevanceType = relevanceType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Long getSubAccountId() {
		return subAccountId;
	}

	public void setSubAccountId(Long subAccountId) {
		this.subAccountId = subAccountId;
	}

	public Long getIsCreditAct() {
		return isCreditAct;
	}

	public void setIsCreditAct(Long isCreditAct) {
		this.isCreditAct = isCreditAct;
	}

	public Date getLoseDate() {
		return loseDate;
	}

	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}

}
