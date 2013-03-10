package com.beike.entity.booking;

import com.beike.util.DateUtils;

public class BookingInfo {

	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private Long goods_id;
	private Long branch_id;
	private Long guest_id;
	private Long trx_id;
	private String person;
	private String phone;
	private String message;
	private String status;
	private String scheduled_consumption_datetime;
	private Long createucid;
	private String branchname;
	private String goodsname;
	
	private String proposal_consumption_datetime;
	public String getProposal_consumption_datetime() {
		return proposal_consumption_datetime;
	}
	public void setProposal_consumption_datetime(
			String proposal_consumption_datetime) {
		this.proposal_consumption_datetime = proposal_consumption_datetime;
	}
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public Long getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public Long getGuest_id() {
		return guest_id;
	}
	public void setGuest_id(Long guest_id) {
		this.guest_id = guest_id;
	}
	public Long getTrx_id() {
		return trx_id;
	}
	public void setTrx_id(Long trx_id) {
		this.trx_id = trx_id;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getScheduled_consumption_datetime() {
		return scheduled_consumption_datetime;
	}
	public void setScheduled_consumption_datetime(
			String scheduled_consumption_datetime) {
		this.scheduled_consumption_datetime = scheduled_consumption_datetime;
	}
	public Long getCreateucid() {
		return createucid;
	}
	public void setCreateucid(Long createucid) {
		this.createucid = createucid;
	}
	public Long getUpdateucid() {
		return updateucid;
	}
	public void setUpdateucid(Long updateucid) {
		this.updateucid = updateucid;
	}
	public String getUpdatetype() {
		return updatetype;
	}
	public void setUpdatetype(String updatetype) {
		this.updatetype = updatetype;
	}
	private String createtime = DateUtils.getNowTime();
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	private Long updateucid;
	private String updatetype = "0";
	private String updatetime = DateUtils.getNowTime();
}
