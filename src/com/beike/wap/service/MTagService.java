package com.beike.wap.service;

import java.util.List;
import java.util.Map;

import com.beike.service.GenericService;
import com.beike.wap.entity.MTag;
/**
 * <p>
 * Title:分类信息Service
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
public interface MTagService extends GenericService<MTag, Long> {

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
