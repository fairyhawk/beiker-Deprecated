package com.beike.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.bean.trx.SAXXMLReaderAli;
/**
 *
 * <p>Title: </p>
 * <p>Description: http utils </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author LiLu
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class HttpUtils {

	private static final String URL_PARAM_CONNECT_FLAG = "&";
	private static final int SIZE = 1024 * 1024;
	private static Log log = LogFactory.getLog(HttpUtils.class);

	private HttpUtils() {
	}

	/**
	 * GET METHOD
	 * @param strUrl String
	 * @param map Map
	 * @throws IOException
	 * @return List
	 */
	@SuppressWarnings("static-access")
	public static List URLGet(String strUrl, Map map) throws IOException {
		String strtTotalURL = "";
		List result = new ArrayList();
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
		log.debug("+++++++++URLGet,http send=" + strtTotalURL);
		URL url = new URL(strtTotalURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		con.setFollowRedirects(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), SIZE);
		while (true) {
			String line = in.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}
		in.close();
		//增加日志
		log.info("++++++++URLGet,http response=" + result);
		return (result);
	}

  

	@SuppressWarnings("static-access")
	public static Map<String, String> URLGetAli(String strUrl, Map map)throws Exception {
		//log.info("URLGetAli,strUrl=" + strUrl + "Map=" + map);
		String strtTotalURL = "";
		Map<String, String> xmlMap = null;
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
//		System.out.println("strtTotalURL:" + strtTotalURL);
		log.info("+++++++++URLGetAli,http send=" + strtTotalURL);
		String lines = "";
		URL url = new URL(strtTotalURL);
		BufferedReader in = null;
		try {
			Long begin = new Date().getTime();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			Long end = new Date().getTime();
			log.info("+++++++++URLGetAli,times=" + (end - begin));
			con.setUseCaches(false);
			con.setFollowRedirects(true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()), SIZE);

			xmlMap = new HashMap<String, String>();

			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else {
					lines = lines + line;
				}
			}
			log.info("+++++++++URLGetAli,http response=" + lines);
//			System.out.println(lines);
			xmlMap = SAXXMLReaderAli.parseSms(lines);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null){
				in.close();
			}
		}
		log.info(xmlMap);
		return xmlMap;
	}


	@SuppressWarnings("static-access")
	public static Map<String, String> URLGetAli(String strUrl, Map reqMap,String charset) throws Exception {
//		log.info("URLGet,strUrl=" + strUrl + "Map=" + reqMap);
		String strtTotalURL = "";
		Map<String, String> xmlMap = null;
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(reqMap);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(reqMap);
		}
//		System.out.println("strtTotalURL:" + strtTotalURL);
		log.info("+++++++++URLGetAli,http send=" + strtTotalURL);
		String lines = "";
		URL url = new URL(strtTotalURL);
		BufferedReader in = null;
		try {
			Long begin = new Date().getTime();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			Long end = new Date().getTime();
			log.info("+++++++++URLGetAli,times=" + (end - begin));
			con.setUseCaches(false);
			con.setFollowRedirects(true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream(),charset), SIZE);

			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else {
					lines = lines + line;
				}
			}

//			System.out.println(lines);
			log.info("+++++++++URLGetAli,http response=" + lines);
			xmlMap = XmlUtils.xml2Map(lines);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null){
				in.close();
			}
		}
		log.info(xmlMap);
		return xmlMap;
	}
  
  
	/**
	 * @param strUrl  退款请求地址
	 * @param map 退款请求参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public static String refundAlipay(String strUrl, Map map) throws Exception {
//		log.info("URLGet,strUrl=" + strUrl + "Map=" + map);
		String strtTotalURL = "";
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
		log.info("+++++++++refundAlipay,http send=" + strtTotalURL);
		URL url = new URL(strtTotalURL);
		BufferedReader in = null;
		String lines = "";
		try {
			Long begin = new Date().getTime();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			Long end = new Date().getTime();
			log.info("URLGet,times=" + (end - begin));
			con.setUseCaches(false);
			con.setFollowRedirects(true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()), SIZE);

			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else {
					lines = lines + line;
				}
			}
			log.info("+++++++++refundAlipay,http response=" + lines);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null){
				in.close();
			}
		}
		return lines;
	}
  
  
	/**
	 * POST METHOD
	 * @param strUrl String
	 * @param content Map
	 * @throws IOException
	 * @return List
	 */

	public static List URLPost(String strUrl, Map map) throws IOException {

		String content = getUrlUTF(map);
		String totalURL = null;
		if (strUrl.indexOf("?") == -1) {
			totalURL = strUrl + "?" + content;
		} else {
			totalURL = strUrl + "&" + content;
		}
		log.info("+++++++++URLPost,http send=" + totalURL);
		URL url = new URL(strUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=GBK");
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
		bout.write(content);
		bout.flush();
		bout.close();
		BufferedReader bin = new BufferedReader(new InputStreamReader(con.getInputStream()), SIZE);
		List result = new ArrayList();
		while (true) {
			String line = bin.readLine();
//			System.out.println("###" + line);
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}
		log.info("+++++++++URLPost,http response=" + result);
		return (result);
	}
  
  
  /**
   * POST METHOD
   * @param urlStr String
   * @param content Map
   * @throws IOException
   * @return List
   */
  public static String URLPost(String urlStr, String content) throws IOException {
		String totalURL = "";
		if (urlStr.indexOf("?") == -1) {
			totalURL = urlStr + "?" + content;
		} else {
			totalURL = urlStr + "&" + content;
		}
		log.info(totalURL);
		URL url = new URL(urlStr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=GBK");
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
		bout.write(content);
		bout.flush();
		bout.close();
		BufferedReader bin = new BufferedReader(new InputStreamReader(con.getInputStream()), SIZE);
		StringBuilder result = new StringBuilder("");
		while (true) {
			String line = bin.readLine();
			if (line == null) {
				break;
			}
			result.append(line);
		}
		return result.toString();
	}
  
  
  /**
   * POST METHOD
   * @param urlStr String
   * @param content Map
   * @throws IOException
   * @return List
   */
  public static String URLPost(String urlStr, String content,String charset) throws IOException {
		String totalURL = "";
		if (urlStr.indexOf("?") == -1) {
			totalURL = urlStr + "?" + content;
		} else {
			totalURL = urlStr + "&" + content;
		}
		log.info(totalURL);
		URL url = new URL(urlStr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset="+charset);
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),charset));
		bout.write(content);
		bout.flush();
		bout.close();
		BufferedReader bin = new BufferedReader(new InputStreamReader(con.getInputStream(),charset), SIZE);
		StringBuilder result = new StringBuilder("");
		while (true) {
			String line = bin.readLine();
			if (line == null) {
				break;
			}
			result.append(line);
		}
		return result.toString();
	}


	/**
	 * 获得URL
	 * @param map Map
	 * @return String
	 */
	private static String getUrl(Map map) {
		if (null == map || map.keySet().size() == 0) {
			return ("");
		}
		StringBuffer url = new StringBuffer();
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = String.valueOf(i.next());
			if (map.containsKey(key)) {
				Object val = map.get(key);
				String str = val != null ? val.toString() : "";
				try {
					str = URLEncoder.encode(str, "GBK");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals(""+ strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}

	private static String getUrlUTF(Map map) {
		if (null == map || map.keySet().size() == 0) {
			return ("");
		}
		
		StringBuffer url = new StringBuffer();
		Set keys = map.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = String.valueOf(i.next());
			if (map.containsKey(key)) {
				Object val = map.get(key);
				String str = val != null ? val.toString() : "";
				url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}

	/**
	 * @desc 创建网票网的选坐地址
	 * @param strUrl
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public static String createWPWURL(String strUrl, Map map) throws IOException {
		String strtTotalURL = "";
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
		System.out.println("====="+strtTotalURL);
		log.debug("+++++++++URLGet,http send=" + strtTotalURL);
		return strtTotalURL ;
	}
}

