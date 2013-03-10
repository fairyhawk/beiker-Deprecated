package com.beike.core.service.trx.vm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.NotChangeParam;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmCancelRecord;
import com.beike.common.entity.vm.VmTrxExtend;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.CancelType;
import com.beike.common.enums.vm.RelevanceType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.AccountHistoryDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.dao.vm.VmCancelRecordDao;
import com.beike.dao.vm.VmTrxExtendDao;
import com.beike.service.common.EmailService;
import com.beike.util.Amount;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;

/**
 * <p>
 * Title:用户虚拟子账户ServiceImpl
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
 * @author wenhua.cheng
 * @version 1.0
 */
@Service("subAccountService")
public class SubAccountServiceImpl implements SubAccountService {

	private final Log logger = LogFactory.getLog(SubAccountServiceImpl.class);
	@Autowired
	private SubAccountDao subAccountDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private AccountHistoryDao accountHistoryDao;

	@Autowired
	private VmCancelRecordDao vmCancelRecordDao;

	@Autowired
	private VmTrxExtendDao vmTrxExtendDao;

	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	public String alertVcActDebitEmail = propertyUtil
			.getProperty("vc_sub_account_debit_alert_email");

	@Autowired
	private EmailService emailService;

	// 扣款报警邮件模板
	public static final String DEBIT_VCACT_EMAIL_ALERT_TEMP = "DEBIT_VCACT_EMAIL_ALERT_TEMP";

	@Override
	public SubAccount create(Long userId, Long accountId, Long vmAccountId,
			Date loseDate) throws AccountException {

		SubAccount subAccount = findByActIdAndVmId(accountId, vmAccountId);

		if (subAccount == null) {// 此前不存在此子账户则创建
			// 组装子账户基本信息
			// 组装子账户信息
			SubAccount subNewAccount = new SubAccount(Long.valueOf(userId),
					accountId, Long.valueOf(vmAccountId), new Date(), loseDate);
			Long deliveryId = StringUtils.getDeliveryIdBase(accountId);// 获取子账户表名结尾值
			Long id = subAccountDao.addSubAccount(subNewAccount, String
					.valueOf(deliveryId));
			SubAccount sa = null;
			if (id != null) {
				sa = subAccountDao.findById(id, String.valueOf(deliveryId));
			}
			return sa; // 返回新创建的子账户
		}

		return subAccount;
	}

	/**
	 * 下发入款
	 * 
	 * @throws StaleObjectStateException
	 */
	@Override
	public void creditByDis(SubAccount subAccount, Account account,
			double trxAmount, ActHistoryType actHistoryType, Long trxId,
			Long trxOrderId, Date trxDate, String description,
			boolean isDisplay, String bizType) throws AccountException,
			StaleObjectStateException {
		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(account
				.getId()));// 子账户所在表后缀
		double beforeSubBalance = subAccount.getBalance();// 个人子账户当前余额
		double beforeBalance = account.getBalance();// 个人子账户当余额
		try {
			logger
					.info("++++++++dis:sub  sub credit befor:+++++beforeSubBalance:"
							+ beforeSubBalance
							+ "++++++trxAmount:"
							+ trxAmount
							+ "+++++++subAccountId:"
							+ subAccount.getId()
							+ "++++accountId:" + account.getId() + "++++");

			logger.info("++++dis:account credit befor:++++++beforeSubBalance:"
					+ beforeBalance + "+++++++trxAmount:" + trxAmount
					+ "+++++++++++subAccountId:" + subAccount.getId()
					+ "++++accountId:" + account.getId() + "++++");
			subAccount.credit(trxAmount);// 个人子账户入款
			account.credit(trxAmount);// 个人总账户入款

			double afterSubBalance = subAccount.getBalance();// 个人子账户入款之后的余额
			double afterBalance = account.getBalance();// 个人总账户入款之后的余额

			logger
					.info("+++++++++dis:sub  sub credit after:+++++afterSubBalance:"
							+ afterSubBalance
							+ "++++++subAccountId:"
							+ subAccount.getId()
							+ "++++accountId:"
							+ account.getId() + "+++++++++++++++");// 个人子账户入款之后的余额

			logger.info("++++++dis:account credit after:+++++++afterBalance:"
					+ afterBalance + "+++++++++++subAccountId:"
					+ subAccount.getId() + "++++accountId:" + account.getId()
					+ "++++++++");// / 个人总账户入款之后的余额

		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}
		subAccount.setUpdateDate(new Date());// 设置子账户更新时间

		subAccountDao.updateSubAccount(subAccount, subSuffix);// 更新子账户余额

		accountDao.updateActBalance(account.getId(), account.getBalance(),
				new Date(), account.getVersion());// 更新总账户余额

		// 更新总账户账户历史
		AccountHistory actHistory = new AccountHistory(account.getBalance(),
				account.getId(), actHistoryType, bizType, trxAmount, trxId,
				trxOrderId);
		actHistory.setDispaly(isDisplay);
		actHistory.setCreateDate(new Date());
		actHistory.setDescription(description);
		accountHistoryDao.addAccountHistory(actHistory);
	}

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
			VmAccountException {
		// 查询该交易下支付时帐务扩展记录
		// 将支付时帐务扩展记录以过期时间从晚到早排序
		// 递归入款
		List<VmTrxExtend> trxVmTrxtendList = new ArrayList<VmTrxExtend>();// 初始化交易关联List
		List<VmTrxExtend> refundVmTrxtendList = new ArrayList<VmTrxExtend>();// 初始化退款关联List

		List<VmTrxExtend> vmTrxExtendList = vmTrxExtendDao.findByTrxId(actId,
				trxOrderId);
		//int rudedVmtTrxEtdCount = 0;
		int trxVmTtdIndex = 0;
		int count = vmTrxExtendList.size();

		int initIndex = count - 1;

		for (int i = initIndex; i >= 0; i--) {// 逆序
			VmTrxExtend item = vmTrxExtendList.get(i);
			if (RelevanceType.SALES.equals(item.getRelevanceType())) {
				trxVmTrxtendList.add(item);
			} else if (RelevanceType.REFUND.equals(item.getRelevanceType())) {
				refundVmTrxtendList.add(item);
			}
		}

		// 递归从支付关联中的退款
		calcuSubActRefund(refundAmount, trxVmTrxtendList, trxVmTtdIndex,
				rudDetailId, description,refundVmTrxtendList);

	}

	/**
	 * 递归计算子账户退款
	 * 
	 * @throws StaleObjectStateException
	 * @throws VmAccountException
	 */
	public void calcuSubActRefund(double refundAmount,
			List<VmTrxExtend> trxVmTrxtendList, int index, Long bizId,
			String description,List<VmTrxExtend> refundVmTrxtendList) throws StaleObjectStateException,
			VmAccountException {
		VmTrxExtend trxVmTrxExtend = trxVmTrxtendList.get(index);
		Long vmAccountId = trxVmTrxExtend.getVmAccountId();
		Long actId = trxVmTrxExtend.getAccountId();
		Long subActId = trxVmTrxExtend.getSubAccountId();
		Long trxOrderId = trxVmTrxExtend.getTrxOrderId();
		double paymentAmount = trxVmTrxExtend.getPaymentAmount();
		Date loseDate = trxVmTrxExtend.getLoseDate();
		String trxRequestId = trxVmTrxExtend.getTrxRequestId();
		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(actId));
		SubAccount subAccount = subAccountDao.findById(subActId, subSuffix);
		double trxVmTtdAmount = trxVmTrxExtend.getAmount();//子账户支付的金额
		if(trxVmTtdAmount==0){
		    // 递归从支付关联中的退款
            calcuSubActRefund(refundAmount, trxVmTrxtendList, index + 1, bizId,
                    description,refundVmTrxtendList);
            return;
		}
		int refundVmTdIndex=0;
		
		if (refundVmTrxtendList!=null && refundVmTrxtendList.size()>0){
            refundVmTrxtendList = mergeList(refundVmTrxtendList);
            
            refundVmTdIndex = refundVmTrxtendList.indexOf(trxVmTrxExtend);//退款list中查找 
            if(refundVmTdIndex==-1){//未退款,走退款流程
                double gapAmount = Amount.sub(refundAmount, trxVmTtdAmount);
                if (gapAmount <= 0) {// 一次性退款完成
                    // 进行退款
                    creditByRefundSub(subAccount, vmAccountId, actId, trxOrderId,
                            bizId, paymentAmount, refundAmount, loseDate, trxRequestId,
                            description);
                    return;
                } else {
                    // 进行退款
                    creditByRefundSub(subAccount, vmAccountId, actId, trxOrderId,
                            bizId, paymentAmount, trxVmTtdAmount, loseDate,
                            trxRequestId, description);

                    // 继续退
                    calcuSubActRefund(gapAmount, trxVmTrxtendList, index + 1, bizId,
                            description,refundVmTrxtendList);
                }
            }else{//退过款
                VmTrxExtend refundedVmTrxExtend = refundVmTrxtendList.get(refundVmTdIndex);
                //还可以退到次子账户的金额
                logger.info("trxVmTrxExtend id:"+trxVmTrxExtend.getId()+"  ,trxVmTrxExtend.getAmount(): "+trxVmTrxExtend.getAmount()
                        +" ,refundedVmTrxExtend.getAmount(): "+refundedVmTrxExtend.getAmount());
                double lastGapRefundGapAmount = Amount.sub(trxVmTrxExtend.getAmount(), refundedVmTrxExtend.getAmount());
               
                if (lastGapRefundGapAmount == 0) {
                    
                } else if (lastGapRefundGapAmount > 0) {
                    //把可退的 退尽了再进入递归
                    if (Amount.sub(refundAmount, lastGapRefundGapAmount) <= 0) {
                        // 如果够退，直接返回
                        creditByRefundSub(subAccount, trxVmTrxExtend
                                .getVmAccountId(), actId, trxOrderId, bizId,
                                trxVmTrxExtend.getPaymentAmount(), refundAmount,
                                trxVmTrxExtend.getLoseDate(), trxVmTrxExtend
                                        .getTrxRequestId(), description);

                        return;
                    } else {
                        // 退款
                        creditByRefundSub(subAccount, trxVmTrxExtend
                                .getVmAccountId(), actId, trxOrderId, bizId,
                                trxVmTrxExtend.getPaymentAmount(),
                                lastGapRefundGapAmount, trxVmTrxExtend
                                        .getLoseDate(), trxVmTrxExtend
                                        .getTrxRequestId(), description);
                        // 重置需退款的金额。剩下进入递归退（子账户交易的金额已经全部退入到子账户，再退就需要向其他子账户退了）
                        refundAmount = Amount.sub(refundAmount,
                                lastGapRefundGapAmount);

                    }

                } else {

                    throw new VmAccountException(
                            BaseException.VM_TRXEXTEND_RUDEXTEND_ERROR); // 同一个SubId下支付关联的金额比其已退款的金额要小.退款交易关联异常（内部错误）

                }
                
                // 递归从支付关联中的退款
                calcuSubActRefund(refundAmount, trxVmTrxtendList, index + 1, bizId,
                        description,refundVmTrxtendList);
            }
            
		}else{
		  //未退款,走退款流程
            double gapAmount = Amount.sub(refundAmount, trxVmTtdAmount);
            if (gapAmount <= 0) {// 一次性退款完成
                // 进行退款
                creditByRefundSub(subAccount, vmAccountId, actId, trxOrderId,
                        bizId, paymentAmount, refundAmount, loseDate, trxRequestId,
                        description);
                return;
            } else {
                // 进行退款
                creditByRefundSub(subAccount, vmAccountId, actId, trxOrderId,
                        bizId, paymentAmount, trxVmTtdAmount, loseDate,
                        trxRequestId, description);

                // 继续退
                calcuSubActRefund(gapAmount, trxVmTrxtendList, index + 1, bizId,
                        description,refundVmTrxtendList);
            }
        
		}
       
	}

	/**
	 * 退款入款子方法
	 * 
	 * @param subAccount
	 * @param vmAccountId
	 * @param actId
	 * @param trxOrderId
	 * @param bizId
	 * @param refundAmount
	 * @param refundAmount2
	 * @param loseDate
	 * @param trxRequestId
	 * @param description
	 * @throws StaleObjectStateException
	 */
	public void creditByRefundSub(SubAccount subAccount, Long vmAccountId,
			Long actId, Long trxOrderId, Long bizId, double paymentAmount,
			double refundAmount, Date loseDate, String trxRequestId,
			String description) throws StaleObjectStateException {
		// 对子账户进行入款
		logger
				.info("++++++++++sub act Refund befor:credit befor:+++++subAccountBalance:"
						+ subAccount.getBalance()
						+ "+++++++++++refundAmount:"
						+ refundAmount
						+ "+++++++subAccountId"
						+ subAccount.getId()
						+ "++++vmAccountId:"
						+ vmAccountId
						+ "++++");
		subAccount.credit((refundAmount));
		subAccount.setUpdateDate(new Date()); // 设置更新时间
		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(actId));// 更新子账户余额
		Long subActId = subAccount.getId();
		subAccount.setLose(false);// 一旦发生退款，立即置为"未过期".能进行继续购买和第二次过期

		// 更新子账户余额额
		subAccountDao.updateSubAccount(subAccount, subSuffix);
		logger
				.info("++++++++++sub act Refund after： credit after:+++++subAccountBalance:"
						+ subAccount.getBalance()
						+ "+++++++++++refundAmount:"
						+ refundAmount
						+ "+++++++subAccountId"
						+ subAccount.getId()
						+ "++++vmAccountId:"
						+ vmAccountId
						+ "++++");
		// 增加扩展帐务支付关联记录
		VmTrxExtend vmTrxExtend = new VmTrxExtend(vmAccountId, actId, subActId,
				trxOrderId, bizId, 1L, paymentAmount, refundAmount, new Date(),
				loseDate, trxRequestId, RelevanceType.REFUND, description);
		vmTrxExtendDao.addVmTrxExtend(vmTrxExtend);

	}

	/**
	 * 支付成功子账户扣款
	 * 将按照子账户最先过期的商品进行扣款。
	 * 如果priorityVmActId（优先扣款虚拟账户）不为空，则优先先扣该数组
	 * @throws StaleObjectStateException
	 */
	@Override
	public NotChangeParam debitByPaySuc(Account account, double trxAmount, Long bizId,Long trxOrderId,
			String trxOrderReqId, Date trxDate,Long priorityVmActIds[],String description) 
			throws AccountException,StaleObjectStateException {
		Long actId = account.getId();
		double paymentAmount = trxAmount;
		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(actId));
		// 查询子账户列表
		List<SubAccount> subAccountList = subAccountDao.findByActId(actId,subSuffix);
		if (subAccountList == null || subAccountList.size()==0) {
			alertDebitVcAct(actId, trxAmount);// 内部报警
			return null;
		}
		
		int count = subAccountList.size();
		List<SubAccount> priorityVmList = new ArrayList<SubAccount>();
		//对priorityVmActId（优先扣款vmAccountId数组）进行放到list最前面
		if(null!=priorityVmActIds && priorityVmActIds.length>0){
			for(int i=priorityVmActIds.length-1;i>=0;i--){
				int j =subAccountList.size()-1;
				while(j>=0){
					SubAccount subAct = subAccountList.get(j);
					if(subAct.getVmAccountId().intValue() == priorityVmActIds[i].intValue()){
						subAccountList.remove(j);
						priorityVmList.add(subAct);
						break;
					}
					j--;
				}
			}
		}
		subAccountList.addAll(priorityVmList);
		
		int index = count-1;
		// 递归扣子账户款项
		// 更新个账户余额
		// 创建支付帐务扩展记录
		NotChangeParam notChangeParam = calcuSubBalance(trxAmount, paymentAmount, subAccountList, actId, index,bizId, trxOrderId,trxOrderReqId, description);
		return notChangeParam;
	}

	/**
	 * 支付成功时个人子账户递归扣款内部方法
	 * 
	 * @param trxAmount
	 *            虚拟币交易金额
	 * @param subAccountList
	 * @param actId
	 *            个人账户总账户ID
	 * @param index
	 * @param bizId
	 *            业务ID（paymentId）
	 * @param paymentAmount
	 *            本次交易使用虚拟币数量（vcPayment金额）
	 * @param txrOrderId
	 *            本次交易交易订单ID
	 * @param trxRequestId
	 *            本次交易交易订单请求号
	 * @param description
	 *            描述
	 * @throws StaleObjectStateException
	 */
	public NotChangeParam calcuSubBalance(double trxAmount, double payementAmount,
			List<SubAccount> subAccountList, Long actId, int index, Long bizId,
			Long trxOrderId, String trxRequestId, String description)
			throws StaleObjectStateException

	{
		SubAccount subAccount = subAccountList.get(index);
		double subActBalance = subAccount.getBalance();
		Long subActId = subAccount.getId();// 子账户ID
		Long vmAccountId = subAccount.getVmAccountId();
		Date loseDate = subAccount.getLoseDate();// 过期时间
		// 计算相邻子账户应扣金额（按过期时间从早到晚排序）
		double gapAmount = Amount.sub(trxAmount, subActBalance);

		logger.info("++++++++calcu:sub  sub credit befor:+++++subActBalance:"
				+ subActBalance + "++++++trxAmount:" + trxAmount
				+ "+++++++subActId" + subActId + "++++vmAccountId:"
				+ vmAccountId + "++++");
		// 增加扩展帐务支付关联记录
		VmTrxExtend vmTrxExtend = null;
		if (gapAmount > 0) {
			vmTrxExtend = new VmTrxExtend(vmAccountId, actId, subActId,
					trxOrderId, bizId, 1L, payementAmount, subActBalance,
					new Date(), loseDate, trxRequestId, RelevanceType.SALES,
					description);
			subAccount.debit(subActBalance);
		} else {

			vmTrxExtend = new VmTrxExtend(vmAccountId, actId, subActId,
					trxOrderId, bizId, 1L, payementAmount, trxAmount,
					new Date(), loseDate, trxRequestId, RelevanceType.SALES,
					description);
			subAccount.debit(trxAmount);
		}

		// 对子账户进行扣款

		subAccount.setUpdateDate(new Date()); // 设置更新时间
		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(actId));// 更新子账户余额

		logger
				.info("++++++++calcu:end  end  end befor:+++++subAccount.getBalance():"
						+ subAccount.getBalance()
						+ "++++++trxAmount:"
						+ trxAmount
						+ "+++++++subActId"
						+ subActId
						+ "++++vmAccountId:" + vmAccountId + "++++");
		// 更新子账户余额额
		subAccountDao.updateSubAccount(subAccount, subSuffix);
		vmTrxExtendDao.addVmTrxExtend(vmTrxExtend);
		if (gapAmount > 0) {
			trxAmount = Amount.sub(trxAmount, subActBalance);
			NotChangeParam  notChangeParam = calcuSubBalance(trxAmount, payementAmount, subAccountList, actId,
					index - 1, bizId, trxOrderId, trxRequestId, description);
			return notChangeParam;
		}else{
		
			NotChangeParam notChangeParam=new NotChangeParam(trxAmount,subAccount.getId(),subAccount.getAccountId(),subAccount.getVmAccountId(),trxOrderId);
			return notChangeParam;
		}
		
	}

	/**
	 * 虚拟款项取消扣款
	 */
	@Override
	public void debitByCancel(SubAccount subAccount, Account account,
			double trxAmount, ActHistoryType actHistoryType, Long trxId,
			Long trxOrderId, Date trxDate, String description,
			boolean isDisplay, String bizType,boolean isLose) throws AccountException,
			StaleObjectStateException {

		String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(account
				.getId()));// 子账户所在表后缀
		double beforeSubBalance = subAccount.getBalance();// 个人子账户当前余额
		double beforeBalance = account.getBalance();// 个人子账户当余额
		try {
			logger.info("+++++cancel:sub  sub debit befor:+++beforeSubBalance:"
					+ beforeSubBalance + "++++++trxAmount:" + trxAmount
					+ "+++++++subAccountId:" + subAccount.getId()
					+ "++++accountId:" + account.getId() + "++++");

			logger.info("++++cancel:account debit befor:+++beforeSubBalance:"
					+ beforeBalance + "+++++++trxAmount:" + trxAmount
					+ "+++++++subAccountId:" + subAccount.getId()
					+ "++++accountId:" + account.getId() + "++");
			subAccount.debit(trxAmount);// 个人子账户扣款
			if(!AccountStatus.ACTIVE.equals(account.getAccountStatus())){
				account.setAccountStatus(AccountStatus.ACTIVE);
			}
			account.debit(trxAmount);// 个人总账户扣款

			double afterSubBalance = subAccount.getBalance();// 个人子账户扣款之后的余额
			double afterBalance = account.getBalance();// 个人总账户扣款之后的余额

			logger.info("++++++cancel:sub debit after:+++++afterSubBalance:"
					+ afterSubBalance + "++++++subAccountId:"
					+ subAccount.getId() + "++++accountId:" + account.getId()
					+ "+++++++++++++++");// 个人子账户扣款之后的余额

			logger.info("+++cancel:account debit after:++++afterBalance:"
					+ afterBalance + "+++++++subAccountId:"
					+ subAccount.getId() + "++++accountId:" + account.getId()
					+ "++++++++");// / 个人总账户扣款之后的余额

		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}
		subAccount.setUpdateDate(new Date());// 设置子账户更新时间
		subAccount.setLose(isLose);// 状态更改为已过期

		subAccountDao.updateSubAccount(subAccount, subSuffix);// 更新子账户余额

		accountDao.updateActBalance(account.getId(), account.getBalance(),
				new Date(), account.getVersion());// 更新总账户余额

		// 更新总账户账户历史
		AccountHistory actHistory = new AccountHistory(account.getBalance(),
				account.getId(), actHistoryType, bizType, trxAmount, trxId,
				trxOrderId);
		actHistory.setDispaly(isDisplay);
		actHistory.setCreateDate(new Date());
		actHistory.setDescription(description);
		accountHistoryDao.addAccountHistory(actHistory);

	}

	@Override
	public SubAccount findByIdAndActId(Long id, Long actId) {

		Long subSuffix = StringUtils.getDeliveryIdBase(actId);// 取模确定子账户在哪一张表
		SubAccount subAccount = subAccountDao.findById(id, String
				.valueOf(subSuffix));
		return subAccount;
	}

	/**
	 * 根据总账户号和虚拟款项ID获取子账户
	 * 
	 * @param actId
	 * @param vmId
	 * @return
	 */
	public SubAccount findByActIdAndVmId(Long actId, Long vmId) {

		Long subSuffix = StringUtils.getDeliveryIdBase(actId);// 取模确定子账户在哪一张表
		SubAccount subAccount = subAccountDao.findByActIdAndVmId(actId, vmId,
				String.valueOf(subSuffix));
		return subAccount;

	}

	/**
	 * 根据过期时间获取子账户
	 * 
	 * 
	 * @return
	 */
	@Override
	public List<SubAccount> findByLose() {

		List<SubAccount> subAccount = subAccountDao.findByLose(new Date());
		return subAccount;
	}

	/**
	 * 过期自动取消
	 * 
	 * @throws AccountException
	 */
	@Override
	public void cancelLose(SubAccount subAccount)
			throws StaleObjectStateException, AccountException {
		boolean isDisplayAactHis = true;
		Long subSuffix = 0L;
		double subBalance = subAccount.getBalance();
		if (subAccount != null) {
			subSuffix = StringUtils
					.getDeliveryIdBase(subAccount.getAccountId());
		}
		logger.info("++++++++++++++++++start cancel++++++++beiker_sub_Account_"
				+ subSuffix + "++++++++++subAccountId" + subAccount.getId()
				+ "++++++++++++++++++++++++++++");

		VmCancelRecord vmCancelRecord = new VmCancelRecord();
		vmCancelRecord.setAccountId(subAccount.getAccountId());
		vmCancelRecord.setAmount(subBalance);
		vmCancelRecord.setCreateDate(new Date());
		vmCancelRecord.setOperatorId(0L);
		vmCancelRecord.setSubAccountId(subAccount.getId());
		vmCancelRecord.setUpdateDate(new Date());
		vmCancelRecord.setVmAccountId(subAccount.getVmAccountId());
		vmCancelRecord.setCancelType(CancelType.CANCEL);
		Account account = accountDao.findById(subAccount.getAccountId());
		Long vmCancelRecordId = vmCancelRecordDao
				.addVmCancelRecord(vmCancelRecord);

		if (subBalance == 0) {
			isDisplayAactHis = false;
		}
		// 虚拟款项个人总账户过期取消扣款
		debitByCancel(subAccount, account, subAccount.getBalance(),
				ActHistoryType.CANCEL, vmCancelRecordId, 0L, new Date(), "",
				isDisplayAactHis, "vm cancel",true);

		logger.info("++++++++++++++++++cancel++++++++vmCancelRecordId="
				+ vmCancelRecordId + "++++++++++subAccountId"
				+ subAccount.getId() + "++++++++++++++++++++++++++++");
	}

	/**
	 * List 部分属性相同的元素合并 .
	 * 
	 * @param list
	 */
	public List<VmTrxExtend> mergeList(List<VmTrxExtend> list) {

		LinkedHashMap<Long, VmTrxExtend> map = new LinkedHashMap<Long, VmTrxExtend>();
		for (VmTrxExtend item : list) {
			Long subActId = item.getSubAccountId();// 个人子账户ID
			if (map.containsKey(subActId)) {
				item.setAmount(Amount.add(map.get(subActId).getAmount(), item
						.getAmount()));
			}
			map.put(item.getSubAccountId(), item);
			// VmTrxExtendList.add(item);

		}
		list.clear();
		list.addAll(map.values());

		return list;
	}

	// 扣款异常报警
	public void alertDebitVcAct(Long actId, double trxAmount) {

		if (trxAmount > 0) {// 报警
			// 发送内部报警邮件
			String alertEmailParams[] = { String.valueOf(actId),
					String.valueOf(trxAmount) };
			if (alertVcActDebitEmail != null
					&& alertVcActDebitEmail.length() > 0) {
				String[] alertVcActDebitEmailAry = alertVcActDebitEmail
						.split(",");
				int alertEmailCount = alertVcActDebitEmailAry.length;

				try {
					for (int i = 0; i < alertEmailCount; i++) {
						emailService.send(null, null, null, null, null, null,
								new String[] { alertVcActDebitEmailAry[i] },
								null, null, new Date(), alertEmailParams,
								DEBIT_VCACT_EMAIL_ALERT_TEMP);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return;

	}
	
	public List<SubAccount> findSubAccountList(Long actId,String subSuffix){
		// 查询子账户列表
		List<SubAccount> subAccountList = subAccountDao.findByActId(actId,subSuffix);
		return subAccountList;
	}
	
	/**
     * 退款优惠券下发入款
     * 
     * @throws StaleObjectStateException
     */
	@Override
    public void creditByRefundCoupon(SubAccount subAccount, Account account,
            double trxAmount, ActHistoryType actHistoryType, Long trxId,
            Long trxOrderId, Date trxDate, String description,
            boolean isDisplay, String bizType) throws AccountException,
            StaleObjectStateException {
        String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(account
                .getId()));// 子账户所在表后缀
        double beforeSubBalance = subAccount.getBalance();// 个人子账户当前余额
        double beforeBalance = account.getBalance();// 个人子账户当余额
        try {
            logger.info("++++++++RefundCoupon:sub  sub credit befor:+++++beforeSubBalance:"
                            + beforeSubBalance
                            + "++++++trxAmount:"
                            + trxAmount
                            + "+++++++subAccountId:"
                            + subAccount.getId()
                            + "++++accountId:" + account.getId() + "++++");
            logger.info("++++RefundCoupon:account credit befor:++++++beforeSubBalance:"
                    + beforeBalance + "+++++++trxAmount:" + trxAmount
                    + "+++++++++++subAccountId:" + subAccount.getId()
                    + "++++accountId:" + account.getId() + "++++");
            subAccount.credit(trxAmount);// 个人子账户入款
            account.credit(trxAmount);// 个人总账户入款
            double afterSubBalance = subAccount.getBalance();// 个人子账户入款之后的余额
            double afterBalance = account.getBalance();// 个人总账户入款之后的余额

            logger.info("+++++++++RefundCoupon:sub  sub credit after:+++++afterSubBalance:"
                            + afterSubBalance
                            + "++++++subAccountId:"
                            + subAccount.getId()
                            + "++++accountId:"
                            + account.getId() + "+++++++++++++++");// 个人子账户入款之后的余额
            logger.info("++++++RefundCoupon:account credit after:+++++++afterBalance:"
                    + afterBalance + "+++++++++++subAccountId:"
                    + subAccount.getId() + "++++accountId:" + account.getId()
                    + "++++++++");// / 个人总账户入款之后的余额
        } catch (IllegalArgumentException e) {
            throw new AccountException(e.getMessage());
        }
        subAccount.setUpdateDate(new Date());// 设置子账户更新时间
        subAccountDao.updateSubAccount(subAccount, subSuffix);// 更新子账户余额
        accountDao.updateActBalance(account.getId(), account.getBalance(),
                new Date(), account.getVersion());// 更新总账户余额
        // 更新总账户账户历史
        AccountHistory actHistory = new AccountHistory(account.getBalance(),
                account.getId(), actHistoryType, bizType, trxAmount, trxId,
                trxOrderId);
        actHistory.setDispaly(isDisplay);
        actHistory.setCreateDate(new Date());
        actHistory.setDescription(description);
        accountHistoryDao.addAccountHistory(actHistory);
    }
	/**
	 * 优惠券不退款，金额扣除
	 */
    @Override
    public void debitByCancelCouponRefund(SubAccount subAccount, Account account,
            double trxAmount, ActHistoryType actHistoryType, Long trxId, Long trxOrderId,
            Date trxDate, String description, boolean isDisplay, String bizType,
            boolean isLost) throws AccountException, StaleObjectStateException {

        String subSuffix = String.valueOf(StringUtils.getDeliveryIdBase(account
                .getId()));// 子账户所在表后缀
        double beforeSubBalance = subAccount.getBalance();// 个人子账户当前余额
        double beforeBalance = account.getBalance();// 个人子账户当余额
        try {
            logger.info("+++++coupon refund cancel:sub  sub debit befor:+++beforeSubBalance:"
                    + beforeSubBalance + "++++++trxAmount:" + trxAmount
                    + "+++++++subAccountId:" + subAccount.getId()
                    + "++++accountId:" + account.getId() + "++++");

            logger.info("++++coupon refund cancel:account debit befor:+++beforeSubBalance:"
                    + beforeBalance + "+++++++trxAmount:" + trxAmount
                    + "+++++++subAccountId:" + subAccount.getId()
                    + "++++accountId:" + account.getId() + "++");
            subAccount.debit(trxAmount);// 个人子账户扣款
            if(!AccountStatus.ACTIVE.equals(account.getAccountStatus())){
                account.setAccountStatus(AccountStatus.ACTIVE);
            }
            account.debit(trxAmount);// 个人总账户扣款

            double afterSubBalance = subAccount.getBalance();// 个人子账户扣款之后的余额
            double afterBalance = account.getBalance();// 个人总账户扣款之后的余额

            logger.info("++++++coupon refund cancel:sub debit after:+++++afterSubBalance:"
                    + afterSubBalance + "++++++subAccountId:"
                    + subAccount.getId() + "++++accountId:" + account.getId()
                    + "+++++++++++++++");// 个人子账户扣款之后的余额

            logger.info("+++coupon refund cancel:account debit after:++++afterBalance:"
                    + afterBalance + "+++++++subAccountId:"
                    + subAccount.getId() + "++++accountId:" + account.getId()
                    + "++++++++");// / 个人总账户扣款之后的余额

        } catch (IllegalArgumentException e) {
            throw new AccountException(e.getMessage());
        }
        subAccount.setUpdateDate(new Date());// 设置子账户更新时间
        subAccount.setLose(isLost);// 状态更改为已过期

        subAccountDao.updateSubAccount(subAccount, subSuffix);// 更新子账户余额

        accountDao.updateActBalance(account.getId(), account.getBalance(),
                new Date(), account.getVersion());// 更新总账户余额

        // 更新总账户账户历史
        AccountHistory actHistory = new AccountHistory(account.getBalance(),
                account.getId(), actHistoryType, bizType, trxAmount, trxId,
                trxOrderId);
        actHistory.setDispaly(isDisplay);
        actHistory.setCreateDate(new Date());
        actHistory.setDescription(description);
        accountHistoryDao.addAccountHistory(actHistory);

    
    }
    
}
