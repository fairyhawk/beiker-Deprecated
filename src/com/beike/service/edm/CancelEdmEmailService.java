package com.beike.service.edm;

import java.util.Map;


/**
 *
 * @author 赵静龙 创建时间：2012-10-18
 */
public interface CancelEdmEmailService{
	/**
	 * 保存所取消订阅邮箱信息
	 * @param edmCancelEmailInfo
	 */
	public void addCancelEdmMail(Map<String,Object> edmCancelEmailInfo);
}
