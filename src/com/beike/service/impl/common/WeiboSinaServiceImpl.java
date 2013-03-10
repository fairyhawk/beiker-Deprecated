package com.beike.service.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.WeiboDao;
import com.beike.form.AccessToken;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserExpandService;
import com.beike.util.sina.RequestToken;
import com.beike.util.sina.SinaAccessToken;

/**
 * <p>
 * Title:新浪微博账户操作
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
 * @date May 11, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("SINACONFIGService")
public class WeiboSinaServiceImpl implements WeiboService {

	@Autowired
	private WeiboDao weiboDao;
	@Autowired
	private UserExpandService userExpandService;
	
	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}

	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
//		SinaAccessToken sinaAccessToken = (SinaAccessToken) accessToken;
		RequestToken sinaAccessToken=(RequestToken) accessToken;
		String token = sinaAccessToken.getToken();
		String tokenSecret = sinaAccessToken.getTokenSecret();
		long sinaUserId = sinaAccessToken.getUserId();
		String screenName=sinaAccessToken.getScreenName();
		Map<String, String> param = new HashMap<String, String>();
		if (token != null) {
			param.put("sina_token", token);
		}
		if (tokenSecret != null) {
			param.put("sina_tokensecret", tokenSecret);
		}
		if (sinaUserId != 0) {
			param.put("sina_userid", sinaUserId + "");
		}
		if(screenName!=null&&!"".equals(screenName)){
			param.put("sina_screenName", screenName);
		}
		weiboDao.addWeiboProType(param, userid, ProfileType.SINACONFIG);
		userExpandService.addUserExpand(userid, accessToken, ProfileType.SINACONFIG);
	}

	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		Map<String, String> param = weiboDao.getWeiboProType(userid,
				ProfileType.SINACONFIG);
		RequestToken sinaAccessToken=new RequestToken();
		if (param != null && param.size() > 0) {
			String accessToken = param.get("sina_token");
			String tokenSecret = param.get("sina_tokensecret");
			String sinaUserId = param.get("sina_userid");
			String screenName=param.get("sina_screenName");
			if(accessToken!=null&&!"".equals(accessToken)){
				sinaAccessToken.setToken(accessToken);
			}
			if(tokenSecret!=null&&!"".equals(tokenSecret)){
				sinaAccessToken.setTokenSecret(tokenSecret);
			}
			if(sinaUserId!=null&&!"".equals(sinaUserId)){
				sinaAccessToken.setUserId(Long.parseLong(sinaUserId));
			}
			if(screenName!=null&&!"".equals(screenName)){
				sinaAccessToken.setScreenName(screenName);
			}
		}
		return sinaAccessToken;
	}

	public boolean isBindingWeibo(Long userid, ProfileType userProfile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, userProfile);
		if (map == null || map.size() == 0)
			return false;
		else
			return true;
	}

	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		RequestToken sinaAccessToken = (RequestToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		String token = sinaAccessToken.getToken();
		String tokenSecret = sinaAccessToken.getTokenSecret();
		long sinaUserId = sinaAccessToken.getUserId();
		String screenName=sinaAccessToken.getScreenName();
		if (token != null&&!"".equals(token)) {
			param.put("sina_token", token);
		}
		if (tokenSecret != null&&!"".equals(tokenSecret)) {
			param.put("sina_tokensecret", tokenSecret);
		}
		if (sinaUserId != 0) {
			param.put("sina_userid", sinaUserId + "");
		}
		if(screenName!=null&&!"".equals(screenName)){
			param.put("sina_screenName", screenName);
		}
		
		weiboDao.updateWeiboProType(param, userid, profile);
	}

	public boolean isBindingWeiboById(Long userId,String weiboId, ProfileType userProfile) {
		Long xuserId=weiboDao.getWeiboUserIdByProType(weiboId, userProfile);
		if(xuserId!=userId)return false;
		return true;
	}

	public void removeBindingAccessToken(Long userId, ProfileType profileType) {
		weiboDao.removeWeiboProType(userId, profileType);
	}

	public void removeBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		weiboDao.removeBindingAccessTokenByWeiboId(weiboid, profileType);
	}

	public Long getBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		return weiboDao.getWeiboUserIdByProType(weiboid, profileType);
		
	}
	
	public Map<String, String> getWeiboNames(Long userid) {
		return weiboDao.getWeiboScreenName(userid);
		
	}
}
