package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.FilmGoodsOrderDao;
import com.beike.util.BeanUtil;
import com.beike.util.StringUtils;
@Repository("filmGoodsOrderDao")
public class FilmGoodsOrderDaoImpl  extends GenericDaoImpl<FilmGoodsOrder, Long> implements FilmGoodsOrderDao {

	
	

	@Override
	public void addFilmGoodsOrder(FilmGoodsOrder filmGoodsOrder)
			throws Exception {
		String istSql = "INSERT INTO  beiker_film_goods_order( film_show_id, user_id,  trx_goods_id, trx_order_id, create_date,  show_time, update_date, film_price, seat_info,  hall_name, " +
				" film_name,    language, dimensional,  cinema_name,  trx_status, description,version,film_trx_sn,film_payno, film_count) "        
					+"VALUES(  ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)        ";
		getSimpleJdbcTemplate().update(istSql, 
				filmGoodsOrder.getFilmShowId(),
				filmGoodsOrder.getUserId(),
				filmGoodsOrder.getTrxGoodsId(),
				filmGoodsOrder.getTrxOrderId(),
				filmGoodsOrder.getCreateDate(),
				filmGoodsOrder.getShowTime(),
				filmGoodsOrder.getUpdateDate(),
				filmGoodsOrder.getFilmPrice(),
				filmGoodsOrder.getSeatInfo(),
				filmGoodsOrder.getHallName(),
				filmGoodsOrder.getFilmName(),
				filmGoodsOrder.getLanguage(),
				filmGoodsOrder.getDimensional(),
				filmGoodsOrder.getCinemaName(),
				filmGoodsOrder.getTrxStatus(),
				filmGoodsOrder.getDescription(),
				filmGoodsOrder.getVersion(),
				filmGoodsOrder.getFilmTrxSn(),
				filmGoodsOrder.getFilmPayNo(),
				filmGoodsOrder.getFilmCount());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> queryFilmGoodsOrderByCondition(
			Map<String, String> condition) throws Exception {
		StringBuilder querySql = new StringBuilder("SELECT id, film_show_id, user_id, trx_goods_id, trx_order_id, create_date, show_time, update_date, film_price, seat_info, hall_name, film_name, language, dimensional," +
				" cinema_name, trx_status, description, version, film_trx_sn, film_payno , film_count  FROM beiker_film_goods_order WHERE 1=1 ");
		
		//查询参数数组长度设定
		int size = condition.size();

		//查询参数数组
		Object[] params = new Object[size ];
		//查询参数类型数组
		int[] types = new int[params.length];
		//参数数组索引
		int index = 0;
		
		//查询条件封装
		if(StringUtils.validNull(condition.get("id"))) {
			querySql.append(" AND id= ?");
			params[index] = condition.get("id");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("filmShowId"))) {
			querySql.append(" AND film_show_id = ?");
			params[index] = condition.get("filmShowId");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("userId"))) {
			querySql.append(" AND user_id = ?");
			params[index] = condition.get("userId");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("trxGoodsId"))) {
			querySql.append(" AND trx_goods_id = ?");
			params[index] = condition.get("trxGoodsId");
			types[index] = Types.INTEGER;
			index++;
		}if(StringUtils.validNull(condition.get("trxOrderId"))) {
			querySql.append(" AND trx_order_id = ?");
			params[index] = condition.get("trxOrderId");
			types[index] = Types.INTEGER;
			index++;
		}if(StringUtils.validNull(condition.get("trxStatus"))) {
			querySql.append(" AND trx_status = ?");
			params[index] = condition.get("trxStatus");
			types[index] = Types.VARCHAR;
			index++;
		}if(StringUtils.validNull(condition.get("filmPayNo"))) {
			querySql.append(" AND film_payno = ?");
			params[index] = condition.get("filmPayNo");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("filmTrxSn"))) {
			querySql.append(" AND film_trx_sn = ?");
			params[index] = condition.get("filmTrxSn");
			types[index] = Types.VARCHAR;
			index++;
		}
		querySql.append(" ORDER BY  id, create_date   ");
		
		//结束
		
		
		List list = getJdbcTemplate().queryForList(querySql.toString(), params, types);
		 List<Object> filmGoodsOrderList = new ArrayList<Object>();
		if(null != list && list.size() > 0) {
			filmGoodsOrderList = BeanUtil.convertResultToObjectList(list, FilmGoodsOrder.class);
		}
		return filmGoodsOrderList;
	}

	@Override
	public void updateFilmGoodsOrderById(FilmGoodsOrder filmGoodsOrder) throws StaleObjectStateException
			 {
		if (filmGoodsOrder == null) {
			throw new IllegalArgumentException("trxOrder not null");
		}
		String updateSql = "UPDATE beiker_film_goods_order SET film_show_id = ?, user_id  = ?, trx_goods_id = ?, trx_order_id = ?, create_date  = ?, " +
				" show_time    = ?, update_date  = ?, film_price   = ?, seat_info    = ? , hall_name    = ?, film_name    = ?, language     = ?, dimensional  = ?, cinema_name  = ?," +
				" trx_status   = ?, description  = ?, version      = ?, film_trx_sn  = ? , film_payno =?, film_count = ?  WHERE id = ? AND version= ? ";
		
		int result =  getSimpleJdbcTemplate().update(updateSql, 
				filmGoodsOrder.getFilmShowId(),
				filmGoodsOrder.getUserId(),
				filmGoodsOrder.getTrxGoodsId(),
				filmGoodsOrder.getTrxOrderId(),
				filmGoodsOrder.getCreateDate(),
				filmGoodsOrder.getShowTime(),
				filmGoodsOrder.getUpdateDate(),
				filmGoodsOrder.getFilmPrice(),
				filmGoodsOrder.getSeatInfo(),
				filmGoodsOrder.getHallName(),
				filmGoodsOrder.getFilmName(),
				filmGoodsOrder.getLanguage(),
				filmGoodsOrder.getDimensional(),
				filmGoodsOrder.getCinemaName(),
				filmGoodsOrder.getTrxStatus(),
				filmGoodsOrder.getDescription(),
				filmGoodsOrder.getVersion()+1L,
				filmGoodsOrder.getFilmTrxSn(),
				filmGoodsOrder.getFilmPayNo(),
				filmGoodsOrder.getFilmCount(),
				filmGoodsOrder.getId(),
				filmGoodsOrder.getVersion());

		if (0==result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
		
	}

	@Override
	public FilmGoodsOrder queryFilmGoodsOrderById(Long id)  {
		if (id == null  || id< 0) {
			throw new IllegalArgumentException("id  not null");
		}
		
		String querySql = "SELECT id, film_show_id, user_id, trx_goods_id, trx_order_id, create_date, show_time, update_date, film_price, seat_info, hall_name, film_name, language, dimensional," +
				" cinema_name, trx_status, description, version, film_trx_sn, film_payno, film_count  FROM beiker_film_goods_order WHERE id = ?";
		List<FilmGoodsOrder> filmGoodsOrder = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), id);
		
		if (filmGoodsOrder.size() > 0) {
			return filmGoodsOrder.get(0);
		}
		return null;
	}
	
	public void updateStatusByTrxGoodsId(Long trxGoodsId){
		if(trxGoodsId==null||trxGoodsId==0){
			return ;
		}
		String sql = "UPDATE beiker_film_goods_order SET trx_status = 'SUCCESS' where trx_goods_id = ?";
		getSimpleJdbcTemplate().update(sql,trxGoodsId);
	}
	
	class RowMapperImpl implements ParameterizedRowMapper<FilmGoodsOrder> {

		@Override
		public FilmGoodsOrder mapRow(ResultSet rs, int num)
				throws SQLException {
			FilmGoodsOrder filmGoodsOrder = new FilmGoodsOrder();
			filmGoodsOrder.setId(rs.getLong("id"));
			filmGoodsOrder.setUserId(rs.getLong("user_id"));
			filmGoodsOrder.setTrxOrderId(rs.getLong("trx_order_id"));
			filmGoodsOrder.setFilmShowId(rs.getLong("film_show_id"));
			filmGoodsOrder.setTrxGoodsId(rs.getLong("trx_goods_id"));
			filmGoodsOrder.setCreateDate(rs.getTimestamp("create_date"));
			filmGoodsOrder.setShowTime(rs.getTimestamp("show_time"));
			filmGoodsOrder.setUpdateDate(rs.getTimestamp("update_date"));
			filmGoodsOrder.setFilmPrice(rs.getBigDecimal("film_price"));
			filmGoodsOrder.setSeatInfo(rs.getString("seat_info"));
			filmGoodsOrder.setHallName(rs.getString("hall_name"));
			filmGoodsOrder.setFilmName(rs.getString("film_name"));
			filmGoodsOrder.setLanguage(rs.getString("language"));
			filmGoodsOrder.setDimensional(rs.getString("dimensional"));
			filmGoodsOrder.setCinemaName(rs.getString("cinema_name"));
			filmGoodsOrder.setTrxStatus(rs.getString("trx_status"));
			filmGoodsOrder.setDescription(rs.getString("description"));
			filmGoodsOrder.setVersion(rs.getLong("version"));
			filmGoodsOrder.setFilmTrxSn(rs.getString("film_trx_sn"));
			filmGoodsOrder.setFilmPayNo(rs.getString("film_payno"));
			filmGoodsOrder.setFilmCount(rs.getLong("film_count"));
			return filmGoodsOrder;
		}
	}	
}
