package com.beike.dao.operation.wish;

import java.util.List;

import com.beike.entity.user.User;
import com.beiker.model.operation.wish.InvitePrizeBean;
import com.beiker.model.operation.wish.InviteRecord;
import com.beiker.model.operation.wish.InviteRecordBean;
import com.beiker.model.operation.wish.PrizePeopleInfo;
import com.beiker.model.operation.wish.UserProfileBean;
import com.beiker.model.operation.wish.WeiboInfo;
import com.beiker.model.operation.wish.WishUser;
import com.beiker.model.operation.wish.WishUserRank;

/**
 * 
 * @author janwen
 *
 */
public interface WishActivityDao {
	
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
     public List<InviteRecord> getInviteRecordByUserID(String userid);
     /**
      * 
      * @return 用户排行
      */
     public List<WishUserRank> getWishUserRank();
     
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
     public Long createInviteRankExtend(String userid);
     
  /**
   * 
   * @param userid
   * @param targetid
   * @param fromweb
   * @param weiboid
   * @return 插入记录id
   */
    public Long saveRecord(String userid,String targetid,String fromweb,String weiboid,String nickname,String mobile);
    
    /***
     * 保存用户奖号信息,包括被邀请用户,邀请用户
     * @param userid
     */
    public void saveNewAward(String userid,Long inviterecord_id,String number);
    
    /**
     * 获得虚假的人数
     * @return
     */
    public Long getFalseCount();
    
    /**
     * 插入假数据记录
     * @param falseCount
     */
    public void saveFalseCount(int falseCount);
	
    /**
     * 查询beiker_inviterecord表, 统计次数, 查询出sourceid、fromweb、weiboid、推荐次数
     * @return 返回查询结果对象
     * 
     * @author kun.wang
     */
    public List<InviteRecordBean> findInviteRecord();
    
    /**
     * 查询beiker_inviterecord表, 统计次数, 查询出sourceid、fromweb、weiboid、推荐次数
     * @return 返回查询结果对象   新方法
     * 
     * @author ye.tian
     */
    public List<InviteRecordBean> createInviteRecordExtend();
    /**
     * 查询参加活动的 微博信息
     * @param userid
     * @return
     */
    public WeiboInfo getWeiboInfo(Long userid);
    /**
     * 查询beiker_userprofile表, 根据sourceid、fromweb分组，查询name、value
     * @param sourceId 用户id
     * @param profiletype 属性类型
     * @return 返回查询结果对象
     * 
     * @author kun.wang
     */
    public List<UserProfileBean> findUserProfile(int sourceId, String profiletype);
    
    /**
     * 查询beiker_user表, 根据userId获取email
     * @param sourceId 用户id
     * @return 返回查询结果对象
     * 
     * @author kun.wang
     */
    public List<User> findUserByUserId(int sourceId);
    
    /**
     * 查询beiker_inviterecord表, 根据userId获取所有记录
     * @param sourceId 用户id
     * @return 返回查询结果对象
     * 
     * @author kun.wang
     */
    public List<InviteRecordBean> findInviteRecordByUserId(int sourceId);
    
    /**
     * 查询beiker_inviteprize表, 根据beiker_inventrecord表id查询奖号
     * @param inventrecordId beiker_inventrecord表id
     * @return 返回查询结果对象
     * 
     * @author kun.wang
     */
    public List<InvitePrizeBean> findInvitePrizeByInventrecordId(long inventrecordId, int userid);
    
    /**
     * 查询该用户获得多少中奖几率
     * @param userid
     * @return
     */
    public Long getUserPrizeCount(Long userid);
    
    /**
     * 获得用户注册时得的那张奖券
     * @param userid
     * @return
     */
    public String getRegistUserPrizeNo(Long userid);
    
    public int findCountByUserId(String userId);
    
    public PrizePeopleInfo getPeopleInfo();
    
    public boolean isActiveMobileExist(String mobile);
    
}
