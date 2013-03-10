package com.beike.dao.trx.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.RefundRecordDao;
import com.beike.util.EnumUtil;

/**
 * @Title: RefundRecordDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description:退款记录DaoImpl
 * @date May 24, 2011 4:41:21 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("refundRecordDao")
public class RefundRecordDaoImpl extends GenericDaoImpl<RefundRecord, Long>
		implements RefundRecordDao {

	// @Transactional(propagation = Propagation.REQUIRED)
	public Long addRefundRecord(final RefundRecord refundRecord) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (refundRecord == null) {
			throw new IllegalArgumentException("refundRecord not null");

		} else {

			final String istSql = "insert into beiker_refund_record(trxorder_id,user_id,trx_goods_id,operator,refund_status,handle_type,"
					+ "order_date,confirm_date,create_date,order_amount,trx_goods_amount,product_name,description,refund_source_type)"
					+ " value(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			this.getJdbcTemplate().update(new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(istSql,
							new String[] { "trxorder_id", "user_id",
									" trx_goods_id", "operator",
									"refund_status", "handle_type",
									"order_date", "confirm_date",
									"create_date", "order_amount",
									"trx_goods_amount", "product_name",
									"description,refund_source_type" });

					ps.setLong(1, refundRecord.getTrxOrderId());
					ps.setLong(2, refundRecord.getUserId());
					ps.setLong(3, refundRecord.getTrxGoodsId());

					ps.setString(4, refundRecord.getOperator());

					ps.setString(5, EnumUtil.transEnumToString(refundRecord
							.getRefundStatus()));
					ps.setString(6, EnumUtil.transEnumToString(refundRecord
							.getHandleType()));
					ps.setTimestamp(7, new Timestamp((refundRecord
							.getOrderDate().getTime())));

					ps.setTimestamp(8, new Timestamp(refundRecord
							.getConfirmDate().getTime()));
					ps.setTimestamp(9, new Timestamp((refundRecord
							.getCreateDate().getTime())));

					ps.setDouble(10, refundRecord.getOrderAmount());
					ps.setDouble(11, refundRecord.getTrxGoodsAmount());
					ps.setString(12, refundRecord.getProductName());
					ps.setString(13, refundRecord.getDescription());
					ps.setString(14, EnumUtil.transEnumToString(refundRecord.getRefundSourceType()));

					return ps;
				}

			}, keyHolder);

		}

		Long refundRecId = keyHolder.getKey().longValue();
		return refundRecId;
	}

	public RefundRecord findById(Long id) {
		if (id == null) {

			throw new IllegalArgumentException("id not null");
		}

		String qrySql = "select rudrecord_id,version,trxorder_id,user_id,trx_goods_id,operator,order_amount,trx_goods_amount,refund_status,handle_type,order_date,confirm_date,create_date,product_name,description,refund_source_type "
				+ "from beiker_refund_record where rudrecord_id=?";

		List<RefundRecord> refundRecordList = getSimpleJdbcTemplate().query(
				qrySql, new RowMapperImpl(), id);

		if (refundRecordList.size() > 0) {
			return refundRecordList.get(0);
		}
		return null;
	}

	public List<RefundRecord> findByOrdId(Long ordId) {
		if (ordId == null) {

			throw new IllegalArgumentException("ordId not null");
		}

		String qrySql = "select rudrecord_id,version,trxorder_id,user_id,trx_goods_id,operator,order_amount,trx_goods_amount,refund_status,handle_type,order_date,confirm_date,create_date,product_name,description,refund_source_type"
				+ " from beiker_refund_record where trxorder_id=?";

		List<RefundRecord> refundRecordList = getSimpleJdbcTemplate().query(
				qrySql, new RowMapperImpl(), ordId);

		if (refundRecordList.size() > 0) {
			return (List<RefundRecord>) refundRecordList;
		}
		return null;

	}

	public RefundRecord findByTrxGoodsId(Long trxGoodsId) {

		if (trxGoodsId == null) {

			throw new IllegalArgumentException("trxGoodsId not null");
		}

		String qrySql = "select rudrecord_id,version,trxorder_id,user_id,trx_goods_id,operator,order_amount,trx_goods_amount,refund_status,handle_type,order_date,confirm_date,create_date,product_name,description,refund_source_type"
				+ " from beiker_refund_record where trx_goods_id=?";

		List<RefundRecord> refundRecordList = getSimpleJdbcTemplate().query(
				qrySql, new RowMapperImpl(), trxGoodsId);

		if (refundRecordList.size() > 0) {
			return refundRecordList.get(0);
		}
		return null;

	}

	public List<RefundRecord> findByUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(RefundRecord refundRecord) {
		
		
		

	}

	public void updateByIdAndRefundStatus(Long id, RefundStatus refundStatus,Long version) throws StaleObjectStateException {
		if (id == null || refundStatus == null) {
			throw new IllegalArgumentException("id and refundStatus not null");
		}
		String uptSql = "update beiker_refund_record set refund_status=?,version=? where rudrecord_id=? and version=?";
		int result = getSimpleJdbcTemplate().update(uptSql, refundStatus.name(),version+1L, id,version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<RefundRecord> {

		public RefundRecord mapRow(ResultSet rs, int num) throws SQLException {

			RefundRecord refundRecord = new RefundRecord();
			refundRecord.setId(rs.getLong("rudrecord_id"));
			refundRecord.setVersion(rs.getLong("version"));
			refundRecord.setTrxOrderId(rs.getLong("trxorder_id"));
			refundRecord.setUserId(rs.getLong("user_id"));
			refundRecord.setTrxGoodsId(rs.getLong("trx_goods_id"));
			refundRecord.setOperator(rs.getString("operator"));
			refundRecord.setRefundStatus(EnumUtil.transStringToEnum(
					RefundStatus.class, rs.getString("refund_status")));
			refundRecord.setHandleType(EnumUtil.transStringToEnum(
					RefundHandleType.class, rs.getString("handle_type")));
			refundRecord.setOrderDate(rs.getDate(("order_date")));
			refundRecord.setConfirmDate(rs.getDate("confirm_date"));
			refundRecord.setCreateDate(rs.getDate("create_date"));
			refundRecord.setOrderAmount(rs.getDouble("order_amount"));
			refundRecord.setTrxGoodsAmount((rs.getDouble("trx_goods_amount")));
			refundRecord.setProductName(rs.getString("product_name"));
			refundRecord.setDescription(rs.getString("description"));
			refundRecord.setRefundSourceType(EnumUtil.transStringToEnum(RefundSourceType.class,rs.getString("refund_source_type")));

			return refundRecord;

		}
	}

    @Override
    public List<RefundRecord> findByStatusAndDate(RefundStatus refundStatus, String date,String trxgoods) {
        StringBuilder builder = new StringBuilder();
        builder.append("select rudrecord_id,version,trxorder_id,user_id,trx_goods_id,operator,order_amount,trx_goods_amount,refund_status,handle_type,order_date,confirm_date,create_date,product_name,description,refund_source_type");
        builder.append(" from beiker_refund_record where refund_status=?  and create_date<=? and trx_goods_id in(");
        builder.append(trxgoods);
        builder.append(")");
        List<RefundRecord> refundRecordList = getSimpleJdbcTemplate().query(
                builder.toString(), new RowMapperImpl(), refundStatus.name(),date);

        if (refundRecordList.size() > 0) {
            return  refundRecordList;
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findByPayTypeAndStatusAndDate(PaymentType paymentType,
            ProviderType providerType, RefundStatus refundStatus, String startDate,String endDate,String payDate) {
        StringBuilder stringBuilder= new StringBuilder();
        
        stringBuilder.append("SELECT rd.ruddetail_id,rr.VERSION,rr.trxorder_id,rr.user_id,rr.trx_goods_id,rr.operator,");
        stringBuilder.append(" rr.order_amount,rr.trx_goods_amount,rr.refund_status,rr.handle_type,rr.order_date,rr.confirm_date,");
        stringBuilder.append(" rr.create_date,rr.product_name,rr.description,rr.refund_source_type");
        stringBuilder.append(" ,rd.amount, rd.payment_amount,rd.pro_refund_request_id,rd.pro_external_id");
        stringBuilder.append(" FROM beiker_refund_record rr LEFT JOIN beiker_refund_detail rd ON ");
        stringBuilder.append(" rr.rudrecord_id=rd.rudrecord_id AND rd.pro_refund_status='REFUNDTOBANK'");
        stringBuilder.append(" LEFT JOIN beiker_payment pm ON ");
        stringBuilder.append(" rr.trxorder_id=pm.trxorder_id AND rd.payment_id=pm.id  ");
        stringBuilder.append(" where pm.payment_type=? AND pm.provider_type=?");
        stringBuilder.append(" AND rr.refund_status=?  AND pm.pay_confirm_date <=? and rd.handle_date BETWEEN  ? and ? "); 
//        List<RefundRecord> refundRecordList = getSimpleJdbcTemplate().query(
//                stringBuilder.toString(), new RowMapperImpl(), paymentType.name(),providerType.name(),refundStatus.name(),date);
        List<Map<String, Object>>  refundRecordList = getSimpleJdbcTemplate().queryForList(stringBuilder.toString(), paymentType.name(),providerType.name(),refundStatus.name(),payDate,startDate,endDate);
        if (refundRecordList.size() > 0) {
            return  refundRecordList;
        }
        return null;
    }

    

//	private void setStringValueWithNull(
//			final PreparedStatement preparedStatement, int index, Object value)
//			throws SQLException {
//		if (value == null) {
//			preparedStatement.setNull(index, Types.VARCHAR);
//		} else {
//			preparedStatement.setString(index, value.toString());
//		}
//	}

}
