package com.beike.wap.dao;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MTag;
/**
 * <p>
 * Title:分类信息数据库相关操作
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-10-17
 * @author lvjx
 * @version 1.0
 */
public interface MTagDao extends GenericDao<MTag, Long> {

	/**
	 * Description : 根据父id查询所属分类信息
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<MTag> queryTagByParendId(int parentId) throws Exception ;
	
	/**
	 * Description :  根据父id查询所属分类信息
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> queryTagByParentIdInfo(int parentId) throws Exception;
	
}
