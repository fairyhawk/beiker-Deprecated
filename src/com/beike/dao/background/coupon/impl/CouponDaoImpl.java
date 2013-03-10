package com.beike.dao.background.coupon.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.coupon.CouponDao;
import com.beike.entity.background.coupon.Coupon;
import com.beike.form.background.coupon.CouponForm;
import com.beike.util.StringUtils;
/**
 * Title : 	CouponDaoImpl
 * <p/>
 * Description	:	优惠券关系数据访问实现
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-06-17    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-17  
 */
@Repository("bgCouponDao")
public class CouponDaoImpl extends GenericDaoImpl<Coupon,Long> implements CouponDao {
	//private MemCacheService  memCacheService=MemCacheServiceImpl.getInstance();
	/*
	 * @see com.beike.dao.background.coupon.CouponDao#addCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String addCoupon(CouponForm couponForm) throws Exception {
		final CouponForm form = couponForm;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_coupon_info(coupon_name,coupon_tagid,coupon_tagextid,coupon_end_time,coupon_logo,coupon_rules,coupon_branch_id,");
		sql.append("coupon_modify_time,coupon_number,coupon_smstemplate,coupon_status,guest_id) VALUES(?,?,?,?,?,?,?,NOW(),?,?,?,?) ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getCouponName().trim());
				ps.setInt(2, form.getCouponTagid());
				ps.setString(3, form.getCouponTagextid());
				ps.setTimestamp(4, form.getCouponEndTime());
				ps.setString(5, form.getCouponLogo());
				ps.setString(6, form.getCouponRules());
				ps.setString(7, form.getCouponBranchId());
				ps.setInt(8, form.getCouponNumber());
				ps.setString(9, form.getCouponSmstemplate());
				ps.setString(10, form.getCouponStatus());
				ps.setInt(11, form.getGuestId());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.coupon.CouponDao#queryCoupon(com.beike.form.background.coupon.CouponForm, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Coupon> queryCoupon(CouponForm couponForm, int startRow,
			int pageSize) throws Exception {
		List tempList = null;
		List<Coupon> couponList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.guest_id guest_id,g.guest_cn_name guest_cn_name,c.coupon_id coupon_id,c.coupon_name coupon_name,c.coupon_tagid coupon_tagid,");
		sql.append("c.coupon_tagextid coupon_tagextid,c.coupon_status coupon_status,c.coupon_on_time coupon_on_time,c.guest_id guest_id,c.coupon_number coupon_number, g.brand_id brand_id ");
		sql.append("FROM beiker_coupon_info c JOIN beiker_guest_info g ");
		sql.append("ON c.guest_id = g.guest_id WHERE 1=1 ");
		if(couponForm.getCouponNumber()>0){
			sql.append(" AND c.coupon_number = ").append(couponForm.getCouponNumber());
		}
		if(StringUtils.validNull(couponForm.getCouponName())){
			sql.append(" AND c.coupon_name like ").append("'%"+couponForm.getCouponName()+"%'");
		}
		if(StringUtils.validNull(couponForm.getCouponStatus())){
			sql.append(" AND c.coupon_status = ").append("'"+couponForm.getCouponStatus()+"'");
		}
		if(couponForm.getCouponTagid()>0){
			sql.append(" AND c.coupon_tagid = ").append(couponForm.getCouponTagid());
		}
		if(StringUtils.validNull(couponForm.getCouponTagextid())){
			//sql.append("");
		}
		if(couponForm.getGuestId()>0){
			sql.append(" AND c.guest_id = ").append(couponForm.getGuestId());
		}
		if(StringUtils.validNull(couponForm.getGuestCnName())){
			sql.append(" AND g.guest_cn_name like ").append("'%"+couponForm.getGuestCnName()+"%'");
		}
		if(null!=couponForm.getCouponOnTimeBegin()&&null!=couponForm.getCouponOnTimeEnd()){
			sql.append(" AND c.coupon_on_time BETWEEN ").append("'"+couponForm.getCouponOnTimeBegin()+"'").append(" AND " ).append("'"+couponForm.getCouponOnTimeEnd()+"'");
		}
		if(couponForm.getBrandId()>0){
			sql.append(" AND g.brand_id = ").append(couponForm.getBrandId());
		}
		sql.append(" ORDER BY coupon_id DESC LIMIT ?,? "); 
		Object[] params = new Object[]{startRow,pageSize};
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			couponList = this.convertResultToObjectList(tempList);
		}
		return couponList;
	}

	/*
	 * @see com.beike.dao.background.coupon.CouponDao#queryCouponCount(com.beike.form.background.coupon.CouponForm)
	 */
	public int queryCouponCount(CouponForm couponForm) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_coupon_info c JOIN beiker_guest_info g ");
		sql.append("ON c.guest_id = g.guest_id WHERE 1=1 ");
		if(couponForm.getCouponNumber()>0){
			sql.append(" AND c.coupon_number = ").append(couponForm.getCouponNumber());
		}
		if(StringUtils.validNull(couponForm.getCouponName())){
			sql.append(" AND c.coupon_name like ").append("'%"+couponForm.getCouponName()+"%'");
		}
		if(StringUtils.validNull(couponForm.getCouponStatus())){
			sql.append(" AND c.coupon_status = ").append("'"+couponForm.getCouponStatus()+"'");
		}
		if(couponForm.getCouponTagid()>0){
			sql.append(" AND c.coupon_tagid = ").append(couponForm.getCouponTagid());
		}
		if(StringUtils.validNull(couponForm.getCouponTagextid())){
			//sql.append("");
		}
		if(couponForm.getGuestId()>0){
			sql.append(" AND c.guest_id = ").append(couponForm.getGuestId());
		}
		if(StringUtils.validNull(couponForm.getGuestCnName())){
			sql.append(" AND g.guest_cn_name like ").append("'%"+couponForm.getGuestCnName()+"%'");
		}
		if(null!=couponForm.getCouponOnTimeBegin()&&null!=couponForm.getCouponOnTimeEnd()){
			sql.append(" AND c.coupon_on_time BETWEEN ").append("'"+couponForm.getCouponOnTimeBegin()+"'").append(" AND " ).append("'"+couponForm.getCouponOnTimeEnd()+"'");
		}
		if(couponForm.getBrandId()>0){
			sql.append(" AND g.brand_id = ").append(couponForm.getBrandId());
		}
		int count = this.getJdbcTemplate().queryForInt(sql.toString());
		return count;
	}

	/*
	 * @see com.beike.dao.background.coupon.CouponDao#queryCouponById(java.lang.String)
	 */
	public Coupon queryCouponById(String couponId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT coupon_id,coupon_name,coupon_tagid,coupon_tagextid,coupon_end_time,coupon_logo,coupon_rules,coupon_branch_id,");
		sql.append("coupon_modify_time,coupon_number,coupon_smstemplate,coupon_status,guest_id FROM beiker_coupon_info WHERE coupon_id= ? ");
		Coupon coupon = this.getSimpleJdbcTemplate().queryForObject(sql.toString(),ParameterizedBeanPropertyRowMapper.newInstance(Coupon.class) ,Integer.parseInt(couponId));
		return coupon;
	}

	/*
	 * @see com.beike.dao.background.coupon.CouponDao#editCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String editCoupon(CouponForm couponForm) throws Exception {
		final CouponForm form = couponForm;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_coupon_info SET coupon_name=?,coupon_tagid=?,coupon_tagextid=?,coupon_end_time=?,coupon_logo=?,coupon_rules=?,");
		sql.append("coupon_branch_id=?,coupon_modify_time= NOW(),coupon_number=?,coupon_smstemplate=?,coupon_status=? WHERE coupon_id = ? ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getCouponName().trim());
				ps.setInt(2, form.getCouponTagid());
				ps.setString(3, form.getCouponTagextid());
				ps.setTimestamp(4, form.getCouponEndTime());
				ps.setString(5, form.getCouponLogo());
				ps.setString(6, form.getCouponRules());
				ps.setString(7, form.getCouponBranchId());
				ps.setInt(8, form.getCouponNumber());
				ps.setString(9, form.getCouponSmstemplate());
				ps.setString(10, form.getCouponStatus());
				ps.setInt(11, form.getCouponId());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.coupon.CouponDao#downCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String downCoupon(CouponForm couponForm) throws Exception {
		final CouponForm form = couponForm;
		String sql = "UPDATE beiker_coupon_info SET coupon_status = ? ,coupon_modify_time = NOW() WHERE coupon_id = ?";
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getCouponStatus());
				ps.setInt(2, form.getCouponId());
			}
		});
		return String.valueOf(flag);
	}
	
	/*
	 * @see com.beike.dao.background.coupon.CouponDao#queryCouponName(com.beike.form.background.coupon.CouponForm)
	 */
	public boolean validatorCouponName(CouponForm couponForm) throws Exception {
		boolean flag = false;
		String sql = "SELECT COUNT(1) FROM beiker_coupon_info WHERE coupon_name = ? AND coupon_number != ? ";
		Object[] params = new Object[]{couponForm.getCouponName().trim(),couponForm.getCouponNumber()};
		int[] types = new int[]{Types.VARCHAR,Types.INTEGER};
		int count = this.getJdbcTemplate().queryForInt(sql.toString(),params,types);
		if(count>0){
			flag = true;
		}
		return flag;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<Coupon> convertResultToObjectList(List results) throws Exception{
        List<Coupon> objList = new ArrayList<Coupon>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                Coupon coupon = this.convertResultMapToObject(result);
                objList.add(coupon);
            }
        }
        return objList;
    }
    
    /**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result   jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private Coupon convertResultMapToObject(Map result) throws Exception{
		Coupon obj = new Coupon();
			if (result != null) {
				
				Long couponId = ((Number)result.get("coupon_id")).longValue();
				if(null!=couponId){
					obj.setCouponId(couponId.intValue());
				}
				Long couponNumber = ((Number)result.get("coupon_number")).longValue();
				if(null!=couponNumber){
					obj.setCouponNumber(couponNumber.intValue());
				}
				//memCacheService.set(Constant.MEM_COUPON_DOWNCOUNT+couponNumber, "10000");
				//obj.setCouponDownCount(Integer.parseInt((String)memCacheService.get(Constant.MEM_COUPON_DOWNCOUNT+couponNumber)));
				if(StringUtils.validNull((String)result.get("coupon_name"))){
					obj.setCouponName(result.get("coupon_name").toString());
				}
				Long guestId = ((Number)result.get("guest_id")).longValue();
				if(null!=guestId){
					obj.setGuestId(guestId.intValue());
				}
				if(StringUtils.validNull((String)result.get("guest_cn_name"))){
					obj.setGuestCnName(result.get("guest_cn_name").toString());
				}
				Long couponTagId = ((Number)result.get("coupon_tagid")).longValue();
				if(null!=couponTagId){
					obj.setCouponTagid(couponTagId.intValue());
				}
				if(StringUtils.validNull((String)result.get("coupon_tagextid"))){
					obj.setCouponTagextid(result.get("coupon_tagextid").toString());
				}
				if(StringUtils.validNull((String)result.get("coupon_status"))){
					obj.setCouponStatus(result.get("coupon_status").toString());
				}
				
				Timestamp time = (Timestamp)result.get("coupon_on_time");
				if(null!=time){
					Timestamp ts = (Timestamp)result.get("coupon_on_time");
					obj.setCouponOnTime(ts);
				}
				Long brandId = ((Number)result.get("brand_id")).longValue();
				if(null!=brandId){
					obj.setBrandId(brandId.intValue());
				}
				
			}
		return obj;

	}

}
