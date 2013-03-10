package com.beike.dao.trx;

import com.beike.common.entity.trx.TrxRule;

/**
 * @Title: TrxRuleDao.java
 * @Package com.beike.dao.trx
 * @Description: 支付规则Dao
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 4:38:03 PM
 * @version V1.0
 */
public interface TrxRuleDao {

	/**
	 * 根据交易表达式标题查询交易表达式
	 * 
	 * @param title
	 * @return
	 */
	public TrxRule findRuleByTitle(String title);

	/**
	 * 根据ID查询交易表达式
	 * 
	 * @param title
	 * @return
	 */
	public TrxRule findRuleById(Long id);

}
