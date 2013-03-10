package com.beike.service.operation.wish;

import java.util.List;

import com.beiker.model.operation.wish.InviteRecordBean;
import com.beiker.model.operation.wish.PrizePeopleInfo;
import com.beiker.model.operation.wish.WeiboInfo;
import com.beiker.model.operation.wish.WishUser;

/**
 * 
 * @author janwen
 *
 */
public interface WishActivityService {

	/**
	 * 
	 * @param userid
	 * @return 参与用户的奖号
	 */
     public WishUser getWishUser(String userid);
     /**
      * 
      * @param userid
      * @return  用户的邀请记录
      */
     public List<InviteRecordBean> getInviteRecordByUserID(int userid);
     /**
      * 
      * @return 用户排行
      */
     public List<InviteRecordBean> getWishUserRank();
     
     
     /**
      * 
      * @return 用户排行
      */
     public List<InviteRecordBean> createWishUserRank();
     
     /**
      * 
      * @return 参与人总数
      */
     public Long getAttendNo();
     
     /**
      * @param userid
      * @return 当前用户推荐排名
      */
     public Long getInviteRank(String userid);
     
     
     /**
      * @param userid
      * @return 当前用户推荐排名
      */
     public Long createInviteRank(String userid);
     
     /***
      * 保存邀请记录
      * @param userid
      * @param targetid
      */
    public Long saveRecord(String userid,String targetid,String fromweb,String weiboid,String weiboName,String mobile);
    
    /***
     * 保存用户奖号信息 邀请者和被邀请者都加奖号
     * @param userid
     */
    public void saveNewAward(String userid,String targetid,Long inviterecord_id);
    
    /**
     * 保存用户奖号信息 被邀请者
     * @param userid
     * @param targetid
     * @param inviterecord_id
     */
    public void saveSourceAward(String userid,Long inviterecord_id,String fromweb,String weiboid,String nickName,String mobile);
    
    /**
     * 生成假的总数
     * @param number 想造假的个数
     * @param key	 memcachekey
     */
    public void saveGenerateNgen(int number,String key);
    
    /**
     * 获得总人数+假人数
     * @return
     */
    public Long getFalseCount();
    
    /**
     * 获取微博信息
     * @param userid 用户id 
     * @return
     */
    public WeiboInfo getWeiboInfo(Long userid);
    
    /**
     * 获得本用户 获得多少次获奖几率
     * @return
     */
    public Long getUserPrizeCount(Long userid);
    
    /**
     * 获得注册后获得的奖券
     * @param userid
     * @return
     */
    public String getRegistUserPrizeNo(Long userid);
	
    
    public int findCountByUserId(String userId);
    
    public PrizePeopleInfo getPeopleInfo();
    
    public boolean isActiveMobileExist(String mobile);
}
