package com.beike.common.entity.trx;

import java.io.Serializable;
import java.util.Date;

import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProCheckStatus;
import com.beike.common.enums.trx.ProPayStatus;
import com.beike.common.enums.trx.ProRefundStatus;
import com.beike.common.enums.trx.ProSettleStatus;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.TrxStatus;

/**
 * @Title: Payment.java
 * @Package com.beike.common.entity.trx
 * @Description: 支付信息
 * @date May 14, 2011 7:47:25 PM
 * @author wh.cheng
 * @version v1.0
 */
public class Payment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date createDate;

	private double trxAmount;

	/** ************** 网银支付所需数据 start*********************** */
	private ProviderType providerType;// 支付结构

	private String payChannel=""; // 支付通道。为空则为到支付机构网关

	private String payRequestId=""; // 支付机构请求号，不可重复

	private ProPayStatus proPayStatus; // 支付结构回调的状态

	private ProRefundStatus proRefundStatus; // 支付结构的退款状态

	private ProCheckStatus proCheckStatus;// 支付机构对账状态

	private ProSettleStatus proSettleStatus;// 支付机构结算状态

	private Date payConfirmDate; // 支付机构支付结果回调状态。即网银支付成功时间

	private String proExternalId="";// 支付机构支付交易流水

	private double payPoundScale=0.0; // 支付费率

	private PaymentType paymentType;

	/** ************** 网银支付所需数据 end*********************** */

	private String extendInfo="";

	private String description="";

	private Long trxorderId;

	private Long accountId;

	private TrxStatus trxStatus; // 平台内的交易状态

	private String paymentSn;

	private String providerName;// 支付结构名称
	
	private String payChannelName; // 支付通道。为空则为到支付机构网关
	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;
	
	private Long couponId=0L;//优惠券id
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	public Payment() {

	}

	public Payment(Date createDate, String paymentSn, double trxAmount,
			TrxStatus trxStatus) {
		this.createDate = createDate;
		this.paymentSn = paymentSn;
		this.trxAmount = trxAmount;
		this.trxStatus = trxStatus;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public double getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}

	public ProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(ProviderType providerType) {
		this.providerType = providerType;
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

	public ProPayStatus getProPayStatus() {
		return proPayStatus;
	}

	public void setProPayStatus(ProPayStatus proPayStatus) {
		this.proPayStatus = proPayStatus;
	}

	public ProRefundStatus getProRefundStatus() {
		return proRefundStatus;
	}

	public void setProRefundStatus(ProRefundStatus proRefundStatus) {
		this.proRefundStatus = proRefundStatus;
	}

	public ProCheckStatus getProCheckStatus() {
		return proCheckStatus;
	}

	public void setProCheckStatus(ProCheckStatus proCheckStatus) {
		this.proCheckStatus = proCheckStatus;
	}

	public ProSettleStatus getProSettleStatus() {
		return proSettleStatus;
	}

	public void setProSettleStatus(ProSettleStatus proSettleStatus) {
		this.proSettleStatus = proSettleStatus;
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
		if (extendInfo == null) {
			extendInfo = "";
		}
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

	public TrxStatus getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(TrxStatus trxStatus) {
		this.trxStatus = trxStatus;
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

	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * @return the payChannelName
	 */
	public String getPayChannelName() {
		return payChannelName;
	}

	/**
	 * @param payChannelName the payChannelName to set
	 */
	public void setPayChannelName(String payChannelName) {
		this.payChannelName = payChannelName;
	}

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
	
}
