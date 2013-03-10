package com.beike.dao.miaosha.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.miaosha.MiaoShaDao;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.page.Pager;
import com.beike.util.StringUtils;

/**      
 * project:beiker  
 * Title:秒杀DAO实现
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:29:15 PM     
 * @version 1.0
 */
@Repository("miaoShaDao")
public class MiaoShaDaoImpl extends GenericDaoImpl<MiaoSha,Long> implements MiaoShaDao {

	@Override
	public MiaoSha getMiaoShaById(Long msId) {
		String selSql = "select id,goods_id,m_title,m_short_title,m_pay_price,m_maxcount,m_settle_price,m_single_count,m_start_time,m_end_time,m_show_start_time,m_show_end_time,m_banner,is_used,is_need_virtual,m_virtual_count,m_settle_price,m_sale_count FROM beiker_miaosha where id=?";
		MiaoSha miaosha = null;
		try{
			miaosha = this.getSimpleJdbcTemplate().queryForObject(selSql,
					new RowMapperImpl(), msId);
		}catch(Exception ex){
			miaosha = null;
		}
		return miaosha;
	}

	@Override
	public int getMiaoShaCount(Long areaId, int status) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("SELECT count(ms.id) msCount ");
		bufSelSql.append("FROM beiker_miaosha ms ");
		bufSelSql.append("JOIN beiker_goods_merchant gm ON ms.goods_id=gm.goodsid ");
		bufSelSql.append("JOIN beiker_merchant bm ON gm.merchantid=bm.merchantid ");
		//已结束
		if(status == 0){
			bufSelSql.append("WHERE ms.is_used=1 AND (ms.m_end_time<=NOW() OR ms.m_maxcount<=ms.m_sale_count) AND bm.areaid=? AND bm.parentId=0 ");
		}else{
		//进行中
			bufSelSql.append("WHERE ms.is_used=1 AND ms.m_end_time>NOW() AND ms.m_maxcount>ms.m_sale_count AND bm.areaid=? AND bm.parentId=0 ");
		}
		return this.getSimpleJdbcTemplate().queryForInt(bufSelSql.toString(),areaId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getMiaoShaIdsByPage(Long areaId, int status, Pager pager) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("SELECT ms.id ");
		bufSelSql.append("FROM beiker_miaosha ms ");
		bufSelSql.append("JOIN beiker_goods_merchant gm ON ms.goods_id=gm.goodsid ");
		bufSelSql.append("JOIN beiker_merchant bm ON gm.merchantid=bm.merchantid ");
		//已结束
		if(status == 0){
			bufSelSql.append("WHERE ms.is_used=1 AND (ms.m_end_time<=NOW() OR ms.m_maxcount<=ms.m_sale_count) AND bm.areaid=? AND bm.parentId=0 ");
			bufSelSql.append("ORDER BY ms.m_start_time,ms.id LIMIT ?,?");
		}else if(status == 1){
		//进行中
			bufSelSql.append("WHERE ms.is_used=1 AND ms.m_end_time>NOW() AND ms.m_maxcount>ms.m_sale_count AND bm.areaid=? AND bm.parentId=0 ");
			bufSelSql.append("ORDER BY ms.m_start_time,ms.id LIMIT ?,?");
		}
		int start = pager.getStartRow();
		int pageSize = pager.getPageSize();
		
		return this.getJdbcTemplate().queryForList(bufSelSql.toString(), new Object[]{areaId,start,pageSize},Long.class);
	}

	@Override
	public List<MiaoSha> getMiaoShaListByIds(List<Long> lstMsIds) {
		if(lstMsIds!=null && lstMsIds.size()>0){
			String inIds = StringUtils.arrayToString(lstMsIds.toArray(),",");
			StringBuilder bufSelSql = new StringBuilder();
			bufSelSql.append("select id,goods_id,m_title,m_short_title,m_pay_price,m_maxcount, ");
			bufSelSql.append("m_settle_price,m_single_count,m_start_time,m_end_time,m_show_start_time, ");
			bufSelSql.append("m_show_end_time,m_banner,is_used,is_need_virtual,m_virtual_count,m_settle_price,m_sale_count FROM beiker_miaosha ");
			bufSelSql.append("where id in (").append(inIds).append(")");
			bufSelSql.append("order by find_in_set(id,'").append(inIds).append("')");
			return this.getSimpleJdbcTemplate().query(bufSelSql.toString(),new RowMapperImpl());
		}else{
			return null;
		}
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<MiaoSha> {
		@Override
		public MiaoSha mapRow(ResultSet rs, int rowNum) throws SQLException {
			MiaoSha miaosha = new MiaoSha();
			miaosha.setMsId(rs.getLong("id"));
			miaosha.setGoodsId(rs.getLong("goods_id"));
			miaosha.setMsTitle(rs.getString("m_title"));
			miaosha.setMsShortTitle(rs.getString("m_short_title"));
			miaosha.setMsPayPrice(rs.getDouble("m_pay_price"));
			miaosha.setMsMaxCount(rs.getInt("m_maxcount"));
			miaosha.setMsSingleCount(rs.getInt("m_single_count"));
			miaosha.setMsStartTime(rs.getTimestamp("m_start_time"));
			miaosha.setMsEndTime(rs.getTimestamp("m_end_time"));
			miaosha.setMsShowStartTime(rs.getTimestamp("m_show_start_time"));
			miaosha.setMsShowEndTime(rs.getTimestamp("m_show_end_time"));
			miaosha.setMsBanner(rs.getString("m_banner"));
			miaosha.setMsStatus(rs.getInt("is_used"));
			miaosha.setNeedVirtual(rs.getInt("is_need_virtual"));
			miaosha.setMsVirtualCount(rs.getInt("m_virtual_count"));
			miaosha.setMsSettlePrice(rs.getDouble("m_settle_price"));
			miaosha.setMsSaleCount(rs.getInt("m_sale_count"));
			return miaosha;
		}
	}

	@Override
	public List<MiaoSha> getMiaoShaListByAreaId(Long areaId, int count) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("SELECT ms.id,ms.goods_id,ms.m_title,ms.m_short_title, ");
		bufSelSql.append("ms.m_pay_price,ms.m_maxcount,ms.m_settle_price, ");
		bufSelSql.append("ms.m_single_count,ms.m_start_time,ms.m_end_time, ");
		bufSelSql.append("ms.m_show_start_time,ms.m_show_end_time,ms.m_banner, ");
		bufSelSql.append("ms.is_used,ms.is_need_virtual,ms.m_virtual_count,ms.m_settle_price,m_sale_count ");
		bufSelSql.append("FROM beiker_miaosha ms ");
		bufSelSql.append("JOIN beiker_goods_merchant gm ON ms.goods_id=gm.goodsid ");
		bufSelSql.append("JOIN beiker_merchant bm ON gm.merchantid=bm.merchantid ");
		bufSelSql.append("WHERE ms.is_used=1 AND ms.m_end_time>NOW() AND ms.m_sale_count<ms.m_maxcount AND bm.areaid=? AND bm.parentId=0 ");
		bufSelSql.append("ORDER BY ms.m_start_time,ms.id LIMIT ? ");
		return this.getSimpleJdbcTemplate().query(bufSelSql.toString(),new RowMapperImpl(),areaId,count);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getIndexMiaoShaByCityId(Long areaId) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("SELECT ms.id ");
		bufSelSql.append("FROM beiker_miaosha ms ");
		bufSelSql.append("JOIN beiker_goods_merchant gm ON ms.goods_id=gm.goodsid ");
		bufSelSql.append("JOIN beiker_merchant bm ON gm.merchantid=bm.merchantid ");
		bufSelSql.append("WHERE ms.is_used=1 AND ms.m_show_end_time>NOW() AND ms.m_banner <> '' AND bm.areaid=? AND bm.parentId=0 ");
		bufSelSql.append("ORDER BY ms.m_show_start_time,ms.id LIMIT 2");
		return this.getJdbcTemplate().queryForList(bufSelSql.toString(), new Object[]{areaId},Long.class);
	}

	@Override
	public List<Map<String,Object>> getNextBeginMiaoShaIDs(String timeS, String timeE) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("SELECT ms.id,ms.m_short_title,ms.m_start_time FROM beiker_miaosha ms ");
		bufSelSql.append("where ms.is_used=1 and ms.m_start_time between ? and ? and ms.m_end_time>now() ");
		return this.getSimpleJdbcTemplate().queryForList(bufSelSql.toString(), new Object[]{timeS,timeE});
	}
}