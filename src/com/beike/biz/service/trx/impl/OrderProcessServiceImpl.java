package com.beike.biz.service.trx.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.BizPaymentServiceFactory;
import com.beike.biz.service.trx.OrderFilmService;
import com.beike.biz.service.trx.PaymentInfoGeneratorFactory;
import com.beike.biz.service.trx.ProcessService;
import com.beike.common.bean.trx.AmountParam;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.PaymentInfo;
import com.beike.common.bean.trx.TrxDataInfo;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.OrderCreateException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.PaymentInfoGeneratorService;
import com.beike.core.service.trx.PaymentService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxRuleService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.img.JsonUtil;

/**
 * @Title: OrderProcessBizManagerIml.java
 * @Package com.beike.biz.service.trx.impl
 * @Description: 交易入口Biz Service
 * @date May 9, 2011 6:33:06 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("orderProcessService")
public class OrderProcessServiceImpl implements ProcessService {

	private final Log logger = LogFactory.getLog(OrderProcessServiceImpl.class);

	@Resource(name = "trxOrderService")
	private TrxOrderService trxOrderService;

	@Resource(name = "accountService")
	private AccountService accountService;

	@Resource(name = "bizPaymentServiceFacroty")
	private BizPaymentServiceFactory bizPaymentServiceFacroty;

	@Resource(name = "trxorderGoodsService")
	private TrxorderGoodsService trxorderGoodsService;

	@Autowired
	private TrxRuleService trxRuleService;

	@Autowired
	private TrxCouponService trxCouponService;

	@Resource(name = "paymentInfoGeneratorFactory")
	private PaymentInfoGeneratorFactory paymentInfoGeneratorFactory;
	
	@Autowired
	private PartnerReqIdService partnerReqIdService;
	@Autowired
	private OrderFilmService orderFilmService;



	@Override
	public OrderInfo processPost(OrderInfo orderInfoResult) throws TrxOrderException,
			PaymentException, TrxorderGoodsException, AccountException,
			VoucherException, RuleException, StaleObjectStateException,
			ProcessServiceException, RebateException, OrderCreateException,CouponException {

		try {
			orderInfoResult.setGoodsName(URLDecoder.decode(orderInfoResult.getGoodsName(),"utf-8"));//对商品名称做decode转码
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 如果是账户支付，则直接出帐。并响应支付结果
		if (orderInfoResult.isActPayFlag()) {
			orderInfoResult.setBizType("act trigger");// 账户余额触发
			TrxDataInfo trxDataInfo=trxOrderService.comleteByBalance(orderInfoResult);
			orderInfoResult.setTgList(trxDataInfo.getAllTgList());//售出的商品订单列表
			orderInfoResult.setSaleCountmap(trxDataInfo.getSaleCountmap());//销售量Map
			
			orderInfoResult.setSingleOverRunTgList(trxDataInfo.getSingleOverRunTgList());		//个人限购商品列表
			orderInfoResult.setUnSingleOverRunTgList(trxDataInfo.getUnSingleOverRunTgList());	//非个人限购商品列表
			
			orderInfoResult.setPayResult("SUCCESS");
		} else {
			// 2.返回支付结果或者组合支付串
			// 2.1检查支付通道在网关是否开通
			String providerType = orderInfoResult.getProviderType();// 支付机构
			String providerChannel = orderInfoResult.getProviderChannel();// 支付通道
			String payRequestId = orderInfoResult.getPayRequestId();

			// 支付网关支持的通道检查（千品余额支付默认开启，无开闭开关）
			Map<String, String> chanelMap = TrxConstant.checkChannelMap(providerType, providerChannel);

			logger.info("++payRequestId:" + payRequestId+ "+++++PayProviderType:" + providerType+ "++++providerChannel:" + providerChannel + "+++");
			if (chanelMap.isEmpty()) {// 如果不支持此通道或通道未开通
				logger.debug("++payRequestId:" + payRequestId+ "+++++CHANNEL_STATUS_INVALID+++");
				throw new OrderCreateException(BaseException.CHANNEL_STATUS_INVALID);
			}
			
			PaymentInfoGeneratorService paymentInfoGeneratorServcie = paymentInfoGeneratorFactory.getPaymentInfoGeneratorService(orderInfoResult);
			String payLinkInfo = paymentInfoGeneratorServcie.getReqDataForPayment(orderInfoResult);
			orderInfoResult.setPayResult("NEEDPAY");
			orderInfoResult.setPayLinkInfo(payLinkInfo);
			logger.info("+++++payRequestId:" + payRequestId+ "++++payLinkInfo:" + payLinkInfo + "+++++++++++++++");
		}

		return orderInfoResult;

	}
	/**
	 * 订单前置处理：下单
	 * @param orderInfo
	 * @return
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 * @throws StaleObjectStateException 
	 * @throws NumberFormatException 
	 * @throws ParseException 
	 */
	public OrderInfo processPro(OrderInfo orderInfo) throws TrxOrderException,CouponException,
			AccountException, PaymentException, TrxorderGoodsException,RuleException, NumberFormatException, StaleObjectStateException {
		
		////分销商请求号处理
		partnerReqIdService.createReqId(orderInfo.getPartnerNo(), orderInfo.getOutRequestId(), new Date());
		
		List<Payment>  paymentList=new ArrayList<Payment>();
		// 获取交易表达式
		String trxType = orderInfo.getTrxType();// 交易类型

		Long trxRuleId = trxRuleService.qryTrxRule(trxType); // 常规交易/0元抽奖/秒杀/打折引擎类型区分

		// 获取参数
		Long userId = orderInfo.getUserId();
		/************************* 抽奖活动交易限制开始 *********************************/
		trxRuleService.limitLottery(userId, orderInfo.getPrizeId(), trxRuleId);
		/************************* 抽奖活动交易限制结束 *********************************/
		String providerType = orderInfo.getProviderType(); // 支付机构
		String providerChannel = orderInfo.getProviderChannel(); // 支付通道
		String goodsName = orderInfo.getGoodsName();// 商品名字

		String goodsId = orderInfo.getGoodsId(); // 商品ID
		String sourcePrice = orderInfo.getSourcePrice(); // 商品原价
		// String currentPrice = orderInfo.getCurrentPrice();// 商品当前价.预留
		String payPrice = orderInfo.getPayPrice(); // 商品支付价格
		String rebatePrice = orderInfo.getRebatePrice(); // 返现价格
		String dividePrice = orderInfo.getDividePrice(); // 分成价格（分成）
		// 新增
		String guestId = orderInfo.getGuestId();// 结算客户
		String orderLoseAbsDate = orderInfo.getOrderLoseAbsDate();// 订单失效时间段
		String orderLoseDate = orderInfo.getOrderLoseDate(); // 订单失效时间点
		String isRefund = orderInfo.getIsRefund();// 是否支持退款
		String isSendMerVou = orderInfo.getIsSendMerVou();// 是否发生商家自有校验码
		String isadvance = orderInfo.getIsadvance();// 是否预付款
		String miaoshaId = orderInfo.getMiaoshaStr();//是否秒杀
		String startComLostDateStr= orderInfo.getStartComLostDateStr();// 计算过期时间起始时间
		String extendInfo = orderInfo.getExtendInfo();

		String description = orderInfo.getDescription();// 备注信息
		String trxBizType = orderInfo.getTrxBizType();// 是否来源0:正常商品1：点餐单2：电影
		String bizJson = orderInfo.getBizJson();//非团购下单信息
		String subGuestId = orderInfo.getSubGuestId();//分店ID

		List<String> trxInfoLlist = new ArrayList<String>();
		// 组装顺序。商品名字，商品ID，商品原价，商品支付价，返现价格，分成价格
		trxInfoLlist.add(goodsName);
		trxInfoLlist.add(goodsId);
		trxInfoLlist.add(sourcePrice);
		trxInfoLlist.add(payPrice);
		trxInfoLlist.add(rebatePrice);
		trxInfoLlist.add(dividePrice);
		trxInfoLlist.add(guestId);
		trxInfoLlist.add(orderLoseAbsDate);
		trxInfoLlist.add(orderLoseDate);
		logger.info("++++++++++orderLoseAbsDate="+orderLoseAbsDate+"++++++++++orderLoseDate="+orderLoseDate);
		trxInfoLlist.add(isRefund);
		trxInfoLlist.add(isSendMerVou);
		trxInfoLlist.add(isadvance);
		trxInfoLlist.add(startComLostDateStr);
		trxInfoLlist.add(miaoshaId);
		trxInfoLlist.add(trxBizType);
		List<String[]> resultList = StringUtils.transTrxInfo(trxInfoLlist);

		String[] payPriceAarray = resultList.get(3);// 获取支付总金额

		double payCountAmount = 0;

		for (String item : payPriceAarray) {
			payCountAmount += Double.parseDouble(item);
		}
		payCountAmount = Amount.cutOff(payCountAmount, 2);

		// 组装订单信息
		TrxOrder trxOrder = new TrxOrder();
		trxOrder.setOrdAmount(payCountAmount);
		trxOrder.setExtendInfo(extendInfo);
		trxOrder.setUserId(userId);
		trxOrder.setOutRequestId(orderInfo.getOutRequestId());
		trxOrder.setMobile(orderInfo.getMobile());
		trxOrder.setReqChannel(orderInfo.getReqChannel());	//请求渠道
		trxOrder.setReqIp(orderInfo.getClientIp());
		if(bizJson.length()>500){
			bizJson = bizJson.substring(0,499);
		}
		trxOrder.setDescription(bizJson);
		List<Account> accountList = accountService.findByUserId(userId);
		Account vcAccount = null;
		Account cashAccount = null;
		if (accountList != null) {
			for (Account account : accountList) {
				if (AccountType.VC.equals(account.getAccountType())) {
					vcAccount = account;
				}
				if (AccountType.CASH.equals(account.getAccountType())) {
					cashAccount = account;
				}
			}
		}
		if (vcAccount == null || cashAccount == null) {
			throw new AccountException(BaseException.ACCOUNT_NOT_FOUND);
		}
		if (!AccountStatus.ACTIVE.equals(vcAccount.getAccountStatus())
				|| !AccountStatus.ACTIVE.equals(cashAccount.getAccountStatus())) {

			throw new AccountException(BaseException.ACCOUNT_STATUS_INVALID);
		}

		

		double couponPayAmount = 0;		//优惠券支付金额
		/*
		 * 判断是否使用 优惠券，如果使用优惠券则判断使用情况
		 */
		TrxCoupon coupon = orderInfo.getTrxCoupon();
		if(coupon!=null){  //此处不用做分销商的券控制，即使分销商有券，外围hessian orderInfo->TrxCoupon为null
			String[] couponGoodsIdArray = null;
			String[] couponMiaoshaIdArray = null; 
			
			if(!"".equals(goodsId)){
				couponGoodsIdArray = resultList.get(1);
			}
			if(!("".equals(miaoshaId) || "0".equals(miaoshaId))){
				couponMiaoshaIdArray =resultList.get(13);//秒杀ID
			}
			trxCouponService.isAvailableCoupon(couponGoodsIdArray, couponMiaoshaIdArray, coupon, payCountAmount);
			/*
			 * 1、计算优惠券的使用金额
			 * 2、计算使用优惠券后的应付金额，订单应付金额=总金额-优惠券的使用金额
			 */
			if(Amount.sub(payCountAmount, coupon.getCouponBalance()) <= 0){
				couponPayAmount = payCountAmount;
			}else{
				couponPayAmount = coupon.getCouponBalance();
			}
			payCountAmount = Amount.sub(payCountAmount, couponPayAmount);//本次交易除去优惠券有效金额后的需要支付账户/现金金额
		}
		
		
		// 1.创建交易订单
		TrxOrder trxOrder2 = trxOrderService.create(trxOrder);
		
		orderInfo.setTrxOrder(trxOrder2);
		Long trxId = trxOrder2.getId();

		orderInfo.setExtendInfo(extendInfo + "-" + trxId); // 重新赋值扩展信息，供日志使用

		double actVcBanlance = Amount.sub(vcAccount.getBalance(), vcAccount.getForzenAmount());

		double actCashBanlance = Amount.sub(cashAccount.getBalance(),cashAccount.getForzenAmount());

		// 计算金额分布
		AmountParam amountParam = computePaymentAmount(actVcBanlance,actCashBanlance, payCountAmount,orderInfo.isNeedActHis());

		//VC的金额，需要加上优惠券的金额 
		amountParam.setActVcPayAmount(Amount.add(amountParam.getActVcPayAmount(), couponPayAmount));

		//增加优惠券使用金额不为零的情况也要记录payment
		if (amountParam.getActVcPayAmount() > 0 || payCountAmount == 0) { // 解除0元购买限制
			// 虚拟币payemnt
			PaymentInfo actVcPaymentInfo = new PaymentInfo();
			actVcPaymentInfo.setPaymentType(PaymentType.ACTVC);
			actVcPaymentInfo.setTrxAmount(amountParam.getActVcPayAmount());
			actVcPaymentInfo.setAccountId(vcAccount.getId());
			actVcPaymentInfo.setTrxorderId(trxId);
			if(null != coupon){
				actVcPaymentInfo.setCouponId(coupon.getId());		//设置优惠券ID
			}
			PaymentService actPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.ACTVC);
			Payment actVcPayment=actPaymentService.create(actVcPaymentInfo);
			paymentList.add(actVcPayment);//payemnt组装

		}

		if (amountParam.getActCashPayAmount() > 0) {

			// actCashpayemnt
			PaymentInfo actCashPaymentInfo = new PaymentInfo();
			actCashPaymentInfo.setPaymentType(PaymentType.ACTCASH);
			actCashPaymentInfo.setTrxAmount(amountParam.getActCashPayAmount());
			actCashPaymentInfo.setAccountId(cashAccount.getId());
			actCashPaymentInfo.setTrxorderId(trxId);
			PaymentService actCashPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.ACTCASH);
			Payment actCashPayment=actCashPaymentService.create(actCashPaymentInfo);
			paymentList.add(actCashPayment);//payemnt组装

		}

		if (amountParam.getNeedPayAmount() > 0) {

			// paycashPayment
			PaymentInfo payCashPaymentInfo = new PaymentInfo();
			payCashPaymentInfo.setPaymentType(PaymentType.PAYCASH);
			payCashPaymentInfo.setTrxAmount(amountParam.getNeedPayAmount());
			payCashPaymentInfo.setAccountId(cashAccount.getId());
			payCashPaymentInfo.setTrxorderId(trxId);
			payCashPaymentInfo.setProviderType(providerType); // 支付结构
			payCashPaymentInfo.setPayChannel(providerChannel); // 支付通道
			PaymentService cashPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.PAYCASH);
			Payment cashPayPaymentResult = cashPaymentService.create(payCashPaymentInfo);
			orderInfo.setPayRequestId(cashPayPaymentResult.getPayRequestId());
			paymentList.add(cashPayPaymentResult);//payemnt组装
			

		}

		// 3.创建trxordergoods
		String[] goodsNameArray = resultList.get(0);
		String[] goodsIdArray = resultList.get(1);
		String[] sourcePriceArray = resultList.get(2);
		String[] payPriceArray = resultList.get(3);
		String[] rebatePriceArray = resultList.get(4);
		String[] dividePriceArray = resultList.get(5);
		// 新增
		String[] guestIdArray = resultList.get(6);
		String[] orderLoseAbsDateArray = resultList.get(7);
		String[] orderLoseDateArray = resultList.get(8);
		String[] isRefundArray = resultList.get(9);
		String[] isSendMerVouArray = resultList.get(10);
		String[] isadvanceArray = resultList.get(11);
		String[] startComLostDateStrArray= resultList.get(12);//计算过期时间的开始时间
		String[] miaoshaIdArray = resultList.get(13);//秒杀ID
		String[] trxBizTypeArray = resultList.get(14);//是否来源0：正常下单1：点餐单2：电影票
		int num = goodsNameArray.length;

		// 暂时放到这里。如果批量插入数据量大，移到dao做批量jdbc插入
		// 如果出现类型转换异常，在外层捕获
		String payGoodsName = "";
		for (int i = 0; i < num; i++) {

			TrxorderGoods trxorderGoods = new TrxorderGoods();
			trxorderGoods.setCreateDate(new Date());
			try {
				String goodsNameSource = URLDecoder.decode(goodsNameArray[i],"utf-8");
				trxorderGoods.setGoodsName(goodsNameSource);
				String orderLoseDateSource = URLDecoder.decode(orderLoseDateArray[i], "utf-8");

				/*
				 * trxorderGoods.setOrderLoseDate(DateUtils.toDate(
				 * orderLoseDateSource, "yyyy-MM-dd HH:mm:ss"));
				 */

				// 新增.先计算出最终的过期时间再入库
				Date orderLoseDateResult;
				Date startComLostDate;
				try {
					startComLostDate =DateUtils.parseToDate(startComLostDateStrArray[i],"yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					startComLostDate=new Date();
					logger.debug(e);
					e.printStackTrace();
				}
				logger.info("++++++++++++startComLostDate="+startComLostDate+"++++++++orderLoseAbsDateArray[i]="+orderLoseAbsDateArray[i]
				           +"+++++++++orderLoseDateSource="+orderLoseDateSource);
				orderLoseDateResult = DateUtils.compareDateInNull(startComLostDate, orderLoseAbsDateArray[i],orderLoseDateSource);
				
				trxorderGoods.setOrderLoseDate(orderLoseDateResult);

				if (trxRuleId.longValue() != 0) {
					trxorderGoods.setExtend_info(orderInfo.getPrizeId());

				}

			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				e.printStackTrace();
			}
			trxorderGoods.setGoodsId((Long.parseLong(goodsIdArray[i])));
			trxorderGoods.setSourcePrice(Amount.cutOff(Double.parseDouble(sourcePriceArray[i]), 2));
			trxorderGoods.setPayPrice(Amount.cutOff(Double.parseDouble(payPriceArray[i]), 2));
			trxorderGoods.setRebatePrice(Amount.cutOff(Double.parseDouble(rebatePriceArray[i]), 2));
			trxorderGoods.setDividePrice(Amount.cutOff(Double.parseDouble(dividePriceArray[i]), 2));

			// 新增
			trxorderGoods.setGuestId((Long.parseLong(guestIdArray[i])));
			trxorderGoods.setOrderLoseAbsDate(Long.parseLong(orderLoseAbsDateArray[i]));
			trxorderGoods.setTrxRuleId(trxRuleId);
			trxorderGoods.setRefund(StringUtils.transBoolean(isRefundArray[i])); // 是否支持退款
			trxorderGoods.setSendMerVou(Integer.valueOf(isSendMerVouArray[i])); // 是否发送商家校验码
			trxorderGoods.setIsadvance(StringUtils.transBoolean(isadvanceArray[i]));// 是否预付款
			trxorderGoods.setBizType(Integer.valueOf(trxBizTypeArray[i]));
			if("1".equals(trxBizTypeArray[i])){//如果为点餐单创建分店ID
				trxorderGoods.setSubGuestId(Long.valueOf(subGuestId));
			}else {
				trxorderGoods.setSubGuestId(0L);
			}
			logger.info("+++++++++++++++++++miaoshaIdArray[i]++++++++"+miaoshaIdArray[i]);
			Long trxRuleIdLong = trxRuleId;
			if(!"0".equals(miaoshaIdArray[i])){   //秒杀需求添加
				trxorderGoods.setExtend_info(miaoshaIdArray[i]);
				trxorderGoods.setRefund(false);
				trxRuleIdLong=3L;
			}
			trxorderGoods.setTrxRuleId(trxRuleIdLong);
			trxorderGoods.setDescription(description);
			trxorderGoods.setTrxorderId(trxId);
			trxorderGoods.setLastUpdateDate(new Date());//最后更新时间
			trxorderGoods.setOutGoodsId(orderInfo.getOutGoodsId());

			Long trxGoodsId = trxorderGoodsService.create(trxorderGoods);
			
			
			//执行beiker_film_goods_order表的更新操作
			if("2".equals(trxBizTypeArray[i])){
				Map<String,Object> jsonMap = JsonUtil.getMapFromJsonString(bizJson);
				String filmPayno = (String)jsonMap.get("filmPayno");//网票网下单成功回传订单号
				String filmId = (String)jsonMap.get("filmId");//网票网下单记录表主键ID
					orderFilmService.updateFilmGoodsOrderById(Long.valueOf(filmId),filmPayno,trxId,trxGoodsId);
			}
		}
		
		
		orderInfo.setTrxId(trxId);
		orderInfo.setRequestId(trxOrder2.getRequestId());// 余额支付时响应给用户订单号
		orderInfo.setActPayFlag(amountParam.isNeedFlag()); // 余额支付标志位
		orderInfo.setNeedPayAamount(amountParam.getNeedPayAmount());
		orderInfo.setPayGoodsName(payGoodsName);
		orderInfo.setPaymentList(paymentList);//预置支付记录列表
		orderInfo.setAccountList(accountList);//预置账户列表
		return orderInfo;

	}
	
	
	/**
	 * 账户支付重试(暂时只对分销商来的订单有效)
	 * @param orderInfo
	 * @return
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 */
	public OrderInfo processReTry(OrderInfo orderInfo){
		
		
		TrxOrder trxOrder=orderInfo.getTrxOrder();
		Long trxId=trxOrder.getId();
		//Long userId=trxOrder.getUserId();
		String requestId=trxOrder.getReqIp();
		String extendInfo=trxOrder.getExtendInfo();
		orderInfo.setExtendInfo(extendInfo + "-" + trxId); // 重新赋值扩展信息，供日志使用

		orderInfo.setPayRequestId("");
		orderInfo.setTrxId(trxId);
		orderInfo.setRequestId(requestId);// 余额支付时响应给用户订单号
		orderInfo.setActPayFlag(true); // 余额支付标志位
		orderInfo.setNeedPayAamount(0.0);
		orderInfo.setPayGoodsName("");
		orderInfo.setGoodsName("");
		
		//List<Account> accountList = accountService.findByUserId(userId);//用户帐户列表（分销商不走帐，暂时不需要账户列表）
		PaymentService actPaymentService = bizPaymentServiceFacroty.getPaymentService(PaymentType.ACTVC);//分销商只有VC支付。暂时只有ACTVC的payment
		List<Payment>  paymentList=actPaymentService.preQryPayment(String.valueOf(trxId));
		orderInfo.setPaymentList(paymentList);//支付记录列表
		//orderInfo.setAccountList(accountList);//账户列表
        
		
		return orderInfo;
		
	}
	/**
	 * 计算金额分布(对分销商，不走帐务的话，则直接将需要支付的金额，赋进actVc)
	 * @param actVcBalance  帐务里虚拟币
	 * @param actCashBalance 账户里现金
	 * @param payCountAmount  需支付的金额
	 * @param isNeedAct  是否需要走帐务
	 * @return
	 */
	public AmountParam computePaymentAmount(double actVcBalance,double actCashBalance, double payCountAmount,boolean isNeedActHis) {
		AmountParam amountParam = new AmountParam();
		if(isNeedActHis){
			logger.info("++++++++actVcBalance:" + actVcBalance+ "->actCashBalance:" + actCashBalance + "->payCountAmount:"+ payCountAmount + "++++++++isNeedActHis:"+isNeedActHis);
			if (actVcBalance >= payCountAmount) { // 全部是VC
				amountParam.setActVcPayAmount(payCountAmount);
				amountParam.setNeedFlag(true); // 余额触发标志
				logger.info("++++++++++++only actvc amount:" + payCountAmount+ "++++++++++++++++isNeedActHis:"+isNeedActHis);
				return amountParam;
			}
			if (Amount.add(actVcBalance, actCashBalance) < payCountAmount) { // 部分是VC。部分是CASH
				amountParam.setActVcPayAmount(actVcBalance);
				amountParam.setActCashPayAmount(actCashBalance);
				double tempAmount = Amount.sub(payCountAmount, actVcBalance);
				amountParam.setNeedPayAmount(Amount.sub(tempAmount, actCashBalance));
				logger.info("++++++++++++NeedPayAmount"+ amountParam.getNeedPayAmount()+ "++++++++++++++++isNeedActHis:"+isNeedActHis);

			} else {
				amountParam.setActVcPayAmount(actVcBalance);
				amountParam.setActCashPayAmount(Amount.sub(payCountAmount,actVcBalance));
				logger.info("++++++++++++actVcPayAmount:" + actVcBalance+ "->actCashPayAmount:" + amountParam.getActCashPayAmount()+ "++++++++++++isNeedActHis:"+isNeedActHis);
				amountParam.setNeedFlag(true); // 余额触发标志
			}
		}else{
			 // 不走帐，则赋需要支付金额到账户虚拟币余额参数
			amountParam.setActVcPayAmount(payCountAmount);
			amountParam.setNeedFlag(true); // 余额触发标志
			logger.info("++++++++++++only actvc amount:" + payCountAmount+ "+++++++++++++++++++++isNeedActHis:"+isNeedActHis);
			return amountParam;
			
		}
		return amountParam;
	}
}
