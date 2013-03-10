package com.beike.dao.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.Account;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: AccountDao.java
 * @Package com.beike.dao.trx
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 4:38:03 PM
 * @version V1.0
 */
public interface AccountDao extends GenericDao<Account, Long> {

	public void updateAccount(Account account) throws StaleObjectStateException;

	public Account findById(Long id);

	public List<Account> findAll();

	public Long addAccount(Account account);

	public Account findByUserIdAndTypeStatus(Long userId,
			AccountType accountType, AccountStatus actStatus);

	public Account findByUserIdAndType(Long userId, AccountType accountType);

	public List<Account> findByUserId(Long userId);

	public void updateActBalance(Long id, double newBalance, Date lastDate,
			Long version) throws StaleObjectStateException;

	public double findBalanceByStatus(Long userId, AccountStatus actStatus);

	/**
	 * 根据用户登录名查询用户ID
	 * 
	 * @param loginName
	 * @return
	 */
	public Long findUserIdByLoginName(String loginName);
	
	/**
	 * 根据账户id查询和状态查询账户
	 * @param idStr
	 * @return
	 */
	public List<Account> findActListByIdAndStatus(String idStr,String actStatus);

}
