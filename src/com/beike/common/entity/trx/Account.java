package com.beike.common.entity.trx;

import java.util.Date;

import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.util.Amount;

/**
 * @Title: Account.java
 * @Package com.beike.common.entity.trx
 * @Description: 账户实体
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 10:58:51 AM
 * @version V1.0
 */
public class Account {

	private Long id;

	// private String actSn;

	private Long version = 0L;// 乐观锁版本号

	private double balance;

	private AccountType accountType;

	private AccountStatus accountStatus;

	private Date lastUpdateDate;

	private Date createDate;

	private double forzenAmount;

	private Long userId;

	public Account() {

	}

	public Account(Long userId, double balance, AccountType accountType,
			AccountStatus accountSatatus, Date createDate, Date lastUpdateDate) {
		this.userId = userId;
		// this.actSn = actSn;
		this.accountStatus = accountSatatus;
		this.accountType = accountType;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
		this.balance = balance;

	}

	// public String getActSn() {
	// return actSn;
	// }
	//
	// public void setActSn(String actSn) {
	// this.actSn = actSn;
	// }

	public double getBalance() {
		return balance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public double getForzenAmount() {
		return forzenAmount;
	}

	public void setForzenAmount(double forzenAmount) {
		this.forzenAmount = forzenAmount;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Long getVersion() {
		return version;
	}

	public void settVersion(Long version) {
		this.version = version;
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
		if (!AccountStatus.ACTIVE.equals(getAccountStatus())) {

			throw new IllegalStateException("account status is invalid");
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
		if (!AccountStatus.ACTIVE.equals(getAccountStatus())) {

			throw new IllegalStateException("account status is invalid");
		}

		setBalance(Amount.sub(this.balance, amount));

	}

}
