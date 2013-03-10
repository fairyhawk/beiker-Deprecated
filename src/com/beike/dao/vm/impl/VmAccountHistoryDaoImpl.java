package com.beike.dao.vm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.vm.VmAccountHistory;
import com.beike.common.enums.vm.VmAccountType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.vm.VmAccountHistoryDao;
import com.beike.util.EnumUtil;

@Repository("vmAccountHistoryDao")
public class VmAccountHistoryDaoImpl extends
		GenericDaoImpl<VmAccountHistory, Long> implements VmAccountHistoryDao {

	@Override
	public VmAccountHistory findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,version,vm_account_id,account_id,sub_account_id,amount,balance,request_id,create_date,vm_account_type,operator_id,is_credit_act from beiker_vm_account_history where id = ?";
			List<VmAccountHistory> vmAccountHistoryList = getSimpleJdbcTemplate()
					.query(sql, new RowMapperImpl(), id);

			if (vmAccountHistoryList.size() > 0) {
				return vmAccountHistoryList.get(0);
			}
			return null;
		}

	}

	/**
	 * 根据请求号查询记录
	 * @param requestId
	 * @return
	 */
	@Override
	public VmAccountHistory findVmActHisByTypeAndReqId(String vmAccountType, String requestId){
		if (requestId == null || requestId.length() == 0) {
			throw new IllegalArgumentException("requestId is null or length == 0");
		}
		String sql = "SELECT id,version,vm_account_id,account_id,sub_account_id,amount,balance,request_id,create_date,vm_account_type,operator_id,is_credit_act FROM beiker_vm_account_history WHERE vm_account_type=? AND request_id=?";
		List<VmAccountHistory> list = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(), vmAccountType,requestId);
		if (list != null && list.size() ==1) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据请求号查询记录
	 * 
	 * @param requestId
	 * @return
	 */
	@Override
	public Long findByTypeAndReqId(String vmAccountType, String requestId) {

		if (requestId == null || requestId.length() == 0 ||  vmAccountType==null || vmAccountType.length()==0) {
			throw new IllegalArgumentException( "requestId is null or length == 0 or vmAccountType is null ");
		}
			String sql = "select count(1) as vmActHisCount from beiker_vm_account_history where vm_account_type = ? and request_id=?";
			List<Map<String, Object>> vmActHisCountList = getSimpleJdbcTemplate()
					.queryForList(sql, vmAccountType, requestId);

			if (vmActHisCountList != null && vmActHisCountList.size() > 0) {
				Map<String, Object> map = vmActHisCountList.get(0);

				Long vmActHisCount = (Long) (map.get("vmActHisCount"));

				return vmActHisCount;
			}
			return 0L;

	}

	@Override
	public Long addVmAccountHistory(final VmAccountHistory vmAccountHistory) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (vmAccountHistory == null) {
			throw new IllegalArgumentException("settleApplyRecord is null");

		}

		final StringBuffer sb = new StringBuffer();
		sb
				.append("insert into  beiker_vm_account_history(vm_account_id,account_id,sub_account_id,amount,balance,request_id,"
						+ "create_date,vm_account_type,operator_id,is_credit_act)");
		sb.append(" value(?,?,?,?,?,?,?,?,?,?)");

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				PreparedStatement ps = con.prepareStatement(sb.toString(),
						new String[] { "vm_account_id", "account_id",
								"sub_account_id", "amount", "balance",
								"request_id", "create_date", "vm_account_type",
								"operator_id", "is_credit_act" });

				ps.setLong(1, vmAccountHistory.getVmAccountId());
				ps.setLong(2, vmAccountHistory.getAccountId());
				ps.setLong(3, vmAccountHistory.getSubAccountId());
				ps.setDouble(4, vmAccountHistory.getAmount());
				ps.setDouble(5, vmAccountHistory.getBalance());
				ps.setString(6, vmAccountHistory.getRequestId());
				ps.setTimestamp(7, new Timestamp(vmAccountHistory
						.getCreateDate().getTime()));
				ps.setString(8, vmAccountHistory.getVmAccountType().name());
				ps.setLong(9, vmAccountHistory.getOperatorId());
				ps.setLong(10, vmAccountHistory.getIsCreditAct());

				return ps;
			}
		}, keyHolder);
		Long vmAccountHistoryId = keyHolder.getKey().longValue();
		return vmAccountHistoryId;
	}

	@Override
	public void updateVmAccountHistory(VmAccountHistory vmAccountHistory)
			throws StaleObjectStateException {
		if (vmAccountHistory == null) {
			return;
		} else {

			StringBuilder sqlSb = new StringBuilder();
			sqlSb.append("update  beiker_vm_account_history set version=?,vm_account_id=?,account_id=?,");
			sqlSb.append("sub_account_id=?,amount=?,balance=?,request_id=?,create_date=?,vm_account_type=?,operator_id=?,is_credit_act=?,version=version+1 where id=?");
			sqlSb.append(" and version=?");
			int result = getSimpleJdbcTemplate().update(
					sqlSb.toString(),
					vmAccountHistory.getVersion() + 1L,
					vmAccountHistory.getVmAccountId(),
					vmAccountHistory.getAccountId(),
					vmAccountHistory.getSubAccountId(),
					vmAccountHistory.getAmount(),
					vmAccountHistory.getBalance(),
					vmAccountHistory.getRequestId(),
					vmAccountHistory.getCreateDate(),
					EnumUtil.transEnumToString(vmAccountHistory.getVmAccountType()),
					vmAccountHistory.getOperatorId(),
					vmAccountHistory.getIsCreditAct(),
					vmAccountHistory.getId(), vmAccountHistory.getVersion());

			if (result == 0) {
				throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);

			}

		}

	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<VmAccountHistory> {
		public VmAccountHistory mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			VmAccountHistory vmAccountHistory = new VmAccountHistory();
			vmAccountHistory.setId(rs.getLong("id"));
			vmAccountHistory.setVmAccountId(rs.getLong("vm_account_id"));
			vmAccountHistory.setAccountId(rs.getLong("account_id"));
			vmAccountHistory.setSubAccountId(rs.getLong("sub_account_id"));
			vmAccountHistory.setAmount(rs.getDouble("amount"));
			vmAccountHistory.setBalance(rs.getDouble("balance"));
			vmAccountHistory.setRequestId(rs.getString("request_id"));
			vmAccountHistory.setCreateDate(rs.getTimestamp("create_date"));
			vmAccountHistory.setVmAccountType(EnumUtil.transStringToEnum(
					VmAccountType.class, rs.getString("vm_account_type")));
			vmAccountHistory.setOperatorId(rs.getLong("operator_id"));
			vmAccountHistory.setVersion(rs.getLong("version"));
			vmAccountHistory.setIsCreditAct(rs.getLong("is_credit_act"));
			return vmAccountHistory;
		}
	}

}
