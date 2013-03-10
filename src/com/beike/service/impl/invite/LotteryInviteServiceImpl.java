package com.beike.service.impl.invite;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.dao.GenericDao;
import com.beike.dao.invite.LotteryInviteDao;
import com.beike.dao.lottery.LotteryDaoNew;
import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.invite.LotteryInviteService;
import com.beike.util.ShortUrlGenerator;

/**   
* @Title:
* @Package com.beike.dao.invite 
* @Description: 0元抽奖 2.0版本 邀请Service实现
* @author wenjie.mai   
* @date Dec 19, 2011 5:17:31 PM
* Company:Sinobo
* @version V1.0
*/
@Service("lotteryInviteService")
public class LotteryInviteServiceImpl extends GenericServiceImpl implements
		LotteryInviteService {

	public LotteryInviteServiceImpl() {
		
	}
	
	@Resource(name = "lotteryInviteDao")
	private LotteryInviteDao lotteryInviteDao;
	
	@Resource(name = "lotteryDaoNew")
	private LotteryDaoNew lotteryDaoNew;
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(LotteryInviteServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public GenericDao getDao() {
		return lotteryInviteDao;
	}

	@Override
	public Object findById(Serializable id) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getShortUrl(String shorURL) {
		List inviteList = lotteryInviteDao.getShortURLByUserID(shorURL,"1");
		return inviteList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String addShortURL(Long userID , String actionurl,String prizeId) {
		// 先判断短地址是否存在
		List li = lotteryInviteDao.getShortURLByUserID(userID.toString(), "1", prizeId);
		if(li != null && li.size()>0){
			Map mc = (Map) li.get(0);
			return (String) mc.get("shortsecret");
		}else{
			String shortURL = ShortUrlGenerator.shortUrl(actionurl);
			int flag = lotteryInviteDao.addShortURLByUserID(userID, shortURL, actionurl,"1");
			if(flag > 0){
				return shortURL;
			}
		}
		
		return "";
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void addPrizeInviteRecord(Long sourceid, Long targetid,Long newprize_id) {

		// 1.生成奖券   2. 生成抽奖邀请记录
		String souid = "";
		String priid = "";
		String telph = ""; // 联系方式
		
		LotteryInfoNew li   =lotteryDaoNew.getLotteryInfoNew(String.valueOf(newprize_id)); // 查找奖品信息
		if(li != null){   // 抽奖未结束
			
			
			if(newprize_id != null){
				priid = String.valueOf(newprize_id);
			}
			if(sourceid != null){
				souid = String.valueOf(sourceid);
			}
			
			List mo = lotteryInviteDao.getUserMobileByUserId(targetid);  // 查询被邀请人手机号或者邮箱
			
			if(mo != null && mo.size() >0){
				Map mx =(Map) mo.get(0);
				String mobile = (String) mx.get("mobile");
				String email  = (String) mx.get("email");
				if(!StringUtils.isEmpty(mobile)){
					telph = telph + mobile.substring(0,3)+"XXXXXX"+mobile.substring(9,11);
				}else{
					telph = telph + email.substring(0,email.indexOf("@"));
				}
			}
			Long lottery_source =lotteryDaoNew.saveLotteryTicketInfo(priid, "邀请好友"+telph+"参加了抽奖", "2", souid);// 邀请人, 增加一个奖券
			List ln = lotteryInviteDao.getInviteLotteryRecord(sourceid, targetid,lottery_source);  // 判断两个用户之前是否有邀请关系
			int start_prizeId = li.getStartprize_id();
			lotteryDaoNew.updateParticipants(String.valueOf(start_prizeId));// 抽奖参与人数+1
			if(ln == null || ln.size() == 0){  // 如果之前存在邀请关系，则不添加
				if(lottery_source != null && lottery_source >0){ // 插入一条邀请记录
					lotteryInviteDao.addPrizeInviteRecord(sourceid, targetid, new Date(), lottery_source);
				}
			}
		}
				
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLotteryInviteByUserId(Long userId, Long prizeId, String getlorrystatus) {
		
		List lotList = lotteryInviteDao.getNewLotteryByUserId(userId, prizeId, getlorrystatus);
		return lotList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLotteryForMySelf(Long userId, Long prizedId) {
		
		List los = lotteryInviteDao.getLotteryForMySelfByUserId(userId, prizedId,"3","0");
		
		return los;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getNewLottery(Long userId) {
		List lot = lotteryInviteDao.getNewLotteryFindPrizeId(userId, "3", "0");
		return lot;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getShortUrlByUserId(Long userId,String prizeId) {
		List li =lotteryInviteDao.getShorUrlInfo(userId, "1",prizeId);
		if(li != null && li.size() >0){
			Map mc = (Map) li.get(0);
			String url = (String) mc.get("shortsecret");
			return url;
		}
		return null;
	}

	@Override
	public void addNewLotteryInfo(String newprizeid,String numbersource, String getlorrystatus, String user_id, int startPrizeid) {
		lotteryDaoNew.saveLotteryTicketInfo(newprizeid,numbersource,getlorrystatus,user_id);
		lotteryDaoNew.updateParticipants(String.valueOf(startPrizeid));// 抽奖参与人数+1
	}

	@Override
	public LotteryInfoNew getPrizeInfo(String prizeid) {
		LotteryInfoNew li   =lotteryDaoNew.getLotteryInfoNew(String.valueOf(prizeid)); // 查找奖品信息
		return li;
	}

	@Override
	public List getNewLotteryByGoodsSn(String trx_goods_sn) {
		List li = lotteryInviteDao.getNewLotteryInfo(trx_goods_sn);
		return li;
	}
	
}
