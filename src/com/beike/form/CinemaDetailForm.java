package com.beike.form;

import java.math.BigDecimal;

import com.beike.entity.film.CinemaInfo;

public class CinemaDetailForm {
	private CinemaInfo cinemaInfo; //影院详情
	private boolean supportOLBooking;//是否支持在线订座
	private boolean supportGroupon;//是否支持团购
	private BigDecimal lowestGrouponPrice; //影院中所有影片的团购最低价

	public CinemaInfo getCinemaInfo() {
		return cinemaInfo;
	}

	public void setCinemaInfo(CinemaInfo cinemaInfo) {
		this.cinemaInfo = cinemaInfo;
	}

	public boolean isSupportOLBooking() {
		return supportOLBooking;
	}

	public void setSupportOLBooking(boolean supportOLBooking) {
		this.supportOLBooking = supportOLBooking;
	}

	public boolean isSupportGroupon() {
		return supportGroupon;
	}

	public void setSupportGroupon(boolean supportGroupon) {
		this.supportGroupon = supportGroupon;
	}

	public BigDecimal getLowestGrouponPrice() {
		return lowestGrouponPrice;
	}

	public void setLowestGrouponPrice(BigDecimal lowestGrouponPrice) {
		this.lowestGrouponPrice = lowestGrouponPrice;
	}

	@Override
	public String toString() {
		return cinemaInfo.toString() + "supportOLBooking:" + supportOLBooking + " supportGroupon:" + supportGroupon;
	}

}
