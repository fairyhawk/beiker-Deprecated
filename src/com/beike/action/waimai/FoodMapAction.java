package com.beike.action.waimai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.page.Pager;
import com.beike.service.lucene.search.LuceneSearchFacadeService;
import com.beike.service.waimai.WaiMaiService;
import com.beike.util.Constant;
import com.beike.util.LatitudeUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.json.JsonUtil;
import com.beike.util.lucene.LuceneSearchConstants;
import com.beike.util.lucene.QueryWordFilter;
 /**
 * com.beike.action.waimai.FoodMapAction.java
 * @description:美食地图Action
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */

@SuppressWarnings("unchecked")
@Controller
public class FoodMapAction {

	Logger log = Logger.getLogger(FoodMapAction.class);
	
	@Autowired
	public WaiMaiService waiMaiService;
	
	@Autowired
	public LuceneSearchFacadeService luceneSearchFacadeService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private static int INIT_POINT_SIZE = 100;
	private static int ACTIVE_POINT_SIZE = 30;
	
	
	@RequestMapping("/ditu/initFoodMap.do")
	public void initFoodMap(HttpServletRequest request,HttpServletResponse response) 
									throws JSONException,IOException{
		String startLng  = request.getParameter("boundsswlng");
		String startLat  = request.getParameter("boundsswlat");
		String endLng  = request.getParameter("boundsnelng");
		String endLat  = request.getParameter("boundsnelat");
		String isfirst = request.getParameter("isfirst");
		JSONObject response_json = new JSONObject();
		response.setCharacterEncoding("utf8");
		try {
			//分店信息		
			if(StringUtils.isNotBlank(startLng) && StringUtils.isNotBlank(startLat) 
					&& StringUtils.isNotBlank(endLng)  && StringUtils.isNotBlank(endLat)){
				
				Map<String,String> conditionMap = new HashMap<String,String>();
				conditionMap.put("startLng", startLng);
				conditionMap.put("endLng", endLng);
				conditionMap.put("startLat", startLat);
				conditionMap.put("endLat", endLat);
				conditionMap.put("support_takeaway", "1");
				//首次到这个页面的时候标小气泡
				if(StringUtils.isNotBlank(isfirst)){
					String city = CityUtils.getCity(request, response);
					List<Map> smallPoint= (List<Map>)memCacheService.get("SMALL_POINT_JSON_"+city);
					if(null == smallPoint){
						city = IPSeeker.getCityByStr(city);
						Map<String,String> spMap = new HashMap<String,String>();
						spMap.put("city" , city);
						spMap.put("support_takeaway" , "1");						
						List<Map<String,Object>> merchantList = waiMaiService.getMerDetailByConditions(spMap, 0, INIT_POINT_SIZE);
						smallPoint = this.getResponseJson(merchantList,false);
						memCacheService.set("SMALL_POINT_JSON_"+city, smallPoint,60*60);
					}
					response_json.put("pointMessage", smallPoint.toArray());
				}
				
				Integer totalCount = (Integer) memCacheService.get(
							"FOOD_MAP_MER_COUNT_"+startLng+endLng+startLat+endLat);
				if(totalCount == null){
					totalCount = waiMaiService.getMerchantCount(conditionMap, true);
					memCacheService.set("FOOD_MAP_MER_COUNT_"+startLng+endLng
													+startLat+endLat,totalCount,60*60);
				}
				String cpage = request.getParameter("cpage");
				if(StringUtils.isBlank(cpage)){
					cpage = "1";
				}
				int currentPage = Integer.parseInt(cpage);
				Pager pager = new Pager(currentPage,totalCount,Constant.PAGE_SIZE);
				
				
				//地图页右侧分店信息（分页缓存）
				List<Map> resList = (List<Map>)this.memCacheService.get(
											"FOOD_MAP_MERDETAIL_"+startLng+endLng+startLat+endLat+currentPage);
				if(null == resList){
					List<Map<String,Object>> listMerchant  = waiMaiService.getDistinctMerDetail(
																conditionMap, pager.getStartRow(),ACTIVE_POINT_SIZE);
					resList = this.getResponseJson(listMerchant,true);
					this.memCacheService.set("FOOD_MAP_MERDETAIL_"
							+startLng+endLng+startLat+endLat+currentPage,resList,60*60);
				}
				
				if(null != resList && resList.size() > 0){
					int totalPage = pager.getTotalPages();
					if(totalPage > 1){
						response_json.put("pageStr", this.getPageStr(pager)); //分页信息
					}
					response_json.put("cpage",cpage);
					response_json.put("merchantForm",resList.toArray());	
				}else{
//				response_json.put("response_json", "some_message");		
				}
			}else{
//			response_json.put("response_json", "some_message");		
			}
			response.getWriter().write(response_json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			response.getWriter().write(response_json.toString());
		}		
	}
	
	@RequestMapping("/ditu/foodMapSearch.do")
	public void foodMapSearch(HttpServletRequest request,HttpServletResponse response)
											throws IOException, JSONException{
		String keyWord = request.getParameter("keyword");
		String startLng  = request.getParameter("boundsswlng");
		String startLat  = request.getParameter("boundsswlat");
		String endLng  = request.getParameter("boundsnelng");
		String endLat  = request.getParameter("boundsnelat");
		String city = CityUtils.getCity(request, response);
		String isfirst = request.getParameter("isfirst");
		JSONObject response_json = new JSONObject();
		response.setCharacterEncoding("utf8");
		try {
			if(StringUtils.isNotBlank(city) && StringUtils.isNotBlank(keyWord) 
					&& StringUtils.isNotBlank(startLng) && StringUtils.isNotBlank(startLat) 
					&& StringUtils.isNotBlank(endLng) && StringUtils.isNotBlank(endLat)){
				Map<String,String> conditionMap = new HashMap<String,String>();
				String foodtype = LuceneSearchConstants.WAIMAI;

				conditionMap.put("support_takeaway", "1");
				conditionMap.put("startLng", startLng);
				conditionMap.put("endLng", endLng);
				conditionMap.put("startLat", startLat);
				conditionMap.put("endLat", endLat);				
				
				//搜索关键词不允许?,*开头
				String luceneKeyWord = QueryWordFilter.filterQueryWord(keyWord);
				//关键词搜索到的所有分店Id
				List<Long> merchantIdList = null;
				if(StringUtils.isNotBlank(luceneKeyWord)){
					luceneKeyWord = StringUtils.trim(luceneKeyWord);
					merchantIdList = (List<Long>)memCacheService.get("FOOD_MAP_SEARCH_MERID_"+city+luceneKeyWord);
					if(null == merchantIdList){
						merchantIdList = luceneSearchFacadeService.getSearchBranchMap(luceneKeyWord, city, foodtype);
						if(null != merchantIdList && merchantIdList.size() > 0){
							memCacheService.set("FOOD_MAP_SEARCH_MERID_"+city+luceneKeyWord, merchantIdList,60*60);
						}
					}
				}
				if(null != merchantIdList && merchantIdList.size() > 0){
					//第一次点搜索分店时候重新标地图上面的小点
					if(StringUtils.isNotBlank(isfirst)){
						List<Map> firstSearchSP = (List<Map>) memCacheService.get("FIRST_SEARCH_SMALL_POINT_"+city+luceneKeyWord);
						if(null == firstSearchSP){
							List<Map<String,Object>> merchantList = this.waiMaiService.getSearchMerDetailByIds(
									merchantIdList, new HashMap<String,String>(), 0,INIT_POINT_SIZE);
							firstSearchSP = this.getResponseJson(merchantList,false);
							memCacheService.set("FIRST_SEARCH_SMALL_POINT_"+city+luceneKeyWord, firstSearchSP,60*60);
						}
						response_json.put("pointMessage", firstSearchSP.toArray());
					}
					Integer totalCount = (Integer)memCacheService.get("FOOD_MAP_SEARCH_COUNT_"
																				+city+keyWord+startLng+endLng+startLat+endLat);
					if(null == totalCount){
						totalCount = this.waiMaiService.getSearchMerCountByIds(merchantIdList, conditionMap);
						memCacheService.set("FOOD_MAP_SEARCH_COUNT_"
								+city+keyWord+startLng+endLng+startLat+endLat, totalCount,60*60);
					}
						
					String cpage = request.getParameter("cpage");
					if(StringUtils.isBlank(cpage)){
						cpage = "1";
					}
					int currentPage = Integer.parseInt(cpage);
					Pager pager = new Pager(currentPage,totalCount,Constant.PAGE_SIZE);
					//美食地图关键词搜索分页缓存（FMSMD:FOOD_MAP_SEARCH_MERCHANT_DETAIL）
					List<Map> resList = (List<Map>)memCacheService.get(
							"FMSMD_"+city+keyWord+startLng+endLng+startLat+endLat+cpage);
					if(null == resList){
						List<Map<String,Object>> merchantList = this.waiMaiService.getSearchMerDetailByIds(
																		merchantIdList, conditionMap, pager.getStartRow(),ACTIVE_POINT_SIZE);
						resList = this.getResponseJson(merchantList,false);
						memCacheService.set("FMSMD_"+city+keyWord+startLng+endLng+startLat+endLat+cpage, resList,60*60);
					}
					if(null != resList && resList.size() > 0){
						if(pager.getTotalPages() > 1){
							response_json.put("pageStr", this.getPageStr(pager));//分页信息
						}
						response_json.put("cpage",cpage);
						response_json.put("merchantForm",resList.toArray());	
					}else{
//					response_json.put("response_json","someMessage");
					}
				}else{
//				response_json.put("response_json","someMessage");
				}
			}else{
//			response_json.put("response_json","someMessage");
			}
			response.getWriter().write(response_json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			log.info(e.getMessage());
			response.getWriter().write(response_json.toString());
		}
	}

	@RequestMapping("/ditu/gotoFoodMap.do")
	public String goToFoodMap(HttpServletRequest request,HttpServletResponse response){
		return "/foodmap/foodmap";		
	}
	
	@RequestMapping("/ditu/foodMapAreaSearch.do")
	public void foodMapAreaSearch(HttpServletRequest request, HttpServletResponse response){
		try{
			String keyWord = request.getParameter("keyword");
			if(StringUtils.isNotBlank(keyWord)){
				String city = CityUtils.getCity(request, response);
				city = IPSeeker.getCityByStr(city);
				Map<String,String> latitudeMap = (Map<String,String>)memCacheService.get(
														"FOODMAP_AREA_SEARCH_"+city+keyWord);
				if(null == latitudeMap){
					latitudeMap = LatitudeUtils.getGeocoderLatitude(city+keyWord);
					if(null != latitudeMap){
						memCacheService.set("FOODMAP_AREA_SEARCH_"+city+keyWord, latitudeMap,60*60*24*7);
					}
				}
				response.getWriter().write(JsonUtil.mapToJson(latitudeMap));
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** 
	 * @description：拼装分页信息
	 * @param pager
	 * @return String
	 * @throws 
	 */
	private String getPageStr(Pager pager){
		StringBuilder pageStr = new StringBuilder("");
		int totalPage = pager.getTotalPages();
		int currentPage = pager.getCurrentPage();
		if(currentPage == 1){
			pageStr.append("<li style='height:40px; width:100%;'>")
			.append("<div class='map_list_page' style='position:absolute;'>")
			.append("<span class='disabled'>首页</span><span class='disabled'>上一页</span>")
			.append("<span class='current'>1</span><a href='javascript:;' page='2'>下一页</a>")
			.append("<a href='javascript:;' page='").append(totalPage).append("'>末页</a></div></li>");
		}else if(totalPage > 2 && currentPage > 1 && currentPage < totalPage ){
			pageStr.append("<li style='height:40px; width:100%;'>")
			.append("<div class='map_list_page' style='position:absolute;'>")
			.append("<a href='javascript:;' page='1'>首页</a>")
			.append("<a href='javascript:;' page='").append(currentPage-1).append("'>上一页</a>")
			.append("<span class='current'>").append(currentPage).append("</span>")
			.append("<a href='javascript:;' page='").append(currentPage+1).append("'>下一页</a>")
			.append("<a href='javascript:;' page='").append(totalPage).append("'>末页</a></div></li>");
		}else{
			pageStr.append("<li style='height:40px; width:100%;'>")
			.append("<div class='map_list_page' style='position:absolute;'>")
			.append("<a href='javascript:;' page='1'>首页</a>")
			.append("<a href='javascript:;' page='").append(totalPage-1).append("'>上一页</a>")
			.append("<span class='current'>").append(totalPage).append("</span>")
			.append("<span class='disabled'>下一页</span>")
			.append("<span class='disabled'>末页</span></div></li>");
		}
		return pageStr.toString();
	}
	
	List<Map> getResponseJson(List<Map<String,Object>> merchantList,boolean distinctMerchant){
		List<Map> resList = new ArrayList<Map>();
		if(null != merchantList && merchantList.size() > 0){
			Set<String> brandSet = new HashSet<String>();
			List<Long> listBrandId = new ArrayList<Long>();
			for(Map<String,Object> resMap : merchantList){
				String brandId = resMap.get("parentId").toString();
				if(!brandSet.contains(brandId)){
					String brandName = (String)resMap.get("brandname");
					String tel = (String)resMap.get("tel");
					String lng = resMap.get("lng").toString();
					String lat = resMap.get("lat").toString();
					String merchantid = resMap.get("merchantid").toString();
					String support_waimai = (String)resMap.get("is_support_takeaway");
					String support_diancan = (String)resMap.get("is_support_online_meal");
					String address = (String)resMap.get("addr");
					String merchantCity = (String)resMap.get("city");
					String merchantname = (String)resMap.get("merchantname");
					listBrandId.add(Long.parseLong(brandId));
					Map<String,String> jsonMap = new HashMap<String,String>();
					jsonMap.put("merchantName", brandName+"("+merchantname+")");
					jsonMap.put("tel", tel);
					jsonMap.put("lng", lng);
					jsonMap.put("lat", lat);
					jsonMap.put("merchantid", merchantid);
					jsonMap.put("address", address);
					jsonMap.put("city", PinyinUtil.hanziToPinyin(merchantCity,""));
					jsonMap.put("song", support_waimai.equals("1")  ?  "OK" : "NO");
					jsonMap.put("dian", support_diancan.equals("1")  ?  "OK" : "NO");
					jsonMap.put("brandid", brandId);
					resList.add(jsonMap);
				}				
				if(distinctMerchant){
					brandSet.add(brandId);
				}
			}
			Set<String> onLineBrand =  waiMaiService.isBrandContainOnLineGoods(listBrandId);
			for(Map<String,String> map : resList){
				String brandId = map.get("brandid");
				map.put("tuan", onLineBrand.contains(brandId) ? "OK" : "NO");
			}
		}
		return resList;
	}
}
