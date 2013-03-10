package com.beike.service.operation.wish;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.guid.GuidGenerator;
import com.beike.dao.operation.wish.WishActivityDao;
import com.beike.entity.user.User;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beiker.model.operation.wish.InvitePrizeBean;
import com.beiker.model.operation.wish.InviteRecordBean;
import com.beiker.model.operation.wish.PrizePeopleInfo;
import com.beiker.model.operation.wish.UserProfileBean;
import com.beiker.model.operation.wish.WeiboInfo;
import com.beiker.model.operation.wish.WishUser;

@Service("wishActivityService")
public class WishActivityServiceImpl implements WishActivityService {

	@Autowired
	private WishActivityDao wishActivityDao;
	
	@Autowired
	private GuidGenerator guidGenerator;
	
	private MemCacheService memcacheService=MemCacheServiceImpl.getInstance();

	@Override
	public Long getAttendNo() {
		return wishActivityDao.getAttendNo();
	}

	@Override
	public Long getInviteRank(String userid) {
//		return wishActivityDao.getInviteRank(userid);
		return wishActivityDao.createInviteRankExtend(userid);
	}

	@Override
	public List<InviteRecordBean> getInviteRecordByUserID(int userid) {
		List<InviteRecordBean> rsList = wishActivityDao.findInviteRecordByUserId(userid);
		if(rsList == null)
		{
			return null;
		}
		
		for(InviteRecordBean irb : rsList)
		{
			int targetId = irb.getTargetId();
			long inventrecordId = irb.getId();
			
			List<User> userList = wishActivityDao.findUserByUserId(targetId);
			if(userList != null)
			{
				// 一个userId对应唯一一条记录，可以get(0)
				User user = userList.get(0);
				String email=user.getEmail();
				if(email!=null){
					email=email.substring(0, email.indexOf("@"));
					email+="...";
				}
				irb.setEmail(email);
				String mobile=irb.getMobile();
				if(mobile==null||"".equals(mobile)){
					mobile=user.getMobile();
				}
				if(mobile!=null){
					String pre=mobile.substring(0, 3);
					String after=mobile.substring(mobile.length()-4);
					StringBuilder sb=new StringBuilder(pre);
					sb.append("****");
					sb.append(after);
					mobile=sb.toString();
				}
				irb.setMobile(mobile);
			}
			
			List<InvitePrizeBean> prizeList = wishActivityDao.findInvitePrizeByInventrecordId(inventrecordId,userid);
			if(prizeList != null)
			{
				// 一个targetId在表beiker_inviteprize对应唯一一条记录，可以直接取get(0)
				InvitePrizeBean ipb = prizeList.get(0);
				irb.setAwardno(ipb.getAwardNo());
			}
			
		}
		return rsList;
//		return wishActivityDao.getInviteRecordByUserID(userid);
	}

	@Override
	public WishUser getWishUser(String userid) {
		return wishActivityDao.getWishUser(userid);
	}

	@Override
	public List<InviteRecordBean> getWishUserRank() {
		
		List<InviteRecordBean> rsList = new ArrayList<InviteRecordBean>();
		// 查询beiker_inviterecord表
//		List<InviteRecordBean> list = wishActivityDao.findInviteRecord();
		List<InviteRecordBean> list=wishActivityDao.createInviteRecordExtend();
		
		if(list == null)
		{
			return null;
		}
		
		for(InviteRecordBean ir : list)
		{
			int sourceId = ir.getTargetId();
			String fromWeb = ir.getFromWeb();
			// 参数不存在，则进行下一次循环
//			if(fromWeb == null)
//			{
//				continue;
//			}
//			List<UserProfileBean> upList = wishActivityDao.findUserProfile(sourceId, fromWeb);
//
//			if(upList != null)
//			{
//				// 存在微博情况
//				UserProfileBean upb = upList.get(0);
//				ir.setProfileName(upb.getName());
//				ir.setProfileValue(upb.getValue());
//				
//			}
			if(fromWeb.equals("EMAILCONFIG")){
				// 不是新浪和腾讯微博的情况, 去beiker_user表中查询user
				List<User> userList = wishActivityDao.findUserByUserId(sourceId);
				User user = userList.get(0);
				ir.setProfileName(user.getEmail());
				ir.setProfileValue(user.getEmail());
				ir.setEmail(user.getEmail());
			}
			
			rsList.add(ir);
		}
		
		return rsList;
	}

	@Override
	public void saveNewAward(String userid,String targetid,Long inviterecord_id){
		 
		String spid=guidGenerator.getSpecialId("AC", 6, false);
		//插入邀请者奖号
		wishActivityDao.saveNewAward(userid, inviterecord_id, spid);
		String spid2=guidGenerator.getSpecialId("AC", 6, false);
		//插入被邀请者奖号
		wishActivityDao.saveNewAward(targetid, inviterecord_id, spid2);
	}
	
	public void saveSourceAward(String userid,Long inviterecord_id,String fromweb,String weiboid,String nickName,String mobile){
		String spid=guidGenerator.getSpecialId("AC", 6, false);
		//插入邀请者奖号
		wishActivityDao.saveRecord("", userid, fromweb, weiboid, nickName,mobile);
		wishActivityDao.saveNewAward(userid, inviterecord_id, spid);
	}
	
	@Override
	public Long saveRecord(String userid, String targetid, String fromweb,
			String weiboid,String weiboName,String mobile) {
		int count = findCountByUserId(userid);
		if(count ==0){
			wishActivityDao.saveRecord(null, userid, "EMAILCONFIG", "","","");
		}
		Long inviterecord_id=wishActivityDao.saveRecord(userid, targetid, fromweb, weiboid,weiboName,mobile);
		saveNewAward(userid, targetid, inviterecord_id);
		return inviterecord_id;
	}

	public WishActivityDao getWishActivityDao() {
		return wishActivityDao;
	}

	public void setWishActivityDao(WishActivityDao wishActivityDao) {
		this.wishActivityDao = wishActivityDao;
	}

	public GuidGenerator getGuidGenerator() {
		return guidGenerator;
	}

	public void setGuidGenerator(GuidGenerator guidGenerator) {
		this.guidGenerator = guidGenerator;
	}

	@Override
	public void saveGenerateNgen(int number, String key) {
		//更新guid的数量
		String nowCount=guidGenerator.getSpecialId("AC", 6, false, Long.parseLong(number+""));
		//记录插入假数据的记录
		wishActivityDao.saveFalseCount(number);
		//更新缓存
		//查询真正记录数 加上 虚拟记录
		Long attendNumber=getAttendNo();
		
		Long falseCount=wishActivityDao.getFalseCount();
		Long totalCount=attendNumber+falseCount;
		memcacheService.set(key, totalCount);
	}
	@Override
	public Long getFalseCount() {
		Long attendNumber=getAttendNo();
		
		Long falseCount=wishActivityDao.getFalseCount();
		Long totalCount=attendNumber+falseCount;
		return totalCount;
		
	}
	@Override
	public WeiboInfo getWeiboInfo(Long userid) {
		WeiboInfo  weiboInfo=wishActivityDao.getWeiboInfo(userid);
		return weiboInfo;
		
	}

	@Override
	public Long getUserPrizeCount(Long userid) {
		return wishActivityDao.getUserPrizeCount(userid);
		
	}

	@Override
	public String getRegistUserPrizeNo(Long userid) {
		return wishActivityDao.getRegistUserPrizeNo(userid);
		
	}
	public int findCountByUserId(String userid) {
		return wishActivityDao.findCountByUserId(userid);
		
	}

	@Override
	public List<InviteRecordBean> createWishUserRank() {
		List<InviteRecordBean> rsList = new ArrayList<InviteRecordBean>();
		// 查询beiker_inviterecord表
//		List<InviteRecordBean> list = wishActivityDao.findInviteRecord();
		List<InviteRecordBean> list=wishActivityDao.createInviteRecordExtend();
		
		if(list == null)
		{
			return null;
		}
		
		for(InviteRecordBean ir : list)
		{
			int sourceId = ir.getTargetId();
			String fromWeb = ir.getFromWeb();
			// 参数不存在，则进行下一次循环
//			if(fromWeb == null)
//			{
//				continue;
//			}
//			List<UserProfileBean> upList = wishActivityDao.findUserProfile(sourceId, fromWeb);
//
//			if(upList != null)
//			{
//				// 存在微博情况
//				UserProfileBean upb = upList.get(0);
//				ir.setProfileName(upb.getName());
//				ir.setProfileValue(upb.getValue());
//				
//			}
			if(fromWeb.equals("EMAILCONFIG")){
				// 不是新浪和腾讯微博的情况, 去beiker_user表中查询user
				List<User> userList = wishActivityDao.findUserByUserId(sourceId);
				User user = userList.get(0);
				ir.setProfileName(user.getEmail());
				ir.setProfileValue(user.getEmail());
				ir.setEmail(user.getEmail());
			}
			if(ir.getTotal()==0){
				ir.setTotal(1);
			}
			rsList.add(ir);
		}
		
		return rsList;
		
	}

	@Override
	public Long createInviteRank(String userid) {
		Long count= wishActivityDao.createInviteRankExtend(userid);
		if(count==0)return getFalseCount();
		return count;
	}

	@Override
	public PrizePeopleInfo getPeopleInfo() {
		return wishActivityDao.getPeopleInfo();
		
	}

	@Override
	public boolean isActiveMobileExist(String mobile) {
		return wishActivityDao.isActiveMobileExist(mobile);
		
	}

}
