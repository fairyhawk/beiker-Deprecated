package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDaoImpl;
import com.beike.util.Constant;
import com.beike.util.EnumUtil;
import com.beike.wap.dao.MTrxorderGoodsDao;
import com.beike.wap.entity.MTrxorderGoods;

/**
 * @Description: 订单商品明细DAO实现类
 * @author k.wang
 */
@Repository("mTrxorderGoodsDao")
public class MTrxorderGoodsDaoImpl extends GenericDaoImpl<MTrxorderGoods, Long>
		implements MTrxorderGoodsDao {
	private static Log log = LogFactory.getLog(MTrxorderGoodsDaoImpl.class);

	@Override
	public int findPageCountByUserId(Long userId, String viewType) {
		if (userId == null) {
			throw new IllegalArgumentException("userId not null");
		}
		Long resultCount = 0L;
		StringBuffer sb = new StringBuffer();

		List<Object> paramList = new ArrayList<Object>();

		sb.append("select count(1)  as count from beiker_trxorder_goods  tg");
		sb.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id ");

		if (Constant.TRX_GOODS_ALL.equals(viewType)) {
			sb.append(" where tg.trx_status<>?");
			paramList.add(TrxStatus.INIT.toString());
		}
		if (Constant.TRX_GOODS_UNCOMMENT.equals(viewType)) {
			sb.append(" where tg.trx_status=?");
			paramList.add(TrxStatus.USED.toString());
		}

		if (Constant.TRX_GOODS_UNUSEED.equals(viewType) || viewType == "" || viewType == null) {
			sb.append(" where tg.trx_status=?");
			paramList.add(TrxStatus.SUCCESS.toString());
		}
		sb.append(" and  t.user_id=?");
		paramList.add(userId);

		try {
			List<Map<String, Object>> txrOrderCountList = getSimpleJdbcTemplate().queryForList(sb.toString(), paramList.toArray(new Object[] {}));

			if (txrOrderCountList != null && txrOrderCountList.size() != 0) {

				resultCount = (Long) txrOrderCountList.get(0).get("count");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("get page count have an exception");
			return 0;
		}
		return resultCount.intValue();
	}

	@Override
	public List<MTrxorderGoods> findOrderByStatusAndTrxId(Long userId, String status, int startPage, int pageSize) {
		StringBuffer sb = new StringBuffer();
		List<Object> paramList = new ArrayList<Object>();
		sb.append("SELECT tg.* FROM beiker_trxorder_goods tg left join beiker_trxorder tb on tb.id=tg.trxorder_id WHERE tb.user_id = ?");
		paramList.add(userId);
		if (Constant.TRX_GOODS_ALL.equals(status)) {
			sb.append(" and tg.trx_status<>?");
			paramList.add(TrxStatus.INIT.toString());
		}
		if (Constant.TRX_GOODS_UNCOMMENT.equals(status)) {
			sb.append(" and tg.trx_status=?");
			paramList.add(TrxStatus.USED.toString());
		}

		if (Constant.TRX_GOODS_UNUSEED.equals(status) || status == "" || status == null) {
			sb.append(" and tg.trx_status=?");
			paramList.add(TrxStatus.SUCCESS.toString());
		}
		sb.append(" order by  tg.id desc  limit ?,?");

		paramList.add(startPage);
		paramList.add(pageSize);
		List<MTrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(sb.toString(), new RowMapperImpl(), paramList.toArray(new Object[] {}));

		if (trxGoodsList == null || trxGoodsList.size() == 0) {
			return null;
		}
		return trxGoodsList;
	}

	/*
	 * @see
	 * com.beike.wap.dao.MTrxorderGoodsDao#getTrxOrderGoodsInfo(java.lang.Long)
	 */
	@Override
	public MTrxorderGoods getTrxOrderGoodsInfo(Long id) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT t.goods_id goods_id ,t.trx_goods_sn trxGoodsSn,g.logo3 logo3,g.goodsname goodsName,");
		sql.append("g.currentPrice currentPrice,g.rebatePrice rebatePrice,t.create_date createDate,");
		sql.append("t.trx_status trxStatus,v.voucher_code voucher_code,t.order_lose_date order_lose_date ,m.tel tel ");
		sql.append("FROM beiker_trxorder_goods t ");
		sql.append("LEFT JOIN beiker_goods g ON g.goodsid = t.goods_id ");
		sql.append("LEFT JOIN beiker_goods_merchant gm ON g.goodsid = gm.goodsid ");
		sql.append("LEFT JOIN beiker_merchant m ON m.merchantid = gm.merchantid ");
		sql.append("LEFT JOIN beiker_voucher v ON t.voucher_id = v.voucher_id  ");
		sql.append("WHERE t.id = ? AND m.parentId = 0 ");
		int[] types = new int[] { Types.INTEGER };
		Object[] params = new Object[] { id };
		MTrxorderGoods trxOrderGood = (MTrxorderGoods) this.getJdbcTemplate()
				.queryForObject(sql.toString(), params, types, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int i)
							throws SQLException {
						MTrxorderGoods goods = new MTrxorderGoods();
						goods.setGoodsId(rs.getLong("goods_id"));
						goods.setTrxGoodsSn(rs.getString("trxGoodsSn"));
						goods.setLogo3(rs.getString("logo3"));
						goods.setGoodsName(rs.getString("goodsName"));
						goods.setCurrentPrice(rs.getDouble("currentPrice"));
						goods.setRebatePrice(rs.getDouble("rebatePrice"));
						goods.setCreateDate(rs.getDate("createDate"));
						goods.setTrxStatusSign(rs.getString("trxStatus"));
						goods.setVoucherCode(rs.getString("voucher_code"));
						goods.setConfirmDate(rs.getDate("order_lose_date"));
						goods.setTel(rs.getString("tel"));
						return goods;
					}
				});
		return trxOrderGood;
	}

	/*
	 * @see
	 * com.beike.wap.dao.MTrxorderGoodsDao#getRefundGoodsInfo(java.lang.String)
	 */
	@Override
	public MTrxorderGoods getRefundGoodsInfo(String id, String status)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT t.goods_id goods_id ,t.trx_goods_sn trxGoodsSn,g.logo3 logo3,g.goodsname goodsName,");
		sql.append("g.currentPrice currentPrice,g.rebatePrice rebatePrice,t.create_date createDate,");
		sql.append("t.trx_status trxStatus,d.handle_date handle_date ");
		sql.append("FROM beiker_trxorder_goods t ");
		sql.append("LEFT JOIN beiker_goods g ON g.goodsid = t.goods_id ");
		sql.append("LEFT JOIN beiker_refund_record r ON r.trxorder_id = t.trxorder_id ");
		sql.append("LEFT JOIN beiker_refund_detail d ON r.rudrecord_id = d.rudrecord_id ");
		sql.append("WHERE t.id = ? ");
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(id);
		if (status.equals(TrxStatus.REFUNDTOACT.toString())
				|| status.equals(TrxStatus.REFUNDTOBANK.toString())) {
			sql.append(" AND r.trx_goods_id = ?");
			paramList.add(id);
		}
		MTrxorderGoods refundGoodsInfo = getSimpleJdbcTemplate().queryForObject(sql.toString(), new ParameterizedRowMapper<MTrxorderGoods>() {
							@Override
							public MTrxorderGoods mapRow(ResultSet rs, int arg1) throws SQLException {
								MTrxorderGoods goods = new MTrxorderGoods();
								goods.setGoodsId(rs.getLong("goods_id"));
								goods.setTrxGoodsSn(rs.getString("trxGoodsSn"));
								goods.setLogo3(rs.getString("logo3"));
								goods.setGoodsName(rs.getString("goodsName"));
								goods.setCurrentPrice(rs
										.getDouble("currentPrice"));
								goods.setRebatePrice(rs
										.getDouble("rebatePrice"));
								goods.setCreateDate(rs.getDate("createDate"));
								goods.setTrxStatusSign(rs
										.getString("trxStatus"));// 对应的状态
								goods.setHandleDate(rs.getDate("handle_date"));
								return goods;
							}}, paramList.toArray(new Object[] {}));
		return refundGoodsInfo;
	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<MTrxorderGoods> {
		@Override
		public MTrxorderGoods mapRow(ResultSet rs, int num) throws SQLException {
			MTrxorderGoods trxorderGoods = new MTrxorderGoods();

			trxorderGoods.setAuthDate(rs.getTimestamp("auth_date"));
			trxorderGoods.setAuthStatus(EnumUtil.transStringToEnum(
					AuthStatus.class, rs.getString("auth_status")));
			trxorderGoods.setCreateDate(rs.getTimestamp("create_date"));
			trxorderGoods.setCurrentPrice(rs.getDouble("current_price"));
			trxorderGoods.setDescription(rs.getString("description"));
			trxorderGoods.setDividePrice(rs.getDouble("divide_price"));
			trxorderGoods.setExtend_info(rs.getString("extend_info"));
			trxorderGoods.setGoodsId(rs.getLong("goods_id"));
			trxorderGoods.setGoodsName(rs.getString("goods_name"));
			trxorderGoods.setId(rs.getLong("id"));
			trxorderGoods.setMerSettleStatus(EnumUtil.transStringToEnum(
					MerSettleStatus.class, rs.getString("mer_settle_status")));
			trxorderGoods.setPayPrice(rs.getDouble("pay_price"));
			trxorderGoods.setRebatePrice(rs.getDouble("rebate_price"));
			trxorderGoods.setSourcePrice(rs.getDouble("source_price"));
			trxorderGoods.setTrxGoodsSn(rs.getString("trx_goods_sn"));
			trxorderGoods.setTrxStatus(EnumUtil.transStringToEnum(
					TrxStatus.class, rs.getString("trx_status")));
			trxorderGoods.setTrxorderId(rs.getLong("trxorder_id"));
			trxorderGoods.setGuestId(rs.getLong("guest_id"));
			trxorderGoods.setVoucherId(rs.getLong("voucher_id"));
			trxorderGoods.setCommentId(rs.getLong("comment_id"));
			trxorderGoods
					.setOrderLoseAbsDate(rs.getLong("order_lose_abs_date"));
			trxorderGoods.setOrderLoseDate(rs.getTimestamp("order_lose_date"));
			trxorderGoods.setTrxRuleId(rs.getLong("trx_rule_id"));// 新增交易类型

			return trxorderGoods;
		}
	}

	@Override
	public MTrxorderGoods findById(Long id) {
		String sql = "SELECT * FROM beiker_trxorder_goods WHERE id=?";
		List<MTrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(sql,
				new RowMapperImpl(), id);
		if (trxGoodsList == null || trxGoodsList.size() == 0) {
			return null;
		}
		return trxGoodsList.get(0);
	}
}
