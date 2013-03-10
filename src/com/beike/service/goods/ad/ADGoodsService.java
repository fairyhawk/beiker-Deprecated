package com.beike.service.goods.ad;

import java.util.List;


public interface ADGoodsService {

	/**
	 * 
	 * @author janwen
	 * @time Feb 15, 2012 6:41:34 PM
	 *
	 * @param cityid
	 * @param categoryid
	 * @param type
	 * @param notinGoodsid
	 * @param limit
	 * @return 推荐商品信息
	 */
	public List<Long> getSameCategoryGoods(int cityid, int categoryid,
			String type, String notinGoodsid, int limit);
}
