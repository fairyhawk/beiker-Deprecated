package com.beike.dao.taiguoyou.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.taiguoyou.TgyActivityDao;
 /*
 * com.beike.dao.taiguoyou.impl.TgyActivityDaoImpl.java
 * @description:泰国游抽奖活动Dao实现
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-7-3，xuxiaoxian ,create class
 *
 */
@SuppressWarnings("unchecked")
@Repository("tgyActivityDao")
public class TgyActivityDaoImpl extends GenericDaoImpl implements TgyActivityDao{
	/** 
	 * @date 2012-7-3
	 * @description:添加抽奖信息
	 * @param userid用户Id
	 * @return boolean
	 * @throws 
	 */
	public boolean addActivityMsg(Long userid){
		String sql = "INSERT INTO beiker_taiguo_activity(userid,jointime) VALUES(?, NOW());";
		int flag = this.getJdbcTemplate().update(sql, new Object[]{userid});
		return flag > 0 ? true : false;
	}
	
	/** 
	 * @date 2012-7-3
	 * @description:通过用户id查询用户抽奖信息
	 * @param userid
	 * @return Map<String,Object>
	 * @throws 
	 */
	public Map<String,Object> getActivityMsgByUserId(Long userid){
		String sql = "SELECT userid,jointime FROM beiker_taiguo_activity WHERE userid = ?;";
		List<Map<String,Object>> activityMsg = this.getJdbcTemplate().queryForList(sql, new Object[]{userid});
		if(activityMsg != null && activityMsg.size() >0){
			return activityMsg.get(0);
		}
		return null;
	}
}
