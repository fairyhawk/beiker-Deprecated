package com.beike.core.service.trx.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.AccountService;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.AccountHistoryDao;
import com.beike.util.Amount;

/**
 * @Title: AccountServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 3:25:41 PM
 * @version V1.0
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private static final Log logger = LogFactory
			.getLog(AccountServiceImpl.class);
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountHistoryDao accountHistoryDao;


	public List<Account> create(Long userId) throws AccountException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is not null");
		}
		Account qryCashAccount = accountDao.findByUserIdAndType(userId,
				AccountType.CASH);
		Account qryVCAccount = accountDao.findByUserIdAndType(userId,
				AccountType.VC);
		if (qryCashAccount != null || qryVCAccount != null) {
			throw new AccountException(BaseException.ACCOUNT_HASED_EXIST);
		}
		Account cashAccount = new Account(userId, 0D, AccountType.CASH,
				AccountStatus.ACTIVE, new Date(), new Date());
		Account vcAccount = new Account(userId, 0D, AccountType.VC,
				AccountStatus.ACTIVE, new Date(), new Date());

		accountDao.addAccount(cashAccount);
		accountDao.addAccount(vcAccount);

		List<Account> accountList = new ArrayList<Account>();
		accountList.add(cashAccount);
		accountList.add(vcAccount);

		return accountList;
	}

	// 扣款
	public void debit(Account account, double trxAmount,
			ActHistoryType actHistoryType, Long trxId, Long trxOrderId,
			Date trxDate, String description, boolean isDisplay, String bizType)
			throws AccountException, StaleObjectStateException {

		if (!AccountStatus.ACTIVE.equals(account.getAccountStatus())) {
			throw new AccountException(BaseException.ACCOUNT_STATUS_INVALID); // 账户未被激活
		}
		double curBalance = account.getBalance();
		if (curBalance - trxAmount < 0)

			throw new AccountException(BaseException.ACCOUNT_NOT_ENOUGH);
		try {
			logger.info("debit befor:======balance:" + curBalance
					+ "=====trxAmount:" + trxAmount + "============");
			account.debit(trxAmount);
		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}

		accountDao.updateActBalance(account.getId(), account.getBalance(),
				new Date(), account.getVersion());

		AccountHistory actHistory = new AccountHistory(account.getBalance(),
				account.getId(), actHistoryType, bizType, trxAmount, trxId,
				trxOrderId);
		actHistory.setCreateDate(new Date());
		actHistory.setDispaly(isDisplay);
		accountHistoryDao.addAccountHistory(actHistory);

	}

	// 入款
	public void credit(Account account, double trxAmount,
			ActHistoryType actHistoryType, Long trxId, Long trxOrderId,
			Date trxDate, String description, boolean isDisplay, String bizType)
			throws AccountException, StaleObjectStateException {
		if (!AccountStatus.ACTIVE.equals(account.getAccountStatus())) {
			throw new AccountException(BaseException.ACCOUNT_STATUS_INVALID); // 账户未被激活
		}
		double curBalance = account.getBalance();
		try {
			logger.info("credit befor:======balance:" + curBalance
					+ "=====trxAmount:" + trxAmount + "============");
			account.credit(trxAmount);
		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}
		accountDao.updateActBalance(account.getId(), account.getBalance(),
				new Date(), account.getVersion());

		AccountHistory actHistory = new AccountHistory(account.getBalance(),
				account.getId(), actHistoryType, bizType, trxAmount, trxId,
				trxOrderId);
		actHistory.setDispaly(isDisplay);
		actHistory.setCreateDate(new Date());
		actHistory.setDescription(description);
		accountHistoryDao.addAccountHistory(actHistory);
	}

	public Account findById(Long actId) {

		return accountDao.findById(actId);
	}

	public double findBalance(Long userId) throws AccountException {

		return accountDao.findBalanceByStatus(userId, AccountStatus.ACTIVE);

	}
	
	/**
	 * 根据账户List或者当前可用金额(扣除冻结金额)
	 * @param accountList
	 * @return
	 */
	public double getCurrentBalance(List<Account> accountList){
		
		double currentBalance=0.0;
		
		for(Account acoount:accountList){
			Long actId=acoount.getId();
			double balance=acoount.getBalance();
			double forzenBlance=acoount.getForzenAmount();
			logger.info("++actId:"+actId+"+++balance:"+balance+"++forzenBlance:"+forzenBlance);
			if(AccountStatus.ACTIVE.equals(acoount.getAccountStatus())){
				
				double itemCurBalance=Amount.sub(acoount.getBalance(),acoount.getForzenAmount());
				logger.info("++actId:"+actId+"+++itemCurBalance:"+itemCurBalance);
				currentBalance=Amount.add(currentBalance, itemCurBalance);
				logger.info("++actId:"+actId+"+++currentBalance:"+currentBalance);
			}
			
		}
		logger.info("++++currentBalance:"+currentBalance);
		return currentBalance;
	}
	/**
	 * 根据accountType获取响应account
	 * @param accountList
	 * @param accountType
	 * @return
	 */
	public Account  getActByActType(List<Account>  accountList,AccountType accountType){
		
		for(Account account:accountList){
			
			if(accountType.equals(account.getAccountType())){
				
				return account;
			}
		}
		
		return null;
		
	}

	public Account findByUserIdAndType(Long userId, AccountType accountType) {

		return accountDao.findByUserIdAndType(userId, accountType);
	}

	public List<Account> findByUserId(Long userId) {

		return accountDao.findByUserId(userId);
	}

	public double findBalanceByType(Long userId, AccountType accountType)
			throws AccountException {
		Account cashAaccount = accountDao.findByUserIdAndTypeStatus(userId,
				AccountType.CASH, AccountStatus.ACTIVE);

		Account vcAaccount = accountDao.findByUserIdAndTypeStatus(userId,
				AccountType.VC, AccountStatus.ACTIVE);
		if (cashAaccount == null || vcAaccount == null) {
			throw new AccountException(BaseException.ACCOUNT_NOT_FOUND);

		}
		if (cashAaccount.getAccountType().equals(accountType)) {
			return cashAaccount.getBalance() - cashAaccount.getForzenAmount();
		}

		if (vcAaccount.getAccountType().equals(accountType)) {
			return vcAaccount.getBalance() - vcAaccount.getForzenAmount();
		}
		return 0D;

	}
	
	
	
	public int findRowsByUserId(Long userId, String qryType) {
		return accountHistoryDao.findRowsByUserId(userId, qryType);

	}



	public List<AccountHistory> listHis(Long userId, int startRow,
			int pageSize, String viewType) {

		return accountHistoryDao.listHis(userId, startRow, pageSize, viewType);
	}
	
	public List<Long> findActIdListByIdAndStatus(String idStr,String actStatus){
		List<Account> list = accountDao.findActListByIdAndStatus(idStr, actStatus);
		List<Long> actIdList = new ArrayList<Long>();
		if(list!=null&&list.size()>0){
			for(Account  actItem:list){
				actIdList.add(actItem.getId());
			}
		}
		return actIdList;
	}

}
