package com.beike.common.enums.trx;

/**
 * 订单短信提醒业务类型
 * @author yurenli
 *
 */
public enum TrxBizType {

	/**
	 * 验证短信提醒
	 */
	INSPECT,
	/**
	 * 退款到账户短信提醒
	 */
	RETURNACT,
	/**
	 * 退款到银行卡短信提醒
	 */
	RETURNBANK,
	/**
	 * 过期短信提醒
	 */
	OVERDUE;
	
	
	
}
