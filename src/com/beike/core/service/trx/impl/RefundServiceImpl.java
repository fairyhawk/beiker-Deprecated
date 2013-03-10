package com.beike.core.service.trx.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.beike.biz.service.trx.PaymentInfoGeneratorFactory;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.RefundReqInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundDetail;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmCancelRecord;
import com.beike.common.entity.vm.VmTrxExtend;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CancelType;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.enums.trx.TrxlogSubType;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.enums.vm.RelevanceType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.RefundException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.exception.VoucherException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxRuleService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.AccountHistoryDao;
import com.beike.dao.trx.PaymentDao;
import com.beike.dao.trx.RefundDetailDao;
import com.beike.dao.trx.RefundRecordDao;
import com.beike.dao.trx.TrxLogDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.dao.trx.soa.proxy.UserSoaDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.dao.vm.VmCancelRecordDao;
import com.beike.dao.vm.VmTrxExtendDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.service.cps.tuan800.CPSTuan800Service;
import com.beike.service.cps.tuan800.impl.CPSTuan800Thread;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.GuidEncryption;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.TrxRuleUtil;

/**
 * @Title: ProcessToActImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 退款
 * @date May 25, 2011 1:27:55 AM
 * @author wh.cheng
 * @version v1.0
 */

public class RefundServiceImpl implements RefundService {

	private final Log logger = LogFactory.getLog(RefundServiceImpl.class);
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	public String refundPaylimitCountEmail = propertyUtil.getProperty("refund_paylimit_count_email");
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private PaymentDao paymentDao;
	@Autowired
	private RefundRecordDao refundRecordDao;
	@Autowired
	private RefundDetailDao refundDetailDao;
    @Autowired
	private CPSTuan800Service cpsTuan800Service;
	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountHistoryDao actHistoryDao;

	@Autowired
	private VoucherService voucherService;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	@Resource(name = "paymentInfoGeneratorFactory")
	private PaymentInfoGeneratorFactory paymentInfoGeneratorFactory;

	@Autowired
	private TrxLogDao trxLogDao;
	@Autowired
	private TrxRuleService trxRuleService;

	@Autowired
	private SubAccountService subAccountService;
	@Autowired
	private PartnerCommonService partnerCommonService;

	// 扣款报警邮件模板
	public static final String REFUND_COUNT_ERROR = "REFUND_COUNT_ERROR";
	@Autowired
	private EmailService emailService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private UserSoaDao userSoaDao;
	@Autowired
	private TrxSoaService trxSoaService;
    @Autowired
    private SubAccountDao subAccountDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private VmCancelRecordDao vmCancelRecordDao;
    @Autowired
    private VmTrxExtendDao vmTrxExtendDao;
    @Autowired
    private VmAccountService vmAccountService;
    @Autowired
    private TrxCouponService trxCouponService;
    @Autowired
    private VoucherDao voucherDao;
	//退款报警邮件 发件人、收件人信息
	private static ResourceBundle rb = ResourceBundle.getBundle("project");
	private static final String sender=rb.getString("refund_sender");
	private static final String toEmail=rb.getString("refund_toer");
	
	/**
	 * 申请退款到账户
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @return
	 * @throws RefundException
	 * @throws StaleObjectStateException
	 */
	public void processApplyForRefundToAct(Long trxGoodsId, String operator,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, StaleObjectStateException {

		checkRefundToAct(trxGoodsId);

		// 同步trxGoogs中的交易状态
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);

		trxSoaService.processScheduled(trxGoodsId, trxorderGoods.getTrxorderId());
	
		if (TrxStatus.REFUNDACCEPT.equals(trxorderGoods.getTrxStatus())// 已经申请成功过，处理中
		) {
			throw new RefundException(BaseException.REFUND_RECORD_APPLY_HAVED);
		}
		// 账户退款前置条件：支付成功（未使用凭证） //第一次申请状态判断
		if (!trxorderGoods.getTrxStatus().isAplyRefundToAct()|| !trxorderGoods.getAuthStatus().isRefund()) {
			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}
		trxorderGoods.setTrxStatus(TrxStatus.REFUNDACCEPT);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);

		try {
			TrxLog trxLog = null;
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(),TrxLogType.REFUNDACCEPT, "账户退款申请", "申请人：" + operator + ";附言："+ description);
			if (RefundSourceType.TIMING.equals(refundSourceType)) {

				trxLog.setTrxlogSubType(TrxlogSubType.EXPIRED_AUTO);
			} else if (RefundSourceType.OVERRUN.equals(refundSourceType)) {
				
				trxLog.setTrxlogSubType(TrxlogSubType.OVERRUN_AUTO);
			} else if(RefundSourceType.PARTNER.equals(refundSourceType)){
				
				trxLog.setTrxlogSubType(TrxlogSubType.PARTNER);
			}else{
				
				trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
			
			}
			
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

		}

	}

	/**
	 * 拒绝账户退款
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param description
	 * @throws RefundException
	 * @throws StaleObjectStateException
	 */
	public void processRefuseForRefundToAct(Long trxGoodsId, String operator,
			String description) throws RefundException,
			StaleObjectStateException {

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);

		if (!TrxStatus.REFUNDACCEPT.equals(trxorderGoods.getTrxStatus())) {

			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}
		if (AuthStatus.TIMEOUT.equals(trxorderGoods.getAuthStatus())) { // 凭商品订单中的授权状态判断是“过期状态->申请退款”还是“常规->申请退款”
			trxorderGoods.setTrxStatus(TrxStatus.EXPIRED);// 先过期，用户死活要运营退款,被拒绝回归状态
		}
		if (AuthStatus.SUCCESS.equals(trxorderGoods.getAuthStatus())) { // 凭商品订单中的授权状态判断是“过期状态->申请退款”还是“常规->申请退款”
			trxorderGoods.setTrxStatus(TrxStatus.SUCCESS);// 常规申请,被拒绝回归状态
		}

		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		try {

			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(),TrxLogType.REFUNDREFUSE, "运营审批拒绝", "审批人:" + operator + ";附言："+ description);
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			logger.error(e);

		}

	}

	/**
	 * 退款到账户
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param rudRecordId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @throws RefundException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws VmAccountException
	 */
	public TrxorderGoods processToAct(Long trxGoodsId, String operator,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, AccountException, VoucherException,
			RuleException, StaleObjectStateException, VmAccountException,CouponException {

	

		checkRefundToAct(trxGoodsId);// 合法性检查
		boolean isNeedActHis=true;//是否需要走账
		String logTitle="";//业务操作日志标题

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
		/*************************************** 0元特殊处理 START **********************/
		if (trxorderGoods.getTrxRuleId().longValue() == 1
				|| trxorderGoods.getPayPrice() == 0) {

			processToActByLottery(trxGoodsId, description, refundSourceType,refundHandleType, description);
			return null;
		}
		/*************************************** 0元特殊处理 END **********************/
		if (!TrxStatus.REFUNDACCEPT.equals(trxorderGoods.getTrxStatus())) {

			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());
		
		PartnerInfo  partnerInfo=partnerCommonService.qryParterByUserIdInMem(trxOrder.getUserId());
		if(partnerInfo!=null){//如果此订单来自分销商，则后续有是否走账逻辑以及向分销商发起退款订单同步或回调
			
			isNeedActHis=false;//无需走账. 
			//todo  预存分销商的返点以及退款计算
		}

		// 开始记录退款记录. 并冗余数据
		RefundRecord refundRecord = new RefundRecord(trxOrder.getId(), trxOrder
				.getUserId(), trxGoodsId, trxOrder.getCreateDate(), trxOrder
				.getCloseDate(), new Date(), trxorderGoods.getGoodsName(),
				trxOrder.getOrdAmount(), trxorderGoods.getPayPrice());
		refundRecord.setRefundStatus(RefundStatus.INIT);
		refundRecord.setOperator(operator);
		refundRecord.setHandleType(refundHandleType);
		refundRecord.setRefundSourceType(refundSourceType);
		refundRecord.setDescription(description);
		Long rudRecordId = refundRecordDao.addRefundRecord(refundRecord);

		Long trxId = trxOrder.getId();
		Long userId = trxOrder.getUserId();

		// 检查之前的payment和trxorder金额是否符合。退款是资金回流，有风险，多校验，就算是多余也是非常有必要的
		// double ordAmount = 0;
		double ordAmount = trxOrder.getOrdAmount();
		double actVcAmount = 0;
		double actCashAmount = 0;
		double payCashAmount = 0;
		double needRudAmount = trxorderGoods.getPayPrice();// 需退款金额

		Payment actVcPayment = null;
		Payment actCashPayment = null;
		Payment payCashPayment = null;
		List<Payment> paymentList = paymentDao.findByTrxId(trxId);

		if (paymentList.size() > 0 && paymentList != null) {

			for (Payment item : paymentList) {

				if (PaymentType.ACTVC.equals(item.getPaymentType())) {
					// 账户里的虚拟币
					actVcPayment = item;
					actVcAmount = actVcPayment.getTrxAmount();
				}

				if (PaymentType.ACTCASH.equals(item.getPaymentType())) {
					// 账户里的余额现金
					actCashPayment = item;
					actCashAmount = actCashPayment.getTrxAmount();
				}
				if (PaymentType.PAYCASH.equals(item.getPaymentType())) {
					// 支付的现金
					payCashPayment = item;
					payCashAmount = payCashPayment.getTrxAmount();
				}

			}
		}

		double newAamount = Amount.cutOff(actVcAmount + actCashAmount+ payCashAmount, 2);
		logger.info("+++++++++++trxGoodsId:" + trxGoodsId + "newAamount:"+ newAamount + "ordAmount:" + ordAmount + "+++++++++++++++++");
		// 核对交易金额
		if (ordAmount != newAamount) {
			throw new RefundException(
					BaseException.REFUND_TRXORDER_PAYMENT_AMOUNT_EQUALS);
		}

		// 记录退款记录资金分布，并冗余数据
		// 校验资金分布
		// 1.查出退款记录中trxrdgoodsId下所有的退款明细中的paymentid记录。包括paycash、actcash、actvc的已退款成功的金额。得到分别未退款的三种金额
		// 2.1.如果未退款的paycash的金额>=需退款的金额且差额大于本次的trxgoods金额，则放到refunddetail中，一条paycash
		// 2.2.如果未退款paycash的金额>=需退款的金额且差额小于本次的trxgoods金额，差额放到refunddetail中，有一条paycash；得到差额。另一部分放到actcash或者actvc中
		// 2.3.如果未退款的actcash>=差额，则追加一条actcash。终止。否则得到2.2的差额-2.3的未退款的差额到2.3.1
		// 2.3.1.如果未退款的actvc-2.3的差额，再追加一条actvc

		double payCashSucRudAmount = 0;
		double actCashSucRudAmount = 0;
		double actVcSucRudAmount = 0;
		// 查出trxid下的record是否已有退款记录
		List<RefundRecord> refundRecordList = refundRecordDao.findByOrdId(trxId);
		if (refundRecordList != null) {// 该trxId下已存在退款，开始计算已退款成功金额

			payCashSucRudAmount = refundDetailDao.findSucRudAmtByTrxId(trxId,
					PaymentType.PAYCASH, RefundStatus.REFUNDTOACT);
			actCashSucRudAmount = refundDetailDao.findSucRudAmtByTrxId(trxId,
					PaymentType.ACTCASH, RefundStatus.REFUNDTOACT);

			actVcSucRudAmount = refundDetailDao.findSucRudAmtByTrxId(trxId,
					PaymentType.ACTVC, RefundStatus.REFUNDTOACT);
			
		}

		double unRudPayCashAmount = Amount.sub(payCashAmount,
				payCashSucRudAmount);// 未退款支付金额
		double unRudActCashAmount = Amount.sub(actCashAmount,
				actCashSucRudAmount);// 未退款账户
		double unRudActVcAmount = Amount.sub(actVcAmount, actVcSucRudAmount);// 未退库虚拟币
		
		// 其中一个未退款金额小于额，都属于内部错误
		if (unRudPayCashAmount < 0 || unRudActCashAmount < 0|| unRudActVcAmount < 0 ) {
			throw new RefundException(BaseException.PAYMENTAMOUNT_PROVIDERAMOUNT_NOT_EQUALS);

		}

		List<Account> accountList = accountService.findByUserId(userId);
		Account cashAccount = null;
		Account vcAccount = null;
		if (accountList.size() > 0 && accountList != null) {
			for (Account item : accountList) {

				if (AccountType.CASH.equals(item.getAccountType())) {
					cashAccount = item;
				}

				if (AccountType.VC.equals(item.getAccountType())) {
					vcAccount = item;
				}
			}

		}
		// 退款逻辑计算开始
		TrxLog trxLog = null;
		trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.REFUNDTOACT);
		if (RefundSourceType.TIMING.equals(refundSourceType)) {
			
			logTitle="过期自动退款系统自动审批通过;账户退款成功";
			trxLog.setTrxlogSubType(TrxlogSubType.EXPIRED_AUTO);
			
		} else if(RefundSourceType.OVERRUN.equals(refundSourceType)){
			
			logTitle="超限自动申请到账户;账户退款成功";
			trxLog.setTrxlogSubType(TrxlogSubType.OVERRUN_AUTO);
			
		}else  if(RefundSourceType.PARTNER.equals(refundSourceType)){

			logTitle="分销商订单系统自动审批;账户退款成功";
			trxLog.setTrxlogSubType(TrxlogSubType.PARTNER);
			
		}else{
			
			logTitle="运营审批通过;账户退款成功";
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("审批人:" + operator + ";账户退款金额：" + needRudAmount+ ";可申请银行卡退款金额：");
		
		
		/*******优惠劵不退款。start add by liuqg 2012-10-30*****************/
		needRudAmount=refundCouponToActVc(actVcPayment, actVcSucRudAmount, needRudAmount, vcAccount, isNeedActHis,
		        rudRecordId, trxId, refundHandleType, operator,trxGoodsId);
		logger.info("++++++++ needRudAmount :" +needRudAmount+",trxGoodsId:"+trxGoodsId);
		//重新查询取版本号
        vcAccount = accountDao.findById(vcAccount.getId());
		/*****************优惠劵不退款 end add by liuqg 2012-10-30***********/
		
		/**
         * 退款入账顺序PAYCAHS->ACTCASH-->ACTVC
         */
		//********************************退款入账部分**************************
		if(needRudAmount>0){
		    if (unRudPayCashAmount >= needRudAmount) {
	            //1.只添加一条paycash的情况(未退款的银行支付金额大于本次退款金额，只退到paycash)
	            Long payCashrudDetailId =addRefundDetail(rudRecordId, payCashPayment, needRudAmount, refundHandleType, operator, PaymentType.PAYCASH);
	            // 帐务历史及入账
	            // 此处TRXID为真正的TrxId。便于分组展示给用户
	            if(isNeedActHis){
	                accountService.credit(cashAccount, needRudAmount,
	                        ActHistoryType.REFUND, rudRecordId, trxId, new Date(),
	                        payCashrudDetailId + ":" + payCashPayment.getId()+ "-在线支付金额全部退款到账户", true, "paycash refund act");
	            }
	            // 业务日志
	            sb.append(Amount.cutOff(needRudAmount, 2));

	        } else {
	            //未退款的银行支付金额小于本次退款金额
	            // 得到差额.差额一定是大于0的
	            double gapPayCashAamount = Amount.sub(needRudAmount,unRudPayCashAmount);//先将paycash的退款入账,差额是需要虚拟账户的部分
	            if (unRudPayCashAmount > 0) { // 2.此时paycash还有钱可退(未退款的银行支付金额大于0，先将paycash的退款入账)
	                Long payCashrudDetailId =addRefundDetail(rudRecordId, payCashPayment, unRudPayCashAmount, refundHandleType, operator, PaymentType.PAYCASH);
	                // 帐务历史及入账
	                // 此处TRXID为真正的TrxId。便于分组展示给用户
	                if(isNeedActHis){
	                    accountService.credit(cashAccount, unRudPayCashAmount,ActHistoryType.REFUND, rudRecordId, trxId,new Date(),
	                                payCashrudDetailId + ":"+ payCashPayment.getId()+ "-在线支付金额部分部退款到账户", true,"paycash refund act");
	                }
	                // 业务日志
	                sb.append(Amount.cutOff(unRudPayCashAmount, 2));
	            }

	            // 乐观锁，重新拿版本号
	            Account cashAccount2 = accountService.findById(cashAccount.getId());
	            if (unRudActCashAmount >= gapPayCashAamount) { //3.未退款的actcash金额足够，只退到actcash即可
	                Long payCashrudDetailId = addRefundDetail(rudRecordId, actCashPayment, gapPayCashAamount, refundHandleType, operator, PaymentType.ACTCASH);
	                // 帐务历史及入账
	                // 此处TRXID为真正的TrxId。便于分组展示给用户
	                if(isNeedActHis){
	                    accountService.credit(cashAccount2, gapPayCashAamount,
	                            ActHistoryType.REFUND, rudRecordId, trxId, new Date(),
	                            payCashrudDetailId + ":" + actCashPayment.getId()+ "-账户现金退款到账户", true, "actcash refund act");
	                }
	                // 业务日志
	                sb.append(0);

	            } else {//4.未退款的actcash金额比需要退款的金额小
	                double gapActCashAamount = Amount.sub(gapPayCashAamount,
	                        unRudActCashAmount);
	                if (unRudActCashAmount > 0) {//5.先将能退到actcash的退款入账到actcash，剩余部分退款入账到actvc里
	                    Long payCashrudDetailId =addRefundDetail(rudRecordId, actCashPayment, unRudActCashAmount, refundHandleType, operator, PaymentType.ACTCASH);
	                    // 帐务历史及入账
	                    // 此处TRXID为真正的TrxId。便于分组展示给用户
	                    if(isNeedActHis){
	                        accountService.credit(cashAccount2, unRudActCashAmount,
	                                ActHistoryType.REFUND, rudRecordId, trxId,
	                                new Date(), payCashrudDetailId + ":"
	                                + actCashPayment.getId() + "-账户现金退款到账户",
	                                true, "actcash refund act");
	                    }
	                    // 业务日志
	                    sb.append(0);

	                }
	                if (actVcPayment != null) {
	                    // 剩下就从actVc里退
	                    Long payCashrudDetailId =addRefundDetail(rudRecordId, actVcPayment, gapActCashAamount, refundHandleType, operator, PaymentType.ACTVC);
	                    // 帐务历史及入账
	                    // 此处TRXID为真正的TrxId。便于分组展示给用户
	                    if (isNeedActHis) {
	                            accountService.credit(vcAccount, gapActCashAamount,
	                                    ActHistoryType.REFUND, rudRecordId, trxId,
	                                    new Date(), payCashrudDetailId + ":"+ actVcPayment.getId() + "-账户虚拟币退款到账户",
	                                    true, "vc refund to act");
	                        // 子账户退款入款(递归)
	                        subAccountService.creditByRefund(vcAccount.getId(),gapActCashAamount, payCashrudDetailId, trxId,"vc refund to act");
	                    
	                    }
	                    // 业务日志
	                    sb.append(0);
	                }
	            }

	        }
		}
		//********************************退款入账部分**************************
		
		// 更新退款记录
		RefundRecord refundRecordResult = refundRecordDao.findById(rudRecordId);

		refundRecordDao.updateByIdAndRefundStatus(refundRecordResult.getId(),
				RefundStatus.REFUNDTOACT, refundRecordResult.getVersion());

		// 销毁凭证-同步销毁凭证库(包括同步trxgoods中凭证状态）
		trxorderGoods.setTrxStatus(TrxStatus.REFUNDTOACT);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		voucherService.destoryVoucher(trxorderGoods.getVoucherId());
		try {

			sb.append(";附言：" + description);
			trxLog.setLogTitle(logTitle);
			trxLog.setLogContent(sb.toString());
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			logger.error(e);

		}
		//CPS tuan800 退款成功 by janwen
		reufundToCpsReq(trxGoodsId,trxorderGoods.getTrxGoodsSn(),trxId);
		return trxorderGoods;
	}

	public void processApplyForRefundToBank(Long trxGoodsId, String operator,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, AccountException, StaleObjectStateException {

		checkRefundToBank(trxGoodsId);

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);

		if (TrxStatus.RECHECK.equals(trxorderGoods.getTrxStatus())) {

			throw new RefundException(BaseException.REFUND_RECORD_APPLY_HAVED);// 已经申请过，财务复核中
		}

		// 前置条件--第一次申请
		if (!TrxStatus.REFUNDTOACT.equals(trxorderGoods.getTrxStatus())) {

			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}

		// 同步商品订单中状态--复核中（财务处理中）
		trxorderGoods.setTrxStatus(TrxStatus.RECHECK);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		try {

			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(),
					TrxLogType.RECHECK, "银行卡原路返回退款申请", "申请人:" + operator+ ";附言：" + description);
			
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			logger.error(e);

		}

	}

	public void processRefuseForRefundToBank(Long trxGoodsId, String operator,
			String description) throws RefundException,
			StaleObjectStateException {

		// 同步商品订单中状态--复核中（财务处理中）

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);

		if (!TrxStatus.RECHECK.equals(trxorderGoods.getTrxStatus())) {
			throw new RefundException(BaseException.REFUND_STATUS_INVALID);

		}
		trxorderGoods.setTrxStatus(TrxStatus.REFUNDTOACT);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		try {

			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(),
					TrxLogType.RECHECK_REFUSE, "财务审批拒绝", "审批人:" + operator + ";附言："+ description);
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			logger.error(e);

		}
	}

//	public TrxorderGoods noTscRefundToBank(Long trxGoodsId, String operator, String description) throws RefundException, AccountException, StaleObjectStateException {
//
//		/**
//		 * 查询在退款明细中有无此ID且是否为账户退款成功
//		 * 是否是PAYCASH或者ACTCASH
//		 * 如果是ACTCASH ，则直接从账户历史中找到此前的LOADPAYMNET 同步到退款明细，并进行退款
//		 * 如果是PAYCASH,则直接退款
//		 */
//
//		//检查退款信息的有效性
//		RefundDetail refundDetail = checkRefundToBank(trxGoodsId);
//		
//		//查询商品订单表的前置条件
//		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
//		if (!TrxStatus.RECHECK.equals(trxorderGoods.getTrxStatus())) {
//			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
//		}
//		
//		
//		/*
//		 * 根据最新时间重新生成新的退款请求号（部分机构需要退款请求号和当前日期必须匹配）
//		 */
//		String oldProRefundReqId = refundDetail.getProRefundrequestId();	// 此前生成的渠道退款请求订单号
//		String subStrPro = oldProRefundReqId.substring(0, 8);				// 此前的前缀
//		String newProRefundReqId = oldProRefundReqId.replace(subStrPro, DateUtils.toString(new Date(), "yyyyMMdd"));// 覆盖前缀，生成当日的前缀。即生成新退款请求订单号
//		refundDetail.setProRefundrequestId(newProRefundReqId);   		//设置新的退款请求订单号
//		
//		// 根据rudRecord查用户id，再去查账户
//		RefundRecord refundRecord = refundRecordDao.findById(refundDetail.getRudRecordId());
//		//查询支付信息，用于向银行发送退款请求数据组装
//		Payment payment = paymentDao.findById(refundDetail.getPaymentId());
//		
//		/*
//		 * 向银行发送退款请求之前处理，更新退款明细表状态为退款处理中，扣除现金账户中的退款金额
//		 */
//		processBeforeRefundToBank(refundDetail,refundRecord,trxorderGoods);
//		
//		/*
//		 * 向银行发送退款请求并得到银行的响应
//		 */
//		OrderInfo refundRspOrderInfo = sendRefundReqToBank(refundDetail,payment,trxorderGoods,operator,description);
//		
//		/*
//		 * 根据银行返回的响应退款结果，进行退款成功、失败的处理
//		 */
//		processAfterRefundToBank(refundRspOrderInfo,refundDetail.getId(),trxorderGoods,operator,description);
//		
//		
//		return trxorderGoods;
//	}
	
	/**
	 * 退款到银行之前处理，更新RefundDetail为退款处理中状态，扣除账户中的退款金额
	 *
	 * @param refundDetail
	 */
	public RefundReqInfo processBeforeRefundToBank(Long trxorderGoodsId) throws StaleObjectStateException,AccountException,RefundException{
		
		/**
		 * 查询在退款明细中有无此ID且是否为账户退款成功
		 * 是否是PAYCASH或者ACTCASH
		 * 如果是ACTCASH ，则直接从账户历史中找到此前的LOADPAYMNET 同步到退款明细，并进行退款
		 * 如果是PAYCASH,则直接退款
		 */
		//检查退款信息的有效性
		RefundDetail refundDetail = checkRefundToBank(trxorderGoodsId);
		
		//查询商品订单表的前置条件
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxorderGoodsId);
		if (!TrxStatus.RECHECK.equals(trxorderGoods.getTrxStatus())) {
			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}
		
		/*
		 * 根据最新时间重新生成新的退款请求号（部分机构需要退款请求号和当前日期必须匹配）
		 */
		String oldProRefundReqId = refundDetail.getProRefundrequestId();	// 此前生成的渠道退款请求订单号
		String subStrPro = oldProRefundReqId.substring(0, 8);				// 此前的前缀
		String newProRefundReqId = oldProRefundReqId.replace(subStrPro, DateUtils.toString(new Date(), "yyyyMMdd"));// 覆盖前缀，生成当日的前缀。即生成新退款请求订单号
		refundDetail.setProRefundrequestId(newProRefundReqId);   		//设置新的退款请求订单号
		
		// 根据rudRecord查用户id，再去查账户
		RefundRecord refundRecord = refundRecordDao.findById(refundDetail.getRudRecordId());
		//查询支付信息，用于向银行发送退款请求数据组装
		Payment payment = paymentDao.findById(refundDetail.getPaymentId());
		
		
		
		double paymentAmount = refundDetail.getPaymentAmount();  //支付金额
		double amount = refundDetail.getAmount();   			 //退款金额
		logger.info("++++++++++handle before refund to bank+++++++++refundDetail.id=" + refundDetail.getId() +"+++refundDetail.proRefundrequestId=" + refundDetail.getProRefundrequestId() 
				+ "+++++refundDetail.paymentAmount=" + paymentAmount + "+++++refundDetail.amount=" + amount + "++++++++++++");
		
		// 更新商品订单，已退款到银行卡
		trxorderGoods.setTrxStatus(TrxStatus.REFUNDTOBANK);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		
		//更新卡账户信息
		Account cashAacount = accountService.findByUserIdAndType(refundRecord.getUserId(), AccountType.CASH);
		accountService.debit(cashAacount, amount,ActHistoryType.REFUNDTOBANK, refundDetail.getId(), refundRecord.getTrxOrderId(), new Date(), 
					refundDetail.getId() + ":"+ paymentAmount + "中" + amount + "退款到银行卡", true,"refund to bank");
		//更新refund_detail退款状态为--退款处理中
		// 更新商品订单，已退款到银行卡
		
		refundDetailDao.updateByIdAndProStatus(refundDetail.getId(),RefundStatus.REFUNDINHANDLE, new Date(), refundDetail.getProRefundrequestId(),refundDetail.getVersion(),RefundStatus.INIT);

		RefundReqInfo refundReqInfo = new RefundReqInfo();
		refundReqInfo.setRefundDetail(refundDetail);
		refundReqInfo.setRefundRecord(refundRecord);
		refundReqInfo.setPayment(payment);
		refundReqInfo.setTrxorderGoods(trxorderGoods);
		return refundReqInfo;
	}
	
	
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
	public OrderInfo sendRefundReqToBank(RefundReqInfo refundReqInfo) throws RefundException{
		RefundDetail refundDetail = refundReqInfo.getRefundDetail();
		Payment payment = refundReqInfo.getPayment();
		TrxorderGoods trxorderGoods = refundReqInfo.getTrxorderGoods();
		String operator = refundReqInfo.getOperator();
		String description = refundReqInfo.getDescription();
		/*
		 * 向银行发送退款请求，并获得银行响应
		 */
		OrderInfo refundRspOrderInfo=null;
		try {
			OrderInfo refundOrderInfo = new OrderInfo();
			refundOrderInfo.setRefundRequestId(refundDetail.getProRefundrequestId());	//退款请求号
			refundOrderInfo.setProExternalId(refundDetail.getProExternalId());			//支付机构流水号
			refundOrderInfo.setRefundReqAmount(refundDetail.getAmount() + "");			//退款金额
			/*
			 * modify by wangweijie 4 新老账号更换
			 * 2012-07-26
			 */
			refundOrderInfo.setPaymentId(payment.getId());	//在OrderInfo中加入了paymentId字段
			refundOrderInfo.setPayRequestId(payment.getPayRequestId());	//
			/*
			 * end modify 
			 */
			
			// 获取支付机构名称，分发退款通道
			refundOrderInfo.setProviderType(EnumUtil.transEnumToString(payment.getProviderType()));
			PaymentInfoGeneratorService paymentInfoGeneratorService = paymentInfoGeneratorFactory.getPaymentInfoGeneratorService(refundOrderInfo);

			refundRspOrderInfo = paymentInfoGeneratorService.refundByTrxId(refundOrderInfo);
			
			logger.info("++++++++++++proExternalId:"+ refundDetail.getProExternalId() + "++++++proRefundRequestId:" + refundDetail.getProRefundrequestId()
					+ "++++++++++++refundAmout:" + refundDetail.getAmount() + "++++++" + refundRspOrderInfo.getRefundRspAmount() + "++++refundRspStatus:"+ refundRspOrderInfo.getRefundStatus() + "++++++++++++++");
			
		} catch (Exception e) {
			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.REFUNDTOBANK,
					"银行卡原路返回退款异常，疑似成功，请联系技术处理", "审批人:" + operator + ";退款金额："+ refundDetail.getAmount() + "附言：" + description);
			try {
				trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
				trxLogDao.addTrxLog(trxLog);
			} catch (Exception e1) {
				logger.error(e1);
			}

			logger.error(e);
			throw new RefundException(BaseException.REFUND_TO_BANK_SUC_OR_FAILED); // 资金风险
		}
		
		return refundRspOrderInfo;
	}

	/**
	 * 根据银行返回的响应退款结果，进行退款成功、失败的处理
	 * @param refundRspOrderInfo
	 * @param refundDetail
	 * @param trxorderGoods
	 * @param operator
	 * @param description
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods processAfterRefundToBank(OrderInfo refundRspOrderInfo,RefundReqInfo refundReqInfo) throws StaleObjectStateException{
		
		RefundDetail refundDetail = refundReqInfo.getRefundDetail();
		TrxorderGoods trxorderGoods = refundReqInfo.getTrxorderGoods();
		String operator = refundReqInfo.getOperator();
		String description = refundReqInfo.getDescription();
		
		
		TrxLog trxLog = null;
		//乐观锁问题，需要重新查询一次
		refundDetail = refundDetailDao.findById(refundDetail.getId());
//		trxorderGoods = trxorderGoodsDao.findById(trxorderGoods.getId());
		
		if ("SUCCESS".equals(refundRspOrderInfo.getRefundStatus())) {
			// 如果退款成功.。更新退款明细中支付机构退款状态，并出帐
			refundDetail.setProRefundStatus(RefundStatus.REFUNDTOBANK);

			// 更新退款明细,操作时间和支付渠道退款请求号
			refundDetailDao.updateByIdAndProStatus(refundDetail.getId(),RefundStatus.REFUNDTOBANK, new Date(), refundDetail.getProRefundrequestId(),refundDetail.getVersion(),RefundStatus.REFUNDINHANDLE);
			
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.REFUNDTOBANK,
					"财务审批通过;银行卡原路返回退款成功", "审批人:" + operator + ";退款金额：" + refundDetail.getAmount() + "附言：" + description);
			
		}else {
			// 更新退款明细,操作时间和支付渠道退款请求号
			refundDetailDao.updateByIdAndProStatus(refundDetail.getId(),RefundStatus.FAILED, new Date(), refundDetail.getProRefundrequestId(),refundDetail.getVersion(),RefundStatus.REFUNDINHANDLE); 
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.REFUNDTOBANK,
					"财务审批通过;银行卡原路返回退款失败", "审批人:" + operator + ";退款金额：" + refundDetail.getAmount() + "附言：" + description + ";失败原因错误码:" + refundRspOrderInfo.getRefundRspCode());
			trxorderGoods = null;//短信部分需要此参数判断
		}

		try {
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {
			logger.error(e);
		}
		return trxorderGoods;
	}
	
	
	/**
	 * 退款到账户0元特殊处理
	 * 
	 * @param trxGoodsId
	 * @param operatorId
	 * @param rudRecordId
	 * @param refundSourceType
	 * @param refundHandleType
	 * @throws RefundException
	 * @throws AccountException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 */
	@SuppressWarnings("unused")
	 public void processToActByLottery(Long trxGoodsId, String operator,
			RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description)
			throws RefundException, AccountException, VoucherException,
			RuleException, StaleObjectStateException {

		// 校验金额
		// 插入/插入
		// 入账。更新
		// todo Auto-generated method stub

		checkRefundToAct(trxGoodsId);// 合法性检查

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);

		boolean isDisActHis = trxRuleService.resolveTrxRule(trxorderGoods
				.getTrxRuleId(), TrxRuleUtil.ACTHIS); // 常规交易/0元抽奖/秒杀/打折引擎类型区分

		if (!TrxStatus.REFUNDACCEPT.equals(trxorderGoods.getTrxStatus())) {

			throw new RefundException(BaseException.REFUND_STATUS_INVALID);
		}

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());

		// 开始记录退款记录. 并冗余数据
		RefundRecord refundRecord = new RefundRecord(trxOrder.getId(), trxOrder
				.getUserId(), trxGoodsId, trxOrder.getCreateDate(), trxOrder
				.getCloseDate(), new Date(), trxorderGoods.getGoodsName(),
				trxOrder.getOrdAmount(), trxorderGoods.getPayPrice());
		refundRecord.setRefundStatus(RefundStatus.INIT);
		refundRecord.setOperator(operator);
		refundRecord.setHandleType(refundHandleType);
		refundRecord.setRefundSourceType(refundSourceType);
		refundRecord.setDescription(description);
		Long rudRecordId = refundRecordDao.addRefundRecord(refundRecord);

		Long trxId = trxOrder.getId();
		Long userId = trxOrder.getUserId();

		// double ordAmount = 0;

		double actVcAmount = 0;
		
		double actCashAmount = 0;
		double payCashAmount = 0;

		double needRudAmount = 0L;// 需退款金额

		Payment actVcPayment = null;
		Payment actCashPayment = null;
		Payment payCashPayment = null;
		List<Payment> paymentList = paymentDao.findByTrxId(trxId);

		if (paymentList.size() > 0 && paymentList != null) {

			for (Payment item : paymentList) {

				if (PaymentType.ACTVC.equals(item.getPaymentType())) {
					// 账户里的虚拟币
					actVcPayment = item;
					actVcAmount = actVcPayment.getTrxAmount();
				}

				if (PaymentType.ACTCASH.equals(item.getPaymentType())) {
					// 账户里的余额现金
					actCashPayment = item;
					actCashAmount = actCashPayment.getTrxAmount();
				}
				if (PaymentType.PAYCASH.equals(item.getPaymentType())) {
					// 支付的现金
					payCashPayment = item;
					payCashAmount = payCashPayment.getTrxAmount();
				}

			}
		}

		logger
				.info("+++++++++++trxGoodsId:" + trxGoodsId
						+ "+++++++++++++++++");

		List<Account> accountList = accountService.findByUserId(userId);
		Account cashAccount = null;
		Account vcAccount = null;
		if (accountList.size() > 0 && accountList != null) {
			for (Account item : accountList) {

				if (AccountType.CASH.equals(item.getAccountType())) {
					cashAccount = item;
				}

				if (AccountType.VC.equals(item.getAccountType())) {
					vcAccount = item;
				}
			}

		}
		// 退款逻辑计算开始
		TrxLog trxLog = null;
		
		if (RefundSourceType.TIMING.equals(refundSourceType)) {
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn() , new Date(), TrxLogType.REFUNDTOACT,
					"0元特殊处理过期自动退款系统自动审批通过;账户退款成功");
			trxLog.setTrxlogSubType(TrxlogSubType.EXPIRED_AUTO);
		}else if(RefundSourceType.OVERRUN.equals(refundSourceType)){
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.REFUNDTOACT,
			"超限自动申请到账户;账户退款成功");
			trxLog.setTrxlogSubType(TrxlogSubType.OVERRUN_AUTO);
		} else {
			trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn() + "", new Date(), TrxLogType.REFUNDTOACT,
					"运营审批通过;账户退款成功");
			trxLog.setTrxlogSubType(TrxlogSubType.MANUAL);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("审批人:" + operator + ";账户退款金额：" + needRudAmount
				+ ";可申请银行卡退款金额：");

		String baseSn3 = guidGenerator.gainCode("RB");// 
		logger.info("++++++++baseSn" + baseSn3 + "+++++++++");

		String refundBatchId4 = processGeneratCode();// 获取业务相关订单或者流水

		RefundDetail rudActVcRefundDetail = new RefundDetail(rudRecordId,
				actVcPayment.getId(), new Date(), RefundStatus.REFUNDTOACT,
				actVcPayment.getTrxAmount());
		rudActVcRefundDetail.setAmount(0L);
		rudActVcRefundDetail.setRefundBatchId(refundBatchId4);

		rudActVcRefundDetail.setHandleType(refundHandleType); // 操作类型
		rudActVcRefundDetail.setOperator(operator);// 操作者
		// rudActVcRefundDetail.setRefundSourceType(refundSourceType);
		// // 申请来源

		rudActVcRefundDetail.setPaymentType(PaymentType.ACTVC);
		Long payCashrudDetailId = refundDetailDao
				.addRefundDetail(rudActVcRefundDetail);

		// 帐务历史及入账
		// 此处TRXID为真正的TrxId。便于分组展示给用户
		accountService.credit(vcAccount, 0L, ActHistoryType.REFUND,
				rudRecordId, trxId, new Date(), payCashrudDetailId + ":"
						+ actVcPayment.getId() + "0元特殊处理退款到账户", isDisActHis,
				"refund to act");
		// 业务日志
		sb.append(0);

		// 更新退款记录
		RefundRecord refundRecordResult = refundRecordDao.findById(rudRecordId);

		refundRecordDao.updateByIdAndRefundStatus(refundRecordResult.getId(),
				RefundStatus.REFUNDTOACT, refundRecordResult.getVersion());

		// 销毁凭证-同步销毁凭证库(包括同步trxgoods中凭证状态）
		trxorderGoods.setTrxStatus(TrxStatus.REFUNDTOACT);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		voucherService.destoryVoucher(trxorderGoods.getVoucherId());

		try {

			sb.append(";附言：" + description);
			trxLog.setLogContent(sb.toString());
			trxLogDao.addTrxLog(trxLog);

		} catch (Exception e) {
			logger.error(e);

		}

	}

	/**
	 * 常规退款到银行卡合法性检查
	 * 
	 * @param trxGoodsId
	 * @return
	 * @throws RefundException
	 * @throws AccountException
	 */
	public RefundDetail checkRefundToBank(Long trxGoodsId) throws RefundException, AccountException {

		// 退款明细中查询该笔商品订单中有无现金
		RefundDetail payCashRefundDetail = null;
		RefundRecord refundRecord = refundRecordDao.findByTrxGoodsId(trxGoodsId);
		List<RefundDetail> refundDetailList = refundDetailDao.findByRefundRecId(refundRecord.getId());
		
		if (refundDetailList.size() > 0 && refundDetailList != null) {
			for (RefundDetail item : refundDetailList) {
				if (PaymentType.PAYCASH.equals(item.getPaymentType())) {
					payCashRefundDetail = item;
					break;
				}
			}
		}

		if (payCashRefundDetail == null) { // 说明该笔商品订单中无支付的现金。申请失败。
			throw new RefundException(BaseException.REFUND_APPLY_FROBACK_NOT_PAYCASH);
		}
		//初始化状态
		else if(RefundStatus.INIT.compareTo(payCashRefundDetail.getProRefundStatus())==0){
			// 查询用户现金账户余额是不是充足
			double cashBalance = accountService.findBalanceByType(refundRecord.getUserId(), AccountType.CASH);
			if (payCashRefundDetail.getAmount() > cashBalance) {
	
				/*
				 * 变更为后台外围限制 trxorderGoods.setTrxStatus(TrxStatus.REFUNDTOACT);//
				 * 如果现金余额不足，则商品订单状态回到“退款到账户”
				 * trxorderGoodsDao.updateTrxGoods(trxorderGoods);
				 */
				throw new AccountException(BaseException.ACCOUNT_NOT_ENOUGH);
			}
		}
		//退款到银行状态
		else if (RefundStatus.REFUNDTOBANK.compareTo(payCashRefundDetail.getProRefundStatus())==0) {
			throw new RefundException(BaseException.REFUND_SUCCESS_HAVED); // 退款成功或者过失败过
		}
		//退款失败状态
		else if (RefundStatus.FAILED.compareTo(payCashRefundDetail.getProRefundStatus())==0) {
			throw new RefundException(BaseException.REFUND_FAILED_HAVED); // 退款成功或者过失败过
		}

		//查看退款状态是否是REFUNDINHANDLE，退款处理中（代表该交易上一次退款时到系统处理异常或者银行端处理失败）
		//不需要检查账户金额（账户中y额已扣）
		else if(RefundStatus.REFUNDINHANDLE.compareTo(payCashRefundDetail.getProRefundStatus())==0){
			throw new RefundException(BaseException.REFUND_FAILED_HAVED); // 退款成功或者过失败过
		}else{
			throw new RefundException(BaseException.REFUND_APPLY_FROBACK_NOT_PAYCASH);
		}

		return payCashRefundDetail;
	}

	/**
	 * 检查账户退款是否合法 查出有无此trx-goodsBYid 有无退过或者有无申请成功过
	 * 
	 * @param trxGoodsId
	 * @return
	 * @throws RefundException
	 */
	public RefundRecord checkRefundToAct(Long trxGoodsId)
			throws RefundException {
		if (trxGoodsId == null) {

			throw new IllegalArgumentException("trxGoodsId is null");
		}
		logger.info("++++++++++++++the trxGoodsId is:" + trxGoodsId+ "+++++++++++++");
	

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
		if (trxorderGoods == null) {
			throw new RefundException(BaseException.TRXORDERGOODS_NOT_FOUND);
		}
	
		// 查找是否有此退款记录
		RefundRecord refundRecord = refundRecordDao.findByTrxGoodsId(trxGoodsId);

		if (refundRecord != null) {

			if (RefundStatus.REFUNDTOACT.equals(refundRecord.getRefundStatus())) {
				throw new RefundException(BaseException.REFUND_SUCCESS_HAVED);
			}
		}

		return refundRecord;
	}

	/**
	 * 生成序列号以及备用序列号
	 * 
	 * @return
	 */
	public String processGeneratCode() {
		// 生成序列号 //防止新起的事务回滚，导致了外部事物回滚。并使用序列号生成替代方案
		String refundBatchId = null;
		try {
			String baseSn = guidGenerator.gainCode("RUD");// 
			logger.info("++++++++baseSn:" + baseSn + "+++++++++");

			refundBatchId = GuidEncryption.encryptSimpler("RUD", baseSn
					.substring(3, baseSn.length() - 1)); // 以refundQeqId为基数生成序列号，保证唯一
		} catch (Exception e) {
			logger.error(e);
			refundBatchId = StringUtils.getSysTimeRandom();
		}

		return refundBatchId;

	}

	/*
	 * 购买超限退款实现 (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.RefundService#processToActByPayLimit(java.
	 * lang.Long, java.lang.String, com.beike.common.enums.trx.RefundSourceType,
	 * com.beike.common.enums.trx.RefundHandleType, java.lang.String)
	 */
	public TrxorderGoods processToActByPayLimit(Long trxGoodsId,
			String operator, RefundSourceType refundSourceType,
			RefundHandleType refundHandleType, String description) {
		TrxorderGoods trxorderGoods = null;
		try {
			processApplyForRefundToAct(trxGoodsId, operator,refundSourceType, refundHandleType,description);
			
			trxorderGoods = processToAct(trxGoodsId, operator,refundSourceType, refundHandleType, description);

		} catch (Exception e) {
			logger.debug("+++trxGoodsId:" + trxGoodsId + e);
			alertMerchantVoucher(trxGoodsId, e.toString());
			e.printStackTrace();
			return null;
		}

		return trxorderGoods;

	}

	/**
	 * 购买超限退款异常邮件报警方法
	 * 
	 * @param guestId
	 * @param branchId
	 */
	public void alertMerchantVoucher(Long trxGoods, String brrer) {

		// 发送内部报警邮件
		String alertEmailParams[] = { String.valueOf(trxGoods), brrer };
		if (refundPaylimitCountEmail != null
				&& refundPaylimitCountEmail.length() > 0) {
			String[] alertVcActDebitEmailAry = refundPaylimitCountEmail
					.split(",");
			int alertEmailCount = alertVcActDebitEmailAry.length;

			try {
				for (int i = 0; i < alertEmailCount; i++) {
					emailService.send(null, null, null, null, null, null,
							new String[] { alertVcActDebitEmailAry[i] }, null,
							null, new Date(), alertEmailParams,
							REFUND_COUNT_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return;

	}

	 
	/**
	 * 处理超限退款
	 * @param overRunTgList
	 * @param overRunType
	 * @return
	 * @throws BaseException 
	 */
	public List<TrxorderGoods>  processOverRunRfd(List<TrxorderGoods> overRunTgList,String overRunType) throws BaseException{
		
		if(overRunTgList==null || overRunTgList.size()==0){
			
			return null;
		}
		String description="";
		RefundSourceType refundSourceType = RefundSourceType.OVERRUN;
		RefundHandleType refundHandleType = RefundHandleType.AUTO;
		
		if("TOTAL_OVER_RUN".equals(overRunType)){
			
			description="总量超限退款";
		}else if("SINGLE_OVER_RUN".equals(overRunType)){
			
			description="个人超限退款";
		}
		for (TrxorderGoods rtog : overRunTgList) {
			
			// 调用退款接口
			String trxSnStr = rtog.getTrxGoodsSn();
			trxSnStr = rtog.getTrxGoodsSn();
			logger.info("+++overRun++++++autoToAct++++++"+rtog.getTrxGoodsSn()+"++++"+description+"++++++++");
			TrxorderGoods tog = processToActByPayLimit(rtog.getId(), "系统",refundSourceType, refundHandleType, description);
			Object[] smsParam = new Object[] { trxSnStr };// 短信参数，需确认模板
			if (tog != null) {
				// 发送超限提醒短信，已退款到账户
				smsNotify(tog, TrxConstant.OVERRUN_AUTO_REFUD_SMS_TEMPLATE, smsParam);
			} else {
				// 发送超限提醒短信，方法内部发送邮件
				smsNotify(tog, TrxConstant.OVERRUN_AUTO_REFUD_ERROR_SMS_TEMPLATE,smsParam);
			}
		}

	
		return overRunTgList;
	
		
	}
	 
	
	

	/**
	 * 发送短信接口
	 * 
	 * @param tog
	 * @param smsTemplate
	 * @throws BaseException
	 */
	public void smsNotify(TrxorderGoods tog, String smsTemplate,
			Object[] smsParam) throws BaseException {
		TrxOrder trxOrder = trxOrderDao.findById(tog.getTrxorderId());

		// 获取用户ID
		Long userId = 0L;
		if (trxOrder != null) {
			userId = trxOrder.getUserId();
		}
		Map<String, Object> userMap = userSoaDao.findMobileById(userId);
		String mobile = "";// 手机号
		if (userMap != null) {
			mobile = (String) userMap.get("mobile");
		}
		logger.info("+++++++++++smsVoucher:mobile:" + mobile + "+++++++");

		Sms sms = smsService.getSmsByTitle(smsTemplate);// 获取短信实体
		String template = sms.getSmscontent(); // 获取短信模板

		String contentResult = MessageFormat.format(template, smsParam);
		SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15", "1");
		smsService.sendSms(sourceBean);
	}

	/**
	 * CPS退款请求方法
	 * @param tgId   商品订单Id
	 * @param tgSn 商品订单号
	 * @param trxOrderId  交易订单号
	 */
	public void reufundToCpsReq(Long tgId, String tgSn, Long trxOrderId) {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("trxorder_id", trxOrderId);
			// List<String> goods_sn_list = new ArrayList<String>();
			// goods_sn_list.add("'" + tgSn + "'");
			params.put("trx_goods_sn", tgSn);
			params.put("trxorder_goods_id", tgId);
			// cpsTuan800Service.cancelOrder(params);
			CPSTuan800Thread cpsTuan800Thread = new CPSTuan800Thread(cpsTuan800Service, params, 2);
			cpsTuan800Thread.start();
		} catch (Exception e) {
			logger.debug(e + "+++++++++tgIdtgId" + tgId + "++++trxOrderId" + trxOrderId);
			e.printStackTrace();
		}

	}
	
	
	
	@Override
	public void sendWarningEmail(String content) {
		if (toEmail != null) {
			String[] emails = toEmail.split(",");
			if (emails != null && emails.length > 0) {
				for (String string : emails) {
					try {
						emailService.sendMail(string, sender, content, "Refund Warning Email");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	public TrxorderGoodsDao getTrxorderGoodsDao() {
		return trxorderGoodsDao;
	}

	public void setTrxorderGoodsDao(TrxorderGoodsDao trxorderGoodsDao) {
		this.trxorderGoodsDao = trxorderGoodsDao;
	}

	public TrxOrderDao getTrxOrderDao() {
		return trxOrderDao;
	}

	public void setTrxOrderDao(TrxOrderDao trxOrderDao) {
		this.trxOrderDao = trxOrderDao;
	}

	public PaymentDao getPaymentDao() {
		return paymentDao;
	}

	public void setPaymentDao(PaymentDao paymentDao) {
		this.paymentDao = paymentDao;
	}

	public RefundRecordDao getRefundRecordDao() {
		return refundRecordDao;
	}

	public void setRefundRecordDao(RefundRecordDao refundRecordDao) {
		this.refundRecordDao = refundRecordDao;
	}

	public RefundDetailDao getRefundDetailDao() {
		return refundDetailDao;
	}

	public void setRefundDetailDao(RefundDetailDao refundDetailDao) {
		this.refundDetailDao = refundDetailDao;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public AccountHistoryDao getActHistoryDao() {
		return actHistoryDao;
	}

	public void setActHistoryDao(AccountHistoryDao actHistoryDao) {
		this.actHistoryDao = actHistoryDao;
	}

	public GuidGenerator getGuidGenerator() {
		return guidGenerator;
	}

	public void setGuidGenerator(GuidGenerator guidGenerator) {
		this.guidGenerator = guidGenerator;
	}
	
    
    @Override
    /**
     * 时间段内更新的状态为RECHECK的商品订单明细
     * @param trxStartTime
     * @param trxEndTime
     * @return List<TrxorderGoods>
     */
    public List<TrxorderGoods>  getRefundRecheckTrxgoods(String date) {
        List<TrxorderGoods> list= trxorderGoodsDao.findTrxGoodsByUpDateTimeAndStatus(date, TrxStatus.RECHECK);
       
        return list;
    }
    @Override
    public List<Map<String, Object>> getRefundtoBankTimeOutUPOP(PaymentType paymentType,
            ProviderType providerType, RefundStatus refundStatus, String startDate,String endDate,String payDate) {
        return refundRecordDao.findByPayTypeAndStatusAndDate(paymentType, providerType, refundStatus, startDate,endDate,payDate);
    }

    @Override
    public List<Map<String, Object>> getRefundtoBankTimeOutAlipay(PaymentType paymentType,
            ProviderType providerType, RefundStatus refundStatus, String startDate,String endDate,String payDate) {
        return refundRecordDao.findByPayTypeAndStatusAndDate(paymentType, providerType, refundStatus, startDate,endDate,payDate);
    }
    /**
     * 获得该订单使用优惠卷的payment.没有则返回null
     * @param trxId
     * @return
     */
    @Override
    public Payment getCouponPaymentByTrxId(Long trxId){
        List<Payment> paymentList = paymentDao.findByTrxId(trxId);
        for(Payment payment : paymentList){
            if(PaymentType.ACTVC.equals(payment.getPaymentType()) && payment.getCouponId()>0 ){
                return  payment;
            }
        }
        return null;
    }
    
    /**
     * 添加退款记录明细
     * @param rudRecordId 退款记录id
     * @param payment 支付方式
     * @param refudAmount 需退款金额
     * @param refundHandleType 
     * @param operator
     * @param paymentType 退款payment方式
     * @return
     */
    public Long addRefundDetail(Long rudRecordId,Payment payment,double refudAmount,RefundHandleType refundHandleType,String operator,
            PaymentType paymentType  ){
        String refundBatchId = processGeneratCode();// 获取业务相关订单或者流水
        RefundDetail rudPayCashrefundDetail = new RefundDetail(rudRecordId,
                payment.getId(), new Date(),
                RefundStatus.REFUNDTOACT, payment.getTrxAmount());
        rudPayCashrefundDetail.setAmount(refudAmount);
        rudPayCashrefundDetail.setProExternalId(payment
                .getProExternalId());
        rudPayCashrefundDetail.setRefundBatchId(refundBatchId);
        rudPayCashrefundDetail.setHandleType(refundHandleType); // 处理类型
        rudPayCashrefundDetail.setOperator(operator); // 操作者
        rudPayCashrefundDetail.setPaymentType(paymentType);
        if(PaymentType.PAYCASH.equals(paymentType)){// 支付机构退款请求号,只有paycash时会用到
            rudPayCashrefundDetail.setProRefundStatus(RefundStatus.INIT); // 支付机构退款状态
            rudPayCashrefundDetail.setProRefundrequestId(DateUtils.toString(
                    new Date(), "yyyyMMdd") + refundBatchId); 
        }
       
        // 生成一条refundDetial
        Long payCashrudDetailId = refundDetailDao.addRefundDetail(rudPayCashrefundDetail);
        return payCashrudDetailId;
        
    };
    
   
    /**
     * 退款到优惠券操作,返回值为需要其他方式继续退款的金额
     * @param actVcPayment 本次支付的vc的payment
     * @param actVcSucRudAmount已经退款的虚拟账户金额
     * @param needRudAmount 本次退款的金额
     * @param vcAccount 本次支付的vc账户
     * @param isNeedActHis 是否需要走账
     * @param rudRecordId 退款记录id
     * @param trxId 订单号
     * @param refundHandleType
     * @param operator
     * @return
     * @throws AccountException
     * @throws StaleObjectStateException
     */
    
    public double refundCouponToActVc(Payment actVcPayment,double actVcSucRudAmount,
            double needRudAmount,Account vcAccount,boolean isNeedActHis,Long rudRecordId,Long trxId,
            RefundHandleType refundHandleType,String operator,Long trxGoodsId) throws AccountException, StaleObjectStateException,CouponException{
      
        double restNeedRudAmount=needRudAmount;//优惠券退完后，继续按paycash-->actCash->actVc退款的金额
        if(actVcPayment==null){
            return restNeedRudAmount;
        }
        if(actVcPayment.getCouponId()>0){
            logger.info("+++++++ refund actVcPayment couponId:"+actVcPayment.getCouponId());
            // 查询优惠券
            TrxCoupon trxCoupon = trxCouponService.queryCouponById(actVcPayment.getCouponId());
            double coponPayAmount=0.00;//订单中使用优惠券支付的金额（也可根据付款的trx extend表获取ｖｍ为优惠券类型的金额）
            if(trxCoupon!=null){
                coponPayAmount=trxCoupon.getCouponBalance()<=actVcPayment.getTrxAmount()?trxCoupon.getCouponBalance():actVcPayment.getTrxAmount();
            }else{
                logger.info("+++++++ refund actVcPayment couponId null:"+actVcPayment.getCouponId());
                throw new CouponException(2105);
            }
            /**
             * 判断该订单是否使用过优惠劵,虚拟账户支付时使用过优惠券
             * 已退款的优惠券金额小于优惠券支付金额，代表优惠券部分未退完，需先退到优惠券
             */
            //剩余可退优惠券金额（优惠券支付金额-已退款的虚拟账户金额）
            double canRefundToCouponAmount=Amount.sub(coponPayAmount, actVcSucRudAmount);
            logger.info("+++++++ refund actVcPayment canRefundToCouponAmount :"+canRefundToCouponAmount +" ,coponPayAmount:"+coponPayAmount+",actVcSucRudAmount:"+actVcSucRudAmount);
            if(canRefundToCouponAmount>0){
                double thisRefundToCouponAmount=0;//本次需要退款到虚拟账户的优惠券金额
                if(needRudAmount<=canRefundToCouponAmount){
                    //剩余可退优惠券金额足够本次退款,退完后无需后续退款操作
                    thisRefundToCouponAmount=needRudAmount;
                    restNeedRudAmount=0;
                }else{
                    //剩余优惠券金额不够本次退款
                    thisRefundToCouponAmount=canRefundToCouponAmount;
                    restNeedRudAmount=Amount.sub(needRudAmount, canRefundToCouponAmount);//需要继续按之前的退款流程退款的金额
                }
                logger.info("++++++++ coponPayAmount:"+coponPayAmount+
                        " ,needRudAmount:"+needRudAmount+
                        " ,actVcSucRudAmount:"+actVcSucRudAmount+
                        " ,canRefundToCouponAmount:"+canRefundToCouponAmount+
                        " ,thisRefundToCouponAmount:"+thisRefundToCouponAmount+" ,vcAccountID:"
                        +vcAccount.getId()+" ,trxId:"+trxId +",trxGoodsId:"+trxGoodsId+" ,actVcPaymentID:"+actVcPayment.getId()
                        );
               //查询本次支付的子账户id。入账。然后出账
                List<VmTrxExtend> vmTrxExtendList = vmTrxExtendDao.findByTrxId(vcAccount.getId(), trxId);
                for(VmTrxExtend vmTrxExtend :vmTrxExtendList){
                    logger.info("coupon refund CouponVmAccountId:"+trxCoupon.getVmAccountId()+" ,vmTrxExtend vmId:"+vmTrxExtend.getVmAccountId()+" ,vmTrxExtend amount:"+vmTrxExtend.getAmount());
                    if(RelevanceType.SALES.equals(vmTrxExtend.getRelevanceType()) ){
                      //跟本次优惠券的所用vmAccountId相同，并且交易金额不为0
                        if((trxCoupon.getVmAccountId().longValue()==vmTrxExtend.getVmAccountId().longValue()) && vmTrxExtend.getAmount()>0){
                            logger.info("user cupon vm:"+vmTrxExtend.getVmAccountId());
                            Map<Long, String> vmAccountMap = vmAccountService.findVmAccount(vmTrxExtend.getVmAccountId());
                            String vmAccountStr = vmAccountMap.get(vmTrxExtend.getVmAccountId());
                            if (vmAccountStr != null && !"".equals(vmAccountStr)) {
                                String[] vmArray = vmAccountStr.split("\\|");
                                String notRefund = vmArray[2];
                                if ("0".equals(notRefund)) {//表示不退款
                                    Long subAccountId =vmTrxExtend.getSubAccountId();//个人子账户ID
                                    Long accountId =vcAccount.getId();//个人总账户ID
                                    String subSuffix = accountId.toString().substring(accountId.toString().length()-1);//子账户表_值
                                    SubAccount subAccount = subAccountDao.findById(subAccountId, subSuffix);
                                    if (subAccount != null) {
                                        // 增加退款明细,记录为ACTVC
                                        Long refunddetailCopponId=addRefundDetail(rudRecordId, actVcPayment, thisRefundToCouponAmount, refundHandleType, operator, PaymentType.ACTVC);
                                        //1.子账户入账  原因是refund
                                        subAccountService.creditByRefundCoupon(subAccount, vcAccount, thisRefundToCouponAmount, 
                                                ActHistoryType.REFUND, rudRecordId, trxId, new Date(), refunddetailCopponId + ":"+ actVcPayment.getId()+"-优惠券金额部分退款到账户", isNeedActHis, "coupon refund act");
                                        //2.子账户出账需要做vm取消记录
                                        subAccount = subAccountDao.findById(subAccountId, subSuffix);//重新查询取版本号
                                        vcAccount = accountDao.findById(vcAccount.getId());
                                        // 添加子账户取消历史
                                        VmCancelRecord vmCancelRecord = new VmCancelRecord();
                                        vmCancelRecord.setAccountId(subAccount.getAccountId());
                                        vmCancelRecord.setAmount(thisRefundToCouponAmount);
                                        vmCancelRecord.setCreateDate(new Date());
                                        vmCancelRecord.setOperatorId(0L);
                                        vmCancelRecord.setSubAccountId(subAccount.getId());
                                        vmCancelRecord.setUpdateDate(new Date());
                                        vmCancelRecord.setVmAccountId(subAccount.getVmAccountId());
                                        vmCancelRecord.setCancelType(CancelType.COUPON_INVALID);// 优惠券不退款
                                        Long vmCancelRecordId = vmCancelRecordDao.addVmCancelRecord(vmCancelRecord);
                                        // 优惠券不退款，金额扣除
                                        subAccountService.debitByCancelCouponRefund(subAccount,vcAccount, thisRefundToCouponAmount,ActHistoryType.COUPON_INVALID,
                                                vmCancelRecordId, trxId, new Date(), refunddetailCopponId+ ":"+ actVcPayment.getId()+"优惠券作废",isNeedActHis, "coupon invalid", false);
                                        //优惠券状态改为退款
    //                                    if(!trxCoupon.getCouponStatus().equals(TrxCouponStatus.REFUND)){
    //                                        trxCouponService.updateTrxCouponStatus(TrxCouponStatus.REFUND, trxCoupon.getId(), trxCoupon.getVersion());
    //                                    }
                                     // 增加扩展帐务支付关联记录
                                        VmTrxExtend vmTrxExtendRefund = new VmTrxExtend(vmTrxExtend.getVmAccountId(), actVcPayment.getAccountId(), subAccount.getId(),
                                                vmTrxExtend.getTrxOrderId(), refunddetailCopponId, 1L, actVcPayment.getTrxAmount(), thisRefundToCouponAmount, new Date(),
                                                vmTrxExtend.getLoseDate(), vmTrxExtend.getTrxRequestId(), RelevanceType.REFUND, "coupon refund:"+trxId);
                                        vmTrxExtendDao.addVmTrxExtend(vmTrxExtendRefund);
                                        
                                    }
                                    break;
                                }
                            }
                            
                        }     
                    }
                }
            }
            
        }
        return restNeedRudAmount;
    }

    @Override
    public double getRefundAmountByTrxGoodsId(Long trxGoodsId) {
        TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
        List<Payment> paymentList = paymentDao.findByTrxId(trxorderGoods.getTrxorderId());
        Payment actVcPayment = null;
        if (paymentList.size() > 0 && paymentList != null) {
            for (Payment item : paymentList) {
                if (PaymentType.ACTVC.equals(item.getPaymentType())) {
                    // 账户里的虚拟币
                    actVcPayment = item;
                }
            }
        }
        double needRudAmount=trxorderGoods.getPayPrice();//商品金额
        if(actVcPayment!=null && actVcPayment.getCouponId()>0){
            double  coponPayAmount=0;//优惠券支付金额
            TrxCoupon trxCoupon = trxCouponService.queryCouponById(actVcPayment.getCouponId());
            coponPayAmount=trxCoupon.getCouponBalance()<=actVcPayment.getTrxAmount()?trxCoupon.getCouponBalance():actVcPayment.getTrxAmount();
            double actVcSucRudAmount = refundDetailDao.findSucRudAmtByTrxId(trxorderGoods.getTrxorderId(),
                    PaymentType.ACTVC, RefundStatus.REFUNDTOACT);//已经退款金额
            //剩余可退优惠券金额（优惠券支付金额-已退款的虚拟账户金额）
            double canRefundToCouponAmount=Amount.sub(coponPayAmount, actVcSucRudAmount);
            if(canRefundToCouponAmount>0){
                needRudAmount=Amount.sub(needRudAmount, canRefundToCouponAmount);//本次退款的金额-优惠券可退金额 <=0 表示全用优惠券，大于0.表示可继续退款的部分
            }
        }
        return needRudAmount>0?needRudAmount:0;
    }
    
    /**
	 * 将已校验商品置为退款到账户
	 * 
	 * @param voucher
	 * @param trxorderGoods
	 * @param voucherVrifySource
	 * @param subGuestId
     * @throws BaseException 
	 */
	@Override
	public TrxorderGoods updateUsedByRefundtoact(Voucher voucher, TrxorderGoods trxorderGoods) throws BaseException {

		//凭证状态置为未使用状态
		voucher.setConfirmDate(new Date());
		voucher.setVoucherStatus(VoucherStatus.ACTIVE);
		voucherDao.update(voucher);

		// 加入同步更新trx_order_goods中的凭证状态 //置为未使用状态
		trxorderGoods.setAuthStatus(AuthStatus.SUCCESS);
		trxorderGoods.setTrxStatus(TrxStatus.SUCCESS);

		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		RefundSourceType refundSourceType = RefundSourceType.FILM;
		RefundHandleType refundHandleType = RefundHandleType.AUTO;
		String description = "网票网完成支付失败退款";
		// 调用退款接口
		String trxSnStr = trxorderGoods.getTrxGoodsSn();
		trxSnStr = trxorderGoods.getTrxGoodsSn();
		logger.info("+++overRun++++++autoToAct++++++"+trxorderGoods.getTrxGoodsSn()+"++++"+description+"++++++++");
		TrxorderGoods tog = processToActByPayLimit(trxorderGoods.getId(), "系统",refundSourceType, refundHandleType, description);
		Object[] smsParam = new Object[] { trxSnStr };// 短信参数，需确认模板
		if (tog != null) {
			// 发送退款成功提醒短信，已退款到账户
			smsNotify(tog, TrxConstant.FILM_REFUD_SMS_TEMPLATE, smsParam);
		} else {
			// 发送退款失败提醒短信，方法内部发送邮件
			smsNotify(tog, TrxConstant.FILM_REFUD_ERROR_SMS_TEMPLATE,smsParam);
		}
		return trxorderGoods;
	}
		
	
	/**
	 * 根据商品订单编号查询退款申请记录
	 * @param trxOrderId
	 * @return
	 */
	public RefundRecord queryRefundRecordByTrxorderId(Long trxOrderId){
		return refundRecordDao.findByTrxGoodsId(trxOrderId);
	}
}
