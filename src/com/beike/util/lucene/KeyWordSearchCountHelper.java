package com.beike.util.lucene;

import java.io.Serializable;
import java.util.Map;

public class KeyWordSearchCountHelper implements Serializable {
	
	//搜索关键词
	private String keyword;
	
	//根据关键字搜索的总次数
	private long totalCount;
	
	//搜索的结果数
	private long resultCount;
	
	//ip及该ip的搜索次数
	private Map<String, Long> map;
	
	public KeyWordSearchCountHelper(String keyword, long totalCount,Map<String,Long> map,long resultCount)
	{
		this.keyword=keyword;
		this.totalCount=totalCount;
		this.map=map;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getResultCount() {
		return resultCount;
	}

	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}

	public Map<String, Long> getMap() {
		return map;
	}

	public void setMap(Map<String, Long> map) {
		this.map = map;
	}
	
	

}
