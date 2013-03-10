/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.beike.util.hao3604j;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;
import com.beike.util.hao3604j.http.AccessToken;
import com.beike.util.hao3604j.http.HttpClient;
import com.beike.util.hao3604j.http.PostParameter;
import com.beike.util.hao3604j.http.RequestToken;
import com.beike.util.hao3604j.http.Response;

public class Hao360 extends Hao360Support implements java.io.Serializable {
	private static final PropertyUtil property = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	public static final String CONSUMER_KEY = property.getProperty("TUAN360_APP_KEY");
	public static final String CONSUMER_SECRET = property.getProperty("TUAN360_APP_SECRET");
	public static final String CALLBACK_URL = property.getProperty("TUAN360_APP_CALLBACK_URL");
	
	private String baseURL = Configuration.getScheme() + "api.hao.360.cn/";
    private String searchBaseURL = Configuration.getScheme() + "api.hao.360.cn/";
    private static final long serialVersionUID = -1486360080128882436L;

    //private SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public Hao360() {
        super();
        //format.setTimeZone(TimeZone.getTimeZone("GMT"));

        http.setRequestTokenURL(Configuration.getScheme() + "api.hao.360.cn/oauth/request_token.php");
        http.setAuthorizationURL(Configuration.getScheme() + "api.hao.360.cn/oauth/authorize.php");
        http.setAccessTokenURL(Configuration.getScheme() + "api.hao.360.cn/oauth/access_token.php");
    }

    /**
     * 设置token信息
     * @param token
     * @param tokenSecret
     */
    public void setToken(String token, String tokenSecret) {
        http.setToken(token, tokenSecret);
    }

    public Hao360(String baseURL) {
        this();
        this.baseURL = baseURL;
    }

    public Hao360(String id, String password) {
        this();
        setUserId(id);
        setPassword(password);
    }

    public Hao360(String id, String password, String baseURL) {
        this();
        setUserId(id);
        setPassword(password);
        this.baseURL = baseURL;
    }

    /**
     * Sets the base URL
     *
     * @param baseURL String the base URL
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Returns the base URL
     *
     * @return the base URL
     */
    public String getBaseURL() {
        return this.baseURL;
    }

    /**
     * Sets the search base URL
     *
     * @param searchBaseURL the search base URL
     * @since Hao3604J 1.1.7
     */
    public void setSearchBaseURL(String searchBaseURL) {
        this.searchBaseURL = searchBaseURL;
    }

    /**
     * Returns the search base url
     * @return search base url
     * @since Hao3604J 1.1.7
     */
    public String getSearchBaseURL(){
        return this.searchBaseURL;
    }

    /**
     *
     * @param consumerKey OAuth consumer key
     * @param consumerSecret OAuth consumer secret
     * @since Hao360 2.0.0
     */
    public synchronized void setOAuthConsumer(String consumerKey, String consumerSecret){
        this.http.setOAuthConsumer(consumerKey, consumerSecret);
    }

    /**
     * Retrieves a request token
     * @return generated request token.
     * @throws Hao360Exception when Hao360 service or network is unavailable
     * @since Hao360 2.0.0
     * @see <a href="http://oauth.net/core/1.0/#auth_step1">OAuth Core 1.0 - 6.1.  Obtaining an Unauthorized Request Token</a>
     */
    public RequestToken getOAuthRequestToken() throws Hao360Exception {
        return http.getOAuthRequestToken();
    }

    public RequestToken getOAuthRequestToken(String callback_url) throws Hao360Exception {
        return http.getOauthRequestToken(callback_url);
    }

    /**
     * Retrieves an access token assosiated with the supplied request token.
     * @param requestToken the request token
     * @return access token associsted with the supplied request token.
     * @throws Hao360Exception when Hao360 service or network is unavailable, or the user has not authorized
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Hao360 2.0.0
     */
    public synchronized AccessToken getOAuthAccessToken(RequestToken requestToken) throws Hao360Exception {
        return http.getOAuthAccessToken(requestToken);
    }

    /**
     * Retrieves an access token assosiated with the supplied request token and sets userId.
     * @param requestToken the request token
     * @param pin pin
     * @return access token associsted with the supplied request token.
     * @throws Hao360Exception when Hao360 service or network is unavailable, or the user has not authorized
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Hao360 2.0.8
     */
    public synchronized AccessToken getOAuthAccessToken(RequestToken requestToken, String pin) throws Hao360Exception {
        AccessToken accessToken = http.getOAuthAccessToken(requestToken, pin);
        setUserId(accessToken.getScreenName());
        return accessToken;
    }

    /**
     * Retrieves an access token assosiated with the supplied request token and sets userId.
     * @param token request token
     * @param tokenSecret request token secret
     * @return access token associsted with the supplied request token.
     * @throws Hao360Exception when Hao360 service or network is unavailable, or the user has not authorized
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Hao360 2.0.1
     */
    public synchronized AccessToken getOAuthAccessToken(String token, String tokenSecret) throws Hao360Exception {
        AccessToken accessToken = http.getOAuthAccessToken(token, tokenSecret);
        setUserId(accessToken.getScreenName());
        return accessToken;
    }

    /**
     * Retrieves an access token assosiated with the supplied request token.
     * @param token request token
     * @param tokenSecret request token secret
     * @param oauth_verifier oauth_verifier or pin
     * @return access token associsted with the supplied request token.
     * @throws Hao360Exception when Hao360 service or network is unavailable, or the user has not authorized
     * @see <a href="http://oauth.net/core/1.0/#auth_step2">OAuth Core 1.0 - 6.2.  Obtaining User Authorization</a>
     * @since Hao360 2.0.8
     */
    public synchronized AccessToken getOAuthAccessToken(String token
            , String tokenSecret, String oauth_verifier) throws Hao360Exception {
        return http.getOAuthAccessToken(token, tokenSecret, oauth_verifier);
    }

    /**
     * Sets the access token
     * @param accessToken accessToken
     * @since Hao360 2.0.0
     */
    public void setOAuthAccessToken(AccessToken accessToken){
        this.http.setOAuthAccessToken(accessToken);
    }

    /**
     * Sets the access token
     * @param token token
     * @param tokenSecret token secret
     * @since Hao360 2.0.0
     */
    public void setOAuthAccessToken(String token, String tokenSecret) {
        setOAuthAccessToken(new AccessToken(token, tokenSecret));
    }

    /**
     * Issues an HTTP GET request.
     *
     * @param url          the request url
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @param name1        the name of the first parameter
     * @param value1       the value of the first parameter
     * @return the response
     * @throws Hao360Exception when Hao360 service or network is unavailable
     */

    protected Response get(String url, String name1, String value1, boolean authenticate) throws Hao360Exception {
        return get(url, new PostParameter[]{new PostParameter(name1, value1)}, authenticate);
    }

    /**
     * Issues an HTTP GET request.
     *
     * @param url          the request url
     * @param name1        the name of the first parameter
     * @param value1       the value of the first parameter
     * @param name2        the name of the second parameter
     * @param value2       the value of the second parameter
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws Hao360Exception when Hao360 service or network is unavailable
     */

    protected Response get(String url, String name1, String value1, String name2, String value2, boolean authenticate) throws Hao360Exception {
        return get(url, new PostParameter[]{new PostParameter(name1, value1), new PostParameter(name2, value2)}, authenticate);
    }

    /**
     * Issues an HTTP GET request.
     *
     * @param url          the request url
     * @param params       the request parameters
     * @param authenticate if true, the request will be sent with BASIC authentication header
     * @return the response
     * @throws Hao360Exception when Hao360 service or network is unavailable
     */
    protected Response get(String url, PostParameter[] params, boolean authenticate) throws Hao360Exception {
		if (url.indexOf("?") == -1) {
			url += "?source=" + CONSUMER_KEY;
		} else if (url.indexOf("source") == -1) {
			url += "&source=" + CONSUMER_KEY;
		}
    	if (null != params && params.length > 0) {
			url += "&" + HttpClient.encodeParameters(params);
		}
        return http.get(url, authenticate);
    }
}