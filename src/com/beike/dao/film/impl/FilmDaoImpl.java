package com.beike.dao.film.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.film.FilmDao;
import com.beike.entity.film.Cinema;
import com.beike.entity.film.FilmRelease;
import com.beike.page.Pager;
import com.beike.util.StringUtils;

/**
 * @Title: 影院Dao实现
 * @Package com.beike.dao.film.impl
 * @Description:
 * @author wenjie.mai
 * @date 2012-11-28 上午15:39:59
 * @version V1.0
 */
@Repository("filmDao")
public class FilmDaoImpl extends GenericDaoImpl<Cinema, Long> implements FilmDao {

	private static Log log = LogFactory.getLog(FilmDaoImpl.class);

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmType() {

		StringBuilder typesql = new StringBuilder();

		typesql.append("SELECT DISTINCT(film_sort),film_py  FROM beiker_film_sort WHERE is_available = 1 ");

		List typelist = this.getJdbcTemplate().queryForList(typesql.toString());

		return typelist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmId(String film_py) {

		StringBuilder idsql = new StringBuilder();

		idsql.append("SELECT DISTINCT(film_id) FROM beiker_film_sort WHERE is_available = 1 ");

		if (org.apache.commons.lang.StringUtils.isNotBlank(film_py)) {
			idsql.append(" AND film_py = '").append(film_py).append("' ");
		}

		List idlist = this.getJdbcTemplate().queryForList(idsql.toString());

		return idlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int getFilmInfoCount(List<Long> filmids) {

		StringBuilder filmcountsql = new StringBuilder();

		filmcountsql.append("SELECT film_id FROM beiker_film_release where is_available = 1 ");

		if (filmids != null && filmids.size() > 0) {
			filmcountsql.append(" AND film_id IN (").append(StringUtils.arrayToString(filmids.toArray(), ",")).append(") ");
		}

		List filmcountlist = this.getJdbcTemplate().queryForList(filmcountsql.toString());

		if (filmcountlist == null || filmcountlist.size() == 0)
			return 0;

		return filmcountlist.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmInfo(List<Long> filmids, int start, int end) {

		StringBuilder filmsql = new StringBuilder();

		filmsql.append("SELECT film_id,film_name,duration,director,starring,small_photo,large_photo,sort,show_date,");
		filmsql.append("area,type,description,lowest_price,grade,msg,url,trailer_des,is_available,upd_time ");
		filmsql.append("FROM beiker_film_release where is_available = 1 ");

		if (filmids != null && filmids.size() > 0) {
			filmsql.append(" AND film_id IN (").append(StringUtils.arrayToString(filmids.toArray(), ",")).append(") ");
		}

		filmsql.append(" ORDER BY grade DESC ");
		filmsql.append(" LIMIT ").append(start).append(",").append(end);

		List filmlist = this.getJdbcTemplate().queryForList(filmsql.toString());

		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPopularFilmRank(int num) {

		StringBuilder popularsql = new StringBuilder();

		popularsql.append("SELECT film_id,film_name,duration,director,starring,small_photo,large_photo,sort,");
		popularsql.append("area,type,description,lowest_price,grade,msg,url,trailer_des,is_available,upd_time,show_date ");
		popularsql.append("FROM beiker_film_release where is_available = 1 ");
		popularsql.append("ORDER BY show_date DESC ");
		popularsql.append("LIMIT ").append(num);

		List filmlist = this.getJdbcTemplate().queryForList(popularsql.toString());

		return filmlist;
	}

	@Override
	public int queryFilmReleaseCountByCinema(Long cityId, Long cinemaId) {
		String sql = "select count(*) from((select distinct fr.id from beiker_film_release fr right join beiker_film_show fs on fr.film_id = fs.film_id where fs.city_id = ? and fr.id is not null and fr.is_available = 1 and fs.cinema_id = ?) tmp )";
		int result = getSimpleJdbcTemplate().queryForInt(sql,cityId, cinemaId);
		return result;
	}

	@Override
	public List<FilmRelease> queryFilmReleaseByCinema(Pager pager, Long cityId, Long cinemaId) {
		String sql = "select distinct fr.film_id,fr.film_name,fr.sort,fr.area,fr.director,fr.starring,fr.small_photo,fr.large_photo,fr.lowest_price,fr.grade from beiker_film_release fr right join beiker_film_show fs on fr.film_id = fs.film_id where fs.city_id =:cityId and fr.id is not null and fr.is_available = 1 and fs.cinema_id = :cinemaId  order by fr.grade desc limit :startRow,:pageSize";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("cityId", cityId);
		parameterSource.addValue("cinemaId", cinemaId);
		parameterSource.addValue("startRow", pager.getStartRow());
		parameterSource.addValue("pageSize", pager.getPageSize());
		List<FilmRelease> result = getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<FilmRelease>() {
			@Override
			public FilmRelease mapRow(ResultSet rs, int rowNum) throws SQLException {
				FilmRelease film = new FilmRelease();
				film.setFilmId(rs.getLong("fr.film_id"));
				film.setFilmName(rs.getString("fr.film_name"));
				film.setSort(rs.getString("fr.sort"));
				film.setArea(rs.getString("fr.area"));
				film.setDirector(rs.getString("fr.director"));
				film.setStarring(rs.getString("fr.starring"));
				film.setSmallPhoto(rs.getString("fr.small_photo"));
				film.setLargePhoto(rs.getString("fr.large_photo"));
				film.setLowestPrice(rs.getBigDecimal("fr.lowest_price"));
				film.setGrade(rs.getBigDecimal("fr.grade"));
				return film;
			}}, parameterSource);
		return result;
	}

	
	
	@Override
	public int queryFilmReleaseCountCityId(Long cityId) {
		String sql = "select count(*) from((select distinct fr.id from beiker_film_release fr right join beiker_film_show fs on fr.film_id = fs.film_id where fr.id is not null and fr.is_available = 1 and fs.city_id = "+cityId+") tmp )";
		int result = getSimpleJdbcTemplate().queryForInt(sql);
		return result;
	}

	@Override
	public List<FilmRelease> queryFilmReleaseCityId(Pager pager, Long cityId) {
		String sql = "select distinct fr.film_id,fr.film_name,fr.sort,fr.area,fr.director,fr.starring,fr.small_photo,fr.large_photo,fr.lowest_price,fr.grade from beiker_film_release fr right join beiker_film_show fs on fr.film_id = fs.film_id where fr.id is not null and fr.is_available = 1 and fs.city_id = "+cityId+" order by fr.grade desc limit :startRow,:pageSize";
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("startRow", pager.getStartRow());
		parameterSource.addValue("pageSize", pager.getPageSize());
		List<FilmRelease> result = getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<FilmRelease>() {
			@Override
			public FilmRelease mapRow(ResultSet rs, int rowNum) throws SQLException {
				FilmRelease film = new FilmRelease();
				film.setFilmId(rs.getLong("fr.film_id"));
				film.setFilmName(rs.getString("fr.film_name"));
				film.setSort(rs.getString("fr.sort"));
				film.setArea(rs.getString("fr.area"));
				film.setDirector(rs.getString("fr.director"));
				film.setStarring(rs.getString("fr.starring"));
				film.setSmallPhoto(rs.getString("fr.small_photo"));
				film.setLargePhoto(rs.getString("fr.large_photo"));
				film.setLowestPrice(rs.getBigDecimal("fr.lowest_price"));
				film.setGrade(rs.getBigDecimal("fr.grade"));
				return film;
			}}, parameterSource);
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getTuanGouFilmRank(Long tagextid, Long cityId, int num) {

		StringBuilder tgsql = new StringBuilder();

		tgsql.append(" SELECT DISTINCT(bg.goodsid) FROM beiker_goods bg");
		tgsql.append(" LEFT JOIN beiker_catlog_good bcd   ON bg.goodsid = bcd.goodid");
		tgsql.append(" LEFT JOIN beiker_goods_profile bgp ON bcd.goodid = bgp.goodsid");
		tgsql.append(" WHERE bg.isavaliable = '1' AND bg.startTime<=NOW()");
		tgsql.append(" AND bg.endTime>=NOW() AND bgp.sales_count < bg.maxcount");
		tgsql.append(" AND bcd.area_id  = ").append(cityId);
		tgsql.append(" AND bcd.tagextid = ").append(tagextid);
		tgsql.append(" ORDER BY bgp.sales_count DESC LIMIT ").append(num);

		List tglist = this.getJdbcTemplate().queryForList(tgsql.toString());
		return tglist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmLanguage(List<Long> filmids) {

		StringBuilder langsql = new StringBuilder();

		langsql.append("SELECT DISTINCT(LANGUAGE),film_id FROM beiker_film_show WHERE is_available = 1 ");

		if (filmids != null && filmids.size() > 0) {
			langsql.append(" AND film_id IN (").append(StringUtils.arrayToString(filmids.toArray(), ",")).append(") ");
			langsql.append(" ORDER BY find_in_set(film_id,'").append(StringUtils.arrayToString(filmids.toArray(), ",")).append("') ");
		}

		List langlist = this.getJdbcTemplate().queryForList(langsql.toString());
		return langlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaIdByFilmId(Long filmId, String startTime, String endTime) {

		StringBuilder filmsql = new StringBuilder();

		filmsql.append("SELECT DISTINCT(cinema_id) FROM beiker_film_show WHERE is_available = 1 ");
		filmsql.append("AND film_id = ").append(filmId).append(" AND show_time>= '").append(startTime).append("' ");
		filmsql.append("AND show_time<= '").append(endTime).append("' ");
		filmsql.append("ORDER BY show_time ASC");

		List filmlist = this.getJdbcTemplate().queryForList(filmsql.toString());
		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getRegionIdByCity(Long cityId) {

		StringBuilder regionsql = new StringBuilder();

		regionsql.append(" SELECT id,region_name,region_enname FROM beiker_region_property WHERE parentid = 0 ");
		regionsql.append(" AND areaid = ").append(cityId);
		regionsql.append(" ORDER BY id ASC ");

		List regionlist = this.getJdbcTemplate().queryForList(regionsql.toString());

		return regionlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getRegionByFilmCount(List<Long> cinemalist,Long cityId) {

		StringBuilder filmsql = new StringBuilder();

		filmsql.append("SELECT DISTINCT(bci.dist_id),brp.region_name FROM beiker_cinema_info bci ");
		filmsql.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		filmsql.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		filmsql.append("LEFT JOIN beiker_region_property brp ON bci.dist_id = brp.id ");
		filmsql.append("WHERE bc.is_available = 1 AND cinema_status =0 AND brp.parentid = 0 "); 
		filmsql.append("AND bc.city_id = ").append(cityId).append(" AND bci.city_id = ").append(cityId);

		if(cinemalist != null && cinemalist.size()>0){
			filmsql.append(" AND bc.cinema_id IN (").append(StringUtils.arrayToString(cinemalist.toArray(),",")).append(") ");
		}

		List filmlist = this.getJdbcTemplate().queryForList(filmsql.toString());
		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaId(List<Long> cinemalist) {

		StringBuilder qpsql = new StringBuilder();

		qpsql.append("SELECT bci.cinema_id,bg.goodsid,bg.currentPrice FROM beiker_cinema_info bci ");
		qpsql.append("LEFT JOIN beiker_goods_cinema bgc ON bci.cinema_id = bgc.cinema_id ");
		qpsql.append("LEFT JOIN beiker_goods bg ON bgc.goods_id = bg.goodsid ");
		qpsql.append("WHERE bg.isavaliable = '1' AND bg.startTime<=NOW() AND bg.endTime>=NOW() ");

		if (cinemalist != null && cinemalist.size() > 0) {
			qpsql.append(" AND bci.cinema_id IN (").append(StringUtils.arrayToString(cinemalist.toArray(), ",")).append(") ");
		}
		qpsql.append(" ORDER BY bg.currentPrice DESC ");

		List goodlist = this.getJdbcTemplate().queryForList(qpsql.toString());
		return goodlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getGoodsIdByCityId(Long cityId, List<Long> idlist) {

		StringBuilder goodsql = new StringBuilder();

		goodsql.append("SELECT bci.cinema_id,SUM(bgp.sales_count) AS totalcount,bci.name,bg.goodsid,bci.address,bci.coord,bci.photo FROM beiker_cinema_info bci ");
		goodsql.append("LEFT JOIN beiker_goods_cinema bgc ON bci.cinema_id = bgc.cinema_id ");
		goodsql.append("LEFT JOIN beiker_goods bg ON bgc.goods_id = bg.goodsid ");
		goodsql.append("LEFT JOIN beiker_goods_profile bgp ON bg.goodsid = bgp.goodsid ");
		goodsql.append("WHERE bg.isavaliable = '1' AND bg.startTime<=NOW() AND bg.endTime>=NOW() ");
		goodsql.append("AND bgp.sales_count < bg.maxcount AND bci.city_id = ").append(cityId);

		if (idlist != null && idlist.size() > 0) {
			goodsql.append(" AND bci.cinema_id IN (").append(StringUtils.arrayToString(idlist.toArray(), ",")).append(") ");
		}

		goodsql.append(" GROUP BY bci.cinema_id ");
		goodsql.append(" ORDER BY totalcount DESC ");

		List goodlist = this.getJdbcTemplate().queryForList(goodsql.toString());
		return goodlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaInfoById(List<Long> cinemalist) {

		StringBuilder csql = new StringBuilder();

		csql.append("SELECT cinema_id,NAME,address FROM beiker_cinema_info where 1=1 ");

		if (cinemalist != null && cinemalist.size() > 0) {
			csql.append(" AND cinema_id IN (").append(StringUtils.arrayToString(cinemalist.toArray(), ",")).append(") ");
		}

		List li = this.getJdbcTemplate().queryForList(cinemalist.toString());
		return li;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmLanguageById(Long filmId) {

		StringBuilder langsql = new StringBuilder();

		langsql.append(" SELECT DISTINCT(LANGUAGE),film_id FROM beiker_film_show WHERE is_available = 1 ");
		langsql.append(" AND film_id = ").append(filmId);

		List langlist = this.getJdbcTemplate().queryForList(langsql.toString());
		return langlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmInfoById(Long filmId) {

		StringBuilder filmsql = new StringBuilder();

		filmsql.append("SELECT film_id,film_name,duration,director,starring,small_photo,large_photo,sort,show_date,");
		filmsql.append("area,type,description,lowest_price,grade,msg,url,trailer_des,is_available,upd_time ");
		filmsql.append("FROM beiker_film_release where is_available = 1 ");
		filmsql.append("AND film_id = ").append(filmId);
		filmsql.append(" LIMIT 1");

		List filmlist = this.getJdbcTemplate().queryForList(filmsql.toString());

		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaInfo(List<Long> cidlist, int start, int end,Long cityId,Long filmId) {

		StringBuilder csql = new StringBuilder();

		csql.append("SELECT bci.cinema_id pid,bci.photo,bci.name,bci.address,");
		csql.append("bci.coord,bci.tel,bc.type,bc.cinema_id wid,MIN(bfs.v_price) vprice FROM beiker_cinema_info bci ");
		csql.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		csql.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		csql.append("LEFT JOIN beiker_film_show bfs ON bc.cinema_id = bfs.cinema_id ");
		csql.append("AND bci.city_id = ").append(cityId).append(" AND bc.city_id = ").append(cityId);
		
		csql.append(" WHERE bc.is_available = 1 AND bci.cinema_status = 0 AND bfs.show_time >=  NOW() ");
		csql.append(" AND bfs.film_id = ").append(filmId);
		
		if (cidlist != null && cidlist.size() > 0) {
			csql.append(" AND bci.cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(), ",")).append(") ");
		}
		csql.append(" GROUP BY bci.cinema_id ");
		csql.append(" LIMIT ").append(start).append(",").append(end);

		List filmlist = this.getJdbcTemplate().queryForList(csql.toString());
		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaInfoIdByCity(Long cityId, String flag, List<Long> notidlist) {

		StringBuilder cisql = new StringBuilder();

		cisql.append(" SELECT DISTINCT(bci.cinema_id) FROM beiker_cinema_info  bci");
		cisql.append(" JOIN beiker_wpw_cinema_map bwcm ON bci.cinema_id = bwcm.cinema_id");
		cisql.append(" JOIN beiker_cinema  bc ON bwcm.cinema_wpw_id = bc.cinema_id");
		cisql.append(" WHERE bci.cinema_status = 0 AND bc.is_available = 1");
		cisql.append(" AND bc.city_id = ").append(cityId).append(" AND bci.city_id = ").append(cityId);

		if (flag.equals("0")) {
			cisql.append(" AND bc.type IN (1,3) ");
		}

		if (notidlist != null && notidlist.size() > 0) {
			cisql.append(" AND bci.cinema_id NOT IN(").append(StringUtils.arrayToString(notidlist.toArray(), ",")).append(") ");
		}

		List cilist = this.getJdbcTemplate().queryForList(cisql.toString());
		return cilist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPopularFilmByGrade(int num) {

		StringBuilder popularsql = new StringBuilder();

		popularsql.append("SELECT film_id,film_name,duration,director,starring,small_photo,large_photo,sort,");
		popularsql.append("area,type,description,lowest_price,grade,msg,url,trailer_des,is_available,upd_time,show_date ");
		popularsql.append("FROM beiker_film_release where is_available = 1 ");
		popularsql.append("ORDER BY grade DESC ");
		popularsql.append("LIMIT ").append(num);

		List filmlist = this.getJdbcTemplate().queryForList(popularsql.toString());

		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaInfoIdByCinemaId(List<Long> cidlist,Long cityId) {
		
		StringBuilder str = new StringBuilder();
		
		str.append("SELECT bci.cinema_id qid,bc.cinema_id wid FROM beiker_cinema_info  bci ");
		str.append("JOIN beiker_wpw_cinema_map bwcm ON bci.cinema_id = bwcm.cinema_id ");
		str.append("JOIN beiker_cinema  bc ON bwcm.cinema_wpw_id = bc.cinema_id ");
		str.append("WHERE bci.cinema_status = 0 AND bc.is_available = 1 ");
		str.append("AND bci.city_id = ").append(cityId).append(" AND bc.city_id = ").append(cityId);
		
		if(cidlist != null && cidlist.size() > 0){
			str.append(" AND bc.cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(),",")).append(") ");
		}
		
		List li = this.getJdbcTemplate().queryForList(str.toString());
		
		return li;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaPriceById(List<Long> cidlist,Long filmId) {
		
		StringBuilder pricesql = new StringBuilder();
		
		pricesql.append("SELECT DISTINCT(cinema_id),v_price FROM beiker_film_show WHERE is_available = 1 ");
		pricesql.append(" AND film_id = ").append(filmId);
		
		if(cidlist != null && cidlist.size() > 0){
			pricesql.append(" AND cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(),",")).append(") ");
		}
		pricesql.append(" ORDER BY v_price DESC ");
		
		List pricelist =  this.getJdbcTemplate().queryForList(pricesql.toString());
		
		return pricelist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaList(List<Long> cidlist, int start, int end,Long cityId,Long regionId,Long filmId) {
		
		StringBuilder csql = new StringBuilder();
		
		csql.append("SELECT bci.cinema_id pid,bci.photo,bci.name,bci.address,");
		csql.append("bci.coord,bci.tel,bc.type,bc.cinema_id wid,bci.dist_id,MIN(bfs.v_price) vprice FROM beiker_cinema_info bci ");
		csql.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		csql.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		csql.append("LEFT JOIN beiker_film_show bfs ON bc.cinema_id = bfs.cinema_id ");
		csql.append("WHERE bc.is_available = 1 AND bci.cinema_status = 0  AND bfs.show_time >=  NOW() "); 
		csql.append(" AND bfs.film_id = ").append(filmId);
		
		if(cityId != null && cityId > 0){
			csql.append(" AND bc.city_id = ").append(cityId).append(" AND bci.city_id = ").append(cityId);
		}
		
		if(regionId != null && regionId > 0){
			csql.append(" AND bc.dist_id = ").append(regionId);
		}
		
		if(cidlist != null && cidlist.size()>0){
			csql.append(" AND bc.cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(),",")).append(") ");
		}
		
		csql.append(" GROUP BY bci.cinema_id ");
		csql.append(" LIMIT ").append(start).append(",").append(end);
		
		List filmlist = this.getJdbcTemplate().queryForList(csql.toString());
		
		return filmlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFilmShowlist(Long filmId, Long cinemaId, String startTime,String endTime) {
		
		StringBuilder str = new StringBuilder();
		
		str.append("SELECT show_time,show_index,LANGUAGE,dimensional,hall_name,v_price FROM beiker_film_show WHERE is_available =1 ");
		str.append(" AND cinema_id = ").append(cinemaId);
		str.append(" AND film_id   = ").append(filmId).append(" AND show_time >= NOW() ");
		str.append(" AND show_time >= '").append(startTime).append("' AND show_time <= '").append(endTime).append("' ");
		str.append(" ORDER BY show_time ASC ");
		
		List showlist = this.getJdbcTemplate().queryForList(str.toString());
		return showlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaIdByCid(Long cinemaId) {
		
		StringBuilder idsql = new StringBuilder();
		
		idsql.append("SELECT bci.cinema_id qid,bc.cinema_id wid FROM beiker_cinema_info bci ");
		idsql.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		idsql.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		idsql.append("WHERE bci.cinema_id = ").append(cinemaId);
		idsql.append(" LIMIT 1 ");
		
		List li = this.getJdbcTemplate().queryForList(idsql.toString());
		
		return li;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaFormList(List<Long> cidlist,Long cityId) {
		
		StringBuilder str = new StringBuilder();
		
		str.append("SELECT bci.cinema_id pid,bci.photo,bci.name,bci.address,");
		str.append("bci.coord,bci.tel,bc.type,bc.cinema_id wid FROM beiker_cinema_info bci ");
		str.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		str.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		str.append("WHERE bci.cinema_status = 0 AND bc.is_available = 1 ");
		str.append("bci.city_id = ").append(cityId).append(" AND bc.city_id = ").append(cityId);
		
		if(cidlist != null && cidlist.size() >0){
			str.append(" AND bci.cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(),",")).append(") ");
		}
		
		List li = this.getJdbcTemplate().queryForList(str.toString());
		return li;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int getCinemaListCount(List<Long> cidlist,Long cityId,Long regionId,Long filmId) {
		
		StringBuilder csql = new StringBuilder();
		
		csql.append("SELECT bci.cinema_id pid,bci.photo,bci.name,bci.address,");
		csql.append("bci.coord,bci.tel,bc.type,bc.cinema_id wid FROM beiker_cinema_info bci ");
		csql.append("LEFT JOIN beiker_wpw_cinema_map bwcp ON bci.cinema_id = bwcp.cinema_id ");
		csql.append("LEFT JOIN beiker_cinema bc ON bwcp.cinema_wpw_id = bc.cinema_id ");
		csql.append("LEFT JOIN beiker_film_show bfs ON bc.cinema_id = bfs.cinema_id ");
		csql.append("WHERE bc.is_available = 1 AND bci.cinema_status = 0 AND bfs.show_time >=  NOW() "); 
		csql.append("AND bfs.film_id = ").append(filmId);
		
		if(cityId != null && cityId > 0){
			csql.append(" AND bc.city_id = ").append(cityId).append(" AND bci.city_id = ").append(cityId);
		}
		
		if(regionId != null && regionId > 0){
			csql.append(" AND bc.dist_id = ").append(regionId);
		}
		
		if(cidlist != null && cidlist.size()>0){
			csql.append(" AND bc.cinema_id IN (").append(StringUtils.arrayToString(cidlist.toArray(),",")).append(") ");
		}
		
		csql.append(" GROUP BY bci.cinema_id ");
		
		List filmlist = this.getJdbcTemplate().queryForList(csql.toString());
		
		if(filmlist == null || filmlist.size() == 0 )
			return 0;
		
		return filmlist.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCinemaInfoList(Long cityId, List<Long> idlist,int num) {
		
		StringBuilder cisql = new StringBuilder();

		cisql.append(" SELECT distinct(bci.cinema_id),bci.name,bci.address,bci.coord,bci.photo FROM beiker_cinema_info  bci");
		cisql.append(" JOIN beiker_wpw_cinema_map bwcm ON bci.cinema_id = bwcm.cinema_id");
		cisql.append(" JOIN beiker_cinema  bc ON bwcm.cinema_wpw_id = bc.cinema_id");
		cisql.append(" WHERE bci.cinema_status = 0 AND bc.is_available = 1");
		cisql.append(" AND bc.city_id = ").append(cityId).append(" AND bci.city_id = ").append(cityId);

		if (idlist != null && idlist.size() > 0) {
			cisql.append(" AND bci.cinema_id IN(").append(StringUtils.arrayToString(idlist.toArray(), ",")).append(") ");
		}
		
		cisql.append(" LIMIT ").append(num);

		List cilist = this.getJdbcTemplate().queryForList(cisql.toString());
		return cilist;
	}

	@Override
	public BigDecimal getLowestPriceByFilm(Long cityId, Long cinemaId, Long filmId) {
//		只查询最近三天的放映计划
		Date now = new Date();
		Calendar threeDaysAfter = Calendar.getInstance();
		threeDaysAfter.set(Calendar.DATE, threeDaysAfter.get(Calendar.DATE) + 3);

		String sql = "select min(v_price) from beiker_film_show where show_time >= :showTimeStart and is_available = 1 and status = 1 and city_id = :cityId and cinema_id = :cinemaId and film_id = :filmId";

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("showTimeStart", now);
		parameterSource.addValue("cityId", cityId);
		parameterSource.addValue("cinemaId", cinemaId);
		parameterSource.addValue("filmId", filmId);

		BigDecimal result = getSimpleJdbcTemplate().queryForObject(sql, BigDecimal.class, parameterSource);
		return result;
	}

}
