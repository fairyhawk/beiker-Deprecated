package com.beike.dao.impl.invite;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.invite.LotteryInviteDao;
import com.beike.util.DateUtils;

/**   
* @Title:
* @Package com.beike.dao.invite 
* @Description:  0元抽奖 2.0版本 邀请DAOImpl 
* @author wenjie.mai   
* @date Dec 19, 2011 5:15:37 PM
* Company:Sinobo
* @version V1.0
*/
@Repository("lotteryInviteDao")
public class LotteryInviteDaoImpl extends GenericDaoImpl implements
		LotteryInviteDao {
	
	@Override
	public List getShortURLByUserID(String userId, String messagetype,
			String prizeId) {
		
		String sql="select bs.user_id, bs.shortsecret , bs.shortmessage , bs.messagetype FROM beiker_shorturl bs " +
				"where bs.user_id = ? and bs.messagetype = ? and bs.shortmessage like ?";
		List rs = this.getJdbcTemplate().queryForList(sql,new Object[]{userId,messagetype,"%="+prizeId});
		return rs;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List getShortURLByUserID(String shorURL, String messagetype) {
		
		String query_shortURL = "select bs.user_id, bs.shortsecret , bs.shortmessage , bs.messagetype FROM beiker_shorturl bs " +
									"where bs.shortsecret = ? and bs.messagetype = ?";
		List rs = this.getJdbcTemplate().queryForList(query_shortURL,new Object[]{shorURL,messagetype});
		return rs;
	}

	@Override
	public int addShortURLByUserID(Long userID, String shortURL , String actionurl, String messagetype) {
		String insert_url = "insert into beiker_shorturl(user_id , shortsecret , shortmessage , messagetype) values(?,?,?,?)";
		int resu = this.getJdbcTemplate().update(insert_url, new Object[]{userID,shortURL,actionurl,messagetype});
		return resu;
	}

	@Override
	public void addPrizeInviteRecord(Long sourceid, Long targetid, Date inventtime, Long newlorry_id) {
		
		String insert_invite = "insert into beiker_prizeinvite(sourceid,targetid,inventtime,newlorry_id) values(?,?,?,?)";
		this.getJdbcTemplate().update(insert_invite, new Object[]{sourceid,targetid,inventtime,newlorry_id});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getNewLotteryByUserId(Long userId, Long prizeId,String getlorrystatus) {
		
		String query_lottery = "select bn.user_id,bn.winnumber,bn.iswinner,bn.createtime,bn.numbersource," +
									"bn.getlorrystatus,bn.newprize_id from beiker_newlorry bn " +
									"where bn.user_id= ? and bn.newprize_id = ? and bn.getlorrystatus = ? ";
		List lo = this.getJdbcTemplate().queryForList(query_lottery,new Object[]{userId,prizeId,getlorrystatus});
		return lo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLotteryForMySelfByUserId(Long userId, Long prizeId,String getlorrystatus, String iswinner) {
		
		String los   = "select bl.user_id,bl.winnumber,bl.iswinner,bl.createtime,bl.numbersource,bl.getlorrystatus,bl.newprize_id from beiker_newlorry bl where bl.user_id=? and bl.newprize_id=? and bl.getlorrystatus=? and bl.iswinner !=?";
		List loslist =  this.getJdbcTemplate().queryForList(los,new Object[]{userId,prizeId,getlorrystatus,iswinner});
		return loslist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getNewLotteryFindPrizeId(Long userId,String getlorrystatus, String iswinner) {
		// 如果userId 同时参加多个抽奖活动，则查最近的一笔奖品ID
		String query_prize = "select bl.newprize_id from beiker_newlorry bl where bl.user_id=? and bl.getlorrystatus=? and bl.iswinner!=? Order By bl.createtime DESC limit 1";
		List prizeList = this.getJdbcTemplate().queryForList(query_prize,new Object[]{userId,getlorrystatus,iswinner});
		return prizeList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getShorUrlInfo(Long userId, String messagetype,String prizeId) {
		String query_shortURL = "select bs.user_id, bs.shortsecret , bs.shortmessage , bs.messagetype FROM beiker_shorturl bs " +
									"where bs.user_id = ? and bs.messagetype = ? and bs.shortmessage like ?";
		List li = this.getJdbcTemplate().queryForList(query_shortURL,new Object[]{userId,messagetype,"%="+prizeId});
		return li;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getInviteLotteryRecord(Long sourceId, Long targetId,Long lorryId) {
		
		String query_invite = "select bp.sourceid,bp.targetid,bp.inventtime,bp.newlorry_id from beiker_prizeinvite bp where bp.sourceid =? and bp.targetid =? and bp.newlorry_id =?";
		List ln = this.getJdbcTemplate().queryForList(query_invite,new Object[]{sourceId,targetId,lorryId});
		return ln;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getUserMobileByUserId(Long userId) {
		String query_mobile = "select bu.mobile,bu.email from beiker_user bu where bu.user_id =?";
		List ml = this.getJdbcTemplate().queryForList(query_mobile,new Object[]{userId});
		return ml;
	}

	@Override
	public List getNewLotteryInfo(String trx_goods_sn) {
		
		String query_lottery = "select bn.newlorry_id,bn.user_id,bn.newprize_id from beiker_newlorry bn where bn.numbersource ='购买商品,订单号"+trx_goods_sn+"'";
		List ls = this.getJdbcTemplate().queryForList(query_lottery);
		return ls;
	}

	

}
