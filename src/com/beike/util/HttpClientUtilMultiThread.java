package com.beike.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: HttpClient </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Apr 25, 2011
 * @author ye.tian
 * @version 1.0
 */
public class HttpClientUtilMultiThread {
	
	private static final Log log = LogFactory.getLog(HttpClientUtilMultiThread.class);
	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private static final int HTTP_SO_TIMEOUT = 5*60*60; //超时时间，5分钟

	private static  HttpClient client = new HttpClient(connectionManager);
	
	/**
	 * Get metohdֵ
	 * @param request
	 */
	public static  String getResponseByGet(String url, NameValuePair[] params) {
		GetMethod method = new GetMethod(url);
		if(params != null) 
			method.setQueryString(params);
		String rel = "";
		try{
			client.executeMethod(method);
			BufferedInputStream is = new BufferedInputStream(method.getResponseBodyAsStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"GBK"));
			StringBuffer result = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result.append(temp);
				result.append("\n");
			}
			rel = result.toString();
			br.close();
			is.close();
			br=null;
			is=null;
			method.abort();
			method.releaseConnection();
			method = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rel;
	}
	public static  String getResponseByGet(String url, NameValuePair[] params,String charset) {
		GetMethod method = new GetMethod(url);
		if(params != null) 
			method.setQueryString(params);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");//
		String rel = "";
		try{
			client.executeMethod(method);
			BufferedInputStream is = new BufferedInputStream(method.getResponseBodyAsStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is,charset));
			StringBuffer result = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result.append(temp);
				result.append("\n");
			}
			rel = result.toString();
			br.close();
			is.close();
			br=null;
			is=null;
			method.abort();
			method.releaseConnection();
			method = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rel;
	}
	
	public static  String getResponseByPost(String url, NameValuePair[] params,String charset) {
		PostMethod method = new PostMethod(url);
		if(params != null) 
			method.setQueryString(params);
		method.setRequestBody(params);
		String rel = "";
		try{
			client.executeMethod(method);
			BufferedInputStream is = new BufferedInputStream(method.getResponseBodyAsStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is,charset));
			StringBuffer result = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				result.append(temp);
				result.append("\n");
			}
			rel = result.toString();
			br.close();
			is.close();
			br=null;
			is=null;
			method.abort();
			method.releaseConnection();
			method = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rel;
	}

	
	/**
	 * 发送post请求Http报文，并获得响应
	 * @param url url地址
	 * @param content 发送内容
	 * @param charset 编码
	 * @return
	 */
	public static String sendPostHTTP(String url,String content,String charset){
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, HTTP_SO_TIMEOUT);

		StringBuffer responseXml = new StringBuffer("");
		try {
			log.info("+++++++++++send post http ==========>>>>> url="+url+"; postContent=" + content);
			method.setRequestEntity(new StringRequestEntity(content,"text/xml", charset));
			client.executeMethod(method);
			
			InputStream is = method.getResponseBodyAsStream();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is,charset));
				String line = br.readLine();
				while(line != null){
					responseXml.append(line);
					line = br.readLine();
				}
			} catch (UnsupportedEncodingException e) {
				//字符编码转化失败
				log.error("Error:resolving http response by charset " + charset,e);
				e.printStackTrace();
			}
			
			log.info("+++++++++++response message <<<<<============:" + responseXml.toString());
		} catch (Exception e) {
			log.error("++++++++++++++send post message error", e);
			e.printStackTrace();
		} finally {
			method.abort();
			method.releaseConnection();
			method = null;
		}
		return responseXml.toString();
	}
}
