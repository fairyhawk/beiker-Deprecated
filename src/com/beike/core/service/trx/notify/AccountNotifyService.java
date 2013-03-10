package com.beike.core.service.trx.notify;

import java.util.List;
import java.util.Map;

import com.beike.entity.notify.AccountNotifyRecord;


import com.beike.common.entity.vm.SubAccount;

/**
 * @Title: AccountNotifyService.java
 * @Package com.beike.core.service.trx.notify
 * @Description: 帐户余额过期提醒服务接口
 * @author wh.cheng@sinobogroup.com
 * @date 13,2, 2012 6:00:18 PM
 * @version V1.0
 */
public interface AccountNotifyService {
	/**
	 * 根据过期时间查询3天和30天即将过期用户
	 */
	public List<Map<String, Object>> qryAllLoseAccount();

	/**
	 * 执行 帐户余额过期提醒插入工作
	 */
	public void processNotifyPrepareDate(AccountNotifyRecord anr);
	
	/**
	 * 根据帐户余额过期表中未发送短信数据进行短信发送操作
	 * @param anr
	 */
	public void processNotifySms(AccountNotifyRecord anr);
	
	/**
	 * 账户余额提醒,查询30天内快到期的余额
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<SubAccount> getRemindAccountBalance(Long userId);

}
