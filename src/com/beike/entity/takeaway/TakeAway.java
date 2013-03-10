package com.beike.entity.takeaway;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TakeAway implements Serializable{
	
	/**菜单类型:文字版*/
	public static final String MENU_TYPE_W = "W";
	/**菜单类型:图片版*/
	public static final String MENU_TYPE_T = "T"; 
	/**菜单状态：上线*/
	public static final String TAKEAWAY_STATUS_ONLINE = "ONLINE";
	/**菜单状态：下线*/
	public static final String TAKEAWAY_STATUS_OFFLINE = "OFFLINE";
	
    private Long takeawayId;

    private Long branchId;
    
    private String merchantname;

    private String takeawayPhone;

    private String deliveryArea;

    private BigDecimal startAmount;

    private String takeawayTime;

    private String businessAddress;

    private String otherExplain;

    private String menuType;

    private String menuLogo;

    private String takeawayStatus;

    public Long getTakeawayId() {
        return takeawayId;
    }

    public void setTakeawayId(Long takeawayId) {
        this.takeawayId = takeawayId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getTakeawayPhone() {
        return takeawayPhone;
    }

    public void setTakeawayPhone(String takeawayPhone) {
        this.takeawayPhone = takeawayPhone == null ? null : takeawayPhone.trim();
    }

    public String getDeliveryArea() {
        return deliveryArea;
    }

    public void setDeliveryArea(String deliveryArea) {
        this.deliveryArea = deliveryArea == null ? null : deliveryArea.trim();
    }

    public BigDecimal getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(BigDecimal startAmount) {
        this.startAmount = startAmount;
    }

    public String getTakeawayTime() {
        return takeawayTime;
    }

    public void setTakeawayTime(String takeawayTime) {
        this.takeawayTime = takeawayTime;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress == null ? null : businessAddress.trim();
    }

    public String getOtherExplain() {
        return otherExplain;
    }

    public void setOtherExplain(String otherExplain) {
        this.otherExplain = otherExplain == null ? null : otherExplain.trim();
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType == null ? null : menuType.trim();
    }

    public String getMenuLogo() {
        return menuLogo;
    }

    public void setMenuLogo(String menuLogo) {
        this.menuLogo = menuLogo == null ? null : menuLogo.trim();
    }

    public String getTakeawayStatus() {
        return takeawayStatus;
    }

    public void setTakeawayStatus(String takeawayStatus) {
        this.takeawayStatus = takeawayStatus == null ? null : takeawayStatus.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", takeawayId=").append(takeawayId);
        sb.append(", branchId=").append(branchId);
        sb.append(", takeawayPhone=").append(takeawayPhone);
        sb.append(", deliveryArea=").append(deliveryArea);
        sb.append(", startAmount=").append(startAmount);
        sb.append(", takeawayTime=").append(takeawayTime);
        sb.append(", businessAddress=").append(businessAddress);
        sb.append(", otherExplain=").append(otherExplain);
        sb.append(", menuType=").append(menuType);
        sb.append(", menuLogo=").append(menuLogo);
        sb.append(", takeawayStatus=").append(takeawayStatus);
        sb.append(", merchantname=").append(merchantname);
        sb.append("]");
        return sb.toString();
    }

	public String getMerchantname() {
		return merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(!(obj instanceof TakeAway))
			return false;
		TakeAway other = (TakeAway)obj;
		return this.getTakeawayId().equals(other.getTakeawayId());
	}
	
	@Override
	public int hashCode() {
	    return getTakeawayId().hashCode();
	}
}