package com.beike.model.lucene;

import java.io.Serializable;

public class APPRegion implements Serializable {


	@Override
	public String toString() {
		return "APPRegion [id=" + id + ", region_name=" + region_name
				+ ", parentid=" + parentid + ", boost=" + boost + ", count="
				+ count + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -9186566924406016755L;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRegion_name() {
		return region_name;
	}
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}
	public int getParentid() {
		return parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	public int getBoost() {
		return boost;
	}
	public void setBoost(int boost) {
		this.boost = boost;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * 分类ID
	 */
	public int id;
	/**
	 * 商圈名称
	 */
	public String region_name;
	/**
	 * 分类所属的父级ID
	 */
	public int parentid;
	/**
	 * 排序号
	 */
	public int boost;
	/**
	 * 数量
	 */
	public int count;
}
