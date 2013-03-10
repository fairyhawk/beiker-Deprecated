package com.beike.action.qianpincard;

import java.util.LinkedHashMap;
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

import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.entity.catlog.QPCardRegionCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.form.GoodsForm;
import com.beike.service.qianpincard.QianPinCardService;
import com.beike.util.BeanUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;

/**
 * @Title: 千品卡Action
 * @Package com.beike.action.qianpincard
 * @Description: TODO
 * @author wenjie.mai
 * @date Feb 29, 2012 10:41:19 AM
 * @version V1.0
 */
@Controller
public class QianPinCardAction extends BaseUserAction {

	public QianPinCardAction() {

	}

	@Autowired
	private QianPinCardService qianPinCardService;

	@Autowired
	private GoodsCatlogService goodsCatlogService;

	private static String CITY_CATLOG = "CITY_CATLOG";

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private static Log log = LogFactory.getLog(QianPinCardAction.class);

	public static final int QIANPINCARD_MEMCACHED_TIME = 10800; // 60*60*3 3小时

	@SuppressWarnings("unchecked")
	@RequestMapping("/card/queryQianpinCardInfo.do")
	public Object queryQianPinCardInfo(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			super.setCookieUrl(request, response);

			// 获得城市ID
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService
					.get(CITY_CATLOG);
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(CITY_CATLOG, mapCity);
			}
			String city = CityUtils.getCity(request, response);
			if (city == null || "".equals(city)) {
				city = "beijing";
				response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME,
						city, CityUtils.validy));
				request.setAttribute(CityUtils.CITY_COOKIENAME, city);
			}
			Long cityid = null;
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}

			// 获得城市中文信息
			String cityStr = IPSeeker.getCityByStr(city.trim());
			request.setAttribute("CITY_CHINESE", cityStr);

			if (!city.equals("beijing") && !city.equals("shanghai")
					&& !city.equals("guangzhou") && !city.equals("shenzhen")) {
				return new ModelAndView("redirect:http://" + city
						+ ".qianpin.com");
			}

			// 查找千品现金券
			List<GoodsForm> cashFormList = null;
			// 千品卡
			List<GoodsForm> cardFormList = null;
			// 热卖商品
			List<GoodsForm> hotGoodForm = null;

			// 查询千品卡现金券
			Map<String, List<GoodsForm>> hotGoodsIfoForm = null;

			try {
				cashFormList = (List<GoodsForm>) memCacheService.get(cityid
						+ "_QianPinCard_CashCoupon");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (cashFormList == null || cashFormList.size() == 0) {
				cashFormList = qianPinCardService
						.getCouponCashFor24Hour(cityid);
				if (cashFormList != null && cashFormList.size() > 0) {
					memCacheService.set(cityid + "_QianPinCard_CashCoupon",
							cashFormList);
				}
			}

			// 查找千品卡集市
			try {
				cardFormList = (List<GoodsForm>) memCacheService.get(cityid
						+ "_QianPinCard_Market");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (cardFormList == null || cardFormList.size() == 0) {
				List<Long> marketGoodsId = qianPinCardService
						.getTopSaleCardGoods(String.valueOf(cityid), 5, 30);
				if (marketGoodsId != null && marketGoodsId.size() > 0) {
					cardFormList = goodsCatlogService
							.getGoodsFormFromId(marketGoodsId);
					memCacheService.set(cityid + "_QianPinCard_Market",
							cardFormList);
				}
			}

			// 查询热门区域
			Map<String, List<QPCardRegionCatlog>> hotRegionMap = null;
			try {
				hotRegionMap = (Map<String, List<QPCardRegionCatlog>>) memCacheService
						.get(cityid + "_QianPinCard_hotRegion");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (hotRegionMap == null || hotRegionMap.isEmpty()) {
				hotRegionMap = qianPinCardService.getHotRegion(cityid
						.intValue());
				if (hotRegionMap != null && !hotRegionMap.isEmpty()) {
					memCacheService.set(cityid + "_QianPinCard_hotRegion",
							hotRegionMap);
				}
			}

			List<RegionCatlog> topRegionList = qianPinCardService
					.getTopCategory();

			// 查询热门商品
			try {
				hotGoodsIfoForm = (Map<String, List<GoodsForm>>) memCacheService
						.get(cityid + "_QianPinCard_hotGoods");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (hotGoodsIfoForm == null || hotGoodsIfoForm.size() == 0) {

				hotGoodsIfoForm = new LinkedHashMap<String, List<GoodsForm>>();
				Map<Long, List<Long>> hotGoodsMap = qianPinCardService
						.getHotGoods(cityid.intValue());

				if (hotGoodsMap != null && hotGoodsMap.size() > 0) {
					Map<Long, String> goodAndcatlog = new LinkedHashMap<Long, String>();
					List<Long> goodidList = new LinkedList<Long>();
					for (Map.Entry<Long, List<Long>> map : hotGoodsMap
							.entrySet()) {
						Long catlog = map.getKey();
						List<Long> goodList = map.getValue();
						for (Long li : goodList) {
							goodAndcatlog.put(li, String.valueOf(catlog));
							goodidList.add(li);
						}
					}
					if (goodidList != null && goodidList.size() > 0) {
						hotGoodForm = goodsCatlogService
								.getGoodsFormFromId(goodidList);
						List<GoodsForm> goodsList = null;
						for (GoodsForm form : hotGoodForm) {
							Long goodsId = form.getGoodsId();
							String catlog = goodAndcatlog.get(goodsId);
							goodsList = hotGoodsIfoForm.get(catlog);
							if (goodsList == null || goodsList.size() == 0) {
								goodsList = new LinkedList<GoodsForm>();
							}
							goodsList.add(form);
							hotGoodsIfoForm.put(catlog, goodsList);
						}
						memCacheService.set(cityid + "_QianPinCard_hotGoods",
								hotGoodsIfoForm, QIANPINCARD_MEMCACHED_TIME);
					}
				}
			}

			Map<String, Long> totalMap = qianPinCardService
					.getGoodsTotal(cityid.intValue());

			request.setAttribute("cashFormList", cashFormList);
			request.setAttribute("cardFormList", cardFormList);
			request.setAttribute("hotGoodsList", hotGoodsIfoForm);
			request.setAttribute("catLogTotalMap", totalMap);
			request.setAttribute("topRegionList", topRegionList);
			request.setAttribute("hotRegionMap", hotRegionMap);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:../500.html");
		}
		return "/qianpincard/showQianPinCard";
	}

//	@RequestMapping("/card/queryQianpinCardList.do")
//	public Object queryQianpinCardList(ModelMap model,
//			HttpServletRequest request, HttpServletResponse response) {
//		super.setCookieUrl(request, response);
//
//		// 获得城市ID
//		Map<String, Long> mapCity = (Map<String, Long>) memCacheService
//				.get(CITY_CATLOG);
//		if (mapCity == null) {
//			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
//			memCacheService.set(CITY_CATLOG, mapCity);
//		}
//		String city = CityUtils.getCity(request, response);
//		// 默认城市北京
//		if (city == null || "".equals(city)) {
//			city = "beijing";
//			response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city,
//					CityUtils.validy));
//			request.setAttribute(CityUtils.CITY_COOKIENAME, city);
//		}
//
//		Long cityid = null;
//		if (mapCity != null) {
//			cityid = mapCity.get(city.trim());
//		}
//		// 获得城市中文信息
//		String cityStr = IPSeeker.getCityByStr(city.trim());
//		request.setAttribute("CITY_CHINESE", cityStr);
//
//		// 查找千品卡集市
//		List<Long> marketGoodsId = qianPinCardService
//				.getCardGoodsOrderOnTime(String.valueOf(cityid));
//		// 千品卡
//		List<GoodsForm> cardFormList = null;
//		if (marketGoodsId != null && marketGoodsId.size() > 0) {
//			cardFormList = goodsCatlogService.getGoodsFormFromId(marketGoodsId);
//		}
//		request.setAttribute("cardFormList", cardFormList);
//
//		return "/qianpincard/qianPinCardList";
//	}
}