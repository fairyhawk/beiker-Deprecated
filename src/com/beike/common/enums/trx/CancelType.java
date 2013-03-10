package com.beike.common.enums.trx;

/**
 * 子账户取消类型
 * @author yurenli
 *
 */
public enum CancelType {
	CANCEL, // 余额过期取消（暂时针对虚拟币帐户）
	NOT_CHANGE,//余额不找零取消（暂时针对虚拟币帐户）
	COUPON_INVALID;//优惠券作废（不退款，优惠劵面值50.实际使用30,退款时只退30，入账后接着出账)
}
