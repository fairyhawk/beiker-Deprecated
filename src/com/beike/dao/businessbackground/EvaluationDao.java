package com.beike.dao.businessbackground;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Title：EvaluationDao.java
 * @Package com.beike.dao.comment
 * @Description：
 * @date：2013-1-28 - 上午10:18:17
 * @author：zhaojinglong@qianpin.com
 * @version
 */
public interface EvaluationDao {
	/**
	 * 根据查询条件查询会员评价记录数
	 *@param queryMap
	 *@return
	 */
	public int getEvaluateCount(Map<String, Object> queryMap);
	
	/**
	 * 根据查询条件查询会员评价记录
	 *@param queryMap  查询条件
	 *@param curPage  当前页
	 *@param pageSize  每页显示记录数
	 *@return
	 */
	public List<Map<String, Object>> getScrollEvaluate(Map<String, Object> queryMap, int curPage, int pageSize);
	
	/**
	 * 根据商品ID集合查询商品
	 *@param goodsidsSet
	 *@return
	 */
	public List<Map<String, Object>>  getGoodsByIds(Set<Long> goodsidsSet);
	
	/**
	 * 根据分店ID查询分店
	 *@param subguestSet
	 *@return
	 */
	public List<Map<String, Object>> getSubGuests(Set<Long> subguestidsSet);
	
}
