package com.beike.dao.impl.catlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.entity.catlog.RegionCatlog;

/**
 * <p>
 * Title:属性分类查询
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
 * @date May 24, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("propertyCatlogDao")
public class PropertyCatlogDaoImpl extends
		GenericDaoImpl<RegionCatlog, Integer> implements RegionCatlogDao {

	public Map<Long, List<RegionCatlog>> getAllCatlog() {
		String sql = "select brp.id,brp.tag_name,brp.parentid,brp.tag_enname from beiker_tag_property brp order by brp.boost desc";

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
			String regionName = (String) map.get("tag_name");
			Long parentId = null;
			if (map.get("parentid") != null) {
				parentId = ((Number) map.get("parentid")).longValue();
			}
			String tag_enname="";
			if(map.get("tag_enname")!=null){
				tag_enname=(String) map.get("tag_enname");
			}
			
			RegionCatlog regionCatlog = new RegionCatlog();
			regionCatlog.setCatlogid(regionid);
			regionCatlog.setCatlogName(regionName);
			regionCatlog.setParentId(parentId);
			/////////////////////////////////////
			//update by ye.tian at 2011.09.05 
			regionCatlog.setRegion_enname(tag_enname);
			/////////////////////////////////////
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
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Long> getCityCatlog() {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public Map<String, Object> getCityIdByRegion(Long regionId) {
		// TODO Auto-generated method stub
		return null;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagId(int parentId) {
		
		List<Map<String,Object>> listGoodsCatlog = new ArrayList<Map<String,Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,tag_name,parentid,boost FROM beiker_tag_property ");
		sql.append("WHERE parentid = ?");
		listGoodsCatlog = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{parentId});
		return listGoodsCatlog;
		
	}

	@Override
	public List<Map<String, Object>> getRegionCatlog(int cityid,int parentid) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, Map<Long, List<RegionCatlog>>> getAllCatlogHavingCity() {
		
		Map<Long, Map<Long, List<RegionCatlog>>>  propertyMap = null;
		
		//author wenjie.mai 新增属性分类关联表
		String sql = "SELECT DISTINCT(brp.id),brp.tag_name,brp.parentid,brp.tag_enname,bcr.areaid FROM beiker_tag_property brp " +
							"LEFT JOIN beiker_catlog_relation bcr ON brp.id = bcr.catlogid OR brp.id = bcr.catlogextid " +
							"WHERE bcr.catlogisavailable = 1 AND bcr.catlogextisavaliable = 1 ORDER BY bcr.areaid,brp.boost DESC";

		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		
		Map<Long, List<RegionCatlog>> rmap = null;
		propertyMap = new HashMap<Long, Map<Long, List<RegionCatlog>>>();
		
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long regionid = null;
			if (map.get("id") != null) {
				regionid = ((Number) map.get("id")).longValue();
			}
			String regionName = (String) map.get("tag_name");
			Long parentId = null;
			if (map.get("parentid") != null) {
				parentId = ((Number) map.get("parentid")).longValue();
			}
			String tag_enname="";
			if(map.get("tag_enname")!=null){
				tag_enname=(String) map.get("tag_enname");
			}
			
			Long cityId  = (Long) map.get("areaid");
						
			RegionCatlog regionCatlog = new RegionCatlog();
			regionCatlog.setCatlogid(regionid);
			regionCatlog.setCatlogName(regionName);
			regionCatlog.setParentId(parentId);
			regionCatlog.setRegion_enname(tag_enname);
			regionCatlog.setCityId(cityId);
			
			rmap = propertyMap.get(cityId);
			if(rmap == null)
				rmap = new HashMap<Long, List<RegionCatlog>>();
			
			List<RegionCatlog> regionList = rmap.get(parentId);
			if(regionList == null)
				regionList = new ArrayList<RegionCatlog>();
			
			regionList.add(regionCatlog);
			
			rmap.put(parentId, regionList);
			propertyMap.put(cityId, rmap);
		}
		return propertyMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagIdHavingCity(int parentId, Long cityid) {
		
		List<Map<String,Object>> listGoodsCatlog = new ArrayList<Map<String,Object>>();
		StringBuilder sql = new StringBuilder();
		
		//author wenjie.mai 新增属性分类关联表、按照城市区分
		sql.append("SELECT DISTINCT(brp.id),brp.tag_name,brp.parentid,brp.boost FROM beiker_tag_property brp ");
		sql.append("LEFT JOIN beiker_catlog_relation bcr ON brp.id = bcr.catlogid OR brp.id = bcr.catlogextid ");
		sql.append("WHERE bcr.catlogisavailable = 1 AND bcr.catlogextisavaliable = 1 AND brp.parentid = ? and bcr.areaid = ? ");
		
		listGoodsCatlog = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{parentId,cityid});
		return listGoodsCatlog;
	}

	@Override
	public Map<Long, List<RegionCatlog>> getAllLabelCatlogProperty() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getGoodsAllCatlogByCityId(Long cityid) {
		
		List<Map<String, Object>> listGoodAllCat = new ArrayList<Map<String,Object>>();
		StringBuilder catlog = new StringBuilder();
		
		catlog.append("SELECT DISTINCT(brp.id),brp.tag_name,brp.parentid,brp.boost FROM beiker_tag_property brp ");
		catlog.append("LEFT JOIN beiker_catlog_relation bcr ON brp.id = bcr.catlogid OR brp.id = bcr.catlogextid ");
		catlog.append("WHERE bcr.catlogisavailable = 1 AND bcr.catlogextisavaliable = 1 AND ");
		catlog.append("bcr.areaid = ").append(cityid).append(" ORDER BY brp.id ASC");
		
		listGoodAllCat = this.getJdbcTemplate().queryForList(catlog.toString());
		
		return listGoodAllCat;
	}

	
	@Override
	public List<Map<String, Object>> getGoodsAllRegionByCityId(Long cityid) {
		 
		return null;
	}

	@Override
	public List<RegionCatlog> queryByIds(List<Long> ids) {
		return null;
	}

}
