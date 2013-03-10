package com.beike.model.lucene;

import java.io.Serializable;

public class AppSearchedGoods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8963486625369453253L;

	@Override
	public String toString() {
		return "AppSearchedGoods [goodsid=" + goodsid + ", branchid=" + branchid + "]";
	}

	private String goodsid;

	public String getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	private String branchid;
}
