package com.beike.service.invite;

import java.util.List;
import com.beike.entity.lottery.LotteryInfoNew;
/**   
* @Title: 
* @Package com.beike.dao.invite 
* @Description:  0元抽奖 2.0版本 邀请Service
* @author wenjie.mai   
* @date Dec 19, 2011 5:16:59 PM
* Company:Sinobo
* @version V1.0
*/
public interface LotteryInviteService {

	/**
	 * 查询用户的短地址
	* @Title: getShortUrl 
	* @Description: TODO
	* @param @param shorURL
	* @param @return    
	* @return List
	* @throws
	 */
	public List getShortUrl(String shorURL);
	
	/**
	 * 通过UserId查询短地址
	* @Title: getShortUrlByUserId 
	* @Description: TODO
	* @param @param userId
	* @param @return    
	* @return List
	* @throws
	 */
	public String getShortUrlByUserId(Long userId,String prizeId);
	
	/**
	 * 添加短地址记录
	* @Title: addShortURL 
	* @Description: TODO
	* @param @param userID
	* @param @param actionurl    
	* @return String
	* @throws
	 */
	public String  addShortURL(Long userID , String actionurl,String prizeId);
	
	/**
	 * 添加抽奖邀请记录
	* @Title: addPrizeInviteRecord 
	* @Description: TODO
	* @param @param sourceid
	* @param @param targetid
	* @param @param newprize_id    
	* @return void
	* @throws
	 */
	public void addPrizeInviteRecord(Long sourceid, Long targetid, Long newprize_id);
	
	/**
	 * 查询用户奖券信息 24小时购买
	* @Title: getLotteryInviteByUserId 
	* @Description: TODO
	* @param @param userId
	* @param @param prizeId
	* @param @param getlorrystatus
	* @param @return    
	* @return List
	* @throws
	 */
	public List getLotteryInviteByUserId(Long userId , Long prizeId , String getlorrystatus);
	
	/**
	 * 查询用户正常抽奖信息
	* @Title: getLotteryForMySelf 
	* @Description: TODO
	* @param @param userId
	* @param @param prizedId
	* @param @param getlorrystatus
	* @param @return    
	* @return List
	* @throws
	 */
	
	public List getLotteryForMySelf(Long userId, Long prizedId);
	
	/**
	 * 查找邀请人的奖品ID
	* @Title: getNewLottery 
	* @Description: TODO
	* @param @param userId
	* @param @return    
	* @return List
	* @throws
	 */
	public List getNewLottery(Long userId);
	

	public void addNewLotteryInfo(String newprizeid,String numbersource, String getlorrystatus, String user_id,int startprizeId);
	
	/**
	 * 查找奖品信息
	* @Title: getPrizeInfo 
	* @Description: TODO
	* @param @param prizeid
	* @param @return    
	* @return LotteryInfoNew
	* @throws
	 */
	public LotteryInfoNew getPrizeInfo(String prizeid);
	
	/**
	 * 通过trx_goods_sn 查询是否加过奖券
	 * @param trx_goods_sn
	 * @return
	 */
	public List getNewLotteryByGoodsSn(String trx_goods_sn);
	
}
