package com.beike.dao;

import java.io.Serializable;
import java.util.Map;

import com.beike.common.enums.user.ProfileType;
import com.beike.entity.user.UserProfile;

/**
 * <p>Title:微博、SNS类操作</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface WeiboDao extends GenericDao<UserProfile, Long>{
	/**
	 * 微博、SNS扩展属性查询
	 * @param userid   用户id		
	 * @param type	   微博、SNS类型
	 * @return
	 */
	public Map<String,String> getWeiboProType(Long userid,ProfileType type);
	
	
	/**
	 * 根据微博UserId 查找用户id
	 * @param weiboid	微博id
	 * @param type		微博类型
	 * @return			用户id
	 */
	public Long getWeiboUserIdByProType(String weiboid,ProfileType type);
	
	
	/**
	 * 增加微博、SNS扩展信息
	 * @param map		
	 */
	public void addWeiboProType(final Map<String,String> map,Long userid,ProfileType type);
	
	
	/**
	 * 更新微博、SNS扩展属性
	 * @param map
	 */
	public void updateWeiboProType(Map<String,String> map,Long userid,ProfileType type);
	
	
	/**
	 * 去除用户微博扩展属性
	 * @param userid		用户id
	 * @param profileType	微博类型
	 */
	public void removeWeiboProType(Long userid,ProfileType profileType);
	
	/**
	 * 根据微博用户id去除绑定的AccessToken
	 * @param weiboid			微博用户id
	 * @param profileType		微博类型
	 */
	public void removeBindingAccessTokenByWeiboId(String weiboid,ProfileType profileType);
	
	public Map<String,String>  getWeiboScreenName(Long userid);
}
