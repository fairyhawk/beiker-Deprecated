package com.beike.service.background.coupon;

import java.util.List;

import com.beike.entity.background.coupon.Coupon;
import com.beike.form.background.coupon.CouponForm;

/**
 * Title : 	CouponService
 * <p/>
 * Description	:优惠券信息服务接口类
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
 * <pre>1     2011-06-17    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-17  
 */
public interface CouponService {
	
	/**
	 * Description : 新增优惠券
	 * @param couponForm
	 * @return
	 * @throws Exception
	 */
	public String addCoupon(CouponForm couponForm) throws Exception;
	
	/**
	 * Description : 查询优惠券
	 * @param couponForm
	 * @param startRow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<Coupon> queryCoupon(CouponForm couponForm,int startRow,int pageSize) throws Exception;

	/**
	 * Description : 查询优惠券数量
	 * @param couponForm
	 * @return
	 * @throws Exception
	 */
	public int queryCouponCount(CouponForm couponForm) throws Exception;
	
	/**
	 * Description : 根据优惠券id查询优惠券信息
	 * @param couponId
	 * @return
	 * @throws Exception
	 */
	public Coupon queryCouponById(String couponId) throws Exception;
	
	/**
	 * Description : 修改优惠券
	 * @param couponForm
	 * @return
	 * @throws Exception
	 */
	public String editCoupon(CouponForm couponForm) throws Exception;
	
	/**
	 * Description : 下架优惠券
	 * @param couponForm
	 * @return
	 * @throws Exception
	 */
	public String downCoupon(CouponForm couponForm) throws Exception;
	
	/**
	 * Description : 查询优惠券名称是否已经存在
	 * @param couponForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorCouponName(CouponForm couponForm) throws Exception;
}
