package com.beike.wap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.wap.dao.MCatalogDao;
import com.beike.wap.entity.MAbstractCatlog;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.entity.MerchantCatlog;
import com.beike.wap.service.MCatalogService;

@Service("mCatalogService")
public class MCatalogServiceImpl implements MCatalogService {

	@Autowired
	private MCatalogDao mCatalogDao;
	
//	@Resource(name = "wapRegionDao")
//	private MRegionDao regionDao;
	
	@Override
	public MAbstractCatlog findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MCouponCatlog getCouponCatlogById(long couponId) {
		MCouponCatlog catalog = mCatalogDao.findCouponCatalogById(couponId);
//		MRegion region = null;
//		try {
//			region = regionDao.findRegionById(catalog.getRegionextid());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(region != null)
//		{
//			catalog.setRegionName(region.getRegionName());
//		}
		return catalog;
	}

	@Override
	public int getCouponCatalogSum(MCouponCatlog couponCatlog) throws Exception {
		return mCatalogDao.getCouponCatalogSum(couponCatlog);
	}

	@Override
	public List<Long> getAllCouponId(int startPage, MCouponCatlog couponCatlog)
			throws Exception {
		return  mCatalogDao.queryCouponId(startPage, couponCatlog);
	}

	@Override
	public int getBrandCatlogSum(MerchantCatlog brandCatlog)
			throws Exception {
		return mCatalogDao.getBrandCatalogSum(brandCatlog);
	}

	@Override
	public List<Long> getAllMerchantId(int startPage,
			MerchantCatlog brandCatlog) throws Exception {
		return mCatalogDao.queryBrandId(startPage, brandCatlog);
	}
}
