package com.beike.dao.background.guest.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.guest.GuestContactDao;
import com.beike.entity.background.guest.GuestContact;
import com.beike.form.background.guest.GuestContactForm;
import com.beike.util.StringUtils;

/**
 * Title : 	GuestCotactDaoImpl
 * <p/>
 * Description	:	客户联系人数据访问实现
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
@Repository("guestContactDao")
public class GuestCotactDaoImpl extends GenericDaoImpl<GuestContact,Long> implements
		GuestContactDao {

	
	public String addGuestContact(List<GuestContactForm> guestContactFormList)
			throws Exception {
		final List<GuestContactForm> contactFormList = guestContactFormList; 
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_guest_contact_info (contact_cn_name,contact_email,contact_telephone,");
		sql.append("contact_mobile,contact_status,guest_id) VALUES (?,?,?,?,?,?) ");
		int[] count = this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter()
		{ 
			public void setValues(PreparedStatement ps,int i) throws SQLException {
				GuestContactForm guestContactForm = contactFormList.get(i);
				ps.setString(1, guestContactForm.getContactCnName());
				ps.setString(2, guestContactForm.getContactEmail());
				ps.setString(3, guestContactForm.getContactTelephone());
				ps.setString(4, guestContactForm.getContactMobile());
				ps.setString(5, guestContactForm.getContactStatus());
				ps.setInt(6, guestContactForm.getGuestId());
			}
			 public int getBatchSize() 
			   { 
				   return contactFormList.size(); 
			   } 
		});
		return String.valueOf(count.length);
	}

	
	@SuppressWarnings("unchecked")
	public List<GuestContact> queryGuestContactByConditions(
			GuestContactForm guestContactForm) throws Exception {
		List tempList = null;
		List<GuestContact> guestContactList = new ArrayList<GuestContact>();
		String sql = "SELECT contact_id,contact_cn_name,contact_email,contact_telephone,contact_mobile FROM beiker_guest_contact_info WHERE guest_id = ? AND contact_status = ? ";
		Object[] params = new Object[]{guestContactForm.getGuestId(),guestContactForm.getContactStatus()};
		int[] types = new int[]{Types.INTEGER,Types.VARCHAR};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			guestContactList = this.convertResultToObjectList(tempList);
		}
		return guestContactList;
	}

	
	public String editGuestContact(GuestContactForm guestContactForm)
			throws Exception {
		
		return null;
	}

	
	public String delGuestContact(String guestContactId) throws Exception {
		String sql = "DELETE FROM beiker_guest_contact_info WHERE guest_id = ? ";
		Object[] params = new Object[]{guestContactId};
		int[] types = new int[]{Types.INTEGER};
		int count = this.getJdbcTemplate().update(sql, params, types);
		return String.valueOf(count);
	}
	
	
	public boolean validatorMobile(String mobile) throws Exception {
		boolean flag = false;
		String sql = "SELECT COUNT(1) FROM beiker_guest_contact_info WHERE contact_mobile= ? ";
		Object[] params = new Object[]{mobile};
		int[] types = new int[]{Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql,params,types);
		if(count>0){
			flag = true;
		}
		return flag;
	}
	
	
	public boolean validatorPhone(String phone) throws Exception {
		boolean flag = false;
		String sql = "SELECT COUNT(1) FROM beiker_guest_contact_info WHERE substring_index(contact_telephone,'-',2)  = ? ";
		Object[] params = new Object[]{phone};
		int[] types = new int[]{Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql, params, types);
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
	private List<GuestContact> convertResultToObjectList(List results) throws Exception{
        List<GuestContact> objList = new ArrayList<GuestContact>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                GuestContact guestContact = this.convertResultMapToObject(result);
                objList.add(guestContact);
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
	private GuestContact convertResultMapToObject(Map result) throws Exception{
		GuestContact obj = new GuestContact();
			if (result != null) {
				Long contactId = (Long)result.get("contact_id");
				if(null!=contactId){
					obj.setContactId(contactId.intValue());
				}
				if(StringUtils.validNull((String)result.get("contact_cn_name"))){
					obj.setContactCnName(result.get("contact_cn_name").toString());
				}
				if(StringUtils.validNull((String)result.get("contact_email"))){
					obj.setContactEmail(result.get("contact_email").toString());
				}
				if(StringUtils.validNull((String)result.get("contact_telephone"))){
					obj.setContactTelephone(result.get("contact_telephone").toString());
				}
				if(StringUtils.validNull((String)result.get("contact_mobile"))){
					obj.setContactMobile(result.get("contact_mobile").toString());
				}
			}
		return obj;
	}

}
