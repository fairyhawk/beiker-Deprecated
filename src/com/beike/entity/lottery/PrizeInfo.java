package com.beike.entity.lottery;

import java.sql.Timestamp;

//用户参与信息
public class PrizeInfo {

	
	
	
	
	private Long goods_id;
	private int prize_id;
	private String goods_name;
	private String prize_name;
	private int winners;
	private Timestamp begintime;
	private Timestamp endtime;
	private String seeddescription;
	private Timestamp seedemergencetime;
	private String prizeseed;
	
	private int status;
	private Long participantscount;
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getPrize_name() {
		return prize_name;
	}
	public void setPrize_name(String prize_name) {
		this.prize_name = prize_name;
	}
	public int getWinners() {
		return winners;
	}
	public void setWinners(int winners) {
		this.winners = winners;
	}
	public Timestamp getBegintime() {
		return begintime;
	}
	public void setBegintime(Timestamp begintime) {
		this.begintime = begintime;
	}
	public Timestamp getEndtime() {
		return endtime;
	}
	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}
	public String getSeeddescription() {
		return seeddescription;
	}
	public void setSeeddescription(String seeddescription) {
		this.seeddescription = seeddescription;
	}
	public Timestamp getSeedemergencetime() {
		return seedemergencetime;
	}
	public void setSeedemergencetime(Timestamp seedemergencetime) {
		this.seedemergencetime = seedemergencetime;
	}
	public String getPrizeseed() {
		return prizeseed;
	}
	public void setPrizeseed(String prizeseed) {
		this.prizeseed = prizeseed;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getParticipantscount() {
		return participantscount;
	}
	public void setParticipantscount(Long participantscount) {
		this.participantscount = participantscount;
	}
	public int getPrize_id() {
		return prize_id;
	}
	public void setPrize_id(int prize_id) {
		this.prize_id = prize_id;
	}
	public Long getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}
	
}
