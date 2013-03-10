package com.beike.common.entity.trx.limit;

import java.util.Date;

public class PayLimit {

	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 商品ID
	 */
	private Long goodsId;
	/**
	 * 已购买数量
	 */
	private Long payCount;
	/**
	 * 创建时间
	 */
	private Date createDate = new Date();
	/**
	 * 修改时间
	 */
	private Date modifyDate = new Date();
	/**
	 * 备注
	 */
	private String description = "";
	
	private Long miaoshaId = 0L;

	public Long getMiaoshaId() {
		return miaoshaId;
	}

	public void setMiaoshaId(Long miaoshaId) {
		this.miaoshaId = miaoshaId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Long getPayCount() {
		return payCount;
	}

	public void setPayCount(Long payCount) {
		this.payCount = payCount;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
