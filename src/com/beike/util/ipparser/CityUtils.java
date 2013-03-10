package com.beike.util.ipparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.BeanUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>
 * Title: 城市选择工具
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
 * @date Jun 28, 2011
 * @author ye.tian
 * @version 1.0
 */

public class CityUtils {

	public static String CITY_COOKIENAME = "QIANPIN_LAST_CITY_";

	public static int validy = 60 * 60 * 24 * 365;

	public static List<String> cityList = new ArrayList<String>();
	
	private static Log log=LogFactory.getLog(CityUtils.class);
	
	public static List<String> dianCanList=new ArrayList<String>();
	public static List<String> waiMaiList=new ArrayList<String>();
	
	
	public static List<String> filmList=new ArrayList<String>();
	
	
	private static final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private static String CITY_CATLOG = "CITY_CATLOG";//城市key

	static {
		cityList.addAll(IPSeeker.getPinyinList());
	}
	public static Map<String, String> cityMap = new HashMap<String, String>();
	static {

		List<String> list = IPSeeker.getList();
		if (list != null && list.size() > 0) {
			for (String string : list) {
				String pinyin = PinyinUtil.hanziToPinyin(string, "");
				cityMap.put(pinyin, string);
			}

		}
		//支持点菜的城市
	
		dianCanList.add("beijing");
		dianCanList.add("shanghai");
		dianCanList.add("guangzhou");
		dianCanList.add("shenzhen");
		dianCanList.add("jinan");
		dianCanList.add("chengdu");
		dianCanList.add("hefei");
		dianCanList.add("fuzhou");
		dianCanList.add("wuhan");

		waiMaiList.add("beijing");
		waiMaiList.add("shanghai");
		waiMaiList.add("guangzhou");
		waiMaiList.add("shenzhen");
		
		
		
		filmList.add("beijing");
		filmList.add("shanghai");
		filmList.add("guangzhou");
		filmList.add("shenzhen");
		filmList.add("tianjin");
		filmList.add("chongqing");
		
	}
	/**
	 * 判断当前城市是否支持点菜
	 * @param request
	 * @param response
	 * @return
	 */
	public static boolean isOpenDiancan(HttpServletRequest request , HttpServletResponse response){
		
		String cityStr=getCity(request, response);
		if(cityStr==null||"".equals(cityStr.trim())) return false;
		if(dianCanList.indexOf(cityStr)!=-1){
			return true;
		}
		return false;
	}
	
	public static boolean isOpenWaiMai(HttpServletRequest request , HttpServletResponse response){
		
		String cityStr=getCity(request, response);
		if(cityStr==null||"".equals(cityStr.trim())) return false;
		if(waiMaiList.indexOf(cityStr)!=-1){
			return true;
		}
		return false;
	}
	
	public static boolean isOpenFilm(HttpServletRequest request , HttpServletResponse response){
		
		String cityStr=getCity(request, response);
		if(cityStr==null||"".equals(cityStr.trim())) return false;
		if(filmList.indexOf(cityStr)!=-1){
			return true;
		}
		return false;
	}
	

	public static String getCityName(String city) {
		if (city == null) {
			return "";
		} else {
			return cityMap.get(city.trim().toLowerCase());
		}
	}

	public static String getStr(String refer) {
		String city = "";
		for (String cy : cityList) {
			if (refer.indexOf(cy) != -1) {
				city = cy;
			}
		}
		return city;
	}

	/**
	 * 获得城市的缩写
	 * 
	 * @param request
	 * @param respnse
	 * @return
	 */
	public static String getCity(HttpServletRequest request,
			HttpServletResponse respnse) {
		String refer = request.getRequestURL().toString();
		String city = request.getParameter("city");
		if (city == null || city.trim().equals("")) {
			city = getStr(refer);
		}
		if (city == null || "".equals(city)) {
			city = WebUtils.getCookieValue(CITY_COOKIENAME, request);
			if (city == null || "".equals(city)) {
				IPSeeker seeker = IPSeeker.getInstance();
				String ip = WebUtils.getIpAddr(request);
				city = seeker.getCity(ip);
				if (!cityList.contains(city)) {
					city = "";
				}
			}
		}
		if (city == null || city.trim().equals("")) {
			city = "";
		} else {
			//判断假如是中文,并且是我们开通的城市返回相应的 否则默认返回北京
			Pattern p = Pattern.compile("[\u4E00-\u9FA5]");
			Matcher m = p.matcher(city);
			if(m.find()){
				if(city.indexOf("市")!=-1){
					city=city.substring(0, city.indexOf("市"));
				}
				if(city.indexOf("省")!=-1){
					city=city.substring(0, city.indexOf("省"));
				}
				String str=PinyinUtil.hanziToPinyin(city, "");
				if(cityList.indexOf(str)!=-1){
					city=str;
				}else{
					city="beijing";
				}
			}
			
			Cookie coo = WebUtils.cookie(CITY_COOKIENAME, city, validy);
			respnse.addCookie(coo);
		}
		
		
		return city;
	}
	/**
	 * 
	* @Title: getCity
	* @Description: 获取城市标识
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public static Long getCityId(HttpServletRequest request,HttpServletResponse response){
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get(CITY_CATLOG);
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set(CITY_CATLOG, mapCity);
		}
		String city = CityUtils.getCity(request, response);
		if (city == null || "".equals(city)) {
			city = "beijing";
		}
		Long cityid = null;
		if (mapCity != null) {
			cityid = mapCity.get(city.trim());
		}
		return cityid;
	}
}
