package com.beike.util.coupin;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.beike.entity.user.User;
import com.beike.util.WebUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title: 优惠券工具类
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2012-07-10  12：00：00
 * @author wenhua.cheng
 * @version 1.0
 */
public class CoupinUtil {
	
	
	public  static  boolean isTipsCoupon(HttpServletRequest request){
		boolean  isTipsCoupon=false;
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if(user!=null){
			
		String 	couponCoo = WebUtils.getCookieValue("NEW_COUPON_TIPS_"+user.getId(), request);
		if(!StringUtils.isEmpty(couponCoo)){
			
			isTipsCoupon=true;
		}
			
		}
		return isTipsCoupon;
	}

}
