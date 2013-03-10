package com.beike.entity.notify;

import java.util.Date;
import java.util.List;

import com.beike.common.enums.trx.NotifyType;

/**
 * @author yurenli 2012-2-10 13:20:27 账户过期通知记录表
 */
public class AccountNotifyRecord {

	private Long id;

	private Long accountId;

	private Long userId;

	private Long subAccountId;

	private Date createDate = new Date();

	private Date notifyDate = new Date();

	private double loseBalance;

	private boolean isNotify;

	private Long version = 0L;

	private String description = "";

	private NotifyType notifyType;

	private Date loseDate;
	// 减少中间查询过度List(保存ID)
	private List<Long> idList;

	public Date getLoseDate() {
		return loseDate;
	}

	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}

	public AccountNotifyRecord() {

	}

	public AccountNotifyRecord(Long accountId, Long userId, Long subAccountId,
			double loseBalance, boolean isNotify, NotifyType notifyType,
			Date loseDate) {
		this.accountId = accountId;
		this.userId = userId;
		this.subAccountId = subAccountId;
		this.loseBalance = loseBalance;
		this.isNotify = isNotify;
		this.notifyType = notifyType;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getSubAccountId() {
		return subAccountId;
	}

	public void setSubAccountId(Long subAccountId) {
		this.subAccountId = subAccountId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getNotifyDate() {
		return notifyDate;
	}

	public void setNotifyDate(Date notifyDate) {
		this.notifyDate = notifyDate;
	}

	public double getLoseBalance() {
		return loseBalance;
	}

	public void setLoseBalance(double loseBalance) {
		this.loseBalance = loseBalance;
	}

	public boolean isNotify() {
		return isNotify;
	}

	public void setNotify(boolean isNotify) {
		this.isNotify = isNotify;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public NotifyType getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(NotifyType notifyType) {
		this.notifyType = notifyType;
	}

	public List<Long> getIdList() {
		return idList;
	}

	public void setIdList(List<Long> idList) {
		this.idList = idList;
	}

}
