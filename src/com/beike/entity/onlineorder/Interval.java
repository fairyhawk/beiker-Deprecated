package com.beike.entity.onlineorder;

import java.io.Serializable;


public class Interval implements Comparable<Interval>,Serializable{

	
	@Override
	public String toString() {
		return "Interval [interval_amount=" + interval_amount
				+ ", less_amount=" + less_amount + "]";
	}

	private double interval_amount;
	
	public double getInterval_amount() {
		return interval_amount;
	}

	public void setInterval_amount(double interval_amount) {
		this.interval_amount = interval_amount;
	}

	private double less_amount;

	public double getLess_amount() {
		return less_amount;
	}

	public void setLess_amount(double less_amount) {
		this.less_amount = less_amount;
	}

	
	@Override
	public int compareTo(Interval o) {
		int r = (int) (getInterval_amount()- o.getInterval_amount());
		return r;
	}

}
