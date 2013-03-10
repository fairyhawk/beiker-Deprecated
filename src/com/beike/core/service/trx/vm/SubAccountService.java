package com.beike.core.service.trx.vm;

import java.util.Date;
import java.util.List;

import com.beike.common.bean.trx.NotChangeParam;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;

/**
 * @Title: SubAccountService
 * @Package com.beike.core.service
 * @Description: 用户虚拟款项账户
 * @author wh.cheng@sinobogroup.com
 * @date May 3, 2011 6:00:18 PM
 * @version V1.0
 */
public interface SubAccountService {

	/**
	 * 子账户创建.
	 * 
	 * @param subAccount
	 * @return
	 * @throws AccountException
	 */
	public SubAccount create(Long userId, Long accountId, Long vmAccountId,
			Date loseDate) throws AccountException;

	/**
	 * 根据子账户id和个人总账户ID组合获取账户
	 * 
	 * @param actId
	 * @param vmActId
	 * @return
	 */
	public SubAccount findByIdAndActId(Long id, Long actId);

	/**
	 * 根据总账户号和虚拟款项ID获取子账户列表
	 * 
	 * @param actId
	 * @param vmId
	 * @return
	 */
	public SubAccount findByActIdAndVmId(Long actId, Long vmId);

	/**
	 * 虚拟款项取消扣款
	 * 
	 * @param subAccount
	 * @param account
	 * @param trxAmount
	 * @param actHistoryType
	 * @param trxId
	 * @param trxOrderId
	 * @param trxDate
	 * @param description
	 * @param isDisplay
	 * @param bizType
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void debitByCancel(SubAccount subAccount, Account account,
			double trxAmount, ActHistoryType actHistoryType, Long trxId,
			Long trxOrderId, Date trxDate, String description,
			boolean isDisplay, String bizType,boolean isLost) throws AccountException,
			StaleObjectStateException;

	/**
	 * 支付成功子账户扣款
	 * 将按照子账户最先过期的商品进行扣款。
	 * 如果priorityVmActIds（优先扣款虚拟账户）不为空，则优先先扣该数组
	 * @throws StaleObjectStateException
	 */
	public NotChangeParam debitByPaySuc(Account account, double trxAmount, Long bizId,Long trxOrderId,
			String trxOrderReqId, Date trxDate,Long priorityVmActIds[],String description) 
			throws AccountException,StaleObjectStateException;

	/**
	 * 下发入款
	 * 
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void creditByDis(SubAccount subAccount, Account account,
			double trxAmount, ActHistoryType actHistoryType, Long trxId,
			Long trxOrderId, Date trxDate, String description,
			boolean isDisplay, String bizType) throws AccountException,
			StaleObjectStateException;

	/**
	 * 账户退款入款
	 * 
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 * @throws VmAccountException
	 */
	public void creditByRefund(Long actId, double refundAmount,
			Long rudDetailId, Long trxOrderId, String description)
			throws AccountException, StaleObjectStateException,
			VmAccountException;

	/**
	 * 根据过期时间获取子账户
	 * 
	 * 
	 * @return
	 */
	public List<SubAccount> findByLose();

	/**
	 *过期子账户更新子账户金额为0
	 * 
	 * 
	 * @return
	 * @throws AccountException
	 */
	public void cancelLose(SubAccount subAccount)
			throws StaleObjectStateException, AccountException;
	
	/**
	 * 根据账户ID查询子账户信息
	 * @param actId
	 * @param subSuffix
	 * @return
	 */
	public List<SubAccount> findSubAccountList(Long actId,String subSuffix);
	/**
	 * 退款时优惠券下发入款
	 * @param subAccount
	 * @param account
	 * @param trxAmount
	 * @param actHistoryType
	 * @param trxId
	 * @param trxOrderId
	 * @param trxDate
	 * @param description
	 * @param isDisplay
	 * @param bizType
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void creditByRefundCoupon (SubAccount subAccount, Account account,
            double trxAmount, ActHistoryType actHistoryType, Long trxId,
            Long trxOrderId, Date trxDate, String description,
            boolean isDisplay, String bizType)throws AccountException,
            StaleObjectStateException ;
	/**
	 * 优惠券不退款，金额扣除
	 * @param subAccount 子账户
	 * @param account vc账户
	 * @param trxAmount 扣款金额
	 * @param actHistoryType 类型为NO_COUPON
	 * @param trxId 退款id
	 * @param trxOrderId 订单id
	 * @param trxDate 
	 * @param description
	 * @param isDisplay
	 * @param bizType
	 * @param isLost
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public void debitByCancelCouponRefund(SubAccount subAccount, Account account,
            double trxAmount, ActHistoryType actHistoryType, Long trxId,
            Long trxOrderId, Date trxDate, String description,
            boolean isDisplay, String bizType,boolean isLost) throws AccountException,
            StaleObjectStateException;
}
