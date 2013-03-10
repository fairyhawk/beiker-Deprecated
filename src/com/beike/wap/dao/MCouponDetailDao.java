package com.beike.wap.dao;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MCoupon;

/**
 * 优惠卷详情数据处理
 */

public interface MCouponDetailDao extends GenericDao<MCoupon, Long>{
	
	/**
	 * 根据id查询详情
	 * @author k.w
	 */
	public MCoupon findById(long couponId);
	
	/**
	 * 根据品牌id查询优惠卷
	 */
	public List<MCoupon> findCouponByBrandId(long brandId);
	
	/**
	 * 根据id列表获取优惠卷
	 * @return
	 */
	public List<MCoupon> findCouponByIds(String couponIds);
}
