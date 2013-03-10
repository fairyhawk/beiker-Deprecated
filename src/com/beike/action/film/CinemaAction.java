package com.beike.action.film;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.CinemaInfo;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.form.CinemaDetailForm;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.film.CinemaService;
import com.beike.service.film.FilmService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.hao3604j.org.json.JSONArray;
import com.beike.util.ipparser.CityUtils;

/**
 * 影院action
 * @author weiwei
 */
@Controller
@SuppressWarnings("unchecked")
public class CinemaAction extends BaseUserAction {

	@Autowired
	private CinemaService cinemaService;

	@Autowired
	private FilmService filmService;

	private MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	//影院列表
	@RequestMapping("/cinema/cinemaList.do")
	public String cinemaList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Long cityId = CityUtils.getCityId(request, response); //城市ID
			Long areaId; //区县ID
			try {
				areaId = Long.valueOf(request.getParameter("area"));
			} catch (Exception e) {
				areaId = null;
			}

			Integer page = NumberUtils.toInt(request.getParameter("page"), 1);

			//影院区域列表
			List<RegionCatlog> cinemaAreas;
			cinemaAreas = (List<RegionCatlog>) memCacheService.get("CinemaAreas_" + cityId);
			if (CollectionUtils.isEmpty(cinemaAreas)) {
				cinemaAreas = cinemaService.getCinemaAreasByCityId(cityId);
				memCacheService.set("CinemaAreas_" + cityId, cinemaAreas);
			}

			//影院列表
			Pager pager = PagerHelper.getPager(page, cinemaService.queryCinemaCount(cityId, areaId), 20);
			List<CinemaInfo> cinemaList = cinemaService.queryCinema(pager, cityId, areaId);

			//热映影片
			List<FilmRelease> hotFilmList = filmService.getFilmRelease(cityId, 10);

			//团购排行(10206L:电影类团购)
			List<GoodsForm> hotFilmGroupon = filmService.getTuanGouFilmRank(10206L, cityId, 2);

			request.setAttribute("cinemaAreas", cinemaAreas);
			request.setAttribute("cinemaList", cinemaList);
			request.setAttribute("pager", pager);
			request.setAttribute("areaId", areaId);
			request.setAttribute("hotFilmList", hotFilmList);
			request.setAttribute("hotFilmGrouponList", hotFilmGroupon);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
		return "cinema/cinema_list";
	}

	//影院详情
	@RequestMapping("/cinema/cinemaDetail.do")
	public String cinemaDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Long cityId = CityUtils.getCityId(request, response); //城市ID
			Long cinemaId = Long.valueOf(request.getParameter("cinemaId"));
			Integer filmListPage = NumberUtils.toInt(request.getParameter("filmPage"), 1);

			//影院详情
			CinemaDetailForm cinemaDetail = cinemaService.queryCinemaDetail(cinemaId);
			if (cinemaDetail == null) {
				return "redirect:../404.html";
			}

			//影院正在放映的影片
			Long wpwCinemaId = cinemaDetail.getCinemaInfo().getWpwCinemaId();
			Pager filmListPager = PagerHelper.getPager(filmListPage, cinemaService.queryFilmReleaseCountByCinema(cityId, wpwCinemaId), 10);
			List<FilmRelease> filmsList;
			//如果没有找到正在放映的影片,并且是千品网的影院,则查找所有影片
			if (filmListPager.getTotalRows() == 0 && cinemaService.queryIsQianpinCinema(cinemaId)) {
				filmListPager = PagerHelper.getPager(filmListPage, cinemaService.queryFilmReleaseCountByCityId(cityId), 10);
				filmsList = cinemaService.queryFilmReleaseCityId(filmListPager, cityId);
			} else {
				filmsList = cinemaService.queryFilmReleaseByCinema(filmListPager, cityId, wpwCinemaId);
			}
			
			//遍历查出影片在线订座最低价
			for (FilmRelease filmRelease : filmsList) {
				BigDecimal lowestPriceByFilm = filmService.getLowestPriceByFilm(cityId, wpwCinemaId, filmRelease.getFilmId());
				filmRelease.setLowestOLBookingPrice(lowestPriceByFilm);
			}
			

			//热映影片
			List<FilmRelease> hotFilmList = filmService.getFilmRelease(cityId, 10);

			//团购排行(10206L:电影类团购)
			List<GoodsForm> hotFilmGroupon = filmService.getTuanGouFilmRank(10206L, cityId, 2);

			request.setAttribute("cinemaDetail", cinemaDetail);
			request.setAttribute("filmsList", filmsList);
			request.setAttribute("filmListPager", filmListPager);

			request.setAttribute("hotFilmList", hotFilmList);
			request.setAttribute("hotFilmGrouponList", hotFilmGroupon);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
		return "cinema/cinema_detail";
	}

	//影院下某影片放映计划
	@RequestMapping("/cinema/cinemaShowPlain.do")
	public String cinemaFilwShowPlain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Long cinemaId = Long.valueOf(request.getParameter("cinemaId"));
			Long filmId = Long.valueOf(request.getParameter("filmId"));

			List<FilmShow> filmShowPlainsList = cinemaService.queryFilmShowPlainByCinema(cinemaId, filmId);

			List<Date> showPlainDateList = new ArrayList<Date>();

			for (FilmShow filmShow : filmShowPlainsList) {
				boolean isExist = false;
				Date showTime = filmShow.getShowTime();
				for (Date date : showPlainDateList) {
					if (showTime.getYear() == date.getYear() && showTime.getMonth() == date.getMonth() && showTime.getDate() == date.getDate()) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					showPlainDateList.add(showTime);
				}
			}
			//放映时间列表排序
			Collections.sort(showPlainDateList, new Comparator<Date>() {
				@Override
				public int compare(Date o1, Date o2) {
					return o1.compareTo(o2);
				}
			});

			//放映计划列表按时间排序
			Collections.sort(filmShowPlainsList, new Comparator<FilmShow>() {
				@Override
				public int compare(FilmShow o1, FilmShow o2) {
					return o1.getShowTime().compareTo(o2.getShowTime());
				}
			});

			Map<Date, List<FilmShow>> filmShowPlainMap = new LinkedHashMap<Date, List<FilmShow>>();
			
		
			
			for (FilmShow filmShow : filmShowPlainsList) {
				Date showTime = filmShow.getShowTime();
				Date showDateFlag = new Date(showTime.getYear(),showTime.getMonth(),showTime.getDate());
				List<FilmShow> filmPlains = filmShowPlainMap.get(showDateFlag);
				if (CollectionUtils.isEmpty(filmPlains)) {
					filmPlains =  new ArrayList<FilmShow>();
					filmShowPlainMap.put(showDateFlag, filmPlains);
				}
				filmPlains.add(filmShow);
			}
			
			
			
			

			request.setAttribute("filmId", filmId);
			request.setAttribute("filmShowPlainMap", filmShowPlainMap);
			request.setAttribute("now", new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
		return "cinema/cinema_film_showplain";
	}

	//影院详情
	@RequestMapping("/cinema/cinemaDetailJson.do")
	public String cinemaDetailJsonData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			Long cinemaId = Long.valueOf(request.getParameter("cinemaId"));

			//影院详情
			CinemaDetailForm cinemaDetail = cinemaService.queryCinemaDetail(cinemaId);
			CinemaInfo cinemaInfo = cinemaDetail.getCinemaInfo();

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "", "" });

			JSONArray jsonArray = new JSONArray();

			JSONObject baseInfoJson = new JSONObject();
			HashMap<String, String> baseInfoMap = new HashMap<String, String>();
			baseInfoMap.put("is_support_takeaway", "0");
			baseInfoMap.put("rate", "0");
			baseInfoMap.put("tel", cinemaInfo.getTel());
			baseInfoMap.put("is_support_online_meal", "0");
			baseInfoMap.put("merchantName", cinemaInfo.getName());
			baseInfoMap.put("buinesstime", "");
			baseInfoMap.put("addr", cinemaInfo.getAddress());
			baseInfoMap.put("merchantId", "0");
			baseInfoMap.put("latitude", cinemaInfo.getCoord().split(",")[0] + "-" + cinemaInfo.getCoord().split(",")[1]);
			baseInfoMap.put("city", "");
			//author wenjie.mai 地图显示特色服务 2013.02.26
			baseInfoMap.put("environment", "");
			baseInfoMap.put("capacity", "");
			baseInfoMap.put("otherservice", "");
			//地图显示特色服务  The End
			baseInfoJson.accumulateAll(baseInfoMap);

			HashMap<String, String> pageInfoMap = new HashMap<String, String>();
			pageInfoMap.put("totalsize", "1");
			pageInfoMap.put("currentPage", "1");
			pageInfoMap.put("totalPage", "1");

			jsonArray.put(baseInfoJson);
			jsonArray.put(pageInfoMap);

			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
