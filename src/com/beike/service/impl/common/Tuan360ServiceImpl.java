package com.beike.service.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.WeiboDao;
import com.beike.form.AccessToken;
import com.beike.service.common.WeiboService;
import com.beike.service.user.UserExpandService;
import com.beike.util.hao3604j.Tuan360Model;

@Service("TUAN360CONFIGService")
public class Tuan360ServiceImpl implements WeiboService{

	@Autowired
	private WeiboDao weiboDao;
	@Autowired
	private UserExpandService userExpandService;
	
	@Override
	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
		Tuan360Model tuan360Model=(Tuan360Model) accessToken;
		String token = tuan360Model.getToken();
		String tokenSecret = tuan360Model.getTokenSecret();
		String tuan360UserId = tuan360Model.getQid();
		String qname = tuan360Model.getQname();
		String from = tuan360Model.getFrom();
		String qmail = tuan360Model.getQmail();
		Map<String, String> param = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(token)) {
			param.put("tuan360_token", token);
		}
		if (StringUtils.isNotEmpty(tokenSecret)) {
			param.put("tuan360_tokensecret", tokenSecret);
		}
		if (StringUtils.isNotEmpty(tuan360UserId)) {
			param.put("tuan360_qid", tuan360UserId);
		}
		if(StringUtils.isNotEmpty(qname)){
			param.put("tuan360_screenName", qname);
		}
		if(StringUtils.isNotEmpty(from)){
			param.put("tuan360_from", from);
		}
		if(StringUtils.isNotEmpty(qmail)){
			param.put("tuan360_qmail", qmail);
		}
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	@Override
	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		Map<String, String> param = weiboDao.getWeiboProType(userid,
				ProfileType.TUAN360CONFIG);
		Tuan360Model tuan360Model= new Tuan360Model();
		if (param != null && param.size() > 0) {
			String token =param.get("tuan360_token");
			String tokenSecret = param.get("tuan360_tokensecret");
			String tuan360UserId = param.get("tuan360_qid");
			String qName = param.get("tuan360_screenName");
			String from = param.get("tuan360_from");
			String qmail = param.get("tuan360_qmail");
			
			if(StringUtils.isNotEmpty(token)){
				tuan360Model.setToken(token);
			}if(StringUtils.isNotEmpty(tokenSecret)){
				tuan360Model.setTokenSecret(tokenSecret);
			}if(StringUtils.isNotEmpty(tuan360UserId)){
				tuan360Model.setQid(tuan360UserId);
			}if(StringUtils.isNotEmpty(qName)){
				tuan360Model.setQname(qName);
			}if(StringUtils.isNotEmpty(from))	{
				tuan360Model.setFrom(from);
			}if(StringUtils.isNotEmpty(qmail)){
				tuan360Model.setQmail(qmail);
			}
		}
		return tuan360Model;
	}
	
	@Override
	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		Tuan360Model tuan360Model = (Tuan360Model) accessToken;
		Map<String, String> param = new HashMap<String, String>();
		String token = tuan360Model.getToken();
		String tokenSecret = tuan360Model.getTokenSecret();
		String tuan360UserId = tuan360Model.getQid();
		String qname = tuan360Model.getQname();
		String from = tuan360Model.getFrom();
		String qmail = tuan360Model.getQmail();
		if (StringUtils.isNotEmpty(token)) {
			param.put("tuan360_token", token);
		}
		if (StringUtils.isNotEmpty(tokenSecret)) {
			param.put("tuan360_tokensecret", tokenSecret);
		}
		if (StringUtils.isNotEmpty(tuan360UserId)) {
			param.put("tuan360_qid", tuan360UserId);
		}
		if(StringUtils.isNotEmpty(qname)){
			param.put("tuan360_screenName", qname);
		}
		if(StringUtils.isNotEmpty(from)){
			param.put("tuan360_from", from);
		}
		if(StringUtils.isNotEmpty(qmail)){
			param.put("tuan360_qmail", qmail);
		}
		weiboDao.updateWeiboProType(param, userid, profile);
	}

	@Override
	public Long getBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		return weiboDao.getWeiboUserIdByProType(weiboid, profileType);
	}

	@Override
	public Map<String, String> getWeiboNames(Long userid) {
		return weiboDao.getWeiboScreenName(userid);
	}

	@Override
	public boolean isBindingWeibo(Long userid, ProfileType userProfile) {
		Map<String, String> map = weiboDao.getWeiboProType(userid, userProfile);
		if (map == null || map.size() == 0)
			return false;
		else
			return true;
	}

	@Override
	public boolean isBindingWeiboById(Long userId, String weiboId,
			ProfileType userProfile) {
		Long xuserId=weiboDao.getWeiboUserIdByProType(weiboId, userProfile);
		if(xuserId!=userId)return false;
		return true;
	}

	@Override
	public void removeBindingAccessToken(Long userId, ProfileType profileType) {
		weiboDao.removeWeiboProType(userId, profileType);
	}

	@Override
	public void removeBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		weiboDao.removeBindingAccessTokenByWeiboId(weiboid, profileType);
	}

	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}
}
