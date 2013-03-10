package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MVoucherDao;
import com.beike.wap.entity.MVoucher;

/**
 * @Description: 订单商品明细DAO实现类
 * @author k.wang
 */
@Repository("mVoucherDao")
public class MVoucherDaoImpl extends GenericDaoImpl<MVoucher, Long>
		implements MVoucherDao {
	private static Log log = LogFactory.getLog(MVoucherDaoImpl.class);

	protected class RowMapperImpl implements ParameterizedRowMapper<MVoucher> 
	{
		public MVoucher mapRow(ResultSet rs, int num) throws SQLException 
		{
			MVoucher voucher = new MVoucher();
			voucher.setVoucher_id(rs.getLong("voucher_id"));
			voucher.setGuest_id(rs.getLong("guest_id"));
			voucher.setCreate_date(rs.getDate("create_date"));
			voucher.setActive_date(rs.getDate("active_date"));
			voucher.setConfirm_date(rs.getDate("confirm_date"));
			voucher.setVoucher_code(rs.getString("voucher_code"));
			voucher.setVoucher_status(rs.getString("voucher_status"));
			voucher.setDescription(rs.getString("description"));
			voucher.setVoucher_verify_source(rs.getString("voucher_verify_source"));
			return voucher;
		}
	}

	@Override
	public MVoucher findById(Long id) {
		String sql = "SELECT * FROM beiker_voucher WHERE voucher_id = ?";
		
		List<MVoucher> vList = this.getSimpleJdbcTemplate().query(sql, new RowMapperImpl(), id);
		if(vList == null || vList.size() == 0)
		{
			return null;
		}
		return vList.get(0);
	}
}
