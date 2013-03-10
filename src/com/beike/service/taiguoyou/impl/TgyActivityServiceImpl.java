package com.beike.service.taiguoyou.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.taiguoyou.TgyActivityDao;
import com.beike.service.taiguoyou.TgyActivityService;
 /*
 * com.beike.service.taiguoyou.impl.TgyActivityServiceImpl.java
 * @description:泰国游抽奖活动service实现
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-7-3，xuxiaoxian ,create class
 *
 */
@Service("tgyActivityService")
public class TgyActivityServiceImpl implements TgyActivityService{
	
	@Autowired
	private TgyActivityDao tgyActivityDao;
	/** 
	 * @date 2012-7-3
	 * @description:添加抽奖信息
	 * @param userid用户Id
	 * @return boolean
	 */
	public boolean addActivityMsg(Long userid){
		Map<String,Object> activityMsg = tgyActivityDao.getActivityMsgByUserId(userid);
		if(activityMsg == null){
			return tgyActivityDao.addActivityMsg(userid);
		}
		return false;
	}
	
	/** 
	 * @date 2012-7-3
	 * @description:通过用户id查询用户抽奖信息
	 * @param userid
	 * @return Map<String,Object>
	 */
	public Map<String,Object> getActivityMsgByUserId(Long userid){
		return tgyActivityDao.getActivityMsgByUserId(userid);
	}
}
