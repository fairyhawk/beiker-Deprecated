package com.beike.wap.entity;

import java.io.Serializable;

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
public abstract class MAbstractCatlog implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long regionid;

	private String regionName;// 一级区域名称

	private Long regionextid;

	private Long tagid;

	private String tagName;// 一级属性名称

	private Long tagextid;

	private Double sort;

	private String orderbydate;

	private String orderbysort;

	private String orderbyprice;

	private Double rangeprice;

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
				&& (orderbyprice == null || "".equals(orderbyprice))) {
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
		if (this instanceof MGoodsCatlog) {
			sb.append(" group by goodid ");
		} else if (this instanceof MerchantCatlog) {
			sb.append(" group by brandid ");
		} else if (this instanceof MCouponCatlog) {
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

	public MAbstractCatlog() {

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

}
