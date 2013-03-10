
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
import com.beike.dao.background.guest.GuestDao;
import com.beike.entity.background.guest.Guest;
import com.beike.form.background.guest.GuestForm;
import com.beike.util.StringUtils;
/**
 * Title : 	GuestDaoImpl
 * <p/>
 * Description	:	客户关系数据访问实现
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
 * <pre>1     2011-06-03    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03  
 */
@Repository("guestDao")
public class GuestDaoImpl extends GenericDaoImpl<Guest,Long> implements GuestDao {

	/*
	 * @see com.beike.dao.background.guest.GuestDao#addGuest(com.beike.form.background.guest.GuestForm)
	 */
	public String addGuest(GuestForm guestForm) throws Exception {
		final GuestForm form = guestForm;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_guest_info(guest_id,guest_pwd,guest_cn_name,guest_type,guest_country_id,guest_province_id,guest_city_id,");
		sql.append("guest_address,guest_account,guest_account_bank,guest_contract_no,guest_email,guest_status,brand_id,guest_update_time ) ");
		sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()) ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, form.getGuestId());
				ps.setString(2, form.getGuestPwd().trim());
				ps.setString(3, form.getGuestCnName().trim());
				ps.setString(4, form.getGuestType().trim());
				ps.setInt(5, form.getGuestCountryId());
				ps.setInt(6, form.getGuestProvinceId());
				ps.setInt(7, form.getGuestCityId());
				ps.setString(8, form.getGuestAddress().trim());
				ps.setString(9, form.getGuestAccount().trim());
				ps.setString(10, form.getGuestAccountBank().trim());
				ps.setString(11, form.getGuestContractNo().trim());
				ps.setString(12, form.getGuestEmail().trim());
				ps.setString(13, form.getGuestStatus());
				ps.setInt(14, form.getBrandId());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.guest.GuestDao#validatorGuestName(com.beike.form.background.guest.GuestForm)
	 */
	public boolean validatorGuestName(GuestForm guestForm) throws Exception {
		boolean flag = false;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_guest_info WHERE guest_cn_name = ? AND guest_status = ? ");
		if(StringUtils.validNull(String.valueOf(guestForm.getGuestId()))){
			sql.append(" AND guest_id != ").append( guestForm.getGuestId());
		}
		Object[] params = new Object[]{guestForm.getGuestCnName().trim(),guestForm.getGuestStatus()};
		int[] types = new int[]{Types.VARCHAR,Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		if(count>0){
			flag = true;
		}
		return flag;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#validatorGuestContractNo(com.beike.form.background.guest.GuestForm)
	 */
	public boolean validatorGuestContractNo(GuestForm guestForm)
		throws Exception {
		boolean flag = false;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_guest_info WHERE guest_contract_no = ? ");
		if(StringUtils.validNull(String.valueOf(guestForm.getGuestId()))){
			sql.append(" AND guest_id != ").append( guestForm.getGuestId() );
		}
		Object[] params = new Object[]{guestForm.getGuestContractNo().trim()};
		int[] types = new int[]{Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		if(count>0){
			flag = true;
		}
		return flag;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#queryGuestByConditions(com.beike.form.background.guest.GuestForm, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Guest> queryGuestByConditions(GuestForm guestForm,
			int startRow, int pageSize) throws Exception {
		List tempList = null;
		List<Guest> guestList = new ArrayList<Guest>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.guest_id guest_id,c.guest_cn_name guest_cn_name,b.brand_name brand_name,c.guest_address guest_address,c.guest_status guest_status,c.brand_id brand_id FROM beiker_guest_info c ");
		sql.append("LEFT JOIN beiker_brand b ON b.brand_id = c.brand_id  WHERE 1=1 ");
		if(guestForm.getGuestId()>0){
			sql.append(" AND c.guest_id = ").append(guestForm.getGuestId());
		}
		if(StringUtils.validNull(guestForm.getGuestCnName())){
			sql.append(" AND c.guest_cn_name LIKE ").append("'%"+guestForm.getGuestCnName().trim()+"%'");
		}
		if(StringUtils.validNull(guestForm.getBrandName())){
			sql.append(" AND b.brand_name LIKE ").append("'%"+guestForm.getBrandName().trim()+"%'");
		}
		if(StringUtils.validNull(guestForm.getGuestAddress())){
			sql.append(" AND c.guest_address LIKE ").append("'%"+guestForm.getGuestAddress().trim()+"%'");
		}
		if(guestForm.getBrandId() > 0){
			sql.append(" AND c.brand_id = ").append(guestForm.getBrandId());
		}
		sql.append(" ORDER BY guest_id DESC LIMIT ?,? "); 
		Object[] params = new Object[]{startRow,pageSize};
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			guestList = this.convertResultToObjectList(tempList);
		}
		return guestList;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#queryGuestCountByConditions(com.beike.form.background.guest.GuestForm)
	 */
	public int queryGuestCountByConditions(GuestForm guestForm)
		throws Exception {
		int totalRows = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_guest_info c LEFT JOIN beiker_brand b ON b.brand_id = c.brand_id  WHERE 1=1 ");
		if(guestForm.getGuestId()>0){
			sql.append(" AND c.guest_id = ").append(guestForm.getGuestId());
		}
		if(StringUtils.validNull(guestForm.getGuestCnName())){
			sql.append(" AND c.guest_cn_name LIKE ").append("'%"+guestForm.getGuestCnName().trim()+"%'");
		}
		if(StringUtils.validNull(guestForm.getBrandName())){
			sql.append(" AND b.brand_name LIKE ").append("'%"+guestForm.getBrandName().trim()+"%'");
		}
		if(StringUtils.validNull(guestForm.getGuestAddress())){
			sql.append(" AND c.guest_address LIKE ").append("'%"+guestForm.getGuestAddress().trim()+"%'");
		}
		if(guestForm.getBrandId()>0){
			sql.append(" AND c.brand_id = ").append(guestForm.getBrandId());
		}
		totalRows = this.getJdbcTemplate().queryForInt(sql.toString());
		return totalRows;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#queryBrandById(java.lang.String)
	 */
	public Guest queryBrandById(String guestId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT guest_id,guest_cn_name,guest_type,guest_country_id,guest_province_id,guest_city_id,guest_address,");
		sql.append("guest_account,guest_account_bank,guest_contract_no,guest_email,guest_status,brand_id,guest_city_area_id FROM beiker_guest_info ");
		sql.append("WHERE guest_id = ? ");
		Guest guest = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(Guest.class), Integer.parseInt(guestId));
		return guest;
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#editGuest(com.beike.form.background.guest.GuestForm)
	 */
	public String editGuest(GuestForm guestForm) throws Exception {
		final GuestForm form = guestForm;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_guest_info SET guest_cn_name=?,guest_type=?,guest_country_id=?,guest_province_id=?,guest_city_id=?,");
		sql.append("guest_address=?,guest_account=?,guest_account_bank=?,guest_contract_no=?,guest_email=?,guest_status=?,guest_update_user=?,guest_update_time=now() ");
		sql.append("WHERE guest_id= ? ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, form.getGuestCnName().trim());
				ps.setString(2, form.getGuestType().trim());
				ps.setInt(3, form.getGuestCountryId());
				ps.setInt(4, form.getGuestProvinceId());
				ps.setInt(5, form.getGuestCityId());
				ps.setString(6, form.getGuestAddress().trim());
				ps.setString(7, form.getGuestAccount().trim());
				ps.setString(8, form.getGuestAccountBank().trim());
				ps.setString(9, form.getGuestContractNo().trim());
				ps.setString(10, form.getGuestEmail().trim());
				ps.setString(11, form.getGuestStatus());
				ps.setString(12, form.getGuestUpdateUser());
				ps.setInt(13, form.getGuestId());
			}
		});
		return String.valueOf(flag);
	}
	
	/*
	 * @see com.beike.dao.background.guest.GuestDao#queryGuestMaxId()
	 */
	public int queryGuestMaxId() throws Exception {
		String sql = "SELECT MAX(guest_id) FROM beiker_guest_info ";
		int pk = this.getJdbcTemplate().queryForInt(sql);
		return pk;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<Guest> convertResultToObjectList(List results) throws Exception{
        List<Guest> objList = new ArrayList<Guest>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                Guest guest = this.convertResultMapToObject(result);
                objList.add(guest);
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
	private Guest convertResultMapToObject(Map result) throws Exception{
		Guest obj = new Guest();
			if (result != null) {
				Long guestId = (Long)result.get("guest_id");
				if(null!=guestId){
					obj.setGuestId(guestId.intValue());
				}
				Long brandId = (Long)result.get("brand_id");
				if(null!=brandId){
					obj.setBrandId(brandId.intValue());
				}
				if(StringUtils.validNull((String)result.get("guest_cn_name"))){
					obj.setGuestCnName(result.get("guest_cn_name").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_type"))){
					obj.setGuestType(result.get("guest_type").toString());
				}
				Long guestCountryId = (Long)result.get("guest_country_id");
				if(null!=guestCountryId){
					obj.setGuestCountryId(guestCountryId.intValue());
				}
				Long guestProvinceId = (Long)result.get("guest_province_id");
				if(null!=guestProvinceId){
					obj.setGuestProvinceId(guestProvinceId.intValue());
				}
				Long guestCityId = (Long)result.get("guest_city_id");
				if(null!=guestCityId){
					obj.setGuestCityId(guestCityId.intValue());
				}
				if(StringUtils.validNull((String)result.get("guest_address"))){
					obj.setGuestAddress(result.get("guest_address").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_account"))){
					obj.setGuestAccount(result.get("guest_account").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_account_bank"))){
					obj.setGuestAccountBank(result.get("guest_account_bank").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_contract_no"))){
					obj.setGuestContractNo(result.get("guest_contract_no").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_email"))){
					obj.setGuestEmail(result.get("guest_email").toString());
				}
				if(StringUtils.validNull((String)result.get("guest_status"))){
					obj.setGuestStatus(result.get("guest_status").toString());
				}
				if(StringUtils.validNull((String)result.get("brand_name"))){
					obj.setBrandName(result.get("brand_name").toString());
				}
			}
		return obj;
	}

	public boolean validatorPwd(String guest_id, String guest_pwd) throws Exception {
		boolean flag = false;
		String sql = " SELECT COUNT(1) FROM beiker_guest_info WHERE guest_id = ? AND guest_pwd = ? ";
		Object[] params = new Object[]{guest_id,StringUtils.md5(guest_pwd)};
		int[] types = new int[]{Types.VARCHAR,Types.VARCHAR};
		int result = this.getJdbcTemplate().queryForInt(sql, params, types);
		if(result>0){
			flag = true;
		}
		return flag;
	}
		
}
