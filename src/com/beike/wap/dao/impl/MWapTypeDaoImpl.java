package com.beike.wap.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MWapTypeDao;
import com.beike.wap.entity.MWapType;

/**
 * <p>
 * Title:商品数据库实现
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-09-23
 * @author lvjx
 * @version 1.0
 */
@Repository("wapTypeDao")
public class MWapTypeDaoImpl extends GenericDaoImpl<MWapType, Long> implements
		MWapTypeDao {

	/*
	 * @see com.beike.wap.dao.WapTypeDao#addWapType(java.util.List)
	 */
	@Override
	public int addWapType(final List<MWapType> wapTypeList) throws Exception {
		StringBuilder sql = new StringBuilder();
		
		sql.append("INSERT INTO beiker_wap_type_info (type_id,type_url,type_type,type_floor,type_page,type_date,type_area) ");
		sql.append("VALUES (?,?,?,?,?,?,?) ");
		int[] count = this.getJdbcTemplate().batchUpdate(sql.toString(),
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						MWapType wapType = wapTypeList.get(i);
						ps.setInt(1, wapType.getTypeId());
						ps.setString(2, wapType.getTypeUrl());
						ps.setInt(3, wapType.getTypeType());
						ps.setInt(4, wapType.getTypeFloor());
						ps.setInt(5, wapType.getTypePage());
						ps.setDate(6, wapType.getTypeDate());
						ps.setString(7, wapType.getTypeArea());
					}

					public int getBatchSize() {
						return wapTypeList.size();
					}
				});
		return count.length;
	}

	/*
	 * @see com.beike.wap.dao.WapTypeDao#queryWapType(int, int, java.util.Date)
	 */
	@Override
	public int queryWapType(int typeType, int typePage, Date currentDate,
			String typeArea) throws Exception {
		int sum = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_wap_type_info ");
		sql.append("WHERE type_type = ? AND type_page = ? AND type_date = ? AND type_area = ? ");
		int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.DATE,
				Types.VARCHAR };
		Object[] params = new Object[] { typeType, typePage, currentDate,
				typeArea };
		sum = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		return sum;
	}

	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

}
