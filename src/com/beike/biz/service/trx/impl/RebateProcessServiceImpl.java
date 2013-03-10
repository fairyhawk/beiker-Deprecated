package com.beike.biz.service.trx.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.ProcessService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.RebRecord;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.RebRecordService;
import com.beike.util.EnumUtil;
import com.beike.util.GuidEncryption;

/**
 * @Title: RebateBizProcessManagerImpl.java
 * @Package com.beike.biz.service.trx.impl
 * @Description: 返现处理实现服务类
 * @date May 9, 2011 6:34:50 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("rebateProcessService")
public class RebateProcessServiceImpl implements ProcessService {

	@Autowired
	private RebRecordService rebRecordService;

	@Autowired
	private AccountService accountService;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	//private final Log logger = LogFactory.getLog(RebateProcessServiceImpl.class);

	// @Transactional(propagation = Propagation.REQUIRED)
	public OrderInfo processPost(OrderInfo orderInfo)
			throws ProcessServiceException, RebateException, AccountException,
			StaleObjectStateException {

		return createProcess(orderInfo);
	}

	/**
	 * 方法重命名后调用，避免与交易的方法的命名的冲突造成事务配置冲突
	 * 
	 * @param orderInfo
	 * @return
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws AccountException
	 * @throws StaleObjectStateException
	 */
	public OrderInfo createProcess(OrderInfo orderInfo)
			throws ProcessServiceException, RebateException, AccountException,
			StaleObjectStateException {

		String requestId = guidGenerator.gainCode("Reb");
		String externalId = GuidEncryption.encryptSimpler("R", requestId
				.substring(3, requestId.length() - 1));
		if (OrderType.INSIDEREBATE.equals(EnumUtil.transStringToEnum(
				OrderType.class, orderInfo.getBizType()))) {
			// 若是内部返现。进入返现记录表查询请求号是否重复
			requestId = orderInfo.getRequestId();
			boolean result = rebRecordService.isRequestIdRepeat(requestId,
					OrderType.INSIDEREBATE);

			if (result) {
				throw new RebateException(
						BaseException.REBATE_REQUESTID_DUCPLIT);

			}

		}

		// 获取参数
		Long userId = orderInfo.getUserId();
		double trxAmount = orderInfo.getTrxAmount();
		// String extendInfo = orderInfo.getExtendInfo();
		String bizType = orderInfo.getBizType();
		String bizId = orderInfo.getGoodsId();
		String extendInfo = orderInfo.getBizType();
		// 组装
		RebRecord rebRecord = new RebRecord();
		rebRecord.setRequestId(requestId);
		rebRecord.setExternalId(externalId);
		rebRecord.setTrxAmount(trxAmount);
		rebRecord.setExtendInfo(extendInfo);
		rebRecord.setUserId(userId);
		rebRecord.setBizId(new Long(bizId));
		rebRecord.setOrderType(OrderType.REBATE);
		rebRecord.setExtendInfo(extendInfo);
		if (OrderType.INSIDEREBATE.equals(EnumUtil.transStringToEnum(
				OrderType.class, orderInfo.getBizType()))) {

			rebRecord.setOrderType(OrderType.INSIDEREBATE);
		}

		RebRecord rebRecord2 = null;

		// 创建返现记录
		rebRecord2 = rebRecordService.create(rebRecord);

		Account account = accountService.findByUserIdAndType(rebRecord2
				.getUserId(), AccountType.VC);

		// 入账和创建者帐务历史
		if (OrderType.INSIDEREBATE.equals(EnumUtil.transStringToEnum(
				OrderType.class, orderInfo.getBizType()))) {

			accountService.credit(account, trxAmount,
					ActHistoryType.INSIDEREBATE, rebRecord2.getId(), 0L,
					new Date(), "insiderebate", true, bizType);

		} else if (OrderType.REBATE.equals(EnumUtil.transStringToEnum(
				OrderType.class, orderInfo.getBizType()))) {

			accountService
					.credit(account, trxAmount, ActHistoryType.RABATE,
							rebRecord2.getId(), 0L, new Date(), "rebate", true,
							bizType);
		}

		// 完成返现
		rebRecordService.complete(rebRecord);

		return null;
	}
	
	
	
	/**
	 * 账户支付重试(暂时只对分销商来的订单有效)（返现废弃,空实现 ）
	 * @param orderInfo
	 * @return
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 */
	public OrderInfo processReTry(OrderInfo orderInfo){
		return null;
		
	}

	public RebRecordService getRebRecordService() {
		return rebRecordService;
	}

	public void setRebRecordService(RebRecordService rebRecordService) {
		this.rebRecordService = rebRecordService;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public GuidGenerator getGuidGenerator() {
		return guidGenerator;
	}

	public void setGuidGenerator(GuidGenerator guidGenerator) {
		this.guidGenerator = guidGenerator;
	}

	@Override
	public OrderInfo processPro(OrderInfo orderInfo) throws TrxOrderException,CouponException,
			AccountException, PaymentException, TrxorderGoodsException,
			RuleException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
