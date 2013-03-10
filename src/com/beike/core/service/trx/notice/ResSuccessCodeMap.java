package com.beike.core.service.trx.notice;

import java.util.HashMap;
import java.util.Map;

import com.beike.util.StringUtils;

/**   
 * @title: ResSuccessCodeMap.java
 * @package com.beike.core.service.trx.notice
 * @description: 各经销商通知返回报文成功代码Map  根据分销商编号于接口类型作为Map的key，value值是分销商返回标示为成功的字符串
 * @author wangweijie  
 * @date 2012-6-20 下午03:28:58
 * @version v1.0   
 */
public class ResSuccessCodeMap {
	private static String DEFAULT = "DEFAULT";	//默认返回
	//map
	private static Map<String,String> successCodeMap = new HashMap<String,String>();
	static{
		//淘宝
		successCodeMap.put("taobao.vmarket.eticket.send", "\"ret_code\":1");		//发码成功回调接口
		successCodeMap.put("taobao.vmarket.eticket.resend", "\"ret_code\":1");		//重发前回调接口
		
		successCodeMap.put("http://tuan.360buy.com/VerifyCouponRequest", "<ResultCode>200</ResultCode>");
		//other
//		successCodeMap.put("taobao.vmarket.eticket.beforeconsume", "\"ret_code\":\"1\"");
		successCodeMap.put("yhd.group.buy.order.verify", "\"totalCount\":1");		//一号店团购订单信息确认
		successCodeMap.put("yhd.group.buy.refund.confirm", "\"totalCount\":1");	//一号店消费券退款确认

		successCodeMap.put(DEFAULT, "\"ret_code\":1");
	}
	
	
	
	
	/**
	 * 获得返回码
	 * @param hostNo
	 * @param method
	 * @return    
	 * @throws
	 */
	public static String getSuccessCode(String method){
		String key = getKey(method);
		if(successCodeMap.containsKey(key)){
			return successCodeMap.get(key);
		}
		return successCodeMap.get(DEFAULT);
	}
	
	/**
	 *设置正确的返回码
	 * @param hostNo
	 * @param method
	 * @param succCode 能标示正确的返回码信息    
	 * @throws
	 */
	public static void setSuccessCode(String method,String succCode){
		successCodeMap.put(getKey(method), succCode);
	}
	
	/**
	 * 组装key （hostNo_method）
	 * @param hostNo 分销商编号
	 * @param method
	 * @return    
	 * @throws
	 */
	private static String getKey(String method){
		if(StringUtils.isEmpty(method)) method = "DEFAULT";
		return StringUtils.toTrim(method);
	}
	
}
