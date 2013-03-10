package com.beike.dao.unionpage;

import java.util.List;
import java.util.Map;

public interface UnionKWDao {

	/**
	 * 
	 * janwen
	 * @param begin
	 * @return seo关键字(使用中)
	 *
	 */
	public List<String> getUsedKW(String isused,Long begin);
	
	/**
	 * 
	 * janwen
	 * @param begin
	 * @return seo关键字(未使用中)
	 *
	 */
	public List<String> getUnUsedKW(Long begin);
	
	/**
	 * 
	 * janwen
	 * @param isused 0/1 
	 * @return kw总数
	 *
	 */
	public Long getKWCount(String isused);
	
	
	/**
	 * 
	 * janwen
	 * @return seo词库最后更新时间
	 *
	 */
	public String getKWUpdateTime();
	
	/** 
	 * @date 2012-5-17
	 * @description:通过关键词Id查询关键词
	 * @param id
	 * @return String
	 * @throws 
	 */
	public String getKeyWordById(int id);
	
	/** 
	 * @date 2012-5-18
	 * @description:查询所有的关键词信息，
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getAllKeyWordMsg();
	
	/** 
	 * @date 2012-5-21
	 * @description:通过关键词查询相应的关键词信息
	 * @param keyWord
	 * @param count 查询关键词数量
	 * @return List<Map<String,String>>
	 * @throws 
	 */
	public List<Map<String,String>> getMsgByKeyWord(String keyWord,int count);
	
	
}
