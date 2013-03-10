package com.beike.wap.dao;

import java.util.Date;
import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MWapType;

/**
 * <p>
 * Title:WAP商品数据库相关操作
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
 * @date 2011-09-23
 * @author lvjx
 * @version 1.0
 */

public interface MWapTypeDao extends GenericDao<MWapType, Long> {

	/**
	 * 保存WAP信息
	 * 
	 * @param wapTypeList
	 * @return
	 * @throws Exception
	 */
	public int addWapType(final List<MWapType> wapTypeList) throws Exception;

	/**
	 * 根据类型，页面，时间查询数据是否已经生成
	 * 
	 * @param typeType
	 * @param typePage
	 * @param currentDate
	 * @return
	 * @throws Exception
	 */
	public int queryWapType(int typeType, int typePage, Date currentDate,
			String typeArea) throws Exception;

}
