package com.beike.service.mobile;

import java.util.List;
import java.util.Map;

/**
 * 搜索,列表相关服务
 * @author janwen
 * Mar 28, 2012
 */
public interface LuceneSearchMobileService {

	/**
	 * 
	 * janwen
	 * @param param
	 * @return 新版商品搜索
	 *
	 */
	public Map<String,Object> searchGoods(SearchParamV2 param);
	
	
	/**
	 * 
	 * janwen
	 * @param param
	 * @return 新版分店搜索
	 *
	 */
	public Map<String,Object> searchBranch(SearchParamV2 param);
	
	/**
	 * 
	 * @param param
	 * @return 列表
	 * 1:49:16 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getListGoods(SearchParam param);
	
	/**
	 * 
	 * @param param
	 * @return 品牌列表
	 * 1:49:24 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getListBrand(SearchParam param);
	
	
	/**
	 * 
	 * @param param
	 * @return 商家列表
	 * 4:10:54 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getListBranch(SearchParam param);
	
	/**
	 * 
	 * janwen
	 * @param querybranchid
	 * @return 通过branchid
	 *
	 */
	public Map<String,Object> getListBranch(List<Long> querybranchid,Double lat,Double lng);
}
