package com.beike.dao.trx.limit.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.limit.PayLimit;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.limit.PayLimitDao;

/**
 * payLimit表的相关数据库操作 2011-9-20 12:47:09
 * 
 * @author yurenli
 * 
 */
@Repository("payLimitDao")
public class PayLimitDaoImpl extends GenericDaoImpl<PayLimit, Long> implements
		PayLimitDao {

	@Override
	public PayLimit findById(Long id) {
		if (id == null) {
			return null;
		} else {
			String sql = "select id,user_id,goods_id,pay_count,create_date,modify_date,description,miaosha_id from beiker_pay_limit where id=?";
			List<PayLimit> limitList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);
			if (limitList.size() > 0) {
				return limitList.get(0);
			}
			return null;
		}
	}

	@Override
	public PayLimit findUseridAndGoodsid(Long uId, Long gId,Long miaoshaId) {

		if (uId == null || gId == null) {
			throw new IllegalArgumentException("uId or gId is null!");
		} else {
			String sql = "select id,user_id,goods_id,pay_count,create_date,modify_date,description,miaosha_id from beiker_pay_limit where user_id=? and goods_id=? and miaosha_id=?";
			List<PayLimit> limitList = getSimpleJdbcTemplate().query(sql,new RowMapperImpl(), uId, gId,miaoshaId);
			if (limitList!=null && limitList.size() > 0) {
				return limitList.get(0);
			}
			return null;
		}

	}

	@Override
	public List<PayLimit> findUseridAndGoodsIdStr(Long uId, String gIdStr) {
		StringBuilder qrySb = new StringBuilder();
		if (uId == null || gIdStr == null || gIdStr.length() == 0) {
			throw new IllegalArgumentException("uId or gIdStr  or gIdStr is null!");
		}
		qrySb.append("select id,user_id,goods_id,pay_count,create_date,modify_date,description,miaosha_id from beiker_pay_limit where user_id=? and goods_id in (");
		qrySb.append(gIdStr);
		qrySb.append(")");
		List<PayLimit> limitList = getSimpleJdbcTemplate().query(qrySb.toString(), new RowMapperImpl(), uId);
		if (limitList != null && limitList.size() > 0) {
			return limitList;
		}

		return null;

	}

	@Override
	public void savePayLimit(PayLimit payLimit) {
		if (payLimit.getId() != null) {
			updatePayLimit(payLimit);
		} else {

			String istSql = "insert beiker_pay_limit(user_id,goods_id,pay_count,create_date,modify_date,description,miaosha_id)"
					+ "value(?,?,?,?,?,?,?)";
			getSimpleJdbcTemplate().update(istSql, payLimit.getUserId(),
					payLimit.getGoodsId(), payLimit.getPayCount(),
					payLimit.getCreateDate(), payLimit.getModifyDate(),
					payLimit.getDescription(),payLimit.getMiaoshaId());

		}

	}

	@Override
	public void updatePayLimit(PayLimit payLimit) {
		if (payLimit == null) {
			throw new IllegalArgumentException("payLimit is null!");
		} else {
			StringBuilder sqlSb =  new  StringBuilder();
			
			sqlSb.append("update  beiker_pay_limit set user_id=?,goods_id=?,miaosha_id=?,pay_count=pay_count+1,create_date=?,");
			sqlSb.append("modify_date=?,description=? where id=?");
			getSimpleJdbcTemplate().update(sqlSb.toString(), payLimit.getUserId(),
					payLimit.getGoodsId(),
					payLimit.getMiaoshaId(),
					payLimit.getCreateDate(), payLimit.getModifyDate(),
					payLimit.getDescription(), payLimit.getId());

		}
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<PayLimit> {
		public PayLimit mapRow(ResultSet rs, int rowNum) throws SQLException {
			PayLimit payLimit = new PayLimit();
			payLimit.setId(rs.getLong("id"));
			payLimit.setUserId(rs.getLong("user_id"));
			payLimit.setGoodsId(rs.getLong("goods_id"));
			payLimit.setPayCount(rs.getLong("pay_count"));
			payLimit.setCreateDate(rs.getTimestamp("create_date"));
			payLimit.setModifyDate(rs.getTimestamp("modify_date"));
			payLimit.setDescription(rs.getString("description"));
			payLimit.setMiaoshaId(rs.getLong("miaosha_id"));
			return payLimit;
		}
	}

}
