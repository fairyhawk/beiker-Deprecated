package com.beike.dao.cps.tuan360.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.cps.tuan360.CPSTuan360Dao;
import com.beike.util.StringUtils;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 9, 2012 2:39:34 PM     
 * @version 1.0
 */
@Repository("cpsTuan360Dao")
public class CPSTuan360DaoImpl extends GenericDaoImpl implements CPSTuan360Dao {

	@Override
	public int saveOrderNoPay(List<Object[]> listOrderParams) {
		if(listOrderParams!=null && listOrderParams.size()>0){
			StringBuilder bufInsSql = new StringBuilder();
			bufInsSql.append("insert into beiker_cps_tuan360(trxorder_id,trxorder_goods_id,goods_id,");
			bufInsSql.append("pay_price,order_time,order_updtime,dividerate,coupon,");
			bufInsSql.append("qid,qihoo_id,ext,order_status) value(?,?,?,?,?,?,?,?,?,?,?,?)");
			
			this.getSimpleJdbcTemplate().batchUpdate(bufInsSql.toString(), listOrderParams);
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public int updateOrderStatus(Long trxOrderId, int newStatus, int oldStatus) {
		String updSql = "update beiker_cps_tuan360 set order_status=?,order_updtime=now() where trxorder_id=? and order_status=?";
		return this.getSimpleJdbcTemplate().update(updSql, newStatus, trxOrderId, oldStatus);
	}

	@Override
	public List<Map<String, Object>> getOrderInfo(String trxorder_id) {
		String selSql = "SELECT btg.goods_id,btg.trxorder_id,btg.id,btg.pay_price,btg.create_date FROM beiker_trxorder_goods btg WHERE btg.trxorder_id=? and btg.trx_rule_id<>3 and btg.biz_type=0 order by id";
		return this.getSimpleJdbcTemplate().queryForList(selSql, trxorder_id);
	}

	@Override
	public List<Map<String, Object>> queryOrdersByOrderId(List<Long> lstOrderIds,
			int maxCount) {
		if(lstOrderIds!=null && lstOrderIds.size()>0){
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select id,goods_id,order_status,pay_price,order_time, ");
			bufSelSql.append("order_updtime,dividerate,coupon,qid,qihoo_id,ext ");
			bufSelSql.append("from beiker_cps_tuan360 ");
			bufSelSql.append("where id in (").append(StringUtils.arrayToString(lstOrderIds.toArray(),",")).append(") ");
			bufSelSql.append("order by id limit ? ");
			
			return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString(),maxCount);
		}else{
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryCpsGoodsByGoodsId(
			List<Long> lstGoodsIds) {
		if(lstGoodsIds!=null && lstGoodsIds.size()>0){
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select bg.goodsid,bg.goodsname,bg.city,bcg.tagid,bcg.tagextid ");
			bufSelSql.append("from beiker_goods bg ");
			bufSelSql.append("join beiker_catlog_good bcg on bcg.goodid=bg.goodsid ");
			bufSelSql.append("where bg.goodsid in (").append(StringUtils.arrayToString(lstGoodsIds.toArray(),",")).append(") ");
			bufSelSql.append("group by bg.goodsid ");
			
			return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString());
		}else{
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryOrdersByCreateTime(String startTime,
			String endTime, Long lastOrderId, int maxCount) {
		if(startTime!=null && endTime!=null){
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select id,goods_id,order_status,pay_price,order_time, ");
			bufSelSql.append("order_updtime,dividerate,coupon,qid,qihoo_id,ext ");
			bufSelSql.append("from beiker_cps_tuan360 ");
			bufSelSql.append("where id>? and order_time>=? and order_time<? and order_status in (5,6) ");
			bufSelSql.append("order by id asc limit ? ");
			
			return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString(),lastOrderId,
					startTime,endTime,maxCount);
		}else{
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryOrdersByUpdTime(String updStartTime,
			String updEndTime, Long lastOrderId, int maxCount) {
		if(updStartTime!=null && updEndTime != null){
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select id,goods_id,order_status,pay_price,order_time, ");
			bufSelSql.append("order_updtime,dividerate,coupon,qid,qihoo_id,ext ");
			bufSelSql.append("from beiker_cps_tuan360 ");
			bufSelSql.append("where id>? and order_updtime>=? and order_updtime<? and order_status in (5,6) ");
			bufSelSql.append("order by id asc limit ? ");
			
			return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString(),lastOrderId,
					updStartTime,updEndTime,maxCount);
		}else{
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> queryOrdersByBillMonth(String startTime,
			String endTime, Long lastOrderId, int maxCount) {
		if(startTime!=null && endTime!=null){
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select id,goods_id,order_status,pay_price,order_time, ");
			bufSelSql.append("order_updtime,dividerate,coupon,qid,qihoo_id,ext ");
			bufSelSql.append("from beiker_cps_tuan360 ");
			bufSelSql.append("where id>? and order_time>=? and order_time<? and order_status=5 ");
			bufSelSql.append("order by id limit ? ");
			
			return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString(),lastOrderId,
					startTime,endTime,maxCount);
		}else{
			return null;
		}
	}

	@Override
	public int cancelOrder(Long trxGoodsId) {
		String updSql = "update beiker_cps_tuan360 set order_status=6,order_updtime=now() where trxorder_goods_id=? and order_status=5";
		return this.getSimpleJdbcTemplate().update(updSql, trxGoodsId);
	}
}
