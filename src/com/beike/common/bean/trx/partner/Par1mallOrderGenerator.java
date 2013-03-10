package com.beike.common.bean.trx.partner;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.beike.common.enums.trx.PartnerApiType;
import com.beike.util.Configuration;
import com.beike.util.StringUtils;
import com.yhd.openapi.client.Md5Util;

/**   
 * @title: Par1mallOrderGenerator.java
 * @package com.beike.common.bean.trx.partner
 * @description: 1号店报文转化及加解密
 * @author wangweijie  
 * @date 2012-11-14 下午03:21:14
 * @version v1.0   
 */
public class Par1mallOrderGenerator {
	public static final String CHAR_ENCODING = "UTF-8"; 		//1号店字符编码
	public static final String FORMAT_XML = "xml";			//xml格式
	public static final String XML_HEAD="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";
	public static final String FORMAT_JSON = "json";		//json格式
	public static final String ERP = "self";	//一号店 对接erp
	public static final String ERPVER = "1.0";	//一号店 对接erp版本
	public static String PARTERNO_1MALL= Configuration.getInstance().getValue("1MALL_PARTNERNO");	//一号店分销商编号
	
	public static final int YHD_VOUCHERRESEND_LIMIT = 5;	//5次
	public static final int YHD_VOUCHERRESEND_TIMES_TIMEOUT = 30*60;	//半小时过期时间
	/**
	 * String转化为1号店数据对象
	 * @param paramInfo
	 * @return
	 */
	public static Par1mallOrderParam transReqInfo(String paramInfo){
		Par1mallOrderParam param = new Par1mallOrderParam();
		Map<String,String> requestMap = getParamMap(paramInfo);
		if(requestMap!=null&&requestMap.size()>0){
			//系统级参数
			param.setCheckCode(requestMap.get("checkCode"));
			param.setMerchantId(requestMap.get("merchantId"));
			param.setSign(requestMap.get("sign"));
			param.setErp(requestMap.get("erp"));
			param.setErpVer(requestMap.get("erpVer"));
			String format = requestMap.get("format");
			param.setFormat(StringUtils.isEmpty(format)?FORMAT_JSON:format);
			param.setVer(requestMap.get("ver"));
			param.setMethod(requestMap.get("method"));
			
			//应用级参数 -- 订单申请
			param.setOrderCode(requestMap.get("orderCode"));	//订单号(订单编码)
			if(requestMap.containsKey("productId")){	//1号商城产品ID
				param.setProductId(Long.parseLong(requestMap.get("productId")));
			}
			if(requestMap.containsKey("productNum")){ //购买数量
				param.setProductNum(Integer.parseInt(requestMap.get("productNum")));
			}
			if(requestMap.containsKey("orderAmount")){ //订单金额
				param.setOrderAmount(Double.parseDouble(requestMap.get("orderAmount")));
			}
			param.setCreateTime(requestMap.get("createTime"));	//购买时间
			param.setPaidTime(requestMap.get("paidTime"));//支付确认时间
			param.setUserPhone(requestMap.get("userPhone"));//用户手机号
			if(requestMap.containsKey("productPrice")){ //产品单价
				param.setProductPrice(Double.parseDouble(requestMap.get("productPrice")));
			}
			if(requestMap.containsKey("outerGroupId")){	//合作方团购ID
				param.setOuterGroupId(Long.parseLong(requestMap.get("outerGroupId")));
			}
			//应用级参数 -- 退款申请
			param.setPartnerOrderCode(requestMap.get("partnerOrderCode"));
			if(requestMap.containsKey("refundAmount")){
				param.setRefundAmount(Double.parseDouble(requestMap.get("refundAmount")));
			}
			param.setRefundRequestTime(requestMap.get("refundRequestTime"));  //退款请求时间 格式：yyyy-MM-dd HH:mm:ss
			
			//消费券短信重新发送
			param.setVoucherCode(requestMap.get("voucherCode"));  //消费券号码
			param.setReceiveMobile(requestMap.get("receiveMobile"));  //接收方手机号码
			param.setRequestTime(requestMap.get("requestTime"));  //请求时间

		}
		
		return param;
	}
	
	
	/**
	 * 组装1号店响应报文
	 * @param messageData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String packageResponseMsg(Par1mallOrderParam resParam) {
		String resMsg ="";
		//json格式
		if(resParam.getFormat().equals(FORMAT_JSON)){
			
			JSONObject jsonMsg = new JSONObject();
			
			//responseMap
			JSONObject responseObj = new JSONObject();
			responseObj.put("errorCount", String.valueOf(resParam.getErrorCount()));		//errorCount
			if(null != resParam.getUpdateCount())
				responseObj.put("updateCount", String.valueOf(resParam.getUpdateCount()));		//updateCount
			if(null != resParam.getTotalCount())
				responseObj.put("totalCount", String.valueOf(resParam.getTotalCount()));		//totalCount

			String errorCode = resParam.getErrorCode();
			//错误信息列表
			if(!StringUtils.isEmpty(errorCode)){
				JSONObject errInfoList = new JSONObject();
				JSONArray errDetailInfoArray = new JSONArray();
				JSONObject errDetailInfo = new JSONObject();
				errDetailInfo.put("errorCode", errorCode);
				errDetailInfo.put("errorDes", getErrorMsgByCode(errorCode,resParam.getErrorDes()));
				errDetailInfo.put("pkInfo", StringUtils.toTrim(resParam.getPkInfo()));
				errDetailInfoArray.add(errDetailInfo);
				errInfoList.put("errDetailInfo",errDetailInfoArray);
				responseObj.put("errInfoList", errInfoList);
			}

			jsonMsg.put("response", responseObj);
			resMsg = jsonMsg.toJSONString();
		}
		
		//xml格式
		else if(resParam.getFormat().equals(FORMAT_XML)){
			StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
			xml.append("<response>");
			if(null != resParam.getErrorCount())
				xml.append("<errorCount>" + resParam.getErrorCount() + "</errorCount>");
			if(null != resParam.getUpdateCount())
				xml.append("<updateCount>" + resParam.getUpdateCount() + "</updateCount>");
			if(null != resParam.getTotalCount())
				xml.append("<totalCount>" + resParam.getTotalCount() + "</totalCount>");
			String errorCode = resParam.getErrorCode();
			if(!StringUtils.isEmpty(errorCode)){
				xml.append("<errInfoList>");
					xml.append("<errDetailInfo>");
						xml.append("<errorCode>"+errorCode+"</errorCode>");
						xml.append("<errorDes>"+getErrorMsgByCode(errorCode,resParam.getErrorDes())+"</errorDes>");
						xml.append("<pkInfo>"+StringUtils.toTrim(resParam.getPkInfo())+"</pkInfo>");
					xml.append("</errDetailInfo>");
				xml.append("</errInfoList>");
			}
			
			xml.append("</response>");
			resMsg = xml.toString();
		}
		
		return resMsg;
	}
	
	/**
	 * 获得错误码
	 * @param errorCode
	 * @param defaultDesc
	 * @return
	 */
	public static String getErrorMsgByCode(String errorCode,String defaultDesc){
		if(StringUtils.isEmpty(defaultDesc)){
			return ParErrorMsgUtil.getParErrorMsgByCode(errorCode, PartnerApiType.YHD.name());
		}
		return defaultDesc;
	}
	/**
	 * 验签
	 * 一号店请求报文，不对系统级参数进行加密
	 */
	public static boolean checkSign(Map<String,String> paramMap,String secretKey,String sign){
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		if (paramMap != null) {
			paramMap.remove("sign");
			//移除系统级参数
			paramMap.remove("checkCode");
			paramMap.remove("merchantId");
			paramMap.remove("erp");
			paramMap.remove("erpVer");
			paramMap.remove("format");
			paramMap.remove("ver");
			paramMap.remove("method");
			treeMap.putAll(paramMap);
		}
		String checkSign = Md5Util.md5Signature(treeMap, secretKey);
		return checkSign.equals(sign);
	}
	/**
	 * 1号店签名
	 * @param paramMap
	 * @param secret
	 * @return
	 */
	public static String sign(Map<String,String> paramMap,String secretKey){
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		if (paramMap != null) {
			paramMap.remove("sign");
			treeMap.putAll(paramMap);
		}

		return Md5Util.md5Signature(treeMap, secretKey);
	}
	
	/**
	 * 组装请求报文
	 * @param paramMap
	 * @param secretKey
	 * @return
	 */
	public static String packageRequest(TreeMap<String,String> paramMap,String secretKey){
		String sign = Par1mallOrderGenerator.sign(paramMap, secretKey);
		paramMap.put("sign", sign);
		
		StringBuffer request = new StringBuffer();
		for(Entry<String, String> entrySet : paramMap.entrySet()){
			request.append(entrySet.getKey()+"="+entrySet.getValue()+"&");
		}
		request.deleteCharAt(request.length()-1);
		return request.toString();
	}
	
	public static Map<String,String> getParamMap(String paramInfo){
		Map<String,String> map = new HashMap<String, String>();
		String[] paramArray = paramInfo.split("&");
		for(int i = 0;i<paramArray.length;i++){
			String[] keyValue = paramArray[i].split("=",2);
			map.put(keyValue[0], keyValue[1]);
		}
		return map;
	}
	
}
