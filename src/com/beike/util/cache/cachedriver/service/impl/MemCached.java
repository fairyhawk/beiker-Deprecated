package com.beike.util.cache.cachedriver.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.PropertiesReader;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * Memcached工具类
 * 
 */
public class MemCached implements MemCacheService {
	private static Log log = LogFactory.getLog(MemCached.class);
	// 创建全局的唯一实例
	private static MemCachedClient mcc = new MemCachedClient();
	// 服务器列表
	private static String servers = PropertiesReader.getValue("memcache",
			"server1"); // "192.168.172.10:15000 192.168.172.10:16000";
	// 默认有效时间(一天)
	private static int defaultTime = 60 * 60 * 24;
	// 重试次数
	private static int retry = 3;
	// 设置与缓存服务器的连接池
	static {
		if (StringUtils.isBlank(servers)) {
			throw new NullArgumentException(
					"memcache property servers is not null");
		}
		// 获取socke连接池的实例对象
		SockIOPool pool = SockIOPool.getInstance();
		// 设置服务器信息
		pool.setServers(servers.split(" +"));
		// 设置初始连接数、最小和最大连接数以及最大处理时间
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);
		// 设置主线程的睡眠时间
		pool.setMaintSleep(0);
		// 是否使用Nagle算法,socket的读取等待超时值,socket的连接等待超时值，设置hash算法 ,连接心跳监测开关
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
		pool.setHashingAlg(3);
		pool.setAliveCheck(false);
		// 连接失败恢复开关,容错开关
		pool.setFailback(true);
		pool.setFailover(false);
		// 初始化连接池
		pool.initialize();
	}

	private static MemCached memCached = new MemCached();

	private MemCached() {
	}

	public static MemCacheService getInstance() {
		return memCached;
	}

	/**
	 * 添加缓存值(有效期一天)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public boolean set(String key, Object value) {
		return set(key, value, defaultTime);
	}

	/**
	 * 添加缓存值(单位:秒 expirt=0 永久)
	 * 
	 * @param key
	 * @param value
	 * @param expiry
	 * @return
	 */
	public boolean set(String key, Object value, int expiry) {
		boolean _result = false;
		try {
			if (StringUtils.isBlank(key) || value == null) {
				return false;
			}
			long l = expiry > 0 ? System.currentTimeMillis() + expiry * 1000
					: 0;

			for (int i = 1; i <= retry; i++) {
				_result = mcc.set(key, value, new Date(l));
				if (_result) {
					break;
				}
				log.error("[FAIL] set obj[key:" + key + " value:"
						+ value.getClass() + " expiry:" + expiry
						+ "] to cache failed begin to retry " + i + " times");
			}
		} catch (Exception e) {
			log.error("[FAIL] set obj[key:" + key + " value:"
					+ value.getClass() + " expiry:" + expiry
					+ "] to cache failed");
			e.printStackTrace();
		}
		return _result;
	}

	/**
	 * 从缓存中取值
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Object _result = null;
		try {
			for (int i = 1; i <= retry; i++) {
				_result = mcc.get(key);
				if (null == _result) {
//					log.error("[FAIL] get obj[key:" + key
//							+ "] failed from cache retry " + i + " times");
					continue;
				}
				break;
			}
		} catch (Exception e) {
			log.error("[FAIL] get obj[key:" + key + "] failed from cache");
			e.printStackTrace();
		}
		if(_result!=null){
			if(_result instanceof Collection<?>){
				//log.info("[OK] get obj[key:" + key+",size:"+((Collection)_result).size());
			}
			if(_result instanceof Map){
				//log.info("[OK] get obj[key:" + key+",size:"+((Map)_result).size());
			}
		}
		return _result;
	}

	/**
	 * 从缓存中删除
	 * 
	 * @param key
	 */
	public boolean remove(String key) {
		Object object = get(key);
		if (null == object) {
			return true;
		}
		boolean _result = false;
		for (int i = 1; i <= retry; i++) {
			_result = mcc.delete(key);
			if (_result) {
				break;
			}
			log.error("[FAIL] remove obj[key:" + key
					+ "] from cache failed begin to retry " + i + " times");
		}
		return _result;
	}

	@Override
	public Map<String, Object> getBulk(Set<String> keys) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (keys == null) {
			return map;
		}
		for (String key : keys) {
			map.put(key, get(key));
		}
		return map;
	}

	public static void main(String[] args) {
		MemCacheService old = MemCacheServiceImpl.getInstance();
		MemCacheService newS = MemCached.getInstance();
		Map<String, Integer> map = new HashMap<String,Integer>();
		newS.remove("LOGIN_IP");
	}
}
