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
import com.beike.util.tencent.OauthKey;
import com.beike.util.tencent.ResModel;

/**
 * <p>Title: 腾讯微博Service</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Sep 14, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("TENCENTCONFIGService")
public class WeiboTencentServiceImpl implements WeiboService {
	@Autowired
	private WeiboDao weiboDao;
	
	@Autowired
	private UserExpandService userExpandService;
	
	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
		
		ResModel oauthKey=(ResModel) accessToken;
		
		String screenname=oauthKey.getWeiboname();
		String tencentid=oauthKey.getWeiboid();
		String token=oauthKey.getToken();
		String secret=oauthKey.getTokenSecret();
		
		Map<String, String> param = new HashMap<String, String>();
		if(screenname!=null){
			param.put("tencent_screenName", screenname);
		}
		if(tencentid!=null){
			param.put("tencentid", tencentid);
		}
		
		if(token!=null){
			param.put("tencent_token", token);
		}
		if(secret!=null){
			param.put("tencent_tokensecret", secret);
		}
		
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		
		Map<String, String> map = weiboDao.getWeiboProType(userid, profile);
		
		ResModel ok=new ResModel();
		
		if(map!=null&&map.size()>0){
			String tencent_screenname=map.get("tencent_screenName");
			if(tencent_screenname!=null){
				ok.setWeiboname(tencent_screenname);
			}
			
			String tencentid=map.get("tencentid");
			if(tencentid!=null){
				ok.setWeiboid(tencentid);
			}
			
			String tencent_token=map.get("tencent_token");
			if(tencent_token!=null){
				ok.setToken(tencent_token);
			}
			
			String tencent_tokensecret=map.get("tencent_tokensecret");
			if(tencent_tokensecret!=null){
				ok.setTokenSecret(tencent_tokensecret);
			}
		}
		return ok;
	}

	public Long getBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		return weiboDao.getWeiboUserIdByProType(weiboid, profileType);
	}

	public Map<String, String> getWeiboNames(Long userid) {
		return weiboDao.getWeiboScreenName(userid);
	}

	public boolean isBindingWeibo(Long userid, ProfileType userProfile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, userProfile);
		if (map == null || map.size() == 0)
			return false;
		else
			return true;
	}

	public boolean isBindingWeiboById(Long userId, String weiboId,
			ProfileType userProfile) {
		return false;

	}

	public void removeBindingAccessToken(Long userId, ProfileType profileType) {
		weiboDao.removeWeiboProType(userId, profileType);

	}

	public void removeBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		weiboDao.removeBindingAccessTokenByWeiboId(weiboid, profileType);

	}

	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		ResModel oauthKey=(ResModel)accessToken;
		String screenname=oauthKey.getWeiboname();
		String tencentid=oauthKey.getWeiboid();
		String token=oauthKey.getToken();
		String secret=oauthKey.getTokenSecret();
		
		Map<String, String> param = new HashMap<String, String>();
		if(screenname!=null){
			param.put("tencent_screenName", screenname);
		}
		if(tencentid!=null){
			param.put("tencentid", tencentid);
		}
		
		if(token!=null){
			param.put("tencent_token", token);
		}
		if(secret!=null){
			param.put("tencent_tokensecret", secret);
		}
		
		weiboDao.updateWeiboProType(param, userid, profile);

	}
	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}
}
