package com.beike.common.enums.trx;

/**
 * @Title: RefundStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 退款状态
 * @date May 23, 2011 2:57:39 PM
 * @author wh.cheng
 * @version v1.0
 */
public enum RefundStatus {
	/**
	 * 初始状态
	 */
	INIT,
	
	REFUNDTOACT,//对应trxStatus中的ALLREFUND
	/*************与TRX-ORDER-GOODS中的TrxStatus。退款记录中的状态应该细化，但为满足运营后台互斥状态、一条sql需求。此处状态分类纠结啊*******************/
	/**
	 * 退款已受理:运营处理中
	 */
	REFUNDACCEPT,
	
	
	/**
	 * 复核：财务处理中
	 */
	RECHECK,
	
	/**
	 * 退款处理中：提交到银行，等待银行处理结果
	 * 如果处于该状态表名退款出现异常，需要处理。 原因1、更新数据出现异常。2、银行响应异常
	 */
	REFUNDINHANDLE,
	
	/**
	 * 退款到银行卡
	 */
	REFUNDTOBANK,
	
	/*******************************************************/
	
	/**
	 * 退款失败。针对第三方机构。平台不可抗拒的原因
	 */
	FAILED,
	
	/**
	 * 系统异常
	 */
	ERROR
	
	
	
	
	/**
	 * 退款已受理
	 *//*
	REFUNDACCEPT,
	*//**
	 * 退款处理完成
	 *//*
	DONE,
	*//**
	 * 退款失败
	 *//*
	FAILED,
	*//**
	 * 待复核
	 *//*
	RECHECK,
	/**
	 * 被拒绝
	 
	REFUSED,
	*//**
	 * 异常
	 *//*
	ERROR,
	*//**
	 * 复核通过
	 *//*
	RECHECKPASS
	*/
	

	
	

}
