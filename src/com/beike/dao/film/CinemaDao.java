package com.beike.dao.film;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.film.Cinema;
import com.beike.entity.film.CinemaInfo;
import com.beike.entity.film.FilmShow;
import com.beike.page.Pager;

public interface CinemaDao extends GenericDao<Cinema, Long> {

	/**
	 * 查询影院区域Id
	 * @param cityId
	 * @return
	 */
	public List<Long> queryCinemaAreaIdsByCity(Long cityId);
	/**
	 * 查询影院详情
	 * @param cinemaId 影院ID
	 * @return
	 */
	public CinemaInfo queryCinemaDetail(Long cinemaId);

	/**
	 * 按区域查询影院总数
	 * @param cityId
	 * @param areaId
	 * @param pager
	 * @return
	 */
	public int queryCinemaCount(Long cityId, Long areaId);
	
	/**
	 * 查询影院正在上映的影片总数
	 * @param pager
	 * @param cinemaId
	 * @return
	 */
	public int queryShowFilmCountByCinema(Long cinemaId);

	/**
	 * 按区域查询影院列表
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
	 * 查询影院下某影片的放映计划
	 * @param cinemaId
	 * @param filmId
	 * @return
	 */
	public List<FilmShow> queryFilmShowPlainByCinema(Long cinemaId,Long filmId);
	
	
	public Long queryQianpinCinemaByWpId(Long cinemaId);

	
	/**
	 * 查询一个影院是否是千品网影院
	 * @param cinemaId
	 * @return
	 */
	public boolean queryIsQianpinCinema(Long cinemaId);
}
