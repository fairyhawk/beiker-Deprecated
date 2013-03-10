package com.beike.core.service.trx.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.RebRecord;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.RebateException;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.RebRecordService;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.RebRecordDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**
 * @Title: RebRecordServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: TODO
 * @date May 9, 2011 7:44:12 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("rebRecordService")
public class RebRecordServiceImpl implements RebRecordService {

	@Autowired
	private RebRecordDao rebRecordDao;
	@Autowired
	private AccountDao accountDao;

	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxOrderDao trxOrderDao;

	@Autowired
	private AccountService accountService;

	//private final Log logger = LogFactory.getLog(RebRecordServiceImpl.class);

	// @Transactional(propagation = Propagation.REQUIRED)
	public RebRecord create(RebRecord rebRecord) throws RebateException {

		if (rebRecord.getUserId() == null) {
			throw new IllegalArgumentException("userid_not_null");
		}

		if (rebRecord.getRequestId() == null) {
			throw new IllegalArgumentException("RequestId_not_null");
		}

		if (rebRecord.getExternalId() == null) {
			throw new IllegalArgumentException("ExternalId_not_null");
		}

		// 检查流水号是否重复。订单号可以重复
		RebRecord rebRecord2 = rebRecordDao.findByExternalId(rebRecord
				.getExternalId());
		if (rebRecord2 != null) {
			throw new RebateException(
					BaseException.REBRECORD_EXTERNALID_DUCPLIT);

		}

		// 检查是否为虚拟币账户
		Account account = accountDao.findByUserIdAndType(rebRecord.getUserId(),
				AccountType.VC);

		// 账户不不存在
		if (account == null) {
			throw new RebateException(BaseException.ACCOUNT_NOT_FOUND);
		}

		// 帐户状态无效.未激活
		if (!AccountStatus.ACTIVE.equals(account.getAccountStatus())) {
			throw new RebateException(BaseException.ACCOUNT_STATUS_INVALID);
		}

		// 组装核心信息
		rebRecord.setCreateDate(new Date());

		rebRecord.setTrxStatus(TrxStatus.INIT);

		rebRecordDao.addRebRecord(rebRecord);

		return rebRecordDao.findByExternalId(rebRecord.getExternalId());
	}

	public double findSucSumAmount(Long userId) {

		return rebRecordDao.findSucSumByUserId(userId);
	}

	public RebRecord update(RebRecord rebRecord) throws RebateException {

		return null;
	}

	public void updateStatus(Long id, TrxStatus TrxStatus)
			throws RebateException {
		// TODO Auto-generated method stub

	}

	public RebRecordDao getRebRecordDao() {
		return rebRecordDao;
	}

	public void setRebRecordDao(RebRecordDao rebRecordDao) {
		this.rebRecordDao = rebRecordDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public TrxorderGoodsDao getTrxorderGoodsDao() {
		return trxorderGoodsDao;
	}

	public void setTrxorderGoodsDao(TrxorderGoodsDao trxorderGoodsDao) {
		this.trxorderGoodsDao = trxorderGoodsDao;
	}

	public TrxOrderDao getTrxOrderDao() {
		return trxOrderDao;
	}

	public void setTrxOrderDao(TrxOrderDao trxOrderDao) {
		this.trxOrderDao = trxOrderDao;
	}

	// @Transactional(propagation = Propagation.REQUIRED)
	public RebRecord complete(RebRecord rebRecord) throws RebateException {
		if (rebRecord.getExternalId() == null) {
			throw new IllegalArgumentException("externalid_not_null");
		}
		if (!TrxStatus.INIT.equals(rebRecord.getTrxStatus())) {
			throw new RebateException(BaseException.REBRECORD_TRXSTATUS_INVALID);
		}
		rebRecordDao.updateStatusByExId(rebRecord.getExternalId(),
				TrxStatus.SUCCESS, new Date());

		return rebRecordDao.findByExternalId(rebRecord.getExternalId());
	}

	public Map<String, String> isRebateByTrxGoodsId(Long trxGoodsId)
			throws RebateException {
		Map<String, String> rspMap = new HashMap<String, String>();
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
		if (trxorderGoods == null) {
			throw new RebateException(BaseException.TRXORDERGOODS_NOT_FOUND);
		}
		if (!TrxStatus.SUCCESS.equals(trxorderGoods.getTrxStatus())) {

			throw new RebateException(
					BaseException.TRXORDERGOODS_TRXSTATUS_INVALID);
		}

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());
		if (trxOrder == null) {
			throw new RebateException(BaseException.TRXORDER_NOT_FOUND);
		}
		if (!TrxStatus.SUCCESS.equals(trxOrder.getTrxStatus())) {

			throw new RebateException(BaseException.TRXORDER_TRXSTATUS_INVALID);
		}
		rspMap.put("userId", trxOrder.getUserId() + "");
		rspMap.put("trxAmount", trxorderGoods.getRebatePrice() + "");
		return rspMap;

	}

	/**
	 * 用户名鉴权
	 * 
	 * @param userLoginName
	 * @return
	 * @throws RebateException
	 */
	public Map<String, String> validateAcccount(String userLoginName)
			throws RebateException {

		Map<String, String> rspMap = new HashMap<String, String>();

		Long userId = accountDao.findUserIdByLoginName(userLoginName);
		if (userId == 0L) {
			throw new RebateException(BaseException.USER_NOT_FOUND);
		}
		Account vcAccount = accountService.findByUserIdAndType(userId,
				AccountType.VC);

		Account cashAccount = accountService.findByUserIdAndType(userId,
				AccountType.CASH);
		if (vcAccount == null || cashAccount == null) {

			throw new RebateException(BaseException.ACCOUNT_NOT_FOUND);
		}
		if (!AccountStatus.ACTIVE.equals(vcAccount.getAccountStatus())
				|| !AccountStatus.ACTIVE.equals(cashAccount.getAccountStatus())) {

			throw new RebateException(BaseException.ACCOUNT_STATUS_INVALID);
		}

		rspMap.put("userId", userId + "");

		return rspMap;

	}

	public double findRebateAmountByUserId(Long userId) {

		return rebRecordDao.findSucSumByUserId(userId);

	}

	public List<RebRecord> listRebRecordByUserId(Long userId, int startRow,
			int pageSize) {

		List<RebRecord> tempList = rebRecordDao.findListByList(userId,
				startRow, pageSize);

		if (tempList == null) {

			return null;
		}
		// 组装In语句
		// String inIdStr="";
		StringBuilder sb = new StringBuilder();
		for (RebRecord item : tempList) {
			if (OrderType.REBATE.equals(item.getOrderType())) {
				sb.append(item.getBizId());
				sb.append(",");
			}

		}
		Map<Long, TrxorderGoods> tmpMap = new HashMap<Long, TrxorderGoods>();
		if (sb != null && !"".equals(sb.toString())) {

			sb.deleteCharAt(sb.length() - 1);
			List<TrxorderGoods> trxGoodsList = trxorderGoodsDao.findListInId(sb
					.toString());
			for (TrxorderGoods txog : trxGoodsList) {
				tmpMap.put(txog.getId(), txog);
			}
		}

		int rebCount = tempList.size();
		for (int i = 0; i < rebCount; i++) {

			RebRecord recode = tempList.get(i);
			if (OrderType.REBATE.equals(recode.getOrderType())) {
				TrxorderGoods txog = tmpMap.get(recode.getBizId());
				recode.setGoodsId(txog.getGoodsId());
				recode.setGoodsName(StringUtils.cutffStr(txog.getGoodsName(),
						10, "..."));
			}
		}
		return tempList;
	}

	public int findRowCountByUserIdAndStatus(Long userId, String status) {

		return rebRecordDao.findRowCountByUserIdAndStatus(userId, status);
	}

	/**
	 * 根据订单类型查询订单号是否重复
	 * 
	 * @param requestId
	 * @param orderType
	 * @return
	 */
	public boolean isRequestIdRepeat(String requestId, OrderType orderType) {
		boolean result = true;
		RebRecord rebRecord = rebRecordDao.findRebByreqIdAndType(requestId,
				EnumUtil.transEnumToString(orderType));
		if (rebRecord == null) {

			result = false;
		}

		return result;

	}
	

	
}
