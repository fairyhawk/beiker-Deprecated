package com.beike.common.search;

import java.util.List;

import com.beike.common.catlog.service.AbstractCatlogService;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;

/**
 * <p>Title:搜索策略接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 20, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface SearchStrategyService {
	
	/**
	 * 根据搜索条件的分类id 查找对应的ID号返回
	 * @param abstractCatlog		AbstractCatlog 搜索级别属性
	 * @return
	 */
	public List<Long> getCatlogId(AbstractCatlog abstractCatlog,Pager pager);

	public List<Long> getCatlogId(List<Long> validGoodsIdList,AbstractCatlog abstractCatlog,Pager pager);
	
	public AbstractCatlogService getAbstractCatlogService();
	
	public void setAbstractCatlogService(AbstractCatlogService abstractCatlogService);
}
