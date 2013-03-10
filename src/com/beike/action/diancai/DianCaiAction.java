package com.beike.action.diancai;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.onlineorder.AbstractEngine;
import com.beike.entity.onlineorder.BookingGoods;
import com.beike.entity.onlineorder.BranchInfo;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.page.Pager;
import com.beike.service.diancai.DianCaiService;
import com.beike.service.seo.SeoService;
import com.beike.util.BeanUtils;
import com.beike.util.CatlogUtils;
import com.beike.util.Constant;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;

/**
 * com.beike.action.diancai.DianCaiAction.java
 * 
 * @description:点菜Action
 * @Author:xuxiaoxian Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
@Controller
public class DianCaiAction {

	@Autowired
	private DianCaiService dianCaiService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@Autowired
	private SeoService seoService;
	
	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}


	private static String REGION_CATLOG = "BASE_REGION_CATLOG";//商圈key

	private static String CITY_CATLOG = "CITY_CATLOG";//城市key
	
	private static String REGION_ORDER = "REGION_ORDER_";//可点餐的商圈
	
	private static int CACHE_TIME = 60*60;//缓存一小时
	
	private String GOODS_URL = "/goods/";
	
	/**
	 * janwen
	 * 
	 * @param request
	 * @param response
	 * @return 进入分店点餐首页
	 * 
	 */
	@RequestMapping("/diancai/gotoPromotion.do")
	public String gotoPromotion(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String branchid = request.getParameter("branchid");
			
			if (checkDigital(branchid)) {
				BranchInfo bi = dianCaiService
						.getBranchInfo(new Long(branchid));
				BookingGoods g = dianCaiService.getTopone(new Long(branchid));
				List<List<OrderMenu>> order_menu = dianCaiService
						.getOrderMenuListByBranchid(new Long(branchid), null);
				request.setAttribute("branch", bi);
				request.setAttribute("g", g);
				request.setAttribute("items", order_menu);
				List<String> all_menus = dianCaiService.getCategory(new Long(
						branchid));
				request.setAttribute("allmenus", all_menus);
				AbstractEngine promotionInfo = dianCaiService
						.getPromotionInfo(new Long(branchid));
				if(promotionInfo != null){
					request.setAttribute("pi", promotionInfo);
					
					boolean isOnline=false;
					//活动在线 再查打折
					if(promotionInfo.isOnline()){
						isOnline=true;
						String historyBranches = WebUtils.getCookieValue("historyBranches",
								request);
						/**
						 *  {
						 *  history:
						 *  [
						 *  {branchid:xx,
						 *  items:[{menuid:98,count:2,index:xxx,tag:xxx},
						 *  {menuid:93,count:2}]},
						 *  {branchid:xx,items:[{menuid:98,count:2},{menuid:93,count:2}]}
						 *  ]
						 *  };
						 */
						try {
							if (StringUtils.validNull(historyBranches)) {
								historyBranches = URLDecoder.decode(historyBranches,"utf-8");
								//logger.info(historyBranches);
								JSONObject history_json = new JSONObject(historyBranches);
								JSONArray jarr = (JSONArray) history_json.get("history");
								List<Long> cookieBranchids = new ArrayList<Long>();
								for(int i=0;i<jarr.length();i++){
									JSONObject j = jarr.getJSONObject(i);
									cookieBranchids.add(j.getLong("branchid"));
								}
								request.setAttribute("historybranches", dianCaiService
										.gethistoryOrderMenus(jarr,
												new Long(branchid)));
								
								
								
								request.setAttribute("cookiebranches", dianCaiService.getHistroyBranchesInfo(cookieBranchids));
								
							}
						} catch (JSONException e) {
							logger.info("点菜历史分店json格式不对");
						}
						
						
						
						
						Timestamp  start = promotionInfo.getStarttime();
						Timestamp end = promotionInfo.getEndtime();
						if(start.compareTo(Calendar.getInstance().getTime()) < 0 && end.compareTo(Calendar.getInstance().getTime()) > 0){
							request.setAttribute("promotion",
									promotionInfo.getPromotionInfo());
							request.setAttribute("discountjson", promotionInfo.formatJson());
						}
					}
					
					request.setAttribute("isOnline", isOnline);
					
				}
			
				
			}

			return "/foodmap/diancan";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
	}

	
	static final Log logger = LogFactory.getLog(DianCaiAction.class);

	@RequestMapping("/diancai/getMorePromotion.do")
	public void getMorePromotion(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String branchid = request.getParameter("branchid");
		String tag = request.getParameter("tag");
		List<String> tags = new ArrayList<String>();
		if (checkDigital(branchid) && StringUtils.validNull(tag)) {
			tags.add(tag);
			List<List<OrderMenu>> order_menu = dianCaiService
					.getOrderMenuListByBranchid(new Long(branchid), tags);
			String json_result = "";
			if (order_menu != null && order_menu.size() != 0) {
				try {
					List<JSONObject> objects = new ArrayList<JSONObject>();
					for (int i = 0; i < order_menu.get(0).size(); i++) {
						objects.add(new JSONObject(order_menu.get(0).get(i)));
					}
					json_result = new JSONArray(objects).toString();
					//logger.info(json_result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			response.setCharacterEncoding("utf-8");
			// response.setContentType("conte");
			response.getWriter().write(json_result);
		}
	}

	boolean checkDigital(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (StringUtils.validNull(params[i])
					&& Pattern.matches("\\d*", params[i].toString())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	* @Title: listOfOrders
	* @Description: 获取点餐分店列表
	* @param @param request
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping("/diancai/diancanlist.do")
	public String listOfOrders(HttpServletRequest request,HttpServletResponse response){
		try{
			boolean isSupport = CityUtils.isOpenDiancan(request, response);
			if(!isSupport){
				return "redirect:../404.html";
			}
			//取参数{城市、一级商圈、二级商圈}
			String city = CityUtils.getCity(request,response);//城市
			Long cityId = CityUtils.getCityId(request, response);//城市标识
			String regionid = request.getParameter("region");//一级商圈
			String regionextid = request.getParameter("regionext");//二级商圈
			String isImg = request.getParameter("isImg");//分为列表和大图两种视图
			if(null==isImg){
				isImg = "_img";//默认大图视图
			}
			request.setAttribute("isImg", isImg);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
			Date now = new Date();
			String nowStr = format.format(now.getTime());//当前日期时间
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("cityId", cityId+"");
			paramMap.put("regionid", regionid);
			paramMap.put("regionextid", regionextid);
			paramMap.put("nowStr", nowStr);
			// 商圈
			//一级商圈
			Map<Long, List<RegionCatlog>> map = (Map<Long, List<RegionCatlog>>) memCacheService.get(REGION_ORDER+cityId);
			if(null == map){
				map = dianCaiService.getSupportOrderRegion(cityId,nowStr);
				memCacheService.set(REGION_ORDER+cityId, map, CACHE_TIME);
			}
			request.setAttribute("regionMap", map);
			//二级商圈
			if (StringUtils.validNull(regionid)) {
				List<RegionCatlog> listRegion = map.get(Long.parseLong(regionid));
				request.setAttribute("listRegion", listRegion);
				request.setAttribute("region", regionid);
				request.setAttribute("regionext", regionextid);
			}
			
			//分页
			String totalCacheKey = city+"_orderslist_total_"+(regionid==null ? "" :regionid+"_")+(regionextid==null ? "" : regionextid);
			Integer total = (Integer)memCacheService.get(totalCacheKey);
			if(null==total){
				total = dianCaiService.getCountListOfOrders(paramMap);
				memCacheService.set(totalCacheKey, total, CACHE_TIME);
			}
			if(total == 0){
				return "/foodmap/diancanlist";
			}
			String cpage = request.getParameter("cpage");
			if(null == cpage || "".equals(cpage)){
				cpage = "1";
			}
			int currentPage = Integer.parseInt(cpage);
			Pager pager = new Pager(currentPage,total,20);
			request.setAttribute("cpage", currentPage);
			request.setAttribute("pager", pager);
			
			//获取商圈内可点餐的分店
			String cacheKey = city+"_orderslist_"+(regionid==null ? "" :regionid+"_")+(regionextid==null ? "" : regionextid+"_")+currentPage;
			List<Map<String, Object>> branchList = (List<Map<String, Object>>)memCacheService.get(cacheKey);
	
			if(null == branchList){
				branchList = dianCaiService.listOfOrders(paramMap,pager);
				memCacheService.set(cacheKey, branchList, CACHE_TIME);
			}
			request.setAttribute("branchList", branchList);
			
			//返回客户端
			return "/foodmap/diancanlist";
		}catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
	} 
	/**
	 * 
	* @Title: getCity
	* @Description: 获取城市
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 *//*
	public String getCity(HttpServletRequest request,HttpServletResponse response){
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get(CITY_CATLOG);
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set(CITY_CATLOG, mapCity);
		}
		String city = CityUtils.getCity(request, response);
		if (city == null || "".equals(city)) {
			city = "beijing";
		}
		return city;
	}
	*//**
	 * 
	* @Title: getCity
	* @Description: 获取城市
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 *//*
	public Long getCityId(HttpServletRequest request,HttpServletResponse response){
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
			cityid = mapCity.get(city.trim());
		}
		return cityid;
	}*/
	/**
	 * 
	* @Title: getRegionMap
	* @Description: 获取商圈
	* @param @param request
	* @param @return    设定文件
	* @return Map<String,Map<Long,List<RegionCatlog>>>    返回类型
	* @throws
	 */
	public Map<String, Map<Long, List<RegionCatlog>>> getRegionMap(HttpServletRequest request){
		Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService.get(REGION_CATLOG);
		// 假如memcache里没有就从数据库里查
		if (regionMap == null) {
			regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
			memCacheService.set(REGION_CATLOG, regionMap);
		}
		return regionMap;
	}
}
