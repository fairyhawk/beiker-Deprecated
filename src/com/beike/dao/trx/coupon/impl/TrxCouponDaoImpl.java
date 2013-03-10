package com.beike.dao.trx.coupon.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.enums.trx.TrxCouponStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.coupon.TrxCouponDao;
import com.beike.util.EnumUtil;

/**   
 * @title: TrxCouponDaoImpl.java
 * @package com.beike.dao.trx.coupon.impl
 * @description: 优惠券Dao实现
 * @author wangweijie  
 * @date 2012-10-30 下午01:59:36
 * @version v1.0   
 */
@Repository("trxCouponDao")
public class TrxCouponDaoImpl extends GenericDaoImpl<TrxCoupon, Long>  implements TrxCouponDao {

	/**
	 * 根据userId查询优惠券
	 * @param id
	 * @return
	 */
	@Override
	public List<TrxCoupon> queryAllTrxCouponsByUserIdForPage(Long userId,int startRow,int pageSize) {
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE user_id = ? ORDER BY modify_date DESC LIMIT ?,?";
		return getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),userId,startRow,pageSize);
	}
	
	/**
	 * 根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	@Override
	public TrxCoupon queryTrxCouponByIdAndUserId(Long id,Long userId){
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE id=? AND user_id = ?";
		List<TrxCoupon> couponList = getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),id,userId);
		if(null != couponList && couponList.size() > 0){
			return couponList.get(0);
		}
		return null;
	}
	/**
	 * 查询id最小的初始化优惠券
	 * @param activityId
	 * @return
	 */
	@Override
	public TrxCoupon queryMinINITCouponId(Long activityId){
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE activity_id=? AND coupon_status='INIT' ORDER BY id LIMIT 1 FOR UPDATE";
		List<TrxCoupon> couponList = getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),activityId);
		if(null != couponList && couponList.size() > 0){
			return couponList.get(0);
		}
		return null;
	}
	/**
	 * 获得用户已参加活动ID
	 * @param userId
	 * @return
	 */
	public List<Long> queryUserJoinCouponActivity(Long userId){
		String sql = "SELECT DISTINCT(activity_id) AS actId FROM beiker_trx_coupon WHERE user_id = ?";
		List<Map<String, Object>> list = getSimpleJdbcTemplate().queryForList(sql, userId);
		if(null != list && list.size() > 0){
			Map<String, Object> map = list.get(0);
			if(null != map && map.size()>0){
				List<Long> actvityIdList = new ArrayList<Long>(map.size());
				for(Entry<String, Object> entry : map.entrySet()){
					actvityIdList.add((Long) entry.getValue());
				}
				return actvityIdList;
			}
		}
		return null;
	}
	
	/**
	 * 根据userId 查询所有的优惠券数量
	 * @param userId
	 * @return
	 */
	public int queryCountAllTrxCouponsByUserId(Long userId){
		String sql = "SELECT count(1) FROM beiker_trx_coupon WHERE user_id = ?";
		return getSimpleJdbcTemplate().queryForInt(sql,userId);
	}
	
	/**
	 * 根据id 查询优惠券
	 * @param userId
	 * @return
	 */
	@Override
	public TrxCoupon queryTrxCouponById(Long id) {
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE id = ?";
		List<TrxCoupon> couponList = getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),id);
		if(null != couponList && couponList.size() > 0){
			return couponList.get(0);
		}
		return null;
	}

	/**
	 * 根据userId和优惠券状态查询优惠券
	 */
	@Override
	public List<TrxCoupon> queryTrxCouponsByUserId(Long userId,TrxCouponStatus couponStatus) {
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE user_id = ? AND coupon_status = ? ORDER BY modify_date DESC";
		return getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),userId,EnumUtil.transEnumToString(couponStatus));
	}


	/**
	 * 绑定优惠券
	 * @param couponId
	 * @param userId
	 * @param description
	 * @param version
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateTrxCouponForBind(Long couponId,Long userId,String description,Long version)throws StaleObjectStateException{
		String updateSql = "UPDATE beiker_trx_coupon SET coupon_status=?,user_id=?,bind_date=?,modify_date=?,description=?,version=version+1 WHERE id=? AND version=? AND coupon_status=?";
		Date date = new Date();
		int result = getSimpleJdbcTemplate().update(updateSql, EnumUtil.transEnumToString(TrxCouponStatus.BINDING),userId,date,date,description,couponId,version,EnumUtil.transEnumToString(TrxCouponStatus.INIT));
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	/**
	 * 使用优惠券
	 * @param couponId
	 * @param isCreditAct
	 * @param description
	 * @param version
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateTrxCouponForSale(Long couponId,int isCreditAct,String requestId,String description,Long version)throws StaleObjectStateException{
		String updateSql = "UPDATE beiker_trx_coupon SET coupon_status=?,is_credit_act=?,request_id=?,use_date=?,modify_date=?,description=?,version=version+1 WHERE id=? AND version=? AND coupon_status=?";
		Date date = new Date();
		int result = getSimpleJdbcTemplate().update(updateSql, EnumUtil.transEnumToString(TrxCouponStatus.USED),isCreditAct,requestId,date,date,description,couponId,version,EnumUtil.transEnumToString(TrxCouponStatus.BINDING));
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	/**
	 * 查询超时优惠券
	 * @return
	 */
	public List<TrxCoupon> queryTimeoutTrxCoupon(){
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE coupon_status=? AND end_date<now()";
		return getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),EnumUtil.transEnumToString(TrxCouponStatus.BINDING));
	}
	
	/**
	 * 优惠券过期操作
	 * @param couponId
	 * @param version
	 * @param description
	 * @throws StaleObjectStateException
	 */
	public void updateTrxCouponForTimeout(Long couponId,Long version,String description) throws StaleObjectStateException{
		String updateSql = "UPDATE beiker_trx_coupon SET coupon_status=?,modify_date=?,description=?,version=version+1 WHERE id=? AND version=?";
		int result = getSimpleJdbcTemplate().update(updateSql, EnumUtil.transEnumToString(TrxCouponStatus.TIMEOUT),new Date(),description,couponId,version);
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	/**
	 * 查询未入账的优惠券
	 * @return
	 */
	@Override
	public List<TrxCoupon> queryNoCreditActCoupon(){
		String querySql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM  beiker_trx_coupon WHERE coupon_status='USED' AND is_credit_act=0 ORDER BY activity_id,modify_date";
		return getSimpleJdbcTemplate().query(querySql,new RowMapperImpl());
	}
	
	
	/**
	 * 更改优惠券为已入账
	 * @param couponId
	 * @param version
	 * @return
	 */
	@Override
	public void updateTrxCouponForCreditAct(Long couponId,Long version,String description) throws StaleObjectStateException{
		String updateSql = "UPDATE beiker_trx_coupon SET is_credit_act=1,modify_date=?,description=?,version=version+1 WHERE id=? AND version=? AND coupon_status=?";
		int result = getSimpleJdbcTemplate().update(updateSql,new Date() ,description,couponId,version,EnumUtil.transEnumToString(TrxCouponStatus.USED));
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}

	/**
	 * 根据优惠券密码查询
	 */
	@Override
	public TrxCoupon queryTrxCouponByPwd(String couponPwd) {
		String sql = "SELECT id,coupon_no,coupon_pwd,coupon_type,coupon_balance,coupon_status,activity_id,user_id,vm_account_id,is_credit_act,request_id,start_date,end_date,bind_date,use_date,create_date,modify_date,description,version FROM beiker_trx_coupon WHERE coupon_pwd = ?";
		List<TrxCoupon> couponList = getSimpleJdbcTemplate().query(sql,new RowMapperImpl(),couponPwd);
		if(null != couponList && couponList.size() > 0){
			return couponList.get(0);
		}
		return null;
	}

	public class RowMapperImpl implements ParameterizedRowMapper<TrxCoupon> {
		@Override
		public TrxCoupon mapRow(ResultSet rs, int num) throws SQLException {
			TrxCoupon coupon = new TrxCoupon();
			coupon.setId(rs.getLong("id"));						//id
			coupon.setCouponNo(rs.getString("coupon_no"));		//优惠券编号
			coupon.setCouponPwd(rs.getString("coupon_pwd"));	//优惠券密码
			coupon.setCouponBalance(rs.getDouble("coupon_balance"));	//优惠券面值
			coupon.setCouponType(rs.getInt("coupon_type"));		//优惠券类型
			coupon.setCouponStatus(EnumUtil.transStringToEnum(TrxCouponStatus.class, rs.getString("coupon_status")));//优惠券状态
			coupon.setUserId(rs.getLong("user_id"));			//用户ID
			coupon.setActivityId(rs.getLong("activity_id")); 	//所属活动ID
			coupon.setVmAccountId(rs.getLong("vm_account_id"));	//所属虚拟款项ID
			coupon.setIsCreditAct(rs.getInt("is_credit_act"));		//是否入账：0:未入账；1：已经入账
			coupon.setRequestId(rs.getString("request_id"));	//入账请求号
			coupon.setStartDate(rs.getTimestamp("start_date"));	//生效日期
			coupon.setEndDate(rs.getTimestamp("end_date"));		//过期时间
			coupon.setBindDate(rs.getTimestamp("bind_date"));	//激活日期
			coupon.setUseDate(rs.getTimestamp("use_date"));		//使用日期
			coupon.setCreateDate(rs.getTimestamp("create_date"));	//创建日期
			coupon.setModifyDate(rs.getTimestamp("modify_date"));	//修改日期
			coupon.setDescription(rs.getString("description"));	//描述
			coupon.setVersion(rs.getLong("version"));			//乐观锁版本号
			return coupon;
		}
	}
}
