package com.beike.dao.businessbackground;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @Title: VipDao.java
 * @Package com.beike.dao.businessbackground
 * @Description: 商家后台会员相关信息查询
 * @date January 28, 2013 10:22:06 AM
 * @version V1.0
 */
public interface VipDao{
	/**
	 *  查询线上会员信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryVip(Map<String, Object> params);
	/**
	 *  通过userId查询线上会员信息
	 * @param userId,guestId
	 * @return
	 */
	Map<String, Object> queryVipById(Long userId,Long guestId);
	/**
	 *  查询线上会员数量
	 * @param params
	 * @return
	 */
	int queryVipCount(Map<String, Object> params);
	/**
	 *  会员未消费商品明细
	 * @param params,其中params中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	List<Map<String, Object>> queryVipProduct(Map<String, Object> params);
	/**
	 *  会员未消费商品数量
	 * @param params,其中params中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	int queryVipProductCount(Map<String, Object> params);
	/**
	 *  查询商家会员信息
	 * @param guestId
	 * @return
	 */
	Map<Long, Timestamp> queryVipByGuestId(Long guestId);
	
	/**
	 *  查询商家会员信息
	 * @param userId,guestId
	 * @return
	 */
	Map<Long, Timestamp> queryVipByUserIdAndGuestId(Long userId,Long guestId);
	/**
	 *  查询商品菜单
	 * @param trxorderId
	 * @return
	 */
	List<Map<String, Object>> queryMenuByOrderId(Long trxorderId);
	/**
	 *  验证所传trxorderid是否属于对应商家
	 * @param trxorderId，guestId
	 * @return
	 */
	public int queryTrxOrderGoodsCount(Long trxorderId,Long guestId);
}
