package com.beike.common.catlog.service;

import java.util.List;

import com.beike.form.CouponForm;
import com.beike.page.Pager;

/**
 * <p>Title:优惠券服务</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface CouponCatlogService extends AbstractCatlogService{
	
	
	/*public String getCatByID(Long id);*/
	public List<CouponForm> getCouponFormByPage(List<Long> listids, Pager pager);
	
	public List<CouponForm> getCouponFormByIds(List<Long> listids);
	
}
