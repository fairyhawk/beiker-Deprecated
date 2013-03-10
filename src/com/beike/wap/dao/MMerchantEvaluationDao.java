package com.beike.wap.dao;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MMerchantEvaluation;
import com.beike.wap.entity.MVoucher;

/**
 * @Title: TrxorderGoodsDao.java
 * @Package com.beike.dao.trx
 * @Description: 凭证DAO
 * @date May 16, 2011 6:53:25 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface MMerchantEvaluationDao extends GenericDao<MMerchantEvaluation, Long> {
	
	public MMerchantEvaluation findByTrxId(Long id);
}
