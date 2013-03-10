package com.beike.common.bean.trx;

import java.util.List;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.ReqChannel;

/**
 * @Title: TrxResponseData.java
 * @Package com.beike.common.bean.trx
 * @Description: 交易hessian接口响应数据
 * @date May 9, 2011 6:25:16 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxResponseData
{

	private double balance;
	private String userId = "" ;
	private String shopCartId = "" ;
	private String goodsId = "" ;
	private String goodsName = "";
	private String goodsCount ="";
	private String goodsTitle ="" ;
	private String goodsDTPicUrl= "";
	private String goodsPayPrice= "";
	private String goodsSubtotal= "";
	private String goodsIsAvailable= "";
	private String goodsLastUpdate= "";
	private String merchantName= "";
	private String allowBuyCount= "" ;
	private String payLimitInfo= "" ;
	private Long totalRows =0L ;
	private Long pageSize  ;
	private Long rowsOffset ;
	private Long vmAccountId ;
	/**
	 * 支付前券使用提示
	 */
	private String vmAmountListStr="";
	
	
	public String getVmAmountListStr() {
		return vmAmountListStr;
	}
	public void setVmAmountListStr(String vmAmountListStr) {
		this.vmAmountListStr = vmAmountListStr;
	}
	/**
	 *  卡号
	 */
	private String cardNo ;
    /**
     * 密码	
     */
	private String cardPwd ;
	
	private String cardValue ;
	
	/**
	 * 优惠券密码、金额
	 */
	private String couponName;
	private String couponPwd;
	private String couponValue;
	private String couponLimitInfo;
	private String couponvalidDate;
	private String couponToponType="TOPON";
	
	/**
	 * 交易对外接口请求类型
	 */
	private ReqChannel reqChannel;
	
	
	
	/**
	 * 返回状态1为成功，其他值为失败
	 */
	private int rspCode;
	
	/**
	 * 商品订单id
	 */
	private String trxorderGoodsId;
	
	/**
	 * 商品订单号
	 */
	private String trxorderGoodsSn;
	
	/**
	 * 凭证码
	 */
	private String voucherCode;
	
	/**
	 * 凭证类型
	 */
	private String voucherType;
	
	/**
	 * 备注
	 */
	private String description;
	
	private String goodsTrxStatus;
	private String createDate ;
	private String loseDate;
	private String usedDate ;
	private String trxOrderId ;
	
	/**
	 * 最后更新时间
	 */
	private String lastUpdateDate;
	
	/**
	 * 账户历史类型
	 */
	private String strActHistoryType;
	/**
	 * 订单金额
	 */
	private String trxAmount;
	
	/**
	 * 限购信息
	 */
	private String payLimitDes="";
	
	/**
	 * 购物车id等信息
	 */
	private String extendInfo;
	
	/**
	 * 查单接口返回状态
	 */
	private String payStatus;
	
	/**
	 * 支付请求号
	 */
	private String payRequestId;
	
	/**
	 * 支付机构交易流水号
	 */
	private String proExternalId;
	
	/**
	 * 查询响应金额
	 */
	private String sucTrxAmount;
	
	/**
	 * 删除购物车使用
	 */
	private List<TrxorderGoods> tgList;
	
	/**
	 *子账户过期信息
	 */
	private String subAccountLose = "";
	
	
	public String getSubAccountLose() {
		return subAccountLose;
	}
	public void setSubAccountLose(String subAccountLose) {
		this.subAccountLose = subAccountLose;
	}
	public List<TrxorderGoods> getTgList() {
		return tgList;
	}
	public void setTgList(List<TrxorderGoods> tgList) {
		this.tgList = tgList;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayRequestId() {
		return payRequestId;
	}
	public void setPayRequestId(String payRequestId) {
		this.payRequestId = payRequestId;
	}
	public String getProExternalId() {
		return proExternalId;
	}
	public void setProExternalId(String proExternalId) {
		this.proExternalId = proExternalId;
	}
	public String getSucTrxAmount() {
		return sucTrxAmount;
	}
	public void setSucTrxAmount(String sucTrxAmount) {
		this.sucTrxAmount = sucTrxAmount;
	}
	public String getExtendInfo() {
		return extendInfo;
	}
	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}
	public String getPayLimitDes() {
		return payLimitDes;
	}
	public void setPayLimitDes(String payLimitDes) {
		this.payLimitDes = payLimitDes;
	}
	/**
	 * 我的钱包需求缓存
	 */
	private String uuid;
	public String getStrActHistoryType() {
		return strActHistoryType;
	}
	public void setStrActHistoryType(String strActHistoryType) {
		this.strActHistoryType = strActHistoryType;
	}
	public String getTrxAmount() {
		return trxAmount;
	}
	public void setTrxAmount(String trxAmount) {
		this.trxAmount = trxAmount;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getGoodsTrxStatus() {
		return goodsTrxStatus;
	}
	public void setGoodsTrxStatus(String goodsTrxStatus) {
		this.goodsTrxStatus = goodsTrxStatus;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getLoseDate() {
		return loseDate;
	}
	public void setLoseDate(String loseDate) {
		this.loseDate = loseDate;
	}
	public String getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(String usedDate) {
		this.usedDate = usedDate;
	}
	public String getTrxOrderId() {
		return trxOrderId;
	}
	public void setTrxOrderId(String trxOrderId) {
		this.trxOrderId = trxOrderId;
	}
	public TrxResponseData(String userId, String trxorderGoodsId, String goodsId, String goodsName, String goodsTitle, String goodsPayPrice,
			String goodsDTPicUrl, String goodsTrxStatus, String createDate, String loseDate, String trxorderGoodsSn, String usedDate,String trxOrderId,
			String voucherType,String merchantName,String lastUpdateDate)
	{
		this.userId = userId;
		this.trxorderGoodsId = trxorderGoodsId;
		this.goodsId = goodsId;
		this.goodsName = goodsName;
		this.goodsTitle = goodsTitle;
		this.goodsPayPrice = goodsPayPrice;
		this.goodsDTPicUrl = goodsDTPicUrl;
		this.goodsTrxStatus = goodsTrxStatus;
		this.createDate = createDate;
		this.loseDate = loseDate;
		this.trxorderGoodsSn = trxorderGoodsSn;
		this.usedDate = usedDate;
		this.trxOrderId = trxOrderId;
		this.voucherType = voucherType;
		this.merchantName = merchantName;
		this.lastUpdateDate = lastUpdateDate;
		
	}
	public Long getTotalRows() {
		return totalRows;
	}
	public Long getPageSize() {
		return pageSize;
	}
	public Long getRowsOffset() {
		return rowsOffset;
	}
	public TrxResponseData(String userId,String trxorderGoodsId,String trxorderGoodsSn,String voucherCode,String voucherType,String description){
		this.userId = userId;
		this.trxorderGoodsId = trxorderGoodsId;
		this.trxorderGoodsSn = trxorderGoodsSn;
		this.voucherCode = voucherCode;
		this.voucherType = voucherType;
		this.description = description;
	}

	public String getTrxorderGoodsId() {
		return trxorderGoodsId;
	}

	public void setTrxorderGoodsId(String trxorderGoodsId) {
		this.trxorderGoodsId = trxorderGoodsId;
	}

	public String getTrxorderGoodsSn() {
		return trxorderGoodsSn;
	}

	public void setTrxorderGoodsSn(String trxorderGoodsSn) {
		this.trxorderGoodsSn = trxorderGoodsSn;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRspCode() {
		return rspCode;
	}

	public void setRspCode(int rspCode) {
		this.rspCode = rspCode;
	}

	public TrxResponseData(String userId, String shopCartId)
	{
		super();
		this.userId = userId;
		this.shopCartId = shopCartId;
	}

	

	public TrxResponseData()
	{

	}

	public TrxResponseData(double balance)
	{
		this.balance = balance;

	}

	public double getBalance()
	{
		return balance;
	}

	public void setBalance(double balance)
	{
		this.balance = balance;
	}

	public String getShopCartId()
	{
		return shopCartId;
	}

	public String getGoodsId()
	{
		return goodsId;
	}

	public String getGoodsName()
	{
		return goodsName;
	}

	public String getGoodsCount()
	{
		return goodsCount;
	}

	public String getGoodsTitle()
	{
		return goodsTitle;
	}

	public String getGoodsDTPicUrl()
	{
		return goodsDTPicUrl;
	}

	public String getGoodsPayPrice()
	{
		return goodsPayPrice;
	}

	public String getGoodsIsAvailable()
	{
		return goodsIsAvailable;
	}

	public String getGoodsLastUpdate()
	{
		return goodsLastUpdate;
	}

	public String getMerchantName()
	{
		return merchantName;
	}

	public void setShopCartId(String shopCartId)
	{
		this.shopCartId = shopCartId;
	}

	public void setGoodsId(String goodsId)
	{
		this.goodsId = goodsId;
	}

	public void setGoodsName(String goodsName)
	{
		this.goodsName = goodsName;
	}

	public void setGoodsCount(String goodsCount)
	{
		this.goodsCount = goodsCount;
	}

	public void setGoodsTitle(String goodsTitle)
	{
		this.goodsTitle = goodsTitle;
	}

	public void setGoodsDTPicUrl(String goodsDTPicUrl)
	{
		this.goodsDTPicUrl = goodsDTPicUrl;
	}

	public void setGoodsPayPrice(String goodsPayPrice)
	{
		this.goodsPayPrice = goodsPayPrice;
	}

	public void setGoodsIsAvailable(String goodsIsAvailable)
	{
		this.goodsIsAvailable = goodsIsAvailable;
	}

	public void setGoodsLastUpdate(String goodsLastUpdate)
	{
		this.goodsLastUpdate = goodsLastUpdate;
	}

	public void setMerchantName(String merchantName)
	{
		this.merchantName = merchantName;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getGoodsSubtotal()
	{
		return goodsSubtotal;
	}

	public void setGoodsSubtotal(String goodsSubtotal)
	{
		this.goodsSubtotal = goodsSubtotal;
	}


	public void setTotalRows(Long totalRows)
	{
		this.totalRows = totalRows;
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
	public ReqChannel getReqChannel()
	{
		return reqChannel;
	}
	public void setCardNo(String cardNo)
	{
		this.cardNo = cardNo;
	}
	public void setCardPwd(String cardPwd)
	{
		this.cardPwd = cardPwd;
	}
	public void setReqChannel(ReqChannel reqChannel)
	{
		this.reqChannel = reqChannel;
	}
	public String getCardValue()
	{
		return cardValue;
	}
	public void setCardValue(String cardValue)
	{
		this.cardValue = cardValue;
	}
	public String getAllowBuyCount()
	{
		return allowBuyCount;
	}
	public void setAllowBuyCount(String allowBuyCount)
	{
		this.allowBuyCount = allowBuyCount;
	}
	public Long getVmAccountId()
	{
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId)
	{
		this.vmAccountId = vmAccountId;
	}
	public String getPayLimitInfo()
	{
		return payLimitInfo;
	}
	public void setPayLimitInfo(String payLimitInfo)
	{
		this.payLimitInfo = payLimitInfo;
	}
	public String getCouponPwd() {
		return couponPwd;
	}
	public void setCouponPwd(String couponPwd) {
		this.couponPwd = couponPwd;
	}
	public String getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(String couponValue) {
		this.couponValue = couponValue;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public String getCouponLimitInfo() {
		return couponLimitInfo;
	}
	public void setCouponLimitInfo(String couponLimitInfo) {
		this.couponLimitInfo = couponLimitInfo;
	}
	public String getCouponvalidDate() {
		return couponvalidDate;
	}
	public void setCouponvalidDate(String couponvalidDate) {
		this.couponvalidDate = couponvalidDate;
	}
	public String getCouponToponType() {
		return couponToponType;
	}
	public void setCouponToponType(String couponToponType) {
		this.couponToponType = couponToponType;
	}
}
