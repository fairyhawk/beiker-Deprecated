package com.beike.wap.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * <p>
 * Title:热门地标信息
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-10-10
 * @author lvjx
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MRegion implements Serializable {

	private int id;
	private int regionId;
	private String regionName;
	private String regionArea;
	private Date regionDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionArea() {
		return regionArea;
	}

	public void setRegionArea(String regionArea) {
		this.regionArea = regionArea;
	}

	public Date getRegionDate() {
		return regionDate;
	}

	public void setRegionDate(Date regionDate) {
		this.regionDate = regionDate;
	}

}
