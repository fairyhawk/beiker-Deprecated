package com.beike.service.lucene.recommend;

import java.util.List;


public interface LuceneRecommendService {


	/**
	 * 
	 * @param originalword
	 * @return 原关键字
	 */
	public String[] getSilimarWords(String originalword,int numSug) throws Exception;
	
	/**
	 * 
	 * janwen
	 * @param begin
	 * @return 关键词
	 *
	 */
	public String getKWData(String isused);
	
	
	/**
	 * 
	 * janwen
	 * @param goodsname 商品名称
	 * @param numRec  推荐数量
	 * @return
	 *
	 */
	public String[] getRecommend(String goodsname,int numRec) throws Exception;
}
