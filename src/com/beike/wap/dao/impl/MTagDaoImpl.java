package com.beike.wap.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MTagDao;
import com.beike.wap.entity.MTag;
/**
 * <p>
 * Title:分类信息数据库实现
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
 * @date 2011-10-17
 * @author lvjx
 * @version 1.0
 */
@Repository("wapTagDao")
public class MTagDaoImpl extends GenericDaoImpl<MTag, Long> implements MTagDao {

	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see com.beike.wap.dao.tag.MTagDao#queryTagByParendId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MTag> queryTagByParendId(int parentId) throws Exception {
		List tempList = null;
		List<MTag> tagList = new ArrayList<MTag>();
		String sql = "SELECT id,tag_name FROM beiker_tag_property WHERE parentid = ? ";
		Object[] params = new Object[]{parentId};
		int[] types = new int[]{Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql,params,types);
		if(null!=tempList&&tempList.size()>0){
			MTag tag = null;
			for(int i=0;i<tempList.size();i++){
				Map result = (Map)tempList.get(i);
				tag = new MTag();
				Integer id = ((Number)result.get("id")).intValue();
				if(null!=id){
					tag.setId(id);
				}
				if(StringUtils.validNull((String)result.get("tag_name"))){
					tag.setTagName(result.get("tag_name").toString());
				}
				tagList.add(tag);
			}
		}
		return tagList;
	}

	/*
	 * @see com.beike.wap.dao.tag.MTagDao#queryTagByParentIdInfo(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryTagByParentIdInfo(int parentId)
			throws Exception {
		Map<String,String> tagMap = new TreeMap<String,String>();
		List tempList = null;
		String sql = "SELECT id,tag_name FROM beiker_tag_property WHERE parentid = ? ";
		Object[] params = new Object[]{parentId};
		int[] types = new int[]{Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql,params,types);
		if(null!=tempList&&tempList.size()>0){
			MTag tag = null;
			for(int i=0;i<tempList.size();i++){
				Map result = (Map)tempList.get(i);
				tag = new MTag();
				Integer id = ((Number)result.get("id")).intValue();
				if(null!=id){
					tag.setId(id);
				}
				if(StringUtils.validNull((String)result.get("tag_name"))){
					tag.setTagName(result.get("tag_name").toString());
				}
				tagMap.put(String.valueOf(id), tag.getTagName());
			}
		}
		return tagMap;
	}

}
