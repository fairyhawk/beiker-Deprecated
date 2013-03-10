package com.beike.wap.service;

import java.util.List;

import com.beike.service.GenericService;
import com.beike.wap.entity.MCoupon;

/**
 * <p>
 * Title:优惠券详情Service
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-09-23
 * @author k.w
 * @version 1.0
 */

public interface MCouponDetailService extends GenericService<MCoupon, Long> {
	
	/**
	 * 查询优惠卷信息by 品牌id
	 */
	public List<MCoupon> queryCouponByBrandId(long brandId);
	
	/**
	 * 根据优惠卷id列表获取优惠卷
	 */
	public List<MCoupon> queryCouponByIdS(String ids);
}
