package com.beike.dao.notify.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.NotifyType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.notify.AccountNotifyRecordDao;
import com.beike.entity.notify.AccountNotifyRecord;
import com.beike.util.EnumUtil;

/**
 * @author yurenli 2012-2-10 13:50:48
 */
@Repository("accountNotifyRecordDao")
public class AccountNotifyRecordDaoImpl extends
		GenericDaoImpl<AccountNotifyRecord, Long> implements
		AccountNotifyRecordDao {

	@Override
	public Long addAccountNotifyRecord(AccountNotifyRecord anr) {
		if (anr == null) {

			throw new IllegalArgumentException();
		}

		String istSql = "insert beiker_account_notify_record(account_id,user_id,sub_account_id,create_date,notify_date,lose_balance,notify_type,is_notify,version,description,lose_date)"
				+ "value(?,?,?,?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(istSql, anr.getAccountId(),
				anr.getUserId(), anr.getSubAccountId(), anr.getCreateDate(),
				anr.getNotifyDate(), anr.getLoseBalance(),
				EnumUtil.transEnumToString(anr.getNotifyType()),
				anr.isNotify(), anr.getVersion(), anr.getDescription(),
				anr.getLoseDate());
		Long actId = getLastInsertId();
		return actId;

	}

	@Override
	public AccountNotifyRecord findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,account_id,user_id,sub_account_id,create_date,notify_date,lose_balance,notify_type,is_notify,version,description,lose_date from beiker_account_notify_record where id = ?";
			List<AccountNotifyRecord> anr = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);

			if (anr.size() > 0) {
				return anr.get(0);
			}
			return null;
		}
	}

	@Override
	public List<AccountNotifyRecord> findByUserId(Long userId, NotifyType type) {

		if (userId == null) {
			return null;
		} else {
			String sql = "select id,account_id,user_id,sub_account_id,create_date,notify_date,lose_balance,notify_type,is_notify,version,description,lose_date from beiker_account_notify_record where is_notify = ? and user_id = ? and notify_type = ?";
			List<AccountNotifyRecord> anr = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), false, userId, type.name());

			if (anr.size() > 0) {
				return anr;
			}
			return null;
		}
	}

	
	@Override
	public int findCountByIsNotify(boolean isNotify) {

		String sql = "select count(1) as count from beiker_account_notify_record where is_notify = ?";

		Long resultCount = 0L;
		
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql,isNotify);

		if (list != null && list.size() > 0) {

				resultCount = (Long) list.get(0).get("count");
			}
			return resultCount.intValue();
	} 
	
	@Override
	public List<AccountNotifyRecord> findByIsNotify(boolean isNotify,int start,int length) {

		String sql = "select id,account_id,user_id,sub_account_id,create_date,notify_date,lose_balance,notify_type,is_notify,version,description,lose_date from beiker_account_notify_record where is_notify = ? limit ?,?";
		List<AccountNotifyRecord> anr = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), isNotify,start,length);

		if (anr.size() > 0) {
			return anr;
		}
		return null;
	} 

	@Override
	public AccountNotifyRecord findByS(Long accountId, Long subAccountId,
			NotifyType type) {

		if (accountId == null || subAccountId == null) {
			return null;
		} else {
			String sql = "select id,account_id,user_id,sub_account_id,create_date,notify_date,lose_balance,notify_type,is_notify,version,description,lose_date from beiker_account_notify_record"
					+ " where account_id = ? and sub_account_id = ? and notify_type = ?";
			List<AccountNotifyRecord> anr = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), accountId, subAccountId, type.name());

			if (anr.size() > 0) {
				return anr.get(0);
			}
			return null;
		}
	}

	@Override
	public void updateAccountNotifyRecord(AccountNotifyRecord anr)
			throws StaleObjectStateException {

		if (anr == null) {
			return;
		} else {

			String sql = "update  beiker_account_notify_record set notify_date=?,is_notify=?,version=?"
					+ " where id=? and version=?";
			int result = getSimpleJdbcTemplate().update(sql,
					anr.getNotifyDate(), anr.isNotify(), anr.getVersion() + 1,
					anr.getId(), anr.getVersion());

			if (result == 0) {
				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}

		}

	}
	@Override
	public void updateAccountNotifyById(String idStr){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("update  beiker_account_notify_record set notify_date=?,is_notify=?");
		sb.append(" where id in(");
		sb.append(idStr);
		sb.append(")");
		
		getSimpleJdbcTemplate().update(sb.toString(),
				new Date(),true);
		
	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<AccountNotifyRecord> {
		public AccountNotifyRecord mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			AccountNotifyRecord anr = new AccountNotifyRecord();
			anr.setId(rs.getLong("id"));
			anr.setAccountId(rs.getLong("account_id"));
			anr.setUserId(rs.getLong("user_id"));
			anr.setSubAccountId(rs.getLong("sub_account_id"));
			anr.setNotifyDate(rs.getTimestamp("notify_date"));
			anr.setLoseBalance(rs.getDouble("lose_balance"));
			anr.setCreateDate(rs.getTimestamp("create_date"));
			anr.setNotifyType(EnumUtil.transStringToEnum(NotifyType.class, rs
					.getString("notify_type")));
			anr.setNotify(rs.getBoolean("is_notify"));
			anr.setVersion(rs.getLong("version"));
			anr.setDescription(rs.getString("description"));
			anr.setLoseDate(rs.getTimestamp("lose_date"));
			return anr;
		}
	}

}
