package com.beike.service.unionpage;

import java.util.List;
import java.util.Map;
 /*
 * com.beike.service.unionpage.UnionPageService.java
 * @description:关键词静态聚合页service
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-17，xuxiaoxian ,create class
 *
 */
public interface UnionPageService {
		
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
	 * @description:通过关键词查询关键词信息
	 * @param keyWord[]
	 * @param  count 查询关键词个数
	 * @return List<Map<String,String>>
	 * @throws 
	 */
	public List<Map<String,String>> getListMsgByKeyWords(String[] keyWords,int count);
}
