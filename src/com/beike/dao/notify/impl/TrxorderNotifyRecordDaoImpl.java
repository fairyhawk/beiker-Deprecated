package com.beike.dao.notify.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.NotifyType;
import com.beike.common.enums.trx.TrxBizType;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.notify.TrxorderNotifyRecordDao;
import com.beike.entity.notify.TrxorderNotifyRecord;
import com.beike.util.EnumUtil;


/**
 * 短信提醒通知记录实现
 * @author yurenli
 *
 */
@Repository("trxorderNotifyRecordDao")
public class TrxorderNotifyRecordDaoImpl extends GenericDaoImpl<TrxorderNotifyRecord, Long> implements TrxorderNotifyRecordDao{

	@Override
	public Long addTrxorderNotifyRecord(TrxorderNotifyRecord tnr) {
		if (tnr == null) {

			throw new IllegalArgumentException();
		}

		String istSql = "insert beiker_trxorder_notify_record(user_id,create_date,notify_date,notify_type,biz_type,is_notify,express,version,description)"
				+ "value(?,?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(istSql,
				tnr.getUserId(),  tnr.getCreateDate(),
				tnr.getNotifyDate(),
				EnumUtil.transEnumToString(tnr.getNotifyType()),
				EnumUtil.transEnumToString(tnr.getBizType()),
				tnr.isNotify(),tnr.getExpress(), tnr.getVersion(), tnr.getDescription()
				);
		Long actId = getLastInsertId();
		return actId;

	}

	@Override
	public TrxorderNotifyRecord findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,user_id,create_date,notify_date,notify_type,biz_type,is_notify,express,version,description from beiker_trxorder_notify_record where id = ?";
			List<TrxorderNotifyRecord> tnr = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);

			if (tnr!=null&&tnr.size() > 0) {
				return tnr.get(0);
			}
			return null;
		}
	}

	@Override
	public List<TrxorderNotifyRecord> findByIsNotify(boolean isNotify,int start,int daemonLength) {

			String sql = "select id,user_id,create_date,notify_date,notify_type,biz_type,is_notify,express,version,description from beiker_trxorder_notify_record where is_notify = ? limit ?,?";
			List<TrxorderNotifyRecord> tnr = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(),isNotify,start,daemonLength);

			return tnr;
	}
	
	@Override
	public int findByIsNotifyCount(boolean isNotify) {

			String sql = "select count(1) as count from beiker_trxorder_notify_record where is_notify = ? ";
			Long resultCount = 0L;
			
			List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql,isNotify);

			if (list != null && list.size() > 0) {

					resultCount = (Long) list.get(0).get("count");
				}
				return resultCount.intValue();
	}

	@Override
	public void updateAccountNotifyById(Long id,boolean result) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("update  beiker_trxorder_notify_record set notify_date=?,is_notify=?");
		sb.append(" where id = ?");
		
		getSimpleJdbcTemplate().update(sb.toString(),
				new Date(),result,id);
		
	}

	
	protected class RowMapperImpl implements
	ParameterizedRowMapper<TrxorderNotifyRecord> {
		public TrxorderNotifyRecord mapRow(ResultSet rs, int rowNum)
		throws SQLException {
	TrxorderNotifyRecord tnr = new TrxorderNotifyRecord();
	tnr.setId(rs.getLong("id"));
	tnr.setUserId(rs.getLong("user_id"));
	tnr.setNotifyDate(rs.getTimestamp("notify_date"));
	tnr.setCreateDate(rs.getTimestamp("create_date"));
	tnr.setNotifyType(EnumUtil.transStringToEnum(NotifyType.class, rs
			.getString("notify_type")));
	tnr.setBizType(EnumUtil.transStringToEnum(TrxBizType.class, rs
			.getString("biz_type")));
	tnr.setExpress(rs.getString("express"));
	tnr.setNotify(rs.getBoolean("is_notify"));
	tnr.setVersion(rs.getLong("version"));
	tnr.setDescription(rs.getString("description"));
	return tnr;
}
}
}
