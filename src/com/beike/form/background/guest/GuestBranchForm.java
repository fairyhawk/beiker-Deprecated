package com.beike.form.background.guest;

import java.sql.Timestamp;

/**
 * Title : 	GuestBranchForm
 * <p/>
 * Description	:分公司表单对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-06-03    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03  
 */
public class GuestBranchForm {

	private int branchId;                    //分店ID    PK
	private String branchCnName;             //分店名称
	private int branchCountryId;             //分店所属国
	private int branchProvinceId;            //分店所属省
	private int branchCityId;                //分店所属市
	private int branchCityAreaId;            //区ID
	private String branchAddress;            //分店详细地址
	private String branchRegionId;           //周边地标
	private String branchBusinessTime;       //营业时间
	private String branchBookPhone;          //预订电话
	private String branchLon;                //经度
	private String branchLat;                //维度
	private String branchStatus;             //分店状态(0,ACTIVE;1,UNACTIVE)
	private int guestId;                   //公司ID
	private String brandName;                //品牌名称                
	private Timestamp branchCreateTime;      //分店创建时间
	private Timestamp branchModifyTime;      //分店修改时间
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public String getBranchCnName() {
		return branchCnName;
	}
	public void setBranchCnName(String branchCnName) {
		this.branchCnName = branchCnName;
	}
	public int getBranchCountryId() {
		return branchCountryId;
	}
	public void setBranchCountryId(int branchCountryId) {
		this.branchCountryId = branchCountryId;
	}
	public int getBranchProvinceId() {
		return branchProvinceId;
	}
	public void setBranchProvinceId(int branchProvinceId) {
		this.branchProvinceId = branchProvinceId;
	}
	public int getBranchCityId() {
		return branchCityId;
	}
	public void setBranchCityId(int branchCityId) {
		this.branchCityId = branchCityId;
	}
	public int getBranchCityAreaId() {
		return branchCityAreaId;
	}
	public void setBranchCityAreaId(int branchCityAreaId) {
		this.branchCityAreaId = branchCityAreaId;
	}
	public String getBranchAddress() {
		return branchAddress;
	}
	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}
	public String getBranchRegionId() {
		return branchRegionId;
	}
	public void setBranchRegionId(String branchRegionId) {
		this.branchRegionId = branchRegionId;
	}
	public String getBranchBusinessTime() {
		return branchBusinessTime;
	}
	public void setBranchBusinessTime(String branchBusinessTime) {
		this.branchBusinessTime = branchBusinessTime;
	}
	public String getBranchBookPhone() {
		return branchBookPhone;
	}
	public void setBranchBookPhone(String branchBookPhone) {
		this.branchBookPhone = branchBookPhone;
	}
	public String getBranchLon() {
		return branchLon;
	}
	public void setBranchLon(String branchLon) {
		this.branchLon = branchLon;
	}
	public String getBranchLat() {
		return branchLat;
	}
	public void setBranchLat(String branchLat) {
		this.branchLat = branchLat;
	}
	public String getBranchStatus() {
		return branchStatus;
	}
	public void setBranchStatus(String branchStatus) {
		this.branchStatus = branchStatus;
	}
	
	public int getGuestId() {
		return guestId;
	}
	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public Timestamp getBranchCreateTime() {
		return branchCreateTime;
	}
	public void setBranchCreateTime(Timestamp branchCreateTime) {
		this.branchCreateTime = branchCreateTime;
	}
	public Timestamp getBranchModifyTime() {
		return branchModifyTime;
	}
	public void setBranchModifyTime(Timestamp branchModifyTime) {
		this.branchModifyTime = branchModifyTime;
	}
	
}
