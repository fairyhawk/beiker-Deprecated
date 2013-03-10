package com.beike.dao.edm;

import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.edm.CancelEdmEmail;

/**
 *
 * @author 赵静龙 创建时间：2012-10-18
 */
public interface CancelEDMMailDao extends GenericDao<CancelEdmEmail, Long>{
	/**
	 * 保存所取消订阅邮箱信息
	 * @param edmCancelEmailInfo
	 */
	public void addCancelEdmMail(Map<String,Object> edmCancelEmailInfo);
	
}
