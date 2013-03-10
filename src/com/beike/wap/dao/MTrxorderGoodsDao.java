package com.beike.wap.dao;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MTrxorderGoods;

/**
 * @Title: TrxorderGoodsDao.java
 * @Package com.beike.dao.trx
 * @Description: 订单商品明细DAO
 * @date May 16, 2011 6:53:25 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface MTrxorderGoodsDao extends GenericDao<MTrxorderGoods, Long> {

	/**
	 * 查询订单数量
	 */
	public int findPageCountByUserId(Long userId, String viewType);

	/**
	 * 根据订单id和订单状态查询
	 * @param trxId 订单id
	 * @param status 订单状态
	 * @return
	 */
	public List<MTrxorderGoods> findOrderByStatusAndTrxId(Long userId, String status, int startPage, int pageSize);
	
	/**
	 * Description：wap--根据id查询过去时间，订单号，服务密码信息
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
	 * Description :根据id查询退款信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MTrxorderGoods getRefundGoodsInfo(String id,String status) throws Exception;
}
