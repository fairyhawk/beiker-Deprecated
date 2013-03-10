package com.beike.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.Cookie;
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
import com.beike.common.listener.CatlogListener;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.dao.coupon.CouponDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.lottery.LotteryDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.user.User;
import com.beike.form.CouponForm;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.form.OrderEvaluationForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.comment.CommentService;
import com.beike.service.goods.GoodsService;
import com.beike.service.goods.ad.ADGoodsService;
import com.beike.service.lucene.recommend.LuceneRecommendService;
import com.beike.service.merchant.MerchantService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.service.unionpage.UnionPageService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title:商品action
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
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class GoodsAction extends BaseUserAction {

	private static Log log = LogFactory.getLog(GoodsAction.class);
	@Autowired
	private GoodsService goodsService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private PayLimitService payLimitService;

	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private CouponDao couponDao;
	@Autowired
	private MerchantDao merchantDao;
	@Autowired
	private LotteryDao lotteryDao;

	@Autowired
	private ADGoodsService adGoodsService;
	@Autowired
	private ShopsBaoService shopsBaoService;
	
	@Autowired
	private LuceneRecommendService luceneRecommendService;

	@Autowired
	private UnionPageService unionPageService;
	
	@Autowired
	private CommentService commentService;
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance("project");

	private static String REGION_CATLOG = "BASE_REGION_CATLOG";
	
	private static int pageSize = 5;

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	public GoodsDao getGoodsDao() {
		return goodsDao;
	}

	public void setGoodsDao(GoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public CouponDao getCouponDao() {
		return couponDao;
	}

	public void setCouponDao(CouponDao couponDao) {
		this.couponDao = couponDao;
	}

	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

	@RequestMapping("/goods/refreshSalesCount.do")
	public String replaceSalesCount(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		String type = request.getParameter("type");

		String ids = request.getParameter("ids");
		if (ids == null || ids.trim().equals("")) {
			return null;
		}
		String iid[] = ids.split(",");
		if (iid != null && iid.length > 0) {
			for (String string : iid) {
				try {
					Long.parseLong(string);
				} catch (Exception e) {
					e.printStackTrace();
					log.info("传入参数不对，疑似黑客。" + string);
					return null;
				}
			}

			if ("goods".equals(type)) {
				List<Map> listMap = new ArrayList<Map>();
				List<GoodsForm> listForm = goodsDao.getSalesCountByIds(ids);
				if (listForm != null && listForm.size() > 0) {
					for (GoodsForm goodsForm : listForm) {
						Map<String, String> map = new HashMap<String, String>();
						Long goodsId = goodsForm.getGoodsId();
						map.put("goodsId", goodsId + "");
						String salesCount = goodsForm.getSalescount();
						map.put("salesCount", salesCount);
						listMap.add(map);
					}
					String json = JsonUtil.listToJson(listMap);
					try {
						print(response, json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if ("coupon".equals(type)) {
				List<Map> listMap = new ArrayList<Map>();
				List<CouponForm> listForm = couponDao.getCouponDownCount(ids);

				if (listForm != null && listForm.size() > 0) {
					for (CouponForm couponForm : listForm) {
						Map<String, String> map = new HashMap<String, String>();
						Long couponId = couponForm.getCouponid();
						Long downCount = couponForm.getDowncount();
						map.put("goodsId", couponId + "");
						map.put("salesCount", downCount + "");
						listMap.add(map);

					}

					String json = JsonUtil.listToJson(listMap);
					try {
						print(response, json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if ("brand".equals(type)) {
				List<Map> listMap = new ArrayList<Map>();
				List<MerchantForm> listForm = merchantDao
						.getMerchantSalesCount(ids);
				if (listForm != null && listForm.size() > 0) {
					for (MerchantForm merchantForm : listForm) {
						Map<String, String> map = new HashMap<String, String>();
						String mid = merchantForm.getId();
						/**
						 * 品牌下商品的真实购买次数
						 */
						String realSalesCount = merchantForm.getSalescount();
						/**
						 * 品牌下商品的虚拟购买次数
						 */
						int virtualCount = merchantForm.getVirtualCount();
						Integer viewSalesCount = virtualCount;
						if (null != realSalesCount
								&& realSalesCount.trim().length() > 0) {
							viewSalesCount = virtualCount
									+ Integer.parseInt(realSalesCount);
						}
						/**
						 * 用于显示的购买次数：真实购买次数 + 虚拟购买次数
						 */
						String salesCount = viewSalesCount.toString();

						map.put("goodsId", mid);
						map.put("salesCount", salesCount);
						listMap.add(map);
					}

					String json = JsonUtil.listToJson(listMap);
					try {
						print(response, json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

			if ("lottery".equals(type)) {
				List<Map> listMap = new ArrayList<Map>();

				List<LotteryInfo> listLotteryInfoList = lotteryDao
						.getLotteryInfoList(ids);
				if (listLotteryInfoList != null
						&& listLotteryInfoList.size() > 0) {
					for (LotteryInfo lotteryInfo : listLotteryInfoList) {
						Map<String, String> map = new HashMap<String, String>();

						map.put("goodsId", lotteryInfo.getLotteryId() + "");
						map.put("salesCount", lotteryInfo
								.getParticipantscount()
								+ "");
						listMap.add(map);
					}

					String json = JsonUtil.listToJson(listMap);
					try {
						print(response, json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return null;

	}

	/**
	 * 功能:商品详情
	 * 
	 * 成功后跳转页面:/jsp/goods/showGoodDetail.jsp
	 * 
	 * 请求参数：
	 * 
	 * goodId：商品id
	 * 
	 * 返回参数：
	 * 
	 * detailUrl：request 范围 包含页面路径
	 * 
	 * 1.goodDetail:Goods对象 request范围 具体属性找到Goods.java 里面有注释(搜索类快捷键ctrl+shift+R)
	 * ${goodDetail.goodsname}
	 * 
	 * 2.recommendedGoods:List里是GoodsForm对象 GoodsForm里属性参照 Goods 类即可 <c:forEach
	 * items="${recommendedGoods}" var="t"> ${t.logo2}
	 * 
	 * 3.MERCHANT_SCORES:商家评价分数 ${MERCHANT_SCORES}
	 * 
	 * 4.MERCHANT_INFO.MerchantForm 对象
	 * 
	 * 1)MERCHANT_INFO.merchantname 商家名称
	 * 
	 * 2)MERCHANT_INFO.sevenrefound 7天是否退款 1显示图 0 不显示
	 * 
	 * 3)MERCHANT_INFO.overrefound 过期退款 1显示图 0 不显示
	 * 
	 * 4)MERCHANT_INFO.quality 质量 1显示图 0 不显示
	 * 
	 * 5.count:评价多少次
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/goods/showGoodDetail.do")
	public Object showGood(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 设置urlcookie
			//super.setCookieUrl(request, response);
			String goodId = request.getParameter("goodId");
			if (goodId == null || "".equals(goodId)) {
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}

			// 百度推广链接参数写入cookie add by qiaowb 2011-10-18
			String referer = request.getHeader("referer");
			String tn = request.getParameter("tn");
			String baiduid = request.getParameter("baiduid");
			if (tn != null && !"".equals(tn) && "baidutuan_tg".equals(tn)
					&& baiduid != null && !"".equals(baiduid)) {
				Cookie cookie = WebUtils.cookie("BAIDU_REFERER_PARAM", tn + "|"
						+ baiduid + "|" + goodId, -1);
				response.addCookie(cookie);
				// 百度推广
				request.setAttribute("baidu_access", "Y");
			}

			GoodsCatlog goodsCatlog = null;
			Goods goods = null;
			try {
				goods = goodsService.findById(Long.parseLong(goodId));
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			if (goods == null) {
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			String staticurl = propertyUtil.getProperty("STATIC_URL");
			// 汉语转换成拼音
			String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
			
			// 获取当前城市ID
			MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
			Long cityid = null;
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}
			
			request.setAttribute("NOWCITY", city);
			response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city,
					CityUtils.validy));
			String refer = request.getRequestURL().toString();
			// 商品所在城市访问路径前缀
			String xcity = "http://" + city;
			if (!refer.startsWith(xcity)) {
				if ("true".equals(staticurl)) {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city + ".qianpin.com/goods/"
							+ goodId + ".html");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						String str = WebUtils.replaceParams(params, "goodId");
						if (str != null) {
							sb.append("showGoods=true&");
							sb.append(str);
						}
					}

					return new ModelAndView(sb.toString());
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city
							+ ".qianpin.com/goods/showGoodDetail.do");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						String str = WebUtils.replaceParams(params, "goodId");
						if (str != null) {
							sb.append(str);
						}
					}
					return new ModelAndView(sb.toString());
				}

			}
			/**
			 * ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 start add by
			 * wenhua.cheng*******
			 */
			StringBuffer goodsDetailInfo = new StringBuffer();
			// 从memCahe里取商品信息
			try {
				goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
						"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}// 商品名字--1
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsId());// 商品ID---2
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSourcePrice());// 商品原价格--3
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getPayPrice());// 商品购买价格--4
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getRebatePrice());// 商品返现价格--5
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getDividePrice());// 商品分成价格--6

			// TODO加入过期时间等
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGuestId()); // guestID--7
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9

			String goodsDetailKey = StringUtils.createUUID();
			memCacheService.set(goodsDetailKey, goodsDetailInfo.toString(),
					18000);
			request.setAttribute("goodsDetailKey", goodsDetailKey);

			/** ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 end******* */
			goodsCatlog = goodsService
					.searchGoodsRegionById(goods.getGoodsId());
			request.setAttribute("goodDetail", goods);
			request.setAttribute("goodsCatlog", goodsCatlog);

			// 包含页面的url
			String detailUrl = goodsService.getGoodDetailIncliudeUrl(Long
					.parseLong(goodId));

			// 获得商品的实际销售量
			String salescount = goodsService.salesCount(Long.parseLong(goodId));
			if (org.apache.commons.lang.StringUtils.isEmpty(salescount)) {
				salescount = "0";
			}
			/**
			 * 此处是用于显示的商品数量
			 * 
			 * Add by zx.liu
			 */
			int viewSalesCount = goods.getVirtualCount();
			if (null != salescount && salescount.trim().length() > 0) {
				viewSalesCount = Integer.parseInt(salescount)
						+ goods.getVirtualCount();
			}
			// 商品的实际销售量
			// request.setAttribute("SALES_COUNT", salescount);
			// 用于页面显示的商品数量
			request.setAttribute("SALES_COUNT", viewSalesCount);
			request.setAttribute("REAL_SALES_COUNT", salescount);
			request.setAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);

			// 判断此文件是否存在 假如不存在页面不包含
			String detailFile = request.getRealPath("") + "/jsp/goods_detail/"
					+ detailUrl;
			File file = new File(detailFile);
			if (!file.exists()) {
				detailUrl = null;
			}
			request.setAttribute("detailUrl", detailUrl);
			BigDecimal big = new BigDecimal(goods.getSourcePrice()
					- goods.getCurrentPrice());
			big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
			request.setAttribute("offerPrice", big.floatValue());

			// 右侧推荐
			// List<GoodsForm> listForm = goodsService.getTopGoodsForm();
			// request.setAttribute("recommendedGoods", listForm);

			// 商家信息
			/*
			 * MerchantForm merchantForm = goodsService.getMerchantById(Long
			 * .parseLong(goodId));
			 */
			MerchantForm merchantForm = shopsBaoService
					.getMerchantDetailByGoodsId(Long.parseLong(goodId));
			request.setAttribute("merchantForm", merchantForm);

			// 判断用户是否登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			// 最大可购买数量
			Long canCount = (long) goods.getGoodsSingleCount();
			Long ssCount = goods.getMaxcount() - Long.parseLong(salescount);// 商品上限量与已购买量差
			if (canCount != 0) {
				if (user != null) {
					canCount = payLimitService.allowPayCount(canCount, user
							.getId(), Long.parseLong(goodId),0l);
				}
				if (canCount > ssCount) {
					canCount = ssCount;
				}
			} else {
				canCount = ssCount;
			}

			if (user == null) {
				request.setAttribute("need_login", "Y");
			}
			request.setAttribute("maxBuyCount", canCount);

			// 最近浏览商品，最近浏览的商品排在前面，最后以"_"结尾
/*			String viewedGoods = WebUtils.getCookieValue(
					"RECENTLY_VIEWED_GOODS", request);
			if (org.apache.commons.lang.StringUtils.isEmpty(viewedGoods)) {
				viewedGoods = goodId + "_";
			} else {
				StringBuffer bufViewed = new StringBuffer(goodId).append("_");

				// 如果当前cookie中已存在当前商品ID，删除掉以保证最后浏览的商品排至首位
				viewedGoods = org.apache.commons.lang.StringUtils.remove(
						viewedGoods, bufViewed.toString());

				String[] aryViewed = org.apache.commons.lang.StringUtils.split(
						viewedGoods, "_");
				for (int i = 0; i < 3 && i < aryViewed.length; i++) {
					bufViewed = bufViewed.append(aryViewed[i]).append("_");
				}
				viewedGoods = bufViewed.toString();
			}
			Cookie cookie = WebUtils.cookie("RECENTLY_VIEWED_GOODS",
					viewedGoods, 30 * 24 * 60 * 60);
			response.addCookie(cookie);*/

			// 商圈、属性分类
			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(CatlogListener.PROPERTY_CATLOG);

			Map<Long, List<RegionCatlog>> property_catlog = null;

			// 假如memcache里没有就从数据库里查
			if (regionMap == null) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
				memCacheService.set(REGION_CATLOG, regionMap);
			}
			
			if(propertCatlogMap == null){
				propertCatlogMap = BeanUtils.getCatlog(request,"propertyCatlogDao");
				property_catlog  = propertCatlogMap.get(cityid);
				memCacheService.set(CatlogListener.PROPERTY_CATLOG, propertCatlogMap,60*60*24*360);
			}else{
				property_catlog  = propertCatlogMap.get(cityid);
			}	
			// 当前城市商圈
			Map<Long, List<RegionCatlog>> curRegionMap = regionMap.get(city);
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
			// 商品分类
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

			// 一级商圈
			String regionName = "";
			List<Map<String, Object>> lstFirstRegion = goodsService
					.getGoodsFirstRegionById(Long.parseLong(goodId));
			if (lstFirstRegion != null && lstFirstRegion.size() == 1) {
				RegionCatlog tmpregion = regionKeyMap.get(lstFirstRegion.get(0)
						.get("regionid"));
				if (tmpregion != null) {
					regionName = tmpregion.getCatlogName();
				}
			}
			request.setAttribute("firstRegionName", regionName);

			// 0220推荐
			int category_id = 0;
			Long goodsRegionid = 0L;
			Long goodsTagid = 0L;
			// 推荐商品
			List<Map<String, Object>> lstRegionIds = goodsService
					.getGoodsRegionIds(Long.parseLong(goodId));
			// 相关分类
			List<Object[]> lstGoodsTag = new LinkedList<Object[]>();
			// 相关商圈
			List<Object[]> lstGoodsRegion = new LinkedList<Object[]>();
			// 用于处理二级地域重复
			int iNextRegionCount = 0;
			String tmpGoodsRegionId = "";
			
			if (lstRegionIds != null && lstRegionIds.size() > 0) {
				Map<String, Object> mapRegionIds = lstRegionIds.get(0);

				Set<String> noRepeatSet = new HashSet<String>();
				for (int i = 0; i < lstRegionIds.size(); i++) {
					Map<String, Object> catlog = lstRegionIds.get(i);
					Long tagId = (Long) catlog.get("tagid");
					Long tagNextId = (Long) catlog.get("tagextid");
					Long regionId = (Long) catlog.get("regionid");
					Long regionextId = (Long) catlog.get("regionextid");
					if (i == 0) {
						category_id = tagId.intValue();
					}
					// 分类
					RegionCatlog tagA = tagKeyMap.get(tagId);
					if (!noRepeatSet.contains(String.valueOf(tagId))) {
						noRepeatSet.add(String.valueOf(tagId));
						if (tagA != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(), "",
									tagA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(tagId)
							+ "|" + tagNextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(tagId)
								+ "|" + tagNextId));

						RegionCatlog tagB = tagKeyMap.get(tagNextId);

						if (tagA != null && tagB != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(),
									tagB.getRegion_enname(),
									tagB.getCatlogName() });
						}
					}

					// 商圈
					RegionCatlog regionA = regionKeyMap.get(regionId);
					if (!noRepeatSet.contains(String.valueOf(regionId))) {
						noRepeatSet.add(String.valueOf(regionId));
						if (regionA != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(), "",
									regionA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(regionId)
							+ "|" + regionextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(regionId)
								+ "|" + regionextId));

						RegionCatlog regionB = regionKeyMap.get(regionextId);
						if (regionA != null && regionB != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(),
									regionB.getRegion_enname(),
									regionB.getCatlogName() });
						}
					}

					// 二级商圈数超过两个，不使用二级商圈做推荐算法
					if (iNextRegionCount <= 1
							&& tmpGoodsRegionId.indexOf(String
									.valueOf(regionextId)) < 0) {
						if (i == 0) {
							tmpGoodsRegionId = String.valueOf(regionextId);
						} else {
							tmpGoodsRegionId = tmpGoodsRegionId + ","
									+ regionextId;
						}

						iNextRegionCount++;
						if (iNextRegionCount > 2) {
							tmpGoodsRegionId = null;
						}
					}

					/*
					 * // 分类 暂时不要删掉 qiaowb 2012-03-29 if (i == 0) { Object[]
					 * aryGoodTag1 = new Object[3]; RegionCatlog tag1 =
					 * tagKeyMap.get(catlog.get("tagid")); category_id =
					 * Integer.parseInt(((Long)
					 * catlog.get("tagid")).toString()); if (tag1 != null) {
					 * aryGoodTag1[0] = tag1.getRegion_enname(); aryGoodTag1[2] =
					 * tag1.getCatlogName(); lstGoodsTag.add(aryGoodTag1); }
					 * 
					 * Object[] aryGoodTag2 = new Object[3]; RegionCatlog tag2 =
					 * tagKeyMap.get(catlog .get("tagextid")); if (tag1 != null &&
					 * tag2 != null) { aryGoodTag2[0] = tag1.getRegion_enname();
					 * aryGoodTag2[1] = tag2.getRegion_enname(); aryGoodTag2[2] =
					 * tag2.getCatlogName(); lstGoodsTag.add(aryGoodTag2); } } //
					 * 只有一个区域 if (lstRegionIds.size() == 1) { Object[]
					 * aryGoodRegion1 = new Object[3]; RegionCatlog region1 =
					 * regionKeyMap.get(catlog .get("regionid")); if (region1 !=
					 * null) { aryGoodRegion1[0] = region1.getRegion_enname();
					 * aryGoodRegion1[2] = region1.getCatlogName();
					 * lstGoodsRegion.add(aryGoodRegion1); }
					 * 
					 * Object[] aryGoodRegion2 = new Object[3]; RegionCatlog
					 * region2 = regionKeyMap.get(catlog .get("regionextid"));
					 * if (region1 != null && region2 != null) {
					 * aryGoodRegion2[0] = region1.getRegion_enname();
					 * aryGoodRegion2[1] = region2.getRegion_enname();
					 * aryGoodRegion2[2] = region2.getCatlogName();
					 * lstGoodsRegion.add(aryGoodRegion2); }
					 * 
					 * tmpGoodsRegionId = String.valueOf(catlog
					 * .get("regionextid")); } else { if (i == 0) { Object[]
					 * aryGoodRegion1 = new Object[3]; RegionCatlog region1 =
					 * regionKeyMap.get(catlog .get("regionid")); if (region1 !=
					 * null) { aryGoodRegion1[0] = region1.getRegion_enname();
					 * aryGoodRegion1[2] = region1.getCatlogName();
					 * lstGoodsRegion.add(aryGoodRegion1); }
					 * 
					 * Object[] aryGoodRegion2 = new Object[3]; RegionCatlog
					 * region2 = regionKeyMap.get(catlog .get("regionextid"));
					 * if (region1 != null && region2 != null) {
					 * aryGoodRegion2[0] = region1.getRegion_enname();
					 * aryGoodRegion2[1] = region2.getRegion_enname();
					 * aryGoodRegion2[2] = region2.getCatlogName();
					 * lstGoodsRegion.add(aryGoodRegion2); }
					 * 
					 * iNextRegionCount = 1; tmpGoodsRegionId =
					 * String.valueOf(catlog .get("regionextid")); } else {
					 * Object[] aryGoodRegion = null; RegionCatlog region1 =
					 * regionKeyMap.get(catlog .get("regionid")); RegionCatlog
					 * region2 = regionKeyMap.get(catlog .get("regionextid"));
					 * if (region1 != null && region2 != null) { aryGoodRegion =
					 * new Object[3]; aryGoodRegion[0] =
					 * region1.getRegion_enname(); aryGoodRegion[1] =
					 * region2.getRegion_enname(); aryGoodRegion[2] =
					 * region2.getCatlogName(); }
					 * 
					 * if (tmpGoodsRegionId.indexOf(String.valueOf(catlog
					 * .get("regionextid"))) < 0) { if (iNextRegionCount <= 1) {
					 * if (aryGoodRegion != null) { lstGoodsRegion.remove(0);
					 * lstGoodsRegion.add(aryGoodRegion); }
					 * 
					 * tmpGoodsRegionId = tmpGoodsRegionId + "," +
					 * catlog.get("regionextid"); } iNextRegionCount++; if
					 * (iNextRegionCount > 2) { tmpGoodsRegionId = null; break; } } } }
					 */
				}

				request.setAttribute("lstGoodsTag", lstGoodsTag);
				request.setAttribute("lstGoodsRegion", lstGoodsRegion);

				goodsRegionid = (Long) mapRegionIds.get("regionid");
				goodsTagid = (Long) mapRegionIds.get("tagid");
			}

			// 0220商品推荐
			List<Long> sameBrandGoodsid = goodsService.getTopGoodsByMerchantId(
					Long.parseLong(merchantForm.getId()), 2, goodId + "");
			/*
			 * if(sameBrandGoodsid.size()>2){
			 * request.setAttribute("morethanthree", "yes");
			 * sameBrandGoodsid.remove(2); }else{
			 * request.setAttribute("morethanthree", "no"); }
			 */
			List<Long> temp = new ArrayList<Long>();
			temp.addAll(sameBrandGoodsid);
			temp.add(goods.getGoodsId());

			List<Long> sameCategoryGoodsid = adGoodsService
					.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
							category_id, "1", StringUtils.arrayToString(temp
									.toArray(), ","), 8);
			sameCategoryGoodsid = getRandomGoodsId(sameCategoryGoodsid, 4);
			temp.addAll(sameCategoryGoodsid);

			List<Long> foodCategoryGoodsid = adGoodsService
					.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
							category_id, "2", StringUtils.arrayToString(temp
									.toArray(), ","), 8);
			foodCategoryGoodsid = getRandomGoodsId(foodCategoryGoodsid, 4);
			temp.addAll(foodCategoryGoodsid);
			// 低价商品算法修改(使用缓存)
			List<Long> lowpriceGoodsid = goodsService.getPartLowestGoods(
					cityid, 1L, StringUtils.arrayToString(temp.toArray(), ","));
			temp.addAll(lowpriceGoodsid);

			// 同店推荐
			request.setAttribute("sameBrandGoods", goodsService
					.getGoodsFormByChildId(sameBrandGoodsid));
			// 同类推荐
			request.setAttribute("sameCategoryGoods", goodsService
					.getGoodsFormByChildId(sameCategoryGoodsid));
			// 美食热销推荐
			request.setAttribute("foodCategoryGoods", goodsService
					.getGoodsFormByChildId(foodCategoryGoodsid));
			// 低价推荐
			request.setAttribute("lowpriceGoods", goodsService
					.getGoodsFormByChildId(lowpriceGoodsid));

			// 周边人气，暂时沿用原推荐商品算法
			List<Long> lstTuijian = goodsService.getSaleWithGoodsIds(
					goodsRegionid, tmpGoodsRegionId, goodsTagid, cityid, 20L,
					"");
			log.debug("lstTuijian===" + lstTuijian);

			// 剔除其他推荐商品ID，查询推荐商品
			if (lstTuijian != null) {
				if (temp != null && temp.size() > 0) {
					lstTuijian.removeAll(temp);
				}
				if (lstTuijian.size() > 4) {
					lstTuijian = lstTuijian.subList(0, 4);
				}
				List<GoodsForm> lstTuijianGoodsForm = goodsService
						.getGoodsFormByChildId(lstTuijian);
				request
						.setAttribute("lstTuijianGoodsForm",
								lstTuijianGoodsForm);
			}

			// 查询商品数量最多的一级商圈
			List<Long> lstTopRegionId = goodsService.getTopRegionCatlogId(
					cityid, 6L);
			List<RegionCatlog> lstTopRegion = new ArrayList<RegionCatlog>();
			for (Long topRegion : lstTopRegionId) {
				lstTopRegion.add(regionKeyMap.get(topRegion));
			}
			request.setAttribute("lstTopRegion", lstTopRegion);

			try{
				//关键词搜索
				String[] aryRecommendKeywords = luceneRecommendService.getRecommend(goods.getGoodsname(), 20);
				List<Map<String,String>> listRecommendMsg = unionPageService.getListMsgByKeyWords(aryRecommendKeywords,20);
				request.setAttribute("recommandKeyWord", listRecommendMsg);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			// 增加日志2012-01-17
			Map<String, String> mapLog = LogAction.getLogMap(request, response);
			mapLog.put("action", "p_dp");
			mapLog.put("goodid", goodId);
			LogAction.printLog(mapLog);

			//author wenjie.mai  增加评价信息  By 2012-03-14
			String score = request.getParameter("score");
			//非0-2之间的数,默认查询所有的评价信息 0很好1满意2差
			int thescore = -1;
			if(score != null && score != ""){
				try{
					thescore = Integer.parseInt(score);
				}catch(NumberFormatException e){
					thescore = -1;
				}
			}
			
			Long userId = 0l;
			if(user != null){
				userId = user.getId();
			}
			Long gid = Long.parseLong(goodId);
			
			// 当前页
			String currentPage = request.getParameter("cpage");
			// 计算分页
			int totalCount = commentService.getEvaluateGoodCount(userId,gid,thescore);	
			if (currentPage == null || "".equals(currentPage)) {
				currentPage = "1";
			}			
						
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),totalCount, pageSize);

			request.setAttribute("pager", pager);
			
			List<OrderEvaluationForm> evaForm = null;
			List<Long> normallist = commentService.getEvaluateGoodID(userId, gid, pager,thescore);
			if(normallist != null && normallist.size() >0){
				request.setAttribute("EVAFLAG", "1"); // 有该商品的评价信息
				evaForm = commentService.getEvaluationInfoByIds(normallist);
				//numMap  = commentService.getAllEvaluationForGood(gid);
			}/*else{
				List<Long> otherlist  = commentService.getEvaluateGoodOtherID(merchantid);
				if(otherlist != null && otherlist.size() > 0){
					request.setAttribute("EVAFLAG", "0"); // 其它商品的评价信息
					evaForm = commentService.getEvaluationInfoByIds(otherlist);
				}
			}*/
			request.setAttribute("evaFormlist", evaForm);
			//author wenjie.mai End
			
			return "/goods/showGoodDetail";
		} catch (Exception ex) {
			ex.printStackTrace();
			// return new ModelAndView("redirect:../500.html");
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 功能: 商品详情页地图信息调用
	 * 
	 * 请求方式：ajax调用
	 * 
	 * 请求参数:
	 * 
	 * goodId：商品id
	 * 
	 * 响应参数:
	 * 
	 * PARAM_ERROR:参数输入有误
	 * 
	 * NO_MERCHANT：没有查到相关商户信息
	 * 
	 * 返回正确：json格式
	 * 
	 * merchantName：商家名称
	 * 
	 * addr：地址
	 * 
	 * buinesstime：营业时间
	 * 
	 * tel：电话
	 * 
	 */
	@RequestMapping("/goods/getGoodMapList.do")
	public String getGoodMapList(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String goodId = request.getParameter("goodId");
		if (goodId == null || "".equals(goodId)) {
			try {
				print(response, "PARAM_ERROR");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		List<MerchantForm> mapList = null;
		String currentPage = request.getParameter("mpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		Pager pager = null;
		int size = 0;
		try {
			Long gid = Long.parseLong(goodId);
			size = goodsService.getAllGoodsMerchantCount(gid);
			pager = PagerHelper.getPager(Integer.valueOf(currentPage), size, 5);
			mapList = goodsService.getGoodsMapMerchant(gid, pager);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				print(response, "PARAM_ERROR");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		if (mapList == null || mapList.size() == 0) {
			// 此种返回不符合常理
			try {
				print(response, "NO_MERCHANT");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		DecimalFormat df = new DecimalFormat(".00");
		
		List<Map> list = new ArrayList<Map>();
		for (MerchantForm merchantForm : mapList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantId", merchantForm.getId());
			map.put("merchantName", merchantForm.getMerchantname());
			map.put("addr", merchantForm.getAddr());
			map.put("buinesstime", merchantForm.getBuinesstime());
			map.put("tel", merchantForm.getTel());
			map.put("latitude", merchantForm.getLatitude());
			map.put("city", merchantForm.getCity());
			map.put("rate",df.format(merchantForm.getSatisfyRate()));
			map.put("is_support_takeaway", merchantForm.getIs_Support_Takeaway());
			map.put("is_support_online_meal", merchantForm.getIs_Support_Online_Meal());
			map.put("environment",merchantForm.getEnvironment());
			map.put("capacity",merchantForm.getCapacity());
			map.put("otherservice",merchantForm.getOtherservice());
			list.add(map);
		}
		if (pager != null) {
			Map<String, String> mpage = new HashMap<String, String>();
			mpage.put("currentPage", pager.getCurrentPage() + "");
			mpage.put("totalPage", pager.getTotalPages() + "");
			mpage.put("totalsize", size + "");
			list.add(mpage);
		}
		String jsonResult = JsonUtil.listToJson(list);
		log.debug("商品详情页:showGoodsDetail:" + jsonResult);
		try {
			print(response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 功能:获得商品详情页右上角的商家信息
	 * 
	 * 调用方式：ajax调用
	 * 
	 * 请求参数:
	 * 
	 * goodId:商品ID
	 * 
	 * 响应参数：
	 * 
	 * PARAM_ERROR:输入参数错误
	 * 
	 * NO_MERCHANT:没有找到此商户
	 * 
	 * 返回正确: json格式数据
	 * 
	 * merchantName：商家名称
	 * 
	 * overrefound:过期退款
	 * 
	 * sevenrefound：七天退款
	 * 
	 * quality：质量保证
	 * 
	 * merchant_scores：商家评价分数 eg:3.7 4.0
	 * 
	 */
	@RequestMapping("/goods/getGoodMerchant.do")
	public String getGoodMerchant(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 设置urlcookie
		super.setCookieUrl(request, response);
		String goodId = request.getParameter("goodId");
		String result = "";
		if (goodId == null) {
			result = "PARAM_ERROR";
			try {
				print(response, result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		// 商家信息
		MerchantForm merchantForm = goodsService.getMerchantById(Long
				.parseLong(goodId));
		if (merchantForm == null) {
			// 没查到此商户
			result = "NO_MERCHANT";
			try {
				print(response, result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		Map<String, String> map = merchantService
				.getMerchantEvaluationScores(Long.parseLong(merchantForm
						.getId()));

		Map<String, String> values = new HashMap<String, String>();
		values.put("merchantName", merchantForm.getMerchantname());
		// 过期退款
		values.put("overrefound", merchantForm.getOverrefound() + "");
		// 七天退款
		values.put("sevenrefound", merchantForm.getSevenrefound() + "");
		// 质量保证
		values.put("quality", merchantForm.getQuality() + "");
		String scores = map.get("evaluationscore");
		String count = map.get("count");
		values.put("merchant_scores", scores + "");
		values.put("count", Integer.parseInt(count + "") + "");
		String jsonResult = "";
		try {
			jsonResult = JsonUtil.mapToJson(values);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			print(response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void print(HttpServletResponse response, String content)
			throws IOException {
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(content);
	}

	public MerchantService getMerchantService() {
		return merchantService;
	}

	public void setMerchantService(MerchantService merchantService) {
		this.merchantService = merchantService;
	}

	public LotteryDao getLotteryDao() {
		return lotteryDao;
	}

	public void setLotteryDao(LotteryDao lotteryDao) {
		this.lotteryDao = lotteryDao;
	}

	/**
	 * 从商品ID集合中随机取固定数量商品ID
	 * 
	 * @param lstSource
	 * @param iCnt
	 * @return
	 */
	private List<Long> getRandomGoodsId(List<Long> lstSource, int iCnt) {
		if (lstSource != null && lstSource.size() > iCnt) {
			Random random = new Random(); // 随机数类
			StringBuffer bufIndex = new StringBuffer();
			int[] aryIndex = new int[iCnt];
			for (int i = 0; i < iCnt; i++) {
				// 取不重复的索引数
				int curI = random.nextInt(lstSource.size());
				while (bufIndex.indexOf(String.valueOf(curI)) >= 0) {
					curI = random.nextInt(lstSource.size());
				}
				bufIndex.append(curI);
				aryIndex[i] = curI;

			}
			// 排序
			Arrays.sort(aryIndex);
			List<Long> lstResult = new ArrayList<Long>();
			for (int i = 0; i < aryIndex.length; i++) {
				lstResult.add(lstSource.get(aryIndex[i]));
			}
			return lstResult;
		} else {
			return lstSource;
		}
	}
}
