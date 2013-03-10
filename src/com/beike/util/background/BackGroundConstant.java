package com.beike.util.background;

import java.util.HashMap;
import java.util.Map;

public class BackGroundConstant {

	public static Map<String,String> GoodsStatusMap = new HashMap<String,String>();
	public static Map<String,String> CouponStatusMap = new HashMap<String,String>();
	static{
		GoodsStatusMap.put("0", "未上架");
		GoodsStatusMap.put("1", "已上架");
		GoodsStatusMap.put("2", "待下架");
		GoodsStatusMap.put("3", "已下架");
		
		CouponStatusMap.put("0", "未上架");
		CouponStatusMap.put("1", "已上架");
		CouponStatusMap.put("2", "待下架");
		CouponStatusMap.put("3", "已下架");
	}
	
}
