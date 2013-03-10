package com.beike.dao.trx.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**
 * @Title: TrxOrderDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: 交易订单DAO
 * @date May 16, 2011 11:50:14 AM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("trxOrderDao")
public class TrxOrderDaoImpl extends GenericDaoImpl<TrxOrder, Long> implements TrxOrderDao {

	/**
	 * 插入订单
	 */
	public void addTrxOrder(TrxOrder trxOrder) {
		if (trxOrder == null) {
			throw new IllegalArgumentException("TrxOrder  not null");
		} else {
			String insertSql = "insert beiker_trxorder" + " (request_id,external_id,order_type,trx_status,close_date,user_id,extend_info,create_date,ord_amount,description,out_request_id,mobile,req_channel,req_ip) " + " value (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			getSimpleJdbcTemplate().update(insertSql, // 插入sql
					trxOrder.getRequestId(), // 交易请求号(对用户)
					trxOrder.getExternalId(), // 交易流水号（对平台）
					EnumUtil.transEnumToString(trxOrder.getOrderType()), // 订单类型
					EnumUtil.transEnumToString(trxOrder.getTrxStatus()), // 交易装态
					trxOrder.getCloseDate(), trxOrder.getUserId(), // 用户id
					trxOrder.getExtendInfo(), trxOrder.getCreateDate(), // 创建日期
					trxOrder.getOrdAmount(), trxOrder.getDescription(), // 订单金额
					trxOrder.getOutRequestId(), trxOrder.getMobile(), // 手机号
					EnumUtil.transEnumToString(trxOrder.getReqChannel()),// 请求渠道
					trxOrder.getReqIp()); // 客户端IP
		}

	}

	/**
	 * 根据请求号和订单状态，查询订单
	 */
	public TrxOrder findByExIdAndStatus(String externalId, TrxStatus trxStatus) {
		if (externalId == null || trxStatus == null) {
			throw new IllegalArgumentException("externalId or trxStatus not null");
		}

		String querySql = "select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where external_id=? and trx_status=? ";
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(querySql, new RowMapperImpl(), externalId, trxStatus.name());

		if (trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}

		return null;
	}

	/**
	 * 根据请求号，查询订单
	 */
	public TrxOrder findByExternalId(String externalId) {
		if (externalId == null || externalId.length() == 0) {
			throw new IllegalArgumentException("externalId  not null");
		}

		String querySql = "select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where external_id=? ";
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(querySql, new RowMapperImpl(), externalId);

		if (trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}

		return null;
	}

	/**
	 * 根据ID查询订单
	 */
	public TrxOrder findById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("id  not null");
		}

		String querySql = "select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where id=? ";
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(querySql, new RowMapperImpl(), id);

		if (trxOrderList != null && trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}

		return null;
	}

	/**
	 * 根据外部请求号，查询订单
	 */
	public TrxOrder findByRequestId(String requestId) {

		if (requestId == null) {
			throw new IllegalArgumentException("requestId  not null");
		}
		String querySql = "select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where request_id=? ";
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(querySql, new RowMapperImpl(), requestId);

		if (trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}
		return null;
	}

	public TrxOrder findByUserIdOutRequestId(String outRequestId, List<Long> userIdList) {

		if (outRequestId == null || "".equals(outRequestId) || userIdList == null || userIdList.size() == 0) {
			throw new IllegalArgumentException("outRequestId  and userIdList  not null");
		}

		StringBuilder userIds = new StringBuilder();
		StringBuilder selectSql = new StringBuilder();
		for (Long userId : userIdList) {
			userIds.append(userId);
			userIds.append(",");
		}

		if (userIds.toString().length() > 0) {
			userIds.deleteCharAt(userIds.length() - 1);
		}

		selectSql.append("select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where ");
		selectSql.append(" user_id in (");
		selectSql.append(userIds.toString());
		selectSql.append(") and out_request_id=?");
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(selectSql.toString(), new RowMapperImpl(), outRequestId);

		if (trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}

		return null;
	}

	public TrxOrder findByUserId(List<Long> userIdList) {

		if (userIdList == null || userIdList.size() == 0) {
			throw new IllegalArgumentException(" userIdList  not null");
		}

		StringBuilder userIds = new StringBuilder();
		StringBuilder selectSql = new StringBuilder();
		for (Long userId : userIdList) {
			userIds.append(userId);
			userIds.append(",");
		}

		if (userIds.toString().length() > 0) {
			userIds.deleteCharAt(userIds.length() - 1);
		}

		selectSql.append("select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id,trx_status,order_type,extend_info,description,out_request_id,mobile,req_channel from  beiker_trxorder where ");
		selectSql.append(" user_id in (");
		selectSql.append(userIds.toString());
		selectSql.append(")");
		List<TrxOrder> trxOrderList = getSimpleJdbcTemplate().query(selectSql.toString(), new RowMapperImpl());

		if (trxOrderList.size() > 0) {
			return trxOrderList.get(0);
		}

		return null;
	}

	public void updateStatusByExId(String externalId, TrxStatus trxStatus, Date closeDate, Long version) throws StaleObjectStateException {

		if (externalId == null || trxStatus == null) {
			throw new IllegalArgumentException("externalId or  trxStatuss  not null");
		}

		String updateSql = "update  beiker_trxorder set trx_status=?,version=?,close_date=? where external_id=? and version=?";

		int result = getSimpleJdbcTemplate().update(updateSql, trxStatus.name(), version + 1L, externalId, version);

		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	public void updateStatusById(Long id, TrxStatus trxStatus, Date closeDate, Long version) throws StaleObjectStateException {

		if (id == null || trxStatus == null) {
			throw new IllegalArgumentException("id or  trxStatuss  not null");
		}
		String updateSql = "update  beiker_trxorder set trx_status=?,version=?,close_date=? where id=? and version=?";

		int result = getSimpleJdbcTemplate().update(updateSql, trxStatus.name(), version + 1L, closeDate, id, version);

		if (result == 0) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	public void updateTrxOrder(TrxOrder trxOrder) throws StaleObjectStateException {

		if (trxOrder == null) {
			throw new IllegalArgumentException("trxOrder not null");
		}
		String updateSql = "update beiker_accounhistory set request_id=?,version=?,external_id=?," + "order_type=?,trx_status=?,close_date=?,user_id=?,extend_info=?,create_date=? ,ord_amount=?,description=?," + "out_request_id=?,mobile=?,req_channel=? where id=? and version=?";

		int result = getSimpleJdbcTemplate().update(updateSql, trxOrder.getRequestId(), trxOrder.getVersion() + 1L, trxOrder.getExternalId(), EnumUtil.transEnumToString(trxOrder.getOrderType()), EnumUtil.transEnumToString(trxOrder.getTrxStatus()), trxOrder.getCloseDate(), trxOrder.getUserId(),
				trxOrder.getExtendInfo(), trxOrder.getCreateDate(), trxOrder.getOrdAmount(), trxOrder.getDescription(), trxOrder.getId(), trxOrder.getVersion(), trxOrder.getOutRequestId(), trxOrder.getMobile(), EnumUtil.transEnumToString(trxOrder.getReqChannel()));

		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<TrxOrder> {

		public TrxOrder mapRow(ResultSet rs, int num) throws SQLException {

			TrxOrder trxOrder = new TrxOrder();
			trxOrder.setId(rs.getLong("id"));
			trxOrder.setVersion(rs.getLong("version"));
			trxOrder.setCreateDate(rs.getTimestamp("create_date"));
			trxOrder.setCloseDate(rs.getTimestamp("close_date"));
			trxOrder.setDescription(rs.getString("description"));
			trxOrder.setOrdAmount(rs.getDouble("ord_amount"));
			trxOrder.setOrderType(EnumUtil.transStringToEnum(OrderType.class, rs.getString("order_type")));
			trxOrder.setRequestId(rs.getString("request_id"));
			trxOrder.setUserId(rs.getLong("user_id"));
			trxOrder.setExtendInfo(rs.getString("extend_info"));
			trxOrder.setExternalId(rs.getString("external_id"));
			trxOrder.setTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class, rs.getString("trx_status")));
			trxOrder.setOutRequestId(rs.getString("out_request_id"));
			trxOrder.setMobile(rs.getString("mobile"));
			trxOrder.setReqChannel(EnumUtil.transStringToEnum(ReqChannel.class, rs.getString("req_channel")));

			return trxOrder;
		}
	}

	public void updateMobileById(Long trxorderId, String mobile) throws StaleObjectStateException {

		if (trxorderId == null || mobile == null) {
			throw new IllegalArgumentException("  trxorderId  not null");
		}

		String updateSql = "update beiker_trxorder set mobile=? where id=? ";

		int result = getSimpleJdbcTemplate().update(updateSql, mobile, trxorderId);

		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	// @Override
	// public Map<String, Object> findtrxOrderById(Long id) {
	// if (id == null) {
	// throw new IllegalArgumentException("id  not null");
	// }
	// String sql =
	// "select id,version,user_id,create_date,close_date,ord_amount,request_id,external_id as orderIdThirdpart,trx_status as status,order_type,extend_info,description,out_request_id as orderId,mobile from  beiker_trxorder where id=? ";
	// Map<String, Object> map =
	// getSimpleJdbcTemplate().queryForMap(sql.toString(), id);
	// return map;
	// }
	/**
     * 查询商品订单列表 分页 
     *@param condition
     *@param startRow 
     *@param pageSize
     *startRow和pageSize都为0时不分页 查询所有的
     *@return List<Map<String, Object>> 
     */
    @Override
    public List<Map<String, Object>> queryTrxGoodsIds(Map<String, String> condition, Boolean isHistory) {
        StringBuilder sql = new StringBuilder("");
        String beiker_trxorder_goods=" beiker_trxorder_goods ";
        String beiker_trxorder =" beiker_trxorder ";
        String beiker_voucher =" beiker_voucher ";
        if(isHistory!=null && isHistory){//查询历史数据表名切换,备用
             beiker_trxorder_goods=" beiker_trxorder_goods ";
             beiker_trxorder =" beiker_trxorder ";
             beiker_voucher =" beiker_voucher ";
        }
	
	
        sql.append("SELECT t1.id trxGoodsId, t1.trx_goods_sn trxGoodsSn, t1.goods_name goodsName, t1.voucher_id voucherId, t1.pay_price payPrice, " +
                        " t1.trx_status trxStatus, t1.goods_id goodsId, t1.trxorder_id trxOrderId," +
                        " t1.guest_id guestId, t1.is_send_mer_vou isSendMerVou,t1.is_freeze isFreeze,t3.user_id userId,t4.email email ,t4.mobile, " +
                        " t3.out_request_id outRequestId ,t3.create_date createDate, t3.mobile  trxOrderMobile,t5.confirm_date  confirmDate, " +
                        " t1.divide_price dividePrice, t1.mer_settle_status merSettleStatus " +
                        
                        " FROM ").append(beiker_trxorder_goods).append(" t1 ");
        //不按城市查询无需关联beiker_goods
        if(StringUtils.validNull(condition.get("city"))) {
            sql.append(" left join beiker_goods t2 on t1.goods_id=t2.goodsid " );
}
        sql.append(" left join ").append(beiker_trxorder).append(" t3 on t1.trxorder_id=t3.id " +
                        " left join beiker_user t4 on t3.user_id=t4.user_id ");
        //不按消费时间查询时无需关联beiker_voucher
//        if(StringUtils.validNull(condition.get("confirmDateBegin")) && StringUtils.validNull(condition.get("confirmDateEnd"))) {
//            sql.append(" left join ").append(beiker_voucher).append(" t5 on t1.voucher_id = t5.voucher_id  ");
//        }
        sql.append(" left join ").append(beiker_voucher).append(" t5 on t1.voucher_id = t5.voucher_id  ");
        sql.append("WHERE  t1.trxorder_id=t3.id ");
        
        List<Object> paramList = new ArrayList<Object>();
        //trxGoodsIds个多个用,隔开不能为空
        if(StringUtils.validNull(condition.get("trxGoodsIds"))) {
            if(condition.get("trxGoodsIds").contains(",")){
                String [] trxGoodsIds =condition.get("trxGoodsIds").split(",");
                sql.append(" AND t1.id in( ");
                String tempTrxGoodsStr="";
                for(String trxGoodsIdStr: trxGoodsIds){
                    tempTrxGoodsStr+=" ?,";
                    paramList.add(trxGoodsIdStr);
                }
                tempTrxGoodsStr=tempTrxGoodsStr.substring(0, tempTrxGoodsStr.lastIndexOf(","));
                sql.append(tempTrxGoodsStr).append(" ) ");
            }else{
                sql.append(" AND t1.id = ?");
                paramList.add(condition.get("trxGoodsIds"));
            }
        }else{
            return null;
        }
        
        
        if(StringUtils.validNull(condition.get("trxStatus"))) {//trxStatus个多个用,隔开
            if(condition.get("trxStatus").contains(",")){
                String [] trxStatusArr =condition.get("trxStatus").split(",");
                sql.append(" AND t1.trx_status in( ");
                String tempTrxs="";
                for(String trxStatusStr: trxStatusArr){
                    tempTrxs+=" ?,";
                    paramList.add(trxStatusStr);
                }
                tempTrxs=tempTrxs.substring(0, tempTrxs.lastIndexOf(","));
                sql.append(tempTrxs).append(" ) ");
            }else{
                sql.append(" AND t1.trx_status = ?");
                paramList.add(condition.get("trxStatus"));
            }
        }
        
        
        if(StringUtils.validNull(condition.get("trxGoodsSn"))) {
            sql.append(" AND t1.trx_goods_sn = ?");
            paramList.add(condition.get("trxGoodsSn"));
        }
        //凭证码
        if(StringUtils.validNull(condition.get("voucherCode"))) {
            String queryVoucherCodeSql = "select voucher_id from beiker_voucher where voucher_code=?";
            List<Map<String,Object>> mapList = getSimpleJdbcTemplate().queryForList(queryVoucherCodeSql, condition.get("voucherCode"));
            if(mapList==null||mapList.size()==0){
                return null;
            }else{
                StringBuffer buffer = new StringBuffer("");
                for (Map<String, Object> map : mapList) {
                    Long voucherId = ((Number) map.get("voucher_id"))
                            .longValue();
                    if (buffer.length() == 0) {
                        buffer.append(voucherId);
                    } else {
                        buffer.append(",").append(voucherId);
                    }
                }
                if(buffer.toString().contains(",")){
                    sql.append(" and t1.voucher_id in (" + buffer + ")");
                }else{
                    sql.append(" and t1.voucher_id = " + buffer);
                }
            }
        }
        
        if(StringUtils.validNull(condition.get("mobile"))) {
            sql.append(" AND t4.mobile = ?");
            paramList.add(condition.get("mobile"));
        }
        
        if(StringUtils.validNull(condition.get("email"))) {
            sql.append(" AND t4.email = ?");
            paramList.add(condition.get("email"));
        }
        
        if(StringUtils.validNull(condition.get("goodsId"))) {
            sql.append(" AND t1.goods_id = ?");
            paramList.add(condition.get("goodsId"));
        }
        
        if(StringUtils.validNull(condition.get("guestId"))) {
            sql.append(" AND t1.guest_id = ?");
            paramList.add(condition.get("guestId"));
        }
        
        if(StringUtils.validNull(condition.get("isFreeze"))){
            sql.append(" AND t1.is_freeze = ?");
            paramList.add(condition.get("isFreeze"));
        }
        
        if(StringUtils.validNull(condition.get("outRequestId"))){
            sql.append(" AND t3.out_request_id = ?");
            paramList.add(condition.get("outRequestId"));
        }
        
        if(StringUtils.validNull(condition.get("createDateBegin")) && StringUtils.validNull(condition.get("createDateEnd"))) {
            sql.append(" AND t1.create_date BETWEEN ? AND ? ");
            paramList.add(condition.get("createDateBegin"));
            paramList.add(condition.get("createDateEnd"));
        }
        if(StringUtils.validNull(condition.get("city"))) {
            sql.append(" AND t2.city = ? ");
            paramList.add(condition.get("city"));
        }
        if(StringUtils.validNull(condition.get("confirmDateBegin")) && StringUtils.validNull(condition.get("confirmDateEnd"))) {
            sql.append(" AND t5.confirm_date BETWEEN ? AND ?");
            paramList.add(condition.get("confirmDateBegin") );
            paramList.add(condition.get("confirmDateEnd"));
        }
        List<Map<String, Object>> rsList =  getSimpleJdbcTemplate().queryForList(sql.toString(),paramList.toArray(new Object[]{}));
        if(rsList == null || rsList.size() == 0) {
            return null;
        }
        return rsList;
    }
    /**
     * 查询多个商品订单凭证
     * @param voucherIds
     * isHistory 为true查询历史记录
     * @return List<Voucher>
     */
    @Override
    public List<Voucher> findConfrimTimeByTrxgoodsIds(String voucherIds,Boolean isHistory) {
        String beiker_voucher =" beiker_voucher ";
        if(isHistory!=null && isHistory){//查询历史数据表名切换
             beiker_voucher =" beiker_voucher ";
        }
        String sql = "select voucher_id,confirm_date,voucher_code from "+beiker_voucher +" where voucher_id in (  "+ voucherIds+" )";
        List<Voucher> voucherList = getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<Voucher>() {
            @Override
            public Voucher mapRow(ResultSet rs, int arg1) throws SQLException {
                Voucher v = new Voucher();
                v.setConfirmDate(rs.getTimestamp("confirm_date"));
                v.setVoucherCode(rs.getString("voucher_code"));
                v.setId(rs.getLong("voucher_id"));
                return v;
            }
        });
        if(voucherList != null && voucherList.size() > 0) {
            return voucherList;
        }
        return null;
    }
}
