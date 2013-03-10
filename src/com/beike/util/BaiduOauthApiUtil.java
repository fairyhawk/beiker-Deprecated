package com.beike.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.beike.form.BaiduAccessToken;
import com.beike.form.BaiduOrderParams;

public class BaiduOauthApiUtil {
	private static Log log = LogFactory.getLog(BaiduOauthApiUtil.class);

	private static PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	private static String apiToken = null; // api access token ,不依赖session_key
	private static long apiTokenExpireTime = 0; // api access token 过期时间

	public final static String HOST = "http://www.qianpin.com";

	public final static String AUTH_URL = "https://openapi.baidu.com/oauth/2.0/authorize";
	public final static String ACCESS_URL = "https://openapi.baidu.com/oauth/2.0/token";
	public final static String API_URL = "https://openapi.baidu.com/rest/2.0/";

	/**
	 * 使用Authentication Code方式获取AcessToken
	 * @param authCode
	 *            AuthCode
	 * @return 
	 *  BaiduAccessToken ,具体值见:http://dev.baidu.com/wiki/connect/index.php?title=%E4%BD%BF%E7%94%A8Authentication_Code%E8%8E%B7%E5%8F%96Access_Token
	 *  如果出错，返回null
	 */
	public static BaiduAccessToken getAccessToken(String authCode) {
		try {
			StringBuffer urlStr = new StringBuffer(ACCESS_URL);
			urlStr.append('?').append("grant_type=authorization_code")
					.append('&').append("client_id=").append(propertyUtil.getProperty("BAIDU_API_KEY")).append('&')
					.append("redirect_uri=").append(propertyUtil.getProperty("BAIDU_CALLBACK_URL")).append('&')
					.append("client_secret=").append(propertyUtil.getProperty("BAIDU_SECRET_KEY")).append('&')
					.append("code=" + authCode);

			JSONObject json = getJsonFromUrl(urlStr.toString(), null);
			return tokenJson2Map(json);
		} catch (Exception ex) {
			log.error("get AccessToken error:" + ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * 通过api和token获取当前登录的用户信息
	 * @param token
	 * @return
	 * map of uid/uname;如果出错，返回null
	 */
	public static Map<String, String> getLoginUser(String token) {
		try {
			StringBuffer urlStr = new StringBuffer(API_URL);
			urlStr.append("passport/users/getLoggedInUser").append('?')
					.append("access_token=").append(token).append('&')
					.append("timestamp=").append(new Date().getTime());

			JSONObject json = getJsonFromUrl(urlStr.toString(), null);

			if (!json.containsKey("uid")) {
				if (json.containsKey("error_code")) {
					String code = json.getString("error_code");
					String msg = json.getString("error_msg");
					log.warn("save to baidu failed. code:" + code + ",error:"
							+ msg);
				}
			} else {
				Map<String, String> userInfo = new HashMap<String, String>();
				userInfo.put("uid", json.getString("uid"));
				userInfo.put("uname", json.getString("uname"));
				userInfo.put("portrait", json.getString("portrait"));
				return userInfo;
			}
		} catch (Exception e) {
			// log it
			e.printStackTrace();
		}
		return null;// error

	}

	/**
	 * 调用百度团购导航openapi的saveOrder团购订单信息提交（在付款成功后调用）
	 * @param orderParams
	 */
	public static boolean saveOrder(BaiduOrderParams orderParams) {
		String token = getAccessToken4Api();
		if (token == null) {
			log.error("cant't get token for api,post order to baidu failed");
			return false;
		}
		try {
			// 所有get参数值需要做urlEncoding ，demo省略，建议使用post
			StringBuffer urlStr = new StringBuffer(API_URL);
			urlStr.append("hao123/saveOrder").append('?').append("access_token=").append(token).append("&format=json");
			//业务参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", orderParams.getOrder_id());//订单号，在提交方系统中唯一
			params.put("title", orderParams.getTitle());//团购商品短标题 <255 bytes 
			params.put("summary", orderParams.getSummary());//商品描述，例如： 价值186元的简单爱蛋糕（南瓜无糖） <2048bytes
			params.put("logo", orderParams.getLogo()); //团购商品图片（海报）url<255bytes 
			params.put("url", orderParams.getUrl());//团购商品url（需要和提交给百度导航的xml api中的商品地址完全一致）<255bytes 
			params.put("price", String.valueOf(orderParams.getPrice())); //商品单价 单位：分 如2100表示rmb21.00 
			params.put("goods_num", String.valueOf(orderParams.getGoods_num()));//购买数量
			params.put("sum_price", String.valueOf(orderParams.getSum_price()));//总价 单位：分 例如：300000 
			params.put("expire", String.valueOf(orderParams.getExpire()));//消费券过期时间，自Jan 1 1970 00:00:00 GMT的秒数; 0为不限制
			params.put("addr", orderParams.getAddr());//商家地址，例如：朝阳区建国路178号汇通时代广场; <1024bytes 
			params.put("uid", orderParams.getUid());//百度uid，如无tn参数，则此参数必填
			params.put("mobile", orderParams.getMobile());//用户手机号
			params.put("tn", orderParams.getTn());// 从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
			params.put("baiduid", orderParams.getBaiduid());// 从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
			params.put("bonus", String.valueOf(orderParams.getBonus())); //百度分成金额（单位：分），值为订单总价*分成比例。 
			params.put("order_time", String.valueOf(orderParams.getOrder_time()));
			params.put("order_city", String.valueOf(orderParams.getOrder_city()));
			
			/*log.info("baidu saveOrder params===" + params);
			JSONObject json = getJsonFromUrl(urlStr.toString(), params);
			String msg = json.getString("error_msg");
			if (json.containsKey("error_code")) {
				String code = json.getString("error_code");
				log.warn("================save to baidu failed, code:" + code + ",error_msg:" + msg + "====================");
				return false;
			} else {
				// sucess ,log it too
				log.info("================save to hao123 done:"+msg + "======id:" + json.getString("id") + "====================");
				return true;
			}*/
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}
	
	/**
	 * 调用百度团购导航openapi的updateExpire团购订单信息提交（在付款成功后调用）
	 * @param params 
	 * 			order_ids:订单号，在提交方系统中唯一，和创建提交的订单号一致；支持批量修改，多个使用英文逗号分隔。
	 * 			expire:消费券过期时间，自Jan 1 1970 00:00:00 GMT的秒数; 0为不限制
	 */
	public static boolean updateExpire(Map<String, String> params){
		String token = getAccessToken4Api();
		if (token == null) {
			log.error("cant't get token for api,post order to baidu failed");
			return false;
		}
		
		try {
			//使用post
			StringBuffer urlStr = new StringBuffer(API_URL);
			urlStr.append("hao123/updateExpire").append('?').append("access_token=").append(token).append("&format=json");
			//业务参数
			//Map<String, String> params = new HashMap<String, String>();
			//params.put("order_ids", "123456");//订单号，在提交方系统中唯一，和创建提交的订单号一致；支持批量修改，多个使用英文逗号分隔。
			//params.put("expire", "123456");//消费券过期时间，自Jan 1 1970 00:00:00 GMT的秒数; 0为不限制 
			
			JSONObject json = getJsonFromUrl(urlStr.toString(), params);
			String msg = json.getString("error_msg");
			if (json.containsKey("error_code")) {
				String code = json.getString("error_code");
				log.warn("save to baidu failed, code:" + code + ",error_msg:" + msg);
				return false;
			} else {
				// sucess ,log it too
				log.info("save to hao123 done:"+msg);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}
	
	/**
	 * 调用百度团购导航openapi的useOrder标记消费券消为已用（在用户消费后调用） 
	 * @param params 
	 * 			order_ids:订单号，在提交方系统中唯一，和创建提交的订单号一致。
	 * 			expire:消费券使用时间，自Jan 1 1970 00:00:00 GMT的秒数。
	 */
	public static boolean useOrder(Map<String, String> params){
		String token = getAccessToken4Api();
		if (token == null) {
			log.error("cant't get token for api,post order to baidu failed");
			return false;
		}
		
		try {
			//使用post
			StringBuffer urlStr = new StringBuffer(API_URL);
			urlStr.append("hao123/useOrder").append('?').append("access_token=").append(token).append("&format=json");
			//业务参数
			//Map<String, String> params = new HashMap<String, String>();
			//params.put("order_id", "123456");//订单号，在提交方系统中唯一，和创建提交的订单号一致。
			//params.put("used_time", "123456");//消费券使用时间，自Jan 1 1970 00:00:00 GMT的秒数。
			
			JSONObject json = getJsonFromUrl(urlStr.toString(), params);
			String msg = json.getString("error_msg");
			if (json.containsKey("error_code")) {
				String code = json.getString("error_code");
				log.warn("save to baidu failed, code:" + code + ",error_msg:" + msg);
				return false;
			} else {
				// sucess ,log it too
				log.info("save to hao123 done:"+msg);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}
	
	/**
	 * 以Client Credentials的方式获取AccessToken，并进行缓存，不依赖session，
	 * 适合所有异步调用openapi的情况（应用需要审核通过才能使用此种方式）
	 * @return 
	 * AccessToken or null
	 */
	public static String getAccessToken4Api() {
		long now = new Date().getTime();
		if (apiToken != null && apiTokenExpireTime > now) {
			return apiToken;
		}
		try {
			StringBuffer urlStr = new StringBuffer(ACCESS_URL);
			urlStr.append('?').append("grant_type=client_credentials")
					.append('&').append("client_id=").append(propertyUtil.getProperty("BAIDU_API_KEY")).append('&')
					.append("client_secret=").append(propertyUtil.getProperty("BAIDU_SECRET_KEY"));

			JSONObject json = getJsonFromUrl(urlStr.toString(), null);
			if (json.containsKey("error_code")) {
				String code = json.getString("error_code");
				String msg = json.getString("error_msg");
				log.warn("save to baidu failed. code:" + code + ",error_msg:" + msg);
			}

			BaiduAccessToken accessToken = tokenJson2Map(json);
			apiToken = accessToken.getAccess_token();
			apiTokenExpireTime = now + 24 * 60 * 60 * 1000;
			return apiToken;
		} catch (Exception ex) {
			log.error("get api token from baidu error:" + ex.getMessage());
			return null;
		}
	}
	
	/**
	 * 将百度返回的token json信息转换成Map格式
	 * @param json
	 * @return
	 */
	private static BaiduAccessToken tokenJson2Map(JSONObject json) {
		if (!json.containsKey("access_token")) {
			log.warn("get token from baidu faile");
			if (json.containsKey("error_code")) {
				String code = json.getString("error_code");
				String msg = json.getString("error_msg");
				log.warn("save to baidu failed. code:" + code + ",error_msg:" + msg);
			}else if (json.containsKey("error")) {
				String code = json.getString("error");
				String msg = json.getString("error_description");
				log.warn("save to baidu failed. error:" + code + ",error_description:" + msg);
			}
			return null;
		}
		BaiduAccessToken accessToken = new BaiduAccessToken();
		accessToken.setAccess_token(json.getString("access_token"));
		accessToken.setExpires_in(json.getInt("expires_in"));
		accessToken.setSession_key(json.getString("session_key"));
		accessToken.setSession_secret(json.getString("session_secret"));
		
		return accessToken;
	}

	/**
	 * 从特定的url中获取json
	 * @param urlStr
	 * @param params
	 * @return
	 * json object ,or null if failed
	 */
	private static JSONObject getJsonFromUrl(String urlStr,
			Map<String, String> params) {
		JSONObject json = null;
		InputStream is = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlStr);
			if (params != null) {
				Iterator<String> keys = params.keySet().iterator();
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				while (keys.hasNext()) {
					String key = keys.next();
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			byte[] bytes = new byte[256];
			StringBuffer sb = new StringBuffer();
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes));
				bytes = new byte[256];
			}
			json = JSONObject.fromObject(sb.toString());
		} catch (Exception e) {
			log.error("http client execute error:" + e.getMessage(), e);
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}

	/**
	 * @return
	 */
	public static String getOAuthzUrl() {
		return AUTH_URL + "?response_type=code" + "&client_id=" + propertyUtil.getProperty("BAIDU_API_KEY")  + "&redirect_uri=" + propertyUtil.getProperty("BAIDU_CALLBACK_URL");
	}
	
	public static String getBaiduApiKey(){
		return propertyUtil.getProperty("BAIDU_API_KEY");
	}
}
