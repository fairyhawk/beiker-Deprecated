package com.beike.entity.goods;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * <p>
 * Title:商品信息
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Goods implements Serializable {

	private Long goodsId;
	private String goodsname; // 商品名称
	private double sourcePrice; // 原价格
	private double currentPrice;// 当前价格
	private double payPrice;
	private double offerPrice;// 优惠了多少钱
	private double rebatePrice;// 返现价格
	private double dividePrice;// 分成价格
	private double discount;// 折扣
	private int maxcount;// 售销上限大最数量
	private Date endTime;// 结束时间
	private String city;// 城市
	private Date startTime;// 开始时间
	private int isavaliable;// 是否可用
	private String logo1;// 详细页面图地址
	private String logo2;// 详细页右侧推荐图地址
	private String logo3;// 列表图
	private String qpsharepic;// 千品物语 图片

	private String goodsTitle;// 商品标题
	private String couponcash;//1是现金券 0不是现金券
	private String isCard;//是否商超卡
	
	private String is_scheduled;
	public String getIs_scheduled() {
		return is_scheduled;
	}
	public void setIs_scheduled(String is_scheduled) {
		this.is_scheduled = is_scheduled;
	}

	/**
	 * 补充属性：商品的虚拟数量 virtual_count字段: 备用 ！！！
	 */
	private int virtualCount;
	//温馨提示
	private String kindlywarnings;
	/**
	 * 补充属性：个人可购买数量
	 */
	private int goodsSingleCount;
	
	/**
	 * 置顶标志
	 */
	private String isTop;
	
	/**
	 * 区域
	 */
	private Set<String> mapRegion;
	
	/**
	 * 区域
	 */
	private String mainRegion;
	
	private String miaoshaid;//秒杀ID

	public String getMiaoshaid() {
		return miaoshaid;
	}
	public void setMiaoshaid(String miaoshaid) {
		this.miaoshaid = miaoshaid;
	}
	public String getMainRegion() {
		return mainRegion;
	}
	public void setMainRegion(String mainRegion) {
		this.mainRegion = mainRegion;
	}
	public Set<String> getMapRegion() {
		return mapRegion;
	}
	public void setMapRegion(Set<String> mapRegion) {
		this.mapRegion = mapRegion;
	}
	public String getIsTop() {
		return isTop;
	}
	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}
	public int getGoodsSingleCount() {
		return goodsSingleCount;
	}

	public void setGoodsSingleCount(int goodsSingleCount) {
		this.goodsSingleCount = goodsSingleCount;
	}

	// add by wh.cheng
	private Long guestId;

	private Long orderLoseAbsDate;
	private Date orderLoseDate;

	// 品牌名称（购物车需求添加字段）renli.yu
	private String merchantname;

	// 品牌id
	private String merchantid;
	private String goodsCount;
	/**
	 * 是否来源于点菜单
	 */
	private int isMenu=0;

	public int getIsMenu() {
		return isMenu;
	}
	public void setIsMenu(int isMenu) {
		this.isMenu = isMenu;
	}

	// 新添加字段是否自动退款 0:不自动退款 1:自动退款
	private int isRefund;

	private int sendRules;// 是否使用千品服务密码
	
	private int isadvance;// 是否预付费
	
	public int getIsadvance() {
		return isadvance;
	}
	public void setIsadvance(int isadvance) {
		this.isadvance = isadvance;
	}

	public int getSendRules() {
		return sendRules;
	}

	public void setSendRules(int sendRules) {
		this.sendRules = sendRules;
	}

	public int getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(int isRefund) {
		this.isRefund = isRefund;
	}

	public String getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(String goodsCount) {
		this.goodsCount = goodsCount;
	}

	public String getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

	public String getMerchantname() {
		return merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Long getOrderLoseAbsDate() {
		return orderLoseAbsDate;
	}

	public void setOrderLoseAbsDate(Long orderLoseAbsDate) {
		this.orderLoseAbsDate = orderLoseAbsDate;
	}

	public Date getOrderLoseDate() {
		return orderLoseDate;
	}

	public void setOrderLoseDate(Date orderLoseDate) {
		this.orderLoseDate = orderLoseDate;
	}

	public String getLogo1() {
		return logo1;
	}

	public void setLogo1(String logo1) {
		this.logo1 = logo1;
	}

	public String getLogo2() {
		return logo2;
	}

	public void setLogo2(String logo2) {
		this.logo2 = logo2;
	}

	public String getLogo3() {
		return logo3;
	}

	public void setLogo3(String logo3) {
		this.logo3 = logo3;
	}

	public Goods() {
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public double getSourcePrice() {
		return sourcePrice;
	}

	public void setSourcePrice(double sourcePrice) {
		this.sourcePrice = sourcePrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(double payPrice) {
		this.payPrice = payPrice;
	}

	public double getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(double offerPrice) {
		this.offerPrice = offerPrice;
	}

	public double getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(double rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public double getDividePrice() {
		return dividePrice;
	}

	public void setDividePrice(double dividePrice) {
		this.dividePrice = dividePrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getMaxcount() {
		return maxcount;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getIsavaliable() {
		return isavaliable;
	}

	public void setIsavaliable(int isavaliable) {
		this.isavaliable = isavaliable;
	}

	public Goods(Long goodsId, String goodsname, double sourcePrice,
			double currentPrice, double payPrice, double offerPrice,
			double rebatePrice, double dividePrice, double discount,
			int maxcount, Date endTime, String city, Date startTime,
			int isavaliable) {
		this.goodsId = goodsId;
		this.goodsname = goodsname;
		this.sourcePrice = sourcePrice;
		this.currentPrice = currentPrice;
		this.payPrice = payPrice;
		this.offerPrice = offerPrice;
		this.rebatePrice = rebatePrice;
		this.dividePrice = dividePrice;
		this.discount = discount;
		this.maxcount = maxcount;
		this.endTime = endTime;
		this.city = city;
		this.startTime = startTime;
		this.isavaliable = isavaliable;
	}

	public String getQpsharepic() {
		return qpsharepic;
	}

	public void setQpsharepic(String qpsharepic) {
		this.qpsharepic = qpsharepic;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	/**
	 * 补充属性：商品的虚拟数量 ,beiker_goods表的virtual_count字段
	 * 
	 * 以下为其 setter 和 getter 方法
	 */
	public int getVirtualCount() {
		return virtualCount;
	}

	public void setVirtualCount(int virtualCount) {
		this.virtualCount = virtualCount;
	}
	public String getCouponcash() {
		return couponcash;
	}
	public void setCouponcash(String couponcash) {
		this.couponcash = couponcash;
	}
	public String getKindlywarnings() {
		return kindlywarnings;
	}
	public void setKindlywarnings(String kindlywarnings) {
		this.kindlywarnings = kindlywarnings;
	}
	public String getIsCard() {
		return isCard;
	}
	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}

}
