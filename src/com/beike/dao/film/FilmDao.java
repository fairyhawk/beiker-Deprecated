package com.beike.dao.film;

import java.math.BigDecimal;
import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.film.Cinema;
import com.beike.entity.film.FilmRelease;
import com.beike.page.Pager;

/**  
* @Title:  影院Dao
* @Package com.beike.dao.film
* @Description: 
* @author wenjie.mai  
* @date 2012-11-28 上午15:39:21
* @version V1.0  
*/
public interface FilmDao extends GenericDao<Cinema,Long>{

	/**
	 * 
	 * @Title: 查询影片类型
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmType();
	
	/**
	 * 
	 * @Title: 查询影片ID
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmId(String film_py);
	
	/**
	 * 
	 * @Title: 查询影片个数
	 * @Description: 
	 * @param 
	 * @return int
	 * @author wenjie.mai
	 */
	public int getFilmInfoCount(List<Long> filmids);
	
	/**
	 * 
	 * @Title: 查询影片信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmInfo(List<Long> filmids,int start,int end);
	
	/**
	 * 
	 * @Title:  查询热片排行(上映时间)
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getPopularFilmRank(int num);
	
	/**
	 * 
	 * @Title: 查询热门影片(影片评分)
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getPopularFilmByGrade(int num);
	/**
	 * 
	 * @Title:  查询团购排行
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getTuanGouFilmRank(Long tagextid,Long cityId,int num);
	
	/**
	 * 
	 * @Title:  查询影片播放语言
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmLanguage(List<Long> filmids);
	
	/**
	 * 
	 * @Title: 通过影片ID查询影院ID
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaIdByFilmId(Long filmId,String startTime,String endTime);
	
	/**
	 * 
	 * @Title:  查询城市下的所有一级商圈
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getRegionIdByCity(Long cityId);
	
	/**
	 * 
	 * @Title:  计算每个一级商圈下影院的个数
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getRegionByFilmCount(List<Long> cinemalist,Long cityId);
	
	/**
	 * 
	 * @Title: 查询千品网影院ID
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaId(List<Long> cinemalist);
	
	/**
	 * 
	 * @Title: 查询某个城市的商品
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getGoodsIdByCityId(Long cityId,List<Long> idlist);
	
	/**
	 * 
	 * @Title: 查询千品网影院信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaInfoById(List<Long> cinemalist);
	
	/**
	 * 
	 * @Title: 查询影片播放语言
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmLanguageById(Long filmId);
	
	/**
	 * 
	 * @Title: 查询详情页影片信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getFilmInfoById(Long filmId);
	
	/**
	 * 
	 * @Title: 分页查询影院信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaInfo(List<Long> cidlist,int start,int end,Long cityId,Long filmId);
	
	/**
	 * 
	 * @Title: 查询某个城市的影院
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaInfoIdByCity(Long cityId,String flag,List<Long> notidlist);
	
	public List getFilmShowlist(Long filmId, Long cinemaId, String startTime,String endTime);
	
	public List getCinemaList(List<Long> cidlist, int start, int end,Long cityId,Long regionId,Long filmId);
	
	public List getCinemaInfoIdByCinemaId(List<Long> cidlist,Long cityId);
	
	public List getCinemaPriceById(List<Long> cidlist,Long filmId);
	
	public List<FilmRelease> queryFilmReleaseByCinema(Pager pager, Long cityId, Long cinemaId);
	
	public int queryFilmReleaseCountByCinema(Long cityId, Long cinemaId);
	
	/**
	 * 
	 * @Title: 通过千品网影院ID查询网票网影院ID
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaIdByCid(Long cinemaId);
	
	/**
	 * 
	 * @Title: getCinemaFormList
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaFormList(List<Long> cidlist,Long cityId);
	
	public int getCinemaListCount(List<Long> cidlist,Long cityId,Long regionId,Long filmId);
	
	
	
	/**
	 * 查询所有上映的影片总数
	 * @param cityId TODO
	 * @param pager
	 * @param cinemaId
	 * @return
	 */
	public int queryFilmReleaseCountCityId(Long cityId);
	/**
	 * 查询所有上映的影片
	 * @param pager
	 * @param cityId TODO
	 * @param cinemaId
	 * @return
	 */
	public List<FilmRelease> queryFilmReleaseCityId(Pager pager, Long cityId);
	
	
	/**
	 * 
	 * @Title: 查询影院信息
	 * @Description: 
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getCinemaInfoList(Long cityId,List<Long> idlist,int num);
	
	
	/**
	 * 查询影院下某个影片在线订座的最低价
	 * @param cityId
	 * @param cinemaId
	 * @param filmId
	 * @return
	 */
	public BigDecimal getLowestPriceByFilm(Long cityId,Long cinemaId,Long filmId);
	
}
