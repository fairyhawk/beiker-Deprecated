package com.beike.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.beanutils.BeanUtils;

import com.beike.entity.shopcart.ShopItem;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONObject;

public class ShopCartUtil {
    MemCacheService cacheService = MemCacheServiceImpl.getInstance();
	public static List<ShopItem> transferCookie2ShopItem(String cookie) throws Exception{
		JSONArray jsonArray = new JSONArray(cookie);
		int length = jsonArray.length();
		ShopItem shopItem = null;
		JSONObject jsonObject = null;
		List<ShopItem> shopItemList = new ArrayList<ShopItem>();
		for(int i=0;i<length;i++){
			shopItem = new ShopItem();
			jsonObject = jsonArray.getJSONObject(i);
			BeanUtils.copyProperties(shopItem, jsonObject);
			shopItemList.add(shopItem);
		}
		
		return shopItemList;
	}
}
