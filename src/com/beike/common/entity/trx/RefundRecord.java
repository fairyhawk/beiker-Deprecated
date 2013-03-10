package com.beike.common.entity.trx;

import java.util.Date;

import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.RefundStatus;

/**
 * @Title: RefundRecord.java
 * @Package com.beike.common.entity.trx
 * @Description: 退款汇总记录
 * @date May 14, 2011 2:35:20 PM
 * @author wh.cheng
 * @version v1.0
 */
public class RefundRecord {

	private Long id;
	
	/**
	 * 原TrxOrderID
	 */
	private Long trxOrderId;

	/**
	 * 原用户ID
	 */
	private Long userId;

	/**
	 * 订单商品明细Id
	 */
	private Long trxGoodsId;

	/**
	 * 退款操作员
	 */
	private String operator="";

	/**
	 * 退款处理状态
	 */
	private RefundStatus refundStatus;

	/**
	 * 退款处理类型。人工还是自动
	 */
	private RefundHandleType handleType;
	
	/**
	 * 退款来源
	 */
	private RefundSourceType refundSourceType;
	
	/**
	 * 原下单时间
	 */
	private Date orderDate;
	/**
	 * 原支付时间
	 */
	private Date confirmDate;

	/**
	 * 退款请求时间
	 */
	private Date createDate;

	/**
	 * 原商品名称
	 */
	private String productName;
	/**
	 * 原订单金额
	 */
	private double orderAmount;
	/**
	 * 原订单商品明细订单金额
	 */
	private double trxGoodsAmount;

	/**
	 * 退款操作备注
	 */
	private String description="";
	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;
	
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	public RefundRecord(){
		
	}

	public RefundRecord(Long trxOrderId,Long userId,Long trxGoodsId,Date orderDate,Date confirmDate,Date createDate,String productName,double orderAmount,double trxGoodsAmount){
		this.trxOrderId=trxOrderId;
		this.userId=userId;
		this.trxGoodsId=trxGoodsId;
		this.orderDate=orderDate;
		this.confirmDate=confirmDate;
		this.createDate=createDate;
		this.productName=productName;
		this.orderAmount=orderAmount;
		this.trxGoodsAmount=trxGoodsAmount;
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTrxOrderId() {
		return trxOrderId;
	}

	public void setTrxOrderId(Long trxOrderId) {
		this.trxOrderId = trxOrderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTrxGoodsId() {
		return trxGoodsId;
	}

	public void setTrxGoodsId(Long trxGoodsId) {
		this.trxGoodsId = trxGoodsId;
	}

	

	public RefundStatus getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(RefundStatus refundStatus) {
		this.refundStatus = refundStatus;
	}

	public RefundHandleType getHandleType() {
		return handleType;
	}

	public void setHandleType(RefundHandleType handleType) {
		this.handleType = handleType;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public double getTrxGoodsAmount() {
		return trxGoodsAmount;
	}

	public void setTrxGoodsAmount(double trxGoodsAmount) {
		this.trxGoodsAmount = trxGoodsAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RefundSourceType getRefundSourceType() {
		return refundSourceType;
	}

	public void setRefundSourceType(RefundSourceType refundSourceType) {
		this.refundSourceType = refundSourceType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


	
}
