package com.beike.wap.entity;

public class MMerchantEvaluation {
	private Long id; // id
	
	private Double evaluationscore; // 分数
	
	private Long merchantid; // 商户ID
	
	private String evaluationcontent; // 评价内容
	
	private Long user_id; // 用户ID
	
	private Long goods_id; // 商品id
	
	private Long trx_goods_id; // beiker_trxorder_goods id

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getEvaluationscore() {
		return evaluationscore;
	}

	public void setEvaluationscore(Double evaluationscore) {
		this.evaluationscore = evaluationscore;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public String getEvaluationcontent() {
		return evaluationcontent;
	}

	public void setEvaluationcontent(String evaluationcontent) {
		this.evaluationcontent = evaluationcontent;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public Long getTrx_goods_id() {
		return trx_goods_id;
	}

	public void setTrx_goods_id(Long trx_goods_id) {
		this.trx_goods_id = trx_goods_id;
	}
}
