package com.beike.service.comment;

import java.util.List;
import java.util.Map;

import com.beike.common.exception.StaleObjectStateException;
import com.beike.entity.merchant.BranchProfile;
import com.beike.form.MerchantForm;
import com.beike.form.OrderEvaluationForm;
import com.beike.page.Pager;

/**
 * Action直接调用该Service,其它事务Service包含到该Service
 * @author janwen
 *
 */
public interface CommentService {
	
	
	/**
	 * 
	 * @param trx_order_id
	 * @param goodsid
	 * @param userid
	 * @return 验证评价订单合法性
	 * 6:15:09 PM
	 * janwen
	 *
	 */
	public boolean isvalid(Long trx_order_id,Long goodsid,Long userid);
	/**
	 * 查询dayCount天内将过期的余额合计
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public double getRemindAccountBalance(Long userId, String dayCount);
	
	/**
	 * 查询商家被评价次数
	 * @param user
	 * @param merchantId
	 * @param score 评价打分
	 * @return
	 */
	public int getEvaluateMerchantCount(Long userId, Long merchantId,int score);
	
	/**
	 * 查询商家评价ID
	 * @param user
	 * @param merchantId
	 * @param pager
	 * @param score
	 * @return
	 */
	public List<Long> getEvaluateMerchantID(Long userId, Long merchantId,Pager pager,int score);
	
	/**
	 * 查询商家评价信息
	 * @param idList
	 * @return
	 */
	public List<OrderEvaluationForm> getEvaluationInfoByIds(List<Long> idList);
		
	/**
	 * 
	 * @param batch
	 * @param trx_order_id
	 * @param id
	 * @param goodsid
	 * @return 订单中心跳转到评论页面相关信息查询{comment_total,branches,total_order}
	 * 10:39:11 AM
	 * janwen
	 *
	 */
	public Map<String,Object> gotoCommentPage(boolean batch,Long trx_order_id,Long id,Long goodsid);
	
	/**
	 * @param isdefault 0否,1是
	 * @param score 0很好,1满意,2差
	 * @param comment
	 * @param merchantID
	 * @param userid
	 * @param goodsid
	 * @param trxorderid
	 * @param photourl
	 * @param well_count
	 * @param satisfy_count
	 * @param poor_count
	 * @param branchCountMap<分店id,数量>注意键值重复
	 * @param trx_order_id trx_goods_order主键id
	 * @return
	 * @throws RuntimeException
	 * 5:17:44 PM
	 * janwen
	 * @throws StaleObjectStateException 
	 *
	 */
	public boolean addComment(int isdefault,Long id,boolean batch,int score,String comment,Long merchantID,Long userid,Long goodsid,Long trxorderid,List<String> photourl,int well_count,int satisfy_count,int poor_count) throws Exception;

	/**
	 * 
	 * @param goodsid
	 * @return 推荐商品id
	 * 7:11:55 PM
	 * janwen
	 *
	 */
	public List<Long> getRecGoodsid(Long goodsid);
	
	/**
	 * 订单消费30天未评价，定时默认评价
	 */
	public int getEvaluateBrandCount(Long userId, Long brandid,int score);
	
	/**
	 * 查询分店评价ID
	 * @param user
	 * @param brandid
	 * @param pager
	 * @param score 评价打分
	 * @return
	 */
	public List<Long> getEvaluateBrandID(Long userId, Long brandid,Pager pager,int score);
	
	/**
	 * 查询分店评价分数
	 * @param brandid
	 * @return
	 */
	public BranchProfile getAllEvaluateForBrand(Long brandid);
	
	/**
	 * 查询expiredDay天前消费为评价的订单
	 * @param expiredDay
	 * @return
	 */
	public List<Map<String,Object>> queryExpiredNoComment(int expiredDay);
	
		
	/**
	 * 查询商家旗下商品的一级分类
	* @Description: 
	* @author wenjie.mai
	* @return 返回类型 List<OrderEvaluationForm>    
	* @throws
	 */
	public List<MerchantForm> getMerchantProfile(Long merchantid);
	
	/**
	 * 查询goodId被评价次数
	 * @param user
	 * @param goodId
	 * @param merchantId
	 * @return
	 */
	public int getEvaluateGoodCount(Long userId,Long goodId,int score);
	/**
	 * 查询goodId被评价次数
	 * @param user
	 * @param goodId
	 * @param merchantId
	 * @return
	 */
	public Integer getEvaluateGoodCount(Long goodId);
	
	/**
	 * 
	 * @param area_id
	 * @param notin 不包括id
	 * @return 当前城市24销售销量最好的goodsid
	 */
	public List<Long> getTopSaledGoodsid(Long area_id,List<Long> notin);
	
	/**
	 * 获得该商品的所有评价
	 * @param user
	 * @param goodId
	 * @param pager
	 * @param score 评价打分
	 * @return
	 */
	public List<Long> getEvaluateGoodID(Long userId,Long goodId,Pager pager,int score);
	
	/**
	 * 获得其它商品的所有评价
	 * @param user
	 * @param goodId
	 * @param merchantId
	 * @param pager
	 * @return
	 */
	public List<Long> getEvaluateGoodOtherID(Long merchantId);
	
	/**
	 * 统计该商品的 很好、满意、差
	 * @param goodId
	 * @return
	 */
	public Map<String,Integer> getAllEvaluationForGood(Long goodId);
	
	/** 
	 * @description：通过分店Id查询分店下面的评价Id（很好满意的评价+5%差评）
	 * @param merchantIdList
	 * @return List<Long>
	 * @throws 
	 */
	public List<Long> getEvaluationIdByMerchantId(List<Long> merchantIdList);
	
}
