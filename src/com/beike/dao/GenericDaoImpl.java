package com.beike.dao;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class GenericDaoImpl<T, ID extends Serializable> extends
		SimpleJdbcDaoSupport implements GenericDao<T, ID> {

	private final Log log = LogFactory.getLog(GenericDaoImpl.class);

	@Resource(name = "dataSource")
	public void setDataSource1(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	public Long getLastInsertId(){
		String sql ="select last_insert_id()";
		return getJdbcTemplate().queryForLong(sql);
	}
 
}