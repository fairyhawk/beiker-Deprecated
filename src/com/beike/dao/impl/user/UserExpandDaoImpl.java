package com.beike.dao.impl.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.user.UserExpandDao;
import com.beike.entity.user.UserExpand;

/**      
 * project:beiker  
 * Title:用户扩展信息
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 7:32:06 PM     
 * @version 1.0
 */
@Repository("userExpandDao")
public class UserExpandDaoImpl extends GenericDaoImpl<UserExpand, Long> implements UserExpandDao {

	/* (non-Javadoc)
	 * @see com.beike.dao.user.UserExpandDao#addUserExpand(com.beike.entity.user.UserExpand)
	 */
	@Override
	public Long addUserExpand(UserExpand userExpand) {
		String sql = "insert into beiker_user_expand (userid,nickname,realname,gender,avatar) values (?,?,?,?,?)";
		int result = getSimpleJdbcTemplate().update(sql, userExpand.getUserId(),
				userExpand.getNickName(), userExpand.getRealName(),
				userExpand.getGender(), userExpand.getAvatar());
		if (result > 0) {
			return this.getLastInsertId();
		}
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.user.UserExpandDao#updateUserExpand(com.beike.entity.user.UserExpand)
	 */
	@Override
	public int updateUserExpand(UserExpand userExpand) {
		String updSql = null;
		//更新头像
		if(StringUtils.isNotEmpty(userExpand.getAvatar())){
			updSql = "update beiker_user_expand set avatar=? where userid=?";
			return getSimpleJdbcTemplate().update(updSql, userExpand.getAvatar(),
					userExpand.getUserId());
		}else{
		//更新用户扩展信息
			updSql = "update beiker_user_expand set nickname=?,realname=?,gender=? where userid=?";
			return getSimpleJdbcTemplate().update(updSql, userExpand.getNickName(),
					userExpand.getRealName(), userExpand.getGender(),
					userExpand.getUserId());
		}
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.user.UserExpandDao#getUserExpandById(java.lang.Long)
	 */
	@Override
	public UserExpand getUserExpandByUserId(Long userId) {
		UserExpand userExpand = null;
		if (userId == null) {
			return null;
		} else {
			String sql = "SELECT id,userid,nickname,realname,gender,avatar FROM beiker_user_expand WHERE userid = ?";
			try {
				userExpand = getSimpleJdbcTemplate().queryForObject(sql,
						new RowMapperImpl(), userId);
			} catch (Exception e) {
				//e.printStackTrace();
				return null;
			}
			return userExpand;
		}
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<UserExpand> {
		@Override
		public UserExpand mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserExpand userExpand = new UserExpand();
			userExpand.setId(rs.getLong("id"));
			userExpand.setUserId(rs.getLong("userid"));
			userExpand.setNickName(rs.getString("nickname"));
			userExpand.setRealName(rs.getString("realname"));
			userExpand.setGender(rs.getInt("gender"));
			userExpand.setAvatar(rs.getString("avatar"));
			return userExpand;
		}
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.user.UserExpandDao#getUserInfoByIds(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getUserInfoByIds(String userIds) {
		StringBuilder bufSql = new StringBuilder();
		bufSql.append("select ue.nickname,ue.avatar,u.email,u.user_id as userid ");
		bufSql.append("from beiker_user u left join beiker_user_expand ue on ue.userid=u.user_id ");
		bufSql.append("where u.user_id in (").append(userIds).append(")");
		return this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
	}
}
