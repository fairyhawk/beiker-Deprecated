package com.beike.dao.catlog;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.RegionCatlog;

/**
 * <p>Title: 属性的分类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 24, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface RegionCatlogDao {
	/**
	 * 获得所有属性
	 * 
	 */
	public Map<Long,List<RegionCatlog>>  getAllCatlog();
	
	
	/**
	 * 获得所有城市 对应的地域
	 * @return
	 */
	public Map<String,Map<Long,List<RegionCatlog>>> getAllCityCatlog();
	
	public Map<String,Long> getCityCatlog();
	
	/**
	 * 根据地域id 查询所在城市id
	 * @return
	 */
	public Map<String,Object> getCityIdByRegion(Long regionId);
	/** 
	 * @date Mar 27, 2012
	 * @description 根据parentid查找商品分类
	 * @param parentId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getGoodsCatlogByTagId(int parentId);
	
	/** 
	 * @date Mar 27, 2012
	 * @description 根据parentid查询商圈
	 * @param parentId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getRegionCatlog(int cityid,int parentid);
	
	/**
	 * 查询所有属性，按城市分组
	 * @return
	 */
	public Map<Long,Map<Long,List<RegionCatlog>>>  getAllCatlogHavingCity();
	
	/**
	 * 根据parentId 查找cityid城市的商品分类
	 * @param parentId
	 * @param cityid
	 * @return
	 */
	public List<Map<String, Object>> getGoodsCatlogByTagIdHavingCity(int parentId,Long cityid);
	
	/**
	 * 获得所有标签
	 * @return
	 */
	public Map<Long,List<RegionCatlog>> getAllLabelCatlogProperty();
	
	
	/**
	 * 
	 * @Title: getGoodsAllCatlogByCityId
	 * @Description: 获得该城市下的所有分类(包括一级、二级分类)
	 * @param  cityid 城市ID
	 * @return List<Map<String,Object>>
	 * @author wenjie.mai
	 */
	public List<Map<String,Object>> getGoodsAllCatlogByCityId(Long cityid);
	
	/**
	 * 
	 * @Title: getGoodsAllRegionByCityId
	 * @Description: 获得该城市下的所有商圈(包括一级、二级商圈)
	 * @param  cityid 城市ID
	 * @return List<Map<String,Object>>
	 * @author wenjie.mai
	 */
	public List<Map<String,Object>> getGoodsAllRegionByCityId(Long cityid);
	
	
	/**
	 * 根据id列表查询区域列表
	 * @param ids
	 * @return
	 */
	public List<RegionCatlog> queryByIds(List<Long> ids);
}
