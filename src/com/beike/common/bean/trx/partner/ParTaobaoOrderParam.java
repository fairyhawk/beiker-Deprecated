package com.beike.common.bean.trx.partner;

import java.util.List;

public class ParTaobaoOrderParam {

	
	//接收发码通知
	
	/**
	 * 分销商对应用户的ID
	 */
	private String userId;//分销商当前有效userId
	
	private List<Long>  userIdList;//隶属该分销商所有userId
	
  	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 接口调用时的时间：yyyy-MM-dd HH:mm:ss
	 */
	private String timestamp;
	
	/**
	 * 签名
	 */
	private String sign;
	
	/**
	 * 淘宝订单交易号
	 */
	private String outRequestId;

	/**
	 * 买家手机号
	 */
	private String mobile;
	
	/**
	 * 购买数量
	 */
	private String num;
	
	/**
	 * 接口名称
	 */
	private String method;
	
	/**
	 * 商家编号
	 */
	private String taobaoSid;
	
	/**
	 * 短信或彩信模板       
	 */
	private String smsTemplate;
	
	/**
	 * 有效期开始时间
	 */
	private String validStart;
	
	/**
	 * 有效期截止时间
	 */
	private String validEnds;
	
	/**
	 * 淘宝商品编号
	 */
	private String numIid;
	
	/**
	 * 千品商品Id
	 */
	private String goodsId;
	
	/**
	 * 验证串
	 */
	private String token;
	
	/**
	 * 商品标题
	 */
	private String itemTitle;
	
	
	//接受重新发码通知
	/**
	 * 剩余的可核销数
	 */
	private String leftNum;
	
	//密匙;
   private String keyValue;
	
	//发码成功回调接口
	
	/**
	 * TOP分配给用户的SeesionKey
	 */
	private String session;
	
	/**
	 * 可选，指定响应格式。默认xml,目前支持xml、json
	 */
	private String format;
	
	/**
	 * TOP分配给应用的AppKey
	 */
	private String appKey;
	
	/**
	 * API协议版本，可选值2.0
	 */
	private String v;
	
	/**
	 * 参数的加密方法选择，可选值是：md5,hmac
	 */
	private String signMethod;
	
	/**
	 * 发送成功的验证码及可验证次数的列表，多个码之间用英文逗号分割，所有字符都为英文半角   如：abc:7表示码abc最高可验证7次
	 */
	private String verifyCodes;
	
	/**
	 * 核销份数
	 */
	private  Long consumeNum;
	
	/**
	 * 淘宝开放平台密钥
	 */
	private String noticeKeyValue;

	
	/**
	 * 客户端IP
	 */
	private String clientIp = "";//客户端IP

	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getNoticeKeyValue() {
		return noticeKeyValue;
	}

	public void setNoticeKeyValue(String noticeKeyValue) {
		this.noticeKeyValue = noticeKeyValue;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTaobaoSid() {
		return taobaoSid;
	}

	public void setTaobaoSid(String taobaoSid) {
		this.taobaoSid = taobaoSid;
	}

	public String getSmsTemplate() {
		return smsTemplate;
	}

	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	public String getValidStart() {
		return validStart;
	}

	public void setValidStart(String validStart) {
		this.validStart = validStart;
	}

	public String getValidEnds() {
		return validEnds;
	}

	public void setValidEnds(String validEnds) {
		this.validEnds = validEnds;
	}

	public String getNumIid() {
		return numIid;
	}

	public void setNumIid(String numIid) {
		this.numIid = numIid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getLeftNum() {
		return leftNum;
	}

	public void setLeftNum(String leftNum) {
		this.leftNum = leftNum;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}

	public String getVerifyCodes() {
		return verifyCodes;
	}

	public void setVerifyCodes(String verifyCodes) {
		this.verifyCodes = verifyCodes;
	}

	public Long getConsumeNum()
	{
		return consumeNum;
	}

	public void setConsumeNum(Long consumeNum)
	{
		this.consumeNum = consumeNum;
	}

	public String getKeyValue()
	{
		return keyValue;
	}

	public void setKeyValue(String keyValue)
	{
		this.keyValue = keyValue;
	}
	

	public String getOutRequestId() {
		return outRequestId;
	}

	public void setOutRequestId(String outRequestId) {
		this.outRequestId = outRequestId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public List<Long> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<Long> userIdList) {
		this.userIdList = userIdList;
	}
	
}
