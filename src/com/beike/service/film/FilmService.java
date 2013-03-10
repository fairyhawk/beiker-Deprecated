package com.beike.service.film;

import java.math.BigDecimal;
import java.util.List;
import org.htmlparser.lexer.Page;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.entity.film.FilmSort;
import com.beike.form.CinemaInfoForm;
import com.beike.form.FilmReleaseForm;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;

/**  
* @Title:  影院Service
* @Package com.beike.service.film
* @Description: 
* @author wenjie.mai  
* @date 2012-11-28 上午15:41:49
* @version V1.0  
*/
public interface FilmService {

	/**
	 * 
	 * @Title: 查询上映影片(上映时间)
	 * @Description: 
	 * @param 
	 * @return List<FilmRelease>
	 * @author wenjie.mai
	 */
	public List<FilmRelease> getFilmRelease(Long cityId,int num);
	
	
	/**
	 * 
	 * @Title: 查询热门影片(影片评分)
	 * @Description: 
	 * @param 
	 * @return List<FilmRelease>
	 * @author wenjie.mai
	 */
	public List<FilmRelease> getFilmReleaseByGrade(Long cityId,int num);
	
	/**
	 * 
	 * @Title: 查询热门影院
	 * @Description: 
	 * @param 
	 * @return List<CinemaInfo>
	 * @author wenjie.mai
	 */
	public List<CinemaInfoForm> getPopularCinema(Long cityId);
	
	/**
	 * 
	 * @Title:  查询影片类型
	 * @Description: 
	 * @param 
	 * @return List<FilmSort>
	 * @author wenjie.mai
	 */
	public List<FilmSort> getFilmSort();
	
	/**
	 * 
	 * @Title: 查询影片ID
	 * @Description: 
	 * @param 
	 * @return List<FilmRelease>
	 * @author wenjie.mai
	 */
	public List<Long> getFilmReleaseInfo(String filmpy,Long cityId);
	
	/**
	 * 
	 * @Title: 查询影片个数
	 * @Description: 
	 * @param 
	 * @return int
	 * @author wenjie.mai
	 */
	public int getFilmIdCount(List<Long> filmidlist);
	
	/**
	 * 
	 * @Title: 分页查询影片信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<FilmReleaseForm> getFilmInfo(List<Long> filmids,Pager pager);
	
	/**
	 * 
	 * @Title: 查询团购排行
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<GoodsForm> getTuanGouFilmRank(Long tagextid,Long cityId,int num);
	
	/**
	 * 
	 * @Title: 查询影片详情页影片信息
	 * @Description: 
	 * @param 
	 * @return List<FilmReleaseForm>
	 * @author wenjie.mai
	 */
	public List<FilmReleaseForm> getFilmInfoById(Long filmId);
	
	/**
	 * 
	 * @Title: 查询影院ID
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<Long> getCinemaIdByFilmId(Long filmId,String startTime,String endTime);
	
	/**
	 * 
	 * @Title: 查询影院所属商圈
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<RegionCatlog> getFilmRegion(Long cityId,List<Long> cidlist);
	
	/**
	 * 
	 * @Title: 分页查询影院信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<CinemaInfoForm> getCinemaInfo(List<Long> cidlist,Pager pager,Long cityId,Long filmId);
	
	/**
	 * 
	 * @Title: 查询详情页筛选的影院信息
	 * @Description: 
	 * @param 
	 * @return List<CinemaInfoForm>
	 * @author wenjie.mai
	 */
	public List<CinemaInfoForm> getCinemaList(List<Long> cidlist,Pager pager,Long cityId,Long filmId,Long reiginId);
	
	/**
	 * 
	 * @Title: 查询影片放映计划
	 * @Description: 
	 * @param 
	 * @return List<FilmShow>
	 * @author wenjie.mai
	 */
	public List<FilmShow> getFilmShowlist(Long filmId,Long cinemaId,String dayTime);
	
	public int getCinemaListCount(List<Long> cidlist,Long cityId,Long regionId,Long filmId);
	
	
	/**
	 * 查询影院下某个影片在线订座的最低价
	 * @param cityId
	 * @param cinemaId
	 * @param filmId
	 * @return
	 */
	public BigDecimal getLowestPriceByFilm(Long cityId,Long cinemaId,Long filmId);
}
