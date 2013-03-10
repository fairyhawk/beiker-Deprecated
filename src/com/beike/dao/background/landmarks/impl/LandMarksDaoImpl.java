package com.beike.dao.background.landmarks.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.landmarks.LandMarksDao;
import com.beike.entity.background.landmarks.LandMarks;
import com.beike.form.background.landmarks.LandMarksForm;
import com.beike.util.StringUtils;
/**
 * Title : 	LandMarksDaoImpl
 * <p/>
 * Description	:	地标数据访问实现
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
 * <pre>1     2011-06-08    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-08  
 */
@Repository("landMarksDao")
public class LandMarksDaoImpl extends GenericDaoImpl<LandMarks,Long> implements LandMarksDao {

	/*
	 * @see com.beike.dao.background.landmarks.LandMarksDao#queryLandMarks()
	 */
	@SuppressWarnings("unchecked")
	public List<LandMarks> queryLandMarks(LandMarksForm landMarksForm) throws Exception {
		List tempList = null;
		List<LandMarks> landMarksList = new ArrayList<LandMarks>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id,region_name,parentid,area_id  FROM beiker_region_property ");
		sql.append(" WHERE parentid = ? ");
		if(landMarksForm.getAreaId()>0){
			sql.append(" AND area_id = ").append(landMarksForm.getAreaId());
		}
		Object[] params = new Object[]{landMarksForm.getParentId()};
		int[] types = new int[]{Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			landMarksList = this.convertResultToObjectList(tempList);
		}
		return landMarksList;
	}

	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<LandMarks> convertResultToObjectList(List results) throws Exception{
        List<LandMarks> objList = new ArrayList<LandMarks>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                LandMarks landMarks = this.convertResultMapToObject(result);
                objList.add(landMarks);
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
	private LandMarks convertResultMapToObject(Map result) throws Exception{
		LandMarks obj = new LandMarks();
			if (result != null) {
				Long id = ((Number)result.get("id")).longValue();
				if(null!=id){
					obj.setId(id.intValue());
				}
				if(StringUtils.validNull((String) result.get("region_name"))){
					obj.setRegionName(result.get("region_name").toString());
				}
				
				Long parentId = ((Number)result.get("parentid")).longValue();
				if(null!=parentId){
					obj.setParentId(parentId.intValue());
				}
			}
		return obj;
	}
}
