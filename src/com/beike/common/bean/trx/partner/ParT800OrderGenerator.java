package com.beike.common.bean.trx.partner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.HttpUtils;
import com.beike.util.img.JsonUtil;

/**
 * Title : Par800OrderGenerator.java <br/>
 * Description : 团800参数转换以及加解密<br/>
 * Company : Sinobo <br/>
 * Copyright : Copyright (c) 2010-2012 All rights reserved.<br/>
 * Created : 2012-11-6 下午3:46:11 <br/>
 * 
 * @author Wenzhong Gu
 * @version 1.0
 */
public class ParT800OrderGenerator {

	private final static Log logger = LogFactory.getLog(ParT800OrderGenerator.class);

	/**
	 * 转换并组装团800请求相关参数
	 * 
	 * @param parmInfo
	 * @return
	 */
	public static ParT800OrderParam transReqInfo(String paramInfo) {
		ParT800OrderParam pop = new ParT800OrderParam();
		if (paramInfo != null && !"".equals(paramInfo)) {
			Map<String, Object> map = getParamMap(paramInfo);

			if (map.get("tuan_order_no") != null) {
				// pop.setExternalId(map.get("tuan_order_no").toString());//团800订单号
				pop.setOutRequestId(map.get("tuan_order_no").toString());// 团800订单号
			}
			if (map.get("site_order_no") != null) {
				pop.setOrderId(map.get("site_order_no").toString()); // 千品订单号
			}
			if (map.get("deal_id") != null) {
				pop.setGoodsId(map.get("deal_id").toString()); // 平台goodsId
			}
			if (map.get("count") != null) {
				pop.setProdCount(map.get("count").toString()); // 商品数量
			}
			if(map.get("total_price")!=null){
				pop.setTotalPrice(map.get("total_price").toString());//总价
			}
			if (map.get("price") != null) {
				pop.setPayPrice(map.get("price").toString()); // 商品的支付金额，单价
			}
			if (map.get("pay_time") != null) {
				pop.setPayDate(map.get("pay_time").toString()); // 支付时间
			}
			if (map.get("sender") != null) {
				pop.setSender(map.get("sender").toString()); // 发货方，主要是由谁来发短信
			}
			if (map.get("sms_template") != null) {
				pop.setSmsTemplate(map.get("sms_template").toString()); // 短信模板
			}
			if (map.get("phone_num") != null) {
				pop.setMobile(map.get("phone_num").toString()); // 手机号
			}
			if (map.get("coupons") != null) {
				pop.setVoucherCode(map.get("coupons").toString());
			}
		}
		return pop;
	}

	public static Map<String, Object> getParamMap(String paramInfo) {
		Map<String, Object> jsonMap = JsonUtil.getMapFromJsonString(paramInfo);
		return jsonMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResponseParamsForT800(Map<String, Object> requestParamsMap, PartnerInfo partnerInfo) throws IOException {

		String url = getUrlByHostNo(partnerInfo.getPartnerNo(), partnerInfo.getApiType());

		List<String> responseList = HttpUtils.URLPost(url, requestParamsMap);
		String currentResult = responseList.get(0);
		logger.info("+++++++++++++currentResult" + currentResult);
		currentResult = new String(currentResult.getBytes("GBK"), "ISO-8859-1");
		currentResult = new String(currentResult.getBytes("ISO-8859-1"), "utf-8");

		Map<String, Object> retMap = JsonUtil.getMapFromJsonString(currentResult);

		return retMap;
	}

	private static String getUrlByHostNo(String hostNo, String name) {
		return Configuration.getInstance().getValue(hostNo + "_" + name);
	}
}