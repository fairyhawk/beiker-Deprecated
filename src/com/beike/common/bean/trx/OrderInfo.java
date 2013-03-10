package com.beike.common.bean.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.BizProcessType;
import com.beike.common.enums.trx.ReqChannel;

/**
 * @Title: OrderInfo.java
 * @Package com.beike.common.bean
 * @Description: 订单bean
 * @date May 9, 2011 6:25:16 PM
 * @author wh.cheng
 * @version v1.0
 */
public class OrderInfo {

	/**
	 * 订单金额
	 */
	private double trxAmount;
	/**
	 * 发起用户
	 */
	private Long userId;
	/**
	 * 发起用户登录名
	 */
	private String userLoginName;
	/**
	 * 扩展信息
	 */
	private String extendInfo = "";

	/**
	 * 商户订单号
	 */
	private String requestId;

	/**
	 * 请求IP
	 * 
	 */
	private String requestIp;

	private String payResult;

	private boolean actPayFlag; // 账户余额支付触发标志位

	private double needPayAamount;
	/**
	 * 业务处理类型
	 */
	private String bizType;
	/**
	 * 交易类型
	 */
	private String trxType; // add by wenhua.cheng 常规交易/0元抽奖/秒杀/打折引擎类型区分

	private String prizeId;// 0元抽奖ID
	
	private Long trxId;
	
	private Long paymentId;	//add by wangweijie 用于支付宝新老账户交替 退款接口

	private String isRefund;// 是否支持退款
	private BizProcessType bizProcessType;

	private String rspCode; // 响应错误码

	private String rspStatus; // 响应状态

	private String balance; // 响应余额

	private String providerType;// 支付机构

	private String providerChannel;// 支付通道
	private String payGoodsName; // 往支付公司请求的名字
	private String proExternalId; // 支付机构交易流水号

	private String payStatus;// 支付状态//自动补单用

	private String qryAmount;// 查询响应金额

	private String refundReqAmount;// 退款请求金额

	private String refundRequestId;// 退款请求号

	private String refundRspAmount;// 退款响应金额

	private String refundStatus;// 退款响应状态

	private String refundRspCode;// 退款响应码
	
	private String payLinkInfo="";//网银支付链接
	
	

	private String guestId;
	private String orderLoseAbsDate;
	private String orderLoseDate;

	/** ***************交易所需数据************************ */
	private String payRequestId=""; // 支付请求号
	private String ordAmount;// 订单金额

	private String goodsName; // 商品名称

	private String sourcePrice; // 商品原价

	private String currentPrice; // 商品当前价

	private String payPrice; // 商品支付价格

	private String rebatePrice; // 返现价格

	private String dividePrice; // 分成价格（分成）

	private String goodsId;// 商品ID
	private String description = "";// 描述

	private String isSendMerVou;// 是否发送商家自有检验码
	
	private String isadvance;// 是否预付费
	
	private  String payLimitDes;//限购表达式
	
	private TrxOrder trxOrder;
	private String createDate;//订单创建时间  ----UPOP查单需要

    private boolean isNeedLock=true;//是否需要应用级别的锁.默认需要
    
    private  boolean isNeedActHis=true;//是否需要走帐务..默认需要
    
    private String mobile="";//分销商需要手机号传入
	
	private  List<Payment>  paymentList;//所需支付记录
	
	private  List<Account>  accountList;//所需个人账户
	
	private  List<TrxorderGoods>  tgList;//所需商品订单
	
	private List<TrxorderGoods> unSingleOverRunTgList;	//非个人超限trxorderGoods数据
	
	private List<TrxorderGoods> singleOverRunTgList;  //个人超限trxorderGoods数据
	
	private ReqChannel reqChannel;		//请求渠道
	/**
	 * 分销商goodsId
	 */
	private String outGoodsId = "0";
	
	private  Map<Long, Integer> saleCountmap;//销售量

	private String partnerNo="";//分销商编号
	
	private String outRequestId="";//分销商外部请求号
	
	private  String outSmsTemplate="";//分销商提供的自有短信模板
	
	private  String  startComLostDateStr;//开始计算过期的时间的时间点(对购买后几日过期有效)
	
	private String miaoshaStr = "";//秒杀ID
	
	private TrxCoupon trxCoupon;//优惠券
	
	/**
	 * 客户端IP
	 */
	private String clientIp = "";//客户端IP
	

	private boolean  isTrxRetry=false;    //是否内部交易重试(对分销商请求的订单)
	
	/**
	 * 是否来源于0:正常下单1：点餐单2：电影票
	 */
	private String trxBizType = "0";
	
	
	public String getTrxBizType() {
		return trxBizType;
	}
	public void setTrxBizType(String trxBizType) {
		this.trxBizType = trxBizType;
	}
	/**
	 * 已点菜单
	 */
	private String bizJson = "";
	
	/**
	 * 分店ID
	 */
	private String subGuestId = "0";	
	
	
	public String getSubGuestId() {
		return subGuestId;
	}
	public void setSubGuestId(String subGuestId) {
		this.subGuestId = subGuestId;
	}


	public String getBizJson() {
		return bizJson;
	}
	public void setBizJson(String bizJson) {
		this.bizJson = bizJson;
	}
	public boolean isTrxRetry() {
		return isTrxRetry;
	}



	public void setTrxRetry(boolean isTrxRetry) {
		this.isTrxRetry = isTrxRetry;
	}



	public String getPartnerNo() {
		return partnerNo;
	}



	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}

	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getMiaoshaStr() {
		return miaoshaStr;
	}

	public void setMiaoshaStr(String miaoshaStr) {
		this.miaoshaStr = miaoshaStr;
	}

	public String getStartComLostDateStr() {
		return startComLostDateStr;
	}

	public void setStartComLostDateStr(String startComLostDateStr) {
		this.startComLostDateStr = startComLostDateStr;
	}

	public String getOutSmsTemplate() {
		return outSmsTemplate;
	}

	public void setOutSmsTemplate(String outSmsTemplate) {
		this.outSmsTemplate = outSmsTemplate;
	}

	public String getOutRequestId() {
		return outRequestId;
	}

	public void setOutRequestId(String outRequestId) {
		this.outRequestId = outRequestId;
	}

	public String getOutGoodsId() {
		return outGoodsId;
	}

	public void setOutGoodsId(String outGoodsId) {
		this.outGoodsId = outGoodsId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public boolean isNeedActHis() {
		return isNeedActHis;
	}

	public void setNeedActHis(boolean isNeedActHis) {
		this.isNeedActHis = isNeedActHis;
	}

	public Map<Long, Integer> getSaleCountmap() {
		return saleCountmap;
	}

	public void setSaleCountmap(Map<Long, Integer> saleCountmap) {
		this.saleCountmap = saleCountmap;
	}
	public boolean isNeedLock() {
		return isNeedLock;
	}

	public void setNeedLock(boolean isNeedLock) {
		this.isNeedLock = isNeedLock;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getIsadvance() {
		return isadvance;
	}

	public void setIsadvance(String isadvance) {
		this.isadvance = isadvance;
	}
	


	public String getPayLimitDes() {
		return payLimitDes;
	}

	/** ***************交易所需数据************************ */

	public double getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(double trxAmount) {
		this.trxAmount = trxAmount;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public BizProcessType getBizProcessType() {
		return bizProcessType;
	}

	public void setBizProcessType(BizProcessType bizProcessType) {
		this.bizProcessType = bizProcessType;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspStatus() {
		return rspStatus;
	}

	public void setRspStatus(String rspStatus) {
		this.rspStatus = rspStatus;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public String getPayGoodsName() {
		return payGoodsName;
	}

	public void setPayGoodsName(String payGoodsName) {
		this.payGoodsName = payGoodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getOrdAmount() {
		return ordAmount;
	}

	public void setOrdAmount(String ordAmount) {
		this.ordAmount = ordAmount;
	}

	public String getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(String sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public String getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	public String getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(String rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public String getDividePrice() {
		return dividePrice;
	}

	public void setDividePrice(String dividePrice) {
		this.dividePrice = dividePrice;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getPayResult() {
		return payResult;
	}

	public void setPayResult(String payResult) {
		this.payResult = payResult;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getProviderChannel() {
		return providerChannel;
	}

	public void setProviderChannel(String providerChannel) {
		this.providerChannel = providerChannel;
	}

	public boolean isActPayFlag() {
		return actPayFlag;
	}

	public void setActPayFlag(boolean actPayFlag) {
		this.actPayFlag = actPayFlag;
	}

	public Long getTrxId() {
		return trxId;
	}

	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}

	public String getPayRequestId() {
		return payRequestId;
	}

	public void setPayRequestId(String payRequestId) {
		this.payRequestId = payRequestId;
	}

	public double getNeedPayAamount() {
		return needPayAamount;
	}

	public void setNeedPayAamount(double needPayAamount) {
		this.needPayAamount = needPayAamount;
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public String getOrderLoseAbsDate() {
		return orderLoseAbsDate;
	}

	public void setOrderLoseAbsDate(String orderLoseAbsDate) {
		this.orderLoseAbsDate = orderLoseAbsDate;
	}

	public String getOrderLoseDate() {
		return orderLoseDate;
	}

	public void setOrderLoseDate(String orderLoseDate) {
		this.orderLoseDate = orderLoseDate;
	}

	public String getProExternalId() {
		return proExternalId;
	}

	public void setProExternalId(String proExternalId) {
		this.proExternalId = proExternalId;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getQryAmount() {
		return qryAmount;
	}

	public void setQryAmount(String qryAmount) {
		this.qryAmount = qryAmount;
	}

	public String getRefundReqAmount() {
		return refundReqAmount;
	}

	public void setRefundReqAmount(String refundReqAmount) {
		this.refundReqAmount = refundReqAmount;
	}

	public String getRefundRspAmount() {
		return refundRspAmount;
	}

	public String getTrxType() {
		return trxType;
	}

	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}

	public String getPrizeId() {
		return prizeId;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public void setPrizeId(String prizeId) {
		this.prizeId = prizeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRefundRspAmount(String refundRspAmount) {
		this.refundRspAmount = refundRspAmount;
	}

	public String getRefundRequestId() {
		return refundRequestId;
	}

	public void setRefundRequestId(String refundRequestId) {
		this.refundRequestId = refundRequestId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getRefundRspCode() {
		return refundRspCode;
	}

	public void setRefundRspCode(String refundRspCode) {
		this.refundRspCode = refundRspCode;
	}

	public String getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(String isRefund) {
		this.isRefund = isRefund;
	}

	public String getIsSendMerVou() {
		return isSendMerVou;
	}

	public void setIsSendMerVou(String isSendMerVou) {
		this.isSendMerVou = isSendMerVou;
	}

	public TrxOrder getTrxOrder() {
		return trxOrder;
	}

	public void setTrxOrder(TrxOrder trxOrder) {
		this.trxOrder = trxOrder;
	}

	public String getPayLinkInfo() {
		return payLinkInfo;
	}

	public void setPayLinkInfo(String payLinkInfo) {
		this.payLinkInfo = payLinkInfo;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	public List<TrxorderGoods> getTgList() {
		return tgList;
	}

	public void setTgList(List<TrxorderGoods> tgList) {
		this.tgList = tgList;
	}

	public ReqChannel getReqChannel() {
		return reqChannel;
	}

	public void setReqChannel(ReqChannel reqChannel) {
		this.reqChannel = reqChannel;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public List<TrxorderGoods> getUnSingleOverRunTgList() {
		return unSingleOverRunTgList;
	}

	public void setUnSingleOverRunTgList(List<TrxorderGoods> unSingleOverRunTgList) {
		this.unSingleOverRunTgList = unSingleOverRunTgList;
	}

	public List<TrxorderGoods> getSingleOverRunTgList() {
		return singleOverRunTgList;
	}

	public void setSingleOverRunTgList(List<TrxorderGoods> singleOverRunTgList) {
		this.singleOverRunTgList = singleOverRunTgList;
	}
	public TrxCoupon getTrxCoupon() {
		return trxCoupon;
	}
	public void setTrxCoupon(TrxCoupon trxCoupon) {
		this.trxCoupon = trxCoupon;
	}
}
