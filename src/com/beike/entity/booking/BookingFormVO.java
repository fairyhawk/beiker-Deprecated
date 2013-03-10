package com.beike.entity.booking;


public class BookingFormVO {

	private Long goods_id;
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
	private Long branch_id;
	private String person;
	private String phone;
	private String message;
	private String scheduled_consumption_datetime;
	private Long createucid;
	private Long trxorder_id;
	private int tobook = 1;
	private Long bookedid;
	public Long getBookedid() {
		return bookedid;
	}
	public void setBookedid(Long bookedid) {
		this.bookedid = bookedid;
	}
	public int getTobook() {
		return tobook;
	}
	public void setTobook(int tobook) {
		this.tobook = tobook;
	}
	public Long getTrxorder_id() {
		return trxorder_id;
	}
	public void setTrxorder_id(Long trxorder_id) {
		this.trxorder_id = trxorder_id;
	}
}
