package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.MenuGoodsOrder;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.MenuGoodsOrderDao;
import com.beike.util.BeanUtil;
import com.beike.util.StringUtils;
@Repository("menuGoodsOrderDao")
public class MenuGoodsOrderDaoImpl extends GenericDaoImpl<Object, Long>  implements MenuGoodsOrderDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> queryMenuGoodsOrderByCondition(
			Map<String, String> condition) throws Exception {
		StringBuilder querySql = new StringBuilder("SELECT  id,  order_id,  menu_id,  menu_count,  trxorder_id,  trx_goods_id,  create_date,  menu_sort,  menu_price,  menu_category,  " +
				"memu_name,  menu_unit,  menu_logo,  menu_explain,  description,  version FROM  beiker_menu_goods_order WHERE 1=1 ");
		
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
		if(StringUtils.validNull(condition.get("orderId"))) {
			querySql.append(" AND order_id = ?");
			params[index] = condition.get("orderId");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("menuId"))) {
			querySql.append(" AND menu_id = ?");
			params[index] = condition.get("menuId");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("menuCount"))) {
			querySql.append(" AND menu_count = ?");
			params[index] = condition.get("menuCount");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("trxOrderId"))) {
			querySql.append(" AND trxorder_id = ?");
			params[index] = condition.get("trxOrderId");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("trxGoodsId"))) {
			querySql.append(" AND trx_goods_id = ?");
			params[index] = condition.get("trxGoodsId");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("trxGoodsId"))) {
			querySql.append(" AND trxgoods_id = ?");
			params[index] = condition.get("trxGoodsId");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("maxCreateDate"))) {
			querySql.append(" AND create_date <= ?");
			params[index] = new Timestamp(Long.parseLong(condition.get("maxCreateDate")));
			types[index] = Types.TIMESTAMP;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("minCreateDate"))) {
			querySql.append(" AND create_date >= ?");
			params[index] = new Timestamp(Long.parseLong(condition.get("minCreateDate")));
			types[index] = Types.TIMESTAMP;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("menuSort"))) {
			querySql.append(" AND menu_sort = ?");
			params[index] = condition.get("menuSort");
			types[index] = Types.INTEGER;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("menuPrice"))) {
			querySql.append(" AND menu_price = ?");
			params[index] = condition.get("menuPrice");
			types[index] = Types.DECIMAL;
			index++;
		}
		
		if(StringUtils.validNull(condition.get("menuCategory"))) {
			querySql.append(" AND menu_category = ?");
			params[index] = condition.get("menuCategory");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("menuName"))) {
			querySql.append(" AND memu_name = ?");
			params[index] = condition.get("menuName");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("menuUnit"))) {
			querySql.append(" AND menu_unit = ?");
			params[index] = condition.get("menuUnit");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("menuLogo"))) {
			querySql.append(" AND menu_logo = ?");
			params[index] = condition.get("menuLogo");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("menuExplain"))) {
			querySql.append(" AND menu_explain = ?");
			params[index] = condition.get("menuExplain");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("description"))) {
			querySql.append(" AND description = ?");
			params[index] = condition.get("description");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("version"))) {
			querySql.append(" AND version = ?");
			params[index] = condition.get("version");
			types[index] = Types.INTEGER;
			index++;
		}
		querySql.append(" ORDER BY  id, create_date   ");
		
		//结束
		
		
		List list = getJdbcTemplate().queryForList(querySql.toString(), params, types);
		 List<Object> menuGoodsOrderList = new ArrayList<Object>();
		if(null != list && list.size() > 0) {
			menuGoodsOrderList = BeanUtil.convertResultToObjectList(list, MenuGoodsOrder.class);
		}
		return menuGoodsOrderList;
	}

	@Override
	public void addMenuGoodsOrder(MenuGoodsOrder menuGoodsOrder) throws Exception {

			String istSql = "INSERT INTO beiker_menu_goods_order(   order_id,  menu_id,  menu_count,  trxorder_id,  trx_goods_id,  create_date, " +
					" menu_sort,  menu_price,  menu_category,  memu_name,  menu_unit,  menu_logo,  menu_explain,  description,  version)"
					+"VALUES (    ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?,   ?)";
			getSimpleJdbcTemplate().update(istSql, 
					menuGoodsOrder.getOrderId(),
					menuGoodsOrder.getMenuId(),
					menuGoodsOrder.getMenuCount(),
					menuGoodsOrder.getTrxOrderId(),
					menuGoodsOrder.getTrxOrderGoodsId(),
					menuGoodsOrder.getCreateDate(),
					menuGoodsOrder.getMenuSort(),
					menuGoodsOrder.getMenuPrice(),
					menuGoodsOrder.getMenuCategory(),
					menuGoodsOrder.getMenuName(),
					menuGoodsOrder.getMenuUnit(),
					menuGoodsOrder.getMenuLogo(),
					menuGoodsOrder.getMenuExplain(),
					menuGoodsOrder.getDescription(),
					menuGoodsOrder.getVersion());
					
					
		
	}

	
	
	@Override
	public List<MenuGoodsOrder> queryMenuGoodsOrderByMenuIds(List<Long> menuIds)
			throws Exception {
		if (menuIds == null  || menuIds.size() ==0) {
			throw new IllegalArgumentException("menuIds  not null");
		}
		String querySql = "SELECT  id,  order_id,  menu_id,  menu_count,  trxorder_id,  trx_goods_id,  create_date,  menu_sort,  menu_price,  menu_category,  memu_name,  menu_unit, " +
				" menu_logo,  menu_explain,  description,  version FROM  beiker_menu_goods_order WHERE menu_id IN (:menuIds) ";
		MapSqlParameterSource args = new MapSqlParameterSource("menuIds", menuIds);
		List<MenuGoodsOrder> menuGoodsOrder = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), args);
		if (menuGoodsOrder!= null && menuGoodsOrder.size() >0) {
			return menuGoodsOrder;
		}
		return null;
	}
	
	
	
	@Override
	public List<Map<String, Object>> queryOrderGuestMapByOrderIdAndGuestId(
			Long orderId, Long guestId) throws Exception {
		if(orderId < 0 || guestId< 0){
			throw new IllegalArgumentException("guestId or orderId  not illegal");
		}
		String sql = "SELECT  id,  order_id,  guest_id,  guest_settle FROM  beiker_order_guest_map WHERE order_id = ? and guest_id = ? ";
		return getSimpleJdbcTemplate().queryForList(sql, new Object[]{orderId, guestId});
	}

	@Override
	public List<MenuGoodsOrder> queryByOrderIdAndGuestId(Long orderId){
		if(orderId < 0 ){
			throw new IllegalArgumentException("guestId or orderId  not illegal");
		}
		String querySql = "SELECT  id,  order_id,  menu_id,  menu_count,  trxorder_id,  trx_goods_id,  create_date,  menu_sort,  menu_price,  menu_category,  memu_name,  menu_unit, " +
		" menu_logo,  menu_explain,  description,  version FROM  beiker_menu_goods_order WHERE trx_goods_id=? ";
		return getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), new Object[]{orderId});
	}
	
	class RowMapperImpl implements ParameterizedRowMapper<MenuGoodsOrder> {

		@Override
		public MenuGoodsOrder mapRow(ResultSet rs, int num)
				throws SQLException {
			MenuGoodsOrder menuGoodsOrder = new MenuGoodsOrder();
			menuGoodsOrder.setId(rs.getLong("id"));
			menuGoodsOrder.setOrderId(rs.getLong("order_id"));
			menuGoodsOrder.setMenuId(rs.getLong("menu_id"));
			menuGoodsOrder.setMenuCount(rs.getLong("menu_count"));
			menuGoodsOrder.setTrxOrderId(rs.getLong("trxorder_id"));
			menuGoodsOrder.setTrxOrderGoodsId(rs.getLong("trx_goods_id"));
			menuGoodsOrder.setCreateDate(rs.getTimestamp("create_date"));
			menuGoodsOrder.setMenuSort(rs.getInt("menu_sort"));
			menuGoodsOrder.setMenuPrice(rs.getBigDecimal("menu_price"));
			menuGoodsOrder.setMenuCategory(rs.getString("menu_category"));
			menuGoodsOrder.setMenuName(rs.getString("memu_name"));
			menuGoodsOrder.setMenuUnit(rs.getString("menu_unit"));
			menuGoodsOrder.setMenuLogo(rs.getString("menu_logo"));
			menuGoodsOrder.setMenuExplain(rs.getString("menu_explain"));
			menuGoodsOrder.setDescription(rs.getString("description"));
			menuGoodsOrder.setVersion(rs.getLong("version"));
			menuGoodsOrder.setVersion(rs.getLong("version"));
			return menuGoodsOrder;
		}
		
	}
	
	
}
