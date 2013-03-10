package com.beike.core.service.trx.discountcoupon;

import java.util.List;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.entity.discountcoupon.DiscountCoupon;
import com.beike.common.enums.trx.DiscountCouponStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.DiscountCouponException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;

/**   
 * @title: DiscountCouponService.java
 * @package com.beike.core.service.trx.discountcoupon
 * @description: 线下优惠券service
 * @author wangweijie  
 * @date 2012-7-11 下午07:11:21
 * @version v1.0   
 */
public interface DiscountCouponService {
	
	/**
	 * 根据密码查询优惠券
	 * @param couponPwd
	 * @return
	 */
	public DiscountCoupon findByCouponPwd(String couponPwd);
	
	/**
	 * 根据用户主键查询优惠券记录
	 * @param userId
	 * @return
	 */
	public List<DiscountCoupon> findByUserId(Long userId);
	
	
	/**
	 * 优惠券充值
	 * @param couponPwd
	 * @param userId
	 * @param reqChannel
	 * @throws StaleObjectStateException
	 * @throws AccountException
	 * @throws VmAccountException
	 */
	public TrxResponseData topupByCouponAndUserId(String couponPwd,Long userId,ReqChannel reqChannel) throws StaleObjectStateException,
		AccountException,VmAccountException,DiscountCouponException;
	
	/**
	 * 查询处于激活状态的过期优惠券记录
	 * @return
	 */
	public List<DiscountCoupon> findExpireCouponInActiveStatus();
	
	/**
	 * 更新优惠券状态
	 * @param status
	 * @param description
	 * @param id
	 * @param version
	 * @throws StaleObjectStateException
	 */
	public void updateCouponStatus(DiscountCouponStatus status,String description,Long id,Long version) throws StaleObjectStateException;
}
