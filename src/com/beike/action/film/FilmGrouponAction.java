package com.beike.action.film;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.common.search.SearchStrategy;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.CinemaInfo;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.film.CinemaService;
import com.beike.service.film.FilmGrouponService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**
 * 电影票团购action
 * @author weiwei
 */
@Controller
@SuppressWarnings("unchecked")
public class FilmGrouponAction extends BaseUserAction {

	private final SearchStrategy searchStrategy = new SearchStrategy();
	@Autowired
	private CinemaService cinemaService;
	@Autowired
	private FilmGrouponService filmGrouponService;
	@Autowired
	private GoodsCatlogService goodsCatlogService;

	private MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	//影院列表
	@RequestMapping("/film/grouplist.do")
	public String cinemaList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Long cityId = CityUtils.getCityId(request, response);
			Long areaId = null;
			try {
				areaId = Long.valueOf(request.getParameter("areaId"));
			} catch (Exception e) {}
			Long cinemaId = null;
			try {
				cinemaId = Long.valueOf(request.getParameter("cinemaId"));
			} catch (Exception e) {}
			String scope = StringUtils.trimToEmpty(request.getParameter("scope"));
			//临时条件
			scope = "all";
			String sort = StringUtils.trimToEmpty(request.getParameter("sort"));
			Integer page = NumberUtils.toInt(request.getParameter("page"), 1);
			boolean cashOnly = BooleanUtils.toBoolean(request.getParameter("cashOnly"));

			//影院区域列表
			List<RegionCatlog> cinemaAreas;
			cinemaAreas = (List<RegionCatlog>) memCacheService.get("CinemaAreas_" + cityId);
			if (CollectionUtils.isEmpty(cinemaAreas)) {
				cinemaAreas = cinemaService.getCinemaAreasByCityId(cityId);
				memCacheService.set("CinemaAreas_" + cityId, cinemaAreas);
			}

			//影院列表
			List<CinemaInfo> cinemaList = cinemaService.queryCinema(cityId, areaId);

			GoodsCatlog goodsCatlog = new GoodsCatlog();
			//只显示现金券
			goodsCatlog.setCashSelected(cashOnly);
			//是否查最新
			goodsCatlog.setIsNew("new".equals(scope));
			//排序方式
			String[] sort_rule = sort.split("_");
			String sort_term = "default";
			String sort_order = "";
			if (sort_rule.length == 2 && (sort_rule[1].equals("asc") || sort_rule[1].equals("desc"))) {
				sort_term = sort_rule[0];
				sort_order = sort_rule[1];
				if ("publishTime".equals(sort_term)) {
					//发布时间
					goodsCatlog.setOrderbydate(sort_order);
				} else if ("salesCount".equals(sort_term)) {
					//销量
					goodsCatlog.setOrderbysort(sort_order);
				} else if ("price".equals(sort_term)) {
					//价格
					goodsCatlog.setOrderbyprice(sort_order);
				} else if ("review".equals(sort_term)) {
					//好评率
					goodsCatlog.setOrderbyrating(sort_order);
				} else if ("rebate".equals(sort_term)) {
					//折扣
					goodsCatlog.setOrderbydiscount(sort_order);
				}
			} else {
				//默认排序
				goodsCatlog.setOrderbydefault("asc");
			}

			goodsCatlog.setCityid(cityId);

			//电影列表
			List<Long> filmGoodsIdList = (List<Long>) memCacheService.get("FilmGoodsIdList_" + cityId + "_" + areaId + "_" + cinemaId);
			if (CollectionUtils.isEmpty(filmGoodsIdList)) {
				filmGoodsIdList = filmGrouponService.querysFilmIds(cityId, areaId, cinemaId);
				memCacheService.set("FilmGoodsIdList_" + cityId + "_" + areaId + "_" + cinemaId, filmGoodsIdList);
			}

//			Pager pager = (Pager) memCacheService.get("FilmGrouponListPager_" + cityId + "_" + areaId + "_" + cinemaId + "_" + scope + "_" + sort);
//			if (pager == null) {
//				pager = PagerHelper.getPager(page, goodsCatlogService.getCatlogCount(filmGoodsIdList, goodsCatlog), 36);
//				memCacheService.set("FilmGrouponListPager_" + cityId + "_" + areaId + "_" + cinemaId + "_" + scope + "_" + sort, pager);
//			}

			Pager pager = PagerHelper.getPager(page, goodsCatlogService.getCatlogCount(filmGoodsIdList, goodsCatlog), 36);

			searchStrategy.setService(request, "goodsCatlogService");
			List<Long> resultGoodsIdList = searchStrategy.getCatlog(filmGoodsIdList, goodsCatlog, pager);
			List<GoodsForm> filmList = goodsCatlogService.getGoodsFormFromId(resultGoodsIdList);
			
			request.setAttribute("cinemaAreas", cinemaAreas);
			request.setAttribute("cinemaList", cinemaList);
			request.setAttribute("filmList", filmList);
			request.setAttribute("pager", pager);

			//请求参数
			request.setAttribute("areaId", areaId);
			request.setAttribute("cinemaId", cinemaId);
			request.setAttribute("scope", scope);
			request.setAttribute("sort", sort);
			request.setAttribute("crnt_sort_term", sort_term);
			request.setAttribute("crnt_sort_order", sort_order);
			request.setAttribute("cashOnly", cashOnly);
			request.setAttribute("page", page);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("/404.html");
			return null;
		}
		return "film/film_groupon";
	}
}
