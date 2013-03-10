package com.beike.wap.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.UserException;
import com.beike.dao.GenericDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MUserDao;
import com.beike.wap.service.MUserService;



/**
 * <p>
 * Title:用户服务相关service
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
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("mUserService")
public class MUserServiceImpl extends GenericServiceImpl<User, Long> implements MUserService {
	private final Log log = LogFactory.getLog(MUserServiceImpl.class);
	
	public static final int URLTIMEOUT = 3;
	private static final int HMAC_KEY_LEN = 60;
	
	@Autowired
	private MUserDao mUserDao;

//	@Autowired
//	private EmailService emailService;

	@Resource(name = "wapClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;	
	@Override
	public User findById(Long id) {
		return mUserDao.findById(id);
	}

	@Override
	public void addUser(String email, String mobile, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User addUserEmailRegist(String email, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserExist(String mobile, String email) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addProfile(String profileName, String profileValue,
			Long userid, ProfileType profileType) throws UserException {
		try {
			ProfileForm profileForm = new ProfileForm();
			profileForm.setName(profileName);
			profileForm.setUserid(userid);
			profileForm.setValue(profileValue);
			profileForm.setProFileType(profileType);
			mUserDao.addProfile(profileForm);
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	@Override
	public UserProfile getProfile(Long userid, String profileName) {
		
		return mUserDao.getUserProfile(userid, profileName);
	}

	public void updateProfile(UserProfile userProfile) {
		// TODO Auto-generated method stub
		mUserDao.updateUserProfile(userProfile);
	}

	@Override
	public boolean isUrlUsable(String urlKey, Long userid, String emailType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User isUserLogin(String mobile, String password, String email) throws UserException {
		User user = null;
		// 根据用户名查找相关用户
		if (mobile != null && !"".equals(mobile)) {
			user = mUserDao.findUserByMobile(mobile);
		} else {
			user = mUserDao.findUserByEmail(email);
		}
		if (user == null) {
			throw new UserException(UserException.PASSWORD_ERROR);
		}

		String customerkey = user.getCustomerkey();
		String secretPassword = MobilePurseSecurityUtils.secrect(password,
				customerkey);
		String databasePassword = user.getPassword();
		// 判断密码是否一致
		if (!secretPassword.equals(databasePassword)) {
			throw new UserException(UserException.PASSWORD_ERROR);
		}
		return user;
	}

	@Override
	public User findUserByEmail(String email) {
		// TODO Auto-generated method stub
		return mUserDao.findUserByEmail(email);
	}

	@Override
	public void activationEmail(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activationMobile(User user, String validateMobile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserProfile getUserProfile(Long userid, String profileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserMessage(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUserByUserKey(String userKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getUnusedTrxorder(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long readyLoseTrxorderCount(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long unComment(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double userbalance(Long userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GenericDao<User, Long> getDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserByMobile(String mobile) {
		return mUserDao.findUserByMobile(mobile);
	}
	
	private void generatePassword(UserForm userForm, String password) {
		String customerKey = StringUtils.getRandomString(HMAC_KEY_LEN);
		password = MobilePurseSecurityUtils.secrect(password, customerKey);
		userForm.setPassword(password);
		userForm.setCustomerKey(customerKey);
	}

	public User addMobileRegist(String mobile, String email, String password, String cusKey) throws AccountException {
		UserForm userForm = new UserForm();
		userForm.setMobile(mobile);
		userForm.setEmail(email);
		userForm.setPassword(password);
		userForm.setCustomerKey(cusKey);
		userForm.setMobile_isavalible(1);
		userForm.setEmail_isavalible(0);
		userForm.setIsavalible(0);
		mUserDao.addUser(userForm);
		Long userId = mUserDao.getLastInsertId();
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("userId", userId + "");
		sourceMap.put("reqChannel","WAP");
		log.info("add user email=" + mobile + ",userId=" + userId);
		Map<String, String> rspMap = trxHessianServiceGateWay.createAccount(sourceMap);
		
		// 创建账户
		log.info("+++++++++++userId:" + userId + "->rspMap:"+ rspMap+ "+++++++++++++++++++++++++++++++++");
		if ("1".equals(String.valueOf(rspMap.get("rspCode")))) {
			log.info("+++++++++++userId:" + userId + "->create act success!+++++++++++++++++++++++++++++++++");
		} else {
			throw new AccountException(rspMap.get("rspCode"));
		}
		
		return mUserDao.findUserByMobile(mobile);
	}
}
