package com.beike.common.entity.coupon;

import java.util.Date;

/**   
 * @title: CouponActivity.java
 * @package com.beike.common.entity.coupon
 * @description: 优惠券活动表
 * @author wangweijie  
 * @date 2012-10-30 上午11:03:41
 * @version v1.0   
 */
public class CouponActivity {
	private Long id;		//主键
	private Long vmAccountId; //虚拟款项ID
	private String activityName;	//优惠券活动名称
	private String activityType;	//优惠券活动类型(MARKETING_ONLINE:市场线上活动；MARKETING_OFFLINE:市场线下活动；OPERATING:运营活动)
	private String csid;		 //渠道代码（用户来源csid 以分号分隔)
	private Date startDate;			//活动开始时间
	private Date endDate;		//活动结束时间
	private Double limitAmount; 	//金额限制，0表示不限制。否则必须大于等于该金额
	private String limitTagid; //一级属性id 限制，以分号做分割,为空说明无此限制，秒杀对应的ID为MIAOSHA
	private Double couponBalance;	//优惠券面值（元为单位)
	private Long couponTotalNum;	//优惠券张数
	private Date couponStartDate;	//优惠券开始时间
	private Date couponEndDate;	//优惠券结束时间
	private Double totalBalance; //总金额
	private Long operator_id;	//操作人
	private Date create_date = new Date();	//创建时间
	private Date modifyDate = new Date(); //修改时间
	private String description;	//描述（备注)
	private Long version = 0L;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVmAccountId() {
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public String getCsid() {
		return csid;
	}
	public void setCsid(String csid) {
		this.csid = csid;
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
	public Double getLimitAmount() {
		return limitAmount;
	}
	public void setLimitAmount(Double limitAmount) {
		this.limitAmount = limitAmount;
	}
	public String getLimitTagid() {
		return limitTagid;
	}
	public void setLimitTagid(String limitTagid) {
		this.limitTagid = limitTagid;
	}
	public Double getCouponBalance() {
		return couponBalance;
	}
	public void setCouponBalance(Double couponBalance) {
		this.couponBalance = couponBalance;
	}
	public Long getCouponTotalNum() {
		return couponTotalNum;
	}
	public void setCouponTotalNum(Long couponTotalNum) {
		this.couponTotalNum = couponTotalNum;
	}
	public Date getCouponStartDate() {
		return couponStartDate;
	}
	public void setCouponStartDate(Date couponStartDate) {
		this.couponStartDate = couponStartDate;
	}
	public Date getCouponEndDate() {
		return couponEndDate;
	}
	public void setCouponEndDate(Date couponEndDate) {
		this.couponEndDate = couponEndDate;
	}
	public Double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}
	public Long getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(Long operatorId) {
		operator_id = operatorId;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date createDate) {
		create_date = createDate;
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
}
