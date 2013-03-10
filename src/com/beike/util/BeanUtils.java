package com.beike.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.entity.catlog.RegionCatlog;

/**
 * <p>Title: bean 工具</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */

public class BeanUtils {
	
	/**
	 * web 环境下获得bean
	 * @param request
	 * @param beanid
	 * @return
	 */
	public static Object getBean(HttpServletRequest request,String beanid){
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		return ac.getBean(beanid);
	}
	
	public static Map<Long,Map<Long,List<RegionCatlog>>> getCatlog(HttpServletRequest request,String beanid){
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		RegionCatlogDao regionCatlogDao=(RegionCatlogDao) ac.getBean(beanid);
		return regionCatlogDao.getAllCatlogHavingCity();
	}
	
	

	public static Map<String, Map<Long, List<RegionCatlog>>>  getCityCatlog(HttpServletRequest request,String beanid){
		
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		RegionCatlogDao regionCatlogDao=(RegionCatlogDao) ac.getBean(beanid);
		return regionCatlogDao.getAllCityCatlog();
	}
	
	public static Map<String,Long> getCity(HttpServletRequest request,String beanid){
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		RegionCatlogDao regionCatlogDao=(RegionCatlogDao) ac.getBean(beanid);
		return regionCatlogDao.getCityCatlog();
	}
	
	public static Map<Long, List<RegionCatlog>> getAllLabelProperty(HttpServletRequest request,String beanid){
		ApplicationContext  ac=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		RegionCatlogDao regionCatlogDao=(RegionCatlogDao) ac.getBean(beanid);
		return regionCatlogDao.getAllLabelCatlogProperty();
	}
}
