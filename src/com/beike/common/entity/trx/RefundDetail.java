package com.beike.common.entity.trx;

import java.util.Date;

import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundStatus;

/**
 * @Title: RefundDetail.java
 * @Package com.beike.common.entity.trx
 * @Description: 退款请求
 * @date May 23, 2011 2:50:14 PM
 * @author wh.cheng
 * @version v1.0
 */
public class RefundDetail {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 退款汇总记录
	 */
	private Long rudRecordId;

	/**
	 * 关联的原交易的支付机构的流水号
	 */

	private Long paymentId;

	/**
	 * 处理的批次号
	 */
	private String refundBatchId;

	/**
	 * 退款处理时间
	 */
	private Date handleDate;

	/**
	 * 账户退款处理状态
	 */
	private RefundStatus actRefundStatus;

	/**
	 * 退款处理类型
	 */
	private RefundHandleType handleType;

	/**
	 * 支付机构退款状态
	 */
	private RefundStatus proRefundStatus;
	
	/**
	 * 原PaymentType
	 */
	private PaymentType paymentType;
	
	/**
	 * 退款来源
	 */
	//private RefundSourceType refundSourceType;
	
	/**
	 * 退款操作员
	 */
	private String operator="";

	/**
	 * 原来的payment金额
	 */
	private double paymentAmount;

	/**
	 * 退款申请金额
	 */
	private double amount;

	/**
	 * 关联的原交易的支付机构的流水号
	 */
	private String proExternalId="";
	/**
	 * 支付机构退款请求号
	 */
	private String proRefundrequestId="";

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
	 public RefundDetail() {
	
	 }

	public RefundDetail(Long rudRecordId, Long paymentId, Date handleDate,
			RefundStatus actRefundStatus, double paymentAmount) {
		this.rudRecordId = rudRecordId;
		this.paymentId = paymentId;
		this.handleDate = handleDate;
		this.actRefundStatus = actRefundStatus;
		this.paymentAmount = paymentAmount;
		
		

	}

	
	
	
	public PaymentType getPaymentType() {
		return paymentType;
	}




	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRudRecordId() {
		return rudRecordId;
	}

	public void setRudRecordId(Long rudRecordId) {
		this.rudRecordId = rudRecordId;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public String getRefundBatchId() {
		return refundBatchId;
	}

	public void setRefundBatchId(String refundBatchId) {
		this.refundBatchId = refundBatchId;
	}

	public RefundStatus getActRefundStatus() {
		return actRefundStatus;
	}

	public void setActRefundStatus(RefundStatus actRefundStatus) {
		this.actRefundStatus = actRefundStatus;
	}

	public RefundHandleType getHandleType() {
		return handleType;
	}

	public void setHandleType(RefundHandleType handleType) {
		this.handleType = handleType;
	}

	public RefundStatus getProRefundStatus() {
		return proRefundStatus;
	}

	public void setProRefundStatus(RefundStatus proRefundStatus) {
		this.proRefundStatus = proRefundStatus;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getProExternalId() {
		return proExternalId;
	}

	public void setProExternalId(String proExternalId) {
		this.proExternalId = proExternalId;
	}

	public Date getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}

	public String getProRefundrequestId() {
		return proRefundrequestId;
	}

	public void setProRefundrequestId(String proRefundrequestId) {
		this.proRefundrequestId = proRefundrequestId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*public RefundSourceType getRefundSourceType() {
		return refundSourceType;
	}

	public void setRefundSourceType(RefundSourceType refundSourceType) {
		this.refundSourceType = refundSourceType;
	}*/

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}



	
}
