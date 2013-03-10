package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**
 * @Title: TrxorderGoodsDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: 订单商品明细DAO实现类
 * @date May 16, 2011 7:48:48 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("trxorderGoodsDao")
public class TrxorderGoodsDaoImpl extends GenericDaoImpl<TrxorderGoods, Long>
		implements TrxorderGoodsDao {

	@Override
	public void addTrxGoods(TrxorderGoods trxGoods) {
		if (trxGoods == null) {

			throw new IllegalArgumentException("TrxGoods object nou null");
		} else {
			StringBuffer sb = new StringBuffer();

			sb
					.append("insert into beiker_trxorder_goods(trx_goods_sn,goods_name,source_price,current_price,trx_rule_id,is_refund,is_send_mer_vou,isadvance,");

			sb
					.append("pay_price,rebate_price,divide_price,trx_status,auth_status,extend_info,description,auth_date,trxorder_id,goods_id,mer_settle_status,create_date,");
			sb
					.append("guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,last_update_date,out_goods_id,biz_type,sub_guest_id) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			getSimpleJdbcTemplate().update(
							sb.toString(),
							trxGoods.getTrxGoodsSn(),
							trxGoods.getGoodsName() + "",
							trxGoods.getSourcePrice(),
							trxGoods.getCurrentPrice(),
							trxGoods.getTrxRuleId(),// 新增交易类型
							trxGoods.isRefund(),// 是否支持退款.默认为支持
							trxGoods.isSendMerVou(),// 是否发送商家自有凭证码
							trxGoods.isIsadvance(),// 是否预付款
					trxGoods.getPayPrice(), trxGoods.getRebatePrice(),
							trxGoods.getDividePrice(),
							EnumUtil.transEnumToString(trxGoods.getTrxStatus()),
					EnumUtil.transEnumToString(trxGoods.getAuthStatus()),
					trxGoods.getExtend_info(), trxGoods.getDescription(),
					trxGoods.getAuthDate(), trxGoods.getTrxorderId(),
							trxGoods.getGoodsId(),
					EnumUtil.transEnumToString(trxGoods.getMerSettleStatus()),
							trxGoods.getCreateDate(), trxGoods.getGuestId(),
							trxGoods.getVoucherId(), trxGoods.getCommentId(),
							trxGoods.getOrderLoseAbsDate(),
					trxGoods.getOrderLoseDate(), trxGoods.getLastUpdateDate(),
					trxGoods.getOutGoodsId(),trxGoods.getBizType(),trxGoods.getSubGuestId());

		}

	}

	@Override
	public TrxorderGoods findById(Long id) {
		if (id == null) {

			throw new IllegalArgumentException("id nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,out_goods_id,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date, biz_type ,credit_status from beiker_trxorder_goods where id=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), id);

		if (trxGoodsList.size() > 0) {

			return trxGoodsList.get(0);
		}
		return null;
	}

	@Override
	public TrxorderGoods findById(Long id, Long userId) {
		if (id == null) {

			throw new IllegalArgumentException("id nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select trxg.id,trxg.version,trxg.goods_id,trxg.trxorder_id,trxg.auth_date,trxg.create_date,trxg.source_price,trxg.current_price,trxg.pay_price,trxg.rebate_price,trxg.trx_rule_id,trxg.sub_guest_id,trxg.is_refund,trxg.is_send_mer_vou,");
		sb
				.append("trxg.divide_price,trxg.trx_goods_sn,trxg.goods_name,trxg.guest_id,trxg.voucher_id,trxg.comment_id,trxg.order_lose_abs_date,trxg.order_lose_date,trxg.out_goods_id,");
		sb
				.append("trxg.trx_status,trxg.auth_status,trxg.extend_info,trxg.description,trxg.mer_settle_status,trxg.is_dis,trxg.isadvance,trxg.last_update_date ,trxg.biz_type ,trxg.credit_status from beiker_trxorder_goods trxg left join beiker_trxorder trx on trx.id=trxorder_id where trxg.id=? and trx.user_id=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), id, userId);

		if (trxGoodsList.size() > 0) {

			return trxGoodsList.get(0);
		}
		return null;
	}

	@Override
	public TrxorderGoods findByVoucherId(Long voucherId) {

		if (voucherId == null) {

			throw new IllegalArgumentException("voucherId nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,out_goods_id,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date, biz_type,credit_status  from beiker_trxorder_goods where voucher_id=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), voucherId);

		if (trxGoodsList.size() > 0) {

			return trxGoodsList.get(0);
		}
		return null;

	}

	@Override
	public List<TrxorderGoods> findByTrxId(Long trxId) {

		if (trxId == null) {

			throw new IllegalArgumentException("trxId nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,trx_goods_sn,goods_name,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id, biz_type ,credit_status from beiker_trxorder_goods where trxorder_id=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), trxId);

		if (trxGoodsList.size() > 0) {

			return trxGoodsList;
		}
		return null;

	}

	/**
	 * 查询本次购买数量（以商品ID分组）
	 * 
	 * @param trxId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Long>> findCountByTrxId(Long trxId) {

		if (trxId == null) {

			throw new IllegalArgumentException("trxId nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select count(1) as trxGoodsCount,goods_id as goodsId from beiker_trxorder_goods where trxorder_id=? group by goods_id");
		List trxGoodsCountList = getSimpleJdbcTemplate().queryForList(
				sb.toString(), new Object[] { trxId });
		List<Map<String, Long>> rspList = new ArrayList<Map<String, Long>>();
		logger.info("trxGoodsCountList===" + trxGoodsCountList.size());
		if (trxGoodsCountList.size() > 0 && trxGoodsCountList != null) {
			int trxGoodsCount = trxGoodsCountList.size();
			for (int i = 0; i < trxGoodsCount; i++) {
				Map valueMap = (Map) trxGoodsCountList.get(i);
				Map<String, Long> map = new HashMap<String, Long>();
				Long trxGoodsCountValue = (Long) valueMap.get("trxGoodsCount");
				Long goodsId = (Long) valueMap.get("goodsId");
				map.put("trxGoodsCount", trxGoodsCountValue);
				map.put("goodsId", goodsId);
				rspList.add(map);
			}

		}
		return rspList;

	}

	@Override
	public List<TrxorderGoods> findInTrxId(String trxIdStr, String goodsIdStr) {

		if (trxIdStr == null) {

			throw new IllegalArgumentException("trxIdStr nou null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,trx_goods_sn,goods_name,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb.append("guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id, biz_type,credit_status  from beiker_trxorder_goods where trxorder_id =? and goods_id =? order by id desc,trx_rule_id");
		/*
		 * sb.append("("); sb.append(trxIdStr); sb.append(")");
		 * sb.append(" and goods_id = "); sb.append(" (");
		 * sb.append(goodsIdStr); sb.append(")");
		 * sb.append(" order by id desc");
		 */
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(sb.toString(), new RowMapperImpl(), trxIdStr, goodsIdStr);

			return trxGoodsList;

	}

	@Override
	public TrxorderGoods findBySn(String trxSn) {

		if (trxSn == null || trxSn.length()==0) {

			throw new IllegalArgumentException("trxSn not null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,");
		sb
				.append("trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id,biz_type,credit_status  from beiker_trxorder_goods where trx_goods_sn=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), trxSn);

		if (trxGoodsList.size() > 0) {

			return trxGoodsList.get(0);
		}
		return null;

	}

	@Override
	public List<Map<String, Object>> findTrxOrderIdByUserID(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId not null");
		}
		String sql = "select id from beiker_trxorder where user_id=?";
		List<Map<String, Object>> txrOrderIdList = this.getSimpleJdbcTemplate()
				.queryForList(sql, userId);

		if (txrOrderIdList == null || txrOrderIdList.size() == 0) {
			return null;
		}

		return txrOrderIdList;
	}

	@Override
	public List<Map<String, Object>> findTrxIdByUserIdAndType(Long userId,
			int startRow, int pageSize, String viewType) {

		StringBuffer sb = new StringBuffer();

		sb.append("select tg.trxorder_id as trxorderId ,tg.goods_id as goodsId,t.mobile as mobile from beiker_trxorder_goods  tg");
		sb.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id ");

		if (Constant.TRX_GOODS_ALL.equals(viewType)) {
			sb.append(" where tg.trx_status<>'");
			sb.append(TrxStatus.INIT);
			sb.append("'");
		}else if (Constant.TRX_GOODS_UNCOMMENT.equals(viewType)) {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.USED);
			sb.append("'");
		}else 	if (Constant.TRX_GOODS_UNUSEED.equals(viewType)) {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.SUCCESS);
			sb.append("'");
		}else {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.SUCCESS);
			sb.append("'");
		}
		sb.append(" and  t.user_id=?");
		sb.append(" group by tg.trxorder_id,tg.goods_id   order by   tg.id desc,t.id desc  limit ?,?");

		List<Map<String, Object>> resultMapList = this.getSimpleJdbcTemplate().queryForList(sb.toString(), userId, startRow, pageSize);


		return resultMapList;
	}

	/**
	 * 取分组求和后的分页总数
	 */
	@Override
	public int findPageCountByUserId(Long userId, String viewType) {
		if (userId == null) {
			throw new IllegalArgumentException("userId not null");
		}
		Long resultCount = 0L;

		StringBuffer sb = new StringBuffer();

		sb
				.append("select count(DISTINCT  tg.trxorder_id,tg.goods_id)  as count from beiker_trxorder_goods  tg");
		sb.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id ");

		if (Constant.TRX_GOODS_ALL.equals(viewType)) {
			sb.append(" where tg.trx_status<>'");
			sb.append(TrxStatus.INIT);
			sb.append("'");
		}else if (Constant.TRX_GOODS_UNCOMMENT.equals(viewType)) {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.USED);
			sb.append("'");
		}else 	if (Constant.TRX_GOODS_UNUSEED.equals(viewType) ) {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.SUCCESS);
			sb.append("'");
		}else {
			sb.append(" where tg.trx_status='");
			sb.append(TrxStatus.SUCCESS);
			sb.append("'");
		}
		sb.append(" and  t.user_id=?");

		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sb.toString(), userId);

		if (txrOrderCountList != null && txrOrderCountList.size() != 0) {

			resultCount = (Long) txrOrderCountList.get(0).get("count");
		}
		return resultCount.intValue();

	}

	public int findPageCountByUserIdStatus(Long userId, String status) {

		if (userId == null) {
			throw new IllegalArgumentException("userId not null");
		}
		Long resultCount = 0L;

		StringBuffer sb = new StringBuffer();

		sb.append("select count(1)  as count from beiker_trxorder_goods  tg");
		sb
				.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id where  t.user_id=");
		sb.append(userId);
		sb.append(status);

		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sb.toString());

		if (txrOrderCountList != null && txrOrderCountList.size() != 0) {

			resultCount = (Long) txrOrderCountList.get(0).get("count");
		}
		return resultCount.intValue();
	}

	public List<Map<String, Object>> findTrxorderGoodsByUserIdStatus(
			Long userId, int startRow, int pageSize, String status) {

		StringBuffer sb = new StringBuffer();
		sb.append("select tg.goods_id as goodsId,t.id as trxorderId");
		sb.append(" from beiker_trxorder_goods  tg");
		sb
				.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id  where t.user_id=");
		sb.append(userId);
		sb.append(status);
		sb.append(" group by t.id,tg.goods_id order by tg.id desc,t.id desc ");
		sb.append(" limit ?,?");
		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sb.toString(), startRow,
						pageSize);
		return txrOrderCountList;
	}

	public List<Map<String, Object>> findTrxorderGoodsByGoodsIdTrxorderId(
			String trxIdStr, String goodsIdStr) {
		StringBuffer sb = new StringBuffer();
		if (trxIdStr == null) {

			throw new IllegalArgumentException("trxIdStr nou null");
		}
		sb
				.append("select t.user_id as userId,tg.id as trxorderGoodsId,tg.goods_id as goodsId,tg.goods_name as goodsName,tg.pay_price as payPrice,");
		sb
				.append("tg.trx_status as trxStatus,tg.create_date as createDate,tg.order_lose_date as loseDate,tg.trx_goods_sn as trxGoodsSn,v.confirm_date as confirmDate,");
		sb
				.append("tg.trxorder_id as trxorderId,tg.is_send_mer_vou as sendMerVou,tg.last_update_date as lastUpdateDate , v.voucher_code as voucherCode from beiker_trxorder_goods  tg");
		sb
				.append(" left join beiker_trxorder t on  tg.trxorder_id= t.id left join beiker_voucher v on v.voucher_id=tg.voucher_id ");
		sb.append("where t.id=? and tg.goods_id=?");

		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sb.toString(), trxIdStr,
						goodsIdStr);
			
		
		return txrOrderCountList;
		

	}

	/**
	 * 取分组求和后的类型总数
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int findCountByUserId(String idStr, String viewType) {
		if (idStr == null || "".equals(idStr)) {
			throw new IllegalArgumentException("userId not null");
		}
		Long resultCount = 0L;

		String sql = "";

		if (Constant.TRX_GOODS_ALL.equals(viewType)) {
			sql = "select count(DISTINCT(goods_id)) as count from beiker_trxorder_goods where trx_status<>'INIT' and  trxorder_id in ("
					+ idStr + ")  group by  trxorder_id";
		}

		if (Constant.TRX_GOODS_UNUSEED.equals(viewType)) {
			sql = "select count(goods_id) as count from beiker_trxorder_goods where  trx_status='SUCCESS' and trxorder_id in ("
					+ idStr + ")";
		}
		if (Constant.TRX_GOODS_UNCOMMENT.equals(viewType)) {
			sql = "select count(goods_id) as count from beiker_trxorder_goods where   trx_status='USED' and trxorder_id in ("
					+ idStr + ")";
		}

		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sql);

		if (txrOrderCountList != null && txrOrderCountList.size() != 0) {

			for (Map item : txrOrderCountList) {

				resultCount += (Long) item.get("count");

			}
		}
		return resultCount.intValue();

	}
	
	/**
	 * 查询商品订单结算信息
	 */
	public List<Map<String, Object>> querySettleDetailByIds(String idStr) {
		StringBuilder sql = new StringBuilder("");
        sql.append("SELECT tg.id trxOrderGoodsId, tg.trx_goods_sn trxGoodsSn ,vou.confirm_date confirmDate, tg.pay_price orderPrice, tg.divide_price dividePrice, tg.mer_settle_status settleStatus " +
            " FROM beiker_trxorder_goods tg LEFT JOIN beiker_voucher vou ON vou.voucher_id = tg.voucher_id");
        sql.append(" WHERE tg.id in ("+idStr+") ORDER BY vou.confirm_date DESC ");
        List<Map<String, Object>> rsList =  getSimpleJdbcTemplate().queryForList(sql.toString());
        if(rsList == null || rsList.size() == 0) {
            return null;
        }
        return rsList;
	}
	
	@Override
	public List<TrxorderGoods> findListInId(String inIdStr) {
		if (inIdStr == null || "".equals(inIdStr)) {

			throw new IllegalArgumentException("inIdStr is not null");
		}
		// StringBuffer sb=new StringBuffer();
		// sb.append("select id,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,");
		// sb.append("trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,");
		// sb.append("trx_status,auth_status,extend_info,description,mer_settle_status from beiker_trxorder_goods where id in (?)");

		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,");
		sb
				.append("trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id, biz_type,credit_status  from beiker_trxorder_goods where id in (");
		sb.append(inIdStr);
		sb.append(")");

		List<TrxorderGoods> resultLlist = this.getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl());

		if (resultLlist != null && resultLlist.size() > 0) {

			return resultLlist;
		}

		return null;

	}

	@Override
	public List<TrxorderGoods> findByStatusAndDate(TrxStatus trxStatus,
			Date date,boolean isRefund,int start,int daemonLength,ReqChannel channel) {

		if (trxStatus == null || date == null) {

			throw new IllegalArgumentException(
					"trxStatus  and  date is not null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select tg.id,tg.version,tg.goods_id,tg.trxorder_id,tg.auth_date,tg.create_date,tg.source_price,tg.current_price,tg.pay_price,tg.rebate_price,tg.divide_price,");
		sb
				.append("tg.trx_goods_sn,tg.goods_name,tg.guest_id,tg.voucher_id,tg.comment_id,tg.order_lose_abs_date,tg.order_lose_date,tg.trx_rule_id,tg.sub_guest_id,tg.is_refund,tg.is_send_mer_vou,");
		sb
				.append("tg.trx_status,tg.auth_status,tg.extend_info,tg.description,tg.mer_settle_status,tg.is_dis,isadvance,tg.last_update_date,tg.out_goods_id , tg.biz_type,tg.credit_status   from beiker_trxorder_goods tg left  join   beiker_trxorder t  on  tg.trxorder_id=t.id where tg.trx_status=? and tg.order_lose_date<? and tg.is_refund=? and t.req_channel=?  limit ?,?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(),
				EnumUtil.transEnumToString(trxStatus), date,isRefund,EnumUtil.transEnumToString(channel),start,daemonLength);

		if (trxGoodsList != null && trxGoodsList.size() > 0 ) {

			return trxGoodsList;
		}
		return null;

	}
	
	@Override
	public int findByStatusAndDateCount(TrxStatus trxStatus,
			Date date,boolean isRefund,ReqChannel channel) {

		if (trxStatus == null || date == null) {

			throw new IllegalArgumentException(
					"trxStatus  and  date is not null");
		}
		StringBuffer sb = new StringBuffer();
		
		sb.append("select count(1) as count from beiker_trxorder_goods tg left  join   beiker_trxorder t  on  tg.trxorder_id=t.id where tg.trx_status=? and tg.order_lose_date<? and tg.is_refund=? and t.req_channel=? ");
		Long resultCount = 0L;
		
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sb.toString(),trxStatus.name(),date,isRefund,channel.name());

		if (list != null && list.size() > 0) {

				resultCount = (Long) list.get(0).get("count");
			}
			return resultCount.intValue();

	}

	/**
	 * 根据状态，是否发送商家验证码、是否支持退款查询
	 * 
	 * @param trxStatus
	 * @param isSendMerVou
	 * @param isRefund
	 * @return
	 */
	@Override
	public List<TrxorderGoods> findByStatusAndIsMerIsRefund(
			TrxStatus trxStatus, Long isSendMerVou, Long isRefund) {

		if (trxStatus == null) {

			throw new IllegalArgumentException("trxStatus   is not null");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,");
		sb
				.append("trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id, biz_type,credit_status  from beiker_trxorder_goods where trx_status=? and is_send_mer_vou=? and is_refund=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(),
				EnumUtil.transEnumToString(trxStatus), isSendMerVou, isRefund);

		if (trxGoodsList.size() > 0 || trxGoodsList != null) {

			return trxGoodsList;
		}
		return null;

	}

	@Override
	public void updateTrxGoods(TrxorderGoods trxGoods)
			throws StaleObjectStateException {
		if (trxGoods.getId() == null) {
			throw new IllegalArgumentException("trxGoodsId not null");
		} else {
			StringBuffer sb = new StringBuffer();

			sb
					.append("update  beiker_trxorder_goods set trx_goods_sn=?,version=?,goods_name=?,source_price=?,current_price=?,");
			sb
					.append("pay_price=?,rebate_price=?,divide_price=?,trx_status=?,auth_status=?,extend_info=?,");
			sb
					.append("description=?,auth_date=?,trxorder_id=?,goods_id=?,mer_settle_status=?,create_date=?,guest_id=?,");
			sb
					.append("voucher_id=?,comment_id=?,order_lose_abs_date=?,order_lose_date=?,sub_guest_id=?,is_dis=?,last_update_date=?,out_goods_id=? ,credit_status=? where id=? and version=?");
			int result = getSimpleJdbcTemplate().update(sb.toString(),
					trxGoods.getTrxGoodsSn(), trxGoods.getVersion() + 1L,
					trxGoods.getGoodsName(), trxGoods.getSourcePrice(),
					trxGoods.getCurrentPrice(), trxGoods.getPayPrice(),
					trxGoods.getRebatePrice(), trxGoods.getDividePrice(),
					EnumUtil.transEnumToString(trxGoods.getTrxStatus()),
					EnumUtil.transEnumToString(trxGoods.getAuthStatus()),
					trxGoods.getExtend_info(), trxGoods.getDescription(),
					trxGoods.getAuthDate(), trxGoods.getTrxorderId(),
					trxGoods.getGoodsId(),
					EnumUtil.transEnumToString(trxGoods.getMerSettleStatus()),
					trxGoods.getCreateDate(), trxGoods.getGuestId(),
					trxGoods.getVoucherId(), trxGoods.getCommentId(),
					trxGoods.getOrderLoseAbsDate(),
					trxGoods.getOrderLoseDate(), trxGoods.getSubGuestId(),
					trxGoods.isDis(), new Date(),trxGoods.getOutGoodsId(), trxGoods.getCreditStatus(),trxGoods.getId(),
					trxGoods.getVersion()

			);
			if (result == 0) {

				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}

		}

	}

	@Override
	public void updateTrxStatusById(Long id, TrxStatus trxStatus, Long version)
			throws StaleObjectStateException {

		if (id == null || trxStatus == null) {

			throw new IllegalArgumentException("id and trxStatus nou null");
		}
		String uptSql = "update  beiker_trxorder_goods  set trx_status=?,version=?,last_update_date=? where id=? and version=?";
		int result = getSimpleJdbcTemplate().update(uptSql, trxStatus.name(),
				version + 1L, new Date(), id, version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}

	}

	@Override
	public void updateTrxAndAauthStatusById(Long id, TrxStatus trxStatus,
			AuthStatus authStatus, Long voucherId, Long version)
			throws StaleObjectStateException {
		if (id == null || trxStatus == null || authStatus == null) {

			throw new IllegalArgumentException(
					"id and trxStatus and authStatus not null");
		}

		String uptSql = "update  beiker_trxorder_goods  set trx_status=?,version=?, auth_status=?,voucher_id=?,last_update_date=? where id=? and version=?";
		int result = getSimpleJdbcTemplate().update(uptSql, trxStatus.name(),
				version + 1L, authStatus.name(), voucherId, new Date(), id,
				version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	@Override
	public void updateTrxStatusBySn(String trxSn, TrxStatus trxStatus,
			Long version) throws StaleObjectStateException {

		if (trxSn == null || trxStatus == null) {

			throw new IllegalArgumentException("trxSn and trxStatus nou null");
		}
		String uptSql = "update  beiker_trxorder_goods  set trx_status=?,version=?,last_update_date=? where trx_goods_sn=? and version=?";
		int result = getSimpleJdbcTemplate().update(uptSql, trxStatus.name(),
				version + 1L, new Date(), trxSn, version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	/**
	 * 查询该交易限制下单个用户对应的抽奖活动的订单数量（该SQL执行次数极少，故联表）
	 * 
	 * @param UId
	 * @param extendInfo
	 * @param trxRuleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long findCountByUIdAndLottery(Long UId, String extendInfo,
			Long trxRuleId) {
		String sql = "select count(1) as count from beiker_trxorder_goods  tg left join beiker_trxorder t on  tg.trxorder_id=t.id where t.user_id=? and tg.extend_info=? and tg.trx_rule_id=?";
		List<Map<String, Object>> txrOrderCountList = this
				.getSimpleJdbcTemplate().queryForList(sql, UId, extendInfo,
						trxRuleId);

		if (txrOrderCountList != null && txrOrderCountList.size() > 0) {

			Map valueMap = txrOrderCountList.get(0);
			Long singleCount = (Long) valueMap.get("count");
			return singleCount;

		}
		return 0L;

	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<TrxorderGoods> {

		@Override
		public TrxorderGoods mapRow(ResultSet rs, int num) throws SQLException {
			TrxorderGoods trxorderGoods = new TrxorderGoods();

			trxorderGoods.setAuthDate(rs.getTimestamp("auth_date"));
			trxorderGoods.setVersion(rs.getLong("version"));
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
			trxorderGoods.setSubGuestId(rs.getLong("sub_guest_id"));// 新增分店ID
			trxorderGoods.setRefund(rs.getBoolean("is_refund"));// 是否支持退款
			trxorderGoods.setSendMerVou(rs.getInt("is_send_mer_vou"));// 是否发送上家自有凭证码
			trxorderGoods.setDis(rs.getBoolean("is_dis"));// 是否返现
			trxorderGoods.setIsadvance(rs.getBoolean("isadvance"));// 是否预付款
			trxorderGoods.setLastUpdateDate(rs.getTimestamp("last_update_date"));
			trxorderGoods.setOutGoodsId(rs.getString("out_goods_id"));
			trxorderGoods.setBizType(rs.getInt("biz_type"));
			trxorderGoods.setCreditStatus(rs.getString("credit_status"));
			return trxorderGoods;

		}
	}

	/**
	 * 根据表后缀随机匹配查询订单号
	 * 
	 * @return
	 */
	@Override
	public Map<String, String> findTrxGoodsSn(String snTbNameInt) {
		if (snTbNameInt == null) {
			throw new IllegalArgumentException("snTbNameInt is  not null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select id,sn from beiker_trx_goods_sn_");
		sb.append(snTbNameInt);
		sb.append(" limit 1 for update");
		Map<String, String> resultMap = new HashMap<String, String>();
		Map<String, Object> qryMap = getSimpleJdbcTemplate().queryForMap(
				sb.toString());
		resultMap.put("id", qryMap.get("id") + "");
		resultMap.put("sn", qryMap.get("sn").toString());
		if (qryMap == null || qryMap.isEmpty()) {
			return null;
		}
		return resultMap;

	}
	/**
	 * 根据表后缀随机匹配查询订单号(带偏移量)
	 * @param snTbNameInt
	 * @param ofset
	 * @return
	 */
	@Override
	public List<Map<String, Object>> findTrxGoodsSnForOfset(String snTbNameInt,int ofset){
		if (snTbNameInt == null || snTbNameInt.length()==0) {
			throw new IllegalArgumentException("snTbNameInt is  not null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select id,sn from beiker_trx_goods_sn_");
		sb.append(snTbNameInt);
		sb.append(" limit 0,");
		sb.append(ofset);
		sb.append(" for update");
		List<Map<String, Object>>  list = this.getSimpleJdbcTemplate().queryForList(sb.toString());
		
	
		return list;

	}
	/**
	 * 根据表后缀及订单号进行删除
	 * 
	 * @return
	 */
	@Override
	public void delTrxGoodsSn(String snTbNameInt, int trxGoodsId) {
		if (snTbNameInt == null) {
			throw new IllegalArgumentException(
					"snTbNameInt  and trxGoodsId is  not null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("delete from beiker_trx_goods_sn_");
		sb.append(snTbNameInt);
		sb.append(" where id=");
		sb.append(trxGoodsId);

		getSimpleJdbcTemplate().update(sb.toString());
	}
	
	/**
	 * 根据表后缀及订单号进行删除
	 * 
	 * @return
	 */
	@Override
	public void delTrxGoodsSnByIds(String snTbNameInt, String idS) {
		if (snTbNameInt == null) {
			throw new IllegalArgumentException(
					"snTbNameInt  and trxGoodsId is  not null");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("delete from beiker_trx_goods_sn_");
		sb.append(snTbNameInt);
		sb.append(" where id in(");
		sb.append(idS);
		sb.append(")");

		getSimpleJdbcTemplate().update(sb.toString());
	}

	/**
	 * 增加乐观锁后取消for update
	 */
	/*
	 * public TrxorderGoods findByIdForUpdate(Long id) {
	 * 
	 * if (id == null) {
	 * 
	 * throw new IllegalArgumentException("id nou null"); } StringBuffer sb =
	 * new StringBuffer(); sb.append(
	 * "select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,"
	 * ); sb.append(
	 * "divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,"
	 * ); sb.append(
	 * "trx_status,auth_status,extend_info,description,mer_settle_status from beiker_trxorder_goods where id=?  for update"
	 * ); List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
	 * sb.toString(), new RowMapperImpl(), id);
	 * 
	 * if (trxGoodsList.size() > 0) {
	 * 
	 * return trxGoodsList.get(0); } return null;
	 * 
	 * }
	 */
	
	/**
	 * 根据tgId更新tg TRX_STATUS状态
	 * @param idStrs
	 * 8:02:03 PM
	 * janwen
	 *
	 */
	public int updateTrxStatusByIds(String idStr,String trxStatusFinal,String trxStatusPro,Long evaluationid){
		if(idStr==null || trxStatusFinal==null || trxStatusPro==null){
			throw new IllegalArgumentException("idStr and  trxStatusFinal and trxStatusPro is not null!");
			
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update  beiker_trxorder_goods  set trx_status=?,version=version+1,comment_id=? where trx_status=? and id in (");
		sb.append(idStr);
		sb.append(")");

		int result=this.getSimpleJdbcTemplate().update(sb.toString(), trxStatusFinal,evaluationid,trxStatusPro);
		return result;
		
	}

	@Override
	public List<TrxorderGoods> findByDis(int isDis) {

		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,divide_price,");
		sb
				.append("trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date,out_goods_id, biz_type,credit_status  from beiker_trxorder_goods where trx_status in('USED','COMMENTED') and is_dis=?");
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl(), isDis);

		if (trxGoodsList != null && trxGoodsList.size() > 0) {

			return trxGoodsList;
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List findSnByTrxId(Long trxId) {

		String query_goods = "SELECT btg.trx_goods_sn FROM beiker_trxorder_goods  btg WHERE trxorder_id = ?";
		List sn = this.getJdbcTemplate().queryForList(query_goods,
				new Object[] { trxId });
		return sn;
	}

	@Override
	public List<Map<String, Object>> findLoseDate(Date loseToNow10Date,
			Date loseToNow3Date) {
		Date lose10DateStart = DateUtils.getMinTime(loseToNow10Date);
		Date lose10DateEnd = DateUtils.getMaxTime(loseToNow10Date);

		Date lose3DateStart = DateUtils.getMinTime(loseToNow3Date);
		Date lose3DateEnd = DateUtils.getMaxTime(loseToNow3Date);

		String sql = "select t.user_id as userId,g.id as id,g.trxorder_id as trxorderId,g.goods_id as goodsId,g.order_lose_date as orderLoseDate from beiker_trxorder_goods g left join beiker_trxorder t on g.trxorder_id=t.id  where  g.trx_status='SUCCESS' and g.pay_price>=1 and g.order_lose_date between ? and ?  group by g.goods_id,g.order_lose_date,t.user_id  union all"
				+ " select t.user_id as userId,g.id as id,g.trxorder_id as trxorderId,g.goods_id as goodsId,g.order_lose_date as orderLoseDate from beiker_trxorder_goods g left join beiker_trxorder t on g.trxorder_id=t.id  where  g.trx_status='SUCCESS' and g.pay_price>=1 and g.order_lose_date between ? and ?  group by g.goods_id,g.order_lose_date,t.user_id";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, lose10DateStart, lose10DateEnd,
						lose3DateStart, lose3DateEnd);

		return list;
	}
	
	
	/**
	 * 查询出过期订单
	 * @param loseToNow3Date
	 * @return
	 */
	public List<Map<String, Object>> findLoseNowDate(Date loseToNowDate,int startCount,int endCount) {

		Date loseDateStart = DateUtils.getMinTime(loseToNowDate);
		Date loseDateEnd = DateUtils.getMaxTime(loseToNowDate);

		String sql =" select t.user_id as userId,g.id as id,g.trxorder_id as trxorderId,g.goods_id as goodsId,count(g.goods_id) count,g.order_lose_date as orderLoseDate from beiker_trxorder_goods g left join beiker_trxorder t on g.trxorder_id=t.id  where  g.trx_status='SUCCESS' and g.pay_price>=1 and g.order_lose_date between ? and ?  group by t.user_id,g.goods_id,g.order_lose_date limit ?,?";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql,loseDateStart, loseDateEnd,startCount,endCount);

		return list;
	}
	
	/**
	 * 查询出过期订单  总数量
	 * @param loseToNowDate
	 * @return
	 */
	public int findLoseCountDate(Date loseToNowDate) {

		Date loseDateStart = DateUtils.getMinTime(loseToNowDate);
		Date loseDateEnd = DateUtils.getMaxTime(loseToNowDate);

		Long resultCount = 0L;
		
		String sql =" select count(distinct  g.goods_id,g.order_lose_date,t.user_id) as count from beiker_trxorder_goods g left join beiker_trxorder t on g.trxorder_id=t.id  where  g.trx_status='SUCCESS' and g.pay_price>=1 and g.order_lose_date between ? and ? ";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql,loseDateStart, loseDateEnd);

		if (list != null && list.size() > 0) {

				resultCount = (Long) list.get(0).get("count");
			}
			return resultCount.intValue();
	}
	
	public List<Map<String,Object>> findByTrxOrderId(Long trxOrderId){
		
		String sql = "select v.voucher_id as ticketId,tg.trx_goods_sn as ticketCode,tg.id as trxGoodsId,v.voucher_code as ticketPass,v.active_date as createDate,tg.order_lose_date as lostDate  from beiker_trxorder_goods tg left join beiker_voucher v on tg.voucher_id=v.voucher_id where tg.trxorder_id=?";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql,trxOrderId);
		
		return list;
	}
	
	@Override
	public Map<String,Object> findByTrxorderIdAndSn(Long trxorderId, String trxGoodsSn){
		StringBuilder sql = new StringBuilder();
		sql.append("select vo.voucher_id as ticketId,tg.trx_goods_sn as ticketCode,v.voucher_code as ticketPass, tg.order_lose_date as endtime,vo.active_date as createDate,vo.voucher_status as status,tg.trx_status as trxStatus");
		sql.append(" from beiker_trxorder_goods tg left join beiker_voucher vo on tg.voucher_id = vo.voucher_id");
		sql.append(" where tg.trxorder_id = ? and tg.trx_goods_sn = ?");
		Map<String, Object> map = getSimpleJdbcTemplate().queryForMap(sql.toString(), trxorderId,trxGoodsSn);
		return map;
	}
	
	@Override
	public Map<String,Object> findByTrxorderIdAndVouId(Long trxorderId, Long voucherId){
		StringBuilder sql = new StringBuilder();
		sql.append("select vo.voucher_id as ticketId,tg.trx_goods_sn as ticketCode,v.voucher_code as ticketPass, tg.order_lose_date as endtime,vo.active_date as createDate,vo.voucher_status as status,tg.trx_status as trxStatus");
		sql.append(" from beiker_trxorder_goods tg left join beiker_voucher vo on tg.voucher_id = vo.voucher_id");
		sql.append(" where tg.trxorder_id = ? and vo.voucher_id = ?");
		Map<String, Object> map = getSimpleJdbcTemplate().queryForMap(sql.toString(), trxorderId,voucherId);
		return map;
	}
	
	/**
	 * 根据更新时间和状态查询凭证及订单信息
	 */
	@Override
	public List<Map<String, Object>> findByLastUpdateDateAndStatus(Date startTime, Date endTime,String userIdStr,String trxStatusStr) {
		if(startTime==null || endTime==null || userIdStr==null  || userIdStr.length()==0 || trxStatusStr==null || trxStatusStr.length()==0){
			throw new IllegalArgumentException("startTime and  endTime and userIdStr is not null!");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select t.out_request_id as outRequestId, t.external_id as externalId,t.id as trxorderId,vo.voucher_id as voucherId,tg.trx_status as trxStatus, tg.goods_id as goodsId, tg.pay_price as payPrice, tg.trx_goods_sn as trxGoodsSn, tg.order_lose_date as orderLoseDate,vo.voucher_code as voucherCode,vo.active_date as activeDate,tg.out_goods_id as outGoodsId");
		sql.append(" from beiker_trxorder_goods tg inner join beiker_voucher vo on tg.voucher_id=vo.voucher_id left join beiker_trxorder t on tg.trxorder_id = t.id");
		sql.append(" where t.user_id in (");
		sql.append(userIdStr);
		sql.append(") and tg.trx_status in(");
		sql.append(trxStatusStr);
		sql.append(") and tg.last_update_date between ? and ?");
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql.toString(), startTime,endTime);
		return list;
	}

	/**
	 * 新增多个券查询接口
	 * 赵飞龙
	 */
	@Override
	public List<TrxorderGoods> findTgByVoucherId(String voucherIdStr) {
		if(voucherIdStr.length()==0 || voucherIdStr==null){
			throw new IllegalArgumentException("voucherId is not null!");
		}
		StringBuffer sb = new StringBuffer();
		sb
				.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
		sb
				.append("divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,out_goods_id,");
		sb
				.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date, biz_type,credit_status  from beiker_trxorder_goods where voucher_id in(");
		sb
		        .append(voucherIdStr);
		sb      .append(")");
		System.out.println(sb);
		List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
				sb.toString(), new RowMapperImpl());

		if (trxGoodsList.size() > 0) {

			return trxGoodsList;
		}
		return null;
	}
	/**
     * 根据更新时间和状态查询商品订单
     * @param startTime
     * @param endTime
     * @param trxStatus
     * @return
     */
    @Override
    public List<TrxorderGoods> findTrxGoodsByUpDateTimeAndStatus(String date, TrxStatus trxStatus) {
        StringBuilder builder = new StringBuilder();
        builder.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
        builder.append("divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,out_goods_id,");
        builder.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date, biz_type,credit_status  from beiker_trxorder_goods where ");
        builder.append(" last_update_date<=? ");
        builder.append(" and trx_status = ? ");
        List<TrxorderGoods> list= getSimpleJdbcTemplate().query(builder.toString(), new RowMapperImpl(), date,trxStatus.name());
       
        return list;
    }
	
    /**
     * 根据trxOrderId查询券有关信息
     * add by feilong.zhao
     */
	@Override
	public List<Map<String, Object>> findVoucherInfoByTrxOrderId(Long trxOrderId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select tg.trx_status as trxStatus,v.voucher_code as voucherCode,tg.auth_date as authDate,tg.last_update_date as lastUpdateDate,v.create_date as createDate,tg.order_lose_date as orderLoseDate from beiker_trxorder_goods as tg left join beiker_voucher as v on tg.voucher_id=v.voucher_id where tg.trx_status <> 'INIT' and tg.trxorder_id=?");
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql.toString(),trxOrderId);
		return list;
	}
	
	/**
     * 查询商品订单信息
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     * 分页参数 startRow offSet
     */
	@Override
	public List<Map<String, Object>> queryTrxOrderGoodsForGuest(Map<String, String> condition, int startRow, int pageSize) {
		StringBuilder sql = new StringBuilder("");
        sql.append("SELECT tg.id trxOrderGoodsId, tg.trx_goods_sn trxGoodsSn, tg.goods_name goodsName,tg.goods_id goodsId,tg.create_date buyDate," +
        	" vou.confirm_date usedDate,tg.voucher_id voucherId, tg.pay_price payPrice, " +
        	" tg.guest_id guestId,tg.sub_guest_id subGuestId, tg.is_freeze isFreeze,vou.voucher_code voucherCode," +
            " tg.trx_status trxStatus,tg.trxorder_id trxOrderId,tg.biz_type bizType " +
            " FROM beiker_trxorder_goods tg LEFT JOIN beiker_voucher vou ON vou.voucher_id = tg.voucher_id");
        sql.append(" WHERE tg.trx_status <> 'INIT' ");
	       
	    List<Object> paramList = new ArrayList<Object>();
	    //商家编号(必须)
	    if(StringUtils.validNull(condition.get("guestId"))) {
            sql.append(" AND tg.guest_id = ?");
            paramList.add(condition.get("guestId"));
	    }
	    //服务密码
        if(StringUtils.validNull(condition.get("voucherCode"))) {
        	 sql.append(" AND vou.voucher_code = ?");
             paramList.add(condition.get("voucherCode"));
        }
	    //购买时间
	    if(StringUtils.validNull(condition.get("buyStartDate")) && StringUtils.validNull(condition.get("buyEndDate"))) {
            sql.append(" AND tg.create_date BETWEEN ? AND ? ");
            paramList.add(condition.get("buyStartDate"));
            paramList.add(condition.get("buyEndDate"));
        }
	    //消费时间
	    if(StringUtils.validNull(condition.get("usedStartDate")) && StringUtils.validNull(condition.get("usedEndDate"))) {
            sql.append(" AND t5.confirm_date BETWEEN ? AND ?");
            paramList.add(condition.get("usedStartDate") );
            paramList.add(condition.get("usedEndDate"));
        }
	    //购买商品ID
	    if(StringUtils.validNull(condition.get("goodsId"))) {
            sql.append(" AND tg.goods_id = ?");
            paramList.add(condition.get("goodsId"));
        }
	    //订单编号
        if(StringUtils.validNull(condition.get("trxGoodsSn"))) {
            sql.append(" AND tg.trx_goods_sn = ?");
            paramList.add(condition.get("trxGoodsSn"));
        }
        //分店编号
	    if(StringUtils.validNull(condition.get("subGuestId"))) {
            sql.append(" AND tg.sub_guest_id = ?");
            paramList.add(condition.get("subGuestId"));
        }
	    //订单状态
	    if(StringUtils.validNull(condition.get("trxStatus"))) {
            sql.append(" AND tg.trx_status in ( "+condition.get("trxStatus")+" )");
        }
	    sql.append(" ORDER BY tg.create_date DESC limit ?,? ");
        paramList.add(startRow);
        paramList.add(pageSize);
        List<Map<String, Object>> rsList =  getSimpleJdbcTemplate().queryForList(sql.toString(),paramList.toArray(new Object[]{}));
        if(rsList == null || rsList.size() == 0) {
            return null;
        }
        
	    return rsList;
    }
	
	/**
     * 查询商品订单信息数量
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * trxStatus订单状态
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     */
	@Override
	public Map<String,Long> queryTrxOrderGoodsForGuestCountGroupByTrxStatus(Map<String, String> condition) {
		StringBuilder sql = new StringBuilder("");
        sql.append("SELECT tg.trx_status trxStatus, count(1) num FROM beiker_trxorder_goods tg LEFT JOIN beiker_voucher vou ON vou.voucher_id = tg.voucher_id");
        sql.append(" WHERE tg.trx_status <> 'INIT' ");
	       
	    List<Object> paramList = new ArrayList<Object>();
	    //商家编号(必须)
	    if(StringUtils.validNull(condition.get("guestId"))) {
            sql.append(" AND tg.guest_id = ?");
            paramList.add(condition.get("guestId"));
        }
	    //分店编号
	    if(StringUtils.validNull(condition.get("subGuestId"))) {
            sql.append(" AND tg.sub_guest_id = ?");
            paramList.add(condition.get("subGuestId"));
        }
	    //服务密码
        if(StringUtils.validNull(condition.get("voucherCode"))) {
        	 sql.append(" AND vou.voucher_code = ?");
             paramList.add(condition.get("voucherCode"));
        }
	    //购买时间
	    if(StringUtils.validNull(condition.get("buyStartDate")) && StringUtils.validNull(condition.get("buyEndDate"))) {
            sql.append(" AND tg.create_date BETWEEN ? AND ? ");
            paramList.add(condition.get("buyStartDate"));
            paramList.add(condition.get("buyEndDate"));
        }
	    //消费时间
	    if(StringUtils.validNull(condition.get("usedStartDate")) && StringUtils.validNull(condition.get("usedEndDate"))) {
            sql.append(" AND t5.confirm_date BETWEEN ? AND ?");
            paramList.add(condition.get("usedStartDate") );
            paramList.add(condition.get("usedEndDate"));
        }
	    //购买商品ID
	    if(StringUtils.validNull(condition.get("goodsId"))) {
            sql.append(" AND tg.goods_id = ?");
            paramList.add(condition.get("goodsId"));
        }
	    //订单编号
        if(StringUtils.validNull(condition.get("trxGoodsSn"))) {
            sql.append(" AND tg.trx_goods_sn = ?");
            paramList.add(condition.get("trxGoodsSn"));
        }
        //分店编号
	    if(StringUtils.validNull(condition.get("subGuestId"))) {
            sql.append(" AND tg.sub_guest_id = ?");
            paramList.add(condition.get("subGuestId"));
        }
	    
	    sql.append(" GROUP BY tg.trx_status ");
        List<Map<String, Object>> rsList =  getSimpleJdbcTemplate().queryForList(sql.toString(),paramList.toArray(new Object[]{}));
        if(rsList == null || rsList.isEmpty()) {
            return null;
        }
        Map<String,Long> countMap = new HashMap<String, Long>();
        for(Map<String,Object> count : rsList){
        	countMap.put((String) count.get("trxStatus"), (Long)count.get("num"));
        }
        return countMap;
    }
	

	
	/**
     * 查询未入账的商品订单
     * @return
     */
    public List<TrxorderGoods> getTrxOrderGoodsByCreditStatus(String startDate,String endDate,CreditStatus creditStatus){
        
        StringBuffer sb = new StringBuffer();
        sb.append("select id,version,goods_id,trxorder_id,auth_date,create_date,source_price,current_price,pay_price,rebate_price,trx_rule_id,sub_guest_id,is_refund,is_send_mer_vou,");
        sb.append("divide_price,trx_goods_sn,goods_name,guest_id,voucher_id,comment_id,order_lose_abs_date,order_lose_date,out_goods_id,");
        sb.append("trx_status,auth_status,extend_info,description,mer_settle_status,is_dis,isadvance,last_update_date, biz_type ,credit_status from beiker_trxorder_goods where last_update_date>=? and last_update_date<=? and credit_status=? and guest_id>=1000000 and guest_id<=9999999 ");
        List<TrxorderGoods> trxGoodsList = getSimpleJdbcTemplate().query(
                sb.toString(), new RowMapperImpl(), startDate,endDate,creditStatus.toString());
        return trxGoodsList;
        
    }
    /**
     * 查询商家购买数量
     */
    @Override
    public List<Map<String, Object>> queryTrxGoodsBuyCountForGuest(Map<String, String> map) {
        String sql="SELECT biz_type as bizType,COUNT(id) AS count FROM beiker_trxorder_goods WHERE " +
        		" guest_id=? and create_date between ? and ? "+
                 " AND trx_status!=?  GROUP BY biz_type";
        return getSimpleJdbcTemplate().queryForList(sql, map.get("guestIdForSales"),map.get("startDate"),map.get("endDate"),
                map.get("trxStatus"));
    }
    /**
     * 查询商家消费数量
     */
    @Override
    public List<Map<String, Object>> queryTrxGoodsUsedCountForGuest(Map<String, String> condition) {
        StringBuffer sql= new StringBuffer();
        List<Object> paramList = new ArrayList<Object>();
        sql.append("SELECT biz_type AS bizType,COUNT(id) AS count FROM  beiker_trxorder_goods WHERE ");
        String tempTrxGoodsStr="";
        if(StringUtils.validNull(condition.get("subGuestIdForUsed"))) {
            if(condition.get("subGuestIdForUsed").contains(",")){
                String [] trxGoodsIds =condition.get("subGuestIdForUsed").split(",");
                sql.append(" sub_guest_id in( ");
                tempTrxGoodsStr="";
                for(String trxGoodsIdStr: trxGoodsIds){
                    tempTrxGoodsStr+=" ?,";
                    paramList.add(trxGoodsIdStr);
                }
                tempTrxGoodsStr=tempTrxGoodsStr.substring(0, tempTrxGoodsStr.lastIndexOf(","));
                sql.append(tempTrxGoodsStr).append(" ) ");
            }else{
                sql.append(" sub_guest_id = ?");
                paramList.add(condition.get("subGuestIdForUsed"));
            }
        }
        sql.append(" AND last_update_date BETWEEN ? AND ? ");
        
        paramList.add(condition.get("startDate"));
        paramList.add(condition.get("endDate"));
        
        if(StringUtils.validNull(condition.get("trxStatus"))) {
            if(condition.get("trxStatus").contains(",")){
                String [] trxGoodsIds =condition.get("trxStatus").split(",");
                sql.append(" AND trx_status in( ");
                tempTrxGoodsStr="";
                for(String trxGoodsIdStr: trxGoodsIds){
                    tempTrxGoodsStr+=" ?,";
                    paramList.add(trxGoodsIdStr);
                }
                tempTrxGoodsStr=tempTrxGoodsStr.substring(0, tempTrxGoodsStr.lastIndexOf(","));
                sql.append(tempTrxGoodsStr).append(" ) ");
            }else{
                sql.append(" AND trx_status = ?");
                paramList.add(condition.get("trxStatus"));
            }
        }
        
        sql.append(" GROUP BY biz_type ");
        List<Map<String, Object>> rsList =  getSimpleJdbcTemplate().queryForList(sql.toString(),paramList.toArray(new Object[]{}));
        if(rsList == null || rsList.size() == 0) {
            return null;
        }
        return rsList;
    }
    
  
}
