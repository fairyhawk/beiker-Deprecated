package com.beike.biz.service.trx;

import java.util.Map;

import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.entity.goods.Goods;
/**
 *  处理电影票service
 * @author ljp
 * @date 20121203
 *
 */
public interface OrderFilmService {
	/**
	 *  根据 seatInfo 生成goods信息
	 * @param seatInfo 买票信息
	 * @param id filmShowid 放映流水号
	 * @return goods
	 * @author ljp
	 * @date 20121203
	 */
	public Goods createFilmGoods( String seatInfo, Long id )throws Exception;
	
	/**
	 * 创建filmgoodsorder数据 并调用网票网下单接口
	 * @param filmGoodsOrder
	 * @param sid 订单号
	 * @param mobile 下单用户的手机号
	 * @throws Exception
	 * @author ljp
	 * @date 20121203
	 */
	public Map<String, String> addFilmGoodsOrder(FilmGoodsOrder filmGoodsOrder, String sid, String mobile)throws Exception;
	
	
	/**
	 *  根据 id 更新filmgoodsorder 
	 * @param filmId id 
	 * @param film_payno 网票网下单回调唯一标识
	 * @param trxOrderId  订单ID
	 * @param trxOrderGoodsId 商品订单id
	 * @throws Exception
	 */
	public void updateFilmGoodsOrderById(Long filmId, String film_payno, Long trxOrderId, Long trxOrderGoodsId)throws StaleObjectStateException;
	
	/**
	 *  根据showIndex 查询filmshow info
	 * @param showIndex
	 * @return
	 * @throws Exception
	 */
	
	public Map<String, Object> queryFilmShowByShowIndex(Long showIndex) throws Exception;
	
	/**
	 *  根据影院id查询影院信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryCinemaInfoByCinemaId(Long cinemaId)throws Exception;
	
	
	/**
	 * 根据trxGoodsId更新网票网订单状态
	 * @param trxGoodsId
	 */
	public void updateStatusByTrxGoodsId(Long trxGoodsId);
	
	/**
	 * 根据trxGoodsId查询影院id
	 * @param trxGoodsId
	 * @return
	 * @throws Exception
	 */
	public Long queryCinemaIdByTrxGoodsId(Long trxGoodsId)throws Exception;
}
