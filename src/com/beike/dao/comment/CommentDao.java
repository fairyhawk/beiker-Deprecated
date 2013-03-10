package com.beike.dao.comment;

import java.util.List;
import java.util.Map;

public interface CommentDao {

	
	/**
	 * 
	 * @param trx_order_id
	 * @param goodsid
	 * @return 是否存在该为评价订单
	 * 6:04:32 PM
	 * janwen
	 *
	 */
	public int findTrxOrder(Long trx_order_id,Long goodsid);
	
	/**
	 * 
	 * @param trx_order_id
	 * @param userid
	 * @return 是否为该用户订单
	 * 6:07:56 PM
	 * janwen
	 *
	 */
	public int findUserid(Long trx_order_id,Long userid); 
	/**
	 * 
	 * @param goodsid
	 * @param id 订单id
	 * @param batch 批量更新
	 * @return 通过订单id,商品id查询相关信息
	 * 9:37:24 PM
	 * janwen
	 *
	 */
	public List getTrxOrderInfo(boolean batch,Long goodsid,Long id,Long trx_order_id);
	
	/**
	 * 
	 * @param batch
	 * @param trx_order_id
	 * @param id
	 * @param goodsid
	 * @return 从订单中心跳转到评论页面的相关信息
	 * 10:47:00 AM
	 * janwen
	 *
	 */
	public Map<String,Object> gotoCommentPage(boolean batch,Long trx_order_id,Long id,Long goodsid);
	/**
	 * 只负责添加评论
	 * @param score 0很好,1满意,2差
	 * @return 评价id
	 */
	public Long addComment(int isdefault,int score,String comment,Long merchantID,Long userid,Long goodsid,Long trxorderid,int ordercount);
	/**
	 * 添加评论图片
	 * @param photourl
	 * @return
	 */
	public int addCommentPhoto(List<String> photourl,Long evaluationid);
	
	/**
	 * 更新商家/品牌评分
	 * mc_score不能小于0
	 * @param merchantid
	 * @param newscore 分数需要乘以订单数量
	 * @param mc_well_count
	 * @param mc_satisfy_count
	 * @param mc_poor_count
	 * @return
	 */
	public int updateMerchanProfile(Long merchantid,int newscore,int mc_well_count,int mc_satisfy_count,int mc_poor_count);
	
	/**
	 *  更新分店好评
	 * @param merchantID
	 * @param branchID branchid branchid=0在service校验,符合条件才call该方法
	 * @param well_count
	 * @param statisfy_count
	 * @param poor_count
	 * @return
	 */
	public int updateBranchProfile(Long merchantID,Long branchID,int well_count,int satisfy_count,int poor_count);
	
	/**
	 * 更新分店与评论关系
	 * @param branchid branchid=0在service校验,并传递
	 * @param evaluationid
	 * @param ordercount 每次参与评价订单数
	 * @return
	 */
	public int updateBranchEvaluation(Long branchid,Long evaluationid,int ordercount);
	
	/**
	 * 更新商品好评数量
	 * @param well_count
	 * @param satisfy_count
	 * @param poor_count
	 * @return
	 * 4:18:46 PM
	 * janwen
	 *
	 */
	public int updateGoodsProfile(Long goodsid,int well_count,int satisfy_count,int poor_count);
	
	
	/**
	 * 
	 * @param goodsid
	 * @return 推荐商品id,二级分类属性
	 * 2:24:48 PM
	 * janwen
	 *
	 */
	public List<Long> getRecommendGoodsidBySec(Long goodsid);
	/**
	 * 
	 * @param goodsid
	 * @param rest  二级分类不足的数量
	 * @return 推荐商品id,一级分类属性
	 * 2:26:37 PM
	 * janwen
	 *
	 */
	public List<Long> getRecommendGoodsidByFir(Long goodsid,List<Long> notingoodsid,int rest,Long area_id);
	/**
	 * 查询商家评价次数
	 * @param userId
	 * @param merchantId
	 * @param score 评价打分
	 * @return
	 */
	public int getEvaluateMerchantCountById(Long userId, Long merchantId, int score);
	
	/**
	 * 查询商家评价ID
	 * @param userId
	 * @param merchantId
	 * @param pager
	 * @param score 评价打分
	 * @return
	 */
	public List<Long> getEvaluateMerchantID(Long userId, Long merchantId, int start, int end, int score);
	
	/**
	 * 查询商家评价信息
	 * @param idList
	 * @return
	 */
	public List<Map<String,Object>> getEvaluationInfoByIds(List<Long> idList);
	
	/**
	 * 查询评价信息的商品名称和品牌名称
	 * 避免关联sql过多，所以分开查询
	 * @param ids
	 * @return
	 */
	public List<Map<String,Object>> getEvaluateGoodNameAndMerName(List<Long> idList);
	
	/**
	 * 查询商家各评价信息
	 * @param merchangId
	 * @return
	 */
	public List<Map<String,Object>> getAllEvaluationForMerchant(Long merchangId);
	
	/**
	 * 查询expiredDay天前消费为评价的订单
	 * @param expiredDay
	 * @return
	 */
	public List<Map<String,Object>> queryExpiredNoComment(int expiredDay);
	
	/**
	 * 查询所有评价的图片
	 * @param ids
	 * @return
	 */
	public List<Map<String,Object>> getEvaluatePhoto(List<Long> ids);
	
	/**
	 * 查询分店评价次数
	 * @param userId
	 * @param brandid
	 * @param score 评价打分
	 * @return
	 */
	public int getEvaluateBrandCountById(Long userId, Long brandid, int score);
	
	/**
	 * 查询分店评价ID
	 * @param userId
	 * @param brandid
	 * @param start
	 * @param end
	 * @param score
	 * @return
	 */
	public List<Long> getEvaluateBrandID(Long userId, Long brandid, int start, int end, int score);
	
	/**
	 * 查询分店评价分数
	 * @param brandid
	 * @return
	 */
	public List<Map<String,Object>> getAllEvaluateForBrand(Long brandid);
	
	/**
	 * 查询商家旗下商品的一级分类
	* @Description: 
	* @author wenjie.mai
	* @return 返回类型 List    
	* @throws
	 */
	public List<Map<String,Object>> getCatlogGoodByBrandid(Long merchantid);
	
	/**
	 * 
	 * @param goodsid
	 * @return 商品areaid
	 */
	public Long getAreaIDByGoodsid(Long goodsid);
	
	/**
	 * 
	 * @param area_id
	 * @param notin 不包括
	 * @return 当前城市商品可售商品id
	 */
	public List<Long> getGoodsidByAreaid(Long area_id,List<Long> notin);
	
	/**
	 * 
	 * @param availableGoodsid
	 * @return
	 */
	public List<Long> getTopSalesGoodsID(List<Long> availableGoodsid);
	
	/**
	 * 商品评价次数
	 * @param user
	 * @param goodId
	 * @param score
	 * @return
	 */
	public int getEvaluateGoodCountById(Long userId, Long goodId,int score);
	
	/**
	 * 查询商品被评价ID
	 * @param user
	 * @param goodId
	 * @param start
	 * @param end
	 * @param score  评价打分
	 * @return
	 */
	public List<Long> getEvaluateGoodsId(Long userId,Long goodId,int start,int end,int score);
	/**
	 * 查询商品被评价ID
	 * @param user
	 * @param goodId
	 * @param start
	 * @param end
	 * @param score  评价打分
	 * @return
	 */
	public Integer getEvaluateGoodCountById(Long goodId);
	
	/**
	 * 查询其它商品的评价信息
	 * @param ids
	 * @return
	 */
	public List<Long> getOtherEvaluateGoodsId(String ids);
	
	/**
	 * 统计该商品的 很好、满意、差
	 * @param goodId
	 * @return
	 */
	public List<Map<String,Object>> getAllEvaluationForGoodById(Long goodId);
	
	/** 
	 * @description：通过分店Id分页查询当前分店下面的评价Id（很好满意的评价+5%差评）
	 * @param merchantIdList
	 * @param score
	 * @param count
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getEvaluationIdByMerchantId(List<Long> merchantIdList,int score,int count);
	
	/** 
	 * @description:通过商品Id和交易Id查询购买数量
	 * @param trxorder_id
	 * @param goodsId
	 * @return int
	 * @throws 
	 */
	public int getOrderCountByTrxId(Long trxorder_id,Long goodsId); 
}
