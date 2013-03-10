package com.beike.dao.lottery;

import java.util.List;
import java.util.Map;

import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.lottery.PrizeInfo;

/**
 * @author jianwen
 */
public interface LotteryDao 
{

public LotteryInfo getLotteryInfo(String prizeid);
	
	public LotteryInfo saveLotteryInfo(String prizeid,String userid);
	public void updatePrizeInfo(PrizeInfo prizeInfo);
	
	
	public String getLotteryNumber(String userid,String prizeid);
	
	public LotteryInfo isJoined(String prizeid,String userid);
	
	public PrizeInfo getPrizeInfo(String prizeid);
	public boolean isPrizeGoods(String goodsid);

	
	/** 
	 * 查询抽奖活动的信息
	 * 信息来源：前台数据库下的 beiker_prize 表
	 *  
	 * Add by  zx.liu
	 */
	public Object findPrizeGoodsById(Long prizeid) throws Exception;

		
	/**
	 * 根据 抽奖活动的ID 和 用户ID 来获取参与抽奖用户的 奖号 和 Email
	 * 信息主要来源：前台数据库下的 beiker_lottery 表
	 * 
	 * Add by zx.liu
	 */
	public Map<String, Object> findLotteryUserById(Long prizeId, Long userId);	
	

	/**
	 * 根据商品的ID来获取商品的信息
	 * 
	 * Add by zx.liu
	 */	
	public Map<String, Object> findGoodsInfo(Long goodsId);	
	
	public List<String> getLotteryWinnersNo(String prizeid);
	
	
	public List<LotteryInfo> getLotteryInfoList(String prizeIds);
	
}