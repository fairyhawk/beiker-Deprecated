package com.beike.dao.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.MenuGoodsOrder;
/**
 * @desc 对MenuGoodsOrder表的DAO
 * @author ljp
 *
 */
public interface MenuGoodsOrderDao {
	/**
	 * @desc 根据条件查询MenuGoodsOrder 
	 * @param condition  key "数据库字段",value 值
	 * @return List<Object>
	 * @throws Exception
	 */
	public List<Object> queryMenuGoodsOrderByCondition(Map<String, String> condition)throws Exception;
	
	/**
	 * @desc 增加MenuGoodsMap
	 * @param menuGoodsMap
	 * @throws Exception
	 */
	public void addMenuGoodsOrder(MenuGoodsOrder menuGoodsOrder)throws Exception;
	
	/**
	 * @desc 根据menuGoodsSn查询MenuGoodsMap
	 * @param menuGoodsSn
	 * @return List<MenuGoodsMap>
	 * @throws Exception
	 */
	public List<MenuGoodsOrder> queryMenuGoodsOrderByMenuIds(List<Long>	menuIds)throws Exception;
	
	/**
	 * @desc 根据活动id和分店id 查询orderguestmap 数据
	 * @param orderId
	 * @param guestId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryOrderGuestMapByOrderIdAndGuestId(Long orderId, Long guestId)throws Exception;
	
	public List<MenuGoodsOrder> queryByOrderIdAndGuestId(Long orderId);
}
