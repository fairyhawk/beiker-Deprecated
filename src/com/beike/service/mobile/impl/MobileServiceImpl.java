package com.beike.service.mobile.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.UserException;
import com.beike.common.mobile.exception.MobileUserException;
import com.beike.dao.WeiboDao;
import com.beike.dao.background.area.AreaDao;
import com.beike.dao.impl.catlog.PropertyCatlogDaoImpl;
import com.beike.dao.impl.catlog.RegionCatlogDaoImpl;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.dao.merchant.ShopsBaoDao;
import com.beike.dao.mobile.LbsMerchantGoodsDao;
import com.beike.dao.mobile.LuceneMobileAssistDao;
import com.beike.dao.user.UserDao;
import com.beike.entity.common.Sms;
import com.beike.entity.merchant.BranchProfile;
import com.beike.entity.mobile.LbsGoodsInfo;
import com.beike.entity.mobile.LbsMerchantGoodsInfo;
import com.beike.entity.mobile.LbsMerchantInfo;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.MerchantForm;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.service.mobile.AppStatService;
import com.beike.service.mobile.LuceneSearchMobileService;
import com.beike.service.mobile.MobileService;
import com.beike.service.mobile.SearchParam;
import com.beike.service.mobile.SearchParamV2;
import com.beike.service.user.UserService;
import com.beike.util.Constant;
import com.beike.util.CryptUtilImpl;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StaticDomain;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.mobile.Map2Object;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Service("mobileHessianService")
public class MobileServiceImpl implements MobileService {

	@Autowired
	private AppStatService appStatService;

	@Autowired
	private LuceneSearchMobileService luceneSearchMobileService;
	@Autowired
	private BranchProfileDao branchProfileDao;

	@Autowired
	private ShopsBaoDao shopDao;
	@Autowired
	private LuceneMobileAssistDao luceneMobileAssistDao;

	private static Log log = LogFactory.getLog(MobileServiceImpl.class);

	@Autowired
	private UserService userService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AreaDao areaDao;

	@Autowired
	@Qualifier("regionCatlogDao")
	private RegionCatlogDaoImpl regionCatlogDao;

	@Autowired
	@Qualifier("propertyCatlogDao")
	private PropertyCatlogDaoImpl propertyCatlogDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private LbsMerchantGoodsDao lbsMerchantGoodsDao;

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	private static CryptUtilImpl cryptUtil = new CryptUtilImpl();

	@Autowired
	private WeiboDao weiboDao;

	private final PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);

	// 3DES 密钥
	private final String CryptKey = propertyUtil.getProperty(Constant.CRYPT_KEY);

	// 邮箱正则验证
	private final String EMAIL_REGX = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";

	// 密码正则验证
	private final String PASSWORD_REGX = "^[A-Za-z0-9_-]{6,20}$";

	// 手机号码正则
	private final String TELPHONE_REGX = "^1[0-9]{10}$";

	private static final int SMS_RANDOM = 6;

	private static final String SMS_TYPE = "15";

	@Override
	public Map<String, Object> getListBranchByID(Map<String, Object> params, Double lat, Double lng) {
		// 返回的结果
		Map<String, Object> return_map = new HashMap<String, Object>();
		try {
			List<Long> querybranchid = (List<Long>) params.get("querybranchid");
			if (!checkList(querybranchid)) {
				return_map.put("code", 0);
			} else {
				Map<Long, List<String>> branch_goods_map = new HashMap<Long, List<String>>();
				Map<String, Object> searchedResult = luceneSearchMobileService.getListBranch(querybranchid, lat, lng);
				List<Map<String, Object>> branch_goods_list = luceneMobileAssistDao.getBranchGoods(querybranchid);
				for (int i = 0; i < branch_goods_list.size(); i++) {
					Map<String, Object> branch_goods = branch_goods_list.get(i);
					String branchid = branch_goods.get("branchid") != null ? branch_goods.get("branchid").toString() : "";
					if (branch_goods_map.get(new Long(branchid)) != null) {
						branch_goods_map.get(new Long(branchid)).add(branch_goods.get("goodsid").toString());
					} else {
						List<String> goodsids = new ArrayList<String>();
						goodsids.add(branch_goods.get("goodsid").toString());
						branch_goods_map.put(new Long(branchid), goodsids);
					}
				}
				List<Map<String, Object>> searchedBranch = (List<Map<String, Object>>) searchedResult.get("searchedbranch");
				for (int i = 0; i < searchedBranch.size(); i++) {
					Map<String, Object> branchinfo = searchedBranch.get(i);
					String searchedbranchid = (String) branchinfo.get("storeid");
					if (branch_goods_map.get(new Long(searchedbranchid)) == null) {
						branchinfo.put("sellgoods", null);
					} else {
						branchinfo.put("sellgoods", branch_goods_map.get(new Long(searchedbranchid)));
					}
				}
				// logger.info(searchedBranch);
				return_map.put("code", 1);
				return_map.put("rs", searchedBranch);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("app店铺列表搜索异常");
			return_map.put("rs", 0);
			return_map.put("code", 0);
		}
		return return_map;

	}

	public static void main(String[] args) {
		String email = "                     ";
		System.out.println(StringUtils.isBlank(email));
	}

	Map<String, String> getBranchofBrandTotal(List<Long> brandids) {
		List<Map<String, Object>> branchofbrand_map_list = luceneMobileAssistDao.getBranchOfBrand(brandids);
		Map<String, String> branchofbrand_cache = new HashMap<String, String>();
		for (int total_index = 0; total_index < branchofbrand_map_list.size(); total_index++) {
			Map<String, Object> branchofbrand_map = branchofbrand_map_list.get(total_index);
			branchofbrand_cache.put(branchofbrand_map.get("merchantid").toString(), branchofbrand_map.get("total").toString());
		}

		return branchofbrand_cache;
	}

	@Override
	public Map<String, Object> getListGoodsByIDs(Map<String, Object> params) {

		List<Long> querygoodsid = (List<Long>) params.get("querygoodsids");
		// 返回的结果
		Map<String, Object> return_map = new HashMap<String, Object>();
		if (!checkList(querygoodsid)) {
			return_map.put("rs", 0);
			return_map.put("code", 0);
			return return_map;
		}

		// 商品,品牌列表详情返回的结果集
		List<Map> goods_brand_map_list = new ArrayList<Map>();
		List<Map> goods_detail = luceneMobileAssistDao.getGoodsList(querygoodsid);
		// 品牌分店总数
		List<Map<String, Object>> brandid_map_list = luceneMobileAssistDao.getBrandofGoods(querygoodsid);
		List<Long> brandids = new ArrayList<Long>();
		for (int brand_index = 0; brand_index < brandid_map_list.size(); brand_index++) {
			brandids.add(new Long(brandid_map_list.get(brand_index).get("merchantid").toString()));
		}
		Map<String, String> branchofbrand_cache = getBranchofBrandTotal(brandids);
		// 分店一级商圈
		List<Map> branche_goods_fir_list = luceneMobileAssistDao.getGoodsBranch(true, querygoodsid);
		Map<Long, List<Long>> branch_fir_map = convertGoodsBranch(true, branche_goods_fir_list);

		// 分店二级商圈
		List<Map> branche_goods_sec_list = luceneMobileAssistDao.getGoodsBranch(false, querygoodsid);
		Map<Long, List<Long>> branch_sec_map = convertGoodsBranch(false, branche_goods_sec_list);

		// 品牌一级分类
		List<Map> goods_brand_list_fir = luceneMobileAssistDao.getGoodsBrand(true, querygoodsid);
		Map<String, Object> goods_brand_fir_map = new HashMap<String, Object>();
		Map<Long, Object> brand_phone = new HashMap<Long, Object>();
		for (int i = 0; i < goods_brand_list_fir.size(); i++) {
			String goodsid = goods_brand_list_fir.get(i).get("goodsid").toString();
			brand_phone.put(new Long(goodsid), goods_brand_list_fir.get(i).get("tel"));
			goods_brand_fir_map.put(goodsid, (Long) goods_brand_list_fir.get(i).get("brandcatfir"));
			// 品牌名称
			goods_brand_fir_map.put(goodsid + "_brand_name", goods_brand_list_fir.get(i).get("brandname"));
		}

		// 品牌二级分类
		List<Map> goods_brand_list_sec = luceneMobileAssistDao.getGoodsBrand(false, querygoodsid);
		Map<Long, List<Long>> goods_brand_sec_map = new HashMap<Long, List<Long>>();
		for (int i = 0; i < goods_brand_list_sec.size(); i++) {
			if (goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")) != null) {
				goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")).add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
			} else {
				List<Long> brand_sec_list = new ArrayList<Long>();
				brand_sec_list.add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
				goods_brand_sec_map.put((Long) goods_brand_list_sec.get(i).get("goodsid"), brand_sec_list);
			}
		}
		// 遍历商品分店
		Map<Long, List<Long>> branch_goods_List_result = convertBranchMap(branche_goods_fir_list);

		// 遍历商品详情将其他信息组装成最后的返回结果
		Map<Long, Map> sorted_goods_detail_map = sortMap(goods_detail, "goodsid");
		List<Map<String, Object>> goods_sales_list = luceneMobileAssistDao.getGoodsSale(querygoodsid);
		Map<Long, Object> sales_count_map = new HashMap<Long, Object>();
		for (int i = 0; i < goods_sales_list.size(); i++) {
			Map<String, Object> goods_sale = goods_sales_list.get(i);
			sales_count_map.put(new Long(goods_sale.get("goodsid").toString()), goods_sale.get("sale") != null ? goods_sale.get("sale").toString() : "0");
		}
		List<Map<String, Object>> goods_price_list = luceneMobileAssistDao.getGoodsCurrentPrice(querygoodsid);
		Map<Long, Object> goods_price_map = new HashMap<Long, Object>();
		for (int i = 0; i < goods_price_list.size(); i++) {
			Map<String, Object> goods_price = goods_price_list.get(i);
			goods_price_map.put(new Long(goods_price.get("goodsid").toString()), goods_price.get("price"));
		}
		for (int n = 0; n < querygoodsid.size(); n++) {

			// Long goodsid = (Long) goods_detai_map.get("goodsid");
			Long goodsid = querygoodsid.get(n);
			String branchtotal = branchofbrand_cache.get(goodsid.toString()) == null ? "0" : branchofbrand_cache.get(querygoodsid.get(n).toString());

			Map goods_detail_map = sorted_goods_detail_map.get(goodsid);
			if (goods_detail_map == null) {
				continue;
			}
			goods_detail_map.put("totalbranch", branchtotal);
			// bg.currentPrice currentprice,salescount从索引获取
			goods_detail_map.put("salescount", sales_count_map.get(goodsid) != null ? sales_count_map.get(goodsid).toString() : "");
			goods_detail_map.put("currentprice", goods_price_map.get(goodsid));
			// 分店信息
			goods_detail_map.put("storelist", branch_goods_List_result.get(goodsid));

			String brandtel = brand_phone.get(goodsid) != null ? brand_phone.get(goodsid).toString() : "";
			goods_detail_map.put("brandphone", brandtel);
			// 品牌一级分类
			goods_detail_map.put("brandcatfir", goods_brand_fir_map.get(goodsid.toString()));
			// 品牌名称
			goods_detail_map.put("brandname", goods_brand_fir_map.get(goodsid + "_brand_name"));
			// 品牌二级分类
			try {
				goods_detail_map.put("brandcatsec",
						com.beike.util.StringUtils.arrayToString(goods_brand_sec_map.get(new Long(goodsid.toString())).toArray(), ","));
			} catch (Exception e) {
				logger.info("goodsid=========================== !! = " + goodsid);
			}
			goods_detail_map.remove("listimgurl");
			goods_brand_map_list.add(goods_detail_map);
		}

		return_map.put("rs", goods_brand_map_list);
		return_map.put("code", 1);
		//logger.info(return_map);
		return return_map;
	}

	private boolean checkList(List<Long> list) {
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> addNewUser(Map<String, Object> params) {

		Map<String, Object> resuMap = new HashMap<String, Object>();

		String uuid = UUID.randomUUID().toString();

		String email = (String) params.get("email");
		String pswd = (String) params.get("password");

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pswd)) {
			log.info("param is null.....email...:" + email + "....AND...pswd:" + pswd + ".....Is....Null");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		log.info("Register UserName Is Not Rule Start.........UserName:" + email + ".......Password:" + pswd + "......");

		email = cryptUtil.decryptDes(email, CryptKey);
		pswd = cryptUtil.decryptDes(pswd, CryptKey);

		// 验证邮箱格式
		if (!email.matches(EMAIL_REGX)) {
			log.info("Regist....email....not.....Compliance with the rules.....");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		// 验证密码格式
		if (!pswd.matches(PASSWORD_REGX)) {
			log.info("Regist....pswd....not.....Compliance with the rules.....");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		// 判断用户是否存在
		boolean flag = false;
		try {
			flag = userService.isUserExist(null, email);
		} catch (UserException e1) {
			log.info("query user Information Is Faild.......");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		// 注册邮箱已经存在
		if (flag) {
			log.info("Regist.....EMAIL......IS.....EXIT.......");
			resuMap.put("code", MobileUserException.USER_EXIST);
			return resuMap;
		}

		User user = null;

		// 建立用户、创建账户
		try {
			user = userService.addUserEmailRegist(email, pswd, "");
		} catch (UserException e) {
			log.info("UserException.......Is.....Faild By....Add....UserInfo....");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		} catch (AccountException e) {
			log.info("AccountException......Add Account Is Faild");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		// 加入扩展信息
		String emailValidateUrl = propertyUtil.getProperty(Constant.EMAIL_VALIDATE_URL);
		StringBuilder sb = new StringBuilder();
		sb.append(emailValidateUrl);
		sb.append("?id=" + user.getId() + "&userkey=");
		String secret = MobilePurseSecurityUtils.hmacSign(user.getCustomerkey(), user.getId() + "");
		sb.append(secret);
		String subject = "千品网邮箱认证邮件"; // 确认?
		UserProfile userProfile;
		try {
			userProfile = userService.getProfile(user.getId(), Constant.EMAIL_REGIST_URLKEY);
		} catch (UserException e) {
			log.info("query UserProfile Is Faild........");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		// 添加用户扩展信息
		try {
			if (userProfile == null) {
				userService.addProfile(Constant.EMAIL_REGIST_URLKEY, secret, user.getId(), ProfileType.USERCONFIG);
			} else {
				userProfile.setValue(secret);
				userService.updateProfile(userProfile);
			}
		} catch (Exception ex) {
			log.info("Add UserProfile Is Faild.......");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
		String date = DateUtils.dateToStr(new Date());

		// 设置动态参数
		Object[] emailParams = new Object[] { time, sb.toString(), date };
		// 邮件模板参数未设置

		// 发送邮件
		try {
			emailService.send(null, null, null, null, null, subject, new String[] { email }, null, null, new Date(), emailParams,
					Constant.EMAIL_VALIDATE_TEMPLATE);
		} catch (Exception e) {
			log.info("Send Regist Email Is Faild........");
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		if (user != null) {
			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
		}

		log.info("User Regist Is Success......");

		resuMap.put("code", "1");
		resuMap.put("uuid", uuid);

		return resuMap;
	}

	@Override
	public Map<String, Object> updateRegisterMailPhone(Map<String, Object> params) {

		Map<String, Object> resuMap = new HashMap<String, Object>();

		String email = (String) params.get("email");
		String tel = (String) params.get("tel");

		if (StringUtils.isEmpty(email) && StringUtils.isEmpty(tel)) {
			log.info("email....AND....Telphone....Both....NULL");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		if (StringUtils.isNotEmpty(email)) {
			email = cryptUtil.decryptDes(email, CryptKey);
		}

		if (StringUtils.isNotEmpty(tel)) {
			tel = cryptUtil.decryptDes(tel, CryptKey);
		}

		if (StringUtils.isEmpty(email) && StringUtils.isEmpty(tel)) {
			log.info("Descrypt Email....AND Descrypt Tel.....Both......NULL");
			resuMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return resuMap;
		}

		if (StringUtils.isNotEmpty(email)) {
			if (!email.matches(EMAIL_REGX)) {
				log.info("Valiad Email....Not......Compliance with the rules.....");
				resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return resuMap;
			}
		}

		if (StringUtils.isNotEmpty(tel)) {
			if (!tel.matches(TELPHONE_REGX)) {
				log.info("Valiad tel....Not.......Compliance with the rules.....");
				resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return resuMap;
			}
		}

		boolean flag = false;
		// 手机为空、证明从注册过来的,否则从绑定手机过来的
		try {
			if (StringUtils.isEmpty(tel)) {
				flag = userService.isUserExist(null, email);
			} else {
				flag = userService.isUserExist(tel, null);
			}
		} catch (Exception e) {
			resuMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return resuMap;
		}

		if (flag) {
			resuMap.put("code", "1");
		} else {
			resuMap.put("code", MobileUserException.USER_EMAIL_OR_TEL_NOTEXIT);
		}

		log.info("updateRegisterMailPhone..........Is.................Success");

		return resuMap;
	}

	@Override
	public Map<String, Object> loginWithQP(Map<String, Object> params) {

		Map<String, Object> resuMap = new HashMap<String, Object>();

		String uuid = UUID.randomUUID().toString();

		String email = (String) params.get("email");
		String pswd = (String) params.get("password");
		String tel = (String) params.get("tel");

		if ((StringUtils.isEmpty(email) && StringUtils.isEmpty(tel)) || StringUtils.isEmpty(pswd)) {
			log.info("Login.....email...OR....pswd....IS.....NULL");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		String username = "";
		if (StringUtils.isEmpty(email)) {
			username = tel;
		} else if (StringUtils.isEmpty(tel)) {
			username = email;
		}

		username = cryptUtil.decryptDes(username, CryptKey);
		pswd = cryptUtil.decryptDes(pswd, CryptKey);

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(pswd)) {
			log.info("Login.....decryptEmail...OR....decryptPswd....IS.....NULL");
			resuMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return resuMap;
		}

		// 用户名格式即不符合邮箱规则，也不符合手机规则，则为非法用户名
		if (!username.matches(EMAIL_REGX)) {
			if (!username.matches(TELPHONE_REGX)) {
				log.info("Valiad Email....Not......Compliance with the rules.....");
				resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return resuMap;
			}
		}

		if (!pswd.matches(PASSWORD_REGX)) {
			log.info("Valiad pswd....Not......Compliance with the rules.....");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		String userEmail = "";
		String userTelph = "";

		// 判断登录名是邮箱还是手机号
		boolean f = MobilePurseSecurityUtils.isJointMobileNumber(username);

		if (f) {
			userTelph = username;
		} else {
			userEmail = username;
		}

		User user = null;
		try {
			user = userService.isUserLogin(userTelph, pswd, userEmail);
		} catch (UserException e) {
			log.info("Login...PassWord....Not Both Equal.......");// 密码不一致
			resuMap.put("code", MobileUserException.USER_LOGIN_PASSWORD_DIFFENT);
			return resuMap;
		}

		if (user != null) {
			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user.getId());
			if (weiboNames != null) {
				memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
			}
		}

		String rEmail = user.getEmail();
		if (StringUtils.isNotEmpty(rEmail)) {
			rEmail = cryptUtil.cryptDes(rEmail, CryptKey);
		}

		String rMobile = user.getMobile();
		if (StringUtils.isNotEmpty(rMobile)) {
			rMobile = cryptUtil.cryptDes(rMobile, CryptKey);
		}

		resuMap.put("code", "1");
		resuMap.put("uuid", uuid);
		resuMap.put("email", rEmail);
		resuMap.put("tel", rMobile);

		log.info("loginWithQP.............Is.............Success");

		return resuMap;
	}

	@Override
	public Map<String, Object> addQPAndOpenID(Map<String, Object> params) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			String openid = (String) params.get("openid");
			String name = (String) params.get("name");
			String pr = (String) params.get("pr");
			String email = (String) params.get("email");
			String password = (String) params.get("password");
			String tp = (String) params.get("tp");

			// 以下参数不能为空，否则参数错误
			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(pr) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)
					|| StringUtils.isEmpty(tp)) {
				retMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return retMap;
			}

			// 解密参数
			openid = cryptUtil.decryptDes(openid, CryptKey);
			name = cryptUtil.decryptDes(name, CryptKey);
			email = cryptUtil.decryptDes(email, CryptKey);
			password = cryptUtil.decryptDes(password, CryptKey);

			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
				retMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
				return retMap;
			}

			// 判断登录名是邮箱还是手机号
			boolean ismobile = MobilePurseSecurityUtils.isJointMobileNumber(email);

			ProfileType trdType = null;
			// 第三方来源
			if ("sina".equalsIgnoreCase(pr)) {
				trdType = ProfileType.SINACONFIG;
			} else if ("renren".equalsIgnoreCase(pr)) {
				trdType = ProfileType.XIAONEICONFIG;
			} else if ("tencent".equalsIgnoreCase(pr)) {
				trdType = ProfileType.TENCENTCONFIG;
			} else if ("baidu".equalsIgnoreCase(pr)) {
				trdType = ProfileType.BAIDUCONFIG;
			} else if ("qq".equalsIgnoreCase(pr)) {
				trdType = ProfileType.QQCONFIG;
			}
			if (trdType == null) {
				retMap.put("code", MobileUserException.USER_INVALID_THIRDPART);
				return retMap;
			} else {
				Map<String, String> paramMap = paseParamMap(openid, name, trdType);
				// 本次登录用户
				Long userid = null;
				// 绑定账号
				if ("0".equals(tp)) {
					User user = null;
					try {
						if (ismobile) {
							user = userService.isUserLogin(email, password, null);
						} else {
							user = userService.isUserLogin(null, password, email);
						}
					} catch (UserException e) {
						e.printStackTrace();
						retMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
						return retMap;
					}
					// 本次登录用户
					userid = user.getId();
					// 检查第三方账号是否已绑定千品账号
					Long oldUserId = weiboDao.getWeiboUserIdByProType(openid, trdType);
					if (oldUserId != null && oldUserId > 0) {
						// 绑定过千品用户
						// 用户绑定过,但不是用的这个账号
						if (oldUserId.longValue() != userid.longValue()) {
							// 删除原绑定关系
							weiboDao.removeBindingAccessTokenByWeiboId(openid, trdType);
							// 检查当前千品用户是否绑定该第三方其他账号
							String oldOpenId = getOldOpenId(weiboDao.getWeiboProType(userid, trdType), trdType);
							if (StringUtils.isEmpty(oldOpenId)) {
								// 未绑定过增加
								weiboDao.addWeiboProType(paramMap, userid, trdType);
							} else {
								// 绑定过更新
								weiboDao.updateWeiboProType(paramMap, userid, trdType);
							}
						}
					} else {
						// 未绑定过千品用户
						// 检查当前千品用户是否绑定该第三方其他账号
						String oldOpenId = getOldOpenId(weiboDao.getWeiboProType(userid, trdType), trdType);
						if (StringUtils.isEmpty(oldOpenId)) {
							// 未绑定过增加
							weiboDao.addWeiboProType(paramMap, userid, trdType);
						} else {
							// 绑定过更新
							weiboDao.updateWeiboProType(paramMap, userid, trdType);
						}
					}

					// 设置登录状态
					String uuid = UUID.randomUUID().toString();
					if (user != null) {
						SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
					}
					retMap.put("code", "1");
					retMap.put("uuid", uuid);
					String tel = user.getMobile();
					if (tel == null) {
						tel = "";
					}
					retMap.put("tel", cryptUtil.cryptDes(tel, CryptKey));
				} else if ("1".equals(tp)) {
					// 新建账号
					// 验证邮箱格式
					if (!email.matches(EMAIL_REGX)) {
						retMap.put("code", MobileUserException.USER_REGISTER_EMAIL_ERROR);
						return retMap;
					}

					// 验证密码格式
					if (!password.matches(PASSWORD_REGX)) {
						retMap.put("code", MobileUserException.USER_REGISTER_PASSWORD_ERROR);
						return retMap;
					}

					// 判断用户是否存在
					User user = userDao.findUserByEmail(email);
					if (user != null) {
						retMap.put("code", MobileUserException.USER_EXIST);
						return retMap;
					}

					User newUser = null;
					try {
						newUser = userService.addUserEmailRegist(email, password, "");
					} catch (Exception e) {
						e.printStackTrace();
						retMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
						return retMap;
					}

					// 加入扩展信息,激活邮件信息
					try {
						String emailValidateUrl = propertyUtil.getProperty(Constant.EMAIL_VALIDATE_URL);
						StringBuilder sb = new StringBuilder();
						sb.append(emailValidateUrl);
						sb.append("?id=" + newUser.getId() + "&userkey=");
						String secret = MobilePurseSecurityUtils.hmacSign(newUser.getCustomerkey(), newUser.getId() + "");
						sb.append(secret);
						String subject = "千品网邮箱认证邮件";
						UserProfile userProfile = userService.getProfile(newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
						if (userProfile == null) {
							userService.addProfile(Constant.EMAIL_REGIST_URLKEY, secret, newUser.getId(), ProfileType.USERCONFIG);
						} else {
							userProfile.setValue(secret);
							userService.updateProfile(userProfile);
						}
						String time = DateUtils.dateToStr(new Date(), "yyyy年MM月dd日 HH:mm");
						String date = DateUtils.dateToStr(new Date());
						// 设置动态参数
						Object[] emailParams = new Object[] { time, sb.toString(), date };

						// 邮件模板参数未设置
						emailService.send(null, null, null, null, null, subject, new String[] { email }, null, null, new Date(), emailParams,
								Constant.EMAIL_VALIDATE_TEMPLATE);
					} catch (Exception e) {
						log.info("send email success....");
						e.printStackTrace();
					}

					// 新注册用户
					userid = newUser.getId();
					// 检查第三方账号是否已绑定千品账号
					Long oldUserId = weiboDao.getWeiboUserIdByProType(openid, trdType);
					if (oldUserId != null && oldUserId > 0) {
						// 绑定过千品用户
						// 用户绑定过,但不是用的这个账号
						if (oldUserId.longValue() != userid.longValue()) {
							// 删除原绑定关系
							weiboDao.removeBindingAccessTokenByWeiboId(openid, trdType);
							weiboDao.addWeiboProType(paramMap, userid, trdType);
						}
					} else {
						// 未绑定过千品用户
						weiboDao.addWeiboProType(paramMap, userid, trdType);
					}

					// 设置登录状态
					String uuid = UUID.randomUUID().toString();
					if (newUser != null) {
						SingletonLoginUtils.addSingletonForMobile(newUser, userService, uuid);
					}
					retMap.put("code", "1");
					retMap.put("uuid", uuid);
					String tel = newUser.getMobile();
					if (tel == null) {
						tel = "";
					}
					retMap.put("tel", cryptUtil.cryptDes(tel, CryptKey));
				} else {
					// 返回错误
					retMap.put("code", "0");
					return retMap;
				}
			}
			return retMap;
		} catch (Exception ex) {
			ex.printStackTrace();
			retMap.put("code", "0");
			return retMap;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> checkAuthCode(Map<String, Object> params) {

		Map<String, Object> checkcodeMap = new HashMap<String, Object>();

		String auth = (String) params.get("auth");
		String tel = (String) params.get("tel");
		String uuid = (String) params.get("uuid");

		if (StringUtils.isEmpty(auth) || StringUtils.isEmpty(tel) || StringUtils.isEmpty(uuid)) {
			log.info("checkAuthCode..... auth....or...tel....is....NULL");
			checkcodeMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return checkcodeMap;
		}

		auth = cryptUtil.decryptDes(auth, CryptKey);
		tel = cryptUtil.decryptDes(tel, CryptKey);

		if (!tel.matches(TELPHONE_REGX)) {
			log.info("checkAuthCode Tel....Not......Compliance with the rules.....");
			checkcodeMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return checkcodeMap;
		}

		User user = SingletonLoginUtils.getMemcacheMobileUser(uuid);

		Map<String, String> map = (Map<String, String>) memCacheService.get("MC_REGIST_RANDOMNUMBER_" + user.getId());

		if (user == null || map == null) {
			log.info("checkAuthCode.....user.....or....map....is.....NULL");
			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			checkcodeMap.put("code", MobileUserException.USER_WEBADDRESS_FAILD);
			return checkcodeMap;
		}
		/**
		 *  手机号如果已经存在 则不让验证
		 */
		if(StringUtils.isNotBlank(user.getMobile())){
			checkcodeMap.put("code", MobileUserException.SMS_CODE_DIFFENT);
			return checkcodeMap;
		}
		

		String mobileCode = map.get(tel);
		String mc = null;
		if (mobileCode != null) {
			log.info("mobileCode:" + mobileCode);
			mc = mobileCode.split(":")[1];
			if (mc == null) {
				user.setMobile(user.getMobile());
				SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			}
		}

		log.info("mc:" + mc + "--->auth:" + auth);
		if (mobileCode == null || mc == null || !mc.equals(auth) || auth == null || auth.equals("")) {
			log.info("checkAuthCode.....sourceCode...is...Diffent.....authcode........");
			user.setMobile(user.getMobile());
			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			checkcodeMap.put("code", MobileUserException.SMS_CODE_DIFFENT);
			return checkcodeMap;
		}

		user.setMobile(tel);
		user.setMobile_isavalible(1);
		user.setIsavalible(1);

		try {
			userService.updateUserMessage(user);
		} catch (UserException e) {
			log.info("update User......Is.....Faild......");
			checkcodeMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return checkcodeMap;
		}

		SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);

		checkcodeMap.put("code", "1");

		log.info("checkAuthCode.....Is....Success");
		return checkcodeMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAuthCode(Map<String, Object> params) {

		Map<String, Object> codeMap = new HashMap<String, Object>();

		String tel = (String) params.get("tel");
		String uuid = (String) params.get("uuid");
		String reqChannel = (String) params.get("reqChannel");

		if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(uuid)) {
			log.info("getAuthCode.....tel....or....uuid....is....NULL");
			codeMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return codeMap;
		}

		tel = cryptUtil.decryptDes(tel, CryptKey);

		if (StringUtils.isEmpty(tel)) {
			log.info("getAuthCode....descryTel......is.....NULL");
			codeMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return codeMap;
		}

		if (!tel.matches(TELPHONE_REGX)) {
			log.info("getCode Tel....Not......Compliance with the rules.....");
			codeMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return codeMap;
		}

		boolean flag = false;
		try {
			flag = userService.isUserExist(tel, null);
		} catch (UserException e) {
			log.info("getAuthCode......query....User...Is....Faild");
			codeMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return codeMap;
		}

		// 判断绑定手机号是否存在
		if (flag) {
			log.info("getAuthCode......User......Is...NOT.....NULL");
			codeMap.put("code", MobileUserException.USER_TELPHONE_ISEXIT);
			return codeMap;
		}

		// 邮件模板
		String smstemplate = "";
		if (reqChannel.equals("MC")) {
			smstemplate = Constant.MOBILE_AUTHCODE_TEMPLATE;
		} else {
			smstemplate = Constant.MOBILE_SMS_TEMPLATE;
		}
		Long uid = SingletonLoginUtils.getMobileLoginUserId(uuid);
		User user = SingletonLoginUtils.getMemcacheMobileUser(uuid);

		if (user == null) {
			log.info("getAuthCode......user.....Is....Null");
			codeMap.put("code", MobileUserException.USER_TELPHONE_ISEXIT);
			return codeMap;
		}

		Map<String, String> map = (Map<String, String>) memCacheService.get("MC_REGIST_RANDOMNUMBER_" + uid);

		String mcode = null;
		String vcode = "";
		if (map != null) {
			String mobileValidateCode = map.get(tel);
			int fcount = 1;
			if (mobileValidateCode != null) {
				String count = mobileValidateCode.split(":")[0];
				vcode = mobileValidateCode.split(":")[1];
				fcount = Integer.parseInt(count);
				if (fcount >= 5) {
					log.info("Send Message Is TimeOut...................");
					codeMap.put("code", MobileUserException.USER_SMS_OUTTIME);
					return codeMap;
				}
				fcount++;
				mcode = fcount + ":" + vcode;
			}
		}

		// 发送短信逻辑
		int count = 1;
		String vCode = "";
		String[] str = null;

		if (mcode != null) {
			str = mcode.split(":");
			if (str != null && str.length == 2) {
				count = Integer.parseInt(str[0]);
				vCode = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
			}
		}

		Sms sms = null;

		try {
			sms = smsService.getSmsByTitle(smstemplate);
		} catch (BaseException e) {
			log.info("getAuthCode......sms.....Is.....NUll");
			codeMap.put("code", MobileUserException.SMSTEMPLATE_NOT_FOUNT);
			return codeMap;
		}

		if (sms != null) {
			SmsInfo sourceBean = null;
			String content = "";
			String template = sms.getSmscontent();
			String randomNumbers = "";
			if (str == null || str.length != 2) {
				randomNumbers = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
				// 剩余条数不足了。加日志看。
				log.info("++++++++++++randomNumbers:" + randomNumbers + "+++++++++++++++++");
			} else {
				randomNumbers = vCode;
			}
			// 短信参数
			Object[] param = new Object[] { randomNumbers };
			content = MessageFormat.format(template, param);
			sourceBean = new SmsInfo(tel, content, SMS_TYPE, "0");
			Map smsMap = smsService.sendSms(sourceBean);
			// 设置到session里
			Map<String, String> sendSmsMap = new HashMap<String, String>();
			if (mcode != null) {
				sendSmsMap.put(tel, count + ":" + randomNumbers);
			} else {
				sendSmsMap.put(tel, "1:" + randomNumbers);
			}

			memCacheService.set("MC_REGIST_RANDOMNUMBER_" + user.getId(), sendSmsMap);

			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			log.info("getAuthCode......Is .............Success.............");
			codeMap.put("code", "1");
			return codeMap;
		}
		codeMap.put("code", "0");
		log.info("getAuthCode......Is .............Faild.............");
		return codeMap;
	}

	static final String IMG_URL_PREFIX = "/jsp/uploadimages/";

	@Override
	public Map<String, Object> getBrandDetail(Map<String, Object> params) {
		Map<String, Object> return_map = new HashMap<String, Object>();
		return_map.put("code", 0);
		try {
			SearchParam args = (SearchParam) Map2Object.convertResultMapToObject(params, SearchParam.class);

			if (args != null && args.getBrandid() != null) {
				List<Long> ids = new ArrayList<Long>();
				List<Map> brand_photo_list = luceneMobileAssistDao.getBrandPhoto(args.getBrandid());
				// 返回的图片列表
				StringBuilder brand_photo_return = new StringBuilder();
				for (int j = 0; j < brand_photo_list.size(); j++) {
					Map brand_photo_map = brand_photo_list.get(j);
					Set keys = brand_photo_map.keySet();

					for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
						String key = (String) iterator.next();
						if (brand_photo_map.get(key) != null && !"".equals(brand_photo_map.get(key))) {
							if (brand_photo_return.length() > 0) {
								brand_photo_return.append(",");
							}
							brand_photo_return.append(IMG_URL_PREFIX
									+ brand_photo_map.get(key).toString().substring(brand_photo_map.get(key).toString().indexOf("|") + 1));
						}
					}
				}
				ids.add(args.getBrandid());
				List<Map> branch_brand_map = luceneMobileAssistDao.getBranchesByBrandID(ids);

				List<Map> brand_basic = luceneMobileAssistDao.getBrandBasic(ids);
				// 品牌一级分类
				List<Map> brand_cat_fir_list = luceneMobileAssistDao.getBrandCat(true, ids);
				// 品牌二级分类
				List<Map> brand_cat_sec_list = luceneMobileAssistDao.getBrandCat(false, ids);
				Map<Long, List<Long>> brand_cat_sec_map = new HashMap<Long, List<Long>>();

				for (int i = 0; i < brand_cat_sec_list.size(); i++) {
					Map temp = brand_cat_sec_list.get(i);

					if (brand_cat_sec_map.get((Long) brand_cat_sec_list.get(i).get("brandid")) != null) {
						brand_cat_sec_map.get((Long) brand_cat_sec_list.get(i).get("brandid")).add((Long) temp.get("brandcatsec"));
					} else {
						List<Long> brand_sec_list_temp = new ArrayList<Long>();
						brand_sec_list_temp.add((Long) temp.get("brandcatsec"));
						brand_cat_sec_map.put((Long) brand_cat_sec_list.get(i).get("brandid"), brand_sec_list_temp);
					}
				}
				// 品牌分店商圈
				List<Map> region_map = luceneMobileAssistDao.getBrandBranches(true, ids);

				// 品牌分店商圈
				List<Map> regionext_map = luceneMobileAssistDao.getBrandBranches(false, ids);

				// 品牌星级,好评率
				List<String> brand_id_str = new ArrayList<String>();
				for (int i = 0; i < ids.size(); i++) {
					brand_id_str.add(ids.get(i).toString());
				}
				List<MerchantForm> brand_mf = shopDao.getBrandReview(brand_id_str);
				MerchantForm mf = null;
				if (brand_mf.size() > 0) {
					mf = brand_mf.get(0).calculateScore();
				}
				Map<Long, List<Map>> brand_branch_map = null;

				// 一个品牌有多个分店
				Map<Long, BranchProfile> branch_profile_map = new HashMap<Long, BranchProfile>();
				if (branch_brand_map.size() > 0) {
					List<Long> branch_ids = new ArrayList<Long>();
					for (int i = 0; i < branch_brand_map.size(); i++) {
						branch_ids.add((Long) branch_brand_map.get(i).get("storeid"));
					}
					if (branch_ids.size() > 0) {
						List<BranchProfile> branchProfiles = branchProfileDao.getBranchProfileById(com.beike.util.StringUtils.arrayToString(
								branch_ids.toArray(), ","));
						for (int i = 0; i < branchProfiles.size(); i++) {
							branch_profile_map.put(branchProfiles.get(i).getBranchId(), branchProfiles.get(i));
						}
						brand_branch_map = convertBrandBranch(region_map, regionext_map, branch_profile_map);
					}

				}
				// 组装品牌详情
				for (int j = 0; j < brand_basic.size(); j++) {
					return_map = brand_basic.get(j);
					return_map.put("brandcatfir", brand_cat_fir_list.get(0).get("brandcatfir"));
					return_map.put("brandcatsec", com.beike.util.StringUtils.arrayToString(brand_cat_sec_map.get(return_map.get("brandid")).toArray(), ","));
					return_map.put("storelist", brand_branch_map.get((Long) return_map.get("brandid")));
					return_map.put(REPUTATION, mf.getSatisfyRate() + "");
					return_map.put(STAR, mf.getMcScore());
					return_map.put(ISVIP, mf.getIsVip());
					return_map.put("imgs", brand_photo_return.toString());
				}
				return_map.put("code", 1);
				return return_map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return return_map;

		}
		return return_map;

	}

	@Override
	public Map<String, Object> getBrandGoods(Map<String, Object> params) {
		log.info("getBrandGoods:" + params);
		Map<String, Object> return_map = new HashMap<String, Object>();
		return_map.put("code", 0);
		try {
			SearchParam args = (SearchParam) Map2Object.convertResultMapToObject(params, SearchParam.class);
			if (args != null && args.getBrandid() != null) {
				List<Long> goodsidList = luceneMobileAssistDao.getBrandGoodsIDList(args.getBrandid());
				if (goodsidList != null && goodsidList.size() > 0) {
					List<Map> goodscatFir = luceneMobileAssistDao.getGoodsCat(true, goodsidList);
					List<Map> goodscatSec = luceneMobileAssistDao.getGoodsCat(false, goodsidList);
					Map<Long, List<Long>> goodscatfirMap = convertGoodsRegion(true, goodscatFir);
					Map<Long, List<Long>> goodscatsecMap = convertGoodsRegion(false, goodscatSec);

					// 分店一级商圈
					List<Map> branche_goods_fir_list = luceneMobileAssistDao.getGoodsBranch(true, goodsidList);
					Map<Long, List<Long>> branch_fir_map = convertGoodsBranch(true, branche_goods_fir_list);

					// 分店二级商圈
					List<Map> branche_goods_sec_list = luceneMobileAssistDao.getGoodsBranch(false, goodsidList);
					Map<Long, List<Long>> branch_sec_map = convertGoodsBranch(false, branche_goods_sec_list);

					// 品牌一级分类
					List<Map> goods_brand_list_fir = luceneMobileAssistDao.getGoodsBrand(true, goodsidList);
					Map<String, Object> goods_brand_fir_map = new HashMap<String, Object>();
					for (int i = 0; i < goods_brand_list_fir.size(); i++) {
						goods_brand_fir_map.put(goods_brand_list_fir.get(i).get("goodsid").toString(), (Long) goods_brand_list_fir.get(i).get("brandcatfir"));
						// 品牌名称
						goods_brand_fir_map.put(goods_brand_list_fir.get(i).get("goodsid").toString() + "_brand_name",
								goods_brand_list_fir.get(i).get("brandname"));
					}

					// 品牌二级分类
					List<Map> goods_brand_list_sec = luceneMobileAssistDao.getGoodsBrand(false, goodsidList);
					Map<Long, List<Long>> goods_brand_sec_map = new HashMap<Long, List<Long>>();
					for (int i = 0; i < goods_brand_list_sec.size(); i++) {
						if (goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")) != null) {
							goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")).add(
									(Long) goods_brand_list_sec.get(i).get("brandcatsec"));
						} else {
							List<Long> brand_sec_list = new ArrayList<Long>();
							brand_sec_list.add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
							goods_brand_sec_map.put((Long) goods_brand_list_sec.get(i).get("goodsid"), brand_sec_list);
						}
					}

					// 遍历商品分店
					Map<Long, List<Map>> branch_goods_List_result = convertBranchMap(branche_goods_fir_list, branch_fir_map, branch_sec_map);

					List<Map> goods_detai_map = luceneMobileAssistDao.getBrandGoodsDeatil(goodsidList);
					// 遍历商品详情,添加商圈信息
					for (int i = 0; i < goods_detai_map.size(); i++) {
						Long goodsid = (Long) goods_detai_map.get(i).get("goodsid");
						goods_detai_map.get(i).put("storelist", branch_goods_List_result.get(goodsid));
						goods_detai_map.get(i).put("brandcatfir", goods_brand_fir_map.get(goodsid.toString()));
						// 品牌名称
						goods_detai_map.get(i).put("brandname", goods_brand_fir_map.get(goodsid + "_brand_name"));
						// 品牌二级分类
						goods_detai_map.get(i).put("brandcatsec",
								com.beike.util.StringUtils.arrayToString(goods_brand_sec_map.get(new Long(goodsid.toString())).toArray(), ","));

					}
					return_map.put("code", 1);
					return_map.put("rs", goods_detai_map);
				} else {
					return_map.put("code", 1);
					// 该品牌下没有商品
					return_map.put("rs", new ArrayList<Map>());
				}
				log.info("getBrandGoods:" + params + "return:" + return_map);
				return return_map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return return_map;
		}
		log.info("getBrandGoods:" + params + "return:" + return_map);
		return return_map;
	}

	@Override
	public Map<String, Object> getCityDic() {

		Map<String, Object> areaMap = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> listArea = areaDao.queryOnlineArea();
			if (null == listArea || listArea.size() == 0) {
				areaMap.put("code", "0");
			} else {
				areaMap.put("code", "1");
				areaMap.put("rs", listArea);
			}
		} catch (Exception e) {
			areaMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
		}
		return areaMap;

	}

	@Override
	public Map<String, Object> getGoodsCat(Map<String, Object> params) {

		Map<String, Object> goodsCatlogMap = new HashMap<String, Object>();
		Object tagid = params.get("tagid");
		Object cityid = params.get("cityid");
		Integer parentid = 0;
		Long areaid = 0L;
		// 验证参数类型
		if (tagid != null) {
			if (tagid instanceof Integer) {
				parentid = (Integer) tagid;
			} else if (tagid instanceof String) {
				try {
					parentid = Integer.valueOf(tagid.toString());
				} catch (NumberFormatException e) {
					// 返回参数错误
					goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
					return goodsCatlogMap;
				}
			} else {
				goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return goodsCatlogMap;
			}
		} else {
			goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return goodsCatlogMap;
		}

		if (cityid != null) {
			if (cityid instanceof Integer) {
				areaid = Long.parseLong(String.valueOf(cityid));
			} else if (cityid instanceof String) {
				try {
					areaid = Long.valueOf(String.valueOf(cityid));
				} catch (NumberFormatException e) {
					// 返回参数错误
					goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
					return goodsCatlogMap;
				}
			} else {
				goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return goodsCatlogMap;
			}
		} else {
			goodsCatlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return goodsCatlogMap;
		}

		List<Map<String, Object>> listCatlog = propertyCatlogDao.getGoodsCatlogByTagIdHavingCity(parentid, areaid);

		if (null == listCatlog || listCatlog.size() == 0) {
			// 错误状态码
			goodsCatlogMap.put("code", "0");
		} else {
			goodsCatlogMap.put("code", "1");
			goodsCatlogMap.put("rs", listCatlog);
		}
		return goodsCatlogMap;
	}

	@Override
	public Map<String, Object> getGoodsDetail(Map<String, Object> params) {
		Map<String, Object> return_map = new HashMap<String, Object>();
		return_map.put("code", 0);
		SearchParam param = null;
		try {
			param = (SearchParam) Map2Object.convertResultMapToObject(params, SearchParam.class);
			if (param != null && param.getGoodsid() != null) {
				List<Long> ids = new ArrayList<Long>();
				ids.add(param.getGoodsid());
				List<Map> goodsdetail_list = luceneMobileAssistDao.getGoodsList(ids);
				List<Map> goods_branch_fir_list = luceneMobileAssistDao.getGoodsBranch(true, ids);
				// 分店一级商圈
				Map<Long, List<Long>> region_fir_map = convertGoodsBranch(true, goods_branch_fir_list);
				// 分店二级商圈
				List<Map> goods_branch_sec_list = luceneMobileAssistDao.getGoodsBranch(false, ids);
				Map<Long, List<Long>> region_sec_map = convertGoodsBranch(false, goods_branch_sec_list);
				// 品牌一级分类
				List<Map> goods_brand_list_fir = luceneMobileAssistDao.getGoodsBrand(true, ids);

				Map<Long, List<Long>> goods_brand_fir_map = new HashMap<Long, List<Long>>();
				for (int i = 0; i < goods_brand_list_fir.size(); i++) {
					if (goods_brand_fir_map.get((Long) goods_brand_list_fir.get(i).get("goodsid")) != null) {
						goods_brand_fir_map.get((Long) goods_brand_list_fir.get(i).get("goodsid")).add((Long) goods_brand_list_fir.get(i).get("brandcatfir"));
					} else {
						List<Long> brand_fir_list = new ArrayList<Long>();
						brand_fir_list.add((Long) goods_brand_list_fir.get(i).get("brandcatfir"));
						goods_brand_fir_map.put((Long) goods_brand_list_fir.get(i).get("goodsid"), brand_fir_list);
					}
				}
				// 品牌二级分类
				List<Map> goods_brand_list_sec = luceneMobileAssistDao.getGoodsBrand(false, ids);
				// 品牌二级分类放入map,需要Tunning
				Map<Long, List<Long>> goods_brand_sec_map = new HashMap<Long, List<Long>>();
				for (int i = 0; i < goods_brand_list_sec.size(); i++) {
					if (goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")) != null) {
						goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")).add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
					} else {
						List<Long> brand_sec_list = new ArrayList<Long>();
						brand_sec_list.add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
						goods_brand_sec_map.put((Long) goods_brand_list_sec.get(i).get("goodsid"), brand_sec_list);
					}
				}
				return_map = goodsdetail_list.get(0);

				// 遍历分店
				Map<Long, List<Map>> branch_goods_temp = convertBranchMap(goods_branch_fir_list, region_fir_map, region_sec_map);
				// 品牌分店
				return_map.put("storelist", branch_goods_temp.get((Long) return_map.get("goodsid")));
				// 品牌id
				return_map.put("brandid", goods_brand_list_fir.get(0).get("brandid"));
				// 品牌一级分类
				return_map.put("brandcatfir",
						com.beike.util.StringUtils.arrayToString(goods_brand_fir_map.get((Long) return_map.get("goodsid")).toArray(), ","));
				// 品牌二级分类
				return_map.put("brandcatsec",
						com.beike.util.StringUtils.arrayToString(goods_brand_sec_map.get((Long) return_map.get("goodsid")).toArray(), ","));
				// 品牌名称
				return_map.put("brandname", goods_brand_list_fir.get(0).get("brandname"));
				return_map.put("branddesc", luceneMobileAssistDao.getBrandIntrByGoodsID((Long) return_map.get("goodsid")).get(0));
				return_map.put("code", 1);
				return_map.put("input", param.toString());
				return return_map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_map.put("input", param.toString());
			return return_map;
		}

		return return_map;
	}

	private Map<Long, List<Long>> convertGoodsRegion(boolean fir, List<Map> goods_region) {
		Map<Long, List<Long>> goods_region_map = new HashMap<Long, List<Long>>();
		String key = "storeregionid";
		if (!fir) {
			key = "storeregionextid";
		}
		for (int i = 0; i < goods_region.size(); i++) {
			if (goods_region_map.get((Long) goods_region.get(i).get("goodsid")) != null) {
				goods_region_map.get((Long) goods_region.get(i).get("goodsid")).add((Long) goods_region.get(i).get(key));
			} else {
				List<Long> goodscat = new ArrayList<Long>();
				goodscat.add((Long) goods_region.get(i).get(key));
				// goodscat.add(new Long(goodscatFir.get(i).get("goodid").toString()));
				goods_region_map.put((Long) goods_region.get(i).get("goodsid"), goodscat);
			}
		}
		return goods_region_map;

	}

	/**
	 * 转换分店与商圈的Map
	 * 
	 * @param goods_branch_list
	 * @return {storeid:{分店信息}}
	 */
	private Map<Long, List<Long>> convertGoodsBranch(boolean fir, List<Map> goods_branch_list) {

		String key = "storeregionid";
		if (!fir) {
			key = "storeregionextid";
		}
		Map<Long, List<Long>> region_map = new HashMap<Long, List<Long>>();
		for (int i = 0; i < goods_branch_list.size(); i++) {
			if (region_map.get((Long) goods_branch_list.get(i).get("storeid")) != null
					&& !region_map.get((Long) goods_branch_list.get(i).get("storeid")).contains((Long) goods_branch_list.get(i).get(key))) {
				region_map.get((Long) goods_branch_list.get(i).get("storeid")).add((Long) goods_branch_list.get(i).get(key));
			} else {
				List<Long> store_region = new ArrayList<Long>();
				store_region.add((Long) goods_branch_list.get(i).get(key));
				region_map.put((Long) goods_branch_list.get(i).get("storeid"), store_region);
			}
		}

		return region_map;

	}

	private Map<Long, List<Long>> convertBranchMap(List<Map> branches) {
		Map<Long, List<Long>> branch_goods_map = new HashMap<Long, List<Long>>();
		// 遍历商品分店(一个商品多个分店)
		for (int i = 0; i < branches.size(); i++) {
			Map temp = branches.get(i);
			Long storeid = (Long) temp.get("storeid");
			Long goodsid = (Long) temp.get("goodsid");
			List<Long> goods_branch_list = branch_goods_map.get(goodsid);
			if (goods_branch_list != null) {
				goods_branch_list.add(storeid);
			} else {
				goods_branch_list = new ArrayList<Long>();
				goods_branch_list.add(storeid);
				branch_goods_map.put(goodsid, goods_branch_list);
			}
		}
		return branch_goods_map;
	}

	private Map<Long, List<Map>> convertBranchMap(List<Map> branches, Map<Long, List<Long>> regionid, Map<Long, List<Long>> regionextid) {
		Map<Long, List<Map>> branch_goods_List_result = new HashMap<Long, List<Map>>();
		// 遍历商品分店(一个商品多个分店)
		for (int i = 0; i < branches.size(); i++) {
			Map temp = branches.get(i);
			Long storeid = (Long) temp.get("storeid");
			Long goodsid = (Long) temp.get("goodsid");
			List<BranchProfile> bp = branchProfileDao.getBranchProfileById(storeid.toString());
			// 分店好评率
			float rep_val = -1;
			if (bp.size() > 0) {
				rep_val = bp.get(0).calculateScore().getSatisfyRate();
			}
			// logger.info("rep_val " + rep_val + "storeid " + storeid);
			temp.put(REPUTATION, rep_val + "");
			if (branch_goods_List_result.get(goodsid) != null) {
				temp.put("storeregionid", com.beike.util.StringUtils.arrayToString(regionid.get(storeid).toArray(), ","));
				temp.put("storeregionextid", com.beike.util.StringUtils.arrayToString(regionextid.get(storeid).toArray(), ","));
				((List<Map>) branch_goods_List_result.get(goodsid)).add(temp);
			} else {
				List<Map> branch_list = new ArrayList<Map>();
				temp.put("storeregionid", com.beike.util.StringUtils.arrayToString(regionid.get(storeid).toArray(), ","));
				temp.put("storeregionextid", com.beike.util.StringUtils.arrayToString(regionextid.get(storeid).toArray(), ","));
				branch_list.add(temp);
				branch_goods_List_result.put(goodsid, branch_list);
			}
		}
		return branch_goods_List_result;
	}

	static final String STAR = "star";
	static final String REPUTATION = "reputation";
	static final String ISVIP = "isvip";

	/**
	 * 转换品牌,分店,商圈关系 janwen
	 * 
	 * @param regionmap
	 * @param regionextmap
	 * @param branchprofilemap
	 * @return
	 * 
	 */
	private Map<Long, List<Map>> convertBrandBranch(List<Map> regionmap, List<Map> regionextmap, Map<Long, BranchProfile> branchprofilemap) {

		// 遍历分店一级商圈
		Map<Long, List<Long>> branch_region_map = new HashMap<Long, List<Long>>();
		for (int i = 0; i < regionmap.size(); i++) {
			if (branch_region_map.get((Long) regionmap.get(i).get("storeid")) != null) {
				branch_region_map.get((Long) regionmap.get(i).get("storeid")).add((Long) regionmap.get(i).get("storeregionid"));
			} else {
				List<Long> temp = new ArrayList<Long>();
				temp.add((Long) regionmap.get(i).get("storeregionid"));
				branch_region_map.put((Long) regionmap.get(i).get("storeid"), temp);
			}
		}

		// 遍历分店二级级商圈
		Map<Long, List<Long>> branch_regionext_map = new HashMap<Long, List<Long>>();
		for (int i = 0; i < regionextmap.size(); i++) {
			if (branch_regionext_map.get((Long) regionextmap.get(i).get("storeid")) != null) {
				branch_region_map.get((Long) regionextmap.get(i).get("storeid")).add((Long) regionextmap.get(i).get("storeregionextid"));
			} else {
				List<Long> temp = new ArrayList<Long>();
				temp.add((Long) regionextmap.get(i).get("storeregionextid"));
				branch_regionext_map.put((Long) regionextmap.get(i).get("storeid"), temp);
			}
		}
		// 品牌分店一个品牌多家分店
		Map<Long, List<Map>> brand_branch_map = new HashMap<Long, List<Map>>();
		for (int j = 0; j < regionmap.size(); j++) {
			// 包含分店电话,营业时间等
			Map map = regionmap.get(j);
			Long storeid = (Long) map.get("storeid");
			// 添加分店好评
			BranchProfile bp = branchprofilemap.get(storeid);
			float rep_val = -1;
			if (bp != null) {
				rep_val = bp.calculateScore().getSatisfyRate();
			}
			map.put(REPUTATION, rep_val + "");
			if (brand_branch_map.get((Long) map.get("brandid")) != null) {
				map.put("storeregionid", com.beike.util.StringUtils.arrayToString(branch_region_map.get(storeid).toArray(), ","));
				map.put("storeregionextid", com.beike.util.StringUtils.arrayToString(branch_regionext_map.get(storeid).toArray(), ","));
				brand_branch_map.get((Long) map.get("brandid")).add(map);
			} else {
				List<Map> temp = new ArrayList<Map>();
				map.put("storeregionid", com.beike.util.StringUtils.arrayToString(branch_region_map.get(storeid).toArray(), ","));
				map.put("storeregionextid", com.beike.util.StringUtils.arrayToString(branch_regionext_map.get(storeid).toArray(), ","));
				temp.add(map);
				brand_branch_map.put((Long) map.get("brandid"), temp);
			}
		}
		return brand_branch_map;
	}

	private static final Logger logger = Logger.getLogger(MobileServiceImpl.class);

	@Override
	public Map<String, Object> getListGoodsOrBrand(Map<String, Object> params) {
		log.info("getListGoodsOrBrand" + params);
		// 返回的结果
		Map<String, Object> return_map = new HashMap<String, Object>();
		// 商品,品牌列表详情返回的结果集
		List<Map> goods_brand_map_list = new ArrayList<Map>();
		SearchParam param = null;
		try {
			param = (SearchParam) Map2Object.convertResultMapToObject(params, SearchParam.class);

			if (param != null && param.getType() == 1) {
				logger.info(param.toString());
				Map<String, Object> rs_map = luceneSearchMobileService.getListGoods(param);
				List<Long> searchedGoodidsList = (List<Long>) rs_map.get("searchedids");
				if (searchedGoodidsList.size() == 0) {
					return_map.put("rs", goods_brand_map_list);
					return_map.put("code", 1);
					return_map.put("n", 0);
					return_map.put("input", param.toString());
					return return_map;
				}
				List<Map> goods_detail = luceneMobileAssistDao.getGoodsList(searchedGoodidsList);
				// 分店一级商圈
				List<Map> branche_goods_fir_list = luceneMobileAssistDao.getGoodsBranch(true, searchedGoodidsList);
				Map<Long, List<Long>> branch_fir_map = convertGoodsBranch(true, branche_goods_fir_list);

				// 分店二级商圈
				List<Map> branche_goods_sec_list = luceneMobileAssistDao.getGoodsBranch(false, searchedGoodidsList);
				Map<Long, List<Long>> branch_sec_map = convertGoodsBranch(false, branche_goods_sec_list);

				// 品牌一级分类
				List<Map> goods_brand_list_fir = luceneMobileAssistDao.getGoodsBrand(true, searchedGoodidsList);
				Map<String, Object> goods_brand_fir_map = new HashMap<String, Object>();
				for (int i = 0; i < goods_brand_list_fir.size(); i++) {
					goods_brand_fir_map.put(goods_brand_list_fir.get(i).get("goodsid").toString(), (Long) goods_brand_list_fir.get(i).get("brandcatfir"));
					// 品牌名称
					goods_brand_fir_map
							.put(goods_brand_list_fir.get(i).get("goodsid").toString() + "_brand_name", goods_brand_list_fir.get(i).get("brandname"));
				}

				// 品牌二级分类
				List<Map> goods_brand_list_sec = luceneMobileAssistDao.getGoodsBrand(false, searchedGoodidsList);
				Map<Long, List<Long>> goods_brand_sec_map = new HashMap<Long, List<Long>>();
				for (int i = 0; i < goods_brand_list_sec.size(); i++) {
					if (goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")) != null) {
						goods_brand_sec_map.get((Long) goods_brand_list_sec.get(i).get("goodsid")).add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
					} else {
						List<Long> brand_sec_list = new ArrayList<Long>();
						brand_sec_list.add((Long) goods_brand_list_sec.get(i).get("brandcatsec"));
						goods_brand_sec_map.put((Long) goods_brand_list_sec.get(i).get("goodsid"), brand_sec_list);
					}
				}
				// 遍历商品分店
				Map<Long, List<Map>> branch_goods_List_result = convertBranchMap(branche_goods_fir_list, branch_fir_map, branch_sec_map);

				// 遍历商品详情将其他信息组装成最后的返回结果
				Map<Long, Map> sorted_goods_detail_map = sortMap(goods_detail, "goodsid");
				Map<Long, String> sales_count_map = (Map<Long, String>) rs_map.get("sales_count_map");
				Map<Long, String> goods_price_map = (Map<Long, String>) rs_map.get("goods_price_map");
				for (int n = 0; n < searchedGoodidsList.size(); n++) {

					// Long goodsid = (Long) goods_detai_map.get("goodsid");
					Long goodsid = searchedGoodidsList.get(n);
					Map goods_detail_map = sorted_goods_detail_map.get(goodsid);
					// bg.currentPrice currentprice,salescount从索引获取
					goods_detail_map.put("salescount", sales_count_map.get(goodsid));
					goods_detail_map.put("currentprice", goods_price_map.get(goodsid));
					// 分店信息
					goods_detail_map.put("storelist", branch_goods_List_result.get(goodsid));
					// 品牌一级分类
					goods_detail_map.put("brandcatfir", goods_brand_fir_map.get(goodsid.toString()));
					// 品牌名称
					goods_detail_map.put("brandname", goods_brand_fir_map.get(goodsid + "_brand_name"));
					// 品牌二级分类
					goods_detail_map.put("brandcatsec",
							com.beike.util.StringUtils.arrayToString(goods_brand_sec_map.get(new Long(goodsid.toString())).toArray(), ","));
					goods_brand_map_list.add(goods_detail_map);
				}
				return_map.put("rs", goods_brand_map_list);
				return_map.put("code", 1);
				return_map.put("input", param.toString());
				return_map.put("n", rs_map.get("total"));
			} else if (param != null && param.getType() == 2) {
				Map<String, Object> search_result = luceneSearchMobileService.getListBrand(param);

				List<Long> searchedbrandidList = (List<Long>) search_result.get("searchedids");

				if (searchedbrandidList.size() == 0) {
					return_map.put("rs", goods_brand_map_list);
					return_map.put("code", 1);
					return_map.put("n", 0);
					return_map.put("input", param.toString());
					return return_map;
				}
				// 一级分类
				List<Map> brand_fir = luceneMobileAssistDao.getBrandCat(true, searchedbrandidList);
				Map<Long, Long> brand_fir_map = new HashMap<Long, Long>();
				for (int i = 0; i < brand_fir.size(); i++) {
					Map temp = brand_fir.get(i);
					brand_fir_map.put((Long) temp.get("brandid"), (Long) temp.get("brandcatfir"));
				}
				// 二级分类
				List<Map> brand_sec = luceneMobileAssistDao.getBrandCat(false, searchedbrandidList);
				Map<Long, List<Long>> brand_sec_map = new HashMap<Long, List<Long>>();
				for (int i = 0; i < brand_sec.size(); i++) {
					Map map = brand_sec.get(i);
					if (brand_sec_map.get((Long) map.get("brandid")) != null) {
						brand_sec_map.get((Long) map.get("brandid")).add((Long) map.get("brandcatsec"));
					} else {
						List<Long> temp = new ArrayList<Long>();
						temp.add((Long) map.get("brandcatsec"));
						brand_sec_map.put((Long) map.get("brandid"), temp);
					}
				}
				// 品牌分店商圈
				List<Map> region_map = luceneMobileAssistDao.getBrandBranches(true, searchedbrandidList);
				List<Map> regionext_map = luceneMobileAssistDao.getBrandBranches(false, searchedbrandidList);
				// 分店好评率
				// 品牌星级,好评率
				List<String> brand_id_str = new ArrayList<String>();
				for (int i = 0; i < searchedbrandidList.size(); i++) {
					brand_id_str.add(searchedbrandidList.get(i).toString());
				}
				List<MerchantForm> brand_mf = shopDao.getBrandReview(brand_id_str);
				// 品牌星级
				Map<Long, String> brand_star_map = (Map<Long, String>) search_result.get("brand_star_map");
				Map<Long, MerchantForm> brand_vip_map = new HashMap<Long, MerchantForm>();
				if (brand_mf.size() > 0) {
					for (int i = 0; i < brand_mf.size(); i++) {
						MerchantForm mf = brand_mf.get(i);
						brand_vip_map.put(new Long(mf.getId()), mf);
					}
				}
				Map<Long, List<Map>> brand_branch_map = null;
				List<Map> branch_brand_map = luceneMobileAssistDao.getBranchesByBrandID(searchedbrandidList);
				// 一个品牌有多个分店
				Map<Long, BranchProfile> branch_profile_map = new HashMap<Long, BranchProfile>();
				if (branch_brand_map.size() > 0) {
					List<Long> branch_ids = new ArrayList<Long>();
					for (int i = 0; i < branch_brand_map.size(); i++) {
						branch_ids.add((Long) branch_brand_map.get(i).get("storeid"));
					}
					if (branch_ids.size() > 0) {
						List<BranchProfile> branchProfiles = branchProfileDao.getBranchProfileById(com.beike.util.StringUtils.arrayToString(
								branch_ids.toArray(), ","));
						for (int i = 0; i < branchProfiles.size(); i++) {
							branch_profile_map.put(branchProfiles.get(i).getBranchId(), branchProfiles.get(i));
						}
					}
				}
				brand_branch_map = convertBrandBranch(region_map, regionext_map, branch_profile_map);
				// 遍历品牌详情
				List<Map> brand_basic = luceneMobileAssistDao.getBrandBasic(searchedbrandidList);
				Map<Long, Map> sorted_brand_map = sortMap(brand_basic, "brandid");
				Map<Long, String> brand_sales_map = (Map<Long, String>) search_result.get("brand_sales_map");
				for (int n = 0; n < searchedbrandidList.size(); n++) {
					Long brandid = searchedbrandidList.get(n);
					Map map = sorted_brand_map.get(brandid);
					// 品牌星级
					Long star_val = -1L;
					// 0(不是)/1
					int isvip = 0;
					if (brand_star_map.get(brandid) != null) {
						star_val = new Long(brand_star_map.get(brandid));
						isvip = brand_vip_map.get(brandid).getIsVip();
					}
					if (star_val == null) {
						star_val = -1L;
					}
					map.put(STAR, star_val);
					map.put(ISVIP, isvip);
					map.put("salescount", brand_sales_map.get(brandid));
					map.put("storelist", brand_branch_map.get(brandid));
					map.put("brandcatfir", brand_fir_map.get(brandid));
					map.put("brandcatsec", com.beike.util.StringUtils.arrayToString(brand_sec_map.get(brandid).toArray(), ","));
					goods_brand_map_list.add(map);
				}

				return_map.put("rs", goods_brand_map_list);
				return_map.put("code", 1);
				return_map.put("n", search_result.get("total"));
				return_map.put("input", param.toString());
			} else {
				logger.info("参数类型不对" + param.getType());
				return_map.put("rs", goods_brand_map_list);
				return_map.put("code", 0);
				return_map.put("n", 0);
				return_map.put("input", param.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_map.put("rs", goods_brand_map_list);
			return_map.put("code", 0);
			return_map.put("n", 0);
			return_map.put("input", param.toString());
		}
		log.info("getListGoodsOrBrand" + params + ",return:" + return_map);
		return return_map;
	}

	/**
	 * map按照搜索出来的id进行排序
	 * 
	 * @param tosortMap
	 * @return
	 */
	private Map<Long, Map> sortMap(List<Map> tosortListMap, String key) {
		Map<Long, Map> sortedMap = new HashMap<Long, Map>();
		for (int n = 0; n < tosortListMap.size(); n++) {
			Map list_map = (Map) tosortListMap.get(n);
			Long value = (Long) list_map.get(key);
			sortedMap.put(value, list_map);
		}
		return sortedMap;
	}

	@Override
	public Map<String, Object> getRegionDic(Map<String, Object> params) {

		Map<String, Object> regionMap = new HashMap<String, Object>();
		Object cityid = params.get("cityid");
		Object parentid = params.get("parentid");
		int thecityid = 0;
		int theparentid = 0;
		// 验证参数格式
		if (cityid != null) {
			if (cityid instanceof Integer) {
				thecityid = (Integer) cityid;
			} else if (cityid instanceof String) {
				try {
					thecityid = Integer.valueOf(cityid.toString());
				} catch (NumberFormatException e) {
					regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
					return regionMap;
				}
			} else {
				// 返回参数错误
				regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return regionMap;
			}
		} else {
			regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return regionMap;
		}
		if (parentid != null) {
			if (parentid instanceof Integer) {
				theparentid = (Integer) parentid;
			} else if (parentid instanceof String) {
				try {
					theparentid = Integer.valueOf(parentid.toString());
				} catch (NumberFormatException e) {
					regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
					return regionMap;
				}
			} else {
				regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return regionMap;
			}
		} else {
			regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return regionMap;
		}
		List<Map<String, Object>> listCatlog = regionCatlogDao.getRegionCatlog(thecityid, theparentid);
		if (null == listCatlog || listCatlog.size() == 0) {
			// 返回错误状态码
			regionMap.put("code", "0");
		} else {
			regionMap.put("code", "1");
			regionMap.put("rs", listCatlog);
		}
		return regionMap;
	}

	@Override
	public Map<String, Object> loginWithOpenID(Map<String, Object> params) {

		Map<String, Object> loginMap = new HashMap<String, Object>();
		String openid = (String) params.get("openid");
		String pr = (String) params.get("pr");

		// 以下参数不能为空，否则参数错误
		if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(pr)) {
			loginMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return loginMap;
		}

		// 解密
		openid = cryptUtil.decryptDes(openid, CryptKey);

		if (StringUtils.isEmpty(openid)) {
			loginMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return loginMap;
		}
		ProfileType trdType = null;
		// 第三方来源
		if ("sina".equalsIgnoreCase(pr)) {
			trdType = ProfileType.SINACONFIG;
		} else if ("renren".equalsIgnoreCase(pr)) {
			trdType = ProfileType.XIAONEICONFIG;
		} else if ("tencent".equalsIgnoreCase(pr)) {
			trdType = ProfileType.TENCENTCONFIG;
		} else if ("baidu".equalsIgnoreCase(pr)) {
			trdType = ProfileType.BAIDUCONFIG;
		} else if ("qq".equalsIgnoreCase(pr)) {
			trdType = ProfileType.QQCONFIG;
		}

		if (trdType == null) {
			loginMap.put("code", MobileUserException.USER_INVALID_THIRDPART);
			return loginMap;
		} else {
			// 查找是否存在用户Id
			long userid = weiboDao.getWeiboUserIdByProType(openid, trdType);
			if (userid > 0L) {
				User user = userService.findById(userid);
				if (user != null) {
					String uuid = UUID.randomUUID().toString();
					String email = user.getEmail();
					String tel = user.getMobile();

					// 加密
					if (StringUtils.isEmpty(email)) {
						email = "";
					}
					if (StringUtils.isEmpty(tel)) {
						tel = "";
					}
					email = cryptUtil.cryptDes(email, CryptKey);
					tel = cryptUtil.cryptDes(tel, CryptKey);

					SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);

					loginMap.put("code", "1");
					loginMap.put("uuid", uuid);
					loginMap.put("email", email);
					loginMap.put("tel", tel);
				} else {
					loginMap.put("code", "0");
				}
			} else {
				loginMap.put("code", "0");
			}
		}

		return loginMap;
	}

	@Override
	public Map<String, Object> getCDN() {
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("code", "1");
		try {
			String cdnurl = StaticDomain.getDomain();
			userMap.put("cdnurl", cdnurl);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("get cdnurl error");
			userMap.put("code", "0");
		}
		return userMap;
	}

	@Override
	public Map<String, Object> getUserIDByUUID(Map<String, Object> params) {

		Map<String, Object> userMap = new HashMap<String, Object>();

		String uuid = (String) params.get("uuid");

		if (StringUtils.isEmpty(uuid)) {
			log.info("getUserIDByUUID........uuid..........Is.......Null");
			userMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return userMap;
		}

		Long uid = SingletonLoginUtils.getMobileLoginUserId(uuid);

		if (uid == null) {
			log.info("getUserIDByUUID..........uid..........Is....NULL");
			userMap.put("code", "0");
			return userMap;
		}

		log.info("getUserIDByUUID..........Is............Success");
		userMap.put("code", "1");
		userMap.put("userid", uid);

		return userMap;
	}

	/**
	 * 
	 * @param paramMap
	 * @param profile
	 * @return
	 */
	private String getOldOpenId(Map<String, String> paramMap, ProfileType profile) {
		String oldOpenId = null;
		if (paramMap != null && !paramMap.isEmpty()) {
			if (ProfileType.SINACONFIG.equals(profile)) {
				oldOpenId = paramMap.get("sina_userid");
			} else if (ProfileType.XIAONEICONFIG.equals(profile)) {
				oldOpenId = paramMap.get("renrenId");
			} else if (ProfileType.TENCENTCONFIG.equals(profile)) {
				oldOpenId = paramMap.get("tencentid");
			} else if (ProfileType.BAIDUCONFIG.equals(profile)) {
				oldOpenId = paramMap.get("baidu_id");
			} else if (ProfileType.QQCONFIG.equals(profile)) {
				oldOpenId = paramMap.get("qq_openid");
			}
		}
		return oldOpenId;
	}

	/**
	 * 
	 * @param openid
	 * @param name
	 * @param profile
	 * @return
	 */
	private Map<String, String> paseParamMap(String openid, String name, ProfileType profile) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (ProfileType.SINACONFIG.equals(profile)) {
			paramMap.put("sina_userid", openid);
			paramMap.put("sina_screenName", name);
		} else if (ProfileType.XIAONEICONFIG.equals(profile)) {
			paramMap.put("renren_id", openid);
			paramMap.put("xiaonei_screenName", name);
		} else if (ProfileType.TENCENTCONFIG.equals(profile)) {
			paramMap.put("tencentid", openid);
			paramMap.put("tencent_screenName", name);
		} else if (ProfileType.BAIDUCONFIG.equals(profile)) {
			paramMap.put("baidu_id", openid);
			paramMap.put("baidu_screenName", name);
		} else if (ProfileType.QQCONFIG.equals(profile)) {
			paramMap.put("qq_openid", openid);
			paramMap.put("qq_screenName", name);
		}
		return paramMap;
	}

	@Override
	public Map<String, Object> getUserInfo(Map<String, Object> params) {

		Map<String, Object> resuMap = new HashMap<String, Object>();

		String uuid = (String) params.get("uuid");

		if (StringUtils.isEmpty(uuid)) {
			log.info("query User......uuid....Is....Null");
			resuMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return resuMap;
		}

		User user = SingletonLoginUtils.getMemcacheMobileUser(uuid);

		if (user == null) {
			log.info("query User ......Is.....Null");
			resuMap.put("code", MobileUserException.USER_LOGIN_OUTTIME);
			return resuMap;
		}
		String email = user.getEmail();
		if (StringUtils.isNotEmpty(email)) {
			email = cryptUtil.cryptDes(email, CryptKey);
		}

		String tel = user.getMobile();
		if (StringUtils.isNotEmpty(tel)) {
			tel = cryptUtil.cryptDes(tel, CryptKey);
		}

		resuMap.put("code", "1");
		resuMap.put("email", email);
		resuMap.put("tel", tel);
		log.info("query....User....Is....Success......");
		return resuMap;
	}

	@Override
	public Map<String, Object> getListBranch(Map<String, Object> params) {
		// 返回的结果
		Map<String, Object> return_map = new HashMap<String, Object>();
		SearchParam param = null;
		try {
			param = (SearchParam) Map2Object.convertResultMapToObject(params, SearchParam.class);
			Map<String, Object> searchedResult = luceneSearchMobileService.getListBranch(param);
			List<String> branch_ids = (List<String>) searchedResult.get("branchids");
			List<String> brand_ids = (List<String>) searchedResult.get("brandids");
			if (branch_ids.size() > 0) {
				return_map.put("n", searchedResult.get("total"));
				Map<String, BranchProfile> branch_map_reputation = new HashMap<String, BranchProfile>();
				Map<String, MerchantForm> brand_map_reputation = new HashMap<String, MerchantForm>();
				List<BranchProfile> profiles = null;
				profiles = branchProfileDao.getBranchProfileById(com.beike.util.StringUtils.arrayToString(branch_ids.toArray(), ","));
				for (int i = 0; i < profiles.size(); i++) {
					BranchProfile bp = profiles.get(i);
					branch_map_reputation.put(bp.getBranchId().toString(), bp);
				}
				List<MerchantForm> brands = null;
				if (brand_ids.size() > 0) {
					brands = shopDao.getBrandReview(brand_ids);
					for (int i = 0; i < brands.size(); i++) {
						brand_map_reputation.put(brands.get(i).getId(), brands.get(i));
					}
				}
				List<Map<String, Object>> branch_list_object = (List<Map<String, Object>>) searchedResult.get("searchedbranch");
				for (int i = 0; i < branch_list_object.size(); i++) {
					Map<String, Object> map = branch_list_object.get(i);
					String storeid = (String) map.get("storeid");
					float rep_val = -1;
					if (branch_map_reputation.get(storeid) != null) {
						rep_val = branch_map_reputation.get(storeid).calculateScore().getSatisfyRate();
					}
					map.put(REPUTATION, rep_val + "");
					String brandid = (String) map.get("brandid");
					Long star_val = -1L;
					if (brand_map_reputation.get(brandid) != null) {
						star_val = brand_map_reputation.get(brandid).calculateScore().getMcScore();
					}
					map.put(STAR, star_val);

				}
				return_map.put("rs", branch_list_object);
				return_map.put("code", 1);
				return_map.put("input", param.toString());
			} else {
				return_map.put("n", 0);
				return_map.put("rs", 0);
				return_map.put("code", 0);
				return_map.put("input", param.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("app店铺列表搜索异常");
			return_map.put("n", 0);
			return_map.put("rs", 0);
			return_map.put("code", 0);
			return_map.put("input", param.toString());
		}
		return return_map;
	}

	@Override
	public Map<String, Object> emailAlert(Map<String, Object> params) {
		List<Map<String, Object>> emails = (List<Map<String, Object>>) params.get("emails");
		Map<String, Object> return_map = new HashMap<String, Object>();
		if (emails != null && emails.size() > 0) {
			try {
				for (int i = 0; i < emails.size(); i++) {
					emailService.sendMail((String) emails.get(i).get("emailAddress"), "qianpin.com", (String) params.get("message"), "App邮件告警");
				}
			} catch (Exception e) {
				return_map.put("code", 0);
				return return_map;
			}
		}

		return_map.put("code", 1);
		return return_map;
	}

	@Override
	public Map<String, Object> messageAlert(Map<String, Object> params) {
		List<Map<String, Object>> phonenumbers = (List<Map<String, Object>>) params.get("phone");
		Map<String, Object> return_map = new HashMap<String, Object>();
		if (phonenumbers != null && phonenumbers.size() > 0) {
			SmsInfo sourceBean = null;
			for (int i = 0; i < phonenumbers.size(); i++) {
				sourceBean = new SmsInfo((String) phonenumbers.get(i).get("phonenumber"), (String) params.get("message"), SMS_TYPE, "0");
				smsService.sendSms(sourceBean);
			}
			return_map.put("code", 1);
		} else {
			return_map.put("code", 0);
		}

		return return_map;
	}

	private Map<Long, Map<String, Object>> getMerExpandMap(String meridsstr) {
		List<Map<String, Object>> merExpandList = lbsMerchantGoodsDao.getMerExpands(meridsstr);
		Map<Long, Map<String, Object>> merExpandMap = new HashMap<Long, Map<String, Object>>();
		for (Map tmpMerExpMap : merExpandList) {
			Long merchantId = (Long) tmpMerExpMap.get("merchantId");
			Map<String, Object> merExpMap = merExpandMap.get(merchantId);
			if (merExpMap == null) {
				merExpMap = new HashMap<String, Object>();
			}
			merExpMap.put("merWellCount", tmpMerExpMap.get("merWellCount"));
			merExpMap.put("merSatisfyCount", tmpMerExpMap.get("merSatisfyCount"));
			merExpMap.put("merPoorCount", tmpMerExpMap.get("merPoorCount"));
			merExpandMap.put(merchantId, merExpMap);
		}
		return merExpandMap;
	}

	private void getRegionsIdsMap(Map<Long, List<Long>> regionsIdsMap, Map<Long, List<String>> regionsMap, String meridsstr) {
		// 获取每一个分店下的商圈标签集合及商圈标签ID集合
		List<Map<String, Object>> branchRegionsMap = lbsMerchantGoodsDao.getBranchRegions(meridsstr);
		for (Map<String, Object> map : branchRegionsMap) {
			Long key = (Long) map.get("branchid");
			// 获取每个分店下的商圈标签ID集合
			List<Long> reginTagIds = regionsIdsMap.get(key);
			if (reginTagIds == null) {
				reginTagIds = new ArrayList<Long>();
			}
			if (reginTagIds.size() == 0) {
				reginTagIds.add((Long) map.get("id2"));
				reginTagIds.add((Long) map.get("id1"));
			} else {
				if (reginTagIds.size() >= 1) {
					reginTagIds.add((Long) map.get("id1"));
				}
			}
			regionsIdsMap.put(key, reginTagIds);

			// 获取每个分店下的商圈标签集合
			List<String> reginTags = regionsMap.get(key);
			if (reginTags == null) {
				reginTags = new ArrayList<String>();
			}
			if (reginTags.size() == 0) {
				reginTags.add((String) map.get("rename2"));
				reginTags.add((String) map.get("rename1"));
			} else {
				if (reginTags.size() >= 1) {
					reginTags.add((String) map.get("rename1"));
				}
			}
			regionsMap.put(key, reginTags);

		}
	}

	/*
	 * private Map<Long, List<String>> getRegionsMap(String meridsstr){ //获取每一个分店下的商圈标签的集合 List<Map<String, Object>> branchRegionsMap =
	 * lbsMerchantGoodsDao.getBranchRegions(meridsstr); Map<Long, List<String>> regionsMap = new HashMap<Long, List<String>>(0); for(Map<String, Object> map :
	 * branchRegionsMap){ Long key = (Long)map.get("branchid"); List<String> reginTags = regionsMap.get(key); if(reginTags == null){ reginTags = new
	 * ArrayList<String>(); } if(reginTags.size() == 0){ reginTags.add((String)map.get("rename2")); reginTags.add((String)map.get("rename1")); }else{
	 * if(reginTags.size() >= 1){ reginTags.add((String)map.get("rename1")); } } regionsMap.put(key, reginTags); } return regionsMap; }
	 */

	@Override
	public List<LbsMerchantInfo> getLbsMerchantInfo(int dataCount, Long lastMaxId) {
		if (lastMaxId == null) {
			lastMaxId = 0L;
		}
		List<LbsMerchantInfo> merchantInfos = lbsMerchantGoodsDao.getLbsMerchantInfo(dataCount, lastMaxId);
		// 所查询分店的分店id集合，用,分隔
		if (merchantInfos == null || merchantInfos.size() == 0) {
			return null;
		}
		StringBuilder meridsstr = new StringBuilder("");
		for (LbsMerchantInfo info : merchantInfos) {
			meridsstr.append(info.getId()).append(",");
		}
		meridsstr.deleteCharAt(meridsstr.length() - 1);

		String merchantIds = meridsstr.toString();
		// 获取分店下的分店扩展信息：
		Map<Long, Map<String, Object>> merExpandMap = getMerExpandMap(merchantIds);

		// 获取分店下的商品id及分类标签
		List<Map<String, Object>> goodIdsAndtypeTags = lbsMerchantGoodsDao.getGoodIdsAndTypeTages(merchantIds);
		Map<Long, Set<Long>> merchantGoodIdMap = new HashMap<Long, Set<Long>>(); // key:分店id； value:商品id集合
		Map<Long, Set<Long>> merchantParentTagIdMap = new HashMap<Long, Set<Long>>(); // key:分店id；value:父级分类标签ID集合
		Map<Long, Set<Long>> merchantChildTagIdMap = new HashMap<Long, Set<Long>>(); // key:分店id；value:子级分类标签ID集合
		Map<Long, Set<String>> merchantParentTagMap = new HashMap<Long, Set<String>>(); // key:分店id；value:父级分类标签集合
		Map<Long, Set<String>> merchantChildTagMap = new HashMap<Long, Set<String>>(); // key:分店id；value:子级分类标签集合
		Map<Long, Set<String>> typeTagMap = new HashMap<Long, Set<String>>(); // key:商品id；value:分类标签集合
		Map<Long, Set<Long>> typeTagIdMap = new HashMap<Long, Set<Long>>(); // key:商品id；value:分类标签ID集合

		StringBuilder goodids = new StringBuilder("");
		for (Map<String, Object> tmpTagMap : goodIdsAndtypeTags) {
			Long merchantid = (Long) tmpTagMap.get("merchantid");
			Long goodid = (Long) tmpTagMap.get("goodid");
			// 分店下商品id的集合
			Set<Long> tmpGoodIdSet = merchantGoodIdMap.get(merchantid);
			goodids.append(goodid).append(",");
			if (tmpGoodIdSet == null) {
				tmpGoodIdSet = new HashSet<Long>();
			}
			tmpGoodIdSet.add(goodid);
			merchantGoodIdMap.put(merchantid, tmpGoodIdSet);

			// 商品下的分类标签集合
			Set<String> typeTags = typeTagMap.get(goodid);
			if (typeTags == null) {
				typeTags = new HashSet<String>();
			}
			if (typeTags.size() == 0) {
				typeTags.add((String) tmpTagMap.get("parentname"));
				typeTags.add((String) tmpTagMap.get("name"));
			} else {
				if (typeTags.size() >= 1) {
					typeTags.add((String) tmpTagMap.get("name"));
				}
			}
			typeTagMap.put(goodid, typeTags);

			// 商品下的分类标签ID集合
			Set<Long> typeTagIds = typeTagIdMap.get(goodid);
			if (typeTagIds == null) {
				typeTagIds = new HashSet<Long>();
			}
			if (typeTagIds.size() == 0) {
				typeTagIds.add((Long) tmpTagMap.get("parentid"));
				typeTagIds.add((Long) tmpTagMap.get("id"));
			} else {
				if (typeTagIds.size() >= 1) {
					typeTagIds.add((Long) tmpTagMap.get("id"));
				}
			}
			typeTagIdMap.put(goodid, typeTagIds);

			// 分店下的分类标签ID集合：
			Set<Long> parentTagIdSet = merchantParentTagIdMap.get(merchantid);
			Set<Long> childTagIdSet = merchantChildTagIdMap.get(merchantid);
			Long tagParentId = (Long) tmpTagMap.get("parentid");
			Long tagChildId = (Long) tmpTagMap.get("id");
			if (parentTagIdSet == null) {
				parentTagIdSet = new HashSet<Long>();
			}
			parentTagIdSet.add(tagParentId);
			merchantParentTagIdMap.put(merchantid, parentTagIdSet);
			if (childTagIdSet == null) {
				childTagIdSet = new HashSet<Long>();
			}
			childTagIdSet.add(tagChildId);
			merchantChildTagIdMap.put(merchantid, childTagIdSet);

			// 分店下的分类标签集合：
			Set<String> parentTagSet = merchantParentTagMap.get(merchantid);
			Set<String> childTagSet = merchantChildTagMap.get(merchantid);
			String tagParentName = (String) tmpTagMap.get("parentname");
			String tagChildName = (String) tmpTagMap.get("name");
			if (parentTagSet == null) {
				parentTagSet = new HashSet<String>();
			}
			parentTagSet.add(tagParentName);
			merchantParentTagMap.put(merchantid, parentTagSet);
			if (childTagSet == null) {
				childTagSet = new HashSet<String>();
			}
			childTagSet.add(tagChildName);
			merchantChildTagMap.put(merchantid, childTagSet);
		}

		// 分店对应的商品信息
		if (goodids.length() > 1) {
			goodids.deleteCharAt(goodids.length() - 1);
		}

		List<Map<String, Object>> goodsInfoOfmersMap = lbsMerchantGoodsDao.getGoodsByGids(goodids.toString());
		// key:分店id； value:分店下的商品集合
		Map<Long, List<LbsMerchantGoodsInfo>> goodsOfMerMap = new HashMap<Long, List<LbsMerchantGoodsInfo>>();
		for (Map<String, Object> map : goodsInfoOfmersMap) {
			Long merchantid = (Long) map.get("merchantid");
			List<LbsMerchantGoodsInfo> goods = goodsOfMerMap.get(merchantid);
			if (goods == null) {
				goods = new ArrayList<LbsMerchantGoodsInfo>();
			}
			LbsMerchantGoodsInfo good = map2ObLbsGoodsInfoOfMerchant(map);

			Set<String> tagList = typeTagMap.get(good.getGoodsId());
			Set<Long> tagIdList = typeTagIdMap.get(good.getGoodsId());
			if (tagList != null && tagList.size() > 0) {
				good.setClassificationTag(StringUtils.join(tagList.toArray(), " "));
			}
			if (tagIdList != null && tagIdList.size() > 0) {
				good.setClassificationTagId(StringUtils.join(tagIdList.toArray(), " "));
			}
			goods.add(good);
			goodsOfMerMap.put(merchantid, goods);
		}

		// 获取每一个分店下的商圈标签及商圈标签ID的集合
		Map<Long, List<Long>> regionsIdsMap = new HashMap<Long, List<Long>>(); // key:分店ID；value:商圈标签ID集合
		Map<Long, List<String>> regionsMap = new HashMap<Long, List<String>>(); // key:分店ID；value:商圈标签集合
		getRegionsIdsMap(regionsIdsMap, regionsMap, merchantIds);

		for (LbsMerchantInfo info : merchantInfos) {
			Long merId = info.getId();
			// 设置分店下的商品集合
			info.setGoodsInfo(goodsOfMerMap.get(merId));

			// 设置分店的分店扩展信息
			Map<String, Object> merexpMap = merExpandMap.get(merId);
			if (merexpMap != null) {
				info.setMerWellCount((Long) merexpMap.get("merWellCount"));
				info.setMerSatisfyCount((Long) merexpMap.get("merSatisfyCount"));
				info.setMerPoorCount((Long) merexpMap.get("merPoorCount"));
			} else {
				info.setMerWellCount(0l);
				info.setMerSatisfyCount(0l);
				info.setMerPoorCount(0l);
			}

			// 设置分店下商品id的集合
			Set<Long> goodIdsSet = merchantGoodIdMap.get(merId);
			if (goodIdsSet != null && goodIdsSet.size() > 0) {
				info.setIds(StringUtils.join(goodIdsSet.toArray(), ","));
			}

			// 设置分店下的分类标签ID集合
			Set<Long> parentTagIdSet = merchantParentTagIdMap.get(merId);
			Set<Long> childTagIdSet = merchantChildTagIdMap.get(merId);
			if (parentTagIdSet == null) {
				parentTagIdSet = new HashSet<Long>();
			}
			if (childTagIdSet != null && childTagIdSet.size() > 0) {
				parentTagIdSet.addAll(childTagIdSet);
			}
			if (childTagIdSet != null && childTagIdSet.size() > 0) {
				info.setClassificationTagId(StringUtils.join(parentTagIdSet.toArray(), " "));
			} else {
				info.setClassificationTagId("");
			}

			// 设置分店下的分类标签集合
			Set<String> parentTagSet = merchantParentTagMap.get(merId);
			Set<String> childTagSet = merchantChildTagMap.get(merId);
			if (parentTagSet == null) {
				parentTagSet = new HashSet<String>();
			}
			if (childTagSet != null && childTagSet.size() > 0) {
				parentTagSet.addAll(childTagSet);
			}
			if (parentTagSet != null && parentTagSet.size() > 0) {
				info.setClassificationTag(StringUtils.join(parentTagSet.toArray(), " "));
			} else {
				info.setClassificationTag("");
			}

			// 设置分店下商圈标签的集合
			List<String> regions = regionsMap.get(merId);
			if (regions != null && regions.size() > 0) {
				info.setBusinessRegionTag(StringUtils.join(regions.toArray(), " "));
			}

			// 设置每个分店下的的商圈标签ID集合
			List<Long> regionsIds = regionsIdsMap.get(merId);
			if (regionsIds != null && regionsIds.size() > 0) {
				info.setBusinessRegionTagId(StringUtils.join(regionsIds.toArray(), " "));
			}
		}
		return merchantInfos;
	}

	private LbsMerchantGoodsInfo map2ObLbsGoodsInfoOfMerchant(Map<String, Object> map) {
		LbsMerchantGoodsInfo good = new LbsMerchantGoodsInfo();
		good.setGoodsId((Long) map.get("goodsid"));
		good.setGoodsName((String) map.get("goodsname"));
		good.setGoodsTitle((String) map.get("goods_title"));
		good.setCity((String) map.get("city"));
		good.setGoodsSourcePrice((BigDecimal) map.get("sourcePrice"));
		good.setGoodsCurrentPrice((BigDecimal) map.get("currentPrice"));
		good.setGoodsDividePrice((BigDecimal) map.get("dividePrice"));
		good.setGoodsRebatePrice((BigDecimal) map.get("rebatePrice"));
		good.setGoodsLogo((String) map.get("logo1"));
		good.setGoodsLogo2((String) map.get("logo2"));
		good.setGoodsLogo3((String) map.get("logo3"));
		good.setGoodsLogo4((String) map.get("logo4"));
		good.setMaxCount((Long) map.get("maxcount"));
		good.setGoodsSingleCount((Integer) map.get("goods_single_count"));
		good.setQpsharePic((String) map.get("qpsharepic"));
		good.setGoodsOrderLoseAbsDate((Integer) map.get("order_lose_abs_date"));
		good.setGoodsOrderLoseDate((Timestamp) map.get("order_lose_date"));
		good.setGoodsStartTime((Timestamp) map.get("startTime"));
		good.setGoodsEndTime((Timestamp) map.get("endTime"));
		good.setGoodsIsAvaliable((Integer) map.get("isavaliable"));
		good.setGoodsIsTop((String) map.get("isTop"));
		good.setKindlyWarnings((String) map.get("kindlywarnings"));
		good.setGoodsIsRefund((Integer) map.get("isRefund"));
		good.setCouponCash((String) map.get("couponcash"));
		good.setGoodsIsAdvance((Integer) map.get("isadvance"));
		good.setGoodsScheduled((String) map.get("is_scheduled"));
		good.setSalesCount((Integer) map.get("sales_count"));
		good.setDetailPageurl((String) map.get("detailpageurl"));
		good.setWellCount((Long) map.get("well_count"));
		good.setSatisfyCount((Long) map.get("satisfy_count"));
		good.setPoorCount((Long) map.get("poor_count"));
		return good;
	}

	@Override
	public List<LbsGoodsInfo> getLbsGoodsInfo(int dataCount, Long lastMaxMerchantId, Long lastMaxGoodsId) {
		if (lastMaxMerchantId == null) {
			lastMaxMerchantId = 0L;
		}
		if (lastMaxGoodsId == null) {
			lastMaxGoodsId = 0L;
		}
		List<LbsGoodsInfo> goodInfos = lbsMerchantGoodsDao.getLbsGoodsInfo(dataCount, lastMaxMerchantId, lastMaxGoodsId);
		if (goodInfos == null || goodInfos.size() == 0) {
			return null;
		}

		Long[] goodidarray = new Long[goodInfos.size()];
		Long[] meridarray = new Long[goodInfos.size()];
		int i = 0;
		for (LbsGoodsInfo info : goodInfos) {
			goodidarray[i] = info.getGoodsId();
			meridarray[i] = info.getMerchantId();
			i++;
		}
		String goodidsstr = StringUtils.join(goodidarray, ",");
		String meridsstr = StringUtils.join(meridarray, ",");
		// 根据分店id获取所属品牌信息及所属品牌的扩展信息
		List<Map<String, Object>> mersList = lbsMerchantGoodsDao.getMerInfoByMerIds(meridsstr);
		Map<Long, Map<String, Object>> mersMap = new HashMap<Long, Map<String, Object>>();
		for (Map<String, Object> map : mersList) {
			Long key = (Long) map.get("merchantid");
			Map<String, Object> merInfo = mersMap.get(key);
			if (merInfo == null) {
				merInfo = new HashMap<String, Object>();
			}
			merInfo.put("brandId", map.get("brandId"));
			merInfo.put("brandName", map.get("brandName"));
			merInfo.put("merchantintroduction", map.get("merchantintroduction"));
			merInfo.put("logo1", map.get("logo1"));
			merInfo.put("logo2", map.get("logo2"));
			merInfo.put("logo3", map.get("logo3"));
			merInfo.put("logo4", map.get("logo4"));
			merInfo.put("salecount", map.get("salecount"));
			merInfo.put("evascores", map.get("evascores"));
			merInfo.put("wellcount", map.get("wellcount"));
			merInfo.put("satisfycount", map.get("satisfycount"));
			merInfo.put("poorcount", map.get("poorcount"));
			mersMap.put(key, merInfo);
		}

		// 获取分店下的分店扩展信息：
		Map<Long, Map<String, Object>> merExpandMap = getMerExpandMap(meridsstr); // key:分店id；value:分店扩展信息

		// 获取每个商品的分类标签集合及分类标签ID集合
		List<Map<String, Object>> typeTagList = lbsMerchantGoodsDao.getTypeTagesByGoodids(goodidsstr);
		Map<Long, List<String>> typeTag = new HashMap<Long, List<String>>(); // key:商品id； value:分类标签集合
		Map<Long, List<Long>> typeTagIdMap = new HashMap<Long, List<Long>>(); // key:商品id； value:分类标签ID集合
		for (Map map : typeTagList) {
			Long key = (Long) map.get("goodid");
			// 商品下的标签集合
			List<String> typeTags = typeTag.get(key);
			if (typeTags == null) {
				typeTags = new ArrayList<String>();
			}
			if (typeTags.size() == 0) {
				typeTags.add((String) map.get("parentname"));
				typeTags.add((String) map.get("name"));
			} else {
				if (typeTags.size() >= 1) {
					typeTags.add((String) map.get("name"));
				}
			}
			typeTag.put(key, typeTags);

			// 商品下的分类标签ID集合
			List<Long> typeTagIds = typeTagIdMap.get(key);
			if (typeTagIds == null) {
				typeTagIds = new ArrayList<Long>();
			}
			if (typeTagIds.size() == 0) {
				typeTagIds.add((Long) map.get("parentid"));
				typeTagIds.add((Long) map.get("id"));
			} else {
				if (typeTagIds.size() >= 1) {
					typeTagIds.add((Long) map.get("id"));
				}
			}
			typeTagIdMap.put(key, typeTagIds);
		}

		// 获取每一个分店下的商圈标签及商圈标签ID的集合
		Map<Long, List<Long>> regionsIdsMap = new HashMap<Long, List<Long>>(); // key:分店ID；value:商圈标签ID集合
		Map<Long, List<String>> regionsMap = new HashMap<Long, List<String>>(); // key:分店ID；value:商圈标签集合
		getRegionsIdsMap(regionsIdsMap, regionsMap, meridsstr);

		for (LbsGoodsInfo info : goodInfos) {
			// 设置每个商品的所属品牌及品牌扩展信息
			Map<String, Object> mapMer = mersMap.get(info.getMerchantId());
			if (mapMer != null) {
				info.setBrandId((Long) mapMer.get("brandId"));
				info.setBrandName((String) mapMer.get("brandName"));
				info.setMerchantIntroduction((String) mapMer.get("merchantintroduction"));
				info.setMcLogo1((String) mapMer.get("logo1"));
				info.setMcLogo2((String) mapMer.get("logo2"));
				info.setMcLogo3((String) mapMer.get("logo3"));
				info.setMcLogo4((String) mapMer.get("logo4"));
				info.setMcSaleCount((Integer) mapMer.get("salecount"));
				info.setMcScore((Long) mapMer.get("evascores"));
				info.setMcWellCount((Long) mapMer.get("wellcount"));
				info.setMcSatisfyCount((Long) mapMer.get("satisfycount"));
				info.setMcPoorCount((Long) mapMer.get("poorcount"));
			}

			// 设置每个商品的分店扩展信息
			Map<String, Object> merexpMap = merExpandMap.get(info.getMerchantId());
			if (merexpMap != null) {
				info.setMerWellCount((Long) merexpMap.get("merWellCount"));
				info.setMerSatisfyCount((Long) merexpMap.get("merSatisfyCount"));
				info.setMerPoorCount((Long) merexpMap.get("merPoorCount"));
			}

			// 设置每个商品的分类标签
			List<String> tagList = typeTag.get(info.getGoodsId());
			if (tagList != null && tagList.size() > 0) {
				Object[] tagArr = tagList.toArray();
				info.setClassificationTag(StringUtils.join(tagArr, " "));
			}

			// 设置每个商品的分类标签ID集合
			List<Long> tagIdList = typeTagIdMap.get(info.getGoodsId());
			if (tagIdList != null && tagIdList.size() > 0) {
				info.setClassificationTagId(StringUtils.join(tagIdList.toArray(), " "));
			}

			// 设置每个商品的商圈集合
			List<String> regions = regionsMap.get(info.getMerchantId());
			if (regions != null && regions.size() > 0) {
				info.setBusinessRegionTag(StringUtils.join(regions.toArray(), " "));
			}

			// 设置每个商品的商圈ID集合
			List<Long> regionsIds = regionsIdsMap.get(info.getMerchantId());
			if (regionsIds != null && regionsIds.size() > 0) {
				info.setBusinessRegionTagId(StringUtils.join(regionsIds.toArray(), " "));
			}
		}
		return goodInfos;
	}

	@Override
	public Map<String, Object> getGoodsAllCat(Map<String, Object> params) {

		Map<String, Object> catlogMap = new LinkedHashMap<String, Object>();
		Object cityid = params.get("cityid");
		Long areaid = 0L;

		if (null != cityid) {
			if (cityid instanceof Integer) {
				areaid = Long.valueOf(String.valueOf(cityid));
			} else if (cityid instanceof String) {
				areaid = Long.valueOf(String.valueOf(cityid));
			} else {
				catlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR); // 返回错误码
				return catlogMap;
			}
		} else {
			catlogMap.put("code", MobileUserException.INPUT_PARAM_ERROR); // 返回错误码
			return catlogMap;
		}

		List<Map<String, Object>> goodCatlog = propertyCatlogDao.getGoodsAllCatlogByCityId(areaid);

		if (null == goodCatlog || goodCatlog.size() == 0) {
			catlogMap.put("code", "0"); // 错误状态码
		} else {
			catlogMap.put("code", "1");
			catlogMap.put("rs", goodCatlog);
		}

		return catlogMap;
	}

	@Override
	public Map<String, Object> getRegionAllDic(Map<String, Object> params) {

		Map<String, Object> regionMap = new LinkedHashMap<String, Object>();
		Object cityid = params.get("cityid");
		Long areaid = 0L;

		if (null != cityid) {
			if (cityid instanceof Integer) {
				areaid = Long.valueOf(String.valueOf(cityid));
			} else if (cityid instanceof String) {
				areaid = Long.valueOf(String.valueOf(cityid));
			} else {
				regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR); // 返回错误码
				return regionMap;
			}
		} else {
			regionMap.put("code", MobileUserException.INPUT_PARAM_ERROR); // 返回错误码
			return regionMap;
		}

		List<Map<String, Object>> goodRegion = regionCatlogDao.getGoodsAllRegionByCityId(areaid);

		if (null == goodRegion || goodRegion.size() == 0) {
			regionMap.put("code", "0"); // 错误状态码
		} else {
			regionMap.put("code", "1");
			regionMap.put("rs", goodRegion);
		}

		return regionMap;
	}

	@Override
	public Map<String, Object> oldUserloginWithOpenID(Map<String, Object> params) {

		Map<String, Object> oldUserLoginMap = new HashMap<String, Object>();

		String openid = (String) params.get("openid");
		String pr = (String) params.get("pr");

		if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(pr)) {
			oldUserLoginMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return oldUserLoginMap;
		}

		openid = cryptUtil.decryptDes(openid, CryptKey);

		if (StringUtils.isEmpty(openid)) {
			oldUserLoginMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return oldUserLoginMap;
		}

		ProfileType trdType = null;
		// 第三方来源
		if ("sina".equalsIgnoreCase(pr)) {
			trdType = ProfileType.SINACONFIG;
		} else if ("renren".equalsIgnoreCase(pr)) {
			trdType = ProfileType.XIAONEICONFIG;
		} else if ("tencent".equalsIgnoreCase(pr)) {
			trdType = ProfileType.TENCENTCONFIG;
		} else if ("baidu".equalsIgnoreCase(pr)) {
			trdType = ProfileType.BAIDUCONFIG;
		} else if ("qq".equalsIgnoreCase(pr)) {
			trdType = ProfileType.QQCONFIG;
		}

		if (trdType == null) {
			oldUserLoginMap.put("code", MobileUserException.USER_INVALID_THIRDPART);
			return oldUserLoginMap;
		} else {
			long userid = weiboDao.getWeiboUserIdByProType(openid, trdType);
			if (userid > 0L) { // 该用户已经注册绑定
				oldUserLoginMap.put("code", "1");
				oldUserLoginMap.put("userid", userid);
			} else {
				oldUserLoginMap.put("code", "0");
			}
		}

		return oldUserLoginMap;
	}

	@Override
	public Map<String, Object> oldUserBindOpenId(Map<String, Object> params) {

		Map<String, Object> oldUserBindMap = new HashMap<String, Object>();

		try {
			String openid = (String) params.get("openid");
			String name = (String) params.get("name");
			String pr = (String) params.get("pr");
			String email = (String) params.get("email");
			String password = (String) params.get("password");

			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(pr) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)
					|| StringUtils.isEmpty(name)) {

				oldUserBindMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return oldUserBindMap;
			}

			openid = cryptUtil.decryptDes(openid, CryptKey);
			name = cryptUtil.decryptDes(name, CryptKey);
			email = cryptUtil.decryptDes(email, CryptKey);
			password = cryptUtil.decryptDes(password, CryptKey);

			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(name) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {

				oldUserBindMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
				return oldUserBindMap;
			}

			// true:手机号 false:邮箱
			boolean ismobile = MobilePurseSecurityUtils.isJointMobileNumber(email);

			ProfileType trdType = null;
			// 第三方来源
			if ("sina".equalsIgnoreCase(pr)) {
				trdType = ProfileType.SINACONFIG;
			} else if ("renren".equalsIgnoreCase(pr)) {
				trdType = ProfileType.XIAONEICONFIG;
			} else if ("tencent".equalsIgnoreCase(pr)) {
				trdType = ProfileType.TENCENTCONFIG;
			} else if ("baidu".equalsIgnoreCase(pr)) {
				trdType = ProfileType.BAIDUCONFIG;
			} else if ("qq".equalsIgnoreCase(pr)) {
				trdType = ProfileType.QQCONFIG;
			}

			if (null == trdType) {
				oldUserBindMap.put("code", MobileUserException.USER_INVALID_THIRDPART);
				return oldUserBindMap;
			} else {
				Map<String, String> paramMap = paseParamMap(openid, name, trdType);
				User user = null;
				Long userid = 0L;

				try {
					if (ismobile) {
						user = userService.isUserLogin(email, password, null);
					} else {
						user = userService.isUserLogin(null, password, email);
					}
				} catch (Exception e) {
					e.printStackTrace();
					oldUserBindMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
					return oldUserBindMap;
				}

				userid = user.getId();
				// 检查第三方账号是否已绑定千品账号
				Long oldUserId = weiboDao.getWeiboUserIdByProType(openid, trdType);
				// 已经绑定过第三方帐号
				if (null != oldUserId && oldUserId > 0) {
					// 绑定的不是同一个帐号
					if (oldUserId.longValue() != userid.longValue()) {
						// 删除之前绑定的帐号
						weiboDao.removeBindingAccessTokenByWeiboId(openid, trdType);
						// 是否绑定过其它帐号
						String oldOpenId = getOldOpenId(weiboDao.getWeiboProType(userid, trdType), trdType);
						// 未绑定过其它帐号
						if (StringUtils.isEmpty(oldOpenId)) {
							// 增加绑定帐号
							weiboDao.addWeiboProType(paramMap, userid, trdType);
						} else {
							// 已经绑定过其它帐号，更新帐号信息
							weiboDao.updateWeiboProType(paramMap, userid, trdType);
						}
					}
				} else {// 未绑定过千品用户
						// 检查当前千品用户是否绑定该第三方其他账号
					String oldOpenId = getOldOpenId(weiboDao.getWeiboProType(userid, trdType), trdType);
					if (StringUtils.isEmpty(oldOpenId)) {
						// 未绑定过增加
						weiboDao.addWeiboProType(paramMap, userid, trdType);
					} else {
						// 绑定过更新
						weiboDao.updateWeiboProType(paramMap, userid, trdType);
					}
				}
				// 设置登录状态
				String uuid = UUID.randomUUID().toString();
				if (user != null) {
					SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
				}
				oldUserBindMap.put("code", "1");
				oldUserBindMap.put("uuid", uuid);
				String tel = user.getMobile();
				if (tel == null) {
					tel = "";
				}
				oldUserBindMap.put("tel", cryptUtil.cryptDes(tel, CryptKey));
			}
		} catch (Exception e) {
			e.printStackTrace();
			oldUserBindMap.put("code", "0");
			return oldUserBindMap;
		}
		return oldUserBindMap;
	}

	@Override
	public Map<String, Object> newUserBindOpenIdReg(Map<String, Object> params) {

		Map<String, Object> newUserRegMap = new HashMap<String, Object>();

		try {
			String openid = (String) params.get("openid");
			String name = (String) params.get("name");
			String pr = (String) params.get("pr");

			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(pr) || StringUtils.isEmpty(name)) {
				newUserRegMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
				return newUserRegMap;
			}

			openid = cryptUtil.decryptDes(openid, CryptKey);
			name = cryptUtil.decryptDes(name, CryptKey);

			if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(name)) {
				newUserRegMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
				return newUserRegMap;
			}

			ProfileType trdType = null;
			// 第三方来源
			if ("sina".equalsIgnoreCase(pr)) {
				trdType = ProfileType.SINACONFIG;
			} else if ("renren".equalsIgnoreCase(pr)) {
				trdType = ProfileType.XIAONEICONFIG;
			} else if ("tencent".equalsIgnoreCase(pr)) {
				trdType = ProfileType.TENCENTCONFIG;
			} else if ("baidu".equalsIgnoreCase(pr)) {
				trdType = ProfileType.BAIDUCONFIG;
			} else if ("qq".equalsIgnoreCase(pr)) {
				trdType = ProfileType.QQCONFIG;
			}

			// 1.验证第三方是否存在
			Long oldUserId = weiboDao.getWeiboUserIdByProType(openid, trdType);
			if (null != oldUserId && oldUserId > 0) {
				newUserRegMap.put("code", MobileUserException.OPENID_EXIT);
				newUserRegMap.put("uuid", "");
				return newUserRegMap;
			}

			// 2.增加用户信息 (当第三方账户昵称在平台注册用户邮箱中存在,添加附加码,规则：保证唯一)
			User newUser = null;
			String uuidString = UUID.randomUUID().toString();
			uuidString = uuidString.substring(0, uuidString.indexOf("-"));
			name = name + "_m_" + uuidString;
			String password = RandomNumberUtils.getRandomNumbers(8); // 随机生成
			try {
				newUser = userService.addUserEmailRegist(name, password, "");
			} catch (Exception e) {
				e.printStackTrace();
				newUserRegMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
				return newUserRegMap;
			}

			// 3.加入扩展信息,激活邮件信息
			try {
				String secret = MobilePurseSecurityUtils.hmacSign(newUser.getCustomerkey(), newUser.getId() + "");
				// 查询用户属性信息
				UserProfile userProfile = userService.getProfile(newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
				// 存在用户属性信息,则更新,不存在直接添加用户属性信息
				if (userProfile == null) {
					userService.addProfile(Constant.EMAIL_REGIST_URLKEY, secret, newUser.getId(), ProfileType.USERCONFIG);
				} else {
					userProfile.setValue(secret);
					userService.updateProfile(userProfile);
				}
			} catch (Exception ex) {
				log.info("newUserBindOpenIdReg send email fail....");
				ex.printStackTrace();
			}

			Map<String, String> paramMap = paseParamMap(openid, name, trdType);
			// 新注册用户
			Long userid = newUser.getId();
			weiboDao.addWeiboProType(paramMap, userid, trdType);
			// 设置登录状态
			String uuid = UUID.randomUUID().toString();
			if (newUser != null) {
				SingletonLoginUtils.addSingletonForMobile(newUser, userService, uuid);
			}
			newUserRegMap.put("code", "1");
			newUserRegMap.put("uuid", uuid);
		} catch (Exception e) {
			e.printStackTrace();
			newUserRegMap.put("code", "0");
			newUserRegMap.put("uuid", "");
			return newUserRegMap;
		}
		return newUserRegMap;
	}

	@Override
	public Map<String, Object> newUserVerifyMobileReg(Map<String, Object> params) {

		Map<String, Object> newUserVerifyMap = new HashMap<String, Object>();
		String mobile = (String) params.get("tel");
		String password = (String) params.get("password");

		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
			newUserVerifyMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return newUserVerifyMap;
		}

		mobile = cryptUtil.decryptDes(mobile, CryptKey);
		password = cryptUtil.decryptDes(password, CryptKey);

		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
			newUserVerifyMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return newUserVerifyMap;
		}

		// 验证手机号格式
		if (!mobile.matches(TELPHONE_REGX)) {
			newUserVerifyMap.put("code", MobileUserException.USER_REGISTER_EMAIL_ERROR);
			return newUserVerifyMap;
		}

		// 验证密码格式
		if (!password.matches(PASSWORD_REGX)) {
			newUserVerifyMap.put("code", MobileUserException.USER_REGISTER_PASSWORD_ERROR);
			return newUserVerifyMap;
		}

		User user = userDao.findUserByMobile(mobile);
		// 该用户名已经存在,返回错误码
		if (null != user) {
			newUserVerifyMap.put("code", "0");
			newUserVerifyMap.put("uuid", "");
			return newUserVerifyMap;
		}

		User newUser = null;
		try {
			newUser = userService.addMobileRegister(mobile, mobile, password, "");
		} catch (Exception e) {
			e.printStackTrace();
			newUserVerifyMap.put("code", MobileUserException.USER_EXIST);
			newUserVerifyMap.put("uuid", "");
			return newUserVerifyMap;
		}

		// 加入扩展信息,激活邮件信息
		try {
			String secret = MobilePurseSecurityUtils.hmacSign(newUser.getCustomerkey(), newUser.getId() + "");
			// 查询用户属性信息
			UserProfile userProfile = userService.getProfile(newUser.getId(), Constant.EMAIL_REGIST_URLKEY);
			// 存在用户属性信息,则更新,不存在直接添加用户属性信息
			if (userProfile == null) {
				userService.addProfile(Constant.EMAIL_REGIST_URLKEY, secret, newUser.getId(), ProfileType.USERCONFIG);
			} else {
				userProfile.setValue(secret);
				userService.updateProfile(userProfile);
			}
		} catch (Exception ex) {
			log.info("newUserBindOpenIdReg send email fail....");
			ex.printStackTrace();
		}

		// 设置登录状态
		String uuid = UUID.randomUUID().toString();
		if (newUser != null) {
			SingletonLoginUtils.addSingletonForMobile(newUser, userService, uuid);
		}
		newUserVerifyMap.put("code", "1");
		newUserVerifyMap.put("uuid", uuid);
		return newUserVerifyMap;
	}

	@Override
	public Map<String, Object> oldUserVerifyMobileLogin(Map<String, Object> params) {

		Map<String, Object> oldUserVerifyMap = new HashMap<String, Object>();
		String mobile = (String) params.get("tel");
		String password = (String) params.get("password");

		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
			oldUserVerifyMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return oldUserVerifyMap;
		}

		mobile = cryptUtil.decryptDes(mobile, CryptKey);
		password = cryptUtil.decryptDes(password, CryptKey);

		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
			oldUserVerifyMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return oldUserVerifyMap;
		}

		// 验证手机号格式
		if (!mobile.matches(TELPHONE_REGX)) {
			oldUserVerifyMap.put("code", MobileUserException.USER_REGISTER_EMAIL_ERROR);
			return oldUserVerifyMap;
		}

		// 验证密码格式
		if (!password.matches(PASSWORD_REGX)) {
			oldUserVerifyMap.put("code", MobileUserException.USER_REGISTER_PASSWORD_ERROR);
			return oldUserVerifyMap;
		}

		User user = null;
		try {
			user = userService.isUserLogin(mobile, password, "");
		} catch (UserException e) {
			log.info("oldUserVerifyMobileLogin...Login...PassWord....Not Both Equal.......");// 密码不一致
			oldUserVerifyMap.put("code", MobileUserException.USER_LOGIN_PASSWORD_DIFFENT);
			return oldUserVerifyMap;
		}

		String uuid = UUID.randomUUID().toString();
		if (user != null) {
			SingletonLoginUtils.addSingletonForMobile(user, userService, uuid);
			userService.updateUserPassWord(user.getId(), password);
			oldUserVerifyMap.put("code", "1");
			oldUserVerifyMap.put("uuid", uuid);
		} else {
			oldUserVerifyMap.put("code", "0");
			oldUserVerifyMap.put("uuid", "");
		}

		return oldUserVerifyMap;
	}

	@Override
	public Map<String, Object> getMobileAuthCode(Map<String, Object> params) {

		Map<String, Object> MobileAuthMap = new HashMap<String, Object>();

		String tel = (String) params.get("tel");
		String templateName = (String) params.get("templateName");

		if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(templateName)) {
			log.info("getMobileAuthCode.....tel....or....templateName....is....NULL");
			MobileAuthMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return MobileAuthMap;
		}

		if (!tel.matches(TELPHONE_REGX)) {
			log.info("getMobileAuthCode Tel....Not......Compliance with the rules.....");
			MobileAuthMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return MobileAuthMap;
		}

		Map<String, String> map = (Map<String, String>) memCacheService.get("MOBILE_AUTH_CODE_" + tel);

		String mcode = null;
		String vcode = "";
		if (map != null) {
			String mobileValidateCode = map.get(tel);
			int fcount = 1;
			if (mobileValidateCode != null) {
				String count = mobileValidateCode.split(":")[0];
				vcode = mobileValidateCode.split(":")[1];
				fcount = Integer.parseInt(count);
				if (fcount >= 5) {
					log.info("Send Message Is TimeOut...................");
					MobileAuthMap.put("code", MobileUserException.USER_SMS_OUTTIME);
					return MobileAuthMap;
				}
				fcount++;
				mcode = fcount + ":" + vcode;
			}
		}

		// 发送短信逻辑
		int count = 1;
		String vCode = "";
		String[] str = null;

		if (mcode != null) {
			str = mcode.split(":");
			if (str != null && str.length == 2) {
				count = Integer.parseInt(str[0]);
				vCode = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
			}
		}

		SmsInfo sourceBean = null;
		String content = "";
		String template = templateName;
		String randomNumbers = "";
		if (str == null || str.length != 2) {
			randomNumbers = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
			// 剩余条数不足了。加日志看。
			log.info("++++++++++++randomNumbers:" + randomNumbers + "+++++++++++++++++");
		} else {
			randomNumbers = vCode;
		}
		// 短信参数
		Object[] param = new Object[] { randomNumbers };
		content = MessageFormat.format(template, param);
		sourceBean = new SmsInfo(tel, content, SMS_TYPE, "0");
		Map smsMap = smsService.sendSms(sourceBean);
		// 设置到session里s
		Map<String, String> sendSmsMap = new HashMap<String, String>();
		if (mcode != null) {
			sendSmsMap.put(tel, count + ":" + randomNumbers);
		} else {
			sendSmsMap.put(tel, "1:" + randomNumbers);
		}
		memCacheService.set("MOBILE_AUTH_CODE_" + tel, sendSmsMap);
		MobileAuthMap.put("code", "1");
		return MobileAuthMap;
	}

	@Override
	public Map<String, Object> checkMobileAuthCode(Map<String, Object> params) {

		Map<String, Object> mobileCheckMap = new HashMap<String, Object>();

		String auth = (String) params.get("auth");
		String tel = (String) params.get("tel");

		if (StringUtils.isEmpty(auth) || StringUtils.isEmpty(tel)) {
			log.info("checkMobileAuthCode..... auth....or...tel....is....NULL");
			mobileCheckMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return mobileCheckMap;
		}

		if (!tel.matches(TELPHONE_REGX)) {
			log.info("checkMobileAuthCode Tel....Not......Compliance with the rules.....");
			mobileCheckMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return mobileCheckMap;
		}

		Map<String, String> map = (Map<String, String>) memCacheService.get("MOBILE_AUTH_CODE_" + tel);

		if (map == null) {
			log.info("checkMobileAuthCode......map....is.....NULL");
			mobileCheckMap.put("code", MobileUserException.USER_WEBADDRESS_FAILD);
			return mobileCheckMap;
		}

		String mobileCode = map.get(tel);
		String mc = null;
		if (mobileCode != null) {
			log.info("mobileCode:" + mobileCode);
			mc = mobileCode.split(":")[1];
		}

		log.info("mc:" + mc + "--->auth:" + auth);
		if (mobileCode == null || mc == null || !mc.equals(auth) || auth == null || auth.equals("")) {
			log.info("checkMobileAuthCode.....sourceCode...is...Diffent.....authcode........");
			mobileCheckMap.put("code", MobileUserException.SMS_CODE_DIFFENT);
			return mobileCheckMap;
		}
		mobileCheckMap.put("code", "1");
		log.info("checkMobileAuthCode.....Is....Success");
		return mobileCheckMap;
	}

	@Override
	public Map<String, Object> searchGoods(SearchParamV2 param) {
		Map<String, Object> result = null;
		try {
			result = luceneSearchMobileService.searchGoods(param);
			result.put("code", 1);
		} catch (Exception e) {
			result = new HashMap<String, Object>();
			result.put("code", 0);
			e.printStackTrace();
			logger.info("APP V2 商品搜索异常");
		}
		return result;
	}

	@Override
	public Map<String, Object> searchBranch(SearchParamV2 param) {
		Map<String, Object> result = null;
		try {
			result = luceneSearchMobileService.searchBranch(param);
			result.put("code", 1);
		} catch (Exception e) {
			result = new HashMap<String, Object>();
			result.put("code", 0);
			e.printStackTrace();
			logger.info("手机分店搜索异常");
		}
		return result;
	}

	@Override
	public Map<String, Object> getCatStats(SearchParamV2 query) {
		Map<String, Object> result = null;
		try {
			result = appStatService.getAppCat(query);
			result.put("code", 1);
		} catch (Exception e) {
			result = new HashMap<String, Object>();
			result.put("code", 0);
			e.printStackTrace();
			logger.info("手机统计信息异常");
		}
		return result;
	}

	@Override
	public Map<String, Object> getAuthCodeForForgetPWD(Map<String, Object> params) {
		
		Map<String,Object> authMap = new HashMap<String,Object>();
		
		String tel = (String) params.get("tel");
		
		if(StringUtils.isBlank(tel)){
		    log.info("getAuthCodeForForgetPWD.....tel......is......NULL");
		    authMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return authMap;
		}
		
		tel = cryptUtil.decryptDes(tel,CryptKey);
		
		if(StringUtils.isBlank(tel)){
			log.info("getAuthCodeForForgetPWD....descryTel......is.....NULL");
			authMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return authMap;
		}
		
		if (!tel.matches(TELPHONE_REGX)) {
			log.info("getAuthCodeForForgetPWD Tel....Not......Compliance with the rules.....");
			authMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return authMap;
		}
		
		boolean flag = false;
		
		try{
			flag = userService.isUserExist(tel,null);
		}catch(Exception ex){
			log.info("getAuthCodeForForgetPWD......query....User...Is....Faild");
			authMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return authMap;
		}
		
		if(!flag){//该手机号不存在
			log.info("getAuthCodeForForgetPWD...tel..."+tel+"....Is...NOT....Exit...");
			authMap.put("code", MobileUserException.USER_EMAIL_TEL_NOTEXIT);
			return authMap;
		}
		
		Map<String,String> map = (Map<String,String>) memCacheService.get("MOBILE_AUTHCODE_FORGETPWD_" + tel);
		
		String mcode = null;
		String vcode = "";
		
		if(map != null){
			String mobileValidateCode = map.get(tel);
			int fcount = 1;
			if(StringUtils.isNotBlank(mobileValidateCode)){
			   String vcount = mobileValidateCode.split(":")[0];
			          vcode  = mobileValidateCode.split(":")[1];
			          fcount = Integer.parseInt(vcount);
			}
			if(fcount >= 5){
				log.info("getAuthCodeForForgetPWD Is TimeOut...................");
				authMap.put("code", MobileUserException.USER_SMS_OUTTIME);
				return authMap;
			}
			fcount++;
			mcode = fcount+":"+vcode;
		}
		
		//发送短信逻辑
		int scount = 1;
		String  scode = "";
		String  str[] = null;
		
		if(StringUtils.isNotBlank(mcode)){
			str = mcode.split(":");
			if(str != null && str.length == 2){
				scount = Integer.parseInt(str[0]);
				scode  = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);;
			}
		}
		
		Sms sms = null;
		
		try{
			sms = smsService.getSmsByTitle(Constant.MOBILE_AUTHCODE_FORGETPWD);
		}catch(Exception e){
			log.info("getAuthCodeForForgetPWD......sms.....Is.....NUll");
			authMap.put("code", MobileUserException.SMSTEMPLATE_NOT_FOUNT);
			return authMap;
		}
		
		if(sms != null){
			SmsInfo sourceBean = null;
			String content = "";
			String template = sms.getSmscontent();
			String randomNumbers = "";
			if(str == null || str.length !=2){
				randomNumbers = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
				log.info("getAuthCodeForForgetPWD+++++++++randomNumbers:" + randomNumbers + "+++++++++++++++++");// 剩余条数不足了。加日志看。
			}else{
				randomNumbers = scode;
			}
			// 短信参数
			Object[] param = new Object[] { randomNumbers };
			content = MessageFormat.format(template, param);
			sourceBean = new SmsInfo(tel, content, SMS_TYPE, "0");
			Map smsMap = smsService.sendSms(sourceBean);
			// 设置到session里
			Map<String, String> sendSmsMap = new HashMap<String, String>();
			if (mcode != null) {
				sendSmsMap.put(tel, scount + ":" + randomNumbers);
			} else {
				sendSmsMap.put(tel, "1:" + randomNumbers);
			}
			
			memCacheService.set("MOBILE_AUTHCODE_FORGETPWD_" + tel, sendSmsMap);
			authMap.put("code", "1");
			
			return authMap;
		}else{
			authMap.put("code", "0");
			return authMap;
		}
	}

	@Override
	public Map<String, Object> sendOldPwdForForgetPWD(Map<String, Object> params) {
		
		Map<String,Object> pwdMap = new HashMap<String,Object>();
		
		String tel  = (String) params.get("tel");
		String auth = (String) params.get("auth");
		
		if(StringUtils.isBlank(tel) || StringUtils.isBlank(auth)){
			log.info("sendOldPwdForForgetPWD.....tel...Or...auth...is...NULL");
			pwdMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return pwdMap;
		}
		
		tel = cryptUtil.decryptDes(tel,CryptKey);
		
		if(StringUtils.isBlank(tel)){
			log.info("sendOldPwdForForgetPWD....descryTel......is.....NULL");
			pwdMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return pwdMap;
		}
		
		if (!tel.matches(TELPHONE_REGX)) {
			log.info("sendOldPwdForForgetPWD Tel....Not......Compliance with the rules.....");
			pwdMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return pwdMap;
		}
		
		boolean flag = false;
		
		try{
			flag = userService.isUserExist(tel,null);
		}catch(Exception ex){
			log.info("sendOldPwdForForgetPWD......query....User...Is....Faild");
			pwdMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
			return pwdMap;
		}
		
		if(!flag){//该手机号不存在
			log.info("sendOldPwdForForgetPWD...tel..."+tel+"....Is...NOT....Exit...");
			pwdMap.put("code", MobileUserException.USER_EMAIL_TEL_NOTEXIT);
			return pwdMap;
		}
		
		Map<String,String> map = (Map<String,String>) memCacheService.get("MOBILE_AUTHCODE_FORGETPWD_" + tel);
		
		if(map != null){
			String mobileValidateCode = map.get(tel);
			if(StringUtils.isNotBlank(mobileValidateCode)){
				String vauth = mobileValidateCode.split(":")[1];
				if(StringUtils.isNotBlank(vauth)){
					if(!auth.equalsIgnoreCase(vauth)){
						pwdMap.put("code","0");
						return pwdMap;
					}else{
						User user = null;
						try {
							user = userService.findUserByMobile(tel);
						} catch (UserException e) {
							log.info("sendOldPwdForForgetPWD......query....User...Is....Faild");
							pwdMap.put("code", MobileUserException.USER_SYSTEM_ERROR);
							return pwdMap;
						}
						
						if(user == null){
							log.info("sendOldPwdForForgetPWD......query....User...Is....Faild");
							pwdMap.put("code", MobileUserException.USER_WEBADDRESS_FAILD);
							return pwdMap;
						}
						
						String sendCount = (String) memCacheService.get("MOBILE_SENDMESSAGE_COUNT_" + tel);
						int sendNum = 1;
						if(StringUtils.isNotBlank(sendCount)){
							sendNum = Integer.parseInt(sendCount);
						}
						
						if(sendNum > 5){
							log.info("sendOldPwdForForgetPWD....Is....TimeOut.....");
							pwdMap.put("code", MobileUserException.USER_SMS_OUTTIME);
							return pwdMap;
						}else{
							String dcpassword = MobilePurseSecurityUtils.decryption(user.getPassword(),user.getCustomerkey());
							String smsTemplate = Constant.MOBILE_OLDPWD_FORGETPWD;
							
							Sms sms = null;
							try {
								sms = smsService.getSmsByTitle(smsTemplate);
							} catch (BaseException e) {
								log.info("sendOldPwdForForgetPWD......sms.....Is.....NUll");
								pwdMap.put("code", MobileUserException.SMSTEMPLATE_NOT_FOUNT);
								return pwdMap;
							}
							
							if(sms != null){
								SmsInfo sourceBean = null;
								String content = "";
								String template = sms.getSmscontent();
								// 短信参数
								Object[] param = new Object[] { dcpassword };
								content = MessageFormat.format(template, param);
								sourceBean = new SmsInfo(tel, content, SMS_TYPE, "0");
								smsService.sendSms(sourceBean);
								memCacheService.set("MOBILE_SENDMESSAGE_COUNT_"+tel,++sendNum+"");
								pwdMap.put("code","1");
								return pwdMap;
							}else{
								pwdMap.put("code","0");
								return pwdMap;
							}
						}
					}
				}else{
					pwdMap.put("code","0");
					return pwdMap;
				}
			}else{
				pwdMap.put("code","0");
				return pwdMap;
			}
		}else{
			pwdMap.put("code","0");
			return pwdMap;
		}
	}

	@Override
	public Map<String, Object> getUserByUserId(Map<String, Object> params) {
		
		Map<String,Object> userMap = new HashMap<String,Object>();
		
		String userId = (String) params.get("userId");
		
		if(StringUtils.isBlank(userId)){
			log.info("getUserByUserId.....userId...is...NULL");
			userMap.put("code", MobileUserException.INPUT_PARAM_ERROR);
			return userMap;
		}
		
		userId = cryptUtil.decryptDes(userId,CryptKey);
		
		if(StringUtils.isBlank(userId)){
			log.info("getUserByUserId....userId......is.....NULL");
			userMap.put("code", MobileUserException.INPUT_PARAM_DECRYPT_ERROR);
			return userMap;
		}
		
		String [] uids = userId.split(",");
		List<Long> idlist = new LinkedList<Long>();
		for(String sx:uids){
			if(StringUtils.isNotBlank(sx)){
				idlist.add(Long.valueOf(sx));
			}
		}
		
		if(idlist == null || idlist.size() == 0){//userId为空，直接返回错误吗
			log.info("getUserByUserId...UserId...Is...NotActive");
			userMap.put("code",MobileUserException.USER_SYSTEM_ERROR);
			return userMap;
		} 
		
		List userlist = userService.getUserInfoByUserIds(idlist);
		
		if(userlist == null || userlist.size() == 0){
			log.info("getUserByUserId...UserId...Is...NotActive");
			userMap.put("code",MobileUserException.USER_SYSTEM_ERROR);
			return userMap;
		}
		
		userMap.put("code", "1");
		userMap.put("rs", userlist);
		
		return userMap;
	}
}
