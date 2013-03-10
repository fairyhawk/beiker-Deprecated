package com.beiker.model.operation.wish;

import java.sql.Timestamp;

public class WishUser {
	private Long userid;
	// 自己获得的奖号
	private String awardNo;
	// 注册时间
	private Timestamp registerTime;
	
	private String fromweb;



	public String getAwardNo() {
		return awardNo;
	}

	public void setAwardNo(String awardNo) {
		this.awardNo = awardNo;
	}

	public Timestamp getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}


	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getFromweb() {
		return fromweb;
	}

	public void setFromweb(String fromweb) {
		this.fromweb = fromweb;
	}
}
