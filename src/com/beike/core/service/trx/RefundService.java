package com.beike.core.service.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.RefundReqInfo;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RefundException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: RefundSerive.java
 * @Package com.beike.core.service.trx
 * @Description: 退款service接口
 * @date May 24, 2011 10:39:31 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface RefundService {

	/**
	 * 申请账户退款
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @param description
	 * @throws RefundException
	 * @throws StaleObjectStateException
	 */
	public void processApplyForRefundToAct(Long trxGoodsId, String operatorer,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, StaleObjectStateException;

	/**
	 * 拒绝账户退款
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param description
	 * @throws RefundException
	 * @throws StaleObjectStateException
	 */
	public void processRefuseForRefundToAct(Long trxGoodsId, String operatorer,
			String description) throws RefundException,
			StaleObjectStateException;

	/**
	 * 根据trx_goods_id进行退款
	 * 
	 * @param trxGoodsId
	 * @throws RefundException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws VmAccountException
	 */
	public TrxorderGoods processToAct(Long trxGoodsId, String operatorer,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, AccountException, VoucherException,
			RuleException, StaleObjectStateException, VmAccountException,CouponException;

	/**
	 * 申请退款到银行账户
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @param description
	 * @throws RefundException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void processApplyForRefundToBank(Long trxGoodsId, String operatorer,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, AccountException, StaleObjectStateException;

	/**
	 * 拒绝退到银行卡申请
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @param description
	 * @throws RefundException
	 * @throws StaleObjectStateException
	 */
	public void processRefuseForRefundToBank(Long trxGoodsId, String operator,
			String description) throws RefundException,
			StaleObjectStateException;

	/**
	 * 退款到银行之前处理，更新RefundDetail为退款处理中状态，扣除账户中的退款金额
	 *
	 * @param refundDetail
	 */
	public RefundReqInfo processBeforeRefundToBank(Long trxorderGoodsId) throws StaleObjectStateException,AccountException,RefundException;
	
	
	/**
	 * 向银行发起付款请求，并得到银行处理
	 *
	 * @param refundDetail
	 * @param payment
	 * @param trxorderGoods
	 * @param operator
	 * @param description
	 * @throws RefundException
	 */
	public OrderInfo sendRefundReqToBank(RefundReqInfo refundReqInfo) throws RefundException;
	
	
	/**
	 * 根据银行返回的响应退款结果，进行退款成功、失败的处理
	 * @param refundRspOrderInfo
	 * @param refundDetail
	 * @param trxorderGoods
	 * @param operator
	 * @param description
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods processAfterRefundToBank(OrderInfo refundRspOrderInfo,RefundReqInfo refundReqInfo) throws StaleObjectStateException;

	
	
	/**
	 * 购买超限退款接口
	 * @param trxGoodsId
	 * @param operator
	 * @param refundSourceType
	 * @param refundHandleType
	 * @param description
	 * @return
	 */
	public TrxorderGoods processToActByPayLimit(Long trxGoodsId,
			String operator, RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description);
	
	/**
	 * 处理超限退款
	 * @param overRunTgList
	 * @param overRunType
	 * @return
	 * @throws BaseException 
	 */
	public List<TrxorderGoods> processOverRunRfd(List<TrxorderGoods> overRunTgList,String overRunType) throws BaseException;

	/**
	 * 退款短信通知接口
	 * @param tog
	 * @param smsTemplate
	 * @param smsParam
	 * @throws BaseException
	 */
	public void smsNotify(TrxorderGoods tog,String smsTemplate,Object[] smsParam) throws BaseException;

	
	/**
	 * 根据支付渠道、状态和时间获得退款申请记录（支付方式银联用）
	 * @param paymentType
	 * @param providerType
	 * @param refundStatus
	 * @param date
	 * @return
	 */
    public List<Map<String, Object>> getRefundtoBankTimeOutUPOP(PaymentType paymentType,
            ProviderType providerType, RefundStatus refundStatus, String startDate,String endDate,String payDate);
	/**
	 * 查询商品明细状态为recheck，更新时间段内的列表
	 * @param trxStartTime
	 * @param trxEndTime
	 * @return
	 */
    public List<TrxorderGoods>  getRefundRecheckTrxgoods(String date);
    
	/**
	 * 退款报警邮件
	 * @param content
	 */
	public void sendWarningEmail(String content);
	
	/**
     * 根据支付渠道、状态和时间获得退款申请记录（支付方式支付宝用）
     * @param paymentType
     * @param providerType
     * @param refundStatus
     * @param date
     * @return
     */
    public List<Map<String, Object>> getRefundtoBankTimeOutAlipay(PaymentType paymentType,
            ProviderType providerType, RefundStatus refundStatus, String startDate,String endDate,String payDate);
    /**
     * 根据订单号获得该订单是否用优惠卷支付过
     * @param trxid
     * @return Payment
     */
    public Payment getCouponPaymentByTrxId(Long trxid);
    /**
     *根据商品订单号获得本次可退款的金额（优惠券部分不退款） 
     */
    public double getRefundAmountByTrxGoodsId(Long trxGoodsId);
    
    /**
	 * 将已校验商品置为退款到账户
	 * @param voucher
	 * @param trxorderGoods
	 * @param voucherVrifySource
	 * @param subGuestId
	 * @return
	 * @throws VoucherException
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods updateUsedByRefundtoact(Voucher voucher, TrxorderGoods trxorderGoods) throws BaseException;
	
	/**
	 * 根据商品订单编号查询退款申请记录
	 * @param trxOrderId
	 * @return
	 */
	public RefundRecord queryRefundRecordByTrxorderId(Long trxOrderId);
}
