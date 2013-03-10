package com.beike.common.enums.trx;

/**
 * @Title: RefundSourceType.java
 * @Package com.beike.common.enums.trx
 * @Description:退款来源
 * @date Jul 4, 2011 2:01:12 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum RefundSourceType {

	TIMING, // 定时任务

	OPERATIONAL, // 运营发起

	// CHECKING, //对账不平发起

	USER, // 用户发起

	OVERRUN,// 超限发起(总量+个人)
    
	PARTNER,//分销商发起
	
	FILM;//网票网完成下单失败发起
}
