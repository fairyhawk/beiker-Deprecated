package com.beike.core.service.trx.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxRule;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.RuleException;
import com.beike.core.service.trx.TrxRuleService;
import com.beike.dao.trx.TrxRuleDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.util.TrxConstant;
import com.beike.util.TrxRuleUtil;

/**
 * @Title: TrxRuleService.java
 * @Package com.beike.core.service.trx
 * @Description: 交易表达式业务服务实现类
 * @author wenhua.cheng
 * @version V1.0
 */
@Service("trxRuleService")
public class TrxRuleServiceImpl implements TrxRuleService {

	//private final Log logger = LogFactory.getLog(TrxRuleServiceImpl.class);

	@Autowired
	private TrxRuleDao trxRuleDao;

	@Autowired
	private TrxorderGoodsDao trxOrderGoodsDao;

	@Override
	public boolean resolveTrxRule(Long trxRuleId, String trxType)
			throws RuleException {
		boolean result = false;
		if (trxRuleId.longValue() == 0) {

			return true;
		}

		TrxRule trxRule = trxRuleDao.findRuleById(trxRuleId);
		if (trxRule == null) {

			throw new RuleException(BaseException.TRXRULE_RESOLVE_ERROR);
		}
		if (TrxRuleUtil.ACTHIS.equals(trxType)) {
			result = TrxRuleUtil.resolveIsActHis(trxRule.getTrxRule());
		}
		/*
		 * if (TrxRuleUtil.ACTHIS.equals(trxType)) { result =
		 * TrxRuleUtil.resolveIsActHis(trxRule.getTrxRule()); } if
		 * (TrxRuleUtil.ACTHIS.equals(trxType)) { result =
		 * TrxRuleUtil.resolveIsActHis(trxRule.getTrxRule()); }
		 */
		return result;
	}

	/**
	 * 根据交易表达式Tile查找交易表达式ID
	 * 
	 * @param trxTitle
	 * @return
	 * @throws RuleException
	 */
	@Override
	public Long qryTrxRule(String trxTitle) throws RuleException {
		Long trxRuleId = 0L;
		if (TrxConstant.TRX_NORMAL.equals(trxTitle)) {
			return trxRuleId;
		}
		TrxRule trxRule = trxRuleDao.findRuleByTitle(trxTitle);
		if (trxRule == null) {
			throw new RuleException(BaseException.TRXRULE_RESOLVE_ERROR);

		}
		return trxRule.getId();

	}

	/**
	 *活动限制
	 * 
	 * @param trxorderId
	 * @param loteryStr
	 * @throws RuleException
	 */
	public void limitLottery(Long userId, String loteryStr, Long trxRuleId)
			throws RuleException {

		if (trxRuleId.longValue() == 1) { // 只有为0元抽奖才有此限制
			Long count = trxOrderGoodsDao.findCountByUIdAndLottery(userId,
					loteryStr, trxRuleId);

			if (count.longValue() > 0) {

				throw new RuleException(BaseException.LOTTERY_OVER_ALLOWCOUNT);

			}
		}

	}
}
