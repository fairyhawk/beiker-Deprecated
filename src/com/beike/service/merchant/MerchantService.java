package com.beike.service.merchant;

import java.util.List;
import java.util.Map;

import com.beike.action.pay.PayInfoParam;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.merchant.Merchant;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;

/**
 * <p>
 * Title:商户服务
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface MerchantService {
	
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 点菜地图信息
	 *
	 */
	public List<MerchantForm> getDiancaiChildBranchid(Long branchid);

	/**
	 * 根据id 查找商户信息
	 * 
	 * @param merchantId
	 *            商户id
	 * @return
	 */
	public Merchant getMerchantById(Long merchantId);

	/**
	 * 增加商户
	 * 
	 * @param form
	 *            商户form
	 */
	public void addMerchant(MerchantForm form);

	/**
	 * 获得某个商家 评价分数
	 * 
	 * @param merchantId
	 *            商家id
	 * @return 返回评分分数
	 */
	public Map<String, String> getMerchantEvaluationScores(Long merchantId);

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
	 * 获得商家评价次数
	 * 
	 * @param merchantId
	 * @return
	 */
	public String getEvationCount(Long merchantId);

	/**
	 * 获得订餐电话
	 * 
	 * @param merchantId
	 * @return
	 */
	public String getFixTel(Long merchantId);

	/**
	 * 获得用户购买 商家 的次数
	 * 
	 * @param merchantId
	 * @return
	 */
	public String getMerchantSalesCount(Long merchantId);

	/**
	 * 根据品牌id 查找品牌
	 * 
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getMerchantFormById(Long merchantId);

	/**
	 * 根据品牌id 查找分店信息
	 * 
	 * @param merchantId
	 *            品牌id
	 * @return
	 */
	public List<MerchantForm> getChildMerchnatById(Long merchantId, Pager pager);

	public List<MerchantForm> getChildMerchnatById(Long merchantId);

	/**
	 * 根据品牌id 获得所有分店的数量
	 * 
	 * @param merchantId
	 *            品牌id
	 * @return
	 */
	public int getChildMerchantCount(Long merchantId);

	/**
	 * 根据优惠券id 查询支持的分店
	 * 
	 * @param couponId
	 *            优惠券id
	 * @param start
	 *            起始位置
	 * @param end
	 *            个数
	 * @return
	 */
	public List<MerchantForm> getMerchantFormByCouponId(String couponId,
			Pager pager);

	public int getMerchantFormByCouponCount(Integer couponId);

	/**
	 * 根据商品ID查询商家 add by wenhua.cheng
	 * 
	 * @param merchantId
	 * @return
	 */
	public Map<String, String> getMerchantByGoodsId(Long goodsId);
	
	/**
	 * 根据商品ID查询商家分店信息 add by renli.yu
	 * 
	 * @param merchantId
	 * @return
	 */
	public List<PayInfoParam> getMerchantsByGoodsId(Long goodsId);

	/**
	 * 根据商品ID获得预约电话 add by wenhua.cheng
	 * 
	 * @param merchantId
	 * @return
	 */
	public String getFixTelByGoodsId(Long goodsId);


	
	/**
	 * 补充方法：
	 * 根据品牌ID, 来获取该品牌下商品的虚拟购买次数
	 * 
	 * 
	 * Add by zx.liu
	 */
	public Long getMerchantVirtualCount (Long merchantId);
	
	/**
	 *  查询商家数量
	* @Title: getCatlogCount 
	* @Description: TODO
	* @param @param abstractCatlog
	* @param @return    
	* @return int
	* @throws
	 */
	public int getBrandCatlogCount(AbstractCatlog abstractCatlog);
	
	/**
	 * 查询商家ID，带分页
	* @Title: getCatlog 
	* @Description: TODO
	* @param @param abstractCatlog
	* @param @param pager
	* @param @return    
	* @return List<Long>
	* @throws
	 */
	public List<Long> getBrandCatlog(AbstractCatlog abstractCatlog,Pager pager);
	
	
	/**
	 * 根据域名查找品牌id
	 * @param domainName  域名
	 * @return
	 */
	public Long getMerchantIdByDomainName(String domainName);
	
	/**
	 * 根据商家 ID查询商家分店信息 add by ljp
	 * 
	 * @param merchantId
	 * @return
	 * @date 20121128
	 */
	public List<PayInfoParam> getMerchantsByMerchantId(Long merchantId);

}