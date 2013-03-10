package com.beike.model.lucene;

import java.io.Serializable;

public class APPTag implements Serializable{

	@Override
	public String toString() {
		return "APPTag [id=" + id + ", tag_name=" + tag_name + ", parentid="
				+ parentid + ", boost=" + boost + ", count=" + count + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3290406264171818412L;
	/**
	 * 分类ID
	 */
	public int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
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
	 * 分类名称
	 */
	public String tag_name;
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
