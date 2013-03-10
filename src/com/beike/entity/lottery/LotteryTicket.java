package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * 用户参与奖券信息
 * 
 * @author janwen
 * @time Dec 19, 2011 3:17:39 PM
 */
public class LotteryTicket {

	private int newlorry_id;
	private Long user_id;
	private String winnumber;
	private int iswinner;
	private Timestamp createtime;
	private String numbersource;
	private String getlorrystatus;
	private int newprize_id;
	private int strartprize_status;

	//奖品页面title
	private String newprize_pagetitle;
	
	
	
	public String getNewprize_pagetitle() {
		return newprize_pagetitle;
	}

	public void setNewprize_pagetitle(String newprize_pagetitle) {
		this.newprize_pagetitle = newprize_pagetitle;
	}

	public int getNewlorry_id() {
		return newlorry_id;
	}

	public void setNewlorry_id(int newlorry_id) {
		this.newlorry_id = newlorry_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getWinnumber() {
		return winnumber;
	}

	public void setWinnumber(String winnumber) {
		this.winnumber = winnumber;
	}

	public int getIswinner() {
		return iswinner;
	}

	public void setIswinner(int iswinner) {
		this.iswinner = iswinner;
	}

	public Timestamp getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	public String getNumbersource() {
		return numbersource;
	}

	public void setNumbersource(String numbersource) {
		this.numbersource = numbersource;
	}

	public String getGetlorrystatus() {
		return getlorrystatus;
	}

	public void setGetlorrystatus(String getlorrystatus) {
		this.getlorrystatus = getlorrystatus;
	}

	public int getNewprize_id() {
		return newprize_id;
	}

	public void setNewprize_id(int newprize_id) {
		this.newprize_id = newprize_id;
	}

	public int getStrartprize_status() {
		return strartprize_status;
	}

	public void setStrartprize_status(int strartprize_status) {
		this.strartprize_status = strartprize_status;
	}

}
