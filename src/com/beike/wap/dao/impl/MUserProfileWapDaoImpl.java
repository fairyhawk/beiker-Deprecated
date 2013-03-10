package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MUserProfileWapDao;
import com.beike.wap.entity.MUserTemp;
/**
 * <p>
 * Title: 用户数据库基本操作
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-09-22
 * @author kun.wang
 * @version 1.0
 */
@Repository("mUserProfileWapDao")
public class MUserProfileWapDaoImpl extends GenericDaoImpl<MUserTemp, Long> implements MUserProfileWapDao{

	private static Log log = LogFactory.getLog(MUserProfileWapDaoImpl.class);
	
	@Override
	public void deleteByMobile(String mobile) {
		String sql = "delete from beiker_userprofile_wap where mobile = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(mobile);
		getSimpleJdbcTemplate().update(sql, list.toArray(new Object[] {}));
	}

	@Override
	public int isMobileExist(String mobile) {
		String sql = "select count(1) from beiker_userprofile_wap where mobile = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(mobile);
		return getSimpleJdbcTemplate().queryForInt(sql,
				list.toArray(new Object[] {}));
	}

	@Override
	public int findByIdAndCode(long id, String vcode) {
		String sql = "select count(1) from beiker_userprofile_wap where id = ? and vcode = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(id);
		list.add(vcode);
		return getSimpleJdbcTemplate().queryForInt(sql,
				list.toArray(new Object[] {}));
	}

	@Override
	public void addUserTemp(MUserTemp userTemp) {
		String sql = "insert into beiker_userprofile_wap (mobile, vcode, regdate, password,email, customerkey) values (?,?,?,?,?,?)";
		
		List<Object> list = new ArrayList<Object>();
		list.add(userTemp.getMobile());
		list.add(userTemp.getvCode());
		list.add(new Date());
		list.add(userTemp.getPassword());
		list.add(userTemp.getEmail());
		list.add(userTemp.getCustomerkey());
		
		getSimpleJdbcTemplate().update(sql, list.toArray(new Object[] {}));
	}
	
	@Override
	public void updateByMobile(MUserTemp userTemp) {
		String sql = "update beiker_userprofile_wap set vcode = ?,password=?,customerkey=?,email=?, regdate = NOW() where mobile = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(userTemp.getvCode());
		list.add(userTemp.getPassword());
		list.add(userTemp.getCustomerkey());
		list.add(userTemp.getEmail());
		list.add(userTemp.getMobile());
		getSimpleJdbcTemplate().update(sql, list.toArray(new Object[] {}));
	}
	
	@Override
	public void updatePassword(MUserTemp userTemp) {
		String sql = "update beiker_userprofile_wap set password=?, customerkey=?,email=?, regdate = NOW() where mobile = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(userTemp.getPassword());
		list.add(userTemp.getCustomerkey());
		list.add(userTemp.getEmail());
		list.add(userTemp.getMobile());
		getSimpleJdbcTemplate().update(sql, list.toArray(new Object[] {}));
		
	}
	
	@Override
	public MUserTemp findByMobile(String mobile) {
		String sql = "select * from beiker_userprofile_wap where mobile = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(mobile);

		List<MUserTemp> rsList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(),list.toArray(new Object[] {}));
		if(rsList == null || rsList.size() == 0)
		{
			return null;
		}
		return rsList.get(0);
	}
	
	@Override
	public MUserTemp findById(long id) {
		String sql = "select * from beiker_userprofile_wap where id = ?";

		List<Object> list = new ArrayList<Object>();
		list.add(id);

		List<MUserTemp> rsList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(),list.toArray(new Object[] {}));
		if(rsList == null || rsList.size() == 0)
		{
			return null;
		}
		return rsList.get(0);
	}
	
	@Override
	public void deleteById(Long id) {
		String sql = "DELETE FROM beiker_userprofile_wap where id = ?";
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(id);
			getSimpleJdbcTemplate().update(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("delete beiker_userprofile_wap error \n id = " + id);
		}
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<MUserTemp> {
		public MUserTemp mapRow(ResultSet rs, int rowNum) throws SQLException {
			MUserTemp user = new MUserTemp();
			user.setId(rs.getInt("id"));
			user.setMobile(rs.getString("mobile"));
			user.setRegDate(rs.getDate("regdate"));
			user.setvCode(rs.getInt("vcode"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setCustomerkey(rs.getString("customerkey"));
			return user;
		}
	}
}
