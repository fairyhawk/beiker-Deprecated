package com.beike.entity.background.guest;

import java.sql.Timestamp;
/**
 * Title : Guest
 * Description :公司实体对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-06-03    lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03
 */
public class Guest {
	private int				guestId;					//公司ID(PK)
	private String		guestCnName;			//公司名称
	private String		guestType;				//公司类型
	private int				guestCountryId;	//所属国
	private int				guestProvinceId;	//所属省
	private int				guestCityId;			//所属市
	private int				guestCityAreaId;	//区ID
	private String		guestAddress;		//公司地址
	private String		guestAccount;		//资金账号
	private String		guestAccountBank; //开户行
	private String		guestContractNo;	//合同编号
	private String		guestEmail;			//开户邮箱
	private Timestamp	guestCreateTime;	//开通时间
	private String		guestUpdateUser;	//修改人ID
	private Timestamp	guestUpdateTime;	//最后修改时间
	private String		guestStatus;			//客户状态(0,ACTIVE;1,UNACTIVE)
	private int				brandId;					//品牌ID
	private String		brandName;				//品牌名称
	private String		guestPwd;
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
	public String getGuestType() {
		return guestType;
	}
	public void setGuestType(String guestType) {
		this.guestType = guestType;
	}
	public int getGuestCountryId() {
		return guestCountryId;
	}
	public void setGuestCountryId(int guestCountryId) {
		this.guestCountryId = guestCountryId;
	}
	public int getGuestProvinceId() {
		return guestProvinceId;
	}
	public void setGuestProvinceId(int guestProvinceId) {
		this.guestProvinceId = guestProvinceId;
	}
	public int getGuestCityId() {
		return guestCityId;
	}
	public void setGuestCityId(int guestCityId) {
		this.guestCityId = guestCityId;
	}
	public String getGuestAddress() {
		return guestAddress;
	}
	public void setGuestAddress(String guestAddress) {
		this.guestAddress = guestAddress;
	}
	public String getGuestAccount() {
		return guestAccount;
	}
	public void setGuestAccount(String guestAccount) {
		this.guestAccount = guestAccount;
	}
	public String getGuestAccountBank() {
		return guestAccountBank;
	}
	public void setGuestAccountBank(String guestAccountBank) {
		this.guestAccountBank = guestAccountBank;
	}
	public String getGuestContractNo() {
		return guestContractNo;
	}
	public void setGuestContractNo(String guestContractNo) {
		this.guestContractNo = guestContractNo;
	}
	public String getGuestEmail() {
		return guestEmail;
	}
	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}
	public Timestamp getGuestCreateTime() {
		return guestCreateTime;
	}
	public void setGuestCreateTime(Timestamp guestCreateTime) {
		this.guestCreateTime = guestCreateTime;
	}
	public String getGuestUpdateUser() {
		return guestUpdateUser;
	}
	public void setGuestUpdateUser(String guestUpdateUser) {
		this.guestUpdateUser = guestUpdateUser;
	}
	public Timestamp getGuestUpdateTime() {
		return guestUpdateTime;
	}
	public void setGuestUpdateTime(Timestamp guestUpdateTime) {
		this.guestUpdateTime = guestUpdateTime;
	}
	public String getGuestStatus() {
		return guestStatus;
	}
	public void setGuestStatus(String guestStatus) {
		this.guestStatus = guestStatus;
	}
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getGuestPwd() {
		return guestPwd;
	}
	public void setGuestPwd(String guestPwd) {
		this.guestPwd = guestPwd;
	}
	public int getGuestCityAreaId() {
		return guestCityAreaId;
	}
	public void setGuestCityAreaId(int guestCityAreaId) {
		this.guestCityAreaId = guestCityAreaId;
	}
	
}
