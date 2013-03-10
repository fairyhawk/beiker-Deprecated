package com.beike.dao.taiguoyou;

import java.util.Map;



public interface TgyActivityDao {
	
	/** 
	 * @date 2012-7-3
	 * @description:添加抽奖信息
	 * @param userid用户Id
	 * @return boolean
	 * @throws 
	 */
	public boolean addActivityMsg(Long userid);
	
	/** 
	 * @date 2012-7-3
	 * @description:通过用户id查询用户抽奖信息
	 * @param userid
	 * @return Map<String,Object>
	 * @throws 
	 */
	public Map<String,Object> getActivityMsgByUserId(Long userid);
}
