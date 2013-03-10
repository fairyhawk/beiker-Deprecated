package com.beike.common.bean.trx.partner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**   
 * @title: Par360buyOrderParam.java
 * @package com.beike.common.bean.trx.partner
 * @description: 京东报文参数
 * @author wangweijie  
 * @date 2012-8-29 下午02:49:01
 * @version v1.0   
 */
public class Par360buyOrderParam {
	private String venderId;		//合作伙伴ID
	private String resultCode;		//返回结果(200-成功 非200失败)
	private String resultMessage;	//响应信息
	private String data;			//响应内容
	
	private String message;			//请求名称
	private String jdTeamId;		//京东团购ID
	private String venderTeamId;	//合作伙伴团购ID
	private String mobile;			//手机号
	private Date orderDate;			//下单时间
	private String teamPrice;		//购买价格
	private String count;			//订购数量
	private String origin;			//订单总额 单位：分 
	private String jdOrderId;		//京东订单ID
	private Date payTime;			//付款时间
	private Map<String,String> couponMap;	//优惠券信息
	private String sellCount;		//购买数量
	private String venderOrderId;	//合作伙伴订单ID
	private String refundMoney;		//申请退款金额
	private String isAllBack;		//是否该单全退
	
	private String partnerNo;		//分销商编号
	private Long userId;			//userId
	private List<Long> userIdList;	//userIdList
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

	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getJdTeamId() {
		return jdTeamId;
	}
	public void setJdTeamId(String jdTeamId) {
		this.jdTeamId = jdTeamId;
	}
	public String getVenderTeamId() {
		return venderTeamId;
	}
	public void setVenderTeamId(String venderTeamId) {
		this.venderTeamId = venderTeamId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getTeamPrice() {
		return teamPrice;
	}
	public void setTeamPrice(String teamPrice) {
		this.teamPrice = teamPrice;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getJdOrderId() {
		return jdOrderId;
	}
	public void setJdOrderId(String jdOrderId) {
		this.jdOrderId = jdOrderId;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public Map<String, String> getCouponMap() {
		return couponMap;
	}
	public void setCouponMap(Map<String, String> couponMap) {
		this.couponMap = couponMap;
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
	public String getVenderId() {
		return venderId;
	}
	public void setVenderId(String venderId) {
		this.venderId = venderId;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String getVenderOrderId() {
		return venderOrderId;
	}
	public void setVenderOrderId(String venderOrderId) {
		this.venderOrderId = venderOrderId;
	}
	public String getRefundMoney() {
		return refundMoney;
	}
	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}
	public String getIsAllBack() {
		return isAllBack;
	}
	public void setIsAllBack(String isAllBack) {
		this.isAllBack = isAllBack;
	}
	public String getSellCount() {
		return sellCount;
	}
	public void setSellCount(String sellCount) {
		this.sellCount = sellCount;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getPartnerNo() {
		return partnerNo;
	}
	public void setPartnerNo(String partnerNo) {
		this.partnerNo = partnerNo;
	}
}
