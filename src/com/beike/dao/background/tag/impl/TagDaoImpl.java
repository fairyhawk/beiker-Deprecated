package com.beike.dao.background.tag.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.tag.TagDao;
import com.beike.entity.background.tag.Tag;
import com.beike.form.background.tag.TagForm;
import com.beike.util.StringUtils;
/**
 * Title : 	TagDaoImpl
 * <p/>
 * Description	:	标签数据访问实现
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
 * <pre>1     2011-06-03    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03  
 */
@Repository("tagDao")
public class TagDaoImpl extends GenericDaoImpl<Tag,Long> implements TagDao {

	/*
	 * @see com.beike.dao.background.tag.TagDao#queryTag(com.beike.form.background.tag.TagForm)
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> queryTag(TagForm tagForm) throws Exception {
		List<Tag> tagList = null;
		List tempList = null;
		String sql = "SELECT * FROM beiker_tag_property WHERE parentid = ? ";
		Object[] params = new Object[]{tagForm.getParentid()};
		int[] types = new int[]{Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql,params,types);
		if(null!=tempList&&tempList.size()>0){
			tagList = this.convertResultToObjectList(tempList);
		}
		return tagList;
	}
	
	/*
	 * @see com.beike.dao.background.tag.TagDao#queryTagMap()
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> queryTagMap() throws Exception {
		Map<Integer,String> tagMap = new HashMap<Integer,String>();
		String sql = "SELECT id,tag_name,parentid FROM beiker_tag_property ";
		List tempList = null;
		List<Tag> tagList = null;
		tempList = this.getJdbcTemplate().queryForList(sql);
		if(null!=tempList&&tempList.size()>0){
			tagList = this.convertResultToObjectList(tempList);
			for(Tag tag : tagList){
				tagMap.put(tag.getId(), tag.getTagName());
			}
		}
		return tagMap;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<Tag> convertResultToObjectList(List results) throws Exception{
        List<Tag> objList = new ArrayList<Tag>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                Tag tag = this.convertResultMapToObject(result);
                objList.add(tag);
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
	private Tag convertResultMapToObject(Map result) throws Exception{
		Tag obj = new Tag();
			if (result != null) {
				Long tagId = ((Number)result.get("id")).longValue();
				if(null!=tagId){
					obj.setId(tagId.intValue());
				}
				if(StringUtils.validNull((String)result.get("tag_name"))){
					obj.setTagName(result.get("tag_name").toString());
				}
				Long parentId = ((Number)result.get("parentid")).longValue();
				if(null!=parentId){
					obj.setParentid(parentId.intValue());
				}
			}
		return obj;
	}

}
