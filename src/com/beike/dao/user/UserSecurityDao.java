package com.beike.dao.user;

import java.util.Map;



/**
 * 用户安全中心
 * @author janwen
 * Sep 20, 2012
 */
public interface UserSecurityDao {

	
	
	public Map getUserBySign(String sign);
	
	
	public void updateSign(String sign);
}
