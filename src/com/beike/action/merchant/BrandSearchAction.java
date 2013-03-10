package com.beike.action.merchant;

import java.util.LinkedList;
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

import com.beike.action.LogAction;
import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.BrandCatlogService;
import com.beike.common.search.SearchStrategy;
import com.beike.entity.catlog.MerchantCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.merchant.MerchantService;
import com.beike.service.seo.SeoService;
import com.beike.util.BeanUtils;
import com.beike.util.CatlogUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**
 * <p>
 * Title:品牌搜索Action
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
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class BrandSearchAction extends BaseUserAction {


	private static Log log = LogFactory.getLog(BrandSearchAction.class);

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private static int pageSize = 50;


	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	private static String CITY_CATLOG = "CITY_CATLOG";

	private static String BRAND_URL = "/brand/";

	@Autowired
	private SeoService seoService;

	@Autowired
	private BrandCatlogService brandCatlogService;

	@Autowired
	private MerchantService merchantService;

	public BrandCatlogService getBrandCatlogService() {
		return brandCatlogService;
	}

	public void setBrandCatlogService(BrandCatlogService brandCatlogService) {
		this.brandCatlogService = brandCatlogService;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/brand/searchBrandsByProperty.do")
	public Object searchBrandsByProperty(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		
		
		// 设置urlcookie
		super.setCookieUrl(request, response);
		// //////////////////////////////////////////////////////////////////////
		try {
			replaceScript(request);
		} catch (Exception e) {
			return new ModelAndView(
					"redirect:http://www.qianpin.com/brand/searchBrandsByProperty.do");
		}
		//品牌类别
		String catlog_value = "";
		/**
		 * 补充说明：by zx.liu 这里 获取值修改为了对应的 tagEnname（标签值）
		 */
		String regionTag = request.getParameter("region");
		if (StringUtils.isNumber(regionTag)) {
			regionTag = seoService.getRegionENName(regionTag);
		}
		String region = seoService.getRegionId(regionTag);

		String region_extTag = request.getParameter("regionext");
		if (StringUtils.isNumber(region_extTag)) {
			region_extTag = seoService.getRegionENName(region_extTag);
		}
		String region_ext = seoService.getRegionId(region_extTag);

		String catlogTag = request.getParameter("catlog");
		if (StringUtils.isNumber(catlogTag)) {
			catlogTag = seoService.getTagENName(catlogTag);
		}
		String catlog = seoService.getTagId(catlogTag);

		String catlog_extTag = request.getParameter("catlogext");
		if (StringUtils.isNumber(catlog_extTag)) {
			catlog_extTag = seoService.getTagENName(catlog_extTag);
		}
		String catlog_ext = seoService.getTagId(catlog_extTag);
		// //////////////////////////////////////////////////////////////////////////////////////////
		// 排序
		String orderbydate = request.getParameter("orderbydate");
		String orderbysort = request.getParameter("orderbysort");

		log.debug("品牌搜索条件:region:" + region + "  regionext:" + region_ext
				+ " catlog:" + catlog + " catlog_ext:" + catlog_ext);

		log.debug("品牌排序条件:orderbydate:" + orderbydate + " orderbysort:"
				+ orderbysort);
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
			cityid = mapCity.get(city);
		}
		MerchantCatlog merchantCatlog = new MerchantCatlog();
		merchantCatlog.setCityid(cityid);
		try {
			if (region != null && !"".equals(region)) {
				merchantCatlog.setRegionid(Long.parseLong(region));
			}
			if (region_ext != null && !"".equals(region_ext)) {
				merchantCatlog.setRegionextid(Long.parseLong(region_ext));
			}
			if (catlog != null && !"".equals(catlog)) {
				merchantCatlog.setTagid(Long.parseLong(catlog));
				catlog_value = catlogTag;
			}
			if (catlog_ext != null && !"".equals(catlog_ext)) {
				merchantCatlog.setTagextid(Long.parseLong(catlog_ext));
			}

			// 排序
			if (orderbydate != null && !"".equals(orderbydate)) {
				merchantCatlog.setOrderbydate(orderbydate);
			} else if (orderbysort != null && !"".equals(orderbysort)) {
				merchantCatlog.setOrderbysort(orderbysort);
			}

		} catch (Exception e) {
			request.setAttribute("ERRMSG", "查询条件输入有误!");
			e.printStackTrace();
			return new ModelAndView("redirect:../500.html");
		}


		// 当前页
		String currentPage = request.getParameter("cpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		//加入统计代码 by janwen at 2012-1-17 11:49:25
		printLog(request, response, catlog_value, currentPage);
		// 计算分页
		int totalCount = 0;

		totalCount = merchantService.getBrandCatlogCount(merchantCatlog);
		Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
				totalCount, pageSize);

		request.setAttribute("pager", pager);

		List<Long> searchListids = merchantService.getBrandCatlog(
				merchantCatlog, pager);
		log.debug("商家ID....." + searchListids);
		List<MerchantForm> listForm = null;

		if (searchListids == null || searchListids.size() == 0) {
			// 假如没有查出数据的默认列表
			merchantCatlog.setRegionextid(null);
			merchantCatlog.setTagextid(null);
			totalCount = merchantService.getBrandCatlogCount(merchantCatlog);
			pager = PagerHelper.getPager(Integer.parseInt(currentPage),
					totalCount, pageSize);
			List<Long> seIds = merchantService.getBrandCatlog(merchantCatlog,
					pager);
			request.setAttribute("goodsNull", "true");
			if (seIds == null || seIds.size() == 0) {
				merchantCatlog.setTagid(null);
				totalCount = merchantService
						.getBrandCatlogCount(merchantCatlog);
				pager = PagerHelper.getPager(Integer.parseInt(currentPage),
						totalCount, pageSize);
				seIds = merchantService.getBrandCatlog(merchantCatlog, pager);
				if (seIds == null || seIds.size() == 0) {
					merchantCatlog.setRegionextid(null);
					merchantCatlog.setRegionid(null);
					merchantCatlog.setTagextid(null);
					merchantCatlog.setTagid(null);
					totalCount = merchantService
							.getBrandCatlogCount(merchantCatlog);
					pager = PagerHelper.getPager(Integer.parseInt(currentPage),
							totalCount, pageSize);
					seIds = merchantService.getBrandCatlog(merchantCatlog,
							pager);
				}
			}
			listForm = brandCatlogService.getGoodsFromIds(seIds);
		} else {
			listForm = brandCatlogService.getGoodsFromIds(searchListids);
		}

		request.setAttribute("listBrands", listForm);

		Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
				.get(REGION_CATLOG);

		Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

		Map<Long, List<RegionCatlog>> property_catlog = null;
		
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
					catlog_extTag, regionTag, region_extTag, null, BRAND_URL);
			request.setAttribute("listRegion", listRegion);
		}

		if (catlog != null && !"".equals(catlog)) {
			List<RegionCatlog> listProperty = property_catlog.get(Long
					.parseLong(catlog));
			CatlogUtils.setCatlogUrl(false, listProperty, catlogTag,
					catlog_extTag, regionTag, region_extTag, null, BRAND_URL);
			request.setAttribute("listProperty", listProperty);
		}
		List<RegionCatlog> listParentProperty = property_catlog.get(0L);
		List<RegionCatlog> listParentRegion = map.get(0L);

		if (merchantCatlog.isNull()) {
			if (listParentRegion != null && listParentRegion.size() > 0) {
				for (RegionCatlog regionCatlog : listParentRegion) {
					CatlogUtils.setInitUrl(true, regionCatlog, BRAND_URL);
				}
			}

			if (listParentProperty != null && listParentProperty.size() > 0) {
				for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, BRAND_URL);
				}
			}

		} else {
			CatlogUtils.setCatlogUrl(true, listParentRegion, catlogTag,
					catlog_extTag, regionTag, region_extTag, null, BRAND_URL);
			CatlogUtils.setCatlogUrl(false, listParentProperty, catlogTag,
					catlog_extTag, regionTag, region_extTag, null, BRAND_URL);
		}

		request.setAttribute("regionMap", map);
		request.setAttribute("propertyMap", property_catlog);

		memCacheService.set(REGION_CATLOG, regionMap);
		return "/brand/listBrands";
	}

	@RequestMapping("/brand/mainSearchBrandsByProperty.do")
	public String mainSearchBrandList(ModelMap model, HttpServletRequest request) {
		String pageParam = request.getParameter("param");
		String ids = request.getParameter("ids");
		List<Long> listIds = new LinkedList<Long>();
		String idStrings[] = ids.split(",");
		for (String string : idStrings) {
			listIds.add(Long.parseLong(string));
		}
		List<MerchantForm> listForm = brandCatlogService
				.getGoodsFromIds(listIds);
		request.setAttribute("listBrandForm", listForm);
		return "index/list/" + pageParam;
	}
	/**
	 * 
	 * @author janwen
	 * @time Jan 17, 2012 1:12:02 PM
	 *
	 * @param request
	 * @param response
	 * @param catlog 品牌分类
	 * @param pageNo  页码
	 */
	private void printLog(HttpServletRequest request,HttpServletResponse response,String catlog,String pageNo){
		
		Map<String, String> mapLog = LogAction.getLogMap(request, response);
		mapLog.put("action","p_list");
		mapLog.put("type","b");
		if(catlog.length() > 0){
			mapLog.put("category", catlog);
		}
		mapLog.put("p", pageNo);
		LogAction.printLog(mapLog);
	}
	public SeoService getSeoService() {
		return seoService;
	}

	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}

	public MerchantService getMerchantService() {
		return merchantService;
	}

	public void setMerchantService(MerchantService merchantService) {
		this.merchantService = merchantService;
	}
}
