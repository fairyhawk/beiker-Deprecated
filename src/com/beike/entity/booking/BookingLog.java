package com.beike.entity.booking;

public class BookingLog {

	private Long scheduled_id;
	public Long getScheduled_id() {
		return scheduled_id;
	}
	public void setScheduled_id(Long scheduled_id) {
		this.scheduled_id = scheduled_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getCreateucid() {
		return createucid;
	}
	public void setCreateucid(Long createucid) {
		this.createucid = createucid;
	}
	public int getCreatetype() {
		return createtype;
	}
	public void setCreatetype(int createtype) {
		this.createtype = createtype;
	}
	private String status;
	private String remark = "";
	private Long createucid;
	private int createtype = 0;
	
}
