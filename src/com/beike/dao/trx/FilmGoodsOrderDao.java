package com.beike.dao.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;
/**
 * @desc 对应FilmGoodsOrder表的dao
 * @author ljp
 * @date 20121129
 *
 */
public interface FilmGoodsOrderDao  extends GenericDao<FilmGoodsOrder, Long> {
	/**
	 * @desc 增加FilmGoodsOrder
	 * @param filmGoodsOrder
	 * @throws Exception
	 * @author ljp
	 */
	public void addFilmGoodsOrder(FilmGoodsOrder filmGoodsOrder)throws Exception;
	
	/**
	 * @desc 根据字段条件查询
	 * @param condtion
	 * @return
	 * @throws Exception
	 * @author ljp
	 */
	public List<Object> queryFilmGoodsOrderByCondition(Map<String, String> condition)throws Exception;
	
	/**
	 * @desc 根据id更新FilmGoodsOrder
	 * @param id
	 * @param filmGoodsOrder
	 * @throws Exception
	 * @author ljp
	 */
	public void updateFilmGoodsOrderById(FilmGoodsOrder filmGoodsOrder)throws StaleObjectStateException;
	
	/**
	 * @desc 根据id查询FilmGoodsOrder
	 * @param id
	 * @return
	 * @throws Exception
	 * @author ljp
	 */
	public FilmGoodsOrder queryFilmGoodsOrderById(Long id);
	
	/**
	 * 根据trxGoodsId更新网票网订单状态
	 * @param trxGoodsId
	 */
	public void updateStatusByTrxGoodsId(Long trxGoodsId);
}
