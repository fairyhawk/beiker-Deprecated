package com.beike.dao.impl.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.user.UserAddressDao;
import com.beike.entity.user.UserAddress;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 10:25:04 AM     
 * @version 1.0
 */
@Repository("userAddressDao")
public class UserAddressDaoImpl extends GenericDaoImpl<UserAddress, Long> implements
		UserAddressDao {

	@Override
	public Long addUserAddress(UserAddress address) {
		String sql = "insert into beiker_user_address (userid,province,city,area,address) values (?,?,?,?,?)";
		int result = getSimpleJdbcTemplate().update(sql, address.getUserid(),
				address.getProvince(),address.getCity(),
				address.getArea(),address.getAddress());
		if (result > 0) {
			return this.getLastInsertId();
		}
		return 0L;
	}

	@Override
	public int updateUserAddress(UserAddress address) {
		String updSql = "update beiker_user_address set province=?,city=?,area=?,address=? where userid=?";
		return getSimpleJdbcTemplate().update(updSql, address.getProvince(),
				address.getCity(), address.getArea(),address.getAddress(),
				address.getUserid());
	}

	@Override
	public List<UserAddress> getUserAddressByUserId(Long userId) {
		String sql = "SELECT id,userid,province,city,area,address FROM beiker_user_address WHERE userid = ?";
		return getSimpleJdbcTemplate().query(sql,new RowMapperImpl(), userId);
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<UserAddress> {
		@Override
		public UserAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserAddress address = new UserAddress();
			address.setId(rs.getLong("id"));
			address.setUserid(rs.getLong("userid"));
			address.setProvince(rs.getString("province"));
			address.setCity(rs.getString("city"));
			address.setArea(rs.getString("area"));
			address.setAddress(rs.getString("address"));
			return address;
		}
	}
}
