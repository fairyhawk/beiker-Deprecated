package com.beike.common.entity.discountcoupon;

import java.util.Date;

import com.beike.common.enums.trx.DiscountCouponStatus;

/**   
 * @title: DiscountCoupon.java
 * @package com.beike.common.entity.discountcoupon
 * @description: 线下优惠券
 * @author wangweijie  
 * @date 2012-7-11 下午06:05:19
 * @version v1.0   
 */
public class DiscountCoupon {
	private Long id;				//主键
	private String couponNo;	//优惠券编号
	private String couponPwd;  	//优惠券密码
	private int couponValue;	//优惠券面值
	private int couponType;		//优惠券类型
	private DiscountCouponStatus couponStatus;	//优惠券状态
	private String batchNo;		//所属批次
	private String topupChannel;	//充值渠道
	private Long userId=0L;			//用户ID
	private Long createOperatorId=0L;	//创建操作员ID
	private Long activeOperatorId=0L;	//激活操作员ID
	private Long vmAccountId=0L;	//所属虚拟款项ID
	private Long bizId=0L;			//业务ID
	private Long version = 0L;	//乐观锁版本号
	private String description;	//备注信息
	private Date createDate; 	//创建时间
	private Date modifyDate; 	//更新时间
	private Date activeDate;	//激活时间
	private Date loseDate; 		//失效时间
	
	
	
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
	public int getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(int couponValue) {
		this.couponValue = couponValue;
	}
	public int getCouponType() {
		return couponType;
	}
	public void setCouponType(int couponType) {
		this.couponType = couponType;
	}
	public DiscountCouponStatus getCouponStatus() {
		return couponStatus;
	}
	public void setCouponStatus(DiscountCouponStatus couponStatus) {
		this.couponStatus = couponStatus;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getTopupChannel() {
		return topupChannel;
	}
	public void setTopupChannel(String topupChannel) {
		this.topupChannel = topupChannel;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCreateOperatorId() {
		return createOperatorId;
	}
	public void setCreateOperatorId(Long createOperatorId) {
		this.createOperatorId = createOperatorId;
	}
	public Long getActiveOperatorId() {
		return activeOperatorId;
	}
	public void setActiveOperatorId(Long activeOperatorId) {
		this.activeOperatorId = activeOperatorId;
	}
	public Long getVmAccountId() {
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}
	public Long getBizId() {
		return bizId;
	}
	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Date getLoseDate() {
		return loseDate;
	}
	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}
	public Date getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}
	
}
