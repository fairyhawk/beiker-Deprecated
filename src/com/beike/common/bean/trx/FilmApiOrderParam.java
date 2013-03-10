package com.beike.common.bean.trx;

/**
 * @author yurenli
 *网票网相关请求返回参数信息
 *2012-12-5 16:12:39
 */
public class FilmApiOrderParam {

	/**
	 * 场次号
	 */
	private String seqNo;
	
	/**
	 * 锁坐请求唯一标示
	 */
	private String lockFlag;
	
	/**
	 * 回调参数地址
	 */
	private String filmUrl; 
	
	/**
	 * 锁坐唯一号
	 */
	private String filmSid;
	
	/**
	 * 下单唯一订单号
	 */
	private String filmPayNo;
	
	/**
	 * 支付平台成功后的标识 （支付 平台 对账标识） 
	 */
	private String platformPayNo;
	
	/**
	 * 下单手机号
	 */
	private String mobile;
	
	/**
	 * 下单支付方式
	 */
	private String payType;
	
	/**
	 *验票码发送方式 
	 */
	private String msgType;
	
	/**
	 * 下单金额
	 */
	private double amount;
	
	/**
	 * 商品类型，默认为1
	 */
	private String goodsType;

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}


	public String getLockFlag() {
		return lockFlag;
	}

	public void setLockFlag(String lockFlag) {
		this.lockFlag = lockFlag;
	}

	public String getFilmUrl() {
		return filmUrl;
	}

	public void setFilmUrl(String filmUrl) {
		this.filmUrl = filmUrl;
	}

	public String getFilmSid() {
		return filmSid;
	}

	public void setFilmSid(String filmSid) {
		this.filmSid = filmSid;
	}

	public String getFilmPayNo() {
		return filmPayNo;
	}

	public void setFilmPayNo(String filmPayNo) {
		this.filmPayNo = filmPayNo;
	}

	public String getPlatformPayNo() {
		return platformPayNo;
	}

	public void setPlatformPayNo(String platformPayNo) {
		this.platformPayNo = platformPayNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	} 
	
	
	
}
