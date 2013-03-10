package com.beike.model.lucene;

import java.util.List;

import org.apache.lucene.document.Document;


/**
 * 查询结果
 * @author ye.tian
 *
 */
public class QueryResult {
	private int recordCount;  	//记录总数
	
	private List<Document> recordList;	//搜索记录列表
	
	private List<QueryParam> queryParamList;//查询结果参数

	public List<QueryParam> getQueryParamList() {
		return queryParamList;
	}

	public void setQueryParamList(List<QueryParam> queryParamList) {
		this.queryParamList = queryParamList;
	}

	public QueryResult(int recordCount, List<Document> recordList) {
		
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public List<Document> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<Document> recordList) {
		this.recordList = recordList;
	}

}
