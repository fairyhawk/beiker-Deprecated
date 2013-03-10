package com.beike.dao.background.guest.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.guest.GuestBranchDao;
import com.beike.entity.background.guest.GuestBranch;
import com.beike.form.background.guest.GuestBranchForm;
import com.beike.util.StringUtils;

/**
 * Title : 	GuestBranchDaoImpl
 * <p/>
 * Description	:	客户分店关系数据访问实现
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
 * <pre>1     2011-06-07    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-07  
 */
@Repository("guestBranchDao")
public class GuestBranchDaoImpl extends GenericDaoImpl<GuestBranch,Long> implements
		GuestBranchDao {

	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#addGuestBranch(com.beike.form.background.guest.GuestBranchForm)
	 */
	public String addGuestBranch(GuestBranchForm guestBranchForm)
			throws Exception {
		final GuestBranchForm form = guestBranchForm ;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_guest_branch_info(branch_id,branch_cn_name,branch_country_id,branch_province_id,branch_city_id,branch_address,branch_region_id,");
		sql.append("branch_business_time,branch_book_phone,branch_lon,branch_lat,branch_status,guest_id,branch_city_area_id,branch_modify_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, form.getBranchId());
				ps.setString(2,form.getBranchCnName());
				ps.setInt(3, form.getBranchCountryId());
				ps.setInt(4, form.getBranchProvinceId());
				ps.setInt(5, form.getBranchCityId());
				ps.setString(6, form.getBranchAddress());
				ps.setString(7, form.getBranchRegionId());
				ps.setString(8, form.getBranchBusinessTime());
				ps.setString(9, form.getBranchBookPhone());
				ps.setString(10, form.getBranchLon());
				ps.setString(11, form.getBranchLat());
				ps.setString(12, form.getBranchStatus());
				ps.setInt(13, form.getGuestId());
				ps.setInt(14, form.getBranchCityAreaId());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#queryGuestBranchConditions(com.beike.form.background.guest.GuestBranchForm, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<GuestBranch> queryGuestBranchConditions(
			GuestBranchForm guestBranchForm,int startRow,int pageSize) throws Exception {
		List tempList = null;
		List<GuestBranch> guestBranchList = new ArrayList<GuestBranch>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.guest_cn_name guest_cn_name,b.guest_id guest_id,b.branch_id branch_id,b.branch_cn_name branch_cn_name,b.branch_address branch_address,brand.brand_name brand_name,c.brand_id brand_id,b.branch_city_area_id branch_city_area_id FROM beiker_guest_branch_info b ");
		sql.append("LEFT JOIN  beiker_guest_info c ON c.guest_id = b.guest_id ");
		sql.append("LEFT JOIN  beiker_brand brand ON brand.brand_id = c.brand_id  WHERE 1=1 ");
		if(guestBranchForm.getGuestId()>0){
			sql.append(" AND b.guest_id=").append(guestBranchForm.getGuestId());
		}
		if(StringUtils.validNull(guestBranchForm.getBranchCnName())){
			sql.append(" AND b.branch_cn_name like ").append("'%"+guestBranchForm.getBranchCnName()+"%'");
		}
		if(StringUtils.validNull(guestBranchForm.getBrandName())){
			sql.append(" AND brand.brand_name like ").append("'%"+guestBranchForm.getBrandName()+"%'");
		}
		sql.append(" ORDER BY b.branch_id DESC LIMIT ?,? "); 
		Object[] params = new Object[]{startRow,pageSize};
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			guestBranchList = this.convertResultToObjectList(tempList);
		}
		return guestBranchList;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#queryGuestBranchCountConditions(com.beike.form.background.guest.GuestBranchForm)
	 */
	public int queryGuestBranchCountConditions(
			GuestBranchForm guestBranchForm) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_guest_branch_info b ");
		sql.append("LEFT JOIN  beiker_guest_info c ON c.guest_id = b.guest_id ");
		sql.append("LEFT JOIN  beiker_brand brand ON brand.brand_id = c.brand_id  WHERE 1=1 ");
		if(guestBranchForm.getGuestId()>0){
			sql.append(" AND b.guest_id=").append(guestBranchForm.getGuestId());
		}
		if(StringUtils.validNull(guestBranchForm.getBranchCnName())){
			sql.append(" AND b.branch_cn_name like ").append("'%"+guestBranchForm.getBranchCnName()+"%'");
		}
		if(StringUtils.validNull(guestBranchForm.getBrandName())){
			sql.append(" AND brand.brand_name like ").append("'%"+guestBranchForm.getBrandName()+"%'");
		}
		int count = this.getJdbcTemplate().queryForInt(sql.toString());
		return count;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#queryGuestBranchById(java.lang.String)
	 */
	public GuestBranch queryGuestBranchById(String branchId)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT branch_id,branch_cn_name,branch_country_id,branch_province_id,branch_city_id,branch_address,");
		sql.append("branch_region_id,branch_business_time,branch_book_phone,branch_lon,branch_lat,branch_status,guest_id,branch_city_area_id ");
		sql.append("FROM beiker_guest_branch_info WHERE branch_id = ? ");
		GuestBranch guestBranch = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(GuestBranch.class), Integer.parseInt(branchId));
		return guestBranch;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#editGuestBranch(com.beike.form.background.guest.GuestBranchForm)
	 */
	public String editGuestBranch(GuestBranchForm guestBranchForm)
		throws Exception {
		final GuestBranchForm form = guestBranchForm ;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_guest_branch_info SET branch_cn_name=?,branch_country_id=?,branch_province_id=?,branch_city_id=?,");
		sql.append("branch_address=?,branch_region_id=?,branch_business_time=?,branch_book_phone=?,branch_lon=?,branch_lat=?,branch_status=?,branch_city_area_id = ?,branch_modify_time=now() ");
		sql.append("WHERE branch_id = ? ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1,form.getBranchCnName());
				ps.setInt(2, form.getBranchCountryId());
				ps.setInt(3, form.getBranchProvinceId());
				ps.setInt(4, form.getBranchCityId());
				ps.setString(5, form.getBranchAddress());
				ps.setString(6, form.getBranchRegionId());
				ps.setString(7, form.getBranchBusinessTime());
				ps.setString(8, form.getBranchBookPhone());
				ps.setString(9, form.getBranchLon());
				ps.setString(10, form.getBranchLat());
				ps.setString(11, form.getBranchStatus());
				ps.setInt(12, form.getBranchCityAreaId());
				ps.setInt(13, form.getBranchId());
			}
		});
		return String.valueOf(flag);
	}	
	
	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#queryBranchInfo(com.beike.form.background.guest.GuestBranchForm)
	 */
	@SuppressWarnings("unchecked")
	public List<GuestBranch> queryBranchInfo(GuestBranchForm guestBranchForm)
		throws Exception {
		List tempList = null;
		List<GuestBranch> guestBranchList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT b.branch_id branch_id,b.branch_cn_name branch_cn_name,b.guest_id guest_id,g.brand_id brand_id,b.branch_city_area_id branch_city_area_id FROM beiker_guest_branch_info b ");
		sql.append("JOIN beiker_guest_info g ON g.guest_id = b.guest_id ");
		sql.append("WHERE g.guest_id = ? ");
		Object[] params = new Object[]{guestBranchForm.getGuestId()};
		int[] types = new int[]{Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			guestBranchList = this.convertResultToObjectList(tempList);
		}
		return guestBranchList;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestBranchDao#validatorBranchName(com.beike.form.background.guest.GuestBranchForm)
	 */
	public boolean validatorBranchName(GuestBranchForm guestBranchForm)
		throws Exception {
		boolean flag = false;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_guest_branch_info WHERE branch_cn_name = ? AND branch_status = ? ");
		if(StringUtils.validNull(String.valueOf(guestBranchForm.getBranchId()))){
			sql.append(" AND branch_id != ").append(guestBranchForm.getBranchId());
		}
		Object[] params = new Object[]{guestBranchForm.getBranchCnName().trim(),guestBranchForm.getBranchStatus()};
		int[] types = new int[]{Types.VARCHAR,Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
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
	private List<GuestBranch> convertResultToObjectList(List results) throws Exception{
        List<GuestBranch> objList = new ArrayList<GuestBranch>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                GuestBranch guestBranch = this.convertResultMapToObject(result);
                objList.add(guestBranch);
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
	private GuestBranch convertResultMapToObject(Map result) throws Exception{
		GuestBranch obj = new GuestBranch();
			if (result != null) {
				//,c.brand_id 
				Long guestBranchId = ((Number)result.get("branch_id")).longValue();
				if(null!=guestBranchId){
					obj.setBranchId(guestBranchId.intValue());
				}
				Long brandId = ((Number)result.get("brand_id")).longValue();
				if(null!=brandId){
					obj.setBrandId(brandId.intValue());
				}
				if(StringUtils.validNull((String)result.get("branch_cn_name"))){
					obj.setBranchCnName(result.get("branch_cn_name").toString());
				}
				Long branchCountryId = (Long)result.get("branch_country_id");
				if(null!=branchCountryId){
					obj.setBranchCountryId(branchCountryId.intValue());
				}
				Long branchProvinceId = (Long)result.get("branch_province_id");
				if(null!=branchProvinceId){
					obj.setBranchProvinceId(branchProvinceId.intValue());
				}
				Long branchCityAreaId = ((Number)result.get("branch_city_area_id")).longValue();
				if(null!=branchCityAreaId){
					obj.setBranchCityAreaId(branchCityAreaId.intValue());
				}
				Long branchCityId = (Long)result.get("branch_city_id");
				if(null!=branchCityId){
					obj.setBranchCityId(branchCityId.intValue());
				}
				if(StringUtils.validNull((String)result.get("branch_address"))){
					obj.setBranchAddress(result.get("branch_address").toString());
				}
				if(StringUtils.validNull((String)result.get("branch_region_id"))){
					obj.setBranchRegionId(result.get("branch_region_id").toString());
				}
				if(StringUtils.validNull((String)result.get("branch_business_time"))){
					obj.setBranchBusinessTime(result.get("branch_business_time").toString());
				}
				if(StringUtils.validNull((String)result.get("branch_lon"))){
					obj.setBranchLon(result.get("branch_lon").toString());
				}
				if(StringUtils.validNull((String)result.get("branch_lat"))){
					obj.setBranchLat(result.get("branch_lat").toString());
				}
				if(StringUtils.validNull((String)result.get("branch_status"))){
					obj.setBranchStatus(result.get("branch_status").toString());
				}
				Long guestId = ((Number)result.get("guest_id")).longValue();
				if(null!=guestId){
					obj.setGuestId(guestId.intValue());
				}
				if(StringUtils.validNull((String)result.get("guest_cn_name"))){
					obj.setGuestName(result.get("guest_cn_name").toString());
				}
				if(StringUtils.validNull((String)result.get("brand_name"))){
					obj.setBrandName(result.get("brand_name").toString());
				}
			}
		return obj;
	}

}
