package com.beike.common.entity.trx;

import java.util.Date;

/**
 * @Title: TrxRule.java
 * @Package com.beike.common.entity.trx
 * @Description: 交易规则实体
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 10:58:51 AM
 * @version V1.0
 */
public class TrxRule {

	private Long id;

	private String trxTitle; // 交易标题

	private String trxRule;// 交易表达式

	private Date createDate;

	private Date modifyDate;

	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrxTitle() {
		return trxTitle;
	}

	public void setTrxTitle(String trxTitle) {
		this.trxTitle = trxTitle;
	}

	public String getTrxRule() {
		return trxRule;
	}

	public void setTrxRule(String trxRule) {
		this.trxRule = trxRule;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
