package com.beike.service.common;


import java.util.Map;

import com.beike.common.enums.user.ProfileType;
import com.beike.form.AccessToken;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface WeiboService {
	
	
	/**
	 * 判断该微博用户是否绑定了账户
	 * @param weiboId			微博id
	 * @param userProfile		微博类型
	 * @return
	 */
	public boolean isBindingWeiboById(Long userId,String weiboId,ProfileType userProfile);
	
	
	/**
	 * 判断该微博用户是否绑定
	 * @param userid			用户id
	 * @param userProfile		微博类型
	 * @return
	 */
	public boolean isBindingWeibo(Long userid,ProfileType userProfile);
	
	/**
	 * 给用户绑定微博
	 * @param userid			用户id
	 * @param accessToken		oauthz 需要
	 * @param profile			微博类型
	 */
	public void addBindingAccess(final Long userid,
			AccessToken accessToken,ProfileType profile);
	
	
	/**
	 * 根据用户id 查询绑定微博token
	 * @param userid			用户id
	 * @param profile			扩展属性类型
	 * @return
	 */
	public AccessToken getBindingAccessToken(final Long userid,ProfileType profile);
	
	/**
	 * 更新用户accessToken
	 * @param accessToken		accessToken
	 * @param userid			用户id
	 * @param profile			
	 */
	public void updateBindingAccessToken(AccessToken accessToken,final Long userid,ProfileType profile);
	
	/**
	 * 去除绑定的AccessToken
	 * @param userId			用户id
	 * @param profileType		扩展属性类型
	 */
	public void removeBindingAccessToken(Long userId,ProfileType profileType);
	
	/**
	 * 根据微博用户id去除绑定的AccessToken
	 * @param weiboid			微博用户id
	 * @param profileType		微博类型
	 */
	public void removeBindingAccessTokenByWeiboId(String weiboid,ProfileType profileType);
	
	
	/**
	 * 根据微博ID 查找用户id
	 * @param weiboid
	 * @param profileType
	 * @return
	 */
	public Long getBindingAccessTokenByWeiboId(String weiboid,ProfileType profileType);
	
	public Map<String,String> getWeiboNames(Long userid);
}
