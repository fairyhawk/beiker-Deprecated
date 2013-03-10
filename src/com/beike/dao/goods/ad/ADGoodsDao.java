package com.beike.dao.goods.ad;

import java.util.List;


/**
 * 商品推荐
 * @author janwen
 * @time Feb 15, 2012 5:47:04 PM
 */
public interface ADGoodsDao {

	/**
	 * 
	 * @author janwen
	 * @time Feb 15, 2012 5:53:18 PM
	 *
	 * @param cityid
	 * @param categoryid
	 * @param type
	 * @param notinGoodsid 已经查找过的goodsid(不包括详情页goodsid,同店在售:eg,1,2,3)
	 * @return 推荐商品goodsid:同类热销,美食热销
	 */
	public List<Long> getSameCategoryGoods(int cityid,int categoryid,String type,String notinGoodsid,int limit);
	
	
	
}
