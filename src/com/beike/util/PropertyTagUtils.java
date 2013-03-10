package com.beike.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

public class PropertyTagUtils {

	static MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	private static final String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	public static Map<Long, Map<Long, List<RegionCatlog>>> getPropetyTag(
			HttpServletRequest request) {
		Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService
				.get(PROPERTY_CATLOG);

		if (propertCatlogMap == null) {
			propertCatlogMap = BeanUtils
					.getCatlog(request, "propertyCatlogDao");
			memCacheService.set(PROPERTY_CATLOG, propertCatlogMap);
		}
		return propertCatlogMap;
	}

	public static Long getCityID(HttpServletRequest request, String city_pinyin) {
		// 获取当前城市ID
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService
				.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set("CITY_CATLOG", mapCity);
		}
		return mapCity.get(city_pinyin.trim());
	}
}
