package com.beike.common.entity.coupon;

import java.util.Date;

import com.beike.common.enums.trx.TrxCouponStatus;

/**   
 * @title: TrxCoupon.java
 * @package com.beike.common.entity.coupon
 * @description: 优惠券表
 * @author wangweijie  
 * @date 2012-10-30 上午11:23:21
 * @version v1.0   
 */
public class TrxCoupon {
	private Long id;	//主键
	private String couponNo; //优惠券编号
	private String couponPwd;	//优惠券密码
	private Integer couponType;	//优惠券类型：0线上；1线下
	private Double couponBalance;	//优惠券面值
	private TrxCouponStatus couponStatus;//优惠券状态：INIT 初始化;BINDING:绑定;USED 已使用;TIMEOUT 过期;
	private Long activityId;	//所属活动ID
	private Long userId;		//用户ID
	private Long vmAccountId;	//所属虚拟款项ID(冗余)
	private Integer isCreditAct;	//是否入账：0:未入账；1：已经入账
	private String requestId;	//入账请求号
	private Date startDate; //生效日期
	private Date endDate; //过期时间(冗余)
	private Date bindDate;	//激活日期
	private Date useDate;	//使用时间
	private Date createDate = new Date();	//创建日期
	private Date modifyDate = new Date(); 	//修改日期
	private String description;	//描述
	private Long version=0L; //乐观锁版本号
	
	
	/**
	 * 页面展示用参数
	 */
	private String couponName;	//优惠券名称
	private String limitInfo; //页面展示限制信息
	private int limitCode;	//限制编号0:表示有效;2105:优惠券不存在;2106:优惠券金额限制;2107:优惠券品类限制;2108:优惠券日期限制;2109:优惠券不可用 
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCouponNo() {
		return couponNo;
	}
	public void setCouponNo(String couponNo) {
		this.couponNo = couponNo;
	}
	public String getCouponPwd() {
		return couponPwd;
	}
	public void setCouponPwd(String couponPwd) {
		this.couponPwd = couponPwd;
	}
	public Integer getCouponType() {
		return couponType;
	}
	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}
	public Double getCouponBalance() {
		return couponBalance;
	}
	public void setCouponBalance(Double couponBalance) {
		this.couponBalance = couponBalance;
	}
	public TrxCouponStatus getCouponStatus() {
		return couponStatus;
	}
	public void setCouponStatus(TrxCouponStatus couponStatus) {
		this.couponStatus = couponStatus;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getVmAccountId() {
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}
	public Integer getIsCreditAct() {
		return isCreditAct;
	}
	public void setIsCreditAct(Integer isCreditAct) {
		this.isCreditAct = isCreditAct;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getBindDate() {
		return bindDate;
	}
	public void setBindDate(Date bindDate) {
		this.bindDate = bindDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Date getUseDate() {
		return useDate;
	}
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
	public String getLimitInfo() {
		return limitInfo;
	}
	public void setLimitInfo(String limitInfo) {
		this.limitInfo = limitInfo;
	}
	public int getLimitCode() {
		return limitCode;
	}
	public void setLimitCode(int limitCode) {
		this.limitCode = limitCode;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
