package com.beike.dao.businessbackground.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.businessbackground.VipDao;
import com.beike.util.StringUtils;
@Repository("vipDao")
public class VipDaoImpl extends GenericDaoImpl implements VipDao {

	@Override
	public List<Map<String, Object>> queryVip(Map<String, Object> params) {
		// TODO Auto-generated method stub
		Map<Long, Timestamp> data = queryVipByGuestId(Long.parseLong(params.get("guest_id").toString()));
		StringBuilder sql = new StringBuilder();
		sql.append("select bu.user_id,bue.realname,bu.mobile,bue.nickname,bue.gender,bu.email from beiker_user bu left join beiker_user_expand bue on bu.user_id = bue.userid ");
		List<Map<String, Object>> result = null;
		if(data != null && data.size() > 0){
			sql.append("where bu.user_id in (");
			sql.append(StringUtils.arrayToString(data.keySet().toArray(), ","));
			sql.append(") ");
			boolean flag = false;
			List<Object> paramList = new ArrayList<Object>();
			if(params.containsKey("email")){
				sql.append(" and bu.email = ? ");
				flag = true;
				paramList.add(params.get("email"));
			}
			if(params.containsKey("mobile")){
				sql.append(" and bu.mobile = ? ");
				paramList.add(params.get("mobile"));
			}
			if(params.containsKey("name")){
				sql.append(" and bue.nickname like ? ");
				paramList.add("%" + params.get("name") + "%");
			}
			if(!flag){
				sql.append(" limit ?,? ");//limit (page-1)*size,size
				paramList.add(Integer.parseInt(params.get("startRow").toString()));
				paramList.add(Integer.parseInt(params.get("pagesize").toString()));
			}
			result = this.getSimpleJdbcTemplate().queryForList(sql.toString(), paramList.toArray(new Object[]{}));
			for(int i = 0;i < result.size();i++){
				result.get(i).put("vip_time", data.get(Long.parseLong(result.get(i).get("user_id").toString())));
			}
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Collections.sort(result, new Comparator<Map<String,Object>>(){  
	            public int compare(Map<String, Object> o1, Map<String, Object> o2)  
	            {  
	                //取出操作时间  
	                int ret = 0;  
	                try  
	                {
	                   ret = df.parse(df.format((Timestamp)o2.get("vip_time"))).compareTo(df.parse(df.format((Timestamp)o1.get("vip_time"))));
	                } catch (ParseException e)  
	                {                     
	                   throw new RuntimeException(e);  
	                }  
	                return  ret;  
	            }  
	        });
		}
		return result;
	}
	
	@Override
	public Map<String, Object> queryVipById(Long userId,Long guestId) {
		// TODO Auto-generated method stub
		Map<Long, Timestamp> tmp = queryVipByUserIdAndGuestId(userId,guestId);
		Map<String, Object> data = null;
		if(tmp != null && tmp.size() > 0){
			StringBuilder sql = new StringBuilder();
			sql.append("select bu.user_id,bue.realname,bu.mobile,bue.nickname,bue.gender,bu.email from beiker_user bu left join beiker_user_expand bue on bu.user_id = bue.userid where bu.user_id = ? ");
			data = this.getSimpleJdbcTemplate().queryForMap(sql.toString(), userId);
			data.put("vip_time", tmp.get(userId));
		}
		return data;
	}

	@Override
	public int queryVipCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		Map<Long, Timestamp> data = queryVipByGuestId(Long.parseLong(params.get("guest_id").toString()));
		StringBuilder sql = new StringBuilder();
		sql.append("select count(0) from beiker_user bu left join beiker_user_expand bue on bu.user_id = bue.userid ");
		if(data != null && data.size() > 0){
			sql.append("where bu.user_id in (");
			sql.append(StringUtils.arrayToString(data.keySet().toArray(), ","));
			sql.append(") ");
			List<Object> paramList = new ArrayList<Object>();
			if(params.containsKey("email")){
				sql.append(" and bu.email = ? ");
				paramList.add(params.get("email"));
			}
			if(params.containsKey("mobile")){
				sql.append(" and bu.mobile = ? ");
				paramList.add(params.get("mobile"));
			}
			if(params.containsKey("name")){
				sql.append(" and bue.nickname like ? ");
				paramList.add("%" + params.get("name") + "%");
			}
			return this.getSimpleJdbcTemplate().queryForInt(sql.toString(), paramList.toArray(new Object[]{}));
		}else {
			return 0;
		}
	}

	@Override
	public List<Map<String, Object>> queryVipProduct(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select btg.trxorder_id,btg.trx_goods_sn,btg.goods_id,btg.biz_type,btg.sub_guest_id,btg.order_lose_date,bt.close_date ");
		if(params.get("isConsume").toString().equals("true")){
			sql.append(",bv.confirm_date");
		}
		sql.append(" from beiker_trxorder_goods btg join beiker_trxorder bt on btg.trxorder_id = bt.id ");
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" join beiker_voucher bv on bv.voucher_id = btg.voucher_id");
		}
		sql.append(" where bt.user_id = ? and btg.guest_id = ? ");
		paramList.add(params.get("user_id"));
		paramList.add(params.get("guest_id"));
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" and btg.trx_status in ('" + TrxStatus.USED + "','" + TrxStatus.COMMENTED + "')");
		}else {
			sql.append(" and btg.trx_status not in ('" + TrxStatus.INIT + "','" + TrxStatus.USED + "','" + TrxStatus.COMMENTED + "')");
		}
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" order by bv.confirm_date desc ");
		}else {
			sql.append(" order by bt.close_date desc ");
		}
		sql.append(" limit ?,? ");
		paramList.add(Integer.parseInt(params.get("startRow").toString()));
		paramList.add(Integer.parseInt(params.get("pagesize").toString()));
		return this.getSimpleJdbcTemplate().queryForList(sql.toString(), paramList.toArray(new Object[]{}));
	}
	
	@Override
	public int queryVipProductCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		sql.append(" select count(0) from beiker_trxorder_goods btg join beiker_trxorder bt on btg.trxorder_id = bt.id ");
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" join beiker_voucher bv on bv.voucher_id = btg.voucher_id");
		}
		sql.append(" where bt.user_id = ? ");
		sql.append(" and btg.guest_id = ? ");
		paramList.add(params.get("user_id"));
		paramList.add(params.get("guest_id"));
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" and btg.trx_status in ('" + TrxStatus.USED + "','" + TrxStatus.COMMENTED + "')");
		}else {
			sql.append(" and btg.trx_status not in ('" + TrxStatus.INIT + "','" + TrxStatus.USED + "','" + TrxStatus.COMMENTED + "')");
		}
		if(params.get("isConsume").toString().equals("true")){
			sql.append(" order by bv.confirm_date desc ");
		}else {
			sql.append(" order by bt.close_date desc ");
		}
		return this.getSimpleJdbcTemplate().queryForInt(sql.toString(), paramList.toArray(new Object[]{}));
	}

	@Override
	public Map<Long, Timestamp> queryVipByGuestId(Long guestId) {
		// TODO Auto-generated method stub
		String sql = "select user_id,vip_time from beiker_vip_statistics where guest_id = ? ";
		Object[] args = {guestId};
		SqlRowSet srs = this.getJdbcTemplate().queryForRowSet(sql,args);
		Map<Long, Timestamp> result = new HashMap<Long, Timestamp>();
		while(srs.next()){
			result.put(srs.getLong(1), srs.getTimestamp(2));
		}
		return result;
	}
	
	@Override
	public Map<Long, Timestamp> queryVipByUserIdAndGuestId(Long userId,Long guestId) {
		// TODO Auto-generated method stub
		String sql = "select user_id,vip_time from beiker_vip_statistics where user_id = ? and guest_id = ? ";
		Object[] args = {userId,guestId};
		SqlRowSet srs = this.getJdbcTemplate().queryForRowSet(sql,args);
		Map<Long, Timestamp> result = new HashMap<Long, Timestamp>();
		while(srs.next()){
			result.put(srs.getLong(1), srs.getTimestamp(2));
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryMenuByOrderId(Long trxorderId) {
		// TODO Auto-generated method stub
		String sql = " select memu_name,menu_count,menu_price from beiker_menu_goods_order where trxorder_id = ? ";
		return this.getSimpleJdbcTemplate().queryForList(sql, trxorderId);
	}
	
	public int queryTrxOrderGoodsCount(Long trxorderId,Long guestId){
		String sql = " select count(0) from beiker_trxorder_goods where guest_id = ? and trxorder_id = ? ";
		return this.getSimpleJdbcTemplate().queryForInt(sql, guestId,trxorderId);
	}
}
