package com.beike.dao.vm;

import com.beike.common.entity.vm.VmAccountHistory;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * <p>
 * Title:虚拟账户历史记录接口
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
public interface VmAccountHistoryDao extends GenericDao<VmAccountHistory, Long> {

	/**
	 * 根据主键id查询虚拟账户历史记录实体 renli.yu
	 * 
	 * @param id
	 * @return
	 */
	public VmAccountHistory findById(Long id);

	/**
	 * 根据请求号查询记录数
	 * 
	 * @param requestId
	 * @return
	 */
	public Long findByTypeAndReqId(String vmAccountType, String requestId);
	
	/**
	 * 根据请求号查询记录,查询结果必须只为一条，否则返回null
	 * @param vmAccountType
	 * @param requestId
	 * @return
	 */
	public VmAccountHistory findVmActHisByTypeAndReqId(String vmAccountType, String requestId);

	/**
	 * 更新虚拟账户历史记录
	 * 
	 * @param vmAccount
	 * @throws StaleObjectStateException
	 */
	public void updateVmAccountHistory(VmAccountHistory vmAccountHistory)
			throws StaleObjectStateException;

	/**
	 * 新添加虚拟账户历史记录
	 * 
	 * @param vmAccount
	 */
	public Long addVmAccountHistory(VmAccountHistory vmAccountHistory);

}
