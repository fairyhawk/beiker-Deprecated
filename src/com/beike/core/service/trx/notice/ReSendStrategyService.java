package com.beike.core.service.trx.notice;

/**   
 * @title: ReSendStrategyService.java
 * @package com.beike.core.service.trx.notice
 * @description: 重发通知间隔策略，指定用户用哪一种通知方式
 * @author wangweijie  
 * @date 2012-6-13 下午06:18:38
 * @version v1.0   
 */
public interface ReSendStrategyService {

	/**
	 * 执行下一个数，如果到达末尾则返回false
	 * @return    
	 * @return boolean
	 * @throws
	 */
	public boolean isEnd(String hostNo,int index);
	
	/**
	 * 获得本次通知间隔时间，并设置下一次的间隔时间
	 * @return long
	 * @throws
	 */
	public long get(String hostNo,int index);
}
