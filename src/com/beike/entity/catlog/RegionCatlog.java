package com.beike.entity.catlog;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Title:地域、属性分类
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
 * @date May 24, 2011
 * @author ye.tian
 * @version 1.0
 */
public class RegionCatlog implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long catlogid; // 地域或者属性类别id

	private String catlogName;// 地域或者属性的名称

	private String url;// 跳转url 伪静态准备

	private List<RegionCatlog> childRegionCatlog;// 本类别下面的子类别

	private String count;// 此标签显示的数量

	private Long parentId;
	
	private String region_enname;
	
	private Long cityId;  // 城市ID
	
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public String getRegion_enname() {
		return region_enname;
	}

	public void setRegion_enname(String region_enname) {
		this.region_enname = region_enname;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		RegionCatlog regionCatlog = null;
		if (obj instanceof RegionCatlog) {
			regionCatlog = (RegionCatlog) obj;
		}
		if (regionCatlog.getCatlogid().equals(this.getCatlogid())) {
			return true;
		}

		return false;

	}

	@Override
	public int hashCode() {
		return this.getCatlogid().hashCode();

	}

	public Long getCatlogid() {
		return catlogid;
	}

	public void setCatlogid(Long catlogid) {
		this.catlogid = catlogid;
	}

	public String getCatlogName() {
		return catlogName;
	}

	public void setCatlogName(String catlogName) {
		this.catlogName = catlogName;
	}

	public List<RegionCatlog> getChildRegionCatlog() {
		return childRegionCatlog;
	}

	public void setChildRegionCatlog(List<RegionCatlog> childRegionCatlog) {
		this.childRegionCatlog = childRegionCatlog;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
