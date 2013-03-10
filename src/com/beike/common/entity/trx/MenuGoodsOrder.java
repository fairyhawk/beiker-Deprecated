package com.beike.common.entity.trx;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @desc 对应MenuGoodsOrder表的实体
 * @author ljp
 *
 */
public class MenuGoodsOrder {
	private Long id; 
	private Long orderId; //菜单活动商品ID
	private Long menuId;//菜品编号
	private Long menuCount;//菜品数量
	private Long trxOrderId;//订单id
	private Long trxOrderGoodsId;//商品订单id
	private Timestamp createDate;//创建时间
	private int menuSort;//菜品排序
	private BigDecimal menuPrice;//菜品价格
	private String menuCategory;//菜品类目
	private String menuName;//菜品名称
	private String menuUnit;//菜品单位
	private String menuLogo;//菜品图片
	private String menuExplain;//菜品备注
	private String description;//备注
	private Long version;//版本
	
	
	public MenuGoodsOrder() {
		super();
	}


	public MenuGoodsOrder(Long id, Long orderId, Long menuId, Long menuCount,
			Long trxOrderId, Timestamp createDate, int menuSort,
			BigDecimal menuPrice, String menuCategory, String menuName,
			String menuUnit, String menuLogo, String menuExplain,
			String description, Long version) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.menuId = menuId;
		this.menuCount = menuCount;
		this.trxOrderId = trxOrderId;
		this.createDate = createDate;
		this.menuSort = menuSort;
		this.menuPrice = menuPrice;
		this.menuCategory = menuCategory;
		this.menuName = menuName;
		this.menuUnit = menuUnit;
		this.menuLogo = menuLogo;
		this.menuExplain = menuExplain;
		this.description = description;
		this.version = version;
	}


	public Long getTrxOrderGoodsId() {
		return trxOrderGoodsId;
	}


	public void setTrxOrderGoodsId(Long trxOrderGoodsId) {
		this.trxOrderGoodsId = trxOrderGoodsId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getOrderId() {
		return orderId;
	}


	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public Long getMenuId() {
		return menuId;
	}


	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}


	public Long getMenuCount() {
		return menuCount;
	}


	public void setMenuCount(Long menuCount) {
		this.menuCount = menuCount;
	}


	public Long getTrxOrderId() {
		return trxOrderId;
	}


	public void setTrxOrderId(Long trxOrderId) {
		this.trxOrderId = trxOrderId;
	}


	public Timestamp getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}


	public int getMenuSort() {
		return menuSort;
	}


	public void setMenuSort(int menuSort) {
		this.menuSort = menuSort;
	}


	public BigDecimal getMenuPrice() {
		return menuPrice;
	}


	public void setMenuPrice(BigDecimal menuPrice) {
		this.menuPrice = menuPrice;
	}


	public String getMenuCategory() {
		return menuCategory;
	}


	public void setMenuCategory(String menuCategory) {
		this.menuCategory = menuCategory;
	}


	public String getMenuName() {
		return menuName;
	}


	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}


	public String getMenuUnit() {
		return menuUnit;
	}


	public void setMenuUnit(String menuUnit) {
		this.menuUnit = menuUnit;
	}


	public String getMenuLogo() {
		return menuLogo;
	}


	public void setMenuLogo(String menuLogo) {
		this.menuLogo = menuLogo;
	}


	public String getMenuExplain() {
		return menuExplain;
	}


	public void setMenuExplain(String menuExplain) {
		this.menuExplain = menuExplain;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Long getVersion() {
		return version;
	}


	public void setVersion(Long version) {
		this.version = version;
	}
	
	
}
