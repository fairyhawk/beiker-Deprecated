package com.beike.dao.background.area.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.area.AreaDao;
import com.beike.entity.background.area.Area;
import com.beike.util.StringUtils;

/**
 * Title : 	AreaDaoImpl
 * <p/>
 * Description	:	地市数据访问实现
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-31    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-31  
 */
@Repository("areaDao")
public class AreaDaoImpl extends GenericDaoImpl<Area,Long> implements AreaDao {

	/*
	 * @see com.beike.dao.background.area.AreaDao#queryArea(com.beike.entity.background.area.Area)
	 */
	@SuppressWarnings("unchecked")
	public List<Area> queryArea(Area area) throws Exception {
		List tempList = null;
		List<Area> areaList = new ArrayList<Area>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT area_id,area_cn_name,area_en_name,area_parent_id,area_level FROM beiker_area ");
		sql.append(" WHERE area_parent_id = ? AND area_is_active = ? ");
		Object[] params = new Object[]{area.getAreaParentId(),area.getAreaIsActive()};
		int[] types = new int[]{Types.INTEGER,Types.VARCHAR};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params, types);
		if(null!=tempList&&tempList.size()>0){
			areaList = this.convertResultToObjectList(tempList);
		}
		return areaList;
	}

	/*
	 * @see com.beike.dao.background.area.AreaDao#queryAreaMap()
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> queryAreaMap() throws Exception {
		Map<Integer,String> areaMap = new HashMap<Integer,String>();
		String sql = "SELECT area_id,area_cn_name FROM beiker_area ";
		List<Area> areaList = null;
		List tempList = this.getJdbcTemplate().queryForList(sql);
		if(null!=tempList&&tempList.size()>0){
			areaList = this.convertResultToObjectList(tempList);
			for(Area area : areaList){
				areaMap.put(area.getAreaId(), area.getAreaCnName());
			}
		}
		return areaMap;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<Area> convertResultToObjectList(List results) throws Exception{
        List<Area> objList = new ArrayList<Area>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                Area area = this.convertResultMapToObject(result);
                objList.add(area);
            }
        }
        return objList;
    }
    
    /**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result   jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private Area convertResultMapToObject(Map result) throws Exception{
		Area obj = new Area();
			if (result != null) {
				Long areaId = (Long)result.get("area_id");
				if(null!=areaId){
					obj.setAreaId(areaId.intValue());
				}
				if(StringUtils.validNull((String) result.get("area_cn_name"))){
					obj.setAreaCnName(result.get("area_cn_name").toString());
				}
				if(StringUtils.validNull((String) result.get("area_en_name"))){
					obj.setAreaEnName(result.get("area_en_name").toString());
				}
				Long parentId = (Long)result.get("area_parent_id");
				if(null!=parentId){
					obj.setAreaParentId(parentId.intValue());
				}
				if(StringUtils.validNull((String) result.get("area_code"))){
					obj.setAreaCode(result.get("area_code").toString());
				}
				if(StringUtils.validNull((String) result.get("area_type"))){
					obj.setAreaType(result.get("area_type").toString());
				}
				Long levelId = (Long)result.get("area_level");
				if(null!=levelId){
					obj.setAreaLevel(levelId.intValue());
				}
				if(StringUtils.validNull((String) result.get("area_is_active"))){
					obj.setAreaIsActive(result.get("area_is_active").toString());
				}
			}
		return obj;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> queryOnlineArea() throws Exception{
		
		List<Map<String,Object>> areaList = new ArrayList<Map<String,Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT area_id,area_cn_name,area_en_name,area_parent_id FROM beiker_area ");
		sql.append("WHERE area_is_online='1'");
		areaList = this.getJdbcTemplate().queryForList(sql.toString());
		return areaList;
		
	}
	
	@Override
	public List<Area> getOnlineArea() throws Exception{
		List<Area> areaList = new ArrayList<Area>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT area_id,area_cn_name,area_en_name,area_parent_id,area_is_active FROM beiker_area ");
		sql.append(" WHERE area_is_online='1'");
		List tempList = this.getJdbcTemplate().queryForList(sql.toString());
		if(null!=tempList && tempList.size()>0){
			areaList = this.convertResultToObjectList(tempList);
		}
		return areaList;
	}
}
