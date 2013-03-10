package com.beike.dao.impl.lottery;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.lottery.LotteryDaoNew;
import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.lottery.LotteryTicket;
import com.beike.entity.lottery.PrizeInfoNew;
import com.beike.util.DateUtils;

@Repository("lotteryDaoNew")
public class LotteryDaoNewImpl extends GenericDaoImpl implements LotteryDaoNew {

	@Override
	public LotteryInfoNew getLotteryInfoNew(String newprize_id) {
		// String sql = "SELECT
		// bn.newprize_id,bn.newprize_name,bn.newprize_starttime,bn.newprize_pagetitle,bn.newprize_pic,temp2.winners,temp2.total,temp2.startprize_seedtime,temp2.startprize_id
		// FROM beiker_newprize bn JOIN(SELECT
		// bs2.prize_id,bs2.startprize_seedtime,temp.total,temp.winners,bs2.startprize_id
		// FROM beiker_startprize bs2 LEFT JOIN(SELECT bs.prize_id,
		// SUM(bs.startprize_winnumbers) AS winners, bs.startprize_jointnumber
		// AS total FROM beiker_startprize bs WHERE bs.prize_id=?) AS temp ON
		// bs2.prize_id=temp.prize_id WHERE bs2.strartprize_status='1' AND
		// bs2.prize_id=? ORDER BY bs2.startprize_seedtime DESC LIMIT 1) AS
		// temp2 ON temp2.prize_id=bn.newprize_id";
		String sql = "SELECT bn.newprize_id,bn.newprize_name,bn.newprize_starttime,bn.newprize_pagetitle,bn.newprize_pic,temp2.strartprize_status,temp2.startprize_jointnumber AS total,temp2.startprize_seedtime,temp2.startprize_id FROM beiker_newprize bn LEFT JOIN beiker_startprize temp2 ON temp2.prize_id = bn.newprize_id WHERE temp2.strartprize_status = '1' AND bn.newprize_id = ? AND temp2.startprize_seedtime>NOW()  ORDER BY temp2.startprize_seedtime ASC LIMIT 1";
		List<LotteryInfoNew> lotteryInfoNewList = getJdbcTemplate().query(
				sql,
				new Object[] { newprize_id },
				ParameterizedBeanPropertyRowMapper
						.newInstance(LotteryInfoNew.class));
		if (lotteryInfoNewList.size() > 0) {
			return lotteryInfoNewList.get(0);
		}
		return null;
	}

	@Override
	public Long isJoined(String newprize_id, String user_id) {
		String sql = "SELECT DISTINCT bn.newprize_id FROM beiker_newlorry bn WHERE bn.newprize_id=? AND bn.user_id=? AND bn.getlorrystatus='3'";
		List results = getJdbcTemplate().queryForList(sql,
				new Object[] { newprize_id, user_id });
		if (results != null && results.size() > 0) {
			return (Long) ((Map) results.get(0)).get("newprize_id");
		}
		return 0L;
	}

	@Override
	public List getRecommendGoodsID(String area_id) {
		String sql = "SELECT DISTINCT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods_profile bg ON bcg.goodid=bg.goodsid WHERE bcg.area_id=? AND bcg.isavaliable='1' ORDER BY bg.sales_count DESC LIMIT 8";
		List goodsid = getJdbcTemplate().queryForList(sql,
				new Object[] { area_id });
		return goodsid;
	}

	@Override
	public List<LotteryTicket> getLotteryTicketInfo(String newprize_id,
			String user_id) {
		String sql = "SELECT bn.newlorry_id,bn.user_id,bn.winnumber,bn.iswinner,bn.createtime,bn.numbersource,bn.getlorrystatus,bs.strartprize_status FROM beiker_newlorry bn JOIN beiker_startprize bs ON (bs.prize_id=bn.newprize_id AND bn.newprize_id=? AND bn.user_id=?) GROUP BY bn.newlorry_id";
		return getJdbcTemplate().query(
				sql,
				new Object[] { newprize_id, user_id },
				ParameterizedBeanPropertyRowMapper
						.newInstance(LotteryTicket.class));
	}

	@Override
	public List<PrizeInfoNew> getPrizeInfoNew(String prize_id) {
		String sql = "SELECT bs.startprize_id,bs.startprize_desc,bs.startprize_seedtime,bs.startprize_title,bs.strartprize_status,bs.startprize_jointnumber,bs.startprize_seed,bs.startprize_number FROM beiker_startprize bs WHERE bs.prize_id=? order by bs.startprize_seedtime ASC";
		return getJdbcTemplate().query(
				sql,
				new Object[] { prize_id },
				ParameterizedBeanPropertyRowMapper
						.newInstance(PrizeInfoNew.class));
	}

	@Override
	public Long saveLotteryTicketInfo(String newprize_id, String numbersource,
			String getlorrystatus, String user_id) {

		String sql = "INSERT INTO beiker_newlorry(user_id,winnumber,iswinner,createtime,numbersource,getlorrystatus,newprize_id) VALUES(?,(SELECT LPAD(IFNULL(MAX(CONVERT(bn.winnumber, UNSIGNED))+1,'1'),8,'0') FROM beiker_newlorry bn WHERE bn.newprize_id=?),'2', NOW(),?,?,?)";
		int result = getJdbcTemplate().update(
				sql,
				new Object[] { user_id, newprize_id, numbersource,
						getlorrystatus, newprize_id });
		if (result > 0) {
			return this.getLastInsertId();
		}
		return null;
	}

	@Override
	public boolean updateParticipants(String startprize_id) {
		String sql = "UPDATE beiker_startprize bs SET bs.startprize_jointnumber=bs.startprize_jointnumber+1 WHERE bs.startprize_id=?";
		int results = getJdbcTemplate().update(sql,
				new Object[] { startprize_id });
		if (results > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Integer getWinnersNumber(String startprize_id) {
		String sql = "SELECT SUM(bs.startprize_winnumbers) AS  winner FROM beiker_startprize bs WHERE prize_id=?";
		List list = this.getJdbcTemplate().queryForList(sql,
				new Object[] { startprize_id });
		if (list == null || list.size() == 0)
			return 0;
		Map map = (Map) list.get(0);
		BigDecimal winner = (BigDecimal) map.get("winner");
		return Integer.parseInt(winner.toString());
	}

	@Override
	public Timestamp getRemainderInviteTime(String userid, String prize_id) {
		String sql = "SELECT bn.createtime FROM beiker_newlorry bn WHERE bn.user_id=? AND bn.getlorrystatus='3'  AND bn.newprize_id=?";
		List list = getJdbcTemplate().queryForList(sql,
				new Object[] { userid, prize_id });
		Map map = (Map) list.get(0);
		Timestamp reminderTime = (Timestamp) map.get("createtime");
		return reminderTime;
	}

	@Override
	public int getLotteryStatus(String prizeid) {
//		String sql = "SELECT * FROM beiker_startprize bs WHERE bs.startprize_seedtime>NOW() AND bs.strartprize_status='1' AND bs.prize_id=?";
		String sql = "SELECT bs.startprize_id,bs.startprize_winnumbers,bs.startprize_desc,bs.startprize_seedtime,bs.startprize_title," +
						"bs.startprize_jointnumber,bs.startprize_seed,bs.prize_id,bs.strartprize_status,bs.startprize_number " +
						"FROM beiker_startprize bs WHERE bs.strartprize_status='1' AND bs.prize_id=? AND bs.startprize_seedtime > ?";
		String now = DateUtils.getNowTime();
		List list = getJdbcTemplate().queryForList(sql,new Object[] { prizeid , now });
		if (list == null || list.size() == 0)
			return 0;
		return list.size();
	}

	@Override
	public int getLotteryInfoStatus(String prizeid) {
		String sql = "SELECT * FROM beiker_newprize bn WHERE bn.newprize_id=?";
		List list = getJdbcTemplate().queryForList(sql,
				new Object[] { prizeid });
		if (list == null || list.size() == 0)
			return 0;
		return list.size();
	}

	@Override
	public List<LotteryTicket> getLotteryTicketList(Long userId) {
		String sql="select bl.createtime,bn.newprize_pagetitle,bl.iswinner,bn.newprize_id from beiker_newlorry bl left join beiker_newprize bn on bl.newprize_id=bn.newprize_id where bl.user_id=?";
		List list=this.getJdbcTemplate().query(sql, new Object[]{userId},ParameterizedBeanPropertyRowMapper
				.newInstance(LotteryTicket.class));
		return list;
	}
	@Override
	/**
	 * 
	 * @author janwen
	 * @time Dec 22, 2011 3:51:32 PM
	 * 
	 * @param prizeid
	 * @return 开奖结束后,统计所有数据
	 */
	public LotteryInfoNew getFinalLotteryResult(String prizeid) {
		String sql = "SELECT bn.newprize_id,bn.newprize_name,bn.newprize_starttime,bn.newprize_pagetitle,bn.newprize_pic,temp2.strartprize_status,temp2.startprize_jointnumber AS total,temp2.startprize_seedtime,temp2.startprize_id FROM beiker_newprize bn LEFT JOIN beiker_startprize temp2 ON temp2.prize_id = bn.newprize_id WHERE (temp2.strartprize_status ='2' OR temp2.startprize_seedtime<=now())  AND bn.newprize_id =? ORDER BY temp2.startprize_seedtime DESC LIMIT 1";
		List<LotteryInfoNew> lotteryInfoNewList = getJdbcTemplate().query(
				sql,
				new Object[] { prizeid },
				ParameterizedBeanPropertyRowMapper
						.newInstance(LotteryInfoNew.class));
		if (lotteryInfoNewList.size() > 0) {
			return lotteryInfoNewList.get(0);
		}
		return null;
	}


}
