package com.beike.core.service.trx.notify;


/**
 * 订单过期提醒服务接口
 * 
 * @author jianjun.huo
 * 
 */
public interface TrxorderNotifyRecordService
{

	/**
	 * 根据过期时间处理10天和3天即将过期订单（入口无事务）
	 */
	public void noTscProcessAllLoseTrxOrder();
	


}
