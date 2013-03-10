package com.beike.service.mobile;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 搜索参数
 * @author janwen
 * Mar 28, 2012
 */
public class SearchParam implements Serializable {
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).append("keyword",keyword).append("type",type)
		.append("start",start).append("requestno",requestno).append("areaid",areaid).append("regionid",regionid).append("regionextid",regionextid).append("tagid",tagid)
		.append("tagextid",tagextid).append("lat",lat).append("lng",lng).append("st",st).append("distance",distance).toString();
	}

/**
    * 搜索关键词最长为8个中文字,app端校验长度
    */
	private String keyword;
	
	private int type;
	
	private Long areaid;
	
	private Long goodsid;
	private Long regionid;
	
	private Long regionextid;
	

	private Long tagextid;
	
	private double lat;
	
	private double lng;
	
	private double distance = 45.0;
	
	private Long brandid;
	private String st;
	
	private int start = 0;
	
	private int requestno = 10;
	static final Log logger  = LogFactory.getLog(SearchParam.class);
	public int getRequestno() {
		return requestno;
	}

	public void setRequestno(int requestno) {
		this.requestno = requestno;
	}

	/**
	 * v1 存在多个一级商圈,所以用string
	 */
	private String tagid;

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getKeyword() {
		
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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


	public Long getTagextid() {
		return tagextid;
	}

	public void setTagextid(Long tagextid) {
		this.tagextid = tagextid;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}



	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}


	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public Long getBrandid() {
		return brandid;
	}

	public void setBrandid(Long brandid) {
		this.brandid = brandid;
	}

	public Long getAreaid() {
		return areaid;
	}

	public void setAreaid(Long areaid) {
		this.areaid = areaid;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	
	
	
}
