package com.beike.wap.service;

import java.util.List;

import com.beike.wap.entity.MTrxorderGoods;

/**
 * @Title: TrxorderGoodsService.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 订单商品CORE service
 * @date May 17, 2011 6:28:57 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface MTrxorderGoodsService {
	
	/**
	 * wap--根据user id获取交易订单信息
	 * @param userId
	 * @return
	 */
	public List<MTrxorderGoods> getTrxOrderInfo(Long userId, int startRow, int pageSize, String queryType);
	
	/**
	 * wap--根据类型查询订单数量
	 * @param userId 用户id
	 * @param qryType 查询类型
	 * @return
	 */
	public int getRecordNum(Long userId, String qryType);
	
	/**
	 * Description：wap--根据id查询过去时间，订单号，服务密码信息(我的订单未使用订单使用)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MTrxorderGoods getTrxOrderGoodsInfo(Long id) throws Exception;
	
	/**
	 * wap--根据id查询数据
	 * @param id
	 * @return
	 */
	public MTrxorderGoods findById(Long id);
	/**
	 * Description :根据id查询退款信息(我的订单退款内容使用)
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MTrxorderGoods getRefundGoodsInfo(String id,String status) throws Exception;
}
