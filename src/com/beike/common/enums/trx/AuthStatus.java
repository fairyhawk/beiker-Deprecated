package com.beike.common.enums.trx;

/**
 * @Title: AuthStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 消费凭证状态
 * @date May 12, 2011 8:40:22 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum AuthStatus {
	INIT, // 初始化
	SUCCESS, // 已下发-准备消费
	DESTROY, // 销毁 --未消费，已退款(不可退款)
	RECOVERY, // 回收--已消费（不可退款）
	TIMEOUT;// 过期--未按时消费

	public boolean isRefund() { // 是否可退款到账户
		switch (this) {
		// case INIT:
		// return true;
		case SUCCESS:
			return true;
		case TIMEOUT:
			return true;
		default:
			return false;
		}

	}

	public boolean isCanUse() {// 可以使用
		switch (this) {
		// case INIT:
		// return true;
		case SUCCESS:
			return true;
		default:
			return false;
		}

	}

}
