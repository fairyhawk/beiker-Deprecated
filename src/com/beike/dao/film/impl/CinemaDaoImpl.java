package com.beike.dao.film.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.film.CinemaDao;
import com.beike.entity.film.Cinema;
import com.beike.entity.film.CinemaInfo;
import com.beike.entity.film.FilmShow;
import com.beike.page.Pager;

@Repository("cinemaDao")
public class CinemaDaoImpl extends GenericDaoImpl<Cinema, Long> implements CinemaDao {

	@Override
	public List<Long> queryCinemaAreaIdsByCity(Long cityId) {
		StringBuffer sql = new StringBuffer("select distinct ci.dist_id from beiker_cinema_info ci where ci.city_id = " + cityId);

		//只查询有电影商品的区域
		sql.append(" and EXISTS(select gc.id from beiker_goods_cinema gc left join beiker_goods g on gc.goods_id = g.goodsid where gc.cinema_id = ci.cinema_id and gc.goods_id is not null and g.isavaliable = 1)");

		List<Long> distIdList = getSimpleJdbcTemplate().query(sql.toString(), new ParameterizedRowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return Long.valueOf(rs.getLong("dist_id"));
			}
		});
		return distIdList;
	}

	@Override
	public CinemaInfo queryCinemaDetail(Long cinemaId) {
		String sql = "select ci.cinema_id,ci.type,ci.name,ci.address,ci.tel,ci.des,ci.special_des,ci.photo,ci.coord,ci.dist_id,c.cinema_id,c.type from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where ci.cinema_id = " + cinemaId + " limit 1";
		CinemaInfo result;
		try {
			result = getSimpleJdbcTemplate().queryForObject(sql, new ParameterizedRowMapper<CinemaInfo>() {
				@Override
				public CinemaInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
					CinemaInfo cinemaInfo = new CinemaInfo();
					cinemaInfo.setCinemaId(rs.getLong("ci.cinema_id"));
					cinemaInfo.setWpwCinemaId(rs.getLong("c.cinema_id"));
//					if (rs.getObject("c.type") != null) {
						cinemaInfo.setType(rs.getLong("c.type"));
//					} else {
//						cinemaInfo.setType(rs.getLong("ci.type"));
//					}
					cinemaInfo.setName(rs.getString("ci.name"));
					cinemaInfo.setAddress(rs.getString("ci.address"));
					cinemaInfo.setTel(rs.getString("ci.tel"));
					cinemaInfo.setDes(rs.getString("ci.des"));
					cinemaInfo.setSpecialDes(rs.getString("ci.special_des"));
					cinemaInfo.setPhoto(rs.getString("ci.photo"));
					cinemaInfo.setCoord(rs.getString("ci.coord"));
					cinemaInfo.setDistId(rs.getLong("ci.dist_id"));
					return cinemaInfo;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	@Override
	public int queryCinemaCount(Long cityId, Long areaId) {
//		StringBuffer sql = new StringBuffer("select count(*) from beiker_cinema_info where city_id = " + cityId);

		StringBuffer sql = new StringBuffer("select count(*) from ((");
		sql.append("select distinct ci.cinema_id,ci.name,ci.address,ci.tel,ci.des,ci.photo,ci.coord from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where ci.city_id = " + cityId);

		if (areaId != null) {
			sql.append(" and  ci.dist_id = " + areaId);
		}

		//只查询支持在线选座或团购的影院
		sql.append(" and ( c.type in (1,3) or EXISTS(select gc.id from beiker_goods_cinema gc left join beiker_goods g on gc.goods_id = g.goodsid where cinema_id = ci.cinema_id and goods_id  is not null and g.isavaliable = 1) )");

		sql.append(") tmp)");
		int result = getSimpleJdbcTemplate().queryForInt(sql.toString());
		return result;
	}

	@Override
	public List<CinemaInfo> queryCinema(Pager pager, Long cityId, Long areaId) {
		StringBuffer sql = new StringBuffer("select distinct ci.cinema_id,ci.name,ci.address,ci.tel,ci.des,ci.photo,ci.coord from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where ci.city_id = " + cityId);

		if (areaId != null) {
			sql.append(" and ci.dist_id = " + areaId);
		}
		//只查询支持在线选座或团购的影院
		sql.append(" and ( c.type in (1,3) or EXISTS(select gc.id from beiker_goods_cinema gc left join beiker_goods g on gc.goods_id = g.goodsid where gc.cinema_id = ci.cinema_id and gc.goods_id  is not null and g.isavaliable = 1) )");

		sql.append(" limit " + pager.getStartRow() + "," + pager.getPageSize());

		List<CinemaInfo> result = getSimpleJdbcTemplate().query(sql.toString(), new ParameterizedRowMapper<CinemaInfo>() {
			@Override
			public CinemaInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				CinemaInfo cinemaInfo = new CinemaInfo();
				cinemaInfo.setCinemaId(rs.getLong("ci.cinema_id"));
				cinemaInfo.setName(rs.getString("ci.name"));
				cinemaInfo.setAddress(rs.getString("ci.address"));
				cinemaInfo.setTel(rs.getString("ci.tel"));
				cinemaInfo.setDes(rs.getString("ci.des"));
				cinemaInfo.setPhoto(rs.getString("ci.photo"));
				cinemaInfo.setCoord(rs.getString("ci.coord"));
				return cinemaInfo;
			}
		});
		return result;
	}

	@Override
	public List<CinemaInfo> queryCinema(Long cityId, Long areaId) {
		StringBuffer sql = new StringBuffer("select distinct ci.cinema_id,ci.name,ci.address,ci.tel,ci.des from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id left join beiker_goods_cinema gc on ci.cinema_id = gc.cinema_id left join beiker_goods g on gc.goods_id = g.goodsid where gc.goods_id is not null and g.isavaliable=1 and  ci.city_id = " + cityId);

		if (areaId != null) {
			sql.append(" and ci.dist_id = " + areaId);
		}

		List<CinemaInfo> result = getSimpleJdbcTemplate().query(sql.toString(), new ParameterizedRowMapper<CinemaInfo>() {
			@Override
			public CinemaInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				CinemaInfo cinemaInfo = new CinemaInfo();
				cinemaInfo.setCinemaId(rs.getLong("ci.cinema_id"));
				cinemaInfo.setName(rs.getString("ci.name"));
				cinemaInfo.setAddress(rs.getString("ci.address"));
				cinemaInfo.setTel(rs.getString("ci.tel"));
				cinemaInfo.setDes(rs.getString("ci.des"));
				return cinemaInfo;
			}
		});
		return result;
	}

	@Override
	public int queryShowFilmCountByCinema(Long cinemaId) {
		return 0;
	}

	@Override
	public List<FilmShow> queryFilmShowPlainByCinema(Long cinemaId, Long filmId) {
//		只查询最近三天的放映计划
		Date now = new Date();
		Calendar threeDaysAfter = Calendar.getInstance();
		threeDaysAfter.set(Calendar.DATE, threeDaysAfter.get(Calendar.DATE) + 3);

		String sql = "select * from beiker_film_show where show_time >= :showTimeStart and is_available = 1 and status = 1 and cinema_id = :cinemaId and film_id = :filmId";

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("showTimeStart", now);
//		parameterSource.addValue("showTimeEnd", threeDaysAfter);
		parameterSource.addValue("cinemaId", cinemaId);
		parameterSource.addValue("filmId", filmId);
		List<FilmShow> result = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(FilmShow.class), parameterSource);
		return result;
	}

	@Override
	public Long queryQianpinCinemaByWpId(Long cinemaId) {
		String sql = "select ci.cinema_id from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where c.cinema_id = " + cinemaId + " limit 1";

		Long qianpin_cinemaId = this.getJdbcTemplate().queryForLong(sql, new Object[Integer.parseInt(cinemaId + "")]);

		return qianpin_cinemaId;
	}

	@Override
	public boolean queryIsQianpinCinema(Long cinemaId) {
		String sql = "select EXISTS(select ci.cinema_id from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where c.cinema_id is not null and c.cinema_id = " + cinemaId + ")";
		int result = getSimpleJdbcTemplate().queryForInt(sql);
		return result == 0;
	}
}
