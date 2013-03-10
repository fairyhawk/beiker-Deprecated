package com.beike.dao.background.guest;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.background.guest.GuestContact;
import com.beike.form.background.guest.GuestContactForm;

/**
 * 
 * Title : 	GuestContactDao
 * <p/>
 * Description	: 客户联系人信息访问数据接口
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
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
public interface GuestContactDao extends GenericDao<GuestContact,Long> {

	/**
	 * Description : 添加客户联系人
	 * @param guestContactFormList
	 * @return
	 * @throws Exception
	 */
	public String addGuestContact(List<GuestContactForm> guestContactFormList) throws Exception;
	
	/**
	 * Description : 查询客户联系人信息
	 * @param guestContactForm
	 * @return java.util.List<guestContact>
	 * @throws Exception
	 */
	public List<GuestContact> queryGuestContactByConditions(GuestContactForm guestContactForm) throws Exception;
	
	/**
	 * Description : 修改客户联系人信息
	 * @param guestContactForm
	 * @return
	 * @throws Exception
	 */
	public String editGuestContact(GuestContactForm guestContactForm) throws Exception;
	
	/**
	 * Description : 删除客户联系人
	 * @param guestId
	 * @return
	 * @throws Exception
	 */
	public String delGuestContact(String guestContactId) throws Exception;
	
	/**
	 * Description : 校验手机号码是否重复
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public boolean validatorMobile(String mobile) throws Exception;
	
	/**
	 * Description : 校验电话号码是否重复
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public boolean validatorPhone(String phone) throws Exception;
}
