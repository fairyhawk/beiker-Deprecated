package com.beike.dao.notify;

import java.util.List;

import com.beike.common.enums.trx.NotifyType;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;
import com.beike.entity.notify.AccountNotifyRecord;

/**
 * @author yurenli 2012-2-10 13:50:57
 */
public interface AccountNotifyRecordDao extends
		GenericDao<AccountNotifyRecord, Long> {

	/**
	 * @return 添加账户过期通知记录相关数据
	 */
	public Long addAccountNotifyRecord(AccountNotifyRecord anr);

	/**
	 * @param anr
	 * @throws StaleObjectStateException
	 *             更新账户过期通知记录相关数据
	 */
	public void updateAccountNotifyRecord(AccountNotifyRecord anr)
			throws StaleObjectStateException;

	/**
	 * @param id
	 * @return 根据主键查询账户过期通知记录
	 */
	public AccountNotifyRecord findById(Long id);

	/**
	 * @param accountId
	 * @param subAccountId
	 * @param isNotify
	 * @param type
	 * @return 根据账户ID，子账户ID，时间点类型等查询通知记录
	 */
	public AccountNotifyRecord findByS(Long accountId, Long subAccountId,
			NotifyType type);

	/**
	 *根据条件是否通知查询出通知记录
	 * 
	 * @return
	 */
	public List<AccountNotifyRecord> findByIsNotify(boolean isNotify,int start,int length);

	/**
	 * 根据userId查询出通知记录
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	public List<AccountNotifyRecord> findByUserId(Long userId, NotifyType type);
	
	/**
	 * 根据主键更新通知记录
	 * @param idStr
	 */
	public void updateAccountNotifyById(String idStr);
	
	/**
	 * 查询出账户过期的条数
	 * @param isNotify
	 * @return
	 */
	public int findCountByIsNotify(boolean isNotify);
}
