package com.beike.dao.businessbackground.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.businessbackground.VipStatisticsDao;
import com.beike.util.DateUtils;

@Repository("vipStatisticsDao")
public class VipStatisticsDaoImpl extends GenericDaoImpl implements VipStatisticsDao {

	@Override
	public List<Map<String, Object>> queryTrxOrderInfo(String dateStart,String dateEnd) {
		String startTime = dateStart+" 00:00:00";
		String endTime = dateEnd+" 00:00:00";
		StringBuilder sql = new StringBuilder();
		sql.append("select bt.user_id,bg.guest_id,bg.goods_id,bt.close_date as vip_time from beiker_trxorder_goods bg join beiker_trxorder bt ");
		sql.append("on bg.trxorder_id=bt.id where bg.trx_status!='INIT' and bt.close_date>=? ");
		sql.append("and bt.close_date<? group by bt.user_id,bg.guest_id");
		return this.getSimpleJdbcTemplate().queryForList(sql.toString(), startTime,endTime);
	}
	
	@Override
	public int queryVipStatistics(Long guestId) {
		String sql ="select count(*) from beiker_vip_statistics where guest_id=?";
		return this.getSimpleJdbcTemplate().queryForInt(sql,guestId);
	}
	
	@Override
	public void addVipStatitics(Map<String, Object> vipInfo) {
		if(vipInfo!=null){
			String insertSql = "insert into beiker_vip_statistics (user_id,guest_id,goods_id,vip_time,create_time) values (?,?,?,?,now())";
			Object []parmas = {vipInfo.get("user_id"),vipInfo.get("guest_id"),vipInfo.get("goods_id"),vipInfo.get("vip_time")};
			this.getSimpleJdbcTemplate().update(insertSql, parmas);
		}
	}
	
	@Override
	public int queryVipOfGuest(Long userId, Long guestId) {
		String querySql = "select count(id) from beiker_vip_statistics where user_id=? and guest_id=?";
		return this.getSimpleJdbcTemplate().queryForInt(querySql, userId,guestId);
	}
	
	@Override
	public int queryOnlineVipCount() {
		String selectSql = "select id as count from beiker_vip_statistics";
		return this.getSimpleJdbcTemplate().queryForInt(selectSql);
	}
	
	@Override
	public int queryOfflineVipCount() {
		return 0;
	}
	
	@Override
	public Map<String,Object> queryVipInfoByMonth(String date) {
		String querySql ="select vip_num from beiker_vip_statistics_month where date_time=?";
		return this.getSimpleJdbcTemplate().queryForMap(querySql, date);
	}
	
	@Override
	public void insertVipInfoByMonth(String date,Long vipNum,Long guest_id) {
		String insertSql ="insert into beiker_vip_statistics_month (vip_num,guest_id,date_time,update_time) values (?,?,?,now())";
		this.getSimpleJdbcTemplate().update(insertSql,vipNum,guest_id,date);
	}
	
	@Override
	public void updateVipInfoByMonth(String date,Long vipNum,Long guest_id) {
		String updateSql = "update beiker_vip_statistics_month set vip_num=?,update_time=now() where date_time=? and guest_id=?";
		this.getSimpleJdbcTemplate().update(updateSql,vipNum,date,guest_id);
	}
	
	@Override
	public int queryVipNumByMonth(String date,Long guestId) {
		String querySql = "select sum(vip_num) from beiker_vip_statistics_month where date_time>=? and guest_id=?";
		return this.getSimpleJdbcTemplate().queryForInt(querySql, date,guestId);
	}
	
	@Override
	public List<Map<String, Object>> queryGuestInfoByDate(String date) {
		String querySql ="select guest_id from beiker_vip_statistics_month where date_time=? ";
		return this.getSimpleJdbcTemplate().queryForList(querySql, date);
	}
	
	@Override
	public int queryBuyActivityByUserIds(Long guestId, String date,String userIds) {
		String querySql = "select count(distinct bt.user_id) from beiker_trxorder_goods bg join beiker_trxorder bt on bg.trxorder_id=bt.id where bt.close_date>=? and bt.trx_status='SUCCESS' and bg.guest_id=? and bt.user_id in("+userIds+")";
		return this.getSimpleJdbcTemplate().queryForInt(querySql,date,guestId);
	}
	
	@Override
	public List<Map<String, Object>> queryVipInfoByDate(Long guestId, String date) {
		String querySql = "select user_id from beiker_vip_statistics where guest_id=? and vip_time<? ";
		return this.getSimpleJdbcTemplate().queryForList(querySql,guestId,date);
	}
	
	@Override
	public List<Map<String, Object>> queryVipNumForMonthByDate(String endDate,Long guestId) {
		String querySql = "select vip_num,date_time from beiker_vip_statistics_month where guest_id=? and date_time<=? order by date_time asc";
		return this.getSimpleJdbcTemplate().queryForList(querySql, guestId,endDate);
	}
	
	@Override
	public List<Map<String, Object>> queryVipStatisticsByMonth(String thismonth,String nextMonth) {
		thismonth = thismonth + "-01 00:00:00";
		nextMonth = nextMonth + "-01 00:00:00";
		String querySql = "select count(user_id) as vip_num,guest_id from beiker_vip_statistics where vip_time>=? and vip_time<? group by guest_id";
		return this.getSimpleJdbcTemplate().queryForList(querySql, thismonth,nextMonth);
	}

	@Override
	public List<Map<String, Object>> queryTuanGouDetail(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select btg.goods_id,btg.biz_type,btg.trx_status,bg.goodsname,bg.couponcash ");
		sql.append(" from beiker_trxorder_goods btg ");
		sql.append(" join beiker_goods bg on btg.goods_id = bg.goodsid");
		sql.append(" where btg.guest_id = ? ");
		sql.append(" and btg.create_date between ? and ? ");
		sql.append(" and btg.trx_status not in ('" + TrxStatus.INIT + "')");
		sql.append(" and btg.biz_type = 0 ");
		sql.append(" order by btg.goods_id desc ");
		paramList.add(params.get("guest_id"));
		paramList.add(params.get("startDate"));
		paramList.add(params.get("endDate"));
		return this.getSimpleJdbcTemplate().queryForList(sql.toString(), paramList.toArray(new Object[]{}));
	}

	@Override
	public int queryTotalCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select count(0) ");
		sql.append(" from beiker_trxorder_goods btg ");
		sql.append(" where btg.guest_id = ? ");
		sql.append(" and btg.create_date between ? and ? ");
		sql.append(" and btg.trx_status not in ('" + TrxStatus.INIT + "')");
		sql.append(" and btg.biz_type = ? ");
		paramList.add(params.get("guest_id"));
		paramList.add(params.get("startDate"));
		paramList.add(params.get("endDate"));
		paramList.add(params.get("isMenu"));
		return this.getSimpleJdbcTemplate().queryForInt(sql.toString(), paramList.toArray(new Object[]{}));
	}
	
	@Override
	public int queryNewVipCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select count(0) ");
		sql.append(" from beiker_vip_statistics btg ");
		sql.append(" join beiker_goods bg on btg.goods_id = bg.goodsid");
		sql.append(" where btg.guest_id = ? ");
		sql.append(" and btg.vip_time between ? and ? ");
		sql.append(" and bg.is_menu = ? ");
		paramList.add(params.get("guest_id"));
		paramList.add(params.get("startDate"));
		paramList.add(params.get("endDate"));
		paramList.add(params.get("isMenu"));
		return this.getSimpleJdbcTemplate().queryForInt(sql.toString(), paramList.toArray(new Object[]{}));
	}

	@Override
	public int queryOldVipCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select bt.user_id ");
		sql.append(" from beiker_trxorder_goods btg ");
		sql.append(" join beiker_trxorder bt on btg.trxorder_id = bt.id ");
		sql.append(" where btg.guest_id = ? ");
		sql.append(" and btg.create_date between ? and ? ");
		sql.append(" and btg.trx_status not in ('" + TrxStatus.INIT + "')");
		sql.append(" and btg.biz_type = ? ");
		paramList.add(params.get("guest_id"));
		paramList.add(params.get("startDate"));
		paramList.add(params.get("endDate"));
		paramList.add(params.get("isMenu"));
		SqlRowSet srs = this.getJdbcTemplate().queryForRowSet(sql.toString(),paramList.toArray(new Object[]{}));
		Set<Long> result = new HashSet<Long>();
		while(srs.next()){
			result.add(srs.getLong("user_id"));
		}
		return result.size();
	}
}
