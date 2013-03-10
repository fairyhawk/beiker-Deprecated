package com.beike.dao.impl.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.user.UserDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;

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
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("userDao")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {
	@Override
	public User findById(Long id) {
		User user = null;
		if (id == null) {
			return null;
		} else {
			String sql = "SELECT bu.email,bu.email_isavalible,bu.user_id,bu.isavalible,bu.mobile,bu.mobile_isavalible,bu.password,bu.customerkey,bu.createdate FROM beiker_user bu WHERE bu.user_id = ?";
			try {
				user = getSimpleJdbcTemplate().queryForObject(sql,
						new RowMapperImpl(), id);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return user;
		}
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<User> {
		@Override
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
			user.setRegistDate(rs.getDate("createdate"));
			return user;
		}
	}

	@Override
	public void addUser(UserForm userForm) {
		String sql = "insert into beiker_user (email,mobile,email_isavalible,mobile_isavalible,password,isavalible,customerkey,createdate,user_ip) values (?,?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(sql, userForm.getEmail(),
				userForm.getMobile(), userForm.getEmail_isavalible() + "",
				userForm.getMobile_isavalible() + "", userForm.getPassword(),
				userForm.getIsavalible() + "", userForm.getCustomerKey(),
				new Date(), userForm.getUserIp());
	}

	@Override
	public void addUser_new(UserForm userForm) {
		String sql = "insert into beiker_user (email,mobile,email_isavalible,mobile_isavalible,password,isavalible,customerkey,createdate,user_ip) values (?,?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(sql, userForm.getEmail(),
				userForm.getMobile(), userForm.getEmail_isavalible() + "",
				userForm.getMobile_isavalible() + "", userForm.getPassword(),
				userForm.getIsavalible() + "", userForm.getCustomerKey(),
				new Date(), userForm.getUserIp());
	}

	@Override
	public User findUserByEmail(String email) {
		// author wenjie.mai sql优化
		if (StringUtils.isBlank(email)) {
			return null;
		}
		String sql = "select user_id,email,email_isavalible,mobile,mobile_isavalible,password,isavalible,customerkey,createdate from beiker_user where email=? limit 1";
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
		// author wenjie.mai sql优化
		if (StringUtils.isBlank(mobile)) {
			return null;
		}
		String sql = "select user_id,email,email_isavalible,mobile,mobile_isavalible,password,isavalible,customerkey,createdate from beiker_user where mobile=? limit 1";
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

	@Override
	public User findUserByPassword(String mobile, String email, String password) {
		// author wenjie.mai sql优化
		if (StringUtils.isBlank(mobile) && StringUtils.isBlank(email)
				&& StringUtils.isBlank(password)) {
			return null;
		}
		String sql = "select user_id,email,email_isavalible,mobile,mobile_isavalible,password,isavalible,customerkey,createdate from beiker_user where ";
		String username = "";
		if (mobile != null && !"".equals(mobile)) {
			sql += "mobile=? and ";
			username = mobile;
		} else if (email != null && !"".equals(email)) {
			sql += "email=? and ";
			username = email;
		}
		sql += " password=?";
		User user = null;
		sql += " limit 1";
		try {
			List<User> userList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), username, password);
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
	public void addProfile(ProfileForm proFileForm) {
		String sql = "insert into beiker_userprofile (name,value,profiletype,userid,profiledate) values(?,?,?,?,?)";
		Date date = new Date();
		getSimpleJdbcTemplate().update(sql, proFileForm.getName(),
				proFileForm.getValue(),
				proFileForm.getProFileType().toString(),
				proFileForm.getUserid(), date);

	}

	@Override
	public UserProfile getUserProfile(Long userid, String value, String name) {
		// author wenjie.mai sql优化
		String sql = "select id,name,value,profiletype,userid,profiledate from beiker_userprofile where userid=? and name=?";
		if (value != null && !"".equals(value)) {
			sql += "  and  value=?";
		}
		UserProfile up = null;
		try {
			if (value != null && !"".equals(value)) {
				List<UserProfile> upList = getSimpleJdbcTemplate().query(sql,
						new ProfileRowMapperImpl(), userid, name, value);
				if (upList.size() > 0) {
					up = upList.get(0);
				}
			} else {
				List<UserProfile> upList = getSimpleJdbcTemplate().query(sql,
						new ProfileRowMapperImpl(), userid, name);
				if (upList.size() > 0) {
					up = upList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return up;
	}

	protected class ProfileRowMapperImpl implements
			ParameterizedRowMapper<UserProfile> {
		@Override
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

	@Override
	public void updateUserProfile(UserProfile userProfile) {
		String sql = "update beiker_userprofile b set b.name=?,b.value=? where b.id=?";
		getSimpleJdbcTemplate().update(sql, userProfile.getName(),
				userProfile.getValue(), userProfile.getId());
	}

	@Override
	public void updateUser(UserForm userForm) {
		String sql = "update beiker_user set email=?,email_isavalible=?,mobile=?,mobile_isavalible=?,isavalible=?,customerkey=?,password=? where user_id=?";
		getSimpleJdbcTemplate().update(sql, userForm.getEmail(),
				userForm.getEmail_isavalible() + "", userForm.getMobile(),
				userForm.getMobile_isavalible() + "",
				userForm.getIsavalible() + "", userForm.getCustomerKey(),
				userForm.getPassword(), userForm.getId());
	}

	@Override
	public User getUserByUserKey(String userKey) {
		// author wenjie.mai sql优化
		String sql = "select bu.user_id,bu.email,bu.email_isavalible,bu.mobile,bu.mobile_isavalible,bu.password,bu.isavalible,bu.customerkey,bu.createdate from beiker_user bu where bu.customerkey=?";

		User user = null;
		try {
			user = getSimpleJdbcTemplate().queryForObject(sql,
					new RowMapperImpl(), userKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;

	}

	@Override
	public Long getUnusedTrxorder(Long userId) {
		String sql = "select  count(1) as count from  beiker_trxorder_goods t1 left join beiker_trxorder t2 on t1.trxorder_id = t2.id where t1.trx_status='SUCCESS' and t2.user_id=?";
		List count = this.getSimpleJdbcTemplate().queryForList(sql, userId);
		if (count == null || count.size() == 0)
			return 0L;
		Map map = (Map) count.get(0);
		Long countx = (Long) map.get("count");
		if (countx == null)
			return 0L;
		return countx;
	}

	@Override
	public List<Map<String, Object>> readyLoseTrxorder(Long userId) {
		String sql = "select t1.order_lose_abs_date,t1.order_lose_date, t1.create_date from  beiker_trxorder_goods t1 left join beiker_trxorder t2 on t1.trxorder_id = t2.id where t1.trx_status='SUCCESS'  and t2.user_id=? ";

		List list = this.getSimpleJdbcTemplate().queryForList(sql, userId);
		if (list == null || list.size() == 0)
			return null;
		List<Map<String, Object>> listDate = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> mapDate = new HashMap<String, Object>();
			Map map = (Map) list.get(i);
			Integer order_lose_abs_date = (Integer) map
					.get("order_lose_abs_date");
			Timestamp order_lose_date = (Timestamp) map.get("order_lose_date");
			Timestamp create_date = (Timestamp) map.get("create_date");
			mapDate.put("order_lose_abs_date", order_lose_abs_date);
			mapDate.put("order_lose_date", order_lose_date);
			mapDate.put("create_date", create_date);
			listDate.add(mapDate);
		}

		return listDate;
	}

	@Override
	public Long unComment(Long userId) {
		String sql = "select  count(1) as count from  beiker_trxorder_goods t1 left join beiker_trxorder t2 on t1.trxorder_id = t2.id where t1.trx_status='SUCCESS' and t1.auth_status='RECOVERY' and t1.comment_id>0 and t2.user_id=?";
		List countlist = this.getSimpleJdbcTemplate().queryForList(sql, userId);
		if (countlist == null)
			return 0L;
		Map map = (Map) countlist.get(0);
		Long countx = (Long) map.get("count");
		if (countx == null)
			return 0L;
		return countx;
	}

	@Override
	public void updateUserPassWord(Long userId, String password) {

		StringBuilder updatepwd = new StringBuilder();

		updatepwd.append("UPDATE beiker_user SET PASSWORD = '")
				.append(password).append("' ");
		updatepwd.append("WHERE user_id = ").append(userId);
		getSimpleJdbcTemplate().update(updatepwd.toString());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getUserInfoByUserIds(List<Long> idlist) {
		
		StringBuilder usersql = new StringBuilder();
		
		usersql.append("SELECT bu.user_id,bu.email,bu.mobile FROM beiker_user bu ");
		usersql.append("WHERE 1=1 ");
		
		if(idlist != null && idlist.size()>0){
			usersql.append(" AND bu.user_id  IN (").append(com.beike.util.StringUtils.arrayToString(idlist.toArray(),",")).append(") ");
		}
		
		List rs = this.getJdbcTemplate().queryForList(usersql.toString());
		
		return rs;
	}
}
