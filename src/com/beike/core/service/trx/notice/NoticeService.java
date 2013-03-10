package com.beike.core.service.trx.notice;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.exception.StaleObjectStateException;

/**   
 * @title: NoticeService.java
 * @package com.beike.core.service.trx.notice
 * @description: 
 * @author wangweijie  
 * @date 2012-6-27 下午04:03:40
 * @version v1.0   
 */
public interface NoticeService {
	/**
	 *查找主键为id的notice
	 * @param id
	 * @return
	 */
	public Notice preQryInWtDBFindById(Long id);

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
	 * 查询token
	 * @param hostNo
	 * @param noticeType
	 * @param requestId
	 * @return
	 */
	public Notice findTokenByHostNo(String hostNo,String methodType,String requestId);
	
	
	/**
	 * 向beiker_notice表添加一个通知记录，
	 * 系统后台会调用定时任务来发送该请求。（如果要求立即执行，调用sendNotice())
	 * @param hostNo 宿主编号 如：分销商接口编号；集群内部业务编号
	 * @param hostName 分销商名称
	 * @param requestId 请求号
	 * @param contentMap 通知内容
	 * @param methodType 接口类型
	 * @return    
	 * @return Notice
	 * @throws
	 */
	public Notice createNotice(String hostNo,String hostName,String requestId,Map<String,String> contentMap,String methodType);
	
	/**
	 * 向beiker_notice表添加一个通知记录，
	 * 系统后台会调用定时任务来发送该请求。（如果要求立即执行，调用sendNotice())
	 * @param hostNo 宿主编号 如：分销商接口编号；集群内部业务编号
	 * @param hostName 分销商名称
	 * @param requestId 请求号
	 * @param content 通知内容
	 * @param methodType 接口类型
	 * @return    
	 * @return Notice
	 * @throws
	 */
	public Notice createNotice(String hostNo,String noticeType,String requestId,String content,String methodType,NoticeStatus noticeStatus);
	
	/**
	 * 补单失败 发送报警邮件
	 */
	public void sendWarningEmail(String content);
}
