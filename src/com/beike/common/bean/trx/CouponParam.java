package com.beike.common.bean.trx;

/**   
 * @title: CouponParam.java
 * @package com.beike.common.bean.trx
 * @description: 优惠券参数集
 * @author wangweijie  
 * @date 2012-11-1 下午03:41:00
 * @version v1.0   
 */
public class CouponParam {
	private double couponBalance;	//优惠券金额
	private double amount;			//实际发生额
	private Long couponId;			//优惠券ID
	private Long vmAccountId;		//虚拟款项ID
	
	
	public double getCouponBalance() {
		return couponBalance;
	}
	public void setCouponBalance(double couponBalance) {
		this.couponBalance = couponBalance;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Long getVmAccountId() {
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}
}
