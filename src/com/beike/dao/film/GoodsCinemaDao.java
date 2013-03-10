package com.beike.dao.film;

import java.math.BigDecimal;
import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.film.GoodsCinema;

public interface GoodsCinemaDao extends GenericDao<GoodsCinema, Long> {

	/**
	 * 查询是否存在指定影院的商品
	 * @param cinemaId
	 * @return
	 */
	public boolean queryExsitCinemaGoods(Long cinemaId);

	/**
	 * 查询影院最低团购商品价
	 * @param cinemaId
	 * @return
	 */
	public BigDecimal queryLowestGrouponPrice(Long cinemaId);
	
	
	
	/**
	 * 查询区域或影院下的所有电影商品ID
	 * @param cityId
	 * @param areaId
	 * @param cinemaId
	 * @return
	 */
	public List<Long> querysFilmIds(Long cityId, Long areaId, Long cinemaId);
	
}
