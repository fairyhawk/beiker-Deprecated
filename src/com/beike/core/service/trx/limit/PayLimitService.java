package com.beike.core.service.trx.limit;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.PayLimitException;

/**
 * @Title: PayLimitService.java
 * @Package com.beike.core.service.trx.limit
 * @Description: 购买限制Service
 * @date May 27, 2011 4:53:09 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PayLimitService {

	/**
	 * 根据用户ID和商品ID，查询用户已购买单个商品数量
	 * 
	 * 
	 * @param Uid
	 * @param GId
	 * @return
	 */
	public Long qryPayCountByUIdAndGId(Long UId, Long GId,Long miaoshaId);

	/**
	 * 根据用户ID和商品ID，允许用户还可购买多少 导致已购买数量>可购买数量.则可购买0件。不能给转发层响应负数。不然会遭用户鄙视。
	 * 
	 * @param sourceCount
	 * @param Uid
	 * @param GId
	 * @return
	 */
	public Long allowPayCount(Long sourceCount, Long UId, Long GId,Long miaoshaId);

	/**
	 * 根据用户ID和商品ID，更新用户购买数量
	 * 
	 * @param sucPayCount
	 * @param Uid
	 * @param GId
	 * @return
	 */

	public boolean processPayLimit(TrxOrder trxOrder, List<TrxorderGoods> trxorderGoodsList);

	/**
	 * 账户支付常规写入购买量
	 * 
	 * @param toPayCount
	 * @param limitCount
	 * @param UId
	 * @param GId
	 */
	/*
	 * public void processNomalPayLimit(Long toPayCount, Long limitCount, Long
	 * UId, Long GId);
	 */

	/**
	 * 根据用户欲购买件数，用户ID，商品ID。校验是否个人限购超限
	 * 
	 * @param toPayCountList
	 * @param uIdList
	 * @param gIdList
	 * @return
	 * @throws PayLimitException
	 */
	public String verifyPayLimit(List<Long> sourcePayCountList, Long uId,

	List<Long> gIdList,List<Long> miaoshaIdList) throws PayLimitException;

	/**
	 * 批量校验
	 * 
	 * @param sourcePayCountList
	 * @param uId
	 * @param gIdList
	 * @return
	 */

	public Map<Object, Object> verifyPayLimitList(TrxOrder trxOrder,
			List<TrxorderGoods> trxorderGoodsList);

	/**
	 * 跟据goodsId获取限购数量
	 * 
	 * @param goodsIdStr
	 * @return
	 */
	public Map<Long, Integer> findSingleCount(Set<Long> goodsIdSet);

	/**
	 * 个人限购一个商品订单一个商品订单判断
	 * 
	 * @param trxOrder
	 * @param tog
	 * @return
	 */
	public boolean isAllowBuyInPayLimit(TrxOrder trxOrder, TrxorderGoods tog);


	/**
	 * 追加已做退款处理的限购信息及缓存处理
	 * @param totalOverRunRfdList
	 * @param singleOverRunRfdList
	 * @param payRequestId
	 */
	public void appendPostPayLimitDes(List<TrxorderGoods> totalOverRunRfdList,List<TrxorderGoods> singleOverRunRfdList,String payRequestId);

	
	
	/**
	 * 支付前个人限购和总量限购统一接口
	 * @param orderInfo
	 * @return
	 * @throws PayLimitException 
	 */
	public String toPayLimitCount(OrderInfo orderInfo) throws PayLimitException;
	
	/**
	 * 商品详情页支付限购
	 * @param goodsId
	 * @param miaoshaId
	 * @param userId
	 * @return
	 */
	public String toPayLimitCountNew(String goodsId,String miaoshaId,String userId);
	
	
}
