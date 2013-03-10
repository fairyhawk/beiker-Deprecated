package com.beike.core.service.trx.notify;

import com.beike.entity.notify.TrxorderNotifyRecord;

/**
 * 订单过期提醒服务接口
 * 
 * @author jianjun.huo
 * 
 */
public interface TrxorderNotifyProcessService
{

	/**
	 *     插入过期短信提醒记录
	 * @param trxorderNotifyRecord
	 */
	 void  processTrxorderNotify  (TrxorderNotifyRecord trxorderNotifyRecord) ;

}
