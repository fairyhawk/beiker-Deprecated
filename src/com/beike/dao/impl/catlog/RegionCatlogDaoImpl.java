package com.beike.dao.impl.catlog;

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
import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.util.PinyinUtil;

/**
 * <p>
 * Title:类别属性操作
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
 * @date May 24, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("regionCatlogDao")
public class RegionCatlogDaoImpl extends GenericDaoImpl<RegionCatlog, Integer> implements RegionCatlogDao {

	public Map<Long, List<RegionCatlog>> getAllCatlog() {
		String sql = "select brp.id,brp.region_name,brp.parentid from beiker_region_property brp";

		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		Map<Long, List<RegionCatlog>> rmap = new HashMap<Long, List<RegionCatlog>>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long regionid = null;
			if (map.get("id") != null) {
				regionid = ((Number) map.get("id")).longValue();
			}
			String regionName = (String) map.get("region_name");
			Long parentId = null;
			if (map.get("parentid") != null) {
				parentId = ((Number) map.get("parentid")).longValue();
			}
			RegionCatlog regionCatlog = new RegionCatlog();
			regionCatlog.setCatlogid(regionid);
			regionCatlog.setCatlogName(regionName);
			regionCatlog.setParentId(parentId);

			List<RegionCatlog> regionList = rmap.get(parentId);
			if (regionList == null) {
				regionList = new ArrayList<RegionCatlog>();
			}

			regionList.add(regionCatlog);
			rmap.put(parentId, regionList);
		}
		return rmap;

	}

	public Map<String, Map<Long, List<RegionCatlog>>> getAllCityCatlog() {

		String sql = "select brp.id,brp.region_name,brp.parentid,ba.area_cn_name,brp.region_enname  from beiker_region_property brp left join beiker_area ba on brp.areaid=ba.area_id where ba.area_parent_id!=0";

		Map<String, Map<Long, List<RegionCatlog>>> regionMap = new HashMap<String, Map<Long, List<RegionCatlog>>>();

		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		// Map<Long, List<RegionCatlog>> rmap = new HashMap<Long,
		// List<RegionCatlog>>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long regionid = null;
			if (map.get("id") != null) {
				regionid = ((Number) map.get("id")).longValue();
			}
			String regionName = (String) map.get("region_name");
			Long parentId = null;
			if (map.get("parentid") != null) {
				parentId = ((Number) map.get("parentid")).longValue();
			}
			RegionCatlog regionCatlog = new RegionCatlog();
			regionCatlog.setCatlogid(regionid);
			regionCatlog.setCatlogName(regionName);
			regionCatlog.setParentId(parentId);

			String areaname = (String) map.get("area_cn_name");
			if (areaname != null) {
				if (areaname.length() > 1) {
					String a = areaname.charAt(areaname.length() - 1) + "";
					if (a.equals("市") || a.equals("省")) {
						areaname = areaname.substring(0, areaname.length() - 1);
					}
				}
			}
			String areastr = PinyinUtil.hanziToPinyin(areaname, "");
			String region_enname = "";
			if (map.get("region_enname") != null) {
				region_enname = (String) map.get("region_enname");
			}
			//////////////////////////////////////
			//update by ye.tian 2011.9.5
			regionCatlog.setRegion_enname(region_enname);
			////////////////////////////////
			Map<Long, List<RegionCatlog>> mm = regionMap.get(areastr);
			if (mm == null) {
				mm = new HashMap<Long, List<RegionCatlog>>();
			}
			List<RegionCatlog> regionList = mm.get(parentId);
			if (regionList == null) {
				regionList = new ArrayList<RegionCatlog>();
			}

			// 设置地域初始url
			// CatlogUtils.setInitUrl(true, regionCatlog);
			regionList.add(regionCatlog);

			mm.put(parentId, regionList);

			regionMap.put(areastr, mm);
		}
		return regionMap;

	}

	public Map<String, Long> getCityCatlog() {
		String sql = "select ba.area_id,ba.area_en_name from beiker_area ba where ba.area_parent_id!=0";
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		Map<String, Long> mapCity = new HashMap<String, Long>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long areaid = (Long) map.get("area_id");
			String areaname = (String) map.get("area_en_name");
			mapCity.put(areaname.toLowerCase().trim(), areaid);
		}
		return mapCity;

	}

	@Override
	public Map<String, Object> getCityIdByRegion(Long regionId) {
		String sql = "select brp.areaid,ba.area_en_name from beiker_region_property brp left join beiker_area ba on brp.areaid=ba.area_id where brp.id=?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { regionId });
		if (list == null || list.size() == 0)
			return new HashMap<String, Object>();
		Map map = (Map) list.get(0);
		Long areaid = (Long) map.get("areaid");
		String areaname = (String) map.get("area_en_name");

		Map<String, Object> rmap = new HashMap<String, Object>();
		rmap.put("areaid", areaid);
		if (areaname != null) {
			areaname = areaname.toLowerCase();
		}
		rmap.put("areaname", areaname);

		return rmap;
	}

	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagId(int parentId) {

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getRegionCatlog(int cityid, int parentid) {

		List<Map<String, Object>> areaList = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,region_name,parentid FROM beiker_region_property ");
		sql.append("WHERE areaid = ? and parentid = ?");
		areaList = this.getJdbcTemplate().queryForList(sql.toString(), new Object[] { cityid, parentid });
		return areaList;

	}

	@Override
	public Map<Long, Map<Long, List<RegionCatlog>>> getAllCatlogHavingCity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagIdHavingCity(int parentId, Long cityid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, List<RegionCatlog>> getAllLabelCatlogProperty() {
		return null;
	}

	@Override
	public List<Map<String, Object>> getGoodsAllCatlogByCityId(Long cityid) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getGoodsAllRegionByCityId(Long cityid) {

		List<Map<String, Object>> listGoodAllReg = new ArrayList<Map<String, Object>>();
		StringBuilder region = new StringBuilder();

		region.append("SELECT id,region_name,parentid FROM beiker_region_property ");
		region.append("WHERE areaid = ").append(cityid).append(" ORDER BY id ASC");

		listGoodAllReg = this.getJdbcTemplate().queryForList(region.toString());

		return listGoodAllReg;
	}

	@Override
	public List<RegionCatlog> queryByIds(List<Long> ids) {
		String sql = "select id,region_name from beiker_region_property where id in (:ids)";
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ids", ids);
		List<RegionCatlog> result = getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<RegionCatlog>() {
			@Override
			public RegionCatlog mapRow(ResultSet rs, int rowNum) throws SQLException {
				RegionCatlog regionCatlog = new RegionCatlog();
				regionCatlog.setCatlogid(rs.getLong("id"));
				regionCatlog.setCatlogName(rs.getString("region_name"));
				return regionCatlog;
			}
		}, mapSqlParameterSource);
		return result;
	}

}
