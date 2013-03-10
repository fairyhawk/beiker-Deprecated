package com.beike.util.hao3604j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.beike.util.Digest;

public class Tuan360Client {
	private static Log logger = LogFactory.getLog(Tuan360Client.class);
	
	public static String SEND_URL = "http://tuan.360.cn/api/deal.php";
	
    public static boolean saveToTuan360(Tuan360OrderParams tuan360Params){
    	try{
        	Map<String, String> map = new HashMap<String,String>();
        	map.put("key", Hao360.CONSUMER_KEY);
        	map.put("qid",tuan360Params.getQid());
        	map.put("order_id", tuan360Params.getOrder_id());
        	map.put("order_time", tuan360Params.getOrder_time());
        	map.put("pid", tuan360Params.getPid());
        	map.put("price", tuan360Params.getPrice());
        	map.put("number", String.valueOf(tuan360Params.getNumber()));
        	map.put("total_price", tuan360Params.getTotal_price());
        	map.put("goods_url", tuan360Params.getGoods_url());
        	map.put("title", tuan360Params.getTitle());
        	map.put("desc", tuan360Params.getDesc());
        	map.put("spend_close_time", tuan360Params.getSpend_close_time());
        	map.put("merchant_addr",tuan360Params.getMerchant_addr());
        	
        	StringBuilder bufSign = new StringBuilder();
        	bufSign.append(Hao360.CONSUMER_KEY).append("|");
        	bufSign.append(tuan360Params.getQid()).append("|");
        	bufSign.append(tuan360Params.getOrder_id()).append("|");
        	bufSign.append(tuan360Params.getOrder_time()).append("|");
        	bufSign.append(tuan360Params.getPid()).append("|");
        	bufSign.append(tuan360Params.getPrice()).append("|");
        	bufSign.append(String.valueOf(tuan360Params.getNumber())).append("|");
        	bufSign.append(tuan360Params.getTotal_price()).append("|");
        	bufSign.append(tuan360Params.getGoods_url()).append("|");
        	bufSign.append(tuan360Params.getTitle()).append("|");
        	bufSign.append(tuan360Params.getDesc()).append("|");
        	bufSign.append(tuan360Params.getSpend_close_time()).append("|");
        	bufSign.append(tuan360Params.getMerchant_addr()).append("|");
        	bufSign.append(Hao360.CONSUMER_SECRET);
        	
         	map.put("sign",Digest.signMD5(bufSign.toString(), "utf-8"));
         	
         	logger.info("tuan360 saveToTuan360 params===" + map);
         	String retValue = sendByPost(map);
         	if (retValue!=null && retValue.contains("errornum=0&msg=succ")) {
         		logger.info("================save to tuan360 done:" + retValue + "====================");
				return true;
			} else {
				logger.info("================save to tuan360 failed, code:" + retValue + "====================");
				return false;
			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}
    }

	/**
	 * 发送数据至团360,POST方式
	 * @param urlStr
	 * @param params
	 * @return
	 * 	errornum=0&msg=succ
	 *	errornum=-2&msg=paramerr
	 */
	private static String sendByPost(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		InputStream is = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SEND_URL);
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
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes));
				bytes = new byte[256];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}