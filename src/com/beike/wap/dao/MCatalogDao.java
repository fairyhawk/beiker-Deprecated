package com.beike.wap.dao;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.entity.MerchantCatlog;

/**
 * 商圈数据处理类
 */

public interface MCatalogDao extends GenericDao<MCouponCatlog, Long>{
	
	/**
	 * 根据id查询优惠卷商圈信息
	 * @param couponId
	 * @return
	 */
	public MCouponCatlog findCouponCatalogById(long couponId);
	
	/**
	 * 查询商圈内优惠卷总数量
	 * @param couponCatlog
	 * @return
	 * @throws Exception
	 */
	public int getCouponCatalogSum(MCouponCatlog couponCatlog) throws Exception;
	
	/**
	 * 查询商圈内品牌总数量
	 * @param couponCatlog
	 * @return
	 * @throws Exception
	 */
	public int getBrandCatalogSum(MerchantCatlog brandCatlog) throws Exception;
	
	/**
	 * 查询商圈条件内优惠卷id
	 * @param page
	 * @param couponCatLog
	 * @return
	 * @throws Exception
	 */
	public List<Long> queryCouponId(int page,MCouponCatlog couponCatLog) throws Exception;
	
	/**
	 * 查询商圈内品牌id列表
	 * @param page
	 * @param couponCatLog
	 * @return
	 * @throws Exception
	 */
	public List<Long> queryBrandId(int page,MerchantCatlog brandCatlog) throws Exception; 
}
