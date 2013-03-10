/**
 * 
 */
package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * @author janwen
 * 
 * 
 * 奖品相关信息
 */
public class LotteryInfoNew {
	// 奖品信息
	private int newprize_id;
	private String newprize_name;
	// 整个抽奖开始时间/第一次抽奖开始时间
	private Timestamp newprize_starttime;
	private String newprize_pagetitle;
	private String newprize_pic;
	
	private int startprize_id;

	// 获奖名额
	private int winners;
	// 总参与人数
	private int total;
	// 种子出现时间/开奖时间
	private Timestamp startprize_seedtime;
	
	private String strartprize_status;

	public String getStrartprize_status() {
		return strartprize_status;
	}

	public void setStrartprize_status(String strartprize_status) {
		this.strartprize_status = strartprize_status;
	}

	public int getNewprize_id() {
		return newprize_id;
	}

	public void setNewprize_id(int newprize_id) {
		this.newprize_id = newprize_id;
	}

	public String getNewprize_name() {
		return newprize_name;
	}

	public void setNewprize_name(String newprize_name) {
		this.newprize_name = newprize_name;
	}

	public Timestamp getNewprize_starttime() {
		return newprize_starttime;
	}

	public void setNewprize_starttime(Timestamp newprize_starttime) {
		this.newprize_starttime = newprize_starttime;
	}

	public String getNewprize_pagetitle() {
		return newprize_pagetitle;
	}

	public void setNewprize_pagetitle(String newprize_pagetitle) {
		this.newprize_pagetitle = newprize_pagetitle;
	}

	public String getNewprize_pic() {
		return newprize_pic;
	}

	public void setNewprize_pic(String newprize_pic) {
		this.newprize_pic = newprize_pic;
	}

	public int getWinners() {
		return winners;
	}

	public void setWinners(int winners) {
		this.winners = winners;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Timestamp getStartprize_seedtime() {
		return startprize_seedtime;
	}

	public void setStartprize_seedtime(Timestamp startprize_seedtime) {
		this.startprize_seedtime = startprize_seedtime;
	}

	public int getStartprize_id() {
		return startprize_id;
	}

	public void setStartprize_id(int startprize_id) {
		this.startprize_id = startprize_id;
	}

	

}
