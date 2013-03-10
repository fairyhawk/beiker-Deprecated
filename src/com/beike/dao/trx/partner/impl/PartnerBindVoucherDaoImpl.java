package com.beike.dao.trx.partner.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.partner.PartnerBindVoucherDao;
import com.beike.entity.partner.PartnerBindVoucher;
import com.beike.entity.partner.PartnerReqId;

/**   
 * @title: PartnerBindVoucherDaoImpl.java
 * @package com.beike.dao.trx.partner.impl
 * @description: 
 * @author wangweijie  
 * @date 2012-9-6 下午07:49:50
 * @version v1.0   
 */
@Repository("partnerBindVoucherDao")
public class PartnerBindVoucherDaoImpl extends GenericDaoImpl<PartnerReqId, Long> implements PartnerBindVoucherDao{

	/**
	 * 添加优惠券
	 */
	@Override
	public void addPartnerVoucher(PartnerBindVoucher pbv) {
		if (null == pbv) {
			throw new IllegalArgumentException("partnerReqId  not null");
		}
		
		String insertSql = "insert into beiker_partner_bind_voucher (trxorder_id,trx_goods_id,voucher_id,partner_no,out_request_id,trx_goods_sn,voucher_code,out_coupon_id,out_coupon_pwd,create_date,modify_date) value (?,?,?,?,?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(insertSql, pbv.getTrxOrderId(),pbv.getTrxGoodsId(),pbv.getVoucherId(),pbv.getPartnerNo(),pbv.getOutRequestId(),
			pbv.getTrxGoodsSn(),pbv.getVoucherCode(),pbv.getOutCouponId(),pbv.getOutCouponPwd(),new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()));
	}

	@Override
	public Map<String, Object> queryVoucherBothSides(String partnerNo,String outRequestId) {
		String querySql = "select trx_goods_sn as trxGoodsSn,out_coupon_id as outCouponId from beiker_partner_bind_voucher where partner_no=? and out_request_id =? ";
		return getSimpleJdbcTemplate().queryForMap(querySql, partnerNo,outRequestId);
	}

	@Override
	public PartnerBindVoucher queryPartnerBindVoucher(String partherNo,Long voucherId) {
		String querySql = "select id,trxorder_id,trx_goods_id,voucher_id,partner_no,out_request_id,trx_goods_sn,voucher_code,out_coupon_id,out_coupon_pwd,create_date,modify_date from beiker_partner_bind_voucher where partner_no = ? and voucher_id = ?";
		List<PartnerBindVoucher> qbvList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), partherNo,voucherId);
		if(null != qbvList && qbvList.size() > 0){
			return qbvList.get(0);
		}
		return null;
	}
	
	
	
	@Override
	public List<PartnerBindVoucher> queryPartnerBindVoucherList( String partherNo, String outRequestId) {
		String querySql = "select id,trxorder_id,trx_goods_id,voucher_id,partner_no,out_request_id,trx_goods_sn,voucher_code,out_coupon_id,out_coupon_pwd,create_date,modify_date from beiker_partner_bind_voucher where partner_no = ? and out_request_id = ?";
		List<PartnerBindVoucher> qbvList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), partherNo,outRequestId);
		return qbvList;
	}



	protected class RowMapperImpl implements ParameterizedRowMapper<PartnerBindVoucher> {
		public PartnerBindVoucher mapRow(ResultSet rs, int num) throws SQLException {
			PartnerBindVoucher pbv = new PartnerBindVoucher();
			pbv.setId(rs.getLong("id"));
			pbv.setTrxGoodsId(rs.getLong("trx_goods_id"));
			pbv.setVoucherId(rs.getLong("voucher_id"));
			pbv.setPartnerNo(rs.getString("partner_no"));
			pbv.setOutRequestId(rs.getString("out_request_id"));
			pbv.setTrxGoodsSn(rs.getString("trx_goods_sn"));
			pbv.setVoucherCode(rs.getString("voucher_code"));
			pbv.setOutCouponId(rs.getString("out_coupon_id"));
			pbv.setOutCouponPwd(rs.getString("out_coupon_pwd"));
			pbv.setCreateDate(new Date(rs.getTimestamp("create_date").getTime()));
			pbv.setModifyDate(new Date(rs.getTimestamp("modify_date").getTime()));
			return pbv;
		}
	}
}
