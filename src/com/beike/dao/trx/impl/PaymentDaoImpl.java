package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.Payment;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProCheckStatus;
import com.beike.common.enums.trx.ProPayStatus;
import com.beike.common.enums.trx.ProRefundStatus;
import com.beike.common.enums.trx.ProSettleStatus;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.PaymentDao;
import com.beike.util.EnumUtil;

/**
 * @Title: PaymentDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: 支付信息DAO实现类
 * @date May 16, 2011 4:41:37 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("paymentDao")
public class PaymentDaoImpl extends GenericDaoImpl<Payment, Long> implements
		PaymentDao {

	public void addPayment(Payment payment) {
		if (payment == null) {

			throw new IllegalArgumentException("payment not null");
		}
		String istSql = "insert into beiker_payment(create_date,trx_amount,provider_type,pay_channel,"
				+ "pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,"
				+ "pay_confirm_date,pay_pound_scale,extend_info,description,trxorder_id,account_id,"
				+ "pro_external_id,coupon_id,trx_status,payment_sn,payment_type"
				+ ") value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		getSimpleJdbcTemplate().update(istSql, payment.getCreateDate(),
				payment.getTrxAmount(),
				EnumUtil.transEnumToString(payment.getProviderType()),
				payment.getPayChannel(), payment.getPayRequestId(),
				EnumUtil.transEnumToString(payment.getProPayStatus()),
				EnumUtil.transEnumToString(payment.getProRefundStatus()),
				EnumUtil.transEnumToString(payment.getProCheckStatus()),
				EnumUtil.transEnumToString(payment.getProSettleStatus()),
				payment.getPayConfirmDate(), payment.getPayPoundScale(),
				payment.getExtendInfo(), payment.getDescription(),
				payment.getTrxorderId(), payment.getAccountId(),
				payment.getProExternalId(),
				payment.getCouponId(),
				EnumUtil.transEnumToString(payment.getTrxStatus()),
				payment.getPaymentSn(),
				EnumUtil.transEnumToString(payment.getPaymentType()));

	}

	public Payment findById(Long id) {

		if (id == null) {
			throw new IllegalArgumentException("id not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where id=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), id);
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;
	}

	public Payment findByPayReqId(String payRequestId) {

		if (payRequestId == null) {
			throw new IllegalArgumentException("payRequestId not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where pay_request_id=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), payRequestId);
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;
	}

	public Payment findByPayReqIdAndType(String payRequestId,
			ProPayStatus proPayStatus, PaymentType paymentType) {

		if (payRequestId == null) {
			throw new IllegalArgumentException("payRequestId not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where pay_request_id=? and pro_pay_status=? and payment_type=? ";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), payRequestId, proPayStatus.name(),
					paymentType.name());
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;
	}

	public Payment findBySn(String sn) {

		if (sn == null) {
			throw new IllegalArgumentException("paymentSn not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where payment_sn=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), sn);
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;
	}

	public Payment findByTrxIdAndType(Long trxId, PaymentType paymentType,
			TrxStatus trxStatus) {

		if (trxId == null || paymentType == null) {
			throw new IllegalArgumentException(
					"trxId and  paymentType not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where trxorder_id=? and payment_type=? and trx_status=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), trxId, paymentType.name(),
					trxStatus.name());
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;

	}

	public Payment findByTrxIdAndType(Long trxId, PaymentType paymentType) {

		if (trxId == null || paymentType == null) {
			throw new IllegalArgumentException(
					"trxId and  paymentType not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,"
					+ "pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where trxorder_id=? and payment_type=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), trxId, paymentType.name());
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;

	}

	public List<Payment> findByTrxIdAndTypeAndStatus(Long trxId,
			TrxStatus trxStatus, String paymentType) {

		if (trxId == null || paymentType == null || paymentType.length() == 0
				|| trxStatus == null) {
			throw new IllegalArgumentException(
					"trxId and  paymentType  and trxStatus not null");
		}
		StringBuilder qrySb = new StringBuilder();
		qrySb
				.append("select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,");
		qrySb
				.append("pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where trxorder_id=? and trx_status=? and payment_type in (");
		qrySb.append(paymentType);
		qrySb.append(")");

		List<Payment> paymentList = getSimpleJdbcTemplate().query(
				qrySb.toString(), new RowMapperImpl(), trxId,
				EnumUtil.transEnumToString(trxStatus));
		if (paymentList != null && paymentList.size() > 0) {
			return paymentList;
		}

		return null;

	}

	public Payment findByTrxIdAndStatus(Long trxId, PaymentType paymentType) {

		if (trxId == null || paymentType == null) {
			throw new IllegalArgumentException(
					"trxId and  paymentType not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,"
					+ "pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where trxorder_id=? and payment_type=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), trxId, paymentType.name());
			if (paymentList.size() > 0) {
				return paymentList.get(0);
			}
		}

		return null;

	}

	public List<Payment> findByTrxId(Long trxId) {

		if (trxId == null) {
			throw new IllegalArgumentException("trxId is  not null");
		} else {
			String qrySql = "select id,version,trxorder_id,account_id,create_date,pay_confirm_date,trx_amount,pay_pound_scale,provider_type,pay_channel,pay_request_id,pro_pay_status,pro_refund_status,"
					+ "pro_check_status,pro_settle_status,extend_info,description,pro_external_id,coupon_id,trx_status,payment_sn,payment_type from beiker_payment where trxorder_id=?";
			List<Payment> paymentList = getSimpleJdbcTemplate().query(qrySql,
					new RowMapperImpl(), trxId);
			if (paymentList.size() > 0) {
				return paymentList;
			}
		}

		return null;

	}

	public void updatePayStatusByReqId(String payReqId,
			ProPayStatus proPaySatus, Long version)
			throws StaleObjectStateException {

		if (payReqId == null || proPaySatus == null) {
			throw new IllegalArgumentException(
					"payReqId and paoPaySatus  not null");
		} else {
			String uptSql = "update  beiker_payment set pro_pay_status=?,version=? where pay_request_id=? and version=?";
			int result = getSimpleJdbcTemplate().update(uptSql,
					proPaySatus.name(), version + 1L, payReqId, version);
			if (result == 0) {

				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}
	}

	public void updatePayStatusByReqId(String payReqId,
			ProPayStatus proPayStatus, Date payConfirmDate, Long version)
			throws StaleObjectStateException {

		if (payReqId == null || proPayStatus == null || payConfirmDate == null) {
			throw new IllegalArgumentException(
					"payReqId and proPayStatus  and payConfirmDate not null");
		} else {
			String uptSql = "update  beiker_payment set pro_pay_status=?,version=?,pay_confirm_date=? where pay_request_id=? and version=?";
			int result = getSimpleJdbcTemplate().update(uptSql,
					proPayStatus.name(), version + 1L, payConfirmDate,
					payReqId, version);

			if (result == 0) {

				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}

	}

	public void updatePayStatusBySn(String sn, ProPayStatus proPayStatus,
			Date payConfirmDate, Long version) throws StaleObjectStateException {

		if (sn == null || proPayStatus == null || payConfirmDate == null) {
			throw new IllegalArgumentException(
					"sn and proPayStatus  and payConfirmDate not null");
		} else {
			String uptSql = "update  beiker_payment set pro_pay_status=?,version=?,pay_confirm_date=? where payment_sn=? and version=?";
			int result = getSimpleJdbcTemplate().update(uptSql,
					proPayStatus.name(), version + 1L, payConfirmDate, sn,
					version);
			if (result == 0) {

				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}

	}

	public void updatePayment(Payment payment) throws StaleObjectStateException {
		if (payment == null) {
			throw new IllegalArgumentException("payment not null");
		}

		String uptSql = "update  beiker_payment set create_date=?,version=?,trx_amount=?,provider_type=?,pay_channel=?,"
				+ "pay_request_id=?,pro_pay_status=?,pro_refund_status=?,pro_check_status=?,pro_settle_status=?,"
				+ "pay_confirm_date=?,pay_pound_scale=?,extend_info=?,description=?,trxorder_id=?,account_id=?,"
				+ "pro_external_id=?,coupon_id=?,trx_status=?,payment_sn=?,payment_type=? where id=? and version=?";

		int result = getSimpleJdbcTemplate().update(uptSql,
				payment.getCreateDate(), payment.getVersion() + 1L,
				payment.getTrxAmount(),
				EnumUtil.transEnumToString(payment.getProviderType()),
				payment.getPayChannel(), payment.getPayRequestId(),
				EnumUtil.transEnumToString(payment.getProPayStatus()),
				EnumUtil.transEnumToString(payment.getProRefundStatus()),
				EnumUtil.transEnumToString(payment.getProCheckStatus()),
				EnumUtil.transEnumToString(payment.getProSettleStatus()),
				payment.getPayConfirmDate(), payment.getPayPoundScale(),
				payment.getExtendInfo(), payment.getDescription(),
				payment.getTrxorderId(), payment.getAccountId(),
				payment.getProExternalId(),
				payment.getCouponId(),
				EnumUtil.transEnumToString(payment.getTrxStatus()),
				payment.getPaymentSn(),
				EnumUtil.transEnumToString(payment.getPaymentType()),
				payment.getId(), payment.getVersion());

		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}

	}

	public void updateTrxSatusBySn(String sn, TrxStatus trxStatus, Long version)
			throws StaleObjectStateException {

		if (sn == null || trxStatus == null) {
			throw new IllegalArgumentException("sn and trxStatus  not null");
		} else {
			String uptSql = "update  beiker_payment set trx_status=?,version=version+1 where payment_sn=? and version=?";
			int result = getSimpleJdbcTemplate().update(uptSql,
					trxStatus.name(), sn, version);

			if (result == 0) {

				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}
		}

	}

	protected class RowMapperImpl implements ParameterizedRowMapper<Payment> {
		public Payment mapRow(ResultSet rs, int num) throws SQLException {
			Payment payment = new Payment();
			payment.setAccountId(rs.getLong("account_id"));
			payment.setVersion(rs.getLong("version"));
			payment.setCreateDate(rs.getTimestamp("create_date"));
			payment.setDescription(rs.getString("description"));
			payment.setExtendInfo(rs.getString("extend_info"));
			payment.setId(rs.getLong("id"));
			payment.setPayChannel(rs.getString("pay_channel"));
			payment.setPayConfirmDate(rs.getTimestamp("pay_confirm_date"));
			payment.setPaymentSn(rs.getString("payment_sn"));
			payment.setPaymentType(EnumUtil.transStringToEnum(PaymentType.class, rs.getString("payment_type")));
			payment.setPayPoundScale(rs.getDouble("pay_pound_scale"));
			payment.setPayRequestId(rs.getString("pay_request_id"));
			payment.setProCheckStatus(EnumUtil.transStringToEnum(ProCheckStatus.class, rs.getString("pro_check_status")));
			payment.setProRefundStatus(EnumUtil.transStringToEnum(ProRefundStatus.class, rs.getString("pro_refund_status")));
			payment.setProPayStatus(EnumUtil.transStringToEnum(ProPayStatus.class, rs.getString("pro_pay_status")));
			payment.setProExternalId(rs.getString("pro_external_id"));
			payment.setProSettleStatus(EnumUtil.transStringToEnum(ProSettleStatus.class, rs.getString("pro_settle_status")));
			payment.setProviderType(EnumUtil.transStringToEnum(ProviderType.class, rs.getString("provider_type")));
			payment.setTrxorderId(rs.getLong("trxorder_id"));
			payment.setTrxAmount(rs.getDouble("trx_amount"));
			payment.setTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class, rs.getString("trx_status")));
			payment.setCouponId(rs.getLong("coupon_id"));
			return payment;
		}
	}

}
