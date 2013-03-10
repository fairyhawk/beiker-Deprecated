package com.beike.service.impl.unionpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.unionpage.UnionKWDao;
import com.beike.service.unionpage.UnionPageService;
 /*
 * com.beike.service.impl.unionpage.UnionPageServiceImpl.java
 * @description:网站静态聚合页service实现
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-17，xuxiaoxian ,create class
 *
 */
@Service("unionPageService")
public class UnionPageServiceImpl implements UnionPageService{
	
	@Autowired
	private UnionKWDao unionKWDao;

	/** 
	 * @date 2012-5-17
	 * @description:通过关键词Id查询关键词
	 * @param id
	 * @return String
	 * @throws 
	 */
	public String getKeyWordById(int id){
		return unionKWDao.getKeyWordById(id);
	}
	
	/** 
	 * @date 2012-5-18
	 * @description:查询所有的关键词信息，
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getAllKeyWordMsg(){
		return unionKWDao.getAllKeyWordMsg();
	}

	public List<Map<String,String>> getListMsgByKeyWords(String[] keyWord,int count){
		List<Map<String,String>> listMsg = new ArrayList<Map<String,String>>();
		if(keyWord!=null && keyWord.length > 0){
			StringBuilder sb = new StringBuilder("");
			for(String KW:keyWord){
				if(StringUtils.isNotEmpty(KW)){
					sb.append("'").append(KW).append("',");
				}
			}
			if(sb.length()>0){
				listMsg = unionKWDao.getMsgByKeyWord(sb.substring(0, sb.length()-1),count);
			}
		}
		
		return listMsg;
	}
	public UnionKWDao getUnionKWDao() {
		return unionKWDao;
	}

	public void setUnionKWDao(UnionKWDao unionKWDao) {
		this.unionKWDao = unionKWDao;
	}

}
