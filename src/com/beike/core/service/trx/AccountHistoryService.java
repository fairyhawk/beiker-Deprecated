package com.beike.core.service.trx;

import java.util.List;

import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.PaymentType;

/**
 * @Title: AccountHistoryService.java
 * @Package com.beike.core.service.trx
 * @Description: 账户
 * @author wangkun
 * @version V1.0
 */
public interface AccountHistoryService {

	public Payment findPaymentByUserIdAndType(Long trxId,
			PaymentType paymentType);

	public List<TrxorderGoods> findTxGoodsByTrxOrderId(Long trxId);

	public List<TrxorderGoods> findRabateByTrxId(Long trxId);

	/**
	 * 我的钱包使用，获取历史交易信息
	 */
	public List<AccountHistory> getHistoryInfoByUserId(Long userId);

	/**
	 * 查询退款信息
	 */
	public List<RefundRecord> getRefundDetailByTreOrderId(Long trxOrderId);

	public List<TrxorderGoods> findGoodsById(long id);

	/**
	 * 根据用户actId查询出账户历史表相关虚拟款项返现和活动信息
	 * 
	 * @param userId
	 * @return
	 */
	public List<AccountHistory> listAccountHistory(Long actId);
}
