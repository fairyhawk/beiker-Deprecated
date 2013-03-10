package com.beike.common.search;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.beike.common.catlog.service.AbstractCatlogService;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;
import com.beike.util.BeanUtils;

/**
 * <p>Title:搜索策略</p>
 * <p>Description: 策略模式，有其他搜索模式直接实现SearchStrategyService</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 20, 2011
 * @author ye.tian
 * @version 1.0
 */

public class SearchStrategy {
	
	@Resource(name="defaultSearchStrategyService")
	private SearchStrategyService serchStrategyService;
	
	/**
	 * 根据策略、servicebean 搜索不同的事物
	 * @param request
	 * @param searchStrategy  策略对象
	 * @param serviceBeanId
	 */
	public void setService(HttpServletRequest request,String serviceBeanId){
		
		SearchStrategyService dssi=this.getSerchStrategyService();
		if(dssi==null){
			dssi=(SearchStrategyService) BeanUtils.getBean(request, "defaultSearchStrategyService");
		}
		AbstractCatlogService acs=dssi.getAbstractCatlogService();
		acs=(AbstractCatlogService)BeanUtils.getBean(request, serviceBeanId);
		dssi.setAbstractCatlogService(acs);
		this.setSerchStrategyService(dssi);
	}
	
	public List<Long> getCatlog(AbstractCatlog abstractCatlog,Pager pager){
		
		
		return serchStrategyService.getCatlogId(abstractCatlog,pager);
	}
	
	
	public List<Long> getCatlog(List<Long> validGoodsIdList ,AbstractCatlog abstractCatlog,Pager pager){
		return serchStrategyService.getCatlogId(validGoodsIdList,abstractCatlog,pager);
	}

	public SearchStrategy() {
		
	}

	public SearchStrategy(SearchStrategyService serchStrategyService) {
		this.serchStrategyService = serchStrategyService;
	}

	public SearchStrategyService getSerchStrategyService() {
		return serchStrategyService;
	}

	public void setSerchStrategyService(SearchStrategyService serchStrategyService) {
		this.serchStrategyService = serchStrategyService;
	}
	
	
	
}
