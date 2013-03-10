package com.beike.dao.lucene;

import java.util.List;
import java.util.Map;


public interface LuceneSearchDao {
	
	
	
	
	/**
	 * 
	 * @param goods_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return 当前页商品id，搜索记录结果数
	 */
	public Map<String, Object> queryGoodsResult(String goods_keyword,String city_en_name,int currentPage,int pagesize);
	
	/**
	 * 
	 * @param brand_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return 当前页品牌id,搜索记录结果数
	 */
	
	public Map<String,Object> queryBrandResult(String brand_keyword,String city_en_name,int currentPage,int pagesize);
	
	
	
	/**
	 * 缓存索引
	 * @author janwen
	 * @time Nov 24, 2011 1:27:46 PM
	 *
	 * @param indexdir
	 * @param indexbir
	 * @param goods_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return
	 */
	public Map<String, Object> queryGoodsResultProxy(String indexdir,String indexdirbak,String goods_keyword,String city_en_name,int currentPage,int pagesize);

	/**
	 * 
	 * @param coupon_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return 当前页优惠券id,搜索记录结果数
	 */
	public Map<String, Object> queryCouponResultProxy(String indexdir,String coupon_keyword,String city_en_name,int currentPage,int pagesize);
	
	
	/**
	 * 缓存索引
	 * @author janwen
	 * @time Nov 24, 2011 2:38:22 PM
	 *
	 * @param indexdir
	 * @param indexdirbak
	 * @param brand_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return
	 */
	public Map<String,Object> queryBrandResultProxy(String indexdir,String indexdirbak,String brand_keyword,String city_en_name,int currentPage,int pagesize);
	
	
	/**
	 * 
	 * janwen
	 * @param indexdir
	 * @param keyword
	 * @param pager
	 * @return 分店搜索
	 *
	 */
	public List<Long> queryBranchResult(String indexdir,String keyword,String citypinyin,String type);
	
}
