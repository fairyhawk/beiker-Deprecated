package com.beike.dao.notify;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.notify.TrxorderNotifyRecord;

/**
 * 短信提醒通知记录接口
 * @author yurenli
 *
 */
public interface TrxorderNotifyRecordDao extends
GenericDao<TrxorderNotifyRecord, Long>{
		


	/**
	 * @return 添加账户过期通知记录相关数据
	 */
	public Long addTrxorderNotifyRecord(TrxorderNotifyRecord tnr);


	/**
	 * @param id
	 * @return 根据主键查询账户过期通知记录
	 */
	public TrxorderNotifyRecord findById(Long id);


	/**
	 *根据条件是否通知查询出通知记录
	 * 
	 * @return
	 */
	public List<TrxorderNotifyRecord> findByIsNotify(boolean isNotify,int start,int daemonLength);

	/**
	 * 根据主键更新通知记录
	 * @param idStr
	 */
	public void updateAccountNotifyById(Long id,boolean result);

	/**
	 * 查询出未通知记录条数
	 * @param isNotify
	 * @return
	 */
	public int findByIsNotifyCount(boolean isNotify) ;
}
