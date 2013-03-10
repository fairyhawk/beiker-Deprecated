package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.TrxRule;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.TrxRuleDao;

/**
 * @Title: TrxRuleDao.java
 * @Package com.beike.dao.trx
 * @Description: 支付规则Dao实现类
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 4:38:03 PM
 * @version V1.0
 */
@Repository("trxRuleDao")
public class TrxRuleDaoImpl extends GenericDaoImpl<TrxRule, Long> implements
		TrxRuleDao {

	/**
	 * 根据交易表达式标题查询交易表达式
	 * 
	 * @param title
	 * @return
	 */
	public TrxRule findRuleByTitle(String title) {
		if (title == null) {
			throw new IllegalArgumentException("title is null ");

		}

		String qrySql = "select id,trx_title,trx_rule,description from beiker_trx_rule  where trx_title=?";
		List<TrxRule> trxRuleList = getSimpleJdbcTemplate().query(qrySql,
				new RowMapperImpl(), title);
		if (trxRuleList.size() > 0) {
			return trxRuleList.get(0);
		}
		return null;
	}

	/**
	 * 根据ID查询交易表达式
	 * 
	 * @param title
	 * @return
	 */
	public TrxRule findRuleById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null ");

		}

		String qrySql = "select id,trx_title,trx_rule,description from beiker_trx_rule  where id=?";
		List<TrxRule> trxRuleList = getSimpleJdbcTemplate().query(qrySql,
				new RowMapperImpl(), id);
		if (trxRuleList.size() > 0) {
			return trxRuleList.get(0);
		}
		return null;
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<TrxRule> {

		public TrxRule mapRow(ResultSet rs, int num) throws SQLException {

			TrxRule trxRule = new TrxRule();
			trxRule.setId(rs.getLong("id"));
			trxRule.setTrxTitle(rs.getString("trx_title"));
			trxRule.setTrxRule(rs.getString("trx_rule"));
			// trxRule.setCreateDate(rs.getTimestamp("create_date"));
			// trxRule.setModifyDate(rs.getTimestamp("modify_date"));
			trxRule.setDescription(rs.getString("description"));
			return trxRule;

		}
	}

}
