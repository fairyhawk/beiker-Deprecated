package com.beike.core.service.trx;

import com.beike.common.exception.RuleException;

/**
 * @Title: TrxRuleService.java
 * @Package com.beike.core.service.trx
 * @Description: 交易表达式业务服务接口
 * @author wenhua.cheng
 * @version V1.0
 */

public interface TrxRuleService {

	/**
	 * 解析交易表达式
	 * 
	 * @param sourceRule
	 * @return
	 * @throws RuleException
	 */
	public boolean resolveTrxRule(Long trxRuleId, String trxType)
			throws RuleException;

	/**
	 * 根据交易表达式Tile查找交易表达式ID
	 * 
	 * @param trxTitle
	 * @return
	 * @throws RuleException
	 */
	public Long qryTrxRule(String trxTitle) throws RuleException;

	/**
	 *活动限制
	 * 
	 * @param trxorderId
	 * @param loteryStr
	 * @throws RuleException
	 */
	public void limitLottery(Long userId, String loteryStr, Long trxRuleId)
			throws RuleException;

}