package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;
import com.beike.wap.dao.MUserDao;
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
@Repository("mUserDao")
public class MUserDaoImpl extends GenericDaoImpl<User, Long> implements MUserDao {

	@Override
	public User findUserByEmail(String email) {
		String sql = "select * from beiker_user where email=?";
		User user = null;
		try {
			List<User> userList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), email);
			if (userList.size() > 0) {
				user = userList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}

	@Override
	public User findUserByMobile(String mobile) {
		String sql = "select * from beiker_user where mobile=?";
		User user = null;
		try {
			List<User> userList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), mobile);
			if (userList.size() > 0) {
				user = userList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setEmail(rs.getString("email"));
			user.setEmail_isavalible(rs.getLong("email_isavalible"));
			user.setId(rs.getInt("user_id"));
			user.setIsavalible(rs.getLong("isavalible"));
			user.setMobile(rs.getString("mobile"));
			user.setMobile_isavalible(rs.getLong("mobile_isavalible"));
			user.setPassword(rs.getString("password"));
			user.setCustomerkey(rs.getString("customerkey"));
			return user;
		}
	}

	@Override
	public void addUser(UserForm userForm) {
		String sql = "insert into beiker_user (email,mobile,email_isavalible,mobile_isavalible,password,isavalible,customerkey,createdate) values (?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(sql, userForm.getEmail(),
				userForm.getMobile(),
				userForm.getEmail_isavalible() + "",
				userForm.getMobile_isavalible() + "", 
				userForm.getPassword(),
				userForm.getIsavalible() + "", 
				userForm.getCustomerKey(),
				new Date());
	}

	@Override
	public User findById(long userId) {
		String sql = "select * from beiker_user where user_id=?";
		User user = null;
		try {
			List<User> userList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), userId);
			if (userList.size() > 0) {
				user = userList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public void addProfile(ProfileForm proFileForm) {
		String sql = "insert into beiker_userprofile (name,value,profiletype,userid,profiledate) values(?,?,?,?,?)";
		Date date = new Date();
		getSimpleJdbcTemplate().update(sql, proFileForm.getName(),
				proFileForm.getValue(),
				proFileForm.getProFileType().toString(),
				proFileForm.getUserid(), date);
	}
	
	@Override
	public void updateUserProfile(UserProfile userProfile) {
		String sql = "update beiker_userprofile b set b.name=?,b.value=? where b.id=?";
		getSimpleJdbcTemplate().update(sql, userProfile.getName(),
				userProfile.getValue(), userProfile.getId());
	}

	@Override
	public UserProfile getUserProfile(Long userid, String name) {
		String sql = "select * from beiker_userprofile where userid=? and name=?";
		UserProfile up = null;
		try {
			List<UserProfile> upList = getSimpleJdbcTemplate().query(sql,
					new ProfileRowMapperImpl(), userid, name);
			if (upList.size() > 0) {
				up = upList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return up;
	}

	protected class ProfileRowMapperImpl implements ParameterizedRowMapper<UserProfile> {
		public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserProfile userProfile = new UserProfile();
			userProfile.setId(rs.getLong("id"));
			userProfile.setName(rs.getString("name"));
			userProfile.setValue(rs.getString("value"));
			userProfile.setUserid(rs.getLong("userid"));
			userProfile.setProfiledate(rs.getDate("profiledate"));

			return userProfile;
		}
	}
}
