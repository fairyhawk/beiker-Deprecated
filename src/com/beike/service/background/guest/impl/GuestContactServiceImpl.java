package com.beike.service.background.guest.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.guest.GuestContactDao;
import com.beike.entity.background.guest.GuestContact;
import com.beike.form.background.guest.GuestContactForm;
import com.beike.service.background.guest.GuestContactService;
/**
 * Title : 	GuestContactServiceImpl
 * <p/>
 * Description	:客户联系人服务实现类
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
 * <pre>1     2011-06-03   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03  
 */
@Service("guestContactService")
public class GuestContactServiceImpl implements GuestContactService {

	
	public String addGuestContact(List<GuestContactForm> guestContactFormList) throws Exception {
		String result = null;
		result = guestContactDao.addGuestContact(guestContactFormList);
		return result;
	}

	
	public List<GuestContact> queryGuestContactByConditions(
			GuestContactForm guestContactForm) throws Exception {
		List<GuestContact> guestContactList = null;
		guestContactList = guestContactDao.queryGuestContactByConditions(guestContactForm);
		return guestContactList;
	}
	
	
	public String editGuestContact(GuestContactForm guestContactForm)
			throws Exception {
		String result = null;
		result = guestContactDao.editGuestContact(guestContactForm);
		return result;
	}
	
	
	public String delGuestContact(String guestId) throws Exception {
		String result = null;
		result = guestContactDao.delGuestContact(guestId);
		return result;
	}
	
	
	public boolean validatorMobile(String mobile) throws Exception {
		boolean flag = false;
		flag = guestContactDao.validatorMobile(mobile);
		return flag;
	}
	
	
	public boolean validatorPhone(String phone) throws Exception {
		boolean flag = false;
		flag = guestContactDao.validatorPhone(phone);
		return flag;
	}
	@Resource(name = "guestContactDao")
	private GuestContactDao guestContactDao;

}
