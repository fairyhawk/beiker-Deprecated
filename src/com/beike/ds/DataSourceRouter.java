package com.beike.ds;



import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 取得数据源的KEY
 * 
 * @author 陆庆润 创建时间：2009-3-20
 */
public class DataSourceRouter extends AbstractRoutingDataSource {
	private final Logger log = Logger.getLogger(DataSourceRouter.class);
	// 数据源key的存储控制器
	private DataSourceKey dataSourceKey;

	/**
	 * 获得数据源的key
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		String key = "";
		try {
			key = dataSourceKey.getKey();
		} catch (Throwable e) {
			throw new RuntimeException("get data source key fail", e);
		}
		return key;
	}

	public DataSourceKey getDataSourceKey() {
		return dataSourceKey;
	}

	public void setDataSourceKey(DataSourceKey dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}


}
