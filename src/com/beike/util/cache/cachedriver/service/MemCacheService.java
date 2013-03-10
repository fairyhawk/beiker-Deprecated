package com.beike.util.cache.cachedriver.service;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author zb.liu
 *
 */
public interface MemCacheService {
	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	Object get(String key);
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean set(String key, Object value);
	
	
	
	
	
	/**
	 * 批量取
	 * @param keys
	 * @return
	 */
	Map<String, Object> getBulk(Set<String> keys);
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	boolean remove(String key);
	
	
	
	/**
	 * 存,设置超时时间
	 * @param key
	 * @param value
	 * @param exp
	 * @return
	 */
	boolean set(String key, Object value, int exp);
}
