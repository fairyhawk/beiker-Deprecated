package com.beike.common.catlog.service;

import java.util.List;

import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;

/**
 * <p>Title:搜索用到 的抽象出的 属性</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */

public  interface AbstractCatlogService {
	
	/**
	 * 根据商品商圈属性、以及标签属性查找相应的数据ID
	 * @param abstractCatlog
	 * @return
	 */
	public List<Long>  getCatlog(AbstractCatlog abstractCatlog,Pager pager);

	public int getCatlogCount(AbstractCatlog abstractCatlog);
	
	public List<Long> getCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) ;
	
}
