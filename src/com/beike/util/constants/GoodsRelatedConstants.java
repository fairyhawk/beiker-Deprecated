package com.beike.util.constants;

/**
 * goods相关常量
 * 缓存key变量名以MEMCAHCECACHE_KEY_为前缀:i.e:MEMCAHCECACHE_KEY_CITY_GOODS_COUNT
 * 缓存key值以MEMECACHEKEY_PREFIX_为前缀
 * @author janwen
 * Jul 19, 2012
 */
public class GoodsRelatedConstants {

	
	private static final String MEMCACHEKEY_PREFIX = "MEMECACHEKEY_";
	
	//商品类别页数量统计缓存key
	public static final String MEMCAHCECACHE_KEY_CITY_GOODS_COUNT = MEMCACHEKEY_PREFIX + "CITY_GOODS_COUNT_";
}
