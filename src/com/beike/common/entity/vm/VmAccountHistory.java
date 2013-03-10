package com.beike.common.entity.vm;

import java.util.Date;

import com.beike.common.enums.vm.VmAccountType;

/**
 * @Title: VmAccountHistory.java
 * @Package com.beike.common.entity.vm
 * @Description: 虚拟账户历史记录实体
 * @author renli.yu
 * @date 2011-11-15 19:04:41
 * @version V1.0
 */
public class VmAccountHistory {

	private Long id;

	private Long version;// 版本号

	private Long isCreditAct = 1L;// 是否入账（仅对用户帐务）(取消异步操作，变更为同步入账，默认为1)

	private Long vmAccountId;// 虚拟款项id

	private Long accountId = 0L;// 用户账户ID

	private Long subAccountId = 0L;// 用户子账户ID

	private double amount;// 发生额

	private double balance;// 当前余额

	private String requestId;// 请求号

	private Date createDate;// 创建时间

	private VmAccountType vmAccountType;// 虚拟款项类型

	private Long operatorId = 0L; // 操作员ID

	public VmAccountHistory() {

	}

	public VmAccountHistory(Long vmAccountId, double balance, double amount,
			Date createDate, String requestId, VmAccountType vmAccountType) {

		this.vmAccountId = vmAccountId;
		this.balance = balance;
		this.amount = amount;
		this.createDate = createDate;
		this.requestId = requestId;
		this.vmAccountType = vmAccountType;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public VmAccountType getVmAccountType() {
		return vmAccountType;
	}

	public void setVmAccountType(VmAccountType vmAccountType) {
		this.vmAccountType = vmAccountType;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getIsCreditAct() {
		return isCreditAct;
	}

	public void setIsCreditAct(Long isCreditAct) {
		this.isCreditAct = isCreditAct;
	}

}
