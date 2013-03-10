/*package com.beike.core.service.trx.asyn.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmAccountHistory;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.core.service.trx.asyn.AccountAsynThreadService;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.dao.vm.VmAccountHistoryDao;

*//**
 * @Title: AccountAsynThreadServiceImpl.java
 * @Package com.beike.core.service.trx.asyn.impl.AccountAsynThreadServiceImpl
 * @Description: 虚拟款项下发异步入账
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 3:25:41 PM
 * @version V1.0
 *//*
@Service("accountAsynThreadService")
public class AccountAsynThreadServiceImpl implements AccountAsynThreadService {

	private final Long actId;// 个人总账户ID
	private final Long subAccountId;// 个人子总账户ID
	private final double trxAmount;
	private final ActHistoryType actHistoryType;
	private final Long trxId;
	private final Long trxOrderId;
	private final String description;
	private final boolean isDisplay;
	private final String bizType;
	private final Long vmActHisId; // 虚拟款项帐务历史ID

	private final Log logger = LogFactory
			.getLog(AccountAsynThreadServiceImpl.class);
	@Autowired
	private SubAccountService subAccountService;

	@Autowired
	private VmAccountHistoryDao vmAccountHistoryDao;
	
	public AccountAsynThreadServiceImpl(Map<String, Object> map) {

		this.actId = (Long) map.get("actId");
		this.subAccountId = (Long) map.get("subAccountId");
		this.vmActHisId = (Long) map.get("vmActHisId");
		this.trxAmount = (Double) map.get("amount");
		this.actHistoryType = (ActHistoryType) map.get("actHistoryType");
		this.trxId = (Long) map.get("trxId");
		this.trxOrderId = (Long) map.get("trxOrderId");
		this.description = String.valueOf(map.get("description"));
		this.isDisplay = Boolean.valueOf(map.get("isDisplay").toString());
		this.bizType = String.valueOf(map.get("bizType"));

	}

	@Override
	public void asynIntoAct(Map<String, Object> map) throws Exception {

		// ExecutorService executorService = Executors.newCachedThreadPool();
		ExecutorService executorService = Executors.newFixedThreadPool(1);// 线程池每次都固定放一个线程

		// 使用ExecutorService执行Callable类型的任务
		Future<String> future = null;
		try {
			future = executorService.submit(new AccountAsynThreadServiceImpl(
					map));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("+++++++++++++" + e + "++++++");

		} finally {
			// 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
			executorService.shutdown();
		}

		logger
				.info("++asyn  credit  future+++++Thread.currentThread().getName():"
						+ Thread.currentThread().getName()
						+ "+++actId:"
						+ actId
						+ "+++subAccountId:"
						+ subAccountId
						+ "+++++vmActHisId:" + vmActHisId + "+++++");
		if (future.isDone()) {
			// 更新入账标志位
			VmAccountHistory vmAccountHistory = vmAccountHistoryDao
					.findById(vmActHisId);
			vmAccountHistory.setIsCreditAct(1L); // 置为已入账
			vmAccountHistoryDao.updateVmAccountHistory(vmAccountHistory);

			logger.info("++asyn  credit  future++++++vmActHisId:" + vmActHisId
					+ "++++isDone+++");

		}
	}

	@Override
	public String call() throws Exception {
		Long sTime = System.currentTimeMillis();
		logger.info("+++startDate:" + System.currentTimeMillis()
				+ "+++++++++Thread.currentThread().getName():"
				+ Thread.currentThread().getName() + "+++actId:" + actId
				+ "+++subAccountId:" + subAccountId + "++++++++");

		// 查询子账户
		SubAccount subAccount = subAccountService.findByIdAndActId(
				subAccountId, actId);

		// 个人账户 入账
		
		 * subAccountService.credit(subAccount, trxAmount, actHistoryType,
		 * trxId, trxOrderId, new Date(), description, isDisplay, bizType);
		 

		Long eTime = System.currentTimeMillis();
		logger.info("+++endDate:" + System.currentTimeMillis()
				+ "+++++++++Thread.currentThread().getName():"
				+ Thread.currentThread().getName() + "+++actId:" + actId
				+ "+++subAccountId:" + subAccountId + "++++e-sTime:"
				+ (eTime - sTime) + "++++");
		return null;
	}
}
*/