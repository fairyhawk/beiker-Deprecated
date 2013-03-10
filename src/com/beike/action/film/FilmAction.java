package com.beike.action.film;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.common.search.SearchStrategy;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.entity.film.FilmSort;
import com.beike.form.CinemaInfoForm;
import com.beike.form.FilmReleaseForm;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.film.FilmGrouponService;
import com.beike.service.film.FilmService;
import com.beike.util.BeanUtils;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**  
* @Title:  影片Action
* @Package com.beike.action.film
* @Description: 
* @author wenjie.mai  
* @date 2012-11-28 下午3:36:48
* @version V1.0  
*/
@Controller
public class FilmAction extends BaseUserAction {

	private final SearchStrategy searchStrategy = new SearchStrategy();
	
	@Autowired
	private FilmService filmService;
	
	@Autowired
	private FilmGrouponService filmGrouponService;
	
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private static Log log = LogFactory.getLog(FilmAction.class);
	
	/**
	 * 
	 * @Title: 看电影频道
	 * @Description: 
	 * @param 
	 * @return Object
	 * @author wenjie.mai
	 */
	@RequestMapping("/film/toshowFilmPage.do")
	public Object getFilmPage(HttpServletRequest request,HttpServletResponse response){
		
		try{
			String city = CityUtils.getCity(request, response);
			Long cityId = null;
			
			Map<String, Long> mapCity = (Map<String,Long>) memCacheService.get("CITY_CATLOG");
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}
			
			if (city == null || "".equals(city)) {
				city = "beijing";
			}
			
			if (mapCity != null) {
				 cityId  = mapCity.get(city.trim());
			}
			
			//1.查询上映影片
			List<FilmRelease> filmlist  = filmService.getFilmReleaseByGrade(cityId,10);
			
			//2.热门影院
			List<CinemaInfoForm> cinemalist = filmService.getPopularCinema(cityId);
			
			List<CinemaInfoForm> clistTwo = null;
			
			if(cinemalist != null && cinemalist.size() >6){
				clistTwo = cinemalist.subList(6,cinemalist.size());
				cinemalist = cinemalist.subList(0,6);
			}
			
			//3.正在团购、显示团购列表页前9个
			GoodsCatlog goodsCatlog = new GoodsCatlog();
			goodsCatlog.setOrderbydefault("asc");
			goodsCatlog.setCityid(cityId);
			goodsCatlog.setCashSelected(false);
			goodsCatlog.setIsNew(false);
			
			Long areaId = null;
			Long cinemaId = null;
			
			List<Long> filmGoodsIdList = (List<Long>) memCacheService.get("FilmGoodsIdList_" + cityId + "_" + areaId + "_" + cinemaId);
			if(filmGoodsIdList == null || filmGoodsIdList.size() == 0){
			    filmGoodsIdList = filmGrouponService.querysFilmIds(cityId, areaId, cinemaId);
				memCacheService.set("FilmGoodsIdList_" + cityId + "_" + areaId + "_" + cinemaId, filmGoodsIdList);
			}
			
			searchStrategy.setService(request, "goodsCatlogService");
			Pager pager = PagerHelper.getPager(1, goodsCatlogService.getCatlogCount(filmGoodsIdList, goodsCatlog), 9);
			List<Long> resultGoodsIdList = searchStrategy.getCatlog(filmGoodsIdList,goodsCatlog,pager);
			List<GoodsForm> tuanGoodsList = goodsCatlogService.getGoodsFormFromId(resultGoodsIdList);
			
			
			request.setAttribute("filmlist", filmlist);
			request.setAttribute("cinemalist", cinemalist);
			request.setAttribute("clistTwo", clistTwo);
			request.setAttribute("tuanGoodsList", tuanGoodsList);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:../500.html");
		}
		
		return "/film/filmIndex";
	}
	
	
	/**
	 * 
	 * @Title: 影片列表
	 * @Description: 
	 * @param 
	 * @return Object
	 * @author wenjie.mai
	 */
	@RequestMapping("/film/toshowFilmList.do")
	public Object getFilmList(HttpServletRequest request,HttpServletResponse response){
		
		try{
			String filmpy      =  request.getParameter("filmpy");
			String currentPage =  request.getParameter("cpage");
			String city = CityUtils.getCity(request, response);
			Long cityId = null;
			
			Map<String, Long> mapCity = (Map<String,Long>) memCacheService.get("CITY_CATLOG");
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}
			
			if (city == null || "".equals(city)) {
				city = "beijing";
			}
			
			if (mapCity != null) {
				 cityId  = mapCity.get(city.trim());
			}
			int    totalCount  = 0;

			if (!StringUtils.validNull(currentPage)) {
				currentPage = "1";
			}
			
			//1.查看影片类型
			List<FilmSort> sortlist = filmService.getFilmSort();
			
			List<Long> filmidlist = filmService.getFilmReleaseInfo(filmpy,cityId);
			
			totalCount = filmService.getFilmIdCount(filmidlist);
			
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),totalCount,20);
			
			//2.影片信息
			List<FilmReleaseForm> releaselist = filmService.getFilmInfo(filmidlist, pager);
			
			//3.热门排行榜
			List<FilmRelease> filmlist  = filmService.getFilmRelease(cityId,10);
			
			//4.团购排行
			List<GoodsForm> goodlist  = filmService.getTuanGouFilmRank(10206L,cityId,2);
			
			request.setAttribute("cpage", currentPage);
			request.setAttribute("pager", pager);
			request.setAttribute("sortlist", sortlist);
			request.setAttribute("releaselist", releaselist);
			request.setAttribute("filmlist", filmlist);
			request.setAttribute("goodlist", goodlist);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:../500.html");
		}
		
		return "/film/filmList";
	}
	
	
	/**
	 * 
	 * @Title: 影片详情
	 * @Description: 
	 * @param 
	 * @return Object
	 * @author wenjie.mai
	 */
	@SuppressWarnings({ "unchecked"})
	@RequestMapping("/film/toshowFilmDetail.do")
	public Object getFilmDetail(HttpServletRequest request,HttpServletResponse response){
		
		
		try{
			String filmId = request.getParameter("filmid");
			Long   fid    = Long.valueOf(filmId); 
			String city = CityUtils.getCity(request, response);
			Long cityId = null;
			FilmReleaseForm  form = null;
			
			Map<String, Long> mapCity = (Map<String,Long>) memCacheService.get("CITY_CATLOG");
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}
			
			if (city == null || "".equals(city)) {
				city = "beijing";
			}
			
			if (mapCity != null) {
				 cityId  = mapCity.get(city.trim());
			}
			
			//观影日期
			String s1 = DateUtils.getNowTime();
			String s3 = DateUtils.getNextDay(s1,"2");
			
			Date d1 = DateUtils.parseToDate(s1,"yyyy-MM-dd");
			Date d3 = DateUtils.parseToDate(s3,"yyyy-MM-dd");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			String startTime = format.format(d1)+" 00:00:00";
			String endTime   = format.format(d3)+" 23:59:59";
			
			//1.影片详情
			List<FilmReleaseForm> filmDetail = filmService.getFilmInfoById(fid);
			
			if(filmDetail != null && filmDetail.size() > 0)
			      form = filmDetail.get(0);
			
			//网票网影院ID
			List<Long> cidlist = (List<Long>) memCacheService.get(cityId+"_"+fid+"_0_0_FILM");
			
			if(cidlist == null || cidlist.size() ==0 ){
				cidlist = filmService.getCinemaIdByFilmId(fid,startTime,endTime);
				memCacheService.set(cityId+"_"+fid+"_0_0_FILM",cidlist ,60*60);
			}
			
			//全城热映
			List<FilmRelease> filmlist  = filmService.getFilmRelease(cityId,10);
			
			//推荐团购
			List<GoodsForm> goodlist  = filmService.getTuanGouFilmRank(10206L,cityId,2);
			
			request.setAttribute("filmDetailForm", form);
			request.setAttribute("filmlist", filmlist);
			request.setAttribute("goodlist", goodlist);
			request.setAttribute("filmid", fid);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:../500.html");
		}
		
		return "/film/filmDetail";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/film/getCinemaList.do")
	public Object getCinemaList(HttpServletRequest request,HttpServletResponse response) throws ParseException{
		
		String day = request.getParameter("day");
		String reiginId = request.getParameter("regionid");
		String filmId = request.getParameter("filmid");
		Long fid = Long.valueOf(filmId);
		String city = CityUtils.getCity(request, response);
		Long cityId = null;
		
		Map<String, Long> mapCity = (Map<String,Long>) memCacheService.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set("CITY_CATLOG", mapCity);
		}
		
		if (city == null || "".equals(city)) {
			city = "beijing";
		}
		
		if (mapCity != null) {
			 cityId  = mapCity.get(city.trim());
		}
		
		String currentPage =  request.getParameter("cpage");

		if (!StringUtils.validNull(currentPage)) {
			currentPage = "1";
		}
		
		String startTime = "";
		String endTime   = "";
		String nowDay = "";
		
		String s1 = DateUtils.getNowTime();
		String s2 = DateUtils.getNextDay(s1,"1");
		String s3 = DateUtils.getNextDay(s1,"2");
		
		Date d1 = DateUtils.parseToDate(s1,"yyyy-MM-dd");
		Date d2 = DateUtils.parseToDate(s2,"yyyy-MM-dd");
		Date d3 = DateUtils.parseToDate(s3,"yyyy-MM-dd");
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		String monthDay1 = formatter.format(d1);
		String monthDay2 = formatter.format(d2);
		String monthDay3 = formatter.format(d3);
		
		String formatDay1 = format.format(d1);
		String formatDay2 = format.format(d2);
		String formatDay3 = format.format(d3);
		
		if(org.apache.commons.lang.StringUtils.isBlank(day)){
			
			startTime = format.format(d1) + " 00:00:00";
			endTime   = format.format(d3) + " 23:59:59";
			day = "0";
		}else{
			
			if(day.equals("0")){ //全部：查询近3天数据
				startTime = format.format(d1) + " 00:00:00";
				endTime   = format.format(d3) + " 23:59:59";
			}else{
				if(day.equals("1")){
					nowDay = formatDay1;
				}else if(day.equals("2")){
					nowDay = formatDay2;
				}else if(day.equals("3")){
					nowDay = formatDay3;
				}else{
					nowDay = formatDay1;
				}
				startTime = nowDay + " 00:00:00";
				endTime   = nowDay + " 23:59:59";
			}
		}
		
		Long rid = null;
		
		if(org.apache.commons.lang.StringUtils.isNotBlank(reiginId)){
			rid = Long.valueOf(reiginId);
		}
		
		//网票网影院ID 
		List<Long> cidlist = (List<Long>) memCacheService.get(cityId+"_"+fid+"_0_0_FILM");
		
		if(cidlist == null || cidlist.size() ==0 ){
			String time1 = format.format(d1) + " 00:00:00";
			String time2 = format.format(d3) + " 23:59:59";
			cidlist = filmService.getCinemaIdByFilmId(fid,time1,time2);
			memCacheService.set(cityId+"_"+fid+"_0_0_FILM",cidlist ,60*60);
		}
		
		List<RegionCatlog> rlist = null;
		if(cidlist != null && cidlist.size() > 0){
			//观影区县
			rlist = filmService.getFilmRegion(cityId,cidlist);
		}
		
		List<Long> idlist = (List<Long>) memCacheService.get(cityId+"_"+fid+"_"+day+"_"+rid+"_FILM");
		
		if(idlist == null || idlist.size() ==0){
			idlist = filmService.getCinemaIdByFilmId(fid,startTime,endTime);
			memCacheService.set(cityId+"_"+fid+"_"+day+"_"+rid+"_FILM",idlist,60*60);
		}
		
		int totalCount = 0;
		Pager pager = null;
		List<CinemaInfoForm> listform = null;
		
		if(idlist != null && idlist.size() > 0){
			totalCount = filmService.getCinemaListCount(idlist,cityId,rid,fid);
			if(totalCount > 0){
				pager = PagerHelper.getPager(Integer.parseInt(currentPage),totalCount,10);
				listform = filmService.getCinemaList(idlist,pager,cityId,fid,rid);
			}
		}
		
		request.setAttribute("day", day);
		request.setAttribute("monthDay1", monthDay1);
		request.setAttribute("monthDay2", monthDay2);
		request.setAttribute("monthDay3", monthDay3);
		request.setAttribute("rlist", rlist);
		request.setAttribute("listform", listform);
		request.setAttribute("cpage", currentPage);
		request.setAttribute("pager", pager);
		request.setAttribute("reiginId", reiginId);
		request.setAttribute("filmid", fid);
		
		
		return "/film/bottomCinemaList";
	}
	
	@RequestMapping("/film/getFilmShowList.do")
	public String getFilmShowList(HttpServletRequest request,HttpServletResponse response) throws ParseException{
		
		
		String filmId = request.getParameter("filmid");
		String cinemaId = request.getParameter("cinemaId");
		
		String monthDay1 = "";
		String monthDay2 = "";
		String monthDay3 = "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
		String s1 = DateUtils.getNowTime();
		String s2 = DateUtils.getNextDay(s1,"1");
		String s3 = DateUtils.getNextDay(s1,"2");
		
		Date d1 = DateUtils.parseToDate(s1,"yyyy-MM-dd");
		Date d2 = DateUtils.parseToDate(s2,"yyyy-MM-dd");
		Date d3 = DateUtils.parseToDate(s3,"yyyy-MM-dd");
		
		monthDay1 = formatter.format(d1);
		monthDay2 = formatter.format(d2);
		monthDay3 = formatter.format(d3);
		
		String dy1 = format.format(d1);
		String dy2 = format.format(d2);
		String dy3 = format.format(d3);
				
		List<FilmShow> showlist1 = filmService.getFilmShowlist(Long.valueOf(filmId),Long.valueOf(cinemaId),monthDay1);
		List<FilmShow> showlist2 = filmService.getFilmShowlist(Long.valueOf(filmId),Long.valueOf(cinemaId),monthDay2);
		List<FilmShow> showlist3 = filmService.getFilmShowlist(Long.valueOf(filmId),Long.valueOf(cinemaId),monthDay3);
		
		
		String flag1 = "1";
		String flag2 = "1";
		String flag3 = "1";
		
		if(showlist1 == null || showlist1.size() == 0)
			flag1 = "0";
		
		if(showlist2 == null || showlist2.size() == 0)
			flag2 = "0";
		
		if(showlist3 == null || showlist3.size() == 0)
			flag3 = "0";
		
		request.setAttribute("flag1",flag1);
		request.setAttribute("flag2",flag2);
		request.setAttribute("flag3",flag3);
		request.setAttribute("showlist1", showlist1);
		request.setAttribute("showlist2", showlist2);
		request.setAttribute("showlist3", showlist3);
		request.setAttribute("dy1", dy1);
		request.setAttribute("dy2", dy2);
		request.setAttribute("dy3", dy3);
		
		return "/film/filmShow";
	}
}
