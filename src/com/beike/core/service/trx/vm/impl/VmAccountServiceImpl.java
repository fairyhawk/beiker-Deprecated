package com.beike.core.service.trx.vm.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmAccount;
import com.beike.common.entity.vm.VmAccountHistory;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.vm.VmAccountType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.VmAccountException;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.vm.VmAccountDao;
import com.beike.dao.vm.VmAccountHistoryDao;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>
 * Title:虚拟款项账户创建、追加、下发VmAccountServiceImpl
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
@Service("vmAccountService")
public class VmAccountServiceImpl implements VmAccountService {

	@Autowired
	private VmAccountDao vmAccountDao;

	@Autowired
	private VmAccountHistoryDao vmAccountHistoryDao;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private SubAccountService subAccountService;
	
	private static MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	private static Log logger = LogFactory.getLog(VmAccountServiceImpl.class);

	@Override
	public void pursueVmAccount(VmAccountParamInfo vmAccountParamInfo)
			throws AccountException, NumberFormatException, VmAccountException,
			StaleObjectStateException {

		Long vmAccountId = Long.parseLong(vmAccountParamInfo.getVmAccountId()); // 虚拟账户ID
		double amount = Double.parseDouble(vmAccountParamInfo.getAmount());
		String operatorId = vmAccountParamInfo.getOperatorId();
		VmAccount vmAccount = vmAccountDao.findById(vmAccountId);
		if (vmAccount == null) {

			throw new AccountException(BaseException.ACCOUNT_NOT_FOUND);
		}
		if (DateUtils.loseDate(vmAccount.getLoseDate())) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_DATE);// 过期时间小于当前时间
		}
		// 入款
		credit(vmAccount, amount, "", VmAccountType.PURSUE, Long
				.parseLong(operatorId));

	}

	@Override
	public Long createVmAccount(VmAccountParamInfo vmAccountParamInfo)
			throws AccountException, VmAccountException, NumberFormatException,
			StaleObjectStateException, TrxOrderException {

		double balance = Double.parseDouble(vmAccountParamInfo.getBalance());// 余额
		String vmAccountSortId = vmAccountParamInfo.getVmAccountSortId();// 类别ID
		boolean isFund = StringUtils.transBoolean(vmAccountParamInfo
				.getIsFund());// 是否有金
		Date loseDate = DateUtils.toDate(vmAccountParamInfo.getLoseDate(),
				"yyyy-MM-dd hh:mm:ss");// 过期时间
		String costBear = vmAccountParamInfo.getCostBear();// 成本承担方
		String proposer = vmAccountParamInfo.getProposer();// 申请人
		String description = vmAccountParamInfo.getDescription();// 描述
		String operatorId = vmAccountParamInfo.getOperatorId();// 操作人ID
		int isNotChange = vmAccountParamInfo.getIsNotChange();
		String notChangeRule = vmAccountParamInfo.getNotChangeRule();
		String isRefund = vmAccountParamInfo.getIsRefund();// 是否退款
		if (DateUtils.loseDate(loseDate)) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_DATE);// 过期时间小于当前时间
		}
		VmAccount vmAccount = new VmAccount(0, new Date(), Long
				.parseLong(vmAccountSortId), isFund, loseDate);

		vmAccount.setCostBear(costBear);
		vmAccount.setProposer(proposer);
		vmAccount.setDescription(description);
		vmAccount.setTotalBalance(0);// 总余额初试化
		vmAccount.setIsNotChange(isNotChange);
		vmAccount.setNotChangeRule(notChangeRule);
		vmAccount.setIsRefund(Integer.valueOf(isRefund));
		// 创建账户
		Long vmAccountId = vmAccountDao.addVmAccount(vmAccount);
		// 入款
		VmAccount findvmAccount = vmAccountDao.findById(vmAccountId);
		credit(findvmAccount, balance, "", VmAccountType.CREATE, Long
				.parseLong(operatorId));
		return vmAccountId;

	}

	@Override
	public int dispatchVmForCard(VmAccountParamInfo vmAccountParamInfo) {
		int result = 0; // 默认为返现下发失败;
		try {
			dispatchVm(vmAccountParamInfo);
			result = 1;
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}
		return result;

	}

	@Override
	public int dispatchVmForVou(VmAccountParamInfo vmAccountParamInfo) {
		int result = 0; // 默认为返现下发失败;
		try {
			dispatchVm(vmAccountParamInfo);
			result = 1;
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}
		return result;

	}
	

	@Override
	public Long dispatchVm(VmAccountParamInfo vmAccountParamInfo)
			throws VmAccountException, NumberFormatException, AccountException,
			StaleObjectStateException {

		Account account = null;
		Long vmAccountId = Long.parseLong(vmAccountParamInfo.getVmAccountId());// 虚拟款项ID
		Long userId = Long.parseLong(vmAccountParamInfo.getUserId());// 用户Id
		String requestId = vmAccountParamInfo.getRequestId();// 下发请求号

		double amount = Double.parseDouble(vmAccountParamInfo.getAmount());// 下发金额

		String operatorId = vmAccountParamInfo.getOperatorId();// 操作人Id
		ActHistoryType actHistoryType = vmAccountParamInfo.getActHistoryType();// 下发类型
		VmAccount vmAccount = vmAccountDao.findById(vmAccountId);

		if (vmAccount == null) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_NOT_FOUND);// 虚拟款项账户未发现
		}
		if (DateUtils.loseDate(vmAccount.getLoseDate())) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_DATE);// 过期时间小于当前时间
		}

		if (requestId == null || requestId.length() == 0) {
			throw new VmAccountException(
					BaseException.VM_DIS_REQUESTID_NOT_NULL);
		}

		Long vmActHisCount = vmAccountHistoryDao.findByTypeAndReqId(EnumUtil
				.transEnumToString(VmAccountType.DISPATCH), requestId); // 下发请求号不能为空

		if (vmActHisCount > 0) {
			// throw new
			// VmAccountException(BaseException.VM_DIS_REQUESTID_DUCPLIT);//
			// 下发请求号不能重复
			return 0L;
		}

		// 查询用户总账户和创建子账户

		Long userCount = trxSoaService.findUserById(userId);
		if (userCount.longValue() == 0) {
			throw new VmAccountException(BaseException.USER_NOT_FOUND);// 用户账户不存在
		}

		if (userId != null && !"".equals(userId)) {
			account = accountService.findByUserIdAndType(Long.valueOf(userId),
					AccountType.VC);
		}

		Long accountId = account.getId();// 个人总账户ID
		Date loseDate = vmAccount.getLoseDate();// 过期时间

		SubAccount subAccount = subAccountService.create(userId, accountId,
				vmAccountId, loseDate);// 创建子账户
		String description = "";
		if (actHistoryType.equals(ActHistoryType.VMDIS) && BizType.CARDLOAD.equals(vmAccountParamInfo.getBizType())) {
			description = "充值时间:" + DateUtils.dateToStr(new Date());
		}else if((actHistoryType.equals(ActHistoryType.VMDIS) && BizType.COUPON.equals(vmAccountParamInfo.getBizType()))|| actHistoryType.equals(ActHistoryType.RABATE)){
				description = vmAccountParamInfo.getDescription();
		}else {
			description = vmAccount.getDescription();// 虚拟款项描述
		}
		// 虚拟账户扣款
		Long vmActHisId = debit(vmAccount, amount, requestId,
				VmAccountType.DISPATCH, accountId, subAccount.getId(), Long
						.parseLong(operatorId));// 返回虚拟款项帐务历史ID
		// 个人总账户和个人子账户入款
		logger.info("+++++++++++vmActHisId" + vmActHisId
				+ "++++creditByDis+++++++++++");
		String bizType = "vmAct dis";
		
		//优惠券和千品卡充值
		if (vmAccountParamInfo.getBizType() != null
				&& (BizType.CARDLOAD.equals(vmAccountParamInfo.getBizType()) || BizType.COUPON.equals(vmAccountParamInfo.getBizType()))) {
			bizType = vmAccountParamInfo.getBizType().name();
		}

		subAccountService.creditByDis(subAccount, account, amount,
				actHistoryType, vmActHisId, 0L, new Date(), description, true,
				bizType);
		
		return vmActHisId;

	}
	
	
	/**
	 * 虚拟账号同步扣款操作
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
	public boolean dispatchVmForSync(VmAccountParamInfo vmAccountParamInfo) throws VmAccountException, NumberFormatException, AccountException,StaleObjectStateException {
		Account account = null;
		Long vmAccountId = Long.parseLong(vmAccountParamInfo.getVmAccountId());// 虚拟款项ID
		Long userId = Long.parseLong(vmAccountParamInfo.getUserId());// 用户Id
		String requestId = vmAccountParamInfo.getRequestId();// 下发请求号

		double amount = Double.parseDouble(vmAccountParamInfo.getAmount());// 下发金额

		String operatorId = vmAccountParamInfo.getOperatorId();// 操作人Id
		ActHistoryType actHistoryType = vmAccountParamInfo.getActHistoryType();// 下发类型
		VmAccount vmAccount = vmAccountDao.findById(vmAccountId);

		if (vmAccount == null) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_NOT_FOUND);// 虚拟款项账户未发现
		}
		
		if (DateUtils.loseDate(vmAccount.getLoseDate())) {
			throw new VmAccountException(BaseException.VM_ACCOUNT_DATE);// 过期时间小于当前时间
		}

		if (requestId == null || requestId.length() == 0) {
			throw new VmAccountException(BaseException.VM_DIS_REQUESTID_NOT_NULL);
		}

		Long vmActHisCount = vmAccountHistoryDao.findByTypeAndReqId(EnumUtil.transEnumToString(VmAccountType.DISPATCH), requestId); // 下发请求号不能为空

		if (vmActHisCount > 0) {
			throw new VmAccountException(BaseException.VM_DIS_REQUESTID_DUCPLIT);//
		}

		// 查询用户总账户和创建子账户

		Long userCount = trxSoaService.findUserById(userId);
		if (userCount.longValue() == 0) {
			throw new VmAccountException(BaseException.USER_NOT_FOUND);// 用户账户不存在
		}

		if (userId != null && !"".equals(userId)) {
			account = accountService.findByUserIdAndType(Long.valueOf(userId),AccountType.VC);
		}

		Long accountId = account.getId();// 个人总账户ID
		Date loseDate = vmAccount.getLoseDate();// 过期时间

		SubAccount subAccount = subAccountService.create(userId, accountId,vmAccountId, loseDate);// 创建子账户
		String description = "";
		if (actHistoryType.equals(ActHistoryType.VMDIS) && BizType.CARDLOAD.equals(vmAccountParamInfo.getBizType())) {
			description = "充值时间:" + DateUtils.dateToStr(new Date());
		}else if((actHistoryType.equals(ActHistoryType.VMDIS) && BizType.COUPON.equals(vmAccountParamInfo.getBizType()))|| actHistoryType.equals(ActHistoryType.RABATE)){
				description = vmAccountParamInfo.getDescription();
		}else {
			description = vmAccount.getDescription();// 虚拟款项描述
		}
		boolean debitSuccess = true;
		
		/*
		 *  虚拟账户扣款
		 *  如果发送乐观锁异常，则忽略（不对虚拟账户扣款，该由定时来批量进行扣款操作）
		 */
		double curBalance = vmAccount.getBalance();
		if (curBalance - amount < 0)
			throw new AccountException(BaseException.ACCOUNT_NOT_ENOUGH);
		try {
			logger.info("vm act debit befor:======vmAccountId:" + vmAccount.getId() + "========balance:" + curBalance + "=====trxAmount:" + amount + "============");
			vmAccount.debit(amount);
		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}
		try{
			vmAccountDao.updateVmAccount(vmAccount);
		}catch (StaleObjectStateException e) {
			//catch乐观锁异常，则需要一部操作
			logger.info("+++++++++++++vm debit StaleObjectStateException++++vmAccountId="+vmAccount.getId()+"+++amount="+amount);
			debitSuccess = false;
		}
		VmAccountHistory vmAccountHistory = new VmAccountHistory(vmAccount .getId(), vmAccount.getBalance(),amount, new Date(), requestId, VmAccountType.DISPATCH);
		vmAccountHistory.setOperatorId(Long.parseLong(operatorId));
		vmAccountHistory.setSubAccountId(subAccount.getId());
		vmAccountHistory.setAccountId(accountId);
		Long vmActHisId = vmAccountHistoryDao.addVmAccountHistory(vmAccountHistory);
		
		logger.info("+++++++++++vmActHisId" + vmActHisId + "++++creditByDis+++++++++++");
		//扣款完成
		
		// 个人总账户和个人子账户入款
		String bizType = "vmAct dis";
		
		//优惠券和千品卡充值
		if (vmAccountParamInfo.getBizType() != null && (BizType.CARDLOAD.equals(vmAccountParamInfo.getBizType()) || BizType.COUPON.equals(vmAccountParamInfo.getBizType()))) {
			bizType = vmAccountParamInfo.getBizType().name();
		}

		subAccountService.creditByDis(subAccount, account, amount,actHistoryType, vmActHisId, 0L, new Date(), description, true,bizType);
		return debitSuccess;
	}
	
	
	// 扣款
	public Long debit(VmAccount vmAccount, double amount, String requestId,VmAccountType vmAccountType, 
			Long accountId, Long subAccountId,Long operatorId) throws AccountException, StaleObjectStateException {
		double curBalance = vmAccount.getBalance();
		if (curBalance - amount < 0)

			throw new AccountException(BaseException.ACCOUNT_NOT_ENOUGH);
		try {
			logger.info("vm   act debit befor:======vmAccountId:"
					+ vmAccount.getId() + "========balance:" + curBalance
					+ "=====trxAmount:" + amount + "============");
			vmAccount.debit(amount);
		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}

		vmAccountDao.updateVmAccount(vmAccount);

		VmAccountHistory vmAccountHistory = new VmAccountHistory(vmAccount
				.getId(), vmAccount.getBalance(), amount, new Date(),
				requestId, vmAccountType);
		vmAccountHistory.setOperatorId(operatorId);
		vmAccountHistory.setSubAccountId(subAccountId);
		vmAccountHistory.setAccountId(accountId);
		return vmAccountHistoryDao.addVmAccountHistory(vmAccountHistory);

	}

	// 入款
	public void credit(VmAccount vmAccount, double amount, String requestId,
			VmAccountType vmAccountType, Long operatorId)
			throws AccountException, VmAccountException,
			StaleObjectStateException {
		double curBalance = vmAccount.getBalance();
		try {
			logger.info("vm act credit befor:======balance:" + curBalance
					+ "=====trxAmount:" + amount + "============");
			vmAccount.credit(amount);
			vmAccount.creditTotal(amount);
		} catch (IllegalArgumentException e) {
			logger.error("++++" + e.getMessage() + "+++++++++");
			throw new VmAccountException(e.getMessage());
		}
		vmAccountDao.updateVmAccount(vmAccount);

		VmAccountHistory vmAccountHistory = new VmAccountHistory(vmAccount
				.getId(), vmAccount.getBalance(), amount, new Date(),
				requestId, vmAccountType);

		vmAccountHistory.setOperatorId(operatorId);
		vmAccountHistoryDao.addVmAccountHistory(vmAccountHistory);
	}

	@Override
	public VmAccount findById(Long id) {
		return vmAccountDao.findById(id);
	}
	
	@Override
	public Map<Long, String> findVmAccount(Long id) {

		Map<Long, String> vmAccountMap = new HashMap<Long, String>();

		StringBuilder vmAccountIdKeySb = new StringBuilder();
		vmAccountIdKeySb.append(TrxConstant.VM_ACCOUNT_ID_KEY);
		vmAccountIdKeySb.append(id.toString());
		String vmAccount = (String) memCacheService.get(vmAccountIdKeySb.toString());
		// 如果缓存里没有，从库里取，然后再放一次
		if (vmAccount == null || vmAccount.length() == 0) {
			Map<String, Object> userMap = vmAccountDao.findVmAccountById(id);
			if(userMap!=null&&!userMap.isEmpty()){
				vmAccount = userMap.get("is_not_change").toString()+"|"+userMap.get("not_change_rule").toString()+"|"+userMap.get("is_refund").toString();
			}
			memCacheService.set(vmAccountIdKeySb.toString(), vmAccount,TrxConstant.VM_ACCOUNT_TIMEOUT);

		}
		vmAccountMap.put(id, vmAccount);
		return vmAccountMap;

	}

	/**
	 * 异步扣款
	 * @param vmAccountId
	 * @param amount
	 */
	public void debitForAsyn(VmAccount vmAccount,double amount,VmAccountType vmAccountType,String requestId)  throws AccountException,StaleObjectStateException{
		double curBalance = vmAccount.getBalance();
		if (curBalance - amount < 0)
			throw new AccountException(BaseException.ACCOUNT_NOT_ENOUGH);
		try {
			logger.info("vm act asyc debit befor:======vmAccountId:" + vmAccount.getId()  + "========balance:" + curBalance + "=====trxAmount:" + amount + "============");
			vmAccount.debit(amount);
		} catch (IllegalArgumentException e) {
			throw new AccountException(e.getMessage());
		}
		//vm账户扣款
		vmAccountDao.updateVmAccount(vmAccount);
		//更新vm账户历史记录表 余额
		VmAccountHistory vmActHis = vmAccountHistoryDao.findVmActHisByTypeAndReqId(vmAccountType.name(), requestId);
		if(null == vmActHis){
			throw new AccountException(BaseException.REBRECORD_EXTERNALID_DUCPLIT);		//记录号不能重复或不存在
		}
		vmActHis.setBalance(vmAccount.getBalance());
		vmAccountHistoryDao.updateVmAccountHistory(vmActHis);
	}
}
