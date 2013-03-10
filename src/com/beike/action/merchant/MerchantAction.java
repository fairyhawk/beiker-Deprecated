package com.beike.action.merchant;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.beike.action.user.BaseUserAction;
import com.beike.dao.coupon.CouponDao;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.MerchantService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.json.JsonUtil;

/**
 * <p>
 * Title: 商户相关Action
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
public class MerchantAction extends BaseUserAction {

	private static Log log = LogFactory.getLog(MerchantAction.class);

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private GoodsService goodsService;
	@Autowired
	private CouponDao couponDao;

	@Resource(name = "shopsBaoService")
	private ShopsBaoService shopsBaoService;
	
	private static int pageSize = 4;

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

	// /**
	// * 弃用,由于新增商铺宝，所以现有方法满足不了新需求。
	// */
	// @RequestMapping("/brand/showMerchant.do")
	// public Object showMerchant(ModelMap model, HttpServletRequest request,
	// HttpServletResponse response)
	// {
	// // 设置urlcookie
	// super.setCookieUrl(request, response);
	//
	// String merchantId = request.getParameter("merchantId");
	// log.info("商家详情id:" + merchantId);
	// if (merchantId == null || "".equals(merchantId)) {
	// request.setAttribute("ERRMSG", "没有找到相关品牌!");
	// return new ModelAndView("redirect:../500.html");
	// }
	// MerchantForm merchantForm = null;
	// Long mid = null;
	// Goods topGoods = null;
	// List<CouponForm> listCouponForm = null;
	// List<GoodsForm> listGoodsForm = null;
	// double savePrice = 0.0;
	// GoodsCatlog goodsCatlog = null;
	// try {
	// mid = Long.parseLong(merchantId);
	// // 品牌form
	// merchantForm = merchantService.getMerchantFormById(mid);
	//
	// // 置顶的商品
	// topGoods = goodsService.getGoodsByBrandId(mid);
	//
	// // 搜索置顶商品的 一级标签属性
	// if (topGoods != null) {
	// goodsCatlog = goodsService.searchGoodsRegionById(topGoods
	// .getGoodsId());
	// // log.info("goodsCatlog:" + goodsCatlog.getTagName());
	// }
	// // 搜索其他商品 按照销售量
	// List<MerchantForm> listIdsForm = merchantService
	// .getChildMerchnatById(mid);
	// // 总数
	// int size = goodsService.getGoodsCount(listIdsForm);
	//
	// String currentPage = request.getParameter("cpage");
	//
	// if (currentPage == null || "".equals(currentPage)) {
	// currentPage = "1";
	// }
	// Pager pager = PagerHelper.getPager(Integer.valueOf(currentPage),
	// size, pageSize);
	// request.setAttribute("pager", pager);
	// String sbid = "";
	// if (listIdsForm != null && listIdsForm.size() > 0) {
	// StringBuilder sbids = new StringBuilder();
	// for (MerchantForm merchantForm2 : listIdsForm) {
	// sbids.append(merchantForm2.getId());
	// sbids.append(",");
	// }
	// sbid = sbids.substring(0, sbids.lastIndexOf(","));
	// }
	//
	// List<Long> listIds = goodsService.getGoodsCountIds(sbid, pager);
	// // 分店的商品 分页显示
	// listGoodsForm = goodsService.getGoodsFormByChildId(listIds);
	// // 查询优惠券10条
	// listCouponForm = couponDao.getCouponListByMerchantId(mid, 10);
	// // 获得销售量
	// if (topGoods != null)
	// {
	// savePrice = topGoods.getSourcePrice() - topGoods.getCurrentPrice();
	// // 商品的真实购买数量
	// String salescount = goodsService.salesCount(topGoods.getGoodsId());
	// int viewSalesCount = topGoods.getVirtualCount();
	// if(null !=salescount && salescount.trim().length()>0){
	// viewSalesCount = Integer.parseInt(salescount)+topGoods.getVirtualCount();
	// }
	// BigDecimal big = new BigDecimal(savePrice);
	// big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
	// savePrice = big.doubleValue();
	// // 商品的实际销售量
	// // request.setAttribute("SALES_COUNT", salescount);
	// // 用于页面显示的商品数量
	// request.setAttribute("SALES_COUNT", viewSalesCount);
	//
	// }
	//
	// } catch (Exception e) {
	// log.info(e);
	// e.printStackTrace();
	// request.setAttribute("ERRMSG", "没有找到相关品牌!");
	// return new ModelAndView("redirect:../404.html");
	// }
	// request.setAttribute("goodsCatlog", goodsCatlog);
	// request.setAttribute("savePrice", savePrice);
	// request.setAttribute("merchantForm", merchantForm);
	// request.setAttribute("topGoods", topGoods);
	// request.setAttribute("listCouponForm", listCouponForm);
	// request.setAttribute("listGoodsForm", listGoodsForm);
	//
	// return "/brand/showBrandDetail";
	//
	// }

	/**
	 * ajax map 请求 品牌分店信息
	 */
	@RequestMapping("/brand/getMerchantMapList.do")
	public String getMerchantMapList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String merchantId = request.getParameter("merchantId");
		if (merchantId == null || "".equals(merchantId)) {
			try {
				print(response, "PARAM_ERROR");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String result = "";
		Long mid = Long.parseLong(merchantId);
		List<MerchantForm> listForm = null;
		String currentPage = request.getParameter("mpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		int size = merchantService.getChildMerchantCount(mid);

		Pager pager = PagerHelper.getPager(Integer.valueOf(currentPage), size,
				5);
		try {
			listForm = merchantService.getChildMerchnatById(mid, pager);
		} catch (Exception e) {
			log.info("品牌分店:" + e);
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
		DecimalFormat df = new DecimalFormat(".00");
		List<Map> list = new ArrayList<Map>();
		for (MerchantForm merchantForm : listForm) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantId", merchantForm.getId());
			map.put("merchantName", merchantForm.getMerchantname());
			map.put("addr", merchantForm.getAddr());
			map.put("buinesstime", merchantForm.getBuinesstime());
			map.put("tel", merchantForm.getTel());
			map.put("latitude", merchantForm.getLatitude());
			map.put("city", merchantForm.getCity());
			map.put("rate", df.format(merchantForm.getSatisfyRate()));
			map.put("is_support_takeaway", merchantForm.getIs_Support_Takeaway());
			map.put("is_support_online_meal", merchantForm.getIs_Support_Online_Meal());
			map.put("environment", merchantForm.getEnvironment());
			map.put("capacity", merchantForm.getCapacity());
			map.put("otherservice",merchantForm.getOtherservice());
			list.add(map);
		}
		Map<String, String> mpage = new HashMap<String, String>();
		mpage.put("currentPage", pager.getCurrentPage() + "");
		mpage.put("totalPage", pager.getTotalPages() + "");
		mpage.put("totalsize", size + "");
		list.add(mpage);
		String jsonResult = JsonUtil.listToJson(list);
		log.debug("分店json:" + jsonResult);

		try {
			print(response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	/**
	 * 
	 * janwen
	 * @param model
	 * @param request
	 * @param response
	 * @return 订餐分店下面没有商品也需要返回地图信息
	 *
	 */
	@RequestMapping("/brand/getDiancaiMerchantMapList.do")
	public String getDiancaiMapList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String merchantId = request.getParameter("merchantId");
		if (merchantId == null || "".equals(merchantId)) {
			try {
				print(response, "PARAM_ERROR");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String result = "";
		Long mid = Long.parseLong(merchantId);
		List<MerchantForm> listForm = null;
		String currentPage = request.getParameter("mpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}

		try {
			listForm = merchantService.getDiancaiChildBranchid(mid);
		} catch (Exception e) {
			log.info("品牌分店:" + e);
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
		DecimalFormat df = new DecimalFormat(".00");
		List<Map> list = new ArrayList<Map>();
		for (MerchantForm merchantForm : listForm) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantId", merchantForm.getId());
			map.put("merchantName", merchantForm.getMerchantname());
			map.put("addr", merchantForm.getAddr());
			map.put("buinesstime", merchantForm.getBuinesstime());
			map.put("tel", merchantForm.getTel());
			map.put("latitude", merchantForm.getLatitude());
			map.put("city", merchantForm.getCity());
			map.put("rate", df.format(merchantForm.getSatisfyRate()));
			map.put("is_support_takeaway", merchantForm.getIs_Support_Takeaway());
			map.put("is_support_online_meal", merchantForm.getIs_Support_Online_Meal());
			list.add(map);
		}
		Map<String, String> mpage = new HashMap<String, String>();
		mpage.put("currentPage", "1");
		mpage.put("totalPage", "1");
		mpage.put("totalsize", "1");
		list.add(mpage);
		String jsonResult = JsonUtil.listToJson(list);
		log.debug("分店json:" + jsonResult);

		try {
			print(response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping("/brand/getBranchMapList.do")
	public Object getBranchMapList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		//商家ID
		String merchantId = request.getParameter("merchantId");
		log.info("商家详情id:" + merchantId);
		if (StringUtils.isEmpty(merchantId)) {
			request.setAttribute("ERRMSG", "没有找到相关品牌!");
			return new ModelAndView("redirect:../500.html");
		}
		
		Long mid = Long.parseLong(merchantId);
		MerchantForm merchantForm = shopsBaoService.getMerchantDetailById(mid);

		if (merchantForm == null) {
			request.setAttribute("ERRMSG", "没有找到相关品牌!");
			return new ModelAndView("redirect:../500.html");
		}
		
		//商品ID
		String goodId = request.getParameter("goodId");
		//当前页
		String currentPage = request.getParameter("cpage");
		if (StringUtils.isEmpty(currentPage)) {
			currentPage = "1";
		}
		
		List<MerchantForm> listForm = null;
		Pager pager = null;
		if(StringUtils.isNotEmpty(goodId)){
		//商品分店
			Long gid = Long.parseLong(goodId);
			int size = goodsService.getAllGoodsMerchantCount(gid);
			pager = PagerHelper.getPager(Integer.valueOf(currentPage), size, 5);
			if (pager.getCurrentPage() > pager.getTotalPages()) {
				pager.setCurrentPage(1);
				pager.setStartRow(0);
			}
			listForm = goodsService.getGoodsMapMerchant(gid, pager);
		}else if(StringUtils.isNotEmpty(merchantId)){
			//商家分店
			int size = merchantService.getChildMerchantCount(mid);
			pager = PagerHelper.getPager(Integer.valueOf(currentPage), size, 5);
			if (pager.getCurrentPage() > pager.getTotalPages()) {
				pager.setCurrentPage(1);
				pager.setStartRow(0);
			}
			
			listForm = merchantService.getChildMerchnatById(mid, pager);
		}
		
		request.setAttribute("pager", pager);
		request.setAttribute("lstBrachForm", listForm);
		request.setAttribute("merchantId", merchantId);
		request.setAttribute("goodId", goodId);
		request.setAttribute("merchantForm", merchantForm);
		
		return new ModelAndView("/brand/showBranchMapList");
	}
	private void print(HttpServletResponse response, String content)
			throws IOException {
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(content);
	}

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

}
