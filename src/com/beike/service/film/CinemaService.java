package com.beike.service.film;

import java.util.List;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.CinemaInfo;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.form.CinemaDetailForm;
import com.beike.page.Pager;

public interface CinemaService {
	
	/**
	 * 查询影院区域
	 * @param cityId
	 * @return
	 */
	public List<RegionCatlog> getCinemaAreasByCityId(Long cityId);

	/**
	 * 查询影院详情
	 * @param cinemaId 影院ID
	 * @return
	 */
	public CinemaDetailForm queryCinemaDetail(Long cinemaId);

	/**
	 * 按区域查询影院总数
	 * @param cityId
	 * @param areaId
	 * @param pager
	 * @return
	 */
	public int queryCinemaCount(Long cityId, Long areaId);

	/**
	 * 按区域分页查询影院列表
	 * @param pager
	 * @param cityId
	 * @param areaId
	 * @return
	 */
	public List<CinemaInfo> queryCinema(Pager pager, Long cityId, Long areaId);

	/**
	 * 按区域所有影院列表
	 * @param pager
	 * @param cityId
	 * @param areaId
	 * @return
	 */
	public List<CinemaInfo> queryCinema(Long cityId, Long areaId);
	
	/**
	 * 查询影院正在上映的影片总数
	 * @param cityId TODO
	 * @param cinemaId
	 * @param pager
	 * @return
	 */
	public int queryFilmReleaseCountByCinema(Long cityId, Long cinemaId);
	/**
	 * 查询影院正在上映的影片
	 * @param pager
	 * @param cityId TODO
	 * @param cinemaId
	 * @return
	 */
	public List<FilmRelease> queryFilmReleaseByCinema(Pager pager, Long cityId, Long cinemaId);

	/**
	 * 查询所有上映的影片总数
	 * @param cityId 城市ID
	 * @param pager
	 * @param cinemaId
	 * @return
	 */
	public int queryFilmReleaseCountByCityId(Long cityId);
	/**
	 * 查询所有上映的影片
	 * @param pager
	 * @param cityId 城市ID
	 * @param cinemaId
	 * @return
	 */
	public List<FilmRelease> queryFilmReleaseCityId(Pager pager, Long cityId);
	

	/**
	 * 查询影院下某影片的放映计划
	 * @param cinemaId
	 * @param filmId
	 * @return
	 */
	public List<FilmShow> queryFilmShowPlainByCinema(Long cinemaId,Long filmId);
	
	
	
	/**
	 * 查询一个影院是否是千品网影院
	 * @param cinemaId
	 * @return
	 */
	public boolean queryIsQianpinCinema(Long cinemaId);
	
	
}
