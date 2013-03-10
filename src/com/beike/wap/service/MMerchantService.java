package com.beike.wap.service;

import java.util.List;

import com.beike.wap.entity.MMerchant;

/**
 * <p>
 * Title:品牌或分店Service
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
public interface MMerchantService {
	/**
	 * 获取品牌
	 */
	public MMerchant getBrandById(long brandId);
	
	/**
	 * 获取分店
	 */
	public List<MMerchant> getBranchByBrandId(long brandId);
	
	/**
	 * 获得商家
	 * 
	 * @param merchantId
	 *            商家ID
	 * @return
	 */
	public String getGoodsMerchantLogo(Long merchantId);
	
	/**
	 * 获得商家评价平均分数
	 * 
	 * @param merchantId
	 *            商家ID
	 * @return
	 */
	public String getAvgEvationScores(Long merchantId);
	
	/**
	 * 根据brandId列表获取品牌列表
	 * @param brandIds
	 * @return
	 */
	public List<MMerchant> getBrandByIds(String brandIds);
	
	/**
	 * Desceription : 根据商品ID去查询商品所属品牌
	 * @param goodsId
	 * @return
	 */
	public MMerchant getBrandByGoodId(String goodsId);
}
