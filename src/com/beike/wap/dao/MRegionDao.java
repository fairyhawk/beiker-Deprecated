package com.beike.wap.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MRegion;

/**
 * <p>
 * Title:热门地标数据库相关操作
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
 * @date 2011-10-10
 * @author lvjx
 * @version 1.0
 */
public interface MRegionDao extends GenericDao<MRegion, Long> {

	/**
	 * Description :根据时间，城市查询是否存在热门地标
	 * 
	 * @param currentDate
	 * @param typeArea
	 * @return
	 * @throws Exception
	 */
	public int queryWapHotRegion(Date currentDate, String typeArea)
			throws Exception;

	/**
	 * Description : 保存热门地标信息
	 * 
	 * @param wapRegionList
	 * @return
	 * @throws Exception
	 */
	public int addWapHotRegion(final List<MRegion> wapRegionList)
			throws Exception;

	/**
	 * Description : 根据日期，城市查询热门地标
	 * 
	 * @param currentDate
	 * @param typeArea
	 * @return
	 * @throws Exception
	 */
	public List<MRegion> queryWapHotRegionData(Date currentDate, String typeArea)
			throws Exception;

	/**
	 * Description : 根据父类ID，查询子类地标信息
	 * @param regionId
	 * @return
	 * @throws Exception
	 */
	public List<MRegion> queryRegion(String regionId,String areaId) throws Exception;

	
	/** 
	 * 根据id获取商圈信息
	 * @param regionId
	 */
	public MRegion findRegionById(long regionId) throws Exception;
	
	/**
	 * Description : 根据parendID，地域id查询地标信息
	 * @param regionId
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> queryRegionInfo(String regionId,String areaId) throws Exception;

	
	/**
	 * Description : 根据热门地标id查询其父类ID
	 * @param regionId
	 * @return
	 * @throws Exception
	 */
	public int queryRegionParentId(String regionId) throws Exception;
	
	/**
	 * Description : 查询地标表中的最大日期
	 * @return
	 * @throws Exception
	 */
	public Date queryMaxDate(String cityName) throws Exception;
	
	/**
	 * Description : 查询热门地标信息
	 * @param nowDate
	 * @param regionArea
	 * @param hotRegionEnName
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public List<MRegion> queryHotRegionData(java.sql.Date nowDate,String regionArea,String hotRegionEnName,int areaId) throws Exception;


	/**
	 * Description: 根据areaenname查询区域ID
	 * @param areaEnName
	 * @return
	 * @throws Exception
	 */
	public int queryAreaId(String areaEnName) throws Exception;
}
