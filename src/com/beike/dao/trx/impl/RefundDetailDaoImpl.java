package com.beike.dao.trx.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.RefundDetail;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.RefundDetailDao;
import com.beike.util.EnumUtil;

/**
 * @Title: RefundDetailDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: 退款明细DaoImpl
 * @date May 24, 2011 9:09:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("refundDetailDao")
public class RefundDetailDaoImpl extends GenericDaoImpl<RefundDetail, Long>
		implements RefundDetailDao {

	public Long addRefundDetail(final RefundDetail refundDetail) {

		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (refundDetail == null) {
			throw new IllegalArgumentException("refundDetail not null");

		} else {

			final String istSql = "insert into beiker_refund_detail(rudrecord_id,payment_id,refund_batch_id,handle_date,"
					+ "act_refund_status,payment_type,handle_type,pro_refund_status,payment_amount,amount,pro_external_id,"
					+ "pro_refund_request_id)"
					+ " value(?,?,?,?,?,?,?,?,?,?,?,?)";

			this.getJdbcTemplate().update(new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con
							.prepareStatement(istSql,
									new String[] { "rudrecord_id",
											"payment_id", "refund_batch_id",
											"handle_date", "act_refund_status",
											"payment_type", "handle_type",
											"pro_refund_status",
											"payment_amount", "amount",
											"pro_external_id",
											"pro_refund_request_id" });

					ps.setLong(1, refundDetail.getRudRecordId());
					ps.setLong(2, refundDetail.getPaymentId());
					ps.setString(3, refundDetail.getRefundBatchId());

					ps.setTimestamp(4, new Timestamp(refundDetail
							.getHandleDate().getTime()));

					ps.setString(5, EnumUtil.transEnumToString(refundDetail
							.getActRefundStatus()));
					ps.setString(6, EnumUtil.transEnumToString(refundDetail
							.getPaymentType()));
					ps.setString(7, EnumUtil.transEnumToString(refundDetail
							.getHandleType()));
					ps.setString(8, EnumUtil.transEnumToString(refundDetail
							.getProRefundStatus()));
					ps.setDouble(9, refundDetail.getPaymentAmount());
					ps.setDouble(10, refundDetail.getAmount());
					ps.setString(11, refundDetail.getProExternalId());
					ps.setString(12, refundDetail.getProRefundrequestId());

					return ps;
				}

			}, keyHolder);

		}

		Long refundDetailId = keyHolder.getKey().longValue();
		return refundDetailId;

	}

	public RefundDetail findById(Long id) {

		if (id == null) {

			throw new IllegalArgumentException("id not null");
		}

		String qrySql = "select ruddetail_id,version,rudrecord_id,payment_id,handle_date,amount,payment_amount,refund_batch_id,payment_type,act_refund_status,handle_type,pro_refund_status"
				+ ",pro_external_id,pro_refund_request_id from beiker_refund_detail where ruddetail_id=?";

		List<RefundDetail> refundDetailList = getSimpleJdbcTemplate().query(
				qrySql, new RowMapperImpl(), id);

		if (refundDetailList.size() > 0) {
			return refundDetailList.get(0);
		}

		return null;
	}

	public List<RefundDetail> findByPaymentId(Long paymentId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * 根据refundRecId和pamentType\actRefundStatus查出所有已退款成功的金额
	 */
	public double findSucRudAmtByTrxId(Long trxId, PaymentType paymentType,
			RefundStatus actRefundStatus) {

		if (trxId == null || paymentType == null || actRefundStatus == null) {
			throw new IllegalArgumentException(
					"trxId and pamentType and actRefundStatus not null");
		}
		StringBuffer sql = new StringBuffer();
		sql
				.append("select sum(de.amount) as sumSucAmount from beiker_refund_detail as de left join beiker_refund_record as re on");
		sql.append(" de.rudrecord_id=re.rudrecord_id where");
		sql.append(" de.payment_type='" + paymentType.name() + "'");
		sql
				.append(" and de.act_refund_status='" + actRefundStatus.name()
						+ "'");
		sql.append(" and re.trxorder_id=?");

		System.out.println(sql);

		// double sunSucAmount = (Double) getJdbcTemplate().queryForObject(
		// sql.toString(), Double.class);
		// return sunSucAmount;

		Map<String, Object> resultMap = getSimpleJdbcTemplate().queryForMap(
				sql.toString(), trxId);

		BigDecimal sumSucAmount = (BigDecimal) resultMap.get("sumSucAmount");
		if (sumSucAmount != null) {
			return sumSucAmount.doubleValue();
		}

		return 0;

	}

	// 根据recordId和pamentType\actRefundStatus查出所有已退款成功的金额
	public double findSucRudAmtByRecId(Long recId, PaymentType paymentType,
			RefundStatus actRefundStatus) {
		if (recId == null || paymentType == null || actRefundStatus == null) {
			throw new IllegalArgumentException(
					"trxId and pamentType and actRefundStatus not null");
		}

		String sql = "select sum(amount)  as sumSucAmount from beiker_refund_detail where  payment_type=?  and act_refund_status=? and rudrecord_id=?";
		Map<String, Object> resultMap = getSimpleJdbcTemplate().queryForMap(
				sql, paymentType.name(), actRefundStatus.name(), recId);

		BigDecimal sunSucAmount = (BigDecimal) resultMap.get("sumSucAmount");
		return sunSucAmount.doubleValue();
	}

	public List<RefundDetail> findByProExternalId(String proExternalId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RefundDetail> findByRefundRecId(Long refundRecId) {
		if (refundRecId == null) {
			throw new IllegalArgumentException("refundRecId not null");

		}
		StringBuffer sb = new StringBuffer();

		sb
				.append("select ruddetail_id,version,rudrecord_id,payment_id,handle_date,amount,payment_amount,refund_batch_id,payment_type,act_refund_status,handle_type,pro_refund_status,");
		sb
				.append("pro_external_id,pro_refund_request_id from beiker_refund_detail where rudrecord_id=?");

		List<RefundDetail> RefundDetailList = this.getSimpleJdbcTemplate()
				.query(sb.toString(), new RowMapperImpl(), refundRecId);

		if (RefundDetailList.size() > 0 && RefundDetailList != null) {
			return RefundDetailList;

		}
		return null;
	}

	public List<RefundDetail> findByRefundRequestId(Long refundRequestId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(RefundDetail refundDetail) {

	}

	/**
	 * 更新支付机构退款状态及操作时间和退款请求号.加入状态前置条件判断//TODO  后期优化，防止支付宝响应时间过长问题，导致乐观锁
	 * @throws StaleObjectStateException 
	 */
	public void updateByIdAndProStatus(Long id, RefundStatus proRefundStatus, Date handleDate, String proRefundRedId,Long version,RefundStatus preProRefundStatus) throws StaleObjectStateException {

		if (id == null || proRefundStatus == null) {
			throw new IllegalArgumentException("id and proRefundStatus not null");
		}
		
		String uptSql = "update beiker_refund_detail set pro_refund_status=?,version=?,handle_date=?, pro_refund_request_id=? where ruddetail_id=? and version=? and pro_refund_status=? ";
		int result = getSimpleJdbcTemplate().update(uptSql, proRefundStatus.name(),version+1L,handleDate, proRefundRedId, id,version,preProRefundStatus.name());
		
		if (result == 0){
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	/**
	 * 更新账户退款状态
	 * @throws StaleObjectStateException 
	 */
	public void updateByIdAndActStatus(Long id, RefundStatus actRefundStatus,Long version) throws StaleObjectStateException {

		if (id == null || actRefundStatus == null) {
			throw new IllegalArgumentException(
					"id and actRefundStatus not null");
		}
		String uptSql = "update beiker_rerund_detail set act_refund_status=?,version=? where id=? and version=?";
		int result = getSimpleJdbcTemplate().update(uptSql, actRefundStatus,version+1L, id,version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}

	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<RefundDetail> {

		public RefundDetail mapRow(ResultSet rs, int num) throws SQLException {

			RefundDetail refundDetail = new RefundDetail();
			refundDetail.setId(rs.getLong("ruddetail_id"));
			refundDetail.setVersion(rs.getLong("version"));
			refundDetail.setRudRecordId(rs.getLong("rudrecord_id"));
			refundDetail.setPaymentId(rs.getLong("payment_id"));
			refundDetail.setRefundBatchId(rs.getString("refund_batch_id"));
			refundDetail.setHandleDate(rs.getTimestamp(("handle_date")));
			refundDetail.setActRefundStatus(EnumUtil.transStringToEnum(
					RefundStatus.class, rs.getString("act_refund_status")));
			refundDetail.setPaymentType(EnumUtil.transStringToEnum(
					PaymentType.class, rs.getString("payment_type")));
			refundDetail.setHandleType(EnumUtil.transStringToEnum(
					RefundHandleType.class, rs.getString("handle_type")));
			refundDetail.setProRefundStatus(EnumUtil.transStringToEnum(
					RefundStatus.class, rs.getString("pro_refund_status")));
			refundDetail.setPaymentAmount(rs.getDouble("payment_amount"));
			refundDetail.setAmount(rs.getDouble("amount"));
			refundDetail.setProExternalId((rs.getString("pro_external_id")));
			refundDetail.setProRefundrequestId(rs
					.getString("pro_refund_request_id"));

			return refundDetail;

		}
	}

}
