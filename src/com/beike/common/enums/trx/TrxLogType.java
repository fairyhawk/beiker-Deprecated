package com.beike.common.enums.trx;    
/**   
 * @Title: TrxLogStype.java
 * @Package com.beike.common.enums.trx
 * @Description: TODO
 * @date Jun 30, 2011 5:02:20 PM
 * @author wh.cheng
 * @version v1.0   
 */
public enum TrxLogType {
	TRXORDERGOODS,  //商品订单操作日志
	REFUND, //退款历史
	
	//trxLog需求修改增加新类型2012年3月30日20:39:19
	/**
	 * 订单创建
	 */
	INIT, 
	 
	/**
	 * 成功
	 */
	SUCCESS,
	 
	/**
	 * 使用
	 */
	USED, 
	 
	/**
	 * 评价
	 */
	COMMENTED,
	 
	/**
	 * 过期
	 */
	EXPIRED,
	
	/**
	 * 账户退款申请
	 */
	REFUNDACCEPT,
	  
	/**
	 * 账户退款：审批拒绝 
	 */
	REFUNDREFUSE,  
	 
	/**
	 * 退款到账户
	 */
	REFUNDTOACT, 
	 
	/**
	 * 原路返回审核
	 */
	RECHECK, 
	 
	/**
	 * 退款到银行卡：复核拒绝
	 */
	RECHECK_REFUSE,
	 
	/**
	 * 退款到银行卡
	 */
	REFUNDTOBANK,
	 
	/**
	 * 退款到银行卡
	 */
	REFUNDTOBANK_FAILED,
	
	/**
	 * 凭证发送
	 */
	VOUCHER_SEND,
	
	/**
	 * 返现
	 */
	REBATE;
}
 