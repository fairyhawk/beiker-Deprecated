package com.beike.core.service.trx.notice;

import com.beike.common.enums.trx.NoticeStatus;

/**   
 * @title: PartnerNoticeService.java
 * @package com.beike.core.service.trx.notice
 * @description: 分销商补单通知接口
 * @author wangweijie  
 * @date 2012-12-4 下午05:15:03
 * @version v1.0   
 */
public interface PartnerNoticeService {
	
	/**
	 * 报文发送
	 * @return
	 */
	public String send(String content) throws Exception;
	
	/**
	 * 判断是否成功
	 * @param resMsg
	 * @param methodType
	 * @return -1:失败；0：成功；1：未成功，需要重发
	 */
	public int isSuccess(String resMsg,String methodType);
	
	/**
	 * 是否结束通知
	 * NoticeStatus.PROCESSING 表示发送异常
	 * NoticeStatus.FAIL 表示对方返回失败
	 * NoticeStatus.SUCCESS 表示对方返回成功
	 * @param noticeStatus
	 * @param times
	 * @return
	 */
	public boolean isEnd(NoticeStatus noticeStatus,int times);
	
	/**
	 * 获得重发延迟时间，times=0时表示第一次发送延迟时间
	 * @param times
	 * @return
	 */
	public long getDelaySecond(int times); 
	
	/**
	 * 获得重发最大次数
	 * @param times
	 * @return
	 */
	public int getMaxReSendNum(); 
	
	/**
	 * 失败情况下，是否支持 24小时 随机3次发送
	 * @return
	 */
	public boolean needRandomSend();
}
