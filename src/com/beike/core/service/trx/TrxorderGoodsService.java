package com.beike.core.service.trx;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.entity.trx.MenuGoodsOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: TrxorderGoodsService.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 订单商品CORE service
 * @date May 17, 2011 6:28:57 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxorderGoodsService {
	/**
	 * 商品订单创建
	 * @param trxorderGoods
	 * @throws TrxorderGoodsException
	 */
	public Long create(TrxorderGoods trxorderGoods)	throws TrxorderGoodsException;
	/**
	 * 商品订单鉴权
	 * @param trxgoodsId
	 * @param userId
	 * @param isVerify  是否需要鉴权(
		如果在外部进行了鉴权。则此处忽略。避免多次查询和重构成本（wap和web的我的订单都调到了此方法，分销商传进的参数是userIdList）)
	 * @return
	 */
	public boolean verifyBelong(Long trxgoodsId, Long userId,boolean isVerify);

	/**
	 * 根据单个商品加入评价
	 * 
	 * @throws VoucherException
	 * @throws TrxorderGoodsException
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 * @throws StaleObjectStateException
	 */
	public Map<String, String> addComment(Long trxGoodsId, Long userId,double commentPoint, String commentContent)throws ProcessServiceException,
			RebateException, AccountException,TrxOrderException, PaymentException, TrxorderGoodsException,VoucherException, StaleObjectStateException;

	/**
	 * 查询符合要求的交易ID，并进行组合处理
	 * 
	 * @param userId
	 * @param startRow
	 * @param pageSize
	 * @param viewType
	 * @return
	 */
	public List<List<TrxorderGoods>> listPageByUserIdAndType(Long userId,int startRow, int pageSize, String viewType);

	/**
	 * 注入商品信息
	 */

	public List<List<TrxorderGoods>> injectGoodsInfo(List<List<TrxorderGoods>> sourceList, String viewType);

	/**
	 * 根据时间和是否退款查询过期的订单
	 * 
	 * @param date
	 * @return
	 */
	public List<TrxorderGoods> qryLoseListByIsRefund(Date date, boolean isRefund,int start,int daemonLength);
	
	
	/**
	 * 获取总量超限是否退款商品订单ListMap
	 * @param overRunTgList
	 * @param type
	 * @return
	 */
	public  Map<String,List<TrxorderGoods>>  processGetTotalOverRfdTgListMap(List<TrxorderGoods> unSingleOvRunTgList);
		
	/**
	 * 判断是否总量限购超限
	 * 
	 * @param trxorderGoods
	 * @return
	 */
	public boolean isCountLimit(TrxorderGoods trxorderGoods);
	
	
	/**
	 * 秒杀商品总量限购，并且更新秒杀商品库存
	 */
	public List<TrxorderGoods> processTotalCountLimitForMiaoSha(List<TrxorderGoods> unSingleOverRunTgList);
	
	/**
	 * 查询交易订单商品信息，需要从写库读取数据
	 * 
	 * @param trxid
	 *            交易订单号
	 * @return
	 * @author qiaowb 2010-10-25
	 */
	public List<TrxorderGoods> preQryInWtDBFindByTrxId(long trxid);

	/**
	 * 取分组求和后的分页总数
	 */
	public int findPageCountByUserId(Long userId, String viewType);

	public int findCountByUserId(Long userId, String viewType);

	public TrxorderGoods findById(Long id);

	public TrxorderGoods findById(Long id, Long userId);

	/**
	 * 查询消费未返现记录
	 * 
	 * @return
	 */
	public List<TrxorderGoods> findByDis();

	/**
	 * 返现补发
	 * 
	 * @param trxorderGoods
	 * @throws NumberFormatException
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void rebateDaemon(TrxorderGoods trxorderGoods)throws VmAccountException, AccountException,StaleObjectStateException;
	
	/**
	 * 根据tgId单笔或批量评价
	 * @param tgIdSet
	 * 7:57:32 PM
	 * janwen
	 * @throws StaleObjectStateException 
	 *
	 */
	public void commentByTgId(Set<Long> tgIdSet,Long evaluationid) throws StaleObjectStateException;
	
	

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
	 * 0元抽奖2.0 查询订单号
	 * 
	 * @Title: findGonTrxGoodsSnByTrxId
	 * @Description: TODO
	 * @param @param trxId
	 * @param @return
	 * @return List
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List findGonTrxGoodsSnByTrxId(Long trxId);

	/**
	 * 手机客户端的我的订单分页数据总数量
	 * 
	 * @param userId
	 * @param trxStatus
	 * @return
	 */
	public int findPageCountByUserIdStatus(Long userId, String trxStatus);

	/**
	 * 手机客户端查询出我的订单分页后数据
	 * 
	 * @param userId
	 * @param startRow
	 * @param pageSize
	 * @param trxStatus
	 * @return
	 */
	public TrxResponseData findTrxorderGoodsByUserIdStatus(Long userId,int startRow, int pageSize, String trxStatus);
	
	
	/**
	 * 根据trxOrderId查询（主库查询）
	 * @param trxOrderId
	 * @return
	 */
	public  List<Map<String, Object>>  preQryInWtDBByByTrxId(Long trxOrderId);
	
	/**
	 * 过期是否退款总数量
	 * @param date
	 * @param isRefund
	 * @return
	 */
	public int qryLoseListByIsRefundCount(Date date, boolean isRefund);

	/**
	 * 根据trxOrderId查询券有关信息
	 * @param trxOrderId
	 * @return
	 */
	public List<Map<String,Object>> findVoucherInfoList(Long trxOrderId);
	/**
     * 更新 入账状态为INIT,结算状态如果为 UNSETTLE改为SETTLEED
     */
	//public boolean  updateTrxOrdrGoodsCreditStatusInit(Long trxGoodsId);
	/**
     * 更新 入账状态为SUCCESS
	 * @throws TrxorderGoodsException 
	 * @throws StaleObjectStateException 
     */
	public void  updateTrxOrdrGoodsCreditStatusSuccess(TrxorderGoods trxorderGoods) throws TrxorderGoodsException, StaleObjectStateException;
	

	/**
	 * 根据voucherId来查询
	 * @param voucherId
	 * @return
	 */
	public TrxorderGoods findByVoucherId(Long voucherId);
	
	/**
	 * 查询点餐订单详情
	 * @param trxGoodsId
	 * @param guestId
	 * @return
	 */
	public List<MenuGoodsOrder> queryMenuGoodsOrderList(Long trxGoodsId);
	
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
	public List<Map<String, Object>> queryTrxOrderGoodsForGuest(Map<String, String> map, int startRow, int pageSize) ;
	
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
	public Map<String,Long> qryTrxOrderGoodsForGuestCount(Map<String, String> condition);

	/**
	 * 查询订单信息
	 * @param idStr
	 * @return
	 */
	public List<Map<String, Object>> querySettleDetailById(String idStr);
	

	/**
     * 根据trxGoodsId查询（主库查询）
     * @param trxGoodsId
     * @return
     */
    public  TrxorderGoods  preQryInWtDBByByTrxGoodsId(Long trxGoodsId);
    
    /**
     * 查询未入账的商品订单
     * @return
     */
    public  List<TrxorderGoods> qryTrxOrderGoodsByCreditStatus(String startDateStr,String endDateStr,CreditStatus creditStatus);
    /**
     * 查询商家购买数量
     * @param map
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> queryTrxGoodsBuyCountForGuest(Map<String, String> map);
    
    /**
     * 查询商家消费数量
     * @param map
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> queryTrxGoodsUsedCountForGuest(Map<String, String> map);

}
