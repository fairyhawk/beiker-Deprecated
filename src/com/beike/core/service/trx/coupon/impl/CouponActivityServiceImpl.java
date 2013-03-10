package com.beike.core.service.trx.coupon.impl;

import org.springframework.stereotype.Service;

import com.beike.common.entity.coupon.CouponActivity;
import com.beike.core.service.trx.coupon.CouponActivityService;
import com.beike.dao.trx.coupon.CouponActivityDao;

/**   
 * @title: CouponActivityServiceImpl.java
 * @package com.beike.core.service.trx.coupon.impl
 * @description: 优惠券活动service实现
 * @author wangweijie  
 * @date 2012-10-30 下午02:54:03
 * @version v1.0   
 */
@Service("couponActivityService")
public class CouponActivityServiceImpl implements CouponActivityService {
	private CouponActivityDao couponActivityDao;
	
	/**
	 * 根据ID查询优惠券活动
	 * @param id
	 * @return
	 */
	@Override
	public CouponActivity queryCouponActivityById(Long id) {
		return couponActivityDao.queryCouponActivityById(id);
	}
}
