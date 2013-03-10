package com.beike.dao.trx.partner.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.partner.PartnerReqIdDao;
import com.beike.entity.partner.PartnerReqId;

@Repository("partnerReqIdDao")
public class PartnerReqIdDaoImpl extends GenericDaoImpl<PartnerReqId, Long> implements PartnerReqIdDao{

	@Override
	public void addPartnerReqId(PartnerReqId partnerReqId) {
		if (partnerReqId == null) {
			throw new IllegalArgumentException("partnerReqId  not null");
		} else{
			String insertSql = "insert into beiker_partner_req_id" +
					" (partner_no,request_id,create_date) " +
					" value (?,?,?)";

			getSimpleJdbcTemplate().update(insertSql, 						//插入sql
					partnerReqId.getPartnerNo(),			//）
					partnerReqId.getRequestId(),	//订单类型
					partnerReqId.getCreateDate()	//交易装态
					);		
		}
		
	}

	@Override
	public PartnerReqId findByPNoAndReqId(String partnerNo, String requestId) {
		if (partnerNo == null || requestId == null) {
			throw new IllegalArgumentException("partnerNo or requestId not null");
		}
		
		String querySql = "select id,version,create_date,partner_no,request_id from  beiker_partner_req_id where partner_no=? and request_id=? ";
		
		List<PartnerReqId> partnerReqId = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), partnerNo,requestId);
		if(partnerReqId!=null&&partnerReqId.size()>0){
			return partnerReqId.get(0);
		}
		return null;
	}

	
	protected class RowMapperImpl implements ParameterizedRowMapper<PartnerReqId> {

		public PartnerReqId mapRow(ResultSet rs, int num) throws SQLException {

			PartnerReqId partnerReqId = new PartnerReqId();
			partnerReqId.setId(rs.getLong("id"));
			partnerReqId.setVersion(rs.getLong("version"));
			partnerReqId.setCreateDate(rs.getTimestamp("create_date"));
			partnerReqId.setRequestId(rs.getString("request_id"));
			partnerReqId.setPartnerNo(rs.getString("partner_no"));			
			return partnerReqId;
		}
	}
}
