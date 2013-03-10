package com.beike.common.enums.trx;

/**
 * @Title: MerSettleStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 对商户的结算状态
 * @date May 14, 2011 9:12:43 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum MerSettleStatus {
	INIT, // 不可结算
	UNSETTLE, // 未结算
	SETTLEED, // 已结算
	NOSETTLE;// 无需结算（不结算）

}
