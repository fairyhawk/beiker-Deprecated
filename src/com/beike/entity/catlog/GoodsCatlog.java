package com.beike.entity.catlog;

import java.util.Date;

/**
 * <p>
 * Title:
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

public class GoodsCatlog extends AbstractCatlog {

	private static final long serialVersionUID = 1L;

	private Long goodsId;

	private Double price;

	private Date createdate;

	private Long cityid;
	
	
	private boolean isNewVersion;
	
	

	public boolean isNewVersion() {
		return isNewVersion;
	}

	public void setNewVersion(boolean isNewVersion) {
		this.isNewVersion = isNewVersion;
	}

	public Long getCityid() {
		return cityid;
	}

	public void setCityid(Long cityid) {
		this.cityid = cityid;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public GoodsCatlog() {

	}

	public GoodsCatlog(Long goodsId) {
		this.goodsId = goodsId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityid == null) ? 0 : cityid.hashCode());
		result = prime * result + ((createdate == null) ? 0 : createdate.hashCode());
		result = prime * result + ((goodsId == null) ? 0 : goodsId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((orderbydate == null) ? 0 : orderbydate.hashCode());
		result = prime * result + ((orderbydefault == null) ? 0 : orderbydefault.hashCode());
		result = prime * result + ((orderbyprice == null) ? 0 : orderbyprice.hashCode());
		result = prime * result + ((orderbysort == null) ? 0 : orderbysort.hashCode());
		result = prime * result +((orderbydiscount== null) ? 0 : orderbydiscount.hashCode());
		result = prime * result +((orderbyrating== null) ? 0 : orderbyrating.hashCode());
		//现金券排序
		result = prime * result +((cashSelected== null) ? 0 : cashSelected.hashCode());
		//商品代金券排序
		result = prime * result +((tokenSelected) ? 0 : tokenSelected.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((rangeprice == null) ? 0 : rangeprice.hashCode());
		result = prime * result + ((regionName == null) ? 0 : regionName.hashCode());
		result = prime * result + ((regionextid == null) ? 0 : regionextid.hashCode());
		result = prime * result + ((regionid == null) ? 0 : regionid.hashCode());
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
		result = prime * result + ((tagextid == null) ? 0 : tagextid.hashCode());
		result = prime * result + ((tagid == null) ? 0 : tagid.hashCode());
		//今日新品
		result = prime * result +((isNew== null) ? 0 : isNew.hashCode());
		
		result = prime * result +((featuretagid== null) ? 0 : featuretagid.hashCode());
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
		GoodsCatlog other = (GoodsCatlog) obj;
		if (cityid == null) {
			if (other.cityid != null)
				return false;
		} else if (!cityid.equals(other.cityid))
			return false;
		if (createdate == null) {
			if (other.createdate != null)
				return false;
		} else if (!createdate.equals(other.createdate))
			return false;
		if (goodsId == null) {
			if (other.goodsId != null)
				return false;
		} else if (!goodsId.equals(other.goodsId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (orderbydate == null) {
			if (other.orderbydate != null)
				return false;
		} else if (!orderbydate.equals(other.orderbydate))
			return false;
		if (orderbydefault == null) {
			if (other.orderbydefault != null)
				return false;
		} else if (!orderbydefault.equals(other.orderbydefault))
			return false;
		if (orderbyprice == null) {
			if (other.orderbyprice != null)
				return false;
		} else if (!orderbyprice.equals(other.orderbyprice))
			return false;
		if (orderbysort == null) {
			if (other.orderbysort != null)
				return false;
		} else if (!orderbysort.equals(other.orderbysort))
			return false;
		
		if (cashSelected == null) {
			if (other.cashSelected != null)
				return false;
		} else if (!cashSelected.equals(other.cashSelected))
			return false;
		if (!tokenSelected) {
			if (other.tokenSelected)
				return false;
		} else if (!tokenSelected.equals(other.tokenSelected))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (rangeprice == null) {
			if (other.rangeprice != null)
				return false;
		} else if (!rangeprice.equals(other.rangeprice))
			return false;
		if (regionName == null) {
			if (other.regionName != null)
				return false;
		} else if (!regionName.equals(other.regionName))
			return false;
		if (regionextid == null) {
			if (other.regionextid != null)
				return false;
		} else if (!regionextid.equals(other.regionextid))
			return false;
		if (regionid == null) {
			if (other.regionid != null)
				return false;
		} else if (!regionid.equals(other.regionid))
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		if (tagName == null) {
			if (other.tagName != null)
				return false;
		} else if (!tagName.equals(other.tagName))
			return false;
		if (tagextid == null) {
			if (other.tagextid != null)
				return false;
		} else if (!tagextid.equals(other.tagextid))
			return false;
		if (tagid == null) {
			if (other.tagid != null)
				return false;
		} else if (!tagid.equals(other.tagid))
			return false;
		
		if (isNew == null) {
			if (other.isNew != null)
				return false;
		} else if (!isNew.equals(other.isNew))
			return false;
		return true;
	}

	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
