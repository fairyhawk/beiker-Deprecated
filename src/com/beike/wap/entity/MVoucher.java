package com.beike.wap.entity;

import java.util.Date;

public class MVoucher {
	private Long voucher_id;// id
	
	private Long guest_id; // 客户id
	
	private Date create_date; // 
	
	private Date active_date; // 凭证激活时间
	
	private Date confirm_date; // 终态时间
	
	private String voucher_code; // 凭证吗
	
	private String voucher_status; // 凭证状态
	
	private String description; // 
	
	private String voucher_verify_source; //

	public Long getVoucher_id() {
		return voucher_id;
	}

	public void setVoucher_id(Long voucher_id) {
		this.voucher_id = voucher_id;
	}

	public Long getGuest_id() {
		return guest_id;
	}

	public void setGuest_id(Long guest_id) {
		this.guest_id = guest_id;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public Date getActive_date() {
		return active_date;
	}

	public void setActive_date(Date active_date) {
		this.active_date = active_date;
	}

	public Date getConfirm_date() {
		return confirm_date;
	}

	public void setConfirm_date(Date confirm_date) {
		this.confirm_date = confirm_date;
	}

	public String getVoucher_code() {
		return voucher_code;
	}

	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}

	public String getVoucher_status() {
		return voucher_status;
	}

	public void setVoucher_status(String voucher_status) {
		this.voucher_status = voucher_status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVoucher_verify_source() {
		return voucher_verify_source;
	}

	public void setVoucher_verify_source(String voucher_verify_source) {
		this.voucher_verify_source = voucher_verify_source;
	}
}
