package com.beike.core.service.trx;

import java.io.IOException;

import com.beike.common.bean.trx.FilmApiOrderParam;

/**
 * @author yurenli
 * 网票网统一对接接口
 * 2012-12-5 15:47:47
 */
public interface FilmApiGoodsOrderService {

	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网下单接口
	 * @return
	 */
	public String createFilmOrder(FilmApiOrderParam filmApiOrderParam)  throws IOException ;
	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网完成支付接口
	 * @return
	 */
	public String updateFilmOrder(FilmApiOrderParam filmApiOrderParam) throws IOException ;
	
	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网重发验票码接口
	 * @return
	 */
	public String resendFilmCode(FilmApiOrderParam filmApiOrderParam) throws IOException ;
	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网选择座位接口
	 * @return
	 */
	public String selectFilmSeat(FilmApiOrderParam filmApiOrderParam)throws IOException;
	
	/**
	 * 查询网票网信息
	 * @param filmApiOrderParam
	 * @return
	 */
	public String findFilmInfo(FilmApiOrderParam filmApiOrderParam)throws IOException;
	
	
}
