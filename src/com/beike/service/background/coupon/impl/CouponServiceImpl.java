package com.beike.service.background.coupon.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.coupon.CouponDao;
import com.beike.entity.background.coupon.Coupon;
import com.beike.form.background.coupon.CouponForm;
import com.beike.service.background.coupon.CouponService;
/**
 * Title : 	CouponServiceImpl
 * <p/>
 * Description	:优惠券服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-06-17   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-17  
 */
@Service("bgCouponService")
public class CouponServiceImpl implements CouponService {

	@Resource(name="bgCouponDao")
	private CouponDao couponDao;
	/*
	 * @see com.beike.service.background.coupon.CouponService#addCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String addCoupon(CouponForm couponForm) throws Exception {
		String result = null;
		result = couponDao.addCoupon(couponForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.coupon.CouponService#queryCoupon(com.beike.form.background.coupon.CouponForm, int, int)
	 */
	public List<Coupon> queryCoupon(CouponForm couponForm, int startRow,
			int pageSize) throws Exception {
		List<Coupon> couponList = null;
		couponList = couponDao.queryCoupon(couponForm, startRow, pageSize);
		return couponList;
	}

	/*
	 * @see com.beike.service.background.coupon.CouponService#queryCouponCount(com.beike.form.background.coupon.CouponForm)
	 */
	public int queryCouponCount(CouponForm couponForm) throws Exception {
		int count = 0;
		count = couponDao.queryCouponCount(couponForm);
		return count;
	}

	/*
	 * @see com.beike.service.background.coupon.CouponService#queryCouponById(java.lang.String)
	 */
	public Coupon queryCouponById(String couponId) throws Exception {
		Coupon coupon = null;
		coupon = couponDao.queryCouponById(couponId);
		return coupon;
	}

	/*
	 * @see com.beike.service.background.coupon.CouponService#editCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String editCoupon(CouponForm couponForm) throws Exception {
		String result = null;
		result = couponDao.editCoupon(couponForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.coupon.CouponService#downCoupon(com.beike.form.background.coupon.CouponForm)
	 */
	public String downCoupon(CouponForm couponForm) throws Exception {
		String result = null;
		result = couponDao.downCoupon(couponForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.coupon.CouponService#queryCouponName(com.beike.form.background.coupon.CouponForm)
	 */
	public boolean validatorCouponName(CouponForm couponForm) throws Exception {
		boolean flag = false;
		flag = couponDao.validatorCouponName(couponForm);
		return flag;
	}
	
}
