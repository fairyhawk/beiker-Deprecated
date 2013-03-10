package com.beike.service.film;

import java.util.List;

import com.beike.form.GoodsForm;
import com.beike.page.Pager;

public interface FilmGrouponService {

	/**
	 * 查询区域或影院下的所有电影商品ID
	 * @param cityId
	 * @param areaId
	 * @param cinemaId
	 * @return
	 */
	public List<Long> querysFilmIds(Long cityId, Long areaId, Long cinemaId);

	/**
	 * 查询影片总数
	 * @param filmGoodsIdList 电影商品ID
	 * @param scope 范围（全部电影、最新电影：3天之内发布的）
	 * @param cashOnly 只搜索现金券
	 * @return
	 */
	public int queryFilmGoodsCount(List<Long> filmGoodsIdList, String scope, boolean cashOnly);

	/**
	 * 查询影片
	 * @param filmGoodsIdList 电影商品ID
	 * @param scope 范围（全部电影、最新电影：3天之内发布的）
	 * @param sort 排序方式(默认\发布时间\销量\价格\好评率\折扣)
	 * @param cashOnly 只搜索现金券
	 * @return
	 */
	public List<GoodsForm> queryFilmGoods(Pager pager, List<Long> filmGoodsIdList, String scope, String sort, boolean cashOnly);
}
