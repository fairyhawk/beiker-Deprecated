package com.beike.service.background.guest.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.guest.GuestDao;
import com.beike.entity.background.guest.Guest;
import com.beike.form.background.guest.GuestContactForm;
import com.beike.form.background.guest.GuestForm;
import com.beike.service.background.guest.GuestContactService;
import com.beike.service.background.guest.GuestService;
/**
 * Title : 	CompanyServiceImpl
 * <p/>
 * Description	:客户服务实现类
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
@Service("guestService")
public class GuestServiceImpl implements GuestService {

	
	public String addGuest(GuestForm guestForm,List<GuestContactForm> contactList) throws Exception {
		String result = null;
		result = guestDao.addGuest(guestForm);
		
		guestContactService.addGuestContact(contactList);
		return result;
	}

	
	public boolean validatorGuestName(GuestForm guestForm) throws Exception {
		boolean flag = false;
		flag = guestDao.validatorGuestName(guestForm);
		return flag;
	}
	
	
	public boolean validatorGuestContractNo(GuestForm guestForm)
		throws Exception {
		boolean flag = false;
		flag = guestDao.validatorGuestContractNo(guestForm);
		return flag;
	}
	
	
	public List<Guest> queryGuestByConditions(GuestForm guestForm,
			int startRow, int pageSize) throws Exception {
		List<Guest> guestList = null;
		guestList = guestDao.queryGuestByConditions(guestForm, startRow, pageSize);
		return guestList;
	}
	
	
	public int queryGuestCountByConditions(GuestForm guestForm) throws Exception {
		int count = 0;
		count = guestDao.queryGuestCountByConditions(guestForm);
		return count;
	}
	
	
	public Guest queryBrandById(String guestId) throws Exception {
		Guest guest = null;
		guest = guestDao.queryBrandById(guestId);
		return guest;
	}
	
	
	public String editGuest(GuestForm guestForm,List<GuestContactForm> contactList) throws Exception {
		String result = null;
		result = guestDao.editGuest(guestForm);
		
		guestContactService.delGuestContact(String.valueOf(guestForm.getGuestId()));
		
		guestContactService.addGuestContact(contactList);
		return result;
	}
	
	
	public int queryGuestMaxId() throws Exception {
		int pk = 0;
		pk = guestDao.queryGuestMaxId();
		return pk;
	}
	
	public boolean validatorPwd(String guest_id, String guest_pwd) throws Exception {
		boolean flag = false;
		flag = guestDao.validatorPwd(guest_id, guest_pwd);
		return flag;
	}
	
	@Resource(name = "guestDao")
	private GuestDao guestDao;
	@Resource(name="guestContactService")
	private GuestContactService guestContactService;
	
}
