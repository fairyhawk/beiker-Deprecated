package com.beike.biz.service.hessian;

import java.util.List;
import java.util.Map;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CardException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.DiscountCouponException;
import com.beike.common.exception.OrderCreateException;
import com.beike.common.exception.PartnerException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RefundException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.ShoppingCartException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VerifyBelongException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: TrxHessianService.java
 * @Package com.beike.biz.service.hessian
 * @Description: 对其它模块提供的交易hessian
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 7:58:06 PM
 * @version V1.0
 */
public interface TrxHessianService {

	/**
	 * 账户创建
	 * 
	 * @param requestData
	 * @return
	 * @throws AccountException
	 */
	public void createAccount(TrxRequestData requestData)
			throws AccountException;

	/**
	 * 返现--弃用
	 * 
	 * @param userId
	 * @param rebateCount
	 * @return
	 * @throws RebateException
	 */
	// public Map<String, String> rebate(Map<String, String> sourceMap);

	/**
	 * 账户查询
	 * 
	 * @param requestData
	 * @return
	 * @throws AccountException
	 */
	public TrxResponseData getActByUserId(TrxRequestData requestData)
			throws AccountException;

	/**
	 * 支付机构查询接口
	 * 
	 * @param requestData
	 * @return
	 * @throws Exception
	 */
	public TrxResponseData qryPayStauts(TrxRequestData requestData)
			throws Exception;



	/**
	 * 创建交易订单(此方法不需事务，方法名以noTsc打头避开事务)
	 * 
	 * @param sourceMap
	 * @return
	 * @throws OrderCreateException
	 * @throws StaleObjectStateException
	 * @throws RuleException
	 * @throws VoucherException
	 * @throws TrxorderGoodsException
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 */
	public OrderInfo noTscCreateTrxOrder(OrderInfo orderInfo)
			throws ProcessServiceException, RebateException, AccountException,
			TrxOrderException, PaymentException, TrxorderGoodsException,
			VoucherException, RuleException, StaleObjectStateException,
			OrderCreateException,CouponException;

	/**
	 * 完成交易(此方法不需事务，方法名以noTsc打头避开事务)
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData noTscCompleteTrx(TrxRequestData requestData) throws PaymentException, TrxOrderException, AccountException, VoucherException, RuleException, StaleObjectStateException, ProcessServiceException, RebateException, TrxorderGoodsException,CouponException;

	/**
	 * 根据订单商品明细ID返现----弃用
	 * 
	 * @param requestData
	 * @return
	 */
	// public TrxResponseData rebatebyTrxGoodsId(TrxRequestData requestData);

	/**
	 * 根据用户帐号进行返现--弃用
	 * 
	 * @param sourceMap
	 * @return
	 */
	// public Map<String, String> rebatebyUserId(Map<String, String> sourceMap);

	/**
	 * 账户退款申请
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundApplyToAct(TrxRequestData requestData)
			throws RefundException, StaleObjectStateException;

	/**
	 * 账户退款拒绝
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundRefuseToAct(TrxRequestData requestData)
			throws RefundException, StaleObjectStateException;

	/**
	 * 退款到账户
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundToAct(TrxRequestData requestData) throws RefundException,
			AccountException, VoucherException, RuleException,
			StaleObjectStateException, VmAccountException,CouponException;

	/**
	 * 银行卡退款申请
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundApplyToBank(TrxRequestData requestData)
			throws RefundException, AccountException, StaleObjectStateException;

	/**
	 * 银行卡退款拒绝
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundRefuseToBank(TrxRequestData requestData)
			throws RefundException, StaleObjectStateException;

	/**
	 * 退款到银行卡
	 * 
	 * @param requestData
	 * @return
	 */
	public void refundToBank(TrxRequestData requestData)
			throws RefundException, AccountException, StaleObjectStateException;

	/**
	 * 人工创建凭证--弃用
	 * 
	 * @return
	 */
	// public TrxResponseData createVoucher(TrxRequestData requestData);

	/**
	 * 销毁凭证
	 * 
	 * @param requestData
	 * @return
	 */
	public void  destoryVoucherByID(TrxRequestData requestData) throws VoucherException,StaleObjectStateException;

	/**
	 * 根据客户号和凭证内容校验凭证及回收
	 * 
	 * @param requestData
	 * @return
	 * @throws PartnerException 
	 */
	public void checkVoucher(TrxRequestData requestData) throws VoucherException, ProcessServiceException, RebateException,
	AccountException, TrxOrderException, PaymentException,
	TrxorderGoodsException, RuleException, StaleObjectStateException, PartnerException ;
	
	/**
	 * 根据凭证码和分店编号查询订单信息
	 * @param requestData
	 * @return
	 */
	public TrxResponseData qryOrderByVouCodeAndGuestId(TrxRequestData requestData) throws VoucherException,TrxorderGoodsException,NumberFormatException;

	public Map<String,String> getQryTrxOrderGoodsForGuestCondition(Map<String,String> sourceMap) throws Exception;
	public Map<String,String> getQueryTrxGoodsByIdsCondition(Map<String,String> sourceMap) throws Exception;
	
	/**
     * 查询商品订单信息数量
     */
	public Map<String,String> qryTrxOrderGoodsForGuestCount(Map<String, String> condition);
	
	/**
	 * 根据商品订单Id查询商品订单结算信息
	 * @param trxOrderGoodsIds
	 * @return
	 */
	public List<Map<String,Object>> qryTrxOrderGoodsDetailForSettle(String trxOrderGoodsIds);
	
	
	/**
     * 查询商家交易订单
     */
    public List<Map<String,Object>> qryTrxorderGoodsForGuest(Map<String, String> condition);
    
    /**
     * 根据商家编号和商品订单id查询商品详情信息
     * @param requestData
     * @return
     */
    public Map<String,Object> qryOrderDetailByGuestIdAndTgId(TrxRequestData requestData)throws TrxorderGoodsException;
    
	/**
	 * 重发凭证
	 * 
	 * @param requestData
	 *            (trxOrderGoodsId,sendType) sendType:EMAIL/SMS/BOTH
	 * @return
	 */
	public void reSendVoucher(TrxRequestData requestData)
			throws TrxorderGoodsException, VoucherException,
			VerifyBelongException, Exception;

	/**
	 * 个人限购支付前校验--弃用.整合到下单接口
	 * 
	 * @param requestData
	 * 
	 * @return
	 */
	//public TrxResponseData validatePayLimit(TrxRequestData requestData) throws PayLimitException;

	/**
	 * 创建虚拟款项账户
	 * 
	 * @return
	 * @throws TrxOrderException 
	 */
	public TrxResponseData createVmAccount(TrxRequestData requestData) throws AccountException, VmAccountException, NumberFormatException,
	StaleObjectStateException, TrxOrderException ;

	/**
	 * 往虚拟款项账户追加余额
	 * 
	 * @return
	 */
	public void pursueVmAccount(TrxRequestData requestData) throws AccountException, NumberFormatException, VmAccountException,
	StaleObjectStateException;

	/**
	 * 下发虚拟款项
	 * 
	 * @return
	 */
	public void dispatchVm(TrxRequestData requestData) throws VmAccountException, NumberFormatException, AccountException,
	StaleObjectStateException ;

	/**
	 * 千品卡查询
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData queryCardInfo(TrxRequestData requestData)
			throws CardException;

	/**
	 * 千品卡充值
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData topupCard(TrxRequestData requestData)
			throws CardException, StaleObjectStateException, AccountException,
			VmAccountException;

	
	/**
	 * 线上活动自动绑定优惠券
	 * @param sourceMap
	 * @return
	 */
	public TrxResponseData autoBindCoupon(TrxRequestData requestData) throws CouponException,StaleObjectStateException;
	
	/**
	 * 优惠券激活
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData activateCoupon(TrxRequestData requestData)throws DiscountCouponException, 
		CouponException,StaleObjectStateException, AccountException,VmAccountException;
	
	/**
	 * 我的钱包
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData queryPurse(TrxRequestData requestData)throws BaseException, Exception ;

	/**
	 * 总量限购支付前校验--弃用。整合到下单接口
	 * 
	 * @param requestData
	 * @return
	 */
	//public TrxResponseData validateTotalLimit(TrxRequestData requestData);
	
	

	/**
	 * 查看购物车列表
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData qryShoppingCart(TrxRequestData requestData);

	/**
	 * 添加购物车
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData addShoppingCart(TrxRequestData requestData)
			throws ShoppingCartException;

	/**
	 * 删除购物车
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData delShoppingCartById(TrxRequestData requestData)
			throws ShoppingCartException;

	/**
	 * 查看商品订单列表
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData qryTrxorderGoodsByUserId(TrxRequestData requestData)
			throws Exception;

	/**
	 * 查看凭证密码
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData qryVoucherByTgId(TrxRequestData requestData)
			throws VerifyBelongException, TrxorderGoodsException, Exception;
	
	/**
	 * 查询子账户金额信息
	 * @param requestData
	 * @return
	 * @throws AccountException
	 */
	public TrxResponseData getSubAccountByUserId(TrxRequestData requestData) throws AccountException, StaleObjectStateException;

	/**
     * 商品订单查询
     */
    public List<Map<String,Object>> getTrxGoodsByIds(Map<String, String> map);
    
    /**
     * 查询商家购买数量
     * @param map
     * @return Map<String,Object>
     */
    public Map<String,Object> queryTrxGoodsBuyCountForGuest(Map<String, String> map);
    
    /**
     * 查询商家消费数量
     * @param map
     * @return Map<String,Object>
     */
    public Map<String,Object> queryTrxGoodsUsedCountForGuest(Map<String, String> map);
    
}
