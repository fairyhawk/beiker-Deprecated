package com.beike.common.bean.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.Payment;
import com.beike.common.enums.trx.PaymentType;

/**
 * @Title: PaymentInfo.java
 * @Package com.beike.common.bean.trx
 * @Description: payment所需信息
 * @date May 17, 2011 5:36:41 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PaymentInfo {

	private double trxAmount;//回调金额

	private String providerType; // 支付机构
	private String payChannel; // 支付通道。为空则为到支付机构网关

	private String payRequestId; // 支付机构请求号，不可重复

	private Date payConfirmDate; // 支付机构支付结果回调状态。即网银支付成功时间

	private String proExternalId;// 支付机构支付交易流水

	private double payPoundScale; // 支付费率

	private PaymentType paymentType;

	private String extendInfo;

	private String description;

	private Long trxorderId;
	
	private  double trxOrderAmount;//交易订单金额

	private Long accountId;

	private String paymentSn;
	
	private Long couponId = 0L;	//优惠券订单号
	
	private List<Payment>   paymentList;//预查询出来的支付记录

	
	
	
	
	
	public PaymentInfo() {
		super();
	}

	public PaymentInfo(double trxAmount, String payRequestId,
			Date payConfirmDate, String proExternalId,PaymentType paymentType,double trxOrderAmount) {
		super();
		this.trxAmount = trxAmount;
		this.payRequestId = payRequestId;
		this.payConfirmDate = payConfirmDate;
		this.proExternalId = proExternalId;
		this.paymentType=paymentType;
		this.trxOrderAmount=trxOrderAmount;
	}
	
	/**
	 * 根据类型paymentType 获取响应Payment
	 * @param paymentList
	 * @return
	 */
	public static Payment getPaymentByType(List<Payment>  paymentList,PaymentType paymentType){

			for (Payment itemPayment : paymentList) {
				if(paymentType.equals(itemPayment.getPaymentType())){
					
					return itemPayment;
				}
		
			
		}
			return  null;
		
		
	}
	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public double getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getPayRequestId() {
		return payRequestId;
	}

	public void setPayRequestId(String payRequestId) {
		this.payRequestId = payRequestId;
	}

	public Date getPayConfirmDate() {
		return payConfirmDate;
	}

	public void setPayConfirmDate(Date payConfirmDate) {
		this.payConfirmDate = payConfirmDate;
	}

	public double getPayPoundScale() {
		return payPoundScale;
	}

	public void setPayPoundScale(double payPoundScale) {
		this.payPoundScale = payPoundScale;
	}

	public String getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTrxorderId() {
		return trxorderId;
	}

	public void setTrxorderId(Long trxorderId) {
		this.trxorderId = trxorderId;
	}


	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getProExternalId() {
		return proExternalId;
	}

	public void setProExternalId(String proExternalId) {
		this.proExternalId = proExternalId;
	}

	public String getPaymentSn() {
		return paymentSn;
	}

	public void setPaymentSn(String paymentSn) {
		this.paymentSn = paymentSn;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public double getTrxOrderAmount() {
		return trxOrderAmount;
	}

	public void setTrxOrderAmount(double trxOrderAmount) {
		this.trxOrderAmount = trxOrderAmount;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
}
