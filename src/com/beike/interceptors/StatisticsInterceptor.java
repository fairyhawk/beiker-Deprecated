package com.beike.interceptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.beike.entity.log.StatisticsModel;

/**
 * <p>
 * Title:数据统计拦截器
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
 * @date Mar 19, 2012
 * @author ye.tian
 * @version 1.0
 */

public class StatisticsInterceptor extends BaseBeikeInterceptor {

	private static final Log log = LogFactory
			.getLog(StatisticsInterceptor.class);

	private static final String PAGE_KEY = "pagekey";

	private static final String PAGE_PARA = "pagepara";

	private static Map<String, StatisticsModel> map = new HashMap<String, StatisticsModel>();

	static {
		// 首页
		map.put("/forward.do?param=index.index", new StatisticsModel("idx", "",
				null));

		// 商品详情 商品id条件
		List<String> list = new ArrayList<String>();
		list.add("goodId");
		// 商品详情
		map.put("/goods/showGoodDetail.do",
				new StatisticsModel("gdp", "", list));
		//探索版详情页
		map.put("/goods/showGoodExplore.do",
				new StatisticsModel("gdp", "", list));
		
		// 购物车
		map.put("/shopcart/shopcart.do", new StatisticsModel("shc", "", null));
		// 支付成功
		map.put("/pay/paySuccess.do", new StatisticsModel("sed", "", null));

		// 商品列表 当前页数条件
		list = new ArrayList<String>();
		list.add("cpage");
		map.put("/goods/searchGoodsByProperty.do", new StatisticsModel("lst",
				"", list));
		
		// 商品列表 新版
		list = new ArrayList<String>();
		list.add("cpage");
		map.put("/goods/searchGoodsByPropertyExplore.do", new StatisticsModel("lst",
				"", list));
		
		// 商铺宝 品牌id条件
		list = new ArrayList<String>();
		list.add("merchantId");
		// 商铺宝
		map.put("/brand/showMerchant.do", new StatisticsModel("bdp", "", list));
		// 搜索 当前页数条件
		list = new ArrayList<String>();
		list.add("pn");
		// 搜索
		map.put("/search/searchGoods.do", new StatisticsModel("sch", "", list));

		// 千品卡
		map.put("/card/queryQianpinCardInfo.do", new StatisticsModel("idk", "",
				null));
		
		//赶集网页面
		map.put("/goods/listIndex.do", new StatisticsModel("igj","",null));
		
		//360活动页面
		map.put("/good/searchHuoDongGood.do", new StatisticsModel("360","",null));
		
		
		//方芳的泰国游
		map.put("/huodong/taiguoyou.do", new StatisticsModel("hd1","",null));
		
		//0元抽奖  prizeid
		List<String> prizeParaList= new ArrayList<String>();
		prizeParaList.add("prizeid");
		map.put("/lottery/lotteryNewAction.do", new StatisticsModel("hch","",prizeParaList));
		
		//购物页面
		map.put("/pay/shoppingCart.do", new StatisticsModel("bcp","",null));
		
		//秒杀详情
		list = new ArrayList<String>();
		list.add("miaoshaId");
		map.put("/miaosha/showMiaoShaDetail.do",new StatisticsModel("msi", "", list));
		
		
		//聚合页
		List<String> unionId= new ArrayList<String>();
		unionId.add("goodsId");
		map.put("/brand/getMergeWithBrands.do", new StatisticsModel("jhy", "", unionId));
		
		
		// 秒杀列表 当前页数条件
		list = new ArrayList<String>();
		list.add("cpage");
		map.put("/miaosha/listMiaoSha.do", new StatisticsModel("msl", "", list));
		
		
		//点菜页
		list = new ArrayList<String>();
		list.add("branchid");
		map.put("/diancai/gotoPromotion.do", new StatisticsModel("dcm", "",
				list));
		
		//外卖单页
		list = new ArrayList<String>();
		list.add("merchantId");
		map.put("/takeaway/showmenu.do", new StatisticsModel("wmy", "",
				list));
		//地图页
		map.put("/ditu/gotoFoodMap.do", new StatisticsModel("dcy", "",
				null));
		
		//点餐列表
		map.put("/diancai/diancanlist.do", new StatisticsModel("dcl", "",
				null));
		//电影首页
		map.put("/film/toshowFilmPage.do", new StatisticsModel("dyy","",null));
		
		
		//影片列表
		map.put("/film/toshowFilmList.do", new StatisticsModel("ypl","",null));
		
		//电影列表
		map.put("/cinema/cinemaList.do", new StatisticsModel("yyl","",null));

		
		//电影团购
		map.put("/film/grouplist.do", new StatisticsModel("dyt","",null));
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		try {
			// log.info("StatisticsInterceptor start...");
			String pagekey = request.getParameter(PAGE_KEY);
			// log.info("page param pagekey:" + pagekey);
			String requesturi = request.getRequestURI();
			if ("/forward.do".equals(requesturi)&&"index.index".equals(request.getParameter("param"))) {
				requesturi += "?param=index.index";
			}
			// log.info("request uri:" + requesturi);
			String pagepara = "";
			StatisticsModel sm = map.get(requesturi);
			if (sm != null) {
				if (org.apache.commons.lang.StringUtils.isBlank(pagekey)) {
					pagekey = sm.getPageKey();
				}
				pagepara = sm.getPagepara();
				List<String> listParam = sm.getParamList();
				if (listParam != null && listParam.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for (String string : listParam) {
						String value = request.getParameter(string);
						if (value == null) {
							value = (String) request.getAttribute(string);
							if (value == null) {
								value = "";
							}
						}
						sb.append(value);
						sb.append("_");
					}
					pagepara = sb.substring(0, sb.lastIndexOf("_"));
				}

			}
			request.setAttribute("pagekey", pagekey);
			request.setAttribute("pagepara", pagepara);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// log.info("StatisticsInterceptor end...");
	}

	@Override
	protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request) {

		return null;
	}

}
