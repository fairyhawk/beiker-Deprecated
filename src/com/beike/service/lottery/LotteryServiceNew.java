package com.beike.service.lottery;

import java.sql.Timestamp;
import java.util.List;

import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.lottery.LotteryTicket;
import com.beike.entity.lottery.PrizeInfoNew;

public interface LotteryServiceNew {

	
	/**
	 * 
	 * @author janwen
	 * @time Dec 19, 2011 2:08:47 PM
	 *
	 * @param newprize_id
	 * @return 奖品信息
	 */
	public LotteryInfoNew getLotteryInfoNew(String newprize_id);
	
	
	
	/**
	 * 如果参与返回抽奖活动newprize_id
	 * @author janwen
	 * @time Dec 19, 2011 1:44:58 PM
	 *
	 * @param newprize_id
	 * @param user_id
	 * @return
	 */
	public Long isJoined(String newprize_id, String user_id);
	
	
	/**
	 * 推荐商品：当前城市热卖top8
	 * @author janwen
	 * @time Dec 19, 2011 2:23:20 PM
	 *
	 * @return top8商品goodsid
	 */
	public List<Long> getRecommendGoodsID(String area_id);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 19, 2011 4:48:08 PM
	 *
	 * @param newprize_id
	 * @param user_id
	 * @return 用户奖券信息
	 */
	public List<LotteryTicket> getLotteryTicketInfo(String newprize_id, String user_id);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 19, 2011 5:06:44 PM
	 *
	 * @param prize_id
	 * @return 开奖信息列表
	 */
	public List<PrizeInfoNew> getPrizeInfoNew(String prize_id);
	
	/**
	 * 保存用户奖券信息，并更新参与人数
	 * @author janwen
	 * @time Dec 19, 2011 6:46:32 PM
	 *
	 * @param newprize_id
	 * @param numbersource 中奖来源信息
	 * @param user_id
	 * @param startprize_id TODO
	 * @param getlorrystatus获得奖品来源状态:1.24小时购买商品 2.邀请注册 3.自己参与
	 * @return
	 */
	public boolean saveLotteryTicketInfo(String newprize_id,String numbersource,String getlorrystatus,String user_id, String startprize_id);
	
	/**
	 * 更正每期抽奖的参与人数
	 * 假如不是 第一次开奖信息 并且之前一次没开奖 
	 * 此时本次参与抽奖的人数 应该是前一次抽奖人数加上本次抽奖的人数
	 * @param newprize_id
	 * @param lotteryInfoNew
	 */
	public void changeLotteryInfoJointNumber(String newprize_id,PrizeInfoNew lotteryInfoNew);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 6:47:54 PM
	 *
	 * @param userid
	 * @param prize_id
	 * @return 返回用户主动参与抽奖的时间
	 */
	public Timestamp getRemainderInviteTime(String userid, String prize_id);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 8:25:41 PM
	 *
	 * @param prizeid
	 * @return 抽奖状态,抽奖是否结束
	 */
	public int getLotteryStatus(String prizeid);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 8:56:15 PM
	 *
	 * @param prizeid
	 * @return判断该抽奖是否存在,防止用户手动修改prizeid
	 */
	public int getLotteryInfoStatus(String prizeid);
	
	/**
	 *  用户中心，获得用户所有开奖信息
	 * @param userId  用户id
	 * @return
	 */
	public List<LotteryTicket> getLotteryTicketInfo(Long userId);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 22, 2011 3:51:32 PM
	 * 
	 * @param prizeid
	 * @return 开奖结束后,统计所有数据
	 */
	public LotteryInfoNew getFinalLotteryResult(String prizeid);
	
}
