package com.beike.interceptors;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.beike.entity.shopcart.ShopItem;
import com.beike.service.shopcart.ShopCartService;
import com.beike.util.WebUtils;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONObject;
import com.beike.util.shopcart.ShopJsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;


@Resource(name="shopcartInterceptor")
public class ShopCartCheck extends BaseBeikeInterceptor {

	
	@Autowired
	private ShopCartService shopCartService;
	
	Logger logger = Logger.getLogger(ShopCartCheck.class);
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 判断用户有未登录
		if(SingletonLoginUtils.getLoginUserid(request) != null) {
			/**
			 * Cookie中参数的格式：Json字符串
			 * [{“merchantid”:xxx,” goodsid”:xxxx,” buy_count”:1,” add_time”:”yyyy-MM-dd HH:mm:ss”},{“merchantid”:xxx,” goodsid”:xxxx,” buy_count”:1,” add_time”:”yyyy-MM-dd HH:mm:ss”},{“merchantid”:xxx,” goodsid”:xxxx,” buy_count”:1,” add_time”:”yyyy-MM-dd HH:mm:ss”}] 
			 */
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String shopcartList = WebUtils.getCookieValue(ShopJsonUtil.ShopCookieKey, request);			
			/**
			 * 
			 * 去除json 串对象的转义  \
			 * 
			 * Add by zx.liu
			 *  
			 */			
			String shopcartItemList = ShopJsonUtil.escape(shopcartList);

			if(shopcartItemList != null && !"".equals(shopcartItemList)){
				JSONArray cookieJson = new JSONArray(shopcartItemList);
				int length = cookieJson.length();
				
				// 通过循环来批量添加Cookie中的购物信息
				for(int i=0;i<length;i++){
					JSONObject jsonObject = cookieJson.getJSONObject(i);
					ShopItem shopItem = new ShopItem();
					BeanUtils.copyProperties(shopItem, jsonObject);
					shopItem.setBuy_count(jsonObject.getLong("buy_count"));
					shopItem.setGoodsid(jsonObject.getLong("goodsid"));
					shopItem.setMerchantid(jsonObject.getLong("merchantid"));
					shopItem.setAddtime(simpleDateFormat.parse(jsonObject.getString("addtime")));
					shopItem.setUserid(SingletonLoginUtils.getLoginUserid(request));
					//防止老用户cookie报异常
					Long miaoshsid = jsonObject.get("miaoshaid") != null ? jsonObject.getLong("miaoshaid") : 0L;
					shopItem.setMiaoshaid(miaoshsid);
					shopCartService.appendShopItem(shopItem);
					
				} // End for
				
				// 清空用户购物信息的Cookie
				Cookie cookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey, "", 0);
				response.addCookie(cookie);		
			} // End if			
		}		
            return true;
	}

	@Override
	protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request) {
		return null;
	}

}
