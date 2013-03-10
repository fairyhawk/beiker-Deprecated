package com.beike.entity.catlog;

import java.io.Serializable;

import com.beike.util.StringUtils;

/**
 * <p>
 * Title:抽象类别分类
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
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
public class AbstractCatlog implements Serializable {

	private static final long serialVersionUID = 1L;

	// 现金券选择
	protected Boolean cashSelected;
	
	//商品代金券选择
	protected Boolean tokenSelected = false;
	
	//是否新品
	protected Boolean isNew;
	
	protected Long id;

	protected Long regionid;

	protected String regionName;// 一级区域名称

	protected Long regionextid;

	protected Long tagid;

	protected String tagName;// 一级属性名称

	protected Long tagextid;
	protected Long featuretagid;


	public Long getFeaturetagid() {
		return featuretagid;
	}

	public void setFeaturetagid(Long featuretagid) {
		this.featuretagid = featuretagid;
	}

	protected Double sort;

	protected String orderbydate;

	protected String orderbysort;

	protected String orderbyprice;
	protected String orderbyrating;

	public String getOrderbyrating() {
		return orderbyrating;
	}

	public void setOrderbyrating(String orderbyrating) {
		this.orderbyrating = orderbyrating;
	}

	public String getOrderbydiscount() {
		return orderbydiscount;
	}

	public void setOrderbydiscount(String orderbydiscount) {
		this.orderbydiscount = orderbydiscount;
	}

	protected String orderbydiscount;
	protected String orderbydefault;
	protected Double rangeprice;

	public boolean isNull() {
		boolean flag = false;
		if ((regionid == null || "".equals(regionid))
				&& (regionextid == null || "".equals(regionextid))
				&& (tagid == null || "".equals(tagid))
				&& (tagextid == null || "".equals(tagextid))
				&& (rangeprice == null || "".equals(rangeprice))) {
			flag = true;
		}
		return flag;
	}

	public boolean isOrderByNull() {
		boolean flag = false;
		if ((orderbydate == null || "".equals(orderbydate))
				&& (orderbysort == null || "".equals(orderbysort))
				&& (orderbyprice == null || "".equals(orderbyprice))
				&& (orderbydefault == null || "".equals(orderbydefault))
				&& (orderbyrating == null || "".equals(orderbyrating))
						&& (orderbydiscount == null || ""
								.equals(orderbydiscount))) {
			flag = true;
		}
		return flag;
	}

	public String getOrderbyprice() {
		return orderbyprice;
	}

	public void setOrderbyprice(String orderbyprice) {
		this.orderbyprice = orderbyprice;
	}

	public Double getRangeprice() {
		return rangeprice;
	}

	public void setRangeprice(Double rangeprice) {
		this.rangeprice = rangeprice;
	}

	public String getOrderbysort() {
		return orderbysort;
	}

	public void setOrderbysort(String orderbysort) {
		this.orderbysort = orderbysort;
	}

	public String getSearchCourse() {
		StringBuilder sb = new StringBuilder();
		if (regionid != null && !"".equals(regionid)) {
			sb.append("regionid=");
			sb.append(regionid);
			sb.append(" and ");
		}
		if (regionextid != null && !"".equals(regionextid)) {
			sb.append("regionextid=");
			sb.append(regionextid);
			sb.append(" and ");
		}
		if (tagid != null && !"".equals(tagid)) {
			sb.append("tagid=");
			sb.append(tagid);
			sb.append(" and ");
		}
		if (tagextid != null && !"".equals(tagextid)) {
			sb.append("tagextid=");
			sb.append(tagextid);
			sb.append(" and ");
		}
		if (rangeprice != null && !"".equals(rangeprice)) {
			if (rangeprice < 1000) {
				sb.append("currentprice<");
				sb.append(rangeprice);
			} else {
				sb.append("currentprice>=500");
			}
		}

		if (sb.toString().endsWith("and ")) {
			sb = new StringBuilder(sb.subSequence(0, sb.lastIndexOf("and"))
					.toString());
		}
		// 判断group by gooid
		if (this instanceof GoodsCatlog) {
			sb.append(" group by goodid ");
		} else if (this instanceof MerchantCatlog) {
			sb.append(" group by brandid ");
		} else if (this instanceof CouponCatlog) {
			sb.append(" group by couponid ");
		}

		if (orderbydate != null && !"".equals(orderbydate)) {
			sb.append(" order by createdate ");
			sb.append(orderbydate);
		} else if (orderbysort != null && !"".equals(orderbysort)) {
			sb.append(" order by sort ");
			sb.append(orderbysort);
		} else if (orderbyprice != null && !"".equals(orderbyprice)) {
			sb.append(" order by currentprice ");
			sb.append(orderbyprice);
		}
		return sb.toString();
	}

	// 默认排序条件特殊处理：排除商超卡
	public String getSearchDefaultGoodCourse() {
		StringBuilder sb = new StringBuilder("bg.iscard='0' and ");
		if (regionid != null && !"".equals(regionid)) {
			sb.append("bcg.regionid=");
			sb.append(regionid);
			sb.append(" and ");
		}
		if (regionextid != null && !"".equals(regionextid)) {
			sb.append("bcg.regionextid=");
			sb.append(regionextid);
			sb.append(" and ");
		}
		if (tagid != null && !"".equals(tagid)) {
			sb.append("bcg.tagid=");
			sb.append(tagid);
			sb.append(" and ");
		}
		if (tagextid != null && !"".equals(tagextid)) {
			sb.append("bcg.tagextid=");
			sb.append(tagextid);
			sb.append(" and ");
		}
		
		// 现金券判断 新增商品代金券判断 add by xuxiaoxian 20130130
		if(cashSelected && tokenSelected){
			sb.append(" (bg.couponcash = '1' or bg.couponcash = '2') and ");
		}else if(cashSelected){
			sb.append(" bg.couponcash='1' and ");
		}else if(tokenSelected){
			sb.append(" bg.couponcash='2' and ");
		}
			
		if (rangeprice != null && !"".equals(rangeprice)) {
			if (rangeprice == 50) {
				sb.append("bcg.currentprice<=50");
			} else if (rangeprice == 100) {
				sb.append("bcg.currentprice>50 and bcg.currentprice<=100 ");
			} else if (rangeprice == 300) {
				sb.append("bcg.currentprice>100 and bcg.currentprice<=300 ");
			} else if (rangeprice == 500) {
				sb.append("bcg.currentprice>300 and bcg.currentprice<=500 ");
			} else {
				sb.append("bcg.currentprice>500 ");
			}
		}
		
		if (sb.toString().endsWith("and ")) {
			sb = new StringBuilder(sb.subSequence(0, sb.lastIndexOf("and"))
					.toString());
		}
		// 判断group by gooid
//		sb.append(" group by bcg.goodid ");
		return sb.toString();
	}

	private boolean orderIsValid(String order) {
		if (order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")) {
			return true;
		}
		return false;
	}

	/**
	 * 特色标签Where生成
	 * janwen
	 * @return
	 *
	 */
	public String getFeatureSearchGoodCourse() {
		StringBuilder sb = new StringBuilder("bg.iscard='0' and ");
		if (regionid != null && !"".equals(regionid)) {
			sb.append("bcg.regionid=");
			sb.append(regionid);
			sb.append(" and ");
		}
		if (regionextid != null && !"".equals(regionextid)) {
			sb.append("bcg.regionextid=");
			sb.append(regionextid);
			sb.append(" and ");
		}
		if (tagid != null && !"".equals(tagid)) {
			sb.append("bcg.tagid=");
			sb.append(tagid);
			sb.append(" and ");
		}
		if (tagextid != null && !"".equals(tagextid)) {
			sb.append("bcg.tagextid=");
			sb.append(tagextid);
			sb.append(" and ");
		}
		// 现金券判断 新增代金券判断 add by xuxiaoxian 20130130
		if (cashSelected && tokenSelected) {
			sb.append(" (bg.couponcash = '1' or bg.couponcash = '2') and ");		
		}else if(cashSelected){
			sb.append(" bg.couponcash = '1' and ");		
		}else if(tokenSelected){
			sb.append(" bg.couponcash = '2' and ");		
		}
		
		if (rangeprice != null && !"".equals(rangeprice)) {
			if (rangeprice == 50) {
				sb.append("bcg.currentprice<=50");
			} else if (rangeprice == 100) {
				sb.append("bcg.currentprice>50 and bcg.currentprice<=100 ");
			} else if (rangeprice == 300) {
				sb.append("bcg.currentprice>100 and bcg.currentprice<=300 ");
			} else if (rangeprice == 500) {
				sb.append("bcg.currentprice>300 and bcg.currentprice<=500 ");
			} else {
				sb.append("bcg.currentprice>500 ");
			}
		}
		if(featuretagid != null){
			if(!sb.toString().trim().endsWith("and")){
				sb.append(" and bbg.biaoqianid=").append(featuretagid)
				.append(" and bbg.isavailable='y'");
			}else{
				sb.append(" bbg.biaoqianid=").append(featuretagid)
				.append(" and bbg.isavailable='y'");
			}
		}

		if (sb.toString().endsWith("and ")) {
			sb = new StringBuilder(sb.subSequence(0, sb.lastIndexOf("and"))
					.toString());
		}
		// 判断group by gooid
		if (this instanceof GoodsCatlog) {
			sb.append(" group by bcg.goodid ");
		} else if (this instanceof MerchantCatlog) {
			sb.append(" group by brandid ");
		} else if (this instanceof CouponCatlog) {
			sb.append(" group by couponid ");
		}

		if (StringUtils.validNull(orderbydate) && orderIsValid(orderbydate)) {
			sb.append(" order by bget.on_time ");
			sb.append(orderbydate);
		} else if (StringUtils.validNull(orderbysort)
				&& orderIsValid(orderbysort)) {
			sb.append(" order by temp.saled ");
			sb.append(orderbysort);
		} else if (StringUtils.validNull(orderbyprice)
				&& orderIsValid(orderbyprice)) {
			sb.append(" order by bcg.currentprice ");
			sb.append(orderbyprice);
		} else if (StringUtils.validNull(orderbyrating)
				&& orderIsValid(orderbyrating)) {
			sb.append(" order by bcg.rating ").append(orderbyrating)
					.append(",bgprofile.sales_count desc");
		} else if (StringUtils.validNull(orderbydiscount)
				&& orderIsValid(orderbydiscount)) {
			sb.append(" order by bcg.discountrate").append(orderbydiscount)
					.append(",bgprofile.sales_count desc");
		} else if (StringUtils.validNull(orderbydefault)) {
			sb.append(" order by temp.top desc,bget.on_time desc");
		} else {
			// 默认排序
			sb.append(" order by temp.top desc,bget.on_time desc");
		}
		return sb.toString();
	}

	/**
	 * 生成where条件：searchCatlog用
	 * @return
	 */
	public String getSearchGoodCourse() {
		StringBuilder sb = new StringBuilder("bg.iscard='0' and ");
		if (regionid != null && !"".equals(regionid)) {
			sb.append("bcg.regionid=");
			sb.append(regionid);
			sb.append(" and ");
		}
		if (regionextid != null && !"".equals(regionextid)) {
			sb.append("bcg.regionextid=");
			sb.append(regionextid);
			sb.append(" and ");
		}
		if (tagid != null && !"".equals(tagid)) {
			sb.append("bcg.tagid=");
			sb.append(tagid);
			sb.append(" and ");
		}
		if (tagextid != null && !"".equals(tagextid)) {
			sb.append("bcg.tagextid=");
			sb.append(tagextid);
			sb.append(" and ");
		}
		// 现金券判断 新增代金券判断 add by xuxiaoxian 20130130
		if (cashSelected && tokenSelected) {
			sb.append(" (bg.couponcash = '1' or bg.couponcash = '2') and ");		
		}else if(cashSelected){
			sb.append(" bg.couponcash = '1' and ");		
		}else if(tokenSelected){
			sb.append(" bg.couponcash = '2' and ");		
		}
		if (rangeprice != null && !"".equals(rangeprice)) {
			if (rangeprice == 50) {
				sb.append("bcg.currentprice<=50");
			} else if (rangeprice == 100) {
				sb.append("bcg.currentprice>50 and bcg.currentprice<=100 ");
			} else if (rangeprice == 300) {
				sb.append("bcg.currentprice>100 and bcg.currentprice<=300 ");
			} else if (rangeprice == 500) {
				sb.append("bcg.currentprice>300 and bcg.currentprice<=500 ");
			} else {
				sb.append("bcg.currentprice>500 ");
			}
		}
		
		if (sb.toString().endsWith("and ")) {
			sb = new StringBuilder(sb.subSequence(0, sb.lastIndexOf("and"))
					.toString());
		}
		// 判断group by gooid
		if (this instanceof GoodsCatlog) {
			sb.append(" group by bcg.goodid ");
		} else if (this instanceof MerchantCatlog) {
			sb.append(" group by brandid ");
		} else if (this instanceof CouponCatlog) {
			sb.append(" group by couponid ");
		}

		if (StringUtils.validNull(orderbydate) && orderIsValid(orderbydate)) {
			sb.append(" order by bget.on_time ");
			sb.append(orderbydate);
		} else if (StringUtils.validNull(orderbysort)
				&& orderIsValid(orderbysort)) {
			sb.append(" order by temp.saled ");
			sb.append(orderbysort);
		} else if (StringUtils.validNull(orderbyprice)
				&& orderIsValid(orderbyprice)) {
			sb.append(" order by bcg.currentprice ");
			sb.append(orderbyprice);
		} else if (StringUtils.validNull(orderbyrating)
				&& orderIsValid(orderbyrating)) {
			sb.append(" order by bcg.rating ").append(orderbyrating)
					.append(",bgprofile.sales_count desc");
		} else if (StringUtils.validNull(orderbydiscount)
				&& orderIsValid(orderbydiscount)) {
			sb.append(" order by bcg.discountrate ").append(orderbydiscount)
					.append(",bgprofile.sales_count desc");
		} else if (StringUtils.validNull(orderbydefault)) {
			sb.append(" order by temp.top desc,bget.on_time desc");
		} else {
			// 默认排序
			sb.append(" order by temp.top desc,bget.on_time desc");
		}
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public AbstractCatlog() {

	}

	public String getOrderbydate() {
		return orderbydate;
	}

	public void setOrderbydate(String orderbydate) {
		this.orderbydate = orderbydate;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Long getRegionid() {
		return regionid;
	}

	public void setRegionid(Long regionid) {
		this.regionid = regionid;
	}

	public Long getRegionextid() {
		return regionextid;
	}

	public void setRegionextid(Long regionextid) {
		this.regionextid = regionextid;
	}

	public Long getTagid() {
		return tagid;
	}

	public void setTagid(Long tagid) {
		this.tagid = tagid;
	}

	public Long getTagextid() {
		return tagextid;
	}

	public void setTagextid(Long tagextid) {
		this.tagextid = tagextid;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getOrderbydefault() {
		return orderbydefault;
	}

	public void setOrderbydefault(String orderbydefault) {
		this.orderbydefault = orderbydefault;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Boolean getCashSelected() {
		return cashSelected;
	}

	public void setCashSelected(Boolean cashSelected) {
		this.cashSelected = cashSelected;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Boolean getTokenSelected() {
		return tokenSelected;
	}

	public void setTokenSelected(Boolean tokenSelected) {
		this.tokenSelected = tokenSelected;
	}


}
