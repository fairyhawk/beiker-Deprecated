package com.beike.service.taiguoyou;

import java.util.Map;
 

public interface TgyActivityService {
	/** 
	 * @date 2012-7-3
	 * @description:添加抽奖信息
	 * @param userid用户Id
	 * @return boolean
	 */
	public boolean addActivityMsg(Long userid);
	
	/** 
	 * @date 2012-7-3
	 * @description:通过用户id查询用户抽奖信息
	 * @param userid
	 * @return Map<String,Object>
	 */
	public Map<String,Object> getActivityMsgByUserId(Long userid);
}
