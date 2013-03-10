package com.beike.dao.trx.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.Account;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.AccountDao;
import com.beike.util.Amount;
import com.beike.util.EnumUtil;

/**
 * @Title: AccountDaoImpl.java
 * @Package com.beike.dao.trx.impl
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 4:46:58 PM
 * @version V1.0
 */
@Repository("accountDao")
public class AccountDaoImpl extends GenericDaoImpl<Account, Long> implements
		AccountDao {

	public Long addAccount(Account account) {
		if (account == null) {

			throw new IllegalArgumentException();
		}

		String istSql = "insert beiker_account(balance,account_type,account_status,create_date,last_update_date,forzen_amount,user_id)"
				+ "value(?,?,?,?,?,?,?)";
		getSimpleJdbcTemplate().update(istSql, account.getBalance(),
				EnumUtil.transEnumToString(account.getAccountType()),
				EnumUtil.transEnumToString(account.getAccountStatus()),
				account.getCreateDate(), account.getLastUpdateDate(),
				account.getForzenAmount(), account.getUserId());
		Long actId = getLastInsertId();
		return actId;

	}

	public List<Account> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Account findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,version,user_id,create_date,last_update_date,balance,forzen_amount,account_type,account_status from beiker_account where id = ?";
			List<Account> accountList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);

			if (accountList.size() > 0) {
				return accountList.get(0);
			}
			return null;
		}

	}

	protected class RowMapperImpl implements ParameterizedRowMapper<Account> {
		public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
			Account account = new Account();
			account.setId(rs.getLong("id"));
			account.settVersion(rs.getLong("version"));
			account.setAccountStatus(EnumUtil.transStringToEnum(
					AccountStatus.class, rs.getString("account_status")));
			account.setAccountType(EnumUtil.transStringToEnum(
					AccountType.class, rs.getString("account_type")));
			// account.setActSn(rs.getString("act_sn"));
			account.setBalance(rs.getDouble("balance"));
			account.setCreateDate(rs.getTimestamp("create_date"));
			account.setForzenAmount(rs.getDouble("forzen_amount"));
			account.setLastUpdateDate(rs.getTimestamp("last_update_date"));
			account.setUserId(rs.getLong("user_id"));
			return account;
		}
	}

	public void updateAccount(Account account) throws StaleObjectStateException {
		if (account == null) {
			return;
		} else {

			String sql = "update  beiker_account set version=?, balance=?,account_type=?,account_status=?,create_date=?,"
					+ "last_update_date=?,forzen_amount=?,user_id=? where id=? and version=?";
			int result = getSimpleJdbcTemplate().update(sql,
					account.getVersion() + 1, account.getBalance(),
					EnumUtil.transEnumToString(account.getAccountType()),
					EnumUtil.transEnumToString(account.getAccountStatus()),
					account.getCreateDate(), account.getLastUpdateDate(),
					account.getForzenAmount(), account.getUserId(),
					account.getId(), account.getVersion());

			if (result == 0) {
				throw new StaleObjectStateException(
						BaseException.OPTIMISTIC_LOCK_ERROR);
			}

		}
	}

	public Account findByUserIdAndTypeStatus(Long userId,
			AccountType accountType, AccountStatus actStatus) {
		if (userId == null || accountType == null) {
			return null;
		} else {
			String sql = "select id,version,user_id,create_date,last_update_date,balance,forzen_amount,account_type,account_status from beiker_account where user_id=? and account_type=?  and account_status=?";
			List<Account> accountList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), userId, accountType.name(),
					actStatus.name());

			if (accountList.size() > 0) {
				return accountList.get(0);
			}
			return null;
		}
	}

	public Account findByUserIdAndType(Long userId, AccountType accountType)

	{

		if (userId == null || accountType == null) {
			return null;
		} else {
			String sql = "select id,version,user_id,create_date,last_update_date,balance,forzen_amount,account_type,account_status from beiker_account where user_id=? and account_type=?";
			List<Account> accountList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), userId, accountType.name());

			if (accountList.size() > 0) {
				return accountList.get(0);
			}
			return null;
		}

	}

	public List<Account> findByUserId(Long userId)

	{

		if (userId == null) {
			return null;
		} else {
			String sql = "select id,version,user_id,create_date,last_update_date,balance,forzen_amount,account_type,account_status from beiker_account where user_id=?";
			List<Account> accountList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), userId);

			if (accountList != null && accountList.size() > 0) {
				return accountList;
			}
			return null;
		}

	}

	public void updateActBalance(Long id, double newBalance, Date lastDate,
			Long version) throws StaleObjectStateException {
		String sql = "update beiker_account set balance=? ,version=version+1, last_update_date=?  where id=?  and version=?";
		int result = getSimpleJdbcTemplate().update(sql, newBalance, lastDate,
				id, version);
		if (result == 0) {

			throw new StaleObjectStateException(
					BaseException.OPTIMISTIC_LOCK_ERROR);
		}

	}

	public double findBalanceByStatus(Long userId, AccountStatus actStatus) {
		String sql = "select sum(balance) as sumBalance,sum(forzen_amount) as forzenAmount from  beiker_account where  account_status=? and user_id=?";

		Map<String, Object> map = getSimpleJdbcTemplate().queryForMap(sql,
				actStatus.name(), userId);

		BigDecimal sumBalance = (BigDecimal) map.get("sumBalance");
		BigDecimal forzenAmount = (BigDecimal) map.get("forzenAmount");
		double sunBanlanceDou = 0.0;
		double forzenAmountDou = 0.0;
		if (sumBalance != null) {
			sunBanlanceDou = sumBalance.doubleValue();

		}
		if (forzenAmount != null) {

			forzenAmountDou = forzenAmount.doubleValue();
		}

		return Amount.cutOff(sunBanlanceDou - forzenAmountDou, 2);

	}
	

	
	/**
	 * 根据账户id查询和状态查询账户
	 * @param idStr
	 * @return
	 */
	public List<Account> findActListByIdAndStatus(String idStr,String actStatus){
		StringBuilder   sqlSb=new  StringBuilder();
		sqlSb.append("select id,version,user_id,create_date,last_update_date,balance,forzen_amount,account_type,account_status from beiker_account where id in(");
		sqlSb.append(idStr);
		sqlSb.append(")");
		sqlSb.append(" and account_status=?");
	
		List<Account> accountList = getSimpleJdbcTemplate().query(sqlSb.toString(),new RowMapperImpl(), actStatus);

		return accountList;

	}
	

	/**
	 * 根据用户登录名查询用户ID
	 * 
	 * @param loginName
	 * @return
	 */
	public Long findUserIdByLoginName(String loginName) {
		Long userId = 0L;
		Map<String, Object> userIdMap = null;
		if (loginName == null || loginName == "") {
			throw new IllegalArgumentException("loginName is  not null");
		}

		String sql = "select user_id  as userId from beiker_user where email= ?";
		try {
			userIdMap = getSimpleJdbcTemplate().queryForMap(sql, loginName);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;

		}
		if (userIdMap != null && !userIdMap.isEmpty()) {
			userId = (Long) userIdMap.get("userId");
			return userId;
		}

		return 0L;

	}
}
