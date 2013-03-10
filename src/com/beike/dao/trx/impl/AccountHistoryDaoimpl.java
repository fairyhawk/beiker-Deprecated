package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.AccountHistoryDao;
import com.beike.util.Constant;
import com.beike.util.EnumUtil;

/**
 * @Title: AccountHistoryServiceImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 9, 2011 10:32:05 AM
 * @version V1.0
 */
@Repository("accountHistoryDao")
public class AccountHistoryDaoimpl extends GenericDaoImpl<AccountHistory, Long>
		implements AccountHistoryDao {

	public void addAccountHistory(AccountHistory actHistory) {
		if (actHistory.getId() != null) {
			throw new IllegalArgumentException("actHistoryId not null");
		} else {
			String istSql = "insert beiker_accounthistory(account_id,act_history_type,balance,biz_type,create_date,"
					+ "description,is_display,trx_id,trx_amount,trxorder_id) value(?,?,?,?,?,?,?,?,?,?)";
			getSimpleJdbcTemplate().update(istSql, actHistory.getAccountId(),
					EnumUtil.transEnumToString(actHistory.getActHistoryType()),
					actHistory.getBalance(), actHistory.getBizType(),
					actHistory.getCreateDate(), actHistory.getDescription(),
					actHistory.isDispaly(), actHistory.getTrxId(),
					actHistory.getTrxAmount(), actHistory.getTrxOrderId());
		}

	}

	public List<AccountHistory> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public AccountHistory findById(Long id) {
		if (id == null) {
			return null;
		} else {
			String qrySql = "select id,version,trx_id,trxorder_id,account_id,create_date,is_display,balance,trx_amount,description,act_history_type,biz_type from beiker_accounthistory where id=?";
			List<AccountHistory> actHistoryList = getSimpleJdbcTemplate()
					.query(qrySql, new RowMapperImpl(), id);
			if (actHistoryList.size() > 0) {
				return actHistoryList.get(0);
			}
			return null;
		}

	}

	public AccountHistory findBybizIdAndType(Long bizId,
			ActHistoryType actHistoryType) {

		if (bizId == null || actHistoryType == null) {
			throw new IllegalArgumentException(
					"bizId and actHistoryType not null");
		} else {
			StringBuffer sb = new StringBuffer();

			sb
					.append("select id,version,trx_id,account_id,trxorder_id,create_date,is_display,balance,trx_amount,description,act_history_type,biz_type from beiker_accounthistory where trx_id=? and act_history_type=");
			sb.append("'" + actHistoryType.name());
			String qrySql = sb.toString();
			List<AccountHistory> actHistoryList = getSimpleJdbcTemplate()
					.query(qrySql, new RowMapperImpl(), bizId);
			if (actHistoryList.size() > 0) {
				return actHistoryList.get(0);
			}
			return null;
		}

	}

	public void updateAccountHistory(AccountHistory actHistory)
			throws StaleObjectStateException {
		if (actHistory == null) {
			return;
		} else {
			String upSql = "update beiker_accounthistory set account_id=?,version=?,act_history_type=?,balance=?,biz_type=?,create_date=?,"
					+ "description=?,is_display=?,trx_id=?,trx_amount=?,trxorder_id=? where id=? and version=?";
			int result = getSimpleJdbcTemplate().update(upSql,
					actHistory.getAccountId(), actHistory.getVersion() + 1L,
					EnumUtil.transEnumToString(actHistory.getActHistoryType()),
					actHistory.getBalance(), actHistory.getBizType(),
					actHistory.getCreateDate(), actHistory.getDescription(),
					actHistory.isDispaly(), actHistory.getTrxId(),
					actHistory.getTrxAmount(), actHistory.getTrxOrderId(),
					actHistory.getId(), actHistory.getVersion());
			if (result == 0) {
				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}

		}

	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<AccountHistory> {

		public AccountHistory mapRow(ResultSet rs, int num) throws SQLException {

			AccountHistory actHistory = new AccountHistory();
			actHistory.setId(rs.getLong("id"));
			actHistory.setVersion(rs.getLong("version"));
			actHistory.setAccountId(rs.getLong("account_id"));
			actHistory.setActHistoryType(EnumUtil.transStringToEnum(
					ActHistoryType.class, rs.getString("act_history_type")));
			actHistory.setBalance(rs.getDouble("balance"));
			actHistory.setBizType(rs.getString("biz_type"));
			actHistory.setCreateDate(rs.getTimestamp("create_date"));
			actHistory.setDescription(rs.getString("description"));
			actHistory.setDispaly(rs.getBoolean("is_display"));
			actHistory.setTrxId(rs.getLong("trx_id"));
			actHistory.setTrxAmount(rs.getDouble("trx_amount"));
			actHistory.setTrxOrderId(rs.getLong("trxorder_id"));
			// actHistory.setExternalId(rs.getString("external_id"));
			// actHistory.setTrxStatus(rs.getString("trx_status"));
			// actHistory.setRequestId(rs.getString("request_id"));
			// actHistory.setOrderType(rs.getString("order_type"));
			return actHistory;

		}
	}

	protected class RowMapperImplForList implements
			ParameterizedRowMapper<AccountHistory> {
		public AccountHistory mapRow(ResultSet rs, int num) throws SQLException {

			AccountHistory actHistory = new AccountHistory();
			actHistory.setId(rs.getLong("id"));
			actHistory.setAccountId(rs.getLong("account_id"));
			actHistory.setActHistoryType(EnumUtil.transStringToEnum(
					ActHistoryType.class, rs.getString("act_history_type")));
			actHistory.setBalance(rs.getDouble("balance"));
			actHistory.setBizType(rs.getString("biz_type"));
			actHistory.setCreateDate(rs.getTimestamp("create_date"));
			actHistory.setDescription(rs.getString("description"));
			actHistory.setDispaly(rs.getBoolean("is_display"));
			actHistory.setTrxId(rs.getLong("trx_id"));
			actHistory.setTrxAmount(rs.getDouble("trx_amount"));
			actHistory.setTrxOrderId(rs.getLong("trxorder_id"));
			// actHistory.setOrdAmount(rs.getDouble("ord_amount"));
			return actHistory;
		}
	}

	protected class RowMapperImplForString implements
			ParameterizedRowMapper<String> {
		public String mapRow(ResultSet rs, int num) throws SQLException {
			return String.valueOf(rs.getLong("id"));
		}
	}

	protected class RowMapperImplForTrxOrder implements
			ParameterizedRowMapper<TrxOrder> {
		public TrxOrder mapRow(ResultSet rs, int num) throws SQLException {
			TrxOrder to = new TrxOrder();
			to.setOrdAmount(rs.getDouble("ord_amount"));
			to.setDescription(rs.getString("description"));
			to.setRequestId(rs.getString("request_id"));
			to.setExternalId(rs.getString("external_id"));
			to.setTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class, rs
					.getString("trx_status")));
			return to;
		}
	}

	protected class RowMapperImplForRefund implements
			ParameterizedRowMapper<RefundRecord> {
		public RefundRecord mapRow(ResultSet rs, int num) throws SQLException {
			RefundRecord rd = new RefundRecord();
			rd.setId(rs.getLong("rudrecord_id"));
			rd.setTrxOrderId(rs.getLong("trxorder_id"));
			rd.setTrxGoodsId(rs.getLong("trx_goods_id"));
			rd.setOrderAmount(rs.getDouble("order_amount"));
			rd.setTrxGoodsAmount(rs.getDouble("trx_goods_amount"));
			rd.setCreateDate(rs.getDate("create_date"));
			rd.setConfirmDate(rs.getDate("confirm_date"));
			rd.setOrderDate(rs.getDate("order_date"));
			rd.setUserId(rs.getLong("user_id"));
			rd.setHandleType(EnumUtil.transStringToEnum(RefundHandleType.class,
					rs.getString("handle_type")));
			rd.setProductName(rs.getString("product_name"));
			rd.setRefundStatus(EnumUtil.transStringToEnum(RefundStatus.class,
					rs.getString("refund_status")));
			return rd;
		}
	}

	protected class RowMapperImplForTrxOrderGoods implements
			ParameterizedRowMapper<TrxorderGoods> {
		public TrxorderGoods mapRow(ResultSet rs, int num) throws SQLException {
			TrxorderGoods tog = new TrxorderGoods();
			tog.setGoodsId(rs.getLong("goods_id"));
			tog.setGoodsName(rs.getString("goods_name"));
			tog.setPayPrice(rs.getDouble("pay_price"));
			tog.setTrxRuleId(rs.getLong("trx_rule_id"));
			tog.setExtend_info(rs.getString("extend_info"));
			tog.setBizType(rs.getInt("biz_type"));
			return tog;
		}
	}

	@SuppressWarnings("unchecked")
	public int findRowsByUserId(Long userId, String qryType) {

		if (userId == null) {
			throw new IllegalArgumentException("userId not null");
		}
		Long resultCount = 0L;
		String sql = "";
		if (Constant.PURSE_ACTHISTROTY.equals(qryType) || qryType == null
				|| qryType == "") {
			// sql="select count(id) as count from beiker_accounthistory where  account_id in (select id  from beiker_account where    user_id=?)  group by  trxorder_id";

			// sql="select count(id) as count from beiker_accounthistory where  account_id in (select id  from beiker_account where    user_id=?)  order by  trxorder_id desc";
			sql = "SELECT count(id) cnum FROM beiker_accounthistory ba WHERE ba.account_id IN (SELECT baid.id FROM beiker_account baid WHERE baid.user_id = ?)";
		}

		if (Constant.PURSE_REBATE.equals(qryType)) {
			sql = "select count(id) cnum as count from beiker_accounthistory where act_history_type='RABATE' and  account_id in (select id  from beiker_account where    user_id=?)  group by  trxorder_id";
		}

		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sql, userId);

		if (txrOrderCountList != null && txrOrderCountList.size() != 0) {

			for (Map item : txrOrderCountList) {

				resultCount += (Long) item.get("cnum");

			}
		}
		return resultCount.intValue();
	}

	public List<AccountHistory> listHis(Long userId, int startRow,
			int pageSize, String viewType) {

		String sql = "";
		if (Constant.PURSE_ACTHISTROTY.equals(viewType) || viewType == null
				|| viewType == "") {
			// sql="select * from beiker_accounthistory where  account_id in (select id  from beiker_account where    user_id=?)  group by  trxorder_id limit ?,?";

			// sql="select * from beiker_accounthistory where  account_id in (select id  from beiker_account where    user_id=?)  order by  trxorder_id desc limit ?,?";
			// sql =
			// "SELECT * FROM  beiker_accounthistory ah, beiker_trxorder txo	WHERE ah.trxorder_id = txo.id AND txo.user_id = ? limit ?, ? order by trxorder_id ";
			sql = new StringBuffer().append(
					"SELECT t1.*, t2.ord_amount, t2.user_id ").append(
					"FROM (SELECT * ").append("FROM beiker_accounthistory ba ")
					.append("WHERE ba.account_id IN(SELECT baid.id ").append(
							"FROM beiker_account baid ").append(
							"WHERE baid.user_id = ?) ").append(
							"ORDER BY ba.create_date DESC) t1 ").append(
							"LEFT JOIN beiker_trxorder t2 ").append(
							"ON t2.id = t1.trxorder_id LIMIT ?,?").toString();
		}
		if (Constant.PURSE_REBATE.equals(viewType)) {
			sql = "select * from beiker_accounthistory where act_history_type='RABATE' and  account_id in (select id  from beiker_account where    user_id=?) limit ?,? order by trxorder_id desc";
		}
		List<AccountHistory> resultList = this.getSimpleJdbcTemplate().query(
				sql, new RowMapperImplForList(), userId, startRow, pageSize);

		if (resultList == null || resultList.size() == 0) {
			return null;
		}

		return resultList;
	}

	/**
	 * 鏍规嵁trxId鏌ヨ鍙嶇幇
	 * 
	 * @param trxId
	 * @return TrxorderGoods瀵硅薄锛屽寘鍚弽鐜颁俊鎭�
	 */
	public List<TrxorderGoods> findRabateByTrxId(long trxId) {
		String sql = " SELECT goods_id, goods_name, pay_price,trx_rule_id,extend_info , btg.biz_type FROM beiker_trxorder_goods btg WHERE btg.id = ?";
		List<TrxorderGoods> resList = this.getSimpleJdbcTemplate().query(sql,
				new RowMapperImplForTrxOrderGoods(), trxId);
		if (resList == null || resList.size() == 0) {
			return null;
		}
		return resList;
	}

	/**
	 * 鏍规嵁userId鏌ヨbeiker_account琛紝鏌ヨ鍑鸿处鎴穒d
	 * 
	 * @param userId
	 *            鐢ㄦ埛ID
	 * @return 杩斿洖id鍒楄〃
	 */
	public List<String> findAccIdByUserId(Long userId) {
		String sql = "SELECT baid.id FROM beiker_account baid WHERE baid.user_id = ?";
		List<String> resList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImplForString(), userId);
		if (resList == null || resList.size() == 0) {
			return null;
		}
		return resList;
	}

	/**
	 * 鏍规嵁account_id鏌ヨbeiker_accounthistory琛�
	 * 
	 * @param idStr
	 *            account_id闆嗗悎
	 * @param startRow
	 *            寮�琛屽彿锛岀敤浜庡垎椤�
	 * @param pageSize
	 *            涓�〉鏄剧ず鏁伴噺
	 * @return 杩斿洖鍘嗗彶璁板綍瀵硅薄鍒楄〃
	 */
	public List<AccountHistory> findAccounthistoryByAccId(String idStr) {
		StringBuffer sql = new StringBuffer();
		sql
				.append(
						"SELECT ba.id, ba.trx_id, ba.trxorder_id, ba.account_id, ba.balance, ba.trx_amount, ")
				.append(
						"ba.description, ba.act_history_type, ba.biz_type ,ba.is_display,ba.create_date ")
				.append(
						" FROM beiker_accounthistory ba WHERE ba.account_id IN (")
				.append(idStr).append(
						")   and  ba.is_display=1 ORDER BY  create_date DESC,id desc ");
		List<AccountHistory> resList = getSimpleJdbcTemplate().query(
				sql.toString(), new RowMapperImplForList());
		if (resList == null || resList.size() == 0) {
			return null;
		}
		return resList;
	}

	/**
	 * 鏍规嵁id鏌ヨ浜ゆ槗璁㈠崟琛╞eiker_trxorder
	 * 
	 * @param trxId
	 *            浜ゆ槗ID
	 * @return 杩斿洖璁㈠崟瀵硅薄鍒楄〃
	 */
	public List<TrxOrder> findTrxOrderObjById(Long trxId) {
		String sql = "SELECT t2.ord_amount, t2.description, t2.request_id, t2.external_id,t2.trx_status FROM beiker_trxorder t2 WHERE t2.id = ?";
		List<TrxOrder> resList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImplForTrxOrder(), trxId);
		if (resList == null || resList.size() == 0) {
			return null;
		}
		return resList;
	}

	public List<RefundRecord> findRefundInfoByTrxOrderId(Long trxOrderId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ").append("t.rudrecord_id, ").append(
				"t.trxorder_id, ").append("t.user_id, ").append(
				"t.trx_goods_id, ").append("t.refund_source_type, ").append(
				"t.operator, ").append("t.order_amount, ").append(
				"t.trx_goods_amount, ").append("t.refund_status, ").append(
				"t.handle_type, ").append("t.order_date, ").append(
				"t.confirm_date, ").append("t.create_date, ").append(
				"t.product_name, ").append("t.description ").append(
				"FROM beiker_refund_record t ").append(
				"WHERE t.trxorder_id = ?  ORDER BY t.create_date DESC");
		List<RefundRecord> rsList = getSimpleJdbcTemplate().query(
				sql.toString(), new RowMapperImplForRefund(), trxOrderId);
		if (rsList == null || rsList.size() == 0) {
			return null;
		}
		return rsList;
	}

	public List<TrxorderGoods> findGoodsIdByTrxOrderId(Long trxOrderId) {
		String sql = "SELECT btg.goods_id, btg.goods_name, btg.pay_price,btg.trx_rule_id,btg.extend_info btg.biz_type FROM beiker_trxorder_goods btg WHERE btg.trxorder_id = ?";
		List<TrxorderGoods> rsList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImplForTrxOrderGoods(), trxOrderId);
		if (rsList == null || rsList.size() == 0) {
			return null;
		}
		return rsList;
	}

	public List<TrxorderGoods> findGoodsById(Long id) {
		String sql = "SELECT btg.goods_id, btg.goods_name, btg.pay_price,btg.trx_rule_id,btg.extend_info, btg.biz_type  FROM beiker_trxorder_goods btg WHERE btg.id = ?";
		List<TrxorderGoods> rsList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImplForTrxOrderGoods(), id);
		if (rsList == null || rsList.size() == 0) {
			return null;
		}
		return rsList;
	}

	public List<AccountHistory> findAccountIdByActType(Long accountId,
			String actHistoryType) {
		if (accountId == null || actHistoryType == null
				|| "".equals(actHistoryType)) {
			return null;
		} else {
			StringBuilder sqlSb = new StringBuilder();
			sqlSb
					.append("select id,version,trx_id,trxorder_id,account_id,create_date,is_display,balance,trx_amount,description,act_history_type,biz_type ");
			sqlSb
					.append("from beiker_accounthistory where account_id=? and act_history_type in (");
			sqlSb.append(actHistoryType);
			sqlSb.append(") order by id desc");
			List<AccountHistory> actHistoryList = getSimpleJdbcTemplate()
					.query(sqlSb.toString(), new RowMapperImpl(), accountId);
			if (actHistoryList != null && actHistoryList.size() > 0) {
				return actHistoryList;
			}
			return null;
		}

	}
}
