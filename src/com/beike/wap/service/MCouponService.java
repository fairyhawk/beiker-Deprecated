package com.beike.wap.service;

import java.util.Date;
import java.util.List;

import com.beike.service.GenericService;
import com.beike.wap.entity.MGoods;

/**
 * <p>
 * Title:优惠券Service
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-09-23
 * @author lvjx
 * @version 1.0
 */

public interface MCouponService extends GenericService<MGoods, Long> {

	/**
	 * Description : 查询需要展示的信息
	 * 
	 * @param typeType
	 * @param typeFloor
	 * @param typePage
	 * @param currentDate
	 * @return
	 * @throws Exception
	 */
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception;

}
