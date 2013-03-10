package com.beike.dao.vm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.common.entity.vm.VmAccount;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * <p>
 * Title:虚拟商户接口
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-11-16 14:14:07
 * @author renli.yu
 * @version 1.0
 */
public interface VmAccountDao extends GenericDao<VmAccount, Long> {

	/**
	 * 更新虚拟商户
	 * 
	 * @param vmAccount
	 * @throws StaleObjectStateException
	 */
	public void updateVmAccount(VmAccount vmAccount)
			throws StaleObjectStateException;

	/**
	 * 根据主键id查询虚拟商户
	 * 
	 * @param id
	 * @return
	 */
	public VmAccount findById(Long id);

	/**
	 * 新添加虚拟商户
	 * 
	 * @param vmAccount
	 */
	public Long addVmAccount(VmAccount vmAccount);

	/**
	 * 根据类别ID查询虚拟款项账户个数
	 */

	public Long findVmActCountBySortId(Long vmSortId);

	/**
	 * 根据过期时间查询3天和30天即将过期用户
	 */
	public List<Map<String, Object>> findLoseDate(Date sourceDate1,
			Date sourceDate2);
	
	/**
	 * 查询虚拟款项信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> findVmAccountById(Long id) ;
	
	/**
	 * 虚拟款项异步入账
	 * @param vmAccountId
	 * @param amount
	 */
	public void updateVmAccountForAsycAccount(Long vmAccountId,Double amount,Long version) throws StaleObjectStateException;

}
