package com.beike.entity.log;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * <p>Title: 用户日志表</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */
@SuppressWarnings("serial")
public class UserLog implements Serializable{
	
	private int id;  //用户主键
	
	private int userid;//用户id
	
	private String operateaction;//操作action名字
	
	private Blob operatedata;//操作数据
	
	private Date operatedate;//操作日期

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getOperateaction() {
		return operateaction;
	}

	public void setOperateaction(String operateaction) {
		this.operateaction = operateaction;
	}


	public Date getOperatedate() {
		return operatedate;
	}

	public void setOperatedate(Date operatedate) {
		this.operatedate = operatedate;
	}
	
	
	public UserLog(){
		
	}
	
	public UserLog(int id, int userid, String operateaction,
			Blob operatedata, Date operatedate) {
		this.id = id;
		this.userid = userid;
		this.operateaction = operateaction;
		this.operatedata = operatedata;
		this.operatedate = operatedate;
	}

	public Blob getOperatedata() {
		return operatedata;
	}

	public void setOperatedata(Blob operatedata) {
		this.operatedata = operatedata;
	}
}
