package com.beike.form;

public class CinemaAreaForm {

	public Long dist_id; //区域ID
	public String areanName; //区域名
	public int cinemaCount;//影院数量

	public Long getDist_id() {
		return dist_id;
	}

	public void setDist_id(Long dist_id) {
		this.dist_id = dist_id;
	}

	public String getAreanName() {
		return areanName;
	}

	public void setAreanName(String areanName) {
		this.areanName = areanName;
	}

	public int getCinemaCount() {
		return cinemaCount;
	}

	public void setCinemaCount(int cinemaCount) {
		this.cinemaCount = cinemaCount;
	}

}
