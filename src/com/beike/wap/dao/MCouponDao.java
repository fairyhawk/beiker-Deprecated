package com.beike.wap.dao;

import java.util.Date;
import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MGoods;

public interface MCouponDao extends GenericDao<MGoods, Long> {

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
