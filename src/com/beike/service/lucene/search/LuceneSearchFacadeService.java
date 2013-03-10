package com.beike.service.lucene.search;

import java.util.List;
import java.util.Map;

import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;

public interface LuceneSearchFacadeService {

	/**
	 * 
	 * @param nextPageGoodsid 当前页商品id
	 * @return 对应商品
	 */
	public List<GoodsForm> getSearchGoodsResult(List<Long> nextPageid);
	/**
	 * 
	 * @param goods_keyword
	 * @param city_en_name
	 * @param currentPage
	 * @param pagesize
	 * @return 当前页商品id,记录总数
	 */
	public Map<String, Object>  getSearchGoodsMap(String goods_keyword,String city_en_name,int currentPage,int pagesize) throws Exception;
	

	/**
	 * 
	 * @param nextPageid
	 * @return 品牌搜索结果
	 */
	public List<MerchantForm> getSearchBrandResult(List<Long> nextPageid);
	public Map<String, Object>  getSearchBrandMap(String goods_keyword,String city_en_name,int currentPage,int pagesize)throws Exception;

	
	/**
	 * 
	 * janwen
	 * @param keyword
	 * @param citypinyin
	 * @param pager
	 * @return 订餐分店搜索{totalCount,List<Long>}
	 *
	 */
	public List<Long> getSearchBranchMap(String keyword,String citypinyin,String type);
}
