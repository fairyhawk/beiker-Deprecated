package com.beike.common.entity.vm;

import java.util.Date;

import com.beike.util.Amount;

/**
 * @Title: SubAccount.java
 * @Package com.beike.common.entity.vm
 * @Description: 子账户实体
 * @author renli.yu
 * @date 2011-11-15 19:04:41
 * @version V1.0
 */
public class SubAccount {

	private Long id;

	private Long version = 0L;// 版本号

	private Long userId; // 用户ID

	private Long accountId; // 用户账户ID

	private Long vmAccountId;// 虚拟款项账户ID

	private double balance = 0;// 余额

	private Date createDate;// 创建时间

	private Date updateDate = new Date();// 更新时间

	private Date loseDate;// 过期时间
	
	private boolean isLose = false;//是否过期

	public boolean isLose() {
		return isLose;
	}

	public void setLose(boolean isLose) {
		this.isLose = isLose;
	}

	public SubAccount() {

	}

	public SubAccount(Long userId, Long accountId, Long vmAccountId,
			Date createDate, Date loseDate) {
		this.userId = userId;
		this.accountId = accountId;
		this.vmAccountId = vmAccountId;
		this.createDate = createDate;
		this.loseDate = loseDate;
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

	public Long getVmAccountId() {
		return vmAccountId;
	}

	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getLoseDate() {
		return loseDate;
	}

	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 入款
	 * 
	 * @param amount
	 */
	public void credit(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount must be > 0");
		}

		setBalance(Amount.add(this.balance, amount));

	}

	/**
	 * 出款
	 * 
	 * @param amount
	 */
	public void debit(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount must be > 0");
		}

		setBalance(Amount.sub(this.balance, amount));

	}

}
