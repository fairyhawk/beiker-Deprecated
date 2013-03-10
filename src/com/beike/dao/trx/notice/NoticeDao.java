package com.beike.dao.trx.notice;

import java.util.List;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**   
 * 
 * @title: NoticeDao.java
 * @package com.beike.dao.trx.notice
 * @description: 接口补单（回调）Dao
 * @author wangweijie  
 * @date 2012-6-13 上午10:17:34
 * @version v1.0   
 */
public interface NoticeDao extends GenericDao<Notice, Long> {
	
	/**
	 * 查找主键为id的notice
	 * @param id 主键
	 * @return    
	 * @return Notice
	 * @throws
	 */
	public Notice findById(Long id);
	
	/**
	 * 添加Notice
	 * @param notice    
	 * @return Long
	 * @throws
	 */
	public Long addNotice(Notice notice);
	
	/**
	 * 本接口只更新count、status、resMsg、modify_date字段
	 * @param count
	 * @param status
	 * @param resMsg 
	 * @return void
	 * @throws
	 */
	public void updateNotice(Notice notice)throws StaleObjectStateException;
	
	/**
	 * 根据主键更新状态
	 * @param status    
	 * @param id
	 * @param version
	 * @return void
	 * @throws
	 */
	public void updateNoticeStatusById(NoticeStatus status,Long id,Long version)throws StaleObjectStateException;
	
	
	/**
	 * 根据状态为status类型的所有数据
	 * @param status    
	 * @return void
	 * @throws
	 */
	public List<Notice> findNoticeListByStatus(NoticeStatus status);
	
	/**
	 * 获取token值
	 * @param hostNo
	 * @param noticeType
	 * @param requestId
	 * @return
	 */
	public Notice findTokenByHostNoNoticeTypeRequestId(String hostNo,String noticeType,String requestId);
	
	
}
