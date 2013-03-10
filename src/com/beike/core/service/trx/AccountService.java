package com.beike.core.service.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.StaleObjectStateException;

/**
 * @Title: AccountService.java
 * @Package com.beike.core.service
 * @Description: 账户
 * @author wh.cheng@sinobogroup.com
 * @date May 3, 2011 6:00:18 PM
 * @version V1.0
 */
public interface AccountService {

	/**
	 * 账户创建.和用户模块解耦合。只要用户ID
	 * 
	 * @param account
	 * @return
	 * @throws AccountException
	 */
	public List<Account> create(Long userId) throws AccountException;

	/**
	 * 根据账户号获取账户
	 * 
	 * @param actId
	 * @return
	 */
	public Account findById(Long actId);

	/**
	 * 根据账户类型获取账户
	 * 
	 * @param user
	 * @param accountType
	 * @return
	 */
	public Account findByUserIdAndType(Long userId, AccountType accountType);

	/**
	 * 根据userId获取账户
	 * 
	 * @param userId
	 * @return
	 */
	public List<Account> findByUserId(Long userId);

	/**
	 * 获取总账户余额（扣除冻结金额）
	 * 
	 * @param user
	 * @return
	 */
	public double findBalance(Long userId) throws AccountException;
	
	/**
	 * 根据账户List或者当前可用金额(扣除冻结金额)
	 * @param accountList
	 * @return
	 */
	public double getCurrentBalance(List<Account> accountList);
	/**
	 * 按账户类型获取响应账户余额（扣除冻结金额）
	 * 
	 * @param user
	 * @return
	 */
	public double findBalanceByType(Long userId, AccountType accountType)
			throws AccountException;

	/**
	 * 根据accountType获取响应account
	 * @param accountList
	 * @param accountType
	 * @return
	 */
	public Account  getActByActType(List<Account>  accountList,AccountType accountType);
	/**
	 * 根据用户ID，查分页总数
	 * 
	 * @param userId
	 * @return
	 */
	public int findRowsByUserId(Long userId, String qryType);

	public List<AccountHistory> listHis(Long userId, int startRow,
			int pageSize, String viewType);

	/**
	 * 
	 */
	/**
	 * 扣款
	 * 
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */

	public void debit(Account account, double trxAmount,
			ActHistoryType actHistoryType, Long trxId, Long trxOrderId,
			Date trxDate, String description, boolean isDisplay, String bizType)
			throws AccountException, StaleObjectStateException;

	/**
	 * 入款
	 * 
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void credit(Account account, double trxAmount,
			ActHistoryType actHistoryType, Long trxId, Long trxOrderId,
			Date trxDate, String description, boolean isDisplay, String bizType)
			throws AccountException, StaleObjectStateException;
	
	/**
	 * 查询冻结状态账户对应IdList
	 * @param idStr
	 * @return
	 */
	public List<Long> findActIdListByIdAndStatus(String idStr,String actStatus);

}
