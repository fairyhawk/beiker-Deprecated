package com.beike.dao.trx.partner.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.partner.PartnerDao;
import com.beike.entity.partner.Partner;

/** 
* @ClassName: PartnerDaoImpl 
* @Description: 分销商相关信息查询
* @author yurenli
* @date 2012-5-30 下午06:16:34 
* @version V1.0 
*/ 
@Repository("partnerDao")
public class PartnerDaoImpl extends GenericDaoImpl<Partner , Long> implements PartnerDao{

	/** (non-Javadoc)
	 * @see com.beike.dao.trx.partner.PartnerDao#findPartnerByPartnerNo(java.lang.String)
	 * 根据分销商号partnerNo查询分销商表
	 * @author zhaofeilong
	 */
	@Override
	public List<Partner> findAllByPartnerNo(String partnerNo) {
		if(partnerNo==null || partnerNo.length()==0){
			throw new IllegalArgumentException("partnerNo  is null!");
			
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner where partner_no = ?");
		List<Partner> partnerList = this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl(), partnerNo);
		return partnerList;
	}

	/**
	 * @author zhaofeilong
	 * 根据分销商号partnerNo和是否有效isAvailable查询分销商表
	 */
	@Override
	public List<Partner> findByPartnerNoAndAva(String partnerNo,
			Long isAvailable) {
		StringBuilder sql = new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner where partner_no = ? and is_available = ?");
		List<Partner> partnerList=this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl(), partnerNo,isAvailable);
		return partnerList;
	}

	/**
	 * 根据id查询分销商表
	 * @author zhaofeilong
	 */
	@Override
	public Partner findByid(Long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner where id = ?");
		List<Partner> partnerList= this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl(), id);
		if(partnerList!=null&& partnerList.size()>0){
			
			return partnerList.get(0);
		}
		return null;
	}
	

	/**
	 * 根据userId  and isAvailable查询partner
	 * @author zhaofeilong
	 */
	@Override
	public List<Partner> findByUserIdAndAva(Long userId,Long isAvailable){

		if(userId==null || userId==0 || isAvailable==null ){
			throw new IllegalArgumentException("userId  or isAvailable  is null!");
		}
		StringBuilder sql=new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner where user_id = ? and is_available=?");
		List<Partner> partnerList = this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl(), userId,isAvailable);
		return partnerList;
	}
	
	
	/**
	 * 根据userId  查询对应的 partner
	 * @author zhaofeilong
	 */
	@Override
	public Partner findByUserId(Long userId){

		if(userId==null || userId==0 ){
			throw new IllegalArgumentException("userId   is null!");
		}
		StringBuilder sql=new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner where user_id = ?");
		List<Partner> partnerList = this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl(), userId);
		if(partnerList!=null&&partnerList.size()>0){
			return partnerList.get(0);
		}
		return null;
	}
	/**
	 * 查出所有的分销商信息（现在的坏境,mem一次性放入的大小不能超过1M。故以后若分销商过多（超过1M），需用其它方案）
	 * @return
	 */
	@Override
	public List<Partner> findAll(){

		StringBuilder sql=new StringBuilder();
		sql.append("select id, partner_no, key_value, user_id, is_available, partner_name, trx_express, api_type, ip, create_date, update_date,sms_express,sessian_key, description,notice_key_value");
		sql.append(" from beiker_partner");
		List<Partner> partnerList = this.getSimpleJdbcTemplate().query(sql.toString(), new PartnerRowMapperImpl());
	
		return partnerList;
	}
	
	
	protected class PartnerRowMapperImpl implements ParameterizedRowMapper<Partner> {
		public Partner mapRow(ResultSet rs, int rowNum) throws SQLException {
			Partner partner = new Partner();
			partner.setId(rs.getLong("id"));
			partner.setPartnerNo(rs.getString("partner_no"));
			partner.setKeyValue(rs.getString("key_value"));
			partner.setUserId(rs.getLong("user_id"));
			partner.setIsAvailable(rs.getLong("is_available"));
			partner.setPartnerName(rs.getString("partner_name"));
			partner.setTrxExpress(rs.getString("trx_express"));
			partner.setApiType(rs.getString("api_type"));
			partner.setIp(rs.getString("ip"));
			partner.setCreateDate(rs.getDate("create_date"));
			partner.setUpdateDate(rs.getDate("update_date"));
			partner.setSessianKey(rs.getString("sessian_key"));
			partner.setDescription(rs.getString("description"));
			partner.setSmsExpress(rs.getString("sms_express"));
			partner.setNoticeKeyValue(rs.getString("notice_key_value"));
			return partner;
		}
	}
	
	

	
	

}
