package com.beike.biz.service.hessian.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.biz.service.trx.BizProcessFactory;
import com.beike.biz.service.trx.PaymentInfoGeneratorFactory;
import com.beike.biz.service.trx.ProcessService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PayRspInfo;
import com.beike.common.bean.trx.RefundReqInfo;
import com.beike.common.bean.trx.TrxDataInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.trx.MenuGoodsOrder;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizProcessType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.trx.CachePageType;
import com.beike.common.enums.trx.PartnerApiType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxBizType;
import com.beike.common.enums.trx.TrxStatus;
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
import com.beike.core.service.trx.AccountHistoryService;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.core.service.trx.RebRecordService;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxLogService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.card.CardService;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.discountcoupon.DiscountCouponService;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.core.service.trx.notify.AccountNotifyService;
import com.beike.core.service.trx.notify.NotifyRecordBizService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.partner.PartnerVoucherService;
import com.beike.core.service.trx.partner.PartnerVoucherServiceFactory;
import com.beike.core.service.trx.settle.GuestSettleService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.service.shopcart.ShopCartService;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * @Title: TrxHessianServiceImpl.java
 * @Package com.beike.biz.service.hessian.impl
 * @Description: 交易系统对外Hessian接口
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:18:52 PM
 * @version V1.0
 */
public class TrxHessianServiceImpl implements TrxHessianService
{

	private BizProcessFactory bizProcessFactory;

	private AccountService accountService;
	private RebRecordService rebRecordService;

	private TrxOrderService trxOrderService;

	private VoucherService voucherService;
	private RefundService refundService;

	private PayLimitService payLimitService;

	private VmAccountService vmAccountService;
	private PaymentInfoGeneratorFactory paymentInfoGeneratorFactory;

	private CardService cardService;
	
	private DiscountCouponService discountCouponService;

	private NotifyRecordBizService notifyRecordBizService;

	private AccountHistoryService accountHistoryService;

	private ShopCartService shopCartService;
	

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	private ProcessService orderProcessService;

	private TrxorderGoodsService trxorderGoodsService;

	private PartnerVoucherServiceFactory partnerVoucherServiceFactory;
	
	private PartnerCommonService partnerCommonService;

	private TrxSoaService trxSoaService;

	private TrxLogService trxLogService;
	
	private SubAccountService subAccountService;
	
	private AccountNotifyService accountNotifyService;

	private TrxCouponService trxCouponService;
	
	private GuestSettleService guestSettleService;

	private final Log logger = LogFactory.getLog(TrxHessianServiceImpl.class);

	private static String  buyCountForSalesKey="countGuestIdForSales";
	private static String  usedCountForSalesKey="countSubGuestIdForUsed";
	
	@Override
	public void createAccount(TrxRequestData requestData) throws AccountException
	{
		Long userId = requestData.getUserId();
		accountService.create(userId);

	}

	@Override
	public TrxResponseData getActByUserId(TrxRequestData requestData) throws AccountException
	{
		Long userId = requestData.getUserId();
		double balance = accountService.findBalance(userId);
		TrxResponseData trxResponseData = new TrxResponseData(balance);
		String isSubAccountLose = requestData.getIsSubAccountLose();//是否查询子账户过期信息
		if("1".equals(isSubAccountLose)){
			List<SubAccount> subList =	accountNotifyService.getRemindAccountBalance(userId);
			if(subList!=null&&subList.size()>0){
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<subList.size();i++){
					SubAccount subAccount = subList.get(i);
					double subBalance = subAccount.getBalance();
					Date loseDate = subAccount.getLoseDate();
					sb.append(subBalance);
					sb.append(",");
					sb.append(DateUtils.toString(loseDate,"yyyy-MM-dd"));
					sb.append("|");
				}
				sb.deleteCharAt(sb.lastIndexOf("|"));
				trxResponseData.setSubAccountLose(sb.toString());
			}
		}

		return trxResponseData;
	}
	@Override
	public TrxResponseData getSubAccountByUserId(TrxRequestData requestData) throws AccountException, StaleObjectStateException
	{
		Long userId = requestData.getUserId();
		double balance = accountService.findBalance(userId);
		double trxAmount=requestData.getTrxAmount();
		TrxResponseData trxResponseData = new TrxResponseData(balance);
		String vmAmountListStr = "";//支付前券使用提示
		if(balance>trxAmount){
			Account vcAccount = accountService.findByUserIdAndType(userId,AccountType.VC);
			if(vcAccount!=null){
				if(vcAccount.getBalance()>trxAmount){//满足VC余额大于下单金额
			
					String subSuffix = vcAccount.getId().toString().substring(vcAccount.getId().toString().length()-1);
					// 查询子账户列表
					List<SubAccount> subAccountList = subAccountService.findSubAccountList(vcAccount.getId(),subSuffix);
			
					if (subAccountList == null ||  subAccountList.size()==0) {
						return trxResponseData;
					}
					int count = subAccountList.size();
					int index = count - 1;
					//子账户递归比较 
					vmAmountListStr = calcuSubBalance(trxAmount,subAccountList,index);
					}
			}
		}
		trxResponseData.setVmAmountListStr(vmAmountListStr);
		
		return trxResponseData;
	}

	
	/**
	 * 子账户递归比较
	 * @param trxAmount 比较金额
	 * @param subAccountList 子账户信息
	 * @param index 子账户顺序
	 * @return
	 * @throws StaleObjectStateException
	 */
	public String calcuSubBalance(double trxAmount,List<SubAccount> subAccountList, int index)throws StaleObjectStateException

	{
		SubAccount subAccount = subAccountList.get(index);
		double subActBalance = subAccount.getBalance();//子账户金额
		Long subActId = subAccount.getId();// 子账户ID
		Long vmAccountId = subAccount.getVmAccountId();
		// 计算相邻子账户应扣金额（按过期时间从早到晚排序）
		double gapAmount = Amount.sub(trxAmount, subActBalance);

		logger.info("++++++++calcu:sub  sub credit befor:+++++subActBalance:"
				+ subActBalance + "++++++trxAmount:" + trxAmount
				+ "+++++++subActId" + subActId + "++++vmAccountId:"
				+ vmAccountId + "++++");
		Map<Long, String> vmAccount =	vmAccountService.findVmAccount(vmAccountId);
		String vmAccountStr = vmAccount.get(vmAccountId);
		// 比较子账户余额额
		if (gapAmount > 0) {
			if(vmAccountStr!=null&&!"".equals(vmAccountStr)){
				String[] vmArray = vmAccountStr.split("\\|");
				String change = vmArray[0];
				if("1".equals(change)){
					double sub = subActBalance>trxAmount?trxAmount:subActBalance;
					int trxSum = ((int)sub)/Integer.valueOf(vmArray[1]);
					trxAmount = Amount.sub(trxAmount, subActBalance);
					String temp = calcuSubBalance(trxAmount, subAccountList,index - 1);
					if(temp.equals("")){
						temp = "0.0";
					}
					double calcun = Amount.add(Double.valueOf(temp),trxSum*Double.valueOf(vmArray[1]));
					return calcun+"";//此处必须返回因为是递归
				}else{
					trxAmount = Amount.sub(trxAmount, subActBalance);
					String calcun = calcuSubBalance(trxAmount, subAccountList,index - 1);
					return calcun;//此处必须返回因为是递归
				}
			}
		}else{//跳出递归
			if(vmAccountStr!=null&&!"".equals(vmAccountStr)){
				String[] vmArray = vmAccountStr.split("\\|");
				String change = vmArray[0];
				if("1".equals(change)){
					double sub = subActBalance>trxAmount?trxAmount:subActBalance;
					int trxSum = ((int)sub)/Integer.valueOf(vmArray[1]);
					Double dou = Amount.sub(Double.valueOf(vmArray[1]),trxAmount%Double.valueOf(vmArray[1]));//获取欲回收金额
					if(dou!=0&&dou!=Double.valueOf(vmArray[1])){
						return Amount.add(Double.valueOf(vmArray[1]),trxSum*Double.valueOf(vmArray[1]))+"";
					}else{
						return trxSum*Double.valueOf(vmArray[1])+"";
					}
				}
			}
	}
		return "";
	}
	
	@Override
	public TrxResponseData qryPayStauts(TrxRequestData requestData) throws Exception
	{

		String payRequestId = requestData.getPayRequestId();
		String providerType = requestData.getProviderType();
		String createDate = requestData.getCreateDate();
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setPayRequestId(payRequestId);
		orderInfo.setProviderType(providerType);
		orderInfo.setCreateDate(createDate);
		PaymentInfoGeneratorService paymentInfoGeneratorService = paymentInfoGeneratorFactory.getPaymentInfoGeneratorService(orderInfo);
		OrderInfo resultOrderInfo = paymentInfoGeneratorService.queryByOrder(orderInfo);// 查单结果
		String payResult = resultOrderInfo.getPayStatus();
		String proExternalId = resultOrderInfo.getProExternalId();
		String confirmAmount = resultOrderInfo.getQryAmount();

		TrxResponseData responseData = new TrxResponseData();
		responseData.setPayStatus(payResult);
		responseData.setProExternalId(proExternalId);
		responseData.setPayRequestId(payRequestId);
		responseData.setSucTrxAmount(confirmAmount);

		return responseData;

	}

	/**
	 * 创建交易订单(此方法不需事务，方法名以noTsc打头避开事务)
	 */
	@Override
	public OrderInfo noTscCreateTrxOrder(OrderInfo orderInfo) throws ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException, VoucherException, RuleException, StaleObjectStateException, OrderCreateException,CouponException
	{

		orderInfo.setBizProcessType(BizProcessType.SALES);
		boolean isTrxRetry=orderInfo.isTrxRetry();//是否进行交易重试（默认false）
		OrderInfo orderinfoResult=null;
		ProcessService processService = bizProcessFactory.getProcessService(orderInfo);

		// 交易组注意：AOP方式配置事务，在本类中事务不能增强,即本类事务调用不生效，需在外部将业务方法拆分。

		//若平台有该trxStatus为INIT的交易订单(暂时只对分销商来的订单有效)(OP霸王硬上工式重启时会导致此情况)则进行重试 
		if(isTrxRetry){
			//分销商交易重试
			orderinfoResult = processService.processReTry(orderInfo);
			
		}else{
			// 下单.事务开启/提交
			orderinfoResult = processService.processPro(orderInfo);
		}
		// 如果账户支付余额不足，则支付失败，则支付所在事务回滚，下单事务成功 。
		// 对分销商，需在此做外部交易订单号重复判断，有，不做下单，继续支付；对直接用户，暂时未开放未支付订单二次支付功能，暂忽略。
		// 账户支付或组装网银支付信息.事务开启/提交
		List<TrxorderGoods> tgList = trxOrderService.preQryInWtDBForTg(orderinfoResult.getTrxId());

		orderinfoResult.setTgList(tgList);// 预查询出来的商品订单列表

		OrderInfo orderInfoPost = processService.processPost(orderinfoResult);// 账户支付或组装网银支付信息
		
		//获得非个人限购商品列表，主要针对秒杀商品的限购处理
		List<TrxorderGoods> unSingleOverRunTgList = orderInfoPost.getUnSingleOverRunTgList();	//个人非限购商品列表
		
		List<TrxorderGoods> sendTgList = orderInfoPost.getTgList();// 需发码商品订单List
		List<Payment> paymentList = orderInfoPost.getPaymentList();// 支付记录列表
		
		//下单日志（非重试，第一次下单）
		if (!isTrxRetry) {
			try {
				trxLogService.addTrxLogForCreate(sendTgList);
			} catch (Exception e) {

				logger.debug(e);
				e.printStackTrace();
			}
		}
		
		//下单接口，只有余额支付成功，才进行以下操作
		if("SUCCESS".equals(orderInfoPost.getPayResult())){
			// 写入支付成功业务操作日志
			trxLogService.addTrxLogForSuc(paymentList, sendTgList);
			
			try {
				Long userId = orderinfoResult.getTrxOrder().getUserId();
				
				voucherService.sendVoucherPostPay(sendTgList,userId,orderInfoPost.getOutSmsTemplate()); // 发码
				trxSoaService.updateSalesCount(orderInfoPost.getSaleCountmap());// 销售总量更新
				
				//秒杀商品总量限购，并更新秒杀商品库存
				List<TrxorderGoods> miaoshaTotalOverRunRfdList = trxorderGoodsService.processTotalCountLimitForMiaoSha(unSingleOverRunTgList);
				
				// 秒杀商品总量超限自动退款
				refundService.processOverRunRfd(miaoshaTotalOverRunRfdList, "TOTAL_OVER_RUN");
				
				
			} catch (Exception e) {
				logger.debug(e);
				e.printStackTrace();
			}
		}
		return orderInfoPost;
	}

	/**
	 * 完成交易(此方法不需事务，方法名以noTsc打头避开事务)
	 * 
	 * @param sourceMap
	 * @return
	 * @throws TrxorderGoodsException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 * @throws StaleObjectStateException
	 * @throws RuleException
	 * @throws VoucherException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 */
	@Override
	public TrxResponseData noTscCompleteTrx(TrxRequestData requestData) throws PaymentException, TrxOrderException, AccountException, VoucherException, RuleException, StaleObjectStateException, ProcessServiceException, RebateException, TrxorderGoodsException,CouponException
	{
		TrxResponseData responseData = new TrxResponseData();
		String payRequestId = requestData.getPayRequestId();
		String proExternallId = requestData.getProExternalId();
		String sucTrxAmount = requestData.getSucTrxAmount();
	
		
		if (payRequestId != null && proExternallId != null)
		{
			PayRspInfo payRspInfo = new PayRspInfo();
			payRspInfo.setPayRequestId(payRequestId);
			payRspInfo.setProExternallId(proExternallId);
			payRspInfo.setProConfirmDate(new Date());
			payRspInfo.setBizType("");
			payRspInfo.setSucTrxAmount(Double.parseDouble(sucTrxAmount));

			payRspInfo = trxOrderService.preQryInWtDBForCash(payRspInfo);// 预查询
			
			if (payRspInfo!=null)
			{	
				// 回调入账，如果有乐观锁导致的事务回滚，有支付机构的通知进行重试
				TrxDataInfo trxDateInfo = trxOrderService.achieveByPayAfter(payRspInfo);
				
				try
				{	
					if(trxDateInfo==null){//余额不足自动变更为充值，则无需走以下流程。包括超限信息。
						return responseData;
					}
					List<TrxorderGoods> tgList = trxDateInfo.getAllTgList();
			
					responseData.setTgList(tgList);//删除购物车使用
					responseData.setExtendInfo(payRspInfo.getTrxOrder().getExtendInfo());//加入购物车Id.供外部删除购物车
					List<TrxorderGoods> unSingleOvRunTgList = trxDateInfo.getUnSingleOverRunTgList();// 非个人超限商品订单列表
					List<TrxorderGoods> singleOvRunTgList = trxDateInfo.getSingleOverRunTgList();// 个人超限商品订单列表
					List<TrxorderGoods> allTgList = trxDateInfo.getAllTgList();// 所有商品订单列表
					List<Payment> paymentList = payRspInfo.getPaymentList();// 支付记录列表
					List<TrxorderGoods> totalOverRunRfdList = null;
					
					// 支付成功操作日志
					trxLogService.addTrxLogForSuc(paymentList, allTgList);
					
					// 获取需要退款的商品订单列表map。个人超限必退，无需进入此方法判断
					Map<String, List<TrxorderGoods>> ovRunRfTgListMap = trxorderGoodsService.processGetTotalOverRfdTgListMap(unSingleOvRunTgList);

					// 处理超限退款和正常发码
					if (ovRunRfTgListMap != null && !ovRunRfTgListMap.isEmpty())
					{
						totalOverRunRfdList = ovRunRfTgListMap.get("TOTAL_OVRUN_AUTO_REFUND");// 总量超限退款商品订单列表
						List<TrxorderGoods> noRfdTgList = ovRunRfTgListMap.get("NO_REFUND");// 无需退款，正常发码

						// 总量超限自动退款
						refundService.processOverRunRfd(totalOverRunRfdList, "TOTAL_OVER_RUN");
						Long userId = payRspInfo.getTrxOrder().getUserId();
						// 正常发码
						voucherService.sendVoucherPostPay(noRfdTgList,userId,"");//外部短信模板置为空

					}
					// 个人超限自动退款
					refundService.processOverRunRfd(singleOvRunTgList, "SINGLE_OVER_RUN");

					// 组装支付成功页的超限信息
					payLimitService.appendPostPayLimitDes(totalOverRunRfdList, singleOvRunTgList, payRequestId);

					// 销售总量更新
					trxSoaService.updateSalesCount(trxDateInfo.getSaleCountmap());
					
				} catch (Exception e)
				{
					logger.debug(e);
					e.printStackTrace();
				}

			
			
			
			}
		}
			//超限信息获取
			String payLimitPostPayCacheKey = TrxConstant.PAY_LIMIT_DES_POST_PAY_CACHE_KEY + payRequestId;
			String payLimitDes = (String) memCacheService.get(payLimitPostPayCacheKey);
			payLimitDes = payLimitDes == null ? "" : payLimitDes;
			responseData.setPayLimitDes(payLimitDes);	

			return responseData;
	}

	/**
	 * 账户退款申请
	 * 
	 * @param sourceMap
	 * @return
	 * @throws StaleObjectStateException
	 * @throws RefundException
	 */
	@Override
	public void refundApplyToAct(TrxRequestData requestData) throws RefundException, StaleObjectStateException
	{
		logger.info("+++++++++++++++++refundApplyToAct date:" + new Date() + "++++++++++++++");

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		RefundSourceType refundSourceType = requestData.getRefundSourceType();// 退款来源类型
		RefundHandleType refundHandleType = requestData.getRefundHandleType(); // 退款处理类型
		String desc = requestData.getDescription(); // 描述
		
	
		TrxOrder trxorder=trxOrderService.findByTgId(trxorderGoodsId);
		PartnerInfo partnerInfo=partnerCommonService.qryParterByUserIdInMem(trxorder.getUserId());//获取分销商
		//如果是 58的订单，不可进行退款
		if(partnerInfo!=null&&(PartnerApiType.TC58.name().equals(partnerInfo.getApiType())||
				PartnerApiType.TAOBAO.name().equals(partnerInfo.getApiType())||
				PartnerApiType.BUY360.name().equals(partnerInfo.getApiType())||
				PartnerApiType.TUAN800.name().equals(partnerInfo.getApiType()))){
			
			  throw new RefundException(BaseException.REFUND_STATUS_INVALID);//退款状态无效
			
		}
		
		refundService.processApplyForRefundToAct(trxorderGoodsId, operator, refundSourceType, refundHandleType, desc);

	}

	/**
	 * 账户退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void refundRefuseToAct(TrxRequestData requestData) throws RefundException, StaleObjectStateException
	{

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		String desc = requestData.getDescription(); // 描述

		refundService.processRefuseForRefundToAct(trxorderGoodsId, operator, desc);

	}

	/**
	 * 退款到账户
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void refundToAct(TrxRequestData requestData) throws RefundException, AccountException, VoucherException, RuleException, StaleObjectStateException, VmAccountException,CouponException
	{

		logger.info("+++++++++++++++++refundToAct date:" + new Date() + "++++++++++++++");

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		RefundSourceType refundSourceType = requestData.getRefundSourceType();// 退款来源类型
		RefundHandleType refundHandleType = requestData.getRefundHandleType(); // 退款处理类型
		String desc = requestData.getDescription(); // 描述
		boolean isSendSmsNotify=true;//默认需要短信提提醒
		TrxorderGoods trxorderGoods = refundService.processToAct(trxorderGoodsId, operator, refundSourceType, refundHandleType, desc);
		
		if (trxorderGoods != null)
		{	
			TrxOrder trxorder=trxOrderService.findByTgId(trxorderGoodsId);
			PartnerInfo partnerInfo=partnerCommonService.qryParterByUserIdInMem(trxorder.getUserId());//获取分销商
			//如果是分销商订单，则不进行短信提醒
			if(partnerInfo!=null){
				isSendSmsNotify=false;
			}
			// 短信提醒部分
			notifyRecordBizService.processNotifyByBizType(trxorderGoods, TrxBizType.RETURNACT,isSendSmsNotify);
		}

	}

	/**
	 * 银行卡退款申请
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void refundApplyToBank(TrxRequestData requestData) throws RefundException, AccountException, StaleObjectStateException
	{
		logger.info("+++++++++++++++++refundApplyToBank date:" + new Date() + "++++++++++++++");

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		RefundSourceType refundSourceType = requestData.getRefundSourceType();// 退款来源类型
		RefundHandleType refundHandleType = requestData.getRefundHandleType(); // 退款处理类型
		String desc = requestData.getDescription(); // 描述

		refundService.processApplyForRefundToBank(trxorderGoodsId, operator, refundSourceType, refundHandleType, desc);

	}

	/**
	 * 银行卡退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void refundRefuseToBank(TrxRequestData requestData) throws RefundException, StaleObjectStateException
	{
		logger.info("+++++++++++++++++refundRefuseToBank date:" + new Date() + "++++++++++++++");

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		String desc = requestData.getDescription(); // 描述

		refundService.processRefuseForRefundToBank(trxorderGoodsId, operator, desc);
	}

	/**
	 * 退款到银行卡
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void refundToBank(TrxRequestData requestData) throws RefundException, AccountException, StaleObjectStateException
	{

		logger.info("+++++++++++++++++refundToBank date:" + new Date() + "++++++++++++++");

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();// 商品订单id
		String operator = requestData.getOperator(); // 操作人
		String desc = requestData.getDescription(); // 描述
		TrxorderGoods trxorderGoods  = null;
		RefundReqInfo refundReqInfo = null; 
		try{
			refundReqInfo = refundService.processBeforeRefundToBank(trxorderGoodsId);
			
		}catch(AccountException ae){//用户帐户现金不足
			
			refundService.processRefuseForRefundToBank(trxorderGoodsId, operator, "用户帐户现金不足，系统自动拒绝");//系统自动复核拒绝
			
			throw new RefundException(BaseException.REFUND_TO_BANK_AUTO_REFUSE);// 个人账户现金余额不足，系统自动拒绝
		}
	
		try{
			refundReqInfo.setOperator(operator);
			refundReqInfo.setDescription(desc);
			OrderInfo orderInfo = refundService.sendRefundReqToBank(refundReqInfo);
			
			//失败情况
			//orderInfo.setRefundRspCode("0");
			
			trxorderGoods = refundService.processAfterRefundToBank(orderInfo, refundReqInfo);
			//退款失败 发邮件
			if(!"SUCCESS".equals(orderInfo.getRefundStatus())){
				throw new RefundException(BaseException.REFUND_FAILED_HAVED);   //退款成功或失败过
			}
		}catch (Exception e){
			logger.error("++++++++++++refund to bank error++++",e);
			//报警 发邮件
			StringBuffer content = new StringBuffer("<font color='red'>退款处理异常:</font><br/>");
			content.append("商品订单号:" + refundReqInfo.getTrxorderGoods().getTrxGoodsSn() +"<br/>");
			content.append("支付渠道:" + refundReqInfo.getPayment().getPayChannel() +"<br/>");
			content.append("支付订单号:" + refundReqInfo.getPayment().getPayRequestId() +"<br/>");
			content.append("支付时间:" + DateUtils.formatDate(refundReqInfo.getPayment().getPayConfirmDate(), "yyyy-MM-dd HH:mm:ss") +"<br/>");
			content.append("退款ID:" + refundReqInfo.getRefundDetail().getId()+"<br/>");
			content.append("退款交易号:" + refundReqInfo.getRefundDetail().getProRefundrequestId() +"<br/>");
			content.append("退款时间:" + DateUtils.formatDate(refundReqInfo.getRefundDetail().getHandleDate(), "yyyy-MM-dd HH:mm:ss")+"<br/>");
			content.append("<br/><br/>" + StringUtils.toTrim(e.getMessage()));
			
			refundService.sendWarningEmail(content.toString());
			throw new RefundException(BaseException.REFUND_TO_BANK_SUC_OR_FAILED);   //资金风险（不同支付机构退款重试策略不一致）
		}
		// 短信提醒部分
		if (trxorderGoods != null) {
			notifyRecordBizService.processNotifyByBizType(trxorderGoods, TrxBizType.RETURNBANK,true);
		}

	}

	/**
	 * 销毁凭证
	 * 
	 * @param sourceMap
	 * @return
	 */
	@Override
	public void destoryVoucherByID(TrxRequestData requestData) throws VoucherException, StaleObjectStateException
	{
		String voucherId = requestData.getVoucherId();
		voucherService.destoryVoucher(new Long(voucherId));
	}

	/**
	 * 根据客户号和凭证内容校验凭证及回收
	 * 
	 * @param sourceMap
	 * @return
	 * @throws PartnerException 
	 */
	@Override
	public void checkVoucher(TrxRequestData requestData) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException, RuleException, StaleObjectStateException, PartnerException
	{

		String guestId = requestData.getGuestId();// 商家id
		String voucherCode = requestData.getVoucherCode(); // 凭证码
		String voucherVerifySource = requestData.getVoucherVerifySource();
		String subGuestId = requestData.getSubGuestId(); // 分店id
		@SuppressWarnings("unused")
		boolean  isSendSmsNotify=true;//是否需要短息通知
		PartnerVoucherService partnerVoucherService=null;
		Map<String,String>  resultMap=null;
		String token="";

		// 预查询
		VoucherInfo voucherInfo = voucherService.preCheckVoucher(new Long(guestId), voucherCode, subGuestId);

		Voucher voucher = voucherInfo.getVoucher();
		TrxorderGoods trxorderGoods = voucherInfo.getTrxorderGoods();
		TrxOrder trxOrder = voucherInfo.getTrxorder();
		Long userId = trxOrder.getUserId();
		
		//订单来源判断及对分销商凭证进行远程查询
		PartnerInfo partnerInfo = partnerCommonService.qryParterByUserIdInMem(userId);// 获取有效分销商信息
	
		 if(partnerInfo!=null){
				partnerVoucherService = partnerVoucherServiceFactory.getPartnerVoucherService(partnerInfo.getApiType());// 获取分销商服务类工厂实现
				resultMap=partnerVoucherService.qryVoucherInfoToPar(voucherInfo, partnerInfo);
				String rspFlag=resultMap.get("status");
				
				if(resultMap.isEmpty()|| !"ALLOW_VALIDATE".equals(rspFlag)){
					
					throw  new PartnerException(BaseException.PAETNER_VOUCHER_STATUS_INVALID);
				}
		 }
		 
		 
		 
		// 调用校验凭证及回收.事务开始/提交
		voucherService.validateVoucher(voucher, trxorderGoods, voucherVerifySource, subGuestId);
		//招财宝商家新清洁算逻辑：商家入账操作,商家ID为7位(同步入账)
		guestSettleService.guestCreditForSync(requestData,trxorderGoods,voucher.getId());
		
		 if(partnerInfo!=null){//分销商订单
			isSendSmsNotify=false;//不发短信通知
		}
		// 短信通知用户
		//notifyRecordBizService.processNotifyByBizType(trxorderGoods, TrxBizType.INSPECT,isSendSmsNotify);
		
		//如果是分销商订单，则我侧校验成功后进行远程推送.此处生吞异常(若本次推送失败，在补单模块可以重试)
		if(partnerInfo!=null){
			try{
				partnerInfo.setToken(token);
				partnerVoucherService.pushVoucherInfo(voucherInfo, partnerInfo);
			}catch(Exception e){
				logger.debug(e);
				e.printStackTrace();
			
		}
		 }
		
	}
	
    
	@Override
	public Map<String, String> getQryTrxOrderGoodsForGuestCondition(Map<String, String> sourceMap) throws Exception {
		Map<String, String> condition = new HashMap<String, String>();
		String guestId = sourceMap.get("guestId");
		// 商家编号
		if (StringUtils.validNull(guestId)) {
			condition.put("guestId", guestId.trim());
		} else {// 商家编号必填
			throw new Exception("+++++++++qryTrxOrderGoodsForGuest+++guestId  is required++++++++");
		}
		// 服务密码
		if (StringUtils.validNull(sourceMap.get("voucherCode"))) {
			condition.put("voucherCode", sourceMap.get("voucherCode").trim());
		}
		// 商品编号
		if (StringUtils.validNull(sourceMap.get("goodsId"))) {
			condition.put("goodsId", sourceMap.get("goodsId").trim());
		}
		// 订单编号
		if (StringUtils.validNull(sourceMap.get("trxGoodsSn"))) {
			condition.put("trxGoodsSn", sourceMap.get("trxGoodsSn").trim());
		}
		// 订单状态
		if (StringUtils.validNull(sourceMap.get("trxStatus"))) {
			String trxStatus = sourceMap.get("trxStatus").trim();
			if ("USED".equals(trxStatus)) { // USED已消费
				trxStatus = "USED,COMMENTED";
			} else if ("REFUND".equals(trxStatus)) { // REFUND已退款
				trxStatus = "REFUNDACCEPT,REFUNDTOACT,RECHECK,REFUNDTOBANK";
			}
			String[] trxStatusArray = trxStatus.split(",");
			StringBuffer trxStatusSb = new StringBuffer("");
			for (String status : trxStatusArray) {
				trxStatusSb.append("'" + status.trim() + "',");
			}
			trxStatusSb.deleteCharAt(trxStatusSb.length() - 1);
			condition.put("trxStatus", trxStatusSb.toString());
		}
		// 分店编号
		if (StringUtils.validNull(sourceMap.get("subGuestId"))) {
			condition.put("subGuestId", sourceMap.get("subGuestId").trim());
		}

		// 购买时间
		if (StringUtils.validNull(sourceMap.get("buyStartDate"))&& StringUtils.validNull(sourceMap.get("buyEndDate"))) {
			condition.put("buyStartDate", sourceMap.get("buyStartDate").trim());
			condition.put("buyEndDate", sourceMap.get("buyEndDate").trim());
			if (!DateUtils.compareConditionDate(sourceMap.get("buyStartDate").trim(), sourceMap.get("buyEndDate").trim())) {
				throw new Exception("++++date condition is invalida++++++++++++++");
			}
		}
		// 消费时间
		if (StringUtils.validNull(sourceMap.get("usedStartDate"))&& StringUtils.validNull(sourceMap.get("usedEndDate"))) {
			condition.put("usedStartDate", sourceMap.get("usedStartDate").trim());
			condition.put("usedEndDate", sourceMap.get("usedEndDate").trim());
			if (!DateUtils.compareConditionDate(sourceMap.get("usedStartDate").trim(), sourceMap.get("usedEndDate").trim())) {
				throw new Exception("++++date condition is invalida++++++++++++++");
			}
		}

		// 分页参数
		if (StringUtils.validNull(sourceMap.get("querySize")) && StringUtils.validNull(sourceMap.get("rowsOffset"))) {
			condition.put("querySize", sourceMap.get("querySize").trim());
			condition.put("rowsOffset", sourceMap.get("rowsOffset").trim());
		} else {// 无分页参数直接返回
			throw new Exception("+++++++++querySize,rowsOffset condition is error+++++++");
		}
		return condition;
	}
	
	

	@Override
	public Map<String, String> getQueryTrxGoodsByIdsCondition(Map<String, String> sourceMap) throws Exception {
		Map<String, String> condition = new HashMap<String, String>();
        //用户手机号
        if(StringUtils.validNull(sourceMap.get("mobile"))) {
            condition.put("mobile", sourceMap.get("mobile").trim());
        }
        // 订单状态trxStatus
        if(StringUtils.validNull(sourceMap.get("trxStatus"))) {
            condition.put("trxStatus", sourceMap.get("trxStatus").trim());
        }
        // tab订单状态trxStatus可多个用,隔开
        if(StringUtils.validNull(sourceMap.get("tabTrxStatus"))) {
            condition.put("tabTrxStatus", sourceMap.get("tabTrxStatus").trim());
        }
        // 用户邮箱
        if(StringUtils.validNull(sourceMap.get("email"))) {
            condition.put("email", sourceMap.get("email").trim());
        }
        // 商家编号
        if(StringUtils.validNull(sourceMap.get("guestId"))) {
            condition.put("guestId", sourceMap.get("guestId").trim());
        }
        // 商品订单号
        if(StringUtils.validNull(sourceMap.get("trxGoodsSn"))) {
            condition.put("trxGoodsSn", sourceMap.get("trxGoodsSn").trim());
        }
        // 商品订单id,多个以逗号隔开
        if(StringUtils.validNull(sourceMap.get("trxGoodsIds"))) {
            condition.put("trxGoodsIds", sourceMap.get("trxGoodsIds").trim());
        }
        //分销商订单号
        if(StringUtils.validNull(sourceMap.get("outRequestId"))){
            condition.put("outRequestId", sourceMap.get("outRequestId").trim());
        }
        // 商品编号
        if(StringUtils.validNull(sourceMap.get("goodsId"))) {
            condition.put("goodsId", sourceMap.get("goodsId").trim());
        }
        //服务密码凭证号
        if(StringUtils.validNull(sourceMap.get("voucherCode"))) {
            condition.put("voucherCode", sourceMap.get("voucherCode").trim());
        }
        //冻结状态
        if(StringUtils.validNull(sourceMap.get("isFreeze"))) {
            condition.put("isFreeze", sourceMap.get("isFreeze").trim());
        }
        // 城市
        if(StringUtils.validNull(sourceMap.get("city"))) {
            condition.put("city", sourceMap.get("city").trim());
        }
        // 购买时间
        if(StringUtils.validNull(sourceMap.get("createDateBegin")) && StringUtils.validNull(sourceMap.get("createDateEnd"))) {
            condition.put("createDateBegin", sourceMap.get("createDateBegin").trim());
            condition.put("createDateEnd", sourceMap.get("createDateEnd").trim());
            if(!DateUtils.compareConditionDate(sourceMap.get("createDateBegin").trim(),sourceMap.get("createDateEnd").trim())){
            	throw new Exception("++++date condition is invalida++++++++++++++");
            }
        }
        // 消费时间
        if(StringUtils.validNull(sourceMap.get("confirmDateBegin")) && StringUtils.validNull(sourceMap.get("confirmDateEnd"))) {
            condition.put("confirmDateBegin", sourceMap.get("confirmDateBegin").trim());
            condition.put("confirmDateEnd", sourceMap.get("confirmDateEnd").trim());
            if(!DateUtils.compareConditionDate(sourceMap.get("confirmDateBegin").trim(),sourceMap.get("confirmDateEnd").trim())){
            	throw new Exception("++++date condition is invalida++++++++++++++");
            }
        }
        
        //参数组装结束
        
        if(condition.size()==0){
        	throw new Exception("++++query condition is empry++++++++++++++");
        }
        return condition;
	}

	/**
     * 查询商品订单信息数量
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     */
	@Override
	public Map<String,String> qryTrxOrderGoodsForGuestCount(Map<String, String> condition){
		if(!StringUtils.validNull(condition.get("guestId"))){
			return null;
		}
		Map<String, Long> countMap = trxorderGoodsService.qryTrxOrderGoodsForGuestCount(condition);
		if(null==countMap || countMap.isEmpty()){
			return null;
		}
		Map<String, String> countStringMap = new HashMap<String, String>();
		countStringMap.put("totalCount", String.valueOf(countMap.get("totalCount")));   //总数量
		countStringMap.put("successCount", String.valueOf(countMap.get("successCount")));   //购买未使用数量
		countStringMap.put("consumeCount", String.valueOf(countMap.get("consumeCount")));   //消费数量
		countStringMap.put("refundCount", String.valueOf(countMap.get("refundCount")));   //退款数量
		countStringMap.put("expiredCount", String.valueOf(countMap.get("expiredCount")));   //失效数量
		return countStringMap;
	}
	
	
	/**
	 * 根据商品订单Id查询商品订单结算信息
	 * @param trxOrderGoodsIds
	 * @return
	 */
	@Override
	public List<Map<String,Object>> qryTrxOrderGoodsDetailForSettle(String trxOrderGoodsIds){
		String[] idArray = trxOrderGoodsIds.split("\\|");
		StringBuffer idsStr = new StringBuffer("");
		for(int i=0;i<idArray.length;i++){
			idsStr.append(idArray[i]+",");
		}
		idsStr.deleteCharAt(idsStr.length()-1);
		List<Map<String,Object>> qryList = trxorderGoodsService.querySettleDetailById(idsStr.toString());
		if(null==qryList || qryList.size()<1){
			return null;
		}
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        for(Map<String, Object> rs : qryList){
        	Map<String,Object> result = new HashMap<String, Object>();
        	result.put("trxOrderGoodsId", String.valueOf(rs.get("trxOrderGoodsId")));
        	result.put("trxGoodsSn", rs.get("trxGoodsSn"));
        	result.put("confirmDate",null!=rs.get("confirmDate")?DateUtil.formatDate((Date)rs.get("confirmDate"),"yyyy-MM-dd HH:mm:ss"):"");
        	result.put("orderPrice", ((BigDecimal)rs.get("orderPrice")).toString());
        	result.put("dividePrice", ((BigDecimal)rs.get("dividePrice")).toString());
        	result.put("settleStatus", rs.get("settleStatus"));
        	resultList.add(result);
        }
        return resultList;
	}
	
	@Override
	public List<Map<String, Object>> qryTrxorderGoodsForGuest(Map<String, String> condition) {
		if(!StringUtils.validNull(condition.get("guestId"))){
			return null;
		}
		int pageSize =Integer.valueOf(condition.get("querySize"));
        int startRow =Integer.valueOf(condition.get("rowsOffset"));
        List<Map<String,Object>> qryList = trxorderGoodsService.queryTrxOrderGoodsForGuest(condition, startRow,pageSize);
		if(null==qryList || qryList.size()<1){
			return null;
		}
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        for(Map<String, Object> rs : qryList){
        	Map<String,Object> result = new HashMap<String, Object>();
    		result.put("trxOrderGoodsId", null != rs.get("trxOrderGoodsId")?String.valueOf(rs.get("trxOrderGoodsId")):"");
    		result.put("trxGoodsSn", null != rs.get("trxGoodsSn")?(String)rs.get("trxGoodsSn"):"");
    		result.put("goodsName", null != rs.get("goodsName")?(String)rs.get("goodsName"):"");
    		result.put("goodsId", null != rs.get("goodsId")?String.valueOf(rs.get("goodsId")):"");
    		result.put("buyDate",null != rs.get("buyDate")?DateUtil.formatDate((Date)rs.get("buyDate"), "yyyy-MM-dd HH:mm:ss"):"");
    		result.put("usedDate",null != rs.get("usedDate")?DateUtil.formatDate((Date)rs.get("usedDate"), "yyyy-MM-dd HH:mm:ss"):"");
    		result.put("voucherId",null != rs.get("voucherId")?String.valueOf(rs.get("voucherId")):"");
    		result.put("payPrice", null != rs.get("payPrice")?((BigDecimal)rs.get("payPrice")).toString():"");
    		result.put("guestId",null != rs.get("guestId")?String.valueOf(rs.get("guestId")):"");
        	result.put("subGuestId",null != rs.get("subGuestId")?String.valueOf(rs.get("subGuestId")):"");
    		result.put("isFreeze", null != rs.get("isFreeze")?(((Boolean)rs.get("isFreeze"))?"1":"0"):"");
    		result.put("voucherCode",null != rs.get("voucherCode")?(String)rs.get("voucherCode"):"");
    		result.put("trxOrderId",null != rs.get("trxOrderId")?String.valueOf(rs.get("trxOrderId")):"");
    		result.put("bizType",null != rs.get("bizType")?String.valueOf(rs.get("bizType")):"");
    		String trxStatus = "";
    		String trxStatusName = "";
    		if(null != rs.get("trxStatus")){
    			trxStatus = (String)rs.get("trxStatus");
    			TrxStatus trxStatusEnum = EnumUtil.transStringToEnum(TrxStatus.class,trxStatus);
    			
    			if(trxStatusEnum.compareTo(TrxStatus.INIT)==0){
    				trxStatusName = "未支付";
    			}else if(trxStatusEnum.compareTo(TrxStatus.SUCCESS)==0){
    				trxStatusName = "未使用";
    			}else if(trxStatusEnum.compareTo(TrxStatus.USED)==0 || trxStatusEnum.compareTo(TrxStatus.COMMENTED)==0){
    				trxStatusName = "已消费";
    			}else if(trxStatusEnum.compareTo(TrxStatus.REFUNDACCEPT)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDACCEPT)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDTOACT)==0 || trxStatusEnum.compareTo(TrxStatus.RECHECK)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDTOBANK)==0 ){
    				trxStatusName = "已退款";
    			}else if(trxStatusEnum.compareTo(TrxStatus.EXPIRED)==0){
    				trxStatusName = "已过期";
    			}else if(trxStatusEnum.compareTo(TrxStatus.CANCEL)==0){
    				trxStatusName = "取消";
    			}
    			
    		}
    		result.put("trxStatus",trxStatus);
			result.put("trxStatusName",trxStatusName);
        	resultList.add(result);
        }
        return resultList;
	}
	
	/**
     * 根据商家编号和商品订单id查询商品详情信息
     * @param requestData
     * @return
     */
	@Override
    public Map<String,Object> qryOrderDetailByGuestIdAndTgId(TrxRequestData requestData)throws TrxorderGoodsException,NumberFormatException{
		Long guestId = Long.parseLong(requestData.getGuestId());;
		Long tgId = requestData.getTrxorderGoodsId();
		if(null ==guestId || null == tgId){
			throw new IllegalArgumentException("+++++guestId+,trxOrderGoodsId is required++++");
		}
		
		TrxorderGoods tg = trxorderGoodsService.findById(tgId);
		if(null == tg || tg.getGuestId().intValue()!= guestId.intValue()){
			throw new TrxorderGoodsException(BaseException.TRXORDERGOODS_NOT_FOUND);  //商品明细不存在
		}
		
		Map<String,Object> detailMap = new HashMap<String, Object>();
		detailMap.put("trxOrderGoodsId",null != tg.getId()?String.valueOf(tg.getId()):"");	 //商品订单ID
		detailMap.put("trxGoodsSn",null != tg.getTrxGoodsSn()?tg.getTrxGoodsSn():"");      //订单编号
		detailMap.put("goodsName",null != tg.getGoodsName()?tg.getGoodsName():"");        //商品名称
		detailMap.put("goodsId",null != tg.getGoodsId()?String.valueOf(tg.getGoodsId()):"");         //商品编号
		detailMap.put("buyDate",null != tg.getCreateDate()?DateUtil.formatDate(tg.getCreateDate(),"yyyy-MM-dd HH:mm:ss"):"");          //购买时间
		detailMap.put("voucherId",null != tg.getVoucherId()?String.valueOf(tg.getVoucherId()):"");        //凭证ID
		detailMap.put("payPrice",String.valueOf(tg.getPayPrice()));         //支付金额
		detailMap.put("guestId",null != tg.getGuestId()?String.valueOf(tg.getGuestId()):"");          //商家编号
		detailMap.put("subGuestId",null != tg.getSubGuestId()?String.valueOf(tg.getSubGuestId()):"");       //分店编号
		detailMap.put("isFreeze",null != tg.getIsFreeze()?String.valueOf(tg.getIsFreeze()):"");         //是否冻结
		String trxStatus = "";
		String trxStatusName = "";
		if(null != tg.getTrxStatus()){
			trxStatus = tg.getTrxStatus().name();
			TrxStatus trxStatusEnum = tg.getTrxStatus();
			
			if(trxStatusEnum.compareTo(TrxStatus.INIT)==0){
				trxStatusName = "未支付";
			}else if(trxStatusEnum.compareTo(TrxStatus.SUCCESS)==0){
				trxStatusName = "未使用";
			}else if(trxStatusEnum.compareTo(TrxStatus.USED)==0 || trxStatusEnum.compareTo(TrxStatus.COMMENTED)==0){
				trxStatusName = "已消费";
			}else if(trxStatusEnum.compareTo(TrxStatus.REFUNDACCEPT)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDACCEPT)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDTOACT)==0 || trxStatusEnum.compareTo(TrxStatus.RECHECK)==0 || trxStatusEnum.compareTo(TrxStatus.REFUNDTOBANK)==0 ){
				trxStatusName = "已退款";
			}else if(trxStatusEnum.compareTo(TrxStatus.EXPIRED)==0){
				trxStatusName = "已过期";
			}else if(trxStatusEnum.compareTo(TrxStatus.CANCEL)==0){
				trxStatusName = "取消";
			}
			
		}
		detailMap.put("trxStatus",trxStatus);
		detailMap.put("trxStatusName",trxStatusName);
		
		detailMap.put("trxOrderId",null != tg.getTrxorderId()?String.valueOf(tg.getTrxorderId()):"");      //订单ID
		detailMap.put("bizType",String.valueOf(tg.getBizType()));          //商品订单业务类型.0:常规订单;1:点餐;2:网票网;
		Voucher voucher = voucherService.findVoucherByid(tg.getVoucherId());
		if(null != voucher){
			detailMap.put("voucherCode",voucher.getVoucherCode());     //服务密码
			if(tg.getTrxStatus().compareTo(TrxStatus.USED)==0 || tg.getTrxStatus().compareTo(TrxStatus.COMMENTED)==0){
				detailMap.put("usedDate",null!=voucher.getConfirmDate()?DateUtil.formatDate(voucher.getConfirmDate(),"yyyy-MM-dd HH:mm:ss"):"");     //消费时间
			}
		}else{
			detailMap.put("usedDate","");
		}
		if(tg.getTrxStatus().compareTo(TrxStatus.RECHECK) == 0 || tg.getTrxStatus().compareTo(TrxStatus.REFUNDACCEPT) == 0 || 
			tg.getTrxStatus().compareTo(TrxStatus.REFUNDTOACT) == 0 || tg.getTrxStatus().compareTo(TrxStatus.REFUNDTOBANK) == 0 ){
			RefundRecord refundRecord = refundService.queryRefundRecordByTrxorderId(tg.getId());
			Date refundDate = refundRecord.getCreateDate();
			detailMap.put("refundDate",null!=refundDate?DateUtil.formatDate(refundDate,"yyyy-MM-dd HH:mm:ss"):"");        //退款时间
		}else{
			detailMap.put("refundDate","");
		}
		//0:常规订单;1:点餐;2:网票网
		//如果是点餐，则需要查询点菜单
		if(tg.getBizType()==1){
			Double menuTotalAmount = new Double(0);
			List<Map<String,Object>> menuMapList = new ArrayList<Map<String,Object>>();
			List<MenuGoodsOrder> menuList = trxorderGoodsService.queryMenuGoodsOrderList(tgId);
			for(MenuGoodsOrder mgd : menuList){
				Map<String,Object> menuMap = new HashMap<String,Object>();
				menuMap.put("menuId", String.valueOf(mgd.getId())); //菜品ID
				menuMap.put("memuName", mgd.getMenuName()); //菜品名称
				menuMap.put("menuCount", String.valueOf(mgd.getMenuCount())); //菜品数量
				menuMap.put("menuPrice", String.valueOf(mgd.getMenuPrice().doubleValue())); //菜品单价
				Double menuAmount = Amount.mul(mgd.getMenuPrice().doubleValue(), mgd.getMenuCount().doubleValue());
				menuMap.put("menuAmount",String.valueOf(menuAmount));  //金额
				menuMapList.add(menuMap);
				menuTotalAmount = Amount.add(menuAmount,menuTotalAmount);
			}
			detailMap.put("menuList", menuMapList);
			detailMap.put("menuTotalAmount", String.valueOf(menuTotalAmount));	  //合计原价总计
		}
		return detailMap;
	}
    
	/**
	 * 根据凭证码和分店编号查询订单信息
	 * @param requestData
	 * @return
	 * @throws TrxorderGoodsException 
	 * @throws NumberFormatException 
	 */
	@Override
	public TrxResponseData qryOrderByVouCodeAndGuestId(TrxRequestData requestData) throws VoucherException,TrxorderGoodsException,NumberFormatException{
		String guestId = requestData.getGuestId();// 商家id
		String voucherCode = requestData.getVoucherCode(); // 凭证码
		Voucher voucher = voucherService.findByGuestIdAndCode(voucherCode, Long.parseLong(guestId));
		if(null == voucher){
			logger.info("+++++++++++++++voucherCode="+voucherCode+"+++++++++no exist++++++++++");
			throw new VoucherException(BaseException.VOUCHER_NOT_FOUND);
		}
		TrxorderGoods trxOrderGoods = trxorderGoodsService.findByVoucherId(voucher.getId());
		if(null == trxOrderGoods){
			logger.info("+++++++++++++++voucherCode="+voucherCode+"+++++++++no exist++++++++++");
			throw new TrxorderGoodsException(BaseException.VOUCHER_NOT_FOUND);
		}
		TrxResponseData responseData = new TrxResponseData();
		responseData.setGoodsId(String.valueOf(trxOrderGoods.getGoodsId()));
		responseData.setGoodsName(trxOrderGoods.getGoodsName());
		responseData.setGoodsPayPrice(String.valueOf(trxOrderGoods.getPayPrice()));
		return responseData;
	}
	
	

	@Override
	public void reSendVoucher(TrxRequestData requestData) throws VerifyBelongException, TrxorderGoodsException, VoucherException, Exception
	{

		String mobile = requestData.getMobile();
		String email = requestData.getEmail();
		Long trxorderGoodsId = requestData.getTrxorderGoodsId();
		boolean isVerifyForTg=requestData.isVerifyForTg();
		String outSmsTemplate=requestData.getOutSmsTemplate();

		logger.info("++++trxOrderGoodsId:" + trxorderGoodsId + "+++++++++ReqChannel" + requestData.getReqChannel().name()+"++isVerifyForTg:"+isVerifyForTg+"++userId:"+requestData.getUserId());
	
			
		TrxOrder trxorder=trxOrderService.findByTgId(trxorderGoodsId);
		PartnerInfo partnerInfo=partnerCommonService.qryParterByUserIdInMem(trxorder.getUserId());//获取分销商
		//如果是Boss来请求且是团800或者京东订单，不支持重发,其他都支持
		if((partnerInfo!=null&& PartnerApiType.TUAN800.name().equals(partnerInfo.getApiType())&&ReqChannel.BOSS.equals(requestData.getReqChannel()))
			|| (partnerInfo!=null&& PartnerApiType.BUY360.name().equals(partnerInfo.getApiType())&&ReqChannel.BOSS.equals(requestData.getReqChannel()))){
			
			throw new PartnerException(BaseException.PAETNER_VOUCHER_NOT_ALLOW_RESEND);//该分销商订单不允许后台重发
		}
		
		String sendType = "";
		if ((email == null || "".equals(email)) && (mobile == null || "".equals(mobile)))
		{	
			sendType = "BOTH";// 手机号 邮箱都为空时全发
		} else
		{
			// 用户鉴权
			boolean verifyResult = trxorderGoodsService.verifyBelong(trxorderGoodsId, requestData.getUserId(),isVerifyForTg);
			if (!verifyResult)
			{
				throw new VerifyBelongException(BaseException.TRXORDERGOODS_VERIFY_BELONG_FAILED);
			}
			if (StringUtils.validNull(mobile) && StringUtils.validNull(email))
			{
				sendType = "BOTH";
			} else if (StringUtils.validNull(mobile))
			{
				sendType = "SMS";
			} else if (StringUtils.validNull(email))
			{
				sendType = "EMAIL";
			}

		}
		
		voucherService.reSendVoucher(trxorderGoodsId, mobile, email, sendType,outSmsTemplate);

	}

	/**
	 * 创建虚拟款项账户
	 * 
	 * @return
	 * @throws TrxOrderException 
	 */
	@Override
	public TrxResponseData createVmAccount(TrxRequestData requestData) throws AccountException, VmAccountException, NumberFormatException, StaleObjectStateException, TrxOrderException
	{

		String balance = requestData.getBalance(); // 创建余额
		String vmAccountSortId = requestData.getVmAccountSortId();// 类别Id
		String loseDateStr = requestData.getLoseDate();// 过期时间
		String costBear = requestData.getCostBear();// //成本承担方
		String isFund = requestData.getIsFund();// 是否有金
		String proposer = requestData.getProposer();// 申请人
		String description = requestData.getDescription();// 描述
		String operatorId = requestData.getOperator();// 操作人ID
		String isNotChange = requestData.getIsNotChange();// 是否找零
		String notChangeRule = requestData.getNotChangeRule();//找零规则
		String isRefund = requestData.getIsRefund();//是否退款
		// 构造虚拟款项
		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo(balance, vmAccountSortId, loseDateStr);
		vmAccountParamInfo.setCostBear(costBear);// 成本承担方
		vmAccountParamInfo.setIsFund(isFund); // 是否有金
		vmAccountParamInfo.setProposer(proposer);// 申请人
		vmAccountParamInfo.setDescription(description);// 描述
		vmAccountParamInfo.setOperatorId(operatorId == null ? "0" : operatorId);// 操作人ID
		vmAccountParamInfo.setIsNotChange(Integer.valueOf(isNotChange));
		vmAccountParamInfo.setNotChangeRule(notChangeRule);
		if(isRefund==null || "".equals(isRefund)){
		    vmAccountParamInfo.setIsRefund("1");//默认值
		}else{
		    vmAccountParamInfo.setIsRefund(isRefund);
		}
		Long vmAccountId = vmAccountService.createVmAccount(vmAccountParamInfo);
		TrxResponseData trxResponseData = new TrxResponseData();
		trxResponseData.setVmAccountId(vmAccountId);

		return trxResponseData;
	}

	/**
	 * 往虚拟款项账户追加余额
	 * 
	 * @return
	 */
	@Override
	public void pursueVmAccount(TrxRequestData requestData) throws AccountException, NumberFormatException, VmAccountException, StaleObjectStateException
	{
		String vmAccountId = requestData.getVmAccountId();// 虚拟款项Id
		String amount = requestData.getAmount(); // 追加余额
		String operatorId = requestData.getOperator();// 操作人ID

		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
		vmAccountParamInfo.setVmAccountId(vmAccountId);
		vmAccountParamInfo.setAmount(amount);
		vmAccountParamInfo.setOperatorId(operatorId);

		vmAccountService.pursueVmAccount(vmAccountParamInfo);
	}

	/**
	 * 下发虚拟款项
	 * 
	 * @return
	 */
	@Override
	public void dispatchVm(TrxRequestData requestData) throws VmAccountException, NumberFormatException, AccountException, StaleObjectStateException
	{

		String vmAccountId = requestData.getVmAccountId();// 虚拟款项Id
		String amount = requestData.getAmount(); // 下发金额
		String requestId = requestData.getRequestId();// 下发请求号
		String userId = requestData.getUserId().toString();// 接收用户主键Id
		String operatorId = requestData.getOperator();// 操作人ID
		String bizType = requestData.getBizType();
		String description = requestData.getDescription();
		// 构建虚拟款项
		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
		vmAccountParamInfo.setVmAccountId(vmAccountId);
		vmAccountParamInfo.setAmount(amount);
		vmAccountParamInfo.setRequestId(requestId);
		vmAccountParamInfo.setUserId(userId);
		vmAccountParamInfo.setOperatorId(operatorId);
		vmAccountParamInfo.setDescription(description);
		vmAccountParamInfo.setActHistoryType(ActHistoryType.VMDIS);
		
		//如果是优惠券的话
		if(!StringUtils.isEmpty(bizType)){
			vmAccountParamInfo.setBizType(BizType.valueOf(bizType));
		}
		vmAccountService.dispatchVm(vmAccountParamInfo);

	}

	/**
	 * 千品卡查询
	 * 
	 * @param sourceMap
	 * @return
	 * @throws CardException
	 */
	public TrxResponseData queryCardInfo(TrxRequestData requestData) throws CardException
	{

		String cardNo = requestData.getCardNo();
		String cardPwd = requestData.getCardPwd();

		TrxResponseData trxResponseData = cardService.queryByCardNo(cardNo, cardPwd);

		return trxResponseData;

	}

	/**
	 * 千品卡充值
	 * 
	 * @param sourceMap
	 * @return
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 * @throws CardException
	 * @throws NumberFormatException
	 */
	public TrxResponseData topupCard(TrxRequestData requestData) throws CardException, StaleObjectStateException, AccountException, VmAccountException
	{

		Long userId = requestData.getUserId();
		String cardNo = requestData.getCardNo();
		String cardPwd = requestData.getCardPwd();
		ReqChannel reqChannel = requestData.getReqChannel();

		TrxResponseData trxResponseData = cardService.topupByCardInfoAndUserId(cardNo, cardPwd, userId, reqChannel);

		return trxResponseData;

	}
	
	/**
	 * 线上活动自动绑定优惠券
	 * @param sourceMap
	 * @return
	 */
	public TrxResponseData autoBindCoupon(TrxRequestData requestData) throws CouponException,StaleObjectStateException{
		Long userId = requestData.getUserId();
		String csid = requestData.getCsid();
		ReqChannel reqChannel = requestData.getReqChannel();
		logger.info("+++++++++userId="+userId+"++++++++csid="+csid+"++++++++reqChannel="+reqChannel);
		trxCouponService.processOnlineActivityAutoBind(userId, csid);
		TrxResponseData trxResponseData = new TrxResponseData();
		return trxResponseData;
	}
	
	/**
	 * 优惠券激活
	 * 
	 * @param sourceMap
	 * @return
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 * @throws DiscountCouponException
	 * @throws NumberFormatException
	 */
	@Override
	public TrxResponseData activateCoupon(TrxRequestData requestData)
			throws DiscountCouponException,CouponException,StaleObjectStateException,AccountException, VmAccountException {
		Long userId = requestData.getUserId();
		String couponPwd = requestData.getCouponPwd();
		ReqChannel reqChannel = requestData.getReqChannel();
		TrxResponseData trxResponseData;
		//优惠券密码为10位时，表示优惠券3期绑定
		if(couponPwd.length()==10){
			logger.info("+++++++++binding coupon_3 +++++++++coupon couponPwd["+couponPwd+"]++++++++++++++");
			trxResponseData = trxCouponService.processBindCoupon(couponPwd, userId,reqChannel);
		}else{
			logger.info("+++++++++topon discount coupon+++++++++coupon couponPwd["+couponPwd+"]++++++++++++++");
			trxResponseData = discountCouponService.topupByCouponAndUserId(couponPwd, userId, reqChannel);
		}
		return trxResponseData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TrxResponseData queryPurse(TrxRequestData requestData) throws BaseException, Exception
	{

		TrxResponseData responseData = new TrxResponseData();

		String userId = requestData.getUserId().toString();
		String pageOffset = requestData.getRowsOffset().toString(); // 偏移量
		String isCachePage = requestData.getIsCachePage();// 是否缓存
		String uuid = requestData.getUuid(); // UUID值
		String reqChannel = requestData.getReqChannel().name(); // 交易对外接口请求类型
		String pageSize = requestData.getPageSize().toString();// 页面大小

		List<AccountHistory> allRsList = null;
		String createDate = "";// 发生时间
		String strActHistoryType = "";// 资金类型
		String description = "";// 备注
		String trxAmount = "";// 金额
		String strUuid = reqChannel + CachePageType.ACTHISLIST.name() + userId + uuid;
		if (Integer.valueOf(isCachePage) == 1)
		{ // 缓存分页
			if (uuid == null || "".equals(uuid))
			{
				throw new Exception();
			} else
			{
				allRsList = (List<AccountHistory>) memCacheService.get(strUuid);
				if (allRsList == null || allRsList.size() <= 0)
				{
					allRsList = accountHistoryService.getHistoryInfoByUserId(Long.valueOf(userId));
					memCacheService.set(strUuid, allRsList, TrxConstant.myPurseTimeout);
				}
			}

		} else
		{ // 不缓存分页
			allRsList = accountHistoryService.getHistoryInfoByUserId(Long.valueOf(userId));
		}

		int totalRows = 0;
		if (allRsList != null)
		{
			totalRows = allRsList.size();
			int startRow = Integer.valueOf(pageOffset);
			int paperSize = Integer.valueOf(pageSize);
			int endRow = startRow + paperSize > totalRows ? totalRows : startRow + paperSize;
			List<AccountHistory> accList = new ArrayList<AccountHistory>();
			for (int i = startRow; i < endRow; i++)
			{
				accList.add(allRsList.get(i));
			}

			for (AccountHistory allrs : accList)
			{
				ActHistoryType actHistoryType = allrs.getActHistoryType();
				if (actHistoryType.equals(ActHistoryType.LOAD))
				{
					trxAmount = trxAmount + allrs.getTrxAmount() + "|";
					createDate = createDate + DateUtils.dateToStrLong(allrs.getCreateDate()) + "|";
					strActHistoryType = strActHistoryType + allrs.getActHistoryType().name() + "|";
					description = description + allrs.getPayment().getPayChannelName() + allrs.getPayment().getProviderName() + allrs.getPayment().getProExternalId() + "|";
				} else if (actHistoryType.equals(ActHistoryType.SALES) || actHistoryType.equals(ActHistoryType.RABATE))
				{
					List<TrxorderGoods> togList = allrs.getTrxOrderGoodsList();
					for (TrxorderGoods tog : togList)
					{
						trxAmount = trxAmount + allrs.getTrxAmount() + "|";
						createDate = createDate + DateUtils.dateToStr(allrs.getCreateDate()) + "|";
						strActHistoryType = strActHistoryType + allrs.getActHistoryType().name() + "|";
						description = description + tog.getGoodsName() + "|";
					}
				} else if (actHistoryType.equals(ActHistoryType.REFUND))
				{
					trxAmount = trxAmount + allrs.getTrxAmount() + "|";
					createDate = createDate + DateUtils.dateToStr(allrs.getCreateDate()) + "|";
					strActHistoryType = strActHistoryType + allrs.getActHistoryType().name() + "|";
					description = description + allrs.getTrxOrderGoodsList().get(0).getGoodsName() + "|";
				} else
				{
					trxAmount = trxAmount + allrs.getTrxAmount() + "|";
					createDate = createDate + DateUtils.dateToStr(allrs.getCreateDate()) + "|";
					strActHistoryType = strActHistoryType + allrs.getActHistoryType().name() + "|";
					description = description + allrs.getDescription() + "|";
				}
			}

			if (createDate.contains("|"))
			{
				createDate = createDate.substring(0, createDate.lastIndexOf("|"));
			}
			if (strActHistoryType.contains("|"))
			{
				strActHistoryType = strActHistoryType.substring(0, strActHistoryType.lastIndexOf("|"));
			}
			if (description.contains("|"))
			{
				description = description.substring(0, description.lastIndexOf("|"));
			}
			if (trxAmount.contains("|"))
			{
				trxAmount = trxAmount.substring(0, trxAmount.lastIndexOf("|"));
			}

			responseData.setCreateDate(createDate);
			responseData.setStrActHistoryType(strActHistoryType);
			responseData.setDescription(description+" ");
			responseData.setTrxAmount(trxAmount);
			responseData.setTotalRows(Long.valueOf(totalRows));
			responseData.setUuid(uuid);
			responseData.setUserId(userId);
			responseData.setRowsOffset(Long.valueOf(pageOffset));
			responseData.setPageSize(Long.valueOf(pageSize));
		} else
		{
			throw new Exception();
		}

		return responseData;
	}

	/**
	 * 查看购物车列表
	 * 
	 * @param requestData
	 * @return
	 */

	public TrxResponseData qryShoppingCart(TrxRequestData requestData)
	{

		Long userId = requestData.getUserId();
		Long pageSize = requestData.getPageSize();
		Long rowsoffset = requestData.getRowsOffset();

		TrxResponseData trxResponseData = shopCartService.qryShoppingCart(userId, pageSize, rowsoffset);

		return trxResponseData;

	}

	/**
	 * 添加购物车
	 * 
	 * @param requestData
	 * @return
	 */

	public TrxResponseData addShoppingCart(TrxRequestData requestData) throws ShoppingCartException
	{

		Long userId = requestData.getUserId();

		String goodsId = requestData.getGoodsId();
		String goodsCount = requestData.getGoodsCount();

		TrxResponseData trxResponseData = shopCartService.addShoppingCart(String.valueOf(userId), goodsId, goodsCount);

		return trxResponseData;
	}

	/**
	 * 删除购物车
	 * 
	 * @param requestData
	 * @return
	 */
	public TrxResponseData delShoppingCartById(TrxRequestData requestData) throws ShoppingCartException
	{

		Long userId = requestData.getUserId();
		String shopCartId = requestData.getShopCartId();

		TrxResponseData trxResponseData = shopCartService.delShoppingCartById(String.valueOf(userId), shopCartId);

		return trxResponseData;

	}

	/**
	 * 查看商品订单列表(供手机客户端用)
	 * 
	 * @param requestData
	 * @return
	 * @throws Exception
	 */
	public TrxResponseData qryTrxorderGoodsByUserId(TrxRequestData requestData) throws Exception
	{

		TrxResponseData responseData = new TrxResponseData();
		Long userId = requestData.getUserId();
		String trxStatus = requestData.getTrxStatus();

		TrxConstant.checkTrxStatus(trxStatus);// 请求状态值校验

		Long rowsOffset = requestData.getRowsOffset();// 偏移量
		Long pageSize = requestData.getPageSize();// 页面大小

		int totalRows = trxorderGoodsService.findPageCountByUserIdStatus(userId, trxStatus);

		responseData = trxorderGoodsService.findTrxorderGoodsByUserIdStatus(userId, rowsOffset.intValue(), pageSize.intValue(), trxStatus);

		responseData.setTotalRows(Long.valueOf(totalRows));
		responseData.setPageSize(pageSize);
		responseData.setRowsOffset(rowsOffset);

		return responseData;

	}

	/**
	 * 查看凭证密码
	 * 
	 * @param requestData
	 * @return
	 * @throws VerifyBelongException
	 * @throws TrxorderGoodsException
	 *             ,Exception
	 */
	public TrxResponseData qryVoucherByTgId(TrxRequestData requestData) throws VerifyBelongException, TrxorderGoodsException, Exception
	{

		Long trxorderGoodsId = requestData.getTrxorderGoodsId();
		TrxResponseData responseData = new TrxResponseData();
		logger.info("++++++++++++trxOrderGoodsId:" + trxorderGoodsId + "+++++++++ReqChannel" + requestData.getReqChannel().name());

		// 用户鉴权
		boolean verifyResult = trxorderGoodsService.verifyBelong(trxorderGoodsId, requestData.getUserId(),true);
		if (!verifyResult)
		{
			throw new VerifyBelongException(BaseException.TRXORDERGOODS_VERIFY_BELONG_FAILED);
		}
		responseData = voucherService.queryVoucher(requestData);

		return responseData;

	}
	
	/**
     * 商品订单查询
     */
    @Override
    public List<Map<String,Object>> getTrxGoodsByIds(Map<String, String> map) {
        return trxOrderService.getTrxGoodsByIds(map);
    }
    /**
     * 查询商家购买数量
     * 0团购        1点餐        2网票        3网店
     * @param map
     * @return Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> queryTrxGoodsBuyCountForGuest(Map<String, String> map) {
        
        //获取商家id
        String guestId=map.get("guestIdForSales");
        logger.info("+++ queryTrxGoodsBuyCountForGuest guestId: "+guestId);
        Map<String, Object> resMap =new HashMap<String, Object>();
        
        if (!(StringUtils.isEmpty(guestId) || guestId.length()!=7)) {
            //memCached中存取半小时
            Map<String, Object> mapMem=(Map<String, Object>)memCacheService.get(buyCountForSalesKey+guestId);
            if(mapMem!=null && mapMem.size()>0){
                logger.info("+++ queryTrxGoodsBuyCountForGuest getfrom memcache guestId: "+guestId);
                return mapMem;
            }else{
                logger.info("+++ queryTrxGoodsBuyCountForGuest getfrom db guestId: "+guestId);
                Date date = new Date();
                String endDate=DateUtils.dateToStr(date, "yyyy-MM-dd HH:mm:ss");
                String startDate= DateUtils.dateToStr(DateUtils.getFirstDateOfMonth(date), "yyyy-MM-dd HH:mm:ss"); 
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                map.put("trxStatus", TrxStatus.INIT.toString());//状态不为INIT的
                List<Map<String, Object>> list =  trxorderGoodsService.queryTrxGoodsBuyCountForGuest(map);
                String tuanBuyCount = "0";
                String dianCanBuyCount = "0";
                String wangPiaoBuyCount = "0";
                String shopBuyCount = "0";
                //循环读取。没有时为默认值"0"
                if(list!=null && list.size()>0){
                    for( Map<String, Object> map2 :list){
                        if("0".equals(map2.get("bizType").toString())){
                            tuanBuyCount=map2.get("count").toString();
                        }else if("1".equals(map2.get("bizType").toString())){
                            dianCanBuyCount=map2.get("count").toString();       
                        }else if("2".equals(map2.get("bizType").toString())){
                            wangPiaoBuyCount=map2.get("count").toString();
                        }else if("3".equals(map2.get("bizType").toString())){
                            shopBuyCount=map2.get("count").toString();
                        }
                    }
                }
                resMap.put("tuanBuyCount", tuanBuyCount);
                resMap.put("dianCanBuyCount", dianCanBuyCount);
                resMap.put("wangPiaoBuyCount", wangPiaoBuyCount);
                resMap.put("shopBuyCount", shopBuyCount);
                //存memCached半小时
                memCacheService.set(buyCountForSalesKey+guestId, resMap, TrxConstant.TRXCOUNTFORGUESTSALESTIMEOUT);
                return resMap;
            }
        }else{
            logger.info("+++ queryTrxGoodsBuyCountForGuest worng length: "+guestId);
            //不符合7位的直接返回0
            resMap.put("tuanBuyCount", "0");
            resMap.put("dianCanBuyCount", "0");
            resMap.put("wangPiaoBuyCount", "0");
            resMap.put("shopBuyCount", "0");
            return resMap; 
        }
        
    }
    /**
     * 查询商家消费数量
     * 0团购        1点餐        2网票        3网店
     * @param map
     * @return Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> queryTrxGoodsUsedCountForGuest(Map<String, String> map) {
        
        //获取商家id
        String subGuestIds=map.get("subGuestIdForUsed");//多个时以逗号隔开
        logger.info("+++ queryTrxGoodsUsedCountForGuest subGuestIds: "+subGuestIds);
        Map<String, Object> resMap =new HashMap<String, Object>();
        
        if (!StringUtils.isEmpty(subGuestIds)) {
            //验证传输的必须为7位
            String [] guestArr= subGuestIds.split(",");
            for(String subGuestId:guestArr){
                if(subGuestId.length()!=7){
                    logger.info("+++ queryTrxGoodsUsedCountForGuest subGuestId worng length: "+subGuestId+","+subGuestIds);
                    //不符合7位的直接返回0
                    resMap.put("tuanUsedCount", "0");
                    resMap.put("dianCanUsedCount", "0");
                    resMap.put("wangPiaoUsedCount", "0");
                    resMap.put("shopUsedCount", "0");
                    return resMap; 
                }
            }
            
            //memCached中存取半小时
            Map<String, Object> mapMem=(Map<String, Object>)memCacheService.get(usedCountForSalesKey+subGuestIds);
            if(mapMem!=null && mapMem.size()>0){
                logger.info("+++ queryTrxGoodsUsedCountForGuest getfrom memcaced: "+subGuestIds);
                return mapMem;
            }else{
                logger.info("+++ queryTrxGoodsUsedCountForGuest get from db: "+subGuestIds);
                Date date = new Date();
                String endDate=DateUtils.dateToStr(date, "yyyy-MM-dd HH:mm:ss");
                String startDate= DateUtils.dateToStr(DateUtils.getFirstDateOfMonth(date), "yyyy-MM-dd HH:mm:ss"); 
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                //查询已经使用和评价的
                map.put("trxStatus", TrxStatus.USED.toString()+","+TrxStatus.COMMENTED.toString());
                List<Map<String, Object>> list =  trxorderGoodsService.queryTrxGoodsUsedCountForGuest(map);
                String tuanUsedCount = "0";
                String dianCanUsedCount = "0";
                String wangPiaoUsedCount = "0";
                String shopUsedCount = "0";
                //循环读取。没有时为默认值"0"
                if(list!=null && list.size()>0){
                    for( Map<String, Object> map2 :list){
                        if("0".equals(map2.get("bizType").toString())){
                            tuanUsedCount=map2.get("count").toString();
                        }else if("1".equals(map2.get("bizType").toString())){
                            dianCanUsedCount=map2.get("count").toString();       
                        }else if("2".equals(map2.get("bizType").toString())){
                            wangPiaoUsedCount=map2.get("count").toString();
                        }else if("3".equals(map2.get("bizType").toString())){
                            shopUsedCount=map2.get("count").toString();
                        }
                    }
                }
                resMap.put("tuanUsedCount", tuanUsedCount);
                resMap.put("dianCanUsedCount", dianCanUsedCount);
                resMap.put("wangPiaoUsedCount", wangPiaoUsedCount);
                resMap.put("shopUsedCount", shopUsedCount);
                //存memCached半小时
                memCacheService.set(usedCountForSalesKey+subGuestIds, resMap, TrxConstant.TRXCOUNTFORGUESTSALESTIMEOUT);
                return resMap;
            }
        }else{
            logger.info("+++ queryTrxGoodsUsedCountForGuest subGuestIds isEmpty: "+subGuestIds);
            //为空直接返回0
            resMap.put("tuanUsedCount", "0");
            resMap.put("dianCanUsedCount", "0");
            resMap.put("wangPiaoUsedCount", "0");
            resMap.put("shopUsedCount", "0");
            return resMap; 
        }
        
    }
	
	public PayLimitService getPayLimitService()
	{
		return payLimitService;
	}

	public void setPayLimitService(PayLimitService payLimitService)
	{
		this.payLimitService = payLimitService;
	}

	public BizProcessFactory getBizProcessFactory()
	{
		return bizProcessFactory;
	}

	public void setBizProcessFactory(BizProcessFactory bizProcessFactory)
	{
		this.bizProcessFactory = bizProcessFactory;
	}

	public AccountService getAccountService()
	{
		return accountService;
	}

	public void setAccountService(AccountService accountService)
	{
		this.accountService = accountService;
	}

	public RebRecordService getRebRecordService()
	{
		return rebRecordService;
	}

	public void setRebRecordService(RebRecordService rebRecordService)
	{
		this.rebRecordService = rebRecordService;
	}

	public TrxOrderService getTrxOrderService()
	{
		return trxOrderService;
	}

	public void setTrxOrderService(TrxOrderService trxOrderService)
	{
		this.trxOrderService = trxOrderService;
	}

	public RefundService getRefundService()
	{
		return refundService;
	}

	public void setRefundService(RefundService refundService)
	{
		this.refundService = refundService;
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	public void setVoucherService(VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

	public PaymentInfoGeneratorFactory getPaymentInfoGeneratorFactory()
	{
		return paymentInfoGeneratorFactory;
	}

	public void setPaymentInfoGeneratorFactory(PaymentInfoGeneratorFactory paymentInfoGeneratorFactory)
	{
		this.paymentInfoGeneratorFactory = paymentInfoGeneratorFactory;
	}

	public VmAccountService getVmAccountService()
	{
		return vmAccountService;
	}

	public void setVmAccountService(VmAccountService vmAccountService)
	{
		this.vmAccountService = vmAccountService;
	}

	public AccountHistoryService getAccountHistoryService()
	{
		return accountHistoryService;
	}

	public void setAccountHistoryService(AccountHistoryService accountHistoryService)
	{
		this.accountHistoryService = accountHistoryService;
	}

	public NotifyRecordBizService getNotifyRecordBizService()
	{
		return notifyRecordBizService;
	}

	public void setNotifyRecordBizService(NotifyRecordBizService notifyRecordBizService)
	{
		this.notifyRecordBizService = notifyRecordBizService;
	}

	public CardService getCardService()
	{
		return cardService;
	}

	public void setCardService(CardService cardService)
	{
		this.cardService = cardService;
	}

	public TrxorderGoodsService getTrxorderGoodsService()
	{
		return trxorderGoodsService;
	}

	public void setTrxorderGoodsService(TrxorderGoodsService trxorderGoodsService)
	{
		this.trxorderGoodsService = trxorderGoodsService;
	}

	public ProcessService getOrderProcessService()
	{
		return orderProcessService;
	}

	public void setOrderProcessService(ProcessService orderProcessService)
	{
		this.orderProcessService = orderProcessService;
	}



	public ShopCartService getShopCartService() {
		return shopCartService;
	}

	public void setShopCartService(ShopCartService shopCartService) {
		this.shopCartService = shopCartService;
	}

	public TrxSoaService getTrxSoaService() {
		return trxSoaService;
	}

	public void setTrxSoaService(TrxSoaService trxSoaService) {
		this.trxSoaService = trxSoaService;
	}

	public TrxLogService getTrxLogService() {
		return trxLogService;
	}

	public void setTrxLogService(TrxLogService trxLogService) {
		this.trxLogService = trxLogService;
	}

	public PartnerVoucherServiceFactory getPartnerVoucherServiceFactory() {
		return partnerVoucherServiceFactory;
	}

	public void setPartnerVoucherServiceFactory(
			PartnerVoucherServiceFactory partnerVoucherServiceFactory) {
		this.partnerVoucherServiceFactory = partnerVoucherServiceFactory;
	}

	public PartnerCommonService getPartnerCommonService() {
		return partnerCommonService;
	}

	public void setPartnerCommonService(PartnerCommonService partnerCommonService) {
		this.partnerCommonService = partnerCommonService;
	}

	public DiscountCouponService getDiscountCouponService() {
		return discountCouponService;
	}

	public void setDiscountCouponService(DiscountCouponService discountCouponService) {
		this.discountCouponService = discountCouponService;
	}

	public SubAccountService getSubAccountService() {
		return subAccountService;
	}

	public void setSubAccountService(SubAccountService subAccountService) {
		this.subAccountService = subAccountService;
	}

	public AccountNotifyService getAccountNotifyService() {
		return accountNotifyService;
	}

	public void setAccountNotifyService(AccountNotifyService accountNotifyService) {
		this.accountNotifyService = accountNotifyService;
	}
	
	public TrxCouponService getTrxCouponService() {
		return trxCouponService;
}
	
	public void setTrxCouponService(TrxCouponService trxCouponService) {
		this.trxCouponService = trxCouponService;
	}

	public GuestSettleService getGuestSettleService() {
		return guestSettleService;
	}

	public void setGuestSettleService(GuestSettleService guestSettleService) {
		this.guestSettleService = guestSettleService;
	}
	
}
