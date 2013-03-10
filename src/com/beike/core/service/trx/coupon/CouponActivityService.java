package com.beike.core.service.trx.coupon;

import com.beike.common.entity.coupon.CouponActivity;

/**   
 * @title: CouponActivityService.java
 * @package com.beike.core.service.trx.coupon
 * @description: 优惠券活动service
 * @author wangweijie  
 * @date 2012-10-30 下午12:07:38
 * @version v1.0   
 */
public interface CouponActivityService {
	/**
	 * 根据ID查询优惠券活动
	 * @param id
	 * @return
	 */
	public CouponActivity queryCouponActivityById(Long id);
	
	
}
