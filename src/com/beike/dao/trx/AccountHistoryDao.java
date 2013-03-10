package com.beike.dao.trx;

import java.util.List;

import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: AccountHistoryService.java
 * @Package com.beike.dao.trx
 * @Description: 帐务历史DAO接口
 * @author wh.cheng@sinobogroup.com
 * @date May 9, 2011 10:31:04 AM
 * @version V1.0
 */
public interface AccountHistoryDao extends GenericDao<AccountHistory, Long> {
	public void updateAccountHistory(AccountHistory actHistry) throws StaleObjectStateException;

	public AccountHistory findById(Long id);

	public List<AccountHistory> findAll();

	public void addAccountHistory(AccountHistory actHistory);
	
	/**
	 * 根据bizId和帐务历史类型查询历史发生的payment
	 * @param bizId
	 * @param actHistoryType
	 * @return
	 */
	public AccountHistory findBybizIdAndType(Long bizId,ActHistoryType actHistoryType);
	
	
	public int findRowsByUserId(Long userId,String qryType);
	
	public List<AccountHistory> listHis(Long userId,int startRow, int pageSize,String viewType);

	public List<TrxorderGoods> findRabateByTrxId(long trxId);
	
	public List<String> findAccIdByUserId(Long userId);
	
	/**
	 * 根据id查询交易订单表beiker_trxorder
	 * @param trxId 交易ID
	 * @return 返回订单对象列表
	 */
	public List<TrxOrder> findTrxOrderObjById(Long trxId);
	
	/**
	 * 根据account_id查询beiker_accounthistory表
	 * @param idStr account_id集合
	 * @param startRow 开始行号，用于分页
	 * @param pageSize 一页显示数量
	 * @return 返回历史记录对象列表
	 */
	public List<AccountHistory> findAccounthistoryByAccId(String idStr);
	
	public List<RefundRecord> findRefundInfoByTrxOrderId(Long trxOrderId);
	
	/**
	 * 根据trxorder_id查询goodsId
	 */
	public List<TrxorderGoods> findGoodsIdByTrxOrderId(Long trxOrderId);
	
	/**
	 * 根据trxorder_id查询goodsId
	 */
	public List<TrxorderGoods> findGoodsById(Long id);
	/**
	 * 根据accountId和actHistoryType查询账户历史获取
	 */
	public List<AccountHistory> findAccountIdByActType(Long accountId,String actHistoryType);
}
