package com.beike.common.bean.trx;

import com.beike.common.entity.trx.SendType;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.enums.trx.BizProcessType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;

/**
 * @Title: TrxRequestData.java
 * @Package com.beike.common.bean.trx
 * @Description: 交易hessian接口请求数据
 * @date May 9, 2011 6:25:16 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxRequestData {

	/**
	 * 发起用户
	 */
	private Long userId;
	
	/**
	 * 商品订单号
	 */
	private Long trxorderGoodsId;
	
	/**
	 * 手机号
	 */
	private String mobile="";
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 描述
	 */
	private String description = "";
	
	/**
	 * 交易对外接口请求类型
	 */
	private ReqChannel reqChannel;
	
	/**
	 * 凭证发送类型
	 */
	private SendType sendType;
	
	/**
	 * 交易状态
	 */
	private String trxStatus;
	
	/**
	 *  卡号
	 */
	private String cardNo ;
    /**
     * 密码	
     */
	private String cardPwd ;
	
	/**
	 * 优惠券密码
	 */
	private String couponPwd;
	
	/**
	 * 操作人
	 */
	private String operator ;
	/**
	 *  退款来源类型
	 */
	private RefundSourceType refundSourceType ;
	/**
	 * 退款处理类型
	 */
	private RefundHandleType refundHandleType ;
	/**
	 * 凭证id
	 */
	private String voucherId ;
	/**
	 * 凭证码
	 */
	private String voucherCode ;
	
	/**
	 * 是否缓存
	 */
	private String isCachePage;
	private String voucherVerifySource ;
	/**
	 * 分店id
	 */
	private String subGuestId ;
	

	
	/**
	 *  UUID值
	 */
	private String uuid;
	
	/**
	 * 用户来源渠道
	 */
	private String csid;
	
	/**
	 * 虚拟账户类别Id
	 */
	private String vmAccountSortId ;
	/**
	 * 虚拟帐号id
	 */
	private String vmAccountId ;
	/**
	 * 过期时间
	 */
	private String loseDate ;
	/**
	 * 成本承担方
	 */
	private String costBear ;
	/**
	 * 是否有金
	 */
	private String isFund ;
	/**
	 * 申请人
	 */
	private String proposer ;
	/**
	 * 追加余额
	 */
	private String amount ;
	/**
	 * 下发请求号
	 */
	private String requestId;
	
	/**
	 * 预购买数量
	 */
	private String toPayCount ;
	
	private String outSmsTemplate="";//分销商外部模板
	
	
	private boolean isVerifyForTg=true;//对于商品订单是否需要鉴权。默认为true。

	/**
	 * 秒杀ID
	 */
	private String miaoshaId;//秒杀ID
	
	/**
	 * 优惠券ID
	 */
	private String couponId;	//优惠券ID 
	
	
	/**
	 * 客户端IP
	 */
	private String clientIp = "";//客户端IP
	
	/**
	 * 找零规则
	 */
	private String notChangeRule="";
	
	/**
	 * 是否找零
	 */
	private String isNotChange = "0";
	
	/**
	 * 是否查询子账户过期信息
	 */
	private String isSubAccountLose = "";
	
	/**
	 * 是否来源于0:正常下单1：点餐单2：电影
	 */
	private String trxBizType = "0";
	
	/**
	 * 已点菜单
	 */
	private String bizJson = "";
	
	
	
	public String getBizJson() {
		return bizJson;
	}
	public void setBizJson(String bizJson) {
		this.bizJson = bizJson;
	}
	public String getTrxBizType() {
		return trxBizType;
	}
	public void setTrxBizType(String trxBizType) {
		this.trxBizType = trxBizType;
	}
	public String getIsSubAccountLose() {
		return isSubAccountLose;
	}
	public void setIsSubAccountLose(String isSubAccountLose) {
		this.isSubAccountLose = isSubAccountLose;
	}
	public String getNotChangeRule() {
		return notChangeRule;
	}
	public void setNotChangeRule(String notChangeRule) {
		this.notChangeRule = notChangeRule;
	}
	public String getIsNotChange() {
		return isNotChange;
	}
	public void setIsNotChange(String isNotChange) {
		this.isNotChange = isNotChange;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getMiaoshaId() {
		return miaoshaId;
	}
	public void setMiaoshaId(String miaoshaId) {
		this.miaoshaId = miaoshaId;
	}
	public String getOutSmsTemplate() {
		return outSmsTemplate;
	}
	public void setOutSmsTemplate(String outSmsTemplate) {
		this.outSmsTemplate = outSmsTemplate;
	}
	public boolean isVerifyForTg() {
		return isVerifyForTg;
	}
	public void setVerifyForTg(boolean isVerifyForTg) {
		this.isVerifyForTg = isVerifyForTg;
	}
	public String getIsCachePage() {
		return isCachePage;
	}
	public void setIsCachePage(String isCachePage) {
		this.isCachePage = isCachePage;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public TrxRequestData(ReqChannel reqChannel,Long userId,Long pageSize,Long rowsOffset,String uuid,String isCachePage){
		this.reqChannel = reqChannel;
		this.userId = userId;
		this.pageSize  = pageSize;
		this.rowsOffset = rowsOffset;
		this.uuid = uuid;
		this.isCachePage = isCachePage;
	}
	

	public String getTrxStatus() {
		return trxStatus;
	}
	
	
	public String payMp;
	
	

	
	/** ***************购物车所需数据************************ */
	
	private String shopCartId;// 购物车id
	private String goodsCount;// 购物车商品的数量
	
	private String goodsId;// 商品ID
	
	
	private String payPrice; // 商品支付价格
	
	public String getSucTrxAmount() {
		return sucTrxAmount;
	}
	public void setSucTrxAmount(String sucTrxAmount) {
		this.sucTrxAmount = sucTrxAmount;
	}


	private String shoppingCartId;//支付成功后删除购物车ID用
	
	private String sucTrxAmount;//完成支付金额
	
	private String createDate;//银联查单使用
	
	private boolean isUseEndDateComLose=false;//是否使用下线时间作为下单时间
	
	private  String  startComLoseDateStr;// 计算过期时间开始时间点
	
	private boolean isUseOutPayPrice=false;//是否使用外部支付价格
	
	
	
	
	public boolean isUseOutPayPrice() {
		return isUseOutPayPrice;
	}
	public void setUseOutPayPrice(boolean isUseOutPayPrice) {
		this.isUseOutPayPrice = isUseOutPayPrice;
	}
	public String getStartComLoseDateStr() {
		return startComLoseDateStr;
	}
	public void setStartComLoseDateStr(String startComLoseDateStr) {
		this.startComLoseDateStr = startComLoseDateStr;
	}
	public boolean isUseEndDateComLose() {
		return isUseEndDateComLose;
	}
	public void setUseEndDateComLose(boolean isUseEndDateComLose) {
		this.isUseEndDateComLose = isUseEndDateComLose;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getShoppingCartId() {
		return shoppingCartId;
	}
	public void setShoppingCartId(String shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}
	public TrxRequestData(Long userId, Long pageSize, Long rowsOffset)
	{
		super();
		this.userId = userId;
		this.pageSize = pageSize;
		this.rowsOffset = rowsOffset;
	}

	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}


	public SendType getSendType() {
		return sendType;
	}

	public void setSendType(SendType sendType) {
		this.sendType = sendType;
	}

	

	public String getPayMp() {
		return payMp;
	}

	public void setPayMp(String payMp) {
		this.payMp = payMp;
	}

	public TrxRequestData(){
		
	}
	
	
	public TrxRequestData(ReqChannel reqChannel,Long userId,String trxStatus,Long pageSize,Long rowsOffset){
		this.reqChannel = reqChannel;
		this.userId = userId;
		this.trxStatus = trxStatus;
		this.pageSize  = pageSize;
		this.rowsOffset = rowsOffset;
	}
	public TrxRequestData(ReqChannel reqChannel,Long trxorderGoodsId,Long userId){
		this.reqChannel = reqChannel;
		this.trxorderGoodsId = trxorderGoodsId;
		this.userId = userId;
	}
	
	public TrxRequestData(ReqChannel reqChannel,Long trxorderGoodsId,Long userId,String mobile,String email,String description){
		this.reqChannel = reqChannel;
		this.trxorderGoodsId = trxorderGoodsId;
		this.userId = userId;
		this.mobile = mobile;
		this.email = email;
		this.description = description;
	}
	
	public Long getTrxorderGoodsId() {
		return trxorderGoodsId;
	}

	public void setTrxorderGoodsId(Long trxorderGoodsId) {
		this.trxorderGoodsId = trxorderGoodsId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ReqChannel getReqChannel() {
		return reqChannel;
	}

	public void setReqChannel(ReqChannel reqChannel) {
		this.reqChannel = reqChannel;
	}
	private String goodsName; // 商品名称
	
	private String rspCode; // 响应错误码
	
	private Long  pageSize ;
	
	private Long  rowsOffset;
	
	

	// //////////////////////////////以上接口2.0版本，以下的会被弃用///////

	

	/**
	 * 订单金额
	 */
	private double trxAmount;

	/**
	 * 发起用户登录名
	 */
	private String userLoginName;
	/**
	 * 扩展信息
	 */
	private String extendInfo = "";

	

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

	private String isRefund;// 是否支持退款
	private BizProcessType bizProcessType;



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

	private String guestId;
	private String orderLoseAbsDate;
	private String orderLoseDate;

	/** ***************交易所需数据************************ */
	private String payRequestId; // 支付请求号
	private String ordAmount;// 订单金额



	private String sourcePrice; // 商品原价

	private String currentPrice; // 商品当前价



	private String rebatePrice; // 返现价格

	private String dividePrice; // 分成价格（分成）




	private String isSendMerVou;// 是否发送商家自有检验码
	
	
	

	
	

	public TrxRequestData(Long userId, String shopCartId)
	{
		super();
		this.userId = userId;
		this.shopCartId = shopCartId;
	}

	public TrxRequestData(Long userId, String goodsId, String goodsCount)
	{
		//super();
		this.userId = userId;
		this.goodsId = goodsId;
		this.goodsCount = goodsCount;
	}


	private String isadvance;// 是否预付费

	public TrxRequestData(Long userId) {
		this.userId = userId;

	}

	public String getIsadvance() {
		return isadvance;
	}

	public void setIsadvance(String isadvance) {
		this.isadvance = isadvance;
	}

	private TrxOrder trxOrder;

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

	public String getGoodsCount()
	{
		return goodsCount;
	}

	public void setGoodsCount(String goodsCount)
	{
		this.goodsCount = goodsCount;
	}

	public String getShopCartId()
	{
		return shopCartId;
	}

	public void setShopCartId(String shopCartId)
	{
		this.shopCartId = shopCartId;
	}

	public Long getPageSize()
	{
		return pageSize;
	}

	public Long getRowsOffset()
	{
		return rowsOffset;
	}

	public void setPageSize(Long pageSize)
	{
		this.pageSize = pageSize;
	}

	public void setRowsOffset(Long rowsOffset)
	{
		this.rowsOffset = rowsOffset;
	}
	public String getCardNo()
	{
		return cardNo;
	}
	public String getCardPwd()
	{
		return cardPwd;
	}
	public void setCardNo(String cardNo)
	{
		this.cardNo = cardNo;
	}
	public void setCardPwd(String cardPwd)
	{
		this.cardPwd = cardPwd;
	}
	public String getOperator()
	{
		return operator;
	}
	public RefundSourceType getRefundSourceType()
	{
		return refundSourceType;
	}
	public RefundHandleType getRefundHandleType()
	{
		return refundHandleType;
	}
	public void setOperator(String operator)
	{
		this.operator = operator;
	}
	public void setRefundSourceType(RefundSourceType refundSourceType)
	{
		this.refundSourceType = refundSourceType;
	}
	public void setRefundHandleType(RefundHandleType refundHandleType)
	{
		this.refundHandleType = refundHandleType;
	}
	public String getVoucherId()
	{
		return voucherId;
	}
	public void setVoucherId(String voucherId)
	{
		this.voucherId = voucherId;
	}
	public String getVoucherCode()
	{
		return voucherCode;
	}
	public String getVoucherVerifySource()
	{
		return voucherVerifySource;
	}
	public void setVoucherCode(String voucherCode)
	{
		this.voucherCode = voucherCode;
	}
	public void setVoucherVerifySource(String voucherVerifySource)
	{
		this.voucherVerifySource = voucherVerifySource;
	}
	public String getSubGuestId()
	{
		return subGuestId;
	}
	public void setSubGuestId(String subGuestId)
	{
		this.subGuestId = subGuestId;
	}
	public String getVmAccountSortId()
	{
		return vmAccountSortId;
	}
	public String getLoseDate()
	{
		return loseDate;
	}
	public String getCostBear()
	{
		return costBear;
	}
	public String getIsFund()
	{
		return isFund;
	}
	public String getProposer()
	{
		return proposer;
	}
	public void setVmAccountSortId(String vmAccountSortId)
	{
		this.vmAccountSortId = vmAccountSortId;
	}
	public void setLoseDate(String loseDate)
	{
		this.loseDate = loseDate;
	}
	public void setCostBear(String costBear)
	{
		this.costBear = costBear;
	}
	public void setIsFund(String isFund)
	{
		this.isFund = isFund;
	}
	public void setProposer(String proposer)
	{
		this.proposer = proposer;
	}
	public String getAmount()
	{
		return amount;
	}
	public void setAmount(String amount)
	{
		this.amount = amount;
	}
	public String getVmAccountId()
	{
		return vmAccountId;
	}
	public void setVmAccountId(String vmAccountId)
	{
		this.vmAccountId = vmAccountId;
	}
	public String getToPayCount()
	{
		return toPayCount;
	}
	public void setToPayCount(String toPayCount)
	{
		this.toPayCount = toPayCount;
	}
	public String getCouponPwd() {
		return couponPwd;
	}
	public void setCouponPwd(String couponPwd) {
		this.couponPwd = couponPwd;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getCsid() {
		return csid;
	}
	public void setCsid(String csid) {
		this.csid = csid;
	}
}
