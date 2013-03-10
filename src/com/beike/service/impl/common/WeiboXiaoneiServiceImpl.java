package com.beike.service.impl.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.WeiboDao;
import com.beike.form.AccessToken;
import com.beike.form.XiaoNeiAccessToken;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserExpandService;

/**
 * <p>
 * Title:人人service相关实现
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
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("XIAONEICONFIGService")
public class WeiboXiaoneiServiceImpl implements WeiboService {
	
	@Autowired
	private UserExpandService userExpandService;
	
	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		XiaoNeiAccessToken xAccessToken=(XiaoNeiAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		if (xAccessToken.getAccess_token() != null) {
			param.put("accessToken", xAccessToken.getAccess_token());
		}

		if (xAccessToken.getCreate_time() != null) {
			param.put("createTime", xAccessToken.getCreate_time());
		}
		param.put("expires_in", xAccessToken.getExpires_in() + "");
		if (xAccessToken.getRefresh_token() != null) {
			param.put("refreshToken", xAccessToken.getRefresh_token());
		}
		if(xAccessToken.getScreenName()!=null&&!"".equals(xAccessToken.getScreenName())){
			param.put("xiaonei_screenName", xAccessToken.getScreenName());
		}
		
		weiboDao.updateWeiboProType(param, userid, profile);
	}

	@Autowired
	private WeiboDao weiboDao;

	public boolean isBindingWeibo(Long userid, ProfileType userProfile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, userProfile);
		if (map == null || map.size() == 0)
			return false;
		else
			return true;
	}

	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
		XiaoNeiAccessToken xAccessToken = (XiaoNeiAccessToken) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		if (xAccessToken.getAccess_token() != null) {
			param.put("accessToken", xAccessToken.getAccess_token());
		}

		if (xAccessToken.getCreate_time() != null) {
			param.put("createTime", xAccessToken.getCreate_time());
		}
		param.put("expires_in", xAccessToken.getExpires_in() + "");
		if (xAccessToken.getRefresh_token() != null) {
			param.put("refreshToken", xAccessToken.getRefresh_token());
		}
		if(xAccessToken.getScreenName()!=null&&!"".equals(xAccessToken.getScreenName())){
			param.put("xiaonei_screenName", xAccessToken.getScreenName());
		}
		param.put("renrenId", xAccessToken.getRenren_id() + "");
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, profile);
		XiaoNeiAccessToken xAccessToken = new XiaoNeiAccessToken();
		if(map!=null&&map.size()>0){
			String accessToken=map.get("accessToken");
			if(accessToken!=null&&!"".equals(accessToken)){
				xAccessToken.setAccess_token(accessToken);
			}
			String createTime=map.get("createTime");
			if(createTime!=null){
				xAccessToken.setCreate_time(createTime);
			}
			String expires_in=map.get("expires_in");
			if(expires_in!=null&&!"".equals(expires_in)){
				xAccessToken.setExpires_in(Long.parseLong(expires_in));
			}
			String refreshToken=map.get("refreshToken");
			if(refreshToken!=null&&!"".equals(refreshToken)){
				xAccessToken.setRefresh_token(refreshToken);
			}
			String renrenId=map.get("renrenId");
			if(renrenId!=null&&!"".equals(renrenId)){
				xAccessToken.setRenren_id(Integer.parseInt(renrenId));
			}
			String screenName=map.get("xiaonei_screenName");
			if(screenName!=null&&!"".equals(screenName)){
				xAccessToken.setScreenName(screenName);
			}
			
		}
		
		return xAccessToken;
	}

	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}

	public boolean isBindingWeiboById(Long userId,String weiboId,ProfileType userProfile) {
		return false;
		
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
