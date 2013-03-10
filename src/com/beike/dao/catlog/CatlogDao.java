package com.beike.dao.catlog;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;

/**
 * <p>Title:基本的地域、属性操作 </p>
 * <p>Description:目前主要是 商品、优惠券、商家 的地域和属性的类别操作</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface CatlogDao {
	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 4:41:12 PM
	 * 
	 * @param abstractLog
	 * @param start
	 * @param end
	 * @return 默认排序所有符合条件的map{goodid}
	 */
	public List searchDefaultGoodsId(AbstractCatlog abstractLog);
	
	
	/** 
	 * @date Apr 5, 2012
	 * @param abstractlog 
	 * @param pager
	 * @return List 通过权重值默认排序符合条件的map{goodid}
	 */
	public List<Long> getDefaultGoodsIdBySortWeight(AbstractCatlog abstractlog,Pager pager);
	  /**
     * 
     * @author janwen
     * @time Dec 26, 2011 4:55:42 PM
     *
     * @return 24小时销售排行榜 map{goods_id,top(24小时销售量)}
     */
	public List getTopSaled();
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 5:11:07 PM
	 *
	 * @param abstractLog
	 * @return map{goods_id,on_time}商品上下架时间
	 */
	public List getGoodsOnTime(AbstractCatlog abstractLog);
	
	/**
	 * 
	 * @author janwen
	 * @time Dec 29, 2011 8:06:29 PM
	 * @return  商品的销售数量包括虚拟销售数量
	 */
	public List getGoodsSaled();
	public List<Long> searchCatlog(AbstractCatlog abstractLog,int start,int end);
	
	public int searchCatlogCount(AbstractCatlog abstractLog);
	
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
	
	/*public String getCatByID(Long id);
	*/
	/**
	 * 获取城市商品属性集合
	 * @param cityid
	 * @return
	 */
	public List<Map<String,Object>> getGoodsCatlogList(Long cityid, int curPage);
	
	/**
	 * 获取商品标签集合
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String,Object>> getGoodsBiaoqianList(String goodsIds);
	
	/**
	 * 
	 * janwen
	 * @param goodsids
	 * @return 查询有特色标签的商品
	 *
	 */
	public List<Map> getFeaturedGoods(List<Long> goodsids,AbstractCatlog query,Pager pager);
}
