package com.beike.dao.adweb.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.adweb.AdWebTrxInfo;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.adweb.AdWebTrxInfoDao;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */
@Repository("adWebTrxInfoDao")
public class AdWebTrxInfoDaoImpl extends GenericDaoImpl<AdWebTrxInfo,Long> implements AdWebTrxInfoDao {

	@Override
	public AdWebTrxInfo getAdWebTrxInfoById(Long adwebTrxId) {
		String sql="select bai.adwebid,bai.adweb_cid,bai.adweb_wi,bai.adwebtrx_id,bai.trxorderid,bai.buycount,bai.ordermoney,bai.ordertime,ba.adweb_trxurl from beiker_adwebtrxinfo bai left join beiker_adweb ba on bai.adwebid=adweb_id where adwebtrx_id=? and bai.trxorderid is null";
		List list=this.getSimpleJdbcTemplate().query(sql,new RowMapperImpl(), adwebTrxId);
		if(list==null||list.size()==0)return null;
		return (AdWebTrxInfo)list.get(0);
		
	}

	@Override
	public AdWebTrxInfo getAdWebTrxInfo(Long adwebid, String adcid, String adwi) {
		
		String sql="select adwebid,adweb_cid,adweb_wi,adwebtrx_id,trxorderid,buycount,ordermoney,ordertime from beiker_adwebtrxinfo where adwebid=? and adweb_cid=? and adweb_wi=?";
		List list=getSimpleJdbcTemplate().queryForList(sql, adwebid,adcid,adwi);
		
		if(list==null||list.size()==0)return null;
		AdWebTrxInfo ati=new AdWebTrxInfo();
		Map map =(Map) list.get(0);
		ati.setAdWebTrxId(((Number)map.get(("adwebtrx_id"))).longValue());
		ati.setTrxorderid((String)map.get(("trxorderid")));
		ati.setAdcid((String)map.get("adweb_cid"));
		ati.setAdwebid(((Number)map.get("adwebid")).longValue());
		ati.setAdwi((String)map.get("adweb_wi"));
		ati.setBuycount(((Number)map.get("buycount")).intValue());
		ati.setOrderMoney(((Number)map.get("ordermoney")).doubleValue());
		ati.setOrderTime((Timestamp)map.get("ordertime"));
		
		return ati;

	}

	@Override
	public void updateAdWebTrxInfo(Long adtrxid, String trxid,
			Integer buycount, Double buymoney) {
		
		String sql="update beiker_adwebtrxinfo ba set ba.trxorderid=?, ba.buycount=? , ba.ordermoney=? , ba.ordertime=? where ba.adwebtrx_id=?";
		this.getSimpleJdbcTemplate().update(sql, trxid,buycount,buymoney,new Date(),adtrxid);

	}

	@Override
	public Long getLastInsertId() {
		return super.getLastInsertId();
	}
	public class RowMapperImpl implements ParameterizedRowMapper<AdWebTrxInfo> {
		@Override
		public AdWebTrxInfo mapRow(ResultSet rs, int num) throws SQLException {
			
			AdWebTrxInfo adWebTrxInfo=new AdWebTrxInfo();
			adWebTrxInfo.setAdWebTrxId(rs.getLong("adwebtrx_id"));
			adWebTrxInfo.setTrxorderid(rs.getString("trxorderid"));
			adWebTrxInfo.setAdcid(rs.getString("adweb_cid"));
			adWebTrxInfo.setAdwebid(rs.getLong("adwebid"));
			adWebTrxInfo.setAdwi(rs.getString("adweb_wi"));
			adWebTrxInfo.setBuycount(rs.getInt("buycount"));
			adWebTrxInfo.setOrderMoney(rs.getDouble("ordermoney"));
			adWebTrxInfo.setOrderTime(rs.getTimestamp("ordertime"));
			
			String adweb_trxurl=rs.getString("adweb_trxurl");
			if(adweb_trxurl!=null){
				adWebTrxInfo.setAdweb_trxurl(adweb_trxurl);
			}
			
			return adWebTrxInfo;
		}
	}
	@Override
	public List<AdWebTrxInfo> getAdWebTrxInfoList(String fromDate,
			String endDate, String srccode, String cid) {
		String sql="select adweb_wi,ordermoney,ordertime,trxorderid from beiker_adwebtrxinfo bai left join beiker_adweb ba on bai.adwebid=adweb_id where  ba.adweb_code=? and bai.adweb_cid=? and bai.ordertime>=? and bai.ordertime<=? and bai.trxorderid is not null";
		List list=this.getSimpleJdbcTemplate().queryForList(sql,srccode, cid,fromDate,endDate);
		if(list==null||list.size()==0)return null;
		List<AdWebTrxInfo> listAdTrxInfo=new ArrayList<AdWebTrxInfo>();
		for(int i=0;i<list.size();i++){
			AdWebTrxInfo ati=new AdWebTrxInfo();
			Map map=(Map) list.get(i);
			String adweb_wi=(String) map.get("adweb_wi");
			ati.setAdwi(adweb_wi);
			ati.setTrxorderid((String)map.get(("trxorderid")));
			Double ordermoney=((BigDecimal) map.get("ordermoney")).doubleValue();
			ati.setOrderMoney(ordermoney);
			Timestamp ordertime=(Timestamp) map.get("ordertime");
			ati.setOrderTime(ordertime);
			listAdTrxInfo.add(ati);
		}
		return listAdTrxInfo;
	}
	
	@Override
	public Long addAdWebTrxInfo(final AdWebTrxInfo tmpadWebTrx) {

		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql="insert into beiker_adwebtrxinfo (adwebid,trxorderid,adweb_cid,adweb_wi,buycount,ordermoney,ordertime,userid,status) values(?,?,?,?,?,?,?,?,?)";
		
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(
						sql,
						new String[] {"adwebid","trxorderid","adweb_cid","adweb_wi","buycount","ordermoney","ordertime","userid","status"});
				ps.setLong(1, tmpadWebTrx.getAdwebid());
				ps.setString(2, tmpadWebTrx.getTrxorderid());
				ps.setString(3, tmpadWebTrx.getAdcid());
				ps.setString(4, tmpadWebTrx.getAdwi());
				ps.setInt(5, tmpadWebTrx.getBuycount());
				ps.setDouble(6, tmpadWebTrx.getOrderMoney());
				ps.setTimestamp(7, new Timestamp(tmpadWebTrx.getOrderTime().getTime()));
				ps.setLong(8, tmpadWebTrx.getUserId());
				ps.setString(9, "1");
				return ps;
			}

		}, keyHolder);
		Long webTrxid = keyHolder.getKey().longValue();
		
		return webTrxid;
	}

	@Override
	public AdWebTrxInfo getAdWebTrxInfoByTrxId(String trxOrderId) {
		String sql="select bai.trxorderid,bai.adweb_cid,bai.adweb_wi,bai.buycount,bai.ordermoney,bai.ordertime,bai.userid,ba.adweb_trxurl from beiker_adwebtrxinfo bai left join beiker_adweb ba on bai.adwebid=adweb_id where trxorderid=?";
		List list=getSimpleJdbcTemplate().queryForList(sql, trxOrderId);
		
		if(list==null||list.size()==0)return null;
		AdWebTrxInfo ati=new AdWebTrxInfo();
		Map map =(Map) list.get(0);
		ati.setTrxorderid((String)map.get(("trxorderid")));
		ati.setAdcid((String)map.get("adweb_cid"));
		ati.setAdwi((String)map.get("adweb_wi"));
		ati.setBuycount(((Number)map.get("buycount")).intValue());
		ati.setOrderMoney(((Number)map.get("ordermoney")).doubleValue());
		ati.setOrderTime((Timestamp)map.get("ordertime"));
		ati.setAdweb_trxurl((String)map.get(("adweb_trxurl")));
		return ati;
	}

	@Override
	public void updateWebTrxStatus(String trxOrderId) {
		String sql="update beiker_adwebtrxinfo set status='2'  where trxorderid=?";
		this.getSimpleJdbcTemplate().update(sql, trxOrderId);
	}

	@Override
	public List<AdWebTrxInfo> getAdWebTrxInfoList(String fromDate,
			String endDate, String trxOrderId) {
		String sql="select adweb_wi,trxorderid,status,ordertime from beiker_adwebtrxinfo where ordertime>=? and ordertime<=? and adweb_cid=?";
		List list=getSimpleJdbcTemplate().queryForList(sql,fromDate,endDate,trxOrderId);
		List<AdWebTrxInfo> listTrxInfo=new ArrayList<AdWebTrxInfo>();
		if(list==null||list.size()==0)return null;
		for (int i = 0; i < list.size(); i++) {
			Map map=(Map) list.get(i);
			AdWebTrxInfo ati=new AdWebTrxInfo();
			ati.setTrxorderid((String)map.get("trxorderid"));
			ati.setTrxStatus((String)map.get("status"));
			ati.setOrderTime((Timestamp)map.get("ordertime"));
			ati.setAdwi((String)map.get("adweb_wi"));
			listTrxInfo.add(ati);
		}
		
		return listTrxInfo;
		
	}
}