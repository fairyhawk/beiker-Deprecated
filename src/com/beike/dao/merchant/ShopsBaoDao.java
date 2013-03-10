package com.beike.dao.merchant;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.goods.Goods;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.merchant.MerchantProfileType;
import com.beike.form.CouponForm;
import com.beike.form.MerchantForm;

/**      
 * project:beiker  
 * Title:
 * Description:商铺宝DAO
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Oct 31, 2011 5:23:37 PM     
 * @version 1.0
 */
public interface ShopsBaoDao extends GenericDao<Merchant, Long> {
	/**
	 * 通过商家ID查询商家详细信息
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getMerchantDetailById(Long merchantId);
	
	
	/**
	 * 
	 * janwen
	 * App使用
	 * @param brandids
	 * @return 品牌好评数,好评率
	 *
	 */
	public List<MerchantForm> getBrandReview(List<String> brandids);
	
	
	
	/**
	 * 通过商家ID查询商家商铺宝详细信息
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getShangpubaoDetailById(Long merchantId);
	
	/**
	 * 查询 现金券 money 品牌下的所有商品
	* @Title: getCashCouponByGoods 
	* @Description: TODO
	* @param @param merchantId
	* @param @param money
	* @param @return    
	* @return String
	* @throws
	 */
	public String getCashCouponByGoods(Long merchantId,Long money);
	
	/**
	 * 通过商家ID 和金额 查询现金券
	* @Title: getCashCouponByIDAndMoney 
	* @Description: TODO
	* @param @param ids
	* @param @param money
	* @param @return    
	* @return List
	* @throws
	 */
	public List getCashCouponByIDAndMoney(String ids,Long money);
			
	/**
	 * 查询该品牌下的所有分店商品
	* @Title: getChildMerchnatById 
	* @Description: TODO
	* @param @param merchantId
	* @param @return    
	* @return List<MerchantForm>
	* @throws
	 */
	public List<MerchantForm> getChildMerchnatById(Long merchantId);
	
	/**
	 * 查询某品牌下的所有商品 带分页
	* @Title: getGoodsCountIds 
	* @Description: TODO
	* @param @param idsCourse
	* @param @param start
	* @param @param end
	* @param @return    
	* @return List<Long>
	* @throws
	 */
	public List<Long> getGoodsCountIds(String idsCourse, int start, int end);
		
	/**
	 * 查询品牌区域名称
	 * @param merchantId
	 * @return
	 */
	public String getMerchantRegionById(Long merchantId);
	
	/**
	 * 查找优惠券
	* @Title: getCouponForShopBaoByMerchantId 
	* @Description: TODO
	* @param @param merchantId
	* @param @param top
	* @param @return    
	* @return List<CouponForm>
	* @throws
	 */
	public List<CouponForm> getCouponForShopBaoByMerchantId(Long merchantId, int top);
	
	/**
	 *
	 * @Title: getGoodsIdTotalCount
	 * @Description: 查询热销商品总个数
	 * @param 
	 * @return int
	 * @author wenjie.mai
	 */
	public int getGoodsIdTotalCount(String idsCourse);
}
