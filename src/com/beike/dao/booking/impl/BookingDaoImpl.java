package com.beike.dao.booking.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.booking.BookingDao;
import com.beike.entity.booking.BookingFormVO;
import com.beike.entity.booking.BookingInfo;
import com.beike.entity.booking.BookingLog;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;

@Repository("bookingDao")
public class BookingDaoImpl  extends GenericDaoImpl implements BookingDao {

	@Override
	public Long isValidOrder(List<Long> trxgoods_ids,BookingFormVO bfv) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(btg.id) FROM beiker_trxorder_goods btg JOIN beiker_trxorder bt ON btg.trxorder_id=bt.id WHERE btg.trx_status=? AND bt.user_id=?");
				if(StringUtils.validNull(bfv.getScheduled_consumption_datetime())){
					sql.append(" AND  DATE(btg.order_lose_date) >=").append("'")
					.append(DateUtils.toString(DateUtils.toDate(bfv.getScheduled_consumption_datetime(), "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd"))
					.append("'");
					
				}
				
				sql.append(" AND btg.id IN(").append(StringUtils.arrayToString(trxgoods_ids.toArray(), ","))
				.append(")");
		return getJdbcTemplate().queryForLong(sql.toString(),new Object[]{TrxStatus.SUCCESS.toString(),bfv.getCreateucid()});
	}

	@Override
	public List<Map> getOrdersByTrxorderidAndGoodsid(Long userid,Long trxorder_id,Long goodsid) {
		List<Long> trxgoods_ids = getUserBookedTrxgoodsID(userid, goodsid);
		String sql = "";
		if(trxgoods_ids != null && trxgoods_ids.size()>0){
			 sql = "SELECT btg.id FROM beiker_trxorder_goods btg WHERE btg.trxorder_id=? AND btg.goods_id=? AND  btg.trx_status=? AND btg.id NOT IN(" + StringUtils.arrayToString(trxgoods_ids.toArray(), ",") +  ") ORDER BY btg.id ASC";
		}else{
			 sql = "SELECT btg.id FROM beiker_trxorder_goods btg WHERE btg.trxorder_id=? AND btg.goods_id=? AND  btg.trx_status=? ORDER BY btg.id ASC";

		}
		return getJdbcTemplate().queryForList(sql, new Object[]{trxorder_id,goodsid,TrxStatus.SUCCESS.toString()});

		
	}

	@Override
	public Long getBookedTotal(BookingFormVO bfv) {
		String sql = "SELECT COUNT(id) FROM beiker_scheduled_application_form bsaf WHERE DATE(bsaf.scheduled_consumption_datetime)=? AND bsaf.branch_id=? AND bsaf.`status`='1' FOR UPDATE";
		return getJdbcTemplate().queryForLong(sql,new Object[]{DateUtils.strToDate(bfv.getScheduled_consumption_datetime()),bfv.getBranch_id()});
	}

	@Override
	public int saveBookingInfo(final List<BookingInfo> bookingInfos) {
		String sql = "INSERT INTO beiker_scheduled_application_form SET " +
				"goods_id=?,branch_id=?,guest_id=?,trx_id=?,person=?,phone=?,message=?," +
				"`status`=?,scheduled_consumption_datetime=?," +
				"createucid=?,createtime=?,updatetype=?,updatetime=?";
		int[] rows = getJdbcTemplate().batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				BookingInfo bi = bookingInfos.get(i);
				ps.setLong(1,bi.getGoods_id());
				ps.setLong(2,bi.getBranch_id());
				ps.setLong(3,bi.getGuest_id());
				ps.setLong(4,bi.getTrx_id());
				ps.setString(5,bi.getPerson());
				ps.setString(6,bi.getPhone());
				ps.setString(7,bi.getMessage());
				ps.setString(8,bi.getStatus());
				ps.setString(9,bi.getScheduled_consumption_datetime());
				ps.setLong(10,bi.getCreateucid());
				ps.setString(11,bi.getCreatetime());
				ps.setString(12,bi.getUpdatetype());
				ps.setString(13,bi.getCreatetime());
			}
			
			@Override
			public int getBatchSize() {
				return bookingInfos.size();
			}
		});
		return rows.length;
	}

	@Override
	public int saveBookingLoginfo(final List<BookingLog> bookinglogs) {
		String sql = "INSERT INTO beiker_scheduled_application_log SET scheduled_id=?,`status`=?,createucid=?," +
				"createtype=?,remark=?";
		int[] rows = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				BookingLog bl = bookinglogs.get(i);
				ps.setLong(1, bl.getScheduled_id());
				ps.setString(2, bl.getStatus());
				ps.setLong(3, bl.getCreateucid());
				ps.setInt(4, bl.getCreatetype());
				ps.setString(5, bl.getRemark());
			}
			
			@Override
			public int getBatchSize() {
				return bookinglogs.size();
			}
		});
		
		return rows.length;
	}


	@Override
	public List<Map> getGuestidByTrxGoodsid(List<Long> trxgoods_ids) {
		String sql = "SELECT btg.id,btg.guest_id FROM beiker_trxorder_goods btg WHERE btg.id IN(" + StringUtils.arrayToString(trxgoods_ids.toArray(), ",") + ")";
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map> getScheduledForm(List<Long> trxgoods_id) {
		String sql = "SELECT bsaf.id,bsaf.message,bsaf.`status`,bsaf.createtime,bsaf.createucid FROM beiker_scheduled_application_form bsaf " +
				"WHERE bsaf.trx_id IN(" + StringUtils.arrayToString(trxgoods_id.toArray(), ",")  +  ")";
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public Map findBytrxgoodsId(String trxgoods_id) {
		String sql = "SELECT bsaf.id,bsaf.message,bsaf.`status`,bsaf.createtime,bsaf.createucid FROM beiker_scheduled_application_form bsaf " +
				"WHERE bsaf.trx_id ="+trxgoods_id+" ORDER BY bsaf.id desc limit 1";
		 List<Map> list = getJdbcTemplate().queryForList(sql);
		 if(list!=null&&list.size()>0){
		return list.get(0);
		 }else{
			 return null;
		 }
	}
	@Override
	public List<Map> getBranchInfoByGoodsid(Long goodsid) {
		String sql = "SELECT DISTINCT bm.merchantname,bm.addr,bm.merchantid FROM beiker_goods_merchant bgm JOIN beiker_merchant bm ON bm.merchantid=bgm.merchantid WHERE bgm.goodsid=? AND bm.parentId != 0 AND bm.scheduled_count > 0";
		
		return getJdbcTemplate().queryForList(sql,new Object[]{goodsid});
	}

	@Override
	public List<Map> getAvailableGoods(Long goodsid) {
		String sql = "SELECT bg.goodsid,bg.goods_title FROM beiker_goods bg WHERE bg.goodsid=? AND is_scheduled='1'";
		return getJdbcTemplate().queryForList(sql, new Object[]{goodsid});
	}

	@Override
	public int cancelBooking(BookingInfo bi) {
		
		int rows = 0;
		String cacelBookingsql = "UPDATE beiker_scheduled_application_form bsaf SET bsaf.`status`='3',bsaf.updateucid=?,bsaf.updatetime=NOW() WHERE bsaf.id=? AND bsaf.`status` IN('0','1','4')";
		rows = getJdbcTemplate().update(cacelBookingsql, new Object[]{bi.getUpdateucid(),bi.getId()});
		
		return rows;

	}

	@Override
	public List<Map> getBookingRecordByID(Long bookingid,Long userid) {
		String sql = "SELECT bsaf.goods_id,bsaf.branch_id,bsaf.id,bsaf.message,bsaf.createucid,bsaf.status,bsaf.proposal_consumption_datetime,bsaf.trx_id,bsaf.phone,bsaf.person,bsaf.scheduled_consumption_datetime,branch_id,bg.goods_title,bm.merchantname FROM beiker_scheduled_application_form bsaf JOIN beiker_goods bg ON bg.goodsid=bsaf.goods_id JOIN beiker_merchant bm ON bm.merchantid=bsaf.branch_id WHERE bsaf.id=? AND bsaf.createucid=? ORDER BY bsaf.createtime DESC LIMIT 1";
		return getJdbcTemplate().queryForList(sql, new Object[]{bookingid,userid});
	}

	@Override
	public Long isBooked(List<Long> trxgoods_ids) {
		String sql = "SELECT COUNT(id) FROM beiker_scheduled_application_form bsaf WHERE bsaf.status IN('0','1') AND bsaf.trx_id IN("
				+ StringUtils.arrayToString(trxgoods_ids.toArray(), ",") + ")";
		return getJdbcTemplate().queryForLong(sql);
	}

	@Override
	public Long getAvailableBookingTotal(Long branchid) {
		String sql = "SELECT bm.scheduled_count FROM beiker_merchant bm WHERE bm.merchantid=? AND bm.parentId !=0";
		return getJdbcTemplate().queryForLong(sql,new Object[]{branchid});
	}

	@Override
	public int reBooking(BookingInfo bi){
		
		int rows = 0;
	    String sql = "SELECT COUNT(id) FROM beiker_scheduled_application_form WHERE id=? AND createucid=? AND `status` = '2'";
		rows = getJdbcTemplate().queryForInt(sql, new Object[]{bi.getId(),bi.getCreateucid()});
		return rows;
	}

	@Override
	public Long getBranchByBranchid(Long branchid) {
		String sql = "SELECT COUNT(merchantid) FROM beiker_merchant bm WHERE bm.merchantid=? AND bm.parentId != 0 AND bm.scheduled_count > 0";
		return getJdbcTemplate().queryForLong(sql,new Object[]{branchid});
	}

	@Override
	public List<Map> getBookingRecordByTrxgoodsID(List<Long> trxgoods_ids) {
		String sql = "SELECT bsaf.goods_id,bsaf.branch_id,bsaf.trx_id,bsaf.status FROM beiker_scheduled_application_form bsaf WHERE bsaf.trx_id IN(" + StringUtils.arrayToString(trxgoods_ids.toArray(), ",") + ")";
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map> getBranchPhone(List<Long> branchids) {
		String sql = "SELECT bbsp.branch_id,bbsp.phone,bbsp.is_send FROM beiker_branch_scheduled_phone bbsp WHERE bbsp.is_send='1' AND bbsp.branch_id IN(" + StringUtils.arrayToString(branchids.toArray(), ",") + ")";
		
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public List<Map> getBookingGoods(Long goodsid) {
		String sql = "SELECT bg.goodsid,bg.goods_title goodsTitle FROM beiker_goods bg WHERE bg.is_scheduled='1' AND bg.goodsid=?";
		return getJdbcTemplate().queryForList(sql,new Object[]{goodsid});
	}

	@Override
	public List<Long> getUserBookedTrxgoodsID(Long userid, Long goodsid) {
		String sql = "SELECT DISTINCT bsaf.trx_id FROM beiker_scheduled_application_form bsaf WHERE bsaf.`status` IN('0','1') AND bsaf.createucid=? AND bsaf.goods_id=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{userid,goodsid}, Long.class);
	}

	@Override
	public List<Map> showTip(Long bookingid, Long userid) {
		String sql = "SELECT booking.id, bookinglog.remark,booking.proposal_consumption_datetime FROM beiker_scheduled_application_form booking JOIN beiker_scheduled_application_log bookinglog ON booking.id=bookinglog.scheduled_id WHERE booking.id=? AND  booking.createucid=? AND booking.`status`='2' AND bookinglog.`status`='2' LIMIT 1";
		return getJdbcTemplate().queryForList(sql, new Object[]{bookingid,userid});
	}

	@Override
	public Long saveBookingInfo(BookingInfo bookingInfo) {
		String sql = "INSERT INTO beiker_scheduled_application_form SET "
				+ "goods_id=?,branch_id=?,guest_id=?,trx_id=?,person=?,phone=?,message=?,"
				+ "`status`=?,scheduled_consumption_datetime=?,"
				+ "createucid=?,createtime=?,updatetype=?,updatetime=?,updateucid=?";
		int rows = getJdbcTemplate().update(
				sql,
				new Object[] { bookingInfo.getGoods_id(),
						bookingInfo.getBranch_id(), bookingInfo.getGuest_id(),
						bookingInfo.getTrx_id(), bookingInfo.getPerson(),
						bookingInfo.getPhone(), bookingInfo.getMessage(),
						bookingInfo.getStatus(),
						bookingInfo.getScheduled_consumption_datetime(),
						bookingInfo.getCreateucid(),
						bookingInfo.getCreatetime(),
						bookingInfo.getUpdatetype(),
						bookingInfo.getUpdatetime(),
						bookingInfo.getUpdateucid()});
		if(rows == 1){
			return getLastInsertId();
		}
		return null;
		
	}
	

}
