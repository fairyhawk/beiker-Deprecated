package com.beike.dao.film.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.film.GoodsCinemaDao;
import com.beike.entity.film.GoodsCinema;

@Repository("goodsCinemaDao")
public class GoodsCinemaDaoImpl extends GenericDaoImpl<GoodsCinema, Long> implements GoodsCinemaDao {

	@Override
	public boolean queryExsitCinemaGoods(Long cinemaId) {
		String sql = "select EXISTS(select gc.id from beiker_goods_cinema gc left join beiker_goods g on gc.goods_id = g.goodsid where gc.cinema_id = " + cinemaId + " and gc.goods_id  is not null and g.isavaliable = 1)";
		int result = getSimpleJdbcTemplate().queryForInt(sql);
		return result == 1;
	}

	@Override
	public BigDecimal queryLowestGrouponPrice(Long cinemaId) {
		String sql = "select min(g.currentPrice) from beiker_goods_cinema gc left join beiker_goods g on gc.goods_id = g.goodsid where g.isavaliable = 1 and gc.cinema_id = " + cinemaId;
		BigDecimal result = getSimpleJdbcTemplate().queryForObject(sql, BigDecimal.class);
		return result;
	}

	@Override
	public List<Long> querysFilmIds(Long cityId, Long areaId, Long cinemaId) {
		StringBuffer sql = new StringBuffer("select distinct gc.goods_id from beiker_cinema_info ci left join beiker_goods_cinema gc on ci.cinema_id = gc.cinema_id where ci.city_id=" + cityId);
		if (areaId != null) {
			sql.append(" and ci.dist_id=" + areaId);
		}
		if (cinemaId != null) {
			sql.append(" and ci.cinema_id=" + cinemaId);
		}
		List<Long> result = getSimpleJdbcTemplate().query(sql.toString(), new ParameterizedRowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("gc.goods_id");
			}
		});
		return result;
	}

}
