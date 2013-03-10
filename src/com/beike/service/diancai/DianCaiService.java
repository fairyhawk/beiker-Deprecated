package com.beike.service.diancai;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.onlineorder.AbstractEngine;
import com.beike.entity.onlineorder.BookingGoods;
import com.beike.entity.onlineorder.BranchInfo;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.page.Pager;
import com.beike.util.json.JSONArray;
 /**
 * com.beike.service.diancai.DianCaiService.java
 * @description:点菜Service
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
public interface DianCaiService {

	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店信息
	 *
	 */
	public BranchInfo getBranchInfo(Long branchid);
	
	
	
	public List<BranchInfo> getHistroyBranchesInfo(List<Long> branchids);
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店卖的最好的商品
	 *
	 */
	public BookingGoods getTopone(Long branchid);
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店促销信息
	 *
	 */
	public AbstractEngine getPromotionInfo(Long branchid);
	
	
	
	
	
	
	
	
	public List<List<OrderMenu>>  getOrderMenuListByID(Long order_id,List<String> tags);

	

	
	
	public List<List<OrderMenu>>  getOrderMenuListByBranchid(Long branchid,List<String> tags);

	/**
	 * 
	* @Title: getOrderByMerId
	* @Description: 获取品牌支持点菜菜的分店
	* @param @param merId:品牌标识
	* @return 支持点菜分店集合
	* @throws ：sql异常
	 */
	public List<Object> getOrderByMerId(Long merId) throws Exception;
	
	
	/**
	 * 
	 * janwen
	 * @param promotionid
	 * @return 分类
	 *
	 */
	public List<String> getCategory(Long branchid);
	
	
	/**
	 * 
	 * janwen
	 * @param order_id
	 * @param items
	 * @return 计算折扣后的价格
	 *
	 */
	public double caculateAmount(Long branchid,List<OrderMenu> items);
	
	
	
	
	
	/**
	 * 
	 * janwen
	 * @param sn
	 * @return 用户点餐详细信息
	 *
	 */
	public Map<String,List<OrderMenu>> getPaidOrderMenuByTrxGoodsid(String trx_goods_id);
	
	
	/**
	 * 
	 * janwen
	 * @param historyjson
	 * @return 历史分店订单
	 *
	 */
	public Map<String,List<OrderMenu>> gethistoryOrderMenus(JSONArray historyjson,Long selectedBranchid) throws Exception;



	/**
	 * 
	* @Title: listOfOrders
	* @Description: 可点餐的分店列表
	* @param @param paramMap    设定文件
	* @return void    返回类型
	* @throws
	 */
	public List<Map<String, Object>> listOfOrders(Map<String, String> paramMap,Pager pager);



	/**
	 * 
	* @Title: getCountListOfOrders
	* @Description: 可点餐的分店列表的总数
	* @param @param paramMap
	* @param @return    设定文件
	* @return int    返回类型
	* @throws
	 */
	public Integer getCountListOfOrders(Map<String, String> paramMap);



	/**
	 * 
	* @Title: getSupportOrderRegion
	* @Description: TODO(这里用一句话描述这个方法的作用)
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
	* @Description: 分店下的点菜
	* @param @param branchids    设定文件
	* @return void    返回类型
	* @throws
	 */
	public List<OrderMenu> getBranchMenuList(String branchids,Integer limitNum);
	
}
