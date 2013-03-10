package com.beike.action.goods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.common.search.SearchStrategy;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.seo.SeoService;
import com.beike.util.BeanUtils;
import com.beike.util.CatlogUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.constants.GoodsRelatedConstants;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;

/**
 * <p>
 * Title:商品搜索Action
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
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class GoodsSearchAction extends BaseUserAction {

	private final SearchStrategy searchStrategy = new SearchStrategy();

	private static Log log = LogFactory.getLog(GoodsSearchAction.class);

	@Autowired
	private GoodsCatlogService goodsCatlogService;

	private static String SERVICE_NAME = "goodsCatlogService";

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	private static String CITY_CATLOG = "CITY_CATLOG";

	private static int pageSize = 36;

	private String GOODS_URL = "/goods/";

	@Autowired
	private SeoService seoService;

	// 现金券相关
	boolean cashSelected = false;
	//商品代金券相关
	boolean tokenSelected = false;

	private String getUrl(StringBuilder sb, String regionTag,
			String region_extTag, String catlogTag, String catlog_extTag,
			String orderbydate, String orderbysort, String orderbyprice,
			String rangeprice, String orderbydefault) {
		if (!org.apache.commons.lang.StringUtils.isEmpty(regionTag)) {
			sb.append("region=");
			sb.append(regionTag);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(region_extTag)) {
			sb.append("regionext=");
			sb.append(region_extTag);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(catlogTag)) {
			sb.append("catlog=");
			sb.append(catlogTag);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(catlog_extTag)) {
			sb.append("catlogext=");
			sb.append(catlog_extTag);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(orderbydate)) {
			sb.append("orderbydate=");
			sb.append(orderbydate);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(orderbysort)) {
			sb.append("orderbysort=");
			sb.append(orderbysort);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(orderbyprice)) {
			sb.append("orderbyprice=");
			sb.append(orderbyprice);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(rangeprice)) {
			sb.append("rangeprice=");
			sb.append(rangeprice);
			sb.append("&");
		}
		if (!org.apache.commons.lang.StringUtils.isEmpty(orderbydefault)) {
			sb.append("orderbydefault=");
			sb.append(orderbydefault);
			sb.append("&");
		}
		return sb.toString();
	}

	boolean isCashSelected(String order) {
		if (Pattern.matches("^\\w*_(both)$", order)) {
			tokenSelected = true;
			cashSelected = true;
			return true;
		}else if(Pattern.matches("^\\w*_(cash)$", order)){
			cashSelected = true;
			return true;			
		}else if(Pattern.matches("^\\w*_(token)$", order)){
			tokenSelected = true;
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 根据属性搜索商品（地域+属性 各两级）
	 */
	@RequestMapping("/goods/searchGoodsByPropertyExplore.do")
	public Object searchGoodsByPropertyExplore(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {

		// 设置urlcookie
		try {
			super.setCookieUrl(request, response);
			try {
				replaceScript(request);
			} catch (Exception e) {
				return new ModelAndView(
						"redirect:http://www.qianpin.com/goods/searchGoodsByProperty.do");
			}
			// ////////////////////////////////////////////////////////////////////////
			/**
			 * 补充说明：by zx.liu 这里 获取值修改为了对应的 tagEnname（标签值）
			 */

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

			String regionTag = request.getParameter("region");
			//特色标签
            String featureTag = request.getParameter("featuretag");
            //获取seo英文名对应id
			if (StringUtils.isNumber(regionTag)) {
				regionTag = seoService.getRegionENName(regionTag);
			}
			String region = seoService.getRegionId(regionTag,cityid,"0");
			String region_extTag = request.getParameter("regionext");
			if (StringUtils.isNumber(region_extTag)) {
				region_extTag = seoService.getRegionENName(region_extTag);
			}
			String region_ext = seoService.getRegionId(region_extTag,cityid,region);
			String catlogTag = request.getParameter("catlog");
			if (StringUtils.isNumber(catlogTag)) {
				catlogTag = seoService.getTagENName(catlogTag);
			}
			String catlog = seoService.getTagId(catlogTag);
			RegionCatlog rc = null;
			if(StringUtils.validNull(catlog)){
				  rc = seoService.getFeatureTag(featureTag, new Long(catlog));
			}
           
			String catlog_extTag = request.getParameter("catlogext");
			if (StringUtils.isNumber(catlog_extTag)) {
				catlog_extTag = seoService.getTagENName(catlog_extTag);
			}
			String catlog_ext = seoService.getTagId(catlog_extTag);
			// ////////////////////////////////////////////////////////////////////////////////////////////
			// 排序参数
			String orderbydate = request.getParameter("orderbydate");
			String orderbysort = request.getParameter("orderbysort");
			String orderbyprice = request.getParameter("orderbyprice");
			String rangeprice = request.getParameter("rangeprice");
			String orderbydefault = request.getParameter("orderbydefault");
			String orderbyrating = request.getParameter("orderbyrating");
			String orderbydiscount = request.getParameter("orderbydiscount");

			// 今日新品
			String isnew = request.getParameter("isnew");
			log.debug("商品搜索条件：region:" + region + "  regionext:" + region_ext
					+ " catlog:" + catlog + " catlog_ext:" + catlog_ext
					+ "orderbydate:" + orderbydate + " orderbysort:"
					+ orderbysort + " orderbyprice:" + orderbyprice
					+ " rangeprice:" + rangeprice + " orderbyrating:"
					+ orderbyrating + " orderbydiscount:" + orderbydiscount);

			// 获得城市中文信息
			String cityStr = IPSeeker.getCityByStr(city.trim());
			request.setAttribute("CITY_CHINESE", cityStr);

			GoodsCatlog goodsCatLog = new GoodsCatlog();

			cashSelected = false;
			tokenSelected = false;

			//排序处理
			if (StringUtils.validNull(orderbydate) && isCashSelected(orderbydate)) {
				orderbydate = orderbydate.substring(0, orderbydate.indexOf("_"));
			} else if (StringUtils.validNull(orderbysort) && isCashSelected(orderbysort)) {
				orderbysort = orderbysort.substring(0, orderbysort.indexOf("_"));
			} else if (StringUtils.validNull(orderbyprice) && isCashSelected(orderbyprice)) {
				orderbyprice = orderbyprice.substring(0, orderbyprice.indexOf("_"));
			} else if (StringUtils.validNull(orderbydefault) && isCashSelected(orderbydefault)) {
				orderbydefault = orderbydefault.substring(0, orderbydefault.indexOf("_"));
			} else if (StringUtils.validNull(orderbyrating) && isCashSelected(orderbyrating)) {
				orderbyrating = orderbyrating.substring(0, orderbyrating.indexOf("_"));
			} else if (StringUtils.validNull(orderbydiscount) && isCashSelected(orderbydiscount)) {
				orderbydiscount = orderbydiscount.substring(0, orderbydiscount.indexOf("_"));
			}

			if (cashSelected) {
				request.setAttribute("cashSelected", "cash");
			}
			if(tokenSelected){
				request.setAttribute("tokenSelected", "token");
			}
			goodsCatLog.setCashSelected(cashSelected);
			goodsCatLog.setTokenSelected(tokenSelected);
			// 今日新品
			if ("1".equals(isnew)) {
				goodsCatLog.setIsNew(true);
				GOODS_URL = "/todaygoods/";
			}else{
				goodsCatLog.setIsNew(false);
				GOODS_URL = "/goods/";
			}
			
			
			//偶数的为 B版测试页面
			goodsCatLog.setNewVersion(true);
			request.setAttribute("isnewversion", true);
			

			// 现金券相关
			// 设置城市id
			goodsCatLog.setCityid(cityid);

			Map<String, Object> rMap = null;
			if (region != null) {
				rMap = goodsCatlogService.getCityIdByRegionId(region);
				if (rMap != null) {
					String areaname = (String) rMap.get("areaname");
					String refer = request.getRequestURL().toString();
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + areaname
							+ ".qianpin.com/goods/searchGoodsByProperty.do?");
					;

					// 商品所在城市访问路径前缀
					String xcity = "http://" + areaname;
					if (areaname != null && !refer.startsWith(xcity)) {
						getUrl(sb, regionTag, region_extTag, catlogTag,
								catlog_extTag, orderbydate, orderbysort,
								orderbyprice, rangeprice, orderbydefault);
						String returnStr = sb.substring(0, sb.lastIndexOf("&"));
						return returnStr;
					}

				}
			}
			String catlog_value = "";
			try {

				if (StringUtils.validNull(region)) {
					goodsCatLog.setRegionid(Long.parseLong(region));
					catlog_value = regionTag;
				}
				if (StringUtils.validNull(region_ext)) {
					goodsCatLog.setRegionextid(Long.parseLong(region_ext));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + region_extTag;
					} else {
						catlog_value = region_extTag;
					}
				}
				if (StringUtils.validNull(catlog)) {
					goodsCatLog.setTagid(Long.parseLong(catlog));
					// 记录类别,加入日志
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + catlogTag;
					} else {
						catlog_value = catlogTag;
					}
				} else {
					catlog = "";
				}
				if (StringUtils.validNull(catlog_ext)) {
					goodsCatLog.setTagextid(Long.parseLong(catlog_ext));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + catlog_extTag;
					} else {
						catlog_value = catlog_extTag;
					}

				} else {
					catlog_ext = "";
				}
				// 价格 条件
				if (StringUtils.validNull(rangeprice)) {
					goodsCatLog.setRangeprice(Double.valueOf(rangeprice));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + rangeprice;
					} else {
						catlog_value = rangeprice;
					}
				}

				// 绑定排序条件
				if (StringUtils.validNull(orderbydate)) {
					goodsCatLog.setOrderbydate(orderbydate);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydate"
								+ orderbydate;
					} else {
						catlog_value = "orderbydate" + orderbydate;
					}
				} else if (StringUtils.validNull(orderbysort)) {
					goodsCatLog.setOrderbysort(orderbysort);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbysort"
								+ orderbysort;
					} else {
						catlog_value = "orderbysort" + orderbysort;
					}
				} else if (StringUtils.validNull(orderbyprice)) {
					goodsCatLog.setOrderbyprice(orderbyprice);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbyprice"
								+ orderbyprice;
					} else {
						catlog_value = "orderbyprice" + orderbyprice;
					}
				} else if (StringUtils.validNull(orderbydefault)) {
					goodsCatLog.setOrderbydefault(orderbydefault);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydefault"
								+ orderbydefault;
					} else {
						catlog_value = "orderbydefault" + orderbydefault;
					}
				} else if (StringUtils.validNull(orderbyrating)) {
					goodsCatLog.setOrderbyrating(orderbyrating);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbyrating"
								+ orderbyrating;
					} else {
						catlog_value = "orderbyrating" + orderbyrating;
					}
				} else if (StringUtils.validNull(orderbydiscount)) {
					goodsCatLog.setOrderbydiscount(orderbydiscount);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydiscount"
								+ orderbydiscount;
					} else {
						catlog_value = "orderbydiscount" + orderbydiscount;
					}
				} else {
					// 今日新品默认按发布时间逆序
					if (goodsCatLog.getIsNew()) {
						goodsCatLog.setOrderbydate("desc");
						if (catlog_value.length() > 0) {
							catlog_value = catlog_value + "," + "orderbydate"
									+ orderbydate;
						} else {
							catlog_value = "orderbydatenull";
						}
					} else {
						goodsCatLog.setOrderbydefault("asc");
						if (catlog_value.length() > 0) {
							catlog_value = catlog_value + ","
									+ "orderbydefault" + orderbydefault;
						} else {
							catlog_value = "orderbydefaultnull";
						}
					}
				}
			} catch (Exception e) {
				request.setAttribute("ERRMSG", "查询条件输入有误!");
				e.printStackTrace();
				return new ModelAndView("redirect:../500.html");
			}

			// 自动根据策略查找 进行商品查询
			// TODO:今后查询策略的配置 可放到数据库中，让运营人员选择 各种策略查询
			searchStrategy.setService(request, SERVICE_NAME);

			// 当前页
			String currentPage = request.getParameter("cpage");

			if (!StringUtils.validNull(currentPage)) {
				currentPage = "1";
			}
			request.setAttribute("cpage", currentPage);
			// 加入统计代码 by janwen at 2012-1-17 11:49:25
			printLog(request, response, catlog_value, currentPage);
			// 计算分页
			int totalCount = 0;
			List<Long> searchListids = null;
			Pager pager = null;
			if(rc != null && rc.getCatlogid() != null){
				goodsCatLog.setFeaturetagid(rc.getCatlogid());
			}
			Object[] data = getMemData(goodsCatLog, currentPage,true);
			totalCount = (Integer) data[0];
			pager = (Pager) data[1];
			searchListids = (List<Long>) data[2];

			List<GoodsForm> listGoodsForm = null;
			if (searchListids == null || searchListids.size() == 0) {
				// 假如没有查出数据的默认列表
				listGoodsForm = (List<GoodsForm>) memCacheService.get(city
						+ "_hot_good_new");
				if (listGoodsForm == null) {
					List<Long> seIds = goodsCatlogService.getCatlogRank(
							goodsCatLog, pager);
					listGoodsForm = goodsCatlogService
							.getGoodsFormFromId(seIds);
					if (listGoodsForm != null) {
						memCacheService.set(city + "_hot_good_new", listGoodsForm);
					}
				}

			} else {
				listGoodsForm = goodsCatlogService
						.getGoodsFormFromId(searchListids);
			}

			request.setAttribute("pager", pager);
			request.setAttribute("listGoods", listGoodsForm);

			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(PROPERTY_CATLOG);

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
			
			if (StringUtils.validNull(region)) {
				List<RegionCatlog> listRegion = map.get(Long.parseLong(region));
				CatlogUtils.setCatlogUrl(true, listRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				request.setAttribute("listRegion", listRegion);
				// 获得地域二级中文名称
				if (region_ext != null && !"".equals(region_ext)) {
					String regionExtName = getCatlogName(listRegion, Long
							.parseLong(region_ext));
					request.setAttribute("title_regionExtName", regionExtName);
				}
			}

			List<RegionCatlog> featuretagslist = null;
			if (StringUtils.validNull(catlog)) {
				List<RegionCatlog> listProperty = property_catlog.get(Long.parseLong(catlog));
				CatlogUtils.setCatlogUrl(false, listProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag,
						rangeprice, GOODS_URL);
				
				request.setAttribute("listProperty", listProperty);
				// 获取特色标签
				Map<Long, List<RegionCatlog>> featuretagsmap = (Map<Long, List<RegionCatlog>>) memCacheService
						.get("LABEL_CATLOG");
				if (featuretagsmap != null) {
					featuretagslist = featuretagsmap
							.get(new Long(catlog));
					if(featuretagslist != null){
						CatlogUtils.setFeatureCatlogUrl(featuretagslist, catlogTag,catlog_extTag,regionTag,region_extTag,rangeprice, GOODS_URL);
					}
				}

				// 获得属性二级中文名称
				if (StringUtils.validNull(catlog_ext)) {
					String catlogExtName = getCatlogName(property_catlog
							.get(Long.parseLong(catlog)), Long
							.parseLong(catlog_ext));
					request.setAttribute("title_catlogExtName", catlogExtName);
				}
			}

			List<RegionCatlog> listParentRegion = map.get(0L);
			// 获得地域一级属性中文名称
			if (StringUtils.validNull(region)) {
				String regionName = getCatlogName(listParentRegion, Long
						.parseLong(region));
				request.setAttribute("title_regionName", regionName);
			}
			List<RegionCatlog> listParentProperty = property_catlog.get(0L);
			// 获得属性一级属性中文名称
			if (StringUtils.validNull(catlog)) {
				String catlogName = getCatlogName(listParentProperty, Long
						.parseLong(catlog));
				request.setAttribute("title_catlogName", catlogName);
			}

			if (goodsCatLog.isNull()) {
				if (listParentRegion != null && listParentRegion.size() > 0) {
					for (RegionCatlog regionCatlog : listParentRegion) {
						CatlogUtils.setInitUrl(true, regionCatlog, GOODS_URL);
					}
				}

				// 如果搜索条件全为空
				for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, GOODS_URL);

				}

			} else {
				CatlogUtils.setCatlogUrl(true, listParentRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				CatlogUtils.setCatlogUrl(false, listParentProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);
			}
			request.setAttribute("listParentProperty", listParentProperty);
			
			//价格区间
			String priceCatlog = CatlogUtils.getPriceCatlog(catlogTag,
					catlog_extTag, regionTag, region_extTag, GOODS_URL);

			// 计算商品分类、分商圈数量 add by qiaowb 2012-03-25
			priceCatlog = calculateCatlogCount(request, goodsCatLog, map,
					property_catlog, priceCatlog, featuretagslist);

			// 商圈
			request.setAttribute("regionMap", map);
			//属性
			request.setAttribute("propertyMap", property_catlog);
			//价格区间
			request.setAttribute("priceCatlog", priceCatlog);
			//标签
			request.setAttribute("featuretags", featuretagslist);
			
			memCacheService.set(REGION_CATLOG, regionMap);
			return "/goods/listGoods_b";
		} catch (Exception e) {
			e.printStackTrace();
			log.error("sb sb sb a", e);
			return new ModelAndView("redirect:../500.html");
		}
	}
	

	/**
	 * 根据属性搜索商品（地域+属性 各两级）
	 */
	@RequestMapping("/goods/searchGoodsByProperty.do")
	public Object searchGoodsByProperty(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {

		// 设置urlcookie
		try {
			super.setCookieUrl(request, response);
			try {
				replaceScript(request);
			} catch (Exception e) {
				return new ModelAndView(
						"redirect:http://www.qianpin.com/goods/searchGoodsByProperty.do");
			}
			// ////////////////////////////////////////////////////////////////////////
			/**
			 * 补充说明：by zx.liu 这里 获取值修改为了对应的 tagEnname（标签值）
			 */

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

			String regionTag = request.getParameter("region");
			//特色标签
            String featureTag = request.getParameter("featuretag");
            //获取seo英文名对应id
			if (StringUtils.isNumber(regionTag)) {
				regionTag = seoService.getRegionENName(regionTag);
			}
			String region = seoService.getRegionId(regionTag,cityid,"0");
			String region_extTag = request.getParameter("regionext");
			if (StringUtils.isNumber(region_extTag)) {
				region_extTag = seoService.getRegionENName(region_extTag);
			}
			String region_ext = seoService.getRegionId(region_extTag,cityid,region);
			String catlogTag = request.getParameter("catlog");
			if (StringUtils.isNumber(catlogTag)) {
				catlogTag = seoService.getTagENName(catlogTag);
			}
			String catlog = seoService.getTagId(catlogTag);
			RegionCatlog rc = null;
			if(StringUtils.validNull(catlog)){
				  rc = seoService.getFeatureTag(featureTag, new Long(catlog));
			}
           
			String catlog_extTag = request.getParameter("catlogext");
			if (StringUtils.isNumber(catlog_extTag)) {
				catlog_extTag = seoService.getTagENName(catlog_extTag);
			}
			String catlog_ext = seoService.getTagId(catlog_extTag);
			// ////////////////////////////////////////////////////////////////////////////////////////////
			// 排序参数
			String orderbydate = request.getParameter("orderbydate");
			String orderbysort = request.getParameter("orderbysort");
			String orderbyprice = request.getParameter("orderbyprice");
			String rangeprice = request.getParameter("rangeprice");
			String orderbydefault = request.getParameter("orderbydefault");
			String orderbyrating = request.getParameter("orderbyrating");
			String orderbydiscount = request.getParameter("orderbydiscount");

			// 今日新品
			String isnew = request.getParameter("isnew");
			log.debug("商品搜索条件：region:" + region + "  regionext:" + region_ext
					+ " catlog:" + catlog + " catlog_ext:" + catlog_ext
					+ "orderbydate:" + orderbydate + " orderbysort:"
					+ orderbysort + " orderbyprice:" + orderbyprice
					+ " rangeprice:" + rangeprice + " orderbyrating:"
					+ orderbyrating + " orderbydiscount:" + orderbydiscount);

			// 获得城市中文信息
			String cityStr = IPSeeker.getCityByStr(city.trim());
			request.setAttribute("CITY_CHINESE", cityStr);

			GoodsCatlog goodsCatLog = new GoodsCatlog();

			cashSelected = false;
			tokenSelected = false;

			//排序处理
			if (StringUtils.validNull(orderbydate) && isCashSelected(orderbydate)) {
				orderbydate = orderbydate.substring(0, orderbydate.indexOf("_"));
			} else if (StringUtils.validNull(orderbysort) && isCashSelected(orderbysort)) {
				orderbysort = orderbysort.substring(0, orderbysort.indexOf("_"));
			} else if (StringUtils.validNull(orderbyprice) && isCashSelected(orderbyprice)) {
				orderbyprice = orderbyprice.substring(0,orderbyprice.indexOf("_"));
			} else if (StringUtils.validNull(orderbydefault)&& isCashSelected(orderbydefault)) {
				orderbydefault = orderbydefault.substring(0,orderbydefault.indexOf("_"));
			} else if (StringUtils.validNull(orderbyrating) && isCashSelected(orderbyrating)) {
				orderbyrating = orderbyrating.substring(0,orderbyrating.indexOf("_"));
			} else if (StringUtils.validNull(orderbydiscount) && isCashSelected(orderbydiscount)) {
				orderbydiscount = orderbydiscount.substring(0, orderbydiscount.indexOf("_"));
			}

			if (cashSelected) {
				request.setAttribute("cashSelected", "cash");
			}
			if(tokenSelected){
				request.setAttribute("tokenSelected", "token");
			}
			
			goodsCatLog.setCashSelected(cashSelected);
			goodsCatLog.setTokenSelected(tokenSelected);

			// 今日新品
			if ("1".equals(isnew)) {
				goodsCatLog.setIsNew(true);
				GOODS_URL = "/todaygoods/";
			}else{
				goodsCatLog.setIsNew(false);
				GOODS_URL = "/goods/";
			}
			
			
			//偶数的为 B版测试页面
			goodsCatLog.setNewVersion(false);
//			if(isIpEven){
//				goodsCatLog.setNewVersion(isIpEven);
//				request.setAttribute("isnewversion", true);
//			}
			

			// 现金券相关
			// 设置城市id
			goodsCatLog.setCityid(cityid);

			Map<String, Object> rMap = null;
			if (region != null) {
				rMap = goodsCatlogService.getCityIdByRegionId(region);
				if (rMap != null) {
					String areaname = (String) rMap.get("areaname");
					String refer = request.getRequestURL().toString();
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + areaname
							+ ".qianpin.com/goods/searchGoodsByProperty.do?");
					;

					// 商品所在城市访问路径前缀
					String xcity = "http://" + areaname;
					if (areaname != null && !refer.startsWith(xcity)) {
						getUrl(sb, regionTag, region_extTag, catlogTag,
								catlog_extTag, orderbydate, orderbysort,
								orderbyprice, rangeprice, orderbydefault);
						String returnStr = sb.substring(0, sb.lastIndexOf("&"));
						return returnStr;
					}

				}
			}
			String catlog_value = "";
			try {

				if (StringUtils.validNull(region)) {
					goodsCatLog.setRegionid(Long.parseLong(region));
					catlog_value = regionTag;
				}
				if (StringUtils.validNull(region_ext)) {
					goodsCatLog.setRegionextid(Long.parseLong(region_ext));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + region_extTag;
					} else {
						catlog_value = region_extTag;
					}
				}
				if (StringUtils.validNull(catlog)) {
					goodsCatLog.setTagid(Long.parseLong(catlog));
					// 记录类别,加入日志
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + catlogTag;
					} else {
						catlog_value = catlogTag;
					}
				} else {
					catlog = "";
				}
				if (StringUtils.validNull(catlog_ext)) {
					goodsCatLog.setTagextid(Long.parseLong(catlog_ext));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + catlog_extTag;
					} else {
						catlog_value = catlog_extTag;
					}

				} else {
					catlog_ext = "";
				}
				// 价格 条件
				if (StringUtils.validNull(rangeprice)) {
					goodsCatLog.setRangeprice(Double.valueOf(rangeprice));
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + rangeprice;
					} else {
						catlog_value = rangeprice;
					}
				}

				// 绑定排序条件
				if (StringUtils.validNull(orderbydate)) {
					goodsCatLog.setOrderbydate(orderbydate);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydate"
								+ orderbydate;
					} else {
						catlog_value = "orderbydate" + orderbydate;
					}
				} else if (StringUtils.validNull(orderbysort)) {
					goodsCatLog.setOrderbysort(orderbysort);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbysort"
								+ orderbysort;
					} else {
						catlog_value = "orderbysort" + orderbysort;
					}
				} else if (StringUtils.validNull(orderbyprice)) {
					goodsCatLog.setOrderbyprice(orderbyprice);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbyprice"
								+ orderbyprice;
					} else {
						catlog_value = "orderbyprice" + orderbyprice;
					}
				} else if (StringUtils.validNull(orderbydefault)) {
					goodsCatLog.setOrderbydefault(orderbydefault);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydefault"
								+ orderbydefault;
					} else {
						catlog_value = "orderbydefault" + orderbydefault;
					}
				} else if (StringUtils.validNull(orderbyrating)) {
					goodsCatLog.setOrderbyrating(orderbyrating);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbyrating"
								+ orderbyrating;
					} else {
						catlog_value = "orderbyrating" + orderbyrating;
					}
				} else if (StringUtils.validNull(orderbydiscount)) {
					goodsCatLog.setOrderbydiscount(orderbydiscount);
					if (catlog_value.length() > 0) {
						catlog_value = catlog_value + "," + "orderbydiscount"
								+ orderbydiscount;
					} else {
						catlog_value = "orderbydiscount" + orderbydiscount;
					}
				} else {
					// 今日新品默认按发布时间逆序
					if (goodsCatLog.getIsNew()) {
						goodsCatLog.setOrderbydate("desc");
						if (catlog_value.length() > 0) {
							catlog_value = catlog_value + "," + "orderbydate"
									+ orderbydate;
						} else {
							catlog_value = "orderbydatenull";
						}
					} else {
						goodsCatLog.setOrderbydefault("asc");
						if (catlog_value.length() > 0) {
							catlog_value = catlog_value + ","
									+ "orderbydefault" + orderbydefault;
						} else {
							catlog_value = "orderbydefaultnull";
						}
					}
				}
			} catch (Exception e) {
				request.setAttribute("ERRMSG", "查询条件输入有误!");
				e.printStackTrace();
				return new ModelAndView("redirect:../500.html");
			}

			// 自动根据策略查找 进行商品查询
			// TODO:今后查询策略的配置 可放到数据库中，让运营人员选择 各种策略查询
			searchStrategy.setService(request, SERVICE_NAME);

			// 当前页
			String currentPage = request.getParameter("cpage");

			if (!StringUtils.validNull(currentPage)) {
				currentPage = "1";
			}
			request.setAttribute("cpage", currentPage);
			// 加入统计代码 by janwen at 2012-1-17 11:49:25
			printLog(request, response, catlog_value, currentPage);
			// 计算分页
			int totalCount = 0;
			List<Long> searchListids = null;
			Pager pager = null;
			if(rc != null && rc.getCatlogid() != null){
				goodsCatLog.setFeaturetagid(rc.getCatlogid());
			}
			Object[] data = getMemData(goodsCatLog, currentPage,false);
			totalCount = (Integer) data[0];
			pager = (Pager) data[1];
			searchListids = (List<Long>) data[2];

			List<GoodsForm> listGoodsForm = null;
			if (searchListids == null || searchListids.size() == 0) {
				// 假如没有查出数据的默认列表
				listGoodsForm = (List<GoodsForm>) memCacheService.get(city
						+ "_hot_good_new");
				if (listGoodsForm == null) {
					List<Long> seIds = goodsCatlogService.getCatlogRank(
							goodsCatLog, pager);
					listGoodsForm = goodsCatlogService
							.getGoodsFormFromId(seIds);
					if (listGoodsForm != null) {
						memCacheService.set(city + "_hot_good_new", listGoodsForm);
					}
				}

			} else {
				listGoodsForm = goodsCatlogService
						.getGoodsFormFromId(searchListids);
			}

			request.setAttribute("pager", pager);
			request.setAttribute("listGoods", listGoodsForm);

			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(PROPERTY_CATLOG);

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
			
			if (StringUtils.validNull(region)) {
				List<RegionCatlog> listRegion = map.get(Long.parseLong(region));
				CatlogUtils.setCatlogUrl(true, listRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				request.setAttribute("listRegion", listRegion);
				// 获得地域二级中文名称
				if (region_ext != null && !"".equals(region_ext)) {
					String regionExtName = getCatlogName(listRegion, Long
							.parseLong(region_ext));
					request.setAttribute("title_regionExtName", regionExtName);
				}
			}

			List<RegionCatlog> featuretagslist = null;
			if (StringUtils.validNull(catlog)) {
				List<RegionCatlog> listProperty = property_catlog.get(Long.parseLong(catlog));
				CatlogUtils.setCatlogUrl(false, listProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag,
						rangeprice, GOODS_URL);
				
				request.setAttribute("listProperty", listProperty);
				// 获取特色标签
				Map<Long, List<RegionCatlog>> featuretagsmap = (Map<Long, List<RegionCatlog>>) memCacheService
						.get("LABEL_CATLOG");
				if (featuretagsmap != null) {
					featuretagslist = featuretagsmap
							.get(new Long(catlog));
					if(featuretagslist != null){
						CatlogUtils.setFeatureCatlogUrl(featuretagslist, catlogTag,catlog_extTag,regionTag,region_extTag,rangeprice, GOODS_URL);
					}
				}

				// 获得属性二级中文名称
				if (StringUtils.validNull(catlog_ext)) {
					String catlogExtName = getCatlogName(property_catlog
							.get(Long.parseLong(catlog)), Long
							.parseLong(catlog_ext));
					request.setAttribute("title_catlogExtName", catlogExtName);
				}
			}

			List<RegionCatlog> listParentRegion = map.get(0L);
			// 获得地域一级属性中文名称
			if (StringUtils.validNull(region)) {
				String regionName = getCatlogName(listParentRegion, Long
						.parseLong(region));
				request.setAttribute("title_regionName", regionName);
			}
			List<RegionCatlog> listParentProperty = property_catlog.get(0L);
			// 获得属性一级属性中文名称
			if (StringUtils.validNull(catlog)) {
				String catlogName = getCatlogName(listParentProperty, Long
						.parseLong(catlog));
				request.setAttribute("title_catlogName", catlogName);
			}

			if (goodsCatLog.isNull()) {
				if (listParentRegion != null && listParentRegion.size() > 0) {
					for (RegionCatlog regionCatlog : listParentRegion) {
						CatlogUtils.setInitUrl(true, regionCatlog, GOODS_URL);
					}
				}

				// 如果搜索条件全为空
				for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, GOODS_URL);

				}

			} else {
				CatlogUtils.setCatlogUrl(true, listParentRegion, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);

				CatlogUtils.setCatlogUrl(false, listParentProperty, catlogTag,
						catlog_extTag, regionTag, region_extTag, rangeprice,
						GOODS_URL);
			}
			request.setAttribute("listParentProperty", listParentProperty);
			
			//价格区间
			String priceCatlog = CatlogUtils.getPriceCatlog(catlogTag,
					catlog_extTag, regionTag, region_extTag, GOODS_URL);

			// 计算商品分类、分商圈数量 add by qiaowb 2012-03-25
			priceCatlog = calculateCatlogCount(request, goodsCatLog, map,
					property_catlog, priceCatlog, featuretagslist);

			// 商圈
			request.setAttribute("regionMap", map);
			//属性
			request.setAttribute("propertyMap", property_catlog);
			//价格区间
			request.setAttribute("priceCatlog", priceCatlog);
			//标签
			request.setAttribute("featuretags", featuretagslist);
			
			memCacheService.set(REGION_CATLOG, regionMap);
			return "/goods/listGoods";
		} catch (Exception e) {
			e.printStackTrace();
			log.error("sb sb sb a", e);
			return new ModelAndView("redirect:../500.html");
		}
	}

	private final Object lock = new Object();

	public Object[] getMemData(GoodsCatlog goodsCatLog, String currentPage,boolean isEven) {
		int finalSize=0;
		finalSize=pageSize;
		int hashCode = goodsCatLog.hashCode();
		int time = 60 * 15;
		String goods_catlog_key="B_NewGoodsCatlog_"+hashCode;
		String goods_catlog_key_currentpage="B_NewGoodsCatlogId_" + currentPage + "_" + hashCode;
		if(isEven){
			finalSize=45;
			goods_catlog_key="B_NewGoodsCatlog_NEW_"+hashCode;
			goods_catlog_key_currentpage="B_NewGoodsCatlogId_NEW_" + currentPage + "_" + hashCode;
		}
		
		
		
		
		Integer totalCount = (Integer) memCacheService.get(goods_catlog_key);
		boolean totalFlag = false;
		List<Long> searchListids = (List<Long>) memCacheService
				.get(goods_catlog_key_currentpage);
		if (totalCount == null) {
			synchronized (lock) {
				totalCount = goodsCatlogService.getCatlogCount(goodsCatLog);
			}
			if (totalCount == null) {
				totalCount = 0;
			}
			totalFlag = true;

		}
		
		
		Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
				totalCount, finalSize);
		if (searchListids == null) {
			synchronized (lock) {
				searchListids = searchStrategy.getCatlog(goodsCatLog, pager);
			}
			if (searchListids == null) {
				memCacheService.set(goods_catlog_key_currentpage, new ArrayList<Long>(), time);
			} else {
				log.debug("searchListids=" + searchListids);
				memCacheService.set(goods_catlog_key_currentpage, searchListids, time);
			}
		}
		if (totalFlag) {
			memCacheService.set(goods_catlog_key, totalCount, time);
		}
		Object[] obj = new Object[3];
		obj[0] = totalCount;
		obj[1] = pager;
		obj[2] = searchListids;
		return obj;
	}

	/**
	 * 首页商品列表
	 */
	@RequestMapping("/goods/mainSearchGoodsByProperty.do")
	public String mainSearchGoodsList(ModelMap modelMap,
			HttpServletRequest request) {
		String pageParam = request.getParameter("param");
		String ids = request.getParameter("ids");
		List<Long> listIds = new LinkedList<Long>();
		String idStrings[] = ids.split(",");
		for (String string : idStrings) {
			listIds.add(Long.parseLong(string));
		}
		List<GoodsForm> listGoodsForm = goodsCatlogService
				.getGoodsFormFromId(listIds);
		request.setAttribute("listGoodsForm", listGoodsForm);
		return "index/list/" + pageParam;
	}

	public GoodsCatlogService getGoodsCatlogService() {
		return goodsCatlogService;
	}

	public void setGoodsCatlogService(GoodsCatlogService goodsCatlogService) {
		this.goodsCatlogService = goodsCatlogService;
	}

	public SeoService getSeoService() {
		return seoService;
	}

	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}

	/**
	 * 
	 * @author janwen
	 * @time Jan 17, 2012 1:07:29 PM
	 * 
	 * @param request
	 * @param response
	 * @param catlog
	 *            类别
	 * @param pageNo
	 *            页码
	 */
	private void printLog(HttpServletRequest request,
			HttpServletResponse response, String catlog, String pageNo) {
		Map<String, String> mapLog = LogAction.getLogMap(request, response);
		mapLog.put("action", "p_list");
		mapLog.put("type", "g");
		mapLog.put("category", catlog);
		mapLog.put("p", pageNo);
		LogAction.printLog(mapLog);
	}
	
	/**
	 * 计算分类数量
	 * @param goodsCatLog
	 * @param regionMap
	 * @param propertyMap
	 */
	private String calculateCatlogCount(HttpServletRequest request,
			GoodsCatlog goodsCatLog, Map<Long, List<RegionCatlog>> regionMap,
			Map<Long, List<RegionCatlog>> propertyMap, String priceCatlog,
			List<RegionCatlog> featuretagslist) {
		Map<String, Integer> mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid());
		//主缓存值取不到，取备份值 add by qiaoweibo 2012-08-20
		if(mapCatlogCount == null){
			mapCatlogCount = (Map<String, Integer>) memCacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + goodsCatLog.getCityid() + "_b");
		}
		if (mapCatlogCount == null || mapCatlogCount.get(String.valueOf(goodsCatLog.getCityid()))==null) {
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
		if(goodsCatLog.getCashSelected() && goodsCatLog.getTokenSelected()){
			cashKey = "both";
		}else if(goodsCatLog.getCashSelected()){
			cashKey = "cash1";
		}else if(goodsCatLog.getTokenSelected()){
			cashKey = "token";
		}
		
		//特色标签
		String biaoqianKey = "";
		if(goodsCatLog.getFeaturetagid()!=null){
			biaoqianKey = String.valueOf(goodsCatLog.getFeaturetagid());
		}
		
		//新品数量
		Integer newGoodsCount = 0;
		
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
			newGoodsCount = mapCatlogCount.get(goodsCatLog.getTagid() + "|new1");
			if(newGoodsCount == null){
				newGoodsCount = 0;
			}
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
		}else{
			//未选择二级
			newGoodsCount = mapCatlogCount.get("tagAll|new1");
			if(newGoodsCount == null){
				newGoodsCount = 0;
			}
/*			List<RegionCatlog> lstPropertyTmpA = propertyMap.get(0L);
			if(lstPropertyTmpA!=null && lstPropertyTmpA.size()>0){
				for(RegionCatlog tagA : lstPropertyA){
					List<RegionCatlog> lstPropertyB = propertyMap.get(tagA.getCatlogid());
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
							tmpKey.append(tagA.getCatlogid())
								.append("|").append(tagB.getCatlogid());
							
							if(bufKey.length() != 0){
								tmpKey.append(bufKey);
							}
							
							Integer goodsCount = mapCatlogCount.get(tmpKey.toString());
							if(goodsCount == null){
								goodsCount = 0;
							}
							tagB.setCount(String.valueOf(goodsCount));
						}
					}
				}
			}*/
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
		
		//特色标签
		int allBiaoqian = 0;
		if(featuretagslist!=null && featuretagslist.size()>0){
			for(RegionCatlog featureTag : featuretagslist){
				StringBuilder bufKey = new StringBuilder();
				
				if(org.apache.commons.lang.StringUtils.isNotEmpty(propertyKey)){
					bufKey.append(propertyKey).append("|");
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(regionKey)){
					bufKey.append(regionKey).append("|");
				}
				if(org.apache.commons.lang.StringUtils.isNotEmpty(priceKey)){
					bufKey.append(priceKey).append("|");
				}
				bufKey.append("B").append(featureTag.getCatlogid());
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
				featureTag.setCount(String.valueOf(goodsCount));
				allBiaoqian = allBiaoqian + goodsCount;
			}
		}
		
		request.setAttribute("allPropertyA", allPropertyA);
		request.setAttribute("allPropertyB", allPropertyB);
		request.setAttribute("allRegionA", allRegionA);
		request.setAttribute("allRegionB", allRegionB);
		request.setAttribute("allPrice", allPrice);
		request.setAttribute("allBiaoqian", allBiaoqian);
		request.setAttribute("newGoodsCount", newGoodsCount);
		return priceCatlog;
	}
}