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
import com.beike.form.TencentqqAccessToken;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserExpandService;

/**
 * @author a
 *
 */
@Service("QQCONFIGService")
public class TencentqqServiceImpl implements WeiboService {
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
		TencentqqAccessToken bAccessToken = (TencentqqAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		
		param.put("qq_openid", bAccessToken.getOpenid() + "");
		if(bAccessToken.getScreenName()!=null&&!"".equals(bAccessToken.getScreenName())){
			param.put("qq_screenName", bAccessToken.getScreenName());
		}
		if (bAccessToken.getAccess_token() != null) {
			param.put("qq_token", bAccessToken.getAccess_token());
		}
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.common.WeiboService#getBindingAccessToken(java.lang.Long, com.beike.common.enums.user.ProfileType)
	 */
	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, profile);
		TencentqqAccessToken bAccessToken = new TencentqqAccessToken();
		bAccessToken.setOpenid("");
		if(map!=null&&map.size()>0){
			String openid=map.get("qq_openid");
			if(openid!=null&&!"".equals(openid)){
				bAccessToken.setOpenid(openid);
			}
			String screenName=map.get("qq_screenName");
			if(screenName!=null&&!"".equals(screenName)){
				bAccessToken.setScreenName(screenName);
			}
			String accessToken=map.get("qq_token");
			if(accessToken!=null&&!"".equals(accessToken)){
				bAccessToken.setAccess_token(accessToken);
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
		TencentqqAccessToken bAccessToken=(TencentqqAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		param.put("qq_openid", bAccessToken.getOpenid() + "");
		if(bAccessToken.getScreenName()!=null&&!"".equals(bAccessToken.getScreenName())){
			param.put("qq_screenName", bAccessToken.getScreenName());
		}
		if (bAccessToken.getAccess_token() != null) {
			param.put("qq_token", bAccessToken.getAccess_token());
		}
		weiboDao.updateWeiboProType(param, userid, profile);
	}
}
