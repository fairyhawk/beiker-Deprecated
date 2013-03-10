package com.beike.util.tencent;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.json.JsonUtil;
import com.beike.util.tencent.QWeiboType.PageFlag;
import com.beike.util.tencent.QWeiboType.ResultType;

public class QWeiboSyncApi {
	
	private static PropertyUtil property=PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	/**
	 * Get request token.
	 * 
	 * @param customKey
	 *            Your AppKey.
	 * @param customSecret
	 *            Your AppSecret.
	 * @return The request token.
	 */
	public String getRequestToken(String customKey, String customSecret,String callbackurl) {
		String url = "https://open.t.qq.com/cgi-bin/request_token";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		//The OAuth Call back URL(You should encode this url if it
		//contains some unreserved characters).
		oauthKey.callbackUrl =callbackurl;

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Get access token.
	 * 
	 * @param customKey
	 *            Your AppKey.
	 * @param customSecret
	 *            Your AppSecret
	 * @param requestToken
	 *            The request token.
	 * @param requestTokenSecret
	 *            The request token Secret
	 * @param verify
	 *            The verification code.
	 * @return
	 */
	public String getAccessToken(String customKey, String customSecret,
			String requestToken, String requestTokenSecrect, String verify) {

		String url = "https://open.t.qq.com/cgi-bin/access_token";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;
		oauthKey.verify = verify;

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Get home page messages.
	 * 
	 * @param customKey
	 *            Your AppKey
	 * @param customSecret
	 *            Your AppSecret
	 * @param requestToken
	 *            The access token
	 * @param requestTokenSecret
	 *            The access token secret
	 * @param format
	 *            Response format, xml or json
	 * @param pageFlag
	 *            Page number.
	 * @param nReqNum
	 *            Number of messages you want.
	 * @return Response messages based on the specified format.
	 */
	public String getHomeMsg(String customKey, String customSecret,
			String requestToken, String requestTokenSecrect, ResultType format,
			PageFlag pageFlag, int nReqNum) {

		String url = "http://open.t.qq.com/api/statuses/home_timeline";
		List<QParameter> parameters = new ArrayList<QParameter>();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;

		String strFormat = null;
		if (format == ResultType.ResultType_Xml) {
			strFormat = "xml";
		} else if (format == ResultType.ResultType_Json) {
			strFormat = "json";
		} else {
			return "";
		}

		parameters.add(new QParameter("format", strFormat));
		parameters.add(new QParameter("pageflag", String.valueOf(pageFlag
				.ordinal())));
		parameters.add(new QParameter("reqnum", String.valueOf(nReqNum)));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, "GET", oauthKey, parameters, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	/**
	 * 获得粉丝列表
	 * 
	 */
	public List<TencentUser> getFansList(int count,int startindex,String customerKey,String customerSecret,String requestToken,String requestTokenSecret,ResultType format){
		String url="http://open.t.qq.com/api/friends/fanslist";
		String httpMethod="GET";
		String strFormat=null;
		if (format == ResultType.ResultType_Xml) {
			strFormat = "xml";
		} else if (format == ResultType.ResultType_Json) {
			strFormat = "json";
		}
		
		QWeiboRequest request = new QWeiboRequest();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customerKey;
		oauthKey.customSecrect = customerSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecret;
		
		List<QParameter> parameters = new ArrayList<QParameter>();
		parameters.add(new QParameter("format", strFormat));
		parameters.add(new QParameter("reqnum", count+""));
		parameters.add(new QParameter("startindex", startindex+""));
		String res=null;
		try {
			res = request.syncRequest(url, httpMethod, oauthKey, parameters,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<TencentUser> listTencentUser=new ArrayList<TencentUser>();
		
		try {
			 JSONObject object = JsonUtil.stringToObject(res);
			 Object obj=object.get("data");
			 JSONObject dataObj=JsonUtil.stringToObject(obj.toString());
			 JSONArray jsonArray= (JSONArray) dataObj.get("info");
			 for(int i=0;i<jsonArray.length();i++){
				 Object o=jsonArray.get(i);
				 JSONObject jsonStr=JsonUtil.stringToObject(o.toString());
				 
				 TencentUser tencentUser=new TencentUser();
				 
				 String nick=(String) jsonStr.get("name");
				 String weiboname=(String) jsonStr.get("nick");
				 JSONArray jsonTweet=(JSONArray) jsonStr.get("tweet");
				 
				 for(int j=0;j<jsonTweet.length();j++){
					 Object objTweet= jsonTweet.get(j);
					 JSONObject jsonT=JsonUtil.stringToObject(objTweet.toString());
					 String id=(String) jsonT.get("id");
					 tencentUser.setUid(id);
				 }
				 tencentUser.setNickname(nick);
				 tencentUser.setName(weiboname);
				 listTencentUser.add(tencentUser);
				 
			 }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return listTencentUser;
	}
	
	/**
	 * 关注微博
	 */
	public boolean addNotice(String name,String customerKey,String customerSecret,String requestToken,String requestTokenSecret,ResultType format){
		String httpMethod="POST";
		String url="http://open.t.qq.com/api/friends/add";
		String strFormat=null;
		if (format == ResultType.ResultType_Xml) {
			strFormat = "xml";
		} else if (format == ResultType.ResultType_Json) {
			strFormat = "json";
		}
		List<QParameter> parameters = new ArrayList<QParameter>();
		QWeiboRequest request = new QWeiboRequest();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customerKey;
		oauthKey.customSecrect = customerSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecret;
		parameters.add(new QParameter("format", strFormat));
		parameters.add(new QParameter("name", name));
		String res = null;
		try {
			res = request.syncRequest(url, httpMethod, oauthKey, parameters,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			JSONObject object=JsonUtil.stringToObject(res);
			Object retobj=object.get("ret");
			if(retobj!=null&&!"".equals(retobj.toString())&&"0".equals(retobj.toString())){
				
				return true;
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * 获得用户信息
	 */
	public TencentUser getUserMsg(String customKey, String customSecret,
			String requestToken, String requestTokenSecrect,ResultType format){
		String httpMethod = "GET";
		String url="http://open.t.qq.com/api/user/info";
		TencentUser tencentUser=new TencentUser();
		String strFormat=null;
		if (format == ResultType.ResultType_Xml) {
			strFormat = "xml";
		} else if (format == ResultType.ResultType_Json) {
			strFormat = "json";
		}
		List<QParameter> parameters = new ArrayList<QParameter>();
		QWeiboRequest request = new QWeiboRequest();
		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;
		parameters.add(new QParameter("format", strFormat));
		String res = null;
		try {
			res = request.syncRequest(url, httpMethod, oauthKey, parameters,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			JSONObject object=JsonUtil.stringToObject(res);
			Object obj=object.get("data");
			JSONObject dataObj=JsonUtil.stringToObject(obj.toString());
			String nick=(String) dataObj.get("name");
			//中文名字
			String weiboName=(String) dataObj.get("nick");
			String uid=(String) dataObj.get("uid");
			String head=(String) dataObj.get("head");
//			StringBuilder sbhead=new StringBuilder("http://t2.qlogo.cn/mbloghead/");
//			sbhead.append(head.substring(head.lastIndexOf("/")+1));
//			sbhead.append("/50");
			tencentUser.setHead(head+"/50");
			tencentUser.setUid(nick);
			//英文名字
			tencentUser.setNickname(nick);
			//中文名字
			tencentUser.setName(weiboName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tencentUser;
		
	}
	

	/**
	 * Publish a Weibo message.
	 * 
	 * @param customKey
	 *            Your AppKey
	 * @param customSecret
	 *            Your AppSecret
	 * @param requestToken
	 *            The access token
	 * @param requestTokenSecrect
	 *            The access token secret
	 * @param content
	 *            The content of your message
	 * @param pic
	 *            The files of your images.
	 * @param format
	 *            Response format, xml or json(Default).
	 * @return Result info based on the specified format.
	 */
	public String publishMsg(String customKey, String customSecret,
			String requestToken, String requestTokenSecrect, String content,
			String pic, ResultType format) {

		List<QParameter> files = new ArrayList<QParameter>();
		String url = null;
		String httpMethod = "POST";

		if (pic == null || pic.trim().equals("")) {
			url = "http://open.t.qq.com/api/t/add";
		} else {
			url = "http://open.t.qq.com/api/t/add_pic";
			files.add(new QParameter("pic", pic));
		}

		OauthKey oauthKey = new OauthKey();
		oauthKey.customKey = customKey;
		oauthKey.customSecrect = customSecret;
		oauthKey.tokenKey = requestToken;
		oauthKey.tokenSecrect = requestTokenSecrect;

		List<QParameter> parameters = new ArrayList<QParameter>();

		String strFormat = null;
		if (format == ResultType.ResultType_Xml) {
			strFormat = "xml";
		} else if (format == ResultType.ResultType_Json) {
			strFormat = "json";
		} else {
			return "";
		}

		parameters.add(new QParameter("format", strFormat));
		parameters.add(new QParameter("content", content));
		
		parameters.add(new QParameter("clientip", "127.0.0.1"));

		QWeiboRequest request = new QWeiboRequest();
		String res = null;
		try {
			res = request.syncRequest(url, httpMethod, oauthKey, parameters,
					files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
