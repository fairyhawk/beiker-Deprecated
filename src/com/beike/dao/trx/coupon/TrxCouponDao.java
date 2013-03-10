package com.beike.dao.trx.coupon;

import java.util.List;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.enums.trx.TrxCouponStatus;
import com.beike.common.exception.StaleObjectStateException;

/**   
 * @title: TrxCouponDao.java
 * @package com.beike.dao.coupon
 * @description: 优惠券dao
 * @author wangweijie  
 * @date 2012-10-30 上午11:37:51
 * @version v1.0   
 */
public interface TrxCouponDao {
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryTrxCouponById(Long id);
	
	
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryTrxCouponByPwd(String couponPwd);
	
	/**
	 * 查询id最小的初始化优惠券
	 * @param activityId
	 * @return
	 */
	public TrxCoupon queryMinINITCouponId(Long activityId);
	
	/**
	 * 获得用户已参加活动ID
	 * @param userId
	 * @return
	 */
	public List<Long> queryUserJoinCouponActivity(Long userId);
	
	/**
	 * 根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryTrxCouponByIdAndUserId(Long id,Long userId);
	
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
	public List<TrxCoupon> queryAllTrxCouponsByUserIdForPage(Long userId,int startRow,int pageSize);
	
	/**
	 * 根据userId和优惠券状态查询优惠券
	 */
	public List<TrxCoupon> queryTrxCouponsByUserId(Long userId,TrxCouponStatus couponStatus);
	
	/**
	 * 绑定优惠券
	 * @param couponId
	 * @param userId
	 * @param description
	 * @param version
	 * @throws StaleObjectStateException
	 */
	public void updateTrxCouponForBind(Long couponId,Long userId,String description,Long version)throws StaleObjectStateException;

	

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
	
	
	/**
	 * 使用优惠券
	 * @param couponId
	 * @param isCreditAct
	 * @param description
	 * @param version
	 * @throws StaleObjectStateException
	 */
	public void updateTrxCouponForSale(Long couponId,int isCreditAct,String requestId,String description,Long version)throws StaleObjectStateException;
	
	/**
	 * 查询未入账的优惠券
	 * @return
	 */
	public List<TrxCoupon> queryNoCreditActCoupon();
	
	
	/**
	 * 更改优惠券为已入账
	 * @param couponId
	 * @param version
	 * @return
	 */
	public void updateTrxCouponForCreditAct(Long couponId,Long version,String description) throws StaleObjectStateException;
}
