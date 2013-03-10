package com.beike.service.background.guest.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.guest.GuestBranchDao;
import com.beike.entity.background.guest.GuestBranch;
import com.beike.form.background.guest.GuestBranchForm;
import com.beike.service.background.guest.GuestBranchService;
/**
 * Title : 	GuestBranchServiceImpl
 * <p/>
 * Description	:客户分店服务实现类
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
 * <pre>1     2011-06-07   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-07  
 */
@Service("guestBranchService")
public class GuestBranchServiceImpl implements GuestBranchService {

	/*
	 * @see com.beike.service.background.guest.GuestBranchService#addGuestBranch(com.beike.form.background.guest.GuestBranchForm)
	 */
	public String addGuestBranch(GuestBranchForm guestBranchForm)
			throws Exception {
		String result = null;
		result = guestBranchDao.addGuestBranch(guestBranchForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.guest.GuestBranchService#queryGuestBranchConditions(com.beike.form.background.guest.GuestBranchForm, int, int)
	 */
	public List<GuestBranch> queryGuestBranchConditions(
			GuestBranchForm guestBranchForm,int startRow,int pageSize) throws Exception {
		List<GuestBranch> guestBranchList = null;
		guestBranchList = guestBranchDao.queryGuestBranchConditions(guestBranchForm,startRow,pageSize);
		return guestBranchList;
	}
	
	/*
	 * @see com.beike.service.background.guest.GuestBranchService#queryGuestBranchCountConditions(com.beike.form.background.guest.GuestBranchForm)
	 */
	public int queryGuestBranchCountConditions(
			GuestBranchForm guestBranchForm) throws Exception {
		int count = 0;
		count = guestBranchDao.queryGuestBranchCountConditions(guestBranchForm);
		return count;
	}
	
	/*
	 * @see com.beike.service.background.guest.GuestBranchService#queryGuestBranchById(java.lang.String)
	 */
	public GuestBranch queryGuestBranchById(String branchId)
		throws Exception {
		GuestBranch guestBranch = null;
		guestBranch = guestBranchDao.queryGuestBranchById(branchId);
		return guestBranch;
	}

	/*
	 * @see com.beike.service.background.guest.GuestBranchService#editGuestBranch(com.beike.form.background.guest.GuestBranchForm)
	 */
	public String editGuestBranch(GuestBranchForm guestBranchForm)
			throws Exception {
		String result = null;
		result = guestBranchDao.editGuestBranch(guestBranchForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.guest.GuestBranchService#queryBranchInfo(com.beike.form.background.guest.GuestBranchForm)
	 */
	public List<GuestBranch> queryBranchInfo(GuestBranchForm guestBranchForm)
		throws Exception {
		List<GuestBranch> guestBranchList = null;
		guestBranchList = guestBranchDao.queryBranchInfo(guestBranchForm);
		return guestBranchList;
	}

	/*
	 * @see com.beike.service.background.guest.GuestBranchService#validatorBranchName(com.beike.form.background.guest.GuestBranchForm)
	 */
	public boolean validatorBranchName(GuestBranchForm guestBranchForm)
			throws Exception {
		boolean flag = false;
		flag = guestBranchDao.validatorBranchName(guestBranchForm);
		return flag;
	}
	@Resource(name="guestBranchDao")
	private GuestBranchDao guestBranchDao;


}
