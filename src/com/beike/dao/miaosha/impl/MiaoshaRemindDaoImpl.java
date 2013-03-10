package com.beike.dao.miaosha.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.miaosha.MiaoshaRemindDao;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.miaosha.MiaoshaRemind;

/**      
 * project:beiker  
 * Title:秒杀提醒DAO实现
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:39:27 PM     
 * @version 1.0
 */
@Repository("miaoshaRemindDao")
public class MiaoshaRemindDaoImpl extends GenericDaoImpl<MiaoshaRemind, Long> implements
		MiaoshaRemindDao {

	@Override
	public int addMiaoshaRemind(MiaoshaRemind msRemind) {
		String insSql = "insert into beiker_miaosha_remind(userid,miaoshaid,phone,addtime) value(?,?,?,now())";
		return this.getSimpleJdbcTemplate().update(insSql, msRemind.getUserid(), msRemind.getMiaoshaid(), msRemind.getPhone());
	}

	@Override
	public int checkRepeatRemind(Long miaoshaid, String phone) {
		String selSql = "select count(id) from beiker_miaosha_remind where miaoshaid=? and phone=?";
		return this.getSimpleJdbcTemplate().queryForInt(selSql, miaoshaid, phone);
	}

	@Override
	public MiaoshaRemind getMiaoshaRemind(Long userId, Long miaoshaId) {
		String selSql = "select userid,miaoshaid,phone from beiker_miaosha_remind where userid=? and miaoshaid=? order by addtime desc limit 1";
		MiaoshaRemind msRemind = null;
		try{
			msRemind = this.getSimpleJdbcTemplate().queryForObject(selSql, new RowMapperImpl(), userId, miaoshaId);
		}catch(Exception ex){
			msRemind = null;
		}
		return msRemind;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<MiaoshaRemind> {
		@Override
		public MiaoshaRemind mapRow(ResultSet rs, int rowNum) throws SQLException {
			MiaoshaRemind msRemind = new MiaoshaRemind();
			msRemind.setUserid(rs.getLong("userid"));
			msRemind.setMiaoshaid(rs.getLong("miaoshaid"));
			msRemind.setPhone(rs.getString("phone"));
			return msRemind;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getRemindPhoneByMiaoshId(Long miaoshaId) {
		StringBuilder bufSelSql = new StringBuilder();
		bufSelSql.append("select distinct phone ");
		bufSelSql.append("from beiker_miaosha_remind ");
		bufSelSql.append("where miaoshaid=? ");
		return this.getJdbcTemplate().queryForList(bufSelSql.toString(), new Object[]{miaoshaId},String.class);
	}

	@Override
	public int deleteMiaoshaRemindByMiaoshId(Long miaoshaId) {
		String delSql = "delete from beiker_miaosha_remind where miaoshaid=?";
		return this.getSimpleJdbcTemplate().update(delSql, miaoshaId);
	}
}
