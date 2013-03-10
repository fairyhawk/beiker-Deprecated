package com.beike.dao.miaosha;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.miaosha.MiaoshaRemind;

/**      
 * project:beiker  
 * Title:秒杀提醒DAO
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:38:42 PM     
 * @version 1.0
 */
public interface MiaoshaRemindDao extends GenericDao<MiaoshaRemind, Long>{
	
	/**
	 * 新增秒杀通知
	 * @return
	 */
	public int addMiaoshaRemind(MiaoshaRemind msRemind);
	
	/**
	 * 检查同一秒杀、同一手机号的秒杀通知是否已存在
	 * @param miaoshaid
	 * @param phone
	 * @return
	 */
	public int checkRepeatRemind(Long miaoshaid, String phone);
	
	/**
	 * 查询指定秒杀用户是否已经预定通知
	 * @param userId
	 * @param miaoshaId
	 * @return
	 */
	public MiaoshaRemind getMiaoshaRemind(Long userId, Long miaoshaId);
	
	/**
	 * 通过秒杀ID查询预订通知的手机号
	 * @param miaoshaId
	 * @return
	 */
	public List<String> getRemindPhoneByMiaoshId(Long miaoshaId);
	
	/**
	 * 通过秒杀ID删除已发送短信的预订通知信息
	 * @param miaoshaId
	 * @return
	 */
	public int deleteMiaoshaRemindByMiaoshId(Long miaoshaId);
}
