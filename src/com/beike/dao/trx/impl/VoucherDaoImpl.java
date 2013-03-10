package com.beike.dao.trx.impl;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.enums.trx.VoucherVerifySource;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.VoucherDao;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;

/**
 * @Title: VoucherDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: TODO
 * @date May 27, 2011 11:28:28 AM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("voucherDao")
public class VoucherDaoImpl extends GenericDaoImpl<Voucher, Long> implements
		VoucherDao {

	@Override
	public Long addVoucher(final Voucher voucher) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (voucher == null) {

			throw new IllegalArgumentException("voucher not null");
		}

		final String istSql = "insert into beiker_voucher(guest_id,create_date,voucher_status,voucher_code,description) value(?,?,?,?,?)";

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con
						.prepareStatement(istSql, new String[] { "guest_id",
								"create_date", "voucher_status",
								"voucher_code", "description" });

				longValueWithNull(ps, 1, voucher.getGuestId());
				ps.setTimestamp(2, new java.sql.Timestamp((voucher
						.getCreateDate()).getTime()));
				// tineValueWithNull(ps,3, new java.sql.Timestamp(((Date)
				// voucher
				// .getActiveDate()).getTime()));
				//
				// tineValueWithNull(ps,4, new java.sql.Timestamp(((Date)
				// voucher
				// .getConfirmDate()).getTime()));

				setStringValueWithNull(ps, 3, EnumUtil
						.transEnumToString(voucher.getVoucherStatus()));
				setStringValueWithNull(ps, 4, voucher.getVoucherCode());

				setStringValueWithNull(ps, 5, voucher.getDescription());
				return ps;
			}

		}, keyHolder);
		Long voucherId = keyHolder.getKey().longValue();
		return voucherId;
	}

	@Override
	public int findByDateAndStatus(Date curDate, VoucherStatus voucherStatus) {
		if (voucherStatus == null) {
			throw new IllegalArgumentException("voucherStatus not null");
		}
		String sql = "select count(*) as count from beiker_voucher where  is_prefetch=0 and  voucher_status=?";
		Map<String, Object> voucherMap = null;
		if (curDate != null) {
			Date maxDate = DateUtils.getMaxTime(curDate);
			Date minDate = DateUtils.getMinTime(curDate);
			sql = sql + " and create_date<=? and create_date>=?";
			voucherMap = getSimpleJdbcTemplate().queryForMap(sql,
					voucherStatus.name(), maxDate, minDate);
		} else {
			voucherMap = getSimpleJdbcTemplate().queryForMap(sql,
					voucherStatus.name());
		}
		if (voucherMap.get("count") == null) {
			return 0;
		}
		int result = (BigInteger.valueOf((Long) voucherMap.get("count")))
				.intValue();
		return result;

	}

	@Override
	public List<Voucher> findByGuestId(Long guestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Voucher findById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("voucherId not null");
		}
		String sql = "select voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from beiker_voucher where voucher_id=?";
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), id);
		if (voucherList.size() > 0) {
			return voucherList.get(0);
		}
		return null;
	}

	@Override
	public void update(Voucher voucher) throws StaleObjectStateException {
		if (voucher.getId() == null) {
			throw new IllegalArgumentException("voucherId not null");
		}
		String sql = "update beiker_voucher set guest_id=?,version=?,create_date=?,active_date=?,confirm_date=?,voucher_status=?,voucher_code=?,voucher_verify_source=?,description=? where voucher_id=? and version=?";
		int result = getSimpleJdbcTemplate()
				.update(
						sql,
						voucher.getGuestId(),
						voucher.getVersion() + 1L,
						voucher.getCreateDate(),
						voucher.getActiveDate(),
						voucher.getConfirmDate(),
						EnumUtil.transEnumToString(voucher.getVoucherStatus()),
						voucher.getVoucherCode(),
						EnumUtil.transEnumToString(voucher
								.getVoucherVerifySource()),
						voucher.getDescription(), voucher.getId(),
						voucher.getVersion());
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	@Override
	public void updateStatusByIdAndDate(Long id, VoucherStatus voucherStatus,
			Date confirmDate, Long version) throws StaleObjectStateException {
		if (id == null || voucherStatus == null) {

			throw new IllegalArgumentException("id and voucherStatus not null");
		}
		String sql = "update beiker_voucher set voucher_status=?,version=?,confirm_date=? where voucher_id=? and version=?";
		int result = getSimpleJdbcTemplate().update(sql, voucherStatus.name(),
				version + 1L, confirmDate, id, version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	@Override
	public void updateByGuestIdAndStatus(Long id, VoucherStatus voucherStatus,
			Long guestId, Long version) throws StaleObjectStateException {
		if (id == null || voucherStatus == null) {

			throw new IllegalArgumentException("id and voucherStatus not null");
		}
		String sql = "update beiker_voucher set voucher_status=?,version=?,guest_id=?  where voucher_id=? and version=?";
		int result = getSimpleJdbcTemplate().update(sql, voucherStatus.name(),
				version + 1L, guestId, id, version);

		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	@Override
	public Voucher findInit(VoucherStatus voucherStatus) {
		if (voucherStatus == null) {
			throw new IllegalArgumentException(" voucherStatus not null");
		}
		String sql = "select  voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from  beiker_voucher where  voucher_status=? limit 0,1 for update";
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), voucherStatus.name());

		if (voucherList.size() == 0 || voucherList == null) {
			return null;
		}
		return voucherList.get(0);
	}

	/**
	 * 批量预取凭证
	 * 
	 * @param voucherStatus
	 * @param prefetchFlag
	 * @param prefetchCount
	 * @return
	 */
	public List<Voucher> findBatchVoucherForPre(VoucherStatus voucherStatus,
			int proPrefetchFlag, int prefetchCount) {
		if (voucherStatus == null) {
			throw new IllegalArgumentException(" voucherStatus not null");
		}
		StringBuilder sqlSb = new StringBuilder();
		sqlSb
				.append("select  voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from  beiker_voucher where  voucher_status=? and is_prefetch=?  limit 0,");
		sqlSb.append(prefetchCount);
		sqlSb.append(" for update");
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(
				sqlSb.toString(), new RowMapperImpl(),
				EnumUtil.transEnumToString(voucherStatus), proPrefetchFlag);

		if (voucherList.size() == 0 || voucherList == null) {
			return null;
		}

		return voucherList;

	}

	/**
	 * 批量预取后的更新
	 */
	public int updateBatchVoucherForPre(List<Long> vouIdList,
			VoucherStatus proVoucherStatus, int postPrefetchFlag,
			int proPrefetchFlag) {
		StringBuilder vouIdStrSb = new StringBuilder();
		StringBuilder sqlSb = new StringBuilder();
		for (Long id : vouIdList) {
			vouIdStrSb.append(id);
			vouIdStrSb.append(",");
		}
		vouIdStrSb.deleteCharAt(vouIdStrSb.length() - 1);

		sqlSb
				.append("update beiker_voucher set prefetch_date=?,is_prefetch=? where  voucher_status=? and is_prefetch=? and ");
		sqlSb.append("voucher_id in (");
		sqlSb.append(vouIdStrSb.toString());
		sqlSb.append(")");

		int result = getSimpleJdbcTemplate().update(sqlSb.toString(),
				new Date(), postPrefetchFlag,
				EnumUtil.transEnumToString(proVoucherStatus), proPrefetchFlag);

		return result;

	}

	@Override
	public Voucher findByVoucherCode(String voucherCode) {

		if (voucherCode == null) {
			throw new IllegalArgumentException("  voucherStatus not null");
		}
		String sql = "select  voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from  beiker_voucher where  voucher_code=? ";
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), voucherCode);

		if (voucherList == null || voucherList.size() == 0) {
			return null;
		}
		return voucherList.get(0);

	}

	@Override
	public List<Voucher> findByVoucherCodes(String voucherCodes) {
		if (voucherCodes == null) {
			throw new IllegalArgumentException("  voucherCode not null");
		}
		String sql = "select  voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from  beiker_voucher where  voucher_code in (?) ";
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), voucherCodes);

		if (voucherList == null || voucherList.size() == 0) {
			return null;
		}
		return voucherList;
		
	}

	@Override
	public Voucher findByGuestIdAndCode(Long guestId, String voucherCode) {

		if (guestId == null || voucherCode == null) {
			throw new IllegalArgumentException(
					" guestId and voucherStatus not null");
		}
		String sql = "select  voucher_id,version,guest_id,create_date,active_date,confirm_date,voucher_code,voucher_status,voucher_verify_source,description from  beiker_voucher where  voucher_code=? and guest_id=?";
		List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), voucherCode, guestId);

		if (voucherList == null || voucherList.size() == 0) {
			return null;
		}
		return voucherList.get(0);

	}

	public class RowMapperImpl implements ParameterizedRowMapper<Voucher> {
		@Override
		public Voucher mapRow(ResultSet rs, int num) throws SQLException {

			Voucher voucher = new Voucher();
			voucher.setId(rs.getLong("voucher_id"));
			voucher.setVersion(rs.getLong("version"));
			voucher.setGuestId(rs.getLong("guest_id"));
			voucher.setCreateDate(rs.getTimestamp("create_date"));
			voucher.setActiveDate(rs.getTimestamp("active_date"));
			voucher.setConfirmDate(rs.getTimestamp("confirm_date"));
			voucher.setVoucherStatus(EnumUtil.transStringToEnum(
					VoucherStatus.class, rs.getString("voucher_status")));
			voucher.setVoucherCode(rs.getString("voucher_code"));
			voucher.setVoucherVerifySource(EnumUtil.transStringToEnum(
					VoucherVerifySource.class, rs
							.getString("voucher_verify_source")));
			voucher.setDescription(rs.getString("description"));

			return voucher;

		}
	}

	/**
	 * 判空设置
	 * 
	 * @param preparedStatement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	private void setStringValueWithNull(
			final PreparedStatement preparedStatement, int index, Object value)
			throws SQLException {
		if (value == null) {
			preparedStatement.setNull(index, Types.VARCHAR);
		} else {
			preparedStatement.setString(index, value.toString());
		}
	}

	/**
	 * 判空设置
	 * 
	 * @param preparedStatement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	private void longValueWithNull(final PreparedStatement preparedStatement,
			int index, Object value) throws SQLException {
		if (value == null) {
			preparedStatement.setNull(index, Types.INTEGER);
		} else {
			preparedStatement.setString(index, value.toString());
		}
	}

	/**
	 * 判空设置
	 * 
	 * @param preparedStatement
	 * @param index
	 * @param value
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private void tineValueWithNull(final PreparedStatement preparedStatement,
			int index, Object value) throws SQLException {
		if (value == null) {
			preparedStatement.setString(index, "");
		} else {
			preparedStatement.setString(index, value.toString());
		}
	}

	/**
	 * 根据激活时间和状态获取凭证及订单信息
	 */
	@Override
	public List<Map<String,Object>> findByActiveDateAndStatus(Date startTime, Date endTime, String userIdStr,String trxStatusStr) {
		if(startTime==null || endTime==null || userIdStr==null || userIdStr.length()==0 || trxStatusStr==null || trxStatusStr.length()==0){
			throw new IllegalArgumentException("startTime and endTime and userIdStr not null");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select t.out_request_id as outRequestId,t.external_id as externalId,t.id as trxorderId,vo.voucher_id as  voucherId,tg.trx_status as trxStatus, tg.goods_id as goodsId, tg.pay_price as payPrice, tg.trx_goods_sn as trxGoodsSn, tg.order_lose_date as orderLoseDate,vo.voucher_code as voucherCode,vo.active_date as activeDate,tg.out_goods_id as outGoodsId");
		sql.append(" from beiker_trxorder_goods tg   inner join beiker_voucher vo on tg.voucher_id=vo.voucher_id left join beiker_trxorder t on tg.trxorder_id = t.id");
		sql.append(" where t.user_id in (");
		sql.append(userIdStr);
		sql.append(") and tg.trx_status in(");
		sql.append(trxStatusStr);
		sql.append(") and vo.active_date between ? and ?");
		List<Map<String, Object>> list = getSimpleJdbcTemplate().queryForList(sql.toString(), startTime,endTime);
		return list;
	}

}