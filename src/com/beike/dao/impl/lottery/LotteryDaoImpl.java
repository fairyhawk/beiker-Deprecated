/**
 * 
 */
package com.beike.dao.impl.lottery;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.lottery.LotteryDao;
import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.lottery.PrizeGoods;
import com.beike.entity.lottery.PrizeInfo;
import com.beike.util.BeanUtil;

/**
 * @author janwen
 *
 */

@SuppressWarnings("unchecked")
@Repository
public class LotteryDaoImpl extends GenericDaoImpl implements LotteryDao {

	/* (non-Javadoc)
	 * @see com.beike.dao.lottery.LotteryDao#getLotteryInfo(java.lang.String, java.lang.String)
	 * 
	 * 获得开奖信息
	 */
	
	@Override
	public LotteryInfo getLotteryInfo(String prizeid) {
		String sql = "select * from beiker_lottery where prize_id=?";
		//LotteryInfo lotteryInfo = getSimpleJdbcTemplate().queryForObject(sql,ParameterizedBeanPropertyRowMapper.newInstance(LotteryInfo.class),prizeid);
		List<LotteryInfo> lotteryInfo = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(LotteryInfo.class), prizeid);
		if(lotteryInfo.size() > 0){
			return lotteryInfo.get(0);
		}
		return null;
	}

	
	//当前服务器时间,比开奖时间晚,结束抽奖,更新中奖活动状态
	@Override
	public void updatePrizeInfo(PrizeInfo prizeInfo) {
		String sql = "update beiker_prize set status=2 where prize_id=?";
		getSimpleJdbcTemplate().update(sql, prizeInfo.getPrize_id());
	}


	@Override
	public boolean isPrizeGoods(String goodsid) {
		String sql = "select goods_id from beiker_prize where goods_id=?";
		List<Integer> result = getSimpleJdbcTemplate().query(sql,ParameterizedBeanPropertyRowMapper.newInstance(Integer.class), goodsid);
		if(result.size() > 0){
			return true;
		}
		return false;
	}

	
	//奖号格式:六位，位数不够补零000001

	@Override
	public String getLotteryNumber(String userid, String prizeid) {
		//FIXME
		String sql = "select * from beiker_lottery where lottery_id=(select max(lottery_id) from beiker_lottery where prize_id=?)";
	    
		//FIXME
		//List<String> awardNo = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(String.class),prizeid);
		List<LotteryInfo> awardNo = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(LotteryInfo.class),prizeid);

		
		if(awardNo.size() > 0 && !"\'\'".equals(awardNo.get(0))){
	    	Long currentNo = Long.parseLong(awardNo.get(0).getWinnumber());
	    	return formatPrizeNo(currentNo);
	    }else{
	    	//第一个抽奖号
	    	return "000001";
	    }
	}

    public String formatPrizeNo(Long currentNo){
    	Long newNo = currentNo.longValue()+1;
    	String newNoStr = Long.toString(newNo);
  	    while(newNoStr.length()<6){
  	    	newNoStr = "0"+newNoStr;
  	    }
  	    return newNoStr;
    }
	@Override
	public LotteryInfo saveLotteryInfo(String prizeid,String userid) {
		
		String sql2 = "INSERT INTO beiker_lottery(user_id, prize_id, winnumber, createtime) (SELECT " + userid + ", " + prizeid + ", (SELECT LPAD((SELECT CAST(IFNULL((SELECT winnumber FROM beiker_lottery WHERE lottery_id = (SELECT MAX(lottery_id) FROM beiker_lottery WHERE prize_id = " + prizeid + ")), '0') AS SIGNED) + 1), 6, '0')), NOW())";
		getJdbcTemplate().execute(sql2);
		String sqlAward = "select * from beiker_lottery where user_id=? and prize_id=?";
		//更新参与人数
		String updateSQL = "update beiker_prize set participantscount=participantscount+1 where prize_id=?";
		getSimpleJdbcTemplate().update(updateSQL, prizeid);
		List<LotteryInfo> lotteryInfoList = getSimpleJdbcTemplate().query(sqlAward, ParameterizedBeanPropertyRowMapper.newInstance(LotteryInfo.class), userid,prizeid);
		if(lotteryInfoList.size() > 0){
			return lotteryInfoList.get(0);
		}
		return null;
	}


	@Override
	public LotteryInfo isJoined(String prizeid, String userid) {
		String sql = "select * from beiker_lottery where prize_id=? and user_id=?";
		List<LotteryInfo> prizeInfoList = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(LotteryInfo.class), prizeid,userid);
		if(prizeInfoList.size() > 0){
			return prizeInfoList.get(0);
		}
		return null;
	}

   //获得中奖活动信息
	@Override
	public PrizeInfo getPrizeInfo(String prizeid) {
		String sql = "select * from beiker_prize where prize_id=?";
		List<PrizeInfo> prizeInfoList = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(PrizeInfo.class), prizeid);
		if(prizeInfoList.size()>0){
			PrizeInfo prizeInfo = prizeInfoList.get(0);
			if(Calendar.getInstance().getTime().after(prizeInfo.getEndtime()) && prizeInfo.getStatus() == 1){
				updatePrizeInfo(prizeInfo);
				prizeInfo.setStatus(2);
			}
			logger.info("*******************prizeinfo.status=*****************" + prizeInfo.getStatus());
			return prizeInfo;
		}
		return null;
	}


	@Override
	public List<String> getLotteryWinnersNo(String prizeid) {
		String sql = "select winnumber from beiker_lottery where prize_id=" + prizeid + " and iswinner=1";
		//List<String> lotteryWinners = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(String.class), prizeid);
		List<String> lotteryWinners = getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet arg0, int arg1) throws SQLException {
				return arg0.getObject("winnumber");
			}
		});
		return lotteryWinners;
	}

	
	/** 
	 * 查询抽奖活动的信息
	 * 信息来源：前台数据库下的 beiker_prize 表
	 *  
	 * Add by  zx.liu
	 */
	@Override
	public Object findPrizeGoodsById(Long prizeId) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT prize_id, goods_id, goods_name, prize_name, winners, " +
				" begintime, endtime, seeddescription, seedemergencetime, prizeseed, " +
				" featuredid_1, featuredid_2, featuredid_3, featuredid_4,participantscount ");
		sql.append(" FROM beiker_prize WHERE prize_id = ? ");
		
		List<Map<String, Object>>  listObj = getSimpleJdbcTemplate().queryForList(sql.toString(), prizeId);
		List<Object> resultList = null;
		if (null != listObj && listObj.size() > 0) {
			resultList = BeanUtil.convertResultToObjectList(listObj, PrizeGoods.class);
			return resultList.get(0);
		}
		
		return null;
	}

	
	/**
	 * 根据 抽奖活动的ID 和 用户ID 来获取参与抽奖用户的 奖号和Email 
	 * 信息主要来源：前台数据库下的 beiker_lottery 表
	 * 
	 * Add by zx.liu
	 */
	@Override
	public Map<String, Object> findLotteryUserById(Long prizeId, Long userId) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT l.winnumber, u.email ");
		sql.append(" FROM beiker_lottery l JOIN beiker_user u ON l.user_id=u.user_id ");
		sql.append(" WHERE l.prize_id=? AND u.user_id=? ");		
		// 根据抽奖活动ID 和用户ID 来唯一确定 用户的奖号 winnumber !
		Map<String, Object>  tempMap = getSimpleJdbcTemplate().queryForMap(sql.toString(), prizeId, userId);
		return tempMap;
	}	
	
	
	/**
	 * 根据商品的ID来获取商品的信息
	 * 
	 * Add by zx.liu
	 */	
	@Override
	public Map<String, Object> findGoodsInfo(Long goodsId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT goodsname, logo4, currentPrice, rebatePrice ");
		sql.append(" FROM beiker_goods WHERE goodsid = ? ");

		Map<String, Object> goodsMap =  getSimpleJdbcTemplate().queryForMap(sql.toString(), goodsId);		
		return goodsMap;
	
	}


	@Override
	public List<LotteryInfo> getLotteryInfoList(String prizeIds) {
		
		String sql="select * from beiker_prize bp where bp.prize_id in ("+prizeIds+")";
		List list=this.getJdbcTemplate().queryForList(sql);
		List<LotteryInfo> listLotteryInfoList=new ArrayList<LotteryInfo>();
		if(list==null||list.size()==0){
			return listLotteryInfoList;
		}
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			LotteryInfo lotteryInfo=new LotteryInfo();
			Long prize_id=((Number) map.get("prize_id")).longValue();
			Long participantscount=((Number) map.get("participantscount")).longValue();
			lotteryInfo.setLotteryId(prize_id);
			lotteryInfo.setParticipantscount(participantscount);
			listLotteryInfoList.add(lotteryInfo);
		}
		
		
		return listLotteryInfoList;
	}		
	
	
}
