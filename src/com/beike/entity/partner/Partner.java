package com.beike.entity.partner;

import java.util.Date;

/**
 * 
 * @author zhaofeilong 2012-05-30 分销商表
 * @version 1.0
 */
public class Partner {
	
	private Long id;
	
	private String partnerNo;//分销商接口编号
	
	private String keyValue;//密匙
	
	private Long userId;//用户ID
	
	private Long isAvailable;//是否有效
	
	private String partnerName;//分销商名称
	
	private String trxExpress;//交易表达式
	
	private String apiType;//api类型
	
	private String subName;//分销商简称
	
	private String smsExpress;//短信发送表达式
	
	private String version;//乐观锁版本号
	
	private String ip;
	
	private Date createDate;//创建时间
	
	private Date updateDate;//更新时间
	
	private String description;//描述信息
	
	private String sessianKey;
	
	/**
	 * 淘宝开放平台密钥
	 */
	private String noticeKeyValue;

	public String getNoticeKeyValue() {
		return noticeKeyValue;
	}

	public void setNoticeKeyValue(String noticeKeyValue) {
		this.noticeKeyValue = noticeKeyValue;
	}

	public String getSessianKey() {
		return sessianKey;
	}

	public void setSessianKey(String sessianKey) {
		this.sessianKey = sessianKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartnerNo() {
		return partnerNo;
	}

	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}


	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Long isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getTrxExpress() {
		return trxExpress;
	}

	public void setTrxExpress(String trxExpress) {
		this.trxExpress = trxExpress;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getSmsExpress() {
		return smsExpress;
	}

	public void setSmsExpress(String smsExpress) {
		this.smsExpress = smsExpress;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
