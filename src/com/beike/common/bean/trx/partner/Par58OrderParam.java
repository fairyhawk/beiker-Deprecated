package com.beike.common.bean.trx.partner;

import java.util.Date;
import java.util.List;

/**
 * 58传入信息转换后的参数集
 * @author q1w2e3r4
 *
 */
public class Par58OrderParam {
	
	
	//订单同步请求参数
	
	
	/**
	 * 平台goodsId
	 */
	private String goodsId ;
	
	/**
	 * 58支付价格
	 */
	private String payPrice;
	
	/**
	 * 商品数量
	 */
	private String prodCount;
	
	/**
	 * 订单创建时间
	 */
	private String createDate ;
	
	/**
	 * 订单支付时间
	 */
	private String payDate;
	
	/**
	 * 订单状态
	 */
	private String  state;
	
	/**
	 * 用户手机号
	 */
	private String mobile;
	
	//退款请求参数
	/**
	 * 订单状态
	 */
	private String status;
	
	/**
	 * 商品订单号
	 */
	private String trxGoodsSn;
	
	/**
	 * 第三方券Id（我侧voucherId）
	 */
	private String voucherId;
	
	/**
	 * 订单号
	 */
	private String orderId;
	
	/**
	 * 第三方订单号（流水号）
	 */
	private String externalId;

	/**
	 * 58券开始时间
	 */
	private Date startTime;
	/**
	 * 58券失效时间
	 */
	private Date endTime;
	
	/**
	 * 分销商对应用户ID
	 */
	private Long userId;
	
	
	private  String reason;//退款原因
	
	
	private List<Long>  userIdList;
	/**
	 * 商家goodsId
	 */
	private String outGoodsId;
	
	/**
	 * 客户端IP
	 */
	private String clientIp = "";//客户端IP

	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getPayPrice() {
		return payPrice;
	}
	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}
	public String getProdCount() {
		return prodCount;
	}
	public void setProdCount(String prodCount) {
		this.prodCount = prodCount;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}
	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}
	public String getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<Long> getUserIdList() {
		return userIdList;
	}
	public void setUserIdList(List<Long> userIdList) {
		this.userIdList = userIdList;
	}
	public String getOutGoodsId() {
		return outGoodsId;
	}
	public void setOutGoodsId(String outGoodsId) {
		this.outGoodsId = outGoodsId;
	}
	
	


	

}
