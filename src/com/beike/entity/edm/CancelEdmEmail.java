package com.beike.entity.edm;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 取消订阅EDM邮箱实体
 * @author 赵静龙 创建时间：2012-10-18
 * 
 */
@SuppressWarnings("serial")
public class CancelEdmEmail implements Serializable {
	/** 主键id **/
	private Long id;
	/** 取消邮箱 **/
	private String email;
	/** 取消时间 **/
	private Timestamp cancelTime;
	/** 退订类型 **/
	private int type;
	
	public Long getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public Timestamp getCancelTime() {
		return cancelTime;
	}
	public int getType() {
		return type;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setCancelTime(Timestamp cancelTime) {
		this.cancelTime = cancelTime;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CancelEdmEmail other = (CancelEdmEmail) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
