package com.beike.dao.vm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.vm.SubAccount;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.vm.SubAccountDao;
import com.beike.util.DateUtils;

@Repository("subAccountDao")
public class SubAccountDaoImpl extends GenericDaoImpl<SubAccount, Long>
		implements SubAccountDao {

	@Override
	public Long addSubAccount(final SubAccount subAccount, String subSuffix) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (subAccount == null) {
			throw new IllegalArgumentException("settleApplyRecord is null");

		}

		final StringBuffer sb = new StringBuffer();
		sb
				.append("insert into  beiker_sub_account_"
						+ subSuffix
						+ "(user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date)");
		sb.append(" value(?,?,?,?,?,?,?)");

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				PreparedStatement ps = con.prepareStatement(sb.toString(),
						new String[] { "user_id", "account_id",
								"vm_account_id", "balance", "create_date",
								"update_date", "lose_date" });

				ps.setLong(1, subAccount.getUserId());
				ps.setLong(2, subAccount.getAccountId());
				ps.setLong(3, subAccount.getVmAccountId());
				ps.setDouble(4, subAccount.getBalance());
				ps.setTimestamp(5, new Timestamp(subAccount.getCreateDate()
						.getTime()));
				ps.setTimestamp(6, new Timestamp(subAccount.getUpdateDate()
						.getTime()));
				ps.setTimestamp(7, new Timestamp(subAccount.getLoseDate()
						.getTime()));

				return ps;
			}
		}, keyHolder);
		Long subAccountId = keyHolder.getKey().longValue();
		return subAccountId;
	}

	@Override
	public void updateSubAccount(SubAccount subAccount, String subSuffix)
			throws StaleObjectStateException {
		if (subAccount == null) {
			return;
		} else {

			String sql = "update  beiker_sub_account_"
					+ subSuffix
					+ " set version=?,user_id=?,account_id=?,vm_account_id=?,balance=?,create_date=?"
					+ ",update_date=?,lose_date=?,is_lose=? where id=? and version = ?";
			int result = getSimpleJdbcTemplate().update(sql,
					subAccount.getVersion() + 1, subAccount.getUserId(),
					subAccount.getAccountId(), subAccount.getVmAccountId(),
					subAccount.getBalance(), subAccount.getCreateDate(),
					subAccount.getUpdateDate(), subAccount.getLoseDate(),
					subAccount.isLose(), subAccount.getId(),
					subAccount.getVersion());
			if (result == 0) {
				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}
	}

	@Override
	public SubAccount findById(Long id, String subSuffix) {

		if (id == null || id.longValue() == 0 || subSuffix == null
				|| subSuffix.length() == 0) {

			throw new IllegalArgumentException();
		}

		String sql = "select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_"
				+ subSuffix + " where id = ? ";
		List<SubAccount> vmAccountHistoryList = getSimpleJdbcTemplate().query(
				sql, new RowMapperImpl(), id);

		if (vmAccountHistoryList != null && vmAccountHistoryList.size() > 0) {
			return vmAccountHistoryList.get(0);
		}
		return null;

	}

	/**
	 * 根据主键主账户id，表后缀以及虚拟款项ID查询子账户实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public SubAccount findByActIdAndVmId(Long actId, Long vmId, String subSuffix) {

		if (actId == null || actId.longValue() == 0 || subSuffix == null
				|| subSuffix.length() == 0 || vmId == null
				|| vmId.longValue() == 0) {

			throw new IllegalArgumentException();
		}

		String sql = "select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_"
				+ subSuffix + " where  account_id = ? and vm_account_id=?";
		List<SubAccount> vmAccountHistoryList = getSimpleJdbcTemplate().query(
				sql, new RowMapperImpl(), actId, vmId);

		if (vmAccountHistoryList != null && vmAccountHistoryList.size() > 0) {
			return vmAccountHistoryList.get(0);
		}
		return null;

	}

	/**
	 * 根据个人账户账户查询子账户列表
	 * 
	 * @param id
	 * @param subSuffix
	 * @return
	 */
	@Override
	public List<SubAccount> findByActId(Long actId, String subSuffix) {

		if (actId == null || actId.longValue() == 0 || subSuffix == null
				|| subSuffix.length() == 0) {

			throw new IllegalArgumentException();
		}

		StringBuilder sbSql = new StringBuilder();
		sbSql.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_");
		sbSql.append(subSuffix);
		sbSql.append(" where   is_lose=0  and   account_id = ? order by lose_date  desc,id  desc");
		List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(sbSql.toString(), new RowMapperImpl(), actId);

		if (vmSubAccountList != null && vmSubAccountList.size() > 0) {
			return vmSubAccountList;
		}
		return null;

	}

	@Override
	public List<SubAccount> findByLose(Date date) {
		List<SubAccount> subList = new ArrayList<SubAccount>();
		for (int i = 0; i < 10; i++) {
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and lose_date<?");
			List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(
					sbSql.toString(), new RowMapperImpl(), date);
			if (vmSubAccountList != null && vmSubAccountList.size() > 0) {
				for (int j = 0; j < vmSubAccountList.size(); j++) {
					subList.add(vmSubAccountList.get(j));

				}
			}
		}
		if (subList.size() > 0) {
			return subList;
		}
		return null;
	}
	
	@Override
	public List<SubAccount> findByLose(Date date,int i,int startCount,int endCount) {
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and lose_date<? limit ?,?");
			List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(
					sbSql.toString(), new RowMapperImpl(), date,startCount,endCount);
			if (vmSubAccountList != null && vmSubAccountList.size() > 0) {
				return vmSubAccountList;
		}
		return null;
	}
	
	@Override
	public int findByLoseCount(Date date,int i) {
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select count(*) as count from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and lose_date<?");
			List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
			.queryForList(sbSql.toString(),
					date);
			Long resultCount = 0L;
	if (list != null && list.size() > 0) {

			resultCount = (Long) list.get(0).get("count");
		}
		return resultCount.intValue();
	}
	
	@Override
	public List<SubAccount> findByVmAccountId(Long accountId){
		List<SubAccount> subList = new ArrayList<SubAccount>();
		for (int i = 0; i < 10; i++) {
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and balance>0 and vm_account_id=?");
			List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(
					sbSql.toString(), new RowMapperImpl(),accountId);
			if (vmSubAccountList != null && vmSubAccountList.size() > 0) {
				for (int j = 0; j < vmSubAccountList.size(); j++) {
					subList.add(vmSubAccountList.get(j));
				}
			}
		}
		if (subList.size() > 0) {
			return subList;
		}
		return null;
	}
	
	@Override
	public List<SubAccount> findByVmAccountId(Long accountId,int i,int startCount,int endCount){
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date,lose_date,is_lose from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and balance>0 and vm_account_id=? limit ?,?");
			List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(
					sbSql.toString(), new RowMapperImpl(),accountId,startCount,endCount);
		if (vmSubAccountList!=null&&vmSubAccountList.size() > 0) {
			return vmSubAccountList;
		}
		return null;
	}
	
	@Override
	public int findByVmAccountIdCount(Long accountId,int i){
			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select count(*) as count from beiker_sub_account_");
			sbSql.append(i);
			sbSql.append(" where is_lose=0 and balance>0 and vm_account_id=?");
			List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
			.queryForList(sbSql.toString(),
					accountId);
			Long resultCount = 0L;
	if (list != null && list.size() > 0) {

			resultCount = (Long) list.get(0).get("count");
		}
		return resultCount.intValue();
	}


	protected class RowMapperImpl implements ParameterizedRowMapper<SubAccount> {
		public SubAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
			SubAccount subAccount = new SubAccount();
			subAccount.setId(rs.getLong("id"));
			subAccount.setVersion(rs.getLong("version"));
			subAccount.setUserId(rs.getLong("user_id"));
			subAccount.setAccountId(rs.getLong("account_id"));
			subAccount.setVmAccountId(rs.getLong("vm_account_id"));
			subAccount.setBalance(rs.getDouble("balance"));
			subAccount.setCreateDate(rs.getTimestamp("create_date"));
			subAccount.setUpdateDate(rs.getTimestamp("update_date"));
			subAccount.setLoseDate(rs.getTimestamp("lose_date"));
			subAccount.setLose(rs.getBoolean("is_lose"));
			return subAccount;
		}
	}

	@Override
	public List<SubAccount> findRemindListByActId(Long actId, String subSuffix, Date beginDate, Date endDate)
	{
		beginDate = DateUtils.getMinTime(beginDate);
		endDate = DateUtils.getMaxTime(endDate);
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("select id,version,user_id,account_id,vm_account_id,balance,create_date,update_date, lose_date, is_lose from beiker_sub_account_");
		sbSql.append(subSuffix);
		sbSql.append(" where balance>0 and is_lose=0  and   account_id = ?  and lose_date  between ?  and ? order by lose_date  asc");
		List<SubAccount> vmSubAccountList = getSimpleJdbcTemplate().query(sbSql.toString(), new RowMapperImpl(), actId, beginDate, endDate);

		return vmSubAccountList;
	}
	

}
