/**
 * 
 */
package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * @author janwen
 * 
 * 
 * 用户参与信息
 */
public class LotteryInfo {

	private int lottery_id;
	private Long lotteryId;
	private int user_id;
	private int prize_id;
	private String winnumber;
	private int iswinner;
	private String trx_id;
	private Timestamp createtime;
	
	private Long participantscount;

	public Long getParticipantscount() {
		return participantscount;
	}

	public void setParticipantscount(Long participantscount) {
		this.participantscount = participantscount;
	}

	public int getLottery_id() {
		return lottery_id;
	}

	public void setLottery_id(int lottery_id) {
		this.lottery_id = lottery_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getPrize_id() {
		return prize_id;
	}

	public void setPrize_id(int prize_id) {
		this.prize_id = prize_id;
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

	public String getTrx_id() {
		return trx_id;
	}

	public void setTrx_id(String trx_id) {
		this.trx_id = trx_id;
	}

	public Timestamp getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	public Long getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(Long lotteryId) {
		this.lotteryId = lotteryId;
	}

}
