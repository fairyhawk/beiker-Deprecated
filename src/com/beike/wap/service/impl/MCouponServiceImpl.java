package com.beike.wap.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.wap.dao.MCouponDao;
import com.beike.wap.entity.MGoods;
import com.beike.wap.service.MCouponService;

/**
 * Title : CouponServiceImpl
 * <p/>
 * Description :优惠券服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : qianpin.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-09-23   lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-09-23
 */
@Service("wapCouponService")
public class MCouponServiceImpl implements MCouponService {

	/*
	 * @see com.beike.wap.service.coupon.CouponService#queryIndexShowMes(int,
	 * int, int, java.util.Date)
	 */
	@Override
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception {
		List<MGoods> goodsList = null;
		goodsList = couponDao.queryIndexShowMes(typeType, typeFloor, typePage,
				currentDate, typeArea);
		return goodsList;
	}

	@Override
	public MGoods findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Resource(name = "wapCouponDao")
	private MCouponDao couponDao;

}
