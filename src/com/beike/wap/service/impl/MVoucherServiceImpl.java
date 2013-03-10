package com.beike.wap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.wap.dao.MVoucherDao;
import com.beike.wap.entity.MVoucher;
import com.beike.wap.service.MVoucherService;

/**
 * @Title: TrxorderGoodsServiceImpl.java
 * @Package com.beike.core.service.trx
 * @Description:凭证 service 实现类
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("mVoucherService")
public class MVoucherServiceImpl implements MVoucherService {

	@Autowired
	private MVoucherDao mVoucherDao;
	
	@Override
	public MVoucher findById(Long id) {
		return mVoucherDao.findById(id);
	}
}
