package com.beike.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;

import com.beike.form.AccessToken;
import com.beike.form.RenrenSessionKey;
import com.beike.form.XiaoNeiAccessToken;
import com.beike.form.XiaoneiUser;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.renren.api.client.RenrenApiClient;
import com.renren.api.client.RenrenApiConfig;

/**
 * <p>
 * Title: 人人工具类
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

public class XiaoneiUtils {

	private static PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);

	/*
	 * 传入sessionkey json串，调用api 获取用户信息接口 把人人网 用户信息封装成本地user
	 */
	public static XiaoneiUser getUserInfo(String sessionkey, String userid) {
		RenrenApiConfig.renrenApiKey = propertyUtil
				.getProperty("RENREN_API_KEY");
		RenrenApiConfig.renrenApiSecret = propertyUtil
				.getProperty("RENREN_API_SECRET");
		/*
		 * 调用JAVA SDK 传入参数sessionkey
		 */
		RenrenApiClient client = new RenrenApiClient(sessionkey);
		String fields = "id,name,sex,birthday,tinyurl,headurl,mainurl";
		/*
		 * 传入userid fields 获取用户信息 返回JSONArray 数据
		 */
		JSONArray renren_user = client.getUserService().getInfo(userid, fields);
		XiaoneiUser user = new XiaoneiUser();
		user.setHeadurl(((org.json.simple.JSONObject) renren_user.get(0)).get(
				"headurl").toString());
		user.setName(((org.json.simple.JSONObject) renren_user.get(0)).get(
		"name").toString());
		
		user.setId(Integer.valueOf(((org.json.simple.JSONObject) renren_user
		.get(0)).get("uid").toString()));
		

		return user;
	}

	public static String getOAuthzUrl() {
		StringBuffer authorizeurl = new StringBuffer();
		authorizeurl.append(Constant.RENREN_OAUTH_AUTHORIZE_URL);
		authorizeurl.append("?client_id="
				+ propertyUtil.getProperty("RENREN_API_KEY"));
		authorizeurl.append("&redirect_uri="
				+ propertyUtil.getProperty("XIAONEICONFIG_REDIRECT_URL"));
		authorizeurl.append("&response_type=code");
		return authorizeurl.toString();
	}

	public static AccessToken getAccessToken(String auth_code,
			String redirect_url) {
		// 此处用的是第三方的包，也可以用java本身的类
		// 设置请求参数，包括：client_id、client_secret、redirect_uri、code、和grant_type

		String apikey = propertyUtil.getProperty("RENREN_API_KEY");
		String secret = propertyUtil.getProperty("RENREN_API_SECRET");

		PostMethod method = new PostMethod(
				Constant.RENREN_OAUTH_ACCESS_TOKEN_URL);
		method.addParameter("client_id", apikey);
		method.addParameter("client_secret", secret);
		method.addParameter("redirect_uri", redirect_url);
		method.addParameter("grant_type", "authorization_code");
		method.addParameter("code", auth_code);

		HttpClient client = new HttpClient();
		XiaoNeiAccessToken access_token = null;
		try {
			client.executeMethod(method);
			InputStream result = method.getResponseBodyAsStream();
			JSONObject jsonObj;
			try {
				StringBuffer accessline = new StringBuffer();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						result, "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					accessline.append(line);
				}
				jsonObj = new JSONObject(accessline.toString());
				access_token = new XiaoNeiAccessToken();
				access_token.setAccess_token(jsonObj.getString("access_token"));
				access_token.setExpires_in(jsonObj.getInt("expires_in"));
				// 开发者已经向开放平台申请了生命周期是永久的AccessToken,返回的参数refresh_token不为空
				if (jsonObj.has("refresh_token")) {
					access_token.setRefresh_token(jsonObj.getString(
							"refresh_token").toString());
				}
				access_token
						.setCreate_time(DateUtils.dateToStrLong(new Date()));
				return access_token;
			} catch (JSONException e) {
				System.out.println("你的应用没有获得永久授权");
				e.printStackTrace();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_token;
	}

	public static AccessToken getAccessToken(String refresh_token) {

		String apikey = propertyUtil.getProperty("RENREN_API_KEY");
		String secret = propertyUtil.getProperty("RENREN_API_SECRET");

		PostMethod method = new PostMethod(
				Constant.RENREN_OAUTH_ACCESS_TOKEN_URL);
		method.addParameter("client_id", apikey);
		method.addParameter("client_secret", secret);
		method.addParameter("refresh_token", refresh_token);
		method.addParameter("grant_type", "authorization_code");
		HttpClient client = new HttpClient();
		XiaoNeiAccessToken access_token = null;
		try {
			client.executeMethod(method);
			InputStream result = method.getResponseBodyAsStream();
			JSONObject jsonObj;
			try {
				StringBuffer accessline = new StringBuffer();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						result, "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					accessline.append(line);
				}
				jsonObj = new JSONObject(accessline.toString());
				access_token = new XiaoNeiAccessToken();
				access_token.setAccess_token(jsonObj.getString("access_token"));
				access_token.setExpires_in(jsonObj.getInt("expires_in"));

				access_token.setRefresh_token(jsonObj
						.getString("refresh_token"));

				access_token
						.setCreate_time(DateUtils.dateToStrLong(new Date()));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_token;
	}

	public static RenrenSessionKey getXiaonei_SessionKey_info(
			String access_token) {
		RenrenSessionKey sessionkey = new RenrenSessionKey();
		PostMethod method = new PostMethod(Constant.RENREN_API_SESSIONKEY_URL);
		method.addParameter("oauth_token", access_token);
		HttpClient client = new HttpClient();
		try {
			int statusCode = client.executeMethod(method);
			InputStream result = method.getResponseBodyAsStream();
			StringBuffer sessionkeyline = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					result, "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				sessionkeyline.append(line);
			}
			// 判断当前状态码是否是200
			if (statusCode == HttpStatus.SC_OK) {
				sessionkey.setRenrenid(getRenrenUserId(sessionkeyline
						.toString()));
				sessionkey.setSessionkey(getSessionKey(sessionkeyline
						.toString()));
				return sessionkey;
			} else {// 有可能是accesstoken过期或是网络有问题
				return null;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 从json串中获取用户在人人网的id
	 */
	public static int getRenrenUserId(String obj) {
		JSONObject renren_userinfo;
		try {
			renren_userinfo = new JSONObject(obj);
			JSONObject sessionkey_obj = new JSONObject(renren_userinfo
					.getString("user"));
			return Integer.valueOf(sessionkey_obj.getString("id"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	/*
	 * 从json串中获取session_key
	 */
	public static String getSessionKey(String obj) {
		JSONObject renren_token;
		try {
			renren_token = new JSONObject(obj);
			JSONObject sessionkey_obj = new JSONObject(renren_token
					.getString("renren_token"));
			return sessionkey_obj.getString("session_key");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return "";
	}
}
