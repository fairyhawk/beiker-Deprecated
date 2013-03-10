package com.beike.dao.impl.diancai;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.diancai.DianCaiDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.onlineorder.DiscoutType;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.mapper.OrderMenuMapper;
import com.beike.page.Pager;
 /**
 * com.beike.dao.impl.diancai.DianCaiDaoImpl.java
 * @description:点菜Dao实现
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
@Repository("dianCaiDao")
public class DianCaiDaoImpl extends GenericDaoImpl implements DianCaiDao  {

	/**
	 * 
	* @Title: getAllDianCaiByMerId
	* @Description: 获取品牌支持点菜的分店
	* @param @param merId:品牌标识
	* @return List<Map<String,Object>> ：支持点菜的分店集合
	* @throws ：sql异常
	 */
	public List<Map<String,Object>> getAllDianCaiByMerId(Long merId) throws Exception{
		StringBuilder sb = new StringBuilder("SELECT merchantid as id,addr,merchantname,tel,parentId ")
		                             .append("FROM beiker_merchant bm ")
                                     .append("WHERE bm.parentId = ? and is_support_online_meal = '1'");
		
		return this.getSimpleJdbcTemplate().queryForList(sb.toString(), merId);
	}

	@Override
	public List<Map<String,Object>> getPromotion(Long branchid) {
		String sql = "SELECT boo.audit_status,boo.order_id,boo.order_explain,boo.discount_engine,boo.order_start_time,boo.order_end_time FROM beiker_online_order boo JOIN beiker_order_guest_map bogm ON boo.order_id=bogm.order_id WHERE bogm.guest_id=? AND boo.audit_status='ONLINE' LIMIT 1";
		String sql2 = "SELECT boo.audit_status,boo.order_id,boo.order_explain,boo.discount_engine,boo.order_start_time,boo.order_end_time FROM beiker_online_order boo JOIN beiker_order_guest_map bogm ON boo.order_id=bogm.order_id WHERE bogm.guest_id=? AND boo.audit_status='OFFLINE' LIMIT 1";
		List<Map<String,Object>> list=getSimpleJdbcTemplate().queryForList(sql, branchid);
		if(list==null||list.size()==0){
			list=getSimpleJdbcTemplate().queryForList(sql2, branchid);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getBranchInfo(Long branchid) {
		String sql = "SELECT branch.merchantid branchid,branch.addr,branch.buinesstime,branch.merchantname branchname,branch.latitude,branch.tel,bmp.mc_logo1 logo,brand.merchantname brandname,brand.merchantid brandid FROM beiker_merchant branch JOIN beiker_merchant brand ON branch.parentId=brand.merchantid JOIN beiker_merchant_profile bmp ON bmp.merchantid=brand.merchantid WHERE branch.merchantid=? AND branch.parentId !=0 AND brand.parentId =0 LIMIT 1";
		
		return getSimpleJdbcTemplate().queryForList(sql, branchid);
	}

	@Override
	public List<Map<String,Object>> getTopone(List<Long> goodsids) {
		String sql = "SELECT btg.goods_id goodsid  FROM beiker_trxorder bt JOIN beiker_trxorder_goods btg ON bt.id=btg.trxorder_id WHERE btg.trx_status != 'INIT' AND btg.goods_id IN(:goodsids) GROUP BY btg.goods_id ORDER BY COUNT(btg.goods_id) DESC LIMIT 1";
		MapSqlParameterSource args = new MapSqlParameterSource("goodsids", goodsids);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}

	@Override
	public List<Map<String, Object>> getOrderMenu(Long promotionid, List<String> tags) {
		StringBuilder sql_param = new StringBuilder();
		for(int i=0;i<tags.size();i++){
			sql_param.append("'").append(tags.get(i)).append("'");
			if(tags.size()-1 != i){
				sql_param.append(",");
			}
		}
		String sql = "SELECT * FROM beiker_order_menu bom WHERE bom.order_id=? AND bom.menu_category IN("
				+ sql_param.toString()
				+") ORDER BY bom.menu_sort ASC";
		return getSimpleJdbcTemplate().queryForList(sql, promotionid);
	}

	@Override
	public List<Long> goodsOfBranch(Long branchid) {
		String sql = "SELECT bgm.goodsid FROM beiker_goods_merchant bgm JOIN beiker_goods bg ON bgm.goodsid=bg.goodsid WHERE bgm.merchantid AND bg.isavaliable=1 AND bgm.merchantid=?";
		return getJdbcTemplate().queryForList(sql,new Object[]{branchid},Long.class);
	}

	@Override
	public List<String> getMenuCat(Long orderid) {
		String sql = "SELECT DISTINCT bom.menu_category FROM beiker_order_menu bom WHERE bom.order_id=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{orderid}, String.class);
	}
	
	


	@Override
	public List<Map<String, Object>> getDiscountEngine(Long order_id,
			String type) {
		String sql = "";
		if(DiscoutType.FULLLESS.toString().equals(type)){
			sql  = "SELECT * FROM beiker_engine_fullless bef WHERE bef.order_id=?";
		}else if(DiscoutType.OVERALLFOLD.toString().equals(type)){
			sql = "SELECT * FROM beiker_engine_overallfold beo WHERE beo.order_id=?";
		}else if(DiscoutType.INTERVALLESS.toString().equals(type)){
			sql = "SELECT * FROM beiker_engine_intervalless bei WHERE bei.order_id=?";
		}
		return getSimpleJdbcTemplate().queryForList(sql, order_id);
	}

	@Override
	public List<Map<String,Object>> getOrderMenu(List<Long> menuids,Long branchid) {
		String sql = "SELECT DISTINCT bom.menu_price price,bom.menu_id,bom.menu_name,bom.menu_category,bom.menu_unit FROM beiker_order_menu bom JOIN beiker_order_guest_map bogm ON bom.order_id=bogm.order_id WHERE bom.menu_id IN(:menuids) AND bogm.guest_id=:branchid";
		MapSqlParameterSource args = new MapSqlParameterSource("menuids", menuids);
		args.addValue("branchid", branchid);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}

	@Override
	public Long getGoodsSold(Long goodsid) {
		String sql = "SELECT bgp.sales_count FROM beiker_goods_profile bgp WHERE bgp.goodsid=?";
		return getJdbcTemplate().queryForLong(sql, new Object[]{goodsid});
	}

	@Override
	public List<String> getGoodsRegionext(Long goodsid) {
		String sql = "SELECT DISTINCT brp.region_name FROM beiker_catlog_good bcg JOIN beiker_region_property brp ON bcg.regionextid=brp.id WHERE bcg.goodid=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{goodsid},String.class);
	}


	@Override
	public List<Map<String, Object>> getPaidOrderMenu(String trx_goods_id) {
		String sql = "SELECT * FROM beiker_menu_goods_order bmgm WHERE bmgm.trx_goods_id=? ";
		return getSimpleJdbcTemplate().queryForList(sql, trx_goods_id);
	}

	@Override
	public Map<String, Object> getOrderAmount(String trx_goods_id) {
		String sql = "SELECT btg.source_price,btg.pay_price FROM beiker_menu_goods_order bmgo JOIN beiker_trxorder_goods btg on bmgo.trxorder_id=btg.trxorder_id WHERE bmgo.trx_goods_id=? LIMIT 1";
		return getSimpleJdbcTemplate().queryForList(sql, trx_goods_id).get(0);
	}

	@Override
	public Long getBranchidByMenuid(Long menuid) {
		String sql = "SELECT DISTINCT bogm.guest_id FROM beiker_order_menu bom JOIN beiker_order_guest_map bogm ON bom.order_id=bogm.order_id WHERE bom.menu_id=?";
		return getJdbcTemplate().queryForLong(sql,new Object[]{menuid});
	}
	
	@Override
	public Map<String, Object> getGuestIdByOrderId(Long orderId) {
		String sql = "SELECT  order_id,  guest_id,  order_sn,  order_start_time,  order_end_time,  discount_engine,  order_explain,  audit_status,  settle_discount,  createucid,  createtime,  onlineucid,  onlinetime,  updateucid,  updatetime FROM beiker_online_order WHERE order_id = ?";
		return getSimpleJdbcTemplate().queryForList(sql, orderId).get(0);
	}

	@Override
	public List<Map<String, Object>> getEngineIntervallessByPrice(double price, Long orderId) {
		String sql = "SELECT engine_id, order_id, interval_amount, less_amount  FROM  beiker_engine_intervalless WHERE interval_amount  <= ? and order_id = ? order by interval_amount desc";
		return getSimpleJdbcTemplate().queryForList(sql, new Object[]{price, orderId});
	}
	
	@Override
	public List<Map<String,Object>> getOrderMenu(List<Long> menuids) {
		String sql = "SELECT DISTINCT bom.menu_price price,bom.menu_id,bom.menu_name,bom.menu_category,bom.menu_unit FROM beiker_order_menu bom JOIN beiker_order_guest_map bogm ON bom.order_id=bogm.order_id WHERE bom.menu_id IN(:menuids) ";
		MapSqlParameterSource args = new MapSqlParameterSource("menuids", menuids);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}
	@Override
	public Map<String, Object> getOrderGuestMapByOrderId(Long orderId){
		String sql = "SELECT id,order_id,guest_id, guest_settle FROM beiker_order_guest_map WHERE order_id = ?";
		List<Map<String,Object>> list = getSimpleJdbcTemplate().queryForList(sql, orderId);
		if (list!= null && list.size() >0) {
			return list.get(0) ;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getHistoryBranches(List<Long> branchids) {
       String sql = "SELECT branch.merchantid branchid,branch.merchantname branchname FROM beiker_merchant branch WHERE branch.merchantid IN(:branchids) AND branch.parentId !=0";
		MapSqlParameterSource args = new MapSqlParameterSource("branchids", branchids);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}
	
	@Override
	public List<Map<String, Object>> getCategoryByMenuIds(List<Long> menuIds){
		String sql = "SELECT   menu_category as category FROM  beiker_order_menu WHERE menu_id in (:menuIds) group by menu_category";
		MapSqlParameterSource args = new MapSqlParameterSource("menuIds", menuIds);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}
	@Override
	public List<OrderMenu> getOrderMenusByMenuId(List<Long> menuIds){
		String sql = "SELECT  menu_id,  order_id,  menu_category,  menu_name,  menu_price,  menu_unit,  menu_sort,  menu_logo,  menu_explain FROM  beiker_order_menu WHERE menu_id in (:menuIds)";
		MapSqlParameterSource args = new MapSqlParameterSource("menuIds", menuIds);
		return getSimpleJdbcTemplate().query(sql,new RowMapperImpl(), args);
	}
	
	@Override
	public List<Map<String, Object>> getOrderMenuByMenuId(List<Long> menuIds) {
		String sql = "SELECT  menu_id,  order_id,  menu_category,  menu_name,  menu_price,  menu_unit,  menu_sort,  menu_logo,  menu_explain FROM  beiker_order_menu WHERE menu_id in (:menuIds)";
		MapSqlParameterSource args = new MapSqlParameterSource("menuIds", menuIds);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<OrderMenu> {
		@Override
		public OrderMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
			OrderMenu orderMenu = new OrderMenu();
			orderMenu.setMenuId(rs.getLong("menu_id"));
			orderMenu.setOrderId(rs.getLong("order_id"));
			orderMenu.setMenuCategory(rs.getString("menu_category"));
			orderMenu.setMenuName(rs.getString("menu_name"));
			orderMenu.setMenuPrice(rs.getDouble("menu_price"));
			orderMenu.setMenuUnit(rs.getString("menu_unit"));
			orderMenu.setMenuSort(rs.getInt("menu_sort"));
			orderMenu.setMenuLogo(rs.getString("menu_logo"));
			orderMenu.setMenuExplain(rs.getString("menu_explain"));
			return orderMenu;
		}
	}
	/**
	 * 
	* @Title: getSupportOrderRegion
	* @Description: 可点餐的商圈
	* @param @param cityId
	* @param @return    设定文件
	* @return Map<Long,List<RegionCatlog>>    返回类型
	* @throws
	 */
	@Override
	public Map<Long, List<RegionCatlog>> getSupportOrderRegion(Long cityId,String nowStr) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT BRP.id,BRP.region_name,BRP.parentid FROM beiker_region_property BRP LEFT JOIN beiker_region_branch BRB ON BRP.id = BRB.regionid OR BRP.id = BRB.regionextid LEFT JOIN beiker_merchant BM ON BRB.branchid = BM.merchantid LEFT JOIN beiker_order_guest_map BOGM ON BOGM.guest_id = BM.merchantid LEFT JOIN beiker_online_order BOO ON BOGM.order_id = BOO.order_id WHERE BRP.areaid=?  AND BM.is_support_online_meal='1' AND BOO.audit_status = 'ONLINE' AND BOO.order_start_time<? AND BOO.order_end_time>?");
		List<Map<String,Object>> regions = getSimpleJdbcTemplate().queryForList(sql.toString(), new Object[]{cityId,nowStr,nowStr});
		
		Map<Long, List<RegionCatlog>> regoinMap = new HashMap<Long, List<RegionCatlog>>();
		List<RegionCatlog> regionList= null;
		RegionCatlog regionCatlog = null;
		for(Map<String,Object> map : regions){
			regionCatlog = new RegionCatlog();
			Long catlogId = ((Number) map.get("id")).longValue();
			String catlogName = map.get("region_name").toString();
			Long catlogParentId = ((Number) map.get("parentid")).longValue();
			
			regionCatlog.setCatlogid(catlogId);
			regionCatlog.setCatlogName(catlogName);
			regionCatlog.setParentId(catlogParentId);
			if(null!=regoinMap.get(catlogParentId)){
				regionList = regoinMap.get(catlogParentId);
				regionList.add(regionCatlog);
			}else{
				regionList = new ArrayList<RegionCatlog>();
				regionList.add(regionCatlog);
				regoinMap.put(catlogParentId, regionList);
			}
		}
		return regoinMap;
	}
	/**
	 * 
	* @Title: getCountListOfOrders
	* @Description:可点餐的分店标识
	* @param @param paramMap
	* @param @return    设定文件
	* @return Integer    返回类型
	* @throws
	 */
	@Override
	public List<Map<String,Object>> getMerchanitIdListOfOrders(Map<String, String> paramMap) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT BM.merchantid FROM beiker_merchant BM  LEFT JOIN beiker_region_branch BRB ON BM.merchantid = BRB.branchid");
		sql.append(" WHERE BM.areaid=").append(paramMap.get("cityId"));
		/*
		 * 查询一级商圈
		 */
		if(org.apache.commons.lang.StringUtils.isNotBlank(paramMap.get("regionid"))){
			sql.append(" AND BRB.regionid=").append(paramMap.get("regionid"));
		}
		/*
		 * 查询二级商圈
		 */
		if(org.apache.commons.lang.StringUtils.isNotBlank(paramMap.get("regionextid"))){
			sql.append(" AND BRB.regionextid=").append(paramMap.get("regionextid"));
		}
		/*
		 * 可点餐的条件
		 */
		sql.append(" AND BM.is_support_online_meal = '1'");
		
		return getSimpleJdbcTemplate().queryForList(sql.toString());
	}
	/**
	 * 
	* @Title: getCountOnlineOfOrder
	* @Description: 可点餐的分店的在线活动数量
	* @param @param merchantIdsStr
	* @param @return    设定文件
	* @return Integer    返回类型
	* @throws
	 */
	@Override
	public Integer getCountOnlineOfOrder(String merchantIdsStr,String nowStr) {

		StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT BOGM.guest_id) FROM beiker_order_guest_map BOGM LEFT JOIN  beiker_online_order BOO ON BOO.order_id=BOGM.order_id WHERE BOGM.guest_id IN (").append(merchantIdsStr).append(") AND BOO.audit_status = 'ONLINE'");
		
		return getSimpleJdbcTemplate().queryForInt(sql.toString());
	}
	/**
	 * 
	* @Title: listOfMerchantOfOrders
	* @Description: 可点餐的分店列表
	* @param @param paramMap
	* @param @return    设定文件
	* @return List<Map<String,String>>    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> listOfMerchantOfOrders(Map<String, String> paramMap) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT BM.merchantid,BM.addr,BM.merchantname,BM.tel,PBM.merchantname AS brandname,PBM.merchantid as brandid FROM beiker_merchant BM  LEFT JOIN beiker_region_branch BRB ON BM.merchantid = BRB.branchid LEFT JOIN beiker_merchant PBM ON PBM.merchantid = BM.parentId LEFT JOIN beiker_merchant_profile BMP ON BMP.merchantid = BM.merchantid");
		sql.append(" WHERE BM.areaid=").append(paramMap.get("cityId"));
		/*
		 * 查询一级商圈
		 */
		if(org.apache.commons.lang.StringUtils.isNotBlank(paramMap.get("regionid"))){
			sql.append(" AND BRB.regionid=").append(paramMap.get("regionid"));
		}
		/*
		 * 查询二级商圈
		 */
		if(org.apache.commons.lang.StringUtils.isNotBlank(paramMap.get("regionextid"))){
			sql.append(" AND BRB.regionextid=").append(paramMap.get("regionextid"));
		}
		/*
		 * 可点餐的条件
		 */
		sql.append(" AND BM.is_support_online_meal = '1' ORDER BY BMP.mc_sale_count DESC") ;
		
//		return getSimpleJdbcTemplate().queryForList(sql.toString(),new Object[] { pager.getStartRow(), pager.getPageSize() });
		return getSimpleJdbcTemplate().queryForList(sql.toString());
	}
	/**
	 * 
	* @Title: getListOfMerchantOfOrdersOnline
	* @Description: 有在线活动的可点餐的分店列表
	* @param @param merchantIdsStr
	* @param @param pager
	* @param @return    设定文件
	* @return List<Map<String,Object>>    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> getListOfMerchantOfOrdersOnline(String merchantIdsStr,String nowStr,Pager pager) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT BOO.order_id,BOO.order_explain,BOO.discount_engine,BOO.order_start_time,BOO.order_end_time,BOO.audit_status,BOGM.guest_id AS branchid FROM beiker_order_guest_map BOGM LEFT JOIN  beiker_online_order BOO ON BOO.order_id=BOGM.order_id WHERE BOGM.guest_id IN (")
		                        .append(merchantIdsStr)
		                        .append(") AND BOO.audit_status = 'ONLINE' ")
		                        .append(" Limit ?,?");
		return getSimpleJdbcTemplate().queryForList(sql.toString(),new Object[] {pager.getStartRow(), pager.getPageSize() });
	}
	/**
	 * 
	* @Title: getRandDishByOrderId
	* @Description: 获取随机的两道菜
	* @param @param object
	* @param @return    设定文件
	* @return OrderMenu    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> getRandDishByOrderId(String orderIds) {
//        String sql = "SELECT menu_id,order_id,menu_category,menu_price,menu_unit,menu_sort,menu_explain,substring_index(GROUP_CONCAT(menu_name),',',2) AS menu_name,menu_logo FROM beiker_order_menu WHERE order_id = ? group by menu_category ORDER BY RAND() LIMIT 1";
        String sql = "SELECT menu_id,order_id,menu_category,menu_price,menu_unit,menu_sort,menu_explain,substring_index(GROUP_CONCAT(menu_name),',',3) AS menu_name,menu_logo FROM beiker_order_menu WHERE order_id in ("+orderIds+")  and menu_logo!='' and  menu_logo is not null group by order_id";	
        List<Map<String, Object>> orderMenuList = getSimpleJdbcTemplate().queryForList(sql);
        return orderMenuList;
	}
	/**
	 * 分店下在菜
	 */
	@Override
	public List<OrderMenu> getBranchMenuList(String branchids, Integer limitNum) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT BOM.menu_name,BOM.menu_price,BOM.menu_logo,BOGM.guest_id ");
		sql.append("FROM beiker_online_order BOO LEFT JOIN beiker_order_guest_map BOGM ON BOO.order_id=BOGM.order_id LEFT JOIN beiker_order_menu BOM ON BOM.order_id = BOGM.order_id ");
		sql.append("WHERE BOGM.guest_id IN ("+branchids+") AND BOO.audit_status = 'ONLINE' ORDER BY BOM.menu_logo DESC ");

		if(null != limitNum){
			sql.append("LIMIT ").append(limitNum);
		}
                             
		return getSimpleJdbcTemplate().query(sql.toString(),new OrderMenuMapper());
	}
}
