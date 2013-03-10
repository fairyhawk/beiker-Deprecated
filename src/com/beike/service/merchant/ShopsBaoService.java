package com.beike.service.merchant;

import java.util.List;
import com.beike.entity.goods.Goods;
import com.beike.form.CashCouponForm;
import com.beike.form.CouponForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;

/**      
 * project:beiker  
 * Title:
 * Description:商铺宝Service
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Oct 31, 2011 5:31:48 PM     
 * @version 1.0
 */
public interface ShopsBaoService {
	/**
	 * 通过商家ID查询商家详细信息
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getMerchantDetailById(Long merchantId);
	
	/**
	 * 通过商家ID查询商家详细信息，商铺宝使用
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getShangpubaoDetailById(Long merchantId);
	
	/**
	 * 根据品牌id获取优惠券数量
	 * @param merchantId
	 * @return
	 */
	public int getCouponCount(Long merchantId);
	
	/**
	 * 根据品牌id获取优惠券ID集合
	 * @param merchantId
	 * @param pager
	 * @return
	 */
	public List<Long> getCouponCountIds(Long merchantId, Pager pager);
	
	
	/**
	 * 查询现金券
	* @Title: getCashCoupon 
	* @Description: TODO
	* @param @param merchantid  品牌ID
	* @param @param money       金额：100、50、20
	* @param @return    
	* @return List<MerchantForm>
	* @throws
	 */
	public CashCouponForm getCashCoupon(Long merchantid,Long money);
	
	/**
	 * 查询该品牌的所有分店商品
	* @Title: getChildMerchnatById 
	* @Description: TODO
	* @param @param merchantId
	* @param @return    
	* @return List<MerchantForm>
	* @throws
	 */
	public List<MerchantForm> getChildMerchnatById(Long merchantId);
	
	/**
	 * 查询某品牌旗下的所有商品 带分页
	* @Title: getGoodsCountIds 
	* @Description: TODO
	* @param @param idsCourse
	* @param @param pager
	* @param @return    
	* @return List<Long>
	* @throws
	 */
	public List<Long> getGoodsCountIds(String idsCourse, Pager pager);
	
	/**
	 * 查询优惠券
	* @Title: getCouponListByMerchantId 
	* @Description: TODO
	* @param @param merchantId
	* @param @param top
	* @param @return    
	* @return List<CouponForm>
	* @throws
	 */
	public List<CouponForm> getCouponListByMerchantId(Long merchantId, int top);
	
	/**
	 * 查询该品牌下的置顶商品
	* @Title: getGoodsByBrandId 
	* @Description: TODO
	* @param @param merchantId
	* @param @return    
	* @return Goods
	* @throws
	 */
	public Goods getGoodsByBrandId(Long merchantId); 
	
	/**
	 * 通过商品ID查询商家详细信息
	 * @param merchantId
	 * @return
	 * @author qiaowb 2011-11-12
	 */
	public MerchantForm getMerchantDetailByGoodsId(Long goodsId);
	
	/** 
	 * @description:通过商品Id和交易Id查询商家信息
	 * @param goodsId
	 * @param trx_order_id
	 * @return MerchantForm
	 * @throws 
	 */
	public MerchantForm getCommMerchantDetail(String goodsId,String trx_order_id);
	
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
