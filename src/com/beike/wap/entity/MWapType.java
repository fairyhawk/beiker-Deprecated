package com.beike.wap.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * <p>
 * Title:WAP端数据库保存信息
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
 * @date 2011-09-23
 * @author lvjx
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MWapType implements Serializable {

	private int id;
	private int typeId;
	private String typeUrl;
	private int typeType;
	private int typeFloor;
	private int typePage;
	private Date typeDate;
	private String typeArea;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getTypeUrl() {
		return typeUrl;
	}

	public void setTypeUrl(String typeUrl) {
		this.typeUrl = typeUrl;
	}

	public int getTypeType() {
		return typeType;
	}

	public void setTypeType(int typeType) {
		this.typeType = typeType;
	}

	public int getTypeFloor() {
		return typeFloor;
	}

	public void setTypeFloor(int typeFloor) {
		this.typeFloor = typeFloor;
	}

	public int getTypePage() {
		return typePage;
	}

	public void setTypePage(int typePage) {
		this.typePage = typePage;
	}

	public Date getTypeDate() {
		return typeDate;
	}

	public void setTypeDate(Date typeDate) {
		this.typeDate = typeDate;
	}

	public String getTypeArea() {
		return typeArea;
	}

	public void setTypeArea(String typeArea) {
		this.typeArea = typeArea;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((typeArea == null) ? 0 : typeArea.hashCode());
		result = prime * result
				+ ((typeDate == null) ? 0 : typeDate.hashCode());
		result = prime * result + typeFloor;
		result = prime * result + typeId;
		result = prime * result + typePage;
		result = prime * result + typeType;
		result = prime * result + ((typeUrl == null) ? 0 : typeUrl.hashCode());
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
		MWapType other = (MWapType) obj;
		if (id != other.id)
			return false;
		if (typeArea == null) {
			if (other.typeArea != null)
				return false;
		} else if (!typeArea.equals(other.typeArea))
			return false;
		if (typeDate == null) {
			if (other.typeDate != null)
				return false;
		} else if (!typeDate.equals(other.typeDate))
			return false;
		if (typeFloor != other.typeFloor)
			return false;
		if (typeId != other.typeId)
			return false;
		if (typePage != other.typePage)
			return false;
		if (typeType != other.typeType)
			return false;
		if (typeUrl == null) {
			if (other.typeUrl != null)
				return false;
		} else if (!typeUrl.equals(other.typeUrl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WapType [id=" + id + ", typeArea=" + typeArea + ", typeDate="
				+ typeDate + ", typeFloor=" + typeFloor + ", typeId=" + typeId
				+ ", typePage=" + typePage + ", typeType=" + typeType
				+ ", typeUrl=" + typeUrl + "]";
	}

}
