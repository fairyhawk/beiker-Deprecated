package com.beike.form.background.coupon;

import java.sql.Timestamp;
/**
 * Title : 	CouponForm
 * <p/>
 * Description	:后台优惠券表单对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-6-17    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-6-17  
 */
public class CouponForm {
	
	private int couponId;
	private String couponName;
	private int couponTagid;
	private String couponTagextid;
	private Timestamp couponEndTime;
	private String couponLogo;
	private String couponRules;
	private String couponBranchId;
	private int goodsId;
	private Timestamp couponCreateTime;
	private int couponModifyTime;
	private int couponNumber;//优惠券编号，用来发给用户的验证编码！6位数字，不重复的！
	private String couponSmstemplate;
	private String couponStatus;
	private int guestId;            //客户id
	private String guestCnName;     //客户名称
	private Timestamp couponOnTimeBegin;
	private Timestamp couponOnTimeEnd;
	private int brandId;
	
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public int getCouponId() {
		return couponId;
	}
	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public int getCouponTagid() {
		return couponTagid;
	}
	public void setCouponTagid(int couponTagid) {
		this.couponTagid = couponTagid;
	}
	public String getCouponTagextid() {
		return couponTagextid;
	}
	public void setCouponTagextid(String couponTagextid) {
		this.couponTagextid = couponTagextid;
	}
	public Timestamp getCouponEndTime() {
		return couponEndTime;
	}
	public void setCouponEndTime(Timestamp couponEndTime) {
		this.couponEndTime = couponEndTime;
	}
	public String getCouponLogo() {
		return couponLogo;
	}
	public void setCouponLogo(String couponLogo) {
		this.couponLogo = couponLogo;
	}
	public String getCouponRules() {
		return couponRules;
	}
	public void setCouponRules(String couponRules) {
		this.couponRules = couponRules;
	}
	public String getCouponBranchId() {
		return couponBranchId;
	}
	public void setCouponBranchId(String couponBranchId) {
		this.couponBranchId = couponBranchId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public Timestamp getCouponCreateTime() {
		return couponCreateTime;
	}
	public void setCouponCreateTime(Timestamp couponCreateTime) {
		this.couponCreateTime = couponCreateTime;
	}
	public int getCouponModifyTime() {
		return couponModifyTime;
	}
	public void setCouponModifyTime(int couponModifyTime) {
		this.couponModifyTime = couponModifyTime;
	}
	public int getCouponNumber() {
		return couponNumber;
	}
	public void setCouponNumber(int couponNumber) {
		this.couponNumber = couponNumber;
	}
	public String getCouponSmstemplate() {
		return couponSmstemplate;
	}
	public void setCouponSmstemplate(String couponSmstemplate) {
		this.couponSmstemplate = couponSmstemplate;
	}
	public String getCouponStatus() {
		return couponStatus;
	}
	public void setCouponStatus(String couponStatus) {
		this.couponStatus = couponStatus;
	}
	public int getGuestId() {
		return guestId;
	}
	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}
	public String getGuestCnName() {
		return guestCnName;
	}
	public void setGuestCnName(String guestCnName) {
		this.guestCnName = guestCnName;
	}
	public Timestamp getCouponOnTimeBegin() {
		return couponOnTimeBegin;
	}
	public void setCouponOnTimeBegin(Timestamp couponOnTimeBegin) {
		this.couponOnTimeBegin = couponOnTimeBegin;
	}
	public Timestamp getCouponOnTimeEnd() {
		return couponOnTimeEnd;
	}
	public void setCouponOnTimeEnd(Timestamp couponOnTimeEnd) {
		this.couponOnTimeEnd = couponOnTimeEnd;
	}
	
}
