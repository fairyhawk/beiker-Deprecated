package com.beike.common.enums.trx;

/**
 * @Title: PaymentTyep.java
 * @Package com.beike.common.enums.trx
 * @Description: 支付信息类型。区别入账来源和类型
 * @date May 16, 2011 4:25:36 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum PaymentType {
	ACTCASH, // 即现金.账户里有的现金
	ACTVC, // 虚拟币
	PAYCASH, // 需要支付的现金
	// 鉴于初期充值量极少，不单独做充值记录
	LOAD
	// 充值。1.常规充值。( 暂未实施)

}
