package com.beike.common.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beike.common.catlog.service.AbstractCatlogService;
import com.beike.common.catlog.service.GoodsCatlogServiceImpl;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;


/**
 * <p>Title:默认搜索策略实现 </p>
 * <p>Description:正常到数据库查找，按照权重</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("defaultSearchStrategyService")
public class DefaultSearchStrategyServiceImpl implements SearchStrategyService {
	 
	private AbstractCatlogService abstractCatlogService;
	

	public AbstractCatlogService getAbstractCatlogService() {
		return abstractCatlogService;
	}


	public void setAbstractCatlogService(AbstractCatlogService abstractCatlogService) {
		this.abstractCatlogService = abstractCatlogService;
	}

	
	public List<Long> getCatlogId(AbstractCatlog abstractCatlog,Pager pager) {
		return abstractCatlogService.getCatlog(abstractCatlog,pager);
	}


	@Override
	public List<Long> getCatlogId(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) {
		return abstractCatlogService.getCatlog(validGoodsIdList, abstractCatlog, pager);
	}


}
