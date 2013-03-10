package com.beike.common.entity.trx;

import java.io.Serializable;
import java.util.Date;

import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.TrxStatus;

/**
 * @Title: Trxoder_goods.java
 * @Package com.beike.common.entity.trx
 * @Description: 订单商品明细
 * @date May 14, 2011 8:52:26 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxorderGoods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String trxGoodsSn;

	private String goodsName; // 商品名称

	private double sourcePrice; // 商品原价

	private double currentPrice; // 商品当前价

	private double payPrice; // 商品支付价格

	private double rebatePrice; // 返现价格

	private double dividePrice; // 分成价格（分成）

	private TrxStatus trxStatus;

	private AuthStatus authStatus;

	private String extend_info = "";

	private String description = "";

	private Date authDate;

	private Long trxorderId;

	private Long goodsId; // 商品ID

	private MerSettleStatus merSettleStatus;

	private Date createDate;

	private Long guestId = 0L;
	private Long voucherId = 0L;
	private Long commentId = 0L;
	private Long orderLoseAbsDate = 0L;
	private Date orderLoseDate;

	private Long subGuestId; // 分店ID
	/**
	 * 前端显示新增属性
	 */
	private String belongMerchant;
	private String goodsPicrl;

	private Date reallyLoseDate;

	private String commentPoint;

	/**
	 * 新增冻结和结算相关属性
	 */

	private Long isFreeze = 0L;

	private Long settlementRecordId = 0L;

	/**
	 * 新增交易类型ID
	 */
	private Long trxRuleId;

	/**
	 * 是否支持退款（对用户和自动过期退款而言,实际运营也可在后台操作退款）
	 * 
	 */

	private boolean isRefund;
	/**
	 * 新增商家ID
	 */
	private Long merchantId;
	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;
	/**
	 * 返现是否下发
	 */
	private boolean isDis = false;

	/**
	 * 是否支持凭证重发
	 */
	@SuppressWarnings("unused")
	private boolean isSupVouReSend;

	/**
	 * 
	 * 是否支持凭证重发通过商家API
	 */
	@SuppressWarnings("unused")
	private boolean isSupVouReSendInMerApi;
	
	/**
	 * 下单来源0：正常下单，1：来源于点菜单，2来源于电影票
	 */
	private int bizType;
	
	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	/**
	 * 分销商goods_id
	 */
	private String outGoodsId ="";//淘宝的超长。用string
	/**
	 * 商家入账状 默认空，入账前置为INIT,成功后置为SUCCESS
	 */
	private String creditStatus="";

	public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public boolean isDis() {
		return isDis;
	}

	/**
	 * 
	 * 是否发送商家自有校验码
	 */
	private int isSendMerVou;
	/**
	 * 
	 * 是否预付款
	 */
	private boolean isadvance = false;

	// 敏感库存时加入，将短信接口分离出交易事物
	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 凭证码
	 */
	private String voucherCode;

	/**
	 * 商品对应的title
	 */
	private String goodsTitle;

	/**
	 * 上传到BOSS的商家码是否充足。同步Voucher pojo 里的isSendMerVou：同真同假
	 */
	private boolean isMerVouEnough;
	
	/**
	 * 最后更新时间
	 */
	private Date lastUpdateDate;
	
	/**
	 * 是否支持预定
	 */
	private Long isScheduled;

	/**
	 * 预定状态
	 */
	private String scheduledStatus;
	
	/**
	 * 预定表主键ID
	 */
	private Long bookingId;
	
	/**
	 * 是否显示全部预定
	 */
	private Long showScheduled;
	/**
	 * 对应影院的id
	 */
	private Long cinemaId ;
	
	// end

	
	public Long getShowScheduled() {
		return showScheduled;
	}

	public Long getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(Long cinemaId) {
		this.cinemaId = cinemaId;
	}

	public void setShowScheduled(Long showScheduled) {
		this.showScheduled = showScheduled;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public Long getIsScheduled() {
		return isScheduled;
	}

	public void setIsScheduled(Long isScheduled) {
		this.isScheduled = isScheduled;
	}

	public String getScheduledStatus() {
		return scheduledStatus;
	}

	public void setScheduledStatus(String scheduledStatus) {
		this.scheduledStatus = scheduledStatus;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getMobile() {
		return mobile;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}



	public String getOutGoodsId() {
		return outGoodsId;
	}

	public void setOutGoodsId(String outGoodsId) {
		this.outGoodsId = outGoodsId;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public boolean isIsadvance() {
		return isadvance;
	}

	public void setIsadvance(boolean isadvance) {
		this.isadvance = isadvance;
	}

	public void setDis(boolean isDis) {
		this.isDis = isDis;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * 
	 * @param trxGoodsSn
	 * @param goodsName
	 * @param sourcePrice
	 * @param currentPrice
	 * @param payPrice
	 * @param rebatePrice
	 * @param dividePrice
	 * @param trxStatus
	 * @param authStatus
	 * @param trxorderId
	 * @param merSettleStatus
	 * @param createDate
	 * @param guestId
	 * @param orderLoseAbsDate
	 * @param orderLoseDate
	 */

	public TrxorderGoods(String trxGoodsSn, String goodsName,
			double sourcePrice,

			double currentPrice, double payPrice, double rebatePrice,
			double dividePrice, TrxStatus trxStatus, AuthStatus authStatus,
			Long trxorderId, MerSettleStatus merSettleStatus, Date createDate,
			Long guestId, Long orderLoseAbsDate, Date orderLoseDate) {
		this.trxGoodsSn = trxGoodsSn;
		this.goodsName = goodsName;
		this.sourcePrice = sourcePrice;
		this.currentPrice = currentPrice;
		this.payPrice = payPrice;
		this.rebatePrice = rebatePrice;
		this.rebatePrice = rebatePrice;
		this.trxStatus = trxStatus;
		this.authStatus = authStatus;
		this.trxorderId = trxorderId;
		this.merSettleStatus = merSettleStatus;
		this.createDate = createDate;
		this.guestId = guestId;
		this.orderLoseAbsDate = orderLoseAbsDate;
		this.orderLoseDate = orderLoseDate;

	}

	public TrxorderGoods() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}

	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public double getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(double sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(double payPrice) {
		this.payPrice = payPrice;
	}

	public double getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(double rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public double getDividePrice() {
		return dividePrice;
	}

	public void setDividePrice(double dividePrice) {
		this.dividePrice = dividePrice;
	}

	public TrxStatus getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(TrxStatus trxStatus) {
		this.trxStatus = trxStatus;
	}

	public AuthStatus getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(AuthStatus authStatus) {
		this.authStatus = authStatus;
	}

	public String getExtend_info() {
		return extend_info;
	}

	public void setExtend_info(String extend_info) {
		this.extend_info = extend_info;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getAuthDate() {
		return authDate;
	}

	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}

	public Long getTrxorderId() {
		return trxorderId;
	}

	public void setTrxorderId(Long trxorderId) {
		this.trxorderId = trxorderId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public MerSettleStatus getMerSettleStatus() {
		return merSettleStatus;
	}

	public void setMerSettleStatus(MerSettleStatus merSettleStatus) {
		this.merSettleStatus = merSettleStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getOrderLoseAbsDate() {
		return orderLoseAbsDate;
	}

	public void setOrderLoseAbsDate(Long orderLoseAbsDate) {
		this.orderLoseAbsDate = orderLoseAbsDate;
	}

	public Date getOrderLoseDate() {
		return orderLoseDate;
	}

	public void setOrderLoseDate(Date orderLoseDate) {
		this.orderLoseDate = orderLoseDate;
	}

	public String getGoodsPicrl() {
		return goodsPicrl;
	}

	public void setGoodsPicrl(String goodsPicrl) {
		this.goodsPicrl = goodsPicrl;
	}

	public Date getReallyLoseDate() {
		return reallyLoseDate;
	}

	public void setReallyLoseDate(Date reallyLoseDate) {
		this.reallyLoseDate = reallyLoseDate;
	}

	public String getBelongMerchant() {
		return belongMerchant;
	}

	public void setBelongMerchant(String belongMerchant) {
		this.belongMerchant = belongMerchant;
	}

	public String getCommentPoint() {
		return commentPoint;
	}

	public void setCommentPoint(String commentPoint) {
		this.commentPoint = commentPoint;
	}

	public Long getIsFreeze() {
		return isFreeze;
	}

	public void setIsFreeze(Long isFreeze) {
		this.isFreeze = isFreeze;
	}

	public Long getSettlementRecordId() {
		return settlementRecordId;
	}

	public void setSettlementRecordId(Long settlementRecordId) {
		this.settlementRecordId = settlementRecordId;
	}

	public Long getTrxRuleId() {
		return trxRuleId;
	}

	public void setTrxRuleId(Long trxRuleId) {
		this.trxRuleId = trxRuleId;
	}

	public Long getSubGuestId() {
		return subGuestId;
	}

	public void setSubGuestId(Long subGuestId) {
		this.subGuestId = subGuestId;
	}

	public boolean isRefund() {
		return isRefund;
	}

	public void setRefund(boolean isRefund) {
		this.isRefund = isRefund;
	}

	public int isSendMerVou() {
		return isSendMerVou;
	}

	public void setSendMerVou(int isSendMerVou) {
		this.isSendMerVou = isSendMerVou;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public boolean isMerVouEnough() {
		return isMerVouEnough;
	}

	public void setMerVouEnough(boolean isMerVouEnough) {
		this.isMerVouEnough = isMerVouEnough;
	}

	public boolean isSupVouReSend() {

		if (TrxStatus.SUCCESS.equals(trxStatus)
				|| (TrxStatus.USED.equals(trxStatus) && (isSendMerVou()==1))
				|| (TrxStatus.COMMENTED.equals(trxStatus) && (isSendMerVou()==1))
				|| isSupVouReSendInMerApi()) {

			return true;

		} else {
			return false;

		}

	}

	public boolean isSupVouReSendInMerApi() {

		if ((TrxStatus.SUCCESS.equals(trxStatus) &&(isSendMerVou()==2))
				|| (TrxStatus.USED.equals(trxStatus)&& (isSendMerVou()==2))
				|| (TrxStatus.COMMENTED.equals(trxStatus) &&(isSendMerVou()==2))
				|| (TrxStatus.USED.equals(trxStatus)&& (isSendMerVou()==3))
				|| (TrxStatus.COMMENTED.equals(trxStatus) &&(isSendMerVou()==3))
				||(TrxStatus.SUCCESS.equals(trxStatus) &&(isSendMerVou()==3))) {

		

			return true;

		} else {
			return false;

		}

	}
}
