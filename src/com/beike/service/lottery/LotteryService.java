/**
 * 
 */
package com.beike.service.lottery;

import java.util.List;
import java.util.Map;

import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.lottery.PrizeInfo;

/**
 * @author janwen
 * 
 */
public interface LotteryService 
{
	public LotteryInfo getLotteryInfo(String prizeid);

	public LotteryInfo saveLotteryInfo(String prizeid, String userid);

	public LotteryInfo isJoined(String prizeid, String userid);
	public List<String> getLotteryWinnersNo(String prizeid);
	public PrizeInfo getPrizeInfo(String prizeid);

	/**
	 * 补充方法：当单个用户参与抽奖时, 调用该接口向参与用户发送邮件（奖号）
	 * Add by zx.liu
	 * @param prizeId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public void participantEmail(Long prizeId, Long userId) throws Exception;
	
	/**
	 * 
	 * @param prizeId
	 * @return 推荐商品信息
	 * @throws Exception
	 */
	public List<Map<String, Object>> getRecommendGoods(Long prizeId)throws Exception;
}
