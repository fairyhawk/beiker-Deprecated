package com.beike.entity.partner;

import java.util.Date;

/**   
 * @title: PartnerBindVoucher.java
 * @package com.beike.common.bean.trx.partner
 * @description: 第三方凭证表
 * @author wangweijie  
 * @date 2012-9-6 下午06:23:27
 * @version v1.0   
 */
public class PartnerBindVoucher {
	private Long id;	//主键
	private Long trxOrderId;		//订单ID
	private Long trxGoodsId;		//商品订单ID
	private Long voucherId;			//千品凭证ID
	private String partnerNo;		//分销商编号
	private String outRequestId;	//外部交易请求号(对分销商)
	private String trxGoodsSn; 		//商品订单号
	private String voucherCode;		//千品凭证号
	private String outCouponId;		//外部券ID
	private String outCouponPwd;	//外部券密码
	private Date createDate = new Date();	//创建日期
	private Date modifyDate = new Date();	//修改时间
	
	
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
	public Long getTrxGoodsId() {
		return trxGoodsId;
	}
	public void setTrxGoodsId(Long trxGoodsId) {
		this.trxGoodsId = trxGoodsId;
	}
	public Long getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}
	public String getPartnerNo() {
		return partnerNo;
	}
	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}
	public String getOutRequestId() {
		return outRequestId;
	}
	public void setOutRequestId(String outRequestId) {
		this.outRequestId = outRequestId;
	}
	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}
	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}
	public String getVoucherCode() {
		return voucherCode;
	}
	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
	public String getOutCouponId() {
		return outCouponId;
	}
	public void setOutCouponId(String outCouponId) {
		this.outCouponId = outCouponId;
	}
	public String getOutCouponPwd() {
		return outCouponPwd;
	}
	public void setOutCouponPwd(String outCouponPwd) {
		this.outCouponPwd = outCouponPwd;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
}
