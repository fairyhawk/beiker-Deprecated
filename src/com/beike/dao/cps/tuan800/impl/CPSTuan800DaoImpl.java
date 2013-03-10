package com.beike.dao.cps.tuan800.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.cps.tuan800.CPSTuan800Dao;
import com.beike.util.StringUtils;

/**
 * @author janwen Apr 17, 2012
 */
@Repository("cpsTuan800Dao")
public class CPSTuan800DaoImpl extends GenericDaoImpl implements CPSTuan800Dao {

	@Override
	public void saveOrderNoPay(Map<String, Object> params,
			final String[] cps_cookie, Map order_info_map) {
		// final List<Map> order_info =
		// getOrderInfo(params.get("trxorder_id").toString());
		int c = getSimpleJdbcTemplate().queryForInt("select count(1) from beiker_cps_tuan800 where trx_goods_sn = '"+order_info_map.get("trx_goods_sn")+"'");
		if(c==0){
			String sql = "INSERT INTO beiker_cps_tuan800(trxorder_id,goods_id,trx_rule_id,create_date,pay_price,divide_price,current_price,trx_goods_sn,order_status,src,cid,outsrc,wi,uid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			getJdbcTemplate().update(
					sql,
					new Object[] { (Long) order_info_map.get("trxorder_id"),
							(Long) order_info_map.get("goodsid"),
							(Long) order_info_map.get("trx_rule_id"),
							(Timestamp) order_info_map.get("create_date"),
							(BigDecimal) order_info_map.get("pay_price"),
							(BigDecimal) order_info_map.get("divide_price"),
							(BigDecimal) order_info_map.get("current_price"),
							(String) order_info_map.get("trx_goods_sn"), 0,
							cps_cookie[0], cps_cookie[1], cps_cookie[2],
							cps_cookie[3], cps_cookie[4]
	
					});
		}
	}

	@Override
	public int saveOrderPay(Map params,Map order_info) {
		String[] cps_value = params.get("cps_cookie").toString().split("\\|");
		//cps站点保存在cookie信息{_src + '|' + _cid + '|' + _outsrc "|" + _wi +'|' + _uid} 
		int c = getSimpleJdbcTemplate().queryForInt("select count(1) from beiker_cps_tuan800 where trx_goods_sn = '"+order_info.get("trx_goods_sn")+"'");
		String sql = "";
		if(c==0){
			sql = "INSERT INTO beiker_cps_tuan800(trxorder_id,goods_id,trx_rule_id,order_status,create_date,pay_price,current_price,divide_price,trx_goods_sn,src,outsrc,cid,wi,uid) VALUES(?,?,?,?, ?,?,?,?,?,?,?,?,?,?)";
			return getJdbcTemplate().update(sql,new Object[]{order_info.get("trxorder_id"),order_info.get("goodsid"),order_info.get("trx_rule_id"),1,order_info.get("create_date"),order_info.get("pay_price"),order_info.get("divide_price")
					,order_info.get("current_price"),order_info.get("trx_goods_sn"),cps_value[0],cps_value[2],cps_value[1],cps_value[3],cps_value[4]});
		}else{
			sql = "update beiker_cps_tuan800 set order_status=1 where trx_goods_sn = '"+order_info.get("trx_goods_sn")+"' and order_status=0";
			return getJdbcTemplate().update(sql);
		}
	}

	@Override
	public int cancelOrder(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_cps_tuan800 bct SET bct.order_status=5 WHERE bct.trx_goods_sn=? AND bct.trxorder_id=? and bct.order_status=1");
		return getJdbcTemplate().update(
				sql.toString(),
				new Object[] { params.get("trx_goods_sn").toString(),
						params.get("trxorder_id").toString() });

	}

	@Override
	public List<Map> getOrderInfo(String trxorder_id) {
		String sql = "SELECT btg.goods_id goodsid,btg.trxorder_id, btg.trx_rule_id, btg.current_price,btg.divide_price,btg.pay_price,btg.create_date,btg.trx_goods_sn FROM beiker_trxorder_goods btg WHERE btg.trxorder_id=? and btg.biz_type=0 order by id ";
		return getJdbcTemplate()
				.queryForList(sql, new Object[] { trxorder_id });
	}
	
	public List<Map<String, Object>> getClassificationIds(String goodsIds){
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT DISTINCT bcg.goodid, bcg.tagextid ");
		sql.append("FROM beiker_catlog_good bcg ");
		sql.append("WHERE bcg.goodid IN(").append(goodsIds).append(") ");
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getOrderInfoCps(String trxorder_id, String trx_goods_sn) {
		String sql = "SELECT bct.goods_id goodsid,bct.trx_rule_id,bct.divide_price,bct.current_price,bct.src,bct.cid,bct.wi,bct.uid,bct.pay_price,bct.create_date,bct.trx_goods_sn FROM beiker_cps_tuan800 bct WHERE bct.trxorder_id=? AND bct.trx_goods_sn=?";
		return getJdbcTemplate().queryForList(sql,
				new Object[] { trxorder_id, trx_goods_sn });
	}

	@Override
	public List<Map> getOrder4Tuan800(Long beingIndex, String startDate,
			String endDate, String cid) {
		String sql = "SELECT bct.goods_id goodsid,bct.trx_rule_id,bct.trxorder_id,bct.order_status,bct.create_date,bct.pay_price,bct.trx_goods_sn,bct.cid,bct.wi FROM beiker_cps_tuan800 bct WHERE bct.create_date BETWEEN ? AND ? ";
		if (cid != null) {
			sql = sql + " AND bct.cid=" + cid
					+ " ORDER BY bct.id DESC LIMIT ?,500";
		} else {
			sql = sql + " ORDER BY bct.id DESC LIMIT ?,500";
		}
		return getJdbcTemplate().queryForList(sql,
				new Object[] { startDate, endDate, beingIndex, });
	}

	@Override
	public List<Map> getGoodsTitle(List<String> goodsidList) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT bg.goodsid,bg.goodsname FROM beiker_goods bg WHERE bg.goodsid IN(")
				.append(StringUtils.arrayToString(goodsidList.toArray(), ","))
				.append(")");
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public Long getTotalResults(String startdate, String enddate, String cid) {
		String sql = "SELECT COUNT(bct.id) FROM beiker_cps_tuan800 bct WHERE bct.create_date BETWEEN ? AND ?";
		if (cid != null) {
			sql = sql + " AND bct.cid=" + cid;
		}
		return getJdbcTemplate().queryForLong(sql,
				new Object[] { startdate, enddate });
	}

}
