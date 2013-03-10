package com.beike.common.entity.vm;

import java.util.Date;

import com.beike.util.Amount;

/**
 * @Title: VmAccount.java
 * @Package com.beike.common.entity.vm
 * @Description: 虚拟账户账户实体
 * @author renli.yu
 * @date 2011-11-15 19:04:41
 * @version V1.0
 */
public class VmAccount {

	private Long id;

	private Long version=0L;// 版本号

	private Date createDate;// 创建时间

	private Date updateDate=new Date();// 更新时间

	private Date loseDate;// 过期时间

	private double totalBalance;// 总余额

	private double balance;// 余额

	private Long vmAccountSortId;// 账户类别ID

	private boolean isFund;// 是否是有金

	private String proposer;// 申请人

	private String costBear;// 成本承担方

	private String description;// 描述
	
	private String notChangeRule;// 下发规则
	
	private int isNotChange;// 是否找零
	
	private int isRefund;// 是否退款
	
	public String getNotChangeRule() {
		return notChangeRule;
	}

	public void setNotChangeRule(String notChangeRule) {
		this.notChangeRule = notChangeRule;
	}

	public int getIsNotChange() {
		return isNotChange;
	}

	public void setIsNotChange(int isNotChange) {
		this.isNotChange = isNotChange;
	}

	public VmAccount() {

	}

	public VmAccount(double balance, Date createDate, Long vmAccountSortId,
			boolean isFund, Date loseDate) {
		this.balance = balance;
		this.createDate = createDate;
		this.vmAccountSortId = vmAccountSortId;
		this.isFund = isFund;
		this.loseDate = loseDate;
	}

	/**
	 * 入款
	 * 
	 * @param amount
	 */
	public void credit(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("vm amount must be > 0");
		}

		setBalance(Amount.add(this.balance, amount));

	}

	/**
	 * 入款（总额）
	 * 
	 * @param amount
	 */
	public void creditTotal(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("vm amount must be > 0");
		}

		setTotalBalance(Amount.add(this.totalBalance, amount));

	}

	/**
	 * 出款
	 * 
	 * @param amount
	 */
	public void debit(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("vm amount must be > 0");
		}

		setBalance(Amount.sub(this.balance, amount));

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Long getVmAccountSortId() {
		return vmAccountSortId;
	}

	public void setVmAccountSortId(Long vmAccountSortId) {
		this.vmAccountSortId = vmAccountSortId;
	}

	public boolean isFund() {
		return isFund;
	}

	public void setFund(boolean isFund) {
		this.isFund = isFund;
	}

	public String getProposer() {
		return proposer;
	}

	public void setProposer(String proposer) {
		this.proposer = proposer;
	}

	public String getCostBear() {
		return costBear;
	}

	public void setCostBear(String costBear) {
		this.costBear = costBear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

    public int getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(int isRefund) {
        this.isRefund = isRefund;
    }
	
}
