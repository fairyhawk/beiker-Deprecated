package com.beike.dao.vm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.vm.VmAccount;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.vm.VmAccountDao;
import com.beike.util.DateUtils;

@Repository("vmAccountDao")
public class VmAccountDaoImpl extends GenericDaoImpl<VmAccount, Long> implements
		VmAccountDao {
	/**
	 * 根据类别ID查询虚拟款项账户个数
	 */
	@Override
	public Long findVmActCountBySortId(Long vmSortId) {

		if (vmSortId == null || vmSortId.longValue() == 0) {
			throw new IllegalArgumentException("settleApplyRecord is null");

		}
		String sql = "select count(1) as vmAccountCount from  beiker_vm_account where vm_account_sort_id =?";
		List<Map<String, Object>> vmAccountList = this.getSimpleJdbcTemplate()
				.queryForList(sql, vmSortId);

		if (vmAccountList != null && vmAccountList.size() > 0) {
			Map<String, Object> rspMap = vmAccountList.get(0);
			Long vmAaccountCount = (Long) rspMap.get("vmAccountCount");

			return vmAaccountCount;

		}
		return 0L;
	}

	@Override
	public Long addVmAccount(final VmAccount vmAccount) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (vmAccount == null) {
			throw new IllegalArgumentException("settleApplyRecord is null");

		}

		final StringBuffer sb = new StringBuffer();
		sb
				.append("insert into  beiker_vm_account(create_date,update_date,lose_date,total_balance,balance,vm_account_sort_id,is_fund,proposer,cost_bear,description,not_change_rule,is_not_change,is_refund)");
		sb.append(" value(?,?,?,?,?,?,?,?,?,?,?,?,?)");

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				PreparedStatement ps = con.prepareStatement(sb.toString(),
						new String[] { "create_date", "update_date",
								"lose_date", "total_balance", "balance",
								"vm_account_sort_id", "is_fund", "proposer",
								"cost_bear", "description" });

				ps.setTimestamp(1, new Timestamp(vmAccount.getCreateDate()
						.getTime()));
				ps.setTimestamp(2, new Timestamp(vmAccount.getUpdateDate()
						.getTime()));
				ps.setTimestamp(3, new Timestamp(vmAccount.getLoseDate()
						.getTime()));
				ps.setDouble(4, vmAccount.getTotalBalance());
				ps.setDouble(5, vmAccount.getBalance());
				ps.setLong(6, vmAccount.getVmAccountSortId());
				ps.setBoolean(7, vmAccount.isFund());
				ps.setString(8, vmAccount.getProposer());
				ps.setString(9, vmAccount.getCostBear());
				ps.setString(10, vmAccount.getDescription());
				ps.setString(11, vmAccount.getNotChangeRule());
				ps.setInt(12, vmAccount.getIsNotChange());
				ps.setInt(13, vmAccount.getIsRefund());
				return ps;
			}
		}, keyHolder);
		Long vmAccountId = keyHolder.getKey().longValue();
		return vmAccountId;
	}

	@Override
	public VmAccount findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,version,create_date,update_date,lose_date,total_balance,balance,vm_account_sort_id,is_fund,proposer,cost_bear,description from beiker_vm_account where id = ?";
			List<VmAccount> vmAccountList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);

			if (vmAccountList.size() > 0) {
				return vmAccountList.get(0);
			}
			return null;
		}

	}

	@Override
	public void updateVmAccount(VmAccount vmAccount)
			throws StaleObjectStateException {
		if (vmAccount == null || vmAccount.getId() == null) {
			return;
		} else {
			StringBuilder sqlSb = new StringBuilder();
			sqlSb
					.append("update  beiker_vm_account set version=?,create_date=?,update_date=?,lose_date=?,total_balance=?,balance=?,");
			sqlSb
					.append("vm_account_sort_id=?,is_fund=?,proposer=?,cost_bear=?,description=? where id=? and version=?");

			int count = getSimpleJdbcTemplate().update(sqlSb.toString(),
					vmAccount.getVersion() + 1L, vmAccount.getCreateDate(),
					vmAccount.getUpdateDate(), vmAccount.getLoseDate(),
					vmAccount.getTotalBalance(), vmAccount.getBalance(),
					vmAccount.getVmAccountSortId(), vmAccount.isFund(),
					vmAccount.getProposer(), vmAccount.getCostBear(),
					vmAccount.getDescription(), vmAccount.getId(),
					vmAccount.getVersion());

			if (count == 0) {
				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}
	}

	public List<Map<String, Object>> findLoseDate(Date sourceDate1,
			Date sourceDate2) {
		Date sourceMinDate1 = DateUtils.getMinTime(sourceDate1);
		Date sourceMaxDate1 = DateUtils.getMaxTime(sourceDate1);

		Date sourceMinDate2 = DateUtils.getMinTime(sourceDate2);
		Date sourceMaxDate2 = DateUtils.getMaxTime(sourceDate2);

		String sql = "select id,lose_date from beiker_vm_account  where lose_date   between  ?  and  ?  union all  select id,lose_date from beiker_vm_account  where lose_date   between  ?  and  ?";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, sourceMinDate1, sourceMaxDate1,
						sourceMinDate2, sourceMaxDate2);

		if (list != null && list.size() > 0) {

			return list;

		}
		return null;
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<VmAccount> {
		public VmAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
			VmAccount vmAccount = new VmAccount();
			vmAccount.setId(rs.getLong("id"));
			vmAccount.setCostBear(rs.getString("cost_bear"));
			vmAccount.setDescription(rs.getString("description"));
			vmAccount.setFund(rs.getBoolean("is_fund"));
			vmAccount.setLoseDate(rs.getTimestamp("lose_date"));
			vmAccount.setProposer(rs.getString("proposer"));
			vmAccount.setTotalBalance(rs.getDouble("total_balance"));
			vmAccount.setUpdateDate(rs.getTimestamp("update_date"));
			vmAccount.setVmAccountSortId(rs.getLong("vm_account_sort_id"));
			vmAccount.setBalance(rs.getDouble("balance"));
			vmAccount.setCreateDate(rs.getTimestamp("create_date"));
			vmAccount.setVersion(rs.getLong("version"));
			return vmAccount;
		}
	}
	
	@Override
	public Map<String, Object> findVmAccountById(Long id) {
		if (id == null || id.longValue() == 0) {
			throw new IllegalArgumentException();
		}

		String sql = "select is_not_change,not_change_rule,is_refund from beiker_vm_account where id=?";
		List<Map<String, Object>> userMapList = this.getSimpleJdbcTemplate()
				.queryForList(sql, id);
		if (userMapList != null && userMapList.size() > 0) {
			return userMapList.get(0);
		}

		return null;
	}

	/**
	 * 虚拟款项异步入账
	 * @param vmAccountId
	 * @param amount
	 */
	@Override
	public void updateVmAccountForAsycAccount(Long vmAccountId,Double amount,Long version) throws StaleObjectStateException{
		String updatesql = "UPDATE beiker_vm_account SET balance=balance-?,version=version+1 from beiker_vm_account where id=? and version=?";
		int count = getSimpleJdbcTemplate().update(updatesql, amount,vmAccountId,version);
		if (count == 0) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
}
