package com.beike.entity.background.guest;

import java.sql.Timestamp;

/**
 * Title : GuestContact
 * <p/>
 * Description	:公司联系人实体对象
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
public class GuestContact {

	private int contactId;                //联系人ID  PK
	private String contactCnName;         //联系人姓名
	private String contactEmail;          //联系人邮箱
	private String contactTelephone;      //联系人电话
	private String contactMobile;         //联系人手机
	private String contactStatus;         //联系人状态
	private int guestId;                //公司ID
	private Timestamp contactCreateTime;  //联系人创建时间
	private Timestamp contactModifyTime;  //联系人修改时间
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getContactCnName() {
		return contactCnName;
	}
	public void setContactCnName(String contactCnName) {
		this.contactCnName = contactCnName;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactTelephone() {
		return contactTelephone;
	}
	public void setContactTelephone(String contactTelephone) {
		this.contactTelephone = contactTelephone;
	}
	public String getContactMobile() {
		return contactMobile;
	}
	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}
	public String getContactStatus() {
		return contactStatus;
	}
	public void setContactStatus(String contactStatus) {
		this.contactStatus = contactStatus;
	}
	
	public int getGuestId() {
		return guestId;
	}
	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}
	public Timestamp getContactCreateTime() {
		return contactCreateTime;
	}
	public void setContactCreateTime(Timestamp contactCreateTime) {
		this.contactCreateTime = contactCreateTime;
	}
	public Timestamp getContactModifyTime() {
		return contactModifyTime;
	}
	public void setContactModifyTime(Timestamp contactModifyTime) {
		this.contactModifyTime = contactModifyTime;
	}
	
}
