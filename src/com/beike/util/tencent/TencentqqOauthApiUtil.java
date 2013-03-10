package com.beike.util.tencent;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.form.TencentqqAccessToken;
import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class TencentqqOauthApiUtil {
	private static Log log = LogFactory.getLog(TencentqqOauthApiUtil.class);

	private static PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);

	public final static String AUTH_URL = "https://graph.qq.com/oauth2.0/authorize";
	public final static String ACCESS_URL = "https://graph.qq.com/oauth2.0/token";
	public final static String OPENID_URL = "https://graph.qq.com/oauth2.0/me";
	public final static String USERAPI_URL = "https://graph.qq.com/user/get_user_info";

	/**
	 * 使用Authentication Code方式获取AcessToken
	 * @param authCode
	 *            AuthCode
	 * @return 
	 *  TencentqqAccessToken
	 *  如果出错，返回null
	 */
	public static TencentqqAccessToken getAccessTokenAndOpenId(String authCode) {
		TencentqqAccessToken accessToken = null;
		try {
			//通过Authorization Code获取Access Token
			StringBuffer urlStr = new StringBuffer(ACCESS_URL);
			urlStr.append('?').append("grant_type=authorization_code")
				.append('&').append("client_id=").append(propertyUtil.getProperty("TENCENT_QQ_ID")).append('&')
				.append("client_secret=").append(propertyUtil.getProperty("TENCENT_QQ_KEY")).append('&')
				.append("code=").append(authCode).append('&')
				.append("state=").append("qianpin").append('&')
				.append("redirect_uri=").append(propertyUtil.getProperty("TENCENT_QQ_CALLBACK_URL"));
			
			String acessToken = null;
			acessToken = httpGet(urlStr.toString(), "");
			if(StringUtils.isNotEmpty(acessToken)){
				String[] aryAcessToken = StringUtils.split(acessToken, "&");
				if(aryAcessToken!=null && aryAcessToken.length>0){
					//使用Access Token来获取用户的OpenID
					StringBuffer openidurlStr = new StringBuffer(OPENID_URL);
					openidurlStr.append("?").append(aryAcessToken[0]);
					acessToken = httpGet(openidurlStr.toString(), "");
					if(StringUtils.isNotEmpty(acessToken)){
						int ibegin = acessToken.indexOf("(");
						int iend = acessToken.indexOf(")");
						String jsonToken = StringUtils.trim(StringUtils.substring(acessToken, ibegin+1, iend));
						
						//返回access_token、client_id、access_token，用于下一步调用api
						JSONObject json = null;
						json = JSONObject.fromObject(jsonToken.toString());
						if(json!=null){
							accessToken = new TencentqqAccessToken();
							accessToken.setAccess_token(StringUtils.replace(aryAcessToken[0], "access_token=", ""));
							accessToken.setOpenid(json.getString("openid"));
						}
					}
				}
			}
			return accessToken;
		} catch (Exception ex) {
			log.error("get AccessToken error:" + ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 使用Access Token以及OpenID来访问和修改用户数据
	 * @param token
	 * @return
	 * map of uid/uname;如果出错，返回null
	 */
	public static TencentqqAccessToken getLoginUser(TencentqqAccessToken accessToken) {
		try {
			StringBuffer urlStr = new StringBuffer(USERAPI_URL);
			urlStr.append('?')
				.append("access_token=").append(accessToken.getAccess_token()).append('&')
				.append("oauth_consumer_key=").append(propertyUtil.getProperty("TENCENT_QQ_ID")).append("&")
				.append("openid=").append(accessToken.getOpenid())
				.append("format=json");

			String userInfo = null;
			userInfo = httpGet(urlStr.toString(), "");
			JSONObject json = null;
			json = JSONObject.fromObject(userInfo.toString());
			if(json!=null){
				if(0 == json.getInt("ret")){
					accessToken.setScreenName(json.getString("nickname"));
					
					String gender = json.getString("gender");
					if("男".equals(gender)){
						accessToken.setGender(0);
					}else if("女".equals(gender)){
						accessToken.setGender(1);
					}
					if(StringUtils.isNotEmpty(json.getString("figureurl_2"))){
						accessToken.setHeadIcon(json.getString("figureurl"));
					}else if(StringUtils.isNotEmpty(json.getString("figureurl_1"))){
						accessToken.setHeadIcon(json.getString("figureurl_1"));
					}else if(StringUtils.isNotEmpty(json.getString("figureurl"))){
						accessToken.setHeadIcon(json.getString("figureurl"));
					}
				}else{
					accessToken = null;
				}
			}
			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 发送get请求
	 * @param url
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url, String queryString) throws Exception {
		String responseData = null;
		if (queryString != null && !queryString.equals("")) {
			url += "?" + queryString;
		}
		HttpClient httpClient = new HttpClient();
		GetMethod httpGet = new GetMethod(url);
		httpGet.getParams().setContentCharset("UTF8");
		httpGet.getParams().setParameter("http.socket.timeout",new Integer(200000));
		try {
			int statusCode = httpClient.executeMethod(httpGet);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("HttpGet Method failed: "
						+ httpGet.getStatusLine());
			}
			responseData = httpGet.getResponseBodyAsString();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			httpGet.releaseConnection();
			httpClient = null;
		}
		return responseData;
	}
	
	/**
	 * @return
	 */
	public static String getOAuthzUrl() {
		return AUTH_URL + "?response_type=code" + "&client_id=" + propertyUtil.getProperty("TENCENT_QQ_ID")  + "&redirect_uri=" + propertyUtil.getProperty("TENCENT_QQ_CALLBACK_URL");
	}
}