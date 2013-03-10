/**
 * 
 */
package com.beike.service.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.WeiboDao;
import com.beike.form.AccessToken;
import com.beike.form.BaiduAccessToken;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserExpandService;

/**
 * @author a
 *
 */
@Service("BAIDUCONFIGService")
public class BaiduServiceImpl implements WeiboService {
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
	
	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#addBindingAccess(java.lang.Long, com.beike.form.AccessToken, com.beike.common.enums.user.ProfileType)
	 */
	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
		BaiduAccessToken bAccessToken = (BaiduAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		
		param.put("baidu_id", bAccessToken.getBaidu_id() + "");
		if(bAccessToken.getScreenName()!=null&&!"".equals(bAccessToken.getScreenName())){
			param.put("baidu_screenName", bAccessToken.getScreenName());
		}
		if (bAccessToken.getAccess_token() != null) {
			param.put("baidu_token", bAccessToken.getAccess_token());
		}
		param.put("baidu_expires_in", bAccessToken.getExpires_in() + "");
		
		if (bAccessToken.getRefresh_token() != null) {
			param.put("baidu_refreshToken", bAccessToken.getRefresh_token());
		}
		if (bAccessToken.getScope() != null) {
			param.put("baidu_scope", bAccessToken.getScope());
		}
		if (bAccessToken.getSession_key() != null) {
			param.put("baidu_session_key", bAccessToken.getSession_key());
		}
		if (bAccessToken.getSession_secret() != null) {
			param.put("baidu_Session_secret", bAccessToken.getSession_secret());
		}
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#getBindingAccessToken(java.lang.Long, com.beike.common.enums.user.ProfileType)
	 */
	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, profile);
		BaiduAccessToken bAccessToken = new BaiduAccessToken();
		bAccessToken.setBaidu_id("");
		if(map!=null&&map.size()>0){
			String baidu_id=map.get("baidu_id");
			if(baidu_id!=null&&!"".equals(baidu_id)){
				bAccessToken.setBaidu_id(baidu_id);
			}
			String screenName=map.get("baidu_screenName");
			if(screenName!=null&&!"".equals(screenName)){
				bAccessToken.setScreenName(screenName);
			}
			String accessToken=map.get("baidu_token");
			if(accessToken!=null&&!"".equals(accessToken)){
				bAccessToken.setAccess_token(accessToken);
			}
			String expires_in=map.get("baidu_expires_in");
			if(expires_in!=null&&!"".equals(expires_in)){
				bAccessToken.setExpires_in(Long.parseLong(expires_in));
			}
			String refreshToken=map.get("baidu_refreshToken");
			if(refreshToken!=null&&!"".equals(refreshToken)){
				bAccessToken.setRefresh_token(refreshToken);
			}
			
			String scope=map.get("baidu_scope");
			if(scope!=null&&!"".equals(scope)){
				bAccessToken.setScope(scope);
			}
			
			String session_key=map.get("baidu_session_key");
			if(session_key!=null&&!"".equals(session_key)){
				bAccessToken.setSession_key(session_key);
			}
			
			String session_secret=map.get("baidu_session_secret");
			if(session_secret!=null&&!"".equals(session_secret)){
				bAccessToken.setSession_secret(session_secret);
			}
		}
		return bAccessToken;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#getBindingAccessTokenByWeiboId(java.lang.String, com.beike.common.enums.user.ProfileType)
	 */
	public Long getBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		return weiboDao.getWeiboUserIdByProType(weiboid, profileType);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#getWeiboNames(java.lang.Long)
	 */
	public Map<String, String> getWeiboNames(Long userid) {
		return weiboDao.getWeiboScreenName(userid);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#isBindingWeibo(java.lang.Long, com.beike.common.enums.user.ProfileType)
	 */
	public boolean isBindingWeibo(Long userid, ProfileType userProfile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, userProfile);
		if (map == null || map.size() == 0){
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#isBindingWeiboById(java.lang.Long, java.lang.String, com.beike.common.enums.user.ProfileType)
	 */
	public boolean isBindingWeiboById(Long userId, String weiboId,
			ProfileType userProfile) {
		Long xuserId = weiboDao.getWeiboUserIdByProType(weiboId, userProfile);
		if(xuserId!=userId)return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#removeBindingAccessToken(java.lang.Long, com.beike.common.enums.user.ProfileType)
	 */
	public void removeBindingAccessToken(Long userId, ProfileType profileType) {
		weiboDao.removeWeiboProType(userId, profileType);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#removeBindingAccessTokenByWeiboId(java.lang.String, com.beike.common.enums.user.ProfileType)
	 */
	public void removeBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		weiboDao.removeBindingAccessTokenByWeiboId(weiboid, profileType);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#updateBindingAccessToken(com.beike.form.AccessToken, java.lang.Long, com.beike.common.enums.user.ProfileType)
	 */
	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		BaiduAccessToken bAccessToken=(BaiduAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		param.put("baidu_id", bAccessToken.getBaidu_id() + "");
		if(bAccessToken.getScreenName()!=null&&!"".equals(bAccessToken.getScreenName())){
			param.put("baidu_screenName", bAccessToken.getScreenName());
		}
		if (bAccessToken.getAccess_token() != null) {
			param.put("baidu_token", bAccessToken.getAccess_token());
		}
		param.put("baidu_expires_in", bAccessToken.getExpires_in() + "");
		
		if (bAccessToken.getRefresh_token() != null) {
			param.put("baidu_refreshToken", bAccessToken.getRefresh_token());
		}
		if (bAccessToken.getScope() != null) {
			param.put("baidu_scope", bAccessToken.getScope());
		}
		if (bAccessToken.getSession_key() != null) {
			param.put("baidu_session_key", bAccessToken.getSession_key());
		}
		if (bAccessToken.getSession_secret() != null) {
			param.put("baidu_Session_secret", bAccessToken.getSession_secret());
		}
		weiboDao.updateWeiboProType(param, userid, profile);
	}
}
