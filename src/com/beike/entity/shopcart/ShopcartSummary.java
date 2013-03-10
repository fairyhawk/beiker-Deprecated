package com.beike.entity.shopcart;

public class ShopcartSummary {

	private int totalProduct;
	private double totalMoney;
	private double totalReturn;
	
	public ShopcartSummary(){};
	public ShopcartSummary(int totalProduct, double totalMoney,
			double totalReturn) {
		super();
		this.totalProduct = totalProduct;
		this.totalMoney = totalMoney;
		this.totalReturn = totalReturn;
	}
	public int getTotalProduct() {
		return totalProduct;
	}
	public void setTotalProduct(int totalProduct) {
		this.totalProduct = totalProduct;
	}
	public double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	public double getTotalReturn() {
		return totalReturn;
	}
	public void setTotalReturn(double totalReturn) {
		this.totalReturn = totalReturn;
	}
}
