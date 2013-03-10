package com.beike.entity.onlineorder;

import com.beike.entity.merchant.Merchant;

public class BranchInfo extends Merchant {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((branchid == null) ? 0 : branchid.hashCode());
		result = prime * result
				+ ((branchname == null) ? 0 : branchname.hashCode());
		result = prime * result
				+ ((businesstime == null) ? 0 : businesstime.hashCode());
		result = prime * result + ((logo == null) ? 0 : logo.hashCode());
		result = prime * result + Float.floatToIntBits(reviewRate);
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
		BranchInfo other = (BranchInfo) obj;
		if (branchid == null) {
			if (other.branchid != null)
				return false;
		} else if (!branchid.equals(other.branchid))
			return false;
		if (branchname == null) {
			if (other.branchname != null)
				return false;
		} else if (!branchname.equals(other.branchname))
			return false;
		if (businesstime == null) {
			if (other.businesstime != null)
				return false;
		} else if (!businesstime.equals(other.businesstime))
			return false;
		if (logo == null) {
			if (other.logo != null)
				return false;
		} else if (!logo.equals(other.logo))
			return false;
		if (Float.floatToIntBits(reviewRate) != Float
				.floatToIntBits(other.reviewRate))
			return false;
		return true;
	}

	private String businesstime;
	
	private String branchname;
	
	private Long branchid;
	public Long getBranchid() {
		return branchid;
	}

	public void setBranchid(Long branchid) {
		this.branchid = branchid;
	}

	private String logo;
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBusinesstime() {
		return businesstime;
	}

	public void setBusinesstime(String businesstime) {
		this.businesstime = businesstime;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public float getReviewRate() {
		return reviewRate;
	}

	public void setReviewRate(float reviewRate) {
		this.reviewRate = reviewRate;
	}

	private float reviewRate;
	
}
