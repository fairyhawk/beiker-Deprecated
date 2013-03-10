package com.beike.dao.catlog;

import java.util.List;

import com.beike.entity.catlog.AbstractCatlog;
import com.beike.page.Pager;


public interface GoodsCatlogDao extends CatlogDao{
	
	
	public int searchCatlogCount(List<Long> validGoodsIdList, AbstractCatlog abstractLog) ;
	public List<Long> getDefaultGoodsIdBySortWeight(List<Long> validGoodsIdList, AbstractCatlog abstractlog, Pager pager) ;
	public List searchDefaultGoodsId(List<Long> validGoodsIdList, AbstractCatlog abstractLog) ;
	public List<Long> searchCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractLog, int start, int end) ;
	public List getGoodsOnTime(List<Long> validGoodsIdList, AbstractCatlog abstractLog) ;
}