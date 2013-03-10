package com.beike.form;

import java.util.HashSet;
import java.util.Set;

 /**
 * com.beike.form.CommendGoodsForm.java
 * @description:推荐商品form（用于保存商品排序中的各种临时变量）
 * @Author:xuxiaoxian
 * Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-9-14
 */
public class CommendGoodsForm implements Comparable<CommendGoodsForm>{
	private Long goodsId;                              //商品Id	
	private double sortWeight;					  //排序权重值
	private int orderCount;							  //订单量
	private int addTimes = 0;						  //二级商圈累加次数（为减少判断初始化为1）
	private Set<Long> regionId = new HashSet<Long>();         //一级商圈Id
	private Set<Long> regionExtId = new HashSet<Long>();	//二级商圈Id
	
	//按权重降序，权重值相同的按订单量降序
	@Override
	public int compareTo(CommendGoodsForm o) {
		if(sortWeight < o.getSortWeight()){
			return 1;
		}else if( sortWeight == o.getSortWeight()){
			if(orderCount < o.getOrderCount()){
				return 1;
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}
	
	
	public CommendGoodsForm(){}
	public CommendGoodsForm(Long goodsId,double sortWeight){
		this.goodsId = goodsId;
		this.sortWeight = sortWeight;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public double getSortWeight() {
		return sortWeight;
	}


	public void setSortWeight(double sortWeight) {
		this.sortWeight = sortWeight;
	}


	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getAddTimes() {
		return addTimes;
	}
	public void setAddTimes(int addTimes) {
		this.addTimes = addTimes;
	}

	public Set<Long> getRegionId() {
		return regionId;
	}

	public void setRegionId(Set<Long> regionId) {
		this.regionId = regionId;
	}

	public Set<Long> getRegionExtId() {
		return regionExtId;
	}

	public void setRegionExtId(Set<Long> regionExtId) {
		this.regionExtId = regionExtId;
	}
	@Override
	public String toString() {
		return "goodid:"+this.getGoodsId()+" sortweight:"+this.getSortWeight()+" orderCount:"+this.getOrderCount();
	}
}
