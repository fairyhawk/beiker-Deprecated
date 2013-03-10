package com.beike.core.service.trx.coupon;

import java.util.List;
import java.util.Map;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.StaleObjectStateException;

/**   
 * @title: TrxCouponService.java
 * @package com.beike.core.service.trx.coupon
 * @description: 优惠券service
 * @author wangweijie  
 * @date 2012-10-30 下午12:07:21
 * @version v1.0   
 */
public interface TrxCouponService {
	
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryCouponById(Long id);
	
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon preQryInWtDBTrxCouponById(Long id);
	
	/**
	 * 根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryTrxCouponByIdAndUserId(Long id,Long userId);
	
	/**
	 * 查写库根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon preQryInWtDBTrxCouponByIdAndUserId(Long id,Long userId);
	
	
	/**
	 * 优惠券自动下发
	 */
	public void processOnlineActivityAutoBind(Long userId,String csid);
	
	/**
	 * 系统自动绑定
	 * @param userId
	 * @param activityId
	 * @return
	 * @throws StaleObjectStateException
	 */
	public Long processAutoBindCoupon(Long userId,Long activityId) throws StaleObjectStateException;
	
	/**
	 * 根据userId 查询所有的优惠券数量
	 * @param userId
	 * @return
	 */
	public int queryCountAllTrxCouponsByUserId(Long userId);
	
	/**
	 * 根据userId 查询所有的优惠券
	 * @param userId
	 * @return
	 */
	public List<TrxCoupon> queryAllTrxCouponsByUserId(Long userId,int startRow,int pageSize);
	
	/**
	 * 根据userId和优惠券状态查询优惠券
	 */
	public List<TrxCoupon> queryTrxCouponsByUserId(Long userId);
	
	/**
	 * 查询未入账的优惠券
	 * @return
	 */
	public List<TrxCoupon> queryNoCreditActCoupon();
	
	/**
	 * 优惠券异步出账
	 */
	public void processCreditVmAccountAsyn(TrxCoupon trxCoupon) throws AccountException,StaleObjectStateException;
	
	/**
	 * 返回页面展示需要的优惠券信息
	 * @param userId
	 * @param goodsIds
	 * @param miaoshaIds
	 * @param payAmount
	 * @return
	 */
	public Map<String,List<TrxCoupon>> queryTrxCouponByUserIdForShow(Long userId,String[] goodsIds,String[] miaoshaIds,double payAmount,boolean isUseCoupon);
	
	/**
	 * 优惠券下发
	 */
	public void dispatchCoupon(TrxCoupon coupon)throws CouponException,StaleObjectStateException;
	
	/**
	 * 优惠券是否有效
	 *  0	表示有效
	 *	2105      优惠券不存在
	 *	2106      优惠券金额限制
	 *	2107      优惠券品类限制
	 *	2108      优惠券日期限制
	 *	2109      优惠券不可用
	 * @return
	 */
	public int isAvailableCoupon(String[] goodsIds,String[] miaoshaIds,TrxCoupon trxCoupon,double orderAmount);
	
	/**
	 * 优惠券绑定
	 * @param couponPwd
	 * @param userId
	 */
	public TrxResponseData processBindCoupon(String couponPwd,Long userId,ReqChannel reqChannel)throws CouponException,StaleObjectStateException;
	

	/**
	 * 查询超时优惠券
	 * @return
	 */
	public List<TrxCoupon> queryTimeoutTrxCoupon();
	
	/**
	 * 优惠券过期操作
	 * @param couponId
	 * @param version
	 * @param description
	 * @throws StaleObjectStateException
	 */
	public void updateTrxCouponForTimeout(Long couponId,Long version,String description) throws StaleObjectStateException;
	
}
