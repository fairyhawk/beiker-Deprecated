package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MCouponDetailDao;
import com.beike.wap.entity.MCoupon;

@Repository("mCouponDetailDao")
public class MCouponDetailDaoImpl extends GenericDaoImpl<MCoupon, Long>  implements MCouponDetailDao{

	/** 日志记录 */
	private static Log log = LogFactory.getLog(MCouponDetailDaoImpl.class);
	
	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MCoupon findById(long couponId) {
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT id, couponname, enddate, downcount, couponlogo, ")
			.append("createdate, merchantid, coupondetaillogo, couponnumber, ")
			.append("couponrules, browsecounts, smstemplate, coupon_title ")
			.append("FROM beiker_coupon WHERE id = ?");
		MCoupon coupon = null;
		log.info("select coupon by id = " + couponId );
		try {
			List<MCoupon> couponList = getSimpleJdbcTemplate().query(sql.toString(),
					new RowMapperImpl(), couponId);
			if (couponList != null && couponList.size() != 0) {
				coupon = couponList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coupon;
	}

	@Override
	public List<MCoupon> findCouponByBrandId(long brandId) {
		String sql = "SELECT id,couponname,enddate,downcount,couponlogo,createdate,merchantid,coupondetaillogo,couponnumber,couponrules,browsecounts,smstemplate,coupon_title FROM beiker_coupon WHERE merchantid = ? ";
		log.info("select coupon by brand id = " + brandId );
		List<MCoupon> couponList = getSimpleJdbcTemplate().query(sql.toString(),
				new RowMapperImpl(), brandId);
		if (couponList == null || couponList.size() == 0) {
			return null;
		}
		return couponList;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<MCoupon> {
		public MCoupon mapRow(ResultSet rs, int rowNum) throws SQLException {
			MCoupon mc = new MCoupon();
			mc.setId(rs.getLong("id"));
			mc.setCouponName(rs.getString("couponname"));
			mc.setEndDate(rs.getDate("enddate"));
			mc.setDownCount(rs.getLong("downcount"));
			mc.setCouponLogo(rs.getString("couponlogo"));
			mc.setCreateDate(rs.getDate("createdate"));
			mc.setMerchantid(rs.getLong("merchantid"));
			mc.setCouponDetailLogo(rs.getString("coupondetaillogo"));
			mc.setCouponNumber(rs.getString("couponnumber"));
			mc.setCouponRules(rs.getString("couponrules"));
			mc.setBrowseCounts(rs.getLong("browsecounts"));
			mc.setSmstemplate(rs.getString("smstemplate"));
			mc.setCoupon_title(rs.getString("coupon_title"));
			
			return mc;
		}
	}

	@Override
	public List<MCoupon> findCouponByIds(String couponIds) {
		StringBuilder sqlSb = new StringBuilder("");
		sqlSb.append("select * FROM beiker_coupon where id in (").append(couponIds).append(") ")
				.append("order by FIND_IN_SET (id, '").append(couponIds).append("')");
		log.info(sqlSb);
		List<MCoupon> couponList = getSimpleJdbcTemplate().query(sqlSb.toString(),
				new RowMapperImpl());
		if (couponList == null || couponList.size() == 0) {
			return null;
		}
		return couponList;
	}
}
