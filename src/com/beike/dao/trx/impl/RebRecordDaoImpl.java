package com.beike.dao.trx.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.RebRecord;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.RebRecordDao;
import com.beike.util.EnumUtil;

/**
 * @Title: RebRecordDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: TODO
 * @date May 9, 2011 2:23:23 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("rebRecordDao")
public class RebRecordDaoImpl extends GenericDaoImpl<RebRecord, Long> implements
		RebRecordDao {

	public void addRebRecord(RebRecord rebRecord) {
		if (rebRecord.getId() != null) {
			update(rebRecord);
		} else {

			String istSql = "insert beiker_rebrecord(request_id,external_id,order_type,trx_status,close_date,user_id,extend_info,create_date,trx_amount,biz_id) value(?,?,?,?,?,?,?,?,?,?)";
			getSimpleJdbcTemplate().update(istSql, rebRecord.getRequestId(),
					rebRecord.getExternalId(),
					EnumUtil.transEnumToString(rebRecord.getOrderType()),
					EnumUtil.transEnumToString(rebRecord.getTrxStatus()),
					rebRecord.getCloseDate(), rebRecord.getUserId(),
					rebRecord.getExtendInfo(), rebRecord.getCreateDate(),
					rebRecord.getTrxAmount(), rebRecord.getBizId());

		}
	}

	public RebRecord findById(Long id) {
		if (id == null) {

			return null;
		} else {
			String qrySql = "select id,user_id,biz_id,close_date,create_date,trx_amount,request_id,external_id,trx_status,order_type,extend_info from beiker_rebrecord where id=?";

			return getSimpleJdbcTemplate().queryForObject(qrySql,
					new RowMapperImpl(), id);
		}

	}

	public void update(RebRecord rebRecord) {
		if (rebRecord == null) {

			return;
		} else {

			String upSql = "update beiker_accounhistory set request_id=?,external_id=?,"
					+ "order_type=?,trx_status=?,close_date=?,user_id=?,extend_info=?,create_date=?,trx_amount=?,biz_id=? where id=?";
			getSimpleJdbcTemplate().update(upSql, rebRecord.getRequestId(),
					rebRecord.getExternalId(),
					EnumUtil.transEnumToString(rebRecord.getOrderType()),
					EnumUtil.transEnumToString(rebRecord.getTrxStatus()),
					rebRecord.getCloseDate(), rebRecord.getUserId(),
					rebRecord.getExtendInfo(), rebRecord.getCreateDate(),
					rebRecord.getTrxAmount(), rebRecord.getBizId(),
					rebRecord.getId());
		}

	}

	protected class RowMapperImpl implements ParameterizedRowMapper<RebRecord> {
		public RebRecord mapRow(ResultSet rs, int num) throws SQLException {
			RebRecord rebRecord = new RebRecord();
			rebRecord.setId(rs.getLong("id"));
			rebRecord.setRequestId(rs.getString("request_id"));
			rebRecord.setExternalId(rs.getString("external_id"));
			rebRecord.setOrderType(EnumUtil.transStringToEnum(OrderType.class,
					rs.getString("order_type")));
			rebRecord.setTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class,
					rs.getString("trx_status")));
			rebRecord.setCloseDate(rs.getTimestamp("close_date"));
			rebRecord.setUserId(rs.getLong("user_id"));
			rebRecord.setExtendInfo(rs.getString("extend_info"));
			rebRecord.setCreateDate(rs.getTimestamp("create_date"));
			rebRecord.setTrxAmount(rs.getDouble("trx_amount"));
			rebRecord.setBizId(rs.getLong("biz_id"));

			return rebRecord;
		}
	}

	public RebRecord findByExternalId(String externalId) {
		if (externalId == null) {

			return null;
		} else {
			String qrySql = "select id,user_id,biz_id,close_date,create_date,trx_amount,request_id,external_id,trx_status,order_type,extend_info from beiker_rebrecord where external_id=?";

			List<RebRecord> rebList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), externalId);
			if (rebList.size() > 0) {
				return rebList.get(0);
			}
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public double findSucSumByUserId(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userid_not_null");
		} else {

			String qrySql = "select sum(trx_amount) as sumAmount from beiker_rebrecord where trx_status='SUCCESS' and user_id=?";
			Map sumMap = getSimpleJdbcTemplate().queryForMap(qrySql, userId);
			if (!sumMap.isEmpty()) {
				BigDecimal sumAmount = (BigDecimal) sumMap.get("sumAmount");
				if (sumAmount != null) {
					return sumAmount.doubleValue();
				}
			}
		}
		return 0.0;

	}

	public List<RebRecord> findListByList(Long userId, int startRow,
			int pageSize) {

		if (userId == null) {

			throw new IllegalArgumentException("userid_not_null");
		} else {
			String qrySql = "select id,user_id,biz_id,close_date,create_date,trx_amount,request_id,external_id,trx_status,order_type,extend_info from beiker_rebrecord where trx_status='SUCCESS' and  user_id=? order by id desc limit ?,?";

			List<RebRecord> rebList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), userId, startRow, pageSize);
			if (rebList.size() > 0) {
				return rebList;
			}
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public int findRowCountByUserIdAndStatus(Long userId, String status) {

		if (userId == null || status == null || "".equals(status)) {
			throw new IllegalArgumentException("userid_not_null");
		} else {

			String qrySql = "select count(1) as count from beiker_rebrecord where trx_status='"
					+ status + "' and user_id=?";
			Map sumMap = getSimpleJdbcTemplate().queryForMap(qrySql, userId);
			if (!sumMap.isEmpty()) {
				return ((Long) sumMap.get("count")).intValue();
			}
		}
		return 0;

	}

	public void updateStatusByExId(String externalId, TrxStatus trxStatus,
			Date closeDate) {

		if (externalId == null || trxStatus == null) {
			throw new IllegalArgumentException(
					"externalId and trxStatus is not null");
		}
		String upSql = "update beiker_rebrecord set trx_status=? ,close_date=?  where external_id=? ";

		getSimpleJdbcTemplate().update(upSql, trxStatus.name(), closeDate,
				externalId);
	}

	public RebRecord findRebByreqIdAndType(String requestId, String orderType) {

		if (requestId == null || orderType == null) {

			throw new IllegalArgumentException(
					"requestId and orderType is not_null");
		}

		String qrySql = "select id,user_id,biz_id,close_date,create_date,trx_amount,request_id,external_id,trx_status,order_type,extend_info from beiker_rebrecord where request_id=? and order_type=?";

		List<RebRecord> rebList = getSimpleJdbcTemplate().query(qrySql,
				new RowMapperImpl(), requestId, orderType);
		if (rebList.size() > 0) {
			return rebList.get(0);
		}
		return null;

	}

}