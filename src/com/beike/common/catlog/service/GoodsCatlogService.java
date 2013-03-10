package com.beike.common.catlog.service;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.AbstractCatlog;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;

/**
 * <p>Title:商品属性类别服务</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface GoodsCatlogService extends AbstractCatlogService{
	
	/**
	 * 
	 * @param goodsid
	 * @return 
	 *//*
	public String getGoodscatByID(Long goodsid);*/
	/**
	 * 根据id列表返回 所有商品
	 * @param listids
	 * @return
	 */
	public List<GoodsForm> getGoodsFormFromId(List<Long> listids);
	/**
	 * 根据id列表返回 所有商品  分页
	 * @param listids
	 * @param pager
	 * @return
	 */
//	public List<GoodsForm> getGoodsFormByPage(List<Long> listids,Pager pager);
	
	/**
	 * 商品列表无结果,走该通道查询,查询规则：
	 * 
	 * @author janwen
	 * @time Nov 19, 2011 5:50:07 PM
	 *
	 * @param abstractCatlog
	 * @param pager
	 * @return
	 */
	public List<Long> getCatlogRank(AbstractCatlog abstractCatlog,Pager pager);
	
	
	/**
	 * 商品列表页右侧推荐商家
	 */
	public List<Long> getHotMerchantIDS(AbstractCatlog abstractCatlog);
	
	/**
	 * 根据地域id 查询城市id
	 * @param regionId  地域id
	 * @return 城市id
	 */
	public Map<String,Object> getCityIdByRegionId(String regionId);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 5:16:24 PM
	 *
	 * @param abstractCatlog
	 * @param pager
	 * @return 默认排序service
	 */
	public List<Long>  getDefaultCatlog(AbstractCatlog abstractCatlog,Pager pager);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 29, 2011 8:01:04 PM
	 *
	 * @param abstractCatlog
	 * @param pager
	 * @return 按销量排序走该通道
	 */
	public List<Long> getSortCatlog(AbstractCatlog abstractCatlog,Pager pager);
	
	/**
	 * 获取商品按属性分组数量
	 * @param cityid
	 * @return
	 */
	public Map<String,Integer> getGoodsCatlogGroupCount(Long cityid);
	
	/**
	 * 获取城市今日新品、在售商品数量
	 * @return
	 */
	public int[] getGoodsCountByCity(String cityen);
	
	public int getCatlogCount(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog) ;
	
}
