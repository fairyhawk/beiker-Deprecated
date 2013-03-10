package com.beike.action.lucene.search;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.LogAction;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.lucene.search.LuceneAlertService;
import com.beike.service.lucene.search.LuceneSearchFacadeService;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.lucene.IndexDatasourcePathUtil;
import com.beike.util.lucene.LuceneSearchConstants;
import com.beike.util.lucene.QueryWordFilter;

@Controller
public class LuceneSearchAction {

	@Autowired
	private LuceneSearchFacadeService facadeService;
	@Autowired
	private LuceneAlertService luceneAlertService;
	static final String ALERT_SUBJECT = "千品网搜索服务异常告警邮件";

	static final String ALERT_EMAILCODE = "SEARCH_SERVICE_ALERT";
	Logger logger = Logger.getLogger(LuceneSearchAction.class);
	MemCacheService mm = MemCacheServiceImpl.getInstance();

	@RequestMapping(value = { "/search/searchGoods.do" })
	public String SearchGoodsResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_keyword = null;
		String city_en_name = null;
		try {
			request.setCharacterEncoding("utf-8");
			// 当前页
			String currentPage = request.getParameter("pn");
			city_en_name = WebUtils.getCookieValue(CityUtils.CITY_COOKIENAME, request);
			goods_keyword = request.getParameter("kw");
			goods_keyword = QueryWordFilter.decodeQueryWord(goods_keyword);
			goods_keyword = StringUtils.trim(goods_keyword);
			//搜索关键词不允许?,*开头
			String goods_keyword_query = QueryWordFilter.filterQueryWord(goods_keyword);
			goods_keyword_query = StringUtils.trim(goods_keyword_query);
			if (currentPage == null || "".equals(currentPage)) {
				currentPage = "1";
			}
			request.setAttribute("pn", currentPage);
			if (city_en_name == null || "".equals(city_en_name) || !Pattern.matches("^[1-9]\\d*$", currentPage) || goods_keyword_query == null || "".equals(goods_keyword_query) || goods_keyword == null || "".equals(goods_keyword)) {
				return "redirect:/goods/searchGoodsByProperty.do";
			}

			int currentPageNo = Integer.parseInt(currentPage);
			//加入统计代码 by janwen at 2012-1-17 11:49:25
			printLog(request, response, URLDecoder.decode(goods_keyword, "utf-8"), "g", currentPage);
			Map<String, Object> searchMap = facadeService.getSearchGoodsMap(goods_keyword_query, city_en_name, currentPageNo, LuceneSearchConstants.GOODS_PAGE_SIZE);
			// 计算分页
			int totalCount = 0;
			totalCount = Integer.parseInt(searchMap.get(LuceneSearchConstants.SEARCH_RESULTS_COUNT).toString());

			Pager pager = PagerHelper.getPager(currentPageNo, totalCount, LuceneSearchConstants.GOODS_PAGE_SIZE);
			List<Long> nextPageid = (List<Long>) searchMap.get(LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID);

			request.setAttribute("pager", pager);
			request.setAttribute("totalResults", totalCount);

			request.setAttribute("searchedGoods", facadeService.getSearchGoodsResult(nextPageid));

			request.setAttribute("keyword", goods_keyword);

			//搜索统计结束
			return "search/searchgoods";
		} catch (IllegalArgumentException e) {
			return "redirect:/goods/searchGoodsByProperty.do";
		} catch (Exception e) {
			// 发送告警邮件
			e.printStackTrace();
			Map<String, String[]> alertmail = IndexDatasourcePathUtil.getAlertEmail(city_en_name, LuceneSearchConstants.SEARCH_TYPE_GOODS, goods_keyword);
			String[] mailaddress = alertmail.get("alertaddress");
			for (int i = 0; i < mailaddress.length; i++) {
				luceneAlertService.sendMail(ALERT_SUBJECT, mailaddress[i], alertmail.get("emailparams"), ALERT_EMAILCODE);
			}

			return "redirect:/404.html";
		}
	}

	@RequestMapping(value = { "/search/searchBrand.do" })
	public String SearchBrandResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String city_en_name = null;
		String brand_keyword = null;
		try {
			request.setCharacterEncoding("utf-8");
			// 当前页
			String currentPage = request.getParameter("pn");

			if (currentPage == null || "".equals(currentPage)) {
				currentPage = "1";
			}
			;
			int currentPageNo = Integer.parseInt(currentPage);

			city_en_name = WebUtils.getCookieValue(CityUtils.CITY_COOKIENAME, request);
			brand_keyword = request.getParameter("kw");
			brand_keyword = QueryWordFilter.decodeQueryWord(brand_keyword);
			//加入统计代码 by janwen at 2012-1-17 11:49:25
			printLog(request, response, URLDecoder.decode(brand_keyword, "utf-8"), "b", currentPage);
			brand_keyword = StringUtils.trim(brand_keyword);
			//搜索关键词不允许?,*开头
			String brand_keyword_query = QueryWordFilter.filterQueryWord(brand_keyword);
			brand_keyword_query = StringUtils.trim(brand_keyword_query);
			if (city_en_name == null || "".equals(city_en_name) || !Pattern.matches("^[1-9]\\d*$", currentPage) || brand_keyword == null || "".equals(brand_keyword) || brand_keyword_query == null || "".equals(brand_keyword_query)) {
				return "redirect:/brand/searchBrandsByProperty.do";
			}

			Map<String, Object> searchMap = facadeService.getSearchBrandMap(brand_keyword_query, city_en_name, currentPageNo, LuceneSearchConstants.BRAND_PAGE_SIZE);
			// 计算分页
			int totalCount = 0;
			totalCount = Integer.parseInt(searchMap.get(LuceneSearchConstants.SEARCH_RESULTS_COUNT).toString());

			Pager pager = PagerHelper.getPager(currentPageNo, totalCount, LuceneSearchConstants.BRAND_PAGE_SIZE);
			List<Long> nextPageid = (List<Long>) searchMap.get(LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID);

			request.setAttribute("pager", pager);
			request.setAttribute("totalResults", totalCount);
			request.setAttribute("searchedBrand", facadeService.getSearchBrandResult(nextPageid));

			request.setAttribute("keyword", brand_keyword);

			return "search/searchbrand";
		} catch (IllegalArgumentException e) {
			return "redirect:/brand/searchBrandsByProperty.do";
		} catch (Exception e) {
			e.printStackTrace();
			// 发送告警邮件
			Map<String, String[]> alertmail = IndexDatasourcePathUtil.getAlertEmail(city_en_name, LuceneSearchConstants.SEARCH_TYPE_BRAND, brand_keyword);
			String[] mailaddress = alertmail.get("alertaddress");
			for (int i = 0; i < mailaddress.length; i++) {
				luceneAlertService.sendMail(ALERT_SUBJECT, mailaddress[i], alertmail.get("emailparams"), ALERT_EMAILCODE);
			}
			return "redirect:/404.html";
		}
	}

	private void printLog(HttpServletRequest request, HttpServletResponse response, String keyword, String type, String pageNo) {

		Map<String, String> mapLog = LogAction.getLogMap(request, response);
		mapLog.put("action", "p_list");
		mapLog.put("keyword", keyword);
		mapLog.put("p", pageNo);
		mapLog.put("type", type);
		LogAction.printLog(mapLog);
	}
}
