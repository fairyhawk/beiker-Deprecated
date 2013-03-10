package com.beike.dao.businessbackground;

import java.util.List;
import java.util.Map;

public interface VipStatisticsDao {

	/**
	 * 查询当天交易信息
	 * @param dateStart 开始日期 
	 * @param dateEnd 结束日期
	 * @return
	 */
	public List<Map<String,Object>> queryTrxOrderInfo(String dateStart,String dateEnd);
	
	/**
	 * 查询所有线上会员信息
	 * @param guestId 商家id
	 * @return
	 */
	public int queryVipStatistics(Long guestId);
	
	/**
	 * 添加新增会员
	 * @param lstVipInfo 会员信息
	 * @return
	 */
	public void addVipStatitics(Map<String,Object> vipInfo);
	
	/**
	 * 查找是否该商家存在该会员
	 * @param userId 会员id
	 * @param guestId 商家id
	 * @return
	 */
	public int queryVipOfGuest(Long userId,Long guestId);
	
	/**
	 * 查询线上会员总数
	 * @return
	 */
	public int queryOnlineVipCount();
	
	/**
	 * 查询线下会员总数
	 * @return
	 */
	public int queryOfflineVipCount();
	
	/**
	 * 按月份查找当月会员数
	 * @param date 日期
	 * @return
	 */
	public Map<String,Object> queryVipInfoByMonth(String date);
	
	/**
	 * 按月份插入会员统计信息
	 * @param date  日期月份
	 * @param vipNum 会员数
	 * @param guest_id 商家
	 * @return
	 */
	public void insertVipInfoByMonth(String date,Long vipNum,Long guest_id);
	
	/**
	 * 按月份更新会员统计信息
	 * @param date  日期月份
	 * @param vipNum 会员数
	 * @param guest_id 商家
	 * @return
	 */ 
	public void updateVipInfoByMonth(String date,Long vipNum,Long guest_id);
	
	/**
	 * 按日期查询会员数
	 * @param date 日期
	 * @param guestId 商家id
	 * @return
	 */ 
	public int queryVipNumByMonth(String date,Long guestId);
	
	/**
	 * 按日期查找商家信息
	 * @param date 日期
	 * @return
	 */ 
	public List<Map<String,Object>> queryGuestInfoByDate(String date);
	
	/**
	 * 按日期、商家id、用户id查询购买行为次数
	 * @param guestId 商家id
	 * @param date 日期
	 * @param userIds 会员id
	 * @return
	 */ 
	public int queryBuyActivityByUserIds(Long guestId,String date,String userIds);
	
	/**
	 * 按日期、商家id查询用户信息
	 * @param guestId 商家id
	 * @param date 日期
	 * @return
	 */ 
	public List<Map<String,Object>> queryVipInfoByDate(Long guestId,String date);
	
	/**
	 * 查询所选日期之前每月vip新增数量
	 * @param date 日期
	 * @param guestId 商家id
	 * @return
	 */ 
	public List<Map<String,Object>> queryVipNumForMonthByDate(String endDate,Long guestId); 
	
	/**
	 * 查找本月内所有商家新增的会员数
	 * @param month 本月
	 * @param nextMonth 下月
	 * @return
	 */
	public List<Map<String,Object>> queryVipStatisticsByMonth(String month,String nextMonth);
	
	/**
	 * 团购明细查询
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryTuanGouDetail(Map<String,Object> params);
	
	/**
	 * 团购/网上商店完成数量
	 * @param params
	 * @return
	 */
	public int queryTotalCount(Map<String,Object> params);
	
	/**
	 * 带来新会员数
	 * @param params,其中params中isMenu为0对应为团购;为1对应网上商店
	 * @return
	 */
	public int queryNewVipCount(Map<String,Object> params);
	
	/**
	 * 老会员数
	 * @param params,其中params中isMenu为0对应为团购;为1对应网上商店
	 * @return
	 */
	public int queryOldVipCount(Map<String,Object> params);
}
