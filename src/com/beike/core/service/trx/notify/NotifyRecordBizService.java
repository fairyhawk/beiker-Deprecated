package com.beike.core.service.trx.notify;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.TrxBizType;
import com.beike.entity.notify.TrxorderNotifyRecord;

/**
 * 订单提醒短信业务接口
 * @author yurenli
 *
 */
public interface NotifyRecordBizService {

	/**
	 * 订单被使用短信通知业务实现接口
	 * @param tog
	 */
	public void processNotifyByBizType(TrxorderGoods tog,TrxBizType bizType,boolean isSendSmsNotify);
	
	/**
	 * 订单提醒通知表中数据使用短信通知业务实现接口
	 * @param tnr
	 */
	public void processNotifySms(TrxorderNotifyRecord tnr);
}
