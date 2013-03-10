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
import com.beike.util.alipay.AlipayModel;
 /*
 * com.beike.service.impl.common.AlipayServiceImpl.java
 * @description:
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-8，xuxiaoxian ,create class
 *
 */
@Service("ALIPAYCONFIGService")
public class AlipayServiceImpl implements WeiboService{

	@Autowired
	private WeiboDao weiboDao;
	
	@Autowired
	private UserExpandService userExpandService;
	
	@Override
	public void addBindingAccess(Long userid, AccessToken accessToken,
			ProfileType profile) {
		
		AlipayModel alipayModel = (AlipayModel)accessToken;
		String user_id = alipayModel.getUser_id();
		String real_name = alipayModel.getReal_name();
		String sign = alipayModel.getSign();
		String token  = alipayModel.getToken();
		String notify_id = alipayModel.getNotify_id();
		String email = alipayModel.getEmail();

		Map<String, String> param = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(user_id)) {
			param.put("alipay_user_id", user_id);
		}
		if (StringUtils.isNotEmpty(real_name)) {
			param.put("alipay_screenName", real_name);
		}
		if (StringUtils.isNotEmpty(sign)) {
			param.put("alipay_sign", sign);
		}
		if(StringUtils.isNotEmpty(token)){
			param.put("alipay_token", token);
		}
		if(StringUtils.isNotEmpty(notify_id)){
			param.put("alipay_notify_id", notify_id);
		}
		if(StringUtils.isNotEmpty(email)){
			param.put("alipay_email", email);
		}
		weiboDao.addWeiboProType(param, userid, profile);
		userExpandService.addUserExpand(userid, accessToken, profile);
	}

	@Override
	public AccessToken getBindingAccessToken(Long userid, ProfileType profile) {
		
		Map<String, String> param = weiboDao.getWeiboProType(userid,
				ProfileType.ALIPAYCONFIG);
		AlipayModel alipayModel = new AlipayModel();
		if (param != null && param.size() > 0) {
			
			String user_id = param.get("alipay_user_id");
			String real_name = param.get("alipay_screenName");
			String sign = param.get("alipay_sign");
			String token  = param.get("alipay_token");
			String notify_id = param.get("alipay_notify_id");		
			String email = param.get("alipay_email");
			
			if(StringUtils.isNotEmpty(user_id)){
				alipayModel.setUser_id(user_id);
			}if(StringUtils.isNotEmpty(real_name)){
				alipayModel.setReal_name(real_name);
			}if(StringUtils.isNotEmpty(sign)){
				alipayModel.setSign(sign);
			}if(StringUtils.isNotEmpty(token)){
				alipayModel.setToken(token);
			}if(StringUtils.isNotEmpty(notify_id))	{
				alipayModel.setNotify_id(notify_id);
			}if(StringUtils.isNotEmpty(email)){
				alipayModel.setNotify_id(email);
			}
		}
		return alipayModel;
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

	@Override
	public void updateBindingAccessToken(AccessToken accessToken, Long userid,
			ProfileType profile) {
		
		AlipayModel alipayModel = (AlipayModel)accessToken;
		String user_id = alipayModel.getUser_id();
		String real_name = alipayModel.getReal_name();
		String sign = alipayModel.getSign();
		String token  = alipayModel.getToken();
		String notify_id = alipayModel.getNotify_id();
		String email = alipayModel.getEmail();

		Map<String, String> param = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(user_id)) {
			param.put("alipay_user_id", user_id);
		}
		if (StringUtils.isNotEmpty(real_name)) {
			param.put("alipay_screenName", real_name);
		}
		if (StringUtils.isNotEmpty(sign)) {
			param.put("alipay_sign", sign);
		}
		if(StringUtils.isNotEmpty(token)){
			param.put("alipay_token", token);
		}
		if(StringUtils.isNotEmpty(notify_id)){
			param.put("alipay_notify_id", notify_id);
		}
		if(StringUtils.isNotEmpty(email)){
			param.put("alipay_email", email);
		}
		weiboDao.updateWeiboProType(param, userid, profile);
	}
}
