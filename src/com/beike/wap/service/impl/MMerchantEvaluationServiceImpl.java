package com.beike.wap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.wap.dao.MMerchantEvaluationDao;
import com.beike.wap.entity.MMerchantEvaluation;
import com.beike.wap.service.MMerchantEvaluationService;

/**
 * @Title: TrxorderGoodsServiceImpl.java
 * @Package com.beike.core.service.trx
 * @Description:凭证 service 实现类
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("mMerchantEvaluationService")
public class MMerchantEvaluationServiceImpl implements MMerchantEvaluationService {

	@Autowired
	private MMerchantEvaluationDao mMerchantEvaluationDao;
	
	@Override
	public MMerchantEvaluation findByTrxorderId(Long trxorderId) {
		return mMerchantEvaluationDao.findByTrxId(trxorderId);
	}

}
