package com.beike.entity.partner;

import java.sql.Timestamp;
import java.util.Date;

import com.beike.common.enums.trx.RtnPointRule;

/**
 * 对应数据库表PartnerRtnsPoint
 * 主要是记录分销商是否返点和对的返点规则
 * @author ljp
 * @date 20121224
 *
 */
public class PartnerRtnPoint {
	private Long id              ;//主键
	private Long trxOrderId     ;//交易订单ID
	private Long trxGoodsId    ;//商品订单ID
	private Long voucherId      ;//凭证ID
	private Long tagId          ;//品类ID
	private int  rtnPointType  ;//是否参与结算返点（0:不参与返点1：参与返点）
	private String partnerNo      ;//分销商编号
	private String outRequestId  ;//外部交易请求号(对分销商)
	private String trxGoodsSn    ;//商品订单序列号
	private RtnPointRule rtnPointRule  ;//结算返点表达式
	private String description     ;//备注
	private String tagName        ;//品类名称
	private Date createDate     ;//创建日期
	private Date modifyDate     ;//修改日期
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
	public Long getTagId() {
		return tagId;
	}
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
	public int getRtnPointType() {
		return rtnPointType;
	}
	public void setRtnPointType(int rtnPointType) {
		this.rtnPointType = rtnPointType;
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
	public RtnPointRule getRtnPointRule() {
		return rtnPointRule;
	}
	public void setRtnPointRule(RtnPointRule rtnPointRule) {
		this.rtnPointRule = rtnPointRule;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
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
	@Override
	public String toString() {
		return "PartnerRtnsPoint [id=" + id + ", trxOrderId=" + trxOrderId
				+ ", trxGoodsId=" + trxGoodsId + ", voucherId=" + voucherId
				+ ", tagId=" + tagId + ", rtnPointType=" + rtnPointType
				+ ", partnerNo=" + partnerNo + ", outRequestId=" + outRequestId
				+ ", trxGoodsSn=" + trxGoodsSn + ", rtnPointRule="
				+ rtnPointRule + ", description=" + description + ", tagName="
				+ tagName + ", createDate=" + createDate + ", modifyDate="
				+ modifyDate + "]";
	}
	
	
	
}
