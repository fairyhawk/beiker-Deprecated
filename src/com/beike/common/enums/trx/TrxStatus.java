package com.beike.common.enums.trx;

/**
 * @Title: TrxStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 平台内的交易状态
 * @author wh.cheng@sinobogroup.com
 * @date May 9, 2011 12:03:42 AM
 * @version V1.0
 */
public enum TrxStatus {
	INIT, // 初始化
	SUCCESS, // 成功
	CANCEL, // 取消

	// PARTREFUND, //部分退款.预留个交易订单使用，暂时闲置

	USED, COMMENTED,

	/************* 提供给TRX-ORDER-GOODS用用户后台查询 *******************/
	/**
	 * 退款已受理:运营处理中
	 */
	REFUNDACCEPT,

	REFUNDTOACT, // 退款到账户
	/**
	 * 复核：财务处理中
	 */
	RECHECK,

	/**
	 * 退款到银行卡
	 */
	REFUNDTOBANK,

	/**
	 * 过期（前置条件：商品订单过期时间满足+不能退款）
	 */
	EXPIRED;

	/**
	 * 是否可以提交退款到账户申请（支付成功或过期状态即可）
	 */
	public boolean isAplyRefundToAct() {

		switch (this) {
		case SUCCESS:
			return true;
		case EXPIRED:
			return true;
		default:
			return false;

		}

	}

}
