package com.beike.dao.impl.waimai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.waimai.WaiMaiDao;
import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;
import com.beike.util.StringUtils;

/**
 * com.beike.dao.impl.waimai.WaiMaiDaoImpl.java
 * @description:外卖DaoImpl
 * @Author:xuxiaoxian
 *                    Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
@SuppressWarnings("unchecked")
@Repository("waiMaiDao")
public class WaiMaiDaoImpl extends GenericDaoImpl implements WaiMaiDao {
	@Override
	public TakeAway getTakeAwayByMerchantId(Long merchantId) {
		String sql = "select takeaway_id,branch_id,takeaway_phone,delivery_area,start_amount,takeaway_time,business_address,other_explain,menu_type,menu_logo,takeaway_status,takeaway_file from beiker_takeaway where branch_id = " + merchantId + " and takeaway_status = 'ONLINE' limit 1";
		TakeAway result;
		try {
			result = getSimpleJdbcTemplate().queryForObject(sql, ParameterizedBeanPropertyRowMapper.newInstance(TakeAway.class));
		} catch (DataAccessException e) {
			return null;
		}
		return result;
	}

	@Override
	public List<TakeAwayMenu> queryMenusByTakeAwayId(Long takeawayId) {
		String sql = "select menu_id,takeaway_id,branch_id,menu_category,menu_name,menu_price,menu_unit,menu_sort from beiker_takeaway_menu where takeaway_id = " + takeawayId;
		List<TakeAwayMenu> resultList = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(TakeAwayMenu.class));
		return resultList;
	}

	/**
	 * @Title: getAllTakeOutByMerId
	 * @Description: 获取品牌支持外卖的分店
	 * @param @param merId:品牌标识
	 * @TABLE:{beiker_takeaway：外卖，beiker_merchant：品牌商家【树结构】
	 * @return List<Map<String,Object>> :支持外卖的分店集合
	 * @throws ：sql异常
	 */
	public List<Map<String, Object>> getAllTakeOutByMerId(Long merId) throws Exception {
		StringBuilder sb = new StringBuilder("SELECT BT.takeaway_id,BT.branch_id,BT.takeaway_phone,BT.business_address,SMER.merchantname ").append("FROM beiker_takeaway BT JOIN  beiker_merchant SMER ON SMER.merchantid = BT.branch_id JOIN  beiker_merchant PMER ON PMER.merchantid = SMER.parentId ").append(
				"WHERE BT.takeaway_status = 'ONLINE' AND SMER.parentId = ? AND SMER.is_support_takeaway = '1'");

		return this.getSimpleJdbcTemplate().queryForList(sb.toString(), merId);
	}

	/** 
	 * @description：根据条件分页查询品牌Id（地图显示）
	 * @param Map<String,String> map
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Long> getBrandIdByCondition(Map<String,String> conditionMap,int startRow,int pageSize){
		StringBuilder sql = new StringBuilder("SELECT DISTINCT bm.parentId FROM beiker_merchant bm LEFT JOIN beiker_merchant_profile bmp ON bm.parentId = bmp.merchantid WHERE bm.parentId <> 0");
		this.getQueryLatitudeCondition(sql, conditionMap);
		sql.append(" ORDER BY bmp.mc_sale_count DESC LIMIT ?,?");
		List<Map<String,Object>> list = this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{startRow,pageSize});
		if(null != list && list.size() > 0){
			List<Long> resList = new ArrayList<Long>();
			for(Map<String,Object> map : list){
				resList.add(Long.parseLong(map.get("parentId").toString()));
			}
			return resList;
		}
		return null;
	}

	public List<Map<String,Object>> getMerDetailByBrandId(List<Long> brandIdList,Map<String,String> conditionMap){
		String ids = StringUtils.arrayToString(brandIdList.toArray(), ",");
		StringBuilder sql = new StringBuilder("SELECT DISTINCT parent.merchantname AS brandname,")
				.append("bm.tel,bm.lng,bm.lat,bm.city,bm.merchantid,bm.addr,bm.parentId,")
				.append("bm.merchantname,bm.is_support_takeaway,bm.is_support_online_meal")
				.append(" FROM beiker_merchant bm")
				.append(" LEFT JOIN beiker_merchant parent ON parent.merchantid = bm.parentId")
				.append(" WHERE bm.parentId IN(").append(ids).append(") ");

		sql = this.getQueryLatitudeCondition(sql, conditionMap);
		
		sql.append(" ORDER BY FIND_IN_SET(bm.parentId,'").append(ids).append("')");
		return this.getJdbcTemplate().queryForList(sql.toString());
		
	}
	
	/**
	 * @description：分页查询分店信息
	 * @param Map<String,String> map
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getMerDetailByCondition(Map<String, String> map, int startRow, int pageSize) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT parent.merchantname AS brandname,")
				.append(" bm.tel,bm.lng,bm.lat,bm.is_support_takeaway,bm.is_support_online_meal,bm.city,")
				.append(" bm.merchantid,bm.addr,bm.merchantname,bm.parentId")
				.append(" FROM beiker_merchant bm")
				.append(" LEFT JOIN beiker_merchant parent ON parent.merchantid = bm.parentId")
				.append(" LEFT JOIN beiker_merchant_profile bmp ON bmp.merchantid = bm.parentId")
				.append(" WHERE bm.parentId <> 0");

		sql = this.getQueryLatitudeCondition(sql, map);
		
		sql.append(" ORDER BY bmp.mc_sale_count DESC, bm.sort_number DESC LIMIT ?,?");
		return this.getJdbcTemplate().queryForList(sql.toString(), new Object[] { startRow, pageSize });
	}

	/**
	 * @description:查询当前可视区域内的分店/品牌 数量
	 * @param map
	 * @return int
	 * @throws
	 */
	public int getMerchantCount(Map<String, String> map, boolean isBrand) {

		StringBuilder sql = new StringBuilder("");
		if (isBrand) {
			sql.append("SELECT DISTINCT bm.parentId FROM beiker_merchant bm ");
		} else {
			sql.append("SELECT DISTINCT bm.merchantid FROM beiker_merchant bm ");
		}
		sql.append(" WHERE bm.parentId <> 0");

		sql = this.getQueryLatitudeCondition(sql, map);

		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		if (null != list && list.size() > 0) {
			return list.size();
		} else {
			return 0;
		}
	}

	/**
	 * @description：通过分店Id分页查询美食地图分店信息
	 * @param ids
	 * @param conditionMap
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getSearchMerDetailByIds(List<Long> ids, Map<String, String> conditionMap, int startRow, int pageSize) {
		String merIds = StringUtils.arrayToString(ids.toArray(), ",");
		StringBuilder sql = new StringBuilder("SELECT DISTINCT parent.merchantname AS brandname,")
				.append(" bm.tel,bm.lng,bm.lat,bm.is_support_takeaway,bm.is_support_online_meal,bm.city,")
				.append(" bm.merchantid,bm.addr,bm.merchantname,bm.parentId")
				.append(" FROM beiker_merchant bm")
				.append(" LEFT JOIN beiker_merchant parent ON parent.merchantid = bm.parentId")
				.append(" LEFT JOIN beiker_merchant_profile bmp ON bmp.merchantid = bm.parentId")
				.append(" WHERE bm.merchantid IN (").append(merIds).append(")");
		
		sql = this.getQueryLatitudeCondition(sql, conditionMap);		
		
		sql.append(" ORDER BY bmp.mc_sale_count DESC,bm.sort_number DESC LIMIT ?,?");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), new Object[] { startRow, pageSize });
		return list;
	}

	/**
	 * @description:根据分店搜索Id查询当前区域内分店数量
	 * @param ids
	 * @param conditionMap
	 * @return int
	 * @throws
	 */
	public int getSearchMerCountByIds(List<Long> ids, Map<String, String> conditionMap) {
		StringBuilder sql = new StringBuilder("SELECT bm.merchantid FROM beiker_merchant bm").append(" WHERE bm.merchantid IN (").append(StringUtils.arrayToString(ids.toArray(), ",")).append(")");
		
		sql = this.getQueryLatitudeCondition(sql, conditionMap);
		
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		if (null != list && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	private StringBuilder getQueryLatitudeCondition(StringBuilder sql, Map<String, String> conditionMap) {
		if (StringUtils.validNull(conditionMap.get("startLng")) && StringUtils.validNull(conditionMap.get("endLng")) 
				&& StringUtils.validNull(conditionMap.get("startLat")) && StringUtils.validNull(conditionMap.get("endLat"))) {
			sql.append(" AND bm.lng > ").append(Double.parseDouble(conditionMap.get("startLng")))
					.append(" AND bm.lng < ").append(Double.parseDouble(conditionMap.get("endLng")))
					.append(" AND bm.lat > ").append(Double.parseDouble(conditionMap.get("startLat")))
					.append(" AND bm.lat < ").append(Double.parseDouble(conditionMap.get("endLat")));
		}
		if (StringUtils.validNull(conditionMap.get("support_takeaway"))) {
			sql.append(" AND bm.is_support_takeaway = '1' ");
		} else if (StringUtils.validNull(conditionMap.get("support_online_meal"))) {
			sql.append(" AND bm.is_support_online_meal = '1' ");
		}
		if(StringUtils.validNull(conditionMap.get("city"))){
			sql.append(" AND bm.city = '").append(conditionMap.get("city")).append("' ");
		}
		return sql;
	}
	/**
	 * @description：分店所属品牌是否含有在售商品
	 * @param brandIdList
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> isBrandContainOnLineGoods(List<Long> brandIdList) {
		String idStr = StringUtils.arrayToString(brandIdList.toArray(), ",");
		StringBuilder sql = new StringBuilder("SELECT DISTINCT bgm.merchantid")
		.append(" FROM beiker_goods_merchant bgm")
		.append(" LEFT JOIN beiker_goods bg ON bgm.goodsid = bg.goodsid")
		.append(" WHERE bgm.merchantid IN (").append(idStr).append(") AND bg.isavaliable = 1");
		return this.getJdbcTemplate().queryForList(sql.toString());
	}
	/**
	 * 
	* @Title: getBranchsTakeAway
	* @Description: 取多个分店，并且取各分店指定数量的菜
	* @param @param branchids
	* @param @param menuCount    设定文件
	* @return void    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> getBranchsTakeAway(String branchids, Integer menuCount)	throws Exception {
		// TODO Auto-generated method stub
		
		StringBuilder sql = new StringBuilder("SELECT BT.takeaway_id,BT.branch_id,BT.takeaway_phone,BT.delivery_area,BT.start_amount,BT.takeaway_time,BTM.menu_name,BTM.menu_price,BTM.menu_unit ");
        sql.append("FROM beiker_takeaway BT LEFT JOIN beiker_takeaway_menu BTM ON BT.takeaway_id = BTM.takeaway_id ");
        sql.append("WHERE BT.branch_id IN (").append(branchids).append(") AND BT.takeaway_status = 'ONLINE' AND BT.menu_type='W'"); 
//        if(null!=menuCount){
//        	sql.append("LIMIT ").append(menuCount);
//        }
        return this.getSimpleJdbcTemplate().queryForList(sql.toString());
	}
}
