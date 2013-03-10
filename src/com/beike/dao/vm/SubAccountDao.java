package com.beike.dao.vm;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.vm.SubAccount;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * <p>
 * Title:子商户接口
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
public interface SubAccountDao extends GenericDao<SubAccount, Long> {

	/**
	 * 根据主键id，表后缀查询子账户实体 renli.yu
	 * 
	 * @param id
	 * @return
	 */
	public SubAccount findById(Long id, String subSuffix);

	/**
	 * 根据主键id，表后缀以及虚拟款项ID查询子账户实体
	 * 
	 * @param id
	 * @return
	 */
	public SubAccount findByActIdAndVmId(Long id, Long vmId, String subSuffix);

	/**
	 * 根据个人账户账户查询子账户列表
	 * 
	 * @param id
	 * @param subSuffix
	 * @return
	 */
	public List<SubAccount> findByActId(Long actId, String subSuffix);

	/**
	 * 根据表后缀更新子账户实体
	 * 
	 * @param vmAccount
	 * @throws StaleObjectStateException 
	 */
	public void updateSubAccount(SubAccount subAccount, String subSuffix) throws StaleObjectStateException;

	/**
	 * 根据表后缀新添加子账户实体
	 * 
	 * @param vmAccount
	 */
	public Long addSubAccount(SubAccount subAccount, String subSuffix);
	
	/**
	 * 根据过期时间查询出已过期子账户信息
	 * 
	 * @param vmAccount
	 */
	public List<SubAccount> findByLose(Date date);
	
	/**
	 * 根据虚拟账户id查询出所有虚拟子账户信息
	 * @return
	 */
	public List<SubAccount> findByVmAccountId(Long accountId);
	
	/**
	 *  账户余额提醒： 根据个人账户查询30内要过期的 余额提醒列表
	 * 
	 * @param id
	 * @param subSuffix
	 * @return
	 */
	public List<SubAccount> findRemindListByActId(Long actId, String subSuffix,Date beginDate,Date endDate);
	
	/**
	 * 获取每个虚拟款项表总数量值
	 * @param accountId
	 * @param i
	 * @return
	 */
	public int findByVmAccountIdCount(Long accountId,int i);
	
	/**
	 * 获取每个虚拟款项表数据
	 * @param accountId
	 * @param i
	 * @param startCount
	 * @param endCount
	 * @return
	 */
	public List<SubAccount> findByVmAccountId(Long accountId,int i,int startCount,int endCount);
	
	/**
	 * 获取即将过期的虚拟款项数据
	 * @param date
	 * @param i
	 * @param startCount
	 * @param endCount
	 * @return
	 */
	public List<SubAccount> findByLose(Date date,int i,int startCount,int endCount) ;
	
	/**
	 * 获取即将过期的虚拟款项表总数量值
	 * @param date
	 * @param i
	 * @return
	 */
	public int findByLoseCount(Date date,int i) ;

}
