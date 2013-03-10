package com.beike.dao.lottery;

import java.sql.Timestamp;
import java.util.List;

import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.lottery.LotteryTicket;
import com.beike.entity.lottery.PrizeInfoNew;

/**
 * @author jianwen
 */
public interface LotteryDaoNew {

	public LotteryInfoNew getLotteryInfoNew(String newprize_id);
	
	public Long isJoined(String newprize_id, String user_id);
	
	public List getRecommendGoodsID(String area_id);
	
	public List<LotteryTicket> getLotteryTicketInfo(String newprize_id,
			String user_id);
	
	public List<PrizeInfoNew> getPrizeInfoNew(String prize_id);

	
	public Long saveLotteryTicketInfo(String newprize_id,String numbersource,String getlorrystatus,String user_id);
	/**
	 * 
	 * @author janwen
	 * @time Dec 19, 2011 7:02:42 PM
	 *
	 * @param startprize_id
	 * @return 更新参与人数
	 */
	public boolean updateParticipants(String startprize_id);
	
	/**
	 * 得到某个奖品中奖名额
	 * @param startprize_id 奖品id
	 * @return	返回中奖名额
	 */
	public Integer getWinnersNumber(String startprize_id);
	
	/**
	 * 判断某个抽奖活动是否过期,抽奖是否结束
	 * @author janwen
	 * @time Dec 21, 2011 8:23:02 PM
	 *
	 * @param prizeid
	 * @return
	 */
	public int getLotteryStatus(String prizeid);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 6:43:55 PM
	 *
	 * @param userid
	 * @param prize_id
	 * @return 用户主动参与抽奖的时间
	 */
	public Timestamp getRemainderInviteTime(String userid,String prize_id);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 8:34:44 PM
	 *
	 * @param prizeid
	 * @return 验证该奖号是否存在
	 */
	public int getLotteryInfoStatus(String prizeid);
	
	public List<LotteryTicket> getLotteryTicketList(Long userId);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 22, 2011 3:51:32 PM
	 *
	 * @param prizeid
	 * @return 开奖结束后,统计所有数据,给为参加抽奖活动人员看
	 */
	public LotteryInfoNew getFinalLotteryResult(String prizeid);
}