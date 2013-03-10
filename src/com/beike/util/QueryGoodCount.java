package com.beike.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.service.goods.GoodsService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.constants.GoodsRelatedConstants;

/**
 * @Title: 查询有效商品数量
 * @Package com.beike.util
 * @Description: TODO
 * @author wenjie.mai
 * @date Apr 10, 2012 11:01:53 AM
 * @version V1.0
 */
public class QueryGoodCount {
	private static Logger log = Logger.getLogger(QueryGoodCount.class);
	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";
	
	public static int getGoodCount(String type, String city,
			HttpServletRequest request) {

		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(city))
			return 0;
		
		ApplicationContext ac = WebApplicationContextUtils
						.getRequiredWebApplicationContext(request.getSession().getServletContext());

		MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
		
		Integer count = (Integer) memCacheService.get("QUERY_GOOD_COUNT_"+type+"_"+city);
		
		if(count == null){
			GoodsService goodsService = (GoodsService) ac.getBean("goodsService");
			int num = goodsService.getGoodCountByCity(city, type);
			
			memCacheService.set("QUERY_GOOD_COUNT_"+type+"_"+city,num,60*5);
			return num;
		}
		
		return count;
	}
	
	/**
	 * 查询二级分类
	 * @param city
	 * @param type
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	public static void getSecondProperty(String city,String catlogid,HttpServletRequest request,String catlog){
		
		MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
		
		
		// 获取当前城市ID
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set("CITY_CATLOG", mapCity);
		}
		
		if (StringUtils.isEmpty(city)) {
			city = "beijing";
		}

		Long cityid = null;
		if (mapCity != null) {
			cityid = mapCity.get(city.trim());
		}
		
		Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

		Map<Long, List<RegionCatlog>> property_catlog = null;
		
		if(propertCatlogMap == null){
			propertCatlogMap = BeanUtils.getCatlog(request,"propertyCatlogDao");
			memCacheService.set(PROPERTY_CATLOG, propertCatlogMap,60*60*24*360);
		}
		property_catlog  = propertCatlogMap.get(cityid);
		if(property_catlog==null){
			log.info("cityid="+cityid+" property_catlog is null");
		}
		List<RegionCatlog> listProperty = property_catlog.get(Long.parseLong(catlogid));
		CatlogUtils.setCatlogUrl(false, listProperty, catlog,
				"", "", "",null, "/goods/");
		GoodsCatlog goodsCatLog = new GoodsCatlog();
		goodsCatLog.setTagid(Long.parseLong(catlogid));
		goodsCatLog.setCityid(cityid);
		calculateCatlogCount(request,goodsCatLog,property_catlog,catlogid);
	}
	
	/**
	 * 计算分类数量
	 * @param goodsCatLog
	 * @param regionMap
	 * @param propertyMap
	 */
	@SuppressWarnings("unchecked")
	private static void calculateCatlogCount(HttpServletRequest request,
			GoodsCatlog goodsCatLog,Map<Long, List<RegionCatlog>> propertyMap,String catlogid) {
		
		MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
		ApplicationContext ac = WebApplicationContextUtils
		.getRequiredWebApplicationContext(request.getSession().getServletContext());
		
		GoodsCatlogService goodsCatlogService = (GoodsCatlogService) ac.getBean("goodsCatlogService");
		
		Map<String, Integer> mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid());
		//主缓存值取不到，取备份值 add by qiaoweibo 2012-08-20
		if(mapCatlogCount == null){
			mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid() + "_b");
		}
		if (mapCatlogCount == null || mapCatlogCount.get(String.valueOf(goodsCatLog.getCityid()))==null) {
			mapCatlogCount = goodsCatlogService.getGoodsCatlogGroupCount(goodsCatLog.getCityid());
			// cache有效期1小时
			memCacheService.set(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid(), mapCatlogCount, 60 * 60);
		}
		
		//分类
		String propertyKey = "";
		if(goodsCatLog.getTagid() != null){
			propertyKey = String.valueOf(goodsCatLog.getTagid());
		}
		
		if(goodsCatLog.getTagextid() != null){
			propertyKey = propertyKey + "|" + String.valueOf(goodsCatLog.getTagextid());
		}
		
		Integer allPropertyA = 0;
		Integer allPropertyB = 0;
		List<RegionCatlog> lstPropertyA = propertyMap.get(0L);
		if(lstPropertyA!=null && lstPropertyA.size()>0){
			StringBuilder bufKey = new StringBuilder();
			//一级分类全部
			StringBuilder allKey = new StringBuilder("tagAll");
			if(bufKey.length() != 0){
				allKey.append(bufKey);
			}
			allPropertyA = mapCatlogCount.get(allKey.toString());
			if(allPropertyA == null){
				allPropertyA = 0;
			}

			for(RegionCatlog tagA : lstPropertyA){
				StringBuilder tmpKey = new StringBuilder();
				tmpKey.append(tagA.getCatlogid());
				if(bufKey.length() != 0){
					tmpKey.append(bufKey);
				}
				Integer goodsCount = mapCatlogCount.get(tmpKey.toString());
				if(goodsCount == null){
					goodsCount = 0;
				}
				tagA.setCount(String.valueOf(goodsCount));
				if(goodsCatLog.getTagid()!=null && tagA.getCatlogid().compareTo(goodsCatLog.getTagid())==0){
					allPropertyB = goodsCount;
				}
			}
		}
		
		//二级分类
		List<RegionCatlog> lstPropertyX = new ArrayList<RegionCatlog>();
		if(goodsCatLog.getTagid()!=null && goodsCatLog.getTagid()>0){
			List<RegionCatlog> lstPropertyB = propertyMap.get(goodsCatLog.getTagid());
			if(lstPropertyB!=null && lstPropertyB.size()>0){
				StringBuilder bufKey = new StringBuilder();
				for(RegionCatlog tagB : lstPropertyB){
					StringBuilder tmpKey = new StringBuilder();
					tmpKey.append(goodsCatLog.getTagid())
						.append("|").append(tagB.getCatlogid());
					
					if(bufKey.length() != 0){
						tmpKey.append(bufKey);
					}
					
					Integer goodsCount = mapCatlogCount.get(tmpKey.toString());
					if(goodsCount == null){
						goodsCount = 0;
					}
					tagB.setCount(String.valueOf(goodsCount));
					if(goodsCount > 0){
						lstPropertyX.add(tagB);
					}
				}
			}
			propertyMap.put(goodsCatLog.getTagid(),lstPropertyX);
			request.setAttribute("listProperty_"+catlogid, lstPropertyX);
		}
		goodsCatLog.setTagid(null);
		goodsCatLog.setCityid(null);
		request.setAttribute("allPropertyB_"+catlogid, allPropertyB);
	}
}
