package com.beike.dao.operation.ouzhoubei;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.entity.operation.ouzhoubei.Predict;
import com.beike.util.StringUtils;

@Repository("euroCupDao")
public class EuroCupDaoImpl  extends GenericDaoImpl implements EuroCupDao {

	@Override
	public List<Map> getUserPredicts(Long userid) {
		String sql = "SELECT cup.matchteams,predict.predict_score FROM beiker_ouzhoubei_predict predict JOIN beiker_ouzhoubei cup ON predict.match_id=cup.id WHERE predict.userid=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{userid});
	}

	@Override
	public Long getMatchesByUserid(Long userid,List<Long> matchids) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM beiker_ouzhoubei_predict predict WHERE predict.userid=").append(userid)
		.append(" AND predict.match_id IN(").append(StringUtils.arrayToString(matchids.toArray(), ",")).append(")");
		return getJdbcTemplate().queryForLong(sql.toString());
	}

	@Override
	public List<Map> getMatchesInfo() {
		String sql = "SELECT cup.id,cup.matchteams,cup.match_time,cup.match_score FROM beiker_ouzhoubei cup WHERE cup.match_time>= NOW() AND cup.match_time< NOW() + INTERVAL 1 DAY";
		return getJdbcTemplate().queryForList(sql);
	}

	@Override
	public Long isvalidMatch(List<Long> ids) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM beiker_ouzhoubei cup WHERE cup.match_time>= NOW() AND cup.match_time< NOW() + INTERVAL 1 DAY AND cup.id IN(")
		.append(StringUtils.arrayToString(ids.toArray(), ",")).append(")");
		return getJdbcTemplate().queryForLong(sql.toString());
	}

	@Override
	public int addPredict(final List<Predict> predicts) {
		String sql = "INSERT INTO beiker_ouzhoubei_predict(userid,match_id,predict_score) VALUES(?,?,?)";
		int[] results = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Predict p = predicts.get(i);
				logger.info(p.toString());
				ps.setLong(1,p.getUserid());
				ps.setLong(2, p.getMatch_id());
				ps.setString(3, p.getPredict_score());
			}
			
			@Override
			public int getBatchSize() {
				return predicts.size();
			}
		});
		return results.length;
	}

}
