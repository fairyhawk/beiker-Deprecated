package com.beike.dao.trx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: TrxorderGoodsDao.java
 * @Package com.beike.dao.trx
 * @Description: 订单商品明细DAO
 * @date May 16, 2011 6:53:25 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxorderGoodsDao extends GenericDao<TrxorderGoods, Long> {

	public void addTrxGoods(TrxorderGoods trxGoods);

	public void updateTrxStatusBySn(String sn, TrxStatus trxStatus, Long version)throws StaleObjectStateException;

	public void updateTrxStatusById(Long id, TrxStatus trxStatus, Long version)throws StaleObjectStateException;

	public void updateTrxAndAauthStatusById(Long id, TrxStatus trxStatus,AuthStatus authStatus, Long voucherId, Long version)throws StaleObjectStateException;

	public void updateTrxGoods(TrxorderGoods trxGoods)throws StaleObjectStateException;

	public TrxorderGoods findBySn(String trxSn);

	public TrxorderGoods findById(Long id);

	// 增加乐观锁后取消for update
	/* public TrxorderGoods findByIdForUpdate(Long id); */

	public TrxorderGoods findById(Long id, Long userId);

	public TrxorderGoods findByVoucherId(Long voucherId);

	public List<TrxorderGoods> findByTrxId(Long trxId);

	public List<Map<String, Object>> findTrxIdByUserIdAndType(Long userId,
			int startRow, int pageSize, String viewType);

	public List<TrxorderGoods> findInTrxId(String trxIdStr, String goodsIdStr);

	public int findCountByUserId(String idStr, String viewType);

	/**
	 * 取分组求和后的分页总数
	 */
	public int findPageCountByUserId(Long userId, String viewType);

	public List<TrxorderGoods> findListInId(String inIdStr);

	/**
	 * 根据时间和状态查询订单
	 */
	public List<TrxorderGoods> findByStatusAndDate(TrxStatus trxStatus,Date date,boolean isRefund,int start,int daemonLength,ReqChannel channel);

	/**
	 * 根据状态，是否发送商家验证码、是否支持退款查询
	 * 
	 * @param trxStatus
	 * @param isSendMerVou
	 * @param isRefund
	 * @return
	 */
	public List<TrxorderGoods> findByStatusAndIsMerIsRefund(TrxStatus trxStatus, Long isSendMerVou, Long isRefund);

	/**
	 * 查询本次购买数量（以商品ID分组）
	 * 
	 * @param trxId
	 * @return
	 */
	public List<Map<String, Long>> findCountByTrxId(Long trxId);

	/**
	 * 查询该交易限制下单个用户对应的抽奖活动的订单数量（该SQL执行次数极少，故联表）
	 * 
	 * @param UId
	 * @param extendInfo
	 * @param trxRuleId
	 * @return
	 */
	public Long findCountByUIdAndLottery(Long UId, String extendInfo,Long trxRuleId);

	/**
	 * 根据表后缀随机匹配查询订单号
	 * 
	 * @return
	 */
	public Map<String, String> findTrxGoodsSn(String snTbNameInt);
	
	/**
	 * 根据表后缀随机匹配查询订单号(带偏移量)
	 * @param snTbNameInt
	 * @param ofset
	 * @return
	 */
	public List<Map<String, Object>> findTrxGoodsSnForOfset(String snTbNameInt,int ofset);

	/**
	 * 根据表后缀及订单号进行删除
	 * 
	 * @return
	 */
	public void delTrxGoodsSn(String snTbNameInt, int trxGoodsId);

	public List<Map<String, Object>> findTrxOrderIdByUserID(Long userId);

	/**
	 * 查询消费未返现记录
	 * 
	 * @return
	 */
	public List<TrxorderGoods> findByDis(int isDis);
		
	/**
	 * 根据tgId更新tg TRX_STATUS状态
	 * @param idStrs
	 * 8:02:03 PM
	 * janwen
	 *
	 */
	public int updateTrxStatusByIds(String idStr,String trxStatusFinal,String trxStatusPro,Long evaluationid);
	
	/** 
	 *  0元抽奖2.0获取订单号
	* @Title: findSnByTrxId 
	* @Description: TODO
	* @param @param trxId
	* @param @return    
	* @return List<Map<String,Long>>
	* @throws 
	*/
	
	@SuppressWarnings("unchecked")
	public List findSnByTrxId(Long trxId);

	/**
	 * 短信提醒: 订单过期提醒
	 * 
	 * @param loseToNow10DateStr
	 * @param loseToNow3Date
	 * @return
	 */
	List<Map<String, Object>> findLoseDate(Date loseToNow10Date,Date loseToNow3Date);
	
	/**
	 * 手机端的分页数据总数量
	 * @param userId
	 * @param status
	 * @param sendMerVou
	 * @return
	 */
	public int findPageCountByUserIdStatus(Long userId,String status);
	
	/**
	 * 手机端的分页后数据
	 * @param userId
	 * @param startRow
	 * @param pageSize
	 * @param trxStatus
	 * @return
	 */
	public List<Map<String, Object>> findTrxorderGoodsByUserIdStatus(Long userId,int startRow,int pageSize, String trxStatus);
	
	
	/**
	 * 手机端我的订单查询
	 * @return
	 */
	public List<Map<String,Object>> findTrxorderGoodsByGoodsIdTrxorderId(String trxIdStr,String goodsIdStr);
	
	/**
	 * 根据订单ID查询58需求信息
	 * @param trxOrderId
	 * @return
	 */
	public List<Map<String,Object>> findByTrxOrderId(Long trxOrderId);
	/**
	 * 根据订单号查询订单状态
	 * @param trxorderId
	 * @param trxGoodsSn
	 * @return
	 */
	public Map<String,Object> findByTrxorderIdAndSn(Long trxorderId, String trxGoodsSn);
	/**
	 * 根据voucherId查询订单状态
	 * @param trxorderId
	 * @param voucherId
	 * @return
	 */
	public Map<String,Object> findByTrxorderIdAndVouId(Long trxorderId, Long voucherId);
	
	/**
	 * 按照更新时间和订单状态查询凭证信息
	 * @param startTime
	 * @param endTime
	 * @param userIdStr
	 * @return
	 */
	public List<Map<String, Object>> findByLastUpdateDateAndStatus(Date startTime, Date endTime,String userIdStr,String trxStatusStr);
	
	
	
	/**
	 * 查询出过期订单
	 * @param loseToNow3Date
	 * @return
	 */
	public List<Map<String, Object>> findLoseNowDate(Date loseToNowDate,int startCount,int endCount);
	
	/**
	 * 查询出过期订单  总数量
	 * @param loseToNowDate
	 * @return
	 */
	public int findLoseCountDate(Date loseToNowDate) ;
	
	public List<TrxorderGoods> findTgByVoucherId(String voucherId);
	
	/**
	 * 查询商品订单结算信息
	 */
	public List<Map<String, Object>> querySettleDetailByIds(String idStr);
	
	/**
	 * 查询出过期是否支持退款的总数量
	 * @param trxStatus
	 * @param date
	 * @param isRefund
	 * @return
	 */
	public int findByStatusAndDateCount(TrxStatus trxStatus,Date date,boolean isRefund,ReqChannel channel) ;
	
	
	/**
	 * 批量删除已经预取的订单号
	 * @param snTbNameInt
	 * @param idS
	 */
	public void delTrxGoodsSnByIds(String snTbNameInt, String idS) ;
	/**
	 * 根据更新时间和状态查询商品订单
	 * @param startTime
	 * @param endTime
	 * @param trxStatus
	 * @return
	 */
	public List<TrxorderGoods> findTrxGoodsByUpDateTimeAndStatus(String date, TrxStatus trxStatus);
	
	/**
	 * 根据trxOrderId查询券有关信息
	 * @param trxOrderId
	 * @return
	 */
	public List<Map<String,Object>> findVoucherInfoByTrxOrderId(Long trxOrderId);
	
	/**
     * 查询商品订单信息
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * trxStatus订单状态
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     * 分页参数 startRow offSet
     */
    public List<Map<String,Object>> queryTrxOrderGoodsForGuest(Map<String, String> map,int startRow,int offSet);
    
    /**
     * 查询商品订单信息数量
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     */
    public Map<String,Long> queryTrxOrderGoodsForGuestCountGroupByTrxStatus(Map<String, String> condition);
	

	/**
	 * 查询未入账的商品订单
	 * @return
	 */
	public List<TrxorderGoods> getTrxOrderGoodsByCreditStatus(String startDate,String endDate,CreditStatus creditStatus);
	/**
     * 查询商家购买数量
     * @param map
     * @return Map<String,Object>
     */
    public List<Map<String, Object>> queryTrxGoodsBuyCountForGuest(Map<String, String> map);
    
    /**
     * 查询商家消费数量
     * @param map
     * @return Map<String,Object>
     */
    public List<Map<String, Object>> queryTrxGoodsUsedCountForGuest(Map<String, String> map);
	
}
