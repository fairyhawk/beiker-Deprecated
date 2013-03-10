package com.beike.service.impl.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.GenericDao;
import com.beike.dao.user.UserAddressDao;
import com.beike.dao.user.UserExpandDao;
import com.beike.entity.user.UserAddress;
import com.beike.entity.user.UserExpand;
import com.beike.form.AccessToken;
import com.beike.form.BaiduAccessToken;
import com.beike.form.TencentqqAccessToken;
import com.beike.form.XiaoNeiAccessToken;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.user.UserExpandService;
import com.beike.util.StaticDomain;
import com.beike.util.alipay.AlipayModel;
import com.beike.util.hao3604j.Tuan360Model;
import com.beike.util.sina.RequestToken;
import com.beike.util.sina.SinaAccessToken;
import com.beike.util.tencent.ResModel;

/**      
 * project:beiker  
 * Title:用户扩展信息Service
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 10:50:36 AM     
 * @version 1.0
 */
@Service("userExpandService")
public class UserExpandServiceImpl extends GenericServiceImpl<UserExpand, Long> implements
		UserExpandService {
	
	@Autowired
	private UserExpandDao userExpandDao;
	
	@Autowired
	private UserAddressDao userAddressDao;
	
	/* (non-Javadoc)
	 * @see com.beike.service.user.UserExpandService#addUserExpand(com.beike.entity.user.UserExpand)
	 */
	@Override
	public Long addUserExpand(UserExpand userExpand) {
		return userExpandDao.addUserExpand(userExpand);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserExpandService#getUserExpandById(java.lang.Long)
	 */
	@Override
	public UserExpand getUserExpandByUserId(Long userId) {
		UserExpand userExpand = userExpandDao.getUserExpandByUserId(userId);
		if(userExpand!=null){
			if(StringUtils.isNotEmpty(userExpand.getAvatar())){
				if(!userExpand.getAvatar().startsWith("http://")){
					userExpand.setAvatar(StaticDomain.getDomain("") + "/" + userExpand.getAvatar());
				}
			}else{
				userExpand.setAvatar("");
			}
		}

		return userExpand;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserExpandService#updateUserAvatar(com.beike.entity.user.UserExpand)
	 */
	@Override
	public int updateUserAvatar(UserExpand userExpand) {
		int ret = userExpandDao.updateUserExpand(userExpand);
		if(ret<=0){
			userExpandDao.addUserExpand(userExpand);
			ret = 1;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.user.UserExpandService#updateUserExpand(com.beike.entity.user.UserExpand)
	 */
	@Override
	public int updateUserExpand(UserExpand userExpand) {
		userExpand.setAvatar("");
		int ret = userExpandDao.updateUserExpand(userExpand);
		if(ret<=0){
			userExpandDao.addUserExpand(userExpand);
			ret = 1;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.GenericService#findById(java.io.Serializable)
	 */
	@Override
	public UserExpand findById(Long id) {
		return null;
	}

	@Override
	public GenericDao<UserExpand, Long> getDao() {
		return userExpandDao;
	}

	@Override
	public int updateUserExpand(UserExpand userExpand, UserAddress userAddress) {
		updateUserExpand(userExpand);
		if(userAddress!=null){
			int ret = userAddressDao.updateUserAddress(userAddress);
			if(ret <= 0){
				userAddressDao.addUserAddress(userAddress);
			}
		}
		return 0;
	}

	@Override
	public int addUserExpand(Long userid, AccessToken accessToken,
			ProfileType profile) {
		try{
			//同步数据
			UserExpand userExpand = new UserExpand();
			userExpand.setUserId(userid);
			userExpand.setGender(0);
			String headIconUrl = null;
			if(accessToken instanceof BaiduAccessToken){
				BaiduAccessToken baiduToken = (BaiduAccessToken)accessToken;
				userExpand.setNickName(baiduToken.getScreenName());
				headIconUrl = baiduToken.getHeadIcon();
			}else if(accessToken instanceof RequestToken){
				RequestToken sinaToken = (RequestToken)accessToken;
				userExpand.setNickName(sinaToken.getScreenName());
				headIconUrl = sinaToken.getHeadIcon();
			}else if(accessToken instanceof XiaoNeiAccessToken){
				XiaoNeiAccessToken xiaoneiToken = (XiaoNeiAccessToken)accessToken;
				userExpand.setNickName(xiaoneiToken.getScreenName());
				headIconUrl = xiaoneiToken.getHeadIcon();
			}else if(accessToken instanceof TencentqqAccessToken){
				TencentqqAccessToken qqToken = (TencentqqAccessToken)accessToken;
				userExpand.setNickName(qqToken.getScreenName());
				headIconUrl = qqToken.getHeadIcon();
			}else if(accessToken instanceof ResModel){
				ResModel tencentweiboToken = (ResModel)accessToken;
				userExpand.setNickName(tencentweiboToken.getWeiboname());
				headIconUrl = tencentweiboToken.getHeadIcon();
			}else if(accessToken instanceof Tuan360Model){
				Tuan360Model tuan360Model=(Tuan360Model) accessToken;
				userExpand.setNickName(tuan360Model.getQname());
				headIconUrl = "";
			}else if(accessToken instanceof AlipayModel){
				AlipayModel alipayModel = (AlipayModel)accessToken;
				userExpand.setNickName(alipayModel.getReal_name());
				headIconUrl = "";
			}
			if(StringUtils.isNotEmpty(headIconUrl)){
				if(profile.equals(ProfileType.BAIDUCONFIG)){
					headIconUrl = "http://himg.bdimg.com/sys/portrait/item/" + headIconUrl;
				}else if(profile.equals(ProfileType.TENCENTCONFIG)){
					headIconUrl = headIconUrl + "/100";
				}
			}

			userExpand.setAvatar(headIconUrl);
			
			if(StringUtils.isNotEmpty(userExpand.getNickName())
					|| StringUtils.isNotEmpty(userExpand.getAvatar())){
				//插入用户扩展信息
				if(userExpandDao.getUserExpandByUserId(userid) == null){
					//插入数据
					userExpandDao.addUserExpand(userExpand);
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return 0;
	}
}
