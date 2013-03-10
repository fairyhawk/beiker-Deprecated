package com.beike.common.bean.trx;

/**
 * @Title: AmountParam.java
 * @Package com.beike.common.bean.trx
 * @Description: 金额分布值参数
 * @date May 29, 2011 8:41:20 PM
 * @author wh.cheng
 * @version v1.0
 */
public class AmountParam {

	private double actVcPayAmount = 0; // VC应该支付多少钱
	private double actCashPayAmount = 0;// Cash应该支付多少钱
	private double needPayAmount = 0; // 还需要支付多少钱

	private boolean isNeedFlag = false;

	public double getActVcPayAmount() {
		return actVcPayAmount;
	}

	public void setActVcPayAmount(double actVcPayAmount) {
		this.actVcPayAmount = actVcPayAmount;
	}

	public double getActCashPayAmount() {
		return actCashPayAmount;
	}

	public void setActCashPayAmount(double actCashPayAmount) {
		this.actCashPayAmount = actCashPayAmount;
	}

	public double getNeedPayAmount() {
		return needPayAmount;
	}

	public void setNeedPayAmount(double needPayAmount) {
		this.needPayAmount = needPayAmount;
	}

	public boolean isNeedFlag() {
		return isNeedFlag;
	}

	public void setNeedFlag(boolean isNeedFlag) {
		this.isNeedFlag = isNeedFlag;
	}

	
	
}
