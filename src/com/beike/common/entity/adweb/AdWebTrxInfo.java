package com.beike.common.entity.adweb;

import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */

public class AdWebTrxInfo {
	
	public Long adWebTrxId; //主键
	
	public Long adwebid;//adweb 表主键
	
	public String trxorderid;//交易订单id
	
	public String adcid;//渠道id
	
	public String adwi;//下属网站
	
	public Integer buycount;//购买数量
	
	public Double orderMoney;//总金额
	
	public Timestamp orderTime;//订单时间
	
	public String srcCode;//来源编码
	
	public String adweb_trxurl;//广告联盟请求地址
	
	public Long userId;//用户ID
	
	public int status;//订单状态：0初始 1支付
	
	public String trxStatus;

	public String getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}

	public Long getAdwebid() {
		return adwebid;
	}

	public void setAdwebid(Long adwebid) {
		this.adwebid = adwebid;
	}

	public String getTrxorderid() {
		return trxorderid;
	}

	public void setTrxorderid(String trxorderid) {
		this.trxorderid = trxorderid;
	}

	public String getAdcid() {
		return adcid;
	}

	public void setAdcid(String adcid) {
		this.adcid = adcid;
	}

	public String getAdwi() {
		return adwi;
	}

	public void setAdwi(String adwi) {
		this.adwi = adwi;
	}

	public Integer getBuycount() {
		return buycount;
	}

	public void setBuycount(Integer buycount) {
		this.buycount = buycount;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public Timestamp getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}
	
	public AdWebTrxInfo(){
		
	}

	public AdWebTrxInfo(Long adwebid, String trxorderid, String adcid,
			String adwi, Integer buycount, Double orderMoney, Timestamp orderTime) {
		this.adwebid = adwebid;
		this.trxorderid = trxorderid;
		this.adcid = adcid;
		this.adwi = adwi;
		this.buycount = buycount;
		this.orderMoney = orderMoney;
		this.orderTime = orderTime;
	}

	public Long getAdWebTrxId() {
		return adWebTrxId;
	}

	public void setAdWebTrxId(Long adWebTrxId) {
		this.adWebTrxId = adWebTrxId;
	}

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public String getAdweb_trxurl() {
		return adweb_trxurl;
	}

	public void setAdweb_trxurl(String adweb_trxurl) {
		this.adweb_trxurl = adweb_trxurl;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
