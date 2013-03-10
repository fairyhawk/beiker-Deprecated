package com.beike.entity.log;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Mar 19, 2012
 * @author ye.tian
 * @version 1.0
 */

public class StatisticsModel {
	
	
	private String pageKey;
	
	private String pagepara;
	
	private List<String> paramList=new ArrayList<String>();

	public String getPageKey() {
		return pageKey;
	}

	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}

	public String getPagepara() {
		return pagepara;
	}

	public void setPagepara(String pagepara) {
		this.pagepara = pagepara;
	}

	public StatisticsModel(){
		
	}
	
	

	public StatisticsModel(String pageKey, String pagepara,
			List<String> paramList) {
		this.pageKey = pageKey;
		this.pagepara = pagepara;
		this.paramList = paramList;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}
	
	
}
