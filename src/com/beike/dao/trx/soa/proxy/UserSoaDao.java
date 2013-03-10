package com.beike.dao.trx.soa.proxy;

import java.util.Map;

import com.beike.dao.GenericDao;

/**
 * @Title: UserSoaDao.java
 * @Package com.beike.dao.trx
 * @Description: 交易解耦。伪SOA,UserDAO代理类
 * @date May 16, 2011 10:47:50 AM
 * @author wh.cheng
 * @version v1.0
 */
public interface UserSoaDao extends GenericDao<Object, Long> {

	/**
	 * 根据手机号查找用户
	 * 
	 * @param mobile
	 *            手机号
	 * @return 用户对象
	 */
	/*
	 * public Map<String,Object> findUserByMobile(String mobile);
	 *//**
	 * 根据email查找用户
	 * 
	 * @param email
	 *            邮箱
	 * @return 用户对象
	 */
	/*
	 * public Map<String,Object> findUserByEmail(String email);
	 */
	/**
	 * 根据userId查找用户
	 * 
	 * @param id
	 *            主键ID
	 * @return 用户对象Map
	 */
	public Map<String, Object> findById(Long id);
	
	/**
	 * 根据userId查找用户
	 * 
	 * @param id
	 *            主键ID
	 * @return 用户对象Map
	 */
	public Map<String, Object> findMobileById(Long id);

	/**
	 * 查用户
	 * @param id
	 * @return
	 */
	
	public Map<String, Object> findUserInfoById(Long id);
	
	/**
	 * 预定用到，查询用户ID
	 * @param trxorderId
	 * @return
	 */
	public Map<String, Object> findBytrxorderId(Long trxorderId);
	
}
