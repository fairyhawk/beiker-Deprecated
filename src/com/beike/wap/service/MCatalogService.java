package com.beike.wap.service;

import java.util.List;

import com.beike.service.GenericService;
import com.beike.wap.entity.MAbstractCatlog;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.entity.MerchantCatlog;

/**
 * <p>
 * Title:优惠券、商品等商圈信息Service
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

public interface MCatalogService extends GenericService<MAbstractCatlog, Long> {

	/**
	 * 获取优惠卷商圈信息
	 * @param couponId
	 * @return
	 */
	public MCouponCatlog getCouponCatlogById(long couponId);
	
	/**
	 * 获取查询优惠卷数量
	 * @param couponCatlog
	 * @return
	 * @throws Exception
	 */
	public int getCouponCatalogSum(MCouponCatlog couponCatlog) throws Exception;
	
	/**
	 * 获取查询条件下品牌数量
	 * @param merchantCatlog
	 * @return
	 * @throws Exception
	 */
	public int getBrandCatlogSum(MerchantCatlog brandCatlog) throws Exception;
	
	/**
	 * 获取查询条件下的优惠卷id
	 */
	public List<Long> getAllCouponId(int startPage, MCouponCatlog couponCatlog) throws Exception;
	
	/**
	 * 获取查询条件下品牌id
	 * @param startPage
	 * @param merchantCatlog
	 * @return
	 * @throws Exception
	 */
	public List<Long> getAllMerchantId(int startPage, MerchantCatlog brandCatlog) throws Exception;
}
