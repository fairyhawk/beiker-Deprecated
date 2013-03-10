package com.beike.common.bean.trx.partner;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.HttpUtils;
import com.beike.util.TrxConstant;






/**
 * TAOBAO参数转换以及加解密
 * @author q1w2e3r4
 *
 */
public class ParTaobaoOrderGenerator {
	
	private final static  Log logger=LogFactory.getLog(ParTaobaoOrderGenerator.class);
	/**
	 * 转换并组装淘宝请求相关参数
	 * @param requestMap
	 * @return
	 */
	public static ParTaobaoOrderParam transReqInfo(String paramInfo){
		Map<String,String> requestMap = new HashMap<String, String>();
		requestMap = getParamMap(paramInfo);
		ParTaobaoOrderParam pop = new ParTaobaoOrderParam();
		if(requestMap!=null&&requestMap.size()>0){
			pop.setOutRequestId(requestMap.get("order_id"));
			pop.setMobile(requestMap.get("mobile"));
			pop.setNum(requestMap.get("num"));
			pop.setTaobaoSid(requestMap.get("taobao_sid"));
			pop.setGoodsId(requestMap.get("outer_iid"));
			pop.setToken(requestMap.get("token"));
			pop.setLeftNum(requestMap.get("left_num"));
			pop.setNumIid(requestMap.get("num_iid"));
			pop.setSmsTemplate(requestMap.get("sms_template"));
		}
		
		return pop;
	}
	
	
	/**
	 * 组装 淘宝请求参数
	 * 
	 * @param orderId
	 * @param voucherCode
	 * @return
	 */
	public  static Map<String, String> getRequestParamsForTaoBao(ParTaobaoOrderParam taobaoParams)
	{

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		//jsonMap.put("order_id", taobaoParams.getOrderId());
		jsonMap.put("verify_code", taobaoParams.getVerifyCodes());
		jsonMap.put("consume_num", taobaoParams.getConsumeNum());
		jsonMap.put("token", TrxConstant.PARTNER_TOKEN_TAOBAO);
		//String jsonStr = JsonUtil.getJsonStringFromMap(jsonMap);
		//String param = PartnerUtil.cryptDes(jsonStr,taobaoParams.getKeyValue());
		Map<String, String> map = new HashMap<String, String>();
		// 暂时这样写， 稍后可以放到配置文件里面
		map.put("method", taobaoParams.getMethod());
		map.put("session", "SeesionKey");
		map.put("timestamp", "2011-01-01");
		map.put("format", "json");
		map.put("app_key", "app_key");
		map.put("v", "1.0");
		map.put("sign", "sign");
		map.put("sign_method", "md5");
	//	map.put("param", param);
		

		return map;
	}
	
	
	
	/**
	 * 获取 淘宝响应结果
	 * 
	 * @param requestParamsMap
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResponseParamsForTaoBao(Map<String, String> requestParamsMap,PartnerInfo partnerInfo) throws IOException
	{
		String url = getUrlByHostNo(partnerInfo.getPartnerNo(),partnerInfo.getApiType());
		List<String> responseList = HttpUtils.URLPost(url, requestParamsMap);
		String currentResult = responseList.get(0);
		logger.info("++++++voucherCode="+requestParamsMap.get("verify_code").toString()+"+++++++currentResult"+currentResult);
		currentResult=new 	String(currentResult.getBytes("GBK"), "ISO-8859-1");
		currentResult=new 	String(currentResult.getBytes("ISO-8859-1"), "utf-8") ;
		
	//	Map<String, Object> map =  JsonUtil.getMapFromJsonString(currentResult);

	//	String vebr = map.get("vmarket_eticket_beforeconsume_response").toString();		
	//	Map<String, Object> retMap =  JsonUtil.getMapFromJsonString(vebr);
		
		//Jason转码异常替代方案.淘宝生产可能抽风.线上测试可快速切换，并解决.仅针对value值为一位的有效
 		Map<String, Object> retMap =new HashMap<String, Object>();
		int  rCodeIndex=currentResult.indexOf("ret_code");
		String rCodeValue=currentResult.substring(rCodeIndex+10, rCodeIndex+11);
		retMap.put("ret_code", rCodeValue);
		retMap.put("item_title", "");
		retMap.put("left_num", "");
		
		return retMap;
	}

	
	private static String getUrlByHostNo(String hostNo,String name){
		String url = Configuration.getInstance().getValue(hostNo+"_"+name);
		
		return url;
	}
	
	
	public static Map<String,String> getParamMap(String paramInfo){
		Map<String,String> map = new HashMap<String, String>();
		String[] paramArray = paramInfo.split("&");
		for(int i = 0;i<paramArray.length;i++){
			String key = paramArray[i].split("=")[0];
			String value = paramArray[i].split("=")[1];
			map.put(key, value);
		}
		return map;
	}
}
