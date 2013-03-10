package com.beike.core.service.trx.vm;

import java.util.Map;

import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.vm.VmAccount;
import com.beike.common.enums.vm.VmAccountType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.VmAccountException;

/**
 * <p>
 * Title:虚拟款项账户创建、追加、下发Service
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
 * @date 2011-11-16 14:14:07s
 * @author wenhua.cheng
 * @version 1.0
 */

public interface VmAccountService {

	/**
	 * 虚拟款项账户建立
	 * 
	 * @throws AccountException
	 * @throws VmAccountException
	 * @throws StaleObjectStateException
	 * @throws NumberFormatException
	 * @throws TrxOrderException 
	 */
	public Long createVmAccount(VmAccountParamInfo vmAccountParamInfo)
			throws AccountException, VmAccountException, NumberFormatException,
			StaleObjectStateException, TrxOrderException;

	/**
	 * 虚拟款项账户余额追加
	 * 
	 * @throws AccountException
	 * @throws VmAccountException
	 * @throws NumberFormatException
	 * @throws StaleObjectStateException
	 */
	public void pursueVmAccount(VmAccountParamInfo vmAccountParamInfo)
			throws AccountException, NumberFormatException, VmAccountException,
			StaleObjectStateException;

	/**
	 * 虚拟款项下发
	 * 
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws NumberFormatException
	 * @throws StaleObjectStateException
	 */
	public Long dispatchVm(VmAccountParamInfo vmAccountParamInfo)
			throws VmAccountException, NumberFormatException, AccountException,
			StaleObjectStateException;

	
	/**
	 * 虚拟账号异步扣款操作
	 * 虚拟账号再做扣款的时候，如果发送乐观锁异常，则忽略（不对虚拟账户扣款，该由定时来批量进行扣款操作），目的解决高并发单点瓶颈
	 * 返回true，表示实时扣款成功，返回false表示实时扣款发生乐观锁，需要后续对vmAccount扣款。
	 * 无论发挥true或false，对账户充值不产生影响
	 * @param vmAccountParamInfo
	 * @return
	 * @throws VmAccountException
	 * @throws NumberFormatException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public boolean dispatchVmForSync(VmAccountParamInfo vmAccountParamInfo) throws VmAccountException, NumberFormatException, AccountException,StaleObjectStateException ;
	/**
	 * 虚拟款项下发（千品卡充值）
	 * 
	 * @throws Exception
	 * 
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws NumberFormatException
	 * @throws StaleObjectStateException
	 */
	public int dispatchVmForCard(VmAccountParamInfo vmAccountParamInfo);
	


	/**
	 * 虚拟款项下发（生吞异常）
	 * 
	 * @throws Exception
	 * 
	 * @throws VmAccountException
	 * @throws AccountException
	 * @throws NumberFormatException
	 * @throws StaleObjectStateException
	 */
	public int dispatchVmForVou(VmAccountParamInfo vmAccountParamInfo);

	/**
	 * 扣款
	 * 
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */

	public Long debit(VmAccount vmAccount, double amount, String requestId,
			VmAccountType vmAccountType, Long accountId, Long subAccountId,
			Long operatorId) throws AccountException, StaleObjectStateException;

	/**
	 * 入款
	 * 
	 * @throws AccountException
	 * @throws VmAccountException
	 * @throws StaleObjectStateException
	 */
	public void credit(VmAccount vmAccount, double amount, String requestId,
			VmAccountType vmAccountType, Long operatorId)
			throws AccountException, VmAccountException,
			StaleObjectStateException;

	public VmAccount findById(Long id);
	
	/**
	 * 根据主键ID查询虚拟账户信息
	 * @param id
	 * @return
	 */
	public Map<Long, String> findVmAccount(Long id);

	/**
	 * 异步扣款
	 * @param vmAccountId
	 * @param amount
	 */
	public void debitForAsyn(VmAccount vmAccount,double amount,VmAccountType vmAccountType,String requestId)  throws AccountException,StaleObjectStateException;
}
