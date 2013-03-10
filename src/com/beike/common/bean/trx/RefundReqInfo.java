package com.beike.common.bean.trx;

import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundDetail;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxorderGoods;

/**   
 * @title: RefundReqInfo.java
 * @package com.beike.common.bean.trx
 * @description: 
 * @author wangweijie  
 * @date 2012-8-13 下午06:34:12
 * @version v1.0   
 */
public class RefundReqInfo {
	private String operator;
	private String description;
	private RefundDetail refundDetail;
	private RefundRecord refundRecord;
	private Payment	payment;
	private TrxorderGoods trxorderGoods;
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public RefundDetail getRefundDetail() {
		return refundDetail;
	}
	public void setRefundDetail(RefundDetail refundDetail) {
		this.refundDetail = refundDetail;
	}
	public RefundRecord getRefundRecord() {
		return refundRecord;
	}
	public void setRefundRecord(RefundRecord refundRecord) {
		this.refundRecord = refundRecord;
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	public TrxorderGoods getTrxorderGoods() {
		return trxorderGoods;
	}
	public void setTrxorderGoods(TrxorderGoods trxorderGoods) {
		this.trxorderGoods = trxorderGoods;
	}
}
