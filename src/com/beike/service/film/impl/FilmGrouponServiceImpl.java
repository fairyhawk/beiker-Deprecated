package com.beike.service.film.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.dao.film.GoodsCinemaDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.impl.catlog.GoodsCatlogDaoImpl;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.service.film.FilmGrouponService;
import com.beike.service.lucene.search.LuceneSearchFacadeService;

@Service("filmGrouponService")
public class FilmGrouponServiceImpl implements FilmGrouponService {

	@Autowired
	private LuceneSearchFacadeService facadeService;
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	@Autowired
	private GoodsCinemaDao goodsCinemaDao;
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private GoodsCatlogDaoImpl goodsCatlogDaoImpl;

	
	@Override
	public List<Long> querysFilmIds(Long cityId, Long areaId, Long cinemaId) {
		List<Long> validFilmIdList = goodsCinemaDao.querysFilmIds(cityId, areaId, cinemaId);
		return validFilmIdList;
	}

	@Override
	public int queryFilmGoodsCount(List<Long> filmGoodsIdList, String scope, boolean cashOnly) {
		int result = goodsDao.queryGoodsCountByIds(filmGoodsIdList, scope, cashOnly);
		return result;
	}

	@Override
	public List<GoodsForm> queryFilmGoods(Pager pager, List<Long> filmGoodsIdList, String scope, String sort, boolean cashOnly) {

		//分页查询符合条件的电影商品
		AbstractCatlog goodsCatLog = new GoodsCatlog();
		
//		List<GoodsForm> filmGoodsList = goodsDao.queryGoodsByIds(pager, filmGoodsIdList, scope, sort, cashOnly);

		List<Long> goodsIdList = goodsCatlogDaoImpl.searchCatlog(filmGoodsIdList, goodsCatLog, pager.getStartRow(), pager.getPageSize());
		List<GoodsForm> result = facadeService.getSearchGoodsResult(goodsIdList);

		return result;
	}

}
