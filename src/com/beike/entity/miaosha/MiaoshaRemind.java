package com.beike.entity.miaosha;
/**      
 * project:beiker  
 * Title:秒杀通知
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 2, 2012 1:19:43 PM     
 * @version 1.0
 */
public class MiaoshaRemind {
	//用户ID
	private Long userid;
	//秒杀ID
	private Long miaoshaid;
	//手机号
	private String phone;
	//状态
	private int status;
	
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Long getMiaoshaid() {
		return miaoshaid;
	}
	public void setMiaoshaid(Long miaoshaid) {
		this.miaoshaid = miaoshaid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
