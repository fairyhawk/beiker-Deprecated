package com.beike.service.waimai;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;
import com.beike.form.TakeAwayDetailForm;

/**
 * com.beike.service.waimai.WaiMaiService.java
 * @description:外卖Service
 * @Author:xuxiaoxian
 *                    Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
public interface WaiMaiService {
	/**
	 * 查询分店的外卖详情
	 * @param merchantId 分店ID
	 * @return TakeAwayDetailForm对象
	 */
	public TakeAwayDetailForm getTakeAwayDetailByMerchantId(Long merchantId);

	/**
	 * @Title: getTakeOutByMerId
	 * @Description: 获取品牌支持外卖的分店
	 * @param @param merId:品牌标识
	 * @return 支持外卖分店集合
	 * @throws ：sql异常
	 */
	public List<Object> getTakeOutByMerId(Long merId) throws Exception;
	
	/** 
	 * @description：根据条件分页查询分店信息（地图显示）
	 * @param Map<String,String> map
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getMerDetailByConditions(Map<String,String> map,int startRow,int pageSize);
	
	
	/** 
	 * @description:查询当前可视区域内的分店/品牌 数量
	 * @param map
	 * @return int
	 * @throws 
	 */
	public int getMerchantCount(Map<String,String> map,boolean isBrand);
	
	/** 
	 * @description：分店所属品牌是否含有在售商品
	 * @param brandIdList
	 * @return Set<String>
	 * @throws 
	 */
	public Set<String> isBrandContainOnLineGoods(List<Long> brandIdList);
	
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
	 * @description:  一个品牌下取一个分店
	 * @param conditionMap
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getDistinctMerDetail(Map<String,String> conditionMap,int startRow,int pageSize);

	/**
	 * 
	* @Title: getBranchsTakeAway
	* @Description: 取多个分店，并且取各分店指定数量的菜
	* @param @param branchids
	* @param @param menuCount    设定文件
	* @return void    返回类型
	* @throws
	 */
	public Map<TakeAway,List<TakeAwayMenu>> getBranchsTakeAway(String branchids, Integer menuCount) throws Exception;
}
