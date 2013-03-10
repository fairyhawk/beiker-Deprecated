package com.beike.dao.trx.coupon;

import java.util.List;

import com.beike.common.entity.coupon.CouponActivity;

/**   
 * @title: CouponActivityDao.java
 * @package com.beike.dao.coupon
 * @description: 优惠券活动dao
 * @author wangweijie  
 * @date 2012-10-30 上午11:38:13
 * @version v1.0   
 */
public interface CouponActivityDao {
	
	/**
	 * 根据ID查询优惠券活动
	 * @param id
	 * @return
	 */
	public CouponActivity queryCouponActivityById(Long id);
	
	/**
	 * 根据类型查询CouponActivity
	 * @param type
	 * @return
	 */
	public List<CouponActivity> queryCouponActivityByType(String type);
	
}
