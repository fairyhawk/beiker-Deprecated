package com.beike.biz.service.trx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.enums.trx.BizProcessType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.OrderCreateException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: BizProcessFactory.java
 * @Package com.beike.biz.service.trx
 * @Description: 业务处理工厂
 * @date May 9, 2011 6:12:03 PM
 * @author wh.cheng
 * @version v1.0
 */
public class BizProcessFactory {

	private Map<String, ProcessService> serviceMap;

	public Map<String, ProcessService> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, ProcessService> serviceMap) {
		this.serviceMap = serviceMap;
	}

	@SuppressWarnings("unchecked")
	private final Map orderLocks = new ConcurrentHashMap();

	/**
	 * 根据业务处理类型来分别获取处理服务
	 * 
	 * @throws RebateException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 * @throws OrderCreateException
	 */

	public ProcessService getProcessService(OrderInfo orderInfo)
			throws ProcessServiceException, RebateException, AccountException,
			TrxOrderException, PaymentException, TrxorderGoodsException,
			VoucherException, RuleException, StaleObjectStateException,
			OrderCreateException {
		// 订单、返现并发锁控制
		boolean isNeedLock=orderInfo.isNeedLock();//是否需要锁
		
		if (isNeedLock) {//如果需要锁
			if (orderInfo.getUserId() == null) {
				throw new IllegalArgumentException("userid not null");
			}
			Object lock = getOrderLocks(orderInfo.getUserId());
			synchronized (lock) {
				BizProcessType bizProcessType = orderInfo.getBizProcessType();
				ProcessService processService = getService(bizProcessType);

				return processService;
			}
		} else {//若不需要。针对分销商进来的订单，若不走帐务，加锁只是做无谓的订单排队（尽管此锁在集群里意义不是很大）
			BizProcessType bizProcessType = orderInfo.getBizProcessType();
			ProcessService processService = getService(bizProcessType);

			return processService;

		}
	}

	@SuppressWarnings("unchecked")
	public synchronized Object getOrderLocks(Long userId) {
		Object lock = orderLocks.get(userId);
		if (lock == null) {
			lock = new Object();
			orderLocks.put(userId, lock);
		}
		return lock;
	}

	private ProcessService getService(BizProcessType bizProcessType) {
		ProcessService processService = serviceMap.get(bizProcessType.name());
		if (processService == null)
			throw new IllegalArgumentException("This bizProcessType:"
					+ bizProcessType.name() + " has not been implements.");
		return processService;
	}

}
