package com.beike.core.service.trx.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.BizPaymentServiceFactory;
import com.beike.common.bean.trx.NotChangeParam;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PayRspInfo;
import com.beike.common.bean.trx.PaymentInfo;
import com.beike.common.bean.trx.TrxDataInfo;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmCancelRecord;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CancelType;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProPayStatus;
import com.beike.common.enums.trx.TrxCouponStatus;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.enums.trx.VoucherVerifySource;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.PaymentService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxRuleService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.PaymentDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.dao.vm.VmCancelRecordDao;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.EnumUtil;
import com.beike.util.GuidEncryption;
import com.beike.util.ListUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxRuleUtil;

/**
 * @Title: TrxOrderServieImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 交易订单
 * @date May 17, 2011 3:06:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("trxOrderService")
public class TrxOrderServiceImpl implements TrxOrderService {
	private final Log logger = LogFactory.getLog(TrxOrderServiceImpl.class);

	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private AccountService accountService;

	@Autowired
	private SubAccountService subAccountService;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	@Resource(name = "bizPaymentServiceFacroty")
	private BizPaymentServiceFactory bizPaymentServiceFacroty;

	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxSoaService trxSoaService;

	@Autowired
	private VoucherService voucherService;

	@Autowired
	private TrxRuleService trxRuleService;

	@Autowired
	private PayLimitService payLimitService;

	@Autowired
	private PaymentDao paymentDao;
	@Autowired
	private SubAccountDao subAccountDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private VmCancelRecordDao vmCancelRecordDao;
	@Autowired
	private VmAccountService vmAccountService;
	@Autowired
	private TrxCouponService trxCouponService;

	@Override
	public TrxOrder create(TrxOrder trxOrder) throws TrxOrderException {

		if (trxOrder.getUserId() == null) {
			throw new IllegalArgumentException("userid_not_null");
		}
		String requestId = guidGenerator.gainCode("Trx");
		if (requestId == null) {
			requestId = "Trx" + StringUtils.getSysTimeRandom();

		}
		logger.info("++++++++++++++++trxOrder requestId:" + requestId + "+++++++++++++++++++++++");
		String externalId = GuidEncryption.encryptSimpler("T", requestId.substring(3, requestId.length() - 1));
		if (externalId == null) {
			externalId = StringUtils.getSysTimeRandom();

		}
		logger.info("++++++++++++++++trxOrder externalId:" + externalId + "+++++++++++++++++++++++");

		// 组装核心信息
		trxOrder.setRequestId(requestId);
		trxOrder.setExternalId(externalId);
		trxOrder.setCreateDate(new Date());
		trxOrder.setOrderType(OrderType.SALES);
		trxOrder.setTrxStatus(TrxStatus.INIT);
		trxOrderDao.addTrxOrder(trxOrder);
		trxOrder.setId(trxOrderDao.getLastInsertId());
		return trxOrder;
	}

	/**
	 * 余额触发交易成功
	 * 
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws TrxorderGoodsException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 */
	@Override
	public TrxDataInfo  comleteByBalance(OrderInfo orderInfo) throws TrxOrderException,
			PaymentException, AccountException, VoucherException,
			RuleException, StaleObjectStateException, ProcessServiceException,
			RebateException, TrxorderGoodsException,CouponException {
		
			List<Payment>  paymentList= orderInfo.getPaymentList();//支付记录列表
			List<Account>  accountList=orderInfo.getAccountList();//账户列表
			TrxOrder trxOrder = orderInfo.getTrxOrder(); // 交易订单
		List<TrxorderGoods> trxGoodsList = orderInfo.getTgList();// 商品订单

			if (trxOrder == null) {
				throw new TrxOrderException(BaseException.TRXORDER_NOT_FOUND);
			}
			if (paymentList == null || paymentList.size() == 0) {
				throw new TrxOrderException(BaseException.VCACTPAYMENT_OR_AND_CASACTHPAYMENT_NOT_FOUND);

			}
			String bizType = orderInfo.getBizType();
		Long trxOrderId = trxOrder.getId();
		double trxOrderAmount = trxOrder.getOrdAmount();
			PaymentInfo paymentInfo = new PaymentInfo();
			paymentInfo.setTrxorderId(trxOrderId);
			paymentInfo.setTrxOrderAmount(trxOrderAmount);
		
			// 获取paymentService工厂
			paymentInfo.setPaymentType(PaymentType.ACTVC);
			PaymentService actPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.ACTVC);
		List<Payment> actPaymentList = actPaymentService.complete(paymentInfo, paymentList, false);

			
			//如果需要走帐，则账户出入帐.（对分销商而言，部分分销商不需要走帐务）
			if(orderInfo.isNeedActHis()){
				
				completeActHis(actPaymentList, trxOrder, trxGoodsList, accountList, orderInfo.getTrxCoupon(),bizType);
				
			}

			// 交易订单、商品订单以及凭证状态更新及处理
		TrxDataInfo trxDataInfo = completeTrxOrderAndTg(paymentList, trxOrder, trxGoodsList, bizType);

		// payLimitService.processPayLimit(trxOrder, trxGoodsList); // 调用限购

			return trxDataInfo;

	}

	/**
	 * 支付公司回调后，处理完成网银payment的需要处理的订单更新业务
	 * 
	 * @param payRspInfo
	 * @return
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws TrxorderGoodsException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 */
	@Override
	public TrxDataInfo achieveByPayAfter(PayRspInfo payRspInfo)
			throws PaymentException, TrxOrderException, AccountException,
			VoucherException, RuleException, StaleObjectStateException,
			ProcessServiceException, RebateException, TrxorderGoodsException,CouponException {

		TrxOrder trxOrder = payRspInfo.getTrxOrder();// 预查询出的交易订单
		List<TrxorderGoods> tgList = payRspInfo.getTgGoodsList();// 预查询出的商品订单列表
		List<Payment> paymentList = payRspInfo.getPaymentList();// 预查询出的支付记录列表
		Payment cashPayment = PaymentInfo.getPaymentByType(paymentList, PaymentType.PAYCASH);// cashPayment
		List<Account> accountList = payRspInfo.getAccountList();// 账户List
		String bizType = payRspInfo.getBizType();
		double ordAmount = trxOrder.getOrdAmount();// 交易订单金额
		double cashPayTrxAmount = payRspInfo.getSucTrxAmount();// 回调金额
		String payRequestId = payRspInfo.getPayRequestId();
		boolean isToLoad = false;// 是否变更为充值。（重复支付且余额+网银支付金额 <订单金额时，则变更为充值）

		// 当前有效余额总额
		double currentBalance = accountService.getCurrentBalance(accountList);

		// 构造PaymentInfo
		PaymentInfo paymentInfo = new PaymentInfo(cashPayTrxAmount, payRspInfo.getPayRequestId(), payRspInfo.getProConfirmDate(), payRspInfo.getProExternallId(), PaymentType.PAYCASH, ordAmount);

		// 获取paymentService工厂
		PaymentService cashPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.PAYCASH);

	
		logger.info("++++payRequestId:"+payRequestId+"++++currentBalance:"+currentBalance+"+++cashPayAmoun:"+cashPayTrxAmount+"++++ordAmount:"+ordAmount);
		
		//优惠券金额
		Double couponAmount = new Double(0);
		TrxCoupon coupon = payRspInfo.getTrxCoupon();
		if(null != coupon){
			couponAmount = coupon.getCouponBalance();
		}
		//先将订单金额减去优惠券金额（没有使用优惠券则减去0），与余额进行判断
		//置充值标志位为真
		if (Amount.add(currentBalance, cashPayment.getTrxAmount()) < Amount.sub(ordAmount, couponAmount)) {
			isToLoad = true;
		}

		// 接收支付机构回调，更新payment状态
		cashPaymentService.complete(paymentInfo, paymentList, isToLoad);

		// 针对重复支付的情况，将相关cashPayPment入账，发现支付余额不足时。直接当作充值处理。
		if (isToLoad) {
			completeLoadByExcess(cashPayment, payRspInfo);
			logger.info("+++++payRequestId:" + payRequestId + "++pay->Load+");
			return null;
		}

		// 账户出入帐
		 completeActHis(paymentList,trxOrder,tgList,accountList,coupon,bizType);
		 
		 //交易订单、商品订单以及凭证状态更新及处理
		 TrxDataInfo rtnTrxDataInfo=completeTrxOrderAndTg(paymentList, trxOrder, tgList, bizType);
		 		 
		 
		return rtnTrxDataInfo;

	}

	/**
	 * 账户出入帐
	 * 
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
	throws RuleException, AccountException, StaleObjectStateException,CouponException
	{

		Long trxOrderId = trxOrder.getId();// 交易订单ID
		String trxOrderReqId = trxOrder.getRequestId();// 交易订单请求号
		Account cashAccount = accountService.getActByActType(accountList, AccountType.CASH);//现金账户
		Account vcAccount = accountService.getActByActType(accountList, AccountType.VC);//虚拟币账户
		Payment cashPayPayment=PaymentInfo.getPaymentByType(paymentList, PaymentType.PAYCASH);//现金Payment
		Payment vcActPayment = PaymentInfo.getPaymentByType(paymentList, PaymentType.ACTVC);//账户虚拟币Payment
		Payment cashActPayment = PaymentInfo.getPaymentByType(paymentList, PaymentType.ACTCASH);//账户现金Payment
		// 账户支付时账户历史是否显示： 常规交易/0元抽奖/秒杀/打折引擎类型区分(暂时只针对虚拟币生效)
		boolean isDisActHis = trxRuleService.resolveTrxRule(trxGoodsList.get(0).getTrxRuleId(), TrxRuleUtil.ACTHIS);

		// 现金出入账
		if (cashPayPayment != null) {

			double trxAmount = cashPayPayment.getTrxAmount();// 现金交易金额
			Long cashPaymentId = cashPayPayment.getId();// 现金paymentID
			// 入帐描述
			String cashCreditDes = "cash-pay|requestId:" + cashPayPayment.getPayRequestId() + "|trxid:" + trxOrderId;
			// 出帐描述
			String cashDeditlDes = "cash-pay|trxid:" + trxOrderId;
			// 现金入账
			accountService.credit(cashAccount, trxAmount, ActHistoryType.LOAD, cashPayPayment.getId(), trxOrderId, new Date(), cashCreditDes, true, bizType);

			// 乐观锁重复更新问题.再查一次
			Account cashPayAccountDebit = accountService.findById(cashPayPayment.getAccountId());

			// 现金出帐
			accountService.debit(cashPayAccountDebit, trxAmount, ActHistoryType.SALES, cashPaymentId, trxOrderId, new Date(), cashDeditlDes, true, bizType);
		}

		// 如果有vctPayment，则虚拟币账户出帐
		if (vcActPayment != null) {
			double trxAmount = vcActPayment.getTrxAmount();// 虚拟币交易金额
			Long vcPaymentId = vcActPayment.getId();// 虚拟币paymentID
			String description = "vc-act|trxid:" + trxOrderId;// 描述
			
			//存在优惠券，则进行优惠券充值
			if(null != coupon){
				//优惠券充值
				trxCouponService.dispatchCoupon(coupon);		//优惠券充值操作
				vcAccount = accountService.findById(vcAccount.getId());	//vc账户已经变更，需要重新查询数据库
			}
				
			
			
			logger.info("++++++vcActPaymentId:+" + vcPaymentId+ "++TrxAmount:+" + trxAmount + "+++++++++++++++++");
			// 个人总账户扣款
			accountService.debit(vcAccount, trxAmount, ActHistoryType.SALES, vcPaymentId, trxOrderId, new Date(), description, isDisActHis, bizType);
			
			boolean haveCoupon =coupon!=null;
			Long priorityVmActIds[] = null;
			if(haveCoupon){
				priorityVmActIds = new Long[]{coupon.getVmAccountId()};
			}
			// 个人子账户扣款
			NotChangeParam notChangeParam = subAccountService.debitByPaySuc(vcAccount, trxAmount, vcPaymentId,trxOrderId, trxOrderReqId, new Date(),priorityVmActIds,description);
			
				
			/*
			 * haveCoupon=false千品币购买不找零
			 * haveCoupon=true优惠券不找零
			 */
			int notChangeType = 0;
			//如果不找零的虚拟款项是优惠券的虚拟款项,则为优惠券不找零
			if(haveCoupon && notChangeParam.getVmAccountId().intValue()==coupon.getVmAccountId().intValue()){
				notChangeType = 1;
			}
			this.processNotChangeSubAccount(notChangeParam,notChangeType);
		}
		
		// 如果有cashActPayment，则账户里沉淀的现金出帐
		if (cashActPayment != null) {
			String description = "vc-act|trxid:" + trxOrderId;// 描述
			Account cashActAccount = accountService.findById(cashActPayment.getAccountId());
			logger.info("++++++cashActPaymentId:+" + cashActPayment.getId() + "++TrxAmount:+" + cashActPayment.getTrxAmount() + "+++++++++++++++++");

			accountService.debit(cashActAccount, cashActPayment.getTrxAmount(), ActHistoryType.SALES, cashActPayment.getId(), trxOrder.getId(), new Date(), description, true, bizType);
		}
	}
	/**
	 * 子账户不找零业务
	 * 
	 * @param subData
	 * @param notChangeType: 0:虚拟款项不找零;1:优惠券不找零
	 * @throws StaleObjectStateException 
	 * @throws AccountException 
	 * 
	 */
	public void processNotChangeSubAccount(NotChangeParam notChangeParam,int notChangeType) throws AccountException, StaleObjectStateException{
	
		if(notChangeParam!=null){
			logger.info("++++++++++++++++++++processSubAccount++++++subData="+notChangeParam.toString());
			boolean isDisplayAactHis = true;
			double trxAmountDb =notChangeParam.getTrxAmount(); //不找零发生金额 或优惠券未使用金额
			Long subAccountId =notChangeParam.getSubAccountId();//个人子账户ID
			Long accountId =notChangeParam.getAccountId();//个人总账户ID
			String subSuffix = accountId.toString().substring(accountId.toString().length()-1);//子账户表_值
			Long vmAccountId = notChangeParam.getVmAccountId();//虚拟款项总账户ID
			Map<Long, String> vmAccount =	vmAccountService.findVmAccount(vmAccountId);
			String vmAccountStr = vmAccount.get(vmAccountId);
			if (vmAccountStr != null && !"".equals(vmAccountStr)) {
				String[] vmArray = vmAccountStr.split("\\|");
				String notChange = vmArray[0];
				if ("1".equals(notChange)) {
					SubAccount subAccount = subAccountDao.findById(subAccountId, subSuffix);
					if (subAccount != null) {
						double doubleRule = Double.valueOf(vmArray[1]).doubleValue();
						double notChanegAmount = 0.0;// 不找零金额，即取消金额
						if (subAccount.getBalance() < doubleRule) {
							// 防止子账户交易后退款在交易导致扣款问题
							notChanegAmount = subAccount.getBalance();
						} else {
							notChanegAmount = Amount.sub(doubleRule, trxAmountDb % Double.valueOf(vmArray[1]));
						}
						if (notChanegAmount != 0 && notChanegAmount != Double.valueOf(vmArray[1]).doubleValue()) {
							
							String description = "vm not change cancel";
							Long trxId = notChangeParam.getTrxId();
							Account account = accountDao.findById(subAccount.getAccountId());
							// 添加子账户取消历史
							VmCancelRecord vmCancelRecord = new VmCancelRecord();
							vmCancelRecord.setAccountId(subAccount.getAccountId());
							vmCancelRecord.setAmount(notChanegAmount);
							vmCancelRecord.setCreateDate(new Date());
							vmCancelRecord.setOperatorId(0L);
							vmCancelRecord.setSubAccountId(subAccount.getId());
							vmCancelRecord.setUpdateDate(new Date());
							vmCancelRecord.setVmAccountId(subAccount.getVmAccountId());
							
							//优惠券不找零
							if(1==notChangeType){
								description = "coupon not change";
							}
							vmCancelRecord.setCancelType(CancelType.NOT_CHANGE);
							Long vmCancelRecordId = vmCancelRecordDao.addVmCancelRecord(vmCancelRecord);

							// 虚拟款项个人总账户不找零扣款
							subAccountService.debitByCancel(subAccount,account, notChanegAmount,ActHistoryType.NOT_CHANGE,vmCancelRecordId, trxId, new Date(), "",isDisplayAactHis,description, false);
						}
					}
				}
			}
		}

	}

	/**
	 * 支付完成后的订单相关操作
	 * 
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
	@SuppressWarnings("unchecked")
	@Override
	public TrxDataInfo completeTrxOrderAndTg(List<Payment> paymentList, TrxOrder trxOrder, List<TrxorderGoods> trxGoodsList, String bizType) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException,
			RuleException, StaleObjectStateException {

		// 记录数多则做JDBC直接批量更新
		Map<Long, Integer> saleCountmap = new HashMap<Long, Integer>();// KEY=goodsId,Value=购买数量
		List<TrxorderGoods> unSingleOverRunTgList = new ArrayList<TrxorderGoods>();// 非个人超限trxorderGoods数据
		List<TrxorderGoods> singleOverRunTgList = new ArrayList<TrxorderGoods>();// 个人超限trxorderGoods数据
		// 更新Trxordr状态。
		if (TrxStatus.INIT.equals(trxOrder.getTrxStatus())) {
			trxOrderDao.updateStatusById(trxOrder.getId(), TrxStatus.SUCCESS, new Date(), trxOrder.getVersion());
		}

		for (TrxorderGoods item : trxGoodsList) {
			if (TrxStatus.INIT.equals(item.getTrxStatus()) && AuthStatus.INIT.equals(item.getAuthStatus())) {
				// 更新凭证和商品订单状态

				boolean isAllowBuyPayLimit = payLimitService.isAllowBuyInPayLimit(trxOrder, item);

				Voucher voucher = voucherService.activeVoucher(item.getGuestId(), item.getGoodsId(), new Date(), item.isSendMerVou(), item.getTrxGoodsSn(), Constant.MER_VOUCHER_OVER_ALERT, isAllowBuyPayLimit);
				trxorderGoodsDao.updateTrxAndAauthStatusById(item.getId(), TrxStatus.SUCCESS, AuthStatus.SUCCESS, voucher.getId(), item.getVersion());

				// 销售量计算累加
				Integer goodsCount = saleCountmap.get(item.getGoodsId());
				if (goodsCount == null) {
					goodsCount = 1;
				} else {
					goodsCount = goodsCount + 1;
				}
				saleCountmap.put(item.getGoodsId(), goodsCount);

				// 通过商家API发送商家码对应的订单(如果同时满足，则优先判断此此逻辑，防止运营录错商品属性)
				item.setMobile(trxOrder.getExtendInfo().split("-")[1]);
				item.setVoucherCode(voucher.getVoucherCode());
				item.setMerVouEnough(voucher.isSendMerVou());// 如果获取到商家到，则将tg“商家码”是否充足状态置为true
				item.setVoucherId(voucher.getId());
				

				if (isAllowBuyPayLimit) { // 如果允许购买（未个人限购超限）
					TrxorderGoods usedTrxorderGoods = trxorderGoodsDao.findById(item.getId());

					// 成功获取到商家码（商家码充足）的订单或者通过商家在线API发送凭证的订单
					// if (TrxConstant.isMerchantVoucherByApi(item.getGuestId())
					// || voucher.isSendMerVou() ) {
					if (usedTrxorderGoods.isSendMerVou() == 2 || voucher.isSendMerVou()||usedTrxorderGoods.isSendMerVou() == 3) {
						// 直接置为已使用
						voucherService.checkVoucherSelf(usedTrxorderGoods, EnumUtil.transEnumToString(VoucherVerifySource.SYSTEMAUTO), "0");

					}
					
					unSingleOverRunTgList.add(item);// 组装非个人超限trxorderGoods数据

				} else {

					singleOverRunTgList.add(item);// //组装个人超限trxorderGoods数据
				}

			}
		}
		// 组装个人超限、总量超限、销售量数据
		TrxDataInfo trxDataInfo = new TrxDataInfo(unSingleOverRunTgList, singleOverRunTgList, ListUtils.union(unSingleOverRunTgList, singleOverRunTgList), saleCountmap);
		trxDataInfo.setTrxOrder(trxOrder);

		return trxDataInfo;

	}

	/**
	 * 冗余支付触发充值
	 * 
	 * @param cashPayPayment
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void completeLoadByExcess(Payment cashPayPayment, PayRspInfo payRspInfo) throws AccountException, StaleObjectStateException {

		// 资金入账
		Account cashPayAccount = accountService.findById(cashPayPayment.getAccountId());
		accountService.credit(cashPayAccount, cashPayPayment.getTrxAmount(), ActHistoryType.LOAD, cashPayPayment.getId(), 0L, new Date(), "cash-pay->load|requestId:" + cashPayPayment.getPayRequestId() + "|trxid:" + cashPayPayment.getTrxorderId(), true, payRspInfo.getBizType());

	}

	/**
	 * 个人购买超限触发充值
	 * 
	 * @param cashPayPayment
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void completeLoadByExcess(Payment cashPayPayment, String test, PayRspInfo payRspInfo) throws AccountException, StaleObjectStateException {

		// 资金入账
		Account cashPayAccount = accountService.findById(cashPayPayment.getAccountId());
		accountService.credit(cashPayAccount, cashPayPayment.getTrxAmount(), ActHistoryType.LOAD, cashPayPayment.getId(), 0L, new Date(), "cash-pay->load|requestId:" + cashPayPayment.getPayRequestId() + "|trxid:" + cashPayPayment.getTrxorderId(), true, payRspInfo.getBizType());

	}

	/**
	 * 在写库中进行网银支付完成后出入帐前的预查询
	 * 
	 * @param payRspInfo
	 * @return
	 * @throws TrxorderGoodsException
	 * @throws TrxOrderException
	 */
	public PayRspInfo preQryInWtDBForCash(PayRspInfo payRspInfo) throws TrxorderGoodsException, TrxOrderException {

		Payment cashPayPayment = paymentDao.findByPayReqIdAndType(payRspInfo.getPayRequestId(), ProPayStatus.INIT, PaymentType.PAYCASH);

		if (cashPayPayment == null) { // 无此支付记录或者已被回调更新
			return null;
		}
		Long trxOrderId = cashPayPayment.getTrxorderId();// 交易订单ID

		TrxOrder trxOrder = trxOrderDao.findById(trxOrderId);// 交易订单
		if (trxOrder == null) {

			throw new TrxOrderException(BaseException.TRXORDER_NOT_FOUND);
		}

		List<TrxorderGoods> tgList = preQryInWtDBForTg(trxOrderId);// 商品订单（预查询）

		if (tgList == null) {
			throw new TrxorderGoodsException(BaseException.TRXORDERGOODS_NOT_FOUND);

		}
		
		List<Payment> paymentList=paymentDao.findByTrxId(trxOrderId);
		
		//判断payment中是否有优惠券交易,如果存在，查询出来，放入

		for(Payment payment : paymentList){
			if(payment.getPaymentType()==PaymentType.ACTVC){
				Long couponId = payment.getCouponId();
				if(null != couponId && couponId.intValue()>0){
					TrxCoupon coupon = trxCouponService.queryCouponById(couponId);
					if(null != coupon && coupon.getCouponStatus()==TrxCouponStatus.BINDING){
						payRspInfo.setTrxCoupon(coupon);
					}else{
						logger.error("++++++++couponId="+couponId+"+is not exist or had been used+++");
					}
					break;
				}
			}
		}
		
		List<Account> accountList=accountService.findByUserId(trxOrder.getUserId());
		
		payRspInfo.setPaymentList(paymentList);
		payRspInfo.setTrxOrder(trxOrder);
		payRspInfo.setTgGoodsList(tgList);
		payRspInfo.setAccountList(accountList);
		return payRspInfo;

	}

	/**
	 * 在写库中进行商品订单查询
	 * 
	 * @param payRspInfo
	 * @return
	 * @throws TrxorderGoodsException
	 */
	public List<TrxorderGoods> preQryInWtDBForTg(Long trxOrderId) throws TrxorderGoodsException {

		List<TrxorderGoods> newTgList = new ArrayList<TrxorderGoods>();
		List<Long> goodsList = new ArrayList<Long>();
		List<TrxorderGoods> tgList = trxorderGoodsDao.findByTrxId(trxOrderId);

		if (tgList == null || tgList.size() == 0) {
				throw new TrxorderGoodsException(BaseException.TRXORDERGOODS_NOT_FOUND);

			}
		for (TrxorderGoods tg : tgList) {

				goodsList.add(tg.getGoodsId());
			}

		Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsList);

		for (TrxorderGoods tg : tgList) {

				tg.setGoodsTitle(goodsTitleMap.get(tg.getGoodsId()));
				newTgList.add(tg);
			}

		return newTgList;
	}

	/**
	 * 根据商品订单Id获取Trxorder
	 * 
	 * @param <TrxOrder>
	 * @param tgId
	 */
	public TrxOrder findByTgId(Long tgId) {

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(tgId);

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());

		return trxOrder;

	}

	public TrxOrder findById(Long id) {

		TrxOrder trxOrder = trxOrderDao.findById(id);

		return trxOrder;

	}

	/**
	 * 根据outRequestId和userIdList查询Trxorder（主库查询）
	 * 
	 * @return
	 */
	public TrxOrder preQryInWtDBByUIdAndOutReqId(String outRequestId, List<Long> userIdList) {

		return trxOrderDao.findByUserIdOutRequestId(outRequestId, userIdList);

	}

	public TrxOrder preQryInWtDBByUIdAndOutReqId(List<Long> userIdList) {

		return trxOrderDao.findByUserId(userIdList);

	}
	/**
     * 查询商品订单列表 
     */
    @Override
    public List<Map<String,Object>> getTrxGoodsByIds(Map<String, String> map ) {
        // 查询商品订单信息,分页后的数据
        List<Map<String,Object>> goodsOrderList = trxOrderDao.queryTrxGoodsIds(map,null);
        if(goodsOrderList == null || goodsOrderList.size()==0){
            return null;
        }
        
        //需要查询退款时间的商品订单id集合
        String  trxRefundIds = "";
        String trxGoodsIds="";//所有的商品id集合
        String voucherIdStrs="";//凭证的id集合
        for(Map<String,Object> order : goodsOrderList){
            trxGoodsIds=trxGoodsIds+order.get("trxGoodsId").toString()+",";
            voucherIdStrs=voucherIdStrs+order.get("voucherId").toString()+",";
            // 需要查询退款的
            String trxStatus= (String)order.get("trxStatus");
            if(trxStatus.equals("REFUNDACCEPT") || trxStatus.equals("REFUNDTOACT")  || trxStatus.equals("RECHECK") || trxStatus.equals("REFUNDTOBANK")){
                trxRefundIds=trxRefundIds+order.get("trxGoodsId").toString()+",";
            }
        }
        if(!"".equals(trxGoodsIds)){
            
            trxGoodsIds=trxGoodsIds.substring(0, trxGoodsIds.length()-1);
            
            //申请退款时间
           /* Map<String,Object> rudRecordMap = new HashMap<String, Object>();
            if(!"".equals(trxRefundIds)){
                trxRefundIds=trxRefundIds.substring(0,trxRefundIds.length()-1);//去掉最后一个逗号
                List<Map<String, Object>> refundRecords = refundRecordDao.findByTrxOrderGoodsIds(trxRefundIds);
                if(refundRecords!=null && refundRecords.size()>0){
                    //将退款时间放到map中。单次循环
                    for(Map<String, Object> rudRecord: refundRecords){
                        rudRecordMap.put(rudRecord.get("trx_goods_id").toString(), rudRecord.get("create_date"));
                    }
                }
            }*/
            
            //补偿状态
           /* List<Map<String, Object>> comRecordList= trxorderGoodsDao.findCompensateRecordListByTrxGoodsIds(trxGoodsIds);
            Map<String,Object> comRecordMap = new HashMap<String, Object>();
            if(comRecordList!=null && comRecordList.size()>0){
                for(Map<String, Object> comMapTmp:comRecordList){
                    comRecordMap.put(comMapTmp.get("trx_goods_id").toString(), comMapTmp);
                }
            }*/
            
            //消费时间、凭证
            Map<String,Object> vouchersMap = new HashMap<String, Object>();
            if(!"".equals(voucherIdStrs)){
                voucherIdStrs=voucherIdStrs.substring(0, voucherIdStrs.length()-1);
                List<Voucher> voucherList = trxOrderDao.findConfrimTimeByTrxgoodsIds(voucherIdStrs,null);
                if(voucherList!=null && voucherList.size()>0){
                    for(Voucher voucher:voucherList){
                        vouchersMap.put(voucher.getId().toString(), voucher);
                    }
                }
             }
            
            //再次循环，设置申请退款时间、补偿状态 、消费时间
            for(Map<String,Object> order : goodsOrderList){
            @SuppressWarnings("unused")
			String theTrxGoodsId=order.get("trxGoodsId").toString();
               //申请退款时间
               /*if(rudRecordMap.size()>0){
                   if(rudRecordMap.get(theTrxGoodsId)!=null){
                       order.put("refundDate",rudRecordMap.get(theTrxGoodsId));
                   }
               }*/
               
               //补偿状态
               /*if(comRecordMap.size()>0){
                   Map<String, Object> comRecordMap2= (Map<String, Object>) comRecordMap.get(theTrxGoodsId);
                   if(comRecordMap2!=null){
                       order.put("compensate", comRecordMap2.get("comp_status"));
                   }else{
                       order.put("compensate", "ON");
                   }
               }else{
                   order.put("compensate", "ON");
               }*/
               //消费时间
               if(vouchersMap.size()>0){
                   Voucher voucherTmp=(Voucher)vouchersMap.get(order.get("voucherId").toString());
                   if(voucherTmp != null){
                       order.put("confirmDate",voucherTmp.getConfirmDate());
                       order.put("voucherCode",voucherTmp.getVoucherCode());
                   }
               }
               
            }
            
        }
        
        
        return goodsOrderList;
    }
}
