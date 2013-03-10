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
* @Title: LabelCatlogDaoImpl.java
* @Package com.beike.dao.impl.catlog
* @Description: TODO
* @author wenjie.mai  
* @date Jul 10, 2012 5:30:06 PM
* @version V1.0  
*/
@Repository("labelCatlogDao")
public class LabelCatlogDaoImpl extends GenericDaoImpl implements RegionCatlogDao {

	
	public LabelCatlogDaoImpl() {

	}

	
	@Override
	public Map<Long, List<RegionCatlog>> getAllCatlog() {
		return null;
	}

	@Override
	public Map<Long, Map<Long, List<RegionCatlog>>> getAllCatlogHavingCity() {
		return null;
	}

	
	@Override
	public Map<String, Map<Long, List<RegionCatlog>>> getAllCityCatlog() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Map<String, Long> getCityCatlog() {
		return null;
	}

	
	@Override
	public Map<String, Object> getCityIdByRegion(Long regionId) {
		return null;
	}

	
	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagId(int parentId) {
		return null;
	}

	
	@Override
	public List<Map<String, Object>> getGoodsCatlogByTagIdHavingCity(
			int parentId, Long cityid) {
		return null;
	}

	
	@Override
	public List<Map<String, Object>> getRegionCatlog(int cityid, int parentid) {
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<RegionCatlog>> getAllLabelCatlogProperty() {
		
		StringBuilder label_sql = new StringBuilder();
		
		label_sql.append("SELECT id,tagname,tag_enname,parentid FROM beiker_biaoqian_property ORDER BY id ASC ");
		
		List labellist = this.getJdbcTemplate().queryForList(label_sql.toString());
		
		if(labellist == null || labellist.size() == 0)
				return null;
		
		Map<Long, List<RegionCatlog>> labelMap = new HashMap<Long, List<RegionCatlog>>();
		
		for(int i=0;i<labellist.size();i++){
			Map mx = (Map) labellist.get(i);
			
			Long id = null;
			if(mx.get("id") != null){
				id = (Long) mx.get("id");
			}
			String tagName = (String) mx.get("tagname");
			String enName  = (String) mx.get("tag_enname");
			Long parentid  = null;
			if(mx.get("parentid") != null){
				parentid = (Long) mx.get("parentid");
			}
			
			RegionCatlog regionCatlog = new RegionCatlog();
			regionCatlog.setCatlogid(id);
			regionCatlog.setCatlogName(tagName);
			regionCatlog.setRegion_enname(enName);
			regionCatlog.setParentId(parentid);
			
			List<RegionCatlog> labelL = labelMap.get(parentid);
			if(labelL == null){
				labelL = new ArrayList<RegionCatlog>();
			}
			
			labelL.add(regionCatlog);
			labelMap.put(parentid,labelL);
		}
		return labelMap;
	}


	@Override
	public List<Map<String, Object>> getGoodsAllCatlogByCityId(Long cityid) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> getGoodsAllRegionByCityId(Long cityid) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<RegionCatlog> queryByIds(List<Long> ids) {
		return null;
	}
	
}
