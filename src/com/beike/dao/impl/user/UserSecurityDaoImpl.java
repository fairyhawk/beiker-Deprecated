package com.beike.dao.impl.user;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.user.UserSecurityDao;


@Repository("userSecurityDao")
public class UserSecurityDaoImpl extends GenericDaoImpl implements
		UserSecurityDao {

	@Override
	public Map getUserBySign(String sign) {
		String sql = "SELECT busu.userid FROM beiker_user_security_url busu WHERE busu.urlsign=?  AND busu.isclicked='n' ORDER BY busu.helptime DESC LIMIT 1";
		List<Map> userids = getJdbcTemplate().queryForList(sql,new Object[]{sign});
		if(userids != null && userids.size() > 0){
			return userids.get(0);
		}
		return null;
	}

	@Override
	public void updateSign(String sign) {
		String sql = "UPDATE beiker_user_security_url busu SET busu.isclicked='y' WHERE busu.urlsign=?";
		getJdbcTemplate().update(sql, new Object[]{sign});
	}

}
