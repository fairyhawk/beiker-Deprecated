package com.beike.wap.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.wap.dao.MTrxorderGoodsDao;
import com.beike.wap.entity.MTrxorderGoods;
import com.beike.wap.service.MTrxorderGoodsService;

/**
 * @Title: TrxorderGoodsServiceImpl.java
 * @Package com.beike.core.service.trx
 * @Description:订单商品CORE service 实现类
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("mTrxorderGoodsService")
public class MTrxorderGoodsServiceImpl implements MTrxorderGoodsService {

	private final Log logger = LogFactory
			.getLog(MTrxorderGoodsServiceImpl.class);

	@Autowired
	private MTrxorderGoodsDao mTrxorderGoodsDao;

	/*
	 * @see com.beike.wap.service.MTrxorderGoodsService#getTrxOrderInfo(java.lang.Long, int, int, java.lang.String)
	 */
	@Override
	public List<MTrxorderGoods> getTrxOrderInfo(Long userId, int startRow,
			int pageSize, String queryType) {
		return mTrxorderGoodsDao.findOrderByStatusAndTrxId(userId, queryType, startRow, pageSize);
	}
	
	/*
	 * @see com.beike.wap.service.MTrxorderGoodsService#getRecordNum(java.lang.Long, java.lang.String)
	 */
	@Override
	public int getRecordNum(Long userId, String qryType) {
		return mTrxorderGoodsDao.findPageCountByUserId(userId,
				qryType);
	}
	
	/*
	 * @see com.beike.wap.service.MTrxorderGoodsService#getTrxOrderGoodsInfo(java.lang.Long)
	 */
	@Override
	public MTrxorderGoods getTrxOrderGoodsInfo(Long id) throws Exception {
		MTrxorderGoods trxOrderGoods = null;
		trxOrderGoods = mTrxorderGoodsDao.getTrxOrderGoodsInfo(id);
		return trxOrderGoods;
	}

	@Override
	public MTrxorderGoods findById(Long id) {
		return mTrxorderGoodsDao.findById(id);
	}

	/*
	 * @see com.beike.wap.service.MTrxorderGoodsService#getRefundGoodsInfo(java.lang.String)
	 */
	@Override
	public MTrxorderGoods getRefundGoodsInfo(String id,String status) throws Exception {
		MTrxorderGoods trxOrderGoods = null;
		trxOrderGoods = mTrxorderGoodsDao.getRefundGoodsInfo(id,status);
		return trxOrderGoods;
	}
}
