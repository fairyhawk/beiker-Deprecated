package com.beike.dao.adweb.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.adweb.AdWebLog;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.adweb.AdWebLogDao;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 2, 2012 5:18:26 PM     
 * @version 1.0
 */
@Repository("adWebLogDao")
public class AdWebLogDaoImpl extends GenericDaoImpl<AdWebLog, Long> implements AdWebLogDao {

	/* (non-Javadoc)
	 * @see com.beike.dao.adweb.AdWebLogDao#addAdWebLog(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Long addAdWebLog(final String adcid, final String adwi, final String adcode) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql="insert into beiker_adweblog (adcid,adwi,adcode,access_date) values(?,?,?,now())";
		this.getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(
						sql,
						new String[] {"adcid","adwi","adcode"});
				ps.setString(1, adcid);
				ps.setString(2, adwi);
				ps.setString(3, adcode);
				return ps;
			}

		}, keyHolder);
		Long adweblogId = keyHolder.getKey().longValue();
		return adweblogId;
	}
}