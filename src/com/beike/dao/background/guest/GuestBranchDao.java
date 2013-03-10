package com.beike.dao.background.guest;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.background.guest.GuestBranch;
import com.beike.form.background.guest.GuestBranchForm;
/**
 * 
 * Title : 	GuestBranchDao
 * <p/>
 * Description	: 客户分店信息访问数据接口
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
public interface GuestBranchDao extends GenericDao<GuestBranch,Long> {

	/**
	 * Description : 新增客户分店信息
	 * @param guestBranchForm
	 * @return
	 * @throws Exception
	 */
	public String addGuestBranch(GuestBranchForm guestBranchForm) throws Exception;
	
	/**
	 * Description : 按条件查询客户分店信息
	 * @param guestBranchForm
	 * @param startRow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<GuestBranch> queryGuestBranchConditions(GuestBranchForm guestBranchForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 按条件查询客户信息总条数
	 * @param guestBranchForm
	 * @return
	 * @throws Exception
	 */
	public int queryGuestBranchCountConditions(GuestBranchForm guestBranchForm) throws Exception;
	
	/**
	 * Description : 根据客户分店id查询分店信息
	 * @param branchId
	 * @return
	 * @throws Exception
	 */
	public GuestBranch queryGuestBranchById(String branchId) throws Exception;
	
	/**
	 * Description : 修改客户分店信息
	 * @param guestBranchForm
	 * @return
	 * @throws Exception
	 */
	public String editGuestBranch(GuestBranchForm guestBranchForm) throws Exception;
	
	/**
	 * Description : 根据客户id查询客户分店信息
	 * @param guestBranchForm
	 * @return
	 * @throws Exception
	 */
	public List<GuestBranch> queryBranchInfo(GuestBranchForm guestBranchForm) throws Exception;
	
	/**
	 * Description : 验证分店名称是否重复
	 * @param guestBranchForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorBranchName(GuestBranchForm guestBranchForm) throws Exception;
}
