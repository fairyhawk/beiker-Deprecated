package com.beike.action.coupon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.beike.action.merchant.BrandSearchAction;
import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.CouponCatlogService;
import com.beike.common.listener.CatlogListener;
import com.beike.common.search.SearchStrategy;
import com.beike.dao.coupon.CouponDao;
import com.beike.entity.catlog.CouponCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.user.User;
import com.beike.form.CouponForm;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.form.SmsInfo;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.common.SmsService;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.MerchantService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.service.seo.SeoService;
import com.beike.util.BeanUtils;
import com.beike.util.CatlogUtils;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JsonUtil;

/**
 * <p>
 * Title:优惠券Controller
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
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class CouponAction extends BaseUserAction {

	private final SearchStrategy searchStrategy = new SearchStrategy();

	private static Log log = LogFactory.getLog(BrandSearchAction.class);

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	private static int pageSize = 48;

	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static final String SMS_TYPE = "15";

	private final PropertyUtil propertyUtil = PropertyUtil.getInstance("project");

	private final String SERVICE_NAME = "couponCatlogService";

	@Autowired
	private CouponCatlogService couponCatlogService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private CouponDao couponDao;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private SeoService seoService;

	@Autowired
	private ShopsBaoService shopsBaoService;

	private static String CITY_CATLOG = "CITY_CATLOG";

	private static String COUPON_URL = "/coupon/";

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/coupon/getCouponById.do")
	public Object getCouponById(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置urlcookie
			super.setCookieUrl(request, response);
			String couponid = request.getParameter("couponid");
			if (couponid == null || "".equals(couponid)) {
				request.setAttribute("ERRMSG", "没有找到该优惠券!");
				return new ModelAndView("redirect:../500.html");
			}

			CouponForm couponForm = null;
			try {
				couponForm = couponDao.getCouponDetailById(Integer.parseInt(couponid));
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到该优惠券!");
				return new ModelAndView("redirect:../500.html");
			}

			if (couponForm == null) {
				request.setAttribute("ERRMSG", "没有找到该优惠券!");
				return new ModelAndView("redirect:../500.html");
			}

			// 记录浏览优惠券详细
			// 获得优惠券城市
			String uppercity = couponDao.getCouponCity(Integer.parseInt(couponid));
			String city = uppercity.toLowerCase();

			// 获取当前城市ID
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}

			Long cityid = null;
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}

			response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city, CityUtils.validy));
			String staticurl = propertyUtil.getProperty("STATIC_URL");
			// 假如请求路径里 城市 和当前商品城市不一样
			String refer = request.getRequestURL().toString();
			String xcity = "http://" + city;
			if (!refer.startsWith(xcity)) {
				if ("true".equals(staticurl)) {
					return new ModelAndView("redirect:http://" + city + ".qianpin.com/coupon/" + couponid + ".html");
				} else {
					return new ModelAndView("redirect:http://" + city + ".qianpin.com/coupon/getCouponById.do?couponid=" + couponid);
				}
			}
			CouponCatlog couponCatlog = couponDao.getCouponCatlogById(Integer.parseInt(couponid));

			request.setAttribute("couponCatlog", couponCatlog);

			request.setAttribute("couponForm", couponForm);

			// 浏览次数
			Long browsecount = (Long) memCacheService.get(Constant.MEM_COUPON_BROWCOUNT + couponForm.getCouponid());
			// 下载次数
			Long downcount = (Long) memCacheService.get(Constant.MEM_COUPON_DOWNCOUNT + couponForm.getCouponid());

			if (browsecount == null) {
				browsecount = couponForm.getBrowsecounts();
			}

			int validy = 60 * 60 * 24;
			if (downcount == null) {
				downcount = couponForm.getDowncount();
				memCacheService.set(Constant.MEM_COUPON_DOWNCOUNT + couponForm.getCouponid(), downcount, validy);
			}
			// 增加浏览次数
			memCacheService.set(Constant.MEM_COUPON_BROWCOUNT + couponForm.getCouponid(), browsecount + 1, validy);

			// 下载次数浏览次数
			request.setAttribute("browsecount", browsecount);
			request.setAttribute("downcount", downcount);

			MerchantForm merchantForm = null;
			try {
				merchantForm = shopsBaoService.getMerchantDetailById(couponForm.getMerchantid());
				// merchantForm =
				// merchantService.getMerchantFormById(couponForm.getMerchantid());
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到该优惠券!");
				return new ModelAndView("redirect:../500.html");
			}

			if (merchantForm == null) {
				request.setAttribute("ERRMSG", "没有找到该优惠券!");
				return new ModelAndView("redirect:../500.html");
			}
			request.setAttribute("merchantForm", merchantForm);

			// 商圈、属性分类
			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(CatlogListener.PROPERTY_CATLOG);

			Map<Long, List<RegionCatlog>> property_catlog = null;
			// 假如memcache里没有就从数据库里查
			if (regionMap == null) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
				memCacheService.set(REGION_CATLOG, regionMap);
			}

			if (propertCatlogMap == null) {
				propertCatlogMap = BeanUtils.getCatlog(request, "propertyCatlogDao");
				property_catlog = propertCatlogMap.get(cityid);
				memCacheService.set(CatlogListener.PROPERTY_CATLOG, propertCatlogMap, 60 * 60 * 24 * 360);
			} else {
				property_catlog = propertCatlogMap.get(cityid);
			}
			// 当前城市商圈
			Map<Long, List<RegionCatlog>> curRegionMap = regionMap.get(city);
			//
			Map<Long, RegionCatlog> regionKeyMap = new HashMap<Long, RegionCatlog>();
			if (curRegionMap != null && !curRegionMap.isEmpty()) {
				for (Long regionKey : curRegionMap.keySet()) {
					List<RegionCatlog> lstRegion = curRegionMap.get(regionKey);
					if (lstRegion != null && lstRegion.size() > 0) {
						for (RegionCatlog region : lstRegion) {
							regionKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}

			Map<Long, RegionCatlog> tagKeyMap = new HashMap<Long, RegionCatlog>();
			if (property_catlog != null && !property_catlog.isEmpty()) {
				for (Long tagKey : property_catlog.keySet()) {
					List<RegionCatlog> lstTag = property_catlog.get(tagKey);
					if (lstTag != null && lstTag.size() > 0) {
						for (RegionCatlog region : lstTag) {
							tagKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}

			// 推荐商品
			List<Map<String, Object>> lstRegionIds = goodsService.getCouponRegionIds(Long.parseLong(couponid));

			// 相关分类
			List<Object[]> lstGoodsTag = new LinkedList<Object[]>();
			// 相关商圈
			List<Object[]> lstGoodsRegion = new LinkedList<Object[]>();
			// 用于处理二级地域重复
			int iNextRegionCount = 0;
			String tmpGoodsRegionId = "";
			if (lstRegionIds != null && lstRegionIds.size() > 0) {
				Map<String, Object> mapRegionIds = lstRegionIds.get(0);

				for (int i = 0; i < lstRegionIds.size(); i++) {
					Map<String, Object> catlog = lstRegionIds.get(i);
					// 分类
					if (i == 0) {
						Object[] aryGoodTag1 = new Object[3];
						RegionCatlog tag1 = tagKeyMap.get(catlog.get("tagid"));
						if (tag1 != null) {
							aryGoodTag1[0] = tag1.getRegion_enname();
							aryGoodTag1[2] = tag1.getCatlogName();
							lstGoodsTag.add(aryGoodTag1);
						}

						Object[] aryGoodTag2 = new Object[3];
						RegionCatlog tag2 = tagKeyMap.get(catlog.get("tagextid"));
						if (tag1 != null && tag2 != null) {
							aryGoodTag2[0] = tag1.getRegion_enname();
							aryGoodTag2[1] = tag2.getRegion_enname();
							aryGoodTag2[2] = tag2.getCatlogName();
							lstGoodsTag.add(aryGoodTag2);
						}
					}
					// 只有一个区域
					if (lstRegionIds.size() == 1) {
						Object[] aryGoodRegion1 = new Object[3];
						RegionCatlog region1 = regionKeyMap.get(catlog.get("regionid"));
						if (region1 != null) {
							aryGoodRegion1[0] = region1.getRegion_enname();
							aryGoodRegion1[2] = region1.getCatlogName();
							lstGoodsRegion.add(aryGoodRegion1);
						}

						Object[] aryGoodRegion2 = new Object[3];
						RegionCatlog region2 = regionKeyMap.get(catlog.get("regionextid"));
						if (region1 != null && region2 != null) {
							aryGoodRegion2[0] = region1.getRegion_enname();
							aryGoodRegion2[1] = region2.getRegion_enname();
							aryGoodRegion2[2] = region2.getCatlogName();
							lstGoodsRegion.add(aryGoodRegion2);
						}
						tmpGoodsRegionId = String.valueOf(catlog.get("regionextid"));
					} else {
						if (i == 0) {
							Object[] aryGoodRegion1 = new Object[3];
							RegionCatlog region1 = regionKeyMap.get(catlog.get("regionid"));
							if (region1 != null) {
								aryGoodRegion1[0] = region1.getRegion_enname();
								aryGoodRegion1[2] = region1.getCatlogName();
								lstGoodsRegion.add(aryGoodRegion1);
							}

							Object[] aryGoodRegion2 = new Object[3];
							RegionCatlog region2 = regionKeyMap.get(catlog.get("regionextid"));
							if (region1 != null && region2 != null) {
								aryGoodRegion2[0] = region1.getRegion_enname();
								aryGoodRegion2[1] = region2.getRegion_enname();
								aryGoodRegion2[2] = region2.getCatlogName();
								lstGoodsRegion.add(aryGoodRegion2);
							}

							iNextRegionCount = 1;
							tmpGoodsRegionId = String.valueOf(catlog.get("regionextid"));
						} else {
							Object[] aryGoodRegion = null;
							RegionCatlog region1 = regionKeyMap.get(catlog.get("regionid"));
							RegionCatlog region2 = regionKeyMap.get(catlog.get("regionextid"));
							if (region1 != null && region2 != null) {
								aryGoodRegion = new Object[3];
								aryGoodRegion[0] = region1.getRegion_enname();
								aryGoodRegion[1] = region2.getRegion_enname();
								aryGoodRegion[2] = region2.getCatlogName();
							}

							if (tmpGoodsRegionId.indexOf(String.valueOf(catlog.get("regionextid"))) < 0) {
								if (iNextRegionCount <= 1) {
									if (aryGoodRegion != null) {
										lstGoodsRegion.remove(0);
										lstGoodsRegion.add(aryGoodRegion);
									}

									tmpGoodsRegionId = tmpGoodsRegionId + "," + catlog.get("regionextid");
								}
								iNextRegionCount++;
								if (iNextRegionCount > 2) {
									tmpGoodsRegionId = null;
									break;
								}
							}
						}
					}
				}

				List<Long> lstTuijian1 = goodsService.getSaleWithGoodsIds((Long) mapRegionIds.get("regionid"), tmpGoodsRegionId, (Long) mapRegionIds.get("tagid"), cityid, 11L, "");

				// 剔除最后商品ID，查询推荐商品
				if (lstTuijian1 != null) {
					if (lstTuijian1.size() > 10) {
						lstTuijian1.remove(lstTuijian1.size() - 1);
					}
					if (lstTuijian1.size() > 0) {
						List<GoodsForm> lstTuijian1GoodsForm = goodsService.getGoodsFormByChildId(lstTuijian1);
						request.setAttribute("lstTuijian1GoodsForm", lstTuijian1GoodsForm);
					}
				}
				request.setAttribute("lstGoodsTag", lstGoodsTag);
				request.setAttribute("lstGoodsRegion", lstGoodsRegion);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*
		 * Goods topGoods = null; // 置顶的商品 topGoods =
		 * goodsService.getGoodsByBrandId(Long.parseLong(merchantForm.getId()));
		 * 
		 * // 右侧推荐 List<GoodsForm> listForm = goodsService.getTopGoodsForm();
		 * 
		 * request.setAttribute("listForm", listForm);
		 * 
		 * request.setAttribute("topGoods", topGoods);
		 * request.setAttribute("merchantForm", merchantForm);
		 */

		return "/coupon/showCouponsDetail";
	}

	@RequestMapping("/coupon/getMerchantMapList.do")
	public String getMerchantMapList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		String couponid = request.getParameter("couponid");
		if (couponid == null || "".equals(couponid)) {
			try {
				print(response, "PARAM_ERROR");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String result = "";
		Integer mid = Integer.parseInt(couponid);
		List<MerchantForm> listForm = null;
		String currentPage = request.getParameter("mpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		// 所有优惠券支持的商家总数
		int size = merchantService.getMerchantFormByCouponCount(mid);

		Pager pager = PagerHelper.getPager(Integer.valueOf(currentPage), size, 5);
		try {
			listForm = merchantService.getMerchantFormByCouponId(couponid, pager);
		} catch (Exception e) {
			e.printStackTrace();
			result = "PARAM_ERROR";
			try {
				print(response, result);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		if (listForm == null || listForm.size() == 0) {
			// 此种返回不符合常理
			try {
				print(response, "NO_MERCHANT");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<Map> list = new ArrayList<Map>();
		for (MerchantForm merchantForm : listForm) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantName", merchantForm.getMerchantname());
			map.put("addr", merchantForm.getAddr());
			map.put("buinesstime", merchantForm.getBuinesstime());
			map.put("tel", merchantForm.getTel());
			map.put("latitude", merchantForm.getLatitude());
			map.put("city", merchantForm.getCity());
			list.add(map);
		}
		Map<String, String> mpage = new HashMap<String, String>();
		mpage.put("currentPage", pager.getCurrentPage() + "");
		mpage.put("totalPage", pager.getTotalPages() + "");
		list.add(mpage);
		String jsonResult = JsonUtil.listToJson(list);

		try {
			print(response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void print(HttpServletResponse response, String content) throws IOException {
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(content);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/coupon/searchCouponByProperty.do")
	public Object searchCouponByProperty(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		// 设置urlcookie
		super.setCookieUrl(request, response);
		// ////////////////////////////////////////////////////////////////////////////
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

		// //////////////////////////////////////////////////////////////////////////////////////////////
		// 排序
		String orderbydate = request.getParameter("orderbydate");
		String orderbysort = request.getParameter("orderbysort");

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
			cityid = mapCity.get(city);
		}

		CouponCatlog couponCatlog = new CouponCatlog();
		couponCatlog.setCityid(cityid);
		try {
			if (region != null && !"".equals(region)) {
				couponCatlog.setRegionid(Long.parseLong(region));
			}
			if (region_ext != null && !"".equals(region_ext)) {
				couponCatlog.setRegionextid(Long.parseLong(region_ext));
			}
			if (catlog != null && !"".equals(catlog)) {
				couponCatlog.setTagid(Long.parseLong(catlog));
			}
			if (catlog_ext != null && !"".equals(catlog_ext)) {
				couponCatlog.setTagextid(Long.parseLong(catlog_ext));
			}

			// 排序
			if (orderbydate != null && !"".equals(orderbydate)) {
				couponCatlog.setOrderbydate(orderbydate);
			} else if (orderbysort != null && !"".equals(orderbysort)) {
				couponCatlog.setOrderbysort(orderbysort);
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "查询条件输入有误!");

			return new ModelAndView("redirect:../500.html");
		}

		searchStrategy.setService(request, SERVICE_NAME);

		// 当前页
		String currentPage = request.getParameter("cpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		// 计算分页
		int totalCount = 0;

		totalCount = couponCatlogService.getCatlogCount(couponCatlog);

		Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage), totalCount, pageSize);

		List<Long> searchListids = searchStrategy.getCatlog(couponCatlog, pager);

		// 当前页

		request.setAttribute("pager", pager);

		List<CouponForm> listCoupon = null;

		if (searchListids == null || searchListids.size() == 0) {
			// 假如没有查出数据的默认列表
			couponCatlog.setRegionextid(null);
			couponCatlog.setTagextid(null);
			totalCount = couponCatlogService.getCatlogCount(couponCatlog);
			pager = PagerHelper.getPager(Integer.parseInt(currentPage), totalCount, pageSize);
			List<Long> seIds = searchStrategy.getCatlog(couponCatlog, pager);
			request.setAttribute("goodsNull", "true");
			if (seIds == null || seIds.size() == 0) {
				couponCatlog.setTagid(null);
				totalCount = couponCatlogService.getCatlogCount(couponCatlog);
				pager = PagerHelper.getPager(Integer.parseInt(currentPage), totalCount, pageSize);
				if (seIds == null || seIds.size() == 0) {
					couponCatlog.setRegionextid(null);
					couponCatlog.setRegionid(null);
					couponCatlog.setTagextid(null);
					couponCatlog.setTagid(null);
					totalCount = couponCatlogService.getCatlogCount(couponCatlog);
					pager = PagerHelper.getPager(Integer.parseInt(currentPage), totalCount, pageSize);
					seIds = searchStrategy.getCatlog(couponCatlog, pager);
				}
			}
			listCoupon = couponCatlogService.getCouponFormByIds(seIds);
		} else {
			listCoupon = couponCatlogService.getCouponFormByIds(searchListids);
		}
		request.setAttribute("listCoupon", listCoupon);

		Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService.get(REGION_CATLOG);

		Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(CatlogListener.PROPERTY_CATLOG);

		Map<Long, List<RegionCatlog>> property_catlog = null;

		// 假如memcache里没有就从数据库里查
		if (regionMap == null) {
			regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
			memCacheService.set(REGION_CATLOG, regionMap);
		}

		if (propertCatlogMap == null) {
			propertCatlogMap = BeanUtils.getCatlog(request, "propertyCatlogDao");
			property_catlog = propertCatlogMap.get(cityid);
			memCacheService.set(CatlogListener.PROPERTY_CATLOG, propertCatlogMap, 60 * 60 * 24 * 360);
		} else {
			property_catlog = propertCatlogMap.get(cityid);
		}

		Map<Long, List<RegionCatlog>> map = regionMap.get(city);
		if (region != null && !"".equals(region)) {
			List<RegionCatlog> listRegion = map.get(Long.parseLong(region));
			CatlogUtils.setCatlogUrl(true, listRegion, catlogTag, catlog_extTag, regionTag, region_extTag, null, COUPON_URL);
			request.setAttribute("listRegion", listRegion);

		}

		if (catlog != null && !"".equals(catlog)) {
			List<RegionCatlog> listProperty = property_catlog.get(Long.parseLong(catlog));
			CatlogUtils.setCatlogUrl(false, listProperty, catlogTag, catlog_extTag, regionTag, region_extTag, null, COUPON_URL);
			request.setAttribute("listProperty", listProperty);

		}
		List<RegionCatlog> listParentProperty = property_catlog.get(0L);
		List<RegionCatlog> listParentRegion = map.get(0L);

		if (couponCatlog.isNull()) {

			if (listParentRegion != null && listParentRegion.size() > 0) {
				for (RegionCatlog regionCatlog : listParentRegion) {
					CatlogUtils.setInitUrl(true, regionCatlog, COUPON_URL);
				}
			}

			if (listParentProperty != null && listParentProperty.size() > 0) {
				for (RegionCatlog regionCatlog : listParentProperty) {
					CatlogUtils.setInitUrl(false, regionCatlog, COUPON_URL);
				}
			}

		} else {

			CatlogUtils.setCatlogUrl(true, listParentRegion, catlogTag, catlog_extTag, regionTag, region_extTag, null, COUPON_URL);

			CatlogUtils.setCatlogUrl(false, listParentProperty, catlogTag, catlog_extTag, regionTag, region_extTag, null, COUPON_URL);
		}

		request.setAttribute("propertyMap", property_catlog);
		request.setAttribute("regionMap", map);

		memCacheService.set(REGION_CATLOG, regionMap);
		return "/coupon/listCoupon";
	}

	@RequestMapping("/coupon/mainSearchCouponByProperty.do")
	public String mainSearchCouponByProperty(ModelMap model, HttpServletRequest request) {
		String pageParam = request.getParameter("param");
		String ids = request.getParameter("ids");
		List<Long> listIds = new LinkedList<Long>();
		String idStrings[] = ids.split(",");
		for (String string : idStrings) {
			listIds.add(Long.parseLong(string));
		}
		List<CouponForm> listCoupon = couponCatlogService.getCouponFormByIds(listIds);
		request.setAttribute("listCouponForm", listCoupon);
		return "index/list/" + pageParam;
	}

	@RequestMapping("/coupon/downloadCoupon.do")
	public String downloadCoupon(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		String couponnumber = request.getParameter("couponnumber");
		String downmobile = request.getParameter("downmobile");
		String couponid = request.getParameter("cid");
		String formCode = request.getParameter("formCode");
		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE", request);
		String validateCode = (String) memCacheService.get("validCode_" + cookieCode);
		// String validateCode=(String)
		// request.getSession().getAttribute("validCode");
		String content = "";

		if (formCode == null || validateCode == null || !formCode.equalsIgnoreCase(validateCode)) {
			content = "validate_code_error";
			try {
				print(response, content);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		User user = getMemcacheUser(request);
		String key = "COUPON_" + downmobile;
		String downMobileValue = (String) memCacheService.get(key);

		// 用户假如未登录或者未验证手机的话 同一个手机只能下载5次
		if (user == null || user.getMobile_isavalible() == 0 || !downmobile.equals(user.getMobile())) {
			Date date = new Date();
			String nowDate = DateUtils.dateToStr(date);
			// 判断假如同一天已经download 5次了不能再下载了
			if (downMobileValue != null) {
				String svalue[] = downMobileValue.split("\\|");
				if (svalue != null && svalue.length == 2) {
					String vs = svalue[1];
					int count = Integer.parseInt(vs);

					if (count >= 5 && nowDate.equals(svalue[0])) {
						content = "download_coupon_error";
						try {
							print(response, content);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}

				}
			}
		}
		// 发送短信
		CouponForm couponForm = couponDao.getCouponDetailById(Integer.parseInt(couponid));
		String message = "千品网优惠券编号为：" + couponnumber + "，" + couponForm.getSmstemplate() + "有效期：" + couponForm.getCreateDate() + "至" + couponForm.getEndDate();
		SmsInfo sourceBean = new SmsInfo(downmobile, message, SMS_TYPE, "1");
		smsService.sendSms(sourceBean);
		// 记录memcache

		Date date = new Date();
		String nowDate = DateUtils.dateToStr(date);
		// 非第一次记录 取出次数加1
		if (downMobileValue != null) {
			String svalue[] = downMobileValue.split("\\|");
			String count = svalue[1];
			int scount = Integer.parseInt(count) + 1;
			if (nowDate.equals(svalue[0])) {
				memCacheService.set(key, svalue[0] + "|" + scount);
			} else {
				memCacheService.set(key, nowDate + "|" + scount);
			}
		} else {
			memCacheService.set(key, nowDate + "|" + 1);
		}
		// 下载记数
		Long downcount = (Long) memCacheService.get(Constant.MEM_COUPON_DOWNCOUNT + couponForm.getCouponid());
		if (downcount == null) {
			downcount = couponForm.getDowncount();
		}
		memCacheService.set(Constant.MEM_COUPON_DOWNCOUNT + couponForm.getCouponid(), downcount + 1);

		content = "ok";
		// 记录下载日志
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		logMap.put("action", "QuanSMS");
		logMap.put("mobile", downmobile);
		logMap.put("quanid", couponid);
		LogAction.printLog(logMap);

		try {
			print(response, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CouponCatlogService getCouponCatlogService() {
		return couponCatlogService;
	}

	public void setCouponCatlogService(CouponCatlogService couponCatlogService) {
		this.couponCatlogService = couponCatlogService;
	}

	public CouponDao getCouponDao() {
		return couponDao;
	}

	public void setCouponDao(CouponDao couponDao) {
		this.couponDao = couponDao;
	}

	public MerchantService getMerchantService() {
		return merchantService;
	}

	public void setMerchantService(MerchantService merchantService) {
		this.merchantService = merchantService;
	}

	public SeoService getSeoService() {
		return seoService;
	}

	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}

}
