package com.beike.core.service.trx;

import java.util.List;

import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxorderGoods;

/**
 * @Title: TrxLogService.java
 * @Package com.beike.core.service.trx
 * @Description: 日志服务类
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 3:25:41 PM
 * @version V1.0
 */
public interface TrxLogService {
	/**
	 * 根据传入的PaymentList和trxorderGoodsList记录运营支付成功操作日志
	 * 
	 * @param paymentList
	 * @param trxorderGoodsList
	 */
	public void addTrxLogForSuc(List<Payment> paymentList,
			List<TrxorderGoods> trxorderGoodsList);
	
	
	/**
	 * 根据传入的trxorderGoodsList记录运营下单操作日志
	 * 
	 * @param trxorderGoodsList
	 */
	public void addTrxLogForCreate(List<TrxorderGoods> trxorderGoodsList);
	
	
	
	
}
