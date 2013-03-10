package com.beike.wap.dao;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.merchant.MerchantProfileType;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.entity.MMerchantProfileType;

public interface MMerchantDao  extends GenericDao<MMerchant, Long>{

	/**
	 * 根据id获取品牌
	 * @param brandId
	 * @return
	 */
	public MMerchant getBrandById(long brandId);
	
	/**
	 * 根据商户Id获取分店
	 */
	public List<MMerchant> getBranchIdByParentId(long brandId);
	
	/**
	 * 获得某个商户的扩展属性
	 * 
	 * @param merchantId
	 *            商家id
	 * @param propertyname
	 *            商家扩展属性名称
	 * @return
	 */
	public MMerchantProfileType getMerchantAvgEvationScoresByMerchantId(Long merchantId);
	
	public MMerchantProfileType getMerchantLogoByMerchantId(Long merchantId);
	
	
	/**
	 * 根据品牌id列表获取品牌列表
	 * @param brandIds
	 * @return
	 */
	public List<MMerchant> getBrandListByIds(String brandIds);
	
	/**
	 * Desceription : 根据商品ID去查询商品所属品牌
	 * @param goodsId
	 * @return
	 */
	public MMerchant getBrandByGoodId(String goodsId);
}
