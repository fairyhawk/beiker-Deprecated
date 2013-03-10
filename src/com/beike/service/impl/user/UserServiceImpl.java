package com.beike.service.impl.user;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.beike.dao.user.UserDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;
import com.beike.service.common.EmailService;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.user.UserService;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.userloginlog.service.UserLoginLogService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;

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
@Service("userService")
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements
		UserService {
	private final Log log = LogFactory.getLog(UserServiceImpl.class);
	public static final int URLTIMEOUT = 3;
	private static final int HMAC_KEY_LEN = 60;
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserLoginLogService userLoginLogService;

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	@Autowired
	private UserDao userDao;

	public TrxHessianServiceGateWay getTrxHessianServiceGateWay() {
		return trxHessianServiceGateWay;
	}

	public void setTrxHessianServiceGateWay(
			TrxHessianServiceGateWay trxHessianServiceGateWay) {
		this.trxHessianServiceGateWay = trxHessianServiceGateWay;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public GenericDao<User, Long> getDao() {
		return userDao;
	}

	public User findById(Long id) {
		return userDao.findById(id);
	}

	public User findUserByEmail(String email) throws UserException {

		return userDao.findUserByEmail(email);
	}

	public void addUser(String email, String mobile, String password)
			throws UserException {
		try {
			UserForm userForm = new UserForm();
			userForm.setEmail(email);
			userForm.setMobile(mobile);
			// 生成
			generatePassword(userForm, password);
			userDao.addUser(userForm);
			// TODO:创建用户账户

		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	public User addUserEmailRegist(String mobile, String email, String password,String ip)
			throws UserException, AccountException {
		UserForm userForm = new UserForm();
		userForm.setEmail(email);
		userForm.setUserIp(ip);
		// userForm.setMobile(mobile);
		// userForm.setMobile_isavalible(1);
		// userForm.setIsavalible(1);
		// 生成
		generatePassword(userForm, password);
		// 判断用户名是否已存在 add by qiaowb 2012-05-18
		User repeatUser = userDao.findUserByEmail(email);
		if (repeatUser == null) {
			userDao.addUser(userForm);
			Long userId = userDao.getLastInsertId();
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("userId", userId + "");
			sourceMap.put("reqChannel", "WEB");
			log.info("add user email=" + email + ",userId=" + userId);
			Map<String, String> rspMap = trxHessianServiceGateWay
					.createAccount(sourceMap);
			// 创建账户
			log.info("+++++++++++userId:" + userId + "->rspMap:" + rspMap
					+ "+++++++++++++++++++++++++++++++++");
			if ("1".equals(String.valueOf(rspMap.get("rspCode")))) {
				log
						.info("+++++++++++userId:"
								+ userId
								+ "->create act success!+++++++++++++++++++++++++++++++++");
			} else {
				throw new AccountException(rspMap.get("rspCode"));
			}
			return userDao.findById(userId);
		} else {
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}

	}

	public User addUserEmailRegist(String email, String password,String ip)
			throws UserException, AccountException {
		UserForm userForm = new UserForm();
		userForm.setEmail(email);
		userForm.setUserIp(ip);
		// 生成
		generatePassword(userForm, password);
		// 判断用户名是否已存在 add by qiaowb 2012-05-18
		User repeatUser = userDao.findUserByEmail(email);
		if (repeatUser == null) {
			userDao.addUser(userForm);
			Long userId = userDao.getLastInsertId();
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("userId", userId + "");
			sourceMap.put("reqChannel", "WEB");
			log.info("add user email=" + email + ",userId=" + userId);
			Map<String, String> rspMap = trxHessianServiceGateWay
					.createAccount(sourceMap);
			// 创建账户
			log.info("+++++++++++userId:" + userId + "->rspMap:" + rspMap
					+ "+++++++++++++++++++++++++++++++++");
			if ("1".equals(String.valueOf(rspMap.get("rspCode")))) {
				log
						.info("+++++++++++userId:"
								+ userId
								+ "->create act success!+++++++++++++++++++++++++++++++++");
			} else {
				throw new AccountException(rspMap.get("rspCode"));
			}
			return userDao.findById(userId);
		} else {
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	public User addUserEmailRegist(UserForm userForm) throws UserException,
			AccountException {
		String email = userForm.getEmail();
		String password = userForm.getPassword();
		userForm.setEmail(email);
		// 生成
		generatePassword(userForm, password);
		// 判断用户名是否已存在 add by qiaowb 2012-05-18
		User repeatUser = userDao.findUserByEmail(email);
		if (repeatUser == null) {
			userDao.addUser_new(userForm);
			Long userId = userDao.getLastInsertId();
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("userId", userId + "");
			sourceMap.put("reqChannel", "WEB");
			log.info("add user email=" + email + ",userId=" + userId);
			Map<String, String> rspMap = trxHessianServiceGateWay
					.createAccount(sourceMap);
			// 创建账户
			log.info("+++++++++++userId:" + userId + "->rspMap:" + rspMap
					+ "+++++++++++++++++++++++++++++++++");
			if ("1".equals(String.valueOf(rspMap.get("rspCode")))) {
				log
						.info("+++++++++++userId:"
								+ userId
								+ "->create act success!+++++++++++++++++++++++++++++++++");
			} else {
				throw new AccountException(rspMap.get("rspCode"));
			}
			return userDao.findById(userId);
		} else {
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	private void generatePassword(UserForm userForm, String password) {
		String customerKey = StringUtils.getRandomString(HMAC_KEY_LEN);
		password = MobilePurseSecurityUtils.secrect(password, customerKey);
		userForm.setPassword(password);
		userForm.setCustomerKey(customerKey);
	}

	public void addUserMobileRegist(String mobile, String password)
			throws UserException {
		try {
			UserForm userForm = new UserForm();
			userForm.setMobile(mobile);
			// 生成密码
			generatePassword(userForm, password);
			userDao.addUser(userForm);
			// TODO:创建用户账户

			// TODO:加入扩展信息
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}

	}

	public boolean isUserExist(String mobile, String email)
			throws UserException {
		User user = null;
		if (mobile != null && !"".equals(mobile)) {
			user = userDao.findUserByMobile(mobile);
		} else {
			user = userDao.findUserByEmail(email);
		}
		if (user == null)
			return false;

		return true;
	}

	public void addProfile(String profileName, String profileValue,
			Long userid, ProfileType profileType) throws UserException {
		try {
			ProfileForm profileForm = new ProfileForm();
			profileForm.setName(profileName);
			profileForm.setUserid(userid);
			profileForm.setValue(profileValue);
			profileForm.setProFileType(ProfileType.USERCONFIG);
			userDao.addProfile(profileForm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	public void updateProfile(UserProfile userProfile) throws UserException {

		userDao.updateUserProfile(userProfile);
	}

	public UserProfile getUserProfile(Long userid, String profileName)
			throws UserException {
		return userDao.getUserProfile(userid, "", profileName);
	}

	public boolean isUrlUsable(String urlKey, Long userid, String emailType)
			throws UserException {

		UserProfile userProfile = userDao.getUserProfile(userid, urlKey,
				emailType);
		if (userProfile == null)
			return false;

		// 判断key时间是否在三天以内
		Date profileDate = userProfile.getProfiledate();
		Date date = new Date();
		String strProfileDate = DateUtils.dateToStrLong(profileDate);
		String strDate = DateUtils.dateToStrLong(date);
		int days = DateUtils.disDay(strProfileDate, strDate);
		if (profileDate.after(date)) {
			return false;
		}
		if (days >= URLTIMEOUT) {
			return false;
		}

		return true;
	}

	public User isUserLogin(String mobile, String password, String email)
			throws UserException {
		User user = null;
		// 根据用户名查找相关用户
		if (mobile != null && !"".equals(mobile)) {
			user = userDao.findUserByMobile(mobile);
		} else {
			user = userDao.findUserByEmail(email);
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

	public void activationEmail(User user) throws UserException {
		UserForm userForm = new UserForm();
		userForm.setId(user.getId());
		userForm.setCustomerKey(user.getCustomerkey());
		userForm.setEmail(user.getEmail());
		userForm.setEmail_isavalible(1);
		userForm.setMobile(user.getMobile());
		userForm.setMobile_isavalible(user.getMobile_isavalible());
		userForm.setPassword(user.getPassword());
		userForm.setIsavalible(0);
		userDao.updateUser(userForm);
	}

	public void activationMobile(User user, String validateMobile)
			throws UserException {
		UserForm userForm = new UserForm();
		userForm.setId(user.getId());
		userForm.setCustomerKey(user.getCustomerkey());
		userForm.setEmail(user.getEmail());
		userForm.setEmail_isavalible(user.getEmail_isavalible());
		userForm.setMobile(user.getMobile());
		userForm.setMobile_isavalible(1);
		userForm.setPassword(user.getPassword());
		userForm.setIsavalible(1);
		userDao.updateUser(userForm);
		// 设置动态参数
		// TODO:邮件内容确定
		if (validateMobile == null) {
			Object[] emailParams = new Object[] { user.getEmail() };
			// 邮件模板参数未设置
			try {
				emailService.send(null, null, null, null, null, "注册成功",
						new String[] { user.getEmail() }, null, null,
						new Date(), emailParams, "REGIST_SUCCESS");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UserProfile getProfile(Long userid, String profileName)
			throws UserException {

		return userDao.getUserProfile(userid, "", profileName);
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void updateUserMessage(User user) throws UserException {
		UserForm userForm = new UserForm();
		userForm.setCustomerKey(user.getCustomerkey());
		userForm.setEmail(user.getEmail());
		userForm.setEmail_isavalible(user.getEmail_isavalible());
		userForm.setId(user.getId());
		userForm.setMobile(user.getMobile());
		userForm.setMobile_isavalible(user.getMobile_isavalible());
		userForm.setIsavalible(user.getIsavalible());
		userForm.setPassword(user.getPassword());
		userDao.updateUser(userForm);
	}

	public User findUserByMobile(String mobile) throws UserException {
		return userDao.findUserByMobile(mobile);

	}

	public User getUserByUserKey(String userKey) {
		return null;

	}

	public Long getUnusedTrxorder(Long userId) {
		return userDao.getUnusedTrxorder(userId);

	}

	public Long readyLoseTrxorderCount(Long userId) {

		List<Map<String, Object>> listDate = userDao.readyLoseTrxorder(userId);
		Long count = 0L;
		if (listDate != null && listDate.size() > 0) {
			for (Map<String, Object> map : listDate) {
				// Integer order_lose_abs_date = (Integer) map
				// .get("order_lose_abs_date");
				Timestamp order_lose_date = (Timestamp) map
						.get("order_lose_date");
				// Timestamp create_date = (Timestamp) map.get("create_date");

				// String
				// olad=DateUtils.dateToStr(order_lose_abs_date,"yyyy-MM-dd
				// HH:mm:ss");
				// Date reallyLoseDate = DateUtils.compareDate(create_date,
				// order_lose_abs_date + "", order_lose_date);
				Date nowDate = new Date();

				long day = 0;
				try {
					day = DateUtils.getDistinceDay(
							DateUtils.dateToStr(nowDate), DateUtils
									.dateToStr(order_lose_date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (day >= 0 && day < 7) {
					count++;
				}
			}
		}
		return count;

	}

	public Long unComment(Long userId) {
		return userDao.unComment(userId);
	}

	public double userbalance(Long userId) {
		double balance = 0.0;
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("userId", userId + "");
		sourceMap.put("reqChannel", "WEB");
		Map<String, String> map = trxHessianServiceGateWay
				.getActByUserId(sourceMap);
		if (map != null && map.size() > 0) {
			String RSPCODE = map.get("rspCode");
			if ("1".equals(RSPCODE)) {
				String status = map.get("balance");
				balance = Double.parseDouble(status);
			}
		}

		return balance;
	}

	/**
	 * 添加优惠券
	 */
	@Override
	public boolean noTscAddCouponsForUser(Long userId, Long vmAccountId,
			Long amount, String description) {
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("reqChannel", "WEB"); // 请求渠道
		sourceMap.put("vmAccountId", String.valueOf(vmAccountId)); // 虚拟款ID
		sourceMap.put("amount", String.valueOf(amount)); // 追加金额
		sourceMap.put("operatorId", "0"); // 操作人ID
		sourceMap.put("requestId", "Dvm" + StringUtils.getSysTimeRandom()); // 下发请求号
		sourceMap.put("userId", String.valueOf(userId)); // 接收用户主键Id
		sourceMap.put("description", StringUtils.toTrim(description)); // 描述
		sourceMap.put("bizType", "COUPON");
		Map<String, String> returnMap = trxHessianServiceGateWay
				.dispatchVm(sourceMap);
		if (null != returnMap && returnMap.size() > 0) {
			String rspCode = returnMap.get("rspCode");
			if ("1".equals(rspCode)) {
				return true;
			}
		}
		return false;
	}

	public UserLoginLogService getUserLoginLogService() {
		return userLoginLogService;
	}

	public void setUserLoginLogService(UserLoginLogService userLoginLogService) {
		this.userLoginLogService = userLoginLogService;
	}
	
	@Override
	public boolean addLoginLog(UserLoginLog ulLog) throws Exception {
		// TODO Auto-generated method stub
		return userLoginLogService.addLoginLog(ulLog);
	}
	
	@Override
	public void updateUserPassWord(Long userId,String password) {
		
		userDao.updateUserPassWord(userId,password);
		
	}

	
	@Override
	public User addMobileRegister(String mobile, String email, String password,String ip)throws UserException, AccountException {
		UserForm userForm = new UserForm();
		userForm.setEmail(email);
		userForm.setUserIp(ip);
		userForm.setMobile(mobile);
		userForm.setMobile_isavalible(1);
		userForm.setIsavalible(1);
		// 生成
		generatePassword(userForm, password);
		// 判断用户名是否已存在 add by qiaowb 2012-05-18
		User repeatUser = userDao.findUserByEmail(email);
		if (repeatUser == null) {
			userDao.addUser(userForm);
			Long userId = userDao.getLastInsertId();
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("userId", userId + "");
			sourceMap.put("reqChannel", "WEB");
			log.info("addMobileRegister...add user email=" + email + ",userId=" + userId);
			Map<String, String> rspMap = trxHessianServiceGateWay.createAccount(sourceMap);
			// 创建账户
			log.info("+++++++++++userId:" + userId + "->rspMap:" + rspMap+ "+++++++++++++++++++++++++++++++++");
			if ("1".equals(String.valueOf(rspMap.get("rspCode")))) {
				log.info("addMobileRegister+++++++++++userId:"+ userId+ "->create act success!+++++++++++++++++++++++++++++++++");
			} else {
				throw new AccountException(rspMap.get("rspCode"));
			}
			return userDao.findById(userId);
		} else {
			throw new UserException(UserException.USER_SYSTEM_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getUserInfoByUserIds(List<Long> idlist) {
		
		List rs = userDao.getUserInfoByUserIds(idlist);
		
		return rs;
	}

}
