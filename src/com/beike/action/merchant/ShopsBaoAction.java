package com.beike.action.merchant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.CouponCatlogService;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.goods.Goods;
import com.beike.form.CashCouponForm;
import com.beike.form.CouponForm;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.MerchantService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**
 * project:beiker Title: Description: Copyright:Copyright (c) 2011
 * Company:Sinobo
 * 
 * @author qiaowb
 * @date Oct 31, 2011 5:56:23 PM
 * @version 1.0
 */
@Controller
public class ShopsBaoAction extends BaseUserAction {

	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance("project");

	@Resource(name = "shopsBaoService")
	private ShopsBaoService shopsBaoService;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private MerchantService merchantService;

	@Resource(name = "couponCatlogService")
	private CouponCatlogService couponCatlogService;

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private static Log log = LogFactory.getLog(ShopsBaoAction.class);

	private static int pageSize = 9;

	// 每页数量
	private static int listPageSize = 24;

	/**
	 * 根据域名跳转到相应的商铺
	 */
	@RequestMapping("/domain/redirectDomainShop.do")
	public String redirectDomainShop(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String domainName = "";
		Long merchantId = null;
		try {
			domainName = request.getParameter("domainName");
			if (domainName == null || "".equals(domainName)) {
				return "redirect:../404.html";
			}
			// 根据域名查找品牌ID
			merchantId = merchantService.getMerchantIdByDomainName(domainName);
			if (merchantId == null) {
				return "redirect:../404.html";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:http://www.qianpin.com/shangpubao/" + merchantId
				+ ".html";
	}

	@RequestMapping("/brand/showMerchant.do")
	public Object getShopBao(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		super.setCookieUrl(request, response);
		String merchantID = request.getParameter("merchantId");
		Goods topGoods = null;
		Long mid = null;
		String merIds = "";
		double savePrice = 0.0;
		GoodsCatlog goodsCatlog = null;
		MerchantForm merchantForm = null;
		CashCouponForm cashcouponForOne = null;
		CashCouponForm cashcouponForFive = null;
		CashCouponForm cashcouponForTwo = null;
		List<GoodsForm> listGoodsForm = null;
		List<MerchantForm> childForm = null;
		List<CouponForm> listCouponForm = null;

		try {
			if (null == merchantID || "".equals(merchantID)) {
				request.setAttribute("ERRMSG", "没有找到相关品牌");
				return new ModelAndView("redirect:../500.html");
			}
			mid = Long.parseLong(merchantID);

			// 品牌form
			merchantForm = shopsBaoService.getShangpubaoDetailById(mid);

			String city = merchantForm.getCity();
			if (city != null && !"".equals(city)) {
				city = PinyinUtil.hanziToPinyin(city, "");
				String refer = request.getRequestURL().toString();
				String xcity = "http://" + city;
				String staticurl = propertyUtil.getProperty("STATIC_URL");
				response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME,
						city, CityUtils.validy));
				if (!refer.startsWith(xcity)) {
					if ("true".equals(staticurl)) {
						StringBuilder sb=new StringBuilder();
						sb.append("redirect:http://" + city
								+ ".qianpin.com/shangpubao/"
								+ merchantForm.getId() + ".html");
							String params=WebUtils.parseQueryString(request);
							if(!org.apache.commons.lang.StringUtils.isBlank(params)){
								sb.append("?");
								String str=WebUtils.replaceParams(params, "merchantId");
								if(str!=null){
									sb.append(str);
								}
							}
						
						return new ModelAndView(sb.toString());
					} else {
						return new ModelAndView(
								"redirect:http://"
										+ city
										+ ".qianpin.com/brand/showMerchant.do?merchantId="
										+ mid);
					}

				}
			}

			// 查询100元现金券
			cashcouponForOne = shopsBaoService.getCashCoupon(mid,
					Long.valueOf("100"));
			// 查询50元现金券
			cashcouponForFive = shopsBaoService.getCashCoupon(mid,
					Long.valueOf("50"));
			// 查询20元现金券
			cashcouponForTwo = shopsBaoService.getCashCoupon(mid,
					Long.valueOf("20"));
			// 置顶的商品
			topGoods = goodsService.getGoodsByBrandId(mid);
			// 搜索置顶商品的 一级标签属性
			if (topGoods != null) {
				goodsCatlog = goodsService.searchGoodsRegionById(topGoods
						.getGoodsId());

				savePrice = topGoods.getSourcePrice()
						- topGoods.getCurrentPrice();
				String salescount = goodsService.salesCount(topGoods
						.getGoodsId());// 商品的真实购买数量
				int viewSalesCount = topGoods.getVirtualCount(); // 页面显示的商品数量
				if (null != salescount && salescount.trim().length() > 0) {
					viewSalesCount = Integer.parseInt(salescount)
							+ topGoods.getVirtualCount();
				}
				BigDecimal big = new BigDecimal(savePrice);
				big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
				savePrice = big.doubleValue();
				request.setAttribute("SALES_COUNT", viewSalesCount); // 用于页面显示的商品数量
			}
			// 查询所有商品 分页显示
			childForm = shopsBaoService.getChildMerchnatById(mid);
			int size = goodsService.getGoodsCount(childForm);
			String currentPage = request.getParameter("cpage");

			if (currentPage == null || "".equals(currentPage)) {
				currentPage = "1";
			}

			Pager pager = PagerHelper.getPager(Integer.valueOf(currentPage),
					size, pageSize);

			if (childForm != null && childForm.size() > 0) {
				StringBuilder ids = new StringBuilder();
				for (MerchantForm mer : childForm) {
					ids.append(mer.getId());
					ids.append(",");
				}
				merIds = ids.substring(0, ids.lastIndexOf(","));
			}

			List<Long> listIds = shopsBaoService
					.getGoodsCountIds(merIds, pager); // 不包括下架商品、售完商品
			listGoodsForm = goodsService.getGoodsFormByChildId(listIds); // 分店商品

			// 店铺环境
			// MerchantForm merForm =
			// shopsBaoService.getMerchantDetailById(mid);
			List<String[]> environList = merchantForm.getListMerchantbaoLogo();

			// 查找优惠券
			listCouponForm = shopsBaoService.getCouponListByMerchantId(mid, 7);

			// 评价次数
			String count = merchantForm.getEvaluation_count();// merchantService.getEvationCount(mid);

			// 平均分数
			String evaluationscore = merchantForm.getAvgscores();// merchantService.getAvgEvationScores(mid);

			request.setAttribute("pager", pager);
			request.setAttribute("topGoods", topGoods);
			if (topGoods != null && topGoods.getMapRegion()!=null) {
				request.setAttribute("REGION_NUMBER", topGoods.getMapRegion()
						.size());
			}
			request.setAttribute("hundredcoupon", cashcouponForOne);
			request.setAttribute("fiftycoupon", cashcouponForFive);
			request.setAttribute("twentycoupon", cashcouponForTwo);
			request.setAttribute("listGoodsForm", listGoodsForm);
			request.setAttribute("listCouponForm", listCouponForm);
			request.setAttribute("goodsCatlog", goodsCatlog);
			request.setAttribute("savePrice", savePrice);
			request.setAttribute("merchantForm", merchantForm);
			request.setAttribute("SHOP_ENVIROMENT", environList);
			request.setAttribute("count", count);
			request.setAttribute("MERCHANT_SCORES", evaluationscore);
			
			//增加日志2012-01-17
			Map<String,String> mapLog=LogAction.getLogMap(request,response);
			mapLog.put("action","p_bdp");
			mapLog.put("brandid",merchantID);
			LogAction.printLog(mapLog);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "没有找到相关品牌!");
			return new ModelAndView("redirect:../404.html");
		}
		return "/brand/shangpubao";
		// return "/brand/showBrandDetail";
	}

	public ShopsBaoService getShopsBaoService() {
		return shopsBaoService;
	}

	public void setShopsBaoService(ShopsBaoService shopsBaoService) {
		this.shopsBaoService = shopsBaoService;
	}

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	@RequestMapping("/shangpubao/shopsbaoGoodsList.do")
	public Object showShopsBaoGoods(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		super.setCookieUrl(request, response);
		try {
			String merchantId = request.getParameter("merchantId");
			if (StringUtils.isEmpty(merchantId)) {
				request.setAttribute("ERRMSG", "没有找到相关品牌!");
				return new ModelAndView("redirect:../500.html");
			}
			// 现金券原价、现金券标志
			// String sourcePrice = request.getParameter("sourcePrice");
			String couponCash = request.getParameter("couponCash");
			String filterInfo = null;
			if (StringUtils.isNotEmpty(couponCash) && "1".equals(couponCash)) {
				filterInfo = "bg.couponcash='1'";
			}
			MerchantForm merchantForm = null;
			List<GoodsForm> listGoodsForm = null;
			Long mid = null;

			mid = Long.parseLong(merchantId);
			merchantForm = shopsBaoService.getMerchantDetailById(mid);

			if (merchantForm == null) {
				request.setAttribute("ERRMSG", "没有找到相关品牌!");
				return new ModelAndView("redirect:../500.html");
			}
			// 查询品牌下所有分店
			List<MerchantForm> listIdsForm = merchantService
					.getChildMerchnatById(mid);
			// 分店在售商品数量
			int goodCount = goodsService.getShopsBaoGoodsCount(listIdsForm,
					filterInfo);
			String currentPage = request.getParameter("cpage");
			if (StringUtils.isEmpty(currentPage)) {
				currentPage = "1";
			}
			// 分页
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
					goodCount, listPageSize);
			if (pager.getCurrentPage() > pager.getTotalPages()) {
				pager.setCurrentPage(1);
				pager.setStartRow(0);
			}
			request.setAttribute("pager", pager);

			// 当前页商品数据
			List<Long> listIds = goodsService.getShopsBaoGoodsCountIds(
					listIdsForm, filterInfo, pager);
			listGoodsForm = goodsService.getGoodsFormByChildId(listIds);

			request.setAttribute("merchantForm", merchantForm);
			request.setAttribute("listGoodsForm", listGoodsForm);
			request.setAttribute("couponCash", couponCash);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "没有找到相关品牌!");
			return new ModelAndView("redirect:../404.html");
		}
		// return "/brand/shopsbaoGoodsList";
		return "/brand/shopsbaoGoodsList";
	}

	@RequestMapping("/shangpubao/shopsbaoCouponList.do")
	public Object showShopsBaoCouponList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		super.setCookieUrl(request, response);
		try {
			String merchantId = request.getParameter("merchantId");
			if (StringUtils.isEmpty(merchantId)) {
				request.setAttribute("ERRMSG", "没有找到相关品牌!");
				return new ModelAndView("redirect:../500.html");
			}

			MerchantForm merchantForm = null;
			List<CouponForm> listCouponForm = null;
			Long mid = null;

			mid = Long.parseLong(merchantId);
			merchantForm = shopsBaoService.getMerchantDetailById(mid);
			if (merchantForm == null) {
				request.setAttribute("ERRMSG", "没有找到相关品牌!");
				return new ModelAndView("redirect:../500.html");
			}

			// 分店优惠劵数量
			int couponCount = shopsBaoService.getCouponCount(mid);
			String currentPage = request.getParameter("cpage");
			if (StringUtils.isEmpty(currentPage)) {
				currentPage = "1";
			}
			// 分页
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
					couponCount, listPageSize);
			if (pager.getCurrentPage() > pager.getTotalPages()) {
				pager.setCurrentPage(1);
				pager.setStartRow(0);
			}
			request.setAttribute("pager", pager);

			// 当前页优惠劵数据
			List<Long> listIds = shopsBaoService.getCouponCountIds(mid, pager);
			listCouponForm = couponCatlogService.getCouponFormByIds(listIds);

			request.setAttribute("merchantForm", merchantForm);
			request.setAttribute("listCouponForm", listCouponForm);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("ERRMSG", "没有找到相关品牌!");
			return new ModelAndView("redirect:../404.html");
		}
		// return "/brand/shopsbaoCouponList";
		return "/brand/shopsbaoCouponList";
	}
}
