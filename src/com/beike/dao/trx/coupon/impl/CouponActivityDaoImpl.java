package com.beike.dao.trx.coupon.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.coupon.CouponActivity;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.coupon.CouponActivityDao;

/**   
 * @title: CouponActivityDaoImpl.java
 * @package com.beike.dao.trx.activity.impl
 * @description: 
 * @author wangweijie  
 * @date 2012-10-30 下午02:01:41
 * @version v1.0   
 */
@Repository("couponActivityDao")
public class CouponActivityDaoImpl extends GenericDaoImpl<CouponActivity, Long> implements CouponActivityDao {

	/**
	 * 根据ID查询优惠券活动
	 * @param id
	 * @return
	 */
	@Override
	public CouponActivity queryCouponActivityById(Long id) {
		String querySql = "SELECT id,vm_account_id,activity_name,activity_type,csid,start_date,end_date,limit_amount,limit_tagid,coupon_balance,coupon_total_num,coupon_start_date,coupon_end_date,total_balance,operator_id,create_date,modify_date,description,version FROM beiker_coupon_activity WHERE id=?";
		List<CouponActivity> activityList = getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(),id);
		if(null != activityList && activityList.size()>0){
			return activityList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据类型查询CouponActivity
	 * @param type
	 * @return
	 */
	public List<CouponActivity> queryCouponActivityByType(String type) {
		String querySql = "SELECT id,vm_account_id,activity_name,activity_type,csid,start_date,end_date,limit_amount,limit_tagid,coupon_balance,coupon_total_num,coupon_start_date,coupon_end_date,total_balance,operator_id,create_date,modify_date,description,version FROM beiker_coupon_activity WHERE activity_type=?";
		return getSimpleJdbcTemplate().query(querySql,new RowMapperImpl(),type);
	}
	
	
	public class RowMapperImpl implements ParameterizedRowMapper<CouponActivity> {
		@Override
		public CouponActivity mapRow(ResultSet rs, int num) throws SQLException {
			CouponActivity activity = new CouponActivity();
			activity.setId(rs.getLong("id"));				//id
			activity.setVmAccountId(rs.getLong("vm_account_id"));		//所属虚拟款项ID
			activity.setActivityName(rs.getString("activity_name"));	//优惠券活动名称
			activity.setActivityType(rs.getString("activity_type"));	//优惠券活动类型(MARKETING_ONLINE:市场线上活动；MARKETING_OFFLINE:市场线下活动；OPERATING:运营活动)
			activity.setCsid(rs.getString("csid"));	//渠道代码（用户来源csid)
			activity.setStartDate(rs.getTimestamp("start_date"));	//生效日期
			activity.setEndDate(rs.getTimestamp("end_date"));	//过期时间
			activity.setLimitAmount(rs.getDouble("limit_amount"));		//金额限制，0表示不限制。否则必须大于等于该金额
			activity.setLimitTagid(rs.getString("limit_tagid"));//一级属性id 限制，以分号做分割,为空说明无此限制，秒杀对应的tag_id为100
			activity.setCouponBalance(rs.getDouble("coupon_balance"));	//优惠券张数
			activity.setCouponTotalNum(rs.getLong("coupon_total_num"));		//用户ID
			activity.setCouponStartDate(rs.getTimestamp("coupon_start_date"));	//优惠券开始时间
			activity.setCouponEndDate(rs.getTimestamp("coupon_end_date"));	//优惠券结束时间
			activity.setTotalBalance(rs.getDouble("total_balance")); //	总金额
			activity.setOperator_id(rs.getLong("operator_id"));		//操作人
			activity.setModifyDate(rs.getTimestamp("modify_date"));	//修改日期
			activity.setDescription(rs.getString("description"));	//描述
			activity.setVersion(rs.getLong("version"));	//乐观锁版本号
			return activity;
		}
	}
}
