package com.beike.util.lucene;

import com.beike.util.PropertiesReader;
import com.beike.util.StringUtils;

public class URLFormat {

	
	public static String getGoodsURL(String goodsid){
		if (StringUtils.validNull(goodsid)) {
			String static_url = PropertiesReader.getValue("project",
					"STATIC_URL");
			if ("false".equals(static_url)) {
				return "goods/showGoodDetail.do?goodId=" + goodsid;
			} else {
				return "goods/" + goodsid + ".html";
			}
		}
		return "";
	}
	
	public static String getCouponURL(String coupoid){
		return "";
	}
	public static String getBrandURL(String brandid){
		return "";
	}
}
