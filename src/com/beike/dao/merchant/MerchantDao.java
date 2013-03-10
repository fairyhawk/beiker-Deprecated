package com.beike.dao.merchant;

import java.util.List;
import java.util.Map;

import com.beike.action.pay.PayInfoParam;
import com.beike.dao.GenericDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.merchant.MerchantEvaluation;
import com.beike.entity.merchant.MerchantProfileType;
import com.beike.form.MerchantForm;

/**
 * <p>
 * Title:商户数据库操作
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

public interface MerchantDao extends GenericDao<Merchant, Long> {

	
	/**
	 *  查询商家品牌的名称
	 * @param id
	 * @return
	 */
	public Merchant getMerchantNameById(Long id);
	
	
	/**
	 * lucene查询索引品牌
	 */
	public List<Merchant> getAllMerchantList();
	/**
	 * 通过商户id 查询商户
	 * 
	 * @param id
	 *            商家id
	 * @return 商家信息
	 */
	public Merchant getMerchantById(Long id);

	/**
	 * 增加商户信息
	 * 
	 * @param form
	 */
	public void addMerchant(MerchantForm form);

	/**
	 * 根据商家id 获得
	 * 
	 * @param merchantId
	 *            商家id
	 * @return 商家评分
	 */
	public Map<String, String> getEvaluationAvgScoreByMerchantId(Long merchantId);

	
	/**
	 * 获得某个商户的扩展属性
	 * 
	 * @param merchantId
	 *            商家id
	 * @param propertyname
	 *            商家扩展属性名称
	 * @return
	 */
	public MerchantProfileType getMerchantProfileTypeByMerchantId(Long merchantId, String propertyname);


	
	/**
	 * 根据品牌ID 来获取该品牌商品的 购买次数
	 * 
	 * @param merchantId
	 * @return
	 */	
	public Long getMerchantVirtualCountById (Long merchantId);
	
	

	/**
	 * 分页查询
	 * 
	 * @param idsCourse
	 * @param start
	 * @param end
	 * @return
	 */
	public List<MerchantForm> getMerchantByIds(String idsCourse);
	
	public List<MerchantForm> getMerchantSalesCount(String idsCourse);

	/**
	 * 查询品牌详细
	 * 
	 * @param merchantId
	 * @return
	 */
	public MerchantForm getMerchantDetailById(Long merchantId);

	/**
	 * 查询品牌下的 分店
	 * 
	 * @param merchantId
	 *            品牌id
	 * @return
	 */
	public List<MerchantForm> getChildMerchnatById(Long merchantId, int start,
			int end);
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 订餐分店地图
	 *
	 */
	public List<MerchantForm>  getDiancaiChildMerchant(Long branchid);
	
	public List<MerchantForm> getChildMerchnatById(Long merchantId);

	/**
	 * 获得品牌下分店数量
	 */
	public int getChildMerchantCount(Long merchantId);

	/**
	 * 根据优惠券id 查询所支持的商家
	 * 
	 * @param couponId
	 * @return
	 */
	public List<MerchantForm> getMerchantFormByCouponId(Integer couponId,
			int start, int end);

	/**
	 * 获得优惠券id 查询所支持商家的个数
	 */
	public int getMerchantFormByCouponCount(Integer couponId);

	/**
	 * 根据商品ID查询商家 add by wenhua.cheng
	 * 
	 * @param merchantId
	 * @param propertyname
	 * @return
	 */
	public Map<String, String> getMerchantByGoodsId(Long goodsId);
	/**
	 * 根据商品ID查询商家预约电话和地址 add by renli.yu
	 * 
	 * @param merchantId
	 * @param propertyname
	 * @return
	 */
	public List<PayInfoParam> getMerchantsByGoodsId(Long goodsId);

	/**
	 * 根据商户ID查询预约电话 add by wenhua.cheng
	 * 
	 * @param goodsId
	 * @param propertyname
	 * @return
	 */
	public MerchantProfileType getMerchantProfileTypeByGoodsId(Long goodsId,
			String propertyname);

	/**
	 * 增加商品的评价 add by wenhua.cheng
	 * 
	 * @param userId
	 * @param merchantid
	 * @param commentPoint
	 * @param commentContent
	 * @return
	 */
	public Long addEvaluation(final Long userId, final Long merchantid,
			final double commentPoint, final String commentContent,
			final Long goodsId, final Long trxGoodsId);

	/**
	 * 根据GoodsId读取个人评价
	 * 
	 * @param goodsId
	 * @return
	 */
	public Map<String, String> getEvaluationByGoodsId(Long goodsId,
			Long trxGoodsId);

	public void updateMerchantSalesCount(Long merchantId);
	
	public int checkMerchantAcgScore(String profileName, Long merchantId);
	
	public void updateMerchantAvgScore(Double score, String profileName, Long merchantId);
	
	public void insertMerchantAvgScore(Double score, String profileName, Long merchantId);
	
	public MerchantEvaluation findAvgScoreByMerchantId(Long merchantId);
	
	/**
	 * 查询商家个数
	* @Title: searchBrandCatlogCount 
	* @Description: TODO
	* @param @param abstractLog
	* @param @return    
	* @return int
	* @throws
	 */
	public int searchBrandCatlogCount(AbstractCatlog abstractLog);
	
	/**
	 * 查询商家ID 带分页
	* @Title: searchCatlog 
	* @Description: TODO
	* @param @param abstractLog
	* @param @param start
	* @param @param end
	* @param @return    
	* @return List<Long>
	* @throws
	 */
	public List<Long> searchBrandCatlog(AbstractCatlog abstractLog,int start,int end);
	/**
	 * 
	 * @author janwen
	 * @time Jan 9, 2012 1:04:49 PM
	 *
	 * @return 获得所有品牌id
	 */
	public List<Long> getAllMerchantID();
	
	/**
	 * lucene搜索确认品牌是否有已经上架商品
	 * @param merchantid
	 * @return
	 */
	public boolean checkMerchantStatus(Long merchantid);
	
	
	/**
	 * 获得merchantid
	 * @param domainName
	 * @return
	 */
	public Long getMerchantIdByDomainName(String domainName);
	
}
