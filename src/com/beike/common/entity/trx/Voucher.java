package com.beike.common.entity.trx;

import java.util.Date;

import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.enums.trx.VoucherVerifySource;

/**
 * @Title: Voucher.java
 * @Package com.beike.common.entity.trx
 * @Description: 凭证实体
 * @date May 26, 2011 4:58:40 PM
 * @author wh.cheng
 * @version v1.0
 */
public class Voucher {

	private Long id;

	private Long guestId = 0L;

	private Date createDate;

	private Date activeDate;

	private Date confirmDate;

	private String voucherCode;

	private VoucherStatus voucherStatus;

	private VoucherVerifySource voucherVerifySource;

	private String description = "";

	private Long isPrefetch;// 是否被预。0：未被预取;1：被预取.

	private Date prefetchDate;// 预取时间

	/**
	 * 乐观锁版本号
	 */
	private Long version = 0L;

	public Long getIsPrefetch() {
		return isPrefetch;
	}

	public void setIsPrefetch(Long isPrefetch) {
		this.isPrefetch = isPrefetch;
	}

	public Date getPrefetchDate() {
		return prefetchDate;
	}

	public void setPrefetchDate(Date prefetchDate) {
		this.prefetchDate = prefetchDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	private boolean isSendMerVou = false;// 是否使用的是商家凭证码（用户商家凭证码不足时候的短信模板切换）

	public Voucher() {

	}

	public Voucher(Date createDate, String voucherCode,
			VoucherStatus voucherStatus) {

		this.createDate = createDate;
		this.voucherCode = voucherCode;
		this.voucherStatus = voucherStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public VoucherStatus getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(VoucherStatus voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	/**
	 * @return the voucherVerifySource
	 */
	public VoucherVerifySource getVoucherVerifySource() {
		return voucherVerifySource;
	}

	/**
	 * @param voucherVerifySource
	 *            the voucherVerifySource to set
	 */
	public void setVoucherVerifySource(VoucherVerifySource voucherVerifySource) {
		this.voucherVerifySource = voucherVerifySource;
	}

	public boolean isSendMerVou() {
		return isSendMerVou;
	}

	public void setSendMerVou(boolean isSendMerVou) {
		this.isSendMerVou = isSendMerVou;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Voucher other = (Voucher) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
