package com.beike.common.enums.trx;

public enum TrxStatusInMC{

	/**
	 * 全部状态
	 */
	ALL,
	/**
	 * 使用（平台码）
	 */
	USEDINPFVOU,
	/**
	 * 使用（平台码）
	 */
	USEDINMERVOU,
	/**
	 * 评价(平台码)
	 */
	COMMENTEDINPFVOU,
	/**
	 * 评价（商家码）
	 */
	COMMENTEDINMERVOU,
	
	/**
	 *  初始化
	 */
	INIT,
	/**
	 * 成功
	 */
	SUCCESS,
	/**
	 * 运营处理中
	 */
	REFUNDACCEPT,
	/**
	 * 退款到账	
	 */
	REFUNDTOACT,  
	/**
	 *  财务处理中
	 */
	RECHECK, 
	/**
	 * 退款到银行卡
	 */
	REFUNDTOBANK, 
	/**
	 * 过期
	 */
	EXPIRED;
}
