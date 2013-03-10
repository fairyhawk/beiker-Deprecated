package com.beike.entity.notify;

import java.util.Date;

import com.beike.common.enums.trx.NotifyType;
import com.beike.common.enums.trx.TrxBizType;

/**
 * 短信提醒通知记录表
 * @author yurenli
 *
 */
public class TrxorderNotifyRecord {

	private Long id;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 通知时间
	 */
	private Date notifyDate;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 是否已通知
	 */
	private boolean isNotify;
	/**
	 * 业务表达式
	 */
	private String express;
	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;
	/**
	 * 备注
	 */
	private String description = "";
	/**
	 * 业务类型（验证、退款、过期）
	 */
	private TrxBizType bizType;
	/**
	 * 通知类型（一次性、10天、三天）
	 */
	private NotifyType notifyType;
	
	public TrxorderNotifyRecord()
	{

	}
	
	public TrxorderNotifyRecord( Long userId, Date createDate, boolean isNotify, String express, TrxBizType bizType, NotifyType notifyType)
	{
		this.userId = userId;
		this.createDate = createDate;
		this.isNotify = isNotify;
		this.express = express;
		this.bizType = bizType;
		this.notifyType = notifyType;
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

	public Date getNotifyDate() {
		return notifyDate;
	}

	public void setNotifyDate(Date notifyDate) {
		this.notifyDate = notifyDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public boolean isNotify() {
		return isNotify;
	}

	public void setNotify(boolean isNotify) {
		this.isNotify = isNotify;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
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

	public TrxBizType getBizType() {
		return bizType;
	}

	public void setBizType(TrxBizType bizType) {
		this.bizType = bizType;
	}

	public NotifyType getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(NotifyType notifyType) {
		this.notifyType = notifyType;
	}

}
