package com.beike.dao.invite;

import java.util.Date;
import java.util.List;
import com.beike.dao.GenericDao;

/**   
* @Title: 
* @Package com.beike.dao.invite 
* @Description: 0元抽奖 2.0版本 邀请DAO
* @author wenjie.mai   
* @date Dec 19, 2011 5:13:22 PM
* Company:Sinobo
* @version V1.0
*/
public interface LotteryInviteDao extends GenericDao {

	/**
	* 通过用户ID，查询该用户的邀请链接
	* @Title: getShortURLByUserID 
	* @Description: TODO
	* @param @param shorURL
	* @param @param messagetype
	* @param @return    
	* @return List
	* @throws
	 */
	@SuppressWarnings("unchecked")
	public List getShortURLByUserID(String shorURL, String messagetype);
	
	/**
	 * 查询用户短地址
	* @Title: getShorUrlInfo 
	* @Description: TODO
	* @param @param userId
	* @param @param messagetype
	* @param @return    
	* @return List
	* @throws
	 */
	public List getShorUrlInfo(Long userId , String messagetype,String prizeId);
	
	/**
	 * 添加用户的短地址信息
	* @Title: addShortURLByUserID 
	* @Description: TODO
	* @param @param userID
	* @param @param shortURL
	* @param @param actionurl
	* @param @param messagetype   
	* @return int
	* @throws
	 */
	public int addShortURLByUserID(Long userID , String shortURL , String actionurl, String messagetype);
		
	/**
	* 添加抽奖邀请记录
	* @Title: addPrizeInviteRecord 
	* @Description: TODO
	* @param @param sourceid
	* @param @param targetid
	* @param @param inventtime
	* @param @param newlorry_id    
	* @return void
	* @throws
	 */
	public void addPrizeInviteRecord(Long sourceid,Long targetid, Date inventtime, Long newlorry_id);
	
	/**
	 * 查询用户的奖券
	* @Title: getNewLotteryByUserId 
	* @Description: TODO
	* @param @param userId
	* @param @param prizeId
	* @param @param getlorrystatus
	* @param @return    
	* @return List
	* @throws
	 */
	public List getNewLotteryByUserId(Long userId, Long prizeId, String getlorrystatus);
	
	public List getLotteryForMySelfByUserId(Long userId,Long prizeId,String getlorrystatus,String iswinner);
	
	/**
	 * 查询邀请人的奖品ID
	* @Title: getNewLotteryFindPrizeId 
	* @Description: TODO
	* @param @param userId
	* @param @param getlorrystatus
	* @param @param iswinner
	* @param @return    
	* @return List
	* @throws
	 */
	public List getNewLotteryFindPrizeId(Long userId, String getlorrystatus, String iswinner);
	
	/**
	 * 查询两个用户之间的邀请关系
	* @Title: getInviteLotteryRecord 
	* @Description: TODO
	* @param @param sourceId
	* @param @param targetId
	* @param @return    
	* @return List
	* @throws
	 */
	public List getInviteLotteryRecord(Long sourceId, Long targetId, Long lorryId);
	
	/**
	 * 查询用户手机号
	* @Title: getUserMobileByUserId 
	* @Description: TODO
	* @param @param userId
	* @param @return    
	* @return List
	* @throws
	 */
	public List getUserMobileByUserId(Long userId);
	
	/**
	 * 
	 * @param shorURL
	 * @param messagetype
	 * @param prizeId
	 * @return
	 */
	public List getShortURLByUserID(String userId, String messagetype,String prizeId);
	
	/**
	 * 查询trx_goods_sn 是否加过奖券
	 * @param trx_goods_sn
	 * @return
	 */
	public List getNewLotteryInfo(String trx_goods_sn);
}
