package com.beike.common.bean.trx.partner;

import java.util.List;

public class Par1mallOrderParam {
	private String checkCode;	//商家验证码,须向运营后台申请开通商家key
	private String merchantId;	//商家ID
	private String sign;		//验证码
	private String erp;			//对接erp
	private String erpVer;		//对接erp版本
	private String format="json";		//返回数据格式 xml/json
	private String ver;			//接口版本1.0
	private String method="";		//方法名
	
	private String orderCode;//订单号(订单编码)
	private Long productId;//1号商城产品ID
	private Integer productNum;//购买数量
	private Double orderAmount;//订单金额
	private String createTime;//购买时间(yyyy-MM-dd HH:mm:ss格式)
	private String paidTime;//支付确认时间(yyyy-MM-dd HH:mm:ss格式)
	private String userPhone;//用户手机号(13、15或18开头的11位)
	private Double productPrice;//产品单价
	private Long outerGroupId;//我方团购ID
	private String partnerOrderCode;//我方订单号
	private Double refundAmount;//退款金额
	private String refundConfirmTime;//退款确认时间（格式：yyyy-MM-dd HH:mm:ss）
	private String voucherCode;//消费券号码
	private String receiveMobile;//接收方手机号码
	private String requestTime;//请求时间
	private String refundRequestTime;//退款请求时间
	private Long userId;			//userId
	private List<Long> userIdList;	//userIdList
	private String clientIp;//客户端IP
	
	private Integer totalCount;	//申请成功记录数
	private Integer updateCount;//返回结果，更新成功记录数
	private Integer errorCount;//返回结果，更新失败记录数
	
	//ErrDetailInfo信息
	private String errorCode="";	//错误编码
	private String errorDes="";	//错误描述
	private String pkInfo="";		//发生错误关键字信息
	
	private String secretKey; //密钥
	private String partnerNo;	//分销商编号
	
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Integer getProductNum() {
		return productNum;
	}
	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}
	public Double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPaidTime() {
		return paidTime;
	}
	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public Long getOuterGroupId() {
		return outerGroupId;
	}
	public void setOuterGroupId(Long outerGroupId) {
		this.outerGroupId = outerGroupId;
	}
	public Double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}
	public String getPartnerOrderCode() {
		return partnerOrderCode;
	}
	public void setPartnerOrderCode(String partnerOrderCode) {
		this.partnerOrderCode = partnerOrderCode;
	}
	public Double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefundConfirmTime() {
		return refundConfirmTime;
	}
	public void setRefundConfirmTime(String refundConfirmTime) {
		this.refundConfirmTime = refundConfirmTime;
	}
	public String getVoucherCode() {
		return voucherCode;
	}
	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
	public String getReceiveMobile() {
		return receiveMobile;
	}
	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getRefundRequestTime() {
		return refundRequestTime;
	}
	public void setRefundRequestTime(String refundRequestTime) {
		this.refundRequestTime = refundRequestTime;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<Long> getUserIdList() {
		return userIdList;
	}
	public void setUserIdList(List<Long> userIdList) {
		this.userIdList = userIdList;
	}
	public Integer getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}
	public Integer getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDes() {
		return errorDes;
	}
	public void setErrorDes(String errorDes) {
		this.errorDes = errorDes;
	}
	public String getPkInfo() {
		return pkInfo;
	}
	public void setPkInfo(String pkInfo) {
		this.pkInfo = pkInfo;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getErp() {
		return erp;
	}
	public void setErp(String erp) {
		this.erp = erp;
	}
	public String getErpVer() {
		return erpVer;
	}
	public void setErpVer(String erpVer) {
		this.erpVer = erpVer;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getPartnerNo() {
		return partnerNo;
	}
	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}
}
