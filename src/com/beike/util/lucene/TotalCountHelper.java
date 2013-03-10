package com.beike.util.lucene;

import java.io.Serializable;
import java.util.Map;
	
	//总搜索次数的帮助类
	public class TotalCountHelper  implements Serializable {
		
		//类别(商品，优惠券，品牌)搜索的总次数
		private long totalCount;
		//Map，用于存储用户Ip及搜索的次数
	    private Map<String,Long> map;
	    
	    public TotalCountHelper(long TotalCount, Map<String,Long> map)
	    {
	    	this.map=map;
	    	this.totalCount=TotalCount;
	    }

		public long getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(long totalCount) {
			this.totalCount = totalCount;
		}

		public Map<String, Long> getMap() {
			return map;
		}

		public void setMap(Map<String, Long> map) {
			this.map = map;
		}
	}

