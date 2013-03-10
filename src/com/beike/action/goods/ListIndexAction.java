package com.beike.action.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.util.BeanUtils;
import com.beike.util.CatlogUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.constants.GoodsRelatedConstants;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;

/**
 * 中转Action
 * 
 * @date 2012-03-28
 * @author qiaowb
 * @version 1.0
 */
@Controller
public class ListIndexAction extends BaseUserAction {

	private static Log log = LogFactory.getLog(ListIndexAction.class);

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	
	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	private static String CITY_CATLOG = "CITY_CATLOG";

	private String GOODS_URL = "/goods/";

	@SuppressWarnings("unchecked")
	@RequestMapping("/goods/listIndex.do")
	public Object searchListIndexGoods(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService
					.get(CITY_CATLOG);
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

			String regionTag = null;
			String region = null;
			//String region = seoService.getRegionId(regionTag,cityid);
			String region_extTag = null;
			String region_ext = null;
			//String region_ext = seoService.getRegionId(region_extTag,cityid);
			String catlogTag = null;
			String catlog = null;
			//String catlog = seoService.getTagId(catlogTag);
			String catlog_extTag = null;
			String catlog_ext = null;
			//String catlog_ext = seoService.getTagId(catlog_extTag);
			
			String rangeprice = null;
			// 获得城市中文信息
			String cityStr = IPSeeker.getCityByStr(city.trim());
			request.setAttribute("CITY_CHINESE", cityStr);

			GoodsCatlog goodsCatLog = new GoodsCatlog();
			goodsCatLog.setCashSelected(false);
			goodsCatLog.setIsNew(false);
			// 设置城市id
			goodsCatLog.setCityid(cityid);
			goodsCatLog.setOrderbydefault("asc");

			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

			Map<Long, List<RegionCatlog>> property_catlog = null;

			// 假如memcache里没有就从数据库里查
			if (regionMap == null) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
				memCacheService.set(REGION_CATLOG, regionMap);
			}

			if(propertCatlogMap == null){
				propertCatlogMap = BeanUtils.getCatlog(request,"propertyCatlogDao");
				property_catlog  = propertCatlogMap.get(cityid);
				memCacheService.set(PROPERTY_CATLOG, propertCatlogMap,60*60*24*360);
			}else{
				property_catlog  = propertCatlogMap.get(cityid);
			}
			
			Map<Long, List<RegionCatlog>> map = regionMap.get(city);
			if (region != null && !"".equals(region)) {
				List<RegionCatlog> listRegion = map.get(Long.parseLong(region));
				CatlogUtils.setCatlogUrl(true, listRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				request.setAttribute("listRegion", listRegion);
				// 获得地域二级中文名称
				/*if (region_ext != null && !"".equals(region_ext)) {
					String regionExtName = getCatlogName(listRegion, Long
							.parseLong(region_ext));
					request.setAttribute("title_regionExtName", regionExtName);
				}*/
			}

			if (catlog != null && !"".equals(catlog)) {
				List<RegionCatlog> listProperty = property_catlog.get(Long.parseLong(catlog));
				CatlogUtils.setCatlogUrl(false, listProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag,
						rangeprice, GOODS_URL);
				request.setAttribute("listProperty", listProperty);
				
				// 获得属性二级中文名称
				/*if (catlog_ext != null && !"".equals(catlog_ext)) {
					String catlogExtName = getCatlogName(property_catlog
							.get(Long.parseLong(catlog)), Long
							.parseLong(catlog_ext));
					request.setAttribute("title_catlogExtName", catlogExtName);
				}*/
			}

			List<RegionCatlog> listParentRegion = map.get(0L);
			// 获得地域一级属性中文名称
			/*if (region != null && !"".equals(region)) {
				String regionName = getCatlogName(listParentRegion, Long
						.parseLong(region));
				request.setAttribute("title_regionName", regionName);
			}*/
			List<RegionCatlog> listParentProperty = property_catlog.get(0L);
			// 获得属性一级属性中文名称
			/*if (catlog != null && !"".equals(catlog)) {
				String catlogName = getCatlogName(listParentProperty, Long
						.parseLong(catlog));
				request.setAttribute("title_catlogName", catlogName);
			}*/
			if (goodsCatLog.isNull()) {
				if (listParentRegion != null && listParentRegion.size() > 0) {
					for (RegionCatlog regionCatlog : listParentRegion) {
						CatlogUtils.setInitUrl(true, regionCatlog, GOODS_URL);
					}
				}

				// 如果搜索条件全为空
				for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, GOODS_URL);
					/*if (regionCatlog.getParentId() == 0) {
						CatlogUtils.setCatlogUrl(false, property_catlog
								.get(regionCatlog.getCatlogid()), regionCatlog
								.getRegion_enname(), catlog_extTag, regionTag,
								region_extTag, rangeprice, GOODS_URL);
					}*/
				}

			} else {
				CatlogUtils.setCatlogUrl(true, listParentRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				// 如果搜索条件不为空，所有的商品2级属性,url需要全部进行处理
				/*for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, GOODS_URL);
					if (regionCatlog.getParentId() == 0) {
						CatlogUtils.setCatlogUrl(false, property_catlog
								.get(regionCatlog.getCatlogid()), regionCatlog
								.getRegion_enname(), catlog_extTag, regionTag,
								region_extTag, rangeprice, GOODS_URL);
					}
				}*/
				CatlogUtils.setCatlogUrl(false, listParentProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);
			}
			//一级分类
			request.setAttribute("listParentProperty", listParentProperty);
			
			//价格区间
			String priceCatlog = CatlogUtils.getPriceCatlog(catlogTag,
					catlog_extTag, regionTag, region_extTag, GOODS_URL);
			
			//计算商品分类、分商圈数量 add by qiaowb 2012-03-25
			priceCatlog = calculateCatlogCount(request,goodsCatLog,map,property_catlog,priceCatlog);
			
			//一级商圈
			request.setAttribute("listParentRegion", listParentRegion);
			//商圈
			//request.setAttribute("regionMap", map);
			//属性
			request.setAttribute("propertyMap", property_catlog);
			//价格区间
			request.setAttribute("priceCatlog", priceCatlog);
			return "/goods/listIndex";
		} catch (Exception e) {
			e.printStackTrace();
			log.error("sb sb sb a", e);
			return new ModelAndView("redirect:../500.html");
		}
	}
	
	/**
	 * 计算分类数量
	 * @param goodsCatLog
	 * @param regionMap
	 * @param propertyMap
	 */
	private String calculateCatlogCount(HttpServletRequest request,
			GoodsCatlog goodsCatLog, Map<Long, List<RegionCatlog>> regionMap,
			Map<Long, List<RegionCatlog>> propertyMap, String priceCatlog) {
		Map<String, Integer> mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid());
		//主缓存值取不到，取备份值 add by qiaoweibo 2012-08-20
		if(mapCatlogCount == null){
			mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid() + "_b");
		}
		if (mapCatlogCount == null) {
			mapCatlogCount = goodsCatlogService
					.getGoodsCatlogGroupCount(goodsCatLog.getCityid());
			// cache有效期1小时
			memCacheService.set(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid(), mapCatlogCount, 60 * 60);
		}
		
		if(mapCatlogCount==null || mapCatlogCount.isEmpty()){
			return priceCatlog;
		}
		
		//分类
		String propertyKey = "";
		if(goodsCatLog.getTagid() != null){
			propertyKey = String.valueOf(goodsCatLog.getTagid());
		}
		
		if(goodsCatLog.getTagextid() != null){
			propertyKey = propertyKey + "|" + String.valueOf(goodsCatLog.getTagextid());
		}
		
		//属性
		String regionKey = "";
		if(goodsCatLog.getRegionid() != null){
			regionKey = String.valueOf(goodsCatLog.getRegionid());
		}
		
		if(goodsCatLog.getRegionextid() != null){
			regionKey = regionKey + "|" + String.valueOf(goodsCatLog.getRegionextid());
		}
		
		//价格区间
		String priceKey = null;
		if(goodsCatLog.getRangeprice()!=null){
			priceKey = String.valueOf(goodsCatLog.getRangeprice().intValue());
		}
		if(priceKey == null){
			priceKey = "";
		}
		
		//今日新品
		String newKey = null;
		if(goodsCatLog.getIsNew()){
			newKey = "new1";
		}
		
		//现金券
		String cashKey = null;
		if(goodsCatLog.getCashSelected()){
			cashKey = "cash1";
		}
		
		//一级分类
		Integer allPropertyA = 0;
		Integer allPropertyB = 0;
		List<RegionCatlog> lstPropertyA = propertyMap.get(0L);
		if(lstPropertyA!=null && lstPropertyA.size()>0){
			StringBuilder bufKey = new StringBuilder();
			if(org.apache.commons.lang.StringUtils.isNotEmpty(regionKey)){
				bufKey.append("|").append(regionKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
				bufKey.append("|").append(priceKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
				bufKey.append("|").append(newKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
				bufKey.append("|").append(cashKey);
			}
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
		if(goodsCatLog.getTagid()!=null && goodsCatLog.getTagid()>0){
			List<RegionCatlog> lstPropertyB = propertyMap.get(goodsCatLog.getTagid());
			if(lstPropertyB!=null && lstPropertyB.size()>0){
				StringBuilder bufKey = new StringBuilder();
				if(org.apache.commons.lang.StringUtils.isNotEmpty(regionKey)){
					bufKey.append("|").append(regionKey);
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
					bufKey.append("|").append(priceKey);
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
					bufKey.append("|").append(newKey);
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
					bufKey.append("|").append(cashKey);
				}
				
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
					//allPropertyB = allPropertyB + goodsCount;
				}
			}
		}
		
		//一级商圈
		Integer allRegionA = 0;
		Integer allRegionB = 0;
		List<RegionCatlog> lstRegionA = regionMap.get(0L);
		if(lstRegionA!=null && lstRegionA.size()>0){
			StringBuilder allKey = new StringBuilder();
			
			if(org.apache.commons.lang.StringUtils.isNotEmpty(propertyKey)){
				allKey.append(propertyKey).append("|");
			}
			allKey.append("regionAll");
			if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
				allKey.append("|").append(priceKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
				allKey.append("|").append(newKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
				allKey.append("|").append(cashKey);
			}
			allRegionA = mapCatlogCount.get(allKey.toString());
			if(allRegionA == null){
				allRegionA = 0;
			}
			
			for(RegionCatlog regionA : lstRegionA){
				StringBuilder bufKey = new StringBuilder();
				
				if(org.apache.commons.lang.StringUtils.isNotEmpty(propertyKey)){
					bufKey.append(propertyKey).append("|");
				}
				bufKey.append(regionA.getCatlogid());
				if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
					bufKey.append("|").append(priceKey);
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
					bufKey.append("|").append(newKey);
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
					bufKey.append("|").append(cashKey);
				}
				
				Integer goodsCount = mapCatlogCount.get(bufKey.toString());
				if(goodsCount == null){
					goodsCount = 0;
				}
				regionA.setCount(String.valueOf(goodsCount));

				if(goodsCatLog.getRegionid()!=null && regionA.getCatlogid().compareTo(goodsCatLog.getRegionid()) == 0){
					allRegionB = goodsCount;
				}
			}
		}
		
		//二级商圈
		if(goodsCatLog.getRegionid()!=null && goodsCatLog.getRegionid()>0){
			List<RegionCatlog> lstRegionB = regionMap.get(goodsCatLog.getRegionid());
			if(lstRegionB!=null && lstRegionB.size()>0){
				for(RegionCatlog regionB : lstRegionB){
					StringBuilder bufKey = new StringBuilder();
					
					if(org.apache.commons.lang.StringUtils.isNotEmpty(propertyKey)){
						bufKey.append(propertyKey).append("|");
					}
					bufKey.append(goodsCatLog.getRegionid())
						.append("|").append(regionB.getCatlogid());
					if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
						bufKey.append("|").append(priceKey);
					}
					if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
						bufKey.append("|").append(newKey);
					}
					if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
						bufKey.append("|").append(cashKey);
					}
					
					Integer goodsCount = mapCatlogCount.get(bufKey.toString());
					if(goodsCount == null){
						goodsCount = 0;
					}
					regionB.setCount(String.valueOf(goodsCount));
					
					//allRegionB = allRegionB + goodsCount;
				}
			}
		}
		
		//价格区间
		int allPrice = 0;
		String[] priceAry = new String[]{"50","100","300","500","1000"};
		for(String price : priceAry){
			StringBuilder bufKey = new StringBuilder();
			
			if(org.apache.commons.lang.StringUtils.isNotEmpty(propertyKey)){
				bufKey.append(propertyKey).append("|");
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(regionKey)){
				bufKey.append(regionKey).append("|");
			}
			bufKey.append(price);
			if(org.apache.commons.lang.StringUtils.isNotEmpty(newKey)){
				bufKey.append("|").append(newKey);
			}
			if(org.apache.commons.lang.StringUtils.isNotEmpty(cashKey)){
				bufKey.append("|").append(cashKey);
			}
			
			Integer goodsCount = mapCatlogCount.get(bufKey.toString());
			if(goodsCount == null){
				goodsCount = 0;
			}
			
			if("50".equals(price)){
				priceCatlog = priceCatlog.replace("50元以下", "50元以下(" + goodsCount + ")");
			}else if("100".equals(price)){
				priceCatlog = priceCatlog.replace("50-100元", "50-100元(" + goodsCount + ")");
			}else if("300".equals(price)){
				priceCatlog = priceCatlog.replace("100-300元", "100-300元(" + goodsCount + ")");
			}else if("500".equals(price)){
				priceCatlog = priceCatlog.replace("300-500元", "300-500元(" + goodsCount + ")");
			}else if("1000".equals(price)){
				priceCatlog = priceCatlog.replace("500元以上", "500元以上(" + goodsCount + ")");
			}
			
			allPrice = allPrice + goodsCount;
		}
		request.setAttribute("allPropertyA", allPropertyA);
		request.setAttribute("allPropertyB", allPropertyB);
		request.setAttribute("allRegionA", allRegionA);
		request.setAttribute("allRegionB", allRegionB);
		request.setAttribute("allPrice", allPrice);
		return priceCatlog;
	}
}