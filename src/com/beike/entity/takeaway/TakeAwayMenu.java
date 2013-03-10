package com.beike.entity.takeaway;

import java.io.Serializable;
import java.math.BigDecimal;

public class TakeAwayMenu implements Serializable{
    private Long menuId;

    private Long takeawayId;

    private Long branchId;

    private String menuCategory;

    private String menuName;

    private String menuPrice;

    private String menuUnit;

    private Short menuSort;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

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

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory == null ? null : menuCategory.trim();
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName == null ? null : menuName.trim();
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuUnit() {
        return menuUnit;
    }

    public void setMenuUnit(String menuUnit) {
        this.menuUnit = menuUnit == null ? null : menuUnit.trim();
    }

    public Short getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Short menuSort) {
        this.menuSort = menuSort;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", menuId=").append(menuId);
        sb.append(", takeawayId=").append(takeawayId);
        sb.append(", branchId=").append(branchId);
        sb.append(", menuCategory=").append(menuCategory);
        sb.append(", memuName=").append(menuName);
        sb.append(", menuPrice=").append(menuPrice);
        sb.append(", menuUnit=").append(menuUnit);
        sb.append(", menuSort=").append(menuSort);
        sb.append("]");
        return sb.toString();
    }
}