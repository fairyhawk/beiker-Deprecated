package com.beike.common.bean.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;

/**
 * @Title: PayParaM.java
 * @Package com.beike.common.bean.trx
 * @Description: TODO
 * @date May 17, 2011 10:01:04 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PayRspInfo {

	private String payRequestId;

	private String proExternallId;

	private Date proConfirmDate;

	private double sucTrxAmount;

	private String bizType;

	private Long trxId;
	
	
	private  List<Payment>  paymentList;//预查询所需支付记录
	
	private  List<Account>  accountList;//预查询所需个人账户
	
	private TrxCoupon trxCoupon;	//预查询 优惠券信息
	
	private  TrxOrder trxOrder;//预查询所需交易订单
	
	private  List<TrxorderGoods>  tgGoodsList;//预查询所需商品订单
	
	
	

	public String getPayRequestId() {
		return payRequestId;
	}

	public void setPayRequestId(String payRequestId) {
		this.payRequestId = payRequestId;
	}

	public String getProExternallId() {
		return proExternallId;
	}

	public void setProExternallId(String proExternallId) {
		this.proExternallId = proExternallId;
	}

	public Date getProConfirmDate() {
		return proConfirmDate;
	}

	public void setProConfirmDate(Date proConfirmDate) {
		this.proConfirmDate = proConfirmDate;
	}

	public double getSucTrxAmount() {
		return sucTrxAmount;
	}

	public void setSucTrxAmount(double sucTrxAmount) {
		this.sucTrxAmount = sucTrxAmount;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public Long getTrxId() {
		return trxId;
	}

	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}



	public TrxOrder getTrxOrder() {
		return trxOrder;
	}

	public void setTrxOrder(TrxOrder trxOrder) {
		this.trxOrder = trxOrder;
	}

	public List<TrxorderGoods> getTgGoodsList() {
		return tgGoodsList;
	}

	public void setTgGoodsList(List<TrxorderGoods> tgGoodsList) {
		this.tgGoodsList = tgGoodsList;
	}

	public TrxCoupon getTrxCoupon() {
		return trxCoupon;
	}

	public void setTrxCoupon(TrxCoupon trxCoupon) {
		this.trxCoupon = trxCoupon;
	}
}
