package com.beike.dao.background.guest;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.background.guest.Guest;
import com.beike.form.background.admin.AdminUserForm;
import com.beike.form.background.guest.GuestForm;

/**
 * 
 * Title : 	GuestDao
 * <p/>
 * Description	: 客户信息访问数据接口
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
public interface GuestDao extends GenericDao<Guest,Long> {

	/**
	 * Description : 新增客户信息
	 * @param guestForm
	 * @return
	 * @throws Exception
	 */
	public String addGuest(GuestForm guestForm) throws Exception;
	
	/**
	 * Description : 验证客户名称是否重复
	 * @param guestName
	 * @return
	 * @throws Exception
	 */
	public boolean validatorGuestName(GuestForm guestForm) throws Exception;
	
	/**
	 * Description : 验证客户合同编号是否重复
	 * @param guestForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorGuestContractNo(GuestForm guestForm) throws Exception;
	
	/**
	 * Description : 查询客户信息
	 * @param guestForm
	 * @param startRow
	 * @param pageSize
	 * @return java.util.List<Brand>
	 * @throws Exception
	 */
	public List<Guest> queryGuestByConditions(GuestForm guestForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 查询状态为激活状态的客户条数
	 * @param guestForm
	 * @return
	 * @throws Exception
	 */
	public int queryGuestCountByConditions(GuestForm guestForm) throws Exception;
	
	/**
	 * Description : 根据guestId查询客户信息
	 * @param guestId
	 * @return
	 * @throws Exception
	 */
	public Guest queryBrandById(String guestId) throws Exception;
	
	/**
	 * Description : 修改客户信息
	 * @param guestForm
	 * @return
	 * @throws Exception
	 */
	public String editGuest(GuestForm guestForm) throws Exception;
	
	/**
	 * Description : 查询数据库中最大id(PK)
	 * @return
	 * @throws Exception
	 */
	public int queryGuestMaxId() throws Exception;
	
	/**
	 * Description : 校验用户名密码是否一致
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public boolean validatorPwd(String guest_id, String guest_pwd) throws Exception;
	
}
