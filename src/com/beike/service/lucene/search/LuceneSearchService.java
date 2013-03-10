package com.beike.service.lucene.search;

import java.util.List;
import java.util.Map;

public interface LuceneSearchService {

	/**
	 * 商品分页 FIXME 需要优化
	 * 
	 * @param goods_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param perPage
	 * @return 当前页商品信息,搜索结果记录数
	 */
	public Map<String, Object> getSearchGoodsMap(String goods_keyword,
			String city_en_name, int currentPage, int pagesize);


	/**
	 * 
	 * @param brand_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return 当前品牌信息,搜索结果记录数
	 */
	public Map<String, Object> getSearchBrandMap(String brand_keyword,
			String city_en_name, int currentPage, int pagesize);

	public List<Long> getNextPageID(List<Long> searchedids, int currentpage,
			int pagesize);

}
