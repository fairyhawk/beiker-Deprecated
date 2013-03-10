package com.beike.core.service.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PayRspInfo;
import com.beike.common.bean.trx.TrxDataInfo;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: TrxorderService.java
 * @Package com.beike.core.service.trx
 * @Description: 交易Trx订单service
 * @date May 17, 2011 2:49:58 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxOrderService {
	public TrxOrder create(TrxOrder trxOrder) throws TrxOrderException;

	


	/**
	 * 余额触发
	 * @param orderInfo
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws TrxorderGoodsException
	 */
	public TrxDataInfo comleteByBalance(OrderInfo orderInfo) throws TrxOrderException,PaymentException, AccountException, VoucherException,
			RuleException, StaleObjectStateException, ProcessServiceException,RebateException, TrxorderGoodsException,CouponException;

	/**
	 * 支付后续处理
	 * @param payRspInfo
	 * @return
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws TrxorderGoodsException
	 */
	public TrxDataInfo achieveByPayAfter(PayRspInfo payRspInfo)throws PaymentException, TrxOrderException, AccountException,VoucherException, 
			RuleException, StaleObjectStateException,ProcessServiceException, RebateException, TrxorderGoodsException,CouponException;
	
	
	/**
	 * 账户出入帐
	 * @param paymentList
	 * @param trxOrder
	 * @param trxGoodsList
	 * @param accountList
	 * @param bizType
	 * @throws RuleException 
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws AccountException 
	 * @throws StaleObjectStateException

	 */
	public void completeActHis(List<Payment> paymentList,TrxOrder trxOrder, List<TrxorderGoods> trxGoodsList,List<Account> accountList,TrxCoupon coupon, String bizType) 
	throws RuleException, AccountException, StaleObjectStateException,CouponException;




	/**
	 * 支付完成后的订单相关操作
	 * @param paymentList
	 * @param trxOrder
	 * @param trxGoodsList
	 * @return
	 * @throws StaleObjectStateException 
	 * @throws RuleException 
	 * @throws TrxorderGoodsException 
	 * @throws PaymentException 
	 * @throws TrxOrderException 
	 * @throws AccountException 
	 * @throws RebateException 
	 * @throws ProcessServiceException 
	 * @throws VoucherException 
	 */
	public TrxDataInfo   completeTrxOrderAndTg(List<Payment> paymentList,TrxOrder trxOrder, List<TrxorderGoods> trxGoodsList,String bizType) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException, RuleException, StaleObjectStateException;
	

	

	
	/**
     *在写库中进行网银支付完成后出入帐前的预查询
	 * @param payRspInfo
	 * @return
	 * @throws TrxorderGoodsException 
	 * @throws TrxOrderException 
	 */
	public PayRspInfo   preQryInWtDBForCash(PayRspInfo payRspInfo) throws TrxorderGoodsException, TrxOrderException;
	
	/**
     *在写库中进行商品订单查询
	 * @param payRspInfo
	 * @return
	 * @throws TrxorderGoodsException 
	 */
	public List<TrxorderGoods>   preQryInWtDBForTg(Long trxOrderId) throws  TrxorderGoodsException;

	/**
	 * 根据商品订单Id获取Trxorder
	 * @param <TrxOrder>
	 * @param tgId
	 */
	public TrxOrder  findByTgId(Long tgId);

	/**主键ID查询TrxOrder
	 * @param id
	 * @return
	 */
	public TrxOrder  findById(Long id);
	
	
	/**
	 * 根据outRequestId和userIdList查询Trxorder（主库查询）
	 * @return
	 */
	public  TrxOrder  preQryInWtDBByUIdAndOutReqId(String  outRequestId,List<Long> userIdList);
	
	public  TrxOrder  preQryInWtDBByUIdAndOutReqId(List<Long> userIdList);
	
	public List<Map<String,Object>> getTrxGoodsByIds(Map<String, String> map );
}
