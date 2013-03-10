package com.beike.wap.entity;

import java.util.Date;

/**
 * beiker_trxorder
 * @author zh-wk
 *
 */
public class MTrxorder {
	/** 主键 */
	private Long id;
	/** 用户id */
	private Long user_id;
	/** 创建日期 */
	private Date create_date;
	/** 关闭日期前 */
	private Date close_date;
	/** 订单金额 */
	private double ord_amount;
	/** 交易请求号(对用户) */
	private String request_id;
	/** 交易流水号(对平台) */
	private String external_id;
	/** 交易状态 */
	private String trx_status;
	/** 订单类型 */
	private String order_type;
	/**  */
	private Long extend_info;
	/**  描述 */
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getClose_date() {
		return close_date;
	}
	public void setClose_date(Date close_date) {
		this.close_date = close_date;
	}
	public double getOrd_amount() {
		return ord_amount;
	}
	public void setOrd_amount(double ord_amount) {
		this.ord_amount = ord_amount;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
	public String getTrx_status() {
		return trx_status;
	}
	public void setTrx_status(String trx_status) {
		this.trx_status = trx_status;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public Long getExtend_info() {
		return extend_info;
	}
	public void setExtend_info(Long extend_info) {
		this.extend_info = extend_info;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
