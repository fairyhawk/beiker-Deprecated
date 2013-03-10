package com.beike.dao.diancai;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.page.Pager;
 /**
 * com.beike.dao.diancai.DianCaiDao.java
 * @description:点菜Dao
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
public interface DianCaiDao {
	
	/**
	 * 
	* @Title: getAllDianCaiByMerId
	* @Description: 获取品牌支持点菜的分店
	* @param @param merId:品牌标识
	* @return List<Map<String,Object>> ：支持点菜的分店集合
	* @throws ：sql异常
	 */
	public List<Map<String,Object>> getAllDianCaiByMerId(Long merId) throws Exception;
	
	
	
	
	
	public List<Map<String,Object>> getPaidOrderMenu(String trx_goods_id);
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店促销信息
	 *
	 */
	public List<Map<String,Object>> getPromotion(Long branchid);
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店信息
	 *
	 */
	public List<Map<String,Object>> getBranchInfo(Long branchid);
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店信息
	 *
	 */
	public List<Map<String,Object>> getTopone(List<Long> goodsids);
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @param tag
	 * @param pager
	 * @return 分店菜单
	 *
	 */
	public List<Map<String,Object>> getOrderMenu(Long promotionid,List<String> tags);
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店在售商品
	 *
	 */
	public List<Long> goodsOfBranch(Long branchid);
	
	
	/**
	 * 
	 * janwen
	 * @param orderid
	 * @return 分类
	 *
	 */
	public List<String> getMenuCat(Long  orderid);
	
	
	/**
	 * 
	 * janwen
	 * @param order_id
	 * @param type
	 * @return 打折引擎
	 *
	 */
	public List<Map<String,Object>> getDiscountEngine(Long order_id,String type);
	
	
	
	/**
	 * 
	 * janwen
	 * @param menuid
	 * @return 订单总价
	 *
	 */
	public  List<Map<String,Object>>  getOrderMenu(List<Long> menuids,Long branchid);
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 销售量
	 *
	 */
	public Long getGoodsSold(Long goodsid);
	
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 商品二级商圈
	 *
	 */
	public List<String> getGoodsRegionext(Long goodsid);
	
	
	/**
	 * 
	 * janwen
	 * @param trx_goods_id
	 * @return 订单价格信息
	 *
	 */
	public Map<String,Object> getOrderAmount(String trx_goods_id);
	
	
	/**
	 * 
	 * janwen
	 * @param menuid
	 * @return 分店id
	 *
	 */
	public Long getBranchidByMenuid(Long menuid);
	
	
	/**
	 * 
	 * @param guestId
	 * @return 
	 */
	public Map<String	, Object> getGuestIdByOrderId(Long orderId);
	
	/**
	 * @desc 查询EngineIntervalless
	 * @param price orderId
	 * @return
	 */
	public List<Map<String, Object>> getEngineIntervallessByPrice(double price, Long orderId);



	public List<Map<String, Object>> getOrderMenu(List<Long> menuids);



	public Map<String, Object> getOrderGuestMapByOrderId(Long orderId);
	
	
	
	public List<Map<String,Object>> getHistoryBranches(List<Long> branchids);
	
	public List<Map<String, Object>> getCategoryByMenuIds(List<Long> menuIds);
	
	public List<OrderMenu> getOrderMenusByMenuId(List<Long> menuIds);
	
	public List<Map<String, Object>> getOrderMenuByMenuId(List<Long> menuIds);


	/**
	 * 
	* @Title: listOfMerchantOfOrders
	* @Description: 可点餐的分店列表
	* @param @param paramMap
	* @param @return    设定文件
	* @return List<Map<String,String>>    返回类型
	* @throws
	 */
	public List<Map<String, Object>> listOfMerchantOfOrders(Map<String, String> paramMap);


	/**
	 * 
	* @Title: getRandDishByOrderId
	* @Description: 获取随机的两道菜
	* @param @param object
	* @param @return    设定文件
	* @return OrderMenu    返回类型
	* @throws
	 */
	public List<Map<String, Object>> getRandDishByOrderId(String orderId);





	/**
	 * 
	* @Title: getCountListOfOrders
	* @Description:可点餐的分店标识
	* @param @param paramMap
	* @param @return    设定文件
	* @return Integer    返回类型
	* @throws
	 */
	public List<Map<String,Object>> getMerchanitIdListOfOrders(Map<String, String> paramMap);




	/**
	 * 
	* @Title: getCountOnlineOfOrder
	* @Description: 可点餐的分店的在线活动数量
	* @param @param merchantIdsStr
	* @param @return    设定文件
	* @return Integer    返回类型
	* @throws
	 */
	public Integer getCountOnlineOfOrder(String merchantIdsStr,String nowStr);





	/**
	 * 
	* @Title: getListOfMerchantOfOrdersOnline
	* @Description: 有在线活动的可点餐的分店列表
	* @param @param merchantIdsStr
	* @param @param pager
	* @param @return    设定文件
	* @return List<Map<String,Object>>    返回类型
	* @throws
	 */
	public List<Map<String, Object>> getListOfMerchantOfOrdersOnline(
			String merchantIdsStr,String nowStr,Pager pager);




	/**
	 * 
	* @Title: getSupportOrderRegion
	* @Description: 可点餐的商圈
	* @param @param cityId
	* @param @return    设定文件
	* @return Map<Long,List<RegionCatlog>>    返回类型
	* @throws
	 */
	public Map<Long, List<RegionCatlog>> getSupportOrderRegion(Long cityId,String nowStr);





	/**
	 * @return 
	 * 
	* @Title: getBranchMenuList
	* @Description: 从可点菜的分店中取菜
	* @param @param branchids
	* @param @param limitNum    设定文件
	* @return void    返回类型
	* @throws
	 */
	public List<OrderMenu> getBranchMenuList(String branchids, Integer limitNum);
}
