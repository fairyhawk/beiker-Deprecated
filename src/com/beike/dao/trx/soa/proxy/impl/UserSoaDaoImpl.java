package com.beike.dao.trx.soa.proxy.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.soa.proxy.UserSoaDao;

@Repository("userSoaDao")
public class UserSoaDaoImpl extends GenericDaoImpl<Object, Long> implements
		UserSoaDao {

	@Override
	public Map<String, Object> findById(Long id) {
		if (id == null || id.longValue() == 0) {
			throw new IllegalArgumentException();
		}

		String sql = "select count(1) as userCount from beiker_user where user_id=?";
		List<Map<String, Object>> userMapList = this.getSimpleJdbcTemplate()
				.queryForList(sql, id);
		if (userMapList != null && userMapList.size() > 0) {
			return userMapList.get(0);
		}

		return null;
	}
	
	
	@Override
	public Map<String, Object> findMobileById(Long id) {
		if (id == null || id.longValue() == 0) {
			throw new IllegalArgumentException();
		}

		String sql = "select mobile_isavalible as ismobile,mobile from beiker_user where user_id=?";
		List<Map<String, Object>> userMapList = this.getSimpleJdbcTemplate()
				.queryForList(sql, id);
		if (userMapList != null && userMapList.size() > 0) {
			return userMapList.get(0);
		}

		return null;
	}
	
	
	@Override
	public Map<String, Object> findUserInfoById(Long id) {
		if (id == null || id.longValue() == 0) {
			throw new IllegalArgumentException();
		}

		String sql = "select  createdate  as createDate,isavalible as isavalible,email as email from beiker_user where user_id=?";
		List<Map<String, Object>> userMapList = this.getSimpleJdbcTemplate()
				.queryForList(sql, id);
		if (userMapList != null && userMapList.size() > 0) {
			return userMapList.get(0);
		}

		return null;
	}
	
	public Map<String, Object> findBytrxorderId(Long trxorderId){
		String sql = "select  user_id from beiker_trxorder where id=?";
		List<Map<String, Object>> userMapList = this.getSimpleJdbcTemplate().queryForList(sql, Long.valueOf(trxorderId));
		if (userMapList != null && userMapList.size() > 0) {
			return userMapList.get(0);
		}
		return null;
	}
}
