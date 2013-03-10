package com.beike.action.unionpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.entity.background.area.Area;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.form.GoodsForm;
import com.beike.service.background.area.AreaService;
import com.beike.service.lucene.recommend.LuceneRecommendService;
import com.beike.service.lucene.search.LuceneSearchFacadeService;
import com.beike.service.unionpage.UnionPageService;
import com.beike.util.BeanUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;
import com.beike.util.lucene.LuceneSearchConstants;
import com.beike.util.lucene.QueryWordFilter;
 /*
 * com.beike.action.unionpage.UnionPageAction.java
 * @description:静态聚合页相关action
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-5-18，xuxiaoxian ,create class
 *
 */
@SuppressWarnings("unchecked")
@Controller
public class UnionPageAction {
	
	Logger logger = Logger.getLogger(UnionPageAction.class);
	
	@Autowired
	private UnionPageService unionPageService;
	
	@Autowired
	private LuceneSearchFacadeService facadeService;
	
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	
	@Autowired
	private LuceneRecommendService luceneRecommendService;
	
	private static String CITY_CATLOG = "CITY_CATLOG";
	
	MemCacheService memCache = MemCacheServiceImpl.getInstance();
	
	@RequestMapping("/union/initUnionIndex.do")
	public Object initWebSiteMap(HttpServletRequest request,
			HttpServletResponse response){		
		List<Map<String,Object>> listKW = unionPageService.getAllKeyWordMsg();
		request.setAttribute("listKeyWord", listKW);
		return "union/unionindex";
	}

	@RequestMapping("/union/keyWordSearch.do")
	public Object keyWordSearch(HttpServletRequest request,
			HttpServletResponse response){
		try{
			String keyWordId = request.getParameter("keywordid");
			String city_en_name = request.getParameter("city_en_name");
			
			logger.info("keyWordId===" + keyWordId + "===city_en_name===" + city_en_name);
			String keyword = unionPageService.getKeyWordById(Integer.parseInt(keyWordId));
			if(StringUtils.isEmpty(keyword)){
				return  "redirect:/union/initUnionIndex.do";
			}
			keyword = StringUtils.trim(keyword);
			logger.info("keyword===" + keyword);
			
			//搜索关键词不允许?,*开头
			String goods_keyword_query = QueryWordFilter.filterQueryWord(keyword);
			goods_keyword_query = StringUtils.trim(goods_keyword_query);
			Map<String,String> keywordMap = new HashMap<String,String>();
			keywordMap.put("keyWordId", keyWordId);
			keywordMap.put("keyWord", goods_keyword_query);
			request.setAttribute("keywordMap", keywordMap);
			
			//所在城市中英文名称
			String currentcity = CityUtils.getCity(request, response);
			String areaCnName = IPSeeker.getCityByStr(currentcity.trim());
			Area currentArea = new Area();
			currentArea.setAreaEnName(currentcity);
			currentArea.setAreaCnName(areaCnName);
			request.setAttribute("currentArea", currentArea);
			
			List<Area> listOnlineArea = areaService.queryOnlineArea();
			
			//分城市关键词推荐导航
			for(Area area : listOnlineArea){
				String area_en_name = area.getAreaEnName();//上线城市英文名
				if(StringUtils.isEmpty(area_en_name)){
					area_en_name = PinyinUtil.hanziToPinyin(area.getAreaCnName());
				}
				area.setAreaEnName(area_en_name.toLowerCase());
			}
			request.setAttribute("listOnLineArea", listOnlineArea);
			
			//其它推荐
			String[] aryRecommendKeywords = luceneRecommendService.getSilimarWords(goods_keyword_query, 6);
			List<Map<String,String>> listRecommendMsg = 
							unionPageService.getListMsgByKeyWords(aryRecommendKeywords,6);
			request.setAttribute("recommandKeyWord", listRecommendMsg);
			logger.info("aryRecommendKeywords===" + ArrayUtils.toString(aryRecommendKeywords));
			
			//截取URL中的二级域名
			if(StringUtils.isEmpty(city_en_name)){
				String url = request.getRequestURL().toString();
				city_en_name = url.substring("http://".length(), url.indexOf("."));
			}
			
			//分城市关键词搜索结果
			if(!city_en_name.equals("www")){
				List<Long> listIds = null;
				try{
					Map<String, Object> searchMap = facadeService.getSearchGoodsMap(
							goods_keyword_query, city_en_name, 1,LuceneSearchConstants.CITY_SEARCH_UNION_PAGE_SIZE);
					listIds = (List<Long>) searchMap.get(
							LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID);	
				}catch(Exception e){
					e.printStackTrace();
				}
				//按关键词查询无商品的时候，查询该城市的热卖商品
				if(listIds == null || listIds.size() == 0){
					listIds = (List<Long>) memCache.get(city_en_name + "_hot_good_keyword");
					if (listIds == null) {
						GoodsCatlog goodsCatLog = new GoodsCatlog();
						Map<String, Long> mapCity = (Map<String, Long>) memCache.get(CITY_CATLOG);
						if (mapCity == null) {
							mapCity = BeanUtils.getCity(request, "regionCatlogDao");
							memCache.set(CITY_CATLOG, mapCity);
						}
						Long cityid = null;
						if (mapCity != null) {
							cityid = mapCity.get(city_en_name);
						}
						goodsCatLog.setCityid(cityid);
						listIds = goodsCatlogService.getCatlogRank(goodsCatLog, null);
						if (listIds != null) {
							memCache.set(city_en_name + "_hot_good_keyword", listIds);
						}
					}
				}
				if(listIds!=null){
					logger.info("listIds===" + ArrayUtils.toString(listIds.toArray()));
				}
				
				List<GoodsForm> listGoodsForm  = facadeService.getSearchGoodsResult(listIds);
				for(GoodsForm goods : listGoodsForm){
					String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
					if(IPSeeker.getCityByStr(city) == null){
						city = "www";
					}
					goods.setCity(city);
				}
				request.setAttribute("listGoodsForm", listGoodsForm);
				request.setAttribute("searchCity", IPSeeker.getCityByStr(city_en_name));
				return "union/citykeywordsearch";
			}else{
				List<Long> goodsIdList = (List<Long>)memCache.get("KEY_WORD_SEARCH_"+keyWordId);
				if(goodsIdList == null || goodsIdList.size() == 0){
					goodsIdList = new ArrayList<Long>();
					Object[] arrayGoodsId = new Object[listOnlineArea.size()];
					for(int i = 0;i<listOnlineArea.size();i++){
						Area area = listOnlineArea.get(i);
						city_en_name = area.getAreaEnName();
						if(StringUtils.isEmpty(city_en_name)){
							city_en_name = PinyinUtil.hanziToPinyin(area.getAreaCnName());
						}
						city_en_name = city_en_name.toLowerCase();
						try{
							Map<String, Object> searchMap = facadeService.getSearchGoodsMap(
									goods_keyword_query, city_en_name, 1,LuceneSearchConstants.UNION_PAGE_SIZE);
							List<Long> nextPageid = (List<Long>) searchMap.get(
									LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID);	
							arrayGoodsId[i] = nextPageid;
						}catch(Exception e){
							e.printStackTrace();
						}					
					}
					goodsIdList = getListId(arrayGoodsId,goodsIdList);
					int exptime = 6*60*60;
					memCache.set("KEY_WORD_SEARCH_"+keyWordId, goodsIdList, exptime);					
				}
				if(goodsIdList!=null){
					logger.info("goodsIdList===" + ArrayUtils.toString(goodsIdList.toArray()));
				}
				List<GoodsForm> listGoodsForm = facadeService.getSearchGoodsResult(goodsIdList);
				for(GoodsForm goods : listGoodsForm){
					String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
					if(IPSeeker.getCityByStr(city) == null){
						city = "www";
					}
					goods.setCity(city);
				}
				request.setAttribute("listGoodsForm", listGoodsForm);				
				return "union/keywordsearch";
			}
		}catch(Exception e){
			e.printStackTrace();
			return  "redirect:/union/initUnionIndex.do";
		}
	}
	
	/** 
	 * @date 2012-5-18
	 * @description：从上线城市中取出相应关键词的搜索结果
	 * @param arrayGoodsId
	 * @param goodsIdList
	 * @param flag
	 * @return List<Long>
	 * @throws 
	 */
	private List<Long> getListId(Object[] arrayGoodsId,List<Long> goodsIdList){

		//遍历数组，取出每个城市的商品Id
		for(int i = 0 ; i< LuceneSearchConstants.UNION_PAGE_SIZE;i++){
			for(int j = 0 ; j<arrayGoodsId.length;j++){
				List<Long> listid = (List<Long>)arrayGoodsId[j];
				if(listid != null && listid.size() > i){
					Long id = listid.get(i);//取出数组中固定位置的商品Id
					if(id != null && !id.equals(0L)){
						if(goodsIdList.size()<LuceneSearchConstants.CITY_SEARCH_UNION_PAGE_SIZE){
							goodsIdList.add(id);
						}else{
							return goodsIdList;
						}
					}
				}	
			}
		}
		return goodsIdList;
	}
	public UnionPageService getUnionPageService() {
		return unionPageService;
	}

	public void setUnionPageService(UnionPageService unionPageService) {
		this.unionPageService = unionPageService;
	}

	public LuceneSearchFacadeService getFacadeService() {
		return facadeService;
	}

	public void setFacadeService(LuceneSearchFacadeService facadeService) {
		this.facadeService = facadeService;
	}

	public AreaService getAreaService() {
		return areaService;
	}

	public void setAreaService(AreaService areaService) {
		this.areaService = areaService;
	}


	public GoodsCatlogService getGoodsCatlogService() {
		return goodsCatlogService;
	}


	public void setGoodsCatlogService(GoodsCatlogService goodsCatlogService) {
		this.goodsCatlogService = goodsCatlogService;
	}

	public LuceneRecommendService getLuceneRecommendService() {
		return luceneRecommendService;
	}

	public void setLuceneRecommendService(
			LuceneRecommendService luceneRecommendService) {
		this.luceneRecommendService = luceneRecommendService;
	}
}
