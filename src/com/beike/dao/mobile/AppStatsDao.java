package com.beike.dao.mobile;

import java.util.List;

import com.beike.model.lucene.APPRegion;
import com.beike.model.lucene.APPTag;
import com.beike.model.lucene.AppSearchQuery;

/**
 * 商品统计
 * @author janwen
 * Oct 31, 2012
 */
public interface AppStatsDao {
  
	
	
	public List<Integer> getOnlineCityid();
	/**
	 * 
	 * janwen
	 * @param cityid
	 * @return 当前城市可用一级属性
	 *
	 */
	public List<APPTag> getAvailableTag(Integer cityid,Integer parentid);
	
	
	public List<APPRegion> getAvailableRegion(Integer cityid,Integer parentid);
	
	
	
	
	
	/**
	 * 
	 * janwen
	 * @param query
	 * @return 统计
	 *
	 */
	public Integer getRegionStats(AppSearchQuery appSearchQuery);
	
	
	public List<Integer> getTagextid(int parentid,int cityid);
	
	
	public List<Integer> getRegionextid(int parentid,int cityid);
}
