package com.beike.dao.discountcoupon.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.card.Card;
import com.beike.common.entity.discountcoupon.DiscountCoupon;
import com.beike.common.enums.trx.DiscountCouponStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.discountcoupon.DiscountCouponDao;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**   
 * @title: DiscountCouponDaoImpl.java
 * @package com.beike.dao.discountcoupon.impl
 * @description: DiscountCoupon实现
 * @author wangweijie  
 * @date 2012-7-11 下午06:47:57
 * @version v1.0   
 */

@Repository("discountCouponDao")
public class DiscountCouponDaoImpl extends GenericDaoImpl<Card, Long> implements DiscountCouponDao {


	/**
	 * 根据ID查询优惠券记录
	 * @param id
	 * @return
	 */
	@Override
	public DiscountCoupon findById(Long id) {
		if(null == id){
			return null;
		}
		
		String querySql = "select id,coupon_no,coupon_pwd,coupon_value,coupon_type,coupon_status,batch_no,topup_channel,user_id,create_operator_id,active_operator_id,vm_account_id,biz_id,create_date,modify_date,lose_date,description,version "
			+ " from beiker_discount_coupon where id = ?";
		List<DiscountCoupon> discountCouponList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), id);
		if (discountCouponList.size() > 0) {
			return discountCouponList.get(0);
		}
		return null;
	}

	/**
	 * 根据couponNo查询优惠券记录
	 * @param couponNo
	 * @return
	 */
	@Override
	public DiscountCoupon findByCouponNo(String couponNo) {
		couponNo = StringUtils.toTrim(couponNo);
		if("".equals(couponNo)){
			return null;
		}
		
		String querySql = "select id,coupon_no,coupon_pwd,coupon_value,coupon_type,coupon_status,batch_no,topup_channel,user_id,create_operator_id,active_operator_id,vm_account_id,biz_id,create_date,modify_date,lose_date,description,version "
			+ " from beiker_discount_coupon where coupon_no = ?";
		List<DiscountCoupon> discountCouponList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), couponNo);
		if (discountCouponList.size() > 0) {
			return discountCouponList.get(0);
		}
		return null;
	}

	/**
	 * 根据couponPwd查询优惠券记录
	 * @param couponPwd
	 * @return
	 */
	@Override
	public DiscountCoupon findByCouponPwd(String couponPwd) {
		couponPwd = StringUtils.toTrim(couponPwd);
		if("".equals(couponPwd)){
			return null;
		}
		
		String querySql = "select id,coupon_no,coupon_pwd,coupon_value,coupon_type,coupon_status,batch_no,topup_channel,user_id,create_operator_id,active_operator_id,vm_account_id,biz_id,create_date,modify_date,active_date,lose_date,description,version "
			+ " from beiker_discount_coupon where coupon_pwd = ?";
		List<DiscountCoupon> discountCouponList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), couponPwd);
		if (discountCouponList.size() > 0) {
			return discountCouponList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据userId查询优惠券记录
	 * @param userId
	 * @return
	 */
	@Override
	public List<DiscountCoupon> findByUserId(Long userId) {
		if(null == userId){
			return null;
		}
		String querySql = "select id,coupon_no,coupon_pwd,coupon_value,coupon_type,coupon_status,batch_no,topup_channel,user_id,create_operator_id,active_operator_id,vm_account_id,biz_id,create_date,modify_date,active_date,lose_date,description,version "
			+ " from beiker_discount_coupon where user_id = ?";
		return getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(), userId);
	}

	
	/**
	 * 更新优惠券状态
	 * @param id
	 * @param coupon
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateCouponStatus(DiscountCouponStatus couponStatus,String description,Long id,Long version) throws StaleObjectStateException{
		if(null == id || null == version){ 
			return;
		}
		description = StringUtils.toTrim(description);
		String updateSql = "update beiker_discount_coupon set coupon_status=?,description=? ,modify_date=? ,version=version+1 where id=? and version=?";
		int result = getSimpleJdbcTemplate().update(updateSql,EnumUtil.transEnumToString(couponStatus),description,new Timestamp(new Date().getTime()),id,version);
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	/**
	 * 更新优惠券
	 * @param coupon
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateCouponStatusAndUserId(DiscountCouponStatus couponStatus,Long userId,Long id,Long version) throws StaleObjectStateException {
		if(null == userId || null == id || null == version){ 
			return;
		}
		
		String updateSql = "update beiker_discount_coupon set coupon_status=?,user_id=?,modify_date=? ,version=version+1 where id=? and version=?";
		int result = getSimpleJdbcTemplate().update(updateSql,EnumUtil.transEnumToString(couponStatus),userId,new Timestamp(new Date().getTime()),id,version);
		if (0 == result) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	public class RowMapperImpl implements ParameterizedRowMapper<DiscountCoupon> {
		@Override
		public DiscountCoupon mapRow(ResultSet rs, int num) throws SQLException {
			DiscountCoupon coupon = new DiscountCoupon();
			coupon.setId(rs.getLong("id"));				//id
			coupon.setCouponNo(rs.getString("coupon_no"));	//优惠券编号
			coupon.setCouponPwd(rs.getString("coupon_pwd"));	//优惠券密码
			coupon.setCouponValue(rs.getInt("coupon_value"));	//优惠券面值
			coupon.setCouponType(rs.getInt("coupon_type"));		//优惠券类型
			coupon.setCouponStatus(EnumUtil.transStringToEnum(DiscountCouponStatus.class, rs.getString("coupon_status")));//优惠券状态
			coupon.setBatchNo(rs.getString("batch_no"));	//所属批次
			coupon.setTopupChannel(rs.getString("topup_channel"));//充值渠道
			coupon.setUserId(rs.getLong("user_id"));		//用户ID
			coupon.setCreateOperatorId(rs.getLong("create_operator_id"));				//
			coupon.setActiveOperatorId(rs.getLong("active_operator_id"));		
			coupon.setVmAccountId(rs.getLong("vm_account_id"));		//所属虚拟款项ID
			coupon.setBizId(rs.getLong("biz_id"));			//业务ID
			coupon.setCreateDate(rs.getTimestamp("create_date"));	//创建日期
			coupon.setModifyDate(rs.getTimestamp("modify_date"));	//修改日期
			coupon.setActiveDate(rs.getTimestamp("active_date"));	//激活时间
			coupon.setLoseDate(rs.getTimestamp("lose_date"));	//过期时间
			coupon.setDescription(rs.getString("description"));	//描述
			coupon.setVersion(rs.getLong("version"));	//乐观锁版本号
			return coupon;
		}
	}

	@Override
	public List<DiscountCoupon> findExpireCouponInActiveStatus() {
		String querySql = "select id,coupon_no,coupon_pwd,coupon_value,coupon_type,coupon_status,batch_no,topup_channel,user_id,create_operator_id,active_operator_id,vm_account_id,biz_id,create_date,modify_date,active_date,lose_date,description,version "
			+ " from beiker_discount_coupon where coupon_status='"+EnumUtil.transEnumToString(DiscountCouponStatus.ACTIVE)+"' and lose_date < now()";
		return getSimpleJdbcTemplate().query(querySql,new RowMapperImpl());
	}

}
