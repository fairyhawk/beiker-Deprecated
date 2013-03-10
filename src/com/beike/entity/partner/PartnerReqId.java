package com.beike.entity.partner;

import java.util.Date;


public class PartnerReqId {
	
	private Long id;
	
	private String partnerNo;//分销商接口编号
	
	private String requestId;//分销商订单号
	
	private Date createDate;//创建时间
	
	private Long version = 0L;

	
	
	
	public PartnerReqId() {
		super();
	}

	public PartnerReqId( String partnerNo, String requestId,
			Date createDate) {
		this.partnerNo = partnerNo;
		this.requestId = requestId;
		this.createDate = createDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartnerNo() {
		return partnerNo;
	}

	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	
}
