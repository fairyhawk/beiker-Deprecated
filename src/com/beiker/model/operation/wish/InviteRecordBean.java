package com.beiker.model.operation.wish;

import java.io.Serializable;

/**
 * beiker_inviterrecord实体类，其中包括beiker_userprofile、beiker_inviteprize、beiker_user一些对应信息，用于返回数据
 * 
 * @author kun.wang
 */
public class InviteRecordBean implements Serializable{
	/** id */
	private long id;
	/** 推荐人id */
	private int sourceId;
	/** 被推荐人id */
	private int targetId;
	/** 来源 */
	private String fromWeb;
	/** 微博id */
	private String weiboId;
	/** 注册时间 */
	private String registTime;
	/** 微博昵称 */
	private String nickName;
	/** 统计推荐人数时使用，表示当前用户推荐的人数 */
	private long total;
	
	/** beiker_userprofile表中对应name */
	private String profileName;
	/** beiker_userprofile表中对应value */
	private String profileValue;
	/** beiker_inviteprize表中对应awardno(奖号) */
	private String awardno;
	/** beiker_user表中mobile对象 */
	private String mobile;
	/** email */
	private String email;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public String getFromWeb() {
		return fromWeb;
	}
	public void setFromWeb(String fromWeb) {
		this.fromWeb = fromWeb;
	}
	public String getWeiboId() {
		return weiboId;
	}
	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}
	public String getRegistTime() {
		return registTime;
	}
	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nikeName) {
		this.nickName = nikeName;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileValue() {
		return profileValue;
	}
	public void setProfileValue(String profileValue) {
		this.profileValue = profileValue;
	}
	public String getAwardno() {
		return awardno;
	}
	public void setAwardno(String awardno) {
		this.awardno = awardno;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
