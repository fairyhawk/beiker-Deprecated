package com.beike.dao.vm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmCancelRecord;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.vm.VmCancelRecordDao;
import com.beike.util.EnumUtil;

@Repository("vmCancelRecordDao")
public class VmCancelRecordDaoImpl extends GenericDaoImpl<VmCancelRecord, Long>
		implements VmCancelRecordDao {

	@Override
	public Long addVmCancelRecord(final VmCancelRecord vmCancelRecord) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (vmCancelRecord == null) {
			throw new IllegalArgumentException("settleApplyRecord is null");
		}

		final StringBuffer sb = new StringBuffer();
		sb
				.append("insert into  beiker_vm_cancel_record (account_id,vm_account_id,sub_account_id,amount,create_date,update_date,operator_id,cancel_type)");
		sb.append(" value(?,?,?,?,?,?,?,?)");

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				PreparedStatement ps = con.prepareStatement(sb.toString(),
						new String[] { "account_id", "vm_account_id",
								"sub_account_id", "amount", "create_date",
								"update_date", "operator_id" });

				ps.setLong(1, vmCancelRecord.getAccountId());
				ps.setLong(2, vmCancelRecord.getVmAccountId());
				ps.setLong(3, vmCancelRecord.getSubAccountId());
				ps.setDouble(4, vmCancelRecord.getAmount());
				ps.setTimestamp(5, new Timestamp(vmCancelRecord.getCreateDate()
						.getTime()));
				ps.setTimestamp(6, new Timestamp(vmCancelRecord.getUpdateDate()
						.getTime()));
				ps.setLong(7, vmCancelRecord.getOperatorId());
				ps.setString(8,EnumUtil.transEnumToString(vmCancelRecord.getCancelType()));

				return ps;
			}
		}, keyHolder);
		Long vmCancelRecordId = keyHolder.getKey().longValue();
		return vmCancelRecordId;
	}

	@Override
	public SubAccount findById(Long id) {

		return null;
	}

}
