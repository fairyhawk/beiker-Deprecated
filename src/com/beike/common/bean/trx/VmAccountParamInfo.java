package com.beike.common.bean.trx;

import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;

/**
 * @Title: VmAccountParamInfo
 * @Package com.beike.common.bean.trx
 * @Description: 虚拟款项参数类
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:18:52 PM
 * @version V1.0
 */
public class VmAccountParamInfo {

	private String loseDate;// 过期时间

	private String totalBalance;// 总余额

	private String balance;// 余额

	private String vmAccountSortId;// 账户类别

	private String isFund;// 是否是有金

	private String proposer;// 申请人

	private String costBear;// 成本承担方

	private String description;// 描述

	private String vmAccountId; // 虚拟款项账户ID
	private String userId;// 用户登录名

	private String requestId;// 下发，追加请求ID

	private String operatorId;// 操作者ID

	private String amount;// 发生额

	private ActHistoryType actHistoryType;// 下发类型
	
	private String notChangeRule;// 下发规则
	
	private int isNotChange;// 是否找零
	
	private String isRefund;// 是否退款
	
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
	
	private BizType bizType;
	public BizType getBizType() {
		return bizType;
	}

	public void setBizType(BizType bizType) {
		this.bizType = bizType;
	}

	public VmAccountParamInfo() {

	}

	public VmAccountParamInfo(String balance, String vmAccountSortId,
			String loseDate) {

		this.balance = balance;
		this.vmAccountSortId = vmAccountSortId;
		this.loseDate = loseDate;

	}

	public ActHistoryType getActHistoryType() {
		return actHistoryType;
	}

	public void setActHistoryType(ActHistoryType actHistoryType) {
		this.actHistoryType = actHistoryType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getLoseDate() {
		return loseDate;
	}

	public void setLoseDate(String loseDate) {
		this.loseDate = loseDate;
	}

	public String getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(String totalBalance) {
		this.totalBalance = totalBalance;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getVmAccountSortId() {
		return vmAccountSortId;
	}

	public String getIsFund() {
		return isFund;
	}

	public void setIsFund(String isFund) {
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

	public String getVmAccountId() {
		return vmAccountId;
	}

	public void setVmAccountId(String vmAccountId) {
		this.vmAccountId = vmAccountId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setVmAccountSortId(String vmAccountSortId) {
		this.vmAccountSortId = vmAccountSortId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

}
