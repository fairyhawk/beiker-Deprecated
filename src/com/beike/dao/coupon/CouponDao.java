package com.beike.dao.coupon;

import java.util.List;

import com.beike.entity.catlog.CouponCatlog;
import com.beike.form.CouponForm;

/**
 * <p>Title:优惠券 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface CouponDao {
	
	/**
	 * 分页查询
	 * @param idsCourse
	 * @param start
	 * @param end
	 * @return
	 */
	public List<CouponForm> getCouponByPage(String idsCourse,int start,int end);
	
	
	public List<CouponForm> getCouponByIds(String idsCourse);
	public List<CouponForm> getLuceneCouponByIds(String idsCourse);
	
	public List<CouponForm> getCouponDownCount(String ids);
	
	/**
	 *  根据品牌id获得优惠券列表
	 */
	public List<CouponForm>  getCouponListByMerchantId(Long merchantId,int top);
	
	
	/**
	 * 根据优惠券id查询优惠券详情
	 * @param couponId	优惠券id
	 * @return			优惠券详情
	 */
	public CouponForm getCouponDetailById(Integer couponId);
	
	/**
	 * 根据优惠券id 查询一级属性
	 */
	public CouponCatlog getCouponCatlogById(Integer couponId);
	
	
	public int getCouponStatusByID(Long couponid);
	public String getCouponCity(Integer couponId);
	/**
	 * 根据品牌id获取优惠券数量
	 * @param merchantId
	 * @return
	 * @author qiaowb 2011-11-01
	 */
	public int getCouponCount(Long merchantId);
	
	/**
	 * 根据品牌id获取优惠券ID集合
	 * @param merchantId
	 * @return
	 * @author qiaowb 2011-11-01
	 */
	public List<Long> getCouponCountIds(Long merchantId, int start, int end);
}
