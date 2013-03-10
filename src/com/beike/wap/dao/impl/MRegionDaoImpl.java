package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MRegionDao;
import com.beike.wap.entity.MRegion;

/**
 * <p>
 * Title:热门地标数据库实现
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
 * @date 2011-10-10
 * @author lvjx
 * @version 1.0
 */
@Repository("wapRegionDao")
public class MRegionDaoImpl extends GenericDaoImpl<MRegion, Long> implements
		MRegionDao {

	/*
	 * @see com.beike.wap.dao.region.MRegionDao#addWapHotRegion(java.util.List)
	 */
	@Override
	public int addWapHotRegion(final List<MRegion> wapRegionList)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_wap_hotregion_info (region_id,region_name,region_area,region_date) ");
		sql.append("VALUES (?,?,?,?) ");
		int[] count = this.getJdbcTemplate().batchUpdate(sql.toString(),
				new BatchPreparedStatementSetter() {

					@Override
					public int getBatchSize() {
						// TODO Auto-generated method stub
						return wapRegionList.size();
					}

					@Override
					public void setValues(java.sql.PreparedStatement ps, int i)
							throws SQLException {
						MRegion mRegion = wapRegionList.get(i);
						ps.setInt(1, mRegion.getRegionId());
						ps.setString(2, mRegion.getRegionName());
						ps.setString(3, mRegion.getRegionArea());
						ps.setDate(4, mRegion.getRegionDate());
					}
				});
		return count.length;
	}

	/*
	 * @see
	 * com.beike.wap.dao.region.MRegionDao#queryWapHotRegion(java.util.Date,
	 * java.lang.String)
	 */
	@Override
	public int queryWapHotRegion(Date currentDate, String typeArea)
			throws Exception {
		int sum = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_wap_hotregion_info WHERE region_area = ? AND region_date = ? ");
		int[] types = new int[] { Types.VARCHAR, Types.DATE };
		Object[] params = new Object[] { typeArea, currentDate };
		sum = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		return sum;
	}

	/*
	 * @see
	 * com.beike.wap.dao.region.MRegionDao#queryWapHotRegionData(java.util.Date,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MRegion> queryWapHotRegionData(Date currentDate, String typeArea)
			throws Exception {
		List tempList = null;
		List<MRegion> regionList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT region_id,region_name FROM beiker_wap_hotregion_info ");
		sql.append("WHERE region_area = ? AND region_date = ? ");
		int[] types = new int[] { Types.VARCHAR, Types.DATE };
		Object[] params = new Object[] { typeArea, currentDate };
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params,
				types);
		MRegion mRegion = null;
		if (null != tempList && tempList.size() > 0) {
			regionList = new ArrayList<MRegion>();
			for (int i = 0; i < tempList.size(); i++) {
				mRegion = new MRegion();
				Map result = (Map) tempList.get(i);
				int regionId = ((Number) result.get("region_id")).intValue();
				if (regionId > 0) {
					mRegion.setRegionArea(String.valueOf(regionId));
					//mRegion.setRegionId(regionId);
				}
				if (StringUtils.validNull((String) result.get("region_name"))) {
					mRegion.setRegionName(result.get("region_name").toString());
				}
				regionList.add(mRegion);
			}
		}
		return regionList;
	}
	
	
	/*
	 * @see com.beike.wap.dao.region.MRegionDao#queryRegion(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MRegion> queryRegion(String regionId,String areaId) throws Exception {
		
		// 判断id是否为数字，如果不是数字，抛异常
		Long.parseLong(regionId);
		Long.parseLong(areaId);
		
		List tempList = null;
		List<MRegion> regionList = new ArrayList<MRegion>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,region_name  FROM beiker_region_property  ");
		sql.append("WHERE parentid = ? AND areaid =  ? ");
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		Object[] params = new Object[]{regionId,areaId};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params, types);
		if(null!=tempList && tempList.size()>0){
			MRegion region = null;
			for(int i=0;i<tempList.size();i++){
				region = new MRegion();
				Map result = (Map)tempList.get(i);
				Integer id = ((Number)result.get("id")).intValue();
				if(id>0){
					region.setId(id);
				}
				if(StringUtils.validNull((String)result.get("region_name"))){
					region.setRegionName(result.get("region_name").toString());
				}
				regionList.add(region);
			}
		}
		return regionList;
	}
	
	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#queryRegionInfo(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryRegionInfo(String regionId, String areaId)
			throws Exception {
		
		Long.parseLong(regionId);
		Long.parseLong(areaId);
		
		List tempList = null;
		Map<String,String> regionMap = new TreeMap<String,String>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,region_name  FROM beiker_region_property  ");
		sql.append("WHERE parentid = ? AND areaid =  ? ");
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		Object[] params = new Object[]{regionId,areaId};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params, types);
		if(null!=tempList && tempList.size()>0){
			MRegion region = null;
			for(int i=0;i<tempList.size();i++){
				region = new MRegion();
				Map result = (Map)tempList.get(i);
				Integer id = ((Number)result.get("id")).intValue();
				if(id>0){
					region.setId(id);
				}
				if(StringUtils.validNull((String)result.get("region_name"))){
					region.setRegionName(result.get("region_name").toString());
				}
				regionMap.put(String.valueOf(region.getId()), region.getRegionName());
			}
		}
		return regionMap;
	}
	
	/*
	 * @see com.beike.wap.dao.MRegionDao#queryRegionParentId(java.lang.String)
	 */
	@Override
	public int queryRegionParentId(String regionId) throws Exception {
		
		Long.parseLong(regionId);
		
		String sql = "SELECT parentid FROM beiker_region_property WHERE id = ? ";
		int[] types = new int[]{Types.INTEGER};
		Object[] params = new Object[]{regionId};
		int parendId = this.getJdbcTemplate().queryForInt(sql, params, types);
		return parendId;
	}
	
	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MRegion findRegionById(long regionId) throws Exception {
		String sql = "SELECT * FROM beiker_region_property WHERE id = ?";
		MRegion region = null;
		try {
			List<MRegion> regionList = getSimpleJdbcTemplate().query(sql.toString(),
					new RowMapperImpl(), regionId);
			if (regionList != null && regionList.size() != 0) {
				region = regionList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return region;
	}
	
	/*
	 * @see com.beike.wap.dao.MRegionDao#queryMaxDate(java.lang.String)
	 */
	@Override
	public Date queryMaxDate(String cityName) throws Exception {
		String sql = "SELECT MAX(region_date) FROM beiker_wap_hotregion_info WHERE region_area = ? ";
		int[] types = new int[]{Types.VARCHAR};
		Object[] params = new Object[]{cityName};
		Date maxDate = (Date)this.getJdbcTemplate().queryForObject(sql, params, types, Date.class);
		return maxDate;
	}

	
	protected class RowMapperImpl implements ParameterizedRowMapper<MRegion> {
		public MRegion mapRow(ResultSet rs, int rowNum) throws SQLException {
			MRegion region = new MRegion();
			region.setId(rs.getInt("id"));
			region.setRegionName(rs.getString("region_name"));
			return region;
		}
	}

	/*
	 * @see com.beike.wap.dao.MRegionDao#queryHotRegionData(java.sql.Date, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<MRegion> queryHotRegionData(java.sql.Date nowDate,
			String regionArea, String hotRegionEnName, int areaId)
			throws Exception {
		List tempList = null;
		List<MRegion> regionList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,region_name  FROM beiker_region_property  ");
		sql.append("WHERE areaid =  ").append(areaId);
		if(StringUtils.validNull(hotRegionEnName)){
			sql.append(" and region_enname in (");
			sql.append(hotRegionEnName);
			sql.append(") group by region_name ");
		}
		else{
			// 如果没有热点信息，查询6条记录
			sql.append(" limit 0, 6");
		}
		
		tempList = getJdbcTemplate().queryForList(sql.toString());
		
		if(null!=tempList && tempList.size()>0){
			regionList = new ArrayList<MRegion>();
			MRegion region = null;
			for(int i=0;i<tempList.size();i++){
				region = new MRegion();
				Map result = (Map)tempList.get(i);
				if(null!=result.get("id")){
					Integer id = ((Number)result.get("id")).intValue();
					region.setRegionId(id);
				}
				if(null!=result.get("region_name")){
					region.setRegionName(result.get("region_name").toString());
				}
				
				region.setRegionArea(regionArea);
				region.setRegionDate(nowDate);
				regionList.add(region);
			}
		}
		return regionList;
	}

	/*
	 * @see com.beike.wap.dao.MRegionDao#queryAreaId(java.lang.String)
	 */
	@Override
	public int queryAreaId(String areaEnName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("select area_id from beiker_area where upper(area_en_name) = ? and area_en_name is not null and area_en_name!='' and area_parent_id !=0 ");
		Object[] param = new Object[]{areaEnName};
		int[] types = new int[]{Types.VARCHAR};
		int areaId = this.getJdbcTemplate().queryForInt(sql.toString(), param, types);
		return areaId;
	}

}
