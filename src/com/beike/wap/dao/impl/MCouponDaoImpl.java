package com.beike.wap.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MCouponDao;
import com.beike.wap.entity.MGoods;

/**
 * <p>
 * Title:商品数据库实现
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-09-19
 * @author lvjx
 * @version 1.0
 */
@Repository("wapCouponDao")
public class MCouponDaoImpl extends GenericDaoImpl<MGoods, Long> implements
		MCouponDao {

	/*
	 * @see com.beike.wap.dao.coupon.CouponDao#queryIndexShowMes(int, int, int,
	 * java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception {
		List tempList = null;
		List<MGoods> goodsList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.id id,c.couponname couponname,c.downcount downcount,t.type_url type_url FROM beiker_coupon c ");
		sql.append("JOIN beiker_wap_type_info t ON t.type_id = c.id ");
		sql.append("WHERE t.type_type = ? AND t.type_floor = ? AND t.type_page = ? AND t.type_date = ? AND t.type_area = ? ");
		Object[] params = new Object[] { typeType, typeFloor, typePage,
				currentDate, typeArea };
		int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.DATE, Types.VARCHAR };
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params,
				types);
		if (null != tempList && tempList.size() > 0) {
			goodsList = this.convertResultToObjectList(tempList);
		}
		return goodsList;
	}

	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 将查询结果（map组成的List）转化成具体的对象列表
	 * 
	 * @param results
	 *            jdbcTemplate返回的查询结果
	 * @return 具体的对象列表
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private List<MGoods> convertResultToObjectList(List results)
			throws Exception {
		List<MGoods> objList = new ArrayList<MGoods>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				Map result = (Map) results.get(i);
				MGoods goods = this.convertResultMapToObject(result);
				objList.add(goods);
			}
		}
		return objList;
	}

	/**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result
	 *            jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private MGoods convertResultMapToObject(Map result) throws Exception {
		MGoods obj = new MGoods();
		if (result != null) {
			Long goodsId = ((Number) result.get("id")).longValue();
			if (null != goodsId) {
				obj.setGoodsId(goodsId);
			}
			if (StringUtils.validNull((String) result.get("couponname"))) {
				obj.setGoodsname(result.get("couponname").toString());
			}
			Long downCount = ((Number) result.get("downcount")).longValue();
			if (null != downCount) {
				obj.setDownCount(downCount);
			}
		}
		return obj;

	}
}
