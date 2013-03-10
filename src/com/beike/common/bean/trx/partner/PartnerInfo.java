package com.beike.common.bean.trx.partner;

import java.io.Serializable;

/**
 * @Title: PartnerInfo.java
 * @Package  com.beike.common.bean.trx
 * @Description: 合作分销商相关参数集合
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PartnerInfo  implements Serializable {

	private static final long serialVersionUID = -6476958616549404664L;

	private String partnerNo;//分销商接口编号
	
	private String keyValue;//密匙
	
	private Long userId;//用户ID
		
	private String partnerName;//分销商名称
	
	private String trxExpress;//交易表达式
	
	private String apiType;//api类型
	
	private String subName;//分销商简称
	
	private String smsExpress;//短信发送表达式
	
	private String ip;
	
	
	private Long isAvailable;//是否有效
	
	private String sessianKey;
	
	private String description;//淘宝APP_key
	
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

	
	/**
	 * 分销商核销回调时使用
	 */
	private String token ;
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSessianKey() {
		return sessianKey;
	}

	public void setSessianKey(String sessianKey) {
		this.sessianKey = sessianKey;
	}

	public PartnerInfo(){
		
		super();
	}

	public PartnerInfo( String keyValue, Long userId,
			String partnerName, Long isAvailable,String trxExpress, String apiType, String subName, String smsExpress, String ip,String sessianKey,String description,String partnerNo,String noticeKeyValue) {
		super();
		this.keyValue = keyValue;
		this.userId = userId;
		this.partnerName = partnerName;
		this.isAvailable=isAvailable;
		this.trxExpress = trxExpress;
		this.apiType = apiType;
		this.subName = subName;
		this.smsExpress = smsExpress;
		this.ip = ip;
		this.sessianKey = sessianKey;
		this.description = description;
		this.partnerNo = partnerNo;
		this.noticeKeyValue = noticeKeyValue;
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

	public Long getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Long isAvailable) {
		this.isAvailable = isAvailable;
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

	@Override
	public String toString() {
		return " [apiType=" + apiType + ",ip=" + ip + ", isAvailable=" + isAvailable
				+ " partnerName="+ partnerName + ", partnerNo=" + partnerNo + ", userId=" 
				+ userId + "]";
	}


	
	

}