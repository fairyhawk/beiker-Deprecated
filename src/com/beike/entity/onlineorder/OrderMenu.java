package com.beike.entity.onlineorder;

import java.io.Serializable;


public class OrderMenu implements Serializable{
	
	

	private Long menuId;

    private Long orderId;

    private String menuCategory;

    private String menuName;

    private double menuPrice;
    
    private int  count;

    private Long branchId;
    
    public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getMenuPrice() {
		return menuPrice;
	}

	public void setMenuPrice(double menuPrice) {
		this.menuPrice = menuPrice;
	}

	private String menuUnit;

    private int menuSort;

    public int getMenuSort() {
		return menuSort;
	}

	public void setMenuSort(int menuSort) {
		this.menuSort = menuSort;
	}

	private String menuLogo;

    private String menuExplain;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory == null ? "" : menuCategory.trim();
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName == null ? "" : menuName.trim();
    }


    public String getMenuUnit() {
        return menuUnit;
    }

    public void setMenuUnit(String menuUnit) {
        this.menuUnit = menuUnit == null ? "" : menuUnit.trim();
    }


    public String getMenuLogo() {
        return menuLogo;
    }

    public void setMenuLogo(String menuLogo) {
        this.menuLogo = menuLogo == null ? "" : menuLogo.trim();
    }

    public String getMenuExplain() {
        return menuExplain;
    }

    public void setMenuExplain(String menuExplain) {
        this.menuExplain = menuExplain == null ? null : menuExplain.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", menuId=").append(menuId);
        sb.append(", orderId=").append(orderId);
        sb.append(", menuCategory=").append(menuCategory);
        sb.append(", memuName=").append(menuName);
        sb.append(", menuPrice=").append(menuPrice);
        sb.append(", menuUnit=").append(menuUnit);
        sb.append(", menuSort=").append(menuSort);
        sb.append(", menuLogo=").append(menuLogo);
        sb.append(", menuExplain=").append(menuExplain);
        sb.append("]");
        return sb.toString();
    }
    
    
    private String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}