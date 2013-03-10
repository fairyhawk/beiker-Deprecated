package com.beike.common.enums.trx;

/**
 * @Title: VoucherStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 凭证状态
 * @date May 26, 2011 5:02:56 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum VoucherStatus {

	INIT, // 初始化
	ACTIVE, // 激活
	USED, // 已使用
	DESTORY; // 销毁

	public boolean isDestory() { // 是否可销毁
		switch (this) {
		case ACTIVE:
			return true;
		case INIT:
			return true;

		case DESTORY: // 之前是销毁状态还是可继续销毁(供已过期的单子继续可退款)
			return true;
		default:
			return false;
		}

	}

}
