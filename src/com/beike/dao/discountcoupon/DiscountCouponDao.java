package com.beike.dao.discountcoupon;

import java.util.List;

import com.beike.common.entity.discountcoupon.DiscountCoupon;
import com.beike.common.enums.trx.DiscountCouponStatus;
import com.beike.common.exception.StaleObjectStateException;

/**   
 * @title: DiscountCouponDao.java
 * @package com.beike.dao.discountcoupon
 * @description: 线下优惠券DAO
 * @author wangweijie  
 * @date 2012-7-11 下午06:27:57
 * @version v1.0   
 */
public interface DiscountCouponDao {

	/**
	 * 根据ID查询优惠券记录
	 * @param id
	 * @return
	 */
	public DiscountCoupon findById(Long id);
	
	/**
	 * 根据couponNo查询优惠券记录
	 * @param couponNo
	 * @return
	 */
	public DiscountCoupon findByCouponNo(String couponNo);
	
	/**
	 * 根据couponPwd查询优惠券记录
	 * @param couponPwd
	 * @return
	 */
	public DiscountCoupon findByCouponPwd(String couponPwd);
	
	/**
	 * 查询处于激活状态的过期优惠券记录
	 */
	public List<DiscountCoupon> findExpireCouponInActiveStatus();
	
	
	/**
	 * 根据userId查询优惠券记录
	 * @param userId
	 * @return
	 */
	public List<DiscountCoupon> findByUserId(Long userId);
	
	/**
	 * 更新优惠券状态
	 * @param status
	 * @param description
	 * @param id
	 * @param coupon
	 * @throws StaleObjectStateException
	 */
	public void updateCouponStatus(DiscountCouponStatus couponStatus,String descritpion,Long id,Long version) throws StaleObjectStateException;
	
	
	/**
	 * 更新优惠券
	 * @param coupon
	 * @throws StaleObjectStateException
	 */
	public void updateCouponStatusAndUserId(DiscountCouponStatus couponStatus,Long userId,Long id,Long version) throws StaleObjectStateException;
}
