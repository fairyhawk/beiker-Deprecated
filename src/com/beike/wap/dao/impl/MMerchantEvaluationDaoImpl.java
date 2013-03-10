package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MMerchantEvaluationDao;
import com.beike.wap.entity.MMerchantEvaluation;

/**
 * @Description: 订单商品明细DAO实现类
 * @author k.wang
 */
@Repository("mMerchantEvaluationDao")
public class MMerchantEvaluationDaoImpl extends GenericDaoImpl<MMerchantEvaluation, Long>
		implements MMerchantEvaluationDao {
	private static Log log = LogFactory.getLog(MMerchantEvaluationDaoImpl.class);

	protected class RowMapperImpl implements ParameterizedRowMapper<MMerchantEvaluation> 
	{
		public MMerchantEvaluation mapRow(ResultSet rs, int num) throws SQLException 
		{
			MMerchantEvaluation me = new MMerchantEvaluation();
			me.setId(rs.getLong("id"));
			me.setEvaluationscore(rs.getDouble("evaluationscore"));
			me.setMerchantid(rs.getLong("merchantid"));
			me.setEvaluationcontent(rs.getString("evaluationcontent"));
			me.setUser_id(rs.getLong("user_id"));
			me.setGoods_id(rs.getLong("goods_id"));
			me.setTrx_goods_id(rs.getLong("trx_goods_id"));
			return me;
		}
	}

	@Override
	public MMerchantEvaluation findByTrxId(Long id) {
		String sql = "SELECT * FROM beiker_merchantevaluation WHERE trx_goods_id  = ?";
		
		List<MMerchantEvaluation> rsList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(), id);
		if(rsList == null || rsList.size() == 0)
		{
			log.info("findByTrxId : query result is null");
			return null;
		}
		return rsList.get(0);
	}
}
