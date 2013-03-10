package com.beike.common.listener;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.util.DateUtils;
import com.beike.util.MUrlConstant;
import com.beike.util.UrlConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>Title:商品类别监听器</p>
 * <p>Description: 加载时将所有类别查询出来,放到Memcache里</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 24, 2011
 * @author ye.tian
 * @version 1.0
 */

public class CatlogListener implements ServletContextListener {
	
	private static Log logger=LogFactory.getLog(CatlogListener.class);
	
	
	private RegionCatlogDao regionCatlogDao;
	
	private MemCacheService memCacheService=MemCacheServiceImpl.getInstance();
	
	private static String REGION_CATLOG="BASE_REGION_CATLOG";
	
	public static String PROPERTY_CATLOG="PROPERTY_CATLOG_NEW";
	
	private static String CITY_CATLOG="CITY_CATLOG";
	
	private static String LABEL_CATLOG = "LABEL_CATLOG";
	
	private static String REQUEST_URL="REQUEST_URL";
	
	public static final String M_REQUEST_URL="M_REQUEST_URL";
	
	public static final String APP_START_TIME=DateUtils.getUserDate("yyyyMMddhhmmss");
	
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	public void contextInitialized(ServletContextEvent arg0) {
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext());
		try{
			//memcache设置一年
			int validateDay=60*60*24*360;
			regionCatlogDao=(RegionCatlogDao) ac.getBean("regionCatlogDao");
			
//			Map<Long,List<RegionCatlog>> regionMap=null;
			Map<Long,List<RegionCatlog>> propertyMap=null;
			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap =null;
			Map<String, Map<Long, List<RegionCatlog>>>  regionMap=null;
			//获得地域属性
			if(regionCatlogDao!=null){
//				regionMap=regionCatlogDao.getAllCatlog();
				regionMap=regionCatlogDao.getAllCityCatlog();
			}
			
			Map<String,Long> mapCity=regionCatlogDao.getCityCatlog();
			
			//获得商品属性
			regionCatlogDao=(RegionCatlogDao) ac.getBean("propertyCatlogDao");
			if(regionCatlogDao!=null){
				propertCatlogMap = regionCatlogDao.getAllCatlogHavingCity();
			}
			
			
			//获得标签属性
			Map<Long, List<RegionCatlog>> labelMap = null;
			regionCatlogDao = (RegionCatlogDao) ac.getBean("labelCatlogDao");
			if(regionCatlogDao != null){
				labelMap = regionCatlogDao.getAllLabelCatlogProperty();
			}
			
			memCacheService.set(LABEL_CATLOG,labelMap);
			
			memCacheService.set(CITY_CATLOG, mapCity);
			//设置Memcache
			memCacheService.set(REGION_CATLOG, regionMap,validateDay);
			memCacheService.set(PROPERTY_CATLOG, propertCatlogMap,validateDay);
			//读取访问路径
			Map<String,Set<String>> requestSet=UrlConstant.getRequestUrl();
			Map<String,Set<String>> mRequestSet=MUrlConstant.getRequestUrl();
			memCacheService.set(REQUEST_URL, requestSet,validateDay);
			memCacheService.set(M_REQUEST_URL, mRequestSet,validateDay);
//			arg0.getServletContext().setAttribute(REQUEST_URL, requestSet);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
