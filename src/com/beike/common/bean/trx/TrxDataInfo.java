package com.beike.common.bean.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;

public class TrxDataInfo {
	/**
	 * 非个人超限trxorderGoods数据
	 */
	private final List<TrxorderGoods> unSingleOverRunTgList;

	/**
	 * 个人超限trxorderGoods数据
	 */
	private List<TrxorderGoods> singleOverRunTgList;

	/**
	 * 总量超限trxorderGoods数据
	 */
	private List<TrxorderGoods> totalOverRunTgList;

	private List<TrxorderGoods> allTgList;
	private String payRequestId;
	
	/**
	 * 订单信息，删除购物车用到
	 */
	private TrxOrder trxOrder;
	
	/**
	 * 商品简称
	 */
	private  Map<Long, String> goodsTitleMap;
	
	/**
	 * 销售总量Map
	 */
	private Map<Long, Integer> saleCountmap;
	
	public TrxOrder getTrxOrder() {
		return trxOrder;
	}

	public void setTrxOrder(TrxOrder trxOrder) {
		this.trxOrder = trxOrder;
	}

	public TrxDataInfo(List<TrxorderGoods> unSingleOverRunTgList,
			List<TrxorderGoods> singleOverRunTgList,TrxOrder trxOrder) {
		super();
		this.unSingleOverRunTgList = unSingleOverRunTgList;
		this.singleOverRunTgList = singleOverRunTgList;
		this.trxOrder = trxOrder;

	}
	public TrxDataInfo(List<TrxorderGoods> unSingleOverRunTgList,
			List<TrxorderGoods> singleOverRunTgList,
			List<TrxorderGoods> allTgList,Map<Long, Integer> saleCountmap) {
		super();
		this.unSingleOverRunTgList = unSingleOverRunTgList;
		this.singleOverRunTgList = singleOverRunTgList;
		this.allTgList = allTgList;
		this.saleCountmap=saleCountmap;

	}


	public List<TrxorderGoods> getUnSingleOverRunTgList() {
		return unSingleOverRunTgList;
	}

	public List<TrxorderGoods> getSingleOverRunTgList() {
		return singleOverRunTgList;
	}

	public void setSingleOverRunTgList(List<TrxorderGoods> singleOverRunTgList) {
		this.singleOverRunTgList = singleOverRunTgList;
	}

	public List<TrxorderGoods> getTotalOverRunTgList() {
		return totalOverRunTgList;
	}

	public void setTotalOverRunTgList(List<TrxorderGoods> totalOverRunTgList) {
		this.totalOverRunTgList = totalOverRunTgList;
	}

	public String getPayRequestId() {
		return payRequestId;
	}

	public void setPayRequestId(String payRequestId) {
		this.payRequestId = payRequestId;
	}

	public List<TrxorderGoods> getAllTgList() {
		return allTgList;
	}

	public void setAllTgList(List<TrxorderGoods> allTgList) {
		this.allTgList = allTgList;
	}

	public Map<Long, String> getGoodsTitleMap() {
		return goodsTitleMap;
	}

	public void setGoodsTitleMap(Map<Long, String> goodsTitleMap) {
		this.goodsTitleMap = goodsTitleMap;
	}

	public Map<Long, Integer> getSaleCountmap() {
		return saleCountmap;
	}

	public void setSaleCountmap(Map<Long, Integer> saleCountmap) {
		this.saleCountmap = saleCountmap;
	}
	
}
