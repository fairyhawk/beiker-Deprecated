package com.beiker.model.operation.wish;

import java.io.Serializable;

/**
 * beiker_inviteprize对应实例
 * 
 * @author kun.wang
 */
public class InvitePrizeBean implements Serializable{
	/** id */
	private long id;
	/** 用户id */
	private int userId;
	/** 奖号 */
	private String awardNo;
	/** beiker_inventrecord表id */
	private long inviterecord_id;
	
	private String fromweb;
	
	private String nickname;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getAwardNo() {
		return awardNo;
	}
	public void setAwardNo(String awardNo) {
		this.awardNo = awardNo;
	}
	public long getInviterecord_id() {
		return inviterecord_id;
	}
	public void setInviterecord_id(long inviterecord_id) {
		this.inviterecord_id = inviterecord_id;
	}
}
