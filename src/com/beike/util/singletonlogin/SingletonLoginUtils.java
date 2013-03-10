package com.beike.util.singletonlogin;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.beike.entity.user.User;
import com.beike.service.user.UserService;
import com.beike.userloginlog.model.UserLoginLog;
import com.beike.util.Constant;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.service.MUserService;

/**
 * <p>
 * Title:单例登录相关操作
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
 * @date Jun 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class SingletonLoginUtils {

	public static final int RANDOM_STRING_NUMBER = 6;

	private static final String SINGLETON_COOKIE_KEY = "SINGLETON_COOKIE_KEY";

	private static MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private static PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);

	private static String domainName = propertyUtil.getProperty("domainname");

	// 一个月cookie
	private static int validy = 60 * 60 * 24 * 30;

	/**
	 * 获得当前登录用户 返回null 时需要到库里查询
	 * 
	 * @param request
	 * @return
	 */
	public static User getMemcacheUser(HttpServletRequest request) {
		Long uid = SingletonLoginUtils.getLoginUserid(request);
		if (uid == null)
			return null;
		User user = (User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN
				+ uid);
		// if(user!=null){
		// request.getSession().setAttribute(Constant.USER_LOGIN, user);
		// }
		return user;
	}
	public static User getMemcacheMobileUser(String uuid){
		
		Long uid = SingletonLoginUtils.getMobileLoginUserId(uuid);
		
		if(uid == null)
			return null;
		
		User user = (User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN+ uid);
		
		return user;
	}

	/**
	 * 获得用户id
	 * 
	 * @param request
	 * @return
	 */
	public static Long getLoginUserid(HttpServletRequest request) {
		Long uid = null;
		try {
			String uuid = WebUtils
					.getCookieValue(SINGLETON_COOKIE_KEY, request);
			if (StringUtils.isBlank(uuid))
				return null;
			String sigletonValue = (String) memCacheService.get(uuid);
			if (!StringUtils.isBlank(sigletonValue)) {
				String userid = sigletonValue.substring(0,
						sigletonValue.indexOf("|"));
				uid = Long.parseLong(userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return uid;
	}
	public static Long getMobileLoginUserId(String uuid){
		Long uid = null;
		try{
			String sigletonValue = (String) memCacheService.get(uuid);
			if (!StringUtils.isBlank(sigletonValue)) {
				String userid = sigletonValue.substring(0,
						sigletonValue.indexOf("|"));
				uid = Long.parseLong(userid);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return uid;
	}

	/**
	 * 用户登录后使用
	 * memcache所存key为userKey，value为：uuid|当前毫秒数|6位随机数|前面字符串生成的签名(用userkey生成)
	 * cookie所存userkey 30天过期
	 * 
	 * @param userKey
	 *            user表里的customerKey
	 * @param response
	 *            HttpServletResponse
	 * @param isForever
	 *            是否永久登录
	 */
	public static void addSingleton(User user, UserService userService,
			String userid, HttpServletResponse response, boolean isForever,
			HttpServletRequest request) {
		// 将userKey放到cookie里
		// if(isForever){
		// validy=60*60*24*3000;
		// }
		// 成功 记录到memcache 根据customerKey查询User
		// 一天一超时
		int timeout = 24 * 60 * 60;
		if (user != null) {
			memCacheService.set(Constant.MEMCACHE_USER_LOGIN + userid, user,
					timeout);
		} else {
			user = (User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN
					+ userid);
			if (user == null) {
				user = userService.findById(Long.parseLong(userid));
				memCacheService.set(
						Constant.MEMCACHE_USER_LOGIN + user.getId(), user,
						timeout);
			} else {
				memCacheService.set(
						Constant.MEMCACHE_USER_LOGIN + user.getId(), user,
						timeout);
			}
		}

		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();

		// UUID
		Cookie cookie = WebUtils.cookie(SINGLETON_COOKIE_KEY, uuidStr, validy);
		response.addCookie(cookie);
		// CurrentTime
		long currentTime = System.currentTimeMillis();
		// 组装Memcache 里存放数据
		StringBuilder sb = new StringBuilder();
		sb.append(userid);
		sb.append("|");
		sb.append(currentTime);
		sb.append("|");
		String hmac = MobilePurseSecurityUtils.hmacSign(sb.toString(), userid);
		sb.append(hmac);
		// 设置memcache
		memCacheService.set(uuidStr, sb.toString());
		// request.getSession().getServletContext().setAttribute(uuidStr,
		// sb.toString());
		
	}

	public static void addSingletonForMobile(User user, UserService userService,String uuid){
		
		int timeout = 24 * 60 * 60;
		Long userid = user.getId();
		if (user != null) {
			memCacheService.set(Constant.MEMCACHE_USER_LOGIN + userid, user, timeout);
		} else {
			user = (User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN+userid);
			if (user == null) {
				user = userService.findById(userid);
				memCacheService.set(Constant.MEMCACHE_USER_LOGIN + userid, user,timeout);
			} else {
				memCacheService.set(Constant.MEMCACHE_USER_LOGIN + userid, user,timeout);
			}
		}

		long currentTime = System.currentTimeMillis();
		// 组装Memcache 里存放数据
		StringBuilder sb = new StringBuilder();
		sb.append(userid);
		sb.append("|");
		sb.append(currentTime);
		sb.append("|");
		String hmac = MobilePurseSecurityUtils.hmacSign(sb.toString(),String.valueOf(userid));
		sb.append(hmac);
		// 设置memcache
		memCacheService.set(uuid,sb.toString());
	}
	/**
	 * wap登录后使用
	 * memcache所存key为userKey，value为：uuid|当前毫秒数|6位随机数|前面字符串生成的签名(用userkey生成)
	 * cookie所存userkey 30天过期
	 * 
	 * @param userKey
	 *            user表里的customerKey
	 * @param response
	 *            HttpServletResponse
	 * @param isForever
	 *            是否永久登录
	 */
	public static void addMSingleton(User user, MUserService mUserService,
			String userid, HttpServletResponse response, boolean isForever,
			HttpServletRequest request, int vTime) {
		// 将userKey放到cookie里
		// 成功 记录到memcache 根据customerKey查询User
		// 一天一超时
		if (user != null) {
			memCacheService.set(Constant.MEMCACHE_USER_LOGIN + userid, user,
					vTime);
		} else {
			user = (User) memCacheService.get(Constant.MEMCACHE_USER_LOGIN
					+ userid);
			if (user == null) {
				user = mUserService.findById(Long.parseLong(userid));
				memCacheService.set(
						Constant.MEMCACHE_USER_LOGIN + user.getId(), user,
						vTime);
			} else {
				memCacheService.set(
						Constant.MEMCACHE_USER_LOGIN + user.getId(), user,
						vTime);
			}
		}

		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();

		// UUID
		WebUtils.setMCookieByKey(response, SINGLETON_COOKIE_KEY, uuidStr, vTime);
		// CurrentTime
		long currentTime = System.currentTimeMillis();
		// 组装Memcache 里存放数据
		StringBuilder sb = new StringBuilder();
		sb.append(userid);
		sb.append("|");
		sb.append(currentTime);
		sb.append("|");
		String hmac = MobilePurseSecurityUtils.hmacSign(sb.toString(), userid);
		sb.append(hmac);
		// 设置memcache
		memCacheService.set(uuidStr, sb.toString());
	}

	/**
	 * 清除cookie、memcache
	 * 
	 * @param userKey
	 * @param response
	 */
	public static void removeSingleton(HttpServletResponse response,
			HttpServletRequest request) {
		String userKey = WebUtils.getCookieValue(SINGLETON_COOKIE_KEY, request);

		if (userKey != null) {
			memCacheService.remove(userKey);
			WebUtils.removeMCookieByKey(response, SINGLETON_COOKIE_KEY);
			// Cookie cookie = WebUtils.removeableCookie(SINGLETON_COOKIE_KEY,
			// ".qianpin.com");
			// response.addCookie(cookie);
		}
		User user = getMemcacheUser(request);
		if (user != null) {
			memCacheService.remove(Constant.MEMCACHE_USER_LOGIN + user.getId());
		}
		// if(userKey!=null){
		// memCacheService.remove(userKey);
		// }

	}

}
