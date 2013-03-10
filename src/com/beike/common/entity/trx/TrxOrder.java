package com.beike.common.entity.trx;

import com.beike.common.enums.trx.ReqChannel;

/**
 * @Title: TrxOrder.java
 * @Package com.beike.common.entity.trx
 * @Description: 交易订单
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 4:21:10 PM
 * @version V1.0
 */    
public class TrxOrder extends BaseOrder {
	private double ordAmount;			//订单金额
	
	private String description="";		//描述
	
	private String mobile="" ;			//手机号
	
	private String outRequestId="";		//外部请求号
	
	private ReqChannel reqChannel;		//请求渠道
	
	private Long version = 0L;		//乐观锁版本号
	
	/**
	 * 客户端IP
	 */
	private String reqIp = "";//客户端IP

	
	
	
	public String getReqIp() {
		return reqIp;
	}

	public void setReqIp(String reqIp) {
		this.reqIp = reqIp;
	}

	/**
	 * get and set method
	 */
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	

	public double getOrdAmount() {
		return ordAmount;
	}

	public void setOrdAmount(double ordAmount) {
		this.ordAmount = ordAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOutRequestId() {
		return outRequestId;
	}

	public void setOutRequestId(String outRequestId) {
		this.outRequestId = outRequestId;
	}

	public ReqChannel getReqChannel() {
		return reqChannel;
	}

	public void setReqChannel(ReqChannel reqChannel) {
		this.reqChannel = reqChannel;
	}

}
