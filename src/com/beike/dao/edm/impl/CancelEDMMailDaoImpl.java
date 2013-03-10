package com.beike.dao.edm.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.edm.CancelEDMMailDao;
import com.beike.entity.edm.CancelEdmEmail;

/**
 * 
 * @author 赵静龙 创建时间：2012-10-18
 */
@Repository("cancelEDMMailDao")
public class CancelEDMMailDaoImpl extends GenericDaoImpl<CancelEdmEmail, Long>
		implements CancelEDMMailDao {
	
	@Override
	public void addCancelEdmMail(Map<String, Object> edmCancelEmailInfo){
		String sql = "INSERT INTO beiker_cancel_edm_email(email, canceltime, type, reason) VALUES(?, now(), ?, ?)";
		getJdbcTemplate().update(sql,
				new Object[] { (String) edmCancelEmailInfo.get("email"),
						(Integer) edmCancelEmailInfo.get("type"),
						(String) edmCancelEmailInfo.get("cancelReason")});
	}
	
}
