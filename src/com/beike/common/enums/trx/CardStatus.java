package com.beike.common.enums.trx;

/**
 * @Title: CardStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 千品卡状态枚举类
 * @date May 9, 2011 6:13:14 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum CardStatus {

	INIT, // 初始化
	INSTORE, // 入库
	ISSUED, // 发放
	ACTIVE, // 激活
	USED, // 已使用
	DESTORY, // 废弃
	TIMEOUT
	// 过期

}
