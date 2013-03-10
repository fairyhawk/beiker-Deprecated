package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * @author zx.liu
 */

public class LotteryUser
{
	private Long lotteryId=0L;
	private Long userId=0L;
	
	private Long prizeId=0L;
	// 兑奖号码
	private String winNumber="";	
	private int iswinner=0;
	private String trxId;
	private Timestamp createTime;
	
	// 临时保存用户的 Email
	private String	email;

	public Long getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(Long lotteryId) {
		this.lotteryId = lotteryId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}

	public String getWinNumber() {
		return winNumber;
	}

	public void setWinNumber(String winNumber) {
		this.winNumber = winNumber;
	}

	public int getIswinner() {
		return iswinner;
	}

	public void setIswinner(int iswinner) {
		this.iswinner = iswinner;
	}

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
		
}

