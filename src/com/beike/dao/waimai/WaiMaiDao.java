package com.beike.dao.waimai;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;

/**
 * com.beike.dao.waimai.WaiMaiDao.java
 * @description:外卖Dao
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
public interface WaiMaiDao {

	public TakeAway getTakeAwayByMerchantId(Long merchantId);

	public List<TakeAwayMenu> queryMenusByTakeAwayId(Long takeawayId);

	/**
	 * @Title: getAllTakeOutByMerId
	 * @Description: 获取品牌支持外卖的分店
	 * @param @param merId:品牌标识
	 * @return List<Map<String,Object>> :支持外卖的分店集合
	 * @throws ：sql异常
	 */
	public List<Map<String, Object>> getAllTakeOutByMerId(Long merId) throws Exception;
	
	/** 
	 * @description：根据条件分页查询分店信息
	 * @param Map<String,String> map
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getMerDetailByCondition(Map<String,String> map,int startRow,int pageSize);
	
	/** 
	 * @description:查询当前可视区域内的分店/品牌 数量
	 * @param map
	 * @return int
	 * @throws 
	 */
	public int getMerchantCount(Map<String,String> map,boolean isBrand);
	


	/** 
	 * @description：通过分店Id分页查询美食地图分店信息
	 * @param ids
	 * @param conditionMap
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getSearchMerDetailByIds(
				List<Long> ids,Map<String,String> conditionMap,int startRow,int pageSize);

	/** 
	 * @description:根据分店搜索Id查询当前区域内分店数量
	 * @param ids
	 * @param conditionMap
	 * @return int
	 * @throws 
	 */
	public int getSearchMerCountByIds(List<Long> ids,Map<String,String> conditionMap);
	/** 
	 * @description：分店所属品牌是否含有在售商品
	 * @param brandIdList
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> isBrandContainOnLineGoods(List<Long> brandIdList);
	
	/** 
	 * @description:通过条件查询品牌Id
	 * @param conditionMap
	 * @return List<Long>
	 * @throws 
	 */
	public List<Long> getBrandIdByCondition(Map<String,String> conditionMap,int startRow,int pageSize);
	
	/** 
	 * @description:根据品牌Id查询在某个区域内的所有分店
	 * @param brandIdList
	 * @param conditionMap
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getMerDetailByBrandId(List<Long> brandIdList,Map<String,String> conditionMap);
	/**
	 * 
	* @Title: getBranchsTakeAway
	* @Description: 取多个分店，并且取各分店指定数量的菜
	* @param @param branchids
	* @param @param menuCount    设定文件
	* @return void    返回类型
	* @throws
	 */
	public List<Map<String, Object>> getBranchsTakeAway(String branchids, Integer menuCount) throws Exception; 
}
