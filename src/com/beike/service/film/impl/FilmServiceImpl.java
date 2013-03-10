package com.beike.service.film.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.beike.dao.film.FilmDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.entity.film.FilmSort;
import com.beike.form.CinemaInfoForm;
import com.beike.form.FilmReleaseForm;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.service.film.FilmService;
import com.beike.service.goods.GoodsService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**  
* @Title:  影院Service实现
* @Package com.beike.service.film.impl
* @Description: 
* @author wenjie.mai  
* @date 2012-11-28 上午10:42:29
* @version V1.0  
*/
@Service("filmService")
public class FilmServiceImpl implements FilmService {

	@Autowired
	private FilmDao  filmDao;
	
	@Autowired
	private GoodsService goodsService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<FilmRelease> getFilmRelease(Long cityId,int num) {
		
		List<FilmRelease> resultFilm = (List<FilmRelease>) memCacheService.get(cityId+"_POPOLAR_FILM");
		
		if(resultFilm != null && resultFilm.size() > 0 )
			return resultFilm;
		
		List filmlist = filmDao.getPopularFilmRank(num);
		
		if(filmlist == null || filmlist.size() ==0)
			return null;
		
		List<FilmRelease> relist = new ArrayList<FilmRelease>();
		
		for(Object obj:filmlist){
			Map map = (Map) obj;
			String smallphoto = (String) map.get("small_photo");
			Long   filmId     = (Long) map.get("film_id");
			String filmName   =  (String) map.get("film_name");
            
			FilmRelease film  = new FilmRelease();
			film.setFilmId(filmId);
			film.setFilmName(filmName);
			film.setSmallPhoto(smallphoto);
			
			relist.add(film);
		}
		
		memCacheService.set(cityId+"_POPOLAR_FILM",relist,60*60);
		
		return relist;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<CinemaInfoForm> getPopularCinema(Long cityId) {
		
		List<CinemaInfoForm> resutlist = (List<CinemaInfoForm>) memCacheService.get(cityId+"_POPULAR_CINEMA");
		
		if(resutlist != null && resutlist.size() >0)
			return resutlist;
		
		Map<String,Object> rMap = getCinemaInfoIds(cityId,"0",null);
		
		if(rMap == null || rMap.size() ==0)//没有有效影院
			  return null;
		
		List<Long> cidlist = (List<Long>) rMap.get("0");
		List<CinemaInfoForm> listform = (List<CinemaInfoForm>) rMap.get("1");
		
		if(cidlist == null || listform == null || cidlist.size() == 0  || listform.size() == 0 )
				return null;
		
		Collections.sort(listform);
		if(listform.size() >= 8){
			listform = listform.subList(0,8);
			return listform;
		}else{
			int num = 8 - listform.size();
			
			Map<String, Object> otherMap = getCinemaInfoIds(cityId,"1",cidlist);
			if(otherMap == null || otherMap.size() == 0)
				return listform;
			
			List<CinemaInfoForm> otherForm = (List<CinemaInfoForm>) otherMap.get("1");
			if(otherForm == null || otherForm.size() == 0)
				return listform;
			
			Collections.sort(otherForm);
			
			if(otherForm.size() > num)
				otherForm = otherForm.subList(0,num);
			
			for(int i=0;i<otherForm.size();i++){
				CinemaInfoForm form = otherForm.get(i);
				listform.add(form);
			}
			
			if(listform.size()<8){//补充
				num = 8 - listform.size();
				List<Long> idli = (List<Long>) otherMap.get("0");
				if(idli != null && idli.size() > 0){
					cidlist.addAll(idli);
				}
				List lx = filmDao.getCinemaInfoIdByCity(cityId,"1",cidlist);
				if(lx != null && lx.size() >0){
					List<Long> idlink = new LinkedList<Long>();
					for(Object obj:lx){
						Map map = (Map) obj;
						Long cid = (Long) map.get("cinema_id");
						idlink.add(cid);
					}
					List flist = filmDao.getCinemaInfoList(cityId,idlink,num);
					for(Object obj:flist){
						Map map = (Map) obj;
						Long    cineid = (Long) map.get("cinema_id");
						BigDecimal scount = (BigDecimal) map.get("totalcount");
						String  name   = (String) map.get("name");
						String address = (String) map.get("address");
						String coord   = (String) map.get("coord");
						String photo   = (String) map.get("photo");

						CinemaInfoForm form = new CinemaInfoForm();
						
						form.setCinemaId(cineid);
						
						if(scount == null){
							form.setSalecount(0);
						}else{
							form.setSalecount(Integer.valueOf(String.valueOf(scount)));
						}
						form.setName(name);
						form.setAddress(address);
						form.setCoord(coord);
						form.setPhoto(photo);
						listform.add(form);
					}
				}
			}
		}
		
		memCacheService.set(cityId+"_POPULAR_CINEMA",listform,60*60);
		
		return listform;
	}
	
	@SuppressWarnings("rawtypes")
	public List<CinemaInfoForm> getCinemaForm(List<Long> clist){
		if(clist == null || clist.size() ==0)
			return null;
		List<CinemaInfoForm> rlist = new LinkedList<CinemaInfoForm>();
		for(Object obj:clist){
			Map map = (Map) obj;
			Long    cineid = (Long) map.get("cinema_id");
			String  name   = (String) map.get("name");
			String address = (String) map.get("address");
			String coord   = (String) map.get("coord");
			String photo   = (String) map.get("photo");

			CinemaInfoForm form = new CinemaInfoForm();
			
			form.setCinemaId(cineid);
			form.setName(name);
			form.setAddress(address);
			form.setCoord(coord);
			form.setPhoto(photo);
			rlist.add(form);
		}
		return rlist;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String,Object> getCinemaInfoIds(Long cityId,String flag,List<Long> idlist){
		
		//查询影院ID(千品影院与网票网同时存在的影院ID)
		List li = filmDao.getCinemaInfoIdByCity(cityId,flag,idlist);
				
	    if(li == null || li.size() ==0){
	    	li = filmDao.getCinemaInfoIdByCity(cityId,"1",idlist);//查询所有影院
	    	if(li == null || li.size() == 0)
		    	return null;//没有有效的影院ID
	    }
				
		List<Long> cidList = new LinkedList<Long>();
		List<CinemaInfoForm> goodformlist = new LinkedList<CinemaInfoForm>();
		List<Long> idList = new LinkedList<Long>();
		Map<String,Object> rMap = new HashMap<String,Object>();
		
		for(Object obj:li){//千品网影院ID
			Map map = (Map) obj;
			Long cid = (Long) map.get("cinema_id");
			cidList.add(cid);
		}
		
		//查询该城市下影院对应所有有效影片
		List filmlist = filmDao.getGoodsIdByCityId(cityId,cidList);
		
		if(filmlist == null || filmlist.size() ==0){//没有团购相关信息
			filmlist = filmDao.getCinemaInfoList(cityId,cidList,8);
			if(filmlist == null || filmlist.size() == 0)
				return null;
		}
		
		for(Object obj:filmlist){
			Map map = (Map) obj;
			Long    cineid = (Long) map.get("cinema_id");
			BigDecimal scount = (BigDecimal) map.get("totalcount");
			String  name   = (String) map.get("name");
			String address = (String) map.get("address");
			String coord   = (String) map.get("coord");
			String photo   = (String) map.get("photo");

			idList.add(cineid);

			CinemaInfoForm form = new CinemaInfoForm();
			
			form.setCinemaId(cineid);
			
			if(scount == null){
				form.setSalecount(0);
			}else{
				form.setSalecount(Integer.valueOf(String.valueOf(scount)));
			}
			form.setName(name);
			form.setAddress(address);
			form.setCoord(coord);
			form.setPhoto(photo);
			goodformlist.add(form);
		}
		
		rMap.put("0", idList);
		rMap.put("1", goodformlist);
		
		return rMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<FilmSort> getFilmSort() {
		
		List li = filmDao.getFilmType();
		if(li == null || li.size() ==0)
			return null;
		
		List<FilmSort> sortlist = new ArrayList<FilmSort>();
		for(Object obj:li){
			Map map = (Map) obj;
			String sort = (String) map.get("film_sort");
			String flpy = (String) map.get("film_py");
			FilmSort filmSort = new FilmSort();
			filmSort.setFilmSort(sort);
			filmSort.setFilmPy(flpy);
			sortlist.add(filmSort);
		}
		return sortlist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Long> getFilmReleaseInfo(String filmpy,Long cityId) {
		
		List<Long> flist = (List<Long>) memCacheService.get(cityId+"_"+filmpy+"_FILM_STYPE_LIST");
		
		if(flist != null && flist.size() > 0)
			return flist;
		
		//1.通过影片类型查询影片ID
		List filmlist = filmDao.getFilmId(filmpy);
		if(filmlist == null || filmlist.size() ==0)
			return null;
		
		List<Long> filmidlist = new LinkedList<Long>();
		for(Object obj:filmlist){
			Map map = (Map) obj;
			Long filmid = (Long) map.get("film_id");
			filmidlist.add(filmid);
		}
		
		memCacheService.set(cityId+"_"+filmpy+"_FILM_STYPE_LIST",filmidlist,60*60);
		
		return filmidlist;
	}

	@Override
	public int getFilmIdCount(List<Long> filmidlist) {
		
		int idCount = filmDao.getFilmInfoCount(filmidlist);
		return idCount;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<FilmReleaseForm> getFilmInfo(List<Long> filmids, Pager pager) {
		
		int start = pager.getStartRow();
		int end   = pager.getPageSize();
		List filmlist = filmDao.getFilmInfo(filmids, start, end);
		
		if(filmlist == null || filmlist.size() ==0)
			return null;
		
		//查询影片播放语言
		List<Long> filmid = new LinkedList<Long>();
		for(Object ojx:filmlist){
			Map  map    = (Map) ojx;
			Long filmId = (Long) map.get("film_id");
			filmid.add(filmId);
		}
		
		List languagelist = filmDao.getFilmLanguage(filmid);
		
		List<FilmReleaseForm> releaselist =  zuZhuangReleaseForm(filmlist,languagelist);
		
		return releaselist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<GoodsForm> getTuanGouFilmRank(Long tagextid, Long cityId, int num) {
		
		List<GoodsForm> rlist = (List<GoodsForm>) memCacheService.get(cityId+"_TUAN_GOODS");
		
		if(rlist != null && rlist.size() > 0)
		      return rlist;
		
		List tuanlist = filmDao.getTuanGouFilmRank(tagextid, cityId, num);
		
		if(tuanlist == null || tuanlist.size() ==0)
			return null;
		
		List<Long> idlist = new LinkedList<Long>();
		
		for(Object obj:tuanlist){
			Map map = (Map) obj;
			Long goodid = (Long) map.get("goodsid");
			idlist.add(goodid);
		}
		
		List<GoodsForm> goodlistForm = goodsService.getGoodsFormByChildId(idlist);
		
		if(goodlistForm == null || goodlistForm.size() == 0)
			   return null;
		
		memCacheService.set(cityId+"_TUAN_GOODS",goodlistForm,60*60);
		
		return goodlistForm;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<FilmReleaseForm> getFilmInfoById(Long filmId) {
		
		List langlist = filmDao.getFilmLanguageById(filmId);
		List relelist = filmDao.getFilmInfoById(filmId);
		
		if(relelist == null || relelist.size() == 0)
			return null;
		
		List<FilmReleaseForm> releaseDetail =  zuZhuangReleaseForm(relelist,langlist);
		
		return releaseDetail;
	}
	
	@SuppressWarnings("rawtypes")
	public List<FilmReleaseForm> zuZhuangReleaseForm(List releaselist,List languagelist){
		
		List<FilmReleaseForm> resultlist = new LinkedList<FilmReleaseForm>();
		Map<Long,String> languageMap = new LinkedHashMap<Long,String>();//影片ID与语言 one--->more
		
		if(languagelist != null && languagelist.size() >0){
			for(Object ox:languagelist){
				Map ma = (Map) ox;
				Long   fid  = (Long) ma.get("film_id");
				String lang = (String) ma.get("LANGUAGE");
				
				if(fid != null && fid > 0){
					String mapValue = languageMap.get(fid);
					if(StringUtils.isNotBlank(mapValue)){
						lang += mapValue;
					}
					languageMap.put(fid,lang);
				}
			}
		}
		
		//组装影片form
		for(Object obj:releaselist){
			Map map = (Map) obj;
			Long   filmId     = (Long) map.get("film_id");
			String smallPhoto = (String) map.get("small_photo");
			String filmName   = (String) map.get("film_name");
			BigDecimal grade  = (BigDecimal) map.get("grade");
			String director   = (String) map.get("director");
			String starring   = (String) map.get("starring");
			String duration   = (String) map.get("duration");
			String sort       = (String) map.get("sort");
			Date   showdate   = (Date) map.get("show_date");
			String description= (String) map.get("description");
			String msg        = (String) map.get("msg");
			String url        = (String) map.get("url");
					
			FilmReleaseForm form = new FilmReleaseForm();
			form.setFilmId(filmId);
			form.setSmallPhoto(smallPhoto);
			form.setFilmName(filmName);
			form.setGrade(grade);
			form.setDirector(director);
			form.setStarring(starring);
			form.setDuration(duration);
			form.setSort(sort);
			form.setShowDate(showdate);
			form.setDescription(description);
			form.setMsg(msg);
			form.setUrl(url);
					
			String syLanguage = languageMap.get(filmId);
			if(StringUtils.isBlank(syLanguage)){
				form.setSyLanguage("");
			}else{
				form.setSyLanguage(syLanguage);
			}
					
			resultlist.add(form);
		}
		
		return resultlist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Long> getCinemaIdByFilmId(Long filmId, String startTime,String endTime) {
		
		List cinemaIdlist = filmDao.getCinemaIdByFilmId(filmId, startTime, endTime);
		
		if(cinemaIdlist == null || cinemaIdlist.size() ==0)
			return null;
		
		List<Long> clist = new LinkedList<Long>();
		for(Object obj:cinemaIdlist){
			Map map = (Map) obj;
			Long cinemaId = (Long) map.get("cinema_id");
			clist.add(cinemaId);
		}
		
		return clist;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<RegionCatlog> getFilmRegion(Long cityId,List<Long> cidlist) {
		
		List li = filmDao.getRegionByFilmCount(cidlist,cityId);
		
		if(li == null || li.size() == 0)
			return null;
		
		List<RegionCatlog> catloglist = new LinkedList<RegionCatlog>();
		
		for(Object obj:li){
			Map mx  = (Map) obj;
			Long id = (Long) mx.get("dist_id"); 
			String rname = (String) mx.get("region_name");
			
			RegionCatlog catlog = new RegionCatlog();
			catlog.setCatlogid(id);
			catlog.setCatlogName(rname);
			catloglist.add(catlog);
		}
				
	   return catloglist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<CinemaInfoForm> getCinemaInfo(List<Long> cidlist, Pager pager,Long cityId,Long filmId) {
		
		List  cilist = filmDao.getCinemaInfo(cidlist,pager.getStartRow(),pager.getPageSize(),cityId,filmId);
		
		return this.getCinemaInfoList(cilist, cidlist, filmId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<FilmRelease> getFilmReleaseByGrade(Long cityId, int num) {
		
		List<FilmRelease> resultFilm = (List<FilmRelease>) memCacheService.get(cityId+"_POPULAR_OPEN_FILM");
		
		if(resultFilm != null && resultFilm.size() > 0 )
			return resultFilm;
		
		List filmlist = filmDao.getPopularFilmByGrade(num);
		
		if(filmlist == null || filmlist.size() ==0)
			return null;
		
		List<FilmRelease> relist = new ArrayList<FilmRelease>();
		
		for(Object obj:filmlist){
			Map map = (Map) obj;
			String smallphoto = (String) map.get("small_photo");
			Long   filmId     = (Long) map.get("film_id");
			String filmName   =  (String) map.get("film_name");
            
			FilmRelease film  = new FilmRelease();
			film.setFilmId(filmId);
			film.setFilmName(filmName);
			film.setSmallPhoto(smallphoto);
			
			relist.add(film);
		}
		
		memCacheService.set(cityId+"_POPULAR_OPEN_FILM",relist,60*60);
		
		return relist;	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<CinemaInfoForm> getCinemaList(List<Long> cidlist, Pager pager,Long cityId, Long filmId, Long reiginId) {
		
		List  cilist = filmDao.getCinemaList(cidlist,pager.getStartRow(),pager.getPageSize(),cityId,reiginId,filmId);
		
		return this.getCinemaInfoList(cilist,cidlist,filmId);
		
	}
	
	@SuppressWarnings("rawtypes")
	public List<CinemaInfoForm> getCinemaInfoList(List cilist,List<Long> cidlist,Long filmId){
		
		if(cilist == null || cilist.size() == 0)
			return null;
		
		List<CinemaInfoForm> cilistform = new LinkedList<CinemaInfoForm>();
		Map<Long,BigDecimal> cgoodMap   = new LinkedHashMap<Long,BigDecimal>();
		List<Long> cinemaInfoId = new LinkedList<Long>(); //千品网影院ID
		
		for(Object obj:cilist){
			Map map = (Map) obj;
			Long pid = (Long) map.get("pid");
			cinemaInfoId.add(pid);
		}
		
		//查询团购数据
		List tlist = filmDao.getCinemaId(cinemaInfoId);
		
		if(tlist != null && tlist.size() >0){
			for(Object ojx : tlist){
				Map mx = (Map) ojx;
				Long cmid = (Long) mx.get("cinema_id");//千品网影院ID
				BigDecimal cprice =  (BigDecimal) mx.get("currentPrice");
				cgoodMap.put(cmid,cprice);
			}
		}
		
		for(Object obj:cilist){
			Map map = (Map) obj;
			Long cid = (Long) map.get("pid");//千品网影院ID
			String photo = (String) map.get("photo");
			String name  = (String) map.get("name");
			String addres=(String) map.get("address");
			String coord = (String) map.get("coord");
			String tel   = (String) map.get("tel");
			Long type    = (Long) map.get("type");
			Long distid  = (Long) map.get("dist_id");
			BigDecimal vprice = (BigDecimal) map.get("vprice");
			
			CinemaInfoForm form = new CinemaInfoForm();
			form.setCinemaId(cid);
			form.setPhoto(photo);
			form.setName(name);
			form.setAddress(addres);
			form.setCoord(coord);
			form.setTel(tel);
			form.setDistId(distid);
			form.setWpwPrice(vprice);
			
			if(type == null){
				form.setOnlinesit(false);
			}else{
				if(type == 1 || type == 3){
					form.setOnlinesit(true);
				}else{
					form.setOnlinesit(false);
				}
			}
			
			if(cgoodMap.get(cid) != null){
				form.setOnlinetuangou(true);
				form.setTgPrice(cgoodMap.get(cid));
			}else{
				form.setOnlinetuangou(false);
			}
			
			cilistform.add(form);
		}
		
		return cilistform;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<FilmShow> getFilmShowlist(Long filmId, Long cinemaId,String dayTime) {
		
		List idli = filmDao.getCinemaIdByCid(cinemaId);
		
		if(idli == null || idli.size() == 0)
			return null;
		
		Map mx = (Map) idli.get(0);
		Long cid = (Long) mx.get("wid");
		
		String startTime = dayTime + " 00:00:00";
		String endTime   = dayTime + " 23:59:59";
		
		List li = filmDao.getFilmShowlist(filmId, cid, startTime, endTime);
		
		if(li == null || li.size() == 0)
			return null;
		
		List<FilmShow> showlist = new LinkedList<FilmShow>();
		
		for(Object obj:li){
			Map map = (Map) obj;
			Timestamp showTime = (Timestamp) map.get("show_time");
			Long showIndex = (Long) map.get("show_index");
			String language= (String) map.get("LANGUAGE");
			String dimensio= (String) map.get("dimensional");
			String hallname= (String) map.get("hall_name");
			BigDecimal vprice = (BigDecimal) map.get("v_price");
			
			FilmShow show = new FilmShow();
			show.setShowTime(showTime);
			show.setShowIndex(showIndex);
			show.setLanguage(language);
			show.setDimensional(dimensio);
			show.setHallName(hallname);
			show.setvPrice(vprice);
			showlist.add(show);
		}
		return showlist;
	}

	@Override
	public int getCinemaListCount(List<Long> cidlist, Long cityId,Long regionId,Long filmId) {
	
		int num = filmDao.getCinemaListCount(cidlist, cityId, regionId,filmId);
		return num;
	}

	@Override
	public BigDecimal getLowestPriceByFilm(Long cityId, Long cinemaId, Long filmId) {
		return filmDao.getLowestPriceByFilm(cityId, cinemaId, filmId);
	}
	
	
	
}
