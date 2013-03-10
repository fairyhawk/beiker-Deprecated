package com.beike.dao.businessbackground.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.businessbackground.OrderDao;

/**
 * @Title：OrderDaoImpl.java
 * @Package com.beike.dao.businessbackground.impl
 * @Description：
 * @date：2013-1-29 - 上午11:04:28
 * @author：zhaojinglong@qianpin.com
 * @version
 */
@SuppressWarnings("unchecked")
@Repository("orderDao")
public class OrderDaoImpl extends GenericDaoImpl implements OrderDao {
	public List<Map<String, Object>> getTrxOrderGoods(Map<String, Object> queryMap){
		List<Object> lstArgs = new ArrayList<Object>();
		lstArgs.add((Long)queryMap.get("guestid"));  //商户ID
		Date startDate = (Date)queryMap.get("startDate");
		Date endDate = (Date)queryMap.get("endDate");
		lstArgs.add(startDate);
		lstArgs.add(endDate);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ordergoods.sub_guest_id AS subguestid, ordergoods.goods_id AS goodsid, SUM(ordergoods.divide_price) AS totaldivideprice, ");
		sql.append("ordergoods.biz_type AS biztype ");
		sql.append("FROM beiker_trxorder_goods ordergoods ");
		sql.append("JOIN beiker_voucher voucher ON ordergoods.voucher_id = voucher.voucher_id ");
		sql.append("WHERE ordergoods.guest_id = ? AND ordergoods.trx_status IN ('USED', 'COMMENTED') ");
		sql.append("AND voucher.confirm_date >= ? AND voucher.confirm_date <= ? ");
		Long subguestid = (Long)queryMap.get("subguestid");
		if(subguestid != null){
			sql.append("AND ordergoods.sub_guest_id = ? ");
			lstArgs.add(subguestid);
		}
		sql.append("GROUP BY ordergoods.sub_guest_id, ordergoods.goods_id ");
		sql.append("ORDER BY ordergoods.sub_guest_id ASC, ordergoods.goods_id DESC");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), lstArgs.toArray());
	}

	@Override
	public Map<String, Object> queryOrderPrice(Long trxorderId) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append(" select source_price,pay_price from beiker_trxorder_goods where trxorder_id = ? ");
		return this.getSimpleJdbcTemplate().queryForMap(sql.toString(), trxorderId);
	}
	
}
