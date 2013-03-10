package com.beike.dao.vm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.vm.VmTrxExtend;
import com.beike.common.enums.vm.RelevanceType;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.vm.VmTrxExtendDao;
import com.beike.util.EnumUtil;

@Repository("vmTrxExtendDao")
public class VmTrxExtendDaoImpl extends GenericDaoImpl<VmTrxExtend, Long>
		implements VmTrxExtendDao {

	@Override
	public VmTrxExtend findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,version,account_id,sub_account_id,vm_account_id,trxorder_id,biz_id,is_credit_act,payment_amount,amount,create_date,"
					+ "lose_date,trx_request_id,relevance_type,description from bekker_vm_trx_extend where id = ?";
			List<VmTrxExtend> vmRelevanceSubHistoryList = getSimpleJdbcTemplate()
					.query(sql, new RowMapperImpl(), id);

			if (vmRelevanceSubHistoryList.size() > 0) {
				return vmRelevanceSubHistoryList.get(0);
			}
			return null;
		}

	}

	/**
	 * 根据个人总账户ID、交易ID和类型查询帐务扩展记录 （查询条件个人总账户ID是多余的，
	 * 但是这是退款啊，风险啊，多个条件更安全啊，强迫症啊有木有）* *
	 * 
	 * @param actId
	 * @param trxOrderId
	 * @param relevanceType
	 * @return
	 */
	public List<VmTrxExtend> findByTrxIdAndType(Long actId, Long trxOrderId,
			RelevanceType relevanceType) {

		if (actId == null || actId.longValue() == 0 || trxOrderId == null
				|| trxOrderId.longValue() == 0 || relevanceType == null) {
			throw new IllegalArgumentException();
		} else {

			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,account_id,sub_account_id,vm_account_id,trxorder_id,biz_id,is_credit_act,payment_amount,amount,create_date,");
			sbSql
					.append("lose_date,trx_request_id,relevance_type,description from beiker_vm_trx_extend where account_id = ?  and trxorder_id=? and relevance_type=? order by lose_date asc,sub_account_id  desc");
			List<VmTrxExtend> vmRelevanceSubHistoryList = getSimpleJdbcTemplate()
					.query(sbSql.toString(), new RowMapperImpl(), actId,
							trxOrderId,
							EnumUtil.transEnumToString(relevanceType));

			if (vmRelevanceSubHistoryList != null
					&& vmRelevanceSubHistoryList.size() > 0) {

				return vmRelevanceSubHistoryList;
			}
			return null;
		}

	}

	/**
	 * 根据个人总账户ID和交易ID查询帐务扩展记录 （查询条件个人总账户ID是多余的， 但是这是退款啊，风险啊，多个条件更安全啊，强迫症啊有木有）*
	 * (子账户ID逆序排列)
	 * 
	 * @param actId
	 * @param trxOrderId
	 * @return
	 */
	public List<VmTrxExtend> findByTrxId(Long actId, Long trxOrderId) {

		if (actId == null || actId.longValue() == 0 || trxOrderId == null
				|| trxOrderId.longValue() == 0) {
			throw new IllegalArgumentException();
		} else {

			StringBuilder sbSql = new StringBuilder();
			sbSql
					.append("select id,version,account_id,sub_account_id,vm_account_id,trxorder_id,biz_id,is_credit_act,payment_amount,amount,create_date,");
			sbSql
					.append("lose_date,trx_request_id,relevance_type,description from beiker_vm_trx_extend where account_id = ?  and trxorder_id=?   order by lose_date asc,sub_account_id  desc ");
			List<VmTrxExtend> vmRelevanceSubHistoryList = getSimpleJdbcTemplate()
					.query(sbSql.toString(), new RowMapperImpl(), actId,
							trxOrderId);

			if (vmRelevanceSubHistoryList != null
					&& vmRelevanceSubHistoryList.size() > 0) {

				return vmRelevanceSubHistoryList;
			}
			return null;
		}

	}

	@Override
	public Long addVmTrxExtend(final VmTrxExtend vmTrxExtend) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (vmTrxExtend == null) {
			throw new IllegalArgumentException("settleApplyRecord is null");

		}

		final StringBuffer sb = new StringBuffer();
		sb
				.append("insert into  beiker_vm_trx_extend(account_id,sub_account_id,vm_account_id,trxorder_id,biz_id,is_credit_act"
						+ ",payment_amount,amount,create_date,lose_date,trx_request_id,relevance_type,description)");
		sb.append(" value(?,?,?,?,?,?,?,?,?,?,?,?,?)");

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				PreparedStatement ps = con.prepareStatement(sb.toString(),
						new String[] { "account_id", "sub_account_id",
								"vm_account_id", "trxorder_id", "biz_id",
								"is_credit_act", "payment_amount", "amount",
								"create_date", "lose_date", "trx_request_id",
								"relevance_type", "description" });

				ps.setLong(1, vmTrxExtend.getAccountId());
				ps.setLong(2, vmTrxExtend.getSubAccountId());
				ps.setLong(3, vmTrxExtend.getVmAccountId());
				ps.setLong(4, vmTrxExtend.getTrxOrderId());
				ps.setLong(5, vmTrxExtend.getBizId());
				ps.setLong(6, vmTrxExtend.getIsCreditAct());
				ps.setDouble(7, vmTrxExtend.getPaymentAmount());
				ps.setDouble(8, vmTrxExtend.getAmount());
				ps.setTimestamp(9, new Timestamp(vmTrxExtend.getCreateDate()
						.getTime()));
				ps.setTimestamp(10, new Timestamp(vmTrxExtend.getLoseDate()
						.getTime()));
				ps.setString(11, vmTrxExtend.getTrxRequestId());
				ps.setString(12, vmTrxExtend.getRelevanceType().name());
				ps.setString(13, vmTrxExtend.getDescription());

				return ps;
			}
		}, keyHolder);
		Long vmTrxExtendId = keyHolder.getKey().longValue();
		return vmTrxExtendId;
	}

	@Override
	public void updateVmTrxExtend(VmTrxExtend vmTrxExtend) {
		if (vmTrxExtend == null) {
			return;
		} else {

			String sql = "update  beiker_vm_trx_extend set version=?,account_id=?,sub_account_id=?,vm_account_id=?,"
					+ "trxorder_id=?,biz_id=?,is_credit_act=?,payment_amount=?,amount=?,"
					+ "create_date=?,lose_date=?,trx_request_id=?,relevance_type=?,description=? where id=?";
			getSimpleJdbcTemplate().update(sql, vmTrxExtend.getVersion() + 1,
					vmTrxExtend.getAccountId(), vmTrxExtend.getSubAccountId(),
					vmTrxExtend.getVmAccountId(), vmTrxExtend.getTrxOrderId(),
					vmTrxExtend.getBizId(), vmTrxExtend.getIsCreditAct(),
					vmTrxExtend.getPaymentAmount(), vmTrxExtend.getAmount(),
					vmTrxExtend.getCreateDate(), vmTrxExtend.getLoseDate(),
					vmTrxExtend.getTrxRequestId(),
					EnumUtil.transEnumToString(vmTrxExtend.getRelevanceType()),
					vmTrxExtend.getDescription(), vmTrxExtend.getId());

		}
	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<VmTrxExtend> {
		public VmTrxExtend mapRow(ResultSet rs, int rowNum) throws SQLException {
			VmTrxExtend vmTrxExtend = new VmTrxExtend();
			vmTrxExtend.setId(rs.getLong("id"));
			vmTrxExtend.setVersion(rs.getLong("version"));
			vmTrxExtend.setVmAccountId(rs.getLong("vm_account_id"));
			vmTrxExtend.setAccountId(rs.getLong("account_id"));
			vmTrxExtend.setSubAccountId(rs.getLong("sub_account_id"));
			vmTrxExtend.setTrxOrderId(rs.getLong("trxorder_id"));
			vmTrxExtend.setBizId(rs.getLong("biz_id"));
			vmTrxExtend.setIsCreditAct(rs.getLong("is_credit_act"));
			vmTrxExtend.setPaymentAmount(rs.getDouble("payment_amount"));
			vmTrxExtend.setAmount(rs.getDouble("amount"));
			vmTrxExtend.setCreateDate(rs.getTimestamp("create_date"));
			vmTrxExtend.setLoseDate(rs.getTimestamp("lose_date"));
			vmTrxExtend.setTrxRequestId(rs.getString("trx_request_id"));
			vmTrxExtend.setRelevanceType(EnumUtil.transStringToEnum(
					RelevanceType.class, rs.getString("relevance_type")));
			vmTrxExtend.setDescription(rs.getString("description"));
			return vmTrxExtend;
		}
	}

}
