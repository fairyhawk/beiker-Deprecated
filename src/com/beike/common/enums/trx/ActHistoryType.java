package com.beike.common.enums.trx;

/**
 * @Title: ActHistoryType.java
 * @Package com.beike.common.enums.trx
 * @Description: 帐务相关历史类型
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 11:04:44 AM
 * @version V1.0
 */
public enum ActHistoryType {

	/**
	 * 与帐务相关历史类型，不关注外在产品业务的类型
	 */
	SALES, // 消费
	ADJUST, // 调帐
	REFUND, // 退款
	REFUNDTOBANK, // 退款
	LOAD, // 充值
	RABATE, // 返现(12.11之前和之后的平台购买返现,类型仍为RABATE)
	INSIDEREBATE, // 内部员工激励（12.11之前的内部员工激励，之后弃用。类型统一为VMDIS）
	VMDIS, // 虚拟币下发
	CANCEL, // 余额过期取消（暂时针对虚拟币帐户）
	NOT_CHANGE,//余额不找零取消（暂时针对虚拟币帐户）
	COUPON_INVALID;//优惠券作废(优惠劵退款后扣款时用)
	public boolean isSales() {
		switch (this) {
		case SALES:
			return true;
		default:
			return false;
		}

	}

	public boolean isAdjust() {
		switch (this) {
		case ADJUST:
			return true;
		default:
			return false;
		}

	}

	public boolean isRefund() {

		switch (this) {

		case REFUND:
			return true;
		case REFUNDTOBANK:
			return true;
		default:
			return false;

		}
	}

	public boolean isLoad() {

		switch (this) {
		case LOAD:
			return true;
		default:
			return false;
		}
	}

	public boolean isRabate() {

		switch (this) {
		case RABATE:
			return true;
		default:
			return false;

		}

	}

}
