package com.beike.dao.adweb.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.adweb.AdWeb;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.adweb.AdWebDao;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */
@Repository("adWebDao")
public class AdWebDaoImpl extends GenericDaoImpl<AdWeb,Long> implements AdWebDao {

	@Override
	public AdWeb getAdWebByCode(String code) {
		if(code==null)return null;
		String sql="select ad.adweb_id,ad.adweb_code,ad.adweb_name,ad.adweb_trxurl from beiker_adweb ad where ad.adweb_code=?";
		List<AdWeb> list=getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), code);
		if(list==null||list.size()==0)return null;
		return list.get(0);
	}

	@Override
	public Long getLastInsertId() {
		return super.getLastInsertId();
	}
	public class RowMapperImpl implements ParameterizedRowMapper<AdWeb> {
		@Override
		public AdWeb mapRow(ResultSet rs, int num) throws SQLException {
			
			AdWeb adWeb=new AdWeb();
			adWeb.setAdwebid(rs.getLong("adweb_id"));
			adWeb.setAdwebCode(rs.getString("adweb_code"));
			adWeb.setAdwebName(rs.getString("adweb_name"));
			adWeb.setAdwebTrxurl(rs.getString("adweb_trxurl"));
			return adWeb;
		}
	}
}
